package com.example.player

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Parsed subtitle cue for external SRT / VTT subtitle rendering with real-time sync offset.
 */
data class SubtitleCue(
    val startMs: Long,
    val endMs: Long,
    val text: String,
)

/**
 * Represents a selectable embedded audio track inside a video container (MKV / MP4 / TS).
 */
data class VideoAudioTrackOption(
    val groupIndex: Int,
    val trackIndex: Int,
    val label: String,
    val isSelected: Boolean,
)

object SubtitleParser {
    private val TIME_LINE_REGEX = Regex(
        """(?:(\d{1,2}):)?(\d{1,2}):(\d{2})[,.](\d{1,3})\s*-->\s*(?:(\d{1,2}):)?(\d{1,2}):(\d{2})[,.](\d{1,3})"""
    )
    private val HTML_TAG_REGEX = Regex("<[^>]+>")

    /**
     * Parses both SubRip (.srt) and WebVTT (.vtt) subtitle text into sorted [SubtitleCue] items.
     */
    fun parse(rawText: String): List<SubtitleCue> {
        if (rawText.isBlank()) return emptyList()
        val normalized = rawText
            .removePrefix("\uFEFF")
            .replace("\r\n", "\n")
            .replace("\r", "\n")

        val cues = mutableListOf<SubtitleCue>()
        val blocks = normalized.split(Regex("\n\\s*\n"))
        for (block in blocks) {
            val lines = block.lines().map { it.trim() }.filter { it.isNotEmpty() }
            if (lines.isEmpty()) continue
            if (lines.first().startsWith("WEBVTT", ignoreCase = true) ||
                lines.first().startsWith("NOTE", ignoreCase = true)
            ) continue

            val timeIdx = lines.indexOfFirst { TIME_LINE_REGEX.containsMatchIn(it) }
            if (timeIdx < 0) continue

            val match = TIME_LINE_REGEX.find(lines[timeIdx]) ?: continue
            val startMs = groupsToMs(
                match.groupValues[1],
                match.groupValues[2],
                match.groupValues[3],
                match.groupValues[4]
            )
            val endMs = groupsToMs(
                match.groupValues[5],
                match.groupValues[6],
                match.groupValues[7],
                match.groupValues[8]
            )
            val captionLines = lines.drop(timeIdx + 1)
                .map { it.replace(HTML_TAG_REGEX, "").trim() }
                .filter { it.isNotEmpty() }
            if (captionLines.isNotEmpty() && endMs > startMs) {
                cues.add(SubtitleCue(startMs, endMs, captionLines.joinToString("\n")))
            }
        }
        return cues.sortedBy { it.startMs }
    }

    /**
     * Finds the active subtitle text at [positionMs] adjusted by [syncOffsetMs].
     * Positive syncOffsetMs delays subtitles; negative syncOffsetMs shows them earlier.
     */
    fun activeCaptionAt(cues: List<SubtitleCue>, positionMs: Long, syncOffsetMs: Long = 0L): String? {
        if (cues.isEmpty()) return null
        val adjustedPos = positionMs - syncOffsetMs
        // Binary search for fast O(log n) lookup at 60fps
        var low = 0
        var high = cues.lastIndex
        var candidate = -1
        while (low <= high) {
            val mid = (low + high) ushr 1
            if (cues[mid].startMs <= adjustedPos) {
                candidate = mid
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        if (candidate < 0) return null
        val cue = cues[candidate]
        return if (adjustedPos in cue.startMs..cue.endMs) cue.text else null
    }

    private fun groupsToMs(hStr: String, mStr: String, sStr: String, msStr: String): Long {
        val h = hStr.toLongOrNull() ?: 0L
        val m = mStr.toLongOrNull() ?: 0L
        val s = sStr.toLongOrNull() ?: 0L
        val ms = msStr.padEnd(3, '0').take(3).toLongOrNull() ?: 0L
        return ((h * 3600L) + (m * 60L) + s) * 1000L + ms
    }
}

object VideoStudioUtils {

    /**
     * Extracts all selectable embedded audio tracks from Media3 [Tracks].
     */
    @OptIn(UnstableApi::class)
    fun extractAudioTracks(tracks: Tracks): List<VideoAudioTrackOption> {
        val list = mutableListOf<VideoAudioTrackOption>()
        var trackCounter = 1
        tracks.groups.forEachIndexed { groupIndex, group ->
            if (group.type == C.TRACK_TYPE_AUDIO) {
                for (i in 0 until group.length) {
                    val format = group.getTrackFormat(i)
                    val lang = format.language?.uppercase()?.takeIf { it != "UND" }
                    val channels = when (format.channelCount) {
                        1 -> "Mono"
                        2 -> "Stereo"
                        6 -> "5.1"
                        8 -> "7.1"
                        else -> if (format.channelCount > 0) "${format.channelCount}ch" else ""
                    }
                    val labelPart = listOfNotNull(
                        format.label?.takeIf { it.isNotBlank() },
                        lang,
                        channels.takeIf { it.isNotBlank() }
                    ).distinct().joinToString(" • ")
                    val finalLabel = if (labelPart.isNotBlank()) {
                        "#$trackCounter • $labelPart"
                    } else {
                        "Track #$trackCounter"
                    }
                    list.add(
                        VideoAudioTrackOption(
                            groupIndex = groupIndex,
                            trackIndex = i,
                            label = finalLabel,
                            isSelected = group.isTrackSelected(i)
                        )
                    )
                    trackCounter++
                }
            }
        }
        return list
    }

    /**
     * Switches ExoPlayer's active embedded audio track cleanly without restarting playback.
     */
    @OptIn(UnstableApi::class)
    fun selectAudioTrack(exoPlayer: ExoPlayer, option: VideoAudioTrackOption) {
        val groups = exoPlayer.currentTracks.groups
        if (option.groupIndex !in groups.indices) return
        val group = groups[option.groupIndex]
        val override = TrackSelectionOverride(group.mediaTrackGroup, listOf(option.trackIndex))
        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
            .buildUpon()
            .clearOverridesOfType(C.TRACK_TYPE_AUDIO)
            .addOverride(override)
            .setTrackTypeDisabled(C.TRACK_TYPE_AUDIO, false)
            .build()
    }

    /**
     * Captures a high-resolution video frame at [positionMs] and saves it into Pictures/Prism
     * via MediaStore (zero broad storage permissions required on Android 10+).
     */
    suspend fun captureFrameSnapshot(
        context: Context,
        uriString: String,
        positionMs: Long,
        videoTitle: String,
    ): Boolean = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        val bitmap: Bitmap = try {
            val uri = Uri.parse(uriString)
            if (uri.scheme == "http" || uri.scheme == "https") {
                retriever.setDataSource(uriString, HashMap())
            } else {
                retriever.setDataSource(context, uri)
            }
            retriever.getFrameAtTime(
                positionMs.coerceAtLeast(0L) * 1000L,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
            ) ?: retriever.frameAtTime ?: return@withContext false
        } catch (_: Throwable) {
            return@withContext false
        } finally {
            runCatching { retriever.release() }
        }

        val safeName = videoTitle
            .replace(Regex("[^a-zA-Z0-9\\u0600-\\u06FF_-]"), "_")
            .take(36)
            .ifBlank { "Prism_Frame" }
        val fileName = "${safeName}_${System.currentTimeMillis()}.png"

        return@withContext saveBitmapToPictures(context, bitmap, fileName)
    }

    fun saveBitmapToPictures(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        return runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Prism")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val destUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: return false
                resolver.openOutputStream(destUri)?.use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                } ?: return false
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(destUri, values, null, null)
                true
            } else {
                val dir = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir,
                    "Prism"
                ).apply { mkdirs() }
                val dest = File(dir, fileName)
                FileOutputStream(dest).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                true
            }
        }.getOrDefault(false)
    }
}
