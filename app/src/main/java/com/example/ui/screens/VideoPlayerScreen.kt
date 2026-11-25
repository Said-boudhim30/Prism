package com.example.ui.screens

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.Rational
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.MediaScanner
import com.example.localization.AppStrings
import com.example.model.VideoItem
import com.example.player.DeviceMediaDriverEngine
import com.example.player.SubtitleCue
import com.example.player.SubtitleParser
import com.example.player.VideoAudioTrackOption
import com.example.player.VideoStudioUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private val SPEED_OPTIONS = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
private val VIDEO_TIMER_OPTIONS_MIN = listOf(0, 15, 30, 60)

private enum class GestureHudType { BRIGHTNESS, VOLUME, SEEK_BACK, SEEK_FWD, SCRUB }

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    video: VideoItem,
    isInPipMode: Boolean,
    strings: AppStrings,
    onClose: () -> Unit,
    initialPositionMs: Long = 0L,
    defaultAspectIndex: Int = 0,
    defaultSpeed: Float = 1.0f,
    defaultLoop: Boolean = false,
    preferHardwareDecoding: Boolean = true,
    seekStepSeconds: Int = 10,
    gesturesEnabled: Boolean = true,
    doubleTapSeekEnabled: Boolean = true,
    defaultSubtitleSizeSp: Int = 18,
    subtitleColor: com.example.model.SubtitleColorStyle = com.example.model.SubtitleColorStyle.WHITE,
    subtitleBgOpacity: Float = 0.72f,
    subtitleBottomOffsetDp: Int = 24,
    defaultMirrorEnabled: Boolean = false,
    autoHideSeconds: Int = 4,
    onToggleMirror: (Boolean) -> Unit = {},
    videoBookmarks: List<com.example.model.MediaBookmark> = emptyList(),
    onAddBookmark: (Long, String) -> Unit = { _, _ -> },
    onRemoveBookmark: (String) -> Unit = {},
    hasPrevNext: Boolean = false,
    onPreviousVideo: () -> Unit = {},
    onNextVideo: () -> Unit = {},
    onPlaybackActiveChanged: (Boolean) -> Unit = {},
    onMinimizeToFloating: (Long) -> Unit = {},
    onPlayAsAudio: ((Long) -> Unit)? = null,
    onRequestSystemPip: (() -> Unit)? = null,
    onRegisterPipToggle: ((() -> Unit)?) -> Unit = {},
    autoNextEnabled: Boolean = false,
    hasNote: Boolean = false,
    onOpenNote: () -> Unit = {},
) {
    BackHandler(onBack = onClose)

    val clampedSeekSec = seekStepSeconds.coerceIn(5, 30)
    val seekStepMs = clampedSeekSec * 1000L
    val context = LocalContext.current
    val activity = context as? Activity
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1) }
    val driverProfile = remember(context) { DeviceMediaDriverEngine.probe(context) }
    var useHardwareDecoder by remember(video.id, preferHardwareDecoding) {
        mutableStateOf(preferHardwareDecoding && driverProfile.hardwareVideoAvailable)
    }

    val exoPlayer = remember(video.id, useHardwareDecoder) {
        val renderersFactory = DeviceMediaDriverEngine.buildOptimizedVideoRenderersFactory(
            context = context,
            profile = driverProfile,
            preferHardwareDecoding = useHardwareDecoder
        )
        val trackSelector = DeviceMediaDriverEngine.buildOptimizedVideoTrackSelector(
            context = context,
            profile = driverProfile
        )
        ExoPlayer.Builder(context, renderersFactory)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(video.uriString)))
                playbackParameters = PlaybackParameters(defaultSpeed.coerceIn(0.5f, 2.5f))
                repeatMode = if (defaultLoop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                prepare()
                if (initialPositionMs > 0L) seekTo(initialPositionMs)
                playWhenReady = true
            }
    }

    var isPlaying by remember(video.id) { mutableStateOf(true) }
    var isEnded by remember(video.id) { mutableStateOf(false) }
    var positionMs by remember(video.id) { mutableLongStateOf(initialPositionMs) }
    var durationMs by remember(video.id) { mutableLongStateOf(video.durationMs.coerceAtLeast(1L)) }
    var currentSpeed by remember(video.id) { mutableFloatStateOf(defaultSpeed.coerceIn(0.5f, 2.5f)) }
    var isHoldSpeedBoost by remember(video.id) { mutableStateOf(false) }
    var controlsVisible by remember { mutableStateOf(true) }
    var isScreenLocked by remember { mutableStateOf(false) }
    var isLoopEnabled by remember(video.id) { mutableStateOf(defaultLoop) }
    var isMuted by remember(video.id) { mutableStateOf(false) }
    var isMirrored by remember(video.id, defaultMirrorEnabled) { mutableStateOf(defaultMirrorEnabled) }
    var isNightDimmer by remember { mutableStateOf(false) }
    var isLandscapeForced by remember { mutableStateOf(false) }
    var resizeModeIndex by remember { mutableIntStateOf(defaultAspectIndex.coerceIn(0, 2)) }
    var zoomScale by remember(video.id) { mutableFloatStateOf(1f) }
    var colorCalibration by remember(video.id) { mutableStateOf(com.example.model.VideoColorCalibration()) }
    var showColorCalibDialog by remember { mutableStateOf(false) }
    var showChaptersDialog by remember { mutableStateOf(false) }
    var isScrubbingTimeline by remember { mutableStateOf(false) }
    var scrubPreviewPosMs by remember { mutableLongStateOf(0L) }
    var scrubPreviewBitmap by remember(video.id) { mutableStateOf<android.graphics.Bitmap?>(null) }

    // Respect user's Controls Auto-Hide Timer (0 = Never auto-hide)
    LaunchedEffect(controlsVisible, isPlaying, isScrubbingTimeline, autoHideSeconds, isScreenLocked) {
        if (controlsVisible && isPlaying && !isScrubbingTimeline && !isScreenLocked && autoHideSeconds > 0) {
            delay(autoHideSeconds.coerceIn(2, 10) * 1000L)
            controlsVisible = false
        }
    }

    LaunchedEffect(isScrubbingTimeline, scrubPreviewPosMs, video.uriString) {
        if (!isScrubbingTimeline) {
            scrubPreviewBitmap = null
            return@LaunchedEffect
        }
        delay(90L)
        val bmp = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val retriever = android.media.MediaMetadataRetriever()
            try {
                val uri = Uri.parse(video.uriString)
                if (uri.scheme == "http" || uri.scheme == "https") {
                    retriever.setDataSource(video.uriString, HashMap())
                } else {
                    retriever.setDataSource(context, uri)
                }
                val raw = retriever.getFrameAtTime(scrubPreviewPosMs * 1000L, android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                if (raw != null) {
                    android.graphics.Bitmap.createScaledBitmap(raw, 280, 158, true).also {
                        if (it !== raw) raw.recycle()
                    }
                } else null
            } catch (_: Throwable) {
                null
            } finally {
                runCatching { retriever.release() }
            }
        }
        scrubPreviewBitmap = bmp
    }

    // A-B Segment Loop state
    var abStartMs by remember(video.id) { mutableStateOf<Long?>(null) }
    var abEndMs by remember(video.id) { mutableStateOf<Long?>(null) }

    // External Subtitle (SRT / VTT) Studio + Sync Offset + Font Size state
    val coroutineScope = rememberCoroutineScope()
    var subtitleUri by remember(video.id) { mutableStateOf<Uri?>(null) }
    var captionsEnabled by remember(video.id) { mutableStateOf(true) }
    var customSubtitleCues by remember(video.id) { mutableStateOf<List<SubtitleCue>>(emptyList()) }
    var subtitleSyncOffsetMs by remember(video.id) { mutableLongStateOf(0L) }
    var subtitleFontSp by remember(defaultSubtitleSizeSp) { mutableIntStateOf(defaultSubtitleSizeSp.coerceIn(14, 28)) }
    var showSubtitleStudioDialog by remember { mutableStateOf(false) }

    // Embedded Multi-Audio Track Switcher + Frame Snapshot state
    var audioTrackOptions by remember(video.id) { mutableStateOf<List<VideoAudioTrackOption>>(emptyList()) }
    var showAudioTrackDialog by remember { mutableStateOf(false) }
    var snapshotStatusMsg by remember { mutableStateOf<String?>(null) }

    // Video Sleep Timer state
    var sleepTimerRemainingSec by remember { mutableIntStateOf(0) }

    // Horizontal scrub preview state
    var scrubDeltaMs by remember { mutableLongStateOf(0L) }
    var scrubTargetMs by remember { mutableLongStateOf(0L) }

    val subtitlePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            subtitleUri = uri
            captionsEnabled = true
            subtitleSyncOffsetMs = 0L

            // Parse SRT / VTT directly into our real-time syncable cue engine
            val parsedCues = runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?.let { SubtitleParser.parse(it) }
                    .orEmpty()
            }.getOrDefault(emptyList())
            customSubtitleCues = parsedCues

            if (parsedCues.isEmpty()) {
                val mime = if (uri.toString().lowercase().contains("vtt")) {
                    MimeTypes.TEXT_VTT
                } else {
                    MimeTypes.APPLICATION_SUBRIP
                }
                val resumePos = exoPlayer.currentPosition.coerceAtLeast(0L)
                val wasPlaying = exoPlayer.isPlaying
                val subConfig = MediaItem.SubtitleConfiguration.Builder(uri)
                    .setMimeType(mime)
                    .setLanguage("und")
                    .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                    .build()
                val updatedItem = MediaItem.Builder()
                    .setUri(Uri.parse(video.uriString))
                    .setSubtitleConfigurations(listOf(subConfig))
                    .build()
                exoPlayer.setMediaItem(updatedItem, resumePos)
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                    .build()
                exoPlayer.prepare()
                exoPlayer.playWhenReady = wasPlaying
            } else {
                // Disable ExoPlayer's built-in text overlay so captions never double-render
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                    .build()
            }
        }
    }

    val toggleOrReplayVideo: () -> Unit = remember(exoPlayer) {
        {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            } else {
                val dur = exoPlayer.duration
                val atEnd = exoPlayer.playbackState == Player.STATE_ENDED ||
                    (dur > 0L && exoPlayer.currentPosition >= dur - 350L)
                if (atEnd) {
                    exoPlayer.seekTo(0L)
                    positionMs = 0L
                    isEnded = false
                }
                if (exoPlayer.playbackState == Player.STATE_IDLE) {
                    exoPlayer.prepare()
                }
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }
        }
    }

    val resizeMode = when (resizeModeIndex) {
        1 -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        2 -> AspectRatioFrameLayout.RESIZE_MODE_FILL
        else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
    }
    val resizeModeLabel = when (resizeModeIndex) {
        1 -> strings.videoAspectCrop
        2 -> strings.videoAspectFill
        else -> strings.videoAspectFit
    }

    var activeHud by remember { mutableStateOf<GestureHudType?>(null) }
    var brightnessLevel by remember {
        val current = activity?.window?.attributes?.screenBrightness ?: -1f
        mutableFloatStateOf(if (current in 0f..1f) current else 0.6f)
    }
    var volumeFraction by remember {
        val cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        mutableFloatStateOf(cur.toFloat() / maxVolume.toFloat())
    }

    DisposableEffect(exoPlayer) {
        onRegisterPipToggle(toggleOrReplayVideo)
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (playing) isEnded = false
                onPlaybackActiveChanged(playing)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY && exoPlayer.duration > 0) {
                    durationMs = exoPlayer.duration
                    isEnded = false
                    audioTrackOptions = VideoStudioUtils.extractAudioTracks(exoPlayer.currentTracks)
                } else if (playbackState == Player.STATE_ENDED) {
                    isEnded = true
                    isPlaying = false
                    controlsVisible = true
                    onPlaybackActiveChanged(false)
                    if (autoNextEnabled && !isLoopEnabled && hasPrevNext) {
                        onNextVideo()
                    }
                }
            }

            override fun onTracksChanged(tracks: Tracks) {
                audioTrackOptions = VideoStudioUtils.extractAudioTracks(tracks)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            onRegisterPipToggle(null)
            onPlaybackActiveChanged(false)
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Restore device orientation cleanly when leaving the video player
    DisposableEffect(activity) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Position ticker + A-B Segment Loop enforcement
    LaunchedEffect(exoPlayer, isPlaying, abStartMs, abEndMs) {
        while (true) {
            val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
            positionMs = pos
            if (exoPlayer.duration > 0) durationMs = exoPlayer.duration
            val start = abStartMs
            val end = abEndMs
            if (start != null && end != null && end > start + 400L && pos >= end) {
                exoPlayer.seekTo(start)
                positionMs = start
            }
            delay(200L)
        }
    }

    // Video Sleep Timer countdown
    LaunchedEffect(sleepTimerRemainingSec) {
        if (sleepTimerRemainingSec > 0) {
            delay(1000L)
            val next = sleepTimerRemainingSec - 1
            sleepTimerRemainingSec = next
            if (next == 0) {
                exoPlayer.pause()
                controlsVisible = true
            }
        }
    }

    LaunchedEffect(activeHud) {
        if (activeHud != null && activeHud != GestureHudType.SCRUB) {
            delay(1100L)
            activeHud = null
        }
    }

    LaunchedEffect(snapshotStatusMsg) {
        if (snapshotStatusMsg != null) {
            delay(2400L)
            snapshotStatusMsg = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(isScreenLocked, currentSpeed, seekStepMs, doubleTapSeekEnabled) {
                detectTapGestures(
                    onTap = { controlsVisible = !controlsVisible },
                    onDoubleTap = { offset ->
                        if (isScreenLocked) return@detectTapGestures
                        if (!doubleTapSeekEnabled) {
                            toggleOrReplayVideo()
                            return@detectTapGestures
                        }
                        val w = size.width.toFloat().coerceAtLeast(1f)
                        when {
                            offset.x < w * 0.35f -> {
                                exoPlayer.seekTo((exoPlayer.currentPosition - seekStepMs).coerceAtLeast(0L))
                                activeHud = GestureHudType.SEEK_BACK
                            }
                            offset.x > w * 0.65f -> {
                                exoPlayer.seekTo((exoPlayer.currentPosition + seekStepMs).coerceAtMost(durationMs))
                                activeHud = GestureHudType.SEEK_FWD
                            }
                            else -> {
                                toggleOrReplayVideo()
                            }
                        }
                    },
                    onLongPress = {
                        if (!isScreenLocked && exoPlayer.isPlaying) {
                            isHoldSpeedBoost = true
                            exoPlayer.playbackParameters = PlaybackParameters(2.0f)
                        }
                    },
                    onPress = {
                        tryAwaitRelease()
                        if (isHoldSpeedBoost) {
                            isHoldSpeedBoost = false
                            exoPlayer.playbackParameters = PlaybackParameters(currentSpeed)
                        }
                    }
                )
            }
            .pointerInput(isScreenLocked, gesturesEnabled) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        if (isScreenLocked || !gesturesEnabled) return@detectVerticalDragGestures
                        change.consume()
                        val delta = -dragAmount / size.height.toFloat() * 1.4f
                        val isLeftSide = change.position.x < size.width / 2f
                        if (isLeftSide) {
                            brightnessLevel = (brightnessLevel + delta).coerceIn(0.05f, 1f)
                            activity?.window?.let { win ->
                                val lp = win.attributes
                                lp.screenBrightness = brightnessLevel
                                win.attributes = lp
                            }
                            activeHud = GestureHudType.BRIGHTNESS
                        } else {
                            volumeFraction = (volumeFraction + delta).coerceIn(0f, 1f)
                            val targetVol = (volumeFraction * maxVolume).roundToInt().coerceIn(0, maxVolume)
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0)
                            activeHud = GestureHudType.VOLUME
                        }
                    }
                )
            }
            .pointerInput(isScreenLocked, durationMs) {
                var dragAccumPx = 0f
                var startPosMs = 0L
                detectHorizontalDragGestures(
                    onDragStart = {
                        if (isScreenLocked) return@detectHorizontalDragGestures
                        dragAccumPx = 0f
                        startPosMs = exoPlayer.currentPosition.coerceAtLeast(0L)
                        scrubDeltaMs = 0L
                        scrubTargetMs = startPosMs
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (isScreenLocked) return@detectHorizontalDragGestures
                        change.consume()
                        dragAccumPx += dragAmount
                        val width = size.width.toFloat().coerceAtLeast(1f)
                        val spanMs = (durationMs.coerceAtMost(180_000L)).coerceAtLeast(30_000L)
                        val deltaMs = ((dragAccumPx / width) * spanMs).toLong()
                        val target = (startPosMs + deltaMs).coerceIn(0L, durationMs)
                        scrubDeltaMs = target - startPosMs
                        scrubTargetMs = target
                        activeHud = GestureHudType.SCRUB
                    },
                    onDragEnd = {
                        if (!isScreenLocked && activeHud == GestureHudType.SCRUB) {
                            exoPlayer.seekTo(scrubTargetMs)
                            positionMs = scrubTargetMs
                            activeHud = null
                        }
                    },
                    onDragCancel = {
                        if (activeHud == GestureHudType.SCRUB) activeHud = null
                    }
                )
            }
            // Two-finger Pinch-to-Zoom (1.0x - 3.0x) without interfering with single-finger gestures
            .pointerInput(isScreenLocked, video.id) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    do {
                        val event = awaitPointerEvent()
                        val pointerCount = event.changes.count { it.pressed }
                        if (!isScreenLocked && pointerCount >= 2) {
                            val zoomChange = event.calculateZoom()
                            if (abs(zoomChange - 1f) > 0.005f) {
                                zoomScale = (zoomScale * zoomChange).coerceIn(1f, 3.0f)
                                event.changes.forEach { it.consume() }
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    this.resizeMode = resizeMode
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { view ->
                if (view.player != exoPlayer) view.player = exoPlayer
                view.resizeMode = resizeMode
            },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = (if (isMirrored) -1f else 1f) * zoomScale
                    scaleY = zoomScale
                }
        )

        // Live Video Color Calibration & Night Cinema Dimmer Overlays
        val warmthTint = when {
            colorCalibration.warmth > 0.04f -> Color(0xFFFFB74D).copy(alpha = (colorCalibration.warmth * 0.45f).coerceIn(0f, 0.24f))
            colorCalibration.warmth < -0.04f -> Color(0xFF4FC3F7).copy(alpha = (-colorCalibration.warmth * 0.45f).coerceIn(0f, 0.22f))
            else -> Color.Transparent
        }
        if (warmthTint != Color.Transparent) {
            Box(modifier = Modifier.fillMaxSize().background(warmthTint))
        }
        val extraDimAlpha = (
            (if (isNightDimmer) 0.40f else 0f) +
                (if (colorCalibration.brightness < 0f) (-colorCalibration.brightness).coerceAtMost(0.45f) else 0f)
            ).coerceIn(0f, 0.65f)
        if (extraDimAlpha > 0.01f) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = extraDimAlpha)))
        } else if (colorCalibration.brightness > 0.03f) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = (colorCalibration.brightness * 0.35f).coerceAtMost(0.18f))))
        }

        // Live Custom Synced Subtitle Caption Pill (.SRT / .VTT with Purity Shield, Color, Opacity & Offset)
        val rawCaptionText = if (captionsEnabled && customSubtitleCues.isNotEmpty()) {
            SubtitleParser.activeCaptionAt(customSubtitleCues, positionMs, subtitleSyncOffsetMs)
        } else {
            null
        }
        val liveCaptionText = rawCaptionText?.let { com.example.security.ContentPurityGuard.sanitizeEncyclopediaText(it) }
        if (!liveCaptionText.isNullOrBlank()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.Black.copy(alpha = subtitleBgOpacity.coerceIn(0f, 1f)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = if (controlsVisible && !isScreenLocked && !isInPipMode) {
                            (104 + subtitleBottomOffsetDp).dp
                        } else {
                            subtitleBottomOffsetDp.dp
                        }
                    )
                    .testTag("video_live_subtitle_caption")
            ) {
                Text(
                    text = liveCaptionText,
                    fontSize = subtitleFontSp.sp,
                    lineHeight = (subtitleFontSp + 6).sp,
                    color = Color(subtitleColor.argb),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        // Frame Snapshot Confirmation Toast Pill
        AnimatedVisibility(
            visible = snapshotStatusMsg != null && !isInPipMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .displayCutoutPadding()
                .padding(top = 74.dp)
        ) {
            snapshotStatusMsg?.let { msg ->
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.96f),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Press-and-Hold 2.0x Fast Forward Pill
        AnimatedVisibility(
            visible = isHoldSpeedBoost && !isInPipMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .displayCutoutPadding()
                .padding(top = 18.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.94f),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FastForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.videoHoldSpeedBadge,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        // Pinch-to-Zoom Active Badge (Tap to Reset to 1.0x)
        AnimatedVisibility(
            visible = zoomScale > 1.05f && !isInPipMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .displayCutoutPadding()
                .padding(top = 68.dp)
        ) {
            Surface(
                onClick = { zoomScale = 1f },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.94f),
                shadowElevation = 6.dp,
                modifier = Modifier.testTag("video_zoom_reset_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ZoomIn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${strings.videoZoomBadge(zoomScale)} • 1.0x",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // Gesture Feedback HUD (Brightness / Volume / Double-Tap Seek / Horizontal Scrub)
        AnimatedVisibility(
            visible = activeHud != null && !isInPipMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            val hud = activeHud
            if (hud != null) {
                val (icon, label) = when (hud) {
                    GestureHudType.BRIGHTNESS -> Icons.Filled.Brightness6 to "${strings.brightness} ${(brightnessLevel * 100).roundToInt()}%"
                    GestureHudType.VOLUME -> Icons.AutoMirrored.Filled.VolumeUp to "${strings.volume} ${(volumeFraction * 100).roundToInt()}%"
                    GestureHudType.SEEK_BACK -> Icons.Filled.Replay10 to "-${clampedSeekSec}s"
                    GestureHudType.SEEK_FWD -> Icons.Filled.Forward10 to "+${clampedSeekSec}s"
                    GestureHudType.SCRUB -> {
                        val deltaSec = (scrubDeltaMs / 1000L)
                        val sign = if (deltaSec >= 0) "+" else "-"
                        val iconRes = if (deltaSec >= 0) Icons.Filled.Forward10 else Icons.Filled.Replay10
                        iconRes to "${MediaScanner.formatDuration(scrubTargetMs)} ($sign${abs(deltaSec)}s)"
                    }
                }
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Black.copy(alpha = 0.76f),
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Screen Locked Floating Unlock Button
        AnimatedVisibility(
            visible = isScreenLocked && controlsVisible && !isInPipMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
        ) {
            Surface(
                onClick = { isScreenLocked = false },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
                modifier = Modifier.testTag("video_unlock_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LockOpen,
                        contentDescription = strings.videoUnlockedMsg,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = strings.videoLockedMsg,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Balanced Material 3 Expressive Video Controls Overlay
        AnimatedVisibility(
            visible = controlsVisible && !isScreenLocked && !isInPipMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.68f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.82f)
                            )
                        )
                    )
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                // Top Bar: Back, Title, Speed Toggle, Float in App, System PiP
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.45f)
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("video_player_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.close,
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${video.extension} • ${video.resolution} • ${if (useHardwareDecoder) "HW" else "SW"} • ${driverProfile.refreshRateHz}Hz",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Live Hardware / Software Decoder Switch Pill
                    Surface(
                        shape = CircleShape,
                        color = if (useHardwareDecoder) {
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.92f)
                        } else {
                            Color.Black.copy(alpha = 0.55f)
                        },
                        onClick = { useHardwareDecoder = !useHardwareDecoder },
                        modifier = Modifier.testTag("video_hw_decoder_toggle")
                    ) {
                        Text(
                            text = if (useHardwareDecoder) "HW" else "SW",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (useHardwareDecoder) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                Color.White
                            },
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        onClick = {
                            val nextIdx = (SPEED_OPTIONS.indexOfFirst { abs(it - currentSpeed) < 0.04f } + 1) % SPEED_OPTIONS.size
                            currentSpeed = SPEED_OPTIONS[nextIdx]
                            exoPlayer.playbackParameters = PlaybackParameters(currentSpeed)
                        },
                        modifier = Modifier.testTag("video_speed_toggle")
                    ) {
                        Text(
                            text = "${currentSpeed}x",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }

                    if (onPlayAsAudio != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.88f)
                        ) {
                            IconButton(
                                onClick = {
                                    val pos = exoPlayer.currentPosition.coerceAtLeast(0L)
                                    exoPlayer.pause()
                                    onPlayAsAudio(pos)
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("video_play_as_audio_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Headphones,
                                    contentDescription = strings.playVideoAsAudioAction,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.45f)
                    ) {
                        IconButton(
                            onClick = {
                                onMinimizeToFloating(exoPlayer.currentPosition.coerceAtLeast(0L))
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("video_float_in_app_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PictureInPicture,
                                contentDescription = strings.floatVideoInApp,
                                tint = Color.White
                            )
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && onRequestSystemPip != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f)
                        ) {
                            IconButton(
                                onClick = {
                                    controlsVisible = false
                                    showSubtitleStudioDialog = false
                                    showAudioTrackDialog = false
                                    showColorCalibDialog = false
                                    showChaptersDialog = false
                                    onRequestSystemPip()
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("video_pip_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PictureInPictureAlt,
                                    contentDescription = strings.pipMode,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // Balanced Center Transport Controls: Prev, -10s, Play/Pause/Replay, +10s, Next
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasPrevNext) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            IconButton(
                                onClick = onPreviousVideo,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("video_prev_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipPrevious,
                                    contentDescription = strings.videoPrevAction,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.size(54.dp)
                    ) {
                        IconButton(
                            onClick = {
                                exoPlayer.seekTo((exoPlayer.currentPosition - seekStepMs).coerceAtLeast(0L))
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Replay10,
                                contentDescription = "-${clampedSeekSec}s",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(72.dp)
                    ) {
                        IconButton(
                            onClick = toggleOrReplayVideo,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("video_play_pause")
                        ) {
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
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier.size(54.dp)
                    ) {
                        IconButton(
                            onClick = {
                                exoPlayer.seekTo((exoPlayer.currentPosition + seekStepMs).coerceAtMost(durationMs))
                            },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Forward10,
                                contentDescription = "+${clampedSeekSec}s",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    if (hasPrevNext) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            IconButton(
                                onClick = onNextVideo,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("video_next_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipNext,
                                    contentDescription = strings.videoNextAction,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }

                // Bottom Timeline + Balanced Pro Cinema Controls Strip
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Floating Timeline Scrub Thumbnail Preview Card
                    if (isScrubbingTimeline) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Black.copy(alpha = 0.84f),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 6.dp)
                                .testTag("video_scrub_preview")
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                scrubPreviewBitmap?.let { bmp ->
                                    androidx.compose.foundation.Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = null,
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                        modifier = Modifier
                                            .width(152.dp)
                                            .height(86.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = MediaScanner.formatDuration(scrubPreviewPosMs),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = MediaScanner.formatDuration(positionMs),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Slider(
                            value = positionMs.toFloat().coerceIn(0f, durationMs.toFloat()),
                            onValueChange = {
                                val target = it.toLong()
                                isScrubbingTimeline = true
                                scrubPreviewPosMs = target
                                positionMs = target
                                exoPlayer.seekTo(target)
                            },
                            onValueChangeFinished = {
                                isScrubbingTimeline = false
                            },
                            valueRange = 0f..durationMs.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("video_seek_slider")
                        )
                        Text(
                            text = MediaScanner.formatDuration(durationMs),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Horizontally Scrollable Pro Toolbar (Lock, Rotate, Aspect, Mute, Subtitles, A-B Loop, Loop, Sleep Timer, Night Dimmer)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VideoControlPill(
                            icon = Icons.Filled.Lock,
                            label = null,
                            active = false,
                            tag = "video_lock_button",
                            onClick = { isScreenLocked = true }
                        )

                        VideoControlPill(
                            icon = Icons.Filled.Flip,
                            label = strings.photoFlipAction,
                            active = isMirrored,
                            tag = "video_mirror_button",
                            onClick = {
                                isMirrored = !isMirrored
                                onToggleMirror(isMirrored)
                            }
                        )

                        VideoControlPill(
                            icon = Icons.Filled.ScreenRotation,
                            label = strings.videoRotateLabel,
                            active = isLandscapeForced,
                            tag = "video_rotate_button",
                            onClick = {
                                isLandscapeForced = !isLandscapeForced
                                activity?.requestedOrientation = if (isLandscapeForced) {
                                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                } else {
                                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                                }
                            }
                        )

                        VideoControlPill(
                            icon = Icons.Filled.AspectRatio,
                            label = resizeModeLabel,
                            active = resizeModeIndex != 0,
                            tag = "video_aspect_button",
                            onClick = { resizeModeIndex = (resizeModeIndex + 1) % 3 }
                        )

                        VideoControlPill(
                            icon = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            label = if (isMuted) strings.videoUnmuteLabel else strings.videoMuteLabel,
                            active = isMuted,
                            tag = "video_mute_button",
                            onClick = {
                                isMuted = !isMuted
                                exoPlayer.volume = if (isMuted) 0f else 1f
                            }
                        )

                        val ccLabel = when {
                            subtitleUri == null -> strings.videoSubtitlesLabel
                            captionsEnabled -> strings.videoSubtitlesLoaded
                            else -> strings.videoSubtitlesOff
                        }
                        VideoControlPill(
                            icon = if (subtitleUri != null && captionsEnabled) Icons.Filled.ClosedCaption else Icons.Filled.ClosedCaptionDisabled,
                            label = ccLabel,
                            active = subtitleUri != null && captionsEnabled,
                            tag = "video_subtitles_button",
                            onClick = { showSubtitleStudioDialog = true }
                        )

                        VideoControlPill(
                            icon = Icons.Filled.Audiotrack,
                            label = if (audioTrackOptions.size > 1) {
                                "${strings.videoAudioTrackLabel} (${audioTrackOptions.size})"
                            } else {
                                strings.videoAudioTrackLabel
                            },
                            active = audioTrackOptions.size > 1,
                            tag = "video_audio_tracks_button",
                            onClick = {
                                audioTrackOptions = VideoStudioUtils.extractAudioTracks(exoPlayer.currentTracks)
                                showAudioTrackDialog = true
                            }
                        )

                        VideoControlPill(
                            icon = Icons.Filled.CameraAlt,
                            label = strings.videoSnapshotAction,
                            active = false,
                            tag = "video_snapshot_button",
                            onClick = {
                                val capturePos = exoPlayer.currentPosition.coerceAtLeast(0L)
                                coroutineScope.launch {
                                    val saved = VideoStudioUtils.captureFrameSnapshot(
                                        context = context,
                                        uriString = video.uriString,
                                        positionMs = capturePos,
                                        videoTitle = video.title
                                    )
                                    snapshotStatusMsg = if (saved) {
                                        strings.videoSnapshotSaved
                                    } else {
                                        strings.videoSnapshotFailed
                                    }
                                }
                            }
                        )

                        val abLabel = when {
                            abStartMs == null -> strings.abLoopSetA
                            abEndMs == null -> strings.abLoopSetB
                            else -> strings.abLoopActive
                        }
                        VideoControlPill(
                            icon = Icons.Filled.Loop,
                            label = abLabel,
                            active = abStartMs != null,
                            tag = "video_ab_loop_button",
                            onClick = {
                                when {
                                    abStartMs == null -> abStartMs = exoPlayer.currentPosition.coerceAtLeast(0L)
                                    abEndMs == null -> {
                                        val now = exoPlayer.currentPosition.coerceAtLeast(0L)
                                        if (now > (abStartMs ?: 0L) + 400L) {
                                            abEndMs = now
                                        } else {
                                            abStartMs = null
                                        }
                                    }
                                    else -> {
                                        abStartMs = null
                                        abEndMs = null
                                    }
                                }
                            }
                        )

                        VideoControlPill(
                            icon = if (isLoopEnabled) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                            label = strings.videoLoopLabel,
                            active = isLoopEnabled,
                            tag = "video_loop_button",
                            onClick = {
                                isLoopEnabled = !isLoopEnabled
                                exoPlayer.repeatMode = if (isLoopEnabled) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                            }
                        )

                        val timerLabel = if (sleepTimerRemainingSec > 0) {
                            "${(sleepTimerRemainingSec + 59) / 60}m"
                        } else {
                            strings.videoSleepTimerLabel
                        }
                        VideoControlPill(
                            icon = Icons.Filled.Timer,
                            label = timerLabel,
                            active = sleepTimerRemainingSec > 0,
                            tag = "video_sleep_timer_button",
                            onClick = {
                                val currentMin = if (sleepTimerRemainingSec > 0) (sleepTimerRemainingSec + 59) / 60 else 0
                                val nextMin = VIDEO_TIMER_OPTIONS_MIN.firstOrNull { it > currentMin } ?: 0
                                sleepTimerRemainingSec = nextMin * 60
                            }
                        )

                        VideoControlPill(
                            icon = Icons.Filled.DarkMode,
                            label = strings.videoNightDimmerLabel,
                            active = isNightDimmer,
                            tag = "video_night_dimmer_button",
                            onClick = { isNightDimmer = !isNightDimmer }
                        )

                        VideoControlPill(
                            icon = Icons.Filled.Brightness6,
                            label = strings.videoColorCalibTitle,
                            active = colorCalibration.preset != com.example.model.VideoColorPreset.NATURAL ||
                                abs(colorCalibration.warmth) > 0.04f || abs(colorCalibration.brightness) > 0.04f,
                            tag = "video_color_calib_button",
                            onClick = { showColorCalibDialog = true }
                        )

                        VideoControlPill(
                            icon = Icons.Filled.Check,
                            label = "${strings.videoChaptersTitle} (${videoBookmarks.size})",
                            active = videoBookmarks.isNotEmpty(),
                            tag = "video_chapters_button",
                            onClick = { showChaptersDialog = true }
                        )

                        // Frame-by-Frame Precision Stepping (-1F / +1F)
                        VideoControlPill(
                            icon = Icons.Filled.FastRewind,
                            label = strings.videoFrameBack,
                            active = false,
                            tag = "video_frame_prev_button",
                            onClick = {
                                exoPlayer.pause()
                                val target = (exoPlayer.currentPosition - 33L).coerceAtLeast(0L)
                                exoPlayer.seekTo(target)
                                positionMs = target
                            }
                        )
                        VideoControlPill(
                            icon = Icons.Filled.FastForward,
                            label = strings.videoFrameForward,
                            active = false,
                            tag = "video_frame_next_button",
                            onClick = {
                                exoPlayer.pause()
                                val target = (exoPlayer.currentPosition + 33L).coerceAtMost(durationMs)
                                exoPlayer.seekTo(target)
                                positionMs = target
                            }
                        )

                        // Personal Media Note for this Video
                        VideoControlPill(
                            icon = Icons.Filled.EditNote,
                            label = strings.mediaNoteTitle,
                            active = hasNote,
                            tag = "video_note_button",
                            onClick = onOpenNote
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }

        // Video Color Calibration Dialog
        if (showColorCalibDialog) {
            AlertDialog(
                onDismissRequest = { showColorCalibDialog = false },
                shape = RoundedCornerShape(26.dp),
                title = { Text(strings.videoColorCalibTitle, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                com.example.model.VideoColorPreset.NATURAL to Triple(0f, 0f, "Natural"),
                                com.example.model.VideoColorPreset.CINEMA to Triple(-0.08f, 0.14f, "Cinema"),
                                com.example.model.VideoColorPreset.EYE_COMFORT to Triple(-0.12f, 0.34f, "Eye Comfort"),
                                com.example.model.VideoColorPreset.VIVID to Triple(0.12f, -0.08f, "Vivid"),
                                com.example.model.VideoColorPreset.NIGHT to Triple(-0.32f, 0.20f, "Night"),
                            ).forEach { (preset, cfg) ->
                                FilterChip(
                                    selected = colorCalibration.preset == preset,
                                    onClick = {
                                        colorCalibration = colorCalibration.copy(
                                            preset = preset,
                                            brightness = cfg.first,
                                            warmth = cfg.second
                                        )
                                    },
                                    label = { Text(cfg.third) }
                                )
                            }
                        }
                        Text("Brightness (${(colorCalibration.brightness * 100).roundToInt()}%)", style = MaterialTheme.typography.labelMedium)
                        Slider(
                            value = colorCalibration.brightness,
                            onValueChange = { colorCalibration = colorCalibration.copy(brightness = it) },
                            valueRange = -0.35f..0.35f
                        )
                        Text("Warmth (${(colorCalibration.warmth * 100).roundToInt()}%)", style = MaterialTheme.typography.labelMedium)
                        Slider(
                            value = colorCalibration.warmth,
                            onValueChange = { colorCalibration = colorCalibration.copy(warmth = it) },
                            valueRange = -0.30f..0.40f
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showColorCalibDialog = false }) { Text(strings.close) }
                }
            )
        }

        // Video Chapters & Timestamp Bookmarks Dialog
        if (showChaptersDialog) {
            var chapterNote by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showChaptersDialog = false },
                shape = RoundedCornerShape(26.dp),
                title = { Text(strings.videoChaptersTitle, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        androidx.compose.material3.OutlinedTextField(
                            value = chapterNote,
                            onValueChange = { chapterNote = it },
                            singleLine = true,
                            placeholder = { Text(MediaScanner.formatDuration(positionMs)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        FilledTonalButton(
                            onClick = {
                                onAddBookmark(exoPlayer.currentPosition.coerceAtLeast(0L), chapterNote.trim())
                                chapterNote = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("${strings.videoAddChapterBtn} (${MediaScanner.formatDuration(positionMs)})")
                        }
                        videoBookmarks.forEach { bm ->
                            Surface(
                                onClick = {
                                    exoPlayer.seekTo(bm.positionMs)
                                    positionMs = bm.positionMs
                                    showChaptersDialog = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(bm.label, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(MediaScanner.formatDuration(bm.positionMs), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                    TextButton(onClick = { onRemoveBookmark(bm.id) }) { Text(strings.deleteAction) }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showChaptersDialog = false }) { Text(strings.close) }
                }
            )
        }

        // Pro Subtitles & Sync Studio Dialog (.SRT / .VTT + ±250ms Sync + S/M/L Size)
        if (showSubtitleStudioDialog) {
            AlertDialog(
                onDismissRequest = { showSubtitleStudioDialog = false },
                shape = RoundedCornerShape(28.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                title = {
                    Text(
                        text = strings.videoSubtitleSettingsTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilledTonalButton(
                            onClick = {
                                showSubtitleStudioDialog = false
                                subtitlePickerLauncher.launch(arrayOf("application/x-subrip", "text/vtt", "text/*", "*/*"))
                            },
                            shape = CircleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("video_subtitle_load_file")
                        ) {
                            Icon(Icons.Filled.ClosedCaption, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strings.videoSubtitleLoadFile, fontWeight = FontWeight.Bold)
                        }

                        if (subtitleUri != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (captionsEnabled) strings.videoSubtitlesLoaded else strings.videoSubtitlesOff,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Switch(
                                    checked = captionsEnabled,
                                    onCheckedChange = { enabled ->
                                        captionsEnabled = enabled
                                        if (customSubtitleCues.isEmpty()) {
                                            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                                .buildUpon()
                                                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !enabled)
                                                .build()
                                        }
                                    }
                                )
                            }
                        }

                        // Sync Offset (-2000ms .. +2000ms in 250ms steps)
                        Text(
                            text = "${strings.videoSubtitleSyncLabel}: ${if (subtitleSyncOffsetMs >= 0) "+" else ""}${subtitleSyncOffsetMs}ms",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { subtitleSyncOffsetMs = (subtitleSyncOffsetMs - 250L).coerceAtLeast(-5000L) },
                                shape = CircleShape,
                                modifier = Modifier.weight(1f)
                            ) { Text("-250ms", maxLines = 1) }
                            OutlinedButton(
                                onClick = { subtitleSyncOffsetMs = 0L },
                                shape = CircleShape,
                                modifier = Modifier.weight(1f)
                            ) { Text("0ms", maxLines = 1) }
                            OutlinedButton(
                                onClick = { subtitleSyncOffsetMs = (subtitleSyncOffsetMs + 250L).coerceAtMost(5000L) },
                                shape = CircleShape,
                                modifier = Modifier.weight(1f)
                            ) { Text("+250ms", maxLines = 1) }
                        }

                        // Subtitle Font Size (S / M / L)
                        Text(
                            text = strings.videoSubtitleSizeLabel,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(14 to "S", 18 to "M", 22 to "L").forEach { (spVal, label) ->
                                FilterChip(
                                    selected = subtitleFontSp == spVal,
                                    onClick = { subtitleFontSp = spVal },
                                    label = { Text(label, fontWeight = FontWeight.Bold) },
                                    shape = CircleShape
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSubtitleStudioDialog = false }) {
                        Text(strings.close)
                    }
                }
            )
        }

        // Multi-Audio Track Switcher Dialog
        if (showAudioTrackDialog) {
            AlertDialog(
                onDismissRequest = { showAudioTrackDialog = false },
                shape = RoundedCornerShape(28.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                title = {
                    Text(
                        text = strings.videoAudioTrackTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (audioTrackOptions.isEmpty()) {
                            Text(
                                text = strings.videoNoExtraAudioTracks,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            audioTrackOptions.forEach { option ->
                                Surface(
                                    onClick = {
                                        VideoStudioUtils.selectAudioTrack(exoPlayer, option)
                                        audioTrackOptions = audioTrackOptions.map {
                                            it.copy(isSelected = it.groupIndex == option.groupIndex && it.trackIndex == option.trackIndex)
                                        }
                                        showAudioTrackDialog = false
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (option.isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceContainer
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = option.label,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (option.isSelected) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                        if (option.isSelected) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAudioTrackDialog = false }) {
                        Text(strings.close)
                    }
                }
            )
        }
    }
}

@Composable
private fun VideoControlPill(
    icon: ImageVector,
    label: String?,
    active: Boolean,
    tag: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (active) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.58f),
        modifier = Modifier.testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (label != null) 12.dp else 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (active) MaterialTheme.colorScheme.onPrimary else Color.White,
                modifier = Modifier.size(16.dp)
            )
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (active) MaterialTheme.colorScheme.onPrimary else Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}
