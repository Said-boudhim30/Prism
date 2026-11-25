package com.example.data

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.model.AudioOutputProfileType
import com.example.model.AudioTrack
import com.example.model.PhotoCropAspect
import com.example.model.PhotoItem
import com.example.model.UserPlaylist
import com.example.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import kotlin.math.min
import kotlin.math.roundToInt

data class PhotoCompressResult(
    val success: Boolean,
    val originalBytes: Long = 0L,
    val compressedBytes: Long = 0L,
    val savedFile: File? = null,
    val error: String? = null,
)

data class VideoAudioExtractResult(
    val success: Boolean,
    val extractedTrack: AudioTrack? = null,
    val error: String? = null,
)

data class AdhkarEntry(
    val id: String,
    val isMorning: Boolean,
    val titleAr: String,
    val titleEn: String,
    val arabicText: String,
    val translationEn: String,
    val repeatTarget: Int,
)

/**
 * Unified Native Android Media Tools Engine:
 * - Real Video -> Audio (.m4a) extractor via MediaExtractor + MediaMuxer
 * - Real M3U / M3U8 playlist export & import parser
 * - Real multi-page Photos -> PDF generator via android.graphics.pdf.PdfDocument
 * - Real Bitmap Photo Studio Editor (Crop 1:1 / 4:3 / 16:9, Rotate 90deg, Brightness/Contrast/Saturation)
 * - Real JPEG Photo Compressor with before/after byte savings
 * - Wallpaper Setter via Android WallpaperManager / system crop-and-set intent
 * - Fast Video Seekbar Frame Scrubbing via MediaMetadataRetriever
 * - Real-time Audio Output Device detection (Speaker / Wired Headset / Bluetooth)
 * - Authentic Morning & Evening Adhkar Catalog
 */
object MediaToolsEngine {

    // 1. Real Audio Output Device Detection (zero special permissions required!)
    fun detectCurrentAudioOutputType(context: Context): AudioOutputProfileType {
        return runCatching {
            val am = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                ?: return AudioOutputProfileType.SPEAKER
            val outputs = am.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val hasBluetooth = outputs.any { dev ->
                dev.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    dev.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && (
                        dev.type == AudioDeviceInfo.TYPE_BLE_HEADSET ||
                            dev.type == AudioDeviceInfo.TYPE_BLE_SPEAKER
                        ))
            }
            if (hasBluetooth) return AudioOutputProfileType.BLUETOOTH

            val hasWired = outputs.any { dev ->
                dev.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                    dev.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                    dev.type == AudioDeviceInfo.TYPE_USB_HEADSET ||
                    dev.type == AudioDeviceInfo.TYPE_USB_DEVICE
            }
            if (hasWired) AudioOutputProfileType.WIRED_HEADSET else AudioOutputProfileType.SPEAKER
        }.getOrDefault(AudioOutputProfileType.SPEAKER)
    }

    // 2. Real M3U Playlist Export & Import
    fun exportPlaylistToM3u(playlist: UserPlaylist, allTracks: List<AudioTrack>): String {
        val byId = allTracks.associateBy { it.id }
        val sb = StringBuilder()
        sb.appendLine("#EXTM3U")
        sb.appendLine("#PLAYLIST:${playlist.name}")
        playlist.trackIds.forEach { id ->
            val track = byId[id] ?: return@forEach
            val durationSec = (track.durationMs / 1000L).coerceAtLeast(0L)
            sb.appendLine("#EXTINF:$durationSec,${track.artist} - ${track.title}")
            sb.appendLine(track.uriString.ifBlank { track.title })
        }
        return sb.toString()
    }

    fun parseM3uPlaylist(
        rawContent: String,
        fallbackName: String,
        libraryTracks: List<AudioTrack>,
    ): Pair<String, List<String>> {
        var playlistName = fallbackName.ifBlank { "Imported Playlist" }
        val matchedIds = mutableListOf<String>()
        val byUri = libraryTracks.associateBy { it.uriString.trim().lowercase() }
        val byTitle = libraryTracks.associateBy { it.title.trim().lowercase() }

        var pendingTitleHint: String? = null
        rawContent.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.forEach { line ->
            when {
                line.startsWith("#PLAYLIST:", ignoreCase = true) -> {
                    val candidate = line.substringAfter(":").trim()
                    if (candidate.isNotBlank()) playlistName = candidate
                }
                line.startsWith("#EXTINF:", ignoreCase = true) -> {
                    val info = line.substringAfter(",", "").trim()
                    pendingTitleHint = info.substringAfter(" - ", info).trim().lowercase()
                }
                !line.startsWith("#") -> {
                    val lower = line.lowercase()
                    val direct = byUri[lower]
                        ?: byTitle[lower]
                        ?: pendingTitleHint?.let { hint ->
                            byTitle[hint] ?: libraryTracks.firstOrNull {
                                it.title.lowercase().contains(hint) || hint.contains(it.title.lowercase())
                            }
                        }
                        ?: libraryTracks.firstOrNull {
                            val fileName = line.substringAfterLast('/').substringBeforeLast('.').lowercase()
                            fileName.isNotBlank() && it.title.lowercase().contains(fileName)
                        }
                    if (direct != null && direct.id !in matchedIds) {
                        matchedIds.add(direct.id)
                    }
                    pendingTitleHint = null
                }
            }
        }
        return playlistName to matchedIds
    }

    // 3. Real Video-to-Audio (.m4a) Remuxer via MediaExtractor + MediaMuxer
    suspend fun extractAudioFromVideo(
        context: Context,
        video: VideoItem,
    ): VideoAudioExtractResult = withContext(Dispatchers.IO) {
        val extractor = MediaExtractor()
        var muxer: MediaMuxer? = null
        try {
            val uri = Uri.parse(video.uriString)
            extractor.setDataSource(context, uri, null)

            var audioTrackIndex = -1
            var audioFormat: MediaFormat? = null
            for (i in 0 until extractor.trackCount) {
                val fmt = extractor.getTrackFormat(i)
                val mime = fmt.getString(MediaFormat.KEY_MIME).orEmpty()
                if (mime.startsWith("audio/")) {
                    audioTrackIndex = i
                    audioFormat = fmt
                    break
                }
            }
            if (audioTrackIndex < 0 || audioFormat == null) {
                return@withContext VideoAudioExtractResult(false, error = "No audio track found in video")
            }

            extractor.selectTrack(audioTrackIndex)
            val exportDir = File(context.filesDir, "extracted_audio").apply { mkdirs() }
            val safeName = video.title.replace(Regex("[^a-zA-Z0-9\\u0600-\\u06FF _-]"), "").trim().ifBlank { "audio_${System.currentTimeMillis()}" }
            val outFile = File(exportDir, "${safeName}_${System.currentTimeMillis() % 10000}.m4a")

            muxer = MediaMuxer(outFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            val muxTrackIdx = muxer.addTrack(audioFormat)
            muxer.start()

            val maxInput = runCatching { audioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE) }
                .getOrDefault(256 * 1024).coerceIn(64 * 1024, 1024 * 1024)
            val buffer = ByteBuffer.allocate(maxInput)
            val info = MediaCodec.BufferInfo()

            while (true) {
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break
                info.offset = 0
                info.size = sampleSize
                info.presentationTimeUs = extractor.sampleTime.coerceAtLeast(0L)
                info.flags = if ((extractor.sampleFlags and MediaExtractor.SAMPLE_FLAG_SYNC) != 0) {
                    MediaCodec.BUFFER_FLAG_KEY_FRAME
                } else 0
                muxer.writeSampleData(muxTrackIdx, buffer, info)
                extractor.advance()
            }

            muxer.stop()
            muxer.release()
            muxer = null

            val fileSize = outFile.length().coerceAtLeast(1L)
            val durationMs = runCatching {
                if (audioFormat.containsKey(MediaFormat.KEY_DURATION)) {
                    audioFormat.getLong(MediaFormat.KEY_DURATION) / 1000L
                } else video.durationMs
            }.getOrDefault(video.durationMs).coerceAtLeast(1000L)

            val track = AudioTrack(
                id = "extracted_${outFile.nameWithoutExtension}_${System.currentTimeMillis()}",
                title = "${video.title} (Audio)",
                artist = "Extracted Audio",
                album = video.folderName.ifBlank { "Prism Studio" },
                uriString = Uri.fromFile(outFile).toString(),
                extension = "M4A",
                mimeType = "audio/mp4",
                durationMs = durationMs,
                sizeBytes = fileSize,
                folderName = "Extracted Audio",
                isDemo = false,
            )
            VideoAudioExtractResult(success = true, extractedTrack = track)
        } catch (e: Exception) {
            runCatching { muxer?.release() }
            VideoAudioExtractResult(success = false, error = e.message ?: "Extraction failed")
        } finally {
            runCatching { extractor.release() }
        }
    }

    // 4. Real Multi-Page Photos -> PDF Builder (android.graphics.pdf.PdfDocument)
    suspend fun exportPhotosToPdf(
        context: Context,
        photos: List<PhotoItem>,
        documentTitle: String = "Prism_Photos",
    ): File? = withContext(Dispatchers.IO) {
        if (photos.isEmpty()) return@withContext null
        val pdf = PdfDocument()
        try {
            val pageW = 1080
            val pageH = 1528 // Clean A4-proportioned HD page
            val margin = 48
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

            photos.forEachIndexed { idx, item ->
                val bmp = decodePhotoBitmap(context, item, maxDim = 1280) ?: return@forEachIndexed
                val pageInfo = PdfDocument.PageInfo.Builder(pageW, pageH, idx + 1).create()
                val page = pdf.startPage(pageInfo)
                val canvas = page.canvas
                canvas.drawColor(android.graphics.Color.WHITE)

                val availW = pageW - margin * 2
                val availH = pageH - margin * 2 - 48
                val scale = min(availW.toFloat() / bmp.width.coerceAtLeast(1), availH.toFloat() / bmp.height.coerceAtLeast(1))
                val drawW = (bmp.width * scale).roundToInt().coerceAtLeast(1)
                val drawH = (bmp.height * scale).roundToInt().coerceAtLeast(1)
                val left = (pageW - drawW) / 2
                val top = margin + (availH - drawH) / 2
                canvas.drawBitmap(bmp, null, Rect(left, top, left + drawW, top + drawH), paint)

                val captionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 24f
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText(item.title.take(46), pageW / 2f, (pageH - 28).toFloat(), captionPaint)
                pdf.finishPage(page)
                bmp.recycle()
            }

            if (pdf.pages.isEmpty()) return@withContext null
            val outDir = File(context.cacheDir, "prism_pdfs").apply { mkdirs() }
            val safeName = documentTitle.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "Prism_Photos" }
            val outFile = File(outDir, "${safeName}_${System.currentTimeMillis() % 100000}.pdf")
            FileOutputStream(outFile).use { pdf.writeTo(it) }
            outFile
        } catch (_: Exception) {
            null
        } finally {
            pdf.close()
        }
    }

    fun shareFile(context: Context, file: File, mimeType: String, chooserTitle: String) {
        runCatching {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(
                Intent.createChooser(intent, chooserTitle).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    // 5. Real Bitmap Photo Editor (Rotate 90deg + Crop Aspect + Artistic Filter + Brightness/Contrast/Saturation + Watermark -> Save to Gallery)
    suspend fun editAndSavePhoto(
        context: Context,
        photo: PhotoItem,
        rotationDegrees: Float,
        cropAspect: PhotoCropAspect,
        brightness: Float, // -100..+100
        contrast: Float,   // 0.5..1.8
        saturation: Float, // 0.0..2.0
        artFilter: com.example.model.PhotoArtFilter = com.example.model.PhotoArtFilter.ORIGINAL,
        watermarkText: String = "",
    ): Boolean = withContext(Dispatchers.IO) {
        val src = decodePhotoBitmap(context, photo, maxDim = 2048) ?: return@withContext false
        try {
            val transformed = applyPhotoEditsToBitmap(
                source = src,
                rotationDegrees = rotationDegrees,
                cropAspect = cropAspect,
                brightness = brightness,
                contrast = contrast,
                saturation = saturation,
                artFilter = artFilter,
                watermarkText = watermarkText,
            )
            val saved = saveBitmapToGallery(context, transformed, "${photo.title}_edited")
            if (transformed !== src) transformed.recycle()
            src.recycle()
            saved
        } catch (_: Exception) {
            src.recycle()
            false
        }
    }

    fun buildPhotoColorMatrix(
        brightness: Float = 0f,
        contrast: Float = 1f,
        saturation: Float = 1f,
        artFilter: com.example.model.PhotoArtFilter = com.example.model.PhotoArtFilter.ORIGINAL,
    ): ColorMatrix {
        val effectiveSat = when (artFilter) {
            com.example.model.PhotoArtFilter.NOIR -> 0f
            com.example.model.PhotoArtFilter.VIVID -> (saturation * 1.38f).coerceIn(0f, 2f)
            else -> saturation.coerceIn(0f, 2f)
        }
        val satMatrix = ColorMatrix().apply { setSaturation(effectiveSat) }
        val c = when (artFilter) {
            com.example.model.PhotoArtFilter.NOIR -> (contrast * 1.18f).coerceIn(0.5f, 1.9f)
            com.example.model.PhotoArtFilter.VIVID -> (contrast * 1.08f).coerceIn(0.5f, 1.9f)
            else -> contrast.coerceIn(0.5f, 1.8f)
        }
        val t = (1f - c) * 128f + brightness.coerceIn(-100f, 100f)
        val bcMatrix = ColorMatrix(
            floatArrayOf(
                c, 0f, 0f, 0f, t,
                0f, c, 0f, 0f, t,
                0f, 0f, c, 0f, t,
                0f, 0f, 0f, 1f, 0f,
            )
        )
        satMatrix.postConcat(bcMatrix)

        val toneMatrix: ColorMatrix? = when (artFilter) {
            com.example.model.PhotoArtFilter.WARM -> ColorMatrix(
                floatArrayOf(
                    1.12f, 0f, 0f, 0f, 10f,
                    0f, 1.03f, 0f, 0f, 4f,
                    0f, 0f, 0.88f, 0f, -6f,
                    0f, 0f, 0f, 1f, 0f,
                )
            )
            com.example.model.PhotoArtFilter.COOL -> ColorMatrix(
                floatArrayOf(
                    0.90f, 0f, 0f, 0f, -4f,
                    0f, 1.02f, 0f, 0f, 4f,
                    0f, 0f, 1.16f, 0f, 12f,
                    0f, 0f, 0f, 1f, 0f,
                )
            )
            com.example.model.PhotoArtFilter.EMERALD -> ColorMatrix(
                floatArrayOf(
                    0.92f, 0f, 0f, 0f, -2f,
                    0f, 1.14f, 0f, 0f, 8f,
                    0f, 0f, 1.04f, 0f, 4f,
                    0f, 0f, 0f, 1f, 0f,
                )
            )
            else -> null
        }
        if (toneMatrix != null) satMatrix.postConcat(toneMatrix)
        return satMatrix
    }

    fun applyPhotoEditsToBitmap(
        source: Bitmap,
        rotationDegrees: Float = 0f,
        cropAspect: PhotoCropAspect = PhotoCropAspect.ORIGINAL,
        brightness: Float = 0f,
        contrast: Float = 1f,
        saturation: Float = 1f,
        artFilter: com.example.model.PhotoArtFilter = com.example.model.PhotoArtFilter.ORIGINAL,
        watermarkText: String = "",
    ): Bitmap {
        // Step 1: Rotation
        val rotated = if (rotationDegrees % 360f != 0f) {
            val m = Matrix().apply { postRotate(rotationDegrees) }
            Bitmap.createBitmap(source, 0, 0, source.width, source.height, m, true)
        } else {
            source
        }

        // Step 2: Center Aspect Crop
        val targetRatio = when (cropAspect) {
            PhotoCropAspect.ORIGINAL -> null
            PhotoCropAspect.SQUARE_1_1 -> 1f
            PhotoCropAspect.CLASSIC_4_3 -> 4f / 3f
            PhotoCropAspect.WIDE_16_9 -> 16f / 9f
        }
        val cropped = if (targetRatio != null && rotated.width > 2 && rotated.height > 2) {
            val curRatio = rotated.width.toFloat() / rotated.height.toFloat()
            val cropW: Int
            val cropH: Int
            if (curRatio > targetRatio) {
                cropH = rotated.height
                cropW = (cropH * targetRatio).roundToInt().coerceIn(1, rotated.width)
            } else {
                cropW = rotated.width
                cropH = (cropW / targetRatio).roundToInt().coerceIn(1, rotated.height)
            }
            val startX = ((rotated.width - cropW) / 2).coerceAtLeast(0)
            val startY = ((rotated.height - cropH) / 2).coerceAtLeast(0)
            Bitmap.createBitmap(rotated, startX, startY, cropW, cropH)
        } else {
            rotated
        }

        // Step 3: Artistic Filter + Brightness + Contrast + Saturation ColorMatrix
        val out = Bitmap.createBitmap(cropped.width, cropped.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        val colorMatrix = buildPhotoColorMatrix(
            brightness = brightness,
            contrast = contrast,
            saturation = saturation,
            artFilter = artFilter,
        )
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }
        canvas.drawBitmap(cropped, 0f, 0f, paint)

        // Step 4: Optional Custom Text / Signature Watermark
        val cleanStamp = watermarkText.trim()
        if (cleanStamp.isNotEmpty() && out.width >= 32 && out.height >= 32) {
            val textSizePx = (minOf(out.width, out.height) * 0.055f).coerceIn(18f, 72f)
            val stampPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textSize = textSizePx
                textAlign = Paint.Align.RIGHT
                isFakeBoldText = true
                setShadowLayer(6f, 2f, 2f, android.graphics.Color.argb(190, 0, 0, 0))
            }
            val padX = out.width * 0.045f
            val padY = out.height * 0.055f
            canvas.drawText(cleanStamp.take(40), out.width - padX, out.height - padY, stampPaint)
        }

        if (cropped !== rotated && cropped !== source) cropped.recycle()
        if (rotated !== source) rotated.recycle()
        return out
    }

    // 5b. Real Lossless MP4 Video Trimmer (MediaExtractor + MediaMuxer)
    data class VideoTrimResult(
        val success: Boolean,
        val trimmedVideo: VideoItem? = null,
        val error: String? = null,
    )

    suspend fun trimVideoMp4(
        context: Context,
        video: VideoItem,
        startMs: Long,
        endMs: Long,
    ): VideoTrimResult = withContext(Dispatchers.IO) {
        if (video.uriString.isBlank() || endMs <= startMs + 500L) {
            return@withContext VideoTrimResult(false, error = "Invalid trim range")
        }
        val extractor = MediaExtractor()
        var muxer: MediaMuxer? = null
        try {
            val uri = Uri.parse(video.uriString)
            extractor.setDataSource(context, uri, null)
            val outDir = File(context.cacheDir, "prism_trimmed_videos").apply { mkdirs() }
            val safeName = video.title.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(32).ifBlank { "prism_clip" }
            val outFile = File(outDir, "${safeName}_trim_${System.currentTimeMillis() % 100000}.mp4")
            muxer = MediaMuxer(outFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            val trackIndexMap = mutableMapOf<Int, Int>()
            var maxInputSize = 256 * 1024
            for (i in 0 until extractor.trackCount) {
                val fmt = extractor.getTrackFormat(i)
                val mime = fmt.getString(MediaFormat.KEY_MIME).orEmpty()
                if (mime.startsWith("video/") || mime.startsWith("audio/")) {
                    extractor.selectTrack(i)
                    trackIndexMap[i] = muxer.addTrack(fmt)
                    if (fmt.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
                        maxInputSize = maxOf(maxInputSize, fmt.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE))
                    }
                }
            }
            if (trackIndexMap.isEmpty()) {
                return@withContext VideoTrimResult(false, error = "No video/audio tracks found")
            }

            muxer.start()
            val startUs = startMs.coerceAtLeast(0L) * 1000L
            val endUs = endMs * 1000L
            extractor.seekTo(startUs, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

            val buffer = ByteBuffer.allocate(maxInputSize.coerceIn(256 * 1024, 4 * 1024 * 1024))
            val info = MediaCodec.BufferInfo()
            var basePtsUs = -1L

            while (true) {
                val sampleSize = extractor.readSampleData(buffer, 0)
                if (sampleSize < 0) break
                val ptsUs = extractor.sampleTime
                if (ptsUs > endUs) break
                val srcTrack = extractor.sampleTrackIndex
                val dstTrack = trackIndexMap[srcTrack]
                if (dstTrack != null && ptsUs >= startUs) {
                    if (basePtsUs < 0L) basePtsUs = ptsUs
                    info.offset = 0
                    info.size = sampleSize
                    info.presentationTimeUs = (ptsUs - basePtsUs).coerceAtLeast(0L)
                    info.flags = extractor.sampleFlags
                    muxer.writeSampleData(dstTrack, buffer, info)
                }
                extractor.advance()
            }

            muxer.stop()
            muxer.release()
            muxer = null
            extractor.release()

            val fileUri = Uri.fromFile(outFile)
            val clipDurationMs = (endMs - startMs).coerceAtLeast(500L)
            val newVideo = VideoItem(
                id = "trim_${System.currentTimeMillis()}",
                title = "${video.title} (Trim)",
                uriString = fileUri.toString(),
                extension = "MP4",
                mimeType = "video/mp4",
                durationMs = clipDurationMs,
                sizeBytes = outFile.length(),
                resolution = video.resolution.ifBlank { "HD" },
                folderName = "Prism Clips",
                isFavorite = false,
                isDemo = false,
            )
            VideoTrimResult(true, trimmedVideo = newVideo)
        } catch (e: Exception) {
            runCatching { muxer?.release() }
            runCatching { extractor.release() }
            VideoTrimResult(false, error = e.message ?: "Trim failed")
        }
    }

    // 6. Real Photo Compressor (JPEG quality 25..95%)
    suspend fun compressPhoto(
        context: Context,
        photo: PhotoItem,
        qualityPercent: Int,
    ): PhotoCompressResult = withContext(Dispatchers.IO) {
        val bmp = decodePhotoBitmap(context, photo, maxDim = 1920)
            ?: return@withContext PhotoCompressResult(false, error = "Could not decode image")
        try {
            val outDir = File(context.cacheDir, "prism_compressed").apply { mkdirs() }
            val outFile = File(outDir, "prism_cmp_${System.currentTimeMillis() % 100000}.jpg")
            FileOutputStream(outFile).use { fos ->
                bmp.compress(Bitmap.CompressFormat.JPEG, qualityPercent.coerceIn(20, 95), fos)
            }
            bmp.recycle()
            val original = photo.sizeBytes.coerceAtLeast(outFile.length())
            PhotoCompressResult(
                success = true,
                originalBytes = original,
                compressedBytes = outFile.length(),
                savedFile = outFile,
            )
        } catch (e: Exception) {
            bmp.recycle()
            PhotoCompressResult(false, error = e.message)
        }
    }

    // 7. Set Photo as Wallpaper (zero invasive permissions via system wallpaper chooser or WallpaperManager)
    suspend fun setPhotoAsWallpaper(context: Context, photo: PhotoItem): Boolean = withContext(Dispatchers.IO) {
        val bmp = decodePhotoBitmap(context, photo, maxDim = 2048) ?: return@withContext false
        try {
            val tempDir = File(context.cacheDir, "prism_wallpaper").apply { mkdirs() }
            val tempFile = File(tempDir, "wallpaper_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { bmp.compress(Bitmap.CompressFormat.JPEG, 92, it) }
            bmp.recycle()

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
            val wm = WallpaperManager.getInstance(context)
            val intent = runCatching { wm.getCropAndSetWallpaperIntent(uri) }.getOrNull()
                ?: Intent(Intent.ACTION_ATTACH_DATA).apply {
                    setDataAndType(uri, "image/jpeg")
                    putExtra("mimeType", "image/jpeg")
                }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            runCatching { bmp.recycle() }
            false
        }
    }

    // 8. Fast Video Frame Extraction for Seekbar Scrubbing Preview
    suspend fun extractVideoFrameAt(
        context: Context,
        uriString: String,
        positionMs: Long,
    ): Bitmap? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            val uri = Uri.parse(uriString)
            retriever.setDataSource(context, uri)
            val raw = retriever.getFrameAtTime(
                (positionMs.coerceAtLeast(0L)) * 1000L,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            ) ?: return@withContext null
            val targetW = 240
            val targetH = ((raw.height.toFloat() / raw.width.coerceAtLeast(1)) * targetW).roundToInt().coerceIn(80, 180)
            val scaled = Bitmap.createScaledBitmap(raw, targetW, targetH, true)
            if (scaled !== raw) raw.recycle()
            scaled
        } catch (_: Exception) {
            null
        } finally {
            runCatching { retriever.release() }
        }
    }

    fun decodePhotoBitmap(context: Context, photo: PhotoItem, maxDim: Int = 1920): Bitmap? {
        return runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            if (photo.resId != null) {
                BitmapFactory.decodeResource(context.resources, photo.resId, bounds)
            } else {
                context.contentResolver.openInputStream(Uri.parse(photo.uriString))?.use {
                    BitmapFactory.decodeStream(it, null, bounds)
                }
            }
            var sample = 1
            while (bounds.outWidth / sample > maxDim || bounds.outHeight / sample > maxDim) {
                sample *= 2
            }
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            if (photo.resId != null) {
                BitmapFactory.decodeResource(context.resources, photo.resId, opts)
            } else {
                context.contentResolver.openInputStream(Uri.parse(photo.uriString))?.use {
                    BitmapFactory.decodeStream(it, null, opts)
                }
            }
        }.getOrNull()
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, titlePrefix: String): Boolean {
        return runCatching {
            val cleanName = titlePrefix.replace(Regex("[^a-zA-Z0-9_-]"), "_").take(36).ifBlank { "Prism_Photo" }
            val filename = "${cleanName}_${System.currentTimeMillis() % 10000}.jpg"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Prism Studio")
                }
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return false
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
                } ?: return false
                true
            } else {
                val outDir = File(context.cacheDir, "prism_edits").apply { mkdirs() }
                val file = File(outDir, filename)
                FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 92, it) }
                true
            }
        }.getOrDefault(false)
    }

    // 9. Authentic Morning & Evening Adhkar Catalog (Verified Classical Supplications)
    val adhkarCatalog: List<AdhkarEntry> = listOf(
        AdhkarEntry(
            id = "m_kursi",
            isMorning = true,
            titleAr = "آية الكرسي",
            titleEn = "Ayat Al-Kursi",
            arabicText = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ...",
            translationEn = "Allah — there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep...",
            repeatTarget = 1,
        ),
        AdhkarEntry(
            id = "m_muawwidhat",
            isMorning = true,
            titleAr = "الإخلاص والفلق والناس",
            titleEn = "Surah Al-Ikhlas, Al-Falaq & An-Nas",
            arabicText = "قُلْ هُوَ اللَّهُ أَحَدٌ... قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ... قُلْ أَعُوذُ بِرَبِّ النَّاسِ...",
            translationEn = "Recite Al-Ikhlas, Al-Falaq, and An-Nas three times each morning for complete protection.",
            repeatTarget = 3,
        ),
        AdhkarEntry(
            id = "m_asbahna",
            isMorning = true,
            titleAr = "أصبحنا وأصبح الملك لله",
            titleEn = "Morning Praise & Gratitude",
            arabicText = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
            translationEn = "We have reached the morning and at this very time all sovereignty belongs to Allah, and all praise is for Allah.",
            repeatTarget = 1,
        ),
        AdhkarEntry(
            id = "m_sayyid",
            isMorning = true,
            titleAr = "سيد الاستغفار",
            titleEn = "Master Supplication of Forgiveness",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ.",
            translationEn = "O Allah, You are my Lord, none has the right to be worshipped except You. You created me and I am Your servant... forgive me, for none forgives sins except You.",
            repeatTarget = 1,
        ),
        AdhkarEntry(
            id = "m_bismillah",
            isMorning = true,
            titleAr = "بسم الله الذي لا يضر مع اسمه شيء",
            titleEn = "Protection by Allah's Name",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ.",
            translationEn = "In the Name of Allah, with Whose Name nothing on earth or in the heavens can cause harm, and He is the All-Hearing, All-Knowing.",
            repeatTarget = 3,
        ),
        AdhkarEntry(
            id = "m_salawat",
            isMorning = true,
            titleAr = "الصلاة على النبي ﷺ",
            titleEn = "Salawat upon the Prophet ﷺ",
            arabicText = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ.",
            translationEn = "O Allah, send peace, blessings, and grace upon our Prophet Muhammad ﷺ.",
            repeatTarget = 10,
        ),
        AdhkarEntry(
            id = "e_amsayna",
            isMorning = false,
            titleAr = "أمسينا وأمسى الملك لله",
            titleEn = "Evening Praise & Gratitude",
            arabicText = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ.",
            translationEn = "We have reached the evening and at this very time all sovereignty belongs to Allah, and all praise is for Allah.",
            repeatTarget = 1,
        ),
        AdhkarEntry(
            id = "e_kalimat",
            isMorning = false,
            titleAr = "أعوذ بكلمات الله التامات",
            titleEn = "Evening Refuge in Allah's Words",
            arabicText = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ.",
            translationEn = "I seek refuge in the Perfect Words of Allah from the evil of what He has created.",
            repeatTarget = 3,
        ),
        AdhkarEntry(
            id = "e_raditu",
            isMorning = false,
            titleAr = "رضيت بالله رباً",
            titleEn = "Contentment in Faith",
            arabicText = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ ﷺ نَبِيًّا.",
            translationEn = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad ﷺ as my Prophet.",
            repeatTarget = 3,
        ),
        AdhkarEntry(
            id = "e_subhan",
            isMorning = false,
            titleAr = "سبحان الله وبحمده",
            titleEn = "Evening Glorification",
            arabicText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ.",
            translationEn = "Glory be to Allah and praise Him; Glory be to Allah the Supreme.",
            repeatTarget = 33,
        ),
    )
}
