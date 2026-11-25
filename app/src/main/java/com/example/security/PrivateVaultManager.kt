package com.example.security

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.model.RenameMediaType
import com.example.model.VaultItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Private AES-256-GCM Encrypted Media Vault stored strictly inside app-private storage.
 * Protects personal family photos and videos from appearing in the public gallery or other apps.
 */
object PrivateVaultManager {

    private const val VAULT_DIR_NAME = "prism_private_vault"
    private const val INDEX_FILE_NAME = "vault_index.json"
    private const val KEY_SEED_FILE = "vault_seed.bin"
    private const val GCM_IV_LEN = 12
    private const val GCM_TAG_BITS = 128

    private fun vaultDir(context: Context): File =
        File(context.filesDir, VAULT_DIR_NAME).apply { mkdirs() }

    private fun getOrCreateVaultKey(context: Context): SecretKeySpec {
        val dir = vaultDir(context)
        val seedFile = File(dir, KEY_SEED_FILE)
        val seedBytes = if (seedFile.exists() && seedFile.length() >= 32) {
            seedFile.readBytes()
        } else {
            ByteArray(32).also {
                SecureRandom().nextBytes(it)
                seedFile.writeBytes(it)
            }
        }
        val digest = MessageDigest.getInstance("SHA-256").digest(
            seedBytes + context.packageName.toByteArray(Charsets.UTF_8)
        )
        return SecretKeySpec(digest, "AES")
    }

    suspend fun listVaultItems(context: Context): List<VaultItem> = withContext(Dispatchers.IO) {
        val indexFile = File(vaultDir(context), INDEX_FILE_NAME)
        if (!indexFile.exists()) return@withContext emptyList()
        runCatching {
            val arr = JSONArray(indexFile.readText())
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.optJSONObject(i) ?: continue
                    val id = o.optString("id")
                    val encName = o.optString("encName")
                    if (id.isBlank() || encName.isBlank()) continue
                    if (!File(vaultDir(context), encName).exists()) continue
                    add(
                        VaultItem(
                            id = id,
                            originalTitle = o.optString("title", "Private File"),
                            mediaType = runCatching {
                                RenameMediaType.valueOf(o.optString("type", RenameMediaType.PHOTO.name))
                            }.getOrDefault(RenameMediaType.PHOTO),
                            encryptedFileName = encName,
                            sizeBytes = o.optLong("size", 0L),
                            addedAtMs = o.optLong("added", 0L),
                            mimeType = o.optString("mime", "image/jpeg"),
                        )
                    )
                }
            }.sortedByDescending { it.addedAtMs }
        }.getOrDefault(emptyList())
    }

    private fun saveIndex(context: Context, items: List<VaultItem>) {
        val arr = JSONArray()
        items.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("title", item.originalTitle)
                    put("type", item.mediaType.name)
                    put("encName", item.encryptedFileName)
                    put("size", item.sizeBytes)
                    put("added", item.addedAtMs)
                    put("mime", item.mimeType)
                }
            )
        }
        File(vaultDir(context), INDEX_FILE_NAME).writeText(arr.toString())
    }

    /**
     * Encrypts bytes from [sourceUri] (or [fallbackDrawableResId]) using AES-256-GCM
     * and stores them privately inside the encrypted vault.
     */
    suspend fun importIntoVault(
        context: Context,
        title: String,
        uriString: String,
        mediaType: RenameMediaType,
        mimeType: String,
        fallbackDrawableResId: Int? = null,
    ): VaultItem? = withContext(Dispatchers.IO) {
        if (ContentPurityGuard.isExplicitOrAdult(title, uriString)) return@withContext null

        val plainBytes: ByteArray = runCatching {
            when {
                uriString.isNotBlank() -> {
                    context.contentResolver.openInputStream(Uri.parse(uriString))?.use { it.readBytes() }
                }
                fallbackDrawableResId != null -> {
                    val bmp = BitmapFactory.decodeResource(context.resources, fallbackDrawableResId)
                    if (bmp != null) {
                        val bos = java.io.ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.JPEG, 92, bos)
                        bmp.recycle()
                        bos.toByteArray()
                    } else null
                }
                else -> null
            }
        }.getOrNull() ?: return@withContext null

        if (plainBytes.isEmpty()) return@withContext null

        return@withContext runCatching {
            val id = UUID.randomUUID().toString()
            val encName = "v_${id.take(12)}.pvault"
            val iv = ByteArray(GCM_IV_LEN).also { SecureRandom().nextBytes(it) }
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateVaultKey(context), GCMParameterSpec(GCM_TAG_BITS, iv))
            val encrypted = cipher.doFinal(plainBytes)

            val outFile = File(vaultDir(context), encName)
            outFile.outputStream().use { out ->
                out.write(iv)
                out.write(encrypted)
            }

            val item = VaultItem(
                id = id,
                originalTitle = title.ifBlank { "Private Media" },
                mediaType = mediaType,
                encryptedFileName = encName,
                sizeBytes = plainBytes.size.toLong(),
                addedAtMs = System.currentTimeMillis(),
                mimeType = mimeType.ifBlank { "application/octet-stream" },
            )
            val current = listVaultItems(context).toMutableList()
            current.add(0, item)
            saveIndex(context, current)
            item
        }.getOrNull()
    }

    suspend fun decryptBytes(context: Context, item: VaultItem): ByteArray? = withContext(Dispatchers.IO) {
        val file = File(vaultDir(context), item.encryptedFileName)
        if (!file.exists()) return@withContext null
        runCatching {
            val all = file.readBytes()
            if (all.size <= GCM_IV_LEN) return@runCatching null
            val iv = all.copyOfRange(0, GCM_IV_LEN)
            val payload = all.copyOfRange(GCM_IV_LEN, all.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateVaultKey(context), GCMParameterSpec(GCM_TAG_BITS, iv))
            cipher.doFinal(payload)
        }.getOrNull()
    }

    suspend fun decryptPhotoBitmap(context: Context, item: VaultItem): Bitmap? = withContext(Dispatchers.IO) {
        val bytes = decryptBytes(context, item) ?: return@withContext null
        runCatching { BitmapFactory.decodeByteArray(bytes, 0, bytes.size) }.getOrNull()
    }

    /**
     * Restores a vaulted photo back to Pictures/Prism and removes its encrypted copy.
     */
    suspend fun restorePhotoToGallery(context: Context, item: VaultItem): Boolean = withContext(Dispatchers.IO) {
        val bytes = decryptBytes(context, item) ?: return@withContext false
        val ext = when {
            item.mimeType.contains("png", ignoreCase = true) -> "png"
            item.mimeType.contains("mp4", ignoreCase = true) -> "mp4"
            else -> "jpg"
        }
        val safeTitle = item.originalTitle
            .replace(Regex("[^a-zA-Z0-9\\u0600-\\u06FF_-]"), "_")
            .take(36)
            .ifBlank { "Restored_Media" }
        val fileName = "${safeTitle}_${System.currentTimeMillis()}.$ext"

        val saved = runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val isVideo = item.mediaType == RenameMediaType.VIDEO
                val collection = if (isVideo) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                val relPath = if (isVideo) {
                    "${Environment.DIRECTORY_MOVIES}/Prism"
                } else {
                    "${Environment.DIRECTORY_PICTURES}/Prism"
                }
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, item.mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, relPath)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(collection, values) ?: return@runCatching false
                resolver.openOutputStream(uri)?.use { it.write(bytes) } ?: return@runCatching false
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
                true
            } else {
                val outDir = File(context.filesDir, "exports").apply { mkdirs() }
                File(outDir, fileName).writeBytes(bytes)
                true
            }
        }.getOrDefault(false)

        if (saved) {
            removeVaultItem(context, item.id)
        }
        saved
    }

    suspend fun removeVaultItem(context: Context, itemId: String) = withContext(Dispatchers.IO) {
        val current = listVaultItems(context)
        val target = current.firstOrNull { it.id == itemId }
        if (target != null) {
            runCatching { File(vaultDir(context), target.encryptedFileName).delete() }
        }
        saveIndex(context, current.filterNot { it.id == itemId })
    }
}
