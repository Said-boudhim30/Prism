package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppStrings
import com.example.model.AppSettings
import com.example.model.LiquidGlassStyle
import com.example.model.MediaTab
import com.example.model.NavBarStyle
import com.example.model.PlayerArtworkStyle
import com.example.model.ReverbEnvironment
import com.example.ui.theme.TrackGenreMood
import com.example.ui.theme.TrackGenrePaletteEngine
import kotlin.math.sin

@Composable
private fun LivePreviewHeaderPill(
    label: String,
    accent: Color = MaterialTheme.colorScheme.primary,
) {
    Surface(
        shape = CircleShape,
        color = accent.copy(alpha = 0.16f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = accent
            )
        }
    }
}

/**
 * 1. LIVE APPEARANCE PREVIEW: Reacts instantly to Liquid Glass vs Material You,
 * Accent Palette, Corner Roundness, Font Scale/Style, and Contrast Level.
 */
@Composable
fun LiveAppearancePreviewCard(
    settings: AppSettings,
    strings: AppStrings,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val corner = scaledCornerDp(24.dp, settings.shapeRoundness)
    val isPureM3 = settings.liquidGlassStyle == LiquidGlassStyle.SOLID_M3
    val containerColor = when (settings.cardStyle) {
        com.example.model.MediaCardStyle.GLASS_CARD ->
            if (isPureM3) cs.surfaceContainerHigh else cs.surfaceContainer.copy(alpha = 0.68f)
        com.example.model.MediaCardStyle.ELEVATED_M3 -> cs.surfaceContainerHigh
        com.example.model.MediaCardStyle.MINIMAL_OUTLINE -> cs.surface
        com.example.model.MediaCardStyle.VIBRANT_TONAL -> cs.primaryContainer.copy(alpha = 0.45f)
    }
    val borderStroke = when (settings.cardStyle) {
        com.example.model.MediaCardStyle.GLASS_CARD -> BorderStroke(1.4.dp, cs.primary.copy(alpha = 0.38f))
        com.example.model.MediaCardStyle.ELEVATED_M3 -> BorderStroke(1.dp, cs.outlineVariant.copy(alpha = 0.35f))
        com.example.model.MediaCardStyle.MINIMAL_OUTLINE -> BorderStroke(1.2.dp, cs.outline.copy(alpha = 0.55f))
        com.example.model.MediaCardStyle.VIBRANT_TONAL -> BorderStroke(1.5.dp, cs.primary.copy(alpha = 0.55f))
    }

    Surface(
        shape = RoundedCornerShape(corner),
        color = containerColor,
        tonalElevation = if (settings.cardStyle == com.example.model.MediaCardStyle.ELEVATED_M3) 6.dp else 1.dp,
        border = borderStroke,
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_preview_appearance")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isPureM3 || settings.cardStyle == com.example.model.MediaCardStyle.MINIMAL_OUTLINE) {
                        Brush.horizontalGradient(listOf(Color.Transparent, cs.primaryContainer.copy(alpha = 0.12f)))
                    } else {
                        Brush.linearGradient(
                            listOf(
                                cs.primary.copy(alpha = 0.22f * settings.liquidGlassIntensity.coerceIn(0.25f, 1f)),
                                cs.tertiary.copy(alpha = 0.16f * settings.liquidGlassIntensity.coerceIn(0.25f, 1f)),
                                cs.surfaceContainerLow.copy(alpha = 0.45f)
                            )
                        )
                    }
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LivePreviewHeaderPill(strings.livePreviewBadge)
                    Text(
                        text = "${strings.cardStyleName(settings.cardStyle)} • ${strings.backdropStyleName(settings.backdropStyle)}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(scaledCornerDp(18.dp, settings.shapeRoundness)),
                        color = cs.primaryContainer,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = cs.onPrimaryContainer)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Prism Media • ${strings.paletteName(settings.accentPalette)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${strings.motionFeelName(settings.motionFeelStyle)} • ${(settings.fontScaleMultiplier * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (settings.showFormatBadges) {
                        Surface(
                            shape = CircleShape,
                            color = cs.primary,
                        ) {
                            Text(
                                text = "FLAC",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = cs.onPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2. LIVE FULL-SCREEN PLAYER CARD PREVIEW (Interactive!):
 * Users can tap Genre chips right inside the preview to watch the dynamic genre palette,
 * Material 3 Expressive artwork shape, subtle extras button, squiggly bar, spectrum & play morph live!
 */
@Composable
fun LiveFullPlayerCardPreview(
    settings: AppSettings,
    strings: AppStrings,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    var demoMood by remember { mutableStateOf(TrackGenreMood.QURAN_SPIRITUAL) }
    var demoPlaying by remember { mutableStateOf(true) }

    val pal = remember(demoMood, settings.darkTheme, settings.playerGenreDynamicColors) {
        if (settings.playerGenreDynamicColors) {
            TrackGenrePaletteEngine.paletteForMood(demoMood, isDark = settings.darkTheme)
        } else {
            TrackGenrePaletteEngine.paletteForMood(demoMood, isDark = settings.darkTheme).copy(
                primaryAccent = cs.primary,
                secondaryGlow = cs.tertiary,
                deepBackdrop = cs.surfaceContainerHigh
            )
        }
    }
    val accent by animateColorAsState(pal.primaryAccent, tween(450), label = "prevAccent")
    val glow by animateColorAsState(pal.secondaryGlow, tween(450), label = "prevGlow")
    val deep by animateColorAsState(pal.deepBackdrop, tween(450), label = "prevDeep")
    val artShape = PrismExpressiveShapes.artworkShapeFor(settings.playerArtworkStyle, settings.shapeRoundness)

    val playCorner by animateDpAsState(
        targetValue = if (settings.playerExpressivePlayMorph && demoPlaying) 14.dp else 22.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "prevPlayMorph"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_preview_player_card"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Miniature Full-Screen Player Card Frame
        Surface(
            shape = RoundedCornerShape(scaledCornerDp(28.dp, settings.shapeRoundness)),
            color = deep,
            border = BorderStroke(1.2.dp, accent.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                accent.copy(alpha = if (settings.darkTheme) 0.32f else 0.22f),
                                deep.copy(alpha = 0.92f),
                                glow.copy(alpha = if (settings.darkTheme) 0.24f else 0.14f)
                            )
                        )
                    )
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top bar with subtle compact Extras button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LivePreviewHeaderPill(strings.livePreviewBadge, accent)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(16.dp)
                            )
                            Surface(
                                shape = CircleShape,
                                color = accent.copy(alpha = if (settings.playerExtrasButtonSubtle) 0.16f else 0.34f),
                                modifier = Modifier
                                    .size(if (settings.playerExtrasButtonSubtle) 26.dp else 32.dp)
                                    .alpha(if (settings.playerExtrasButtonSubtle) 0.68f else 1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.MoreHoriz,
                                        contentDescription = null,
                                        tint = cs.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Expressive Artwork Shape Preview
                    Box(
                        modifier = Modifier
                            .size(106.dp)
                            .clip(artShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(accent.copy(alpha = 0.85f), glow.copy(alpha = 0.78f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MusicNote,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                        if (settings.playerArtworkStyle == PlayerArtworkStyle.VINYL_DISC) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(deep)
                            )
                        }
                    }

                    // Track title + optional Tech Telemetry Pill
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = strings.genreMoodName(demoMood),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                        if (settings.playerShowTechTelemetry) {
                            Spacer(Modifier.height(3.dp))
                            Surface(
                                shape = CircleShape,
                                color = accent.copy(alpha = 0.18f)
                            ) {
                                Text(
                                    text = if (settings.playerDirectBitExactDsp) {
                                        "FLAC • 48kHz • Bit-Exact Pure"
                                    } else {
                                        "FLAC • 48kHz • 32-bit DSP${if (settings.reverbEnvironment != ReverbEnvironment.NONE) " • 3D" else ""}"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    if (settings.showFullPlayerSpectrum) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .height(16.dp)
                        ) {
                            val bars = 16
                            val step = size.width / bars
                            for (i in 0 until bars) {
                                val hFrac = (0.35f + 0.6f * kotlin.math.abs(sin(i * 0.72 + (if (demoPlaying) 1.0 else 0.2)))).toFloat()
                                val barH = size.height * hFrac
                                drawLine(
                                    color = accent.copy(alpha = 0.85f),
                                    start = Offset(i * step + step / 2f, size.height),
                                    end = Offset(i * step + step / 2f, size.height - barH),
                                    strokeWidth = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }

                    // Bottom Seek + Transport Controls Preview
                    SquigglyAudioProgressBar(
                        progress = 0.56f,
                        isPlaying = demoPlaying,
                        squigglyEnabled = settings.squigglyProgress,
                        onSeekFraction = {},
                        activeColor = accent,
                        inactiveColor = cs.onSurface.copy(alpha = 0.20f),
                        strokeWidth = 2.6.dp,
                        waveAmplitudeDp = 2.2.dp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.SkipPrevious, contentDescription = null, modifier = Modifier.size(22.dp))
                        Surface(
                            onClick = { demoPlaying = !demoPlaying },
                            shape = RoundedCornerShape(playCorner),
                            color = accent,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (demoPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Icon(Icons.Filled.SkipNext, contentDescription = null, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }

        // Interactive Genre Mood Tester Strip (tap to watch live genre color shifts!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TrackGenreMood.entries.forEach { mood ->
                val moodPal = TrackGenrePaletteEngine.paletteForMood(mood, isDark = settings.darkTheme)
                val selected = demoMood == mood
                Surface(
                    onClick = { demoMood = mood },
                    shape = CircleShape,
                    color = if (selected) moodPal.primaryAccent else cs.surfaceContainerHigh,
                    border = BorderStroke(1.dp, moodPal.primaryAccent.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = strings.genreMoodName(mood),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) Color.White else cs.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

/**
 * 3. LIVE LOCK-SCREEN PREVIEW: Realistic miniature Android Lock Screen showing live clock,
 * Prism branded identity badge, expressive artwork, lyrics line, compact/full mode & controls.
 */
@Composable
fun LiveLockScreenPreviewCard(
    settings: AppSettings,
    strings: AppStrings,
    onPreviewFullLockScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val artShape = PrismExpressiveShapes.artworkShapeFor(settings.playerArtworkStyle, settings.shapeRoundness)

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color(0xFF0A0D18),
        border = BorderStroke(1.5.dp, cs.primary.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_preview_lock_screen")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (settings.lockScreenBlurBackdrop) {
                        Brush.verticalGradient(
                            listOf(
                                cs.primary.copy(alpha = 0.34f),
                                Color(0xFF0B1020),
                                cs.tertiary.copy(alpha = 0.25f)
                            )
                        )
                    } else {
                        Brush.verticalGradient(listOf(Color(0xFF090B12), Color(0xFF10131F)))
                    }
                )
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LivePreviewHeaderPill(strings.livePreviewBadge, cs.primary)
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.72f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (settings.lockScreenShowPrismIdentity) {
                    Surface(
                        shape = CircleShape,
                        color = cs.primary.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = cs.primary, modifier = Modifier.size(12.dp))
                            Text(
                                text = "Prism Media",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                if (settings.lockScreenShowClock) {
                    Text(
                        text = "09:41",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )
                }

                // Miniature Lock-Screen Media Card
                if (settings.lockScreenControlsEnabled) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = Color(0xFF151A2C).copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (settings.lockScreenCompactMode) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (settings.lockScreenShowArtwork) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(artShape)
                                                .background(cs.primary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Surah Al-Mulk", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Prism Audio • FLAC", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.68f))
                                    }
                                    Icon(Icons.Filled.Pause, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                }
                            } else {
                                if (settings.lockScreenShowArtwork) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(artShape)
                                            .background(Brush.linearGradient(listOf(cs.primary, cs.tertiary))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                    }
                                }
                                Text("Surah Al-Mulk", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Color.White)
                                if (settings.lockScreenShowLyrics) {
                                    Text(
                                        text = "♪ تبارك الذي بيده الملك...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = cs.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.SkipPrevious, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    Surface(shape = CircleShape, color = cs.primary, modifier = Modifier.size(34.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Filled.Pause, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                    Icon(Icons.Filled.SkipNext, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                FilledTonalButton(
                    onClick = onPreviewFullLockScreen,
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_preview_lock_screen_btn")
                ) {
                    Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(strings.lockScreenPreviewAction, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * 4. LIVE NAVIGATION BAR PREVIEW: Shows exact NavBarStyle + custom tab order live.
 */
@Composable
fun LiveNavBarPreviewCard(
    settings: AppSettings,
    strings: AppStrings,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val barShape = when (settings.navBarStyle) {
        NavBarStyle.FLOATING_PILL -> CircleShape
        NavBarStyle.DOCKED_MATERIAL_YOU -> RoundedCornerShape(16.dp)
        NavBarStyle.COMPACT_DOCK -> RoundedCornerShape(22.dp)
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = cs.surfaceContainerHigh,
        border = BorderStroke(1.dp, cs.outlineVariant.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_preview_nav_bar")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LivePreviewHeaderPill(strings.livePreviewBadge)
                Text(
                    text = strings.navBarStyleName(settings.navBarStyle),
                    style = MaterialTheme.typography.labelSmall,
                    color = cs.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = barShape,
                color = cs.surfaceContainerLowest,
                border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = if (settings.navBarStyle == NavBarStyle.COMPACT_DOCK) 6.dp else 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    settings.navTabOrder.forEach { tab ->
                        val active = tab == settings.startupTab
                        val icon = when (tab) {
                            MediaTab.MUSIC -> Icons.Filled.MusicNote
                            MediaTab.VIDEOS -> Icons.Filled.VideoLibrary
                            MediaTab.PHOTOS -> Icons.Filled.PhotoLibrary
                            MediaTab.SETTINGS -> Icons.Filled.Settings
                        }
                        Surface(
                            shape = CircleShape,
                            color = if (active) cs.primaryContainer else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (active) cs.onPrimaryContainer else cs.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                if (settings.navBarStyle != NavBarStyle.COMPACT_DOCK || active) {
                                    Text(
                                        text = strings.tabDisplayName(tab),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                        color = if (active) cs.onPrimaryContainer else cs.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 5. LIVE MUSIC DSP & EQ CURVE PREVIEW: Visualizes actual 5-band EQ frequency curve
 * + Preamp, Reverb, Gapless, Crossfade & Pitch live!
 */
@Composable
fun LiveMusicDspPreviewCard(
    settings: AppSettings,
    strings: AppStrings,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val gains = settings.eqGainsDb.ifEmpty { listOf(0f, 0f, 0f, 0f, 0f) }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = cs.surfaceContainerHigh,
        border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_preview_music_dsp")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LivePreviewHeaderPill(strings.livePreviewBadge)
                Text(
                    text = if (settings.playerDirectBitExactDsp) "Bit-Exact Pure" else "${strings.eqPresetName(settings.eqPreset)} • ${settings.audioPreampPercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = cs.primary
                )
            }

            // Live 5-Band Biquad EQ Frequency Response Curve
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(cs.surfaceContainerLowest)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                val midY = size.height / 2f
                drawLine(
                    color = cs.outlineVariant.copy(alpha = 0.4f),
                    start = Offset(0f, midY),
                    end = Offset(size.width, midY),
                    strokeWidth = 1.dp.toPx()
                )
                val stepX = if (gains.size > 1) size.width / (gains.size - 1) else size.width
                val path = Path()
                gains.forEachIndexed { idx, db ->
                    val effDb = if (settings.eqEnabled && !settings.playerDirectBitExactDsp) db else 0f
                    val x = idx * stepX
                    val y = midY - (effDb.coerceIn(-12f, 12f) / 12f) * (size.height * 0.42f)
                    if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(
                    path = path,
                    color = if (settings.eqEnabled && !settings.playerDirectBitExactDsp) cs.primary else cs.outline,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                gains.forEachIndexed { idx, db ->
                    val effDb = if (settings.eqEnabled && !settings.playerDirectBitExactDsp) db else 0f
                    val x = idx * stepX
                    val y = midY - (effDb.coerceIn(-12f, 12f) / 12f) * (size.height * 0.42f)
                    drawCircle(
                        color = cs.primary,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val badges = listOf(
                    "32-Bit DSP",
                    if (settings.gaplessPlayback) "Gapless" else "Standard",
                    if (settings.crossfadeSeconds > 0) "Fade ${settings.crossfadeSeconds}s" else "No Fade",
                    if (settings.reverbEnvironment != ReverbEnvironment.NONE) settings.reverbEnvironment.name else "Dry"
                )
                badges.forEach { badge ->
                    Surface(
                        shape = CircleShape,
                        color = cs.secondaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 6. LIVE VIDEO CINEMA PREVIEW: Visualizes Aspect Ratio, Subtitle Color/Size/Opacity,
 * Playback Speed, Mirror, and PiP state live.
 */
@Composable
fun LiveVideoCinemaPreviewCard(
    settings: AppSettings,
    strings: AppStrings,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val aspectLabel = when (settings.videoDefaultAspect) {
        1 -> strings.videoAspectCrop
        2 -> strings.videoAspectFill
        else -> strings.videoAspectFit
    }
    val innerFraction = when (settings.videoDefaultAspect) {
        1 -> 0.92f
        2 -> 1f
        else -> 0.78f
    }

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0A0C14),
        border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_preview_video_cinema")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LivePreviewHeaderPill(strings.livePreviewBadge)
                Text(
                    text = "$aspectLabel • ${settings.videoDefaultSpeed}x",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(innerFraction)
                        .fillMaxHeight()
                        .background(
                            Brush.linearGradient(
                                listOf(cs.primary.copy(alpha = 0.45f), cs.tertiary.copy(alpha = 0.35f))
                            )
                        )
                )
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(30.dp)
                )
                // Live Subtitle Style Preview
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = settings.videoSubtitleBgOpacity.coerceIn(0f, 1f)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Subtitles,
                            contentDescription = null,
                            tint = Color(settings.videoSubtitleColor.argb),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Prism Cinema • ${settings.videoSubtitleSizeSp}sp",
                            fontSize = (settings.videoSubtitleSizeSp * 0.62f).coerceIn(9f, 15f).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(settings.videoSubtitleColor.argb)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 7. LIVE PHOTO GALLERY PREVIEW: Visualizes actual Grid Columns, Corner Roundness,
 * Show Titles toggle, and Slideshow Transition live!
 */
@Composable
fun LivePhotoGridPreviewCard(
    settings: AppSettings,
    strings: AppStrings,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val cols = if (settings.photoGridColumns == 0) 3 else settings.photoGridColumns.coerceIn(2, 4)
    val tileCorner = scaledCornerDp(12.dp, settings.shapeRoundness)

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = cs.surfaceContainerHigh,
        border = BorderStroke(1.dp, cs.outlineVariant.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_preview_photo_grid")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LivePreviewHeaderPill(strings.livePreviewBadge)
                Text(
                    text = "${cols}× • ${settings.photoSlideshowSeconds}s • ${settings.photoSlideshowTransition.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = cs.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(cols) { idx ->
                    val tileColor = when (idx % 3) {
                        0 -> cs.primaryContainer
                        1 -> cs.secondaryContainer
                        else -> cs.tertiaryContainer
                    }
                    Surface(
                        shape = RoundedCornerShape(tileCorner),
                        color = tileColor,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.15f)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Filled.PhotoLibrary,
                                contentDescription = null,
                                tint = cs.onSurface.copy(alpha = 0.55f),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(20.dp)
                            )
                            if (settings.photoShowTitles) {
                                Text(
                                    text = "IMG_${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
