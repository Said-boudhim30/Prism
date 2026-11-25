package com.example.ui.components

import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localization.AppStrings
import com.example.model.LiquidGlassStyle
import com.example.model.LrcLine
import com.example.model.SmartMusicMix
import com.example.model.SurfaceDepthStyle
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Global Material You Liquid Glass configuration propagated through PrismTheme.
 * Grounded strictly in Google's Material 3 Expressive tonal color roles
 * (primaryContainer / secondaryContainer / tertiaryContainer / surfaceContainerHigh)
 * rather than generic monochrome frosted overlays.
 */
@Immutable
data class LiquidGlassConfig(
    val style: LiquidGlassStyle = LiquidGlassStyle.MATERIAL_LIQUID,
    val intensity: Float = 0.72f,
    val shapeRoundness: Float = 1.0f,
    val edgeGlowEnabled: Boolean = true,
    val depthStyle: SurfaceDepthStyle = SurfaceDepthStyle.EXPRESSIVE,
)

val LocalLiquidGlassConfig = staticCompositionLocalOf { LiquidGlassConfig() }

fun scaledCornerDp(baseDp: Dp, roundness: Float): Dp {
    return (baseDp.value * roundness.coerceIn(0.6f, 1.4f)).dp
}

/**
 * Authentic Material You Expressive Liquid Glass container.
 * Uses tonal surfaceContainerHigh blended with Material You primary/tertiary container washes
 * plus a chromatic specular refractive rim so it looks unmistakably Material You.
 */
@Composable
fun MaterialLiquidGlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    shadowElevation: Dp = 10.dp,
    tonalBoost: Float = 1f,
    content: @Composable BoxScope.() -> Unit,
) {
    val cfg = LocalLiquidGlassConfig.current
    val intensity = cfg.intensity.coerceIn(0f, 1f)
    val cs = MaterialTheme.colorScheme

    val resolvedShadow = when (cfg.depthStyle) {
        SurfaceDepthStyle.FLAT -> 0.dp
        SurfaceDepthStyle.ELEVATED -> (shadowElevation * 0.55f).coerceAtLeast(1.dp)
        SurfaceDepthStyle.EXPRESSIVE -> shadowElevation
    }
    val resolvedTonal = when (cfg.depthStyle) {
        SurfaceDepthStyle.FLAT -> 1.dp
        SurfaceDepthStyle.ELEVATED -> 3.dp
        SurfaceDepthStyle.EXPRESSIVE -> 6.dp
    }

    if (cfg.style == LiquidGlassStyle.SOLID_M3) {
        Surface(
            shape = shape,
            color = cs.surfaceContainerHigh,
            tonalElevation = resolvedTonal,
            shadowElevation = if (cfg.depthStyle == SurfaceDepthStyle.FLAT) 0.dp else (resolvedShadow * 0.6f).coerceAtLeast(1.dp),
            border = BorderStroke(
                1.dp,
                cs.outlineVariant.copy(alpha = if (cfg.depthStyle == SurfaceDepthStyle.FLAT) 0.62f else 0.42f)
            ),
            modifier = modifier
        ) {
            Box(
                modifier = Modifier.clip(shape),
                content = content
            )
        }
        return
    }

    val containerAlpha = when (cfg.style) {
        LiquidGlassStyle.MATERIAL_LIQUID -> (0.97f - intensity * 0.13f).coerceIn(0.84f, 0.98f)
        LiquidGlassStyle.CRYSTAL_TONAL -> (0.93f - intensity * 0.19f).coerceIn(0.74f, 0.95f)
        LiquidGlassStyle.VELVET_FROST -> (0.98f - intensity * 0.08f).coerceIn(0.90f, 0.99f)
        LiquidGlassStyle.SOLID_M3 -> 1.0f
    }

    // Authentic Material You tonal elevation wash — keeps primary/secondary container identity crisp
    val washAlpha = when (cfg.style) {
        LiquidGlassStyle.MATERIAL_LIQUID -> (0.08f + intensity * 0.12f) * tonalBoost
        LiquidGlassStyle.CRYSTAL_TONAL -> (0.12f + intensity * 0.16f) * tonalBoost
        LiquidGlassStyle.VELVET_FROST -> (0.06f + intensity * 0.08f) * tonalBoost
        LiquidGlassStyle.SOLID_M3 -> 0f
    }.coerceIn(0f, 0.28f)

    val rimAlpha = when (cfg.style) {
        LiquidGlassStyle.MATERIAL_LIQUID -> 0.22f + intensity * 0.30f
        LiquidGlassStyle.CRYSTAL_TONAL -> 0.30f + intensity * 0.38f
        LiquidGlassStyle.VELVET_FROST -> 0.16f + intensity * 0.20f
        LiquidGlassStyle.SOLID_M3 -> 0f
    }.coerceIn(0.14f, 0.72f)

    val glassBrush = remember(cfg.style, intensity, cs, washAlpha) {
        Brush.verticalGradient(
            colors = listOf(
                cs.primaryContainer.copy(alpha = washAlpha),
                cs.surfaceContainerHigh.copy(alpha = containerAlpha),
                cs.secondaryContainer.copy(alpha = washAlpha * 0.65f),
            )
        )
    }

    val refractiveBorderBrush = remember(cfg.style, intensity, cs, rimAlpha) {
        Brush.linearGradient(
            colors = listOf(
                cs.primary.copy(alpha = rimAlpha),
                cs.outlineVariant.copy(alpha = rimAlpha * 0.85f),
                cs.tertiary.copy(alpha = rimAlpha * 0.65f),
            )
        )
    }

    val specularTopSheen = remember(cfg.style, intensity) {
        val topAlpha = when (cfg.style) {
            LiquidGlassStyle.CRYSTAL_TONAL -> (0.14f + intensity * 0.14f).coerceAtMost(0.28f)
            LiquidGlassStyle.MATERIAL_LIQUID -> (0.09f + intensity * 0.10f).coerceAtMost(0.20f)
            LiquidGlassStyle.VELVET_FROST -> (0.05f + intensity * 0.06f).coerceAtMost(0.12f)
            LiquidGlassStyle.SOLID_M3 -> 0f
        }
        Brush.verticalGradient(
            0f to Color.White.copy(alpha = topAlpha),
            0.32f to Color.White.copy(alpha = topAlpha * 0.18f),
            1f to Color.Transparent
        )
    }

    Surface(
        shape = shape,
        color = cs.surfaceContainer.copy(alpha = containerAlpha),
        tonalElevation = resolvedTonal,
        shadowElevation = resolvedShadow,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(glassBrush)
                .background(specularTopSheen)
                .border(
                    width = 1.2.dp,
                    brush = refractiveBorderBrush,
                    shape = shape
                ),
            content = content
        )
    }
}

/**
 * Living Material You Ambient Mesh Backdrop grounded in Material 3 Expressive tonal palettes.
 * Keeps subtle dynamic color depth behind crisp Material You surfaces without washing out contrast.
 */
@Composable
fun MaterialLiquidAmbientBackdrop(
    isDarkTheme: Boolean,
    amoledBlack: Boolean = false,
    isPlaying: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val cfg = LocalLiquidGlassConfig.current
    val backdropStyle = com.example.ui.theme.LocalBackdropStyle.current
    val accentStrength = com.example.ui.theme.LocalAccentStrength.current
    val cs = MaterialTheme.colorScheme
    val intensity = cfg.intensity.coerceIn(0f, 1f)

    if (cfg.style == LiquidGlassStyle.SOLID_M3 || backdropStyle == com.example.model.BackdropAnimationStyle.PURE_CLEAN) {
        Box(modifier = modifier.background(cs.background))
        return
    }

    val cycleMs = when (backdropStyle) {
        com.example.model.BackdropAnimationStyle.SUBTLE_MIST -> if (isPlaying) 24000 else 36000
        com.example.model.BackdropAnimationStyle.PRISM_RIBBON -> if (isPlaying) 14000 else 22000
        else -> if (isPlaying) 16000 else 26000
    }

    val infiniteTransition = rememberInfiniteTransition(label = "liquidAmbientMesh")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = cycleMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "liquidPhase"
    )

    val baseStrength = when {
        amoledBlack -> 0.06f
        isDarkTheme -> 0.11f
        else -> 0.09f
    }
    val styleBoost = when (cfg.style) {
        LiquidGlassStyle.MATERIAL_LIQUID -> 1.0f
        LiquidGlassStyle.CRYSTAL_TONAL -> 1.2f
        LiquidGlassStyle.VELVET_FROST -> 0.72f
        LiquidGlassStyle.SOLID_M3 -> 0.0f
    }
    val alphaFactor = (baseStrength * (0.35f + intensity * 0.85f) * styleBoost * accentStrength).coerceIn(0.02f, 0.28f)

    val c1 = cs.primary.copy(alpha = alphaFactor * 1.15f)
    val c2 = cs.tertiary.copy(alpha = alphaFactor * 0.96f)
    val c3 = cs.secondary.copy(alpha = alphaFactor * 0.80f)
    val c4 = cs.primaryContainer.copy(alpha = alphaFactor * 0.85f)
    val moteColorPrimary = cs.primary.copy(alpha = (if (isPlaying) 0.28f else 0.16f) * accentStrength.coerceAtMost(1.25f))
    val moteColorTertiary = cs.tertiary.copy(alpha = (if (isPlaying) 0.24f else 0.14f) * accentStrength.coerceAtMost(1.25f))
    val moteColorWhite = Color.White.copy(alpha = if (isDarkTheme) (if (isPlaying) 0.16f else 0.09f) else 0.12f)

    val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && intensity > 0.15f) {
        Modifier.blur((18.dp * intensity).coerceAtLeast(4.dp))
    } else {
        Modifier
    }

    Box(modifier = modifier.background(cs.background)) {
        when (backdropStyle) {
            com.example.model.BackdropAnimationStyle.SUBTLE_MIST -> {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(blurModifier)
                ) {
                    val w = size.width
                    val h = size.height
                    val topCenter = Offset(w * (0.5f + 0.14f * cos(phase)), h * 0.14f)
                    val bottomCenter = Offset(w * (0.5f - 0.14f * sin(phase)), h * 0.88f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(c1.copy(alpha = c1.alpha * 0.85f), Color.Transparent),
                            center = topCenter,
                            radius = w * 0.95f
                        ),
                        radius = w * 0.95f,
                        center = topCenter
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(c2.copy(alpha = c2.alpha * 0.72f), Color.Transparent),
                            center = bottomCenter,
                            radius = w * 0.85f
                        ),
                        radius = w * 0.85f,
                        center = bottomCenter
                    )
                }
            }

            com.example.model.BackdropAnimationStyle.PRISM_RIBBON -> {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(blurModifier)
                ) {
                    val w = size.width
                    val h = size.height
                    val driftA = 0.16f * sin(phase)
                    val driftB = 0.14f * cos(phase * 0.8f)
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                c1.copy(alpha = (c1.alpha * 1.15f).coerceAtMost(0.32f)),
                                c2.copy(alpha = (c2.alpha * 1.05f).coerceAtMost(0.28f)),
                                Color.Transparent
                            ),
                            start = Offset(w * (0.05f + driftA), h * (0.05f - driftB)),
                            end = Offset(w * (0.95f + driftB), h * (0.65f + driftA))
                        )
                    )
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                c3.copy(alpha = (c3.alpha * 1.1f).coerceAtMost(0.28f)),
                                c4.copy(alpha = (c4.alpha * 0.95f).coerceAtMost(0.26f)),
                                Color.Transparent
                            ),
                            start = Offset(w * (0.9f - driftA), h * (0.35f + driftB)),
                            end = Offset(w * (0.1f + driftB), h * (0.98f - driftA))
                        )
                    )
                }
            }

            else -> {
                // AURORA_ORBS: Living Dreamlike Aurora Mesh Orbs + Ethereal Bokeh Motes
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(blurModifier)
                ) {
                    val w = size.width
                    val h = size.height
                    val orb1 = Offset(
                        x = w * (0.18f + 0.11f * cos(phase)),
                        y = h * (0.10f + 0.06f * sin(phase))
                    )
                    val orb2 = Offset(
                        x = w * (0.84f + 0.09f * sin(phase * 0.85f)),
                        y = h * (0.28f + 0.08f * cos(phase * 0.85f))
                    )
                    val orb3 = Offset(
                        x = w * (0.46f + 0.14f * cos(phase * 0.65f + 1.4f)),
                        y = h * (0.78f + 0.07f * sin(phase * 0.65f))
                    )
                    val orb4 = Offset(
                        x = w * (0.22f + 0.12f * sin(phase * 0.55f + 2.3f)),
                        y = h * (0.52f + 0.10f * cos(phase * 0.55f + 0.9f))
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(c1, Color.Transparent),
                            center = orb1,
                            radius = w * 0.74f
                        ),
                        radius = w * 0.74f,
                        center = orb1
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(c2, Color.Transparent),
                            center = orb2,
                            radius = w * 0.64f
                        ),
                        radius = w * 0.64f,
                        center = orb2
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(c3, Color.Transparent),
                            center = orb3,
                            radius = w * 0.70f
                        ),
                        radius = w * 0.70f,
                        center = orb3
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(c4, Color.Transparent),
                            center = orb4,
                            radius = w * 0.54f
                        ),
                        radius = w * 0.54f,
                        center = orb4
                    )
                }

                if (!amoledBlack && intensity > 0.18f) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val moteCount = 14
                        for (i in 0 until moteCount) {
                            val seed = i * 1.6180339f
                            val baseX = ((seed * 0.37f) % 1f)
                            val baseY = ((seed * 0.61f) % 1f)
                            val driftX = 0.045f * cos(phase * (0.5f + (i % 3) * 0.25f) + seed)
                            val driftY = 0.055f * sin(phase * (0.4f + (i % 4) * 0.2f) + seed * 1.3f)
                            val cx = w * (baseX + driftX).coerceIn(0.03f, 0.97f)
                            val cy = h * (baseY + driftY).coerceIn(0.03f, 0.97f)
                            val pulse = 0.72f + 0.28f * sin(phase * 1.5f + seed)
                            val radius = (3.5f + (i % 5) * 3.8f) * density * pulse
                            val moteColor = when (i % 3) {
                                0 -> moteColorPrimary
                                1 -> moteColorTertiary
                                else -> moteColorWhite
                            }
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(moteColor, Color.Transparent),
                                    center = Offset(cx, cy),
                                    radius = radius * 2.2f
                                ),
                                radius = radius * 2.2f,
                                center = Offset(cx, cy)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Audio-Reactive Edge Lighting Glow.
 * Gently illuminates the screen perimeter in sync with live PCM spectrum energy
 * using the active Material You primary / tertiary / secondary palette.
 */
@Composable
fun AudioReactiveEdgeGlowOverlay(
    spectrumLevelsFlow: StateFlow<List<Float>>,
    isPlaying: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!enabled || !isPlaying) return
    val levels by spectrumLevelsFlow.collectAsStateWithLifecycle()
    val avgEnergy = remember(levels) {
        if (levels.isEmpty()) 0.15f else levels.average().toFloat().coerceIn(0.08f, 1f)
    }
    val smoothEnergy by animateFloatAsState(
        targetValue = avgEnergy,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow),
        label = "edgeGlowEnergy"
    )

    val cs = MaterialTheme.colorScheme
    val sweepTransition = rememberInfiniteTransition(label = "edgeGlowSweep")
    val shift by sweepTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "edgeGlowShift"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val alpha = (0.14f + smoothEnergy * 0.42f).coerceIn(0.12f, 0.62f)
        val strokePx = (4.dp.toPx() + smoothEnergy * 7.dp.toPx())
        val brush = Brush.linearGradient(
            colors = listOf(
                cs.primary.copy(alpha = alpha),
                cs.tertiary.copy(alpha = alpha * 0.9f),
                cs.secondary.copy(alpha = alpha * 0.85f),
                cs.primary.copy(alpha = alpha),
            ),
            start = Offset(size.width * shift, 0f),
            end = Offset(size.width * (1f - shift), size.height)
        )
        drawRoundRect(
            brush = brush,
            cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
            style = Stroke(width = strokePx)
        )
    }
}

/**
 * Pure parser for standard `.lrc` time-stamped lyrics (`[mm:ss.xx]` or `[mm:ss]`).
 * Gracefully falls back to plain lines if no timestamps exist.
 */
object LrcLyricsParser {
    private val TIMESTAMP_REGEX = Regex("""\[(\d{1,2}):(\d{2})(?:[.:](\d{1,3}))?]""")

    fun parse(raw: String?): List<LrcLine> {
        if (raw.isNullOrBlank()) return emptyList()
        val parsed = mutableListOf<LrcLine>()
        raw.lines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) return@forEach
            val matches = TIMESTAMP_REGEX.findAll(trimmed).toList()
            if (matches.isNotEmpty()) {
                val lyricBody = TIMESTAMP_REGEX.replace(trimmed, "").trim()
                if (lyricBody.isNotEmpty()) {
                    matches.forEach { m ->
                        val min = m.groupValues[1].toLongOrNull() ?: 0L
                        val sec = m.groupValues[2].toLongOrNull() ?: 0L
                        val fracRaw = m.groupValues.getOrNull(3).orEmpty()
                        val ms = when (fracRaw.length) {
                            1 -> (fracRaw.toLongOrNull() ?: 0L) * 100L
                            2 -> (fracRaw.toLongOrNull() ?: 0L) * 10L
                            3 -> fracRaw.toLongOrNull() ?: 0L
                            else -> 0L
                        }
                        parsed += LrcLine(
                            timeMs = (min * 60_000L) + (sec * 1_000L) + ms,
                            text = lyricBody
                        )
                    }
                }
            }
        }
        return parsed.sortedBy { it.timeMs }
    }

    fun hasTimestamps(raw: String?): Boolean = parse(raw).isNotEmpty()

    fun activeLineIndex(lines: List<LrcLine>, positionMs: Long): Int {
        if (lines.isEmpty()) return -1
        var active = 0
        for (i in lines.indices) {
            if (positionMs >= lines[i].timeMs) {
                active = i
            } else {
                break
            }
        }
        return active
    }
}

/**
 * Synced Live Karaoke Lyrics Card.
 * Highlights the active line dynamically as the song plays and lets users tap any line
 * to jump straight to that exact timestamp.
 */
@Composable
fun SyncedKaraokeLyricsCard(
    lyricsText: String?,
    positionMsFlow: StateFlow<Long>,
    durationMs: Long,
    strings: AppStrings,
    onSeekTo: (Long) -> Unit,
    onEditLyrics: () -> Unit,
    fontSizeSp: Int = 16,
    onChangeFontSize: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val lrcLines = remember(lyricsText) { LrcLyricsParser.parse(lyricsText) }
    val plainLines = remember(lyricsText) {
        lyricsText.orEmpty().lines().map { it.trim() }.filter { it.isNotEmpty() }
    }
    val hasSynced = lrcLines.isNotEmpty()
    val posMs by positionMsFlow.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(true) }
    val clampedSp = fontSizeSp.coerceIn(13, 24)

    val activeIndex = remember(lrcLines, plainLines, posMs, durationMs) {
        if (hasSynced) {
            LrcLyricsParser.activeLineIndex(lrcLines, posMs)
        } else if (plainLines.isNotEmpty() && durationMs > 1000L) {
            val frac = (posMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 0.999f)
            (frac * plainLines.size).toInt().coerceIn(0, plainLines.lastIndex)
        } else {
            -1
        }
    }

    MaterialLiquidGlassSurface(
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 6.dp,
        tonalBoost = 1.25f,
        modifier = modifier.testTag("full_player_lyrics_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lyrics,
                        contentDescription = strings.lyricsTitle,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = if (hasSynced) strings.syncedLyricsTitle else strings.lyricsTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (hasSynced) {
                            Text(
                                text = strings.syncedLyricsTapHint,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (expanded && onChangeFontSize != null) {
                        Surface(
                            onClick = { onChangeFontSize((clampedSp - 2).coerceAtLeast(13)) },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.testTag("lyrics_font_dec")
                        ) {
                            Text(
                                text = "A-",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            onClick = { onChangeFontSize((clampedSp + 2).coerceAtMost(24)) },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            modifier = Modifier.testTag("lyrics_font_inc")
                        ) {
                            Text(
                                text = "A+",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Surface(
                        onClick = onEditLyrics,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.testTag("full_player_edit_lyrics")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = strings.editLyricsAction,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = strings.editLyricsAction,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            if (expanded) {
                when {
                    hasSynced -> {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            lrcLines.forEachIndexed { idx, line ->
                                val isActive = idx == activeIndex
                                val lineColor by animateColorAsState(
                                    targetValue = if (isActive) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                                    },
                                    label = "lrcLineColor"
                                )
                                val bgColor by animateColorAsState(
                                    targetValue = if (isActive) {
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f)
                                    } else {
                                        Color.Transparent
                                    },
                                    label = "lrcLineBg"
                                )
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = bgColor,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onSeekTo(line.timeMs)
                                        }
                                        .testTag("lrc_line_$idx")
                                ) {
                                    Text(
                                        text = line.text,
                                        fontSize = if (isActive) androidx.compose.ui.unit.TextUnit(clampedSp + 2f, androidx.compose.ui.unit.TextUnitType.Sp)
                                        else androidx.compose.ui.unit.TextUnit(clampedSp.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp),
                                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                                        color = lineColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                    plainLines.isNotEmpty() -> {
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            plainLines.forEachIndexed { idx, line ->
                                val isActive = idx == activeIndex
                                Text(
                                    text = line,
                                    fontSize = if (isActive) androidx.compose.ui.unit.TextUnit(clampedSp + 2f, androidx.compose.ui.unit.TextUnitType.Sp)
                                    else androidx.compose.ui.unit.TextUnit(clampedSp.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp),
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isActive) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                    }
                                )
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = strings.noLyricsYetHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Bottom Collapse Pill so user never has to scroll back up to close lyrics
                Surface(
                    onClick = { expanded = false },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .testTag("lyrics_collapse_bottom")
                ) {
                    Text(
                        text = "▲ ${strings.collapseSectionAction}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

/**
 * 1-Tap Smart Mixes bar for instant mood/collection switching.
 */
@Composable
fun SmartMusicMixesRow(
    selectedMix: SmartMusicMix,
    strings: AppStrings,
    onSelectMix: (SmartMusicMix) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val mixes = remember {
        listOf(
            Triple(SmartMusicMix.ALL_TRACKS, Icons.Filled.LibraryMusic, "mix_all"),
            Triple(SmartMusicMix.QURAN_RECITATIONS, Icons.Filled.MenuBook, "mix_quran"),
            Triple(SmartMusicMix.FAVORITES_MIX, Icons.Filled.Favorite, "mix_favorites"),
            Triple(SmartMusicMix.RECENTLY_ADDED, Icons.Filled.NewReleases, "mix_recent"),
            Triple(SmartMusicMix.LONG_LECTURES, Icons.Filled.Podcasts, "mix_lectures"),
            Triple(SmartMusicMix.HI_RES_LOSSLESS, Icons.Filled.HighQuality, "mix_hires"),
            Triple(SmartMusicMix.MOST_PLAYED_MIX, Icons.AutoMirrored.Filled.TrendingUp, "mix_most_played"),
            Triple(SmartMusicMix.QUICK_DISCOVERY, Icons.Filled.Shuffle, "mix_discovery"),
        )
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(mixes, key = { it.third }) { (mix, icon, tag) ->
            val selected = selectedMix == mix
            val interaction = remember { MutableInteractionSource() }
            Surface(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelectMix(mix)
                },
                interactionSource = interaction,
                shape = CircleShape,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.82f)
                },
                border = BorderStroke(
                    1.dp,
                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .expressivePressScale(interaction, pressedScale = 0.93f)
                    .testTag(tag)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = strings.smartMixName(mix),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
