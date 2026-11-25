package com.example.data

import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile
import com.example.R
import com.example.model.AudioTrack
import com.example.model.DiscoveredSubfolder
import com.example.model.DuplicateGroup
import com.example.model.DuplicateMediaType
import com.example.model.PhotoItem
import com.example.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

data class ScannedMediaResult(
    val audioTracks: List<AudioTrack> = emptyList(),
    val videos: List<VideoItem> = emptyList(),
    val photos: List<PhotoItem> = emptyList(),
)

data class CachedMediaLookup(
    val audioById: Map<String, AudioTrack> = emptyMap(),
    val videoById: Map<String, VideoItem> = emptyMap(),
    val photoById: Map<String, PhotoItem> = emptyMap(),
)

object MediaScanner {
    private val audioExtensions = setOf("MP3", "FLAC", "WAV", "OGG", "AAC", "M4A", "OPUS", "WEBM")
    private val videoExtensions = setOf("MP4", "MKV", "WEBM", "MOV", "AVI")
    private val photoExtensions = setOf("JPG", "JPEG", "PNG", "WEBP", "GIF", "SVG")

    private val docProjection = arrayOf(
        DocumentsContract.Document.COLUMN_DOCUMENT_ID,
        DocumentsContract.Document.COLUMN_DISPLAY_NAME,
        DocumentsContract.Document.COLUMN_MIME_TYPE,
        DocumentsContract.Document.COLUMN_SIZE,
        DocumentsContract.Document.COLUMN_LAST_MODIFIED,
    )

    /**
     * Resolves a clean, user-friendly folder name from a SAF tree URI (e.g., "Music", "Download").
     */
    fun resolveFolderDisplayName(context: Context, treeUri: Uri): String {
        val docIdName = runCatching {
            val treeDocId = DocumentsContract.getTreeDocumentId(treeUri)
            extractFolderNameFromDocId(treeDocId)
        }.getOrNull()
        if (!docIdName.isNullOrBlank()) return docIdName

        val docFileName = runCatching {
            DocumentFile.fromTreeUri(context, treeUri)?.name
        }.getOrNull()
        if (!docFileName.isNullOrBlank()) return docFileName

        return extractFolderNameFromDocId(treeUri.lastPathSegment ?: "")
            .ifBlank { "Device Folder" }
    }

    /**
     * Pure helper to extract a readable folder name from SAF document IDs like "primary:Music/FLAC".
     */
    fun extractFolderNameFromDocId(docId: String): String {
        val cleaned = docId.trim().trimEnd('/')
        if (cleaned.isBlank()) return ""
        val afterColon = cleaned.substringAfter(':', cleaned).trim('/')
        if (afterColon.isBlank()) {
            return if (cleaned.startsWith("primary", ignoreCase = true)) {
                "Internal Storage"
            } else {
                cleaned.substringBefore(':').ifBlank { "Storage" }
            }
        }
        return afterColon.substringAfterLast('/').ifBlank { afterColon }
    }

    /**
     * Backward-compatible fast scan wrapper.
     */
    suspend fun scanFolderTree(
        context: Context,
        treeUri: Uri,
        favoriteIds: Set<String> = emptySet(),
    ): ScannedMediaResult {
        val discovered = scanFolderTreeFast(
            context = context,
            treeUri = treeUri,
            favoriteIds = favoriteIds
        )
        return enrichMediaParallel(context, discovered)
    }

    /**
     * Fast inspection of an existing user-granted folder tree: returns the root folder plus
     * its existing subfolders (up to [maxDepth]) with direct media file counts so the user can
     * pick exact folders with Checkboxes before confirming a strictly scoped scan.
     */
    suspend fun inspectTreeSubfolders(
        context: Context,
        treeUri: Uri,
        maxDepth: Int = 2,
    ): List<DiscoveredSubfolder> = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val treeUriStr = treeUri.toString()
        val rootDisplayName = resolveFolderDisplayName(context, treeUri)
        val rootDocId = runCatching { DocumentsContract.getTreeDocumentId(treeUri) }.getOrNull()
            ?: return@withContext emptyList()

        data class InspectNode(val docId: String, val name: String, val path: String, val depth: Int)
        val queue = ArrayDeque<InspectNode>()
        queue.add(InspectNode(rootDocId, rootDisplayName, rootDisplayName, 0))
        val discovered = mutableListOf<DiscoveredSubfolder>()
        val maxFolders = 48

        while (queue.isNotEmpty() && discovered.size < maxFolders) {
            val current = queue.removeFirst()
            var directMedia = 0
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, current.docId)
            try {
                resolver.query(childrenUri, docProjection, null, null, null)?.use { cursor ->
                    val idIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                    val nameIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                    val mimeIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)

                    while (cursor.moveToNext()) {
                        val childDocId = if (idIdx >= 0) cursor.getString(idIdx) else continue
                        val displayName = (if (nameIdx >= 0) cursor.getString(nameIdx) else null) ?: continue
                        if (displayName.startsWith(".")) continue
                        val mimeRaw = if (mimeIdx >= 0) cursor.getString(mimeIdx) else null
                        if (mimeRaw == DocumentsContract.Document.MIME_TYPE_DIR) {
                            if (current.depth < maxDepth &&
                                !displayName.equals("Android", ignoreCase = true) &&
                                !displayName.equals("LOST.DIR", ignoreCase = true)
                            ) {
                                queue.add(
                                    InspectNode(
                                        docId = childDocId,
                                        name = displayName,
                                        path = "${current.path}/$displayName",
                                        depth = current.depth + 1
                                    )
                                )
                            }
                        } else {
                            val ext = displayName.substringAfterLast('.', "").uppercase(Locale.US)
                            val mime = mimeRaw?.lowercase(Locale.US) ?: inferMimeFromExtension(ext)
                            val normExt = normalizeExtension(ext, mime)
                            if (mime.startsWith("audio/") || mime.startsWith("video/") || mime.startsWith("image/") ||
                                normExt in audioExtensions || normExt in videoExtensions || normExt in photoExtensions
                            ) {
                                directMedia++
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                // Skip unreadable dir cleanly
            }
            discovered += DiscoveredSubfolder(
                treeUriString = treeUriStr,
                docId = current.docId,
                displayName = current.name,
                parentPath = current.path,
                isRoot = current.depth == 0,
                directMediaCount = directMedia,
            )
        }
        discovered
    }

    /**
     * Phase 1 — Ultra-Fast Single-Query-Per-Directory SAF Traversal.
     * When [allowedDocIds] is non-null and non-empty, scans ONLY those exact checked folders
     * (never scanning outside the user's checkboxes!). Also respects [includeAudio], [includeVideo],
     * and [includePhoto] scope filters.
     */
    suspend fun scanFolderTreeFast(
        context: Context,
        treeUri: Uri,
        favoriteIds: Set<String> = emptySet(),
        cacheLookup: CachedMediaLookup = CachedMediaLookup(),
        allowedDocIds: Set<String>? = null,
        includeAudio: Boolean = true,
        includeVideo: Boolean = true,
        includePhoto: Boolean = true,
        onBatchDiscovered: (suspend (ScannedMediaResult, Int) -> Unit)? = null,
    ): ScannedMediaResult = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val sourceFolderUriStr = treeUri.toString()
        val rootDisplayName = resolveFolderDisplayName(context, treeUri)
        val rootDocId = runCatching { DocumentsContract.getTreeDocumentId(treeUri) }.getOrNull()
            ?: return@withContext ScannedMediaResult()
        val strictScope = !allowedDocIds.isNullOrEmpty()

        val audioAll = mutableListOf<AudioTrack>()
        val videosAll = mutableListOf<VideoItem>()
        val photosAll = mutableListOf<PhotoItem>()

        val batchAudio = mutableListOf<AudioTrack>()
        val batchVideos = mutableListOf<VideoItem>()
        val batchPhotos = mutableListOf<PhotoItem>()

        var totalFound = 0

        suspend fun flushBatch(force: Boolean = false) {
            val batchSize = batchAudio.size + batchVideos.size + batchPhotos.size
            if (batchSize == 0) return
            if (force || batchSize >= 24) {
                onBatchDiscovered?.invoke(
                    ScannedMediaResult(
                        audioTracks = batchAudio.toList(),
                        videos = batchVideos.toList(),
                        photos = batchPhotos.toList()
                    ),
                    totalFound
                )
                batchAudio.clear()
                batchVideos.clear()
                batchPhotos.clear()
            }
        }

        data class DirNode(val docId: String, val folderName: String, val depth: Int)
        val queue = ArrayDeque<DirNode>()
        if (strictScope && allowedDocIds != null) {
            // Strictly enqueue ONLY the user-checked folders (never scan unchecked folders!)
            allowedDocIds.forEach { checkedDocId ->
                val name = extractFolderNameFromDocId(checkedDocId).ifBlank { rootDisplayName }
                queue.add(DirNode(checkedDocId, name, 0))
            }
        } else {
            queue.add(DirNode(rootDocId, rootDisplayName, 0))
        }

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current.depth > 8) continue

            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, current.docId)
            try {
                resolver.query(childrenUri, docProjection, null, null, null)?.use { cursor ->
                    val idIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                    val nameIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                    val mimeIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
                    val sizeIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE)
                    val modIdx = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)

                    while (cursor.moveToNext()) {
                        val childDocId = if (idIdx >= 0) cursor.getString(idIdx) else continue
                        val displayName = (if (nameIdx >= 0) cursor.getString(nameIdx) else null) ?: continue
                        // Skip hidden/system cache files & folders (starts with '.') for instant discovery
                        if (displayName.startsWith(".")) continue

                        val mimeRaw = if (mimeIdx >= 0) cursor.getString(mimeIdx) else null
                        if (mimeRaw == DocumentsContract.Document.MIME_TYPE_DIR) {
                            // In strict checkbox scope, NEVER descend into unchecked subfolders!
                            if (strictScope) continue
                            if (current.depth <= 1 && (displayName.equals("Android", ignoreCase = true) ||
                                        displayName.equals("LOST.DIR", ignoreCase = true))
                            ) {
                                continue
                            }
                            queue.add(DirNode(childDocId, displayName, current.depth + 1))
                            continue
                        }

                        val sizeBytes = if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) cursor.getLong(sizeIdx) else 0L
                        val lastModified = if (modIdx >= 0 && !cursor.isNull(modIdx)) cursor.getLong(modIdx) else 0L
                        val fileUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, childDocId)
                        val uriStr = fileUri.toString()

                        val ext = displayName.substringAfterLast('.', "").uppercase(Locale.US)
                        val mime = mimeRaw?.lowercase(Locale.US) ?: inferMimeFromExtension(ext)
                        val normalizedExt = normalizeExtension(ext, mime)
                        val isFav = favoriteIds.contains(uriStr)

                        when {
                            includePhoto && (mime.startsWith("image/") || normalizedExt in photoExtensions) -> {
                                val cached = cacheLookup.photoById[uriStr]
                                val item = if (cached != null &&
                                    cached.sizeBytes == sizeBytes &&
                                    cached.dateModifiedMs == lastModified
                                ) {
                                    cached.copy(
                                        isFavorite = isFav,
                                        folderName = current.folderName,
                                        sourceFolderUri = sourceFolderUriStr,
                                    )
                                } else {
                                    PhotoItem(
                                        id = uriStr,
                                        title = displayName.substringBeforeLast('.').ifBlank { displayName },
                                        uriString = uriStr,
                                        extension = normalizedExt,
                                        mimeType = mime.ifBlank { "image/jpeg" },
                                        sizeBytes = sizeBytes,
                                        width = 0,
                                        height = 0,
                                        isFavorite = isFav,
                                        folderName = current.folderName,
                                        sourceFolderUri = sourceFolderUriStr,
                                        dateModifiedMs = lastModified,
                                    )
                                }
                                photosAll += item
                                batchPhotos += item
                                totalFound++
                            }

                            includeVideo && (mime.startsWith("video/") || (normalizedExt in videoExtensions && !mime.startsWith("audio/"))) -> {
                                val cached = cacheLookup.videoById[uriStr]
                                val item = if (cached != null &&
                                    cached.sizeBytes == sizeBytes &&
                                    cached.dateModifiedMs == lastModified &&
                                    cached.durationMs > 0L
                                ) {
                                    cached.copy(
                                        isFavorite = isFav,
                                        folderName = current.folderName,
                                        sourceFolderUri = sourceFolderUriStr,
                                    )
                                } else {
                                    VideoItem(
                                        id = uriStr,
                                        title = displayName.substringBeforeLast('.').ifBlank { displayName },
                                        uriString = uriStr,
                                        extension = normalizedExt,
                                        mimeType = mime.ifBlank { "video/*" },
                                        durationMs = 0L,
                                        sizeBytes = sizeBytes,
                                        resolution = "HD",
                                        isFavorite = isFav,
                                        folderName = current.folderName,
                                        sourceFolderUri = sourceFolderUriStr,
                                        dateModifiedMs = lastModified,
                                    )
                                }
                                videosAll += item
                                batchVideos += item
                                totalFound++
                            }

                            includeAudio && (mime.startsWith("audio/") || normalizedExt in audioExtensions) -> {
                                val cached = cacheLookup.audioById[uriStr]
                                val item = if (cached != null &&
                                    cached.sizeBytes == sizeBytes &&
                                    cached.dateModifiedMs == lastModified &&
                                    cached.durationMs > 0L
                                ) {
                                    cached.copy(
                                        isFavorite = isFav,
                                        folderName = current.folderName,
                                        sourceFolderUri = sourceFolderUriStr,
                                    )
                                } else {
                                    AudioTrack(
                                        id = uriStr,
                                        title = displayName.substringBeforeLast('.').ifBlank { displayName },
                                        artist = current.folderName.ifBlank { "Local Artist" },
                                        album = rootDisplayName.ifBlank { "Device Library" },
                                        uriString = uriStr,
                                        extension = normalizedExt,
                                        mimeType = mime.ifBlank { "audio/*" },
                                        durationMs = 0L,
                                        sizeBytes = sizeBytes,
                                        isFavorite = isFav,
                                        folderName = current.folderName,
                                        sourceFolderUri = sourceFolderUriStr,
                                        dateModifiedMs = lastModified,
                                    )
                                }
                                audioAll += item
                                batchAudio += item
                                totalFound++
                            }
                        }

                        if (batchAudio.size + batchVideos.size + batchPhotos.size >= 24) {
                            flushBatch(force = true)
                        }
                    }
                }
            } catch (_: Exception) {
                // Skip unreadable subdirectory without breaking the rest of the scan
            }
        }

        flushBatch(force = true)
        ScannedMediaResult(audioAll, videosAll, photosAll)
    }

    /**
     * Phase 2 — Parallel Metadata Enrichment.
     * Concurrently enriches ONLY audio tracks & videos that have durationMs == 0L (uncached/new),
     * bounded to 4 parallel MediaMetadataRetriever workers so the UI stays 120fps silky smooth.
     */
    suspend fun enrichMediaParallel(
        context: Context,
        discovered: ScannedMediaResult,
        onPartialUpdate: (suspend (List<AudioTrack>, List<VideoItem>, Int) -> Unit)? = null,
    ): ScannedMediaResult = withContext(Dispatchers.IO) {
        val semaphore = Semaphore(4)
        var enrichedCount = 0

        val audioNeedsMeta = discovered.audioTracks.filter { !it.isDemo && it.durationMs == 0L }
        val videosNeedMeta = discovered.videos.filter { !it.isDemo && it.durationMs == 0L }

        if (audioNeedsMeta.isEmpty() && videosNeedMeta.isEmpty()) {
            return@withContext discovered
        }

        val enrichedAudioMap = mutableMapOf<String, AudioTrack>()
        val enrichedVideoMap = mutableMapOf<String, VideoItem>()

        // Process in chunks of 16 so the UI receives smooth progressive metadata updates (artwork, artist, duration)
        audioNeedsMeta.chunked(16).forEach { chunk ->
            val results = coroutineScope {
                chunk.map { track ->
                    async {
                        semaphore.withPermit {
                            enrichSingleAudio(context, track)
                        }
                    }
                }.awaitAll()
            }
            results.forEach { enrichedAudioMap[it.id] = it }
            enrichedCount += results.size
            onPartialUpdate?.invoke(results, emptyList(), enrichedCount)
        }

        videosNeedMeta.chunked(12).forEach { chunk ->
            val results = coroutineScope {
                chunk.map { video ->
                    async {
                        semaphore.withPermit {
                            enrichSingleVideo(context, video)
                        }
                    }
                }.awaitAll()
            }
            results.forEach { enrichedVideoMap[it.id] = it }
            enrichedCount += results.size
            onPartialUpdate?.invoke(emptyList(), results, enrichedCount)
        }

        ScannedMediaResult(
            audioTracks = discovered.audioTracks.map { enrichedAudioMap[it.id] ?: it },
            videos = discovered.videos.map { enrichedVideoMap[it.id] ?: it },
            photos = discovered.photos,
        )
    }

    suspend fun scanPickedUris(
        context: Context,
        uris: List<Uri>,
        favoriteIds: Set<String> = emptySet(),
    ): ScannedMediaResult = withContext(Dispatchers.IO) {
        val audio = mutableListOf<AudioTrack>()
        val videos = mutableListOf<VideoItem>()
        val photos = mutableListOf<PhotoItem>()

        for (uri in uris) {
            val queryMeta = queryOpenableMeta(context.contentResolver, uri)
            val displayName = queryMeta.first
                ?: runCatching { DocumentFile.fromSingleUri(context, uri)?.name }.getOrNull()
                ?: uri.lastPathSegment
                ?: "media_item"
            val mimeHint = context.contentResolver.getType(uri)
            val sizeHint = queryMeta.second
            val ext = displayName.substringAfterLast('.', "").uppercase(Locale.US)
            val mime = mimeHint?.lowercase(Locale.US) ?: inferMimeFromExtension(ext)
            val normalizedExt = normalizeExtension(ext, mime)
            val uriStr = uri.toString()
            val isFav = favoriteIds.contains(uriStr)

            when {
                mime.startsWith("image/") || normalizedExt in photoExtensions -> {
                    photos += PhotoItem(
                        id = uriStr,
                        title = displayName.substringBeforeLast('.').ifBlank { displayName },
                        uriString = uriStr,
                        extension = normalizedExt,
                        mimeType = mime.ifBlank { "image/jpeg" },
                        sizeBytes = sizeHint,
                        width = 0,
                        height = 0,
                        isFavorite = isFav,
                        folderName = "Imported",
                    )
                }

                mime.startsWith("video/") || (normalizedExt in videoExtensions && !mime.startsWith("audio/")) -> {
                    videos += VideoItem(
                        id = uriStr,
                        title = displayName.substringBeforeLast('.').ifBlank { displayName },
                        uriString = uriStr,
                        extension = normalizedExt,
                        mimeType = mime.ifBlank { "video/*" },
                        durationMs = 0L,
                        sizeBytes = sizeHint,
                        resolution = "HD",
                        isFavorite = isFav,
                        folderName = "Imported",
                    )
                }

                mime.startsWith("audio/") || normalizedExt in audioExtensions -> {
                    audio += AudioTrack(
                        id = uriStr,
                        title = displayName.substringBeforeLast('.').ifBlank { displayName },
                        artist = "Local Artist",
                        album = "Imported",
                        uriString = uriStr,
                        extension = normalizedExt,
                        mimeType = mime.ifBlank { "audio/*" },
                        durationMs = 0L,
                        sizeBytes = sizeHint,
                        isFavorite = isFav,
                        folderName = "Imported",
                    )
                }
            }
        }
        enrichMediaParallel(context, ScannedMediaResult(audio, videos, photos))
    }

    private fun enrichSingleAudio(context: Context, track: AudioTrack): AudioTrack {
        val uri = Uri.parse(track.uriString)
        val retriever = MediaMetadataRetriever()
        var title = track.title
        var artist = track.artist
        var album = track.album
        var durationMs = track.durationMs
        var artUriStr = track.artworkUriString

        try {
            retriever.setDataSource(context, uri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?.takeIf { it.isNotBlank() }?.let { title = it }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?.takeIf { it.isNotBlank() }?.let { artist = it }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                ?.takeIf { it.isNotBlank() }?.let { album = it }
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()?.let { durationMs = it }
            if (artUriStr == null) {
                val artBytes = retriever.embeddedPicture
                if (artBytes != null && artBytes.isNotEmpty()) {
                    val artDir = File(context.cacheDir, "artwork").apply { mkdirs() }
                    val artFile = File(artDir, "${track.uriString.hashCode().toUInt()}.jpg")
                    if (!artFile.exists()) {
                        artFile.writeBytes(artBytes)
                    }
                    artUriStr = Uri.fromFile(artFile).toString()
                }
            }
        } catch (_: Exception) {
        } finally {
            runCatching { retriever.release() }
        }

        return track.copy(
            title = title,
            artist = artist,
            album = album,
            durationMs = durationMs,
            artworkUriString = artUriStr,
        )
    }

    private fun enrichSingleVideo(context: Context, video: VideoItem): VideoItem {
        val uri = Uri.parse(video.uriString)
        val retriever = MediaMetadataRetriever()
        var durationMs = video.durationMs
        var resolution = video.resolution

        try {
            retriever.setDataSource(context, uri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()?.let { durationMs = it }
            val w = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()
            val h = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()
            if (w != null && h != null && w > 0 && h > 0) {
                resolution = "${w}x${h}"
            }
        } catch (_: Exception) {
        } finally {
            runCatching { retriever.release() }
        }

        return video.copy(
            durationMs = durationMs,
            resolution = resolution,
        )
    }

    /**
     * Detects likely duplicate media files across folders by normalized title + file size (bytes).
     */
    fun findDuplicateGroups(
        audioTracks: List<AudioTrack>,
        videos: List<VideoItem>,
        photos: List<PhotoItem>,
    ): List<DuplicateGroup> {
        fun normalizeKey(title: String): String = title
            .trim()
            .lowercase(Locale.US)
            .replace(Regex("\\s*\\(\\d+\\)$"), "")
            .replace(Regex("\\s*-\\s*copy$"), "")
            .replace(Regex("\\s+"), " ")

        val groups = mutableListOf<DuplicateGroup>()

        audioTracks
            .filterNot { it.isDemo }
            .groupBy { "${normalizeKey(it.title)}|${it.sizeBytes}" }
            .forEach { (key, list) ->
                if (list.size >= 2) {
                    groups += DuplicateGroup(
                        key = "aud_$key",
                        displayTitle = list.first().title,
                        sizeBytes = list.first().sizeBytes,
                        itemIds = list.map { it.id },
                        mediaType = DuplicateMediaType.AUDIO,
                    )
                }
            }

        videos
            .filterNot { it.isDemo }
            .groupBy { "${normalizeKey(it.title)}|${it.sizeBytes}" }
            .forEach { (key, list) ->
                if (list.size >= 2) {
                    groups += DuplicateGroup(
                        key = "vid_$key",
                        displayTitle = list.first().title,
                        sizeBytes = list.first().sizeBytes,
                        itemIds = list.map { it.id },
                        mediaType = DuplicateMediaType.VIDEO,
                    )
                }
            }

        photos
            .filterNot { it.isDemo }
            .groupBy { "${normalizeKey(it.title)}|${it.sizeBytes}" }
            .forEach { (key, list) ->
                if (list.size >= 2) {
                    groups += DuplicateGroup(
                        key = "img_$key",
                        displayTitle = list.first().title,
                        sizeBytes = list.first().sizeBytes,
                        itemIds = list.map { it.id },
                        mediaType = DuplicateMediaType.PHOTO,
                    )
                }
            }

        return groups.sortedByDescending { it.sizeBytes * (it.itemIds.size - 1) }
    }

    private fun normalizeExtension(ext: String, mime: String): String = when {
        ext == "JPEG" -> "JPG"
        ext.isNotBlank() -> ext
        mime.startsWith("audio/") -> mime.substringAfter('/').uppercase(Locale.US)
        mime.startsWith("video/") -> mime.substringAfter('/').uppercase(Locale.US)
        mime.startsWith("image/") -> mime.substringAfter('/').uppercase(Locale.US)
        else -> "FILE"
    }

    private fun queryOpenableMeta(resolver: ContentResolver, uri: Uri): Pair<String?, Long> {
        var name: String? = null
        var size = 0L
        try {
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (nameIdx >= 0) name = cursor.getString(nameIdx)
                        if (sizeIdx >= 0 && !cursor.isNull(sizeIdx)) size = cursor.getLong(sizeIdx)
                    }
                }
        } catch (_: Exception) {
        }
        return Pair(name, size)
    }

    private fun inferMimeFromExtension(ext: String): String = when (ext) {
        "MP3" -> "audio/mpeg"
        "FLAC" -> "audio/flac"
        "WAV" -> "audio/wav"
        "OGG" -> "audio/ogg"
        "AAC" -> "audio/aac"
        "M4A" -> "audio/mp4"
        "OPUS" -> "audio/opus"
        "WEBM" -> "video/webm"
        "MP4" -> "video/mp4"
        "MKV" -> "video/x-matroska"
        "MOV" -> "video/quicktime"
        "AVI" -> "video/x-msvideo"
        "JPG", "JPEG" -> "image/jpeg"
        "PNG" -> "image/png"
        "WEBP" -> "image/webp"
        "GIF" -> "image/gif"
        "SVG" -> "image/svg+xml"
        else -> ""
    }

    fun loadDemoMedia(context: Context, favoriteIds: Set<String> = emptySet()): ScannedMediaResult {
        val pkg = context.packageName
        fun rawUri(resId: Int) = "android.resource://$pkg/$resId"
        fun drawableUri(resId: Int) = "android.resource://$pkg/$resId"

        val demoAudio = listOf(
            AudioTrack(
                id = "demo_audio_1",
                title = "Aurora Nocturne",
                artist = "Prism Analog Ensemble",
                album = "Lavender Sessions",
                uriString = rawUri(R.raw.demo_aurora_synth),
                extension = "MP3",
                mimeType = "audio/mpeg",
                durationMs = 18_000L,
                sizeBytes = 288_000L,
                artworkResId = R.drawable.img_demo_album_one,
                isFavorite = favoriteIds.contains("demo_audio_1"),
                isDemo = true,
                folderName = "Prism Studio",
            ),
            AudioTrack(
                id = "demo_audio_2",
                title = "Velvet Horizon (Lossless)",
                artist = "Nadia Al-Mansoor",
                album = "Studio Master 24-bit",
                uriString = rawUri(R.raw.demo_velvet_horizon),
                extension = "FLAC",
                mimeType = "audio/flac",
                durationMs = 16_000L,
                sizeBytes = 742_000L,
                artworkResId = R.drawable.img_demo_album_two,
                isFavorite = favoriteIds.contains("demo_audio_2"),
                isDemo = true,
                folderName = "Studio Masters",
            ),
            AudioTrack(
                id = "demo_audio_3",
                title = "Desert Echoes (Uncompressed)",
                artist = "Sama Acoustic Lab",
                album = "Dune Resonances",
                uriString = rawUri(R.raw.demo_desert_echo),
                extension = "WAV",
                mimeType = "audio/wav",
                durationMs = 12_000L,
                sizeBytes = 529_244L,
                artworkResId = R.drawable.img_demo_photo_one,
                isFavorite = favoriteIds.contains("demo_audio_3"),
                isDemo = true,
                folderName = "Studio Masters",
            ),
            AudioTrack(
                id = "demo_audio_4",
                title = "Oasis Drift",
                artist = "Kareem & The Synths",
                album = "Mirage FM",
                uriString = rawUri(R.raw.demo_oasis_drift),
                extension = "OGG",
                mimeType = "audio/ogg",
                durationMs = 15_000L,
                sizeBytes = 192_000L,
                artworkResId = R.drawable.img_onboarding_hero,
                isFavorite = favoriteIds.contains("demo_audio_4"),
                isDemo = true,
                folderName = "Prism Studio",
            ),
            AudioTrack(
                id = "demo_audio_5",
                title = "Midnight Oud",
                artist = "Layla Trio",
                album = "Levant Nights",
                uriString = rawUri(R.raw.demo_midnight_oud),
                extension = "M4A",
                mimeType = "audio/mp4",
                durationMs = 14_000L,
                sizeBytes = 175_000L,
                artworkResId = R.drawable.img_demo_album_one,
                isFavorite = favoriteIds.contains("demo_audio_5"),
                isDemo = true,
                folderName = "Prism Studio",
            ),
            AudioTrack(
                id = "demo_audio_6_short",
                title = "Crystal Chime (Short Clip)",
                artist = "Prism Foley",
                album = "UI Micro-Sounds",
                uriString = rawUri(R.raw.demo_chime_clip),
                extension = "MP3",
                mimeType = "audio/mpeg",
                durationMs = 3_000L,
                sizeBytes = 38_000L,
                artworkResId = R.drawable.img_demo_album_two,
                isFavorite = favoriteIds.contains("demo_audio_6_short"),
                isDemo = true,
                folderName = "Prism Studio",
            ),
        )

        val demoVideos = listOf(
            VideoItem(
                id = "demo_video_1",
                title = "Golden Hour Concrete Arches",
                uriString = rawUri(R.raw.demo_cinema_arches),
                extension = "MP4",
                mimeType = "video/mp4",
                durationMs = 10_000L,
                sizeBytes = 848_000L,
                resolution = "720x406",
                thumbnailResId = R.drawable.img_demo_photo_one,
                isFavorite = favoriteIds.contains("demo_video_1"),
                isDemo = true,
                folderName = "Cinema Demos",
            ),
            VideoItem(
                id = "demo_video_2",
                title = "Prism Aurora Spectrum Loop",
                uriString = rawUri(R.raw.demo_aurora_loop),
                extension = "WEBM",
                mimeType = "video/webm",
                durationMs = 8_000L,
                sizeBytes = 285_000L,
                resolution = "720x406",
                thumbnailResId = R.drawable.img_onboarding_hero,
                isFavorite = favoriteIds.contains("demo_video_2"),
                isDemo = true,
                folderName = "Prism Studio",
            ),
        )

        val demoPhotos = listOf(
            PhotoItem(
                id = "demo_photo_1",
                title = "Solstice Arches",
                uriString = drawableUri(R.drawable.img_demo_photo_one),
                extension = "JPG",
                mimeType = "image/jpeg",
                sizeBytes = 412_000L,
                width = 1280,
                height = 960,
                resId = R.drawable.img_demo_photo_one,
                isFavorite = favoriteIds.contains("demo_photo_1"),
                isDemo = true,
                folderName = "Architecture",
            ),
            PhotoItem(
                id = "demo_photo_2",
                title = "Iridescent Violet Orb",
                uriString = drawableUri(R.drawable.img_demo_album_one),
                extension = "JPG",
                mimeType = "image/jpeg",
                sizeBytes = 365_000L,
                width = 1024,
                height = 1024,
                resId = R.drawable.img_demo_album_one,
                isFavorite = favoriteIds.contains("demo_photo_2"),
                isDemo = true,
                folderName = "Abstract Art",
            ),
            PhotoItem(
                id = "demo_photo_3",
                title = "Amber & Emerald Resonance",
                uriString = drawableUri(R.drawable.img_demo_album_two),
                extension = "JPG",
                mimeType = "image/jpeg",
                sizeBytes = 398_000L,
                width = 1024,
                height = 1024,
                resId = R.drawable.img_demo_album_two,
                isFavorite = favoriteIds.contains("demo_photo_3"),
                isDemo = true,
                folderName = "Abstract Art",
            ),
            PhotoItem(
                id = "demo_photo_4",
                title = "Prism Studio Aperture",
                uriString = drawableUri(R.drawable.img_onboarding_hero),
                extension = "JPG",
                mimeType = "image/jpeg",
                sizeBytes = 440_000L,
                width = 1376,
                height = 768,
                resId = R.drawable.img_onboarding_hero,
                isFavorite = favoriteIds.contains("demo_photo_4"),
                isDemo = true,
                folderName = "Architecture",
            ),
        )

        return ScannedMediaResult(
            audioTracks = demoAudio,
            videos = demoVideos,
            photos = demoPhotos,
        )
    }

    fun formatDuration(ms: Long): String {
        if (ms <= 0L) return "0:00"
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%d:%02d", minutes, seconds)
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes <= 0L) return "—"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format(Locale.US, "%.2f GB", gb)
            mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
            else -> String.format(Locale.US, "%.0f KB", kb.coerceAtLeast(1.0))
        }
    }
}
