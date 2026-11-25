package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.model.AppSettings
import kotlin.math.PI
import kotlin.math.sin

/**
 * Tactile Google Material 3 Expressive spring scale press feedback.
 */
fun Modifier.expressivePressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.93f,
): Modifier = composed {
    val motionFeel = com.example.ui.theme.LocalMotionFeelStyle.current
    val pressed by interactionSource.collectIsPressedAsState()
    val targetScale = when (motionFeel) {
        com.example.model.MotionFeelStyle.REDUCED_MOTION -> if (pressed) 0.985f else 1f
        com.example.model.MotionFeelStyle.BOUNCY_EXPRESSIVE -> if (pressed) pressedScale else 1f
        com.example.model.MotionFeelStyle.SMOOTH_FLUID -> if (pressed) ((pressedScale + 1f) / 2f) else 1f
        com.example.model.MotionFeelStyle.SNAPPY_FAST -> if (pressed) pressedScale else 1f
    }
    val spec = when (motionFeel) {
        com.example.model.MotionFeelStyle.BOUNCY_EXPRESSIVE -> spring<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
        com.example.model.MotionFeelStyle.SMOOTH_FLUID -> spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        )
        com.example.model.MotionFeelStyle.SNAPPY_FAST -> spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh
        )
        com.example.model.MotionFeelStyle.REDUCED_MOTION -> tween(durationMillis = 80)
    }
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spec,
        label = "expressivePressScale"
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Authentic Google Android 13/14/15 Material You Squiggly Media Progress Bar.
 * Ripples smoothly when audio is playing and flattens seamlessly when paused or flat-mode is enabled.
 */
@Composable
fun SquigglyAudioProgressBar(
    progress: Float,
    isPlaying: Boolean,
    squigglyEnabled: Boolean,
    onSeekFraction: (Float) -> Unit,
    modifier: Modifier = Modifier,
    showThumb: Boolean = true,
    strokeWidth: Dp = 3.5.dp,
    waveAmplitudeDp: Dp = 3.2.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val haptics = LocalHapticFeedback.current

    val targetAmp = if (isPlaying && squigglyEnabled) 1f else 0f
    val animatedAmp by animateFloatAsState(
        targetValue = targetAmp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "squigglyAmplitude"
    )

    val phase = if (animatedAmp > 0.01f) {
        val infinite = rememberInfiniteTransition(label = "squigglyPhase")
        val p by infinite.animateFloat(
            initialValue = 0f,
            targetValue = (2.0 * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1250, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "squigglyWavePhase"
        )
        p
    } else {
        0f
    }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val frac = (offset.x / size.width.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSeekFraction(frac)
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    change.consume()
                    val frac = (change.position.x / size.width.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
                    onSeekFraction(frac)
                }
            }
    ) {
        val w = size.width
        val centerY = size.height / 2f
        val strokePx = strokeWidth.toPx()
        val thumbHalfGap = if (showThumb) 8.dp.toPx() else 0f
        val activeEndX = (w * clampedProgress).coerceIn(0f, w)

        // Inactive flat track on the right of the thumb
        val inactiveStartX = (activeEndX + thumbHalfGap).coerceAtMost(w)
        if (inactiveStartX < w) {
            drawLine(
                color = inactiveColor,
                start = Offset(inactiveStartX, centerY),
                end = Offset(w, centerY),
                strokeWidth = strokePx,
                cap = StrokeCap.Round
            )
        }

        // Active squiggly / flat track on the left of the thumb
        val waveEndX = (activeEndX - if (showThumb) 4.dp.toPx() else 0f).coerceAtLeast(0f)
        if (waveEndX > 1f) {
            val ampPx = waveAmplitudeDp.toPx() * animatedAmp
            if (ampPx > 0.15f) {
                val wavelengthPx = 22.dp.toPx()
                val stepPx = 3f
                val path = Path()
                var x = 0f
                var first = true
                while (x <= waveEndX) {
                    // Gently taper wave near the playhead so it meets the thumb cleanly
                    val distFromEnd = (waveEndX - x).coerceAtLeast(0f)
                    val taper = (distFromEnd / 14.dp.toPx()).coerceIn(0f, 1f)
                    val y = centerY + sin((x / wavelengthPx) * 2f * PI.toFloat() - phase) * ampPx * taper
                    if (first) {
                        path.moveTo(x, y)
                        first = false
                    } else {
                        path.lineTo(x, y)
                    }
                    x += stepPx
                }
                path.lineTo(waveEndX, centerY)
                drawPath(
                    path = path,
                    color = activeColor,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            } else {
                drawLine(
                    color = activeColor,
                    start = Offset(0f, centerY),
                    end = Offset(waveEndX, centerY),
                    strokeWidth = strokePx,
                    cap = StrokeCap.Round
                )
            }
        }

        // Authentic Google Material You vertical rounded-pill thumb
        if (showThumb) {
            val thumbW = 4.5.dp.toPx()
            val thumbH = 16.dp.toPx()
            val thumbX = activeEndX.coerceIn(thumbW / 2f, (w - thumbW / 2f).coerceAtLeast(thumbW / 2f))
            drawRoundRect(
                color = activeColor,
                topLeft = Offset(thumbX - thumbW / 2f, centerY - thumbH / 2f),
                size = Size(thumbW, thumbH),
                cornerRadius = CornerRadius(thumbW / 2f, thumbW / 2f)
            )
        }
    }
}

/**
 * Authentic Material You Expressive Morphing Play/Pause Button:
 * Smoothly morphs corner radius between Circle (paused) and Rounded Squircle (playing)
 * with spring bounce press physics.
 */
@Composable
fun MorphingPlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    val haptics = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val cornerRadius by animateDpAsState(
        targetValue = if (isPlaying) size * 0.30f else size * 0.50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "morphPlayPauseCorner"
    )

    Surface(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(cornerRadius),
        color = containerColor,
        shadowElevation = 4.dp,
        modifier = modifier
            .size(size)
            .expressivePressScale(interactionSource, pressedScale = 0.88f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            AnimatedContent(
                targetState = isPlaying,
                label = "morphPlayPauseIcon"
            ) { playing ->
                Icon(
                    imageVector = if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (playing) "Pause" else "Play",
                    tint = contentColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

/**
 * Google-Style "Drop-Through / Drag-to-X" Dismiss Target Zone.
 * Appears smoothly at the bottom when dragging a Floating Audio Bubble or Floating Video Window.
 * Releasing inside the X circle stops playback completely and dismisses the window.
 */
@Composable
fun GoogleDismissDropZone(
    visible: Boolean,
    isHovered: Boolean,
    strings: AppStrings,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(isHovered) {
        if (isHovered) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
        ) + fadeIn(tween(150)) + scaleIn(initialScale = 0.8f),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(tween(130)) + scaleOut(targetScale = 0.8f),
        modifier = modifier
    ) {
        val circleSize by animateDpAsState(
            targetValue = if (isHovered) 74.dp else 60.dp,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
            label = "dropZoneSize"
        )
        val circleBg by animateColorAsState(
            targetValue = if (isHovered) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.96f)
            },
            label = "dropZoneBg"
        )
        val iconTint by animateColorAsState(
            targetValue = if (isHovered) {
                MaterialTheme.colorScheme.onError
            } else {
                MaterialTheme.colorScheme.onErrorContainer
            },
            label = "dropZoneIcon"
        )

        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.94f),
                tonalElevation = 6.dp
            ) {
                Text(
                    text = if (isHovered) strings.releaseToStopHint else strings.dragToXHint,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isHovered) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Surface(
                shape = CircleShape,
                color = circleBg,
                shadowElevation = if (isHovered) 18.dp else 10.dp,
                border = BorderStroke(
                    width = if (isHovered) 3.dp else 1.5.dp,
                    color = if (isHovered) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .size(circleSize)
                    .testTag("google_drag_to_x_zone")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = strings.pipStopAction,
                        tint = iconTint,
                        modifier = Modifier.size(if (isHovered) 34.dp else 28.dp)
                    )
                }
            }
        }
    }
}

/**
 * Reusable Material You Player Bar Customizer Content — lets users toggle Squiggly Progress,
 * Compact Size, Floating Mode, and every individual companion add-on chip in real time.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayerBarCustomizerContent(
    settings: AppSettings,
    strings: AppStrings,
    onSquigglyChange: (Boolean) -> Unit,
    onCompactChange: (Boolean) -> Unit,
    onFloatingChange: (Boolean) -> Unit,
    onToggleAddon: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Primary layout toggles
        CustomizerSwitchRow(
            icon = Icons.Filled.Waves,
            title = strings.squigglyBarTitle,
            subtitle = strings.squigglyBarDesc,
            checked = settings.squigglyProgress,
            onCheckedChange = onSquigglyChange,
            tag = "customizer_squiggly"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                onClick = { onCompactChange(!settings.miniPlayerCompact) },
                shape = RoundedCornerShape(20.dp),
                color = if (settings.miniPlayerCompact) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                },
                border = if (settings.miniPlayerCompact) {
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                } else null,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("customizer_compact_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (settings.miniPlayerCompact) Icons.Filled.OpenInFull else Icons.Filled.CloseFullscreen,
                        contentDescription = null,
                        tint = if (settings.miniPlayerCompact) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (settings.miniPlayerCompact) strings.compactBarAction else strings.expandBarAction,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                onClick = { onFloatingChange(!settings.miniPlayerFloating) },
                shape = RoundedCornerShape(20.dp),
                color = if (settings.miniPlayerFloating) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                },
                border = if (settings.miniPlayerFloating) {
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary)
                } else null,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("customizer_floating_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PictureInPictureAlt,
                        contentDescription = null,
                        tint = if (settings.miniPlayerFloating) {
                            MaterialTheme.colorScheme.onTertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (settings.miniPlayerFloating) strings.dockPlayerAction else strings.floatPlayerAction,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = strings.barAddonsHeader,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        val addonItems = listOf(
            Triple("spectrum", strings.addonSpectrum, Pair(Icons.Filled.GraphicEq, settings.barShowSpectrum)),
            Triple("seek10", strings.addonSeek10, Pair(Icons.Filled.Forward10, settings.barShowSeek10)),
            Triple("favorite", strings.addonFavorite, Pair(Icons.Filled.Favorite, settings.barShowFavorite)),
            Triple("speed", strings.addonSpeed, Pair(Icons.Filled.Speed, settings.barShowSpeed)),
            Triple("ab_loop", strings.addonAbLoop, Pair(Icons.Filled.Loop, settings.barShowAbLoop)),
            Triple("eq", strings.addonEq, Pair(Icons.Filled.Equalizer, settings.barShowEq)),
            Triple("timer", strings.addonTimer, Pair(Icons.Filled.Timer, settings.barShowTimer)),
            Triple("queue", strings.addonQueue, Pair(Icons.AutoMirrored.Filled.QueueMusic, settings.barShowQueue)),
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            addonItems.forEach { (key, label, iconAndState) ->
                val (icon, enabled) = iconAndState
                FilterChip(
                    selected = enabled,
                    onClick = { onToggleAddon(key, !enabled) },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(
                            text = label,
                            fontWeight = if (enabled) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    shape = CircleShape,
                    modifier = Modifier.testTag("addon_chip_$key")
                )
            }
        }
    }
}

@Composable
private fun CustomizerSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String,
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(tag)
            )
        }
    }
}
