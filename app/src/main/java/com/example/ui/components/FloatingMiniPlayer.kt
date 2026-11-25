package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.rounded.CloseFullscreen
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.OpenInFull
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.localization.AppStrings
import com.example.model.AppSettings
import com.example.model.AudioTrack
import com.example.model.EdgeDockSide
import com.example.model.PlaybackUiState
import com.example.model.RepeatMode as TrackRepeatMode
import com.example.ui.theme.AlbumArtPalette
import com.example.ui.theme.AlbumArtPaletteExtractor
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

/**
 * Sticky Edge-Tuck Handle Button glued directly to the side of any minimized/floating media player
 * (Music, Video, or Photo). Tapping slides the player off to the screen edge while leaving THIS
 * EXACT SAME button anchored on the edge; tapping it again slides the player right back!
 */
@Composable
fun StickyEdgeTuckHandle(
    isTucked: Boolean,
    dockSide: EdgeDockSide,
    badgeIcon: ImageVector,
    isActivePulse: Boolean,
    contentDescription: String,
    testTag: String,
    onToggleTuck: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    val chevron = when (dockSide) {
        EdgeDockSide.RIGHT -> if (isTucked) Icons.Filled.ChevronLeft else Icons.Filled.ChevronRight
        EdgeDockSide.LEFT -> if (isTucked) Icons.Filled.ChevronRight else Icons.Filled.ChevronLeft
    }
    val shape = when (dockSide) {
        EdgeDockSide.RIGHT -> RoundedCornerShape(
            topStart = 18.dp,
            bottomStart = 18.dp,
            topEnd = if (isTucked) 4.dp else 18.dp,
            bottomEnd = if (isTucked) 4.dp else 18.dp
        )
        EdgeDockSide.LEFT -> RoundedCornerShape(
            topStart = if (isTucked) 4.dp else 18.dp,
            bottomStart = if (isTucked) 4.dp else 18.dp,
            topEnd = 18.dp,
            bottomEnd = 18.dp
        )
    }

    Surface(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onToggleTuck()
        },
        interactionSource = interaction,
        shape = shape,
        color = if (isTucked) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.94f)
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isActivePulse) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.72f)
            } else {
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            }
        ),
        shadowElevation = if (isTucked) 12.dp else 6.dp,
        modifier = modifier
            .width(if (isTucked) 42.dp else 30.dp)
            .height(if (isTucked) 60.dp else 54.dp)
            .expressivePressScale(interaction, pressedScale = 0.9f)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isTucked) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Icon(
                imageVector = chevron,
                contentDescription = contentDescription,
                tint = if (isTucked) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                },
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun FloatingMiniPlayer(
    playbackState: PlaybackUiState,
    settings: AppSettings,
    positionMsFlow: StateFlow<Long>,
    spectrumLevelsFlow: StateFlow<List<Float>>,
    strings: AppStrings,
    onExpand: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onSeekRelative: (Long) -> Unit,
    onToggleFavorite: (AudioTrack) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleAbLoop: () -> Unit,
    onCycleSpeed: () -> Unit,
    onToggleCompact: () -> Unit,
    onToggleFloating: () -> Unit,
    onOpenCustomize: () -> Unit,
    onOpenQuickEq: () -> Unit,
    onOpenQuickTimer: () -> Unit,
    onOpenQueue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val track = playbackState.currentTrack
    val haptics = LocalHapticFeedback.current
    var showCompanionControls by remember { mutableStateOf(false) }
    var isEdgeTucked by remember { mutableStateOf(false) }

    val compact = settings.miniPlayerCompact
    val dockSide = settings.miniPlayerEdgeDockSide

    AnimatedVisibility(
        visible = track != null && !settings.miniPlayerFloating,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
        ) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        if (track != null) {
            val context = LocalContext.current
            val durationMs = playbackState.durationMs.coerceAtLeast(1L)

            var artPalette by remember(track.id) { mutableStateOf<AlbumArtPalette?>(null) }
            LaunchedEffect(track.id, track.artworkResId, track.uriString, settings.albumArtTintEnabled) {
                artPalette = if (settings.albumArtTintEnabled) {
                    AlbumArtPaletteExtractor.extractForTrack(context, track)
                } else {
                    null
                }
            }
            val dynamicTint by animateColorAsState(
                targetValue = if (settings.albumArtTintEnabled) {
                    artPalette?.vibrant ?: MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                animationSpec = tween(durationMillis = 500),
                label = "miniPlayerArtTint"
            )
            val seekStepSec = settings.seekStepSeconds.coerceIn(5, 30)
            val seekStepMs = seekStepSec * 1000L

            val discSize by animateDpAsState(
                targetValue = if (compact) 38.dp else 52.dp,
                animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMedium),
                label = "miniDiscSize"
            )
            val verticalPad by animateDpAsState(
                targetValue = if (compact) 6.dp else 10.dp,
                animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
                label = "miniVertPad"
            )
            val glassCfg = LocalLiquidGlassConfig.current
            val cardCorner by animateDpAsState(
                targetValue = scaledCornerDp(if (compact) 28.dp else 26.dp, glassCfg.shapeRoundness),
                label = "miniCardCorner"
            )

            var swipeAccumulator by remember(track.id) { mutableFloatStateOf(0f) }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = when {
                    isEdgeTucked && dockSide == EdgeDockSide.RIGHT -> Alignment.CenterEnd
                    isEdgeTucked && dockSide == EdgeDockSide.LEFT -> Alignment.CenterStart
                    else -> Alignment.Center
                }
            ) {
                if (isEdgeTucked) {
                    // Exact same glued handle anchored on the screen edge; tap to slide mini-player back!
                    StickyEdgeTuckHandle(
                        isTucked = true,
                        dockSide = dockSide,
                        badgeIcon = Icons.Filled.MusicNote,
                        isActivePulse = playbackState.isPlaying,
                        contentDescription = strings.restoreFromEdgeAction,
                        testTag = "mini_player_edge_handle",
                        onToggleTuck = { isEdgeTucked = false }
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .widthIn(max = if (compact) 490.dp else 610.dp)
                            .fillMaxWidth()
                            .padding(
                                start = if (dockSide == EdgeDockSide.LEFT) 0.dp else if (compact) 18.dp else 10.dp,
                                end = if (dockSide == EdgeDockSide.RIGHT) 0.dp else if (compact) 18.dp else 10.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (dockSide == EdgeDockSide.LEFT) {
                            StickyEdgeTuckHandle(
                                isTucked = false,
                                dockSide = EdgeDockSide.LEFT,
                                badgeIcon = Icons.Filled.MusicNote,
                                isActivePulse = playbackState.isPlaying,
                                contentDescription = strings.tuckToEdgeAction,
                                testTag = "mini_player_edge_handle",
                                onToggleTuck = { isEdgeTucked = true }
                            )
                        }

                        MaterialLiquidGlassSurface(
                            shape = RoundedCornerShape(cardCorner),
                            shadowElevation = 12.dp,
                            tonalBoost = 1.1f,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(cardCorner))
                                .pointerInput(track.id) {
                                    detectHorizontalDragGestures(
                                        onDragEnd = {
                                            if (swipeAccumulator < -65f) {
                                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onNext()
                                            } else if (swipeAccumulator > 65f) {
                                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onPrevious()
                                            }
                                            swipeAccumulator = 0f
                                        },
                                        onDragCancel = { swipeAccumulator = 0f },
                                        onHorizontalDrag = { _, dragAmount ->
                                            swipeAccumulator += dragAmount
                                        }
                                    )
                                }
                                .testTag("mini_player_card")
                        ) {
                val tintModifier = if (settings.albumArtTintEnabled) {
                    Modifier.background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                dynamicTint.copy(alpha = 0.14f),
                                Color.Transparent,
                                dynamicTint.copy(alpha = 0.08f)
                            )
                        )
                    )
                } else {
                    Modifier
                }
                Column(modifier = tintModifier) {
                    // Main Song Row (Tap to expand full player; Swipe horizontally to skip)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onExpand)
                            .padding(horizontal = 10.dp, vertical = verticalPad),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Isolated draw-phase Spinning Vinyl Disc (0 recompositions/sec)
                        MiniSpinningDiscLeaf(
                            track = track,
                            isPlaying = playbackState.isPlaying,
                            discSize = discSize,
                            compact = compact
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = track.title,
                                    style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                if (!compact && playbackState.isPlaying && settings.barShowSpectrum) {
                                    MiniSpectrumLeaf(spectrumLevelsFlow = spectrumLevelsFlow)
                                } else if (!compact) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = track.extension,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            if (!compact) {
                                Text(
                                    text = track.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Compact / Shrink Size button right on the bar
                        IconButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onToggleCompact()
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("mini_player_toggle_compact")
                        ) {
                            Icon(
                                imageVector = if (compact) Icons.Rounded.OpenInFull else Icons.Rounded.CloseFullscreen,
                                contentDescription = if (compact) strings.expandBarAction else strings.compactBarAction,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Float / Detach button right on the bar
                        IconButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onToggleFloating()
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("mini_player_toggle_float")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PictureInPictureAlt,
                                contentDescription = strings.floatPlayerAction,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (!compact) {
                            // Toggle Companion Strip button
                            IconButton(
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    showCompanionControls = !showCompanionControls
                                },
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("mini_player_toggle_companion")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Tune,
                                    contentDescription = strings.companionTools,
                                    tint = if (showCompanionControls) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        // Authentic Material You Morphing Play/Pause Button (Circle <-> Squircle)
                        MorphingPlayPauseButton(
                            isPlaying = playbackState.isPlaying,
                            onClick = onTogglePlayPause,
                            size = if (compact) 40.dp else 46.dp,
                            iconSize = if (compact) 21.dp else 24.dp,
                            modifier = Modifier.testTag("mini_player_play_pause")
                        )

                        IconButton(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNext()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("mini_player_next")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SkipNext,
                                contentDescription = "Next track",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Customizable Companion Quick-Control Strip (unfolds smoothly on tap)
                    AnimatedVisibility(
                        visible = !compact && showCompanionControls,
                        enter = expandVertically(spring(dampingRatio = 0.85f)) + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        val abLabel = when {
                            playbackState.abLoopStartMs != null && playbackState.abLoopEndMs != null -> strings.abLoopActive
                            playbackState.abLoopStartMs != null -> strings.abLoopSetB
                            else -> strings.abLoopSetA
                        }
                        val abActive = playbackState.abLoopStartMs != null

                        Column {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Quick Customize Bar button
                                CompanionLabeledPill(
                                    icon = Icons.Filled.Tune,
                                    label = strings.customizeBarTitle,
                                    active = true,
                                    onClick = onOpenCustomize,
                                    tag = "companion_customize_bar"
                                )
                                CompanionIconPill(
                                    icon = Icons.Filled.SkipPrevious,
                                    contentDescription = "Previous",
                                    active = false,
                                    onClick = onPrevious,
                                    tag = "companion_prev"
                                )
                                if (settings.barShowSeek10) {
                                    CompanionIconPill(
                                        icon = Icons.Filled.Replay10,
                                        contentDescription = "-${seekStepSec}s",
                                        active = false,
                                        onClick = { onSeekRelative(-seekStepMs) },
                                        tag = "companion_rewind_10"
                                    )
                                    CompanionIconPill(
                                        icon = Icons.Filled.Forward10,
                                        contentDescription = "+${seekStepSec}s",
                                        active = false,
                                        onClick = { onSeekRelative(seekStepMs) },
                                        tag = "companion_forward_10"
                                    )
                                }
                                if (settings.barShowFavorite) {
                                    CompanionIconPill(
                                        icon = if (track.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = strings.menuAddFavorite,
                                        active = track.isFavorite,
                                        onClick = { onToggleFavorite(track) },
                                        tag = "companion_favorite"
                                    )
                                }
                                CompanionIconPill(
                                    icon = Icons.Filled.Shuffle,
                                    contentDescription = strings.shuffleLabel,
                                    active = playbackState.shuffleEnabled,
                                    onClick = onToggleShuffle,
                                    tag = "companion_shuffle"
                                )
                                CompanionIconPill(
                                    icon = if (playbackState.repeatMode == TrackRepeatMode.ONE) {
                                        Icons.Filled.RepeatOne
                                    } else {
                                        Icons.Filled.Repeat
                                    },
                                    contentDescription = when (playbackState.repeatMode) {
                                        TrackRepeatMode.OFF -> strings.repeatOff
                                        TrackRepeatMode.ALL -> strings.repeatAll
                                        TrackRepeatMode.ONE -> strings.repeatOne
                                    },
                                    active = playbackState.repeatMode != TrackRepeatMode.OFF,
                                    onClick = onCycleRepeat,
                                    tag = "companion_repeat"
                                )
                                if (settings.barShowAbLoop) {
                                    CompanionLabeledPill(
                                        icon = Icons.Filled.Loop,
                                        label = abLabel,
                                        active = abActive,
                                        onClick = onToggleAbLoop,
                                        tag = "companion_ab_loop"
                                    )
                                }
                                if (settings.barShowSpeed) {
                                    CompanionLabeledPill(
                                        icon = Icons.Filled.Speed,
                                        label = "${playbackState.playbackSpeed}x",
                                        active = playbackState.playbackSpeed != 1.0f,
                                        onClick = onCycleSpeed,
                                        tag = "companion_speed"
                                    )
                                }
                                if (settings.barShowEq) {
                                    CompanionLabeledPill(
                                        icon = Icons.Filled.Equalizer,
                                        label = strings.paneEqualizer,
                                        active = false,
                                        onClick = onOpenQuickEq,
                                        tag = "companion_eq"
                                    )
                                }
                                if (settings.barShowTimer) {
                                    CompanionLabeledPill(
                                        icon = Icons.Filled.Timer,
                                        label = if (playbackState.sleepTimerRemainingSec > 0) {
                                            formatSecondsClock(playbackState.sleepTimerRemainingSec)
                                        } else {
                                            strings.sleepTimerTitle
                                        },
                                        active = playbackState.sleepTimerRemainingSec > 0,
                                        onClick = onOpenQuickTimer,
                                        tag = "companion_timer"
                                    )
                                }
                                if (settings.barShowQueue) {
                                    CompanionIconPill(
                                        icon = Icons.AutoMirrored.Filled.QueueMusic,
                                        contentDescription = strings.upNextQueue,
                                        active = false,
                                        onClick = onOpenQueue,
                                        tag = "companion_queue"
                                    )
                                }
                            }
                        }
                    }

                    // Isolated Material You Squiggly / Flat Scrubbable Progress Bar Leaf
                    MiniScrubBarLeaf(
                        positionMsFlow = positionMsFlow,
                        durationMs = durationMs,
                        isPlaying = playbackState.isPlaying,
                        squigglyEnabled = settings.squigglyProgress,
                        compact = compact,
                        onSeek = onSeek
                    )
                }
                        }

                        if (dockSide == EdgeDockSide.RIGHT) {
                            StickyEdgeTuckHandle(
                                isTucked = false,
                                dockSide = EdgeDockSide.RIGHT,
                                badgeIcon = Icons.Filled.MusicNote,
                                isActivePulse = playbackState.isPlaying,
                                contentDescription = strings.tuckToEdgeAction,
                                testTag = "mini_player_edge_handle",
                                onToggleTuck = { isEdgeTucked = true }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Draggable Floating Material You Audio Bubble / Mini-Card!
 * Floats freely anywhere over the app with Shrink/Expand size, Re-Dock, Play/Pause, Next,
 * and Google Drop-Through Drag-to-X dismiss to stop playback completely.
 */
@Composable
fun FloatingAudioBubbleWindow(
    playbackState: PlaybackUiState,
    settings: AppSettings,
    positionMsFlow: StateFlow<Long>,
    strings: AppStrings,
    containerHeightPx: Float,
    onExpandFull: () -> Unit,
    onDockBack: () -> Unit,
    onToggleCompact: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Long) -> Unit,
    onDragStateChanged: (isDragging: Boolean, isOverDismissZone: Boolean) -> Unit,
    onDropToStop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val track = playbackState.currentTrack ?: return
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isEdgeTucked by remember { mutableStateOf(false) }
    val dockSide = settings.miniPlayerEdgeDockSide

    val compact = settings.miniPlayerCompact
    val cardWidth by animateDpAsState(
        targetValue = if (compact) 168.dp else 256.dp,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMedium),
        label = "floatingAudioWidth"
    )

    // Threshold in Y pixels near the bottom of screen where the Google X Drop Zone lives
    val dismissThresholdY = with(density) {
        (containerHeightPx * 0.56f).coerceAtLeast(240.dp.toPx())
    }

    if (isEdgeTucked) {
        Box(
            modifier = modifier
                .absoluteOffset { IntOffset(0, offsetY.roundToInt()) }
        ) {
            StickyEdgeTuckHandle(
                isTucked = true,
                dockSide = dockSide,
                badgeIcon = Icons.Filled.MusicNote,
                isActivePulse = playbackState.isPlaying,
                contentDescription = strings.restoreFromEdgeAction,
                testTag = "floating_audio_edge_handle",
                onToggleTuck = { isEdgeTucked = false }
            )
        }
        return
    }

    val bubbleCorner = scaledCornerDp(28.dp, LocalLiquidGlassConfig.current.shapeRoundness)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .absoluteOffset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(containerHeightPx) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        onDragStateChanged(true, offsetY > dismissThresholdY)
                    },
                    onDragEnd = {
                        val droppedInX = offsetY > dismissThresholdY
                        isDragging = false
                        onDragStateChanged(false, false)
                        if (droppedInX) {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            offsetX = 0f
                            offsetY = 0f
                            onDropToStop()
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                        onDragStateChanged(false, false)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                        onDragStateChanged(true, offsetY > dismissThresholdY)
                    }
                )
            }
    ) {
        if (dockSide == EdgeDockSide.LEFT) {
            StickyEdgeTuckHandle(
                isTucked = false,
                dockSide = EdgeDockSide.LEFT,
                badgeIcon = Icons.Filled.MusicNote,
                isActivePulse = playbackState.isPlaying,
                contentDescription = strings.tuckToEdgeAction,
                testTag = "floating_audio_edge_handle",
                onToggleTuck = { isEdgeTucked = true }
            )
        }

        MaterialLiquidGlassSurface(
            shape = RoundedCornerShape(bubbleCorner),
            shadowElevation = if (isDragging) 20.dp else 14.dp,
            tonalBoost = if (isDragging) 1.35f else 1.15f,
            modifier = Modifier
                .width(cardWidth)
                .testTag("floating_audio_bubble")
        ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpandFull)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 34.dp else 40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    TrackArtworkImage(track = track, modifier = Modifier.fillMaxSize())
                }

                if (!compact) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = track.artist,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                MorphingPlayPauseButton(
                    isPlaying = playbackState.isPlaying,
                    onClick = onTogglePlayPause,
                    size = 36.dp,
                    iconSize = 18.dp,
                    modifier = Modifier.testTag("floating_audio_play_pause")
                )

                if (!compact) {
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next",
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            // Mini Floating Controls Row: Shrink/Expand Size + Re-Dock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggleCompact()
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.testTag("floating_audio_resize")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = if (compact) Icons.Filled.OpenInFull else Icons.Filled.CloseFullscreen,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (compact) strings.expandBarAction else strings.compactBarAction,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Surface(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDockBack()
                    },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.testTag("floating_audio_dock")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VerticalAlignBottom,
                            contentDescription = strings.dockPlayerAction,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = strings.dockPlayerAction,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            MiniScrubBarLeaf(
                positionMsFlow = positionMsFlow,
                durationMs = playbackState.durationMs.coerceAtLeast(1L),
                isPlaying = playbackState.isPlaying,
                squigglyEnabled = settings.squigglyProgress,
                compact = true,
                onSeek = onSeek
            )
        }
        }

        if (dockSide == EdgeDockSide.RIGHT) {
            StickyEdgeTuckHandle(
                isTucked = false,
                dockSide = EdgeDockSide.RIGHT,
                badgeIcon = Icons.Filled.MusicNote,
                isActivePulse = playbackState.isPlaying,
                contentDescription = strings.tuckToEdgeAction,
                testTag = "floating_audio_edge_handle",
                onToggleTuck = { isEdgeTucked = true }
            )
        }
    }
}

@Composable
private fun MiniSpectrumLeaf(spectrumLevelsFlow: StateFlow<List<Float>>) {
    val levels by spectrumLevelsFlow.collectAsStateWithLifecycle()
    LiveAudioSpectrumBars(
        levels = levels.take(5),
        isPlaying = true,
        barCount = 5,
        modifier = Modifier
            .width(24.dp)
            .height(14.dp)
    )
}

@Composable
private fun MiniScrubBarLeaf(
    positionMsFlow: StateFlow<Long>,
    durationMs: Long,
    isPlaying: Boolean,
    squigglyEnabled: Boolean,
    compact: Boolean,
    onSeek: (Long) -> Unit,
) {
    val posMs by positionMsFlow.collectAsStateWithLifecycle()
    val safeDur = durationMs.coerceAtLeast(1L)
    val progress = (posMs.toFloat() / safeDur.toFloat()).coerceIn(0f, 1f)

    SquigglyAudioProgressBar(
        progress = progress,
        isPlaying = isPlaying,
        squigglyEnabled = squigglyEnabled,
        onSeekFraction = { frac -> onSeek((frac * safeDur).toLong()) },
        showThumb = !compact,
        strokeWidth = if (compact) 2.5.dp else 3.dp,
        waveAmplitudeDp = if (compact) 2.dp else 2.6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (compact) 12.dp else 14.dp)
            .padding(bottom = if (compact) 4.dp else 6.dp)
            .height(if (compact) 10.dp else 15.dp)
            .testTag("mini_player_scrub_bar")
    )
}

@Composable
private fun CompanionIconPill(
    icon: ImageVector,
    contentDescription: String,
    active: Boolean,
    onClick: () -> Unit,
    tag: String,
) {
    val haptics = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    Surface(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interaction,
        shape = CircleShape,
        color = if (active) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .size(36.dp)
            .expressivePressScale(interaction, pressedScale = 0.88f)
            .testTag(tag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (active) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun CompanionLabeledPill(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    tag: String,
) {
    val haptics = LocalHapticFeedback.current
    val interaction = remember { MutableInteractionSource() }
    Surface(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        interactionSource = interaction,
        shape = CircleShape,
        color = if (active) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .height(36.dp)
            .expressivePressScale(interaction, pressedScale = 0.92f)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (active) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (active) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
fun TrackArtworkImage(
    track: AudioTrack,
    modifier: Modifier = Modifier,
) {
    when {
        track.artworkResId != null -> {
            Image(
                painter = painterResource(id = track.artworkResId),
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
        track.artworkUriString != null -> {
            AsyncImage(
                model = track.artworkUriString,
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }
        else -> {
            Box(
                modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = track.title,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun MiniSpinningDiscLeaf(
    track: AudioTrack,
    isPlaying: Boolean,
    discSize: androidx.compose.ui.unit.Dp,
    compact: Boolean,
) {
    val angleState = if (isPlaying) {
        val infinite = rememberInfiniteTransition(label = "miniDiscSpin")
        infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 8000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "miniDiscAngle"
        )
    } else {
        null
    }
    Box(
        modifier = Modifier
            .size(discSize)
            .graphicsLayer {
                rotationZ = angleState?.value ?: 0f
            }
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        TrackArtworkImage(
            track = track,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .size(if (compact) 8.dp else 11.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        )
    }
}

