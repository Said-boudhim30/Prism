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
 * 4-Leaf Clover, Editorial Arch) with pre-allocated cached singletons
 * so Compose never re-allocates or re-tessellates paths during recomposition.
 */
object PrismExpressiveShapes {

    private val cachedScalloped12: Shape = buildScallopedCookie(lobes = 12, depth = 0.064f)
    private val cachedScalloped10: Shape = buildScallopedCookie(lobes = 10, depth = 0.055f)
    private val cachedSoftSquircle: Shape = buildSoftSquircle(exponent = 3.2f)
    private val cachedFourLeafClover: Shape = buildFourLeafClover(depth = 0.105f)
    private val cachedExpressiveArch: Shape = RoundedCornerShape(
        topStartPercent = 50,
        topEndPercent = 50,
        bottomStartPercent = 18,
        bottomEndPercent = 18,
    )

    private fun buildScallopedCookie(lobes: Int, depth: Float): Shape = GenericShape { size, _ ->
        val cx = size.width / 2f
        val cy = size.height / 2f
        val baseR = min(cx, cy) * (1f - depth)
        val amp = min(cx, cy) * depth
        val steps = 96
        for (i in 0..steps) {
            val theta = (i.toFloat() / steps) * (2.0 * PI)
            val r = baseR + amp * cos(lobes * theta).toFloat()
            val x = cx + r * cos(theta).toFloat()
            val y = cy + r * sin(theta).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    private fun buildSoftSquircle(exponent: Float): Shape = GenericShape { size, _ ->
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rx = size.width / 2f
        val ry = size.height / 2f
        val p = 2f / exponent
        val steps = 84
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

    private fun buildFourLeafClover(depth: Float): Shape = GenericShape { size, _ ->
        val cx = size.width / 2f
        val cy = size.height / 2f
        val baseR = min(cx, cy) * (1f - depth)
        val amp = min(cx, cy) * depth
        val steps = 96
        for (i in 0..steps) {
            val theta = (i.toFloat() / steps) * (2.0 * PI)
            val r = baseR + amp * cos(4.0 * (theta - PI / 4.0)).toFloat()
            val x = cx + r * cos(theta).toFloat()
            val y = cy + r * sin(theta).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    /**
     * Iconic Material 3 Expressive Scalloped Cookie / Flower Seal with smooth sinusoidal lobes.
     */
    fun scallopedCookie(lobes: Int = 12, depth: Float = 0.068f): Shape = when {
        lobes == 12 && abs(depth - 0.064f) < 0.01f -> cachedScalloped12
        lobes == 10 && abs(depth - 0.055f) < 0.01f -> cachedScalloped10
        else -> buildScallopedCookie(lobes, depth)
    }

    /**
     * Continuous-curvature Superellipse (Squircle) with soft expressive corners.
     */
    fun softSquircle(exponent: Float = 3.4f): Shape =
        if (abs(exponent - 3.2f) < 0.3f) cachedSoftSquircle else buildSoftSquircle(exponent)

    /**
     * Material 3 Expressive 4-Leaf Cushion / Clover silhouette.
     */
    fun fourLeafClover(depth: Float = 0.115f): Shape =
        if (abs(depth - 0.105f) < 0.02f) cachedFourLeafClover else buildFourLeafClover(depth)

    /**
     * Architectural / Editorial Arch Window shape.
     */
    fun expressiveArch(): Shape = cachedExpressiveArch

    /**
     * True for non-convex/generic paths where Android RenderNode elevation shadows
     * can trigger expensive Skia tessellation stalls; callers use radial glow instead.
     */
    fun usesGenericOutline(style: PlayerArtworkStyle): Boolean = when (style) {
        PlayerArtworkStyle.SCALLOPED_COOKIE,
        PlayerArtworkStyle.SQUIRCLE_EXPRESSIVE,
        PlayerArtworkStyle.CLOVER_FOUR,
        -> true
        else -> false
    }

    /**
     * Resolves the compose [Shape] for any [PlayerArtworkStyle].
     */
    fun artworkShapeFor(style: PlayerArtworkStyle, roundness: Float = 1.0f): Shape = when (style) {
        PlayerArtworkStyle.SCALLOPED_COOKIE -> cachedScalloped12
        PlayerArtworkStyle.SQUIRCLE_EXPRESSIVE -> cachedSoftSquircle
        PlayerArtworkStyle.VINYL_DISC -> CircleShape
        PlayerArtworkStyle.SOFT_PILL_ARCH -> cachedExpressiveArch
        PlayerArtworkStyle.CLOVER_FOUR -> cachedFourLeafClover
        PlayerArtworkStyle.CIRCLE_GLOW -> CircleShape
        PlayerArtworkStyle.ROUNDED_CARD -> RoundedCornerShape(scaledCornerDp(32.dp, roundness))
    }
}
