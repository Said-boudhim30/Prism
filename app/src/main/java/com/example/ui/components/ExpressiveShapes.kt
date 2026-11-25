package com.example.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.model.PlayerArtworkStyle
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin

/**
 * Material 3 Expressive organic shapes (Scalloped Cookie, Soft Squircle,
 * 4-Leaf Clover, Editorial Arch, Starburst Seal) for Prism's Full-Screen Player,
 * Lock-Screen Card, and Live Settings Previews.
 */
object PrismExpressiveShapes {

    /**
     * Iconic Material 3 Expressive Scalloped Cookie / Flower Seal with smooth sinusoidal lobes.
     */
    fun scallopedCookie(lobes: Int = 12, depth: Float = 0.068f): Shape = GenericShape { size, _ ->
        val cx = size.width / 2f
        val cy = size.height / 2f
        val baseR = min(cx, cy) * (1f - depth)
        val amp = min(cx, cy) * depth
        val steps = 180
        for (i in 0..steps) {
            val theta = (i.toFloat() / steps) * (2.0 * PI)
            val r = baseR + amp * cos(lobes * theta).toFloat()
            val x = cx + r * cos(theta).toFloat()
            val y = cy + r * sin(theta).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /**
     * Continuous-curvature Superellipse (Squircle) with soft expressive corners.
     */
    fun softSquircle(exponent: Float = 3.4f): Shape = GenericShape { size, _ ->
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rx = size.width / 2f
        val ry = size.height / 2f
        val p = 2f / exponent
        val steps = 144
        for (i in 0..steps) {
            val theta = (i.toFloat() / steps) * (2.0 * PI)
            val ct = cos(theta).toFloat()
            val st = sin(theta).toFloat()
            val x = cx + rx * sign(ct) * abs(ct).pow(p)
            val y = cy + ry * sign(st) * abs(st).pow(p)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /**
     * Material 3 Expressive 4-Leaf Cushion / Clover silhouette.
     */
    fun fourLeafClover(depth: Float = 0.115f): Shape = GenericShape { size, _ ->
        val cx = size.width / 2f
        val cy = size.height / 2f
        val baseR = min(cx, cy) * (1f - depth)
        val amp = min(cx, cy) * depth
        val steps = 160
        for (i in 0..steps) {
            val theta = (i.toFloat() / steps) * (2.0 * PI)
            // Offset by PI/4 so the 4 soft lobes sit diagonally like a plush cushion
            val r = baseR + amp * cos(4.0 * (theta - PI / 4.0)).toFloat()
            val x = cx + r * cos(theta).toFloat()
            val y = cy + r * sin(theta).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /**
     * Architectural / Editorial Arch Window shape (full half-circle top + expressive softly rounded base).
     */
    fun expressiveArch(): Shape = RoundedCornerShape(
        topStartPercent = 50,
        topEndPercent = 50,
        bottomStartPercent = 18,
        bottomEndPercent = 18,
    )

    /**
     * Resolves the compose [Shape] for any [PlayerArtworkStyle].
     */
    fun artworkShapeFor(style: PlayerArtworkStyle, roundness: Float = 1.0f): Shape = when (style) {
        PlayerArtworkStyle.SCALLOPED_COOKIE -> scallopedCookie(lobes = 12, depth = 0.064f)
        PlayerArtworkStyle.SQUIRCLE_EXPRESSIVE -> softSquircle(exponent = 3.2f)
        PlayerArtworkStyle.VINYL_DISC -> CircleShape
        PlayerArtworkStyle.SOFT_PILL_ARCH -> expressiveArch()
        PlayerArtworkStyle.CLOVER_FOUR -> fourLeafClover(depth = 0.105f)
        PlayerArtworkStyle.CIRCLE_GLOW -> CircleShape
        PlayerArtworkStyle.ROUNDED_CARD -> RoundedCornerShape(scaledCornerDp(32.dp, roundness))
    }
}
