package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Flare
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.RestorePage
import androidx.compose.material.icons.filled.RoundedCorner
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.localization.AppStrings
import com.example.model.AccentPalette
import com.example.model.AppLanguage
import com.example.model.AppSettings
import com.example.model.EqPreset
import com.example.model.IconPackStyle
import com.example.model.IntruderAttempt
import com.example.model.LiquidGlassStyle
import com.example.model.MaterialContrastLevel
import com.example.model.MaterialFontStyle
import com.example.model.NavTransitionStyle
import com.example.model.PlaybackUiState
import com.example.model.WatchedFolder
import com.example.player.DeviceDriverProfile
import com.example.player.DeviceMediaDriverEngine
import com.example.security.AppSecurityVerifier
import com.example.security.BiometricAuthHelper
import com.example.security.IntruderLogDialog
import com.example.security.SecurityAuditInfo
import com.example.security.SetupPinDialog
import com.example.ui.components.EqualizerCard
import com.example.ui.components.LocalLiquidGlassConfig
import com.example.ui.components.MaterialLiquidGlassSurface
import com.example.ui.components.PlayerBarCustomizerContent
import com.example.ui.components.SleepTimerCard
import com.example.ui.components.expressivePressScale
import com.example.ui.components.formatSecondsClock
import com.example.ui.components.scaledCornerDp
import com.example.ui.theme.LocalIconPackStyle
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.HeadsetOff
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.ViewSidebar
import androidx.compose.material.icons.filled.WbSunny
import com.example.model.AppIconVariant
import com.example.model.AudioOutputProfileType
import com.example.model.EdgeDockSide
import com.example.model.MediaTab
import com.example.model.NavBarStyle
import com.example.model.PlayerArtworkStyle
import com.example.model.ReverbEnvironment
import com.example.model.SettingsLayoutMode
import com.example.model.SlideshowTransitionStyle
import com.example.model.SpectrumVisualStyle
import com.example.model.SubtitleColorStyle
import com.example.model.SurfaceDepthStyle
import com.example.model.UiDensity
import com.example.ui.theme.palettePreviewColor
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

import com.example.ui.components.LiveAppearancePreviewCard
import com.example.ui.components.LiveFullPlayerCardPreview
import com.example.ui.components.LiveLockScreenPreviewCard
import com.example.ui.components.LiveMusicDspPreviewCard
import com.example.ui.components.LiveNavBarPreviewCard
import com.example.ui.components.LivePhotoGridPreviewCard
import com.example.ui.components.LiveVideoCinemaPreviewCard

private val AllSectionKeys = listOf(
    "studio_hub",
    "appearance",
    "player_card",
    "lock_screen",
    "nav_layout",
    "music",
    "video",
    "photos",
    "player_bar",
    "widgets",
    "library",
    "system",
    "taqwa_dua",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    playbackState: PlaybackUiState,
    securityAudit: SecurityAuditInfo,
    spectrumLevelsFlow: StateFlow<List<Float>>,
    strings: AppStrings,
    bottomContentPadding: Dp,
    onDarkThemeChange: (Boolean) -> Unit,
    onAmoledBlackChange: (Boolean) -> Unit,
    onPaletteChange: (AccentPalette) -> Unit,
    onIconStyleChange: (IconPackStyle) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onAutoPlayChange: (Boolean) -> Unit,
    onResumePositionChange: (Boolean) -> Unit,
    onFilterShortChange: (Boolean) -> Unit,
    onShowLyricsChange: (Boolean) -> Unit,
    onAutoPipChange: (Boolean) -> Unit,
    onVideoDefaultAspectChange: (Int) -> Unit,
    onVideoDefaultSpeedChange: (Float) -> Unit,
    onVideoDefaultLoopChange: (Boolean) -> Unit,
    onPhotoGridColumnsChange: (Int) -> Unit,
    onPhotoSlideshowSecondsChange: (Int) -> Unit,
    onPhotoShowTitlesChange: (Boolean) -> Unit,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onTamperGuardEnabledChange: (Boolean) -> Unit,
    intruderLog: List<IntruderAttempt> = emptyList(),
    onSetupPin: (String) -> Unit = {},
    onDisableAppLock: () -> Unit = {},
    onAppLockOnBackgroundChange: (Boolean) -> Unit = {},
    onScreenPrivacyChange: (Boolean) -> Unit = {},
    onClearIntruderLog: () -> Unit = {},
    onSquigglyProgressChange: (Boolean) -> Unit,
    onMiniPlayerCompactChange: (Boolean) -> Unit,
    onMiniPlayerFloatingChange: (Boolean) -> Unit,
    onToggleBarAddon: (String, Boolean) -> Unit,
    onEqEnabledChange: (Boolean) -> Unit,
    onBassBoostChange: (Float) -> Unit,
    onSelectEqPreset: (EqPreset) -> Unit,
    onResetEq: () -> Unit,
    onBandChange: (Int, Float) -> Unit,
    onStartSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    onSelectFolder: () -> Unit,
    onImportFiles: () -> Unit,
    onPickGallery: () -> Unit,
    onOpenStreamDialog: () -> Unit,
    onLoadDemoMedia: () -> Unit,
    onClearLibrary: () -> Unit,
    onPostBanner: (String) -> Unit,
    watchedFolders: List<WatchedFolder> = emptyList(),
    duplicateGroupsCount: Int = 0,
    onAutoScanWatchedFoldersChange: (Boolean) -> Unit = {},
    onOpenWatchedFoldersManager: () -> Unit = {},
    onOpenStorageInsights: () -> Unit = {},
    onLiquidGlassStyleChange: (LiquidGlassStyle) -> Unit = {},
    onLiquidGlassIntensityChange: (Float) -> Unit = {},
    onShapeRoundnessChange: (Float) -> Unit = {},
    onContrastLevelChange: (MaterialContrastLevel) -> Unit = {},
    onNavTransitionStyleChange: (NavTransitionStyle) -> Unit = {},
    onEdgeGlowEnabledChange: (Boolean) -> Unit = {},
    onAudioPreampChange: (Int) -> Unit = {},
    onAudioPitchChange: (Float) -> Unit = {},
    onCrossfadeSecondsChange: (Int) -> Unit = {},
    driverProfile: DeviceDriverProfile? = null,
    onFontStyleChange: (MaterialFontStyle) -> Unit = {},
    onHardwareVideoAccelChange: (Boolean) -> Unit = {},
    onRefreshDrivers: () -> Unit = {},
    onBiometricUnlockChange: (Boolean) -> Unit = {},
    onExportBackup: () -> Unit = {},
    onImportBackup: () -> Unit = {},
    onNavBarStyleChange: (NavBarStyle) -> Unit = {},
    onStartupTabChange: (MediaTab) -> Unit = {},
    onUiDensityChange: (UiDensity) -> Unit = {},
    onPlayerArtworkStyleChange: (PlayerArtworkStyle) -> Unit = {},
    onAlbumArtTintChange: (Boolean) -> Unit = {},
    onFullPlayerSpectrumChange: (Boolean) -> Unit = {},
    onSeekStepSecondsChange: (Int) -> Unit = {},
    onHapticsEnabledChange: (Boolean) -> Unit = {},
    onResetCustomization: () -> Unit = {},
    onAutoOnlineEnrichmentChange: (Boolean) -> Unit = {},
    onAutoFetchLyricsChange: (Boolean) -> Unit = {},
    onAutoFetchArtistPhotosChange: (Boolean) -> Unit = {},
    onClearKnowledgeCache: () -> Unit = {},
    onGaplessPlaybackChange: (Boolean) -> Unit = {},
    onAudioNormalizationChange: (Boolean) -> Unit = {},
    onPauseOnHeadsetUnplugChange: (Boolean) -> Unit = {},
    onVideoGesturesChange: (Boolean) -> Unit = {},
    onVideoDoubleTapSeekChange: (Boolean) -> Unit = {},
    onVideoRememberPositionChange: (Boolean) -> Unit = {},
    onVideoSubtitleSizeChange: (Int) -> Unit = {},
    onPhotoAutoShowExifChange: (Boolean) -> Unit = {},
    onPhotoHighResDecodeChange: (Boolean) -> Unit = {},
    onPhotoSlideshowTransitionChange: (SlideshowTransitionStyle) -> Unit = {},
    onSurfaceDepthStyleChange: (SurfaceDepthStyle) -> Unit = {},
    onKhushuModeChange: (Boolean) -> Unit = {},
    onLectureModeChange: (Boolean) -> Unit = {},
    onSkipSilenceChange: (Boolean) -> Unit = {},
    onAudioBalanceChange: (Float) -> Unit = {},
    onAudioMonoChange: (Boolean) -> Unit = {},
    onShakeToShuffleChange: (Boolean) -> Unit = {},
    onStripExifOnShareChange: (Boolean) -> Unit = {},
    onAppIconVariantChange: (AppIconVariant) -> Unit = {},
    onVideoSubtitleColorChange: (SubtitleColorStyle) -> Unit = {},
    onVideoSubtitleBgOpacityChange: (Float) -> Unit = {},
    onVideoSubtitleBottomOffsetChange: (Int) -> Unit = {},
    onOpenQuranHub: () -> Unit = {},
    onOpenMorningAlarm: () -> Unit = {},
    onOpenTasbih: () -> Unit = {},
    onOpenListeningStats: () -> Unit = {},
    onOpenPrivateVault: () -> Unit = {},
    onOpenCollageStudio: () -> Unit = {},
    onOpenWifiTransfer: () -> Unit = {},
    onOpenCustomSeed: () -> Unit = {},
    onOpenThemeCode: () -> Unit = {},
    onSettingsLayoutModeChange: (SettingsLayoutMode) -> Unit = {},
    onMiniPlayerEdgeDockSideChange: (EdgeDockSide) -> Unit = {},
    onSpectrumVisualStyleChange: (SpectrumVisualStyle) -> Unit = {},
    onLyricsFontSizeChange: (Int) -> Unit = {},
    onDriveModeChange: (Boolean) -> Unit = {},
    onPlayerGestureSwipeArtChange: (Boolean) -> Unit = {},
    onVideoMirrorChange: (Boolean) -> Unit = {},
    onVideoAutoHideSecondsChange: (Int) -> Unit = {},
    onApplyQuickThemePreset: (String) -> Unit = {},
    trashedCount: Int = 0,
    activeAudioOutput: AudioOutputProfileType = AudioOutputProfileType.SPEAKER,
    onReverbEnvironmentChange: (ReverbEnvironment) -> Unit = {},
    onOpenAudioProfiles: () -> Unit = {},
    onVideoAutoNextChange: (Boolean) -> Unit = {},
    onMoveNavTab: (MediaTab, Int) -> Unit = { _, _ -> },
    onResetNavTabOrder: () -> Unit = {},
    onHeaderShowStreamBtnChange: (Boolean) -> Unit = {},
    onHeaderShowMultiWindowBtnChange: (Boolean) -> Unit = {},
    onOpenRecycleBin: () -> Unit = {},
    onOpenGlobalSearch: () -> Unit = {},
    onOpenAdhkarStudio: () -> Unit = {},
    onOpenOnboardingGuide: () -> Unit = {},
    onPlayerGenreDynamicColorsChange: (Boolean) -> Unit = {},
    onPlayerShowTechTelemetryChange: (Boolean) -> Unit = {},
    onPlayerExtrasButtonSubtleChange: (Boolean) -> Unit = {},
    onPlayerDirectBitExactDspChange: (Boolean) -> Unit = {},
    onPlayerExpressivePlayMorphChange: (Boolean) -> Unit = {},
    onLockScreenControlsEnabledChange: (Boolean) -> Unit = {},
    onLockScreenShowArtworkChange: (Boolean) -> Unit = {},
    onLockScreenShowIdentityChange: (Boolean) -> Unit = {},
    onLockScreenFullScreenEnabledChange: (Boolean) -> Unit = {},
    onLockScreenShowClockChange: (Boolean) -> Unit = {},
    onLockScreenShowLyricsChange: (Boolean) -> Unit = {},
    onLockScreenBlurBackdropChange: (Boolean) -> Unit = {},
    onLockScreenCompactModeChange: (Boolean) -> Unit = {},
    onPreviewLockScreen: () -> Unit = {},
    onPipMusicEnabledChange: (Boolean) -> Unit = {},
    onPipVideoEnabledChange: (Boolean) -> Unit = {},
    onPipPhotoEnabledChange: (Boolean) -> Unit = {},
    onCardStyleChange: (com.example.model.MediaCardStyle) -> Unit = {},
    onBackdropStyleChange: (com.example.model.BackdropAnimationStyle) -> Unit = {},
    onMotionFeelChange: (com.example.model.MotionFeelStyle) -> Unit = {},
    onHeaderStyleChange: (com.example.model.HeaderTitleStyle) -> Unit = {},
    onSwipeGesturesChange: (Boolean) -> Unit = {},
    onShowFormatBadgesChange: (Boolean) -> Unit = {},
    onAccentStrengthChange: (Float) -> Unit = {},
    onOpenQuickCustomizer: () -> Unit = {},
    onOpenProAudioStudio: () -> Unit = {},
    onOpenDefaultPlayerSettings: () -> Unit = {},
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val screenProfile = remember(context) { AppSecurityVerifier.inspectScreenProfile(context) }
    val activeDriverProfile = driverProfile ?: remember(context) { DeviceMediaDriverEngine.probe(context) }
    var showSetupPinDialog by remember { mutableStateOf(false) }
    var showIntruderLogDialog by remember { mutableStateOf(false) }
    var settingsSearchQuery by remember { mutableStateOf("") }

    // Ordered Expandable Accordion state — all sections start cleanly collapsed so users never get lost!
    val expandedSections = remember {
        mutableStateMapOf(
            "studio_hub" to false,
            "appearance" to false,
            "player_card" to false,
            "lock_screen" to false,
            "nav_layout" to false,
            "music" to false,
            "video" to false,
            "photos" to false,
            "player_bar" to false,
            "widgets" to false,
            "library" to false,
            "system" to false,
            "taqwa_dua" to false,
        )
    }

    val normalizedQuery = settingsSearchQuery.trim().lowercase()
    fun matchesSection(vararg keywords: String): Boolean {
        if (normalizedQuery.isEmpty()) return true
        return keywords.any { it.lowercase().contains(normalizedQuery) }
    }
    fun isSectionExpanded(key: String): Boolean {
        return if (normalizedQuery.isNotEmpty()) true else (expandedSections[key] == true)
    }

    val allExpanded = AllSectionKeys.all { expandedSections[it] == true }
    val anySectionExpanded = AllSectionKeys.any { expandedSections[it] == true }
    val isPureMaterialYou = settings.liquidGlassStyle == LiquidGlassStyle.SOLID_M3

    val settingsSectionSpacing = when (com.example.ui.theme.LocalUiDensity.current) {
        UiDensity.COMFORTABLE -> 16.dp
        UiDensity.BALANCED -> 12.dp
        UiDensity.COMPACT -> 8.dp
    }

    Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 6.dp,
            bottom = bottomContentPadding + 48.dp
        ),
        verticalArrangement = Arrangement.spacedBy(settingsSectionSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Clean, Self-Contained Settings Header Card (Search + Layout + Expand/Collapse + Version + Pro Studio)
        item(key = "settings_accordion_toolbar") {
            Surface(
                shape = RoundedCornerShape(scaledCornerDp(28.dp, settings.shapeRoundness)),
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.42f)),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = settingsSearchQuery,
                        onValueChange = { settingsSearchQuery = it },
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = strings.settingsSearchHint,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = strings.settingsSearchHint,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (settingsSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { settingsSearchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = strings.close
                                    )
                                }
                            }
                        },
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_search_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isHorizontal = settings.settingsLayoutMode == SettingsLayoutMode.HORIZONTAL_PAGES
                        val layoutInteraction = remember { MutableInteractionSource() }
                        Surface(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSettingsLayoutModeChange(
                                    if (isHorizontal) SettingsLayoutMode.VERTICAL_LIST
                                    else SettingsLayoutMode.HORIZONTAL_PAGES
                                )
                            },
                            interactionSource = layoutInteraction,
                            shape = CircleShape,
                            color = if (isHorizontal) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            },
                            border = BorderStroke(
                                1.dp,
                                if (isHorizontal) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                                else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .expressivePressScale(layoutInteraction, pressedScale = 0.94f)
                                .testTag("settings_layout_mode_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (isHorizontal) Icons.Filled.SwapHoriz else Icons.Filled.ViewList,
                                    contentDescription = strings.settingsLayoutModeTitle,
                                    tint = if (isHorizontal) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = if (isHorizontal) strings.settingsLayoutHorizontal else strings.settingsLayoutVertical,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHorizontal) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        val toggleAllInteraction = remember { MutableInteractionSource() }
                        Surface(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                val target = !allExpanded
                                AllSectionKeys.forEach { expandedSections[it] = target }
                            },
                            interactionSource = toggleAllInteraction,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .weight(1f)
                                .expressivePressScale(toggleAllInteraction, pressedScale = 0.94f)
                                .testTag("settings_toggle_all_sections")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (allExpanded) Icons.Filled.UnfoldLess else Icons.Filled.UnfoldMore,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = if (allExpanded) strings.collapseAllSections else strings.expandAllSections,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = onOpenProAudioStudio,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.42f)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_quick_pro_audio_studio")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = strings.proStudioTitle,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = strings.proStudioTitle,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f)),
                            modifier = Modifier.testTag("settings_version_badge")
                        ) {
                            Text(
                                text = strings.appVersionLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Prism Studio & Tools Hub — neatly contained inside its own Collapsible Section Card!
        if (matchesSection(
                strings.settingsStudioHubSectionTitle,
                strings.quranHubTitle,
                strings.morningAlarmTitle,
                strings.tasbihTitle,
                strings.listeningStatsTitle,
                strings.privateVaultTitle,
                strings.collageStudioTitle,
                strings.wifiTransferTitle,
                strings.customSeedTitle,
                strings.themeCodeTitle,
                "quran", "khatma", "tasbih", "alarm", "vault", "collage", "wifi", "stats", "seed", "theme", "قرآن", "ختمة", "تسبيح", "خزنة", "كولاج", "واي فاي", "إحصائيات"
            )
        ) {
            item(key = "sec_studio_hub") {
                ExpandableSettingsSection(
                    title = strings.settingsStudioHubSectionTitle,
                    subtitle = strings.settingsStudioHubSectionSubtitle,
                    icon = Icons.Filled.AutoAwesome,
                    expanded = isSectionExpanded("studio_hub"),
                    strings = strings,
                    tag = "settings_studio_hub",
                    onToggle = { expandedSections["studio_hub"] = !(expandedSections["studio_hub"] ?: false) }
                ) {
                    val hubEntries = listOf(
                        Triple(Icons.Filled.Search, strings.globalSearchTitle, onOpenGlobalSearch),
                        Triple(Icons.Filled.MenuBook, strings.quranHubTitle, onOpenQuranHub),
                        Triple(Icons.Filled.WbSunny, strings.adhkarTitle, onOpenAdhkarStudio),
                        Triple(Icons.Filled.AccessAlarm, strings.morningAlarmTitle, onOpenMorningAlarm),
                        Triple(Icons.Filled.AutoAwesome, strings.tasbihTitle, onOpenTasbih),
                        Triple(Icons.Filled.Headphones, strings.audioProfilesTitle, onOpenAudioProfiles),
                        Triple(Icons.Filled.DeleteOutline, "${strings.recycleBinTitle} ($trashedCount)", onOpenRecycleBin),
                        Triple(Icons.Filled.BarChart, strings.listeningStatsTitle, onOpenListeningStats),
                        Triple(Icons.Filled.Shield, strings.privateVaultTitle, onOpenPrivateVault),
                        Triple(Icons.Filled.GridView, strings.collageStudioTitle, onOpenCollageStudio),
                        Triple(Icons.Filled.Wifi, strings.wifiTransferTitle, onOpenWifiTransfer),
                        Triple(Icons.Filled.ColorLens, strings.customSeedTitle, onOpenCustomSeed),
                        Triple(Icons.Filled.QrCode2, strings.themeCodeTitle, onOpenThemeCode),
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        hubEntries.forEachIndexed { idx, (icon, label, action) ->
                            Surface(
                                onClick = action,
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.82f),
                                modifier = Modifier.testTag("studio_hub_btn_$idx")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. Appearance, Optional True Material You vs Liquid Glass, Dynamic Wallpaper, Icons & Typography
        if (matchesSection(
                strings.settingsAppearance,
                strings.visualEngineTitle,
                strings.visualEngineMaterialYouTitle,
                strings.visualEngineLiquidGlassTitle,
                strings.darkMode,
                strings.dynamicPalette,
                strings.iconStyleTitle,
                strings.fontStyleTitle,
                strings.fontSizeTitle,
                strings.shapeRoundnessTitle,
                strings.contrastLevelTitle,
                strings.edgeGlowTitle,
                "theme", "color", "glass", "material", "font", "icon", "زجاج", "ماتيريال", "لون", "خط", "أيقونة"
            )
        ) {
        item(key = "sec_appearance") {
            val modeSummary = if (isPureMaterialYou) {
                strings.visualEngineMaterialYouTitle
            } else {
                strings.visualEngineLiquidGlassTitle
            }
            ExpandableSettingsSection(
                title = strings.settingsAppearance,
                subtitle = "$modeSummary • ${strings.paletteName(settings.accentPalette)}",
                icon = Icons.Filled.Palette,
                expanded = isSectionExpanded("appearance"),
                strings = strings,
                tag = "section_appearance",
                onToggle = { expandedSections["appearance"] = !(expandedSections["appearance"] ?: false) }
            ) {
                LiveAppearancePreviewCard(
                    settings = settings,
                    strings = strings,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                FilledTonalButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onOpenQuickCustomizer()
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("settings_open_quick_studio")
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.quickStudioTitle, fontWeight = FontWeight.Bold)
                }
                SettingsPagedDeck(
                    layoutMode = settings.settingsLayoutMode,
                    strings = strings,
                    tagPrefix = "appearance_deck",
                    pages = listOf(
                        strings.visualEngineTitle to {
                            QuickAestheticPresetsRow(
                                strings = strings,
                                onApplyPreset = { key ->
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onApplyQuickThemePreset(key)
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                // Visual Identity Mode Selector: True Material You vs Liquid Glass (Both 100% Optional!)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.visualEngineTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.visualEngineSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val m3Interaction = remember { MutableInteractionSource() }
                        Surface(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onLiquidGlassStyleChange(LiquidGlassStyle.SOLID_M3)
                            },
                            interactionSource = m3Interaction,
                            shape = RoundedCornerShape(22.dp),
                            color = if (isPureMaterialYou) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            },
                            border = BorderStroke(
                                width = if (isPureMaterialYou) 2.dp else 1.dp,
                                color = if (isPureMaterialYou) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .expressivePressScale(m3Interaction, pressedScale = 0.96f)
                                .testTag("visual_mode_material_you")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Palette,
                                        contentDescription = null,
                                        tint = if (isPureMaterialYou) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    if (isPureMaterialYou) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = strings.visualEngineMaterialYouTitle,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPureMaterialYou) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = strings.visualEngineMaterialYouDesc,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isPureMaterialYou) {
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }

                        val glassSelected = !isPureMaterialYou
                        val glassInteraction = remember { MutableInteractionSource() }
                        Surface(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                if (isPureMaterialYou) {
                                    onLiquidGlassStyleChange(LiquidGlassStyle.MATERIAL_LIQUID)
                                }
                            },
                            interactionSource = glassInteraction,
                            shape = RoundedCornerShape(22.dp),
                            color = if (glassSelected) {
                                MaterialTheme.colorScheme.tertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            },
                            border = BorderStroke(
                                width = if (glassSelected) 2.dp else 1.dp,
                                color = if (glassSelected) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .expressivePressScale(glassInteraction, pressedScale = 0.96f)
                                .testTag("visual_mode_liquid_glass")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.BlurOn,
                                        contentDescription = null,
                                        tint = if (glassSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    if (glassSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = strings.visualEngineLiquidGlassTitle,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (glassSelected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = strings.visualEngineLiquidGlassDesc,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (glassSelected) {
                                        MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }

                    // Live Real-Time Visual Engine Preview
                    MaterialLiquidGlassSurface(
                        shape = RoundedCornerShape(scaledCornerDp(20.dp, settings.shapeRoundness)),
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("visual_mode_live_preview")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(scaledCornerDp(14.dp, settings.shapeRoundness)),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.MusicNote,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = strings.liveThemePreviewLabel,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "$modeSummary • ${strings.fontStyleName(settings.fontStyle)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = strings.paletteName(settings.accentPalette),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.DarkMode,
                    title = strings.darkMode,
                    subtitle = strings.darkModeDesc,
                    checked = settings.darkTheme,
                    onCheckedChange = onDarkThemeChange,
                    testTag = "setting_dark_mode"
                )

                if (settings.darkTheme) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    SettingsToggleRow(
                        icon = Icons.Filled.Contrast,
                        title = strings.amoledBlack,
                        subtitle = strings.amoledBlackDesc,
                        checked = settings.amoledBlack,
                        onCheckedChange = onAmoledBlackChange,
                        testTag = "setting_amoled_black"
                    )
                }
                        },
                        strings.dynamicPalette to {
                // Material You Accent Palette Swatches (System Dynamic Wallpaper promoted first!)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = strings.dynamicPalette,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (settings.accentPalette == AccentPalette.SYSTEM) {
                            Text(
                                text = strings.wallpaperDynamicHint,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val orderedPalettes = remember {
                    listOf(AccentPalette.SYSTEM) + AccentPalette.entries.filterNot { it == AccentPalette.SYSTEM }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    orderedPalettes.forEach { palette ->
                        val selected = settings.accentPalette == palette
                        val swatch = if (palette == AccentPalette.SYSTEM) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            palettePreviewColor(palette)
                        }
                        val swatchInteraction = remember { MutableInteractionSource() }
                        Surface(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onPaletteChange(palette)
                            },
                            interactionSource = swatchInteraction,
                            shape = CircleShape,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            },
                            border = if (selected) {
                                BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            } else null,
                            modifier = Modifier
                                .expressivePressScale(swatchInteraction, pressedScale = 0.93f)
                                .testTag("palette_${palette.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(swatch),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = strings.paletteName(palette),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Style,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.iconStyleTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconPackStyle.entries.forEach { style ->
                        val selected = settings.iconStyle == style
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onIconStyleChange(style)
                            },
                            label = {
                                Text(
                                    text = strings.iconStyleName(style),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("icon_style_${style.name.lowercase()}")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                // Authentic Bundled Material Typography Font Packs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FontDownload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.fontStyleTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MaterialFontStyle.entries.forEach { fontPack ->
                        val selected = settings.fontStyle == fontPack
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onFontStyleChange(fontPack)
                            },
                            label = {
                                Text(
                                    text = strings.fontStyleName(fontPack),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("font_style_${fontPack.name.lowercase()}")
                        )
                    }
                }
                        },
                        strings.fontSizeTitle to {
                // Real Global Font Size / Typography Scaling Control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.FormatSize,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.fontSizeTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${(settings.fontScaleMultiplier * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val fontPresets = listOf(
                    0.88f to strings.fontSizeSmall,
                    1.00f to strings.fontSizeDefault,
                    1.12f to strings.fontSizeLarge,
                    1.25f to strings.fontSizeXL,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    fontPresets.forEach { (scale, label) ->
                        val selected = kotlin.math.abs(settings.fontScaleMultiplier - scale) < 0.03f
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onFontScaleChange(scale)
                            },
                            label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("font_preset_${(scale * 100).roundToInt()}")
                        )
                    }
                }

                Slider(
                    value = settings.fontScaleMultiplier,
                    onValueChange = onFontScaleChange,
                    valueRange = 0.85f..1.30f,
                    steps = 8,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("font_scale_slider")
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Material You Liquid Glass Engine (Authentic Google M3 Tonal Glass)
                // Optional Liquid Glass Fine-Tuning (automatically hidden when True Material You is active)
                AnimatedVisibility(
                    visible = !isPureMaterialYou,
                    enter = expandVertically(spring(dampingRatio = 0.85f)) + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.BlurOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.liquidGlassTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = strings.liquidGlassSubtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                LiquidGlassStyle.MATERIAL_LIQUID,
                                LiquidGlassStyle.CRYSTAL_TONAL,
                                LiquidGlassStyle.VELVET_FROST
                            ).forEach { style ->
                                val selected = settings.liquidGlassStyle == style
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onLiquidGlassStyleChange(style)
                                    },
                                    label = {
                                        Text(
                                            text = strings.liquidGlassStyleName(style),
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    },
                                    shape = CircleShape,
                                    modifier = Modifier.testTag("liquid_glass_style_${style.name.lowercase()}")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Glass Intensity Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = strings.liquidGlassIntensityTitle,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                                Text(
                                    text = "${(settings.liquidGlassIntensity * 100).roundToInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Slider(
                            value = settings.liquidGlassIntensity,
                            onValueChange = onLiquidGlassIntensityChange,
                            valueRange = 0f..1f,
                            steps = 9,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("liquid_glass_intensity_slider")
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                // M3 Expressive Shape Roundness Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.RoundedCorner,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = strings.shapeRoundnessTitle,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(
                            text = "${(settings.shapeRoundness * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
                Slider(
                    value = settings.shapeRoundness,
                    onValueChange = onShapeRoundnessChange,
                    valueRange = 0.6f..1.4f,
                    steps = 7,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shape_roundness_slider")
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Material You Contrast Level
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Contrast,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.contrastLevelTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MaterialContrastLevel.entries.forEach { level ->
                        val selected = settings.contrastLevel == level
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onContrastLevelChange(level)
                            },
                            label = {
                                Text(
                                    text = strings.contrastLevelName(level),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("contrast_level_${level.name.lowercase()}")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Surface Elevation & Depth Style (Flat / Balanced / Expressive)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Layers,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.surfaceDepthTitle,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SurfaceDepthStyle.entries.forEach { depth ->
                        val selected = settings.surfaceDepthStyle == depth
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSurfaceDepthStyleChange(depth)
                            },
                            label = {
                                Text(
                                    text = strings.surfaceDepthName(depth),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("surface_depth_${depth.name.lowercase()}")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Audio-Reactive Edge Glow
                SettingsToggleRow(
                    icon = Icons.Filled.Flare,
                    title = strings.edgeGlowTitle,
                    subtitle = strings.edgeGlowDesc,
                    checked = settings.edgeGlowEnabled,
                    onCheckedChange = onEdgeGlowEnabledChange,
                    testTag = "setting_edge_glow"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Custom Monet Seed Color Picker & Shareable Theme Code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onOpenCustomSeed,
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("setting_custom_seed_btn")
                    ) {
                        Icon(Icons.Filled.ColorLens, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.customSeedTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    OutlinedButton(
                        onClick = onOpenThemeCode,
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("setting_theme_code_btn")
                    ) {
                        Icon(Icons.Filled.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.themeCodeTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Dynamic Adaptive App Icon Switcher (5 variants)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AppShortcut, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = strings.appIconSwitcherTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = strings.appIconSwitcherSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppIconVariant.entries.forEach { variant ->
                        val selected = settings.appIconVariant == variant
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onAppIconVariantChange(variant)
                            },
                            label = {
                                Text(
                                    text = variant.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("app_icon_${variant.name.lowercase()}")
                        )
                    }
                }
                        },
                        strings.cardStyleTitle to {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                // Media Card Style
                                Text(
                                    text = strings.cardStyleTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    com.example.model.MediaCardStyle.entries.forEach { style ->
                                        FilterChip(
                                            selected = settings.cardStyle == style,
                                            onClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onCardStyleChange(style)
                                            },
                                            label = { Text(strings.cardStyleName(style)) },
                                            shape = CircleShape,
                                            modifier = Modifier.testTag("setting_card_style_${style.name.lowercase()}")
                                        )
                                    }
                                }

                                HorizontalDivider()

                                // Ambient Backdrop Style
                                Text(
                                    text = strings.backdropStyleTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    com.example.model.BackdropAnimationStyle.entries.forEach { style ->
                                        FilterChip(
                                            selected = settings.backdropStyle == style,
                                            onClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onBackdropStyleChange(style)
                                            },
                                            label = { Text(strings.backdropStyleName(style)) },
                                            shape = CircleShape,
                                            modifier = Modifier.testTag("setting_backdrop_${style.name.lowercase()}")
                                        )
                                    }
                                }

                                HorizontalDivider()

                                // Touch & Motion Physics
                                Text(
                                    text = strings.motionFeelTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    com.example.model.MotionFeelStyle.entries.forEach { feel ->
                                        FilterChip(
                                            selected = settings.motionFeelStyle == feel,
                                            onClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onMotionFeelChange(feel)
                                            },
                                            label = { Text(strings.motionFeelName(feel)) },
                                            shape = CircleShape,
                                            modifier = Modifier.testTag("setting_motion_${feel.name.lowercase()}")
                                        )
                                    }
                                }

                                HorizontalDivider()

                                // Top Header Style
                                Text(
                                    text = strings.headerStyleTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    com.example.model.HeaderTitleStyle.entries.forEach { style ->
                                        FilterChip(
                                            selected = settings.headerTitleStyle == style,
                                            onClick = {
                                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                onHeaderStyleChange(style)
                                            },
                                            label = { Text(strings.headerStyleName(style)) },
                                            shape = CircleShape,
                                            modifier = Modifier.testTag("setting_header_${style.name.lowercase()}")
                                        )
                                    }
                                }

                                HorizontalDivider()

                                // Color Vibrancy Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = strings.accentStrengthTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${(settings.accentStrength * 100).roundToInt()}%",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Slider(
                                    value = settings.accentStrength,
                                    onValueChange = onAccentStrengthChange,
                                    valueRange = 0.5f..1.4f,
                                    modifier = Modifier.testTag("setting_accent_strength_slider")
                                )

                                HorizontalDivider()

                                SettingsToggleRow(
                                    icon = Icons.Filled.TouchApp,
                                    title = strings.swipeGesturesTitle,
                                    subtitle = strings.swipeGesturesDesc,
                                    checked = settings.swipeGesturesEnabled,
                                    onCheckedChange = onSwipeGesturesChange,
                                    testTag = "setting_swipe_gestures"
                                )

                                SettingsToggleRow(
                                    icon = Icons.Filled.Style,
                                    title = strings.showFormatBadgesTitle,
                                    subtitle = strings.showFormatBadgesDesc,
                                    checked = settings.showFormatBadges,
                                    onCheckedChange = onShowFormatBadgesChange,
                                    testTag = "setting_show_format_badges"
                                )
                            }
                        }
                    )
                )
            }
        }
        }

        // 1B. Dedicated Full-Screen Player Card & Pro Audio Technologies Section (with Live Interactive Preview!)
        if (matchesSection(
                strings.settingsPlayerCard,
                strings.settingsPlayerCardDesc,
                strings.playerArtworkStyleTitle,
                strings.playerGenreColorsTitle,
                strings.playerTechTelemetryTitle,
                strings.playerExtrasSubtleTitle,
                strings.playerBitExactDspTitle,
                strings.playerExpressivePlayMorphTitle,
                "player", "card", "expressive", "cookie", "genre", "dsp", "bit", "telemetry", "مشغل", "بطاقة", "تقنيات", "اكسبريسيف", "ألوان"
            )
        ) {
            item(key = "sec_player_card") {
                ExpandableSettingsSection(
                    title = strings.settingsPlayerCard,
                    subtitle = "${strings.playerArtworkStyleName(settings.playerArtworkStyle)} • ${if (settings.playerDirectBitExactDsp) "Bit-Exact" else "32-bit DSP"}",
                    icon = Icons.Filled.Album,
                    expanded = isSectionExpanded("player_card"),
                    strings = strings,
                    tag = "section_player_card",
                    onToggle = { expandedSections["player_card"] = !(expandedSections["player_card"] ?: false) }
                ) {
                    LiveFullPlayerCardPreview(
                        settings = settings,
                        strings = strings,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    SettingsPagedDeck(
                        layoutMode = settings.settingsLayoutMode,
                        strings = strings,
                        tagPrefix = "player_card_deck",
                        pages = listOf(
                            strings.playerArtworkStyleTitle to {
                                Text(
                                    text = strings.playerArtworkStyleTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    PlayerArtworkStyle.entries.forEach { style ->
                                        FilterChip(
                                            selected = settings.playerArtworkStyle == style,
                                            onClick = { onPlayerArtworkStyleChange(style) },
                                            label = {
                                                Text(
                                                    text = strings.playerArtworkStyleName(style),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            },
                                            shape = CircleShape,
                                            modifier = Modifier.testTag("player_card_shape_${style.name.lowercase()}")
                                        )
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.Palette,
                                    title = strings.playerGenreColorsTitle,
                                    subtitle = strings.playerGenreColorsDesc,
                                    checked = settings.playerGenreDynamicColors,
                                    onCheckedChange = onPlayerGenreDynamicColorsChange,
                                    testTag = "settings_player_genre_colors"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.AutoAwesome,
                                    title = strings.albumArtTintTitle,
                                    subtitle = strings.albumArtTintDesc,
                                    checked = settings.albumArtTintEnabled,
                                    onCheckedChange = onAlbumArtTintChange,
                                    testTag = "settings_player_album_tint"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.Tune,
                                    title = strings.playerExtrasSubtleTitle,
                                    subtitle = strings.playerExtrasSubtleDesc,
                                    checked = settings.playerExtrasButtonSubtle,
                                    onCheckedChange = onPlayerExtrasButtonSubtleChange,
                                    testTag = "settings_player_extras_subtle"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.PlayArrow,
                                    title = strings.playerExpressivePlayMorphTitle,
                                    subtitle = strings.settingsPlayerCardDesc,
                                    checked = settings.playerExpressivePlayMorph,
                                    onCheckedChange = onPlayerExpressivePlayMorphChange,
                                    testTag = "settings_player_play_morph"
                                )
                            },
                            strings.proAudioDeckTitle to {
                                SettingsToggleRow(
                                    icon = Icons.Rounded.Memory,
                                    title = strings.playerTechTelemetryTitle,
                                    subtitle = strings.playerTechTelemetryDesc,
                                    checked = settings.playerShowTechTelemetry,
                                    onCheckedChange = onPlayerShowTechTelemetryChange,
                                    testTag = "settings_player_tech_telemetry"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.HighQuality,
                                    title = strings.playerBitExactDspTitle,
                                    subtitle = strings.playerBitExactDspDesc,
                                    checked = settings.playerDirectBitExactDsp,
                                    onCheckedChange = onPlayerDirectBitExactDspChange,
                                    testTag = "settings_player_bit_exact"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.GraphicEq,
                                    title = strings.fullPlayerSpectrumTitle,
                                    subtitle = strings.fullPlayerSpectrumDesc,
                                    checked = settings.showFullPlayerSpectrum,
                                    onCheckedChange = onFullPlayerSpectrumChange,
                                    testTag = "settings_player_card_spectrum"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.Flare,
                                    title = strings.edgeGlowTitle,
                                    subtitle = strings.edgeGlowDesc,
                                    checked = settings.edgeGlowEnabled,
                                    onCheckedChange = onEdgeGlowEnabledChange,
                                    testTag = "settings_player_card_edge_glow"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.TouchApp,
                                    title = strings.swipeArtworkTitle,
                                    subtitle = strings.swipeArtworkDesc,
                                    checked = settings.playerGestureSwipeArt,
                                    onCheckedChange = onPlayerGestureSwipeArtChange,
                                    testTag = "settings_player_card_swipe_art"
                                )
                            },
                        )
                    )
                }
            }
        }

        // 1C. Dedicated Lock-Screen Identity & Playback Customization Section (with Live Phone Preview!)
        if (matchesSection(
                strings.settingsLockScreen,
                strings.settingsLockScreenDesc,
                strings.lockScreenControlsTitle,
                strings.lockScreenShowIdentityTitle,
                strings.lockScreenFullScreenTitle,
                strings.lockScreenShowClockTitle,
                strings.lockScreenShowLyricsTitle,
                "lock", "screen", "identity", "clock", "قفل", "شاشة القفل", "هوية", "ساعة"
            )
        ) {
            item(key = "sec_lock_screen") {
                ExpandableSettingsSection(
                    title = strings.settingsLockScreen,
                    subtitle = if (settings.lockScreenControlsEnabled) {
                        "${strings.lockScreenShowIdentityTitle} • ${if (settings.lockScreenFullScreenEnabled) "Full Mode" else "System Card"}"
                    } else strings.lockScreenControlsTitle,
                    icon = Icons.Filled.Lock,
                    expanded = isSectionExpanded("lock_screen"),
                    strings = strings,
                    tag = "section_lock_screen",
                    onToggle = { expandedSections["lock_screen"] = !(expandedSections["lock_screen"] ?: false) }
                ) {
                    LiveLockScreenPreviewCard(
                        settings = settings,
                        strings = strings,
                        onPreviewFullLockScreen = onPreviewLockScreen,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    SettingsPagedDeck(
                        layoutMode = settings.settingsLayoutMode,
                        strings = strings,
                        tagPrefix = "lock_screen_deck",
                        pages = listOf(
                            strings.lockScreenControlsTitle to {
                                SettingsToggleRow(
                                    icon = Icons.Filled.Lock,
                                    title = strings.lockScreenControlsTitle,
                                    subtitle = strings.lockScreenControlsDesc,
                                    checked = settings.lockScreenControlsEnabled,
                                    onCheckedChange = onLockScreenControlsEnabledChange,
                                    testTag = "settings_lock_controls"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.Verified,
                                    title = strings.lockScreenShowIdentityTitle,
                                    subtitle = strings.lockScreenShowIdentityDesc,
                                    checked = settings.lockScreenShowPrismIdentity,
                                    onCheckedChange = onLockScreenShowIdentityChange,
                                    testTag = "settings_lock_identity"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.Smartphone,
                                    title = strings.lockScreenFullScreenTitle,
                                    subtitle = strings.lockScreenFullScreenDesc,
                                    checked = settings.lockScreenFullScreenEnabled,
                                    onCheckedChange = onLockScreenFullScreenEnabledChange,
                                    testTag = "settings_lock_full_screen"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.PhotoLibrary,
                                    title = strings.lockScreenShowArtworkTitle,
                                    subtitle = strings.settingsLockScreenDesc,
                                    checked = settings.lockScreenShowArtwork,
                                    onCheckedChange = onLockScreenShowArtworkChange,
                                    testTag = "settings_lock_artwork"
                                )
                            },
                            strings.lockScreenShowClockTitle to {
                                SettingsToggleRow(
                                    icon = Icons.Filled.AccessAlarm,
                                    title = strings.lockScreenShowClockTitle,
                                    subtitle = strings.settingsLockScreenDesc,
                                    checked = settings.lockScreenShowClock,
                                    onCheckedChange = onLockScreenShowClockChange,
                                    testTag = "settings_lock_clock"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.Lyrics,
                                    title = strings.lockScreenShowLyricsTitle,
                                    subtitle = strings.settingsLockScreenDesc,
                                    checked = settings.lockScreenShowLyrics,
                                    onCheckedChange = onLockScreenShowLyricsChange,
                                    testTag = "settings_lock_lyrics"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.BlurOn,
                                    title = strings.lockScreenBlurBackdropTitle,
                                    subtitle = strings.settingsLockScreenDesc,
                                    checked = settings.lockScreenBlurBackdrop,
                                    onCheckedChange = onLockScreenBlurBackdropChange,
                                    testTag = "settings_lock_blur"
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                SettingsToggleRow(
                                    icon = Icons.Filled.ViewAgenda,
                                    title = strings.lockScreenCompactModeTitle,
                                    subtitle = strings.settingsLockScreenDesc,
                                    checked = settings.lockScreenCompactMode,
                                    onCheckedChange = onLockScreenCompactModeChange,
                                    testTag = "settings_lock_compact"
                                )
                            },
                        )
                    )
                }
            }
        }

        // 2. Navigation & Interface Layout Customization (Nav Bar Style, Startup Tab, UI Density, Transitions, Haptics)
        if (matchesSection(
                strings.settingsNavLayoutSection,
                strings.navBarStyleTitle,
                strings.startupTabTitle,
                strings.uiDensityTitle,
                strings.navTransitionTitle,
                strings.hapticsTitle,
                "nav", "layout", "dock", "pill", "density", "haptic", "transition", "تنقل", "تخطيط", "كثافة", "اهتزاز", "شريط"
            )
        ) {
        item(key = "sec_nav_layout") {
            ExpandableSettingsSection(
                title = strings.settingsNavLayoutSection,
                subtitle = "${strings.navBarStyleName(settings.navBarStyle)} • ${strings.uiDensityName(settings.uiDensity)}",
                icon = Icons.Filled.DashboardCustomize,
                expanded = isSectionExpanded("nav_layout"),
                strings = strings,
                tag = "section_nav_layout",
                onToggle = { expandedSections["nav_layout"] = !(expandedSections["nav_layout"] ?: false) }
            ) {
                LiveNavBarPreviewCard(
                    settings = settings,
                    strings = strings,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                SettingsPagedDeck(
                    layoutMode = settings.settingsLayoutMode,
                    strings = strings,
                    tagPrefix = "nav_layout_deck",
                    pages = listOf(
                        strings.navBarStyleTitle to {
                // Navigation Bar Style
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ViewAgenda,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.navBarStyleTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NavBarStyle.entries.forEach { style ->
                        val selected = settings.navBarStyle == style
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavBarStyleChange(style)
                            },
                            label = {
                                Text(
                                    text = strings.navBarStyleName(style),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("nav_bar_style_${style.name.lowercase()}")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Default Startup Tab
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayCircleOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.startupTabTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MediaTab.entries.forEach { tab ->
                        val selected = settings.startupTab == tab
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onStartupTabChange(tab)
                            },
                            label = {
                                Text(
                                    text = strings.tabDisplayName(tab),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("startup_tab_${tab.name.lowercase()}")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Interface Density
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.GridView,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.uiDensityTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UiDensity.entries.forEach { density ->
                        val selected = settings.uiDensity == density
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onUiDensityChange(density)
                            },
                            label = {
                                Text(
                                    text = strings.uiDensityName(density),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("ui_density_${density.name.lowercase()}")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Navigation Transition Animations
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Animation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.navTransitionTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NavTransitionStyle.entries.forEach { transition ->
                        val selected = settings.navTransitionStyle == transition
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavTransitionStyleChange(transition)
                            },
                            label = {
                                Text(
                                    text = strings.navTransitionName(transition),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("nav_transition_${transition.name.lowercase()}")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Tactile Haptic Feedback
                SettingsToggleRow(
                    icon = Icons.Filled.Vibration,
                    title = strings.hapticsTitle,
                    subtitle = strings.hapticsDesc,
                    checked = settings.hapticsEnabled,
                    onCheckedChange = onHapticsEnabledChange,
                    testTag = "setting_haptics"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Sticky Floating Mini-Window Edge-Dock Side (Right or Left edge tuck button)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ViewSidebar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.edgeDockSideTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EdgeDockSide.entries.forEach { side ->
                        val selected = settings.miniPlayerEdgeDockSide == side
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onMiniPlayerEdgeDockSideChange(side)
                            },
                            label = {
                                Text(
                                    text = if (side == EdgeDockSide.RIGHT) strings.edgeDockRight else strings.edgeDockLeft,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("edge_dock_${side.name.lowercase()}")
                        )
                    }
                }
                        },
                        strings.customTabOrderTitle to {
                // Custom Bottom Navigation Tab Order
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SwapVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = strings.customTabOrderTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.customTabOrderDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onResetNavTabOrder()
                        },
                        shape = CircleShape,
                        modifier = Modifier.testTag("reset_nav_tab_order")
                    ) {
                        Text(strings.eqReset, style = MaterialTheme.typography.labelMedium)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    settings.navTabOrder.forEachIndexed { idx, tab ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${idx + 1}. ${strings.tabDisplayName(tab)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                Row {
                                    IconButton(
                                        onClick = { onMoveNavTab(tab, -1) },
                                        enabled = idx > 0,
                                        modifier = Modifier.testTag("move_nav_tab_up_${tab.name.lowercase()}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.KeyboardArrowUp,
                                            contentDescription = "Move up"
                                        )
                                    }
                                    IconButton(
                                        onClick = { onMoveNavTab(tab, 1) },
                                        enabled = idx < settings.navTabOrder.lastIndex,
                                        modifier = Modifier.testTag("move_nav_tab_down_${tab.name.lowercase()}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.KeyboardArrowDown,
                                            contentDescription = "Move down"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = strings.headerButtonsTitle,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Link,
                    title = strings.headerShowStreamTitle,
                    subtitle = strings.playStreamUrl,
                    checked = settings.headerShowStreamBtn,
                    onCheckedChange = onHeaderShowStreamBtnChange,
                    testTag = "setting_header_show_stream"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Splitscreen,
                    title = strings.headerShowSplitTitle,
                    subtitle = strings.multiWindowTitle,
                    checked = settings.headerShowMultiWindowBtn,
                    onCheckedChange = onHeaderShowMultiWindowBtnChange,
                    testTag = "setting_header_show_split"
                )
                        }
                    )
                )
            }
        }
        }

        // 3. Dedicated Music Settings (Player Artwork, Tint, Seek Step, Audio Playback, Lyrics, 5-Band DSP Equalizer & Sleep Timer)
        if (matchesSection(
                strings.settingsMusicSection,
                strings.playerArtworkStyleTitle,
                strings.albumArtTintTitle,
                strings.fullPlayerSpectrumTitle,
                strings.seekStepTitle,
                strings.equalizerTitle,
                strings.lyricsTitle,
                strings.sleepTimerTitle,
                "music", "audio", "eq", "bass", "vinyl", "seek", "موسيقى", "صوت", "معادل", "كلمات", "أسطوانة"
            )
        ) {
        item(key = "sec_music") {
            val eqTag = if (settings.eqEnabled) strings.eqActiveStatus else strings.eqBypassedStatus
            ExpandableSettingsSection(
                title = strings.settingsMusicSection,
                subtitle = "${strings.playerArtworkStyleName(settings.playerArtworkStyle)} • ${strings.eqPresetName(settings.eqPreset)} ($eqTag)",
                icon = Icons.Filled.MusicNote,
                expanded = isSectionExpanded("music"),
                strings = strings,
                tag = "section_music",
                onToggle = { expandedSections["music"] = !(expandedSections["music"] ?: false) }
            ) {
                LiveMusicDspPreviewCard(
                    settings = settings,
                    strings = strings,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                SettingsPagedDeck(
                    layoutMode = settings.settingsLayoutMode,
                    strings = strings,
                    tagPrefix = "music_deck",
                    pages = listOf(
                        strings.playerArtworkStyleTitle to {
                // Full Player Artwork Style
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Album,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.playerArtworkStyleTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PlayerArtworkStyle.entries.forEach { style ->
                        val selected = settings.playerArtworkStyle == style
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onPlayerArtworkStyleChange(style)
                            },
                            label = {
                                Text(
                                    text = strings.playerArtworkStyleName(style),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("artwork_style_${style.name.lowercase()}")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                SettingsToggleRow(
                    icon = Icons.Filled.Palette,
                    title = strings.albumArtTintTitle,
                    subtitle = strings.albumArtTintDesc,
                    checked = settings.albumArtTintEnabled,
                    onCheckedChange = onAlbumArtTintChange,
                    testTag = "setting_album_art_tint"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Rounded.PictureInPictureAlt,
                    title = strings.pipMusicTitle,
                    subtitle = strings.pipMusicDesc,
                    checked = settings.pipMusicEnabled,
                    onCheckedChange = onPipMusicEnabledChange,
                    testTag = "setting_pip_music"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.GraphicEq,
                    title = strings.fullPlayerSpectrumTitle,
                    subtitle = strings.fullPlayerSpectrumDesc,
                    checked = settings.showFullPlayerSpectrum,
                    onCheckedChange = onFullPlayerSpectrumChange,
                    testTag = "setting_full_player_spectrum"
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Spectrum Visualizer Style (Classic Bars / Wave Pulse / Minimal Dots)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Equalizer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.spectrumStyleTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SpectrumVisualStyle.entries.forEach { style ->
                        val selected = settings.spectrumVisualStyle == style
                        val label = when (style) {
                            SpectrumVisualStyle.CLASSIC_BARS -> strings.spectrumClassicBars
                            SpectrumVisualStyle.WAVE_PULSE -> strings.spectrumWavePulse
                            SpectrumVisualStyle.MINIMAL_DOTS -> strings.spectrumMinimalDots
                        }
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSpectrumVisualStyleChange(style)
                            },
                            label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("spectrum_style_${style.name.lowercase()}")
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                // Drive / Car Mode & Swipe Artwork Gesture & Lyrics Font Size
                SettingsToggleRow(
                    icon = Icons.Filled.DirectionsCar,
                    title = strings.driveModeTitle,
                    subtitle = strings.driveModeDesc,
                    checked = settings.driveModeEnabled,
                    onCheckedChange = onDriveModeChange,
                    testTag = "setting_drive_mode"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.TouchApp,
                    title = strings.swipeArtworkTitle,
                    subtitle = strings.swipeArtworkDesc,
                    checked = settings.playerGestureSwipeArt,
                    onCheckedChange = onPlayerGestureSwipeArtChange,
                    testTag = "setting_swipe_artwork"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.lyricsSizeLabel,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${settings.lyricsFontSizeSp}sp",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Slider(
                    value = settings.lyricsFontSizeSp.toFloat(),
                    onValueChange = { onLyricsFontSizeChange(it.roundToInt()) },
                    valueRange = 13f..28f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setting_lyrics_font_size")
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Online Knowledge, Real Artist Photos & Auto-Synced Lyrics
                SettingsToggleRow(
                    icon = Icons.Filled.Public,
                    title = strings.onlineEnrichmentTitle,
                    subtitle = strings.onlineEnrichmentDesc,
                    checked = settings.autoOnlineEnrichment,
                    onCheckedChange = onAutoOnlineEnrichmentChange,
                    testTag = "setting_online_enrichment"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Lyrics,
                    title = strings.autoFetchLyricsTitle,
                    subtitle = strings.autoFetchLyricsDesc,
                    checked = settings.autoFetchLyrics,
                    onCheckedChange = onAutoFetchLyricsChange,
                    testTag = "setting_auto_fetch_lyrics"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.PersonSearch,
                    title = strings.autoFetchArtistPhotoTitle,
                    subtitle = strings.autoFetchArtistPhotoDesc,
                    checked = settings.autoFetchArtistPhotos,
                    onCheckedChange = onAutoFetchArtistPhotosChange,
                    testTag = "setting_auto_fetch_artist_photo"
                )
                Spacer(modifier = Modifier.height(8.dp))
                FilledTonalButton(
                    onClick = onClearKnowledgeCache,
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_knowledge_cache_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteSweep,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.clearKnowledgeCacheAction, fontWeight = FontWeight.Bold)
                }
                        },
                        strings.dspPageSound to {
                // Pro Audio Engine Controls (Gapless, Loudness Normalization, Headset Unplug)
                SettingsToggleRow(
                    icon = Icons.Filled.MusicNote,
                    title = strings.gaplessPlaybackTitle,
                    subtitle = strings.gaplessPlaybackDesc,
                    checked = settings.gaplessPlayback,
                    onCheckedChange = onGaplessPlaybackChange,
                    testTag = "setting_gapless_playback"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Equalizer,
                    title = strings.audioNormalizationTitle,
                    subtitle = strings.audioNormalizationDesc,
                    checked = settings.audioNormalization,
                    onCheckedChange = onAudioNormalizationChange,
                    testTag = "setting_audio_normalization"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.HeadsetOff,
                    title = strings.headsetUnplugPauseTitle,
                    subtitle = strings.headsetUnplugPauseDesc,
                    checked = settings.pauseOnHeadsetUnplug,
                    onCheckedChange = onPauseOnHeadsetUnplugChange,
                    testTag = "setting_headset_unplug_pause"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Custom Quick Seek Step (Applies to Music & Video Players)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Forward10,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = strings.seekStepTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.seekStepDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(5, 10, 15, 30).forEach { seconds ->
                        val selected = settings.seekStepSeconds == seconds
                        FilterChip(
                            selected = selected,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSeekStepSecondsChange(seconds)
                            },
                            label = {
                                Text(
                                    text = "${seconds}s",
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = CircleShape,
                            modifier = Modifier.testTag("seek_step_$seconds")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                SettingsToggleRow(
                    icon = Icons.Filled.PlayCircleOutline,
                    title = strings.autoPlayNext,
                    subtitle = strings.autoPlayNextDesc,
                    checked = settings.autoPlayNext,
                    onCheckedChange = onAutoPlayChange,
                    testTag = "setting_autoplay"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Restore,
                    title = strings.resumePosition,
                    subtitle = strings.resumePositionDesc,
                    checked = settings.resumePlaybackPosition,
                    onCheckedChange = onResumePositionChange,
                    testTag = "setting_resume"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.FilterAlt,
                    title = strings.filterShortClips,
                    subtitle = strings.filterShortClipsDesc,
                    checked = settings.filterShortAudio,
                    onCheckedChange = onFilterShortChange,
                    testTag = "setting_filter_short"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Lyrics,
                    title = strings.showLyricsSetting,
                    subtitle = strings.showLyricsDesc,
                    checked = settings.showLyricsInPlayer,
                    onCheckedChange = onShowLyricsChange,
                    testTag = "setting_show_lyrics"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Studio 200% Software Volume Pre-Amp Boost
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = strings.preampBoostTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.preampBoostDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = if (settings.audioPreampPercent > 100) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                    ) {
                        Text(
                            text = "${settings.audioPreampPercent}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (settings.audioPreampPercent > 100) {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Slider(
                    value = settings.audioPreampPercent.toFloat(),
                    onValueChange = { onAudioPreampChange((it / 10f).roundToInt() * 10) },
                    valueRange = 100f..200f,
                    steps = 9,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setting_preamp_slider")
                )

                // Independent Audio Pitch Shift (0.75x - 1.50x)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GraphicEq,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = strings.audioPitchTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.audioPitchDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Surface(
                        onClick = { onAudioPitchChange(1.0f) },
                        shape = CircleShape,
                        color = if (kotlin.math.abs(settings.audioPitch - 1.0f) > 0.01f) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                    ) {
                        Text(
                            text = String.format(java.util.Locale.US, "%.2fx", settings.audioPitch),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (kotlin.math.abs(settings.audioPitch - 1.0f) > 0.01f) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Slider(
                    value = settings.audioPitch,
                    onValueChange = { onAudioPitchChange((it * 20f).roundToInt() / 20f) },
                    valueRange = 0.75f..1.50f,
                    steps = 14,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setting_pitch_slider")
                )

                // Studio Smooth Crossfade (0 - 12s)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = strings.crossfadeTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.crossfadeDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = if (settings.crossfadeSeconds > 0) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                    ) {
                        Text(
                            text = if (settings.crossfadeSeconds == 0) {
                                strings.crossfadeOffLabel
                            } else {
                                "${settings.crossfadeSeconds}s"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (settings.crossfadeSeconds > 0) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Slider(
                    value = settings.crossfadeSeconds.toFloat(),
                    onValueChange = { onCrossfadeSecondsChange(it.roundToInt()) },
                    valueRange = 0f..12f,
                    steps = 11,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setting_crossfade_slider")
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Khushu Mode, Lecture Mode, Auto-Skip Silence, Stereo Balance, Mono, Shake-to-Next
                SettingsToggleRow(
                    icon = Icons.Filled.SelfImprovement,
                    title = strings.khushuModeTitle,
                    subtitle = strings.khushuModeDesc,
                    checked = settings.khushuModeEnabled,
                    onCheckedChange = onKhushuModeChange,
                    testTag = "setting_khushu_mode"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Podcasts,
                    title = strings.lectureModeTitle,
                    subtitle = strings.lectureModeDesc,
                    checked = settings.lectureModeEnabled,
                    onCheckedChange = onLectureModeChange,
                    testTag = "setting_lecture_mode"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Speed,
                    title = strings.skipSilenceTitle,
                    subtitle = strings.skipSilenceDesc,
                    checked = settings.skipSilenceEnabled,
                    onCheckedChange = onSkipSilenceChange,
                    testTag = "setting_skip_silence"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Real-time DSP Stereo Balance L/R
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Hearing,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = strings.stereoBalanceTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.stereoBalanceDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    val balPct = (settings.audioBalance * 100).roundToInt()
                    val balLabel = when {
                        balPct < -5 -> "L ${-balPct}%"
                        balPct > 5 -> "R $balPct%"
                        else -> "C"
                    }
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                        Text(
                            text = balLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Slider(
                    value = settings.audioBalance,
                    onValueChange = { onAudioBalanceChange(it) },
                    valueRange = -1f..1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setting_stereo_balance_slider")
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Hearing,
                    title = strings.audioMonoTitle,
                    subtitle = strings.audioMonoDesc,
                    checked = settings.audioMonoEnabled,
                    onCheckedChange = onAudioMonoChange,
                    testTag = "setting_mono_audio"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.ScreenRotation,
                    title = strings.shakeToShuffleTitle,
                    subtitle = strings.shakeToShuffleDesc,
                    checked = settings.shakeToShuffleEnabled,
                    onCheckedChange = onShakeToShuffleChange,
                    testTag = "setting_shake_next"
                )
                        },
                        strings.equalizerTitle to {
                // Beta 4.4 Pro Audio Mastering Studio Hero Card (10-Band EQ + Auto-Studio + 15+ DSP)
                Surface(
                    onClick = onOpenProAudioStudio,
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.72f),
                    border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.48f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_open_pro_audio_studio")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = strings.proStudioTitle,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.proStudioTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${strings.proEqPresetName(settings.proEqPreset)} • ${if (settings.autoStudioMasteringEnabled) strings.studioAutoTitle else strings.proStudioSubtitle}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

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
                    onToggleEq = onEqEnabledChange,
                    onBassBoostChange = onBassBoostChange
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3D Reverb & Acoustic Space DSP Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SurroundSound,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.reverbTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ReverbEnvironment.entries.forEach { env ->
                        val label = when (env) {
                            ReverbEnvironment.NONE -> strings.reverbNone
                            ReverbEnvironment.STUDIO -> strings.reverbStudio
                            ReverbEnvironment.HALL -> strings.reverbHall
                            ReverbEnvironment.MOSQUE -> strings.reverbMosque
                            ReverbEnvironment.SPATIAL_3D -> strings.reverbSpatial3d
                        }
                        FilterChip(
                            selected = settings.reverbEnvironment == env,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onReverbEnvironmentChange(env)
                            },
                            label = { Text(label, fontWeight = FontWeight.SemiBold) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("setting_reverb_${env.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val outputLabel = when (activeAudioOutput) {
                    AudioOutputProfileType.SPEAKER -> strings.audioOutputSpeaker
                    AudioOutputProfileType.WIRED_HEADSET -> strings.audioOutputWired
                    AudioOutputProfileType.BLUETOOTH -> strings.audioOutputBluetooth
                }
                FilledTonalButton(
                    onClick = onOpenAudioProfiles,
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("settings_open_audio_profiles")
                ) {
                    Icon(Icons.Filled.Headphones, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${strings.audioProfilesTitle} • $outputLabel",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                SleepTimerCard(
                    remainingSec = playbackState.sleepTimerRemainingSec,
                    totalMin = playbackState.sleepTimerTotalMin,
                    strings = strings,
                    onStartTimer = onStartSleepTimer,
                    onCancelTimer = onCancelSleepTimer
                )
                        }
                    )
                )
            }
        }
        }

        // 4. Dedicated Video Settings (Aspect Ratio, Speed, Loop, Auto-PiP)
        if (matchesSection(
                strings.settingsVideoSection,
                strings.videoDefaultAspectTitle,
                strings.videoDefaultSpeedTitle,
                strings.videoDefaultLoopTitle,
                strings.autoPipTitle,
                "video", "pip", "speed", "aspect", "loop", "فيديو", "سرعة", "تكرار"
            )
        ) {
        item(key = "sec_video") {
            val aspectLabel = when (settings.videoDefaultAspect) {
                1 -> strings.videoAspectCrop
                2 -> strings.videoAspectFill
                else -> strings.videoAspectFit
            }
            ExpandableSettingsSection(
                title = strings.settingsVideoSection,
                subtitle = "$aspectLabel • ${settings.videoDefaultSpeed}x • PiP",
                icon = Icons.Filled.VideoLibrary,
                expanded = isSectionExpanded("video"),
                strings = strings,
                tag = "section_video",
                onToggle = { expandedSections["video"] = !(expandedSections["video"] ?: false) }
            ) {
                LiveVideoCinemaPreviewCard(
                    settings = settings,
                    strings = strings,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                SettingsPagedDeck(
                    layoutMode = settings.settingsLayoutMode,
                    strings = strings,
                    tagPrefix = "video_deck",
                    pages = listOf(
                        strings.videoDefaultAspectTitle to {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AspectRatio, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.videoDefaultAspectTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        0 to strings.videoAspectFit,
                        1 to strings.videoAspectCrop,
                        2 to strings.videoAspectFill,
                    ).forEach { (idx, label) ->
                        FilterChip(
                            selected = settings.videoDefaultAspect == idx,
                            onClick = { onVideoDefaultAspectChange(idx) },
                            label = { Text(label, fontWeight = FontWeight.SemiBold) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("setting_video_aspect_$idx")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.videoDefaultSpeedTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                        val selected = kotlin.math.abs(settings.videoDefaultSpeed - spd) < 0.04f
                        FilterChip(
                            selected = selected,
                            onClick = { onVideoDefaultSpeedChange(spd) },
                            label = { Text("${spd}x", fontWeight = FontWeight.SemiBold) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("setting_video_speed_${(spd * 100).roundToInt()}")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                SettingsToggleRow(
                    icon = Icons.Filled.Repeat,
                    title = strings.videoDefaultLoopTitle,
                    subtitle = strings.videoDefaultLoopDesc,
                    checked = settings.videoDefaultLoop,
                    onCheckedChange = onVideoDefaultLoopChange,
                    testTag = "setting_video_default_loop"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.SkipNext,
                    title = strings.videoAutoNextTitle,
                    subtitle = strings.videoAutoNextDesc,
                    checked = settings.videoAutoNextEnabled,
                    onCheckedChange = onVideoAutoNextChange,
                    testTag = "setting_video_auto_next"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Rounded.PictureInPictureAlt,
                    title = strings.pipVideoTitle,
                    subtitle = strings.pipVideoDesc,
                    checked = settings.pipVideoEnabled,
                    onCheckedChange = { enabled ->
                        onPipVideoEnabledChange(enabled)
                        onAutoPipChange(enabled)
                    },
                    testTag = "setting_auto_pip"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.TouchApp,
                    title = strings.videoGesturesTitle,
                    subtitle = strings.videoGesturesDesc,
                    checked = settings.videoGesturesEnabled,
                    onCheckedChange = onVideoGesturesChange,
                    testTag = "setting_video_gestures"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Forward10,
                    title = strings.videoDoubleTapTitle,
                    subtitle = strings.videoDoubleTapDesc,
                    checked = settings.videoDoubleTapSeekEnabled,
                    onCheckedChange = onVideoDoubleTapSeekChange,
                    testTag = "setting_video_double_tap"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.History,
                    title = strings.videoRememberPosTitle,
                    subtitle = strings.videoRememberPosDesc,
                    checked = settings.videoRememberPosition,
                    onCheckedChange = onVideoRememberPositionChange,
                    testTag = "setting_video_remember_pos"
                )
                        },
                        strings.videoSubtitleSizeTitle to {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Subtitles, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.videoSubtitleSizeTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(14, 18, 22, 26).forEach { spSize ->
                        FilterChip(
                            selected = settings.videoSubtitleSizeSp == spSize,
                            onClick = { onVideoSubtitleSizeChange(spSize) },
                            label = { Text("${spSize}sp", fontWeight = FontWeight.SemiBold) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("setting_video_sub_size_$spSize")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                Text(
                    text = strings.subtitleColorLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SubtitleColorStyle.entries.forEach { colorStyle ->
                        FilterChip(
                            selected = settings.videoSubtitleColor == colorStyle,
                            onClick = { onVideoSubtitleColorChange(colorStyle) },
                            label = { Text(colorStyle.name, fontWeight = FontWeight.SemiBold) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("setting_video_sub_color_${colorStyle.name.lowercase()}")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(strings.subtitleBgOpacityLabel, style = MaterialTheme.typography.labelMedium)
                    Text("${(settings.videoSubtitleBgOpacity * 100).roundToInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = settings.videoSubtitleBgOpacity,
                    onValueChange = { onVideoSubtitleBgOpacityChange(it) },
                    valueRange = 0f..1f,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(strings.subtitlePositionLabel, style = MaterialTheme.typography.labelMedium)
                    Text("${settings.videoSubtitleBottomOffsetDp}dp", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = settings.videoSubtitleBottomOffsetDp.toFloat(),
                    onValueChange = { onVideoSubtitleBottomOffsetChange(it.roundToInt()) },
                    valueRange = 8f..120f,
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Flip,
                    title = strings.videoMirrorTitle,
                    subtitle = strings.videoMirrorDesc,
                    checked = settings.videoMirrorEnabled,
                    onCheckedChange = onVideoMirrorChange,
                    testTag = "setting_video_mirror"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.videoAutoHideTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(2, 4, 6, 0).forEach { sec ->
                        val label = if (sec == 0) strings.videoAutoHideNever else "${sec}s"
                        FilterChip(
                            selected = settings.videoAutoHideSeconds == sec,
                            onClick = { onVideoAutoHideSecondsChange(sec) },
                            label = { Text(label, fontWeight = FontWeight.SemiBold) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("setting_video_autohide_$sec")
                        )
                    }
                }
                        }
                    )
                )
            }
        }
        }

        // 5. Dedicated Photo Gallery Settings (Grid Columns, Slideshow Interval, Show Titles)
        if (matchesSection(
                strings.settingsPhotosSection,
                strings.photoGridColumnsTitle,
                strings.photoSlideshowSpeedTitle,
                strings.photoShowTitlesLabel,
                "photo", "gallery", "grid", "slideshow", "صور", "معرض", "شبكة"
            )
        ) {
        item(key = "sec_photos") {
            val colsSummary = if (settings.photoGridColumns == 0) {
                strings.photoGridAuto
            } else {
                strings.photoColumnsCount(settings.photoGridColumns)
            }
            ExpandableSettingsSection(
                title = strings.settingsPhotosSection,
                subtitle = "$colsSummary • ${strings.secondsShort(settings.photoSlideshowSeconds)}",
                icon = Icons.Filled.PhotoLibrary,
                expanded = isSectionExpanded("photos"),
                strings = strings,
                tag = "section_photos",
                onToggle = { expandedSections["photos"] = !(expandedSections["photos"] ?: false) }
            ) {
                LivePhotoGridPreviewCard(
                    settings = settings,
                    strings = strings,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                SettingsPagedDeck(
                    layoutMode = settings.settingsLayoutMode,
                    strings = strings,
                    tagPrefix = "photos_deck",
                    pages = listOf(
                        strings.photoGridColumnsTitle to {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.GridView, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.photoGridColumnsTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        0 to strings.photoGridAuto,
                        2 to strings.photoColumnsCount(2),
                        3 to strings.photoColumnsCount(3),
                    ).forEach { (cols, label) ->
                        FilterChip(
                            selected = settings.photoGridColumns == cols,
                            onClick = { onPhotoGridColumnsChange(cols) },
                            label = { Text(label, fontWeight = FontWeight.SemiBold) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("setting_photo_cols_$cols")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Slideshow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.photoSlideshowSpeedTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(2, 3, 5, 8).forEach { sec ->
                        FilterChip(
                            selected = settings.photoSlideshowSeconds == sec,
                            onClick = { onPhotoSlideshowSecondsChange(sec) },
                            label = { Text(strings.secondsShort(sec), fontWeight = FontWeight.SemiBold) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("setting_photo_slideshow_$sec")
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                SettingsToggleRow(
                    icon = Icons.Filled.TextFields,
                    title = strings.photoShowTitlesLabel,
                    subtitle = strings.photoShowTitlesDesc,
                    checked = settings.photoShowTitles,
                    onCheckedChange = onPhotoShowTitlesChange,
                    testTag = "setting_photo_show_titles"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Animation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = strings.photoSlideshowTransitionTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SlideshowTransitionStyle.entries.forEach { style ->
                        FilterChip(
                            selected = settings.photoSlideshowTransition == style,
                            onClick = { onPhotoSlideshowTransitionChange(style) },
                            label = { Text(strings.slideshowTransitionName(style), fontWeight = FontWeight.SemiBold) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("setting_photo_transition_${style.name.lowercase()}")
                        )
                    }
                }
                        },
                        strings.photoAutoExifTitle to {
                SettingsToggleRow(
                    icon = Icons.Filled.CameraAlt,
                    title = strings.photoAutoExifTitle,
                    subtitle = strings.photoAutoExifDesc,
                    checked = settings.photoAutoShowExif,
                    onCheckedChange = onPhotoAutoShowExifChange,
                    testTag = "setting_photo_auto_exif"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.HighQuality,
                    title = strings.photoHighResTitle,
                    subtitle = strings.photoHighResDesc,
                    checked = settings.photoHighResDecode,
                    onCheckedChange = onPhotoHighResDecodeChange,
                    testTag = "setting_photo_high_res"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Filled.Shield,
                    title = strings.stripExifShareTitle,
                    subtitle = strings.stripExifShareDesc,
                    checked = settings.stripExifOnShare,
                    onCheckedChange = onStripExifOnShareChange,
                    testTag = "setting_strip_exif_share"
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                SettingsToggleRow(
                    icon = Icons.Rounded.PictureInPictureAlt,
                    title = strings.pipPhotoTitle,
                    subtitle = strings.pipPhotoDesc,
                    checked = settings.pipPhotoEnabled,
                    onCheckedChange = onPipPhotoEnabledChange,
                    testTag = "setting_pip_photo"
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onOpenPrivateVault,
                        shape = CircleShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.privateVaultTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    OutlinedButton(
                        onClick = onOpenCollageStudio,
                        shape = CircleShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.GridView, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.collageStudioTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                        }
                    )
                )
            }
        }
        }

        // 6. Player Bar Customization & Add-ons Studio
        if (matchesSection(
                strings.playerBarSectionTitle,
                strings.playerBarSectionDesc,
                strings.squigglyBarTitle,
                strings.compactBarAction,
                strings.floatPlayerAction,
                "bar", "mini", "squiggly", "floating", "شريط", "مشغل", "عائم", "متعرج"
            )
        ) {
        item(key = "sec_player_bar") {
            ExpandableSettingsSection(
                title = strings.playerBarSectionTitle,
                subtitle = strings.playerBarSectionDesc,
                icon = Icons.Filled.Tune,
                expanded = isSectionExpanded("player_bar"),
                strings = strings,
                tag = "section_player_bar",
                onToggle = { expandedSections["player_bar"] = !(expandedSections["player_bar"] ?: false) }
            ) {
                PlayerBarCustomizerContent(
                    settings = settings,
                    strings = strings,
                    onSquigglyChange = onSquigglyProgressChange,
                    onCompactChange = onMiniPlayerCompactChange,
                    onFloatingChange = onMiniPlayerFloatingChange,
                    onToggleAddon = onToggleBarAddon
                )
            }
        }
        }

        // 7. Authentic Material You Home Screen Widgets (6 Interactive Pinnable Widgets!)
        if (matchesSection(
                strings.settingsWidgetTitle,
                strings.widgetsSixGalleryTitle,
                strings.widgetCompactTitle,
                "widget", "home", "ودجت", "ودجتس", "شاشة رئيسية"
            )
        ) {
        item(key = "sec_widgets") {
            ExpandableSettingsSection(
                title = strings.widgetsSixGalleryTitle,
                subtitle = "6 Material You Widgets • 4×1 • 4×2 • 2×2",
                icon = Icons.Filled.Widgets,
                expanded = isSectionExpanded("widgets"),
                strings = strings,
                tag = "section_widget",
                onToggle = { expandedSections["widgets"] = !(expandedSections["widgets"] ?: false) }
            ) {
                SixWidgetsShowcaseSection(
                    settings = settings,
                    playbackState = playbackState,
                    strings = strings,
                    onPostBanner = onPostBanner
                )
            }
        }
        }

        // 8. Media Library, Auto-Watch Folders, Storage Insights & HTTPS Streams
        if (matchesSection(
                strings.settingsLibrary,
                strings.autoScanStartupTitle,
                strings.storageInsightsTitle,
                strings.backupSectionTitle,
                "library", "folder", "scan", "backup", "stream", "مكتبة", "مجلد", "نسخ", "مسح"
            )
        ) {
        item(key = "sec_library") {
            ExpandableSettingsSection(
                title = strings.settingsLibrary,
                subtitle = "${strings.selectedFoldersLabel(watchedFolders.size)} • ${strings.storageInsightsTitle}",
                icon = Icons.Filled.FolderOpen,
                expanded = isSectionExpanded("library"),
                strings = strings,
                tag = "section_library",
                onToggle = { expandedSections["library"] = !(expandedSections["library"] ?: false) }
            ) {
                SettingsPagedDeck(
                    layoutMode = settings.settingsLayoutMode,
                    strings = strings,
                    tagPrefix = "library_deck",
                    pages = listOf(
                        strings.settingsLibrary to {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Default Media Player Card (1-Tap Android System Default Media Player Setup)
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.58f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.38f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_default_player_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = strings.defaultPlayerTitle,
                                            tint = MaterialTheme.colorScheme.onSecondary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.defaultPlayerTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = strings.defaultPlayerDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Button(
                                onClick = onOpenDefaultPlayerSettings,
                                shape = CircleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("settings_set_default_player_btn")
                            ) {
                                Icon(Icons.Filled.Verified, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(strings.defaultPlayerAction, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Play-Compliant Persistent Storage & Folder Access Hub
                    val grantedUriCount = remember(watchedFolders.size) {
                        runCatching { context.contentResolver.persistedUriPermissions.size }.getOrDefault(watchedFolders.size)
                    }
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_storage_access_hub")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.FolderOpen,
                                            contentDescription = strings.storageAccessHubTitle,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.storageAccessHubTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "${strings.storageGrantedFoldersLabel(maxOf(grantedUriCount, watchedFolders.size))} • ${strings.storageAccessReadyBadge}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = strings.storageAccessHubDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = onSelectFolder,
                                shape = CircleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("settings_grant_folder_access_btn")
                            ) {
                                Icon(Icons.Filled.CreateNewFolder, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = strings.storageGrantFolderAction,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = onImportFiles,
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("settings_grant_downloads_files_btn")
                                ) {
                                    Text(strings.storagePickDownloadsAction, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                FilledTonalButton(
                                    onClick = onPickGallery,
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("settings_grant_gallery_btn")
                                ) {
                                    Text(strings.storagePickGalleryAction, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }

                    SettingsToggleRow(
                        icon = Icons.Filled.FolderCopy,
                        title = strings.autoScanStartupTitle,
                        subtitle = strings.autoScanStartupDesc,
                        checked = settings.autoScanWatchedFolders,
                        onCheckedChange = onAutoScanWatchedFoldersChange,
                        testTag = "setting_auto_scan_folders"
                    )

                    Button(
                        onClick = onOpenWatchedFoldersManager,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("settings_manage_watched_folders")
                    ) {
                        Icon(Icons.Filled.FolderCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${strings.manageFoldersAction} (${watchedFolders.size})",
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    FilledTonalButton(
                        onClick = onOpenStorageInsights,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_storage_insights")
                    ) {
                        Icon(Icons.Filled.Storage, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (duplicateGroupsCount > 0) {
                                "${strings.storageInsightsTitle} ($duplicateGroupsCount)"
                            } else {
                                strings.storageInsightsTitle
                            },
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    FilledTonalButton(
                        onClick = onOpenRecycleBin,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_recycle_bin")
                    ) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${strings.recycleBinTitle} ($trashedCount)",
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    FilledTonalButton(
                        onClick = onSelectFolder,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_rescan_folder")
                    ) {
                        Icon(Icons.Filled.CreateNewFolder, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.rescanFolders, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = onOpenStreamDialog,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_play_stream_url")
                    ) {
                        Icon(Icons.Filled.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.playStreamUrl, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = onImportFiles,
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("settings_import_files")
                        ) {
                            Icon(Icons.Filled.AudioFile, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.importFiles, maxLines = 1)
                        }

                        FilledTonalButton(
                            onClick = onPickGallery,
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("settings_pick_gallery")
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.pickGalleryMedia, maxLines = 1)
                        }
                    }
                }
                        },
                        strings.backupSectionTitle to {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Verified Local Backup & Restore (.prism.json)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Backup,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.backupSectionTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = strings.backupSectionDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FilledTonalButton(
                                    onClick = onExportBackup,
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("settings_export_backup")
                                ) {
                                    Icon(Icons.Filled.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(strings.backupExportAction, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                OutlinedButton(
                                    onClick = onImportBackup,
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("settings_import_backup")
                                ) {
                                    Icon(Icons.Filled.RestorePage, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(strings.backupImportAction, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }

                    // Cloud-Free Local Wi-Fi File Transfer Portal Launcher
                    FilledTonalButton(
                        onClick = onOpenWifiTransfer,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_wifi_transfer_btn")
                    ) {
                        Icon(Icons.Filled.Wifi, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.wifiTransferTitle, fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onLoadDemoMedia,
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("settings_load_demo")
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.loadDemoMedia, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = onClearLibrary,
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("settings_clear_library")
                        ) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.clearLibrary, maxLines = 1)
                        }
                    }
                }
                        }
                    )
                )
            }
        }
        }

        // 9. Language, Hardware Drivers, Screen Profile, Security Integrity & Designer Said Boudhim
        if (matchesSection(
                strings.settingsSystemSection,
                strings.languageSetting,
                strings.notifSettingTitle,
                strings.screenProfileTitle,
                strings.driversCardTitle,
                strings.appLockTitle,
                strings.securityScoreTitle,
                strings.resetCustomizationAction,
                "system", "language", "driver", "hardware", "screen", "notch", "security", "pin", "biometric", "reset", "لغة", "نظام", "أمان", "حماية", "درايفر", "شاشة", "إعادة"
            )
        ) {
        item(key = "sec_system") {
            ExpandableSettingsSection(
                title = strings.settingsSystemSection,
                subtitle = "${strings.languageDisplayName(settings.language)} • ${strings.driversAutoBadge} • ${strings.designerName}",
                icon = Icons.Filled.Security,
                expanded = isSectionExpanded("system"),
                strings = strings,
                tag = "section_system",
                onToggle = { expandedSections["system"] = !(expandedSections["system"] ?: false) }
            ) {
                SettingsPagedDeck(
                    layoutMode = settings.settingsLayoutMode,
                    strings = strings,
                    tagPrefix = "system_deck",
                    pages = listOf(
                        strings.languageSetting to {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 4-Language Switch (English, العربية RTL, Français, Español)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.languageSetting,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = strings.languageDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            FilterChip(
                                selected = settings.language == lang,
                                onClick = { onLanguageChange(lang) },
                                label = { Text(strings.languageDisplayName(lang), fontWeight = FontWeight.SemiBold) },
                                shape = CircleShape,
                                modifier = Modifier.testTag("lang_${lang.name.lowercase()}")
                            )
                        }
                    }

                    HorizontalDivider()

                    // Status Bar Media Notification Icon Toggle
                    SettingsToggleRow(
                        icon = Icons.Filled.NotificationsActive,
                        title = strings.notifSettingTitle,
                        subtitle = strings.notifSettingDesc,
                        checked = settings.notificationsEnabled,
                        onCheckedChange = onNotificationsEnabledChange,
                        testTag = "setting_media_notifications"
                    )

                    HorizontalDivider()

                    // Re-homed Hardware Audio/Video Drivers & Screen Adaptation Section
                    Text(
                        text = strings.settingsHardwareSubLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Live Auto Screen, Resolution & Notch Adaptation Profile Card
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("screen_adaptation_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Smartphone,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = strings.screenProfileTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = strings.screenAdaptedBadge,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${screenProfile.widthPx}×${screenProfile.heightPx} px • ${screenProfile.widthDp}×${screenProfile.heightDp} dp • ${screenProfile.densityDpi} dpi • ${screenProfile.refreshRateHz}Hz • ${screenProfile.aspectRatioLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (screenProfile.hasNotchOrCutout) {
                                    "${strings.notchDetectedLabel} (${screenProfile.cutoutTopDp} dp)"
                                } else {
                                    strings.notchStandardLabel
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Automatic Device Audio, Video & Display Hardware Drivers Profile Card
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.28f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hardware_drivers_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Memory,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = strings.driversCardTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = strings.driversAutoBadge,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            DriverMetricRow(
                                icon = Icons.Rounded.GraphicEq,
                                label = strings.driversAudioRow,
                                value = activeDriverProfile.audioBadgeLabel
                            )
                            DriverMetricRow(
                                icon = Icons.Rounded.Memory,
                                label = strings.driversVideoRow,
                                value = activeDriverProfile.videoBadgeLabel
                            )
                            DriverMetricRow(
                                icon = Icons.Rounded.DisplaySettings,
                                label = strings.driversDisplayRow,
                                value = activeDriverProfile.displayBadgeLabel
                            )

                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                            SettingsToggleRow(
                                icon = Icons.Rounded.Memory,
                                title = strings.driversHwVideoToggleTitle,
                                subtitle = strings.driversHwVideoToggleDesc,
                                checked = settings.hardwareVideoAcceleration,
                                onCheckedChange = onHardwareVideoAccelChange,
                                testTag = "setting_hw_video_accel"
                            )

                            FilledTonalButton(
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onRefreshDrivers()
                                },
                                shape = CircleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("settings_rescan_drivers")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.driversRescanAction,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                }
                        },
                        strings.securityScoreTitle to {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Defense Shield Score Card (Anti-Intruder & Anti-Hacker)
                    val defenseScore = remember(
                        securityAudit,
                        settings.appLockEnabled,
                        settings.screenPrivacyEnabled,
                        settings.tamperGuardEnabled
                    ) {
                        AppSecurityVerifier.computeSecurityScore(
                            audit = securityAudit,
                            appLockEnabled = settings.appLockEnabled,
                            screenPrivacyEnabled = settings.screenPrivacyEnabled,
                            tamperGuardEnabled = settings.tamperGuardEnabled
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("security_defense_score_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Security,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = strings.securityScoreTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = if (defenseScore >= 80) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.tertiaryContainer
                                    }
                                ) {
                                    Text(
                                        text = "$defenseScore%",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (defenseScore >= 80) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onTertiaryContainer
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            LinearProgressIndicator(
                                progress = { defenseScore / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        }
                    }

                    // App Lock PIN Shield (PBKDF2 Salted PIN + Brute-Force Lockout)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (settings.appLockEnabled) Icons.Filled.Lock else Icons.Filled.LockOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.appLockTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = strings.appLockDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { showSetupPinDialog = true },
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("settings_setup_pin_button")
                        ) {
                            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (settings.appLockEnabled) strings.appLockChangePinAction else strings.appLockSetupAction,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }

                        if (settings.appLockEnabled) {
                            OutlinedButton(
                                onClick = onDisableAppLock,
                                shape = CircleShape,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("settings_disable_pin_button")
                            ) {
                                Icon(Icons.Filled.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.appLockDisableAction, maxLines = 1)
                            }
                        }
                    }

                    if (settings.appLockEnabled) {
                        SettingsToggleRow(
                            icon = Icons.Filled.Shield,
                            title = strings.appLockOnBackgroundTitle,
                            subtitle = strings.appLockOnBackgroundDesc,
                            checked = settings.appLockOnBackground,
                            onCheckedChange = onAppLockOnBackgroundChange,
                            testTag = "setting_app_lock_background"
                        )
                        if (BiometricAuthHelper.isBiometricAvailable(context) || settings.biometricUnlockEnabled) {
                            SettingsToggleRow(
                                icon = Icons.Filled.Fingerprint,
                                title = strings.biometricUnlockTitle,
                                subtitle = strings.biometricUnlockDesc,
                                checked = settings.biometricUnlockEnabled,
                                onCheckedChange = onBiometricUnlockChange,
                                testTag = "setting_biometric_unlock"
                            )
                        }
                    }

                    HorizontalDivider()

                    // Screen Privacy (FLAG_SECURE anti-screenshot & screen recorder shield)
                    SettingsToggleRow(
                        icon = Icons.Filled.PrivacyTip,
                        title = strings.screenPrivacyTitle,
                        subtitle = strings.screenPrivacyDesc,
                        checked = settings.screenPrivacyEnabled,
                        onCheckedChange = onScreenPrivacyChange,
                        testTag = "setting_screen_privacy"
                    )

                    // Intruder Attempt Log Button
                    OutlinedButton(
                        onClick = { showIntruderLogDialog = true },
                        shape = CircleShape,
                        colors = if (intruderLog.isNotEmpty()) {
                            ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        } else {
                            ButtonDefaults.outlinedButtonColors()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("settings_intruder_log_button")
                    ) {
                        Icon(Icons.Filled.Report, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.intruderLogButton(intruderLog.size),
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
                        },
                        strings.builtInProtectionTitle to {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    // Calm, Always-On Built-In Protection Reassurance Banner (No Toggle Button)
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.30f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("builtin_protection_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Rounded.VerifiedUser,
                                            contentDescription = strings.builtInProtectionTitle,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.builtInProtectionTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            Text(
                                text = strings.builtInProtectionBody,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.88f)
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    strings.builtInPillIntegrity,
                                    strings.builtInPillPrivacy,
                                    strings.builtInPillEncrypted
                                ).forEach { pillText ->
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f)
                                    ) {
                                        Text(
                                            text = pillText,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider()

                    // Replay Dreamlike First-Launch Welcome & Feature Guide
                    FilledTonalButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onOpenOnboardingGuide()
                        },
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_replay_onboarding")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = strings.replayOnboardingAction,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.replayOnboardingAction,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 1-Tap Reset All Visual & Navigation Customization to Clean Defaults
                    OutlinedButton(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onResetCustomization()
                        },
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("settings_reset_customization")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.RestartAlt,
                            contentDescription = strings.resetCustomizationAction,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.resetCustomizationAction,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider()

                    // Designer Credit Card — Said Boudhim (سعيد بودحيم)
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("designer_credit_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = strings.designerName,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.designerCardTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = strings.designerName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${strings.designerRole} • ${strings.appTitle} ${strings.appVersionLabel}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = strings.appVersionLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
                        }
                    )
                )
            }
        }
        }

        // 10. Dedicated Collapsible Supplication (Dua), Taqwa Warning & Ethical Purity Shield
        if (matchesSection(
                strings.taqwaSectionTitle,
                strings.taqwaSectionSubtitle,
                strings.taqwaDuaHeading,
                strings.taqwaWarningHeading,
                strings.taqwaPurityShieldTitle,
                "dua", "taqwa", "allah", "quran", "purity", "clean", "دعاء", "تقوى", "الله", "تحذير", "نقاء", "أخلاق"
            )
        ) {
        item(key = "sec_taqwa_dua") {
            ExpandableSettingsSection(
                title = strings.taqwaSectionTitle,
                subtitle = strings.taqwaSectionSubtitle,
                icon = Icons.Filled.VolunteerActivism,
                expanded = isSectionExpanded("taqwa_dua"),
                strings = strings,
                tag = "section_taqwa_dua",
                onToggle = { expandedSections["taqwa_dua"] = !(expandedSections["taqwa_dua"] ?: false) }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 1. Heartfelt Dua Card
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.40f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("taqwa_dua_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VolunteerActivism,
                                    contentDescription = strings.taqwaDuaHeading,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = strings.taqwaDuaHeading,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            Text(
                                text = strings.taqwaDuaBody,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // 2. Solemn Warning & Reminder for Those Who Do Not Fear Allah
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.42f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("taqwa_warning_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = strings.taqwaWarningHeading,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = strings.taqwaWarningHeading,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = strings.taqwaWarningVerse,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Text(
                                text = strings.taqwaWarningBody,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // 3. Built-In Ethical Purity Shield Reassurance Card (Always-On)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.48f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("taqwa_purity_shield_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.VerifiedUser,
                                    contentDescription = strings.taqwaPurityShieldTitle,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = strings.taqwaPurityShieldTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Text(
                                text = strings.taqwaPurityShieldBody,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                                    Text(
                                        text = strings.taqwaPurityBadgeExplicit,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.tertiaryContainer) {
                                    Text(
                                        text = strings.taqwaPurityBadgeLyrics,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
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

        // Floating Quick-Collapse Pill — close open menus instantly from ANY scroll depth!
        AnimatedVisibility(
            visible = anySectionExpanded && normalizedQuery.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = bottomContentPadding + 8.dp)
        ) {
            val quickCollapseInteraction = remember { MutableInteractionSource() }
            Surface(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    AllSectionKeys.forEach { expandedSections[it] = false }
                },
                interactionSource = quickCollapseInteraction,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 8.dp,
                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                modifier = Modifier
                    .expressivePressScale(quickCollapseInteraction, pressedScale = 0.90f)
                    .testTag("settings_floating_collapse_all")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.UnfoldLess,
                        contentDescription = strings.collapseAllSectionsAction,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.collapseAllSectionsAction,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }

    if (showSetupPinDialog) {
        SetupPinDialog(
            strings = strings,
            onDismiss = { showSetupPinDialog = false },
            onConfirmPin = { pin ->
                showSetupPinDialog = false
                onSetupPin(pin)
            }
        )
    }

    if (showIntruderLogDialog) {
        IntruderLogDialog(
            attempts = intruderLog,
            strings = strings,
            onSendReport = {
                AppSecurityVerifier.launchTamperAlertEmail(
                    context = context,
                    audit = securityAudit,
                    screen = screenProfile,
                    intruderSummaries = intruderLog.map { "${it.formattedTime} — ${it.note}" }
                )
                showIntruderLogDialog = false
            },
            onClearLog = onClearIntruderLog,
            onDismiss = { showIntruderLogDialog = false }
        )
    }
}

/**
 * Organized Material You Expandable Accordion Card with a morphing Expand/Collapse button
 * that physically scales/grows and rotates smoothly with Google spring physics when toggled.
 */
@Composable
private fun ExpandableSettingsSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    expanded: Boolean,
    strings: AppStrings,
    tag: String,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val headerInteraction = remember { MutableInteractionSource() }

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "accordionArrowRot"
    )
    // Expand/Collapse button physically grows when expanded and shrinks neatly when collapsed!
    val buttonSize by animateDpAsState(
        targetValue = if (expanded) 46.dp else 36.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "accordionBtnSize"
    )
    val buttonCorner by animateDpAsState(
        targetValue = if (expanded) 15.dp else 20.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "accordionBtnCorner"
    )
    val buttonBg by animateColorAsState(
        targetValue = if (expanded) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        },
        label = "accordionBtnBg"
    )
    val buttonIconTint by animateColorAsState(
        targetValue = if (expanded) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "accordionBtnTint"
    )
    val cardBorderColor by animateColorAsState(
        targetValue = if (expanded) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.42f)
        } else {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f)
        },
        label = "accordionBorder"
    )

    val glassCfg = LocalLiquidGlassConfig.current
    val sectionCorner = scaledCornerDp(28.dp, glassCfg.shapeRoundness)

    MaterialLiquidGlassSurface(
        shape = RoundedCornerShape(sectionCorner),
        shadowElevation = if (expanded) 8.dp else 4.dp,
        tonalBoost = if (expanded) 1.12f else 0.90f,
        modifier = Modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(sectionCorner))
            .testTag(tag)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Tappable Section Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = headerInteraction,
                        indication = androidx.compose.material3.ripple(bounded = true),
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToggle()
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val iconStyle = LocalIconPackStyle.current
                val badgeShape = when (iconStyle) {
                    IconPackStyle.GOOGLE_ROUNDED -> RoundedCornerShape(16.dp)
                    IconPackStyle.FILLED_EXPRESSIVE -> CircleShape
                    IconPackStyle.OUTLINED_MINIMAL -> RoundedCornerShape(12.dp)
                    IconPackStyle.DUOTONE_PILL -> RoundedCornerShape(22.dp)
                }
                val badgeColor = when (iconStyle) {
                    IconPackStyle.OUTLINED_MINIMAL -> Color.Transparent
                    IconPackStyle.DUOTONE_PILL -> if (expanded) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
                    IconPackStyle.FILLED_EXPRESSIVE -> if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                    IconPackStyle.GOOGLE_ROUNDED -> if (expanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh
                }
                val badgeTint = when (iconStyle) {
                    IconPackStyle.FILLED_EXPRESSIVE -> if (expanded) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                    IconPackStyle.DUOTONE_PILL -> if (expanded) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                    else -> if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                }
                val badgeBorder = if (iconStyle == IconPackStyle.OUTLINED_MINIMAL) {
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f))
                } else {
                    null
                }
                Surface(
                    shape = badgeShape,
                    color = badgeColor,
                    border = badgeBorder,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Morphing Expand/Collapse Button (grows & changes shape/color + rotates chevron)
                val btnInteraction = remember { MutableInteractionSource() }
                Surface(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onToggle()
                    },
                    interactionSource = btnInteraction,
                    shape = RoundedCornerShape(buttonCorner),
                    color = buttonBg,
                    modifier = Modifier
                        .size(buttonSize)
                        .expressivePressScale(btnInteraction, pressedScale = 0.86f)
                        .testTag("${tag}_expand_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) strings.collapseSectionDesc else strings.expandSectionDesc,
                            tint = buttonIconTint,
                            modifier = Modifier
                                .size(if (expanded) 26.dp else 22.dp)
                                .rotate(arrowRotation)
                        )
                    }
                }
            }

            // Smooth Expressive Expandable Body
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = 0.86f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = 0.92f,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 18.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 14.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                    )
                    content()
                    Spacer(modifier = Modifier.height(14.dp))
                    // Effortless Bottom Collapse Pill — never scroll back up to close this section!
                    val bottomCollapseInteraction = remember { MutableInteractionSource() }
                    Surface(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToggle()
                        },
                        interactionSource = bottomCollapseInteraction,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .expressivePressScale(bottomCollapseInteraction, pressedScale = 0.96f)
                            .testTag("${tag}_collapse_bottom")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = strings.collapseSectionDesc,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.collapseSectionAction,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DriverMetricRow(icon: ImageVector, label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCheckedChange(!checked)
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onCheckedChange(it)
            },
            modifier = Modifier.testTag(testTag)
        )
    }
}
