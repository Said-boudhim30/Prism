package com.example.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.model.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

/**
 * Extracted Material You-compatible dynamic colors from the currently playing song's cover art.
 */
data class AlbumArtPalette(
    val dominant: Color,
    val vibrant: Color,
    val deepTint: Color,
)

/**
 * Ultra-fast, zero-dependency album artwork color quantizer with in-memory LRU-like cache.
 * Computes vibrant & dominant tonal seeds asynchronously on Dispatchers.Default so every track
 * dynamically tints the Full Player and Mini Player smoothly without frame drops.
 */
object AlbumArtPaletteExtractor {
    private val cache = ConcurrentHashMap<String, AlbumArtPalette>()

    suspend fun extractForTrack(context: Context, track: AudioTrack): AlbumArtPalette? {
        val key = "${track.id}:${track.artworkResId ?: 0}:${track.uriString}"
        cache[key]?.let { return it }

        return withContext(Dispatchers.Default) {
            val bitmap = decodeSmallSampleBitmap(context, track) ?: return@withContext null
            val palette = extractFromBitmap(bitmap)
            if (cache.size > 64) cache.clear()
            cache[key] = palette
            palette
        }
    }

    fun extractFromBitmap(bitmap: Bitmap): AlbumArtPalette {
        val scaled = if (bitmap.width > 40 || bitmap.height > 40) {
            Bitmap.createScaledBitmap(bitmap, 36, 36, true)
        } else {
            bitmap
        }

        val width = scaled.width
        val height = scaled.height
        val pixels = IntArray(width * height)
        scaled.getPixels(pixels, 0, width, 0, 0, width, height)

        // Quantize RGB into 16x16x16 buckets and score by population + saturation
        val bucketCounts = HashMap<Int, Int>()
        val bucketSumR = HashMap<Int, Long>()
        val bucketSumG = HashMap<Int, Long>()
        val bucketSumB = HashMap<Int, Long>()
        val hsv = FloatArray(3)

        var bestVibrantScore = -1f
        var bestVibrantColor = 0xFF6750A4.toInt()
        var bestDominantCount = -1
        var bestDominantKey = -1

        for (pixel in pixels) {
            val alpha = (pixel ushr 24) and 0xFF
            if (alpha < 180) continue
            val r = (pixel ushr 16) and 0xFF
            val g = (pixel ushr 8) and 0xFF
            val b = pixel and 0xFF

            val key = ((r shr 4) shl 8) or ((g shr 4) shl 4) or (b shr 4)
            val newCount = (bucketCounts[key] ?: 0) + 1
            bucketCounts[key] = newCount
            bucketSumR[key] = (bucketSumR[key] ?: 0L) + r
            bucketSumG[key] = (bucketSumG[key] ?: 0L) + g
            bucketSumB[key] = (bucketSumB[key] ?: 0L) + b

            if (newCount > bestDominantCount) {
                bestDominantCount = newCount
                bestDominantKey = key
            }

            android.graphics.Color.RGBToHSV(r, g, b, hsv)
            val sat = hsv[1]
            val value = hsv[2]
            if (sat >= 0.28f && value in 0.22f..0.95f) {
                val score = sat * 1.4f + (1f - kotlin.math.abs(value - 0.62f))
                if (score > bestVibrantScore) {
                    bestVibrantScore = score
                    bestVibrantColor = pixel
                }
            }
        }

        val dominantColorInt = if (bestDominantKey >= 0 && bestDominantCount > 0) {
            val avgR = ((bucketSumR[bestDominantKey] ?: 0L) / bestDominantCount).toInt().coerceIn(0, 255)
            val avgG = ((bucketSumG[bestDominantKey] ?: 0L) / bestDominantCount).toInt().coerceIn(0, 255)
            val avgB = ((bucketSumB[bestDominantKey] ?: 0L) / bestDominantCount).toInt().coerceIn(0, 255)
            android.graphics.Color.rgb(avgR, avgG, avgB)
        } else {
            bestVibrantColor
        }

        val dominant = Color(dominantColorInt)
        val vibrant = Color(if (bestVibrantScore > 0f) bestVibrantColor else dominantColorInt)

        // Create a rich Material You deep tonal tint for legible surfaces
        android.graphics.Color.colorToHSV(vibrant.toArgb(), hsv)
        hsv[1] = min(hsv[1] * 0.78f, 0.72f)
        hsv[2] = max(min(hsv[2] * 0.45f, 0.42f), 0.16f)
        val deepTint = Color(android.graphics.Color.HSVToColor(hsv))

        if (scaled !== bitmap) {
            runCatching { scaled.recycle() }
        }
        return AlbumArtPalette(dominant = dominant, vibrant = vibrant, deepTint = deepTint)
    }

    private fun decodeSmallSampleBitmap(context: Context, track: AudioTrack): Bitmap? {
        return runCatching {
            val opts = BitmapFactory.Options().apply { inSampleSize = 4 }
            when {
                track.artworkResId != null -> BitmapFactory.decodeResource(context.resources, track.artworkResId, opts)
                track.uriString.isNotBlank() && !track.uriString.startsWith("http") -> {
                    val retriever = MediaMetadataRetriever()
                    try {
                        retriever.setDataSource(context, Uri.parse(track.uriString))
                        val bytes = retriever.embeddedPicture ?: return null
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
                    } finally {
                        runCatching { retriever.release() }
                    }
                }
                else -> null
            }
        }.getOrNull()
    }
}
