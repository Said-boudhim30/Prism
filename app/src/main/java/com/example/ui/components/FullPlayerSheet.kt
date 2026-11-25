package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MediaScanner
import com.example.localization.AppStrings
import com.example.model.AppSettings
import com.example.model.AudioTrack
import com.example.model.EqPreset
import com.example.model.MediaBookmark
import com.example.model.MediaKnowledgeReport
import com.example.model.PlaybackUiState
import com.example.model.PlayerArtworkStyle
import com.example.model.QueueSortOption
import com.example.model.RepeatMode as TrackRepeatMode
import com.example.model.ReverbEnvironment
import com.example.model.SpectrumVisualStyle
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tune
import com.example.ui.theme.AlbumArtPalette
import com.example.ui.theme.AlbumArtPaletteExtractor
import com.example.ui.theme.LocalHapticsEnabled
import com.example.ui.theme.TrackGenrePaletteEngine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerSheet(
    playbackState: PlaybackUiState,
    positionMsFlow: StateFlow<Long>,
    spectrumLevelsFlow: StateFlow<List<Float>>,
    settings: AppSettings,
    queueTracks: List<AudioTrack>,
    lyricsText: String?,
    audioDriverLabel: String = "",
    strings: AppStrings,
    onDismiss: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onSeekRelative: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onToggleAbLoop: () -> Unit,
    onCycleSpeed: () -> Unit,
    onPlayFromQueue: (AudioTrack) -> Unit,
    onMoveQueueUp: (String) -> Unit = {},
    onMoveQueueDown: (String) -> Unit = {},
    onRemoveFromQueue: (String) -> Unit = {},
    onSortQueue: (QueueSortOption) -> Unit = {},
    onToggleFavorite: (AudioTrack) -> Unit,
    onSaveLyrics: (String, String) -> Unit,
    onToggleEq: (Boolean) -> Unit,
    onBassBoostChange: (Float) -> Unit,
    onSelectEqPreset: (EqPreset) -> Unit,
    onResetEq: () -> Unit,
    onBandChange: (Int, Float) -> Unit,
    onStartSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    bookmarks: List<MediaBookmark> = emptyList(),
    onAddBookmark: (String, Long, String) -> Unit = { _, _, _ -> },
    onRemoveBookmark: (String) -> Unit = {},
    onEditTags: (AudioTrack) -> Unit = {},
    knowledgeReport: MediaKnowledgeReport? = null,
    isEnriching: Boolean = false,
    onRefreshKnowledge: () -> Unit = {},
    onFetchOnlineLyrics: () -> Unit = {},
    onToggleKhushuMode: (Boolean) -> Unit = {},
    onOpenAudioCutter: (AudioTrack) -> Unit = {},
    onOpenTasbih: () -> Unit = {},
    onToggleMute: () -> Unit = {},
    onToggleDriveMode: (Boolean) -> Unit = {},
    onPreampChange: (Int) -> Unit = {},
    onPitchChange: (Float) -> Unit = {},
    onCrossfadeChange: (Int) -> Unit = {},
    onNormalizationChange: (Boolean) -> Unit = {},
    onStereoBalanceChange: (Float) -> Unit = {},
    onMonoChange: (Boolean) -> Unit = {},
    onSkipSilenceChange: (Boolean) -> Unit = {},
    onLectureModeChange: (Boolean) -> Unit = {},
    onHeadsetUnplugChange: (Boolean) -> Unit = {},
    hifzRepeatTarget: Int = 0,
    onHifzRepeatChange: (Int) -> Unit = {},
    onSpectrumStyleChange: (SpectrumVisualStyle) -> Unit = {},
    onArtworkStyleChange: (PlayerArtworkStyle) -> Unit = {},
    onSwipeArtToggle: (Boolean) -> Unit = {},
    onLyricsFontSizeChange: (Int) -> Unit = {},
    onReverbChange: (ReverbEnvironment) -> Unit = {},
    hasNote: Boolean = false,
    onOpenNote: (AudioTrack) -> Unit = {},
    onOpenAudioProfiles: () -> Unit = {},
    onRequestMusicPip: (() -> Unit)? = null,
    onOpenProAudioStudio: () -> Unit = {},
    onToggleAutoStudio: (Boolean) -> Unit = {},
    onToggleDspBypass: (Boolean) -> Unit = {},
    onFetchCoverAndLyrics: (AudioTrack) -> Unit = {},
) {
    val track = playbackState.currentTrack ?: return
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val seekStepSec = settings.seekStepSeconds.coerceIn(5, 30)
    val seekStepMs = seekStepSec * 1000L
    BackHandler(onBack = onDismiss)

    // Dynamic Material 3 Genre + Album Art Color Engine (smoothly crossfades per song genre & cover!)
    var artPalette by remember(track.id) { mutableStateOf<AlbumArtPalette?>(null) }
    LaunchedEffect(track.id, track.artworkResId, track.uriString, settings.albumArtTintEnabled) {
        artPalette = if (settings.albumArtTintEnabled) {
            AlbumArtPaletteExtractor.extractForTrack(context, track)
        } else {
            null
        }
    }
    val combinedGenrePalette = remember(
        track.id,
        knowledgeReport?.mediaId,
        artPalette,
        settings.darkTheme,
        settings.playerGenreDynamicColors,
        settings.albumArtTintEnabled,
    ) {
        TrackGenrePaletteEngine.resolveCombinedPalette(
            track = track,
            report = knowledgeReport,
            artPalette = artPalette,
            isDark = settings.darkTheme,
            genreColorsEnabled = settings.playerGenreDynamicColors,
            albumTintEnabled = settings.albumArtTintEnabled,
            fallbackPrimary = Color(0xFF7C4DFF),
            fallbackSecondary = Color(0xFF00BFA5),
            fallbackDeep = Color(0xFF141827),
        )
    }
    val dynamicAccentTint by animateColorAsState(
        targetValue = if (settings.playerGenreDynamicColors || settings.albumArtTintEnabled) {
            combinedGenrePalette.primaryAccent
        } else {
            MaterialTheme.colorScheme.primary
        },
        animationSpec = tween(durationMillis = 650),
        label = "fullPlayerArtAccent"
    )
    val dynamicSecondaryGlow by animateColorAsState(
        targetValue = if (settings.playerGenreDynamicColors || settings.albumArtTintEnabled) {
            combinedGenrePalette.secondaryGlow
        } else {
            MaterialTheme.colorScheme.tertiary
        },
        animationSpec = tween(durationMillis = 650),
        label = "fullPlayerSecondaryGlow"
    )
    val dynamicDeepTint by animateColorAsState(
        targetValue = if (settings.playerGenreDynamicColors || settings.albumArtTintEnabled) {
            combinedGenrePalette.deepBackdrop
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        animationSpec = tween(durationMillis = 650),
        label = "fullPlayerArtDeep"
    )

    var showQuickToolsDrawer by remember { mutableStateOf(false) }
    var showExtrasSheet by remember { mutableStateOf(false) }
    var extrasInitialTab by remember { mutableStateOf(0) }
    var showEqModal by remember { mutableStateOf(false) }
    var showTimerModal by remember { mutableStateOf(false) }
    var showQueueModal by remember { mutableStateOf(false) }
    var showBookmarksModal by remember { mutableStateOf(false) }
    var showEditLyricsDialog by remember { mutableStateOf(false) }
    var showTapSyncDialog by remember { mutableStateOf(false) }

    // Smooth interactive swipe-down-to-dismiss state
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    val animatedDragY by animateFloatAsState(
        targetValue = dragOffsetY,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
        label = "fullPlayerDragY"
    )
    val dismissScale = (1f - (animatedDragY / 2600f).coerceIn(0f, 0.08f))

    val gradientBackdrop = remember(dynamicAccentTint, dynamicDeepTint, dynamicSecondaryGlow, settings.darkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                dynamicAccentTint.copy(alpha = if (settings.darkTheme) 0.30f else 0.22f),
                dynamicDeepTint.copy(alpha = if (settings.darkTheme) 0.56f else 0.26f),
                dynamicSecondaryGlow.copy(alpha = if (settings.darkTheme) 0.26f else 0.14f),
            )
        )
    }

    val abLabel = when {
        playbackState.abLoopStartMs != null && playbackState.abLoopEndMs != null -> strings.abLoopActive
        playbackState.abLoopStartMs != null -> strings.abLoopSetB
        else -> strings.abLoopSetA
    }
    val abActive = playbackState.abLoopStartMs != null
    val driveBig = settings.driveModeEnabled
    val genreLabel = strings.genreMoodName(combinedGenrePalette.mood)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = animatedDragY
                scaleX = dismissScale
                scaleY = dismissScale
            },
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Full-Screen Uncluttered 3-Zone Stage: Top Bar -> Centered Expressive Hero -> Pinned Bottom Deck
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBackdrop)
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ZONE 1: Clean Top Header + Subtle Compact Extras Trigger
                Column(
                    modifier = Modifier
                        .widthIn(max = 560.dp)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragEnd = {
                                    if (dragOffsetY > 150f) {
                                        if (hapticsEnabled) {
                                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                        dragOffsetY = 0f
                                        onDismiss()
                                    } else {
                                        dragOffsetY = 0f
                                    }
                                },
                                onDragCancel = { dragOffsetY = 0f },
                                onVerticalDrag = { _, dragAmount ->
                                    dragOffsetY = (dragOffsetY + dragAmount).coerceAtLeast(0f)
                                }
                            )
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp, bottom = 6.dp)
                            .width(42.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("full_player_collapse")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = strings.close,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        // Subtle Genre Mood / Album Pill
                        Surface(
                            shape = CircleShape,
                            color = dynamicAccentTint.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, dynamicAccentTint.copy(alpha = 0.32f)),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Text(
                                text = if (settings.playerGenreDynamicColors) genreLabel else track.album.ifBlank { strings.nowPlaying },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { onToggleFavorite(track) },
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("full_player_favorite")
                            ) {
                                Icon(
                                    imageVector = if (track.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = if (track.isFavorite) strings.menuRemoveFavorite else strings.menuAddFavorite,
                                    tint = if (track.isFavorite) dynamicAccentTint else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Clean Expressive Collapsible Menu Toggle Button (Opens/Closes Quick Tools Drawer cleanly)
                            val subtle = settings.playerExtrasButtonSubtle
                            Surface(
                                onClick = {
                                    if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    showQuickToolsDrawer = !showQuickToolsDrawer
                                },
                                shape = CircleShape,
                                color = if (showQuickToolsDrawer) {
                                    dynamicAccentTint.copy(alpha = 0.28f)
                                } else if (subtle) {
                                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.48f)
                                } else {
                                    dynamicAccentTint.copy(alpha = 0.22f)
                                },
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = dynamicAccentTint.copy(alpha = if (showQuickToolsDrawer) 0.65f else if (subtle) 0.22f else 0.45f)
                                ),
                                modifier = Modifier
                                    .size(if (subtle && !showQuickToolsDrawer) 40.dp else 44.dp)
                                    .alpha(if (subtle && !showQuickToolsDrawer) 0.78f else 1f)
                                    .testTag("full_player_extras_trigger")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (showQuickToolsDrawer) Icons.Rounded.ExpandLess else Icons.Rounded.Tune,
                                        contentDescription = if (showQuickToolsDrawer) strings.playerToolsDrawerClose else strings.playerToolsDrawerOpen,
                                        tint = if (showQuickToolsDrawer) dynamicAccentTint else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ZONE 2: Enlarged, Flagship Centered Material 3 Expressive Artwork Hero (Zero subcomposition)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .widthIn(max = 560.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    var artSwipeAccum by remember(track.id) { mutableFloatStateOf(0f) }
                    val artShape = remember(settings.playerArtworkStyle, settings.shapeRoundness) {
                        PrismExpressiveShapes.artworkShapeFor(settings.playerArtworkStyle, settings.shapeRoundness)
                    }
                    val artShadowDp = if (PrismExpressiveShapes.usesGenericOutline(settings.playerArtworkStyle)) 0.dp else 16.dp

                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .sizeIn(minWidth = 200.dp, minHeight = 200.dp, maxWidth = 316.dp, maxHeight = 316.dp)
                            .aspectRatio(1f)
                            .then(
                                if (settings.playerGestureSwipeArt) {
                                    Modifier.pointerInput(track.id) {
                                        detectHorizontalDragGestures(
                                            onDragEnd = {
                                                if (artSwipeAccum < -75f) {
                                                    if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    onNext()
                                                } else if (artSwipeAccum > 75f) {
                                                    if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    onPrevious()
                                                }
                                                artSwipeAccum = 0f
                                            },
                                            onDragCancel = { artSwipeAccum = 0f },
                                            onHorizontalDrag = { _, dragAmount -> artSwipeAccum += dragAmount }
                                        )
                                    }
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Living Genre-Reactive Aura Halo behind artwork
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            dynamicAccentTint.copy(alpha = if (playbackState.isPlaying) 0.44f else 0.24f),
                                            dynamicSecondaryGlow.copy(alpha = 0.20f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        when (settings.playerArtworkStyle) {
                            PlayerArtworkStyle.VINYL_DISC -> {
                                SpinningVinylDiscLeaf(
                                    track = track,
                                    isPlaying = playbackState.isPlaying,
                                    accentTint = dynamicAccentTint,
                                    deepTint = dynamicDeepTint,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp)
                                )
                            }
                            else -> {
                                val framePad = if (settings.playerArtworkStyle == PlayerArtworkStyle.CIRCLE_GLOW) 14.dp else 8.dp
                                Surface(
                                    shape = artShape,
                                    tonalElevation = 10.dp,
                                    shadowElevation = artShadowDp,
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    border = BorderStroke(2.dp, dynamicAccentTint.copy(alpha = 0.54f)),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(framePad)
                                        .testTag("full_player_artwork_${settings.playerArtworkStyle.name.lowercase()}")
                                ) {
                                    TrackArtworkImage(
                                        track = track,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(artShape)
                                    )
                                }
                            }
                        }
                    }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Bold Flagship Title & Artist Hierarchy
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = track.artist,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    // Optional Sleek Single-Line Audio Tech Telemetry Pill
                    if (settings.playerShowTechTelemetry) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val dspText = when {
                            settings.playerDirectBitExactDsp -> "Bit-Exact Pure"
                            settings.reverbEnvironment != ReverbEnvironment.NONE -> "32-bit DSP • ${settings.reverbEnvironment.name}"
                            else -> "32-bit DSP"
                        }
                        val driverShort = audioDriverLabel.take(22)
                        Surface(
                            shape = CircleShape,
                            color = dynamicAccentTint.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, dynamicAccentTint.copy(alpha = 0.28f)),
                            modifier = Modifier.testTag("full_player_audio_driver_badge")
                        ) {
                            Text(
                                text = buildString {
                                    append(track.extension)
                                    append(" • ")
                                    append(dspText)
                                    if (driverShort.isNotBlank()) append(" • ").append(driverShort)
                                },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Optional Subtle Live Audio Spectrum Visualizer
                    if (settings.showFullPlayerSpectrum && !settings.khushuModeEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        FullPlayerSpectrumLeaf(
                            spectrumLevelsFlow = spectrumLevelsFlow,
                            isPlaying = playbackState.isPlaying,
                            style = settings.spectrumVisualStyle,
                            accentOverride = dynamicAccentTint,
                            secondaryOverride = dynamicSecondaryGlow,
                            onCycleStyle = {
                                val all = SpectrumVisualStyle.entries
                                val next = all[(all.indexOf(settings.spectrumVisualStyle) + 1) % all.size]
                                onSpectrumStyleChange(next)
                            }
                        )
                    }
                }

                // ZONE 3: Bottom-Anchored Seek Bar + Main Transport Controls + Whisper-Quiet Micro Strip
                Column(
                    modifier = Modifier
                        .widthIn(max = 560.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Seek Bar & Timecodes right above controls
                    FullPlayerSeekLeaf(
                        positionMsFlow = positionMsFlow,
                        durationMs = playbackState.durationMs.coerceAtLeast(1L),
                        isPlaying = playbackState.isPlaying,
                        squigglyEnabled = settings.squigglyProgress,
                        activeColor = dynamicAccentTint,
                        onSeek = onSeek
                    )

                    // Primary Bottom Transport Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!driveBig) {
                            IconButton(
                                onClick = onToggleShuffle,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("full_player_shuffle")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Shuffle,
                                    contentDescription = strings.shuffleLabel,
                                    tint = if (playbackState.shuffleEnabled) dynamicAccentTint else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.88f),
                            modifier = Modifier.size(if (driveBig) 74.dp else 60.dp)
                        ) {
                            IconButton(
                                onClick = onPrevious,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("full_player_prev")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SkipPrevious,
                                    contentDescription = "Previous",
                                    modifier = Modifier.size(if (driveBig) 40.dp else 32.dp)
                                )
                            }
                        }

                        // Expressive Shape-Morphing Genre-Tinted Play/Pause Button (cached shape, zero concave shadow stall)
                        val isScallopedPlay = settings.playerExpressivePlayMorph && playbackState.isPlaying
                        val playShape = remember(settings.playerExpressivePlayMorph, playbackState.isPlaying) {
                            when {
                                isScallopedPlay -> PrismExpressiveShapes.scallopedCookie(lobes = 10, depth = 0.055f)
                                settings.playerExpressivePlayMorph -> RoundedCornerShape(26.dp)
                                else -> CircleShape
                            }
                        }
                        Surface(
                            onClick = {
                                if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onTogglePlayPause()
                            },
                            shape = playShape,
                            color = dynamicAccentTint,
                            shadowElevation = if (isScallopedPlay) 0.dp else 10.dp,
                            modifier = Modifier
                                .size(if (driveBig) 96.dp else 80.dp)
                                .testTag("full_player_play_pause")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (playbackState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                    contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(if (driveBig) 48.dp else 40.dp)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.88f),
                            modifier = Modifier.size(if (driveBig) 74.dp else 60.dp)
                        ) {
                            IconButton(
                                onClick = onNext,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("full_player_next")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.SkipNext,
                                    contentDescription = "Next",
                                    modifier = Modifier.size(if (driveBig) 40.dp else 32.dp)
                                )
                            }
                        }

                        if (!driveBig) {
                            IconButton(
                                onClick = onCycleRepeat,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("full_player_repeat")
                            ) {
                                Icon(
                                    imageVector = if (playbackState.repeatMode == TrackRepeatMode.ONE) {
                                        Icons.Rounded.RepeatOne
                                    } else {
                                        Icons.Rounded.Repeat
                                    },
                                    contentDescription = strings.repeatAll,
                                    tint = if (playbackState.repeatMode != TrackRepeatMode.OFF) dynamicAccentTint else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Animated Clean Collapsible Tools & Pro Studio Drawer (Hidden by default so card stays pure!)
                    AnimatedVisibility(
                        visible = showQuickToolsDrawer,
                        enter = expandVertically(animationSpec = spring(dampingRatio = 0.84f)) + fadeIn(),
                        exit = shrinkVertically(animationSpec = spring(dampingRatio = 0.84f)) + fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, dynamicAccentTint.copy(alpha = 0.34f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("full_player_collapsible_drawer")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    selected = settings.proEqEnabled && !settings.dspBypassEnabled,
                                    onClick = {
                                        showQuickToolsDrawer = false
                                        onOpenProAudioStudio()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    label = { Text(strings.proStudioTitle.take(18), maxLines = 1) },
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("full_player_drawer_pro_studio")
                                )
                                FilterChip(
                                    selected = settings.autoStudioMasteringEnabled,
                                    onClick = { onToggleAutoStudio(!settings.autoStudioMasteringEnabled) },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    label = { Text(strings.studioAutoTitle.take(16), maxLines = 1) },
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("full_player_drawer_auto_studio")
                                )
                                FilterChip(
                                    selected = !settings.dspBypassEnabled,
                                    onClick = { onToggleDspBypass(!settings.dspBypassEnabled) },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    label = {
                                        Text(
                                            if (settings.dspBypassEnabled) strings.studioBypassOriginal else strings.studioBypassMastered,
                                            maxLines = 1
                                        )
                                    },
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("full_player_drawer_ab_compare")
                                )
                                FilterChip(
                                    selected = playbackState.sleepTimerRemainingSec > 0,
                                    onClick = {
                                        showQuickToolsDrawer = false
                                        showTimerModal = true
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    label = {
                                        Text(
                                            if (playbackState.sleepTimerRemainingSec > 0) {
                                                formatSecondsClock(playbackState.sleepTimerRemainingSec)
                                            } else strings.sleepTimerTitle,
                                            maxLines = 1
                                        )
                                    },
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("full_player_drawer_timer")
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        showQuickToolsDrawer = false
                                        extrasInitialTab = 0
                                        showExtrasSheet = true
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.MoreHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    label = { Text(strings.playerExtrasStudioTitle, maxLines = 1) },
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("full_player_drawer_more")
                                )
                            }
                        }
                    }

                    // Minimal Bottom Micro-Utility Strip (Queue • Collapsible Tools Toggle Pill • Pro Studio)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showQueueModal = true },
                            modifier = Modifier
                                .size(48.dp)
                                .alpha(0.80f)
                                .testTag("full_player_open_queue")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                contentDescription = strings.upNextQueue,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Expressive Expand/Collapse Tools & Studio Pill
                        Surface(
                            onClick = {
                                if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showQuickToolsDrawer = !showQuickToolsDrawer
                            },
                            shape = CircleShape,
                            color = if (showQuickToolsDrawer) {
                                dynamicAccentTint.copy(alpha = 0.24f)
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHighest.copy(
                                    alpha = if (settings.playerExtrasButtonSubtle) 0.46f else 0.78f
                                )
                            },
                            border = BorderStroke(
                                1.dp,
                                dynamicAccentTint.copy(alpha = if (showQuickToolsDrawer) 0.58f else if (settings.playerExtrasButtonSubtle) 0.24f else 0.45f)
                            ),
                            modifier = Modifier
                                .alpha(if (settings.playerExtrasButtonSubtle && !showQuickToolsDrawer) 0.78f else 1f)
                                .testTag("full_player_compact_studio_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (showQuickToolsDrawer) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = if (showQuickToolsDrawer) strings.playerToolsDrawerClose else strings.playerToolsDrawerOpen,
                                    tint = dynamicAccentTint,
                                    modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    text = if (showQuickToolsDrawer) strings.playerToolsDrawerClose else strings.playerToolsDrawerOpen,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }

                        IconButton(
                            onClick = { onOpenProAudioStudio() },
                            modifier = Modifier
                                .size(48.dp)
                                .alpha(0.84f)
                                .testTag("full_player_quick_pro_studio")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Equalizer,
                                contentDescription = strings.proStudioTitle,
                                tint = if (settings.autoStudioMasteringEnabled) dynamicAccentTint else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Audio-Reactive Edge Lighting Glow
            AudioReactiveEdgeGlowOverlay(
                spectrumLevelsFlow = spectrumLevelsFlow,
                isPlaying = playbackState.isPlaying,
                enabled = settings.edgeGlowEnabled,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    // CLEAN TABBED EXTRAS & PRO STUDIO MODAL SHEET (All tools neatly organized, zero main-screen clutter!)
    if (showExtrasSheet) {
        val trackBookmarksCount = remember(bookmarks, track.id) {
            bookmarks.count { it.mediaId == track.id }
        }
        ModalBottomSheet(
            onDismissRequest = { showExtrasSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            var activeTab by remember(extrasInitialTab) { mutableStateOf(extrasInitialTab) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 6.dp)
                    .testTag("full_player_extras_sheet"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = strings.playerExtrasStudioTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                // 3 Crisp Segmented Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple(0, Icons.Filled.Tune, strings.dspPageSound),
                        Triple(1, Icons.Filled.GraphicEq, strings.proAudioDeckTitle),
                        Triple(2, Icons.Filled.Lyrics, strings.artistBioTitle),
                    ).forEach { (idx, icon, label) ->
                        FilterChip(
                            selected = activeTab == idx,
                            onClick = { activeTab = idx },
                            leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            shape = CircleShape,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                when (activeTab) {
                    0 -> {
                        // Primary Pro Audio Mastering Studio Launcher (10-Band EQ + Auto-Studio + 15+ DSP)
                        Button(
                            onClick = { showExtrasSheet = false; onOpenProAudioStudio() },
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("full_player_open_pro_studio")
                        ) {
                            Icon(Icons.Rounded.GraphicEq, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(strings.proStudioOpenAction, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        // Quick Actions Row (Quick 5-Band EQ & Sleep Timer)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilledTonalButton(
                                onClick = { showExtrasSheet = false; showEqModal = true },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.weight(1f).height(48.dp).testTag("full_player_open_eq")
                            ) {
                                Icon(Icons.Rounded.Equalizer, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(strings.equalizerTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            FilledTonalButton(
                                onClick = { showExtrasSheet = false; showTimerModal = true },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.weight(1f).height(48.dp).testTag("full_player_open_timer")
                            ) {
                                Icon(Icons.Rounded.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = if (playbackState.sleepTimerRemainingSec > 0) {
                                        formatSecondsClock(playbackState.sleepTimerRemainingSec)
                                    } else strings.sleepTimerTitle,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Compact Pill FlowRow of all secondary controls
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChip(
                                selected = playbackState.playbackSpeed != 1.0f,
                                onClick = onCycleSpeed,
                                leadingIcon = { Icon(Icons.Filled.Speed, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text("${playbackState.playbackSpeed}x") },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_speed")
                            )
                            FilterChip(
                                selected = abActive,
                                onClick = onToggleAbLoop,
                                leadingIcon = { Icon(Icons.Filled.Loop, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text(abLabel) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_ab_loop")
                            )
                            FilterChip(
                                selected = false,
                                onClick = { onSeekRelative(-seekStepMs) },
                                leadingIcon = { Icon(Icons.Filled.Replay10, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text("-${seekStepSec}s") },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_seek_back")
                            )
                            FilterChip(
                                selected = false,
                                onClick = { onSeekRelative(seekStepMs) },
                                leadingIcon = { Icon(Icons.Filled.Forward10, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text("+${seekStepSec}s") },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_seek_fwd")
                            )
                            FilterChip(
                                selected = trackBookmarksCount > 0,
                                onClick = { showExtrasSheet = false; showBookmarksModal = true },
                                leadingIcon = { Icon(Icons.Filled.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text(if (trackBookmarksCount > 0) "${strings.bookmarksTitle} ($trackBookmarksCount)" else strings.bookmarksTitle) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_open_bookmarks")
                            )
                            FilterChip(
                                selected = false,
                                onClick = { showExtrasSheet = false; onEditTags(track) },
                                leadingIcon = { Icon(Icons.Filled.EditNote, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text(strings.editTagsAction) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_edit_tags")
                            )
                            FilterChip(
                                selected = false,
                                onClick = { showExtrasSheet = false; onOpenAudioCutter(track) },
                                label = { Text(strings.audioCutterTitle) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_audio_cutter")
                            )
                            FilterChip(
                                selected = settings.khushuModeEnabled,
                                onClick = { onToggleKhushuMode(!settings.khushuModeEnabled) },
                                label = { Text(strings.khushuModeTitle) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_khushu_toggle")
                            )
                            FilterChip(
                                selected = false,
                                onClick = { showExtrasSheet = false; onOpenTasbih() },
                                label = { Text(strings.tasbihTitle) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_tasbih_btn")
                            )
                            FilterChip(
                                selected = hasNote,
                                onClick = { showExtrasSheet = false; onOpenNote(track) },
                                label = { Text(strings.mediaNoteTitle) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_note_btn")
                            )
                            FilterChip(
                                selected = settings.driveModeEnabled,
                                onClick = { onToggleDriveMode(!settings.driveModeEnabled) },
                                leadingIcon = { Icon(Icons.Filled.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text(strings.driveModeTitle) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_drive_mode")
                            )
                            FilterChip(
                                selected = playbackState.isMuted,
                                onClick = onToggleMute,
                                leadingIcon = {
                                    Icon(
                                        if (playbackState.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(if (playbackState.isMuted) strings.unmuteAudioAction else strings.muteAudioAction) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_mute")
                            )
                            FilterChip(
                                selected = false,
                                onClick = {
                                    if (track.uriString.isNotBlank()) {
                                        shareMediaItem(context, track.title, track.uriString, track.mimeType)
                                    }
                                },
                                leadingIcon = { Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text(strings.menuShare) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_share")
                            )
                            FilterChip(
                                selected = isEnriching,
                                onClick = {
                                    showExtrasSheet = false
                                    onFetchCoverAndLyrics(track)
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(strings.fetchCoverAndLyricsAction) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_fetch_cover_lyrics")
                            )
                            if (onRequestMusicPip != null) {
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        showExtrasSheet = false
                                        onRequestMusicPip()
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Rounded.PictureInPictureAlt,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    label = { Text(strings.pipMusicTitle) },
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("full_player_pip_btn")
                                )
                            }
                        }
                    }

                    1 -> {
                        // Full 3-Page Pro DSP Studio Deck (Always expanded cleanly inside Tab 1!)
                        ProAudioDspStudioDeck(
                            expanded = true,
                            onToggleExpanded = { showExtrasSheet = false },
                            settings = settings,
                            playbackState = playbackState,
                            hifzRepeatTarget = hifzRepeatTarget,
                            strings = strings,
                            onPreampChange = onPreampChange,
                            onPitchChange = onPitchChange,
                            onCrossfadeChange = onCrossfadeChange,
                            onNormalizationChange = onNormalizationChange,
                            onStereoBalanceChange = onStereoBalanceChange,
                            onMonoChange = onMonoChange,
                            onSkipSilenceChange = onSkipSilenceChange,
                            onLectureModeChange = onLectureModeChange,
                            onHeadsetUnplugChange = onHeadsetUnplugChange,
                            onHifzRepeatChange = onHifzRepeatChange,
                            onSpectrumStyleChange = onSpectrumStyleChange,
                            onArtworkStyleChange = onArtworkStyleChange,
                            onSwipeArtToggle = onSwipeArtToggle,
                            onLyricsFontSizeChange = onLyricsFontSizeChange,
                            onReverbChange = onReverbChange,
                            onOpenAudioProfiles = { showExtrasSheet = false; onOpenAudioProfiles() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    else -> {
                        // Lyrics + Tap-Sync + Rich Artist & Knowledge Card
                        SyncedKaraokeLyricsCard(
                            lyricsText = lyricsText,
                            positionMsFlow = positionMsFlow,
                            durationMs = playbackState.durationMs.coerceAtLeast(1L),
                            strings = strings,
                            onSeekTo = onSeek,
                            onEditLyrics = { showEditLyricsDialog = true },
                            fontSizeSp = settings.lyricsFontSizeSp,
                            onChangeFontSize = onLyricsFontSizeChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            FilledTonalButton(
                                onClick = { showTapSyncDialog = true },
                                shape = CircleShape,
                                modifier = Modifier.testTag("full_player_tap_sync_btn")
                            ) {
                                Icon(Icons.Filled.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(strings.tapSyncLyricsTitle, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        RichArtistAndKnowledgeCard(
                            track = track,
                            report = knowledgeReport,
                            isEnriching = isEnriching,
                            showArtistPhoto = settings.autoFetchArtistPhotos,
                            strings = strings,
                            onRefreshKnowledge = onRefreshKnowledge,
                            onFetchOnlineLyrics = onFetchOnlineLyrics,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
            }
        }
    }

    if (showEditLyricsDialog) {
        var lyricsDraft by remember(track.id, lyricsText) { mutableStateOf(lyricsText.orEmpty()) }
        AlertDialog(
            onDismissRequest = { showEditLyricsDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    text = "${strings.editLyricsAction} • ${track.title}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            text = {
                OutlinedTextField(
                    value = lyricsDraft,
                    onValueChange = { lyricsDraft = it },
                    placeholder = { Text(strings.lyricsPlaceholder) },
                    minLines = 5,
                    maxLines = 10,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lyrics_editor_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveLyrics(track.id, lyricsDraft)
                        showEditLyricsDialog = false
                    },
                    shape = CircleShape,
                    modifier = Modifier.testTag("lyrics_save_button")
                ) {
                    Text(strings.save, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditLyricsDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }

    if (showTapSyncDialog) {
        val currentPosMs by positionMsFlow.collectAsStateWithLifecycle()
        TapToSyncLyricsDialog(
            trackTitle = track.title,
            initialRawLyrics = lyricsText.orEmpty(),
            currentPositionMs = currentPosMs,
            onDismiss = { showTapSyncDialog = false },
            onSaveSyncedLrc = { lrc -> onSaveLyrics(track.id, lrc) }
        )
    }

    if (showEqModal) {
        ModalBottomSheet(
            onDismissRequest = { showEqModal = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            EqualizerCard(
                gainsDb = settings.eqGainsDb,
                selectedPreset = settings.eqPreset,
                strings = strings,
                onSelectPreset = onSelectEqPreset,
                onBandChange = onBandChange,
                onReset = onResetEq,
                eqEnabled = settings.eqEnabled,
                bassBoost = settings.bassBoost,
                spectrumLevelsFlow = spectrumLevelsFlow,
                isPlaying = playbackState.isPlaying,
                onToggleEq = onToggleEq,
                onBassBoostChange = onBassBoostChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showTimerModal) {
        ModalBottomSheet(
            onDismissRequest = { showTimerModal = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            SleepTimerCard(
                remainingSec = playbackState.sleepTimerRemainingSec,
                totalMin = playbackState.sleepTimerTotalMin,
                strings = strings,
                onStartTimer = onStartSleepTimer,
                onCancelTimer = onCancelSleepTimer,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showQueueModal) {
        ModalBottomSheet(
            onDismissRequest = { showQueueModal = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            UpNextQueueSheetContent(
                tracks = queueTracks,
                currentTrackId = track.id,
                strings = strings,
                onSelectTrack = {
                    onPlayFromQueue(it)
                    showQueueModal = false
                },
                onMoveUp = onMoveQueueUp,
                onMoveDown = onMoveQueueDown,
                onRemoveFromQueue = onRemoveFromQueue,
                onSortQueue = onSortQueue
            )
        }
    }

    if (showBookmarksModal) {
        val currentPosMs by positionMsFlow.collectAsStateWithLifecycle()
        val trackBookmarks = remember(bookmarks, track.id) {
            bookmarks.filter { it.mediaId == track.id }.sortedBy { it.positionMs }
        }
        ModalBottomSheet(
            onDismissRequest = { showBookmarksModal = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            TrackBookmarksSheetContent(
                trackTitle = track.title,
                currentPositionMs = currentPosMs,
                bookmarks = trackBookmarks,
                strings = strings,
                onAddBookmark = { note -> onAddBookmark(track.id, currentPosMs, note) },
                onJumpToBookmark = { pos ->
                    onSeek(pos)
                    showBookmarksModal = false
                },
                onDeleteBookmark = onRemoveBookmark
            )
        }
    }
}

@Composable
private fun ProAudioDspStudioDeck(
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    settings: AppSettings,
    playbackState: PlaybackUiState,
    hifzRepeatTarget: Int,
    strings: AppStrings,
    onPreampChange: (Int) -> Unit,
    onPitchChange: (Float) -> Unit,
    onCrossfadeChange: (Int) -> Unit,
    onNormalizationChange: (Boolean) -> Unit,
    onStereoBalanceChange: (Float) -> Unit,
    onMonoChange: (Boolean) -> Unit,
    onSkipSilenceChange: (Boolean) -> Unit,
    onLectureModeChange: (Boolean) -> Unit,
    onHeadsetUnplugChange: (Boolean) -> Unit,
    onHifzRepeatChange: (Int) -> Unit,
    onSpectrumStyleChange: (SpectrumVisualStyle) -> Unit,
    onArtworkStyleChange: (PlayerArtworkStyle) -> Unit,
    onSwipeArtToggle: (Boolean) -> Unit,
    onLyricsFontSizeChange: (Int) -> Unit,
    onReverbChange: (ReverbEnvironment) -> Unit,
    onOpenAudioProfiles: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val scope = rememberCoroutineScope()

    MaterialLiquidGlassSurface(
        shape = RoundedCornerShape(26.dp),
        shadowElevation = 6.dp,
        tonalBoost = 1.2f,
        modifier = modifier.testTag("full_player_pro_dsp_deck")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpanded)
                    .testTag("pro_dsp_deck_header"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = strings.proAudioDeckTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.proAudioDeckSubtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = strings.collapseSectionAction,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Horizontal Sub-Page Selector Pills
                    val tabTitles = listOf(
                        strings.dspPageSound to Icons.Filled.GraphicEq,
                        strings.dspPageClarity to Icons.Filled.Hearing,
                        strings.dspPageHifz to Icons.Filled.RecordVoiceOver,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tabTitles.forEachIndexed { idx, (label, icon) ->
                            FilterChip(
                                selected = pagerState.currentPage == idx,
                                onClick = { scope.launch { pagerState.animateScrollToPage(idx) } },
                                leadingIcon = {
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(15.dp))
                                },
                                label = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                shape = CircleShape,
                                modifier = Modifier.testTag("pro_dsp_tab_$idx")
                            )
                        }
                    }

                    // Swipe Left/Right Horizontal Pager (compact, never vertically long!)
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                when (page) {
                                    0 -> {
                                        // Page 0: Live Sound Engine (Preamp 200%, Pitch, Crossfade, RMS Normalization)
                                        DspSliderRow(
                                            label = "${strings.preampBoostTitle}: ${settings.audioPreampPercent}%",
                                            value = settings.audioPreampPercent.toFloat(),
                                            range = 100f..200f,
                                            onValueChange = { onPreampChange(it.roundToInt()) }
                                        )
                                        DspSliderRow(
                                            label = "${strings.audioPitchTitle}: ${"%.2f".format(settings.audioPitch)}x",
                                            value = settings.audioPitch,
                                            range = 0.75f..1.50f,
                                            onValueChange = { onPitchChange((it * 20).roundToInt() / 20f) }
                                        )
                                        DspSliderRow(
                                            label = "${strings.crossfadeTitle}: ${settings.crossfadeSeconds}s",
                                            value = settings.crossfadeSeconds.toFloat(),
                                            range = 0f..12f,
                                            onValueChange = { onCrossfadeChange(it.roundToInt()) }
                                        )
                                        DspToggleRow(
                                            title = strings.audioNormalizationTitle,
                                            checked = settings.audioNormalization,
                                            onCheckedChange = onNormalizationChange,
                                            tag = "dsp_normalization_toggle"
                                        )
                                        Text(
                                            text = strings.reverbTitle,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            ReverbEnvironment.entries.forEach { env ->
                                                val lbl = when (env) {
                                                    ReverbEnvironment.NONE -> strings.reverbNone
                                                    ReverbEnvironment.STUDIO -> strings.reverbStudio
                                                    ReverbEnvironment.HALL -> strings.reverbHall
                                                    ReverbEnvironment.MOSQUE -> strings.reverbMosque
                                                    ReverbEnvironment.SPATIAL_3D -> strings.reverbSpatial3d
                                                }
                                                FilterChip(
                                                    selected = settings.reverbEnvironment == env,
                                                    onClick = { onReverbChange(env) },
                                                    label = { Text(lbl, style = MaterialTheme.typography.labelSmall) },
                                                    shape = CircleShape,
                                                    modifier = Modifier.testTag("dsp_reverb_${env.name.lowercase()}")
                                                )
                                            }
                                        }
                                        FilledTonalButton(
                                            onClick = onOpenAudioProfiles,
                                            shape = CircleShape,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("dsp_open_audio_profiles")
                                        ) {
                                            Icon(Icons.Filled.Hearing, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text(strings.audioProfilesTitle, style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                    1 -> {
                                        // Page 1: Clarity & Ears (Balance L/R, Mono, Skip Silence, Lecture Vocal Boost, Headset Unplug)
                                        val balLabel = when {
                                            settings.audioBalance < -0.05f -> "L ${(-settings.audioBalance * 100).roundToInt()}%"
                                            settings.audioBalance > 0.05f -> "R ${(settings.audioBalance * 100).roundToInt()}%"
                                            else -> "C (0%)"
                                        }
                                        DspSliderRow(
                                            label = "${strings.stereoBalanceTitle}: $balLabel",
                                            value = settings.audioBalance,
                                            range = -1f..1f,
                                            onValueChange = { onStereoBalanceChange((it * 20).roundToInt() / 20f) }
                                        )
                                        DspToggleRow(
                                            title = strings.audioMonoTitle,
                                            checked = settings.audioMonoEnabled,
                                            onCheckedChange = onMonoChange,
                                            tag = "dsp_mono_toggle"
                                        )
                                        DspToggleRow(
                                            title = strings.skipSilenceTitle,
                                            checked = settings.skipSilenceEnabled,
                                            onCheckedChange = onSkipSilenceChange,
                                            tag = "dsp_skip_silence_toggle"
                                        )
                                        DspToggleRow(
                                            title = strings.lectureModeTitle,
                                            checked = settings.lectureModeEnabled,
                                            onCheckedChange = onLectureModeChange,
                                            tag = "dsp_lecture_toggle"
                                        )
                                        DspToggleRow(
                                            title = strings.headsetUnplugPauseTitle,
                                            checked = settings.pauseOnHeadsetUnplug,
                                            onCheckedChange = onHeadsetUnplugChange,
                                            tag = "dsp_headset_toggle"
                                        )
                                    }
                                    else -> {
                                        // Page 2: Hifz Repeat Counter + Spectrum Visual + Artwork Style + Swipe Art
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = strings.hifzLoopTitle,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (playbackState.hifzRemainingLoops > 0) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                                ) {
                                                    Text(
                                                        text = strings.hifzRemainingLabel(playbackState.hifzRemainingLoops),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Row(
                                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(0 to "∞", 3 to "3×", 5 to "5×", 7 to "7×", 10 to "10×").forEach { (count, lbl) ->
                                                FilterChip(
                                                    selected = hifzRepeatTarget == count,
                                                    onClick = { onHifzRepeatChange(count) },
                                                    label = { Text(lbl, fontWeight = FontWeight.Bold) },
                                                    shape = CircleShape,
                                                    modifier = Modifier.testTag("dsp_hifz_$count")
                                                )
                                            }
                                        }

                                        Text(
                                            text = strings.spectrumStyleTitle,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf(
                                                SpectrumVisualStyle.CLASSIC_BARS to strings.spectrumClassicBars,
                                                SpectrumVisualStyle.WAVE_PULSE to strings.spectrumWavePulse,
                                                SpectrumVisualStyle.MINIMAL_DOTS to strings.spectrumMinimalDots,
                                            ).forEach { (style, lbl) ->
                                                FilterChip(
                                                    selected = settings.spectrumVisualStyle == style,
                                                    onClick = { onSpectrumStyleChange(style) },
                                                    label = { Text(lbl, style = MaterialTheme.typography.labelSmall) },
                                                    shape = CircleShape
                                                )
                                            }
                                        }

                                        Text(
                                            text = strings.playerArtworkStyleTitle,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            PlayerArtworkStyle.entries.forEach { artStyle ->
                                                FilterChip(
                                                    selected = settings.playerArtworkStyle == artStyle,
                                                    onClick = { onArtworkStyleChange(artStyle) },
                                                    label = {
                                                        Text(
                                                            text = strings.playerArtworkStyleName(artStyle),
                                                            style = MaterialTheme.typography.labelSmall
                                                        )
                                                    },
                                                    shape = CircleShape
                                                )
                                            }
                                        }

                                        DspToggleRow(
                                            title = strings.swipeArtworkTitle,
                                            checked = settings.playerGestureSwipeArt,
                                            onCheckedChange = onSwipeArtToggle,
                                            tag = "dsp_swipe_art_toggle"
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Page Indicator + Bottom Collapse Pill (never scroll up to close!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.swipeHintLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            onClick = onToggleExpanded,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.testTag("pro_dsp_collapse_bottom")
                        ) {
                            Text(
                                text = "▲ ${strings.collapseSectionAction}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DspSliderRow(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.height(34.dp)
        )
    }
}

@Composable
private fun DspToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun FullPlayerSpectrumLeaf(
    spectrumLevelsFlow: StateFlow<List<Float>>,
    isPlaying: Boolean,
    style: SpectrumVisualStyle = SpectrumVisualStyle.CLASSIC_BARS,
    accentOverride: Color = MaterialTheme.colorScheme.primary,
    secondaryOverride: Color = MaterialTheme.colorScheme.tertiary,
    onCycleStyle: () -> Unit = {},
) {
    val levels by spectrumLevelsFlow.collectAsStateWithLifecycle()
    val primary = accentOverride
    val tertiary = secondaryOverride

    Box(
        modifier = Modifier
            .widthIn(max = 360.dp)
            .fillMaxWidth()
            .height(34.dp)
            .clip(CircleShape)
            .clickable(onClick = onCycleStyle)
            .padding(horizontal = 20.dp)
            .testTag("full_player_spectrum_visualizer"),
        contentAlignment = Alignment.Center
    ) {
        when (style) {
            SpectrumVisualStyle.CLASSIC_BARS -> {
                LiveAudioSpectrumBars(
                    levels = levels,
                    isPlaying = isPlaying,
                    modifier = Modifier.fillMaxSize()
                )
            }
            SpectrumVisualStyle.WAVE_PULSE -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val count = 16
                    val stepX = size.width / (count - 1).coerceAtLeast(1)
                    val midY = size.height / 2f
                    val path = Path()
                    for (i in 0 until count) {
                        val lv = if (isPlaying) levels.getOrElse(i % levels.size.coerceAtLeast(1)) { 0.2f }.coerceIn(0.08f, 1f) else 0.1f
                        val sign = if (i % 2 == 0) -1f else 1f
                        val y = midY + sign * (lv * midY * 0.88f)
                        val x = i * stepX
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(listOf(primary, tertiary, primary)),
                        style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
            SpectrumVisualStyle.MINIMAL_DOTS -> {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val count = 12
                    val stepX = size.width / (count + 1)
                    val midY = size.height / 2f
                    for (i in 0 until count) {
                        val lv = if (isPlaying) levels.getOrElse(i % levels.size.coerceAtLeast(1)) { 0.2f }.coerceIn(0.1f, 1f) else 0.14f
                        val r = (3.dp.toPx() + lv * 6.dp.toPx())
                        drawCircle(
                            color = if (i % 2 == 0) primary else tertiary,
                            radius = r,
                            center = Offset((i + 1) * stepX, midY)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FullPlayerSeekLeaf(
    positionMsFlow: StateFlow<Long>,
    durationMs: Long,
    isPlaying: Boolean,
    squigglyEnabled: Boolean,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    onSeek: (Long) -> Unit,
) {
    val positionMs by positionMsFlow.collectAsStateWithLifecycle()
    val safeDur = durationMs.coerceAtLeast(1L)
    val progress = (positionMs.toFloat() / safeDur.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .widthIn(max = 560.dp)
            .fillMaxWidth()
    ) {
        SquigglyAudioProgressBar(
            progress = progress,
            isPlaying = isPlaying,
            squigglyEnabled = squigglyEnabled,
            onSeekFraction = { frac -> onSeek((frac * safeDur).toLong()) },
            showThumb = true,
            strokeWidth = 4.dp,
            waveAmplitudeDp = 3.5.dp,
            activeColor = activeColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .testTag("full_player_seek_slider")
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = MediaScanner.formatDuration(positionMs),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = MediaScanner.formatDuration(durationMs),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Isolated draw-phase-only spinning vinyl artwork leaf (0 recompositions/second).
 */
@Composable
private fun SpinningVinylDiscLeaf(
    track: AudioTrack,
    isPlaying: Boolean,
    accentTint: Color,
    deepTint: Color,
    modifier: Modifier = Modifier,
) {
    val angleState = if (isPlaying) {
        val infinite = rememberInfiniteTransition(label = "vinylLeafSpin")
        infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(16000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "vinylLeafAngle"
        )
    } else {
        null
    }

    Surface(
        shape = CircleShape,
        tonalElevation = 10.dp,
        shadowElevation = 16.dp,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(2.dp, accentTint.copy(alpha = 0.52f)),
        modifier = modifier.testTag("full_player_artwork_vinyl")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            TrackArtworkImage(
                track = track,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = angleState?.value ?: 0f
                    }
                    .clip(CircleShape)
            )
            Surface(
                shape = CircleShape,
                color = deepTint,
                border = BorderStroke(2.dp, accentTint.copy(alpha = 0.72f)),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(accentTint)
                    )
                }
            }
        }
    }
}

