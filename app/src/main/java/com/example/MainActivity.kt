package com.example

import android.Manifest
import android.app.KeyguardManager
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon as AndroidIcon
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Rational
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Splitscreen
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.LibraryAdd
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Splitscreen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.OutlinedTextField
import com.example.data.DynamicShortcutsHelper
import com.example.localization.LocalAppStrings
import com.example.localization.stringsFor
import com.example.player.MediaNotificationManager
import com.example.player.MorningAlarmScheduler
import com.example.security.AppLockScreenOverlay
import com.example.security.AppSecurityVerifier
import com.example.widget.PrismExpressiveCardWidgetProvider
import com.example.model.AppLanguage
import com.example.model.AudioTrack
import com.example.model.MediaTab
import com.example.model.NavTransitionStyle
import com.example.model.RenameMediaType
import com.example.model.ScanProgressState
import com.example.model.SecondaryPaneMode
import com.example.model.VideoItem
import com.example.ui.components.AudioCutterSheet
import com.example.ui.components.AudioReactiveEdgeGlowOverlay
import com.example.ui.components.BatchTagEditorDialog
import com.example.ui.components.CustomSeedColorDialog
import com.example.ui.components.DreamOnboardingWizard
import com.example.ui.components.EditAudioTagsDialog
import com.example.ui.components.EqualizerCard
import com.example.ui.components.FloatingAudioBubbleWindow
import com.example.ui.components.FloatingInAppPhotoWindow
import com.example.ui.components.FloatingInAppVideoWindow
import com.example.ui.components.FloatingMiniPlayer
import com.example.ui.components.FloatingPillNavBar
import com.example.ui.components.FolderCustomizeDialog
import com.example.ui.components.FullPlayerSheet
import com.example.ui.components.GoogleDismissDropZone
import com.example.ui.components.ListeningStatsSheet
import com.example.ui.components.LockScreenPlayerOverlay
import com.example.ui.components.MaterialLiquidAmbientBackdrop
import com.example.ui.components.MediaDetailsDialog
import com.example.ui.components.MorningAlarmSheet
import com.example.ui.components.MultiWindowSecondaryHeader
import com.example.ui.components.PhotoCollageStudioDialog
import com.example.ui.components.PipAudioModeView
import com.example.ui.components.PipPhotoModeView
import com.example.ui.components.PlayerBarCustomizerContent
import com.example.ui.components.PlaylistPickerDialog
import com.example.ui.components.PrismNavigationRail
import com.example.ui.components.PrivateVaultSheet
import com.example.ui.components.QuranMushafKhatmaSheet
import com.example.ui.components.RenameMediaDialog
import com.example.ui.components.SleepTimerCard
import com.example.ui.components.SmartMediaImportSheet
import com.example.ui.components.SmartSafFolderNavigator
import com.example.ui.components.expressivePressScale
import com.example.ui.components.StorageInsightsDialog
import com.example.ui.components.StreamUrlDialog
import com.example.ui.components.TasbihCounterDialog
import com.example.ui.components.ThemeCodeDialog
import com.example.ui.components.AudioDeviceProfilesSheet
import com.example.ui.components.GlobalSpotlightSearchSheet
import com.example.ui.components.MediaNoteDialog
import com.example.ui.components.MorningEveningAdhkarSheet
import com.example.ui.components.RecycleBinSheet
import com.example.ui.components.UpNextQueueSheetContent
import com.example.ui.components.VideoTrimmerDialog
import com.example.ui.components.WatchedFoldersManagerSheetContent
import com.example.ui.components.WifiTransferDialog
import com.example.ui.screens.MusicScreen
import com.example.ui.screens.PhotoLightboxViewer
import com.example.ui.screens.PhotosScreen
import com.example.ui.screens.SettingsScreen
import com.example.model.IconPackStyle
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.screens.VideosScreen
import com.example.ui.theme.LocalIconPackStyle
import com.example.ui.theme.PrismTheme
import com.example.viewmodel.MediaViewModel
import kotlin.math.abs
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    companion object {
        private const val ACTION_PIP_PLAY_PAUSE = "com.example.pip.ACTION_PLAY_PAUSE"
        private const val ACTION_PIP_NEXT = "com.example.pip.ACTION_NEXT"
        private const val ACTION_PIP_PREV = "com.example.pip.ACTION_PREV"
        private const val ACTION_PIP_CLOSE = "com.example.pip.ACTION_CLOSE"
    }

    private var isInPipModeState by mutableStateOf(false)
    private var requestedWidgetTabState by mutableStateOf<MediaTab?>(null)
    private var requestedQuickActionState by mutableStateOf<String?>(null)
    private var externalMediaIntentState by mutableStateOf<Pair<Uri, String?>?>(null)
    private var sharedMediaIntentState by mutableStateOf<Pair<List<Uri>, String?>?>(null)
    private var morningAlarmTriggerState by mutableStateOf<Pair<String?, Int>?>(null)
    private var shouldAutoEnterPip: Boolean = false
    private var isPipPlaying: Boolean = false
    private var isPipVideo: Boolean = false
    private var isPipPhoto: Boolean = false

    private var onPipPlayPauseCallback: (() -> Unit)? = null
    private var onPipNextCallback: (() -> Unit)? = null
    private var onPipPrevCallback: (() -> Unit)? = null
    private var onPipDismissStopAllCallback: (() -> Unit)? = null
    private var onAppBackgroundedCallback: (() -> Unit)? = null

    private val pipActionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PIP_PLAY_PAUSE -> onPipPlayPauseCallback?.invoke()
                ACTION_PIP_NEXT -> onPipNextCallback?.invoke()
                ACTION_PIP_PREV -> onPipPrevCallback?.invoke()
                ACTION_PIP_CLOSE -> {
                    onPipDismissStopAllCallback?.invoke()
                    finishAndRemoveTask()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun handleShortcutIntent(intent: Intent?) {
        if (intent == null) return
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                intent.data?.let { dataUri ->
                    externalMediaIntentState = dataUri to intent.type
                    return
                }
            }
            Intent.ACTION_SEND -> {
                val streamUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM)
                }
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (streamUri != null || !text.isNullOrBlank()) {
                    sharedMediaIntentState = listOfNotNull(streamUri) to text
                    return
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                val uris = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM)
                }.orEmpty()
                if (uris.isNotEmpty()) {
                    sharedMediaIntentState = uris to null
                    return
                }
            }
            MorningAlarmScheduler.ACTION_MORNING_ALARM_TRIGGER -> {
                val trackId = intent.getStringExtra(MorningAlarmScheduler.EXTRA_ALARM_TRACK_ID)
                val fadeSec = intent.getIntExtra(MorningAlarmScheduler.EXTRA_ALARM_FADE_SECONDS, 30)
                morningAlarmTriggerState = trackId to fadeSec
                return
            }
        }
        intent.getStringExtra(DynamicShortcutsHelper.EXTRA_QUICK_ACTION)?.takeIf { it.isNotBlank() }?.let { action ->
            requestedQuickActionState = action
        }
        val tabName = intent.getStringExtra(PrismExpressiveCardWidgetProvider.EXTRA_OPEN_TAB) ?: return
        runCatching { MediaTab.valueOf(tabName) }.getOrNull()?.let { tab ->
            requestedWidgetTabState = tab
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleShortcutIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Auto-adapt to notched / punch-hole displays seamlessly across all Android devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                } else {
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }
        }
        handleShortcutIntent(intent)

        val filter = IntentFilter().apply {
            addAction(ACTION_PIP_PLAY_PAUSE)
            addAction(ACTION_PIP_NEXT)
            addAction(ACTION_PIP_PREV)
            addAction(ACTION_PIP_CLOSE)
        }
        ContextCompat.registerReceiver(
            this,
            pipActionReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        setContent {
            PrismAppRoot(
                isInPipMode = isInPipModeState,
                requestedTab = requestedWidgetTabState,
                onConsumeRequestedTab = { requestedWidgetTabState = null },
                requestedQuickAction = requestedQuickActionState,
                onConsumeQuickAction = { requestedQuickActionState = null },
                externalMediaIntent = externalMediaIntentState,
                onConsumeExternalMedia = { externalMediaIntentState = null },
                sharedMediaIntent = sharedMediaIntentState,
                onConsumeSharedMedia = { sharedMediaIntentState = null },
                morningAlarmTrigger = morningAlarmTriggerState,
                onConsumeMorningAlarm = { morningAlarmTriggerState = null },
                onSyncPipState = { autoEnter, playing, videoMode, photoMode, onPlayPause, onNext, onPrev, onStopAll ->
                    shouldAutoEnterPip = autoEnter
                    isPipPlaying = playing
                    isPipVideo = videoMode
                    isPipPhoto = photoMode
                    onPipPlayPauseCallback = onPlayPause
                    onPipNextCallback = onNext
                    onPipPrevCallback = onPrev
                    onPipDismissStopAllCallback = onStopAll
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        runCatching {
                            setPictureInPictureParams(buildPipParams(autoEnter, playing, videoMode, photoMode))
                        }
                    }
                },
                onEnterPipNow = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        runCatching {
                            enterPictureInPictureMode(
                                buildPipParams(shouldAutoEnterPip, isPipPlaying, isPipVideo, isPipPhoto)
                            )
                        }
                    }
                },
                onRegisterBackgroundLock = { callback ->
                    onAppBackgroundedCallback = callback
                }
            )
        }
    }

    override fun onStop() {
        super.onStop()
        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        val isScreenInteractive = powerManager?.isInteractive != false
        // NEVER kill audio playback when the screen turns off or the user leaves the app.
        // Only stop active video PiP if the user explicitly dismissed the video PiP window while awake.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            isInPictureInPictureMode &&
            isScreenInteractive &&
            isPipVideo
        ) {
            onPipDismissStopAllCallback?.invoke()
        } else if (isScreenInteractive) {
            onAppBackgroundedCallback?.invoke()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching { unregisterReceiver(pipActionReceiver) }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // Automatic PiP fallback for Android 8.0 - 11 (API 26-30); API 31+ uses setAutoEnterEnabled(true)
        if (Build.VERSION.SDK_INT in Build.VERSION_CODES.O until Build.VERSION_CODES.S &&
            shouldAutoEnterPip &&
            !isInPipModeState
        ) {
            runCatching {
                enterPictureInPictureMode(buildPipParams(true, isPipPlaying, isPipVideo, isPipPhoto))
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipModeState = isInPictureInPictureMode
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildPipParams(
        autoEnter: Boolean,
        playing: Boolean,
        videoMode: Boolean,
        photoMode: Boolean = false,
    ): PictureInPictureParams {
        val aspect = if (photoMode) Rational(4, 3) else Rational(16, 9)
        val actions = mutableListOf<RemoteAction>()

        if (!photoMode) {
            if (!videoMode) {
                actions += createPipRemoteAction(
                    action = ACTION_PIP_PREV,
                    requestCode = 101,
                    iconRes = android.R.drawable.ic_media_previous,
                    title = "Previous"
                )
            }
            actions += createPipRemoteAction(
                action = ACTION_PIP_PLAY_PAUSE,
                requestCode = 102,
                iconRes = if (playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                title = if (playing) "Pause" else "Play"
            )
            if (!videoMode) {
                actions += createPipRemoteAction(
                    action = ACTION_PIP_NEXT,
                    requestCode = 103,
                    iconRes = android.R.drawable.ic_media_next,
                    title = "Next"
                )
            }
        }
        actions += createPipRemoteAction(
            action = ACTION_PIP_CLOSE,
            requestCode = 104,
            iconRes = android.R.drawable.ic_menu_close_clear_cancel,
            title = "Close"
        )

        val builder = PictureInPictureParams.Builder()
            .setAspectRatio(aspect)
            .setActions(actions)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setAutoEnterEnabled(autoEnter)
            builder.setSeamlessResizeEnabled(true)
        }
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPipRemoteAction(
        action: String,
        requestCode: Int,
        iconRes: Int,
        title: String,
    ): RemoteAction {
        val intent = Intent(action).setPackage(packageName)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return RemoteAction(
            AndroidIcon.createWithResource(this, iconRes),
            title,
            title,
            pendingIntent
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrismAppRoot(
    isInPipMode: Boolean,
    requestedTab: MediaTab? = null,
    onConsumeRequestedTab: () -> Unit = {},
    requestedQuickAction: String? = null,
    onConsumeQuickAction: () -> Unit = {},
    externalMediaIntent: Pair<Uri, String?>? = null,
    onConsumeExternalMedia: () -> Unit = {},
    sharedMediaIntent: Pair<List<Uri>, String?>? = null,
    onConsumeSharedMedia: () -> Unit = {},
    morningAlarmTrigger: Pair<String?, Int>? = null,
    onConsumeMorningAlarm: () -> Unit = {},
    onSyncPipState: (
        autoEnter: Boolean,
        playing: Boolean,
        videoMode: Boolean,
        photoMode: Boolean,
        onPlayPause: () -> Unit,
        onNext: () -> Unit,
        onPrev: () -> Unit,
        onStopAll: () -> Unit,
    ) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onEnterPipNow: () -> Unit = {},
    onRegisterBackgroundLock: ((() -> Unit)?) -> Unit = {},
    viewModel: MediaViewModel = viewModel(),
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val filteredAudio by viewModel.filteredAudioTracks.collectAsStateWithLifecycle()
    val activeQueueTracks by viewModel.activeQueueTracks.collectAsStateWithLifecycle()
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val photos by viewModel.photos.collectAsStateWithLifecycle()
    val hasAnyMedia by viewModel.hasAnyMedia.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackUiState.collectAsStateWithLifecycle()
    val customLyrics by viewModel.customLyrics.collectAsStateWithLifecycle()
    val securityAudit by viewModel.securityAudit.collectAsStateWithLifecycle()
    val showTamperAlertDialog by viewModel.showTamperAlertDialog.collectAsStateWithLifecycle()
    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()
    val failedPinAttempts by viewModel.failedPinAttempts.collectAsStateWithLifecycle()
    val lockoutUntilMs by viewModel.lockoutUntilMs.collectAsStateWithLifecycle()
    val intruderLog by viewModel.intruderLog.collectAsStateWithLifecycle()
    val searchQuery by viewModel.audioSearchQuery.collectAsStateWithLifecycle()
    val selectedFormat by viewModel.selectedAudioFormat.collectAsStateWithLifecycle()
    val selectedSmartMix by viewModel.selectedSmartMix.collectAsStateWithLifecycle()
    val favoritesOnly by viewModel.favoritesOnly.collectAsStateWithLifecycle()
    val audioSort by viewModel.audioSortOption.collectAsStateWithLifecycle()
    val videoSearchQuery by viewModel.videoSearchQuery.collectAsStateWithLifecycle()
    val videoFavoritesOnly by viewModel.videoFavoritesOnly.collectAsStateWithLifecycle()
    val videoSort by viewModel.videoSortOption.collectAsStateWithLifecycle()
    val photoSearchQuery by viewModel.photoSearchQuery.collectAsStateWithLifecycle()
    val photoFavoritesOnly by viewModel.photoFavoritesOnly.collectAsStateWithLifecycle()
    val photoSort by viewModel.photoSortOption.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val selectedPlaylistId by viewModel.selectedPlaylistId.collectAsStateWithLifecycle()
    val playlistTrackTarget by viewModel.playlistTrackTarget.collectAsStateWithLifecycle()
    val batchPlaylistTrackIds by viewModel.batchPlaylistTrackIds.collectAsStateWithLifecycle()
    val renameTarget by viewModel.renameTarget.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycle()
    val showFirstLaunchWizard by viewModel.showFirstLaunchFolderWizard.collectAsStateWithLifecycle()
    val watchedFolders by viewModel.watchedFolders.collectAsStateWithLifecycle()
    val availableMusicFolders by viewModel.availableMusicFolders.collectAsStateWithLifecycle()
    val availableVideoFolders by viewModel.availableVideoFolders.collectAsStateWithLifecycle()
    val availablePhotoFolders by viewModel.availablePhotoFolders.collectAsStateWithLifecycle()
    val selectedMusicFolder by viewModel.selectedMusicFolder.collectAsStateWithLifecycle()
    val selectedVideoFolder by viewModel.selectedVideoFolder.collectAsStateWithLifecycle()
    val selectedPhotoFolder by viewModel.selectedPhotoFolder.collectAsStateWithLifecycle()
    val recentlyPlayedTracks by viewModel.recentlyPlayedTracks.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val editTagTarget by viewModel.editTagTarget.collectAsStateWithLifecycle()
    val duplicateGroups by viewModel.duplicateGroups.collectAsStateWithLifecycle()
    val rawAudioList by viewModel.rawAudioList.collectAsStateWithLifecycle()
    val rawVideosList by viewModel.rawVideosList.collectAsStateWithLifecycle()
    val rawPhotosList by viewModel.rawPhotosList.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val activeVideo by viewModel.activeVideo.collectAsStateWithLifecycle()
    val activeVideoInitialPosMs by viewModel.activeVideoInitialPosMs.collectAsStateWithLifecycle()
    val floatingVideo by viewModel.floatingVideo.collectAsStateWithLifecycle()
    val floatingVideoPosMs by viewModel.floatingVideoPosMs.collectAsStateWithLifecycle()
    val isVideoPlaying by viewModel.isVideoPlaying.collectAsStateWithLifecycle()
    val multiWindowEnabled by viewModel.multiWindowEnabled.collectAsStateWithLifecycle()
    val secondaryPaneMode by viewModel.secondaryPaneMode.collectAsStateWithLifecycle()
    val activePhotoIndex by viewModel.activePhotoIndex.collectAsStateWithLifecycle()
    val floatingPhoto by viewModel.floatingPhoto.collectAsStateWithLifecycle()
    val detailsInfo by viewModel.detailsInfo.collectAsStateWithLifecycle()
    val driverProfile by viewModel.driverProfile.collectAsStateWithLifecycle()
    val knowledgeReports by viewModel.knowledgeReports.collectAsStateWithLifecycle()
    val enrichingIds by viewModel.enrichingIds.collectAsStateWithLifecycle()
    val quranKhatmaProgress by viewModel.quranKhatmaProgress.collectAsStateWithLifecycle()
    val morningAlarmConfig by viewModel.morningAlarmConfig.collectAsStateWithLifecycle()
    val folderCustomizations by viewModel.folderCustomizations.collectAsStateWithLifecycle()
    val vaultItems by viewModel.vaultItems.collectAsStateWithLifecycle()
    val listeningStats by viewModel.listeningStats.collectAsStateWithLifecycle()
    val wifiTransferStatus by viewModel.wifiTransferStatus.collectAsStateWithLifecycle()
    val mediaNotes by viewModel.mediaNotes.collectAsStateWithLifecycle()
    val audioDeviceProfiles by viewModel.audioDeviceProfiles.collectAsStateWithLifecycle()
    val activeAudioOutput by viewModel.activeAudioOutput.collectAsStateWithLifecycle()
    val trashedItems by viewModel.trashedItems.collectAsStateWithLifecycle()
    val musicBrowseMode by viewModel.musicBrowseMode.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        onRegisterBackgroundLock { viewModel.lockAppIfConfigured() }
    }

    // Apply or clear FLAG_SECURE dynamically to block screenshots & screen capture
    LaunchedEffect(settings.screenPrivacyEnabled) {
        val window = (context as? ComponentActivity)?.window ?: return@LaunchedEffect
        if (settings.screenPrivacyEnabled) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    LaunchedEffect(requestedTab) {
        if (requestedTab != null) {
            viewModel.selectTab(requestedTab)
            onConsumeRequestedTab()
        }
    }

    LaunchedEffect(externalMediaIntent) {
        val pair = externalMediaIntent ?: return@LaunchedEffect
        viewModel.openExternalMediaUri(pair.first, pair.second)
        onConsumeExternalMedia()
    }

    LaunchedEffect(sharedMediaIntent) {
        val payload = sharedMediaIntent ?: return@LaunchedEffect
        viewModel.handleSharedIntentUris(payload.first, payload.second)
        onConsumeSharedMedia()
    }

    LaunchedEffect(morningAlarmTrigger) {
        val alarm = morningAlarmTrigger ?: return@LaunchedEffect
        viewModel.triggerMorningAlarmPlayback(alarm.first, alarm.second)
        onConsumeMorningAlarm()
    }

    var askedNotificationPermission by remember { mutableStateOf(false) }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            MediaNotificationManager.updatePlaybackNotification(
                context = context,
                track = playbackState.currentTrack,
                isPlaying = playbackState.isPlaying,
                enabled = settings.notificationsEnabled,
                lockScreenControlsEnabled = settings.lockScreenControlsEnabled,
                lockScreenShowArtwork = settings.lockScreenShowArtwork,
                lockScreenShowIdentity = settings.lockScreenShowPrismIdentity
            )
        }
    }

    LaunchedEffect(playbackState.isPlaying, settings.notificationsEnabled) {
        if (playbackState.isPlaying && settings.notificationsEnabled &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !askedNotificationPermission &&
            !MediaNotificationManager.hasNotificationPermission(context)
        ) {
            askedNotificationPermission = true
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Companion bar, Customizer & Stream URL quick-modal states
    var showMiniEqSheet by remember { mutableStateOf(false) }
    var showMiniTimerSheet by remember { mutableStateOf(false) }
    var showMiniQueueSheet by remember { mutableStateOf(false) }
    var showPlayerCustomizerSheet by remember { mutableStateOf(false) }
    var showStreamUrlDialog by remember { mutableStateOf(false) }
    var showWatchedFoldersSheet by remember { mutableStateOf(false) }
    var showStorageInsightsDialog by remember { mutableStateOf(false) }

    // 24-Feature Studio Modal States
    var showQuranHub by remember { mutableStateOf(false) }
    var showTasbihDialog by remember { mutableStateOf(false) }
    var showMorningAlarm by remember { mutableStateOf(false) }
    var audioCutterTarget by remember { mutableStateOf<AudioTrack?>(null) }
    var showListeningStats by remember { mutableStateOf(false) }
    var showPrivateVault by remember { mutableStateOf(false) }
    var collageInitialIds by remember { mutableStateOf<Set<String>?>(null) }
    var batchTagTargetIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showCustomSeed by remember { mutableStateOf(false) }
    var showThemeCode by remember { mutableStateOf(false) }
    var showWifiTransfer by remember { mutableStateOf(false) }
    var customizeFolderName by remember { mutableStateOf<String?>(null) }

    // 17-Upgrade Suite Modal States
    var showSpotlightSearch by remember { mutableStateOf(false) }
    var showRecycleBin by remember { mutableStateOf(false) }
    var showAdhkarStudio by remember { mutableStateOf(false) }
    var showAudioProfiles by remember { mutableStateOf(false) }
    var noteTarget by remember { mutableStateOf<Triple<String, String, RenameMediaType>?>(null) }
    var trimVideoTarget by remember { mutableStateOf<VideoItem?>(null) }
    var pendingExportM3uPlaylistId by remember { mutableStateOf<String?>(null) }
    var showSmartImportSheet by remember { mutableStateOf(false) }
    var showLockScreenOverlay by remember { mutableStateOf(false) }
    var showQuickCustomizer by remember { mutableStateOf(false) }
    var showProAudioStudio by remember { mutableStateOf(false) }
    val customStudioPresets by viewModel.customStudioPresets.collectAsStateWithLifecycle()
    val detectedAutoStudioProfile by viewModel.detectedAutoStudioProfile.collectAsStateWithLifecycle()

    val openDefaultPlayerSettings: () -> Unit = {
        val pkgUri = Uri.fromParts("package", context.packageName, null)
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(android.provider.Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS, pkgUri)
        } else {
            Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, pkgUri)
        }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching {
            context.startActivity(intent)
        }.onFailure {
            runCatching {
                context.startActivity(
                    Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, pkgUri)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
        viewModel.postBanner(stringsFor(settings.language).defaultPlayerOpenedBanner)
    }

    // Enable Prism Full-Screen Lock-Screen Playback Identity when playing & user enabled
    LaunchedEffect(settings.lockScreenFullScreenEnabled, playbackState.isPlaying) {
        val activity = context as? ComponentActivity ?: return@LaunchedEffect
        val allowOnLock = settings.lockScreenFullScreenEnabled && playbackState.isPlaying
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            activity.setShowWhenLocked(allowOnLock)
        } else {
            @Suppress("DEPRECATION")
            if (allowOnLock) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            }
        }
    }

    // Automatically show/dismiss LockScreenPlayerOverlay when the screen wakes locked vs unlocked
    DisposableEffect(settings.lockScreenFullScreenEnabled, playbackState.isPlaying) {
        if (!settings.lockScreenFullScreenEnabled) {
            onDispose { }
        } else {
            val keyguard = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            val screenReceiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    when (intent?.action) {
                        Intent.ACTION_SCREEN_ON -> {
                            if (keyguard?.isKeyguardLocked == true && playbackState.isPlaying) {
                                showLockScreenOverlay = true
                            }
                        }
                        Intent.ACTION_USER_PRESENT -> {
                            showLockScreenOverlay = false
                        }
                    }
                }
            }
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_USER_PRESENT)
            }
            ContextCompat.registerReceiver(
                context,
                screenReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            onDispose {
                runCatching { context.unregisterReceiver(screenReceiver) }
            }
        }
    }

    // Publish dynamic Android home-screen quick shortcuts in the user's active language
    LaunchedEffect(settings.language) {
        val curStrings = stringsFor(settings.language)
        runCatching {
            DynamicShortcutsHelper.publishShortcuts(context = context, strings = curStrings)
        }
    }

    // Handle quick-launch shortcut & 6-widget action intents
    LaunchedEffect(requestedQuickAction) {
        when (requestedQuickAction) {
            DynamicShortcutsHelper.ACTION_QURAN_HUB -> showQuranHub = true
            DynamicShortcutsHelper.ACTION_PRIVATE_VAULT -> {
                viewModel.refreshVaultItems()
                showPrivateVault = true
            }
            DynamicShortcutsHelper.ACTION_WIFI_TRANSFER -> showWifiTransfer = true
            DynamicShortcutsHelper.ACTION_LISTENING_STATS -> showListeningStats = true
            "tasbih_counter" -> showTasbihDialog = true
            "open_eq" -> showMiniEqSheet = true
        }
        if (requestedQuickAction != null) onConsumeQuickAction()
    }

    // Real Hardware Accelerometer Shake-to-Next Track listener with 1.8s debounce
    DisposableEffect(settings.shakeToShuffleEnabled) {
        if (!settings.shakeToShuffleEnabled) {
            onDispose { }
        } else {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
            val accel = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            var lastShakeMs = 0L
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    val values = event?.values ?: return
                    val gForce = sqrt(
                        (values[0] * values[0] + values[1] * values[1] + values[2] * values[2]).toDouble()
                    ).toFloat() / SensorManager.GRAVITY_EARTH
                    val now = System.currentTimeMillis()
                    if (gForce > 2.35f && now - lastShakeMs > 1800L) {
                        lastShakeMs = now
                        viewModel.playNextAudio()
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }
            if (accel != null) {
                sensorManager.registerListener(listener, accel, SensorManager.SENSOR_DELAY_UI)
            }
            onDispose {
                sensorManager?.unregisterListener(listener)
            }
        }
    }

    // Real-time AudioDeviceCallback to auto-switch EQ/Bass/3D Reverb when Bluetooth or Wired headsets connect/disconnect
    DisposableEffect(settings.autoAudioDeviceProfiles) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        if (audioManager == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onDispose { }
        } else {
            val callback = object : AudioDeviceCallback() {
                override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
                    viewModel.refreshDetectedAudioOutput(applyIfAuto = settings.autoAudioDeviceProfiles)
                }

                override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
                    viewModel.refreshDetectedAudioOutput(applyIfAuto = settings.autoAudioDeviceProfiles)
                }
            }
            audioManager.registerAudioDeviceCallback(callback, Handler(Looper.getMainLooper()))
            onDispose {
                runCatching { audioManager.unregisterAudioDeviceCallback(callback) }
            }
        }
    }

    // Google Drag-to-X Drop-Through state for Floating Audio Bubble & Floating Video Window
    var isDraggingFloatingWindow by remember { mutableStateOf(false) }
    var isHoveringDismissX by remember { mutableStateOf(false) }

    // Bridge video player's toggle-play for Android PiP RemoteAction
    var videoPipToggleCallback by remember { mutableStateOf<(() -> Unit)?>(null) }

    val isArabic = settings.language == AppLanguage.AR
    val strings = remember(settings.language) { stringsFor(settings.language) }
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Synchronize Optional Per-Mode Picture-in-Picture params (Music / Video / Photos independently)
    val isVideoSession = activeVideo != null || floatingVideo != null
    val activePipPhoto = activePhotoIndex?.let { photos.getOrNull(it) } ?: floatingPhoto
    val isPhotoSession = !isVideoSession && activePipPhoto != null
    val isAnyMediaPlaying = if (isVideoSession) isVideoPlaying else playbackState.isPlaying
    val shouldAutoEnterPip = when {
        isVideoSession -> settings.pipVideoEnabled && isVideoPlaying
        isPhotoSession -> settings.pipPhotoEnabled
        playbackState.isPlaying -> settings.pipMusicEnabled
        else -> false
    }

    LaunchedEffect(shouldAutoEnterPip, isAnyMediaPlaying, isVideoSession, isPhotoSession, videoPipToggleCallback) {
        onSyncPipState(
            shouldAutoEnterPip,
            isAnyMediaPlaying,
            isVideoSession,
            isPhotoSession,
            {
                if (isVideoSession && videoPipToggleCallback != null) {
                    videoPipToggleCallback?.invoke()
                } else {
                    viewModel.togglePlayPause()
                }
            },
            { viewModel.playNextAudio() },
            { viewModel.playPreviousAudio() },
            { viewModel.stopAllPlaybackAndClosePip() }
        )
    }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            viewModel.importFolder(uri)
        }
    }

    val filesPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.importFiles(uris)
        }
    }

    val downloadsFilesPickerLauncher = rememberLauncherForActivityResult(
        contract = com.example.ui.components.OpenMultipleDocumentsInSubpath()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.importFiles(uris)
        }
    }

    val visualPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.importFiles(uris)
        }
    }

    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.exportBackupToUri(uri)
        }
    }

    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importBackupFromUri(uri)
        }
    }

    val exportM3uLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("audio/x-mpegurl")
    ) { uri ->
        val plId = pendingExportM3uPlaylistId
        pendingExportM3uPlaylistId = null
        if (uri != null && plId != null) {
            viewModel.exportPlaylistToM3uUri(plId, uri)
        }
    }

    val importM3uLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importPlaylistFromM3uUri(uri)
        }
    }

    val launchSafFolderSubpath: (String) -> Unit = { subpath ->
        folderPickerLauncher.launch(SmartSafFolderNavigator.buildInitialUri(subpath))
    }
    val launchFolderPicker = { showSmartImportSheet = true }
    val launchFilesPicker = {
        filesPickerLauncher.launch(arrayOf("audio/*", "video/*", "image/*"))
    }
    val launchDownloadsFilesPicker = {
        downloadsFilesPickerLauncher.launch(
            SmartSafFolderNavigator.buildInitialUri(SmartSafFolderNavigator.SUBPATH_DOWNLOADS) to
                arrayOf("audio/*", "video/*", "image/*")
        )
    }
    val launchVisualPicker = {
        visualPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(statusBanner) {
        val msg = statusBanner
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusBanner()
        }
    }

    val baseDensity = LocalDensity.current
    val scaledDensity = remember(baseDensity, settings.fontScaleMultiplier) {
        Density(
            density = baseDensity.density,
            fontScale = (baseDensity.fontScale * settings.fontScaleMultiplier).coerceIn(0.75f, 2.2f)
        )
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        LocalDensity provides scaledDensity,
        LocalAppStrings provides strings
    ) {
        PrismTheme(
            darkTheme = settings.darkTheme,
            accentPalette = settings.accentPalette,
            isArabic = isArabic,
            amoledBlack = settings.amoledBlack,
            contrastLevel = settings.contrastLevel,
            shapeRoundness = settings.shapeRoundness,
            liquidGlassStyle = settings.liquidGlassStyle,
            liquidGlassIntensity = settings.liquidGlassIntensity,
            edgeGlowEnabled = settings.edgeGlowEnabled,
            fontStyle = settings.fontStyle,
            iconStyle = settings.iconStyle,
            uiDensity = settings.uiDensity,
            hapticsEnabled = settings.hapticsEnabled,
            surfaceDepthStyle = settings.surfaceDepthStyle,
            customSeedColorArgb = settings.customSeedColorArgb,
            cardStyle = settings.cardStyle,
            backdropStyle = settings.backdropStyle,
            motionFeelStyle = settings.motionFeelStyle,
            showFormatBadges = settings.showFormatBadges,
            accentStrength = settings.accentStrength
        ) {
            // Dedicated clean PiP view when Android shrinks the app into Picture-in-Picture
            if (isInPipMode) {
                val pipVideo = activeVideo ?: floatingVideo
                if (pipVideo != null) {
                    VideoPlayerScreen(
                        video = pipVideo,
                        isInPipMode = true,
                        strings = strings,
                        initialPositionMs = if (activeVideo != null) activeVideoInitialPosMs else floatingVideoPosMs,
                        defaultAspectIndex = settings.videoDefaultAspect,
                        defaultSpeed = settings.videoDefaultSpeed,
                        defaultLoop = settings.videoDefaultLoop,
                        preferHardwareDecoding = settings.hardwareVideoAcceleration,
                        seekStepSeconds = settings.seekStepSeconds,
                        gesturesEnabled = settings.videoGesturesEnabled,
                        doubleTapSeekEnabled = settings.videoDoubleTapSeekEnabled,
                        defaultSubtitleSizeSp = settings.videoSubtitleSizeSp,
                        defaultMirrorEnabled = settings.videoMirrorEnabled,
                        autoHideSeconds = settings.videoAutoHideSeconds,
                        onToggleMirror = viewModel::setVideoMirrorEnabled,
                        onPlaybackActiveChanged = viewModel::setVideoPlayingActive,
                        onRegisterPipToggle = { videoPipToggleCallback = it },
                        onClose = viewModel::closeVideo
                    )
                } else if (activePipPhoto != null) {
                    PipPhotoModeView(
                        photo = activePipPhoto,
                        strings = strings
                    )
                } else {
                    PipAudioModeView(
                        playbackState = playbackState,
                        spectrumLevelsFlow = viewModel.spectrumLevels,
                        strings = strings
                    )
                }
                return@PrismTheme
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Authentic Material You Living Ambient Backdrop behind Liquid Glass surfaces
                MaterialLiquidAmbientBackdrop(
                    isDarkTheme = settings.darkTheme,
                    amoledBlack = settings.amoledBlack,
                    isPlaying = playbackState.isPlaying,
                    modifier = Modifier.fillMaxSize()
                )
                val isWideScreen = maxWidth >= 700.dp
                val containerHeightPx = with(LocalDensity.current) { maxHeight.toPx() }
                val hasDockedMiniPlayer = playbackState.currentTrack != null && !settings.miniPlayerFloating
                val navExtraBottom: Dp = when (settings.navBarStyle) {
                    com.example.model.NavBarStyle.DOCKED_MATERIAL_YOU -> 16.dp
                    com.example.model.NavBarStyle.COMPACT_DOCK -> (-8).dp
                    com.example.model.NavBarStyle.FLOATING_PILL -> 0.dp
                }
                val bottomPadding: Dp = when {
                    isWideScreen && hasDockedMiniPlayer -> if (settings.miniPlayerCompact) 116.dp else 148.dp
                    isWideScreen -> 48.dp
                    hasDockedMiniPlayer -> (if (settings.miniPlayerCompact) 196.dp else 228.dp) + navExtraBottom
                    else -> 148.dp + navExtraBottom
                }

                val primaryTabContent: @Composable (MediaTab, Dp) -> Unit = { tab, pad ->
                    when (tab) {
                        MediaTab.MUSIC -> MusicScreen(
                            tracks = filteredAudio,
                            hasAnyMedia = hasAnyMedia,
                            currentTrackId = playbackState.currentTrack?.id,
                            isPlaying = playbackState.isPlaying,
                            searchQuery = searchQuery,
                            selectedFormat = selectedFormat,
                            favoritesOnly = favoritesOnly,
                            sortOption = audioSort,
                            viewMode = settings.musicViewMode,
                            playlists = playlists,
                            selectedPlaylistId = selectedPlaylistId,
                            availableFolders = availableMusicFolders,
                            selectedFolder = selectedMusicFolder,
                            recentlyPlayed = recentlyPlayedTracks,
                            selectedSmartMix = selectedSmartMix,
                            strings = strings,
                            bottomContentPadding = pad,
                            onSearchChange = viewModel::setAudioSearchQuery,
                            onSelectFormat = viewModel::setSelectedAudioFormat,
                            onSelectSmartMix = viewModel::selectSmartMix,
                            onToggleFavoritesFilter = viewModel::toggleFavoritesFilter,
                            onSelectSortOption = viewModel::setAudioSortOption,
                            onSelectViewMode = viewModel::setMusicViewMode,
                            onSelectFolderFilter = viewModel::setMusicFolderFilter,
                            onSelectPlaylist = viewModel::selectPlaylist,
                            onCreatePlaylist = { name -> viewModel.createPlaylist(name) },
                            onDeletePlaylist = viewModel::deletePlaylist,
                            onAddTrackToPlaylist = viewModel::openPlaylistPickerForTrack,
                            onAddSelectedToPlaylist = viewModel::openBatchPlaylistPicker,
                            onPlayTrack = viewModel::playAudio,
                            onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                            onFavoriteSelected = viewModel::favoriteItemsByIds,
                            onRenameTrack = {
                                viewModel.openRenameDialog(it.id, it.title, RenameMediaType.AUDIO)
                            },
                            onRemoveTrack = viewModel::removeAudio,
                            onRemoveSelected = viewModel::removeAudioByIds,
                            onShowDetails = viewModel::showDetails,
                            onSelectFolder = launchFolderPicker,
                            onImportFiles = launchFilesPicker,
                            onLoadDemoMedia = viewModel::loadDemoMedia,
                            folderCustomizations = folderCustomizations,
                            onLongPressFolder = { customizeFolderName = it },
                            onBatchEditTags = { batchTagTargetIds = it },
                            onOpenQuranHub = { showQuranHub = true },
                            onOpenMorningAlarm = { showMorningAlarm = true },
                            onOpenListeningStats = { showListeningStats = true },
                            onOpenTasbih = { showTasbihDialog = true },
                            browseMode = musicBrowseMode,
                            onSelectBrowseMode = viewModel::setMusicBrowseMode,
                            allAudioTracks = rawAudioList,
                            onMovePlaylistTrack = viewModel::movePlaylistTrack,
                            onExportPlaylistM3u = { plId ->
                                val plName = playlists.firstOrNull { it.id == plId }?.name
                                    ?.replace(Regex("[^a-zA-Z0-9_\\- ]"), "")
                                    ?.trim()
                                    ?.ifBlank { "prism_playlist" }
                                    ?: "prism_playlist"
                                pendingExportM3uPlaylistId = plId
                                exportM3uLauncher.launch("$plName.m3u")
                            },
                            onImportPlaylistM3u = {
                                importM3uLauncher.launch(arrayOf("audio/x-mpegurl", "audio/mpegurl", "text/plain", "*/*"))
                            },
                            mediaNotes = mediaNotes,
                            onOpenNote = { tr ->
                                noteTarget = Triple(tr.id, tr.title, RenameMediaType.AUDIO)
                            },
                            swipeGesturesEnabled = settings.swipeGesturesEnabled
                        )

                        MediaTab.VIDEOS -> VideosScreen(
                            videos = videos,
                            hasAnyMedia = hasAnyMedia,
                            searchQuery = videoSearchQuery,
                            favoritesOnly = videoFavoritesOnly,
                            sortOption = videoSort,
                            viewMode = settings.videoViewMode,
                            availableFolders = availableVideoFolders,
                            selectedFolder = selectedVideoFolder,
                            strings = strings,
                            bottomContentPadding = pad,
                            onSearchChange = viewModel::setVideoSearchQuery,
                            onToggleFavoritesFilter = viewModel::toggleVideoFavoritesFilter,
                            onSelectSortOption = viewModel::setVideoSortOption,
                            onSelectViewMode = viewModel::setVideoViewMode,
                            onSelectFolderFilter = viewModel::setVideoFolderFilter,
                            onOpenVideo = { viewModel.openVideo(it) },
                            onPlayVideoAsAudio = { viewModel.playVideoAsAudio(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                            onFavoriteSelected = viewModel::favoriteItemsByIds,
                            onRenameVideo = {
                                viewModel.openRenameDialog(it.id, it.title, RenameMediaType.VIDEO)
                            },
                            onRemoveVideo = viewModel::removeVideo,
                            onRemoveSelected = viewModel::removeVideosByIds,
                            onShowDetails = viewModel::showDetails,
                            onSelectFolder = launchFolderPicker,
                            onImportFiles = launchFilesPicker,
                            onLoadDemoMedia = viewModel::loadDemoMedia,
                            folderCustomizations = folderCustomizations,
                            onLongPressFolder = { customizeFolderName = it },
                            onExtractAudio = viewModel::extractAudioFromVideo,
                            onTrimVideo = { vid -> trimVideoTarget = vid },
                            onOpenNote = { vid ->
                                noteTarget = Triple(vid.id, vid.title, RenameMediaType.VIDEO)
                            }
                        )

                        MediaTab.PHOTOS -> PhotosScreen(
                            photos = photos,
                            hasAnyMedia = hasAnyMedia,
                            searchQuery = photoSearchQuery,
                            favoritesOnly = photoFavoritesOnly,
                            sortOption = photoSort,
                            viewMode = settings.photoViewMode,
                            availableFolders = availablePhotoFolders,
                            selectedFolder = selectedPhotoFolder,
                            strings = strings,
                            bottomContentPadding = pad,
                            gridColumns = settings.photoGridColumns,
                            showTitles = settings.photoShowTitles,
                            onSearchChange = viewModel::setPhotoSearchQuery,
                            onToggleFavoritesFilter = viewModel::togglePhotoFavoritesFilter,
                            onSelectSortOption = viewModel::setPhotoSortOption,
                            onSelectViewMode = viewModel::setPhotoViewMode,
                            onSelectFolderFilter = viewModel::setPhotoFolderFilter,
                            onOpenPhoto = viewModel::openPhoto,
                            onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                            onFavoriteSelected = viewModel::favoriteItemsByIds,
                            onRenamePhoto = {
                                viewModel.openRenameDialog(it.id, it.title, RenameMediaType.PHOTO)
                            },
                            onRemovePhoto = viewModel::removePhoto,
                            onRemoveSelected = viewModel::removePhotosByIds,
                            onShowDetails = viewModel::showDetails,
                            onSelectFolder = launchFolderPicker,
                            onImportFiles = launchFilesPicker,
                            onLoadDemoMedia = viewModel::loadDemoMedia,
                            stripExifOnShare = settings.stripExifOnShare,
                            folderCustomizations = folderCustomizations,
                            onLongPressFolder = { customizeFolderName = it },
                            onOpenPrivateVault = {
                                viewModel.refreshVaultItems()
                                showPrivateVault = true
                            },
                            onOpenCollageStudio = { collageInitialIds = it },
                            onOpenNote = { photo ->
                                noteTarget = Triple(photo.id, photo.title, RenameMediaType.PHOTO)
                            },
                            onShowBanner = viewModel::postBanner
                        )

                        MediaTab.SETTINGS -> PrismSettingsTabHost(
                            viewModel = viewModel,
                            settings = settings,
                            playbackState = playbackState,
                            securityAudit = securityAudit,
                            intruderLog = intruderLog,
                            watchedFolders = watchedFolders,
                            duplicateGroupsCount = duplicateGroups.size,
                            driverProfile = driverProfile,
                            trashedCount = trashedItems.size,
                            activeAudioOutput = activeAudioOutput,
                            strings = strings,
                            bottomContentPadding = pad,
                            onRequestNotificationsPermission = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    !MediaNotificationManager.hasNotificationPermission(context)
                                ) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            },
                            onSelectFolder = launchFolderPicker,
                            onImportFiles = launchFilesPicker,
                            onPickGallery = launchVisualPicker,
                            onExportBackup = { exportBackupLauncher.launch("prism_backup.json") },
                            onImportBackup = { importBackupLauncher.launch(arrayOf("application/json", "*/*")) },
                            onOpenStreamDialog = { showStreamUrlDialog = true },
                            onOpenWatchedFoldersManager = { showWatchedFoldersSheet = true },
                            onOpenStorageInsights = { showStorageInsightsDialog = true },
                            onOpenQuranHub = { showQuranHub = true },
                            onOpenMorningAlarm = { showMorningAlarm = true },
                            onOpenTasbih = { showTasbihDialog = true },
                            onOpenListeningStats = { showListeningStats = true },
                            onOpenPrivateVault = {
                                viewModel.refreshVaultItems()
                                showPrivateVault = true
                            },
                            onOpenCollageStudio = { collageInitialIds = emptySet() },
                            onOpenWifiTransfer = { showWifiTransfer = true },
                            onOpenCustomSeed = { showCustomSeed = true },
                            onOpenThemeCode = { showThemeCode = true },
                            onOpenAudioProfiles = {
                                viewModel.refreshDetectedAudioOutput(applyIfAuto = false)
                                showAudioProfiles = true
                            },
                            onOpenRecycleBin = { showRecycleBin = true },
                            onOpenGlobalSearch = { showSpotlightSearch = true },
                            onOpenAdhkarStudio = { showAdhkarStudio = true },
                            onPreviewLockScreen = { showLockScreenOverlay = true },
                            onOpenQuickCustomizer = { showQuickCustomizer = true },
                            onOpenProAudioStudio = { showProAudioStudio = true },
                            onOpenDefaultPlayerSettings = openDefaultPlayerSettings
                        )
                    }
                }

                Scaffold(
                    containerColor = Color.Transparent,
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.padding(
                                bottom = if (isWideScreen) 16.dp else if (hasDockedMiniPlayer) 210.dp else 96.dp
                            )
                        ) { data ->
                            Snackbar(
                                snackbarData = data,
                                shape = RoundedCornerShape(20.dp),
                                containerColor = MaterialTheme.colorScheme.inverseSurface,
                                contentColor = MaterialTheme.colorScheme.inverseOnSurface
                            )
                        }
                    }
                ) { _ ->
                    Row(modifier = Modifier.fillMaxSize()) {
                        if (isWideScreen) {
                            PrismNavigationRail(
                                selectedTab = selectedTab,
                                strings = strings,
                                iconStyle = settings.iconStyle,
                                tabOrder = settings.navTabOrder,
                                onSelectTab = viewModel::selectTab
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .statusBarsPadding()
                                    .displayCutoutPadding()
                            ) {
                                // Clean Expressive Header Bar with Global Spotlight Search + Customizable Quick Shortcuts
                                PrismTopHeader(
                                    title = when (selectedTab) {
                                        MediaTab.MUSIC -> strings.tabMusic
                                        MediaTab.VIDEOS -> strings.tabVideos
                                        MediaTab.PHOTOS -> strings.tabPhotos
                                        MediaTab.SETTINGS -> strings.tabSettings
                                    },
                                    appName = strings.appTitle,
                                    isScanning = isScanning,
                                    scanProgress = scanProgress,
                                    scanningCountText = if (scanProgress.discoveredCount > 0) {
                                        strings.scanningLiveCount(scanProgress.discoveredCount)
                                    } else {
                                        strings.scanningLibrary
                                    },
                                    multiWindowEnabled = multiWindowEnabled,
                                    multiWindowContentDesc = strings.multiWindowTitle,
                                    streamUrlContentDesc = strings.playStreamUrl,
                                    spotlightSearchContentDesc = strings.globalSearchTitle,
                                    importFilesLabel = strings.importFiles,
                                    moreActionsDesc = strings.moreHeaderActionsDesc,
                                    showStreamBtn = settings.headerShowStreamBtn,
                                    showMultiWindowBtn = settings.headerShowMultiWindowBtn,
                                    headerTitleStyle = settings.headerTitleStyle,
                                    isPlaying = playbackState.isPlaying,
                                    onOpenQuickStudio = { showQuickCustomizer = true },
                                    onOpenSpotlightSearch = { showSpotlightSearch = true },
                                    onToggleMultiWindow = viewModel::toggleMultiWindow,
                                    onOpenStreamDialog = { showStreamUrlDialog = true },
                                    onSelectFolder = {
                                        if (watchedFolders.isNotEmpty()) {
                                            showWatchedFoldersSheet = true
                                        } else {
                                            launchFolderPicker()
                                        }
                                    },
                                    onImportFiles = launchFilesPicker
                                )

                                // Main Content (120Hz Interactive Swipeable HorizontalPager respecting Custom Tab Order) or In-App Split-Screen Multi-Window
                                if (!multiWindowEnabled) {
                                    val orderedTabs = settings.navTabOrder
                                    val targetPage = orderedTabs.indexOf(selectedTab).coerceAtLeast(0)
                                    val pagerState = rememberPagerState(
                                        initialPage = targetPage,
                                        pageCount = { orderedTabs.size }
                                    )
                                    LaunchedEffect(selectedTab, orderedTabs) {
                                        val desired = orderedTabs.indexOf(selectedTab).coerceAtLeast(0)
                                        if (pagerState.currentPage != desired) {
                                            pagerState.animateScrollToPage(
                                                page = desired,
                                                animationSpec = spring(
                                                    dampingRatio = 0.88f,
                                                    stiffness = Spring.StiffnessMediumLow
                                                )
                                            )
                                        }
                                    }
                                    LaunchedEffect(pagerState.settledPage, orderedTabs) {
                                        val settledTab = orderedTabs.getOrNull(pagerState.settledPage) ?: return@LaunchedEffect
                                        if (settledTab != selectedTab) {
                                            viewModel.selectTab(settledTab)
                                        }
                                    }
                                    HorizontalPager(
                                        state = pagerState,
                                        beyondViewportPageCount = 3,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) { pageIndex ->
                                        val pageOffset = ((pagerState.currentPage - pageIndex) +
                                            pagerState.currentPageOffsetFraction).coerceIn(-1f, 1f)
                                        val absOffset = abs(pageOffset)
                                        val transitionStyle = settings.navTransitionStyle
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .graphicsLayer {
                                                    when (transitionStyle) {
                                                        NavTransitionStyle.EXPRESSIVE_SPRING -> {
                                                            val scale = 1f - 0.045f * absOffset
                                                            scaleX = scale
                                                            scaleY = scale
                                                            alpha = 1f - 0.16f * absOffset
                                                        }
                                                        NavTransitionStyle.PARALLAX_DEPTH -> {
                                                            cameraDistance = 14f * density
                                                            val scale = 1f - 0.10f * absOffset
                                                            scaleX = scale
                                                            scaleY = scale
                                                            rotationY = pageOffset * -10f
                                                            alpha = 1f - 0.24f * absOffset
                                                        }
                                                        NavTransitionStyle.LIQUID_MORPH -> {
                                                            scaleX = 1f - 0.08f * absOffset
                                                            scaleY = 1f - 0.035f * absOffset
                                                            translationY = absOffset * 34f
                                                            alpha = 1f - 0.28f * absOffset
                                                        }
                                                        NavTransitionStyle.SHARED_AXIS -> {
                                                            val scale = 1f - 0.03f * absOffset
                                                            scaleX = scale
                                                            scaleY = scale
                                                            translationX = pageOffset * 44f
                                                            alpha = 1f - 0.34f * absOffset
                                                        }
                                                    }
                                                }
                                        ) {
                                            val pageTab = orderedTabs.getOrElse(pageIndex) { MediaTab.MUSIC }
                                            primaryTabContent(pageTab, bottomPadding)
                                        }
                                    }
                                } else {
                                    // True In-App Multi-Window Split-Screen (Side-by-side on wide, Top/Bottom on portrait)
                                    val secondaryPaneContent: @Composable () -> Unit = {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                                .background(MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.72f))
                                        ) {
                                            MultiWindowSecondaryHeader(
                                                selectedMode = secondaryPaneMode,
                                                strings = strings,
                                                onSelectMode = viewModel::setSecondaryPaneMode,
                                                onCloseMultiWindow = viewModel::toggleMultiWindow
                                            )
                                            Box(modifier = Modifier.weight(1f)) {
                                                when (secondaryPaneMode) {
                                                    SecondaryPaneMode.MUSIC -> primaryTabContent(MediaTab.MUSIC, bottomPadding)
                                                    SecondaryPaneMode.VIDEOS -> primaryTabContent(MediaTab.VIDEOS, bottomPadding)
                                                    SecondaryPaneMode.PHOTOS -> primaryTabContent(MediaTab.PHOTOS, bottomPadding)
                                                    SecondaryPaneMode.EQUALIZER -> {
                                                        LazyColumn(
                                                            modifier = Modifier.fillMaxSize(),
                                                            contentPadding = PaddingValues(
                                                                start = 12.dp,
                                                                end = 12.dp,
                                                                top = 6.dp,
                                                                bottom = bottomPadding
                                                            ),
                                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                                        ) {
                                                            item {
                                                                EqualizerCard(
                                                                    gainsDb = settings.eqGainsDb,
                                                                    selectedPreset = settings.eqPreset,
                                                                    strings = strings,
                                                                    onSelectPreset = viewModel::selectEqPreset,
                                                                    onBandChange = viewModel::updateEqBand,
                                                                    onReset = viewModel::resetEqualizer,
                                                                    eqEnabled = settings.eqEnabled,
                                                                    bassBoost = settings.bassBoost,
                                                                    spectrumLevelsFlow = viewModel.spectrumLevels,
                                                                    isPlaying = playbackState.isPlaying,
                                                                    onToggleEq = viewModel::setEqEnabled,
                                                                    onBassBoostChange = viewModel::setBassBoost
                                                                )
                                                            }
                                                            item {
                                                                SleepTimerCard(
                                                                    remainingSec = playbackState.sleepTimerRemainingSec,
                                                                    totalMin = playbackState.sleepTimerTotalMin,
                                                                    strings = strings,
                                                                    onStartTimer = viewModel::startSleepTimer,
                                                                    onCancelTimer = viewModel::cancelSleepTimer
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (isWideScreen) {
                                        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                                primaryTabContent(selectedTab, bottomPadding)
                                            }
                                            VerticalDivider(
                                                thickness = 2.dp,
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                            )
                                            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                                secondaryPaneContent()
                                            }
                                        }
                                    } else {
                                        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                            Box(modifier = Modifier.weight(0.48f).fillMaxWidth()) {
                                                primaryTabContent(selectedTab, 16.dp)
                                            }
                                            HorizontalDivider(
                                                thickness = 2.dp,
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                            )
                                            Box(modifier = Modifier.weight(0.52f).fillMaxWidth()) {
                                                secondaryPaneContent()
                                            }
                                        }
                                    }
                                }
                            }

                            // Draggable Floating In-App Video Window (with Sticky Edge-Dock Handle & Drag-to-X!)
                            val miniVideo = floatingVideo
                            if (miniVideo != null && activeVideo == null) {
                                FloatingInAppVideoWindow(
                                    video = miniVideo,
                                    initialPositionMs = floatingVideoPosMs,
                                    strings = strings,
                                    onExpand = viewModel::expandFloatingVideo,
                                    onClose = viewModel::closeFloatingVideo,
                                    onPlayingStateChanged = viewModel::setVideoPlayingActive,
                                    dockSide = settings.miniPlayerEdgeDockSide,
                                    containerHeightPx = containerHeightPx,
                                    onDragStateChanged = { dragging, overX ->
                                        isDraggingFloatingWindow = dragging
                                        isHoveringDismissX = overX
                                    },
                                    modifier = Modifier
                                        .align(
                                            if (settings.miniPlayerEdgeDockSide == com.example.model.EdgeDockSide.LEFT) {
                                                Alignment.TopStart
                                            } else {
                                                Alignment.TopEnd
                                            }
                                        )
                                        .statusBarsPadding()
                                        .padding(top = 72.dp)
                                )
                            }

                            // Draggable Floating In-App Photo Window (with Sticky Edge-Dock Handle & Drag-to-X!)
                            val miniPhoto = floatingPhoto
                            if (miniPhoto != null && activePhotoIndex == null) {
                                FloatingInAppPhotoWindow(
                                    photo = miniPhoto,
                                    strings = strings,
                                    onExpand = viewModel::expandFloatingPhoto,
                                    onClose = viewModel::closeFloatingPhoto,
                                    dockSide = settings.miniPlayerEdgeDockSide,
                                    containerHeightPx = containerHeightPx,
                                    onDragStateChanged = { dragging, overX ->
                                        isDraggingFloatingWindow = dragging
                                        isHoveringDismissX = overX
                                    },
                                    modifier = Modifier
                                        .align(
                                            if (settings.miniPlayerEdgeDockSide == com.example.model.EdgeDockSide.LEFT) {
                                                Alignment.TopStart
                                            } else {
                                                Alignment.TopEnd
                                            }
                                        )
                                        .statusBarsPadding()
                                        .padding(top = if (miniVideo != null) 216.dp else 72.dp)
                                )
                            }

                            // Draggable Floating Material You Audio Bubble (when user taps Float on player bar!)
                            if (settings.miniPlayerFloating && playbackState.currentTrack != null) {
                                FloatingAudioBubbleWindow(
                                    playbackState = playbackState,
                                    settings = settings,
                                    positionMsFlow = viewModel.playbackPositionMs,
                                    strings = strings,
                                    containerHeightPx = containerHeightPx,
                                    onExpandFull = { viewModel.setPlayerExpanded(true) },
                                    onDockBack = { viewModel.setMiniPlayerFloating(false) },
                                    onToggleCompact = { viewModel.setMiniPlayerCompact(!settings.miniPlayerCompact) },
                                    onTogglePlayPause = viewModel::togglePlayPause,
                                    onNext = viewModel::playNextAudio,
                                    onSeek = viewModel::seekAudio,
                                    onDragStateChanged = { dragging, overX ->
                                        isDraggingFloatingWindow = dragging
                                        isHoveringDismissX = overX
                                    },
                                    onDropToStop = {
                                        viewModel.stopAllPlaybackAndClosePip()
                                        viewModel.setMiniPlayerFloating(false)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .statusBarsPadding()
                                        .padding(top = 80.dp, start = 14.dp)
                                )
                            }

                            // Persistent Floating Mini-Player (Customizable + Compact/Float + Squiggly) + Floating Pill Nav
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                FloatingMiniPlayer(
                                    playbackState = playbackState,
                                    settings = settings,
                                    positionMsFlow = viewModel.playbackPositionMs,
                                    spectrumLevelsFlow = viewModel.spectrumLevels,
                                    strings = strings,
                                    onExpand = { viewModel.setPlayerExpanded(true) },
                                    onTogglePlayPause = viewModel::togglePlayPause,
                                    onNext = viewModel::playNextAudio,
                                    onPrevious = viewModel::playPreviousAudio,
                                    onSeek = viewModel::seekAudio,
                                    onSeekRelative = viewModel::seekRelative,
                                    onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                                    onToggleShuffle = viewModel::toggleShuffle,
                                    onCycleRepeat = viewModel::cycleRepeatMode,
                                    onCycleSpeed = viewModel::cyclePlaybackSpeed,
                                    onToggleAbLoop = viewModel::toggleAbLoopPoint,
                                    onToggleCompact = { viewModel.setMiniPlayerCompact(!settings.miniPlayerCompact) },
                                    onToggleFloating = { viewModel.setMiniPlayerFloating(!settings.miniPlayerFloating) },
                                    onOpenCustomize = { showPlayerCustomizerSheet = true },
                                    onOpenQuickEq = { showMiniEqSheet = true },
                                    onOpenQuickTimer = { showMiniTimerSheet = true },
                                    onOpenQueue = { showMiniQueueSheet = true },
                                    modifier = if (isWideScreen) {
                                        Modifier
                                            .navigationBarsPadding()
                                            .padding(bottom = 12.dp)
                                    } else {
                                        Modifier.padding(bottom = 2.dp)
                                    }
                                )

                                if (!isWideScreen) {
                                    FloatingPillNavBar(
                                        selectedTab = selectedTab,
                                        strings = strings,
                                        iconStyle = settings.iconStyle,
                                        navBarStyle = settings.navBarStyle,
                                        tabOrder = settings.navTabOrder,
                                        onSelectTab = viewModel::selectTab
                                    )
                                }
                            }

                            // Google Drop-Through Drag-to-X Dismiss Target Zone
                            GoogleDismissDropZone(
                                visible = isDraggingFloatingWindow,
                                isHovered = isHoveringDismissX,
                                strings = strings,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }

                // Audio-Reactive Edge Lighting Glow
                AudioReactiveEdgeGlowOverlay(
                    spectrumLevelsFlow = viewModel.spectrumLevels,
                    isPlaying = playbackState.isPlaying,
                    enabled = settings.edgeGlowEnabled && !playbackState.isPlayerExpanded && activeVideo == null,
                    modifier = Modifier.fillMaxSize()
                )

                // Player Bar Customizer Modal Bottom Sheet
                if (showPlayerCustomizerSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showPlayerCustomizerSheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = strings.customizeBarTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            PlayerBarCustomizerContent(
                                settings = settings,
                                strings = strings,
                                onSquigglyChange = viewModel::setSquigglyProgress,
                                onCompactChange = viewModel::setMiniPlayerCompact,
                                onFloatingChange = viewModel::setMiniPlayerFloating,
                                onToggleAddon = viewModel::setBarAddon
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                // Quick Modals launched from Song Bar Companion Strip
                if (showMiniEqSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showMiniEqSheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        EqualizerCard(
                            gainsDb = settings.eqGainsDb,
                            selectedPreset = settings.eqPreset,
                            strings = strings,
                            onSelectPreset = viewModel::selectEqPreset,
                            onBandChange = viewModel::updateEqBand,
                            onReset = viewModel::resetEqualizer,
                            eqEnabled = settings.eqEnabled,
                            bassBoost = settings.bassBoost,
                            spectrumLevelsFlow = viewModel.spectrumLevels,
                            isPlaying = playbackState.isPlaying,
                            onToggleEq = viewModel::setEqEnabled,
                            onBassBoostChange = viewModel::setBassBoost,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                if (showMiniTimerSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showMiniTimerSheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        SleepTimerCard(
                            remainingSec = playbackState.sleepTimerRemainingSec,
                            totalMin = playbackState.sleepTimerTotalMin,
                            strings = strings,
                            onStartTimer = viewModel::startSleepTimer,
                            onCancelTimer = viewModel::cancelSleepTimer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                if (showMiniQueueSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showMiniQueueSheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        UpNextQueueSheetContent(
                            tracks = activeQueueTracks,
                            currentTrackId = playbackState.currentTrack?.id,
                            strings = strings,
                            onSelectTrack = {
                                viewModel.playAudio(it)
                                showMiniQueueSheet = false
                            },
                            onMoveUp = viewModel::moveQueueTrackUp,
                            onMoveDown = viewModel::moveQueueTrackDown,
                            onRemoveFromQueue = viewModel::removeTrackFromQueue,
                            onSortQueue = viewModel::sortActiveQueue
                        )
                    }
                }

                // Full-Screen Audio Player Sheet (Spring-Physics Expressive Sheet)
                AnimatedVisibility(
                    visible = playbackState.isPlayerExpanded && playbackState.currentTrack != null,
                    enter = slideInVertically(
                        animationSpec = spring(
                            dampingRatio = 0.88f,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) { it } + fadeIn(tween(180)),
                    exit = slideOutVertically(
                        animationSpec = spring(
                            dampingRatio = 0.92f,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) { it } + fadeOut(tween(150))
                ) {
                    val activeLyrics = playbackState.currentTrack?.let { tr ->
                        viewModel.resolveLyricsForTrack(tr, customLyrics)
                    }
                    FullPlayerSheet(
                        playbackState = playbackState,
                        positionMsFlow = viewModel.playbackPositionMs,
                        spectrumLevelsFlow = viewModel.spectrumLevels,
                        settings = settings,
                        queueTracks = activeQueueTracks,
                        lyricsText = activeLyrics,
                        strings = strings,
                        onDismiss = { viewModel.setPlayerExpanded(false) },
                        onTogglePlayPause = viewModel::togglePlayPause,
                        onNext = viewModel::playNextAudio,
                        onPrevious = viewModel::playPreviousAudio,
                        onSeek = viewModel::seekAudio,
                        onSeekRelative = viewModel::seekRelative,
                        onToggleShuffle = viewModel::toggleShuffle,
                        onCycleRepeat = viewModel::cycleRepeatMode,
                        onCycleSpeed = viewModel::cyclePlaybackSpeed,
                        onToggleAbLoop = viewModel::toggleAbLoopPoint,
                        onPlayFromQueue = viewModel::playAudio,
                        onMoveQueueUp = viewModel::moveQueueTrackUp,
                        onMoveQueueDown = viewModel::moveQueueTrackDown,
                        onRemoveFromQueue = viewModel::removeTrackFromQueue,
                        onSortQueue = viewModel::sortActiveQueue,
                        onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                        onSaveLyrics = viewModel::saveTrackLyrics,
                        onToggleEq = viewModel::setEqEnabled,
                        onBassBoostChange = viewModel::setBassBoost,
                        onSelectEqPreset = viewModel::selectEqPreset,
                        onResetEq = viewModel::resetEqualizer,
                        onBandChange = viewModel::updateEqBand,
                        onStartSleepTimer = viewModel::startSleepTimer,
                        onCancelSleepTimer = viewModel::cancelSleepTimer,
                        bookmarks = bookmarks,
                        onAddBookmark = viewModel::addAudioBookmark,
                        onRemoveBookmark = viewModel::removeBookmark,
                        onEditTags = viewModel::openTagEditor,
                        audioDriverLabel = driverProfile.audioBadgeLabel,
                        knowledgeReport = playbackState.currentTrack?.let { knowledgeReports[it.id] },
                        isEnriching = playbackState.currentTrack?.id in enrichingIds,
                        onRefreshKnowledge = {
                            playbackState.currentTrack?.let { viewModel.enrichAudioTrack(it, forceRefresh = true) }
                        },
                        onFetchOnlineLyrics = {
                            playbackState.currentTrack?.let { viewModel.fetchLyricsOnlineForTrack(it) }
                        },
                        onToggleKhushuMode = viewModel::setKhushuModeEnabled,
                        onOpenAudioCutter = { audioCutterTarget = it },
                        onOpenTasbih = { showTasbihDialog = true },
                        onToggleMute = viewModel::toggleAudioMute,
                        onToggleDriveMode = viewModel::setDriveModeEnabled,
                        onPreampChange = viewModel::setAudioPreampPercent,
                        onPitchChange = viewModel::setAudioPitch,
                        onCrossfadeChange = viewModel::setCrossfadeSeconds,
                        onNormalizationChange = viewModel::setAudioNormalization,
                        onStereoBalanceChange = viewModel::setAudioBalance,
                        onMonoChange = viewModel::setAudioMonoEnabled,
                        onSkipSilenceChange = viewModel::setSkipSilenceEnabled,
                        onLectureModeChange = viewModel::setLectureModeEnabled,
                        onHeadsetUnplugChange = viewModel::setPauseOnHeadsetUnplug,
                        hifzRepeatTarget = quranKhatmaProgress.hifzRepeatTarget,
                        onHifzRepeatChange = viewModel::setQuranHifzRepeat,
                        onSpectrumStyleChange = viewModel::setSpectrumVisualStyle,
                        onArtworkStyleChange = viewModel::setPlayerArtworkStyle,
                        onSwipeArtToggle = viewModel::setPlayerGestureSwipeArt,
                        onLyricsFontSizeChange = viewModel::setLyricsFontSizeSp,
                        onReverbChange = viewModel::setReverbEnvironment,
                        hasNote = playbackState.currentTrack?.id in mediaNotes,
                        onOpenNote = { tr ->
                            noteTarget = Triple(tr.id, tr.title, RenameMediaType.AUDIO)
                        },
                        onOpenAudioProfiles = {
                            viewModel.refreshDetectedAudioOutput(applyIfAuto = false)
                            showAudioProfiles = true
                        },
                        onRequestMusicPip = if (settings.pipMusicEnabled) onEnterPipNow else null,
                        onOpenProAudioStudio = { showProAudioStudio = true },
                        onToggleAutoStudio = viewModel::setAutoStudioMasteringEnabled,
                        onToggleDspBypass = viewModel::setDspBypassEnabled
                    )
                }

                // Smart Video Cinema Player Overlay
                val currentVideo = activeVideo
                AnimatedVisibility(
                    visible = currentVideo != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (currentVideo != null) {
                        VideoPlayerScreen(
                            video = currentVideo,
                            isInPipMode = false,
                            strings = strings,
                            initialPositionMs = activeVideoInitialPosMs,
                            defaultAspectIndex = settings.videoDefaultAspect,
                            defaultSpeed = settings.videoDefaultSpeed,
                            defaultLoop = settings.videoDefaultLoop,
                            preferHardwareDecoding = settings.hardwareVideoAcceleration,
                            seekStepSeconds = settings.seekStepSeconds,
                            gesturesEnabled = settings.videoGesturesEnabled,
                            doubleTapSeekEnabled = settings.videoDoubleTapSeekEnabled,
                            defaultSubtitleSizeSp = settings.videoSubtitleSizeSp,
                            subtitleColor = settings.videoSubtitleColor,
                            subtitleBgOpacity = settings.videoSubtitleBgOpacity,
                            subtitleBottomOffsetDp = settings.videoSubtitleBottomOffsetDp,
                            defaultMirrorEnabled = settings.videoMirrorEnabled,
                            autoHideSeconds = settings.videoAutoHideSeconds,
                            onToggleMirror = viewModel::setVideoMirrorEnabled,
                            videoBookmarks = bookmarks.filter { it.mediaId == currentVideo.id },
                            onAddBookmark = { posMs, label ->
                                viewModel.addVideoChapterBookmark(currentVideo.id, posMs, label)
                            },
                            onRemoveBookmark = viewModel::removeBookmark,
                            hasPrevNext = videos.size > 1,
                            onPreviousVideo = viewModel::playPreviousVideo,
                            onNextVideo = viewModel::playNextVideo,
                            onPlaybackActiveChanged = viewModel::setVideoPlayingActive,
                            onMinimizeToFloating = viewModel::minimizeVideoToFloating,
                            onPlayAsAudio = { posMs -> viewModel.playVideoAsAudio(currentVideo, posMs) },
                            onRequestSystemPip = if (settings.pipVideoEnabled) onEnterPipNow else null,
                            onRegisterPipToggle = { videoPipToggleCallback = it },
                            onClose = viewModel::closeVideo,
                            autoNextEnabled = settings.videoAutoNextEnabled,
                            hasNote = currentVideo.id in mediaNotes,
                            onOpenNote = {
                                noteTarget = Triple(currentVideo.id, currentVideo.title, RenameMediaType.VIDEO)
                            }
                        )
                    }
                }

                // Photo Lightbox Overlay
                val photoIdx = activePhotoIndex
                AnimatedVisibility(
                    visible = photoIdx != null && photos.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (photoIdx != null && photos.isNotEmpty()) {
                        PhotoLightboxViewer(
                            photos = photos,
                            initialIndex = photoIdx,
                            strings = strings,
                            slideshowSeconds = settings.photoSlideshowSeconds,
                            stripExifOnShare = settings.stripExifOnShare,
                            onMoveToVault = { photo ->
                                runCatching { Uri.parse(photo.uriString) }.getOrNull()?.let { uri ->
                                    viewModel.importIntoVaultUri(uri)
                                }
                            },
                            onMinimizeToFloating = viewModel::minimizePhotoToFloating,
                            onRequestSystemPip = if (settings.pipPhotoEnabled) onEnterPipNow else null,
                            onClose = viewModel::closePhoto,
                            onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                            onOpenNote = { photo ->
                                noteTarget = Triple(photo.id, photo.title, RenameMediaType.PHOTO)
                            }
                        )
                    }
                }

                // File Details Dialog + Live Online Encyclopedia / Quran / Hadith / GPS Knowledge
                val info = detailsInfo
                if (info != null) {
                    val infoKey = info.mediaId.ifBlank { info.uriString }
                    MediaDetailsDialog(
                        details = info,
                        strings = strings,
                        onDismiss = viewModel::dismissDetails,
                        knowledgeReport = knowledgeReports[infoKey],
                        isEnriching = infoKey in enrichingIds,
                        onRefreshKnowledge = { viewModel.enrichMediaDetails(info, forceRefresh = true) }
                    )
                }

                // Rename Media Dialog
                val renaming = renameTarget
                if (renaming != null) {
                    RenameMediaDialog(
                        target = renaming,
                        strings = strings,
                        onDismiss = viewModel::dismissRenameDialog,
                        onConfirm = viewModel::confirmRename
                    )
                }

                // Add Track to Playlist Dialog
                val trackForPlaylist = playlistTrackTarget
                if (trackForPlaylist != null) {
                    PlaylistPickerDialog(
                        track = trackForPlaylist,
                        playlists = playlists,
                        strings = strings,
                        onToggleTrackInPlaylist = viewModel::toggleTrackInPlaylist,
                        onCreatePlaylist = { name, trackId -> viewModel.createPlaylist(name, trackId) },
                        onDismiss = viewModel::dismissPlaylistPicker
                    )
                }

                // Direct HTTPS Stream URL Dialog
                if (showStreamUrlDialog) {
                    StreamUrlDialog(
                        strings = strings,
                        onDismiss = { showStreamUrlDialog = false },
                        onPlayStream = viewModel::playStreamUrl
                    )
                }

                // Batch Add Selected Tracks to Playlist Dialog
                if (batchPlaylistTrackIds.isNotEmpty()) {
                    var newBatchPlaylistName by remember { mutableStateOf("") }
                    AlertDialog(
                        onDismissRequest = viewModel::dismissBatchPlaylistPicker,
                        shape = RoundedCornerShape(28.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        title = {
                            Text(
                                text = strings.playlistsTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newBatchPlaylistName,
                                        onValueChange = { newBatchPlaylistName = it },
                                        placeholder = { Text(strings.playlistNameHint) },
                                        singleLine = true,
                                        shape = CircleShape,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape)
                                            .clickable(enabled = newBatchPlaylistName.isNotBlank()) {
                                                viewModel.createPlaylistWithTracks(
                                                    newBatchPlaylistName,
                                                    batchPlaylistTrackIds
                                                )
                                                newBatchPlaylistName = ""
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Filled.Add,
                                                contentDescription = strings.createPlaylist,
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                }
                                playlists.forEach { pl ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable {
                                                viewModel.addBatchTracksToPlaylist(pl.id, batchPlaylistTrackIds)
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = pl.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = strings.playlistTrackCount(pl.trackIds.size),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = viewModel::dismissBatchPlaylistPicker) {
                                Text(strings.close)
                            }
                        }
                    )
                }

                // Anti-Tamper Integrity Alert Dialog (triggers if unauthorized APK modification is detected)
                if (showTamperAlertDialog) {
                    val screenProfile = remember(context) { AppSecurityVerifier.inspectScreenProfile(context) }
                    AlertDialog(
                        onDismissRequest = viewModel::dismissTamperAlertDialog,
                        shape = RoundedCornerShape(28.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        title = {
                            Text(
                                text = strings.tamperDialogTitle,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = strings.tamperDialogMessage,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                securityAudit.tamperReasons.forEach { reason ->
                                    Text(
                                        text = "• $reason",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    AppSecurityVerifier.launchTamperAlertEmail(
                                        context = context,
                                        audit = securityAudit,
                                        screen = screenProfile,
                                        intruderSummaries = intruderLog.map { "${it.formattedTime} — ${it.note}" }
                                    )
                                    viewModel.dismissTamperAlertDialog()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = CircleShape,
                                modifier = Modifier.testTag("tamper_alert_send_email")
                            ) {
                                Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.sendSecurityReportAction, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = viewModel::dismissTamperAlertDialog) {
                                Text(strings.close)
                            }
                        }
                    )
                }

                // Watched Folders Manager Bottom Sheet
                if (showWatchedFoldersSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showWatchedFoldersSheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        WatchedFoldersManagerSheetContent(
                            folders = watchedFolders,
                            autoScanEnabled = settings.autoScanWatchedFolders,
                            isScanning = isScanning,
                            strings = strings,
                            onAddFolder = launchFolderPicker,
                            onToggleFolder = viewModel::toggleWatchedFolder,
                            onRescanFolder = viewModel::rescanSingleFolder,
                            onRemoveFolder = viewModel::removeWatchedFolder,
                            onRescanAll = viewModel::rescanSavedFolders,
                            onAutoScanChange = viewModel::setAutoScanWatchedFolders
                        )
                    }
                }

                // Storage Insights & Duplicate File Finder Dialog
                if (showStorageInsightsDialog) {
                    StorageInsightsDialog(
                        audioTracks = rawAudioList,
                        videos = rawVideosList,
                        photos = rawPhotosList,
                        duplicateGroups = duplicateGroups,
                        strings = strings,
                        onCleanGroup = viewModel::cleanDuplicateGroup,
                        onCleanAll = viewModel::cleanAllDuplicates,
                        onDismiss = { showStorageInsightsDialog = false }
                    )
                }

                // Audio Tag / Metadata Editor Dialog
                val tagTarget = editTagTarget
                if (tagTarget != null) {
                    EditAudioTagsDialog(
                        track = tagTarget,
                        strings = strings,
                        onDismiss = viewModel::dismissTagEditor,
                        onSave = { title, artist, album ->
                            viewModel.saveTrackTags(tagTarget.id, title, artist, album)
                        }
                    )
                }

                // 1) Quran Mushaf & 114-Surah Khatma Hub
                if (showQuranHub) {
                    QuranMushafKhatmaSheet(
                        progress = quranKhatmaProgress,
                        khushuModeEnabled = settings.khushuModeEnabled,
                        audioTracks = rawAudioList,
                        strings = strings,
                        onToggleSurahCompleted = viewModel::toggleQuranSurahCompleted,
                        onSetHifzRepeat = viewModel::setQuranHifzRepeat,
                        onToggleKhushuMode = viewModel::setKhushuModeEnabled,
                        onPlayMatchingTrack = { tr ->
                            viewModel.playAudio(tr)
                            showQuranHub = false
                        },
                        onOpenTasbih = {
                            showQuranHub = false
                            showTasbihDialog = true
                        },
                        onDismiss = { showQuranHub = false }
                    )
                }

                // 2) Adhkar & Tasbih Counter Dialog
                if (showTasbihDialog) {
                    TasbihCounterDialog(
                        dailyCount = settings.tasbihDailyCount,
                        strings = strings,
                        onIncrementDaily = { viewModel.setTasbihDailyCount(settings.tasbihDailyCount + it) },
                        onResetDaily = { viewModel.setTasbihDailyCount(0) },
                        onDismiss = { showTasbihDialog = false }
                    )
                }

                // 3) Morning Quran & Nasheed Gentle Alarm Scheduler
                if (showMorningAlarm) {
                    MorningAlarmSheet(
                        config = morningAlarmConfig,
                        audioTracks = rawAudioList,
                        strings = strings,
                        onSaveConfig = { cfg ->
                            viewModel.saveMorningAlarm(cfg)
                            viewModel.postBanner(strings.morningAlarmTitle)
                            showMorningAlarm = false
                        },
                        onPreviewFadeIn = { track, sec ->
                            viewModel.previewMorningAlarmFadeIn(track, sec)
                        },
                        onDismiss = { showMorningAlarm = false }
                    )
                }

                // 4) Audio Trimmer & Ringtone Cutter Studio
                val cutTarget = audioCutterTarget
                if (cutTarget != null) {
                    AudioCutterSheet(
                        track = cutTarget,
                        strings = strings,
                        onPreviewRange = { startMs -> viewModel.seekAudio(startMs) },
                        onTrimCompleted = { result ->
                            viewModel.postBanner(
                                if (result.success) strings.audioCutterSavedToast else (result.errorMessage ?: strings.photoSaveFailedBanner)
                            )
                        },
                        onDismiss = { audioCutterTarget = null }
                    )
                }

                // 5) Listening Stats & Monthly Recap Dashboard
                if (showListeningStats) {
                    ListeningStatsSheet(
                        stats = listeningStats,
                        strings = strings,
                        onDismiss = { showListeningStats = false }
                    )
                }

                // 6) Private AES-256-GCM Encrypted Media Vault
                if (showPrivateVault) {
                    PrivateVaultSheet(
                        vaultItems = vaultItems,
                        strings = strings,
                        onImportUri = viewModel::importIntoVaultUri,
                        onRestoreItem = viewModel::restoreVaultItem,
                        onDeleteItem = viewModel::deleteVaultItem,
                        onDismiss = { showPrivateVault = false }
                    )
                }

                // 7) HD Photo Collage & Watermark Studio
                val collageIds = collageInitialIds
                if (collageIds != null) {
                    PhotoCollageStudioDialog(
                        photos = rawPhotosList,
                        initialSelectedIds = collageIds,
                        strings = strings,
                        onCollageSaved = { ok ->
                            viewModel.postBanner(if (ok) strings.photoSavedCopyBanner else strings.photoSaveFailedBanner)
                            if (ok) collageInitialIds = null
                        },
                        onDismiss = { collageInitialIds = null }
                    )
                }

                // 8) Batch Audio Tag & Cover Art Editor
                if (batchTagTargetIds.isNotEmpty()) {
                    BatchTagEditorDialog(
                        selectedCount = batchTagTargetIds.size,
                        strings = strings,
                        onApply = { artist, album, artworkUri ->
                            viewModel.saveBatchTrackTags(batchTagTargetIds, artist, album, artworkUri)
                            viewModel.postBanner(strings.batchTagTitle)
                            batchTagTargetIds = emptySet()
                        },
                        onDismiss = { batchTagTargetIds = emptySet() }
                    )
                }

                // 9) Custom Monet Seed Color Picker & Photo Color Extractor
                if (showCustomSeed) {
                    CustomSeedColorDialog(
                        currentSeedArgb = settings.customSeedColorArgb,
                        strings = strings,
                        onSelectSeedArgb = { argb ->
                            viewModel.setCustomSeedColorArgb(argb)
                            showCustomSeed = false
                        },
                        onDismiss = { showCustomSeed = false }
                    )
                }

                // 10) Shareable Theme Code Studio
                if (showThemeCode) {
                    ThemeCodeDialog(
                        settings = settings,
                        strings = strings,
                        onApplyThemePreset = { preset ->
                            viewModel.applyDecodedThemePreset(preset)
                            viewModel.postBanner(strings.themeCodeApply)
                            showThemeCode = false
                        },
                        onDismiss = { showThemeCode = false }
                    )
                }

                // 11) Cloud-Free Local Wi-Fi File Transfer Server Dialog
                if (showWifiTransfer) {
                    WifiTransferDialog(
                        status = wifiTransferStatus,
                        strings = strings,
                        onStartServer = viewModel::startWifiTransfer,
                        onStopServer = viewModel::stopWifiTransfer,
                        onDismiss = { showWifiTransfer = false }
                    )
                }

                // 12) Folder Pin, Tint & Badge Customizer Dialog
                val folderToCustomize = customizeFolderName
                if (folderToCustomize != null) {
                    FolderCustomizeDialog(
                        folderName = folderToCustomize,
                        existing = folderCustomizations[folderToCustomize],
                        strings = strings,
                        onSave = { custom ->
                            viewModel.saveFolderCustomization(custom)
                            customizeFolderName = null
                        },
                        onDismiss = { customizeFolderName = null }
                    )
                }

                // 13) Global Spotlight Search Sheet (Audio, Videos, Photos, Playlists, Notes & Quran)
                if (showSpotlightSearch) {
                    GlobalSpotlightSearchSheet(
                        audioTracks = rawAudioList,
                        videos = rawVideosList,
                        photos = rawPhotosList,
                        playlists = playlists,
                        mediaNotes = mediaNotes,
                        onDismiss = { showSpotlightSearch = false },
                        onPlayAudio = { tr ->
                            viewModel.playAudio(tr)
                            showSpotlightSearch = false
                        },
                        onOpenVideo = { vid ->
                            viewModel.openVideo(vid)
                            showSpotlightSearch = false
                        },
                        onOpenPhoto = { idx ->
                            viewModel.openPhoto(idx)
                            showSpotlightSearch = false
                        },
                        onSelectPlaylist = { plId ->
                            viewModel.selectPlaylist(plId)
                            viewModel.selectTab(MediaTab.MUSIC)
                            showSpotlightSearch = false
                        },
                        onOpenQuranHub = {
                            showSpotlightSearch = false
                            showQuranHub = true
                        }
                    )
                }

                // 14) Smart Recycle Bin Sheet
                if (showRecycleBin) {
                    RecycleBinSheet(
                        trashedItems = trashedItems,
                        onDismiss = { showRecycleBin = false },
                        onRestoreItem = viewModel::restoreTrashedItem,
                        onRestoreAll = viewModel::restoreAllTrashed,
                        onEmptyBin = viewModel::emptyRecycleBin
                    )
                }

                // 15) Morning & Evening Adhkar Studio Sheet
                if (showAdhkarStudio) {
                    MorningEveningAdhkarSheet(
                        language = settings.language,
                        completedKeys = settings.adhkarCompletedKeys,
                        onToggleCompleted = viewModel::toggleAdhkarCompleted,
                        onResetAll = viewModel::resetDailyAdhkar,
                        onDismiss = { showAdhkarStudio = false }
                    )
                }

                // 16) Audio Output Device Profiles Sheet (Speaker / Wired / Bluetooth)
                if (showAudioProfiles) {
                    AudioDeviceProfilesSheet(
                        activeOutput = activeAudioOutput,
                        profiles = audioDeviceProfiles,
                        autoSwitchEnabled = settings.autoAudioDeviceProfiles,
                        onToggleAutoSwitch = viewModel::setAutoAudioDeviceProfiles,
                        onSaveProfile = viewModel::saveAudioDeviceProfile,
                        onApplyProfile = { type -> viewModel.applyAudioDeviceProfile(type, showBanner = true) },
                        onRefreshOutput = { viewModel.refreshDetectedAudioOutput(applyIfAuto = false) },
                        onDismiss = { showAudioProfiles = false }
                    )
                }

                // 17) Personal Media Notes Dialog (Audio / Video / Photo)
                val activeNoteTarget = noteTarget
                if (activeNoteTarget != null) {
                    val (nId, nTitle, nType) = activeNoteTarget
                    val currentPos = if (nType == RenameMediaType.AUDIO && playbackState.currentTrack?.id == nId) {
                        viewModel.playbackPositionMs.value
                    } else 0L
                    MediaNoteDialog(
                        mediaId = nId,
                        mediaTitle = nTitle,
                        mediaType = nType,
                        existingNote = mediaNotes[nId],
                        currentPositionMs = currentPos,
                        onDismiss = { noteTarget = null },
                        onSave = { text, ts ->
                            viewModel.saveMediaNote(nId, nTitle, nType, text, ts)
                            noteTarget = null
                        },
                        onDelete = {
                            viewModel.deleteMediaNote(nId)
                            noteTarget = null
                        }
                    )
                }

                // 18) Lossless MP4 Video Trimmer Dialog
                val activeTrimVideo = trimVideoTarget
                if (activeTrimVideo != null) {
                    VideoTrimmerDialog(
                        video = activeTrimVideo,
                        onDismiss = { trimVideoTarget = null },
                        onTrimmed = { trimmedClip ->
                            viewModel.addTrimmedVideo(trimmedClip)
                            trimVideoTarget = null
                        },
                        onError = { err ->
                            viewModel.postBanner(err)
                            trimVideoTarget = null
                        }
                    )
                }

                // Dreamlike 4-Page First-Launch Onboarding & Interactive Setup Wizard
                if (showFirstLaunchWizard && !isAppLocked) {
                    DreamOnboardingWizard(
                        settings = settings,
                        watchedFolders = watchedFolders,
                        scanProgress = scanProgress,
                        strings = strings,
                        onLanguageChange = viewModel::setLanguage,
                        onDarkThemeChange = viewModel::setDarkTheme,
                        onAmoledBlackChange = viewModel::setAmoledBlack,
                        onLiquidGlassStyleChange = viewModel::setLiquidGlassStyle,
                        onPaletteChange = viewModel::setAccentPalette,
                        onAutoScanChange = viewModel::setAutoScanWatchedFolders,
                        onAutoPipChange = viewModel::setAutoPipEnabled,
                        onAutoAudioProfilesChange = viewModel::setAutoAudioDeviceProfiles,
                        onOnlineEnrichmentChange = viewModel::setAutoOnlineEnrichment,
                        onNotificationsChange = { enabled ->
                            viewModel.setNotificationsEnabled(enabled)
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                !MediaNotificationManager.hasNotificationPermission(context)
                            ) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        },
                        onHapticsChange = viewModel::setHapticsEnabled,
                        onAddFolder = launchFolderPicker,
                        onRemoveFolder = viewModel::removeWatchedFolder,
                        onFinishSetup = viewModel::completeFirstLaunchSetup
                    )
                }

                // Smart Zero-Friction Media & Allowed-Folder Import Sheet (Fixes Android 11-16 SAF root restriction)
                if (showSmartImportSheet) {
                    SmartMediaImportSheet(
                        activeTab = selectedTab,
                        strings = strings,
                        onDismiss = { showSmartImportSheet = false },
                        onLaunchFolderWithSubpath = { subpath ->
                            showSmartImportSheet = false
                            launchSafFolderSubpath(subpath)
                        },
                        onLaunchDirectFilesPicker = {
                            showSmartImportSheet = false
                            launchFilesPicker()
                        },
                        onLaunchDownloadsFilesPicker = {
                            showSmartImportSheet = false
                            launchDownloadsFilesPicker()
                        },
                        onLaunchVisualGalleryPicker = {
                            showSmartImportSheet = false
                            launchVisualPicker()
                        }
                    )
                }

                // 1-Tap Live Interactive Quick Customization Studio Sheet (Accessible from Prism Logo on any tab!)
                if (showQuickCustomizer) {
                    com.example.ui.components.QuickCustomizerSheet(
                        settings = settings,
                        strings = strings,
                        onDismiss = { showQuickCustomizer = false },
                        onDarkThemeChange = viewModel::setDarkTheme,
                        onAmoledBlackChange = viewModel::setAmoledBlack,
                        onAccentPaletteChange = viewModel::setAccentPalette,
                        onLiquidGlassStyleChange = viewModel::setLiquidGlassStyle,
                        onCardStyleChange = viewModel::setCardStyle,
                        onBackdropStyleChange = viewModel::setBackdropStyle,
                        onMotionFeelChange = viewModel::setMotionFeelStyle,
                        onHeaderStyleChange = viewModel::setHeaderTitleStyle,
                        onShapeRoundnessChange = viewModel::setShapeRoundness,
                        onAccentStrengthChange = viewModel::setAccentStrength,
                        onSwipeGesturesChange = viewModel::setSwipeGesturesEnabled,
                        onShowFormatBadgesChange = viewModel::setShowFormatBadges,
                        onApplyQuickThemePreset = viewModel::applyQuickThemePreset
                    )
                }

                // Beta 4.4 Pro Audio Mastering Studio Modal Sheet (10-Band EQ + Auto-Studio Mastering + 15+ Real DSP Controls)
                if (showProAudioStudio) {
                    com.example.ui.components.ProAudioStudioModalSheet(
                        settings = settings,
                        vuLevelsFlow = viewModel.vuLevels,
                        detectedAutoProfile = detectedAutoStudioProfile,
                        customPresets = customStudioPresets,
                        strings = strings,
                        onDismiss = { showProAudioStudio = false },
                        onToggleProEq = viewModel::setProEqEnabled,
                        onSelectProPreset = viewModel::selectProEqPreset,
                        onUpdateTenBand = viewModel::updateProTenBand,
                        onToggleAutoStudio = viewModel::setAutoStudioMasteringEnabled,
                        onAnalyzeAutoNow = viewModel::analyzeAndApplyAutoStudioNow,
                        onTubeWarmthChange = viewModel::setTubeWarmth,
                        onStereoWidthChange = viewModel::setStereoWidth,
                        onCrossfeedChange = viewModel::setCrossfeedEnabled,
                        onSubBassPunchChange = viewModel::setSubBassPunch,
                        onAirBrillianceChange = viewModel::setAirBrilliance,
                        onVocalClarityChange = viewModel::setVocalClarity,
                        onCompressorChange = viewModel::setCompressorStrength,
                        onTruePeakLimiterChange = viewModel::setTruePeakLimiterEnabled,
                        onNightModeChange = viewModel::setNightModeEnabled,
                        onRumbleFilterChange = viewModel::setRumbleFilterHz,
                        onChannelSwapChange = viewModel::setChannelSwapEnabled,
                        onHeadphoneProfileChange = viewModel::setHeadphoneProfile,
                        onBypassChange = viewModel::setDspBypassEnabled,
                        onReverbChange = viewModel::setReverbEnvironment,
                        onPreampChange = viewModel::setAudioPreampPercent,
                        onNormalizationChange = viewModel::setAudioNormalization,
                        onStereoBalanceChange = viewModel::setAudioBalance,
                        onMonoChange = viewModel::setAudioMonoEnabled,
                        onResetAll = viewModel::resetProStudioAll,
                        onSaveCustomPreset = viewModel::saveCurrentAsCustomStudioPreset,
                        onApplyCustomPreset = viewModel::applyCustomStudioPreset,
                        onDeleteCustomPreset = viewModel::deleteCustomStudioPreset,
                    )
                }

                // Prism Full-Screen Lock-Screen Identity & Playback Overlay (Active on Lock Screen & Live Settings Preview)
                if (showLockScreenOverlay && !isAppLocked) {
                    val lockLyrics = playbackState.currentTrack?.let { tr ->
                        viewModel.resolveLyricsForTrack(tr, customLyrics)
                    }
                    LockScreenPlayerOverlay(
                        playbackState = playbackState,
                        positionMsFlow = viewModel.playbackPositionMs,
                        settings = settings,
                        lyricsText = lockLyrics,
                        knowledgeReport = playbackState.currentTrack?.let { knowledgeReports[it.id] },
                        strings = strings,
                        onTogglePlayPause = viewModel::togglePlayPause,
                        onNext = viewModel::playNextAudio,
                        onPrevious = viewModel::playPreviousAudio,
                        onSeek = viewModel::seekAudio,
                        onToggleFavorite = { viewModel.toggleFavorite(it.id) },
                        onDismissToApp = { showLockScreenOverlay = false }
                    )
                }

                // Full-Screen App Lock PIN Vault Overlay (Topmost z-order gate blocking intruders)
                if (isAppLocked) {
                    AppLockScreenOverlay(
                        failedAttempts = failedPinAttempts,
                        lockoutUntilMs = lockoutUntilMs,
                        strings = strings,
                        onVerifyPin = viewModel::verifyUnlockPin,
                        biometricEnabled = settings.biometricUnlockEnabled,
                        onBiometricUnlock = viewModel::unlockWithBiometric
                    )
                }
            }
        }
    }
}

@Composable
private fun PrismTopHeader(
    title: String,
    appName: String,
    isScanning: Boolean,
    scanProgress: ScanProgressState = ScanProgressState(),
    scanningCountText: String = "",
    multiWindowEnabled: Boolean,
    multiWindowContentDesc: String,
    streamUrlContentDesc: String,
    spotlightSearchContentDesc: String = "Search",
    importFilesLabel: String = "Import files",
    moreActionsDesc: String = "More",
    showStreamBtn: Boolean = true,
    showMultiWindowBtn: Boolean = true,
    headerTitleStyle: com.example.model.HeaderTitleStyle = com.example.model.HeaderTitleStyle.EXPRESSIVE_BOLD,
    isPlaying: Boolean = false,
    onOpenQuickStudio: () -> Unit = {},
    onOpenSpotlightSearch: () -> Unit = {},
    onToggleMultiWindow: () -> Unit,
    onOpenStreamDialog: () -> Unit,
    onSelectFolder: () -> Unit,
    onImportFiles: () -> Unit,
) {
    val iconStyle = LocalIconPackStyle.current
    var showMoreMenu by remember { mutableStateOf(false) }
    val logoInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val logoIcon = when (iconStyle) {
        IconPackStyle.OUTLINED_MINIMAL -> Icons.Outlined.GraphicEq
        else -> Icons.Rounded.GraphicEq
    }
    val logoShape = when (iconStyle) {
        IconPackStyle.FILLED_EXPRESSIVE -> CircleShape
        IconPackStyle.OUTLINED_MINIMAL -> RoundedCornerShape(12.dp)
        else -> RoundedCornerShape(16.dp)
    }
    val logoSize = if (headerTitleStyle == com.example.model.HeaderTitleStyle.MINIMAL_COMPACT) 38.dp else 44.dp
    val vPad = if (headerTitleStyle == com.example.model.HeaderTitleStyle.MINIMAL_COMPACT) 6.dp else 10.dp

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = vPad),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive Prism Emblem — Tap from ANY tab to open Live Quick Customization Studio!
            Surface(
                onClick = onOpenQuickStudio,
                interactionSource = logoInteraction,
                shape = logoShape,
                color = if (iconStyle == IconPackStyle.OUTLINED_MINIMAL) {
                    Color.Transparent
                } else if (isPlaying) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                },
                border = if (iconStyle == IconPackStyle.OUTLINED_MINIMAL) {
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                } else if (isPlaying) {
                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f))
                } else {
                    null
                },
                modifier = Modifier
                    .size(logoSize)
                    .expressivePressScale(logoInteraction, pressedScale = 0.88f)
                    .testTag("header_quick_studio_emblem")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = logoIcon,
                        contentDescription = appName,
                        tint = if (iconStyle == IconPackStyle.OUTLINED_MINIMAL) {
                            MaterialTheme.colorScheme.primary
                        } else if (isPlaying) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        },
                        modifier = Modifier.size(if (headerTitleStyle == com.example.model.HeaderTitleStyle.MINIMAL_COMPACT) 20.dp else 24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = if (headerTitleStyle == com.example.model.HeaderTitleStyle.CENTERED_ELEGANT) {
                    Alignment.CenterHorizontally
                } else {
                    Alignment.Start
                }
            ) {
                if (headerTitleStyle != com.example.model.HeaderTitleStyle.MINIMAL_COMPACT || isScanning) {
                    Text(
                        text = if (isScanning && scanningCountText.isNotBlank()) scanningCountText else appName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = title,
                    style = when (headerTitleStyle) {
                        com.example.model.HeaderTitleStyle.MINIMAL_COMPACT -> MaterialTheme.typography.titleLarge
                        com.example.model.HeaderTitleStyle.CENTERED_ELEGANT -> MaterialTheme.typography.titleLarge
                        else -> MaterialTheme.typography.headlineSmall
                    },
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Calm 3-action Material 3 Header: Spotlight Search • Smart Import • More Overflow Menu
            IconButton(
                onClick = onOpenSpotlightSearch,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("header_spotlight_search")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = spotlightSearchContentDesc,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = onSelectFolder,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("header_select_folder")
            ) {
                Icon(
                    imageVector = Icons.Rounded.LibraryAdd,
                    contentDescription = "Select folder",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Box {
                IconButton(
                    onClick = { showMoreMenu = true },
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("header_more_menu")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = moreActionsDesc,
                        tint = if (multiWindowEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
                DropdownMenu(
                    expanded = showMoreMenu,
                    onDismissRequest = { showMoreMenu = false },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(Icons.Rounded.AudioFile, contentDescription = null)
                        },
                        text = { Text(importFilesLabel) },
                        onClick = {
                            showMoreMenu = false
                            onImportFiles()
                        },
                        modifier = Modifier.testTag("header_import_files")
                    )
                    if (showStreamBtn) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(Icons.Rounded.Link, contentDescription = null)
                            },
                            text = { Text(streamUrlContentDesc) },
                            onClick = {
                                showMoreMenu = false
                                onOpenStreamDialog()
                            },
                            modifier = Modifier.testTag("header_stream_url")
                        )
                    }
                    if (showMultiWindowBtn) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Splitscreen,
                                    contentDescription = null,
                                    tint = if (multiWindowEnabled) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            },
                            text = { Text(multiWindowContentDesc) },
                            onClick = {
                                showMoreMenu = false
                                onToggleMultiWindow()
                            },
                            modifier = Modifier.testTag("header_multi_window_toggle")
                        )
                    }
                }
            }
        }

        if (isScanning || scanProgress.isScanning) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PrismSettingsTabHost(
    viewModel: MediaViewModel,
    settings: com.example.model.AppSettings,
    playbackState: com.example.model.PlaybackUiState,
    securityAudit: com.example.security.SecurityAuditInfo,
    intruderLog: List<com.example.model.IntruderAttempt>,
    watchedFolders: List<com.example.model.WatchedFolder>,
    duplicateGroupsCount: Int,
    driverProfile: com.example.player.DeviceDriverProfile?,
    trashedCount: Int,
    activeAudioOutput: com.example.model.AudioOutputProfileType,
    strings: com.example.localization.AppStrings,
    bottomContentPadding: Dp,
    onRequestNotificationsPermission: () -> Unit,
    onSelectFolder: () -> Unit,
    onImportFiles: () -> Unit,
    onPickGallery: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onOpenStreamDialog: () -> Unit,
    onOpenWatchedFoldersManager: () -> Unit,
    onOpenStorageInsights: () -> Unit,
    onOpenQuranHub: () -> Unit,
    onOpenMorningAlarm: () -> Unit,
    onOpenTasbih: () -> Unit,
    onOpenListeningStats: () -> Unit,
    onOpenPrivateVault: () -> Unit,
    onOpenCollageStudio: () -> Unit,
    onOpenWifiTransfer: () -> Unit,
    onOpenCustomSeed: () -> Unit,
    onOpenThemeCode: () -> Unit,
    onOpenAudioProfiles: () -> Unit,
    onOpenRecycleBin: () -> Unit,
    onOpenGlobalSearch: () -> Unit,
    onOpenAdhkarStudio: () -> Unit,
    onPreviewLockScreen: () -> Unit,
    onOpenQuickCustomizer: () -> Unit = {},
    onOpenProAudioStudio: () -> Unit = {},
    onOpenDefaultPlayerSettings: () -> Unit = {},
) {
    SettingsScreen(
        settings = settings,
        playbackState = playbackState,
        securityAudit = securityAudit,
        spectrumLevelsFlow = viewModel.spectrumLevels,
        strings = strings,
        bottomContentPadding = bottomContentPadding,
        onDarkThemeChange = viewModel::setDarkTheme,
        onAmoledBlackChange = viewModel::setAmoledBlack,
        onPaletteChange = viewModel::setAccentPalette,
        onIconStyleChange = viewModel::setIconStyle,
        onFontScaleChange = viewModel::setFontScaleMultiplier,
        onLanguageChange = viewModel::setLanguage,
        onAutoPlayChange = viewModel::setAutoPlayNext,
        onResumePositionChange = viewModel::setResumePlayback,
        onFilterShortChange = viewModel::setFilterShortAudio,
        onShowLyricsChange = viewModel::setShowLyricsInPlayer,
        onAutoPipChange = viewModel::setAutoPipEnabled,
        onVideoDefaultAspectChange = viewModel::setVideoDefaultAspect,
        onVideoDefaultSpeedChange = viewModel::setVideoDefaultSpeed,
        onVideoDefaultLoopChange = viewModel::setVideoDefaultLoop,
        onPhotoGridColumnsChange = viewModel::setPhotoGridColumns,
        onPhotoSlideshowSecondsChange = viewModel::setPhotoSlideshowSeconds,
        onPhotoShowTitlesChange = viewModel::setPhotoShowTitles,
        onNotificationsEnabledChange = { enabled ->
            viewModel.setNotificationsEnabled(enabled)
            if (enabled) onRequestNotificationsPermission()
        },
        onTamperGuardEnabledChange = viewModel::setTamperGuardEnabled,
        intruderLog = intruderLog,
        onSetupPin = viewModel::setupAppLockPin,
        onDisableAppLock = viewModel::disableAppLock,
        onAppLockOnBackgroundChange = viewModel::setAppLockOnBackground,
        onScreenPrivacyChange = viewModel::setScreenPrivacyEnabled,
        onClearIntruderLog = viewModel::clearIntruderLog,
        onSquigglyProgressChange = viewModel::setSquigglyProgress,
        onMiniPlayerCompactChange = viewModel::setMiniPlayerCompact,
        onMiniPlayerFloatingChange = viewModel::setMiniPlayerFloating,
        onToggleBarAddon = viewModel::setBarAddon,
        onEqEnabledChange = viewModel::setEqEnabled,
        onBassBoostChange = viewModel::setBassBoost,
        onSelectEqPreset = viewModel::selectEqPreset,
        onResetEq = viewModel::resetEqualizer,
        onBandChange = viewModel::updateEqBand,
        onStartSleepTimer = viewModel::startSleepTimer,
        onCancelSleepTimer = viewModel::cancelSleepTimer,
        onSelectFolder = onSelectFolder,
        onImportFiles = onImportFiles,
        onPickGallery = onPickGallery,
        onOpenStreamDialog = onOpenStreamDialog,
        onLoadDemoMedia = viewModel::loadDemoMedia,
        onClearLibrary = viewModel::clearLibrary,
        onPostBanner = viewModel::postBanner,
        watchedFolders = watchedFolders,
        duplicateGroupsCount = duplicateGroupsCount,
        onAutoScanWatchedFoldersChange = viewModel::setAutoScanWatchedFolders,
        onOpenWatchedFoldersManager = onOpenWatchedFoldersManager,
        onOpenStorageInsights = onOpenStorageInsights,
        onLiquidGlassStyleChange = viewModel::setLiquidGlassStyle,
        onLiquidGlassIntensityChange = viewModel::setLiquidGlassIntensity,
        onShapeRoundnessChange = viewModel::setShapeRoundness,
        onContrastLevelChange = viewModel::setContrastLevel,
        onNavTransitionStyleChange = viewModel::setNavTransitionStyle,
        onEdgeGlowEnabledChange = viewModel::setEdgeGlowEnabled,
        onAudioPreampChange = viewModel::setAudioPreampPercent,
        onAudioPitchChange = viewModel::setAudioPitch,
        onCrossfadeSecondsChange = viewModel::setCrossfadeSeconds,
        driverProfile = driverProfile,
        onFontStyleChange = viewModel::setFontStyle,
        onHardwareVideoAccelChange = viewModel::setHardwareVideoAcceleration,
        onRefreshDrivers = viewModel::refreshDeviceDrivers,
        onBiometricUnlockChange = viewModel::setBiometricUnlockEnabled,
        onExportBackup = onExportBackup,
        onImportBackup = onImportBackup,
        onNavBarStyleChange = viewModel::setNavBarStyle,
        onStartupTabChange = viewModel::setStartupTab,
        onUiDensityChange = viewModel::setUiDensity,
        onPlayerArtworkStyleChange = viewModel::setPlayerArtworkStyle,
        onAlbumArtTintChange = viewModel::setAlbumArtTintEnabled,
        onFullPlayerSpectrumChange = viewModel::setShowFullPlayerSpectrum,
        onSeekStepSecondsChange = viewModel::setSeekStepSeconds,
        onHapticsEnabledChange = viewModel::setHapticsEnabled,
        onResetCustomization = viewModel::resetCustomizationDefaults,
        onAutoOnlineEnrichmentChange = viewModel::setAutoOnlineEnrichment,
        onAutoFetchLyricsChange = viewModel::setAutoFetchLyrics,
        onAutoFetchArtistPhotosChange = viewModel::setAutoFetchArtistPhotos,
        onClearKnowledgeCache = viewModel::clearKnowledgeCache,
        onGaplessPlaybackChange = viewModel::setGaplessPlayback,
        onAudioNormalizationChange = viewModel::setAudioNormalization,
        onPauseOnHeadsetUnplugChange = viewModel::setPauseOnHeadsetUnplug,
        onVideoGesturesChange = viewModel::setVideoGesturesEnabled,
        onVideoDoubleTapSeekChange = viewModel::setVideoDoubleTapSeekEnabled,
        onVideoRememberPositionChange = viewModel::setVideoRememberPosition,
        onVideoSubtitleSizeChange = viewModel::setVideoSubtitleSizeSp,
        onPhotoAutoShowExifChange = viewModel::setPhotoAutoShowExif,
        onPhotoHighResDecodeChange = viewModel::setPhotoHighResDecode,
        onPhotoSlideshowTransitionChange = viewModel::setPhotoSlideshowTransition,
        onSurfaceDepthStyleChange = viewModel::setSurfaceDepthStyle,
        onKhushuModeChange = viewModel::setKhushuModeEnabled,
        onLectureModeChange = viewModel::setLectureModeEnabled,
        onSkipSilenceChange = viewModel::setSkipSilenceEnabled,
        onAudioBalanceChange = viewModel::setAudioBalance,
        onAudioMonoChange = viewModel::setAudioMonoEnabled,
        onShakeToShuffleChange = viewModel::setShakeToShuffleEnabled,
        onStripExifOnShareChange = viewModel::setStripExifOnShare,
        onAppIconVariantChange = viewModel::setAppIconVariant,
        onVideoSubtitleColorChange = viewModel::setVideoSubtitleColor,
        onVideoSubtitleBgOpacityChange = viewModel::setVideoSubtitleBgOpacity,
        onVideoSubtitleBottomOffsetChange = viewModel::setVideoSubtitleBottomOffsetDp,
        onOpenQuranHub = onOpenQuranHub,
        onOpenMorningAlarm = onOpenMorningAlarm,
        onOpenTasbih = onOpenTasbih,
        onOpenListeningStats = onOpenListeningStats,
        onOpenPrivateVault = onOpenPrivateVault,
        onOpenCollageStudio = onOpenCollageStudio,
        onOpenWifiTransfer = onOpenWifiTransfer,
        onOpenCustomSeed = onOpenCustomSeed,
        onOpenThemeCode = onOpenThemeCode,
        onSettingsLayoutModeChange = viewModel::setSettingsLayoutMode,
        onMiniPlayerEdgeDockSideChange = viewModel::setMiniPlayerEdgeDockSide,
        onSpectrumVisualStyleChange = viewModel::setSpectrumVisualStyle,
        onLyricsFontSizeChange = viewModel::setLyricsFontSizeSp,
        onDriveModeChange = viewModel::setDriveModeEnabled,
        onPlayerGestureSwipeArtChange = viewModel::setPlayerGestureSwipeArt,
        onVideoMirrorChange = viewModel::setVideoMirrorEnabled,
        onVideoAutoHideSecondsChange = viewModel::setVideoAutoHideSeconds,
        onApplyQuickThemePreset = viewModel::applyQuickThemePreset,
        trashedCount = trashedCount,
        activeAudioOutput = activeAudioOutput,
        onReverbEnvironmentChange = viewModel::setReverbEnvironment,
        onOpenAudioProfiles = onOpenAudioProfiles,
        onVideoAutoNextChange = viewModel::setVideoAutoNextEnabled,
        onMoveNavTab = viewModel::moveNavTab,
        onResetNavTabOrder = viewModel::resetNavTabOrder,
        onHeaderShowStreamBtnChange = viewModel::setHeaderShowStreamBtn,
        onHeaderShowMultiWindowBtnChange = viewModel::setHeaderShowMultiWindowBtn,
        onOpenRecycleBin = onOpenRecycleBin,
        onOpenGlobalSearch = onOpenGlobalSearch,
        onOpenAdhkarStudio = onOpenAdhkarStudio,
        onOpenOnboardingGuide = viewModel::reopenOnboardingWizard,
        onPlayerGenreDynamicColorsChange = viewModel::setPlayerGenreDynamicColors,
        onPlayerShowTechTelemetryChange = viewModel::setPlayerShowTechTelemetry,
        onPlayerExtrasButtonSubtleChange = viewModel::setPlayerExtrasButtonSubtle,
        onPlayerDirectBitExactDspChange = viewModel::setPlayerDirectBitExactDsp,
        onPlayerExpressivePlayMorphChange = viewModel::setPlayerExpressivePlayMorph,
        onLockScreenControlsEnabledChange = viewModel::setLockScreenControlsEnabled,
        onLockScreenShowArtworkChange = viewModel::setLockScreenShowArtwork,
        onLockScreenShowIdentityChange = viewModel::setLockScreenShowPrismIdentity,
        onLockScreenFullScreenEnabledChange = viewModel::setLockScreenFullScreenEnabled,
        onLockScreenShowClockChange = viewModel::setLockScreenShowClock,
        onLockScreenShowLyricsChange = viewModel::setLockScreenShowLyrics,
        onLockScreenBlurBackdropChange = viewModel::setLockScreenBlurBackdrop,
        onLockScreenCompactModeChange = viewModel::setLockScreenCompactMode,
        onPreviewLockScreen = onPreviewLockScreen,
        onPipMusicEnabledChange = viewModel::setPipMusicEnabled,
        onPipVideoEnabledChange = viewModel::setPipVideoEnabled,
        onPipPhotoEnabledChange = viewModel::setPipPhotoEnabled,
        onCardStyleChange = viewModel::setCardStyle,
        onBackdropStyleChange = viewModel::setBackdropStyle,
        onMotionFeelChange = viewModel::setMotionFeelStyle,
        onHeaderStyleChange = viewModel::setHeaderTitleStyle,
        onSwipeGesturesChange = viewModel::setSwipeGesturesEnabled,
        onShowFormatBadgesChange = viewModel::setShowFormatBadges,
        onAccentStrengthChange = viewModel::setAccentStrength,
        onOpenQuickCustomizer = onOpenQuickCustomizer,
        onOpenProAudioStudio = onOpenProAudioStudio,
        onOpenDefaultPlayerSettings = onOpenDefaultPlayerSettings
    )
}
