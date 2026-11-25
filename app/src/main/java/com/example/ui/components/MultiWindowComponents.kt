package com.example.ui.components

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.data.MediaScanner
import com.example.localization.AppStrings
import com.example.model.AudioTrack
import com.example.model.EdgeDockSide
import com.example.model.PhotoItem
import com.example.model.PlaybackUiState
import com.example.model.QueueSortOption
import com.example.model.SecondaryPaneMode
import com.example.model.VideoItem
import kotlin.math.roundToInt

/**
 * In-App Split-Screen Secondary Pane Header Bar.
 * Allows switching what runs inside the second window (Music, Videos, Photos, or Live Pro EQ).
 */
@Composable
fun MultiWindowSecondaryHeader(
    selectedMode: SecondaryPaneMode,
    strings: AppStrings,
    onSelectMode: (SecondaryPaneMode) -> Unit,
    onCloseMultiWindow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("multi_window_header")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Splitscreen,
                contentDescription = strings.multiWindowTitle,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SecondaryPaneMode.entries.forEach { mode ->
                    val label = when (mode) {
                        SecondaryPaneMode.MUSIC -> strings.tabMusic
                        SecondaryPaneMode.VIDEOS -> strings.tabVideos
                        SecondaryPaneMode.PHOTOS -> strings.tabPhotos
                        SecondaryPaneMode.EQUALIZER -> strings.paneEqualizer
                    }
                    val icon = when (mode) {
                        SecondaryPaneMode.MUSIC -> Icons.Filled.MusicNote
                        SecondaryPaneMode.VIDEOS -> Icons.Filled.VideoLibrary
                        SecondaryPaneMode.PHOTOS -> Icons.Filled.PhotoLibrary
                        SecondaryPaneMode.EQUALIZER -> Icons.Filled.Equalizer
                    }
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { onSelectMode(mode) },
                        leadingIcon = {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp))
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        },
                        shape = CircleShape,
                        modifier = Modifier.testTag("multi_pane_${mode.name.lowercase()}")
                    )
                }
            }

            IconButton(
                onClick = onCloseMultiWindow,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("multi_window_close")
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = strings.close,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Draggable Floating In-App Video Mini-Window.
 * Lets the user browse Photos, Music, or Settings while keeping their video playing inside the app.
 */
@Composable
fun FloatingInAppVideoWindow(
    video: VideoItem,
    initialPositionMs: Long,
    strings: AppStrings,
    onExpand: (Long) -> Unit,
    onClose: () -> Unit,
    onPlayingStateChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    dockSide: EdgeDockSide = EdgeDockSide.RIGHT,
    containerHeightPx: Float = 1600f,
    isInPipMode: Boolean = false,
    onDragStateChanged: (isDragging: Boolean, isOverDismissZone: Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isPlaying by remember { mutableStateOf(true) }
    var isEnded by remember { mutableStateOf(false) }
    var isEdgeTucked by remember { mutableStateOf(false) }
    var sizeStep by remember { mutableIntStateOf(1) } // 0=Small, 1=Medium, 2=Large
    var isMuted by remember { mutableStateOf(false) }

    val winWidthDp = when (sizeStep) {
        0 -> 196.dp
        2 -> 276.dp
        else -> 232.dp
    }
    val winHeightDp = when (sizeStep) {
        0 -> 122.dp
        2 -> 168.dp
        else -> 142.dp
    }

    val dismissThresholdY = with(density) {
        (containerHeightPx * 0.56f).coerceAtLeast(240.dp.toPx())
    }

    val exoPlayer = remember(video.id) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(video.uriString)))
            prepare()
            if (initialPositionMs > 0L) seekTo(initialPositionMs)
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (playing) isEnded = false
                onPlayingStateChanged(playing)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    isEnded = true
                    isPlaying = false
                    onPlayingStateChanged(false)
                } else if (playbackState == Player.STATE_READY) {
                    isEnded = false
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Keep Row & ExoPlayer alive even when tucked or in Android system PiP (zero codec teardown!)
    val effectiveTucked = isEdgeTucked && !isInPipMode
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = (if (isInPipMode) Modifier.fillMaxSize() else modifier)
            .then(
                if (isInPipMode) {
                    Modifier
                } else {
                    Modifier.absoluteOffset {
                        IntOffset(
                            x = if (effectiveTucked) 0 else offsetX.roundToInt(),
                            y = offsetY.roundToInt()
                        )
                    }
                }
            )
            .then(
                if (!effectiveTucked && !isInPipMode) {
                    Modifier.pointerInput(containerHeightPx) {
                        detectDragGestures(
                            onDragStart = {
                                onDragStateChanged(true, offsetY > dismissThresholdY)
                            },
                            onDragEnd = {
                                val droppedInX = offsetY > dismissThresholdY
                                onDragStateChanged(false, false)
                                if (droppedInX) {
                                    haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                    exoPlayer.stop()
                                    onClose()
                                }
                            },
                            onDragCancel = {
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
                } else {
                    Modifier
                }
            )
    ) {
        if (!isInPipMode && dockSide == EdgeDockSide.LEFT) {
            StickyEdgeTuckHandle(
                isTucked = isEdgeTucked,
                dockSide = EdgeDockSide.LEFT,
                badgeIcon = Icons.Filled.Videocam,
                isActivePulse = isPlaying,
                contentDescription = if (isEdgeTucked) strings.restoreFromEdgeAction else strings.tuckToEdgeAction,
                testTag = "floating_video_edge_handle",
                onToggleTuck = { isEdgeTucked = !isEdgeTucked }
            )
        }

        Surface(
            shape = if (isInPipMode) androidx.compose.ui.graphics.RectangleShape else RoundedCornerShape(22.dp),
            color = Color.Black,
            shadowElevation = if (effectiveTucked || isInPipMode) 0.dp else 16.dp,
            tonalElevation = if (effectiveTucked || isInPipMode) 0.dp else 10.dp,
            border = if (effectiveTucked || isInPipMode) null else BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)),
            modifier = (if (isInPipMode) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .width(if (effectiveTucked) 1.dp else winWidthDp)
                    .height(if (effectiveTucked) 1.dp else winHeightDp)
                    .alpha(if (effectiveTucked) 0f else 1f)
            }).testTag("floating_in_app_video_window")
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (!effectiveTucked && !isInPipMode) {
                    // Subtle top overlay controls (Title + Resize + Expand + Close)
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                                )
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 6.dp)
                        )
                        IconButton(
                            onClick = { sizeStep = (sizeStep + 1) % 3 },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("floating_video_resize")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AspectRatio,
                                contentDescription = strings.resizeWindowAction,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        IconButton(
                            onClick = { onExpand(exoPlayer.currentPosition.coerceAtLeast(0L)) },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("floating_video_expand")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.OpenInFull,
                                contentDescription = strings.expandVideo,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("floating_video_close")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = strings.close,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Bottom transport bar: Mute + -10s + +10s + Play/Pause
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                                )
                            )
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    isMuted = !isMuted
                                    exoPlayer.volume = if (isMuted) 0f else 1f
                                },
                                modifier = Modifier
                                    .size(30.dp)
                                    .testTag("floating_video_mute")
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = if (isMuted) strings.unmuteAudioAction else strings.muteAudioAction,
                                    tint = if (isMuted) MaterialTheme.colorScheme.error else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    exoPlayer.seekTo((exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L))
                                },
                                modifier = Modifier
                                    .size(30.dp)
                                    .testTag("floating_video_seek_back")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Replay10,
                                    contentDescription = "-10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    val dur = exoPlayer.duration.takeIf { it > 0L } ?: Long.MAX_VALUE
                                    exoPlayer.seekTo((exoPlayer.currentPosition + 10_000L).coerceAtMost(dur))
                                },
                                modifier = Modifier
                                    .size(30.dp)
                                    .testTag("floating_video_seek_fwd")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Forward10,
                                    contentDescription = "+10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }

                        Surface(
                            onClick = {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                } else {
                                    val dur = exoPlayer.duration
                                    val atEnd = exoPlayer.playbackState == Player.STATE_ENDED ||
                                        (dur > 0L && exoPlayer.currentPosition >= dur - 350L)
                                    if (atEnd) {
                                        exoPlayer.seekTo(0L)
                                        isEnded = false
                                    }
                                    if (exoPlayer.playbackState == Player.STATE_IDLE) {
                                        exoPlayer.prepare()
                                    }
                                    exoPlayer.playWhenReady = true
                                    exoPlayer.play()
                                }
                            },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("floating_video_play_pause")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when {
                                        isPlaying -> Icons.Filled.Pause
                                        isEnded -> Icons.Filled.Replay
                                        else -> Icons.Filled.PlayArrow
                                    },
                                    contentDescription = when {
                                        isPlaying -> "Pause"
                                        isEnded -> "Replay"
                                        else -> "Play"
                                    },
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (!isInPipMode && dockSide == EdgeDockSide.RIGHT) {
            StickyEdgeTuckHandle(
                isTucked = isEdgeTucked,
                dockSide = EdgeDockSide.RIGHT,
                badgeIcon = Icons.Filled.Videocam,
                isActivePulse = isPlaying,
                contentDescription = if (isEdgeTucked) strings.restoreFromEdgeAction else strings.tuckToEdgeAction,
                testTag = "floating_video_edge_handle",
                onToggleTuck = { isEdgeTucked = !isEdgeTucked }
            )
        }
    }
}

/**
 * Draggable Floating In-App Photo Mini-Window with attached Sticky Edge-Tuck Handle.
 * Lets the user pin any photo while browsing Music, Videos, or Settings, and tuck/restore it
 * from the screen edge with the exact same glued handle button.
 */
@Composable
fun FloatingInAppPhotoWindow(
    photo: PhotoItem,
    strings: AppStrings,
    onExpand: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    dockSide: EdgeDockSide = EdgeDockSide.RIGHT,
    containerHeightPx: Float = 1600f,
    onDragStateChanged: (isDragging: Boolean, isOverDismissZone: Boolean) -> Unit = { _, _ -> },
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isEdgeTucked by remember { mutableStateOf(false) }
    var isLargeSize by remember { mutableStateOf(false) }
    val photoSizeDp = if (isLargeSize) 216.dp else 160.dp

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
                badgeIcon = Icons.Filled.Photo,
                isActivePulse = true,
                contentDescription = strings.restoreFromEdgeAction,
                testTag = "floating_photo_edge_handle",
                onToggleTuck = { isEdgeTucked = false }
            )
        }
        return
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .absoluteOffset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(containerHeightPx) {
                detectDragGestures(
                    onDragStart = {
                        onDragStateChanged(true, offsetY > dismissThresholdY)
                    },
                    onDragEnd = {
                        val droppedInX = offsetY > dismissThresholdY
                        onDragStateChanged(false, false)
                        if (droppedInX) {
                            haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            onClose()
                        }
                    },
                    onDragCancel = {
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
                badgeIcon = Icons.Filled.Photo,
                isActivePulse = true,
                contentDescription = strings.tuckToEdgeAction,
                testTag = "floating_photo_edge_handle",
                onToggleTuck = { isEdgeTucked = true }
            )
        }

        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.Black,
            shadowElevation = 16.dp,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.78f)),
            modifier = Modifier
                .width(photoSizeDp)
                .height(photoSizeDp)
                .testTag("floating_in_app_photo_window")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onExpand)
            ) {
                if (photo.resId != null) {
                    Image(
                        painter = painterResource(id = photo.resId),
                        contentDescription = photo.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = photo.uriString,
                        contentDescription = photo.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.68f), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = photo.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    )
                    IconButton(
                        onClick = { isLargeSize = !isLargeSize },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("floating_photo_resize")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AspectRatio,
                            contentDescription = strings.resizeWindowAction,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(
                        onClick = onExpand,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("floating_photo_expand")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.OpenInFull,
                            contentDescription = strings.expandBarAction,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("floating_photo_close")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = strings.close,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        if (dockSide == EdgeDockSide.RIGHT) {
            StickyEdgeTuckHandle(
                isTucked = false,
                dockSide = EdgeDockSide.RIGHT,
                badgeIcon = Icons.Filled.Photo,
                isActivePulse = true,
                contentDescription = strings.tuckToEdgeAction,
                testTag = "floating_photo_edge_handle",
                onToggleTuck = { isEdgeTucked = true }
            )
        }
    }
}

/**
 * Clean, full-bleed Android PiP window UI when exiting the app while Music is playing.
 * System PiP RemoteAction controls (Prev / Play-Pause / Next) appear directly over this window.
 */
@Composable
fun PipAudioModeView(
    playbackState: PlaybackUiState,
    spectrumLevelsFlow: StateFlow<List<Float>>,
    strings: AppStrings,
) {
    val track = playbackState.currentTrack
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        MaterialTheme.colorScheme.secondaryContainer,
                    )
                )
            )
            .padding(12.dp)
    ) {
        if (track != null) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    TrackArtworkImage(track = track, modifier = Modifier.fillMaxSize())
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = strings.pipAudioBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    PipMiniSpectrumLeaf(
                        spectrumLevelsFlow = spectrumLevelsFlow,
                        isPlaying = playbackState.isPlaying,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.GraphicEq,
                    contentDescription = strings.appTitle,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
private fun PipMiniSpectrumLeaf(
    spectrumLevelsFlow: StateFlow<List<Float>>,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val spectrumLevels by spectrumLevelsFlow.collectAsStateWithLifecycle()
    LiveAudioSpectrumBars(
        levels = spectrumLevels.take(8),
        isPlaying = isPlaying,
        barCount = 8,
        modifier = modifier
    )
}

/**
 * Dedicated Android Picture-in-Picture view for Photos mode when the user enables optional Photo PiP.
 */
@Composable
fun PipPhotoModeView(
    photo: PhotoItem,
    strings: AppStrings,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (photo.resId != null) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = photo.resId),
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            coil.compose.AsyncImage(
                model = photo.uriString,
                contentDescription = photo.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.58f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Text(
                text = photo.title.ifBlank { strings.pipPhotoBadge },
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * Reusable Interactive Up Next Queue Bottom Sheet Content with live sorting & Up/Down reordering.
 */
@Composable
fun UpNextQueueSheetContent(
    tracks: List<AudioTrack>,
    currentTrackId: String?,
    strings: AppStrings,
    onSelectTrack: (AudioTrack) -> Unit,
    onMoveUp: (String) -> Unit = {},
    onMoveDown: (String) -> Unit = {},
    onRemoveFromQueue: (String) -> Unit = {},
    onSortQueue: (QueueSortOption) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${strings.upNextQueue} (${tracks.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (tracks.size > 1) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    QueueSortOption.NAME to strings.queueSortName,
                    QueueSortOption.ARTIST to strings.queueSortArtist,
                    QueueSortOption.DURATION to strings.queueSortDuration,
                    QueueSortOption.REVERSE to strings.queueSortReverse,
                    QueueSortOption.SHUFFLE to strings.queueSortShuffle,
                ).forEach { (opt, label) ->
                    Surface(
                        onClick = { onSortQueue(opt) },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.testTag("queue_sort_${opt.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = if (opt == QueueSortOption.SHUFFLE) Icons.Filled.Shuffle else Icons.Filled.SwapVert,
                                contentDescription = label,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (tracks.isEmpty()) {
            Text(
                text = strings.queueEmpty,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tracks.size, key = { idx -> tracks[idx].id }) { index ->
                    val item = tracks[index]
                    val isCurrent = item.id == currentTrackId
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isCurrent) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onSelectTrack(item) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                TrackArtworkImage(track = item, modifier = Modifier.fillMaxSize())
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${item.artist} • ${MediaScanner.formatDuration(item.durationMs)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (isCurrent) {
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // Move Up / Down / Remove from Active Queue
                            IconButton(
                                onClick = { onMoveUp(item.id) },
                                enabled = index > 0,
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("queue_up_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowUp,
                                    contentDescription = strings.moveUpAction,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { onMoveDown(item.id) },
                                enabled = index < tracks.lastIndex,
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("queue_down_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = strings.moveDownAction,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (tracks.size > 1 && !isCurrent) {
                                IconButton(
                                    onClick = { onRemoveFromQueue(item.id) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("queue_remove_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = strings.removeFromQueueAction,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
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
 * Direct HTTPS Media Stream Dialog — unlocks online audio/video links (.mp3, .mp4, .m3u8, radio).
 */
@Composable
fun StreamUrlDialog(
    strings: AppStrings,
    onDismiss: () -> Unit,
    onPlayStream: (url: String, title: String, isVideo: Boolean) -> Boolean,
) {
    var urlInput by remember { mutableStateOf("") }
    var titleInput by remember { mutableStateOf("") }
    var isVideo by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        title = {
            Text(
                text = strings.streamUrlDialogTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isVideo,
                        onClick = { isVideo = false },
                        leadingIcon = {
                            Icon(Icons.Filled.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        label = { Text(strings.streamTypeAudio, fontWeight = FontWeight.SemiBold) },
                        shape = CircleShape,
                        modifier = Modifier.testTag("stream_type_audio")
                    )
                    FilterChip(
                        selected = isVideo,
                        onClick = { isVideo = true },
                        leadingIcon = {
                            Icon(Icons.Filled.VideoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        label = { Text(strings.streamTypeVideo, fontWeight = FontWeight.SemiBold) },
                        shape = CircleShape,
                        modifier = Modifier.testTag("stream_type_video")
                    )
                }

                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text(strings.streamUrlHint) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stream_url_input")
                )

                OutlinedTextField(
                    value = titleInput,
                    onValueChange = { titleInput = it },
                    label = { Text(strings.streamTitleHint) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stream_title_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (onPlayStream(urlInput, titleInput, isVideo)) {
                        onDismiss()
                    }
                },
                shape = CircleShape,
                modifier = Modifier.testTag("stream_play_confirm")
            ) {
                Text(strings.playStreamAction, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}
