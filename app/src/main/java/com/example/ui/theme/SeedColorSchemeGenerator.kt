package com.example.ui.theme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

/**
 * Generates a full Material You tonal ColorScheme (Light & Dark) from ANY user-chosen seed color
 * or extracted photo dominant color using balanced HSL tonal shifts.
 */
object SeedColorSchemeGenerator {

    fun generateColorScheme(seedArgb: Int, darkTheme: Boolean): ColorScheme {
        val hsl = FloatArray(3)
        android.graphics.Color.colorToHSV(seedArgb, hsl)
        val hue = hsl[0]
        val baseSat = hsl[1].coerceIn(0.32f, 0.88f)
        val tertiaryHue = (hue + 52f) % 360f
        val secondarySat = (baseSat * 0.42f).coerceIn(0.14f, 0.38f)

        return if (darkTheme) {
            darkColorScheme(
                primary = hslColor(hue, (baseSat * 0.82f).coerceIn(0.38f, 0.85f), 0.78f),
                onPrimary = hslColor(hue, (baseSat * 0.90f).coerceIn(0.40f, 0.90f), 0.16f),
                primaryContainer = hslColor(hue, (baseSat * 0.72f).coerceIn(0.30f, 0.72f), 0.28f),
                onPrimaryContainer = hslColor(hue, (baseSat * 0.85f).coerceIn(0.35f, 0.85f), 0.91f),
                secondary = hslColor(hue, secondarySat, 0.76f),
                onSecondary = hslColor(hue, secondarySat, 0.18f),
                secondaryContainer = hslColor(hue, secondarySat, 0.28f),
                onSecondaryContainer = hslColor(hue, secondarySat, 0.90f),
                tertiary = hslColor(tertiaryHue, (baseSat * 0.70f).coerceIn(0.32f, 0.75f), 0.78f),
                onTertiary = hslColor(tertiaryHue, (baseSat * 0.75f).coerceIn(0.35f, 0.80f), 0.18f),
                tertiaryContainer = hslColor(tertiaryHue, (baseSat * 0.60f).coerceIn(0.28f, 0.65f), 0.29f),
                onTertiaryContainer = hslColor(tertiaryHue, (baseSat * 0.75f).coerceIn(0.35f, 0.80f), 0.91f),
                background = hslColor(hue, 0.14f, 0.07f),
                onBackground = hslColor(hue, 0.12f, 0.91f),
                surface = hslColor(hue, 0.14f, 0.08f),
                onSurface = hslColor(hue, 0.12f, 0.91f),
                surfaceVariant = hslColor(hue, 0.14f, 0.28f),
                onSurfaceVariant = hslColor(hue, 0.12f, 0.78f),
                surfaceContainerLowest = hslColor(hue, 0.16f, 0.05f),
                surfaceContainerLow = hslColor(hue, 0.14f, 0.11f),
                surfaceContainer = hslColor(hue, 0.15f, 0.14f),
                surfaceContainerHigh = hslColor(hue, 0.16f, 0.18f),
                surfaceContainerHighest = hslColor(hue, 0.16f, 0.23f),
                outline = hslColor(hue, 0.10f, 0.56f),
                outlineVariant = hslColor(hue, 0.14f, 0.28f),
            )
        } else {
            lightColorScheme(
                primary = hslColor(hue, (baseSat * 0.88f).coerceIn(0.40f, 0.88f), 0.36f),
                onPrimary = Color.White,
                primaryContainer = hslColor(hue, (baseSat * 0.82f).coerceIn(0.35f, 0.85f), 0.89f),
                onPrimaryContainer = hslColor(hue, (baseSat * 0.90f).coerceIn(0.40f, 0.90f), 0.12f),
                secondary = hslColor(hue, secondarySat, 0.38f),
                onSecondary = Color.White,
                secondaryContainer = hslColor(hue, secondarySat, 0.89f),
                onSecondaryContainer = hslColor(hue, secondarySat, 0.14f),
                tertiary = hslColor(tertiaryHue, (baseSat * 0.70f).coerceIn(0.32f, 0.75f), 0.38f),
                onTertiary = Color.White,
                tertiaryContainer = hslColor(tertiaryHue, (baseSat * 0.75f).coerceIn(0.35f, 0.80f), 0.89f),
                onTertiaryContainer = hslColor(tertiaryHue, (baseSat * 0.80f).coerceIn(0.35f, 0.85f), 0.14f),
                background = hslColor(hue, 0.20f, 0.98f),
                onBackground = hslColor(hue, 0.14f, 0.11f),
                surface = hslColor(hue, 0.20f, 0.98f),
                onSurface = hslColor(hue, 0.14f, 0.11f),
                surfaceVariant = hslColor(hue, 0.18f, 0.89f),
                onSurfaceVariant = hslColor(hue, 0.14f, 0.30f),
                surfaceContainerLowest = Color.White,
                surfaceContainerLow = hslColor(hue, 0.20f, 0.95f),
                surfaceContainer = hslColor(hue, 0.20f, 0.92f),
                surfaceContainerHigh = hslColor(hue, 0.20f, 0.90f),
                surfaceContainerHighest = hslColor(hue, 0.20f, 0.87f),
                outline = hslColor(hue, 0.12f, 0.46f),
                outlineVariant = hslColor(hue, 0.14f, 0.77f),
            )
        }
    }

    /**
     * Extracts the richest vibrant seed color from any photo URI chosen by the user.
     */
    suspend fun extractSeedFromPhotoUri(context: Context, uri: Uri): Int? = withContext(Dispatchers.IO) {
        val bitmap: Bitmap = runCatching {
            val opts = BitmapFactory.Options().apply { inSampleSize = 8 }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opts)
            }
        }.getOrNull() ?: return@withContext null

        val result = extractSeedFromBitmap(bitmap)
        runCatching { bitmap.recycle() }
        result
    }

    fun extractSeedFromBitmap(bitmap: Bitmap): Int {
        val w = bitmap.width.coerceAtLeast(1)
        val h = bitmap.height.coerceAtLeast(1)
        val stepX = (w / 16).coerceAtLeast(1)
        val stepY = (h / 16).coerceAtLeast(1)
        val hsv = FloatArray(3)
        var bestScore = -1f
        var bestColor = Color(0xFF6750A4).toArgb()

        var y = 0
        while (y < h) {
            var x = 0
            while (x < w) {
                val pixel = bitmap.getPixel(x, y)
                android.graphics.Color.colorToHSV(pixel, hsv)
                val sat = hsv[1]
                val value = hsv[2]
                // Prefer vivid, mid-luminance tones ideal for Material You Monet seeds
                val score = sat * 1.6f + (1f - abs(value - 0.58f))
                if (value in 0.18f..0.92f && score > bestScore) {
                    bestScore = score
                    bestColor = pixel
                }
                x += stepX
            }
            y += stepY
        }
        return bestColor
    }

    private fun hslColor(hueDeg: Float, saturation: Float, lightness: Float): Color {
        val h = ((hueDeg % 360f) + 360f) % 360f
        val s = saturation.coerceIn(0f, 1f)
        val l = lightness.coerceIn(0f, 1f)
        val c = (1f - abs(2f * l - 1f)) * s
        val x = c * (1f - abs(((h / 60f) % 2f) - 1f))
        val m = l - c / 2f
        val (r1, g1, b1) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        return Color(
            red = (r1 + m).coerceIn(0f, 1f),
            green = (g1 + m).coerceIn(0f, 1f),
            blue = (b1 + m).coerceIn(0f, 1f),
            alpha = 1f
        )
    }
}
