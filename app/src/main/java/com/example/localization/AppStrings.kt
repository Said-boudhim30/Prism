package com.example.localization

import com.example.model.AccentPalette
import com.example.model.AppLanguage
import com.example.model.EqPreset
import com.example.model.IconPackStyle
import com.example.model.LiquidGlassStyle
import com.example.model.MaterialContrastLevel
import com.example.model.MaterialFontStyle
import com.example.model.MediaSortOption
import com.example.model.MediaTab
import com.example.model.NavBarStyle
import com.example.model.NavTransitionStyle
import com.example.model.PlayerArtworkStyle
import com.example.model.SlideshowTransitionStyle
import com.example.model.SmartMusicMix
import com.example.model.SurfaceDepthStyle
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.model.UiDensity
import com.example.ui.theme.TrackGenreMood

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { stringsFor(AppLanguage.EN) }

interface AppStrings {
    val appTitle: String
    val tabMusic: String
    val tabVideos: String
    val tabPhotos: String
    val tabSettings: String
    val selectMediaFolder: String
    val importFiles: String
    val pickGalleryMedia: String
    val loadDemoMedia: String
    val onboardingTitle: String
    val onboardingSubtitle: String
    val scanningLibrary: String
    val searchAudioHint: String
    val filterAll: String
    val favoritesOnly: String
    val tracksCount: (Int) -> String
    val videosCount: (Int) -> String
    val photosCount: (Int) -> String
    val nowPlaying: String
    val equalizerTitle: String
    val equalizerSubtitle: String
    val presetFlat: String
    val presetDeepBass: String
    val presetVocalBoost: String
    val presetClub: String
    val presetCustom: String
    val eqReset: String
    val eqActiveStatus: String
    val eqBypassedStatus: String
    val bassBoostLabel: String
    val sleepTimerTitle: String
    val sleepTimerSubtitle: String
    val minutesShort: (Int) -> String
    val customTimer: String
    val customTimerDialogTitle: String
    val customTimerHint: String
    val startTimer: String
    val cancelTimer: String
    val timerActiveRemaining: (String) -> String
    val menuPlayNow: String
    val menuViewNow: String
    val menuAddFavorite: String
    val menuRemoveFavorite: String
    val menuAddToPlaylist: String
    val menuFileDetails: String
    val menuRename: String
    val menuShare: String
    val menuRemove: String
    val renameDialogTitle: String
    val renameHint: String
    val save: String
    val cancel: String
    val sortTitle: String
    val sortOptionName: (MediaSortOption) -> String
    val playlistsChip: String
    val playlistsTitle: String
    val createPlaylist: String
    val playlistNameHint: String
    val noPlaylistsYet: String
    val playlistTrackCount: (Int) -> String
    val clearPlaylistFilter: String
    val fileDetailsTitle: String
    val detailName: String
    val detailFormat: String
    val detailMime: String
    val detailSize: String
    val detailMetadata: String
    val detailLocation: String
    val close: String
    val settingsAppearance: String
    val darkMode: String
    val darkModeDesc: String
    val amoledBlack: String
    val amoledBlackDesc: String
    val dynamicPalette: String
    val iconStyleTitle: String
    val iconStyleName: (IconPackStyle) -> String
    val languageSetting: String
    val languageDesc: String
    val settingsPlayback: String
    val autoPlayNext: String
    val autoPlayNextDesc: String
    val resumePosition: String
    val resumePositionDesc: String
    val filterShortClips: String
    val filterShortClipsDesc: String
    val settingsWidgetTitle: String
    val settingsWidgetDesc: String
    val addWidgetAction: String
    val widgetPinnedBanner: String
    val widgetManualHint: String
    val settingsSecurityTitle: String
    val settingsSecurityDesc: String
    val securityCertLabel: String
    val securityPrivacyLabel: String
    val securityPrivacyValue: String
    val securityNetworkLabel: String
    val securityNetworkValue: String
    val securityCompatLabel: String
    val designerCardTitle: String
    val designerName: String
    val designerRole: String
    val settingsLibrary: String
    val rescanFolders: String
    val clearLibrary: String
    val emptyMusicFiltered: String
    val emptyVideos: String
    val emptyPhotos: String
    val speedLabel: String
    val pipMode: String
    val brightness: String
    val volume: String
    val paletteName: (AccentPalette) -> String
    val eqPresetName: (EqPreset) -> String
    val demoLoadedBanner: String
    val libraryClearedBanner: String
    val importedCountBanner: (Int) -> String
    val timerExpiredToast: String
    val autoPipTitle: String
    val autoPipDesc: String
    val multiWindowTitle: String
    val splitScreenBannerOn: String
    val splitScreenBannerOff: String
    val paneEqualizer: String
    val floatVideoInApp: String
    val expandVideo: String
    val shuffleLabel: String
    val repeatOff: String
    val repeatAll: String
    val repeatOne: String
    val upNextQueue: String
    val queueEmpty: String
    val companionTools: String
    val pipAudioBadge: String
    val playStreamUrl: String
    val streamUrlDialogTitle: String
    val streamUrlHint: String
    val streamTitleHint: String
    val streamTypeAudio: String
    val streamTypeVideo: String
    val playStreamAction: String
    val invalidHttpsUrlBanner: String
    val abLoopSetA: String
    val abLoopSetB: String
    val abLoopActive: String
    val searchVideosHint: String
    val searchPhotosHint: String
    val videoAspectFit: String
    val videoAspectCrop: String
    val videoAspectFill: String
    val videoLockedMsg: String
    val videoUnlockedMsg: String
    val videoLoopLabel: String
    val photoSlideshow: String
    val photoRotate: String
    val photoFilterOriginal: String
    val photoFilterVivid: String
    val photoFilterWarm: String
    val photoFilterNoir: String
    val photoFilterCool: String
    // Material You Expressive, Customizable Bar, Drop-to-X & Accordion Settings
    val playerBarSectionTitle: String
    val playerBarSectionDesc: String
    val squigglyBarTitle: String
    val squigglyBarDesc: String
    val compactBarAction: String
    val expandBarAction: String
    val floatPlayerAction: String
    val dockPlayerAction: String
    val customizeBarTitle: String
    val barAddonsHeader: String
    val addonSpectrum: String
    val addonSeek10: String
    val addonFavorite: String
    val addonSpeed: String
    val addonAbLoop: String
    val addonEq: String
    val addonTimer: String
    val addonQueue: String
    val dragToXHint: String
    val releaseToStopHint: String
    val pipStopAction: String
    val expandSectionDesc: String
    val collapseSectionDesc: String
    val expandAllSections: String
    val collapseAllSections: String
    val wallpaperDynamicHint: String
    val settingsSystemSection: String
    // Separated Per-Media Settings, Font Size, Screen/Notch, Lyrics, Notifications, Multi-Widgets & Anti-Tamper
    val settingsMusicSection: String
    val settingsMusicDesc: String
    val settingsVideoSection: String
    val settingsVideoDesc: String
    val settingsPhotosSection: String
    val settingsPhotosDesc: String
    val showLyricsSetting: String
    val showLyricsDesc: String
    val videoDefaultAspectTitle: String
    val videoDefaultSpeedTitle: String
    val videoDefaultLoopTitle: String
    val videoDefaultLoopDesc: String
    val photoGridColumnsTitle: String
    val photoGridAuto: String
    val photoColumnsCount: (Int) -> String
    val photoSlideshowSpeedTitle: String
    val secondsShort: (Int) -> String
    val photoShowTitlesLabel: String
    val photoShowTitlesDesc: String
    val fontSizeTitle: String
    val fontSizeSmall: String
    val fontSizeDefault: String
    val fontSizeLarge: String
    val fontSizeXL: String
    val screenProfileTitle: String
    val notchDetectedLabel: String
    val notchStandardLabel: String
    val screenAdaptedBadge: String
    val artistProfileBadge: String
    val lyricsTitle: String
    val editLyricsAction: String
    val lyricsPlaceholder: String
    val noLyricsYetHint: String
    val lyricsSavedBanner: String
    val notifSettingTitle: String
    val notifSettingDesc: String
    val widgetCompactTitle: String
    val widgetHubTitle: String
    val widgetHubDesc: String
    val addHubWidgetAction: String
    val tamperGuardTitle: String
    val tamperGuardDesc: String
    val tamperStatusClean: String
    val tamperStatusAlert: String
    val sendSecurityReportAction: String
    val tamperDialogTitle: String
    val tamperDialogMessage: String
    val securityAlertEmailLabel: String
    val securityInstallerLabel: String
    // Multi-Selection, Batch Actions & View Modes
    val selectModeAction: String
    val selectedCountLabel: (Int, Int) -> String
    val selectAllAction: String
    val deselectAllAction: String
    val invertSelectionAction: String
    val deleteAction: String
    val confirmDeleteTitle: String
    val confirmDeleteMessage: (Int) -> String
    val deletedCountBanner: (Int) -> String
    val favoritedCountBanner: (Int) -> String
    val addedToPlaylistBanner: (Int) -> String
    val viewModeTitle: String
    val viewModeList: String
    val viewModeCompact: String
    val viewModeGrid: String
    // Active Queue Reordering & Sorting
    val queueSortName: String
    val queueSortArtist: String
    val queueSortDuration: String
    val queueSortReverse: String
    val queueSortShuffle: String
    val moveUpAction: String
    val moveDownAction: String
    val removeFromQueueAction: String
    val queueReorderedBanner: String
    // Balanced Pro Video Player Features
    val videoRotateLabel: String
    val videoMuteLabel: String
    val videoUnmuteLabel: String
    val videoSubtitlesLabel: String
    val videoSubtitlesLoaded: String
    val videoSubtitlesOff: String
    val videoNightDimmerLabel: String
    val videoSleepTimerLabel: String
    val videoHoldSpeedBadge: String
    val videoPrevAction: String
    val videoNextAction: String
    // Anti-Intruder & Anti-Hacker Security Shield
    val securityScoreTitle: String
    val appLockTitle: String
    val appLockDesc: String
    val appLockSetupAction: String
    val appLockChangePinAction: String
    val appLockDisableAction: String
    val appLockOnBackgroundTitle: String
    val appLockOnBackgroundDesc: String
    val appLockEnterPinTitle: String
    val appLockEnterPinSubtitle: String
    val appLockWrongPin: (Int) -> String
    val appLockCooldownMsg: (Int) -> String
    val appLockUnlockAction: String
    val appLockSetupTitle: String
    val appLockSetupDesc: String
    val appLockPinHint: String
    val appLockConfirmHint: String
    val appLockMinLengthError: String
    val appLockMismatchError: String
    val appLockSavedBanner: String
    val appLockDisabledBanner: String
    val screenPrivacyTitle: String
    val screenPrivacyDesc: String
    val intruderLogTitle: String
    val intruderLogEmpty: String
    val intruderLogButton: (Int) -> String
    val clearLogAction: String
    val sendReportShort: String
    val rootCheckLabel: String
    val proxyCheckLabel: String
    val statusClean: String
    val statusAlert: String
    // Fast Discovery, First-Launch Auto-Watch Folders & Studio Extras
    val firstLaunchTitle: String
    val firstLaunchSubtitle: String
    val firstLaunchAutoScanHint: String
    val addWatchFolderAction: String
    val startExploringAction: String
    val skipForNowAction: String
    val selectedFoldersLabel: (Int) -> String
    val noFoldersPickedYet: String
    val watchedFoldersTitle: String
    val watchedFoldersDesc: String
    val manageFoldersAction: String
    val autoScanStartupTitle: String
    val autoScanStartupDesc: String
    val rescanAllAction: String
    val folderRemovedBanner: String
    val folderAddedBanner: (String, Int) -> String
    val scanningLiveCount: (Int) -> String
    val allFoldersChip: String
    val recentlyPlayedTitle: String
    val playsCountShort: (Int) -> String
    val storageInsightsTitle: String
    val storageInsightsDesc: String
    val totalLibrarySizeLabel: String
    val duplicatesFoundTitle: (Int) -> String
    val noDuplicatesFound: String
    val cleanAllDuplicatesAction: String
    val cleanDuplicateAction: String
    val duplicatesCleanedBanner: (Int) -> String
    val duplicateCopiesBadge: (Int) -> String
    val editTagsAction: String
    val editTagsTitle: String
    val tagTitleLabel: String
    val tagArtistLabel: String
    val tagAlbumLabel: String
    val tagsSavedBanner: String
    val bookmarksTitle: String
    val addBookmarkAction: String
    val bookmarkLabelHint: String
    val noBookmarksYet: String
    val bookmarkSavedBanner: String
    val folderItemsSummary: (Int, Int, Int) -> String
    // Material You Tonal Surface, Typography, Transitions, Hardware Drivers & Built-In Security
    val liquidGlassTitle: String
    val liquidGlassSubtitle: String
    val liquidGlassStyleName: (LiquidGlassStyle) -> String
    val liquidGlassIntensityTitle: String
    val shapeRoundnessTitle: String
    val contrastLevelTitle: String
    val contrastLevelName: (MaterialContrastLevel) -> String
    val fontStyleTitle: String
    val fontStyleName: (MaterialFontStyle) -> String
    val navTransitionTitle: String
    val navTransitionName: (NavTransitionStyle) -> String
    val edgeGlowTitle: String
    val edgeGlowDesc: String
    val preampBoostTitle: String
    val preampBoostDesc: String
    val audioPitchTitle: String
    val audioPitchDesc: String
    val crossfadeTitle: String
    val crossfadeDesc: String
    val crossfadeOffLabel: String
    val syncedLyricsTitle: String
    val syncedLyricsTapHint: String
    val smartMixName: (SmartMusicMix) -> String
    val playVideoAsAudioAction: String
    val playingVideoAsAudioBanner: String
    // Hardware Audio & Video Drivers Auto-Detection
    val driversCardTitle: String
    val driversAutoBadge: String
    val driversAudioRow: String
    val driversVideoRow: String
    val driversDisplayRow: String
    val driversHwVideoToggleTitle: String
    val driversHwVideoToggleDesc: String
    val driversRescanAction: String
    val driversRescannedBanner: String
    val photoNativeQualityBadge: String
    // Calm Built-In Security Reassurance (No toggle)
    val builtInProtectionTitle: String
    val builtInProtectionBody: String
    val builtInPillIntegrity: String
    val builtInPillPrivacy: String
    val builtInPillEncrypted: String
    // Biometric Unlock, Video Studio (Subtitles Sync/Tracks/Snapshot), Photo EXIF/Save & Backup
    val biometricUnlockTitle: String
    val biometricUnlockDesc: String
    val biometricPromptTitle: String
    val biometricPromptSubtitle: String
    val biometricUsePinFallback: String
    val videoSubtitleSettingsTitle: String
    val videoSubtitleLoadFile: String
    val videoSubtitleSyncLabel: String
    val videoSubtitleSizeLabel: String
    val videoAudioTrackLabel: String
    val videoAudioTrackTitle: String
    val videoNoExtraAudioTracks: String
    val videoSnapshotAction: String
    val videoSnapshotSaved: String
    val videoSnapshotFailed: String
    val photoExifTitle: String
    val photoExifCamera: String
    val photoExifDate: String
    val photoExifDims: String
    val photoExifSettings: String
    val photoExifNoData: String
    val photoSaveCopyAction: String
    val photoSavedCopyBanner: String
    val photoSaveFailedBanner: String
    val backupSectionTitle: String
    val backupSectionDesc: String
    val backupExportAction: String
    val backupImportAction: String
    val backupExportSuccess: String
    val backupImportSuccess: String
    val backupFailedBanner: String
    val openedExternalMediaBanner: (String) -> String
    // Deep Customization Studio (Optional True Material You vs Liquid Glass, Navigation, Player & Reset)
    val settingsSearchHint: String
    val visualEngineTitle: String
    val visualEngineSubtitle: String
    val visualEngineMaterialYouTitle: String
    val visualEngineMaterialYouDesc: String
    val visualEngineLiquidGlassTitle: String
    val visualEngineLiquidGlassDesc: String
    val liveThemePreviewLabel: String
    val settingsNavLayoutSection: String
    val navBarStyleTitle: String
    val navBarStyleName: (NavBarStyle) -> String
    val startupTabTitle: String
    val uiDensityTitle: String
    val uiDensityName: (UiDensity) -> String
    val hapticsTitle: String
    val hapticsDesc: String
    val playerArtworkStyleTitle: String
    val playerArtworkStyleName: (PlayerArtworkStyle) -> String
    val albumArtTintTitle: String
    val albumArtTintDesc: String
    val fullPlayerSpectrumTitle: String
    val fullPlayerSpectrumDesc: String
    val seekStepTitle: String
    val seekStepDesc: String
    val resetCustomizationAction: String
    val resetCustomizationBanner: String
    val settingsHardwareSubLabel: String
    val tabDisplayName: (MediaTab) -> String
    // Online Knowledge Engine, Artist Portraits, Synced Lyrics, Track Energy, Quran & Hadith, Pro Settings & 4 Languages
    val onlineEnrichmentTitle: String
    val onlineEnrichmentDesc: String
    val autoFetchLyricsTitle: String
    val autoFetchLyricsDesc: String
    val autoFetchArtistPhotoTitle: String
    val autoFetchArtistPhotoDesc: String
    val clearKnowledgeCacheAction: String
    val knowledgeCacheClearedBanner: String
    val fetchingKnowledgeLabel: String
    val refreshKnowledgeAction: String
    val fetchLyricsOnlineAction: String
    val openEncyclopediaAction: String
    val trackEnergyLabel: String
    val trackBpmLabel: String
    val trackMoodLabel: String
    val artistBioTitle: String
    val quranSurahBadge: String
    val quranAyahsCountLabel: (Int) -> String
    val quranVersesPreviewTitle: String
    val quranTranslationTitle: String
    val hadithReferenceTitle: String
    val encyclopediaSectionTitle: String
    val photoLocationLabel: String
    val knowledgeSourcePrefix: String
    val noOnlineInfoFound: String
    val fetchCoverAndLyricsAction: String
    val batchEnrichLibraryAction: String
    val batchEnrichProgressLabel: (Int, Int) -> String
    val batchEnrichCompletedBanner: (Int, Int) -> String
    val coverAndLyricsUpdatedBanner: String
    val gaplessPlaybackTitle: String
    val gaplessPlaybackDesc: String
    val audioNormalizationTitle: String
    val audioNormalizationDesc: String
    val headsetUnplugPauseTitle: String
    val headsetUnplugPauseDesc: String
    val videoGesturesTitle: String
    val videoGesturesDesc: String
    val videoDoubleTapTitle: String
    val videoDoubleTapDesc: String
    val videoRememberPosTitle: String
    val videoRememberPosDesc: String
    val videoSubtitleSizeTitle: String
    val photoAutoExifTitle: String
    val photoAutoExifDesc: String
    val photoHighResTitle: String
    val photoHighResDesc: String
    val photoSlideshowTransitionTitle: String
    val slideshowTransitionName: (SlideshowTransitionStyle) -> String
    val surfaceDepthTitle: String
    val surfaceDepthName: (SurfaceDepthStyle) -> String
    val languageDisplayName: (AppLanguage) -> String
    // Built-In Ethical Purity Shield & Collapsible Dua + Taqwa Reminder
    val contentBlockedBanner: String
    val lyricsBlockedBanner: String
    val taqwaSectionTitle: String
    val taqwaSectionSubtitle: String
    val taqwaDuaHeading: String
    val taqwaDuaBody: String
    val taqwaWarningHeading: String
    val taqwaWarningVerse: String
    val taqwaWarningBody: String
    val taqwaPurityShieldTitle: String
    val taqwaPurityShieldBody: String
    val taqwaPurityBadgeExplicit: String
    val taqwaPurityBadgeLyrics: String
    // 24 Pro & Spiritual Features Strings
    val quranHubTitle: String
    val quranHubSubtitle: String
    val quranKhatmaProgressLabel: String
    val quranHifzRepeatLabel: String
    val quranMarkSurahDone: String
    val khushuModeTitle: String
    val khushuModeDesc: String
    val tasbihTitle: String
    val tasbihTodayLabel: String
    val tasbihReset: String
    val morningAlarmTitle: String
    val morningAlarmSubtitle: String
    val morningAlarmFadeLabel: String
    val morningAlarmTestNow: String
    val audioCutterTitle: String
    val audioCutterSubtitle: String
    val audioCutterExportBtn: String
    val audioCutterSavedToast: String
    val listeningStatsTitle: String
    val listeningStatsSubtitle: String
    val totalPlaysLabel: String
    val totalMinutesLabel: String
    val topTracksLabel: String
    val topArtistsLabel: String
    val formatBreakdownLabel: String
    val lectureModeTitle: String
    val lectureModeDesc: String
    val skipSilenceTitle: String
    val skipSilenceDesc: String
    val stereoBalanceTitle: String
    val stereoBalanceDesc: String
    val audioMonoTitle: String
    val audioMonoDesc: String
    val shakeToShuffleTitle: String
    val shakeToShuffleDesc: String
    val batchTagTitle: String
    val batchTagSubtitle: String
    val batchTagApply: String
    val privateVaultTitle: String
    val privateVaultSubtitle: String
    val privateVaultEmpty: String
    val privateVaultAddBtn: String
    val privateVaultRestoreBtn: String
    val privateVaultLockedHint: String
    val stripExifShareTitle: String
    val stripExifShareDesc: String
    val collageStudioTitle: String
    val collageStudioSubtitle: String
    val collageWatermarkHint: String
    val collageSaveBtn: String
    val customSeedTitle: String
    val customSeedSubtitle: String
    val customSeedFromPhoto: String
    val customSeedClear: String
    val appIconSwitcherTitle: String
    val appIconSwitcherSubtitle: String
    val themeCodeTitle: String
    val themeCodeSubtitle: String
    val themeCodeCopy: String
    val themeCodeApply: String
    val wifiTransferTitle: String
    val wifiTransferSubtitle: String
    val wifiTransferStart: String
    val wifiTransferStop: String
    val videoColorCalibTitle: String
    val videoChaptersTitle: String
    val videoAddChapterBtn: String
    val subtitleColorLabel: String
    val subtitleBgOpacityLabel: String
    val subtitlePositionLabel: String
    val folderPinLabel: String
    val folderUnpinLabel: String
    val folderCustomizeTitle: String
    val smartMixRecentlyAdded: String
    val smartMixLongLectures: String
    val smartMixHiRes: String
    val smartMixQuran: String
    val proToolsHubTitle: String
    val proToolsHubSubtitle: String
    val settingsLayoutModeTitle: String
    val settingsLayoutHorizontal: String
    val settingsLayoutVertical: String
    val collapseSectionAction: String
    val collapseAllSectionsAction: String
    val swipeHintLabel: String
    val tuckToEdgeAction: String
    val restoreFromEdgeAction: String
    val edgeDockSideTitle: String
    val edgeDockRight: String
    val edgeDockLeft: String
    val floatPhotoAction: String
    val floatingPhotoBadge: String
    val widgetsSixGalleryTitle: String
    val widgetsSixGallerySubtitle: String
    val widget3Title: String
    val widget3Desc: String
    val widget4Title: String
    val widget4Desc: String
    val widget5Title: String
    val widget5Desc: String
    val widget6Title: String
    val widget6Desc: String
    val proAudioDeckTitle: String
    val proAudioDeckSubtitle: String
    val driveModeTitle: String
    val driveModeDesc: String
    val muteAudioAction: String
    val unmuteAudioAction: String
    val spectrumStyleTitle: String
    val spectrumClassicBars: String
    val spectrumWavePulse: String
    val spectrumMinimalDots: String
    val swipeArtworkTitle: String
    val swipeArtworkDesc: String
    val lyricsSizeLabel: String
    val hifzRemainingLabel: (Int) -> String
    val hifzLoopTitle: String
    val dspPageSound: String
    val dspPageClarity: String
    val dspPageHifz: String
    val videoMirrorTitle: String
    val videoMirrorDesc: String
    val videoAutoHideTitle: String
    val videoAutoHideNever: String
    val photoFlipAction: String
    val photoBrightBoostAction: String
    val quickPresetsTitle: String
    val presetMaterialYou: String
    val presetLiquidCrystal: String
    val presetAmoledMidnight: String
    val presetEmeraldMosque: String
    val presetAppliedBanner: String
    // 17-Upgrade Suite Strings
    val globalSearchTitle: String
    val globalSearchHint: String
    val globalSearchNoResults: String
    val recycleBinTitle: String
    val recycleBinSubtitle: String
    val recycleBinEmpty: String
    val restoreItemAction: String
    val restoreAllAction: String
    val emptyBinAction: String
    val movedToBinBanner: String
    val restoredFromBinBanner: String
    val mediaNoteTitle: String
    val mediaNoteHint: String
    val saveNoteAction: String
    val deleteNoteAction: String
    val noteSavedBanner: String
    val reverbTitle: String
    val reverbNone: String
    val reverbStudio: String
    val reverbHall: String
    val reverbMosque: String
    val reverbSpatial3d: String
    val tapSyncLyricsTitle: String
    val tapSyncLyricsHint: String
    val tapSyncStampLine: String
    val browseTracksTab: String
    val browseArtistsTab: String
    val browseAlbumsTab: String
    val exportM3uAction: String
    val importM3uAction: String
    val m3uExportedBanner: String
    val m3uImportedBanner: (Int) -> String
    val extractAudioAction: String
    val extractingAudioBanner: String
    val audioExtractedBanner: String
    val photoEditStudioTitle: String
    val photoCropLabel: String
    val photoRotate90Action: String
    val photoBrightnessLabel: String
    val photoContrastLabel: String
    val photoSaturationLabel: String
    val photoSetWallpaperAction: String
    val photoWallpaperSuccess: String
    val photoExportPdfAction: String
    val photoPdfReadyBanner: String
    val photoCompressAction: String
    val photoCompressedBanner: (String, String) -> String
    val photoCompareTitle: String
    val photoCompareBefore: String
    val photoCompareAfter: String
    val videoZoomBadge: (Float) -> String
    val videoFrameBack: String
    val videoFrameForward: String
    val videoSleepTimerTitle: String
    val videoAutoNextTitle: String
    val videoAutoNextDesc: String
    val adhkarTitle: String
    val adhkarMorningTab: String
    val adhkarEveningTab: String
    val adhkarCompletedBadge: String
    val adhkarResetAction: String
    val audioProfilesTitle: String
    val audioProfilesDesc: String
    val audioOutputSpeaker: String
    val audioOutputWired: String
    val audioOutputBluetooth: String
    val audioProfileAppliedBanner: (String) -> String
    val customTabOrderTitle: String
    val customTabOrderDesc: String
    val headerButtonsTitle: String
    val headerShowStreamTitle: String
    val headerShowSplitTitle: String
    val resizeWindowAction: String
    // Dream First-Launch Onboarding & Pro Studio Polish
    val onboardingWelcomeTitle: String
    val onboardingWelcomeSubtitle: String
    val onboardingFeaturesTitle: String
    val onboardingFeaturesSubtitle: String
    val onboardingSetupTitle: String
    val onboardingSetupSubtitle: String
    val onboardingLibraryTitle: String
    val onboardingNextAction: String
    val onboardingBackAction: String
    val onboardingSkipAction: String
    val onboardingEnterPrismAction: String
    val replayOnboardingAction: String
    val trimVideoAction: String
    val videoTrimmerTitle: String
    val videoTrimStartLabel: String
    val videoTrimEndLabel: String
    val videoTrimSuccessBanner: String
    val photoArtFilterLabel: String
    val photoWatermarkHint: String
    val recycleDaysLeft: (Int) -> String
    val quickTileLabel: String
    val sharedToPrismBanner: String
    // Smart Folder Import, Expressive Player Card & Pro Tech, Live Previews & Lock Screen Identity
    val smartImportTitle: String
    val smartImportSubtitle: String
    val smartImportSafTip: String
    val smartImportQuickFoldersLabel: String
    val smartImportDirectFilesAction: String
    val smartImportBrowseOther: String
    val genreMoodName: (TrackGenreMood) -> String
    val settingsPlayerCard: String
    val settingsPlayerCardDesc: String
    val playerGenreColorsTitle: String
    val playerGenreColorsDesc: String
    val playerTechTelemetryTitle: String
    val playerTechTelemetryDesc: String
    val playerExtrasSubtleTitle: String
    val playerExtrasSubtleDesc: String
    val playerBitExactDspTitle: String
    val playerBitExactDspDesc: String
    val playerExpressivePlayMorphTitle: String
    val playerExtrasStudioTitle: String
    val livePreviewBadge: String
    val settingsLockScreen: String
    val settingsLockScreenDesc: String
    val lockScreenControlsTitle: String
    val lockScreenControlsDesc: String
    val lockScreenShowArtworkTitle: String
    val lockScreenShowIdentityTitle: String
    val lockScreenShowIdentityDesc: String
    val lockScreenFullScreenTitle: String
    val lockScreenFullScreenDesc: String
    val lockScreenShowClockTitle: String
    val lockScreenShowLyricsTitle: String
    val lockScreenBlurBackdropTitle: String
    val lockScreenCompactModeTitle: String
    val lockScreenPreviewAction: String
    val lockScreenUnlockAction: String
    // Per-Mode Optional Picture-in-Picture & Clean Filter Sheet Strings
    val pipMusicTitle: String
    val pipMusicDesc: String
    val pipVideoTitle: String
    val pipVideoDesc: String
    val pipPhotoTitle: String
    val pipPhotoDesc: String
    val pipPhotoBadge: String
    val filtersAndToolsAction: String
    val clearFiltersAction: String
    val moreHeaderActionsDesc: String
    val importDownloadsDirectAction: String
    val importDownloadsPrivacyExplainer: String
    val quickStudioTitle: String
    val quickStudioSubtitle: String
    val cardStyleTitle: String
    val cardStyleGlass: String
    val cardStyleElevated: String
    val cardStyleMinimal: String
    val cardStyleVibrant: String
    val backdropStyleTitle: String
    val backdropAurora: String
    val backdropMist: String
    val backdropRibbon: String
    val backdropClean: String
    val motionFeelTitle: String
    val motionBouncy: String
    val motionSmooth: String
    val motionSnappy: String
    val motionReduced: String
    val headerStyleTitle: String
    val headerBold: String
    val headerCompact: String
    val headerCentered: String
    val swipeGesturesTitle: String
    val swipeGesturesDesc: String
    val showFormatBadgesTitle: String
    val showFormatBadgesDesc: String
    val accentStrengthTitle: String
    val swipeFavoriteHint: String
    val swipePlaylistHint: String
    // Beta 4.4 Pro Audio Studio, Auto-Mastering, Clean Collapsible Menus, Default Player & Storage Access
    val appVersionLabel: String
    val proStudioTitle: String
    val proStudioSubtitle: String
    val proStudioOpenAction: String
    val studioAutoTitle: String
    val studioAutoDesc: String
    val studioAutoAnalyzeNow: String
    val studioAutoAppliedBanner: String
    val studioAutoNoTrackHint: String
    val studioBypassOriginal: String
    val studioBypassMastered: String
    val studioResetAll: String
    val studioVuMeterLabel: String
    val studioRackEqTitle: String
    val studioRackToneTitle: String
    val studioRackDynamicsTitle: String
    val studioRackHeadphoneTitle: String
    val studioTubeWarmth: String
    val studioStereoWidth: String
    val studioSubBassPunch: String
    val studioAirBrilliance: String
    val studioVocalClarity: String
    val studioCompressor: String
    val studioCrossfeedTitle: String
    val studioCrossfeedDesc: String
    val studioTruePeakTitle: String
    val studioTruePeakDesc: String
    val studioNightModeTitle: String
    val studioNightModeDesc: String
    val studioRumbleFilterTitle: String
    val studioChannelSwapTitle: String
    val studioChannelSwapDesc: String
    val studioHeadphoneProfileTitle: String
    val studioCustomPresetsTitle: String
    val studioSavePresetAction: String
    val studioSavePresetHint: String
    val studioNoCustomPresets: String
    val studioPresetSavedBanner: String
    val studioPresetLoadedBanner: String
    val playerToolsDrawerOpen: String
    val playerToolsDrawerClose: String
    val defaultPlayerTitle: String
    val defaultPlayerDesc: String
    val defaultPlayerAction: String
    val defaultPlayerOpenedBanner: String
    val storageAccessHubTitle: String
    val storageAccessHubDesc: String
    val storageGrantedFoldersLabel: (Int) -> String
    val storageGrantFolderAction: String
    val storagePickDownloadsAction: String
    val storagePickGalleryAction: String
    val storageAccessReadyBadge: String
    val selectiveFolderChecklistTitle: String
    val selectiveFolderChecklistHint: String
    val selectiveConfirmScanBtn: (Int) -> String
    val selectiveSelectAll: String
    val selectiveClearAll: String
    val selectiveScanningOnlyCheckedHint: String
    val selectiveDirectFilesBadge: (Int) -> String
    val selectiveMediaTypesLabel: String
    val selectiveEmptyChecklistHint: String
    val selectiveInspectingFoldersLabel: String
    val selectiveScannedConfirmedBanner: (Int, Int) -> String
    val settingsStudioHubSectionTitle: String
    val settingsStudioHubSectionSubtitle: String

    fun proEqPresetName(preset: com.example.model.ProEqPreset): String
    fun headphoneProfileName(profile: com.example.model.HeadphoneTuningProfile): String
    fun autoStudioCategoryName(category: com.example.model.AutoStudioCategory): String

    fun cardStyleName(style: com.example.model.MediaCardStyle): String = when (style) {
        com.example.model.MediaCardStyle.GLASS_CARD -> cardStyleGlass
        com.example.model.MediaCardStyle.ELEVATED_M3 -> cardStyleElevated
        com.example.model.MediaCardStyle.MINIMAL_OUTLINE -> cardStyleMinimal
        com.example.model.MediaCardStyle.VIBRANT_TONAL -> cardStyleVibrant
    }

    fun backdropStyleName(style: com.example.model.BackdropAnimationStyle): String = when (style) {
        com.example.model.BackdropAnimationStyle.AURORA_ORBS -> backdropAurora
        com.example.model.BackdropAnimationStyle.SUBTLE_MIST -> backdropMist
        com.example.model.BackdropAnimationStyle.PRISM_RIBBON -> backdropRibbon
        com.example.model.BackdropAnimationStyle.PURE_CLEAN -> backdropClean
    }

    fun motionFeelName(style: com.example.model.MotionFeelStyle): String = when (style) {
        com.example.model.MotionFeelStyle.BOUNCY_EXPRESSIVE -> motionBouncy
        com.example.model.MotionFeelStyle.SMOOTH_FLUID -> motionSmooth
        com.example.model.MotionFeelStyle.SNAPPY_FAST -> motionSnappy
        com.example.model.MotionFeelStyle.REDUCED_MOTION -> motionReduced
    }

    fun headerStyleName(style: com.example.model.HeaderTitleStyle): String = when (style) {
        com.example.model.HeaderTitleStyle.EXPRESSIVE_BOLD -> headerBold
        com.example.model.HeaderTitleStyle.MINIMAL_COMPACT -> headerCompact
        com.example.model.HeaderTitleStyle.CENTERED_ELEGANT -> headerCentered
    }
}

private object EnglishStrings : AppStrings {
    override val appTitle = "Prism Media"
    override val tabMusic = "Music"
    override val tabVideos = "Videos"
    override val tabPhotos = "Photos"
    override val tabSettings = "Settings"
    override val selectMediaFolder = "Select Media Folder"
    override val importFiles = "Import Files"
    override val pickGalleryMedia = "Pick Photos & Videos"
    override val loadDemoMedia = "Load Demo Media"
    override val onboardingTitle = "Your Studio, Ready to Play"
    override val onboardingSubtitle = "Import a folder or files from your device to unlock lossless audio, smart cinema playback, and a fluid gallery — or explore instantly with bundled demo media."
    override val scanningLibrary = "Scanning media files..."
    override val searchAudioHint = "Search tracks, artists, or formats..."
    override val filterAll = "All"
    override val favoritesOnly = "Favorites"
    override val tracksCount: (Int) -> String = { "$it tracks" }
    override val videosCount: (Int) -> String = { "$it videos" }
    override val photosCount: (Int) -> String = { "$it photos" }
    override val nowPlaying = "Now Playing"
    override val equalizerTitle = "5-Band Equalizer"
    override val equalizerSubtitle = "Real-time audio DSP shaping"
    override val presetFlat = "Flat"
    override val presetDeepBass = "Deep Bass"
    override val presetVocalBoost = "Vocal Boost"
    override val presetClub = "Club"
    override val presetCustom = "Custom"
    override val eqReset = "Reset"
    override val eqActiveStatus = "DSP Active"
    override val eqBypassedStatus = "Bypassed"
    override val bassBoostLabel = "Bass Boost"
    override val sleepTimerTitle = "Sleep Timer"
    override val sleepTimerSubtitle = "Automatically pauses audio when countdown finishes"
    override val minutesShort: (Int) -> String = { "${it}m" }
    override val customTimer = "Custom"
    override val customTimerDialogTitle = "Custom Sleep Timer"
    override val customTimerHint = "Minutes (1 - 360)"
    override val startTimer = "Start"
    override val cancelTimer = "Cancel Timer"
    override val timerActiveRemaining: (String) -> String = { "Pauses in $it" }
    override val menuPlayNow = "Play Now"
    override val menuViewNow = "Open Viewer"
    override val menuAddFavorite = "Add to Favorites"
    override val menuRemoveFavorite = "Remove Favorite"
    override val menuAddToPlaylist = "Add to Playlist"
    override val menuFileDetails = "Media Info"
    override val menuRename = "Rename"
    override val menuShare = "Share"
    override val menuRemove = "Remove from Library"
    override val renameDialogTitle = "Rename Media"
    override val renameHint = "Enter display title"
    override val save = "Save"
    override val cancel = "Cancel"
    override val sortTitle = "Sort by"
    override val sortOptionName: (MediaSortOption) -> String = {
        when (it) {
            MediaSortOption.NEWEST -> "Newest"
            MediaSortOption.NAME -> "Name"
            MediaSortOption.DURATION -> "Duration"
            MediaSortOption.SIZE -> "File Size"
        }
    }
    override val playlistsChip = "Playlists"
    override val playlistsTitle = "Custom Playlists"
    override val createPlaylist = "Create Playlist"
    override val playlistNameHint = "Playlist name..."
    override val noPlaylistsYet = "Create your first playlist to organize tracks."
    override val playlistTrackCount: (Int) -> String = { "$it tracks" }
    override val clearPlaylistFilter = "All Tracks"
    override val fileDetailsTitle = "Media Info"
    override val detailName = "File Name"
    override val detailFormat = "Extension"
    override val detailMime = "MIME Type"
    override val detailSize = "Size"
    override val detailMetadata = "Properties"
    override val detailLocation = "Source URI"
    override val close = "Close"
    override val settingsAppearance = "Appearance, Themes & Icons"
    override val darkMode = "Dark Mode"
    override val darkModeDesc = "Switch between deep studio dark and soft daylight surfaces"
    override val amoledBlack = "AMOLED Pure Black"
    override val amoledBlackDesc = "True #000000 black surfaces in Dark Mode for OLED screens"
    override val dynamicPalette = "Material You Themes"
    override val iconStyleTitle = "Google & Expressive Icon Style"
    override val iconStyleName: (IconPackStyle) -> String = {
        when (it) {
            IconPackStyle.GOOGLE_ROUNDED -> "Google Rounded"
            IconPackStyle.FILLED_EXPRESSIVE -> "Bold Filled"
            IconPackStyle.OUTLINED_MINIMAL -> "Clean Outlined"
            IconPackStyle.DUOTONE_PILL -> "Duotone Pill"
        }
    }
    override val languageSetting = "Interface Language"
    override val languageDesc = "Switch seamlessly between LTR English and RTL العربية"
    override val settingsPlayback = "Playback & Audio"
    override val autoPlayNext = "Auto-play next track"
    override val autoPlayNextDesc = "Continue seamlessly to the next track in your queue"
    override val resumePosition = "Resume playback position"
    override val resumePositionDesc = "Remember exact timestamp when switching tracks"
    override val filterShortClips = "Filter out short audio clips (< 5s)"
    override val filterShortClipsDesc = "Hide notification sounds and tiny clips from Music"
    override val settingsWidgetTitle = "Google-Style Home Widget"
    override val settingsWidgetDesc = "Expressive Material You pill widget with instant Play/Pause & track skip controls."
    override val addWidgetAction = "Add Widget to Home Screen"
    override val widgetPinnedBanner = "Widget pin request sent to your launcher"
    override val widgetManualHint = "Long-press your Home Screen → Widgets → Prism to add it."
    override val settingsSecurityTitle = "Security & Play Integrity"
    override val settingsSecurityDesc = "Verified signing certificate, zero broad storage permissions, and strict HTTPS policy."
    override val securityCertLabel = "APK SHA-256 Signature"
    override val securityPrivacyLabel = "Storage Privacy"
    override val securityPrivacyValue = "Zero Broad Permissions (SAF)"
    override val securityNetworkLabel = "Transport Security"
    override val securityNetworkValue = "HTTPS-Only (Cleartext Blocked)"
    override val securityCompatLabel = "Device Runtime"
    override val designerCardTitle = "Crafted & Designed By"
    override val designerName = "Said Boudhim"
    override val designerRole = "Lead Designer & Creator • Prism Studio"
    override val settingsLibrary = "Media Library"
    override val rescanFolders = "Rescan / Import Folder"
    override val clearLibrary = "Clear Library"
    override val emptyMusicFiltered = "No matching tracks. Try another filter or disable the <5s clip filter."
    override val emptyVideos = "No videos loaded yet. Import a folder or load demo media."
    override val emptyPhotos = "No photos loaded yet. Pick photos or load demo media."
    override val speedLabel = "Speed"
    override val pipMode = "PiP"
    override val brightness = "Brightness"
    override val volume = "Volume"
    override val paletteName: (AccentPalette) -> String = {
        when (it) {
            AccentPalette.LAVENDER -> "Lavender"
            AccentPalette.EMERALD -> "Emerald"
            AccentPalette.OCEAN -> "Ocean Blue"
            AccentPalette.AMBER -> "Warm Amber"
            AccentPalette.ROSE -> "Rose Velvet"
            AccentPalette.CORAL -> "Sunset Coral"
            AccentPalette.MIDNIGHT -> "Midnight"
            AccentPalette.SYSTEM -> "Dynamic"
        }
    }
    override val eqPresetName: (EqPreset) -> String = {
        when (it) {
            EqPreset.FLAT -> "Flat"
            EqPreset.DEEP_BASS -> "Deep Bass"
            EqPreset.VOCAL_BOOST -> "Vocal Boost"
            EqPreset.CLUB -> "Club"
            EqPreset.CUSTOM -> "Custom"
        }
    }
    override val demoLoadedBanner = "Demo studio media loaded across Music, Videos & Photos"
    override val libraryClearedBanner = "Media library cleared"
    override val importedCountBanner: (Int) -> String = { "Imported $it media items" }
    override val timerExpiredToast = "Sleep timer finished — playback paused"
    override val autoPipTitle = "Auto Picture-in-Picture on Exit"
    override val autoPipDesc = "Automatically shrink into a floating PiP window when leaving the app during playback"
    override val multiWindowTitle = "In-App Split Screen"
    override val splitScreenBannerOn = "In-app Multi-Window enabled"
    override val splitScreenBannerOff = "Single-window mode restored"
    override val paneEqualizer = "Pro EQ"
    override val floatVideoInApp = "Float in App"
    override val expandVideo = "Full Screen"
    override val shuffleLabel = "Shuffle"
    override val repeatOff = "Repeat Off"
    override val repeatAll = "Repeat All"
    override val repeatOne = "Repeat One"
    override val upNextQueue = "Up Next Queue"
    override val queueEmpty = "No upcoming tracks in queue"
    override val companionTools = "Quick Controls"
    override val pipAudioBadge = "Prism PiP Audio"
    override val playStreamUrl = "Stream URL"
    override val streamUrlDialogTitle = "Play Online Stream (HTTPS)"
    override val streamUrlHint = "https://... (.mp3, .mp4, .m3u8, radio)"
    override val streamTitleHint = "Optional title"
    override val streamTypeAudio = "Audio Stream"
    override val streamTypeVideo = "Video Stream"
    override val playStreamAction = "Play Now"
    override val invalidHttpsUrlBanner = "Please enter a valid https:// media link"
    override val abLoopSetA = "Loop A"
    override val abLoopSetB = "Set B"
    override val abLoopActive = "A↔B On"
    override val searchVideosHint = "Search videos or formats..."
    override val searchPhotosHint = "Search photos..."
    override val videoAspectFit = "Fit"
    override val videoAspectCrop = "Crop"
    override val videoAspectFill = "Fill"
    override val videoLockedMsg = "Screen locked — tap lock to unlock"
    override val videoUnlockedMsg = "Controls unlocked"
    override val videoLoopLabel = "Loop"
    override val photoSlideshow = "Slideshow"
    override val photoRotate = "Rotate"
    override val photoFilterOriginal = "Original"
    override val photoFilterVivid = "Vivid"
    override val photoFilterWarm = "Warm"
    override val photoFilterNoir = "Noir"
    override val photoFilterCool = "Cool"
    override val playerBarSectionTitle = "Player Bar & Add-ons"
    override val playerBarSectionDesc = "Customize squiggly progress, compact size, floating bubble, and quick modules"
    override val squigglyBarTitle = "Material You Squiggly Progress"
    override val squigglyBarDesc = "Animated Google-style wavy timeline while playing"
    override val compactBarAction = "Compact"
    override val expandBarAction = "Normal"
    override val floatPlayerAction = "Float"
    override val dockPlayerAction = "Dock"
    override val customizeBarTitle = "Customize Player Bar"
    override val barAddonsHeader = "Active Bar Add-ons"
    override val addonSpectrum = "Live Wave"
    override val addonSeek10 = "±10s Skip"
    override val addonFavorite = "Favorite"
    override val addonSpeed = "Speed"
    override val addonAbLoop = "A-B Loop"
    override val addonEq = "Quick EQ"
    override val addonTimer = "Timer"
    override val addonQueue = "Queue"
    override val dragToXHint = "Drag to X to stop & close"
    override val releaseToStopHint = "Release to stop playback"
    override val pipStopAction = "Stop"
    override val expandSectionDesc = "Expand section"
    override val collapseSectionDesc = "Collapse section"
    override val expandAllSections = "Expand All"
    override val collapseAllSections = "Collapse All"
    override val wallpaperDynamicHint = "Follows your Android Wallpaper (Material You)"
    override val settingsSystemSection = "Language, Anti-Tamper Security & Credits"
    override val settingsMusicSection = "Music Settings"
    override val settingsMusicDesc = "Auto-play, resume, lyrics display, DSP equalizer & sleep timer"
    override val settingsVideoSection = "Video Settings"
    override val settingsVideoDesc = "Default aspect ratio, playback speed, video loop & Auto-PiP"
    override val settingsPhotosSection = "Photos Gallery Settings"
    override val settingsPhotosDesc = "Grid columns, slideshow speed & card title display"
    override val showLyricsSetting = "Show Lyrics in Player"
    override val showLyricsDesc = "Display interactive song lyrics & artist profile card while playing"
    override val videoDefaultAspectTitle = "Default Video Aspect Ratio"
    override val videoDefaultSpeedTitle = "Default Video Speed"
    override val videoDefaultLoopTitle = "Loop Videos by Default"
    override val videoDefaultLoopDesc = "Automatically repeat videos when they reach the end"
    override val photoGridColumnsTitle = "Gallery Grid Columns"
    override val photoGridAuto = "Auto (Screen Fit)"
    override val photoColumnsCount: (Int) -> String = { "$it Columns" }
    override val photoSlideshowSpeedTitle = "Slideshow Interval"
    override val secondsShort: (Int) -> String = { "${it}s" }
    override val photoShowTitlesLabel = "Show Photo Titles"
    override val photoShowTitlesDesc = "Display filename bar under photo thumbnails in the grid"
    override val fontSizeTitle = "Interface Font Size"
    override val fontSizeSmall = "Small"
    override val fontSizeDefault = "Default"
    override val fontSizeLarge = "Large"
    override val fontSizeXL = "Extra Large"
    override val screenProfileTitle = "Auto Screen & Notch Adaptation"
    override val notchDetectedLabel = "Notch / Cutout Protected"
    override val notchStandardLabel = "Standard Edge-to-Edge"
    override val screenAdaptedBadge = "Auto-Matched"
    override val artistProfileBadge = "Artist Profile"
    override val lyricsTitle = "Song Lyrics"
    override val editLyricsAction = "Edit Lyrics"
    override val lyricsPlaceholder = "Paste or write song lyrics here..."
    override val noLyricsYetHint = "No lyrics added yet. Tap Edit Lyrics to add lyrics for this song."
    override val lyricsSavedBanner = "Song lyrics saved"
    override val notifSettingTitle = "Status Bar Media Notification"
    override val notifSettingDesc = "Show Prism icon & playback controls in phone notifications"
    override val widgetCompactTitle = "Widget 1 • Compact Pill Player (4×1)"
    override val widgetHubTitle = "Widget 2 • Expressive Media Hub (4×2)"
    override val widgetHubDesc = "Full playback controls + instant shortcuts to Music, Videos & Photos."
    override val addHubWidgetAction = "Add Hub Widget (4×2)"
    override val tamperGuardTitle = "Anti-Tamper Integrity Shield"
    override val tamperGuardDesc = "Monitors APK signature & modifications; prepares alert email to owner"
    override val tamperStatusClean = "Protected • Signature Verified"
    override val tamperStatusAlert = "Tampering / Modification Detected!"
    override val sendSecurityReportAction = "Send Security Alert to saidboudhim0@gmail.com"
    override val tamperDialogTitle = "App Modification Alert"
    override val tamperDialogMessage = "Unauthorized modification or re-signing of Prism was detected. Send the technical security diagnostics report to the owner immediately."
    override val securityAlertEmailLabel = "Owner Security Alert Email"
    override val securityInstallerLabel = "Install Source"
    override val selectModeAction = "Select"
    override val selectedCountLabel: (Int, Int) -> String = { sel, total -> "$sel / $total selected" }
    override val selectAllAction = "All"
    override val deselectAllAction = "None"
    override val invertSelectionAction = "Invert"
    override val deleteAction = "Delete"
    override val confirmDeleteTitle = "Remove Selected Items?"
    override val confirmDeleteMessage: (Int) -> String = { count -> "Remove $count selected item(s) from your Prism library?" }
    override val deletedCountBanner: (Int) -> String = { count -> "Removed $count item(s)" }
    override val favoritedCountBanner: (Int) -> String = { count -> "Updated favorites for $count item(s)" }
    override val addedToPlaylistBanner: (Int) -> String = { count -> "Added $count track(s) to playlist" }
    override val viewModeTitle = "View Mode"
    override val viewModeList = "Detailed List"
    override val viewModeCompact = "Compact View"
    override val viewModeGrid = "Grid Cards"
    override val queueSortName = "Name"
    override val queueSortArtist = "Artist"
    override val queueSortDuration = "Duration"
    override val queueSortReverse = "Reverse"
    override val queueSortShuffle = "Shuffle"
    override val moveUpAction = "Move Up"
    override val moveDownAction = "Move Down"
    override val removeFromQueueAction = "Remove from Queue"
    override val queueReorderedBanner = "Active queue order updated"
    override val videoRotateLabel = "Rotate"
    override val videoMuteLabel = "Mute"
    override val videoUnmuteLabel = "Unmute"
    override val videoSubtitlesLabel = "Subtitles"
    override val videoSubtitlesLoaded = "CC On"
    override val videoSubtitlesOff = "CC Off"
    override val videoNightDimmerLabel = "Night"
    override val videoSleepTimerLabel = "Timer"
    override val videoHoldSpeedBadge = "2.0x Fast Forward"
    override val videoPrevAction = "Previous Video"
    override val videoNextAction = "Next Video"
    override val securityScoreTitle = "Defense Shield Score"
    override val appLockTitle = "App Lock PIN Shield"
    override val appLockDesc = "Protect your media vault with a salted PBKDF2 PIN + brute-force lockout"
    override val appLockSetupAction = "Set PIN"
    override val appLockChangePinAction = "Change PIN"
    override val appLockDisableAction = "Disable Lock"
    override val appLockOnBackgroundTitle = "Auto-Lock on Background"
    override val appLockOnBackgroundDesc = "Require PIN again when returning from other apps"
    override val appLockEnterPinTitle = "Prism Security Vault"
    override val appLockEnterPinSubtitle = "Enter your PIN to unlock your media library"
    override val appLockWrongPin: (Int) -> String = { n -> "Incorrect PIN ($n failed attempt(s) logged)" }
    override val appLockCooldownMsg: (Int) -> String = { sec -> "Too many failed tries. Locked for ${sec}s" }
    override val appLockUnlockAction = "Unlock"
    override val appLockSetupTitle = "Configure App Lock PIN"
    override val appLockSetupDesc = "Choose a 4 to 6 digit PIN. Stored strictly as a cryptographic PBKDF2 hash."
    override val appLockPinHint = "Enter 4-6 digit PIN"
    override val appLockConfirmHint = "Confirm PIN"
    override val appLockMinLengthError = "PIN must be at least 4 digits"
    override val appLockMismatchError = "PINs do not match"
    override val appLockSavedBanner = "App Lock PIN enabled"
    override val appLockDisabledBanner = "App Lock PIN disabled"
    override val screenPrivacyTitle = "Anti-Screenshot & Screen Capture Block"
    override val screenPrivacyDesc = "Enable FLAG_SECURE to block screenshots, recorders & recent-app previews"
    override val intruderLogTitle = "Intruder Attempt Log"
    override val intruderLogEmpty = "No unauthorized unlock attempts recorded. Your vault is clean."
    override val intruderLogButton: (Int) -> String = { n -> "Intruder Log ($n)" }
    override val clearLogAction = "Clear"
    override val sendReportShort = "Send Alert"
    override val rootCheckLabel = "Root / SU Binary Check"
    override val proxyCheckLabel = "Network Proxy / MITM Guard"
    override val statusClean = "Clean & Safe"
    override val statusAlert = "Warning Detected"
    override val firstLaunchTitle = "Choose Your Media Folders"
    override val firstLaunchSubtitle = "Select folders once and Prism will auto-discover supported music, videos & photos in milliseconds on every launch — saved until you change them."
    override val firstLaunchAutoScanHint = "Auto-sync saved folders on startup (0 broad permissions)"
    override val addWatchFolderAction = "Add Device Folder"
    override val startExploringAction = "Save & Start Exploring"
    override val skipForNowAction = "Skip for now"
    override val selectedFoldersLabel: (Int) -> String = { n -> "Saved Watch Folders ($n)" }
    override val noFoldersPickedYet = "Tap \"Add Device Folder\" to pick Music, Movies, Downloads, or DCIM folders."
    override val watchedFoldersTitle = "Watched Folders & Auto-Scan"
    override val watchedFoldersDesc = "Manage saved folders scanned automatically on launch"
    override val manageFoldersAction = "Manage Watched Folders"
    override val autoScanStartupTitle = "Auto-Scan Saved Folders on Launch"
    override val autoScanStartupDesc = "Instantly load cached media and sync new/changed files in background"
    override val rescanAllAction = "Fast Rescan All"
    override val folderRemovedBanner = "Folder removed from watched list"
    override val folderAddedBanner: (String, Int) -> String = { name, count -> "Folder \"$name\" saved • $count files ready" }
    override val scanningLiveCount: (Int) -> String = { n -> "Scanning • $n files" }
    override val allFoldersChip = "All Folders"
    override val recentlyPlayedTitle = "Recently Played"
    override val playsCountShort: (Int) -> String = { n -> "${n}x" }
    override val storageInsightsTitle = "Storage Insights & Duplicates"
    override val storageInsightsDesc = "Breakdown by media type & 1-tap duplicate cleaner"
    override val totalLibrarySizeLabel = "Total Library Size"
    override val duplicatesFoundTitle: (Int) -> String = { n -> "Duplicate Groups Found ($n)" }
    override val noDuplicatesFound = "No duplicate files detected in your library."
    override val cleanAllDuplicatesAction = "Clean All Duplicates"
    override val cleanDuplicateAction = "Clean"
    override val duplicatesCleanedBanner: (Int) -> String = { n -> "Cleaned $n duplicate entries from library" }
    override val duplicateCopiesBadge: (Int) -> String = { n -> "$n copies" }
    override val editTagsAction = "Edit Tags"
    override val editTagsTitle = "Edit Audio Tags"
    override val tagTitleLabel = "Track Title"
    override val tagArtistLabel = "Artist"
    override val tagAlbumLabel = "Album"
    override val tagsSavedBanner = "Audio tags updated"
    override val bookmarksTitle = "Bookmarks"
    override val addBookmarkAction = "Mark Current Time"
    override val bookmarkLabelHint = "Bookmark note (optional)"
    override val noBookmarksYet = "Save timestamp bookmarks for long tracks, mixes, or podcasts."
    override val bookmarkSavedBanner = "Bookmark saved"
    override val folderItemsSummary: (Int, Int, Int) -> String = { a, v, p -> "$a audio • $v video • $p photo" }
    override val liquidGlassTitle = "Material You Liquid Glass"
    override val liquidGlassSubtitle = "Tonal M3 Expressive glass, refractive borders & dynamic mesh"
    override val liquidGlassStyleName: (LiquidGlassStyle) -> String = {
        when (it) {
            LiquidGlassStyle.MATERIAL_LIQUID -> "Material Liquid"
            LiquidGlassStyle.CRYSTAL_TONAL -> "Crystal Tonal"
            LiquidGlassStyle.VELVET_FROST -> "Velvet Frost"
            LiquidGlassStyle.SOLID_M3 -> "Solid M3"
        }
    }
    override val liquidGlassIntensityTitle = "Liquid Glass Intensity"
    override val shapeRoundnessTitle = "M3 Shape Roundness"
    override val contrastLevelTitle = "Material You Contrast"
    override val contrastLevelName: (MaterialContrastLevel) -> String = {
        when (it) {
            MaterialContrastLevel.STANDARD -> "Standard"
            MaterialContrastLevel.MEDIUM -> "Medium"
            MaterialContrastLevel.HIGH -> "High"
        }
    }
    override val fontStyleTitle = "Material Typography Font Pack"
    override val fontStyleName: (MaterialFontStyle) -> String = {
        when (it) {
            MaterialFontStyle.MATERIAL_EXPRESSIVE -> "Material Expressive"
            MaterialFontStyle.GOOGLE_CLEAN -> "Clean Sans"
            MaterialFontStyle.EDITORIAL_STUDIO -> "Studio Display"
        }
    }
    override val navTransitionTitle = "Navigation Transition Animation"
    override val navTransitionName: (NavTransitionStyle) -> String = {
        when (it) {
            NavTransitionStyle.EXPRESSIVE_SPRING -> "Expressive Spring"
            NavTransitionStyle.PARALLAX_DEPTH -> "Parallax Depth"
            NavTransitionStyle.LIQUID_MORPH -> "Liquid Morph"
            NavTransitionStyle.SHARED_AXIS -> "Shared Axis"
        }
    }
    override val edgeGlowTitle = "Audio-Reactive Edge Glow"
    override val edgeGlowDesc = "Luminous Material You perimeter aura pulsing with live music energy"
    override val preampBoostTitle = "Studio 200% Volume Boost"
    override val preampBoostDesc = "Clean software volume amplifier up to 200% with anti-clipping limiter"
    override val audioPitchTitle = "Audio Pitch Control"
    override val audioPitchDesc = "Fine-tune vocal & musical key independently from speed"
    override val crossfadeTitle = "Smooth Crossfade Transition"
    override val crossfadeDesc = "Gradual volume ramp when transitioning between tracks"
    override val crossfadeOffLabel = "Off"
    override val syncedLyricsTitle = "Synced Live Lyrics"
    override val syncedLyricsTapHint = "Tap any line to jump straight to that moment"
    override val smartMixName: (SmartMusicMix) -> String = {
        when (it) {
            SmartMusicMix.ALL_TRACKS -> "All Library"
            SmartMusicMix.FAVORITES_MIX -> "Favorites Mix"
            SmartMusicMix.MOST_PLAYED_MIX -> "Most Played"
            SmartMusicMix.QUICK_DISCOVERY -> "Smart Mix"
            SmartMusicMix.RECENTLY_ADDED -> smartMixRecentlyAdded
            SmartMusicMix.LONG_LECTURES -> smartMixLongLectures
            SmartMusicMix.HI_RES_LOSSLESS -> smartMixHiRes
            SmartMusicMix.QURAN_RECITATIONS -> smartMixQuran
        }
    }
    override val playVideoAsAudioAction = "Play as Audio (Background)"
    override val playingVideoAsAudioBanner = "Playing video soundtrack in Music engine"
    override val driversCardTitle = "Hardware Audio & Video Drivers"
    override val driversAutoBadge = "Auto-Matched"
    override val driversAudioRow = "Audio Driver"
    override val driversVideoRow = "Video Hardware"
    override val driversDisplayRow = "Display Color"
    override val driversHwVideoToggleTitle = "Hardware Video Acceleration"
    override val driversHwVideoToggleDesc = "Automatically decode 4K/HDR video on device GPU with smart fallback"
    override val driversRescanAction = "Re-Scan Device Drivers"
    override val driversRescannedBanner = "Hardware audio, video & display drivers re-calibrated"
    override val photoNativeQualityBadge = "Native Full-Res"
    override val builtInProtectionTitle = "Built-In Security & Privacy Active"
    override val builtInProtectionBody = "Your app runs with always-on integrity verification, private folder access without broad storage permissions, and encrypted connections."
    override val builtInPillIntegrity = "Verified Core"
    override val builtInPillPrivacy = "Private Storage"
    override val builtInPillEncrypted = "Encrypted HTTPS"
    override val biometricUnlockTitle = "Unlock with Fingerprint / Face"
    override val biometricUnlockDesc = "Instant biometric authentication alongside your PIN vault"
    override val biometricPromptTitle = "Unlock Prism Media"
    override val biometricPromptSubtitle = "Verify with your fingerprint or face"
    override val biometricUsePinFallback = "Use PIN"
    override val videoSubtitleSettingsTitle = "Subtitles & Sync Studio"
    override val videoSubtitleLoadFile = "Load .SRT / .VTT File"
    override val videoSubtitleSyncLabel = "Subtitle Sync Offset"
    override val videoSubtitleSizeLabel = "Subtitle Size"
    override val videoAudioTrackLabel = "Audio"
    override val videoAudioTrackTitle = "Audio Tracks"
    override val videoNoExtraAudioTracks = "Single default audio track detected"
    override val videoSnapshotAction = "Snapshot"
    override val videoSnapshotSaved = "Frame snapshot saved to Pictures/Prism"
    override val videoSnapshotFailed = "Unable to capture frame for this video"
    override val photoExifTitle = "Photo EXIF & Camera Info"
    override val photoExifCamera = "Camera"
    override val photoExifDate = "Captured"
    override val photoExifDims = "Resolution"
    override val photoExifSettings = "Exposure"
    override val photoExifNoData = "No embedded camera EXIF metadata found"
    override val photoSaveCopyAction = "Save Edited Copy"
    override val photoSavedCopyBanner = "Edited photo saved to Pictures/Prism"
    override val photoSaveFailedBanner = "Could not save photo copy"
    override val backupSectionTitle = "Local Backup & Restore"
    override val backupSectionDesc = "Export or restore playlists, folders, lyrics, bookmarks & settings"
    override val backupExportAction = "Export Backup"
    override val backupImportAction = "Restore Backup"
    override val backupExportSuccess = "Verified Prism backup exported successfully"
    override val backupImportSuccess = "Backup verified and restored successfully"
    override val backupFailedBanner = "Backup file could not be verified or processed"
    override val openedExternalMediaBanner: (String) -> String = { "Opened $it in Prism" }
    override val settingsSearchHint = "Filter settings sections..."
    override val visualEngineTitle = "Visual Identity Mode"
    override val visualEngineSubtitle = "Choose freely between Pure Material You or Material Liquid Glass"
    override val visualEngineMaterialYouTitle = "True Material You"
    override val visualEngineMaterialYouDesc = "Pure solid M3 tonal surfaces & crisp elevation"
    override val visualEngineLiquidGlassTitle = "Liquid Glass"
    override val visualEngineLiquidGlassDesc = "Translucent M3 refractive glass & ambient mesh"
    override val liveThemePreviewLabel = "Live Theme Preview"
    override val settingsNavLayoutSection = "Navigation & Interface Layout"
    override val navBarStyleTitle = "Navigation Bar Style"
    override val navBarStyleName: (NavBarStyle) -> String = {
        when (it) {
            NavBarStyle.FLOATING_PILL -> "Floating Pill"
            NavBarStyle.DOCKED_MATERIAL_YOU -> "Material You Bar"
            NavBarStyle.COMPACT_DOCK -> "Compact Dock"
        }
    }
    override val startupTabTitle = "Default Startup Tab"
    override val uiDensityTitle = "Interface Spacing & Density"
    override val uiDensityName: (UiDensity) -> String = {
        when (it) {
            UiDensity.COMFORTABLE -> "Spacious"
            UiDensity.BALANCED -> "Balanced"
            UiDensity.COMPACT -> "Compact"
        }
    }
    override val hapticsTitle = "Tactile Haptic Feedback"
    override val hapticsDesc = "Subtle vibrations on buttons, sliders & gesture controls"
    override val playerArtworkStyleTitle = "Full Player Artwork Frame"
    override val playerArtworkStyleName: (PlayerArtworkStyle) -> String = {
        when (it) {
            PlayerArtworkStyle.SCALLOPED_COOKIE -> "Expressive Cookie"
            PlayerArtworkStyle.SQUIRCLE_EXPRESSIVE -> "Soft Squircle"
            PlayerArtworkStyle.VINYL_DISC -> "Spinning Vinyl"
            PlayerArtworkStyle.SOFT_PILL_ARCH -> "Editorial Arch"
            PlayerArtworkStyle.CLOVER_FOUR -> "4-Leaf Clover"
            PlayerArtworkStyle.CIRCLE_GLOW -> "Circle Halo"
            PlayerArtworkStyle.ROUNDED_CARD -> "Rounded Cover"
        }
    }
    override val albumArtTintTitle = "Dynamic Album Art Colors"
    override val albumArtTintDesc = "Blend each song's cover palette smoothly into the player"
    override val fullPlayerSpectrumTitle = "Live Audio Spectrum Bars"
    override val fullPlayerSpectrumDesc = "Show real-time PCM frequency visualizer inside Full Player"
    override val seekStepTitle = "Fast-Forward / Rewind Step"
    override val seekStepDesc = "Jump interval for Music & Video quick-seek buttons"
    override val resetCustomizationAction = "Reset Customization to Defaults"
    override val resetCustomizationBanner = "Visual & layout customization restored to defaults"
    override val settingsHardwareSubLabel = "Device Screen & Hardware Drivers"
    override val tabDisplayName: (MediaTab) -> String = {
        when (it) {
            MediaTab.MUSIC -> tabMusic
            MediaTab.VIDEOS -> tabVideos
            MediaTab.PHOTOS -> tabPhotos
            MediaTab.SETTINGS -> tabSettings
        }
    }
    override val onlineEnrichmentTitle = "Smart Online Knowledge & Encyclopedia"
    override val onlineEnrichmentDesc = "Auto-research artist photos, bios, Quran verses, Hadith, lyrics & photo locations"
    override val autoFetchLyricsTitle = "Auto-Fetch Synced Lyrics"
    override val autoFetchLyricsDesc = "Automatically search live timestamped .LRC lyrics when playing a song"
    override val autoFetchArtistPhotoTitle = "Real Artist Portraits"
    override val autoFetchArtistPhotoDesc = "Load verified artist photos & biographical profiles online"
    override val clearKnowledgeCacheAction = "Clear Knowledge Cache"
    override val knowledgeCacheClearedBanner = "Online knowledge cache cleared"
    override val fetchingKnowledgeLabel = "Researching live encyclopedia & metadata..."
    override val refreshKnowledgeAction = "Refresh Info"
    override val fetchLyricsOnlineAction = "Fetch Lyrics"
    override val openEncyclopediaAction = "Read Full Article"
    override val trackEnergyLabel = "Track Energy"
    override val trackBpmLabel = "BPM"
    override val trackMoodLabel = "Mood"
    override val artistBioTitle = "Artist Profile & Encyclopedia"
    override val quranSurahBadge = "Holy Quran Surah"
    override val quranAyahsCountLabel: (Int) -> String = { "$it Verses" }
    override val quranVersesPreviewTitle = "Verified Uthmani Verses"
    override val quranTranslationTitle = "Meaning & Translation"
    override val hadithReferenceTitle = "Authentic Hadith Reference"
    override val encyclopediaSectionTitle = "Live Encyclopedia & Context"
    override val photoLocationLabel = "Captured Location"
    override val knowledgeSourcePrefix = "Sources:"
    override val noOnlineInfoFound = "Connect to the internet or tap Refresh to research this file."
    override val fetchCoverAndLyricsAction = "Fetch Cover & Lyrics"
    override val batchEnrichLibraryAction = "Auto-Fetch Covers & Lyrics for Library"
    override val batchEnrichProgressLabel: (Int, Int) -> String = { done, total -> "Fetching metadata $done / $total..." }
    override val batchEnrichCompletedBanner: (Int, Int) -> String = { covers, lyrics ->
        "Updated $covers HD covers & $lyrics synced lyrics"
    }
    override val coverAndLyricsUpdatedBanner = "HD cover art & lyrics updated"
    override val gaplessPlaybackTitle = "Gapless Audio Playback"
    override val gaplessPlaybackDesc = "Eliminate silence gaps between consecutive album tracks"
    override val audioNormalizationTitle = "Loudness Normalization"
    override val audioNormalizationDesc = "Balance perceived volume evenly across quiet and loud tracks"
    override val headsetUnplugPauseTitle = "Pause on Headset Disconnect"
    override val headsetUnplugPauseDesc = "Automatically pause playback when headphones or Bluetooth unplug"
    override val videoGesturesTitle = "Swipe Gestures (Brightness & Volume)"
    override val videoGesturesDesc = "Drag vertically on left or right side of video to adjust brightness & volume"
    override val videoDoubleTapTitle = "Double-Tap to Quick Seek"
    override val videoDoubleTapDesc = "Double-tap left or right side of video to rewind or fast-forward"
    override val videoRememberPosTitle = "Remember Video Position"
    override val videoRememberPosDesc = "Resume videos right where you stopped watching"
    override val videoSubtitleSizeTitle = "Default Subtitle Font Size"
    override val photoAutoExifTitle = "Auto-Show Camera EXIF & GPS"
    override val photoAutoExifDesc = "Automatically inspect camera metadata & reverse-geocode photo location"
    override val photoHighResTitle = "Full-Resolution Photo Decode"
    override val photoHighResDesc = "Render photos at maximum native sharpness when zooming"
    override val photoSlideshowTransitionTitle = "Slideshow Transition Effect"
    override val slideshowTransitionName: (SlideshowTransitionStyle) -> String = {
        when (it) {
            SlideshowTransitionStyle.FADE -> "Smooth Fade"
            SlideshowTransitionStyle.ZOOM -> "Ken Burns Zoom"
            SlideshowTransitionStyle.SLIDE -> "Dynamic Slide"
        }
    }
    override val surfaceDepthTitle = "Surface Elevation & Depth"
    override val surfaceDepthName: (SurfaceDepthStyle) -> String = {
        when (it) {
            SurfaceDepthStyle.FLAT -> "Flat Minimal"
            SurfaceDepthStyle.ELEVATED -> "Balanced Depth"
            SurfaceDepthStyle.EXPRESSIVE -> "Expressive M3"
        }
    }
    override val languageDisplayName: (AppLanguage) -> String = {
        when (it) {
            AppLanguage.EN -> "English (LTR)"
            AppLanguage.AR -> "العربية (RTL)"
            AppLanguage.FR -> "Français (LTR)"
            AppLanguage.ES -> "Español (LTR)"
        }
    }
    override val contentBlockedBanner = "Blocked: Prism strictly refuses explicit, adult, or indecent content."
    override val lyricsBlockedBanner = "Vulgar or indecent lyrics were automatically blocked by the Purity Shield."
    override val taqwaSectionTitle = "Supplication (Du'a) & Taqwa Reminder"
    override val taqwaSectionSubtitle = "Sincere supplication • Moral warning • Purity shield"
    override val taqwaDuaHeading = "Sincere Supplication (Du'a)"
    override val taqwaDuaBody = "O Allah, we ask You for guidance, piety, chastity, and contentment. Protect our hearing, sight, and hearts from everything that displeases You, bless this app for goodness and beneficial knowledge, and keep us far from all indecency. Forgive us, our parents, and all believers. Ameen."
    override val taqwaWarningHeading = "Warning to Whoever Does Not Fear Allah"
    override val taqwaWarningVerse = "“Indeed, the hearing, the sight, and the heart — about all of those one will be questioned.” (Surah Al-Isra 17:36)"
    override val taqwaWarningBody = "Fear Allah in secret and in public. Allah sees and hears everything you watch and listen to. Do not let your device bear witness against you with explicit or unlawful content — make it a witness for goodness, beneficial knowledge, and the Noble Quran. The creator disavows before Allah any misuse."
    override val taqwaPurityShieldTitle = "Built-in Purity Shield Always Active"
    override val taqwaPurityShieldBody = "Explicit/adult content and vulgar lyrics are automatically refused inside the app core with zero bypass."
    override val taqwaPurityBadgeExplicit = "Explicit blocked"
    override val taqwaPurityBadgeLyrics = "Clean lyrics"
    override val quranHubTitle = "Quran Mushaf & Khatma"
    override val quranHubSubtitle = "Track 114 Surahs, Hifz repeat & Khushu listening"
    override val quranKhatmaProgressLabel = "Khatma Progress"
    override val quranHifzRepeatLabel = "Hifz A-B Repeat"
    override val quranMarkSurahDone = "Mark Completed"
    override val khushuModeTitle = "Khushu Focus Mode"
    override val khushuModeDesc = "Calm, distraction-free player for Quran & lectures"
    override val tasbihTitle = "Adhkar & Tasbih Counter"
    override val tasbihTodayLabel = "Today's Dhikr"
    override val tasbihReset = "Reset"
    override val morningAlarmTitle = "Morning Quran Alarm"
    override val morningAlarmSubtitle = "Wake up gently with Quran or calm Nasheed fade-in"
    override val morningAlarmFadeLabel = "Gentle fade-in"
    override val morningAlarmTestNow = "Preview Fade-In"
    override val audioCutterTitle = "Audio Trimmer & Ringtone"
    override val audioCutterSubtitle = "Trim any clip and export to Ringtones"
    override val audioCutterExportBtn = "Trim & Save Ringtone"
    override val audioCutterSavedToast = "Trimmed clip saved to Ringtones/Prism"
    override val listeningStatsTitle = "Listening Stats & Recap"
    override val listeningStatsSubtitle = "Hours listened, top reciters, formats & weekly chart"
    override val totalPlaysLabel = "Total Plays"
    override val totalMinutesLabel = "Minutes"
    override val topTracksLabel = "Most Played Tracks"
    override val topArtistsLabel = "Top Artists / Reciters"
    override val formatBreakdownLabel = "Audio Formats"
    override val lectureModeTitle = "Lecture & Podcast Mode"
    override val lectureModeDesc = "Auto-resume long lessons & speech speed presets"
    override val skipSilenceTitle = "Skip Long Silences"
    override val skipSilenceDesc = "Automatically compress dead air during lectures"
    override val stereoBalanceTitle = "Stereo Balance (L / R)"
    override val stereoBalanceDesc = "Shift audio focus left or right for comfort"
    override val audioMonoTitle = "Mono Audio (Single Earbud)"
    override val audioMonoDesc = "Combine stereo channels evenly into both ears"
    override val shakeToShuffleTitle = "Shake to Next Track"
    override val shakeToShuffleDesc = "Gently shake phone to jump to the next clean track"
    override val batchTagTitle = "Batch Tag & Cover Editor"
    override val batchTagSubtitle = "Update artist, album, or artwork for all selected tracks"
    override val batchTagApply = "Apply to Selected"
    override val privateVaultTitle = "Private Encrypted Vault"
    override val privateVaultSubtitle = "AES-256 protected folder for personal family media"
    override val privateVaultEmpty = "Your encrypted vault is empty. Import private photos or videos safely."
    override val privateVaultAddBtn = "Lock File into Vault"
    override val privateVaultRestoreBtn = "Restore to Gallery"
    override val privateVaultLockedHint = "Protected with AES-256-GCM encryption"
    override val stripExifShareTitle = "Strip EXIF Privacy on Share"
    override val stripExifShareDesc = "Remove GPS location & camera tags before sharing photos"
    override val collageStudioTitle = "Collage & Watermark Studio"
    override val collageStudioSubtitle = "Combine 2–4 photos with optional personal signature"
    override val collageWatermarkHint = "Watermark signature (optional)"
    override val collageSaveBtn = "Save HD Collage"
    override val customSeedTitle = "Custom Monet Seed Color"
    override val customSeedSubtitle = "Pick any hue or extract Material You colors from a photo"
    override val customSeedFromPhoto = "Extract from Photo"
    override val customSeedClear = "Use Preset Palette"
    override val appIconSwitcherTitle = "Dynamic App Icon"
    override val appIconSwitcherSubtitle = "Switch Prism's home-screen icon style"
    override val themeCodeTitle = "Shareable Theme Code"
    override val themeCodeSubtitle = "Copy or import a complete Prism visual theme code"
    override val themeCodeCopy = "Copy Theme Code"
    override val themeCodeApply = "Import Theme Code"
    override val wifiTransferTitle = "Local Wi-Fi Transfer"
    override val wifiTransferSubtitle = "Send & receive files with PC/tablet over LAN without cables"
    override val wifiTransferStart = "Start Wi-Fi Server"
    override val wifiTransferStop = "Stop Server"
    override val videoColorCalibTitle = "Video Color Calibration"
    override val videoChaptersTitle = "Chapters & Bookmarks"
    override val videoAddChapterBtn = "Save Current Moment"
    override val subtitleColorLabel = "Subtitle Color"
    override val subtitleBgOpacityLabel = "Subtitle Backdrop"
    override val subtitlePositionLabel = "Subtitle Height"
    override val folderPinLabel = "Pin to Top"
    override val folderUnpinLabel = "Unpin Folder"
    override val folderCustomizeTitle = "Customize Folder"
    override val smartMixRecentlyAdded = "Recently Added"
    override val smartMixLongLectures = "Long Lectures"
    override val smartMixHiRes = "Hi-Res Lossless"
    override val smartMixQuran = "Quran & Recitations"
    override val proToolsHubTitle = "Prism Studio & Spiritual Hub"
    override val proToolsHubSubtitle = "Quran Khatma • Vault • Cutter • Stats • Wi-Fi • Collage"
    override val settingsLayoutModeTitle = "Settings Panel Style"
    override val settingsLayoutHorizontal = "Swipe Pages ⇄"
    override val settingsLayoutVertical = "Vertical List ↕"
    override val collapseSectionAction = "Collapse Section"
    override val collapseAllSectionsAction = "Collapse All"
    override val swipeHintLabel = "Swipe left / right ⇄"
    override val tuckToEdgeAction = "Tuck to Screen Edge"
    override val restoreFromEdgeAction = "Bring Mini-Player Back"
    override val edgeDockSideTitle = "Sticky Edge-Tuck Side"
    override val edgeDockRight = "Right Edge"
    override val edgeDockLeft = "Left Edge"
    override val floatPhotoAction = "Minimize to Floating Photo"
    override val floatingPhotoBadge = "Floating Photo"
    override val widgetsSixGalleryTitle = "6 Home Screen Widgets"
    override val widgetsSixGallerySubtitle = "Swipe right & left to preview and pin all 6 Material You widgets"
    override val widget3Title = "Vinyl Square (2×2)"
    override val widget3Desc = "Compact square artwork with live title & play/pause"
    override val widget4Title = "Quran Khatma & Tasbih (4×2)"
    override val widget4Desc = "Live Khatma progress, daily Tasbih count & instant Mushaf launch"
    override val widget5Title = "Pro Audio Deck (4×2)"
    override val widget5Desc = "Now Playing + active EQ preset, speed badge & instant EQ button"
    override val widget6Title = "Quick Studio Dock (4×1)"
    override val widget6Desc = "1-tap Music, Videos, Photos, Quran & Play/Pause dock"
    override val proAudioDeckTitle = "Pro Sound & DSP Studio"
    override val proAudioDeckSubtitle = "Preamp • Pitch • Normalization • Balance • Speech • Hifz"
    override val driveModeTitle = "Drive / Big Controls Mode"
    override val driveModeDesc = "Oversized touch controls for effortless one-hand operation"
    override val muteAudioAction = "Mute"
    override val unmuteAudioAction = "Unmute"
    override val spectrumStyleTitle = "Live Spectrum Style"
    override val spectrumClassicBars = "Classic Bars"
    override val spectrumWavePulse = "Wave Pulse"
    override val spectrumMinimalDots = "Minimal Dots"
    override val swipeArtworkTitle = "Swipe Artwork to Skip"
    override val swipeArtworkDesc = "Swipe left or right on album art to skip Next or Previous"
    override val lyricsSizeLabel = "Lyrics Size"
    override val hifzRemainingLabel: (Int) -> String = { "$it repeats left" }
    override val hifzLoopTitle = "Hifz A-B Repeat"
    override val dspPageSound = "Sound Engine"
    override val dspPageClarity = "Clarity & Ears"
    override val dspPageHifz = "Hifz & Tools"
    override val videoMirrorTitle = "Mirror / Flip Video"
    override val videoMirrorDesc = "Flip video horizontally for tutorials and study clips"
    override val videoAutoHideTitle = "Controls Auto-Hide"
    override val videoAutoHideNever = "Never"
    override val photoFlipAction = "Flip"
    override val photoBrightBoostAction = "Max Bright"
    override val quickPresetsTitle = "1-Tap Aesthetic Presets"
    override val presetMaterialYou = "Pure Material You"
    override val presetLiquidCrystal = "Liquid Crystal"
    override val presetAmoledMidnight = "AMOLED Midnight"
    override val presetEmeraldMosque = "Emerald Serenity"
    override val presetAppliedBanner = "Aesthetic preset applied"
    override val globalSearchTitle = "Spotlight Search"
    override val globalSearchHint = "Search music, videos, photos, surahs, notes..."
    override val globalSearchNoResults = "No matching items across your library"
    override val recycleBinTitle = "Smart Recycle Bin"
    override val recycleBinSubtitle = "Restore accidentally removed library items in one tap"
    override val recycleBinEmpty = "Recycle Bin is clean and empty"
    override val restoreItemAction = "Restore"
    override val restoreAllAction = "Restore All"
    override val emptyBinAction = "Empty Bin"
    override val movedToBinBanner = "Moved to Recycle Bin (tap to restore anytime)"
    override val restoredFromBinBanner = "Restored back to library"
    override val mediaNoteTitle = "Personal Note"
    override val mediaNoteHint = "Write a reflection, timestamp note, or reminder..."
    override val saveNoteAction = "Save Note"
    override val deleteNoteAction = "Delete Note"
    override val noteSavedBanner = "Note saved to item"
    override val reverbTitle = "3D Spatializer & Reverb"
    override val reverbNone = "Off"
    override val reverbStudio = "Studio"
    override val reverbHall = "Concert Hall"
    override val reverbMosque = "Mosque Echo"
    override val reverbSpatial3d = "3D Surround"
    override val tapSyncLyricsTitle = "Tap-to-Sync LRC Studio"
    override val tapSyncLyricsHint = "Play track and tap Stamp on each line as it is sung"
    override val tapSyncStampLine = "Stamp Current Line"
    override val browseTracksTab = "Tracks"
    override val browseArtistsTab = "Artists"
    override val browseAlbumsTab = "Albums"
    override val exportM3uAction = "Export M3U"
    override val importM3uAction = "Import M3U"
    override val m3uExportedBanner = "Playlist exported as M3U"
    override val m3uImportedBanner: (Int) -> String = { "Imported M3U playlist ($it tracks)" }
    override val extractAudioAction = "Extract Audio (M4A)"
    override val extractingAudioBanner = "Extracting audio track from video..."
    override val audioExtractedBanner = "Audio extracted and added to Music library!"
    override val photoEditStudioTitle = "Photo Studio Editor"
    override val photoCropLabel = "Crop Ratio"
    override val photoRotate90Action = "Rotate 90°"
    override val photoBrightnessLabel = "Brightness"
    override val photoContrastLabel = "Contrast"
    override val photoSaturationLabel = "Saturation"
    override val photoSetWallpaperAction = "Set Wallpaper"
    override val photoWallpaperSuccess = "Opening Wallpaper Setter"
    override val photoExportPdfAction = "Export PDF"
    override val photoPdfReadyBanner = "HD PDF document ready to share"
    override val photoCompressAction = "Compress JPEG"
    override val photoCompressedBanner: (String, String) -> String = { before, after -> "Compressed $before → $after" }
    override val photoCompareTitle = "Photo Compare Slider"
    override val photoCompareBefore = "Original"
    override val photoCompareAfter = "Compare"
    override val videoZoomBadge: (Float) -> String = { String.format(java.util.Locale.US, "%.1fx Zoom", it) }
    override val videoFrameBack = "-1 Frame"
    override val videoFrameForward = "+1 Frame"
    override val videoSleepTimerTitle = "Video Sleep Timer"
    override val videoAutoNextTitle = "Auto-Play Next Video"
    override val videoAutoNextDesc = "Automatically advance to the next video when playback finishes"
    override val adhkarTitle = "Morning & Evening Adhkar"
    override val adhkarMorningTab = "Morning"
    override val adhkarEveningTab = "Evening"
    override val adhkarCompletedBadge = "Completed"
    override val adhkarResetAction = "Reset Progress"
    override val audioProfilesTitle = "Audio Device Profiles"
    override val audioProfilesDesc = "Auto-switch EQ, Bass & Reverb for Speaker, Wired, and Bluetooth"
    override val audioOutputSpeaker = "Phone Speaker"
    override val audioOutputWired = "Wired Headset"
    override val audioOutputBluetooth = "Bluetooth Audio"
    override val audioProfileAppliedBanner: (String) -> String = { "Applied audio profile: $it" }
    override val customTabOrderTitle = "Navigation Tab Order"
    override val customTabOrderDesc = "Reorder bottom navigation tabs to fit your thumb"
    override val headerButtonsTitle = "Top Bar Shortcuts"
    override val headerShowStreamTitle = "Show Stream URL Button"
    override val headerShowSplitTitle = "Show Split-Screen Button"
    override val resizeWindowAction = "Resize Window"
    override val onboardingWelcomeTitle = "Welcome to Prism Media"
    override val onboardingWelcomeSubtitle = "A serene, dreamlike sanctuary for Hi-Fi music, cinema video, pro photo editing, and Quranic contemplation."
    override val onboardingFeaturesTitle = "Crafted Like a Dream Studio"
    override val onboardingFeaturesSubtitle = "Every tool you love — unified in expressive Material 3 with zero clutter."
    override val onboardingSetupTitle = "Shape Your Atmosphere"
    override val onboardingSetupSubtitle = "Activate your favorite visual style and smart features right from your very first launch."
    override val onboardingLibraryTitle = "Connect Your Media"
    override val onboardingNextAction = "Next"
    override val onboardingBackAction = "Back"
    override val onboardingSkipAction = "Skip"
    override val onboardingEnterPrismAction = "Enter Prism Media"
    override val replayOnboardingAction = "Replay Welcome & Feature Guide"
    override val trimVideoAction = "Trim Video (MP4)"
    override val videoTrimmerTitle = "Lossless MP4 Video Trimmer"
    override val videoTrimStartLabel = "Start"
    override val videoTrimEndLabel = "End"
    override val videoTrimSuccessBanner = "Trimmed MP4 clip saved to Videos!"
    override val photoArtFilterLabel = "Artistic Preset"
    override val photoWatermarkHint = "Optional text watermark / signature"
    override val recycleDaysLeft: (Int) -> String = { "$it days left" }
    override val quickTileLabel = "Prism Play"
    override val sharedToPrismBanner = "Imported shared media into Prism"
    override val smartImportTitle = "Import Folders & Media"
    override val smartImportSubtitle = "Jump straight into allowed folders or pick files directly"
    override val smartImportSafTip = "Android blocks picking the root or Downloads folder directly. Choose a subfolder below (Music/Movies/Pictures) or tap Direct File Import for files inside Downloads."
    override val smartImportQuickFoldersLabel = "Direct Allowed Folders (No New Folder Needed)"
    override val smartImportDirectFilesAction = "Pick Files Directly (Works in Downloads)"
    override val smartImportBrowseOther = "Browse Folders"
    override val genreMoodName: (TrackGenreMood) -> String = {
        when (it) {
            TrackGenreMood.QURAN_SPIRITUAL -> "Quran & Spiritual"
            TrackGenreMood.NASHEED_WARM -> "Nasheed & Warm"
            TrackGenreMood.CLASSICAL_ORCHESTRAL -> "Classical & Acoustic"
            TrackGenreMood.ELECTRONIC_ENERGY -> "Electronic & Energy"
            TrackGenreMood.LOFI_AMBIENT -> "Calm & Night"
            TrackGenreMood.LECTURE_PODCAST -> "Lecture & Podcast"
            TrackGenreMood.ACOUSTIC_POP -> "Dynamic Mix"
        }
    }
    override val settingsPlayerCard = "Player Card & Pro Tech"
    override val settingsPlayerCardDesc = "Full-screen expressive shapes, genre colors, DSP & live preview"
    override val playerGenreColorsTitle = "Genre-Adaptive Dynamic Colors"
    override val playerGenreColorsDesc = "Shift player hues automatically based on song genre & mood"
    override val playerTechTelemetryTitle = "Audio Engine Telemetry Pill"
    override val playerTechTelemetryDesc = "Show live format, sample rate & 32-bit DSP status badge"
    override val playerExtrasSubtleTitle = "Subtle Compact Extras Button"
    override val playerExtrasSubtleDesc = "Keep tools neatly tucked behind a sleek low-profile button"
    override val playerBitExactDspTitle = "Bit-Exact Pure Bypass"
    override val playerBitExactDspDesc = "Bypass EQ & reverb coloration for pure reference output"
    override val playerExpressivePlayMorphTitle = "Expressive Play Button Morph"
    override val playerExtrasStudioTitle = "Player Tools & Studio"
    override val livePreviewBadge = "LIVE PREVIEW"
    override val settingsLockScreen = "Lock Screen & Identity"
    override val settingsLockScreenDesc = "Control Prism's lock-screen presence, clock, art & playback"
    override val lockScreenControlsTitle = "Lock-Screen System Media Card"
    override val lockScreenControlsDesc = "Display Prism playback controls & seek bar on lock screen"
    override val lockScreenShowArtworkTitle = "Show Album Art on Lock Screen"
    override val lockScreenShowIdentityTitle = "Prism Branded Identity Badge"
    override val lockScreenShowIdentityDesc = "Show Prism Media identity & active audio mood on lock screen"
    override val lockScreenFullScreenTitle = "Full-Screen Lock Player Mode"
    override val lockScreenFullScreenDesc = "Show Prism's expressive player when waking the locked screen"
    override val lockScreenShowClockTitle = "Expressive Lock-Screen Clock"
    override val lockScreenShowLyricsTitle = "Live Synced Lyric Line"
    override val lockScreenBlurBackdropTitle = "Dreamy Aurora Art Backdrop"
    override val lockScreenCompactModeTitle = "Compact Lock-Screen Card"
    override val lockScreenPreviewAction = "Preview Lock Screen Live"
    override val lockScreenUnlockAction = "Open Prism Media"
    override val pipMusicTitle = "Music Picture-in-Picture"
    override val pipMusicDesc = "Optional floating mini window when leaving while playing music (off keeps silent background audio)"
    override val pipVideoTitle = "Video Picture-in-Picture"
    override val pipVideoDesc = "Optional floating video window when leaving the app"
    override val pipPhotoTitle = "Photo Picture-in-Picture"
    override val pipPhotoDesc = "Optional floating photo preview when leaving the photo viewer"
    override val pipPhotoBadge = "Prism Photo PiP"
    override val filtersAndToolsAction = "Filters & Tools"
    override val clearFiltersAction = "Reset"
    override val moreHeaderActionsDesc = "More actions"
    override val importDownloadsDirectAction = "Pick Files from Downloads (No Restriction)"
    override val importDownloadsPrivacyExplainer = "Android blocks granting whole-folder access to the main Download folder for privacy. Use the button above to pick files inside Downloads directly, or enter a subfolder inside Download (like Music or Prism)."
    override val quickStudioTitle = "Quick Customization Studio"
    override val quickStudioSubtitle = "Tune theme, cards, backdrop & feel live from anywhere"
    override val cardStyleTitle = "Media Card Style"
    override val cardStyleGlass = "Liquid Glass"
    override val cardStyleElevated = "Elevated M3"
    override val cardStyleMinimal = "Minimal Outline"
    override val cardStyleVibrant = "Vibrant Tonal"
    override val backdropStyleTitle = "Ambient Backdrop Style"
    override val backdropAurora = "Aurora Orbs"
    override val backdropMist = "Calm Mist"
    override val backdropRibbon = "Prism Ribbons"
    override val backdropClean = "Pure Clean"
    override val motionFeelTitle = "Touch & Motion Physics"
    override val motionBouncy = "Expressive Bounce"
    override val motionSmooth = "Silky Smooth"
    override val motionSnappy = "Ultra Snappy"
    override val motionReduced = "Calm Minimal"
    override val headerStyleTitle = "Top Header Style"
    override val headerBold = "Expressive Bold"
    override val headerCompact = "Minimal Compact"
    override val headerCentered = "Centered Elegant"
    override val swipeGesturesTitle = "Interactive Track Swipes"
    override val swipeGesturesDesc = "Swipe tracks right to favorite or left to add to a playlist"
    override val showFormatBadgesTitle = "Show Format & Size Badges"
    override val showFormatBadgesDesc = "Display MP3/FLAC badges and file sizes on library cards"
    override val accentStrengthTitle = "Color Vibrancy & Accent Depth"
    override val swipeFavoriteHint = "Favorite"
    override val swipePlaylistHint = "Playlist"
    override val appVersionLabel = "beta 4.4"
    override val proStudioTitle = "Pro Audio Mastering Studio"
    override val proStudioSubtitle = "10-Band EQ, Auto-Studio Mastering & 15+ Real-Time DSP Effects"
    override val proStudioOpenAction = "Open Pro Audio Studio"
    override val studioAutoTitle = "Auto Studio Mastering"
    override val studioAutoDesc = "Automatically detects track style & applies studio-grade EQ, warmth, width & dynamics"
    override val studioAutoAnalyzeNow = "Auto-Master Now"
    override val studioAutoAppliedBanner = "Studio Master applied"
    override val studioAutoNoTrackHint = "Play a track first to auto-master its studio profile"
    override val studioBypassOriginal = "Original (A)"
    override val studioBypassMastered = "Mastered (B)"
    override val studioResetAll = "Reset Studio"
    override val studioVuMeterLabel = "Live Output VU (L / R)"
    override val studioRackEqTitle = "10-Band Pro Equalizer (31Hz–16kHz)"
    override val studioRackToneTitle = "Analog Warmth, Clarity & Soundstage"
    override val studioRackDynamicsTitle = "Dynamics, Protection & Acoustic Filters"
    override val studioRackHeadphoneTitle = "Headphone Calibration & Saved Presets"
    override val studioTubeWarmth = "Analog Tube Warmth"
    override val studioStereoWidth = "Mid-Side Stereo Width"
    override val studioSubBassPunch = "48Hz Sub-Bass Punch"
    override val studioAirBrilliance = "12kHz Studio Air & Brilliance"
    override val studioVocalClarity = "3.2kHz Vocal Presence Clarity"
    override val studioCompressor = "Studio Dynamic Glue Compressor"
    override val studioCrossfeedTitle = "Binaural Headphone Crossfeed"
    override val studioCrossfeedDesc = "Natural speaker-style acoustic blend to prevent headphone fatigue"
    override val studioTruePeakTitle = "True-Peak Brickwall Limiter"
    override val studioTruePeakDesc = "Protects speakers & ears at -0.5 dBFS with zero harsh digital clipping"
    override val studioNightModeTitle = "Late-Night Whisper Mode"
    override val studioNightModeDesc = "Tames sudden loud peaks and gently lifts quiet details at night"
    override val studioRumbleFilterTitle = "High-Pass Sub-Rumble Filter"
    override val studioChannelSwapTitle = "Swap Stereo Channels (L ↔ R)"
    override val studioChannelSwapDesc = "Instantly flip left and right stereo channels"
    override val studioHeadphoneProfileTitle = "Headphone & Speaker Calibration"
    override val studioCustomPresetsTitle = "My Custom Studio Presets"
    override val studioSavePresetAction = "Save Profile"
    override val studioSavePresetHint = "Preset name (e.g. Late Night Hi-Fi)"
    override val studioNoCustomPresets = "Save your custom 10-band curve & DSP rack for 1-tap recall."
    override val studioPresetSavedBanner = "Studio preset saved"
    override val studioPresetLoadedBanner = "Studio preset loaded"
    override val playerToolsDrawerOpen = "Tools & Studio"
    override val playerToolsDrawerClose = "Hide Menu"
    override val defaultPlayerTitle = "Default Media Player"
    override val defaultPlayerDesc = "Set Prism as your default Music, Video & Photo player in Android Settings"
    override val defaultPlayerAction = "Set as Default"
    override val defaultPlayerOpenedBanner = "Select Open by Default to make Prism your default player"
    override val storageAccessHubTitle = "Storage & Folder Access Hub"
    override val storageAccessHubDesc = "Grant persistent access to Music, Movies, Pictures & Downloads cleanly and safely"
    override val storageGrantedFoldersLabel: (Int) -> String = { count -> "$count granted folders active" }
    override val storageGrantFolderAction = "Grant Folder Access"
    override val storagePickDownloadsAction = "Access Downloads Files"
    override val storagePickGalleryAction = "Gallery Access"
    override val storageAccessReadyBadge = "Safe Persistent Access"
    override val selectiveFolderChecklistTitle = "Choose Folders to Scan"
    override val selectiveFolderChecklistHint = "Check only the folders you want — Prism will never scan unchecked folders."
    override val selectiveConfirmScanBtn: (Int) -> String = { n -> "Confirm & Scan Selected ($n)" }
    override val selectiveSelectAll = "Select All"
    override val selectiveClearAll = "Clear"
    override val selectiveScanningOnlyCheckedHint = "Strict scope: only checked folders & selected media types will be scanned."
    override val selectiveDirectFilesBadge: (Int) -> String = { n -> "$n files" }
    override val selectiveMediaTypesLabel = "Media Types to Scan"
    override val selectiveEmptyChecklistHint = "Tap any device folder above (Music, Movies, Pictures, DCIM…) to list its subfolders with checkboxes."
    override val selectiveInspectingFoldersLabel = "Reading existing folders…"
    override val selectiveScannedConfirmedBanner: (Int, Int) -> String = { folders, files ->
        "Scanned $files items strictly from $folders checked folder(s)"
    }
    override val settingsStudioHubSectionTitle = "Prism Studio & Tools Hub"
    override val settingsStudioHubSectionSubtitle = "Quran, Adhkar, Vault, Stats, Collage, Wi-Fi & Audio Profiles"

    override fun proEqPresetName(preset: com.example.model.ProEqPreset): String = when (preset) {
        com.example.model.ProEqPreset.FLAT -> "Flat Reference"
        com.example.model.ProEqPreset.STUDIO_MASTER -> "Studio Master"
        com.example.model.ProEqPreset.QURAN_CRYSTAL -> "Quran Crystal"
        com.example.model.ProEqPreset.DEEP_SUB_CINEMA -> "Deep Sub Cinema"
        com.example.model.ProEqPreset.ACOUSTIC_WARMTH -> "Acoustic Warmth"
        com.example.model.ProEqPreset.VOCAL_AIR -> "Vocal Air"
        com.example.model.ProEqPreset.CONCERT_HALL -> "Concert Hall"
        com.example.model.ProEqPreset.LATE_NIGHT -> "Late Night"
        com.example.model.ProEqPreset.ELECTRONIC_PUNCH -> "Electronic Punch"
        com.example.model.ProEqPreset.LOFI_VELVET -> "Lo-Fi Velvet"
        com.example.model.ProEqPreset.CUSTOM -> "Custom Curve"
    }

    override fun headphoneProfileName(profile: com.example.model.HeadphoneTuningProfile): String = when (profile) {
        com.example.model.HeadphoneTuningProfile.NEUTRAL_STUDIO -> "Neutral Studio"
        com.example.model.HeadphoneTuningProfile.OPEN_BACK_HIFI -> "Open-Back Hi-Fi"
        com.example.model.HeadphoneTuningProfile.IEM_IN_EAR -> "In-Ear IEM"
        com.example.model.HeadphoneTuningProfile.BASS_HEAD_CLUB -> "Bass-Head Club"
        com.example.model.HeadphoneTuningProfile.SPEAKER_COMPENSATION -> "Speaker Boost"
    }

    override fun autoStudioCategoryName(category: com.example.model.AutoStudioCategory): String = when (category) {
        com.example.model.AutoStudioCategory.QURAN_SANCTUARY -> "Quran Sanctuary"
        com.example.model.AutoStudioCategory.LOSSLESS_MASTERING -> "Lossless Reference"
        com.example.model.AutoStudioCategory.VOCAL_BROADCAST -> "Vocal Broadcast"
        com.example.model.AutoStudioCategory.ACOUSTIC_WARMTH -> "Acoustic Warmth"
        com.example.model.AutoStudioCategory.ELECTRONIC_STAGE -> "Electronic Stage"
        com.example.model.AutoStudioCategory.CINEMATIC_WIDE -> "Cinematic Wide"
        com.example.model.AutoStudioCategory.BALANCED_STUDIO -> "Studio Master"
    }
}

private object ArabicStrings : AppStrings {
    override val appTitle = "بريزم للوسائط"
    override val tabMusic = "الموسيقى"
    override val tabVideos = "الفيديو"
    override val tabPhotos = "الصور"
    override val tabSettings = "الإعدادات"
    override val selectMediaFolder = "اختيار مجلد الوسائط"
    override val importFiles = "استيراد ملفات"
    override val pickGalleryMedia = "اختيار صور وفيديو"
    override val loadDemoMedia = "تحميل وسائط تجريبية"
    override val onboardingTitle = "استوديو الوسائط جاهز للتشغيل"
    override val onboardingSubtitle = "اختر مجلدًا أو ملفات من جهازك لتشغيل الصوتيات بجودة عالية ومشاهدة الفيديو وتصفح الصور — أو جرّب فورًا عبر الوسائط التجريبية."
    override val scanningLibrary = "جاري فحص ملفات الوسائط..."
    override val searchAudioHint = "ابحث عن مقطع أو فنان أو صيغة..."
    override val filterAll = "الكل"
    override val favoritesOnly = "المفضلة"
    override val tracksCount: (Int) -> String = { "$it مقطع" }
    override val videosCount: (Int) -> String = { "$it فيديو" }
    override val photosCount: (Int) -> String = { "$it صورة" }
    override val nowPlaying = "قيد التشغيل الآن"
    override val equalizerTitle = "معادل الصوت"
    override val equalizerSubtitle = "معالجة صوتية خماسية النطاقات بالوقت الفعلي"
    override val presetFlat = "مسطح"
    override val presetDeepBass = "جهير عميق"
    override val presetVocalBoost = "تعزيز الصوت"
    override val presetClub = "نادي ليلي"
    override val presetCustom = "مخصص"
    override val eqReset = "إعادة ضبط"
    override val eqActiveStatus = "المعالج نشط"
    override val eqBypassedStatus = "متوقف"
    override val bassBoostLabel = "تضخيم البيس"
    override val sleepTimerTitle = "مؤقت النوم"
    override val sleepTimerSubtitle = "إيقاف التشغيل تلقائيًا عند انتهاء الوقت"
    override val minutesShort: (Int) -> String = { "$it د" }
    override val customTimer = "مخصص"
    override val customTimerDialogTitle = "مؤقت نوم مخصص"
    override val customTimerHint = "عدد الدقائق (1 - 360)"
    override val startTimer = "بدء"
    override val cancelTimer = "إلغاء المؤقت"
    override val timerActiveRemaining: (String) -> String = { "يتوقف خلال $it" }
    override val menuPlayNow = "تشغيل الآن"
    override val menuViewNow = "عرض الآن"
    override val menuAddFavorite = "إضافة إلى المفضلة"
    override val menuRemoveFavorite = "إزالة من المفضلة"
    override val menuAddToPlaylist = "إضافة لقائمة تشغيل"
    override val menuFileDetails = "معلومات الملف"
    override val menuRename = "إعادة تسمية"
    override val menuShare = "مشاركة"
    override val menuRemove = "حذف من المكتبة"
    override val renameDialogTitle = "إعادة تسمية الملف"
    override val renameHint = "أدخل الاسم الجديد"
    override val save = "حفظ"
    override val cancel = "إلغاء"
    override val sortTitle = "ترتيب حسب"
    override val sortOptionName: (MediaSortOption) -> String = {
        when (it) {
            MediaSortOption.NEWEST -> "الأحدث"
            MediaSortOption.NAME -> "الاسم"
            MediaSortOption.DURATION -> "المدة"
            MediaSortOption.SIZE -> "حجم الملف"
        }
    }
    override val playlistsChip = "قوائم التشغيل"
    override val playlistsTitle = "قوائم التشغيل الخاصة"
    override val createPlaylist = "إنشاء قائمة"
    override val playlistNameHint = "اسم القائمة..."
    override val noPlaylistsYet = "أنشئ أول قائمة تشغيل لتنظيم مقاطعك الصوتية."
    override val playlistTrackCount: (Int) -> String = { "$it مقطع" }
    override val clearPlaylistFilter = "جميع المقاطع"
    override val fileDetailsTitle = "معلومات الملف"
    override val detailName = "اسم الملف"
    override val detailFormat = "الامتداد"
    override val detailMime = "نوع MIME"
    override val detailSize = "الحجم"
    override val detailMetadata = "الخصائص"
    override val detailLocation = "مسار المصدر"
    override val close = "إغلاق"
    override val settingsAppearance = "المظهر والثيمات والأيقونات"
    override val darkMode = "الوضع الداكن"
    override val darkModeDesc = "التبديل بين الوضع الداكن الاحترافي والوضع الفاتح"
    override val amoledBlack = "أسود OLED خالص"
    override val amoledBlackDesc = "خلفيات سوداء حقيقية #000000 لتوفير الطاقة في الوضع الداكن"
    override val dynamicPalette = "ثيمات Material You الحقيقية"
    override val iconStyleTitle = "نمط الأيقونات (ستايل جوجل)"
    override val iconStyleName: (IconPackStyle) -> String = {
        when (it) {
            IconPackStyle.GOOGLE_ROUNDED -> "جوجل دائري"
            IconPackStyle.FILLED_EXPRESSIVE -> "ممتلئ بارز"
            IconPackStyle.OUTLINED_MINIMAL -> "مفرغ أنيق"
            IconPackStyle.DUOTONE_PILL -> "ثنائي اللون"
        }
    }
    override val languageSetting = "لغة الواجهة"
    override val languageDesc = "التبديل الفوري بين العربية (RTL) والإنجليزية (LTR)"
    override val settingsPlayback = "التشغيل والصوت"
    override val autoPlayNext = "التشغيل التلقائي للمقطع التالي"
    override val autoPlayNextDesc = "الانتقال تلقائيًا إلى المقطع التالي عند الانتهاء"
    override val resumePosition = "استئناف موضع التشغيل"
    override val resumePositionDesc = "حفظ موضع التوقف الدقيق لكل مقطع صوتي"
    override val filterShortClips = "تصفية المقاطع القصيرة (< 5 ثوانٍ)"
    override val filterShortClipsDesc = "إخفاء النغمات والمقاطع القصيرة جدًا من قائمة الموسيقى"
    override val settingsWidgetTitle = "ودجت الشاشة الرئيسية (ستايل جوجل)"
    override val settingsWidgetDesc = "ودجت Material You للتحكم الفوري بالتشغيل والإيقاف والتنقل من الشاشة الرئيسية."
    override val addWidgetAction = "إضافة الودجت للشاشة الرئيسية"
    override val widgetPinnedBanner = "تم إرسال طلب تثبيت الودجت للشاشة الرئيسية"
    override val widgetManualHint = "اضغط مطولًا على الشاشة الرئيسية ← الودجات ← بريزم لإضافته."
    override val settingsSecurityTitle = "الأمان وموثوقية Google Play"
    override val settingsSecurityDesc = "شهادة توقيع SHA-256 معتمدة، بدون أذونات تخزين حساسة، وحماية كاملة للاتصال."
    override val securityCertLabel = "بصمة شهادة التوقيع SHA-256"
    override val securityPrivacyLabel = "خصوصية التخزين"
    override val securityPrivacyValue = "آمن بالكامل عبر SAF (بدون صلاحيات خطرة)"
    override val securityNetworkLabel = "أمان الشبكة"
    override val securityNetworkValue = "HTTPS فقط (حظر الاتصال غير المشفر)"
    override val securityCompatLabel = "بيئة التشغيل"
    override val designerCardTitle = "تصميم وتطوير"
    override val designerName = "سعيد بودحيم"
    override val designerRole = "المصمم والمطور • استوديو بريزم للوسائط"
    override val settingsLibrary = "مكتبة الوسائط"
    override val rescanFolders = "إعادة فحص / استيراد مجلد"
    override val clearLibrary = "مسح المكتبة"
    override val emptyMusicFiltered = "لا توجد مقاطع مطابقة. جرّب فلترًا آخر أو عطّل تصفية أقل من 5 ثوانٍ."
    override val emptyVideos = "لا توجد فيديوهات بعد. استورد مجلدًا أو حمّل الوسائط التجريبية."
    override val emptyPhotos = "لا توجد صور بعد. اختر صورًا أو حمّل الوسائط التجريبية."
    override val speedLabel = "السرعة"
    override val pipMode = "نافذة عائمة"
    override val brightness = "السطوع"
    override val volume = "الصوت"
    override val paletteName: (AccentPalette) -> String = {
        when (it) {
            AccentPalette.LAVENDER -> "لافندر"
            AccentPalette.EMERALD -> "زمردي"
            AccentPalette.OCEAN -> "أزرق محيطي"
            AccentPalette.AMBER -> "عنبر دافئ"
            AccentPalette.ROSE -> "وردي مخملي"
            AccentPalette.CORAL -> "مرجاني غروب"
            AccentPalette.MIDNIGHT -> "منتصف الليل"
            AccentPalette.SYSTEM -> "ديناميكي"
        }
    }
    override val eqPresetName: (EqPreset) -> String = {
        when (it) {
            EqPreset.FLAT -> "مسطح"
            EqPreset.DEEP_BASS -> "جهير عميق"
            EqPreset.VOCAL_BOOST -> "تعزيز الصوت"
            EqPreset.CLUB -> "نادي"
            EqPreset.CUSTOM -> "مخصص"
        }
    }
    override val demoLoadedBanner = "تم تحميل الوسائط التجريبية للموسيقى والفيديو والصور"
    override val libraryClearedBanner = "تم مسح مكتبة الوسائط"
    override val importedCountBanner: (Int) -> String = { "تم استيراد $it ملف وسائط" }
    override val timerExpiredToast = "انتهى مؤقت النوم — تم إيقاف التشغيل"
    override val autoPipTitle = "صورة داخل صورة تلقائيًا عند الخروج"
    override val autoPipDesc = "يتحول التطبيق تلقائيًا لنافذة عائمة (PiP) عند الخروج أثناء تشغيل الفيديو أو الموسيقى"
    override val multiWindowTitle = "النوافذ المتعددة داخل التطبيق"
    override val splitScreenBannerOn = "تم تفعيل وضع النوافذ المتعددة داخل التطبيق"
    override val splitScreenBannerOff = "تم الرجوع لوضع النافذة الواحدة"
    override val paneEqualizer = "المعادل"
    override val floatVideoInApp = "تطفية الفيديو داخل التطبيق"
    override val expandVideo = "ملء الشاشة"
    override val shuffleLabel = "تشغيل عشوائي"
    override val repeatOff = "إيقاف التكرار"
    override val repeatAll = "تكرار القائمة"
    override val repeatOne = "تكرار الأغنية"
    override val upNextQueue = "قائمة الانتظار"
    override val queueEmpty = "لا توجد مقاطع قادمة في قائمة الانتظار"
    override val companionTools = "أدوات التحكم السريع"
    override val pipAudioBadge = "بريزم • نافذة صوت عائمة"
    override val playStreamUrl = "تشغيل رابط"
    override val streamUrlDialogTitle = "تشغيل بث أو رابط مباشر (HTTPS)"
    override val streamUrlHint = "https://... (mp3, mp4, m3u8, راديو)"
    override val streamTitleHint = "اسم اختياري"
    override val streamTypeAudio = "بث صوتي"
    override val streamTypeVideo = "بث فيديو"
    override val playStreamAction = "تشغيل الآن"
    override val invalidHttpsUrlBanner = "يرجى إدخال رابط وسائط صحيح يبدأ بـ https://"
    override val abLoopSetA = "نقطة A"
    override val abLoopSetB = "نقطة B"
    override val abLoopActive = "تكرار A↔B"
    override val searchVideosHint = "ابحث عن فيديو أو صيغة..."
    override val searchPhotosHint = "ابحث عن صورة..."
    override val videoAspectFit = "ملائم"
    override val videoAspectCrop = "ملء مقصوص"
    override val videoAspectFill = "تمديد"
    override val videoLockedMsg = "تم قفل الشاشة — اضغط القفل للفتح"
    override val videoUnlockedMsg = "تم فتح التحكم"
    override val videoLoopLabel = "تكرار"
    override val photoSlideshow = "عرض تلقائي"
    override val photoRotate = "تدوير"
    override val photoFilterOriginal = "أصلي"
    override val photoFilterVivid = "حيوي"
    override val photoFilterWarm = "دافئ"
    override val photoFilterNoir = "أبيض وأسود"
    override val photoFilterCool = "بارد"
    override val playerBarSectionTitle = "تخصيص شريط المشغل والإضافات"
    override val playerBarSectionDesc = "تحكم في شريط الوقت المموج، تقليص الحجم، التعويم، وأزرار الإضافات"
    override val squigglyBarTitle = "شريط وقت متموج (Material You)"
    override val squigglyBarDesc = "حركة تموج جوجل الأصلية لشريط الأغنية أثناء التشغيل"
    override val compactBarAction = "تقليص"
    override val expandBarAction = "تكبير"
    override val floatPlayerAction = "تعويم"
    override val dockPlayerAction = "تثبيت"
    override val customizeBarTitle = "تخصيص شريط المشغل"
    override val barAddonsHeader = "الإضافات الظاهرة في الشريط"
    override val addonSpectrum = "طيف صوتي"
    override val addonSeek10 = "تقديم ±10ث"
    override val addonFavorite = "المفضلة"
    override val addonSpeed = "السرعة"
    override val addonAbLoop = "تكرار A-B"
    override val addonEq = "معادل سريع"
    override val addonTimer = "مؤقت"
    override val addonQueue = "قائمة الانتظار"
    override val dragToXHint = "اسحب إلى علامة X للإيقاف والإغلاق"
    override val releaseToStopHint = "أفلت الآن لإيقاف التشغيل نهائيًا"
    override val pipStopAction = "إيقاف"
    override val expandSectionDesc = "توسيع القسم"
    override val collapseSectionDesc = "طي القسم"
    override val expandAllSections = "توسيع الكل"
    override val collapseAllSections = "طي الكل"
    override val wallpaperDynamicHint = "يتغير تلقائيًا مع ألوان خلفية هاتفك (Material You)"
    override val settingsSystemSection = "اللغة، درع الأمان وحقوق التصميم"
    override val settingsMusicSection = "إعدادات الموسيقى"
    override val settingsMusicDesc = "التشغيل التلقائي، الكلمات، المعادل الصوتي، ومؤقت النوم"
    override val settingsVideoSection = "إعدادات الفيديو"
    override val settingsVideoDesc = "أبعاد الشاشة الافتراضية، السرعة، التكرار، والنافذة العائمة PiP"
    override val settingsPhotosSection = "إعدادات الصور والمعرض"
    override val settingsPhotosDesc = "عدد الأعمدة، سرعة العرض التلقائي، وإظهار العناوين"
    override val showLyricsSetting = "إظهار كلمات الأغنية في المشغل"
    override val showLyricsDesc = "عرض بطاقة المغني وكلمات الأغنية التفاعلية أثناء التشغيل"
    override val videoDefaultAspectTitle = "أبعاد عرض الفيديو الافتراضية"
    override val videoDefaultSpeedTitle = "سرعة الفيديو الافتراضية"
    override val videoDefaultLoopTitle = "تكرار الفيديو تلقائيًا"
    override val videoDefaultLoopDesc = "إعادة تشغيل الفيديو تلقائيًا عند نهايته"
    override val photoGridColumnsTitle = "أعمدة شبكة الصور"
    override val photoGridAuto = "تلقائي (حسب الشاشة)"
    override val photoColumnsCount: (Int) -> String = { "$it أعمدة" }
    override val photoSlideshowSpeedTitle = "مدة العرض التلقائي للصور"
    override val secondsShort: (Int) -> String = { "$it ث" }
    override val photoShowTitlesLabel = "إظهار أسماء الصور"
    override val photoShowTitlesDesc = "عرض شريط عنوان الصورة أسفل كل بطاقة في المعرض"
    override val fontSizeTitle = "حجم خط التطبيق"
    override val fontSizeSmall = "صغير"
    override val fontSizeDefault = "عادي"
    override val fontSizeLarge = "كبير"
    override val fontSizeXL = "كبير جدًا"
    override val screenProfileTitle = "التعرف التلقائي على الشاشة والنوتش"
    override val notchDetectedLabel = "تم رصد النوتش وحمايته"
    override val notchStandardLabel = "شاشة كاملة متطابقة"
    override val screenAdaptedBadge = "متطابق تلقائيًا"
    override val artistProfileBadge = "معرّف الفنان"
    override val lyricsTitle = "كلمات الأغنية"
    override val editLyricsAction = "تعديل الكلمات"
    override val lyricsPlaceholder = "أضف أو الصق كلمات الأغنية هنا..."
    override val noLyricsYetHint = "لا توجد كلمات محفوظة لهذه الأغنية بعد. اضغط «تعديل الكلمات» لإضافتها."
    override val lyricsSavedBanner = "تم حفظ كلمات الأغنية بنجاح"
    override val notifSettingTitle = "أيقونة وإشعارات التشغيل بالهاتف"
    override val notifSettingDesc = "إظهار أيقونة بريزم وأزرار التحكم في شريط إشعارات الهاتف"
    override val widgetCompactTitle = "ودجت 1 • شريط المشغل السريع (4×1)"
    override val widgetHubTitle = "ودجت 2 • مركز الوسائط الكامل (4×2)"
    override val widgetHubDesc = "تحكم كامل بالتشغيل مع اختصارات فورية للموسيقى والفيديو والصور."
    override val addHubWidgetAction = "إضافة ودجت المركز (4×2)"
    override val tamperGuardTitle = "درع الحماية ضد التعديل والاختراق"
    override val tamperGuardDesc = "فحص شهادة التوقيع وكشف أي تعديل مع تجهيز تنبيه بريد إلكتروني للمالك"
    override val tamperStatusClean = "محمي بالكامل • التوقيع الأصلي سليم"
    override val tamperStatusAlert = "تم رصد محاولة تعديل غير مصرح بها!"
    override val sendSecurityReportAction = "إرسال تنبيه الأمان إلى saidboudhim0@gmail.com"
    override val tamperDialogTitle = "تنبيه أمان — تعديل التطبيق"
    override val tamperDialogMessage = "رصد درع بريزم تغييرًا غير مصرح به في حزمة أو توقيع التطبيق. يمكنك إرسال تقرير فني كامل عن الجهاز والتعديل فورًا لبريد المالك."
    override val securityAlertEmailLabel = "بريد تنبيهات الأمان"
    override val securityInstallerLabel = "مصدر التثبيت"
    override val selectModeAction = "تحديد"
    override val selectedCountLabel: (Int, Int) -> String = { sel, total -> "$sel / $total محدد" }
    override val selectAllAction = "الكل"
    override val deselectAllAction = "إلغاء"
    override val invertSelectionAction = "عكس"
    override val deleteAction = "حذف"
    override val confirmDeleteTitle = "حذف العناصر المحددة؟"
    override val confirmDeleteMessage: (Int) -> String = { count -> "هل تريد حذف $count عنصر من مكتبة بريزم؟" }
    override val deletedCountBanner: (Int) -> String = { count -> "تم حذف $count عنصر" }
    override val favoritedCountBanner: (Int) -> String = { count -> "تم تحديث المفضلة لـ $count عنصر" }
    override val addedToPlaylistBanner: (Int) -> String = { count -> "تمت إضافة $count مقطع لقائمة التشغيل" }
    override val viewModeTitle = "طريقة العرض"
    override val viewModeList = "قائمة مفصلة"
    override val viewModeCompact = "عرض مدمج"
    override val viewModeGrid = "شبكة بطاقات"
    override val queueSortName = "الاسم"
    override val queueSortArtist = "الفنان"
    override val queueSortDuration = "المدة"
    override val queueSortReverse = "عكس الترتيب"
    override val queueSortShuffle = "خلط عشوائي"
    override val moveUpAction = "تحريك لأعلى"
    override val moveDownAction = "تحريك لأسفل"
    override val removeFromQueueAction = "إزالة من القائمة"
    override val queueReorderedBanner = "تم تحديث ترتيب قائمة التشغيل الحالية"
    override val videoRotateLabel = "تدوير"
    override val videoMuteLabel = "كتم"
    override val videoUnmuteLabel = "صوت"
    override val videoSubtitlesLabel = "ترجمة"
    override val videoSubtitlesLoaded = "الترجمة مفعلة"
    override val videoSubtitlesOff = "بدون ترجمة"
    override val videoNightDimmerLabel = "ليلي"
    override val videoSleepTimerLabel = "مؤقت"
    override val videoHoldSpeedBadge = "تسريع 2.0x أثناء الضغط"
    override val videoPrevAction = "الفيديو السابق"
    override val videoNextAction = "الفيديو التالي"
    override val securityScoreTitle = "مستوى الحماية الشامل"
    override val appLockTitle = "قفل التطبيق برمز سري (PIN)"
    override val appLockDesc = "تشفير PBKDF2 للرمز السري مع حظر تلقائي عند محاولات التطفل"
    override val appLockSetupAction = "تعيين رمز"
    override val appLockChangePinAction = "تغيير الرمز"
    override val appLockDisableAction = "إلغاء القفل"
    override val appLockOnBackgroundTitle = "قفل تلقائي عند الخروج"
    override val appLockOnBackgroundDesc = "طلب الرمز السري فور الرجوع للتطبيق لحماية خصوصيتك"
    override val appLockEnterPinTitle = "خزنة بريزم المحمية"
    override val appLockEnterPinSubtitle = "أدخل الرمز السري لفتح مكتبة الوسائط الخاصة بك"
    override val appLockWrongPin: (Int) -> String = { n -> "رمز غير صحيح (تم تسجيل $n محاولة فاشلة)" }
    override val appLockCooldownMsg: (Int) -> String = { sec -> "محاولات خاطئة متكررة! تم القفل لمدة $sec ثانية" }
    override val appLockUnlockAction = "فتح"
    override val appLockSetupTitle = "إعداد قفل الرمز السري"
    override val appLockSetupDesc = "اختر رمزًا من 4 إلى 6 أرقام. يُحفظ بتشفير PBKDF2 آمن ولا يُخزن كنص صريح أبدًا."
    override val appLockPinHint = "أدخل رمزًا من 4 إلى 6 أرقام"
    override val appLockConfirmHint = "تأكيد الرمز السري"
    override val appLockMinLengthError = "يجب أن يتكون الرمز من 4 أرقام على الأقل"
    override val appLockMismatchError = "الرمزان غير متطابقين"
    override val appLockSavedBanner = "تم تفعيل قفل التطبيق بالرمز السري بنجاح"
    override val appLockDisabledBanner = "تم إيقاف قفل الرمز السري"
    override val screenPrivacyTitle = "منع تصوير وتسجيل الشاشة"
    override val screenPrivacyDesc = "تفعيل FLAG_SECURE لحجب لقطات الشاشة وتسجيل الفيديو ومعاينة التطبيقات"
    override val intruderLogTitle = "سجل محاولات التطفل"
    override val intruderLogEmpty = "لا توجد أي محاولات فتح غير مصرح بها. تطبيقك آمن تمامًا."
    override val intruderLogButton: (Int) -> String = { n -> "سجل المتطفلين ($n)" }
    override val clearLogAction = "مسح السجل"
    override val sendReportShort = "إرسال تنبيه"
    override val rootCheckLabel = "فحص الروت وصلاحيات SU"
    override val proxyCheckLabel = "حماية الشبكة ضد التنصت والـ Proxy"
    override val statusClean = "سليم وآمن"
    override val statusAlert = "تنبيه رصد خطر"
    override val firstLaunchTitle = "اختر مجلدات الوسائط الخاصة بك"
    override val firstLaunchSubtitle = "حدد مجلداتك مرة واحدة ليقوم بريزم باستكشاف الموسيقى والفيديو والصور المدعومة تلقائيًا وبسرعة فائقة عند كل دخول — وتُحفظ اختياراتك حتى تغيّرها."
    override val firstLaunchAutoScanHint = "مزامنة المجلدات المحفوظة تلقائيًا عند الفتح (بخصوصية تامة)"
    override val addWatchFolderAction = "إضافة مجلد من الجهاز"
    override val startExploringAction = "حفظ وبدء الاستكشاف"
    override val skipForNowAction = "تخطي الآن"
    override val selectedFoldersLabel: (Int) -> String = { n -> "المجلدات المحفوظة للمراقبة ($n)" }
    override val noFoldersPickedYet = "اضغط «إضافة مجلد من الجهاز» لاختيار مجلد الموسيقى أو التنزيلات أو الفيديو أو الصور."
    override val watchedFoldersTitle = "المجلدات المراقبة والفحص التلقائي"
    override val watchedFoldersDesc = "إدارة المجلدات المحفوظة التي تُفحص تلقائيًا عند الدخول"
    override val manageFoldersAction = "إدارة المجلدات المحفوظة"
    override val autoScanStartupTitle = "فحص المجلدات تلقائيًا عند الفتح"
    override val autoScanStartupDesc = "عرض المكتبة فورًا من الكاش وتحديث الملفات الجديدة في الخلفية بسرعة فائقة"
    override val rescanAllAction = "إعادة فحص سريع للكل"
    override val folderRemovedBanner = "تمت إزالة المجلد من القائمة المحفوظة"
    override val folderAddedBanner: (String, Int) -> String = { name, count -> "تم حفظ مجلد «$name» • $count ملف جاهز" }
    override val scanningLiveCount: (Int) -> String = { n -> "جاري الفحص • $n ملف" }
    override val allFoldersChip = "كل المجلدات"
    override val recentlyPlayedTitle = "شُغّلت مؤخرًا"
    override val playsCountShort: (Int) -> String = { n -> "$n×" }
    override val storageInsightsTitle = "تحليل التخزين وكشف المكررات"
    override val storageInsightsDesc = "إحصائيات المساحة لكل نوع وسائط مع تنظيف الملفات المكررة بضغطة واحدة"
    override val totalLibrarySizeLabel = "إجمالي حجم المكتبة"
    override val duplicatesFoundTitle: (Int) -> String = { n -> "مجموعات ملفات مكررة ($n)" }
    override val noDuplicatesFound = "مكتبتك نظيفة — لم يتم العثور على ملفات مكررة."
    override val cleanAllDuplicatesAction = "تنظيف كل المكررات"
    override val cleanDuplicateAction = "تنظيف"
    override val duplicatesCleanedBanner: (Int) -> String = { n -> "تم تنظيف $n ملف مكرر من المكتبة" }
    override val duplicateCopiesBadge: (Int) -> String = { n -> "$n نسخ" }
    override val editTagsAction = "تعديل الوسوم"
    override val editTagsTitle = "تعديل معلومات الأغنية"
    override val tagTitleLabel = "عنوان المقطع"
    override val tagArtistLabel = "اسم الفنان"
    override val tagAlbumLabel = "الألبوم"
    override val tagsSavedBanner = "تم حفظ معلومات المقطع بنجاح"
    override val bookmarksTitle = "العلامات المرجعية"
    override val addBookmarkAction = "حفظ الموضع الحالي"
    override val bookmarkLabelHint = "ملاحظة للعلامة (اختياري)"
    override val noBookmarksYet = "احفظ مواضع زمنية للعودة إليها فورًا في المقاطع الطويلة والبودكاست."
    override val bookmarkSavedBanner = "تم حفظ العلامة المرجعية"
    override val folderItemsSummary: (Int, Int, Int) -> String = { a, v, p -> "$a صوت • $v فيديو • $p صورة" }
    override val liquidGlassTitle = "الزجاج السائل بهوية Material You"
    override val liquidGlassSubtitle = "زجاج Material 3 Expressive لوني بحواف ضوئية وخلفية ديناميكية (بصمة جوجل)"
    override val liquidGlassStyleName: (LiquidGlassStyle) -> String = {
        when (it) {
            LiquidGlassStyle.MATERIAL_LIQUID -> "زجاج ماتيريال سائل"
            LiquidGlassStyle.CRYSTAL_TONAL -> "كريستال لوني"
            LiquidGlassStyle.VELVET_FROST -> "مخملي ناعم"
            LiquidGlassStyle.SOLID_M3 -> "صلب كلاسيكي"
        }
    }
    override val liquidGlassIntensityTitle = "قوة وشفافية الزجاج السائل"
    override val shapeRoundnessTitle = "استدارة الزوايا (M3 Shapes)"
    override val contrastLevelTitle = "تباين ألوان Material You"
    override val contrastLevelName: (MaterialContrastLevel) -> String = {
        when (it) {
            MaterialContrastLevel.STANDARD -> "قياسي"
            MaterialContrastLevel.MEDIUM -> "متوسط"
            MaterialContrastLevel.HIGH -> "عالي الوضوح"
        }
    }
    override val fontStyleTitle = "خطوط Material Expressive"
    override val fontStyleName: (MaterialFontStyle) -> String = {
        when (it) {
            MaterialFontStyle.MATERIAL_EXPRESSIVE -> "ماتيريال معبّر"
            MaterialFontStyle.GOOGLE_CLEAN -> "خط نظيف حديث"
            MaterialFontStyle.EDITORIAL_STUDIO -> "خط الاستوديو"
        }
    }
    override val navTransitionTitle = "رسوم وحركات التنقل بين الشاشات"
    override val navTransitionName: (NavTransitionStyle) -> String = {
        when (it) {
            NavTransitionStyle.EXPRESSIVE_SPRING -> "نابض معبّر"
            NavTransitionStyle.PARALLAX_DEPTH -> "عمق ثلاثي الأبعاد"
            NavTransitionStyle.LIQUID_MORPH -> "تحوّل سائل"
            NavTransitionStyle.SHARED_AXIS -> "محور جوجل المشترك"
        }
    }
    override val edgeGlowTitle = "توهج الحواف المتفاعل مع الصوت"
    override val edgeGlowDesc = "إضاءة حواف ديناميكية بألوان Material You تنبض مع إيقاع الموسيقى"
    override val preampBoostTitle = "مضخّم الصوت الاحترافي (200%)"
    override val preampBoostDesc = "تضخيم حقيقي للصوت حتى 200% مع حماية ذكية ضد التشويش"
    override val audioPitchTitle = "حدة ونغمة الصوت (Pitch)"
    override val audioPitchDesc = "تعديل طبقة الصوت الموسيقية بشكل مستقل عن السرعة"
    override val crossfadeTitle = "انتقال ناعم بين الأغاني (Crossfade)"
    override val crossfadeDesc = "تدرج سلس للصوت عند الانتقال من أغنية لأخرى بدون انقطاع مفاجئ"
    override val crossfadeOffLabel = "إيقاف"
    override val syncedLyricsTitle = "كلمات متزامنة حية (Karaoke)"
    override val syncedLyricsTapHint = "اضغط على أي سطر للقفز فورًا إلى لحظته في الأغنية"
    override val smartMixName: (SmartMusicMix) -> String = {
        when (it) {
            SmartMusicMix.ALL_TRACKS -> "كل المكتبة"
            SmartMusicMix.FAVORITES_MIX -> "مزيج المفضلة"
            SmartMusicMix.MOST_PLAYED_MIX -> "الأكثر استماعًا"
            SmartMusicMix.QUICK_DISCOVERY -> "مزيج ذكي"
            SmartMusicMix.RECENTLY_ADDED -> smartMixRecentlyAdded
            SmartMusicMix.LONG_LECTURES -> smartMixLongLectures
            SmartMusicMix.HI_RES_LOSSLESS -> smartMixHiRes
            SmartMusicMix.QURAN_RECITATIONS -> smartMixQuran
        }
    }
    override val playVideoAsAudioAction = "تشغيل كصوت فقط (بالخلفية)"
    override val playingVideoAsAudioBanner = "جاري تشغيل صوت الفيديو في محرك الموسيقى"
    override val driversCardTitle = "مشغّلات ودرايفرات الصوت والصورة"
    override val driversAutoBadge = "مضبوط تلقائيًا لجهازك"
    override val driversAudioRow = "درايفر الصوت"
    override val driversVideoRow = "معالج الفيديو"
    override val driversDisplayRow = "جودة الشاشة والألوان"
    override val driversHwVideoToggleTitle = "تسريع الفيديو عبر عتاد الجهاز (HW)"
    override val driversHwVideoToggleDesc = "تشغيل فيديوهات 4K وHDR بكامل جودتها عبر معالج الجهاز مع بديل تلقائي"
    override val driversRescanAction = "إعادة فحص درايفرات الجهاز"
    override val driversRescannedBanner = "تم فحص وضبط درايفرات الصوت والصورة تلقائيًا حسب جهازك"
    override val photoNativeQualityBadge = "الدقة الأصلية الكاملة"
    override val builtInProtectionTitle = "حماية وخصوصية مدمجة وتعمل تلقائيًا"
    override val builtInProtectionBody = "التطبيق محمي تلقائيًا من الداخل بفحص سلامة مستمر، ووصول خاص وآمن لملفاتك بدون أذونات تخزين شاملة، واتصالات مشفّرة بالكامل."
    override val builtInPillIntegrity = "نواة محمية"
    override val builtInPillPrivacy = "خصوصية الملفات"
    override val builtInPillEncrypted = "اتصال مشفّر"
    override val biometricUnlockTitle = "فتح القفل بالبصمة أو الوجه"
    override val biometricUnlockDesc = "فتح سريع وآمن عبر البصمة أو التعرف على الوجه إلى جانب رمز PIN"
    override val biometricPromptTitle = "فتح Prism Media"
    override val biometricPromptSubtitle = "تحقق عبر البصمة أو الوجه للمتابعة"
    override val biometricUsePinFallback = "استخدام رمز PIN"
    override val videoSubtitleSettingsTitle = "استوديو الترجمة والمزامنة"
    override val videoSubtitleLoadFile = "استيراد ملف ترجمة (.SRT / .VTT)"
    override val videoSubtitleSyncLabel = "ضبط توقيت الترجمة (Sync)"
    override val videoSubtitleSizeLabel = "حجم خط الترجمة"
    override val videoAudioTrackLabel = "الصوت"
    override val videoAudioTrackTitle = "المسارات الصوتية"
    override val videoNoExtraAudioTracks = "يحتوي الفيديو على مسار صوتي افتراضي واحد"
    override val videoSnapshotAction = "لقطة"
    override val videoSnapshotSaved = "تم حفظ لقطة الفيديو في مجلد Pictures/Prism"
    override val videoSnapshotFailed = "تعذّر التقاط صورة لهذا الفيديو"
    override val photoExifTitle = "تفاصيل الكاميرا وEXIF"
    override val photoExifCamera = "الكاميرا"
    override val photoExifDate = "تاريخ الالتقاط"
    override val photoExifDims = "الدقة"
    override val photoExifSettings = "إعدادات العدسة"
    override val photoExifNoData = "لا توجد بيانات EXIF مدمجة في هذه الصورة"
    override val photoSaveCopyAction = "حفظ نسخة معدّلة"
    override val photoSavedCopyBanner = "تم حفظ النسخة المعدّلة في Pictures/Prism"
    override val photoSaveFailedBanner = "تعذّر حفظ النسخة المعدّلة"
    override val backupSectionTitle = "النسخ الاحتياطي والاستعادة المحلية"
    override val backupSectionDesc = "تصدير أو استعادة القوائم والمجلدات والكلمات والإشارات والإعدادات"
    override val backupExportAction = "تصدير نسخة احتياطية"
    override val backupImportAction = "استعادة نسخة"
    override val backupExportSuccess = "تم تصدير النسخة الاحتياطية الموثّقة بنجاح"
    override val backupImportSuccess = "تم التحقق من النسخة الاحتياطية واستعادتها بنجاح"
    override val backupFailedBanner = "تعذّر التحقق من ملف النسخة الاحتياطية أو معالجته"
    override val openedExternalMediaBanner: (String) -> String = { "تم فتح $it داخل Prism" }
    override val settingsSearchHint = "ابحث في أقسام الإعدادات..."
    override val visualEngineTitle = "نمط الهوية البصرية"
    override val visualEngineSubtitle = "اختر بحرية بين ماتيريال يو الحقيقي أو الزجاج السائل"
    override val visualEngineMaterialYouTitle = "ماتيريال يو الحقيقي"
    override val visualEngineMaterialYouDesc = "أسطح M3 نقية ومتدرجة باللون بدون شفافية زجاجية"
    override val visualEngineLiquidGlassTitle = "الزجاج السائل"
    override val visualEngineLiquidGlassDesc = "زجاج ماتيريال شفاف مع انعكاس لوني وخلفية حية"
    override val liveThemePreviewLabel = "معاينة فورية للمظهر"
    override val settingsNavLayoutSection = "التنقل وتخطيط الواجهة"
    override val navBarStyleTitle = "شكل شريط التنقل السفلي"
    override val navBarStyleName: (NavBarStyle) -> String = {
        when (it) {
            NavBarStyle.FLOATING_PILL -> "كبسولة عائمة"
            NavBarStyle.DOCKED_MATERIAL_YOU -> "شريط ماتيريال يو"
            NavBarStyle.COMPACT_DOCK -> "شريط مدمج"
        }
    }
    override val startupTabTitle = "الصفحة الافتراضية عند الفتح"
    override val uiDensityTitle = "كثافة وتباعد الواجهة"
    override val uiDensityName: (UiDensity) -> String = {
        when (it) {
            UiDensity.COMFORTABLE -> "مريح وواسع"
            UiDensity.BALANCED -> "متوازن"
            UiDensity.COMPACT -> "مكثّف"
        }
    }
    override val hapticsTitle = "الاهتزاز اللمسي التفاعلي"
    override val hapticsDesc = "استجابة لمسية ناعمة عند لمس الأزرار وأشرطة التمرير"
    override val playerArtworkStyleTitle = "شكل غلاف الأغنية بالمشغّل"
    override val playerArtworkStyleName: (PlayerArtworkStyle) -> String = {
        when (it) {
            PlayerArtworkStyle.SCALLOPED_COOKIE -> "متموّج إكسبريسيف"
            PlayerArtworkStyle.SQUIRCLE_EXPRESSIVE -> "مربع ناعم"
            PlayerArtworkStyle.VINYL_DISC -> "أسطوانة دوّارة"
            PlayerArtworkStyle.SOFT_PILL_ARCH -> "قوس معماري"
            PlayerArtworkStyle.CLOVER_FOUR -> "رباعي البتلات"
            PlayerArtworkStyle.CIRCLE_GLOW -> "دائرة بهالة"
            PlayerArtworkStyle.ROUNDED_CARD -> "بطاقة غلاف"
        }
    }
    override val albumArtTintTitle = "ألوان ديناميكية من الغلاف"
    override val albumArtTintDesc = "تلوين خلفية المشغّل تلقائيًا حسب ألوان غلاف كل أغنية"
    override val fullPlayerSpectrumTitle = "طيف الصوت الحي بالمشغّل"
    override val fullPlayerSpectrumDesc = "إظهار أشرطة الترددات الحية داخل المشغّل الكامل"
    override val seekStepTitle = "مدة القفز للتقديم والتأخير"
    override val seekStepDesc = "ثواني القفز السريع في مشغّل الموسيقى ومشغّل الفيديو"
    override val resetCustomizationAction = "إعادة ضبط التخصيص للوضع الافتراضي"
    override val resetCustomizationBanner = "تمت إعادة إعدادات المظهر والتخطيط للوضع الافتراضي"
    override val settingsHardwareSubLabel = "الشاشة ودرايفرات العتاد"
    override val tabDisplayName: (MediaTab) -> String = {
        when (it) {
            MediaTab.MUSIC -> tabMusic
            MediaTab.VIDEOS -> tabVideos
            MediaTab.PHOTOS -> tabPhotos
            MediaTab.SETTINGS -> tabSettings
        }
    }
    override val onlineEnrichmentTitle = "البحث الذكي والموسوعة الحية"
    override val onlineEnrichmentDesc = "جلب صور الفنانين، النبذ، الآيات القرآنية، الأحاديث، الكلمات ومواقع الصور تلقائيًا"
    override val autoFetchLyricsTitle = "جلب كلمات الأغاني المتزامنة تلقائيًا"
    override val autoFetchLyricsDesc = "البحث الفوري عن كلمات LRC المتزامنة مع الوقت عند تشغيل أي أغنية"
    override val autoFetchArtistPhotoTitle = "صور الفنانين الحقيقية"
    override val autoFetchArtistPhotoDesc = "عرض صورة المغني الحقيقية ومعلوماته الموسوعية داخل بطاقة الفنان"
    override val clearKnowledgeCacheAction = "مسح ذاكرة البحث الموسوعي"
    override val knowledgeCacheClearedBanner = "تم مسح ذاكرة معلومات البحث بنجاح"
    override val fetchingKnowledgeLabel = "جاري البحث في الموسوعة والمصادر الحية..."
    override val refreshKnowledgeAction = "تحديث المعلومات"
    override val fetchLyricsOnlineAction = "جلب الكلمات"
    override val openEncyclopediaAction = "قراءة المقال الكامل"
    override val trackEnergyLabel = "طاقة الأغنية"
    override val trackBpmLabel = "الإيقاع BPM"
    override val trackMoodLabel = "المزاج"
    override val artistBioTitle = "بطاقة الفنان والموسوعة"
    override val quranSurahBadge = "القرآن الكريم"
    override val quranAyahsCountLabel: (Int) -> String = { "$it آية" }
    override val quranVersesPreviewTitle = "آيات السورة بالرسم العثماني"
    override val quranTranslationTitle = "المعنى والترجمة"
    override val hadithReferenceTitle = "مرجع الحديث النبوي الشريف"
    override val encyclopediaSectionTitle = "الموسوعة والمعلومات الحية"
    override val photoLocationLabel = "موقع التقاط الصورة"
    override val knowledgeSourcePrefix = "المصادر:"
    override val noOnlineInfoFound = "اتصل بالإنترنت أو اضغط «تحديث المعلومات» لاستكشاف تفاصيل هذا الملف."
    override val fetchCoverAndLyricsAction = "جلب الغلاف والكلمات"
    override val batchEnrichLibraryAction = "جلب الأغلفة والكلمات للمكتبة تلقائيًا"
    override val batchEnrichProgressLabel: (Int, Int) -> String = { done, total -> "جاري جلب البيانات $done / $total..." }
    override val batchEnrichCompletedBanner: (Int, Int) -> String = { covers, lyrics ->
        "تم تحديث $covers غلاف عالي الدقة و $lyrics كلمات متزامنة"
    }
    override val coverAndLyricsUpdatedBanner = "تم تحديث الغلاف والكلمات بنجاح"
    override val gaplessPlaybackTitle = "تشغيل متصل بدون فواصل (Gapless)"
    override val gaplessPlaybackDesc = "إزالة لحظات الصمت بين المقاطع المتتالية في الألبومات والقرآن"
    override val audioNormalizationTitle = "موازنة ارتفاع الصوت تلقائيًا"
    override val audioNormalizationDesc = "توحيد مستوى الصوت بين المقاطع الهادئة والمرتفعة بسلاسة"
    override val headsetUnplugPauseTitle = "إيقاف تلقائي عند فصل السماعات"
    override val headsetUnplugPauseDesc = "إيقاف التشغيل فور فصل السماعة السلكية أو البلوتوث"
    override val videoGesturesTitle = "إيماءات السحب (السطوع والصوت)"
    override val videoGesturesDesc = "السحب عموديًا على جانبي الفيديو لضبط السطوع ومستوى الصوت"
    override val videoDoubleTapTitle = "النقر المزدوج للتقديم والتأخير"
    override val videoDoubleTapDesc = "انقر مرتين يمينًا أو يسارًا للقفز السريع داخل الفيديو"
    override val videoRememberPosTitle = "تذكّر موضع توقف الفيديو"
    override val videoRememberPosDesc = "استئناف مشاهدة الفيديو من نفس اللحظة التي توقفت عندها"
    override val videoSubtitleSizeTitle = "حجم خط الترجمة الافتراضي"
    override val photoAutoExifTitle = "عرض تفاصيل الكاميرا والموقع تلقائيًا"
    override val photoAutoExifDesc = "قراءة بيانات EXIF وتحديد المدينة والدولة من إحداثيات GPS"
    override val photoHighResTitle = "عرض الصور بأعلى دقة كاملة"
    override val photoHighResDesc = "فك تشفير الصورة بكامل حدّتها الأصلية عند التكبير"
    override val photoSlideshowTransitionTitle = "حركة انتقال العرض التلقائي"
    override val slideshowTransitionName: (SlideshowTransitionStyle) -> String = {
        when (it) {
            SlideshowTransitionStyle.FADE -> "تلاشي ناعم"
            SlideshowTransitionStyle.ZOOM -> "تقريب سينمائي"
            SlideshowTransitionStyle.SLIDE -> "انزلاق ديناميكي"
        }
    }
    override val surfaceDepthTitle = "عمق وبروز البطاقات والأسطح"
    override val surfaceDepthName: (SurfaceDepthStyle) -> String = {
        when (it) {
            SurfaceDepthStyle.FLAT -> "مسطح بسيط"
            SurfaceDepthStyle.ELEVATED -> "عمق متوازن"
            SurfaceDepthStyle.EXPRESSIVE -> "بروز M3 معبّر"
        }
    }
    override val languageDisplayName: (AppLanguage) -> String = {
        when (it) {
            AppLanguage.EN -> "English (LTR)"
            AppLanguage.AR -> "العربية (RTL)"
            AppLanguage.FR -> "Français (LTR)"
            AppLanguage.ES -> "Español (LTR)"
        }
    }
    override val contentBlockedBanner = "تم الرفض: يرفض تطبيق Prism بشكل كامل أي محتوى إباحي أو خادش للحياء."
    override val lyricsBlockedBanner = "تم حجب الكلمات المخلة بالأدب تلقائياً بواسطة درع النقاء."
    override val taqwaSectionTitle = "الدعاء والتذكير بتقوى الله"
    override val taqwaSectionSubtitle = "دعاء خالص • تحذير لمن لا يتقي الله • درع النقاء الدائم"
    override val taqwaDuaHeading = "دعاء خالص لله تعالى"
    override val taqwaDuaBody = "اللهم إنا نسألك الهدى والتقى والعفاف والغنى، اللهم احفظ أسماعنا وأبصارنا وقلوبنا من كل ما يغضبك، وبارك في هذا التطبيق واجعله شاهداً لنا لا علينا في الخير والعلم النافع وتلاوة كتابك، وجنبنا الفواحش ما ظهر منها وما بطن، واغفر لنا ولوالدينا ولجميع المسلمين يا أرحم الراحمين. آمين."
    override val taqwaWarningHeading = "تحذير وتذكير لمن لا يتقي الله"
    override val taqwaWarningVerse = "﴿ إِنَّ السَّمْعَ وَالْبَصَرَ وَالْفُؤَادَ كُلُّ أُولَٰئِكَ كَانَ عَنْهُ مَسْئُولًا ﴾ [الإسراء: 36]"
    override val taqwaWarningBody = "اتقِ الله في السر والعلن، واعلم أن الله يراك ويسمعك ويعلم خائنة الأعين وما تخفي الصدور. لا تجعل هاتفك شاهداً عليك يوم القيامة بالمحتوى الإباحي أو الكلمات الفاحشة، بل اجعله وسيلة للخير والطاعة وتلاوة القرآن والعلم النافع. ومطوّر هذا التطبيق يبرأ إلى الله من كل استعمال محرم أو يغضب الله عز وجل."
    override val taqwaPurityShieldTitle = "درع النقاء المدمج مفعّل دائماً"
    override val taqwaPurityShieldBody = "يتم رفض المحتوى الإباحي وحجب الكلمات المخلة بالأدب تلقائياً من داخل نواة التطبيق دون إمكانية تعطيله."
    override val taqwaPurityBadgeExplicit = "رفض الإباحية"
    override val taqwaPurityBadgeLyrics = "كلمات نقية"
    override val quranHubTitle = "المصحف الصوتي والختمة"
    override val quranHubSubtitle = "تتبع الـ 114 سورة، تكرار الحفظ، والاستماع الخاشع"
    override val quranKhatmaProgressLabel = "تقدّم الختمة"
    override val quranHifzRepeatLabel = "تكرار الحفظ والمراجعة"
    override val quranMarkSurahDone = "تمّت القراءة"
    override val khushuModeTitle = "الوضع الخاشع (بدون مشتتات)"
    override val khushuModeDesc = "واجهة هادئة خالية من المؤثرات لسماع القرآن والدروس"
    override val tasbihTitle = "المسبحة والأذكار اليومية"
    override val tasbihTodayLabel = "أذكار اليوم"
    override val tasbihReset = "تصفير"
    override val morningAlarmTitle = "منبّه الصباح بالقرآن"
    override val morningAlarmSubtitle = "استيقظ بهدوء على تلاوة أو نشيد بتدرّج صوتي لطيف"
    override val morningAlarmFadeLabel = "تدرّج الصوت"
    override val morningAlarmTestNow = "تجربة التدرّج الآن"
    override val audioCutterTitle = "مقصّ الصوت وصانع النغمات"
    override val audioCutterSubtitle = "قصّ أي مقطع بدقة وحفظه كنغمة رنين"
    override val audioCutterExportBtn = "قصّ وحفظ كنغمة"
    override val audioCutterSavedToast = "تم حفظ المقطع في مجلد النغمات Ringtones/Prism"
    override val listeningStatsTitle = "إحصائيات الاستماع والملخّص"
    override val listeningStatsSubtitle = "دقائق الاستماع، أكثر المقرئين، الصيغ والنشاط اليومي"
    override val totalPlaysLabel = "مرات التشغيل"
    override val totalMinutesLabel = "دقيقة استماع"
    override val topTracksLabel = "المقاطع الأكثر استماعاً"
    override val topArtistsLabel = "أكثر القراء والأصوات"
    override val formatBreakdownLabel = "توزيع صيغ الصوت"
    override val lectureModeTitle = "وضع المحاضرات والبودكاست"
    override val lectureModeDesc = "حفظ موضع التوقف تلقائياً وسرعات مناسبة للدروس"
    override val skipSilenceTitle = "تخطي الصمت التلقائي"
    override val skipSilenceDesc = "اختصار لحظات السكوت الطويلة أثناء المحاضرات"
    override val stereoBalanceTitle = "توازن السماعتين (يمين / يسار)"
    override val stereoBalanceDesc = "ضبط اتجاه الصوت بين الأذن اليسرى واليمنى"
    override val audioMonoTitle = "الصوت الأحادي (Mono)"
    override val audioMonoDesc = "دمج القناتين بالتساوي لمن يستعمل سماعة واحدة"
    override val shakeToShuffleTitle = "هزّ الهاتف للتالي"
    override val shakeToShuffleDesc = "هزّة خفيفة للانتقال للمقطع النظيف التالي"
    override val batchTagTitle = "تعديل جماعي للوسوم والغلاف"
    override val batchTagSubtitle = "تحديث اسم القارئ والألبوم والغلاف لجميع المحدد"
    override val batchTagApply = "تطبيق على المحدد"
    override val privateVaultTitle = "الخزنة الخاصة المشفّرة"
    override val privateVaultSubtitle = "مجلد محمي بتشفير AES-256 لصورك وفيديوهاتك العائلية"
    override val privateVaultEmpty = "خزنتك المشفّرة فارغة. أضف صورك أو فيديوهاتك الخاصة بأمان."
    override val privateVaultAddBtn = "قفل ملف داخل الخزنة"
    override val privateVaultRestoreBtn = "استعادة إلى المعرض"
    override val privateVaultLockedHint = "حماية بتشفير AES-256-GCM داخل الهاتف"
    override val stripExifShareTitle = "مسح بيانات الخصوصية قبل المشاركة"
    override val stripExifShareDesc = "إزالة الموقع الجغرافي ومعلومات الكاميرا تلقائياً عند مشاركة الصور"
    override val collageStudioTitle = "صانع المجمّعات والتوقيع المائي"
    override val collageStudioSubtitle = "دمج 2 إلى 4 صور مع توقيعك الخاص بدقة عالية"
    override val collageWatermarkHint = "نص التوقيع المائي (اختياري)"
    override val collageSaveBtn = "حفظ المجمّع بدقة عالية"
    override val customSeedTitle = "استخراج ألوان Material You الحرة"
    override val customSeedSubtitle = "اختر أي لون أو استخرج لوحة التطبيق من صورة في هاتفك"
    override val customSeedFromPhoto = "استخراج من صورة"
    override val customSeedClear = "العودة للألوان الجاهزة"
    override val appIconSwitcherTitle = "تبديل أيقونة التطبيق"
    override val appIconSwitcherSubtitle = "اختر شكل أيقونة Prism على الشاشة الرئيسية"
    override val themeCodeTitle = "رمز الثيم القابل للمشاركة"
    override val themeCodeSubtitle = "انسخ أو استورد رمز تخصيصك الكامل بضغطة واحدة"
    override val themeCodeCopy = "نسخ رمز الثيم"
    override val themeCodeApply = "استيراد رمز ثيم"
    override val wifiTransferTitle = "النقل المحلي عبر Wi-Fi"
    override val wifiTransferSubtitle = "إرسال واستقبال الملفات مع الحاسوب بدون إنترنت أو كابل"
    override val wifiTransferStart = "تشغيل خادم Wi-Fi"
    override val wifiTransferStop = "إيقاف الخادم"
    override val videoColorCalibTitle = "معايرة ألوان الفيديو"
    override val videoChaptersTitle = "الفصول والعلامات المرجعية"
    override val videoAddChapterBtn = "حفظ اللحظة الحالية"
    override val subtitleColorLabel = "لون الترجمة"
    override val subtitleBgOpacityLabel = "خلفية الترجمة"
    override val subtitlePositionLabel = "ارتفاع الترجمة"
    override val folderPinLabel = "تثبيت بالأعلى"
    override val folderUnpinLabel = "إلغاء التثبيت"
    override val folderCustomizeTitle = "تخصيص المجلد"
    override val smartMixRecentlyAdded = "المضاف حديثاً"
    override val smartMixLongLectures = "المحاضرات الطويلة"
    override val smartMixHiRes = "جودة فائقة Hi-Res"
    override val smartMixQuran = "القرآن والتلاوات"
    override val proToolsHubTitle = "مركز أدوات Prism والروحانيات"
    override val proToolsHubSubtitle = "المصحف والختمة • الخزنة • مقصّ الصوت • الإحصائيات • Wi-Fi • المجمّعات"
    override val settingsLayoutModeTitle = "طريقة عرض القوائم عند الفتح"
    override val settingsLayoutHorizontal = "تمرير يمين/يسار ⇄"
    override val settingsLayoutVertical = "قائمة عمودية ↕"
    override val collapseSectionAction = "طيّ القائمة"
    override val collapseAllSectionsAction = "إغلاق القوائم"
    override val swipeHintLabel = "اسحب يميناً ويساراً ⇄"
    override val tuckToEdgeAction = "إخفاء لجانب الشاشة"
    override val restoreFromEdgeAction = "إرجاع المشغّل"
    override val edgeDockSideTitle = "جهة التصاق المشغّل المصغّر"
    override val edgeDockRight = "الجانب الأيمن"
    override val edgeDockLeft = "الجانب الأيسر"
    override val floatPhotoAction = "تصغير الصورة كنافذة عائمة"
    override val floatingPhotoBadge = "صورة عائمة"
    override val widgetsSixGalleryTitle = "6 ودجتس للشاشة الرئيسية"
    override val widgetsSixGallerySubtitle = "اسحب يميناً ويساراً لمعاينة وتثبيت أي من الودجتس الـ 6 مباشرة"
    override val widget3Title = "قرص فينيل مربع (2×2)"
    override val widget3Desc = "بطاقة مربعة أنيقة بالغلاف والعنوان وزر التشغيل"
    override val widget4Title = "ختمة القرآن والتسبيح (4×2)"
    override val widget4Desc = "تقدّم الختمة، عدّاد التسبيح اليومي ودخول فوري للمصحف"
    override val widget5Title = "منصّة الصوت الاحترافية (4×2)"
    override val widget5Desc = "المقطع الحالي + وضع المعادل والسرعة واختصار مباشر للمعادل"
    override val widget6Title = "شريط الاستوديو السريع (4×1)"
    override val widget6Desc = "أزرار سريعة للموسيقى والفيديو والصور والقرآن والتشغيل"
    override val proAudioDeckTitle = "استوديو الصوت والمعالجة الحية"
    override val proAudioDeckSubtitle = "التضخيم • النبرة • توازن الصوت • وضوح الكلام • تكرار الحفظ"
    override val driveModeTitle = "وضع القيادة (أزرار كبيرة)"
    override val driveModeDesc = "أزرار ضخمة وواضحة للتحكم السهل والسريع بيد واحدة"
    override val muteAudioAction = "كتم الصوت"
    override val unmuteAudioAction = "إلغاء الكتم"
    override val spectrumStyleTitle = "نمط الموجات الصوتية الحية"
    override val spectrumClassicBars = "أعمدة كلاسيكية"
    override val spectrumWavePulse = "نبض موجي"
    override val spectrumMinimalDots = "نقاط هادئة"
    override val swipeArtworkTitle = "السحب على الغلاف لتغيير المقطع"
    override val swipeArtworkDesc = "اسحب يميناً أو يساراً على صورة الألبوم للتنقل بسرعة"
    override val lyricsSizeLabel = "حجم خط الكلمات"
    override val hifzRemainingLabel: (Int) -> String = { "باقي $it تكرار" }
    override val hifzLoopTitle = "تكرار الحفظ (A-B)"
    override val dspPageSound = "محرك الصوت"
    override val dspPageClarity = "الوضوح والسمع"
    override val dspPageHifz = "الحفظ والأدوات"
    override val videoMirrorTitle = "عكس الفيديو أفقياً (مرآة)"
    override val videoMirrorDesc = "قلب صورة الفيديو أفقياً للدروس والشروحات"
    override val videoAutoHideTitle = "إخفاء أزرار الفيديو تلقائياً"
    override val videoAutoHideNever = "أبداً"
    override val photoFlipAction = "عكس"
    override val photoBrightBoostAction = "سطوع كامل"
    override val quickPresetsTitle = "قوالب جمالية بضغطة واحدة"
    override val presetMaterialYou = "ماتيريال يو نقي"
    override val presetLiquidCrystal = "زجاج سائل"
    override val presetAmoledMidnight = "ليلي أسود نقي"
    override val presetEmeraldMosque = "زمردي هادئ"
    override val presetAppliedBanner = "تم تطبيق القالب الجمالي بنجاح"
    override val globalSearchTitle = "البحث الشامل"
    override val globalSearchHint = "ابحث في الموسيقى، الفيديو، الصور، السور، الملاحظات..."
    override val globalSearchNoResults = "لا توجد نتائج مطابقة في المكتبة"
    override val recycleBinTitle = "سلة المحذوفات الذكية"
    override val recycleBinSubtitle = "استرجع أي ملف أُزيل من المكتبة بضغطة واحدة"
    override val recycleBinEmpty = "سلة المحذوفات فارغة ونظيفة"
    override val restoreItemAction = "استرجاع"
    override val restoreAllAction = "استرجاع الكل"
    override val emptyBinAction = "إفراغ السلة"
    override val movedToBinBanner = "تم النقل إلى سلة المحذوفات (يمكنك استرجاعه)"
    override val restoredFromBinBanner = "تم الاسترجاع إلى المكتبة بنجاح"
    override val mediaNoteTitle = "ملاحظة شخصية"
    override val mediaNoteHint = "اكتب فائدة أو ملاحظة أو تذكيراً لهذا الملف..."
    override val saveNoteAction = "حفظ الملاحظة"
    override val deleteNoteAction = "حذف الملاحظة"
    override val noteSavedBanner = "تم حفظ الملاحظة بنجاح"
    override val reverbTitle = "الصدى والمحيط ثلاثي الأبعاد"
    override val reverbNone = "إيقاف"
    override val reverbStudio = "استوديو"
    override val reverbHall = "قاعة كبرى"
    override val reverbMosque = "صدى المسجد"
    override val reverbSpatial3d = "محيطي 3D"
    override val tapSyncLyricsTitle = "مزامنة الكلمات باللمس (LRC)"
    override val tapSyncLyricsHint = "شغّل المقطع واضغط تسجيل التوقيت مع كل سطر"
    override val tapSyncStampLine = "توقيت السطر الحالي"
    override val browseTracksTab = "المقاطع"
    override val browseArtistsTab = "الفنانون"
    override val browseAlbumsTab = "الألبومات"
    override val exportM3uAction = "تصدير M3U"
    override val importM3uAction = "استيراد M3U"
    override val m3uExportedBanner = "تم تصدير قائمة التشغيل بصيغة M3U"
    override val m3uImportedBanner: (Int) -> String = { "تم استيراد القائمة ($it مقطع)" }
    override val extractAudioAction = "استخراج الصوت (M4A)"
    override val extractingAudioBanner = "جارٍ استخراج الصوت من الفيديو..."
    override val audioExtractedBanner = "تم استخراج الصوت وإضافته لمكتبة الموسيقى!"
    override val photoEditStudioTitle = "استوديو تعديل الصور"
    override val photoCropLabel = "نسبة القص"
    override val photoRotate90Action = "تدوير 90°"
    override val photoBrightnessLabel = "الإضاءة"
    override val photoContrastLabel = "التباين"
    override val photoSaturationLabel = "التشبع اللوني"
    override val photoSetWallpaperAction = "تعيين كخلفية"
    override val photoWallpaperSuccess = "جارٍ فتح تعيين الخلفية"
    override val photoExportPdfAction = "تحويل إلى PDF"
    override val photoPdfReadyBanner = "ملف PDF عالي الدقة جاهز للمشاركة"
    override val photoCompressAction = "ضغط الصورة"
    override val photoCompressedBanner: (String, String) -> String = { before, after -> "تم الضغط من $before إلى $after" }
    override val photoCompareTitle = "مقارنة الصور بالسحب"
    override val photoCompareBefore = "الأصلية"
    override val photoCompareAfter = "المقارنة"
    override val videoZoomBadge: (Float) -> String = { String.format(java.util.Locale.US, "تكبير %.1fx", it) }
    override val videoFrameBack = "إطار -"
    override val videoFrameForward = "إطار +"
    override val videoSleepTimerTitle = "مؤقت نوم الفيديو"
    override val videoAutoNextTitle = "التشغيل التلقائي للفيديو التالي"
    override val videoAutoNextDesc = "الانتقال تلقائياً للفيديو الموالي عند انتهاء العرض"
    override val adhkarTitle = "أذكار الصباح والمساء"
    override val adhkarMorningTab = "الصباح"
    override val adhkarEveningTab = "المساء"
    override val adhkarCompletedBadge = "مكتمل"
    override val adhkarResetAction = "إعادة العداد"
    override val audioProfilesTitle = "بروفايلات السماعات والأجهزة"
    override val audioProfilesDesc = "تبديل تلقائي للمعادل والصدى حسب السماعة أو البلوتوث"
    override val audioOutputSpeaker = "مكبر الهاتف"
    override val audioOutputWired = "سماعة سلكية"
    override val audioOutputBluetooth = "بلوتوث"
    override val audioProfileAppliedBanner: (String) -> String = { "تم تطبيق بروفايل الصوت: $it" }
    override val customTabOrderTitle = "ترتيب تبويبات الشريط السفلي"
    override val customTabOrderDesc = "رتّب التبويبات حسب راحتك"
    override val headerButtonsTitle = "أزرار الشريط العلوي"
    override val headerShowStreamTitle = "إظهار زر البث المباشر"
    override val headerShowSplitTitle = "إظهار زر تقسيم الشاشة"
    override val resizeWindowAction = "تغيير الحجم"
    override val onboardingWelcomeTitle = "مرحباً بك في بريزم للوسائط"
    override val onboardingWelcomeSubtitle = "عالم بصري هادئ وحالم يجمع الموسيقى الفائقة، السينما، استوديو الصور، وواحة القرآن والأذكار."
    override val onboardingFeaturesTitle = "استوديو متكامل يعيشك الحلم"
    override val onboardingFeaturesSubtitle = "كل الأدوات الاحترافية بتصميم ماتيريال 3 المعبّر والزجاج السائل في مكان واحد."
    override val onboardingSetupTitle = "شكّل أجواءك لأول مرة"
    override val onboardingSetupSubtitle = "فعّل الخصائص الذكية واختر مظهرك المفضل (زجاج سائل أو ماتيريال يو) بضغطة واحدة."
    override val onboardingLibraryTitle = "اربط مكتبة وسائطك"
    override val onboardingNextAction = "التالي"
    override val onboardingBackAction = "السابق"
    override val onboardingSkipAction = "تخطي"
    override val onboardingEnterPrismAction = "ادخل إلى بريزم"
    override val replayOnboardingAction = "إعادة عرض دليل الترحيب والميزات"
    override val trimVideoAction = "قص الفيديو (MP4)"
    override val videoTrimmerTitle = "قص الفيديو بدون فقدان الجودة"
    override val videoTrimStartLabel = "البداية"
    override val videoTrimEndLabel = "النهاية"
    override val videoTrimSuccessBanner = "تم حفظ مقطع الفيديو المقصوص في مكتبة الفيديو!"
    override val photoArtFilterLabel = "فلتر فني"
    override val photoWatermarkHint = "توقيع أو علامة مائية نصية (اختياري)"
    override val recycleDaysLeft: (Int) -> String = { "متبقي $it يوم" }
    override val quickTileLabel = "تشغيل بريزم"
    override val sharedToPrismBanner = "تم استقبال الوسائط المشاركة في بريزم"
    override val smartImportTitle = "استيراد المجلدات والملفات بسهولة"
    override val smartImportSubtitle = "ادخل مباشرة لمجلداتك المسموحة أو اختر الملفات فوراً"
    override val smartImportSafTip = "أندرويد يمنع اختيار المجلد الرئيسي أو Downloads كمجلد كامل؛ اختر مجلداً فرعياً (Music / Movies / Pictures) بدون الحاجة لإنشاء مجلد، أو اضغط اختيار الملفات مباشرة."
    override val smartImportQuickFoldersLabel = "مجلدات جاهزة مباشرة (بدون إنشاء مجلد جديد)"
    override val smartImportDirectFilesAction = "اختيار الملفات مباشرة (يعمل حتى في Downloads)"
    override val smartImportBrowseOther = "تصفح المجلدات"
    override val genreMoodName: (TrackGenreMood) -> String = {
        when (it) {
            TrackGenreMood.QURAN_SPIRITUAL -> "قرآن وروحانيات"
            TrackGenreMood.NASHEED_WARM -> "أناشيد ودافئ"
            TrackGenreMood.CLASSICAL_ORCHESTRAL -> "كلاسيكي وعزف"
            TrackGenreMood.ELECTRONIC_ENERGY -> "حماسي وإلكتروني"
            TrackGenreMood.LOFI_AMBIENT -> "هادئ وليلي"
            TrackGenreMood.LECTURE_PODCAST -> "محاضرات وبودكاست"
            TrackGenreMood.ACOUSTIC_POP -> "مزاج ديناميكي"
        }
    }
    override val settingsPlayerCard = "بطاقة المشغّل والتقنيات"
    override val settingsPlayerCardDesc = "أشكال ماتيريال إكسبريسيف، ألوان حسب نوع الأغنية، DSP ومعاينة حية"
    override val playerGenreColorsTitle = "ألوان ديناميكية حسب نوع الأغنية"
    override val playerGenreColorsDesc = "تتغير أجواء وألوان البطاقة تلقائياً حسب طابع التلاوة أو الأغنية"
    override val playerTechTelemetryTitle = "شارة تقنيات محرك الصوت"
    override val playerTechTelemetryDesc = "إظهار جودة الملف والتردد ومعالج 32-Bit DSP بشكل أنيق"
    override val playerExtrasSubtleTitle = "زر الإضافات الصغير وشبه المخفي"
    override val playerExtrasSubtleDesc = "إبقاء واجهة المشغّل نقية ووضع الأدوات خلف زر أنيق صغير"
    override val playerBitExactDspTitle = "وضع النقاء المباشر (Bit-Exact)"
    override val playerBitExactDspDesc = "تجاوز تأثيرات المعادل للحصول على صوت خام نقي"
    override val playerExpressivePlayMorphTitle = "تحوّل شكل زر التشغيل إكسبريسيف"
    override val playerExtrasStudioTitle = "إضافات وأدوات المشغّل"
    override val livePreviewBadge = "معاينة حية"
    override val settingsLockScreen = "شاشة القفل والهوية"
    override val settingsLockScreenDesc = "إظهار هوية بريزم والتحكم في التشغيل وتخصيص شاشة القفل"
    override val lockScreenControlsTitle = "بطاقة المشغّل على شاشة القفل"
    override val lockScreenControlsDesc = "إظهار هوية التطبيق وأزرار التحكم وشريط الوقت أثناء قفل الشاشة"
    override val lockScreenShowArtworkTitle = "إظهار الغلاف على شاشة القفل"
    override val lockScreenShowIdentityTitle = "شارة هوية Prism Media"
    override val lockScreenShowIdentityDesc = "إبراز هوية التطبيق ونوع الصوت النشط على شاشة القفل"
    override val lockScreenFullScreenTitle = "مشغّل شاشة القفل الكامل"
    override val lockScreenFullScreenDesc = "عرض واجهة بريزم الكاملة بتحكم فوري عند إيقاظ الشاشة المقفلة"
    override val lockScreenShowClockTitle = "ساعة ماتيريال إكسبريسيف"
    override val lockScreenShowLyricsTitle = "سطر الكلمات المتزامن على القفل"
    override val lockScreenBlurBackdropTitle = "خلفية شفق ضبابية حالمة"
    override val lockScreenCompactModeTitle = "بطاقة قفل مدمجة وبسيطة"
    override val lockScreenPreviewAction = "معاينة شاشة القفل الآن"
    override val lockScreenUnlockAction = "فتح تطبيق Prism Media"
    override val pipMusicTitle = "صورة داخل صورة للموسيقى (اختياري)"
    override val pipMusicDesc = "نافذة عائمة عند الخروج أثناء تشغيل الصوت (إيقافها يبقي الصوت يعمل بهدوء في الخلفية)"
    override val pipVideoTitle = "صورة داخل صورة للفيديو (اختياري)"
    override val pipVideoDesc = "تشغيل الفيديو في نافذة عائمة صغيرة عند الخروج من التطبيق"
    override val pipPhotoTitle = "صورة داخل صورة للصور (اختياري)"
    override val pipPhotoDesc = "إبقاء الصورة المفتوحة في نافذة عائمة مصغّرة عند الخروج"
    override val pipPhotoBadge = "نافذة صور عائمة"
    override val filtersAndToolsAction = "التصفية والأدوات"
    override val clearFiltersAction = "إعادة تعيين"
    override val moreHeaderActionsDesc = "خيارات إضافية"
    override val importDownloadsDirectAction = "اختيار الملفات مباشرة من Download (بلا قيود)"
    override val importDownloadsPrivacyExplainer = "نظام أندرويد يمنع منح صلاحية لمجلد Download الرئيسي لحماية الخصوصية. استعمل الزر أعلاه لاختيار ملفاتك مباشرة من Download، أو ادخل لمجلد فرعي داخله مثل Music أو Prism."
    override val quickStudioTitle = "استوديو التخصيص السريع"
    override val quickStudioSubtitle = "خصّص الألوان والبطاقات والخلفية والحركة مباشرة من أي شاشة"
    override val cardStyleTitle = "نمط بطاقات الوسائط"
    override val cardStyleGlass = "زجاج سائل"
    override val cardStyleElevated = "ماتيريال بارز"
    override val cardStyleMinimal = "إطار هادئ"
    override val cardStyleVibrant = "لوني نابض"
    override val backdropStyleTitle = "نمط الخلفية التفاعلية"
    override val backdropAurora = "هالات الشفق"
    override val backdropMist = "ضباب هادئ"
    override val backdropRibbon = "أشرطة منشورية"
    override val backdropClean = "صافية بالكامل"
    override val motionFeelTitle = "فيزياء اللمس والحركة"
    override val motionBouncy = "مرن وتفاعلي"
    override val motionSmooth = "انسيابي ناعم"
    override val motionSnappy = "سريع وفوري"
    override val motionReduced = "هادئ وبسيط"
    override val headerStyleTitle = "نمط الشريط العلوي"
    override val headerBold = "بارز ومعبّر"
    override val headerCompact = "مدمج وبسيط"
    override val headerCentered = "متوسط وأنيق"
    override val swipeGesturesTitle = "سحب البطاقات التفاعلي"
    override val swipeGesturesDesc = "اسحب المقطع يميناً للمفضلة أو يساراً لإضافته لقائمة تشغيل"
    override val showFormatBadgesTitle = "إظهار شارات الصيغة والحجم"
    override val showFormatBadgesDesc = "عرض شارة MP3/FLAC وحجم الملف داخل بطاقات المكتبة"
    override val accentStrengthTitle = "عمق وحيوية الألوان"
    override val swipeFavoriteHint = "المفضلة"
    override val swipePlaylistHint = "قائمة تشغيل"
    override val appVersionLabel = "beta 4.4"
    override val proStudioTitle = "استوديو هندسة الصوت الاحترافي"
    override val proStudioSubtitle = "معادل 10 نطاقات، ضبط استوديو تلقائي وأكثر من 15 ميزة صوتية حقيقية"
    override val proStudioOpenAction = "فتح استوديو الصوت الاحترافي"
    override val studioAutoTitle = "ضبط الاستوديو التلقائي"
    override val studioAutoDesc = "يكتشف طابع المقطع تلقائياً ويضبط المعادل والدفء والعرض الصوتي كأنه خارج من استوديو"
    override val studioAutoAnalyzeNow = "تحليل وضبط الآن"
    override val studioAutoAppliedBanner = "تم تطبيق إعداد الاستوديو التلقائي"
    override val studioAutoNoTrackHint = "شغّل مقطعاً أولاً لتحليل وضبط إعداد الاستوديو"
    override val studioBypassOriginal = "الأصلي (A)"
    override val studioBypassMastered = "الاستوديو (B)"
    override val studioResetAll = "إعادة ضبط الاستوديو"
    override val studioVuMeterLabel = "مقياس الإخراج المباشر (يسار / يمين)"
    override val studioRackEqTitle = "المعادل الاحترافي 10 نطاقات (31Hz–16kHz)"
    override val studioRackToneTitle = "دفء الأنابيب، الوضوح والمشهد الصوتي"
    override val studioRackDynamicsTitle = "الديناميكية، الحماية وفلاتر التردد"
    override val studioRackHeadphoneTitle = "معايرة السماعات وحفظ الإعدادات"
    override val studioTubeWarmth = "دفء الأنابيب التناظري (Tube Warmth)"
    override val studioStereoWidth = "اتساع الستيريو (Mid-Side Width)"
    override val studioSubBassPunch = "قوة البيس العميق (48Hz Sub-Bass)"
    override val studioAirBrilliance = "بريق ونقاء الهواء (12kHz Air)"
    override val studioVocalClarity = "وضوح وإبراز الصوت البشري (3.2kHz)"
    override val studioCompressor = "ضاغط الاستوديو الديناميكي"
    override val studioCrossfeedTitle = "المزج الصوتي الطبيعي للسماعات (Crossfeed)"
    override val studioCrossfeedDesc = "يمزج القناتين بلطف لمنع إرهاق الأذن ومحاكاة مكبرات الاستوديو"
    override val studioTruePeakTitle = "محدد القمم الحقيقية (-0.5 dBFS)"
    override val studioTruePeakDesc = "يحمي السماعات والأذن من التشويش الرقمي عند رفع الترددات"
    override val studioNightModeTitle = "الوضع الليلي الهامس"
    override val studioNightModeDesc = "يخفض القفزات الصوتية العالية ويبرز التفاصيل الهادئة للاستماع الليلي"
    override val studioRumbleFilterTitle = "فلتر إزالة الهدير والضجيج المنخفض"
    override val studioChannelSwapTitle = "عكس قناتي الستيريو (يسار ↔ يمين)"
    override val studioChannelSwapDesc = "تبديل فوري بين القناة اليسرى واليمنى للسماعة"
    override val studioHeadphoneProfileTitle = "معايرة نوع السماعة والمكبر"
    override val studioCustomPresetsTitle = "إعداداتي المحفوظة"
    override val studioSavePresetAction = "حفظ الإعداد"
    override val studioSavePresetHint = "اسم الإعداد (مثلاً: استوديو السهرة)"
    override val studioNoCustomPresets = "احفظ منحنى المعادل وإعدادات الاستوديو لاستدعائها بلمسة واحدة."
    override val studioPresetSavedBanner = "تم حفظ إعداد الاستوديو"
    override val studioPresetLoadedBanner = "تم تحميل إعداد الاستوديو"
    override val playerToolsDrawerOpen = "القوائم والأدوات"
    override val playerToolsDrawerClose = "إغلاق القائمة"
    override val defaultPlayerTitle = "المشغّل الافتراضي للجهاز"
    override val defaultPlayerDesc = "اجعل بريزم مشغّل الصوتيات والفيديو والصور الافتراضي من إعدادات النظام"
    override val defaultPlayerAction = "تعيين كافتراضي"
    override val defaultPlayerOpenedBanner = "اختر الفتح افتراضياً لتعيين بريزم كمشغّل رئيسي"
    override val storageAccessHubTitle = "صلاحيات الوصول للتخزين والمجلدات"
    override val storageAccessHubDesc = "امنح التطبيق وصولاً دائماً وآمناً لمجلدات الموسيقى والفيديو والصور والتنزيلات"
    override val storageGrantedFoldersLabel: (Int) -> String = { count -> "$count مجلدات مفعّلة بوصول دائم" }
    override val storageGrantFolderAction = "منح صلاحية مجلد"
    override val storagePickDownloadsAction = "الوصول لملفات التنزيلات"
    override val storagePickGalleryAction = "الوصول للمعرض"
    override val storageAccessReadyBadge = "وصول دائم وآمن"
    override val selectiveFolderChecklistTitle = "تحديد المجلدات المطلوبة للفحص"
    override val selectiveFolderChecklistHint = "علّم فقط على المجلدات التي تريدها — لن يفحص التطبيق أي مجلد غير محدد."
    override val selectiveConfirmScanBtn: (Int) -> String = { n -> "تأكيد وفحص المحدد ($n)" }
    override val selectiveSelectAll = "تحديد الكل"
    override val selectiveClearAll = "إلغاء الكل"
    override val selectiveScanningOnlyCheckedHint = "فحص دقيق: يتم فحص المجلدات المحددة وأنواع الوسائط المختارة فقط."
    override val selectiveDirectFilesBadge: (Int) -> String = { n -> "$n ملف" }
    override val selectiveMediaTypesLabel = "أنواع الوسائط المراد فحصها"
    override val selectiveEmptyChecklistHint = "اختر أي مجلد موجود في هاتفك أعلاه (الموسيقى، الفيديو، الصور، الكاميرا...) لعرض مجلداته بعلامات التحديد."
    override val selectiveInspectingFoldersLabel = "جارٍ قراءة المجلدات الموجودة…"
    override val selectiveScannedConfirmedBanner: (Int, Int) -> String = { folders, files ->
        "تم فحص $files ملف بدقة من $folders مجلد محدد فقط"
    }
    override val settingsStudioHubSectionTitle = "أدوات واستوديو بريزم"
    override val settingsStudioHubSectionSubtitle = "القرآن، الأذكار، الخزنة، الإحصائيات، الكولاج، الواي فاي وملفات الصوت"

    override fun proEqPresetName(preset: com.example.model.ProEqPreset): String = when (preset) {
        com.example.model.ProEqPreset.FLAT -> "مرجعي مسطح"
        com.example.model.ProEqPreset.STUDIO_MASTER -> "ماستر استوديو"
        com.example.model.ProEqPreset.QURAN_CRYSTAL -> "نقاء القرآن"
        com.example.model.ProEqPreset.DEEP_SUB_CINEMA -> "بيس سينمائي"
        com.example.model.ProEqPreset.ACOUSTIC_WARMTH -> "دفء أكوستيك"
        com.example.model.ProEqPreset.VOCAL_AIR -> "صفاء الصوت"
        com.example.model.ProEqPreset.CONCERT_HALL -> "قاعة حفلات"
        com.example.model.ProEqPreset.LATE_NIGHT -> "سهرة هادئة"
        com.example.model.ProEqPreset.ELECTRONIC_PUNCH -> "إلكتروني قوي"
        com.example.model.ProEqPreset.LOFI_VELVET -> "لو-فاي مخملي"
        com.example.model.ProEqPreset.CUSTOM -> "منحنى مخصص"
    }

    override fun headphoneProfileName(profile: com.example.model.HeadphoneTuningProfile): String = when (profile) {
        com.example.model.HeadphoneTuningProfile.NEUTRAL_STUDIO -> "استوديو محايد"
        com.example.model.HeadphoneTuningProfile.OPEN_BACK_HIFI -> "سماعات مفتوحة"
        com.example.model.HeadphoneTuningProfile.IEM_IN_EAR -> "سماعات أذن IEM"
        com.example.model.HeadphoneTuningProfile.BASS_HEAD_CLUB -> "بيس عميق"
        com.example.model.HeadphoneTuningProfile.SPEAKER_COMPENSATION -> "مكبر صوت"
    }

    override fun autoStudioCategoryName(category: com.example.model.AutoStudioCategory): String = when (category) {
        com.example.model.AutoStudioCategory.QURAN_SANCTUARY -> "استوديو القرآن النقي"
        com.example.model.AutoStudioCategory.LOSSLESS_MASTERING -> "ماستر نقي بدون فقدان"
        com.example.model.AutoStudioCategory.VOCAL_BROADCAST -> "استوديو الإذاعة والصوت"
        com.example.model.AutoStudioCategory.ACOUSTIC_WARMTH -> "دفء الآلات الطبيعية"
        com.example.model.AutoStudioCategory.ELECTRONIC_STAGE -> "مسرح إلكتروني عريض"
        com.example.model.AutoStudioCategory.CINEMATIC_WIDE -> "محيطي سينمائي"
        com.example.model.AutoStudioCategory.BALANCED_STUDIO -> "ماستر استوديو متوازن"
    }
}

private object FrenchStrings : AppStrings by EnglishStrings {
    override val appTitle = "Prism Media"
    override val tabMusic = "Musique"
    override val tabVideos = "Vidéos"
    override val tabPhotos = "Photos"
    override val tabSettings = "Paramètres"
    override val selectMediaFolder = "Choisir un dossier"
    override val importFiles = "Importer des fichiers"
    override val pickGalleryMedia = "Choisir photos & vidéos"
    override val loadDemoMedia = "Charger la démo"
    override val onboardingTitle = "Votre Studio, Prêt à Jouer"
    override val onboardingSubtitle = "Sélectionnez un dossier ou des fichiers pour profiter de l'audio haute fidélité, du cinéma intelligent et d'une galerie fluide."
    override val scanningLibrary = "Analyse des fichiers médias..."
    override val searchAudioHint = "Rechercher pistes, artistes ou formats..."
    override val filterAll = "Tout"
    override val favoritesOnly = "Favoris"
    override val tracksCount: (Int) -> String = { "$it pistes" }
    override val videosCount: (Int) -> String = { "$it vidéos" }
    override val photosCount: (Int) -> String = { "$it photos" }
    override val nowPlaying = "En lecture"
    override val equalizerTitle = "Égaliseur 5 bandes"
    override val equalizerSubtitle = "Traitement audio DSP en temps réel"
    override val sleepTimerTitle = "Minuteur de veille"
    override val menuPlayNow = "Lire maintenant"
    override val menuViewNow = "Ouvrir"
    override val menuAddFavorite = "Ajouter aux favoris"
    override val menuRemoveFavorite = "Retirer des favoris"
    override val menuAddToPlaylist = "Ajouter à une playlist"
    override val menuFileDetails = "Infos & Encyclopédie"
    override val menuRename = "Renommer"
    override val menuShare = "Partager"
    override val menuRemove = "Retirer de la bibliothèque"
    override val save = "Enregistrer"
    override val cancel = "Annuler"
    override val close = "Fermer"
    override val fileDetailsTitle = "Infos & Encyclopédie"
    override val settingsAppearance = "Apparence, Thèmes & Icônes"
    override val darkMode = "Mode sombre"
    override val darkModeDesc = "Basculer entre surfaces studio sombres et claires"
    override val amoledBlack = "Noir pur AMOLED"
    override val dynamicPalette = "Palettes Material You"
    override val languageSetting = "Langue de l'interface"
    override val languageDesc = "Choisissez parmi 4 langues complètes (EN, AR, FR, ES)"
    override val settingsMusicSection = "Paramètres Musique"
    override val settingsMusicDesc = "Lecture, paroles synchronisées, portraits d'artistes, EQ & encyclopédie"
    override val settingsVideoSection = "Paramètres Vidéo"
    override val settingsVideoDesc = "Format, vitesse, gestes, double-tap, sous-titres & PiP"
    override val settingsPhotosSection = "Paramètres Galerie Photos"
    override val settingsPhotosDesc = "Grille, diaporama, haute résolution & localisation EXIF"
    override val settingsSystemSection = "Langue, Sécurité & Crédits"
    override val settingsNavLayoutSection = "Navigation & Mise en page"
    override val settingsLibrary = "Bibliothèque Média"
    override val visualEngineTitle = "Mode d'identité visuelle"
    override val visualEngineSubtitle = "Choisissez entre True Material You ou Liquid Glass"
    override val visualEngineMaterialYouTitle = "True Material You"
    override val visualEngineLiquidGlassTitle = "Verre Liquide"
    override val liveThemePreviewLabel = "Aperçu en direct"
    override val resetCustomizationAction = "Réinitialiser la personnalisation"
    override val resetCustomizationBanner = "Personnalisation réinitialisée par défaut"
    override val settingsSearchHint = "Filtrer les sections..."
    override val onlineEnrichmentTitle = "Encyclopédie & Recherche Intelligente"
    override val onlineEnrichmentDesc = "Récupère photos d'artistes, bios, Coran, Hadiths, paroles & lieux GPS"
    override val autoFetchLyricsTitle = "Paroles synchronisées automatiques"
    override val autoFetchLyricsDesc = "Télécharge automatiquement les paroles .LRC en jouant un titre"
    override val autoFetchArtistPhotoTitle = "Portraits d'artistes réels"
    override val autoFetchArtistPhotoDesc = "Affiche les vraies photos et biographies d'artistes"
    override val clearKnowledgeCacheAction = "Vider le cache encyclopédique"
    override val knowledgeCacheClearedBanner = "Cache encyclopédique vidé"
    override val fetchingKnowledgeLabel = "Recherche dans l'encyclopédie en ligne..."
    override val refreshKnowledgeAction = "Actualiser"
    override val fetchLyricsOnlineAction = "Chercher paroles"
    override val openEncyclopediaAction = "Lire l'article complet"
    override val trackEnergyLabel = "Énergie du titre"
    override val trackBpmLabel = "BPM"
    override val trackMoodLabel = "Ambiance"
    override val artistBioTitle = "Profil Artiste & Encyclopédie"
    override val quranSurahBadge = "Saint Coran"
    override val quranAyahsCountLabel: (Int) -> String = { "$it Versets" }
    override val quranVersesPreviewTitle = "Versets Uthmani vérifiés"
    override val quranTranslationTitle = "Traduction & Sens"
    override val hadithReferenceTitle = "Référence Hadith Authentique"
    override val encyclopediaSectionTitle = "Encyclopédie & Contexte en direct"
    override val photoLocationLabel = "Lieu de prise de vue"
    override val knowledgeSourcePrefix = "Sources :"
    override val noOnlineInfoFound = "Connectez-vous à Internet ou appuyez sur Actualiser pour explorer ce fichier."
    override val fetchCoverAndLyricsAction = "Pochette & Paroles"
    override val batchEnrichLibraryAction = "Récupérer pochettes & paroles (bibliothèque)"
    override val batchEnrichProgressLabel: (Int, Int) -> String = { done, total -> "Récupération $done / $total..." }
    override val batchEnrichCompletedBanner: (Int, Int) -> String = { covers, lyrics ->
        "$covers pochettes HD et $lyrics paroles mises à jour"
    }
    override val coverAndLyricsUpdatedBanner = "Pochette HD et paroles mises à jour"
    override val gaplessPlaybackTitle = "Lecture sans blanc (Gapless)"
    override val gaplessPlaybackDesc = "Supprime les silences entre les pistes consécutives"
    override val audioNormalizationTitle = "Normalisation du volume"
    override val audioNormalizationDesc = "Équilibre automatiquement le volume sonore entre les pistes"
    override val headsetUnplugPauseTitle = "Pause au débranchement du casque"
    override val headsetUnplugPauseDesc = "Met en pause automatiquement lors de la déconnexion audio"
    override val videoGesturesTitle = "Gestes de balayage (Luminosité & Volume)"
    override val videoGesturesDesc = "Glissez verticalement à gauche ou à droite de la vidéo"
    override val videoDoubleTapTitle = "Double-tap pour avancer/reculer"
    override val videoDoubleTapDesc = "Appuyez deux fois à gauche ou à droite pour sauter"
    override val videoRememberPosTitle = "Mémoriser la position vidéo"
    override val videoRememberPosDesc = "Reprendre la vidéo exactement là où vous l'avez laissée"
    override val videoSubtitleSizeTitle = "Taille par défaut des sous-titres"
    override val photoAutoExifTitle = "EXIF & GPS automatiques"
    override val photoAutoExifDesc = "Affiche les infos caméra et localise la photo automatiquement"
    override val photoHighResTitle = "Décodage photo pleine résolution"
    override val photoHighResDesc = "Affiche une netteté maximale lors du zoom"
    override val photoSlideshowTransitionTitle = "Effet de transition diaporama"
    override val slideshowTransitionName: (SlideshowTransitionStyle) -> String = {
        when (it) {
            SlideshowTransitionStyle.FADE -> "Fondu doux"
            SlideshowTransitionStyle.ZOOM -> "Zoom cinéma"
            SlideshowTransitionStyle.SLIDE -> "Glissement"
        }
    }
    override val surfaceDepthTitle = "Profondeur & Relief des cartes"
    override val surfaceDepthName: (SurfaceDepthStyle) -> String = {
        when (it) {
            SurfaceDepthStyle.FLAT -> "Plat minimal"
            SurfaceDepthStyle.ELEVATED -> "Relief équilibré"
            SurfaceDepthStyle.EXPRESSIVE -> "Expressif M3"
        }
    }
    override val tabDisplayName: (MediaTab) -> String = {
        when (it) {
            MediaTab.MUSIC -> tabMusic
            MediaTab.VIDEOS -> tabVideos
            MediaTab.PHOTOS -> tabPhotos
            MediaTab.SETTINGS -> tabSettings
        }
    }
    override val contentBlockedBanner = "Bloqué : Prism refuse strictement tout contenu explicite ou indécent."
    override val lyricsBlockedBanner = "Les paroles vulgaires ou indécentes ont été bloquées par le Bouclier de Pureté."
    override val taqwaSectionTitle = "Invocation (Du'a) et Rappel de Taqwa"
    override val taqwaSectionSubtitle = "Invocation sincère • Avertissement moral • Bouclier de pureté"
    override val taqwaDuaHeading = "Invocation sincère (Du'a)"
    override val taqwaDuaBody = "Ô Allah, nous Te demandons la guidée, la piété, la chasteté et la suffisance. Préserve notre ouïe, notre vue et nos cœurs de tout ce qui Te déplaît, bénis cette application pour le bien et éloigne-nous de toute turpitude. Pardonne-nous, à nos parents et à tous les croyants. Amine."
    override val taqwaWarningHeading = "Avertissement à celui qui ne craint pas Allah"
    override val taqwaWarningVerse = "« L'ouïe, la vue et le cœur : sur tout cela, en vérité, on sera interrogé. » (Sourate Al-Isra 17:36)"
    override val taqwaWarningBody = "Crains Allah en secret et en public. Allah voit et entend tout ce que tu regardes et écoutes. Ne laisse pas ton téléphone témoigner contre toi par un contenu indécent ou illicite ; fais-en un témoin du bien, du savoir utile et du Noble Coran. Le créateur se désavoue devant Allah de tout mauvais usage."
    override val taqwaPurityShieldTitle = "Bouclier de pureté intégré toujours actif"
    override val taqwaPurityShieldBody = "Les contenus explicites et les paroles vulgaires sont refusés automatiquement au cœur de l'application."
    override val taqwaPurityBadgeExplicit = "Explicite bloqué"
    override val taqwaPurityBadgeLyrics = "Paroles propres"
    override val quranHubTitle = "Mushaf Audio & Khatma"
    override val quranHubSubtitle = "Suivi des 114 sourates, répétition Hifz & écoute sereine"
    override val khushuModeTitle = "Mode Khushu (Sans distraction)"
    override val tasbihTitle = "Adhkar & Compteur Tasbih"
    override val morningAlarmTitle = "Réveil Coran Matinal"
    override val audioCutterTitle = "Découpeur Audio & Sonneries"
    override val listeningStatsTitle = "Statistiques d'écoute"
    override val lectureModeTitle = "Mode Conférences & Podcasts"
    override val skipSilenceTitle = "Sauter les silences"
    override val stereoBalanceTitle = "Balance Stéréo (G / D)"
    override val audioMonoTitle = "Audio Mono"
    override val shakeToShuffleTitle = "Secouer pour piste suivante"
    override val batchTagTitle = "Éditeur de tags par lot"
    override val privateVaultTitle = "Coffre Privé Chiffré"
    override val stripExifShareTitle = "Supprimer EXIF avant partage"
    override val collageStudioTitle = "Studio Collage & Filigrane"
    override val customSeedTitle = "Couleur Material You libre"
    override val appIconSwitcherTitle = "Icône d'application dynamique"
    override val themeCodeTitle = "Code de thème partageable"
    override val wifiTransferTitle = "Transfert Wi-Fi Local"
    override val videoColorCalibTitle = "Calibration Couleur Vidéo"
    override val videoChaptersTitle = "Chapitres & Repères"
    override val smartMixRecentlyAdded = "Récemment ajoutés"
    override val smartMixLongLectures = "Longues conférences"
    override val smartMixHiRes = "Hi-Res sans perte"
    override val smartMixQuran = "Coran & Récitations"
    override val proToolsHubTitle = "Hub Studio & Spirituel Prism"
    override val settingsLayoutModeTitle = "Style des panneaux de réglages"
    override val settingsLayoutHorizontal = "Glisser ⇄"
    override val settingsLayoutVertical = "Liste verticale ↕"
    override val collapseSectionAction = "Réduire la section"
    override val collapseAllSectionsAction = "Tout réduire"
    override val swipeHintLabel = "Glisser gauche / droite ⇄"
    override val tuckToEdgeAction = "Replier sur le bord"
    override val restoreFromEdgeAction = "Rappeler le lecteur"
    override val edgeDockSideTitle = "Bord d'ancrage du mini-lecteur"
    override val edgeDockRight = "Bord droit"
    override val edgeDockLeft = "Bord gauche"
    override val floatPhotoAction = "Réduire en photo flottante"
    override val floatingPhotoBadge = "Photo flottante"
    override val widgetsSixGalleryTitle = "6 Widgets d'écran d'accueil"
    override val widgetsSixGallerySubtitle = "Glissez pour prévisualiser et épingler les 6 widgets Material You"
    override val proAudioDeckTitle = "Studio Son & DSP Pro"
    override val driveModeTitle = "Mode Conduite (Grands boutons)"
    override val muteAudioAction = "Muet"
    override val unmuteAudioAction = "Son activé"
    override val spectrumStyleTitle = "Style du spectre audio"
    override val swipeArtworkTitle = "Glisser la pochette pour passer"
    override val videoMirrorTitle = "Miroir vidéo horizontal"
    override val videoAutoHideTitle = "Masquage auto des contrôles"
    override val quickPresetsTitle = "Préréglages esthétiques 1-clic"
    override val globalSearchTitle = "Recherche globale"
    override val globalSearchHint = "Chercher musique, vidéos, photos, sourates, notes..."
    override val recycleBinTitle = "Corbeille intelligente"
    override val mediaNoteTitle = "Note personnelle"
    override val reverbTitle = "Spatialisation 3D & Réverb"
    override val tapSyncLyricsTitle = "Sync Paroles LRC tactile"
    override val browseTracksTab = "Pistes"
    override val browseArtistsTab = "Artistes"
    override val browseAlbumsTab = "Albums"
    override val extractAudioAction = "Extraire l'audio (M4A)"
    override val photoEditStudioTitle = "Éditeur photo Studio"
    override val photoExportPdfAction = "Exporter en PDF"
    override val photoSetWallpaperAction = "Fond d'écran"
    override val photoCompareTitle = "Comparateur photo"
    override val adhkarTitle = "Adhkar Matin & Soir"
    override val audioProfilesTitle = "Profils audio par appareil"
    override val customTabOrderTitle = "Ordre des onglets"
    override val onboardingWelcomeTitle = "Bienvenue dans Prism Media"
    override val onboardingWelcomeSubtitle = "Un sanctuaire onirique pour votre musique Hi-Fi, vos films, vos photos et le Noble Coran."
    override val onboardingFeaturesTitle = "Un Studio de Rêve Complet"
    override val onboardingFeaturesSubtitle = "Tous vos outils créatifs réunis dans un design Material 3 expressif et fluide."
    override val onboardingSetupTitle = "Façonnez votre Atmosphère"
    override val onboardingSetupSubtitle = "Activez votre style visuel et vos fonctions intelligentes dès le premier lancement."
    override val onboardingLibraryTitle = "Connectez votre Bibliothèque"
    override val onboardingNextAction = "Suivant"
    override val onboardingBackAction = "Retour"
    override val onboardingSkipAction = "Passer"
    override val onboardingEnterPrismAction = "Entrer dans Prism"
    override val replayOnboardingAction = "Revoir le guide de bienvenue"
    override val trimVideoAction = "Couper la vidéo (MP4)"
    override val videoTrimmerTitle = "Découpe Vidéo MP4 sans perte"
    override val videoTrimStartLabel = "Début"
    override val videoTrimEndLabel = "Fin"
    override val videoTrimSuccessBanner = "Extrait MP4 enregistré dans Vidéos !"
    override val photoArtFilterLabel = "Filtre artistique"
    override val photoWatermarkHint = "Signature ou filigrane texte (optionnel)"
    override val recycleDaysLeft: (Int) -> String = { "$it j restants" }
    override val quickTileLabel = "Lecture Prism"
    override val sharedToPrismBanner = "Média partagé importé dans Prism"
    override val smartImportTitle = "Importer dossiers et médias"
    override val smartImportSubtitle = "Accédez directement aux dossiers autorisés ou fichiers"
    override val smartImportSafTip = "Android bloque la racine et Téléchargements. Choisissez un sous-dossier (Music/Movies/Pictures) ou importez directement des fichiers."
    override val smartImportQuickFoldersLabel = "Dossiers directs autorisés"
    override val smartImportDirectFilesAction = "Choisir des fichiers (fonctionne dans Téléchargements)"
    override val smartImportBrowseOther = "Parcourir dossiers"
    override val settingsPlayerCard = "Carte Lecteur & Tech Pro"
    override val settingsPlayerCardDesc = "Formes expressives, couleurs par genre, DSP et aperçu direct"
    override val playerGenreColorsTitle = "Couleurs dynamiques par genre"
    override val playerGenreColorsDesc = "Adapte la palette selon le genre et l'ambiance du titre"
    override val playerTechTelemetryTitle = "Badge technique moteur audio"
    override val playerTechTelemetryDesc = "Affiche le format, la fréquence et le DSP 32-bit"
    override val playerExtrasSubtleTitle = "Bouton d'extras discret et compact"
    override val playerExtrasSubtleDesc = "Garde le lecteur plein écran épuré et minimaliste"
    override val playerBitExactDspTitle = "Mode pur Bit-Exact"
    override val playerBitExactDspDesc = "Contourne l'égaliseur pour une sortie audio neutre"
    override val playerExpressivePlayMorphTitle = "Bouton lecture à forme expressive"
    override val playerExtrasStudioTitle = "Outils & Studio du lecteur"
    override val livePreviewBadge = "APERÇU DIRECT"
    override val settingsLockScreen = "Écran de verrouillage & Identité"
    override val settingsLockScreenDesc = "Identité Prism, horloge, pochette et contrôles sur écran verrouillé"
    override val lockScreenControlsTitle = "Carte média sur écran verrouillé"
    override val lockScreenControlsDesc = "Afficher Prism et les contrôles audio sur l'écran de verrouillage"
    override val lockScreenShowArtworkTitle = "Afficher la pochette"
    override val lockScreenShowIdentityTitle = "Badge d'identité Prism Media"
    override val lockScreenShowIdentityDesc = "Affiche l'identité Prism et l'ambiance du morceau"
    override val lockScreenFullScreenTitle = "Lecteur plein écran sur verrouillage"
    override val lockScreenFullScreenDesc = "Affiche l'interface Prism lorsque l'écran verrouillé s'allume"
    override val lockScreenShowClockTitle = "Horloge expressive"
    override val lockScreenShowLyricsTitle = "Paroles synchronisées"
    override val lockScreenBlurBackdropTitle = "Fond aurore flouté"
    override val lockScreenCompactModeTitle = "Mode carte compacte"
    override val lockScreenPreviewAction = "Tester l'écran de verrouillage"
    override val lockScreenUnlockAction = "Ouvrir Prism Media"
    override val pipMusicTitle = "Image dans l'image Musique"
    override val pipMusicDesc = "Fenêtre flottante optionnelle en quittant (désactivé garde l'audio en arrière-plan)"
    override val pipVideoTitle = "Image dans l'image Vidéo"
    override val pipVideoDesc = "Fenêtre vidéo flottante optionnelle en quittant l'application"
    override val pipPhotoTitle = "Image dans l'image Photos"
    override val pipPhotoDesc = "Aperçu photo flottant optionnel en quittant la visionneuse"
    override val pipPhotoBadge = "Photo PiP Prism"
    override val filtersAndToolsAction = "Filtres & Outils"
    override val clearFiltersAction = "Réinitialiser"
    override val moreHeaderActionsDesc = "Plus d'actions"
    override val importDownloadsDirectAction = "Choisir des fichiers dans Téléchargements"
    override val importDownloadsPrivacyExplainer = "Android bloque l'accès complet au dossier racine Téléchargements par confidentialité. Utilisez le bouton ci-dessus pour importer vos fichiers directement, ou ouvrez un sous-dossier."
    override val quickStudioTitle = "Studio de personnalisation rapide"
    override val quickStudioSubtitle = "Ajustez thème, cartes, fond et animations en direct"
    override val cardStyleTitle = "Style des cartes média"
    override val cardStyleGlass = "Verre liquide"
    override val cardStyleElevated = "Matériel M3"
    override val cardStyleMinimal = "Contour minimal"
    override val cardStyleVibrant = "Tonal vibrant"
    override val backdropStyleTitle = "Style d'arrière-plan animé"
    override val backdropAurora = "Orbes aurore"
    override val backdropMist = "Brume douce"
    override val backdropRibbon = "Rubans prisme"
    override val backdropClean = "Pur épuré"
    override val motionFeelTitle = "Physique tactile et animations"
    override val motionBouncy = "Rebond expressif"
    override val motionSmooth = "Fluide soyeux"
    override val motionSnappy = "Ultra vif"
    override val motionReduced = "Calme réduit"
    override val headerStyleTitle = "Style d'en-tête supérieur"
    override val headerBold = "Expressif fort"
    override val headerCompact = "Compact épuré"
    override val headerCentered = "Centré élégant"
    override val swipeGesturesTitle = "Glissements interactifs"
    override val swipeGesturesDesc = "Glissez une piste à droite pour favori ou à gauche pour playlist"
    override val showFormatBadgesTitle = "Badges de format et taille"
    override val showFormatBadgesDesc = "Afficher les badges MP3/FLAC et la taille sur les cartes"
    override val accentStrengthTitle = "Intensité et éclat des couleurs"
    override val swipeFavoriteHint = "Favori"
    override val swipePlaylistHint = "Playlist"
    override val appVersionLabel = "beta 4.4"
    override val proStudioTitle = "Studio de Mastering Audio Pro"
    override val proStudioSubtitle = "Égaliseur 10 bandes, Mastering Auto et 15+ effets DSP temps réel"
    override val proStudioOpenAction = "Ouvrir le Studio Audio Pro"
    override val studioAutoTitle = "Mastering Studio Automatique"
    override val studioAutoDesc = "Détecte le style de chaque piste et applique EQ, chaleur et spatialisation studio"
    override val studioAutoAnalyzeNow = "Masteriser maintenant"
    override val studioBypassOriginal = "Original (A)"
    override val studioBypassMastered = "Masterisé (B)"
    override val playerToolsDrawerOpen = "Outils & Studio"
    override val playerToolsDrawerClose = "Fermer le menu"
    override val defaultPlayerTitle = "Lecteur média par défaut"
    override val defaultPlayerDesc = "Définir Prism comme lecteur par défaut pour musique, vidéos et photos"
    override val defaultPlayerAction = "Définir par défaut"
    override val storageAccessHubTitle = "Accès au stockage et dossiers"
    override val storageAccessHubDesc = "Accordez un accès persistant sécurisé aux dossiers Musique, Vidéos, Photos et Téléchargements"
    override val storageGrantFolderAction = "Autoriser un dossier"
    override val storagePickDownloadsAction = "Fichiers Téléchargements"
    override val storagePickGalleryAction = "Accès Galerie"
    override val selectiveFolderChecklistTitle = "Sélectionner les dossiers à scanner"
    override val selectiveFolderChecklistHint = "Cochez uniquement les dossiers voulus — aucun dossier non coché ne sera scanné."
    override val selectiveConfirmScanBtn: (Int) -> String = { n -> "Confirmer et scanner ($n)" }
    override val selectiveSelectAll = "Tout cocher"
    override val selectiveClearAll = "Effacer"
    override val selectiveScanningOnlyCheckedHint = "Portée stricte : seuls les dossiers cochés et types sélectionnés sont scannés."
    override val selectiveDirectFilesBadge: (Int) -> String = { n -> "$n fichiers" }
    override val selectiveMediaTypesLabel = "Types de médias à scanner"
    override val selectiveEmptyChecklistHint = "Choisissez un dossier du téléphone ci-dessus pour afficher ses sous-dossiers avec cases à cocher."
    override val selectiveInspectingFoldersLabel = "Lecture des dossiers existants…"
    override val selectiveScannedConfirmedBanner: (Int, Int) -> String = { folders, files ->
        "$files éléments scannés depuis $folders dossier(s) coché(s)"
    }
    override val settingsStudioHubSectionTitle = "Studio et Outils Prism"
    override val settingsStudioHubSectionSubtitle = "Coran, Adhkar, Coffre, Stats, Collage, Wi-Fi et Profils Audio"
}

private object SpanishStrings : AppStrings by EnglishStrings {
    override val appTitle = "Prism Media"
    override val tabMusic = "Música"
    override val tabVideos = "Vídeos"
    override val tabPhotos = "Fotos"
    override val tabSettings = "Ajustes"
    override val selectMediaFolder = "Elegir carpeta"
    override val importFiles = "Importar archivos"
    override val pickGalleryMedia = "Elegir fotos y vídeos"
    override val loadDemoMedia = "Cargar demo"
    override val onboardingTitle = "Tu Estudio, Listo para Sonar"
    override val onboardingSubtitle = "Importa carpetas o archivos de tu dispositivo para disfrutar de audio de alta fidelidad, vídeo inteligente y galería fluida."
    override val scanningLibrary = "Escaneando archivos multimedia..."
    override val searchAudioHint = "Buscar canciones, artistas o formatos..."
    override val filterAll = "Todo"
    override val favoritesOnly = "Favoritos"
    override val tracksCount: (Int) -> String = { "$it pistas" }
    override val videosCount: (Int) -> String = { "$it vídeos" }
    override val photosCount: (Int) -> String = { "$it fotos" }
    override val nowPlaying = "Reproduciendo"
    override val equalizerTitle = "Ecualizador 5 bandas"
    override val equalizerSubtitle = "Procesamiento DSP en tiempo real"
    override val sleepTimerTitle = "Temporizador"
    override val menuPlayNow = "Reproducir ahora"
    override val menuViewNow = "Abrir"
    override val menuAddFavorite = "Añadir a favoritos"
    override val menuRemoveFavorite = "Quitar de favoritos"
    override val menuAddToPlaylist = "Añadir a lista"
    override val menuFileDetails = "Info y Enciclopedia"
    override val menuRename = "Renombrar"
    override val menuShare = "Compartir"
    override val menuRemove = "Quitar de la biblioteca"
    override val save = "Guardar"
    override val cancel = "Cancelar"
    override val close = "Cerrar"
    override val fileDetailsTitle = "Info y Enciclopedia"
    override val settingsAppearance = "Apariencia, Temas e Iconos"
    override val darkMode = "Modo oscuro"
    override val darkModeDesc = "Alterna entre superficies oscuras y claras"
    override val amoledBlack = "Negro puro AMOLED"
    override val dynamicPalette = "Paletas Material You"
    override val languageSetting = "Idioma de la interfaz"
    override val languageDesc = "Cambia entre 4 idiomas completos (EN, AR, FR, ES)"
    override val settingsMusicSection = "Ajustes de Música"
    override val settingsMusicDesc = "Reproducción, letras sincronizadas, fotos de artistas, EQ y enciclopedia"
    override val settingsVideoSection = "Ajustes de Vídeo"
    override val settingsVideoDesc = "Relación de aspecto, velocidad, gestos, doble toque, subtítulos y PiP"
    override val settingsPhotosSection = "Ajustes de Galería"
    override val settingsPhotosDesc = "Columnas, presentación, alta resolución y ubicación EXIF"
    override val settingsSystemSection = "Idioma, Seguridad y Créditos"
    override val settingsNavLayoutSection = "Navegación y Diseño"
    override val settingsLibrary = "Biblioteca Multimedia"
    override val visualEngineTitle = "Modo de identidad visual"
    override val visualEngineSubtitle = "Elige libremente entre True Material You o Cristal Líquido"
    override val visualEngineMaterialYouTitle = "True Material You"
    override val visualEngineLiquidGlassTitle = "Cristal Líquido"
    override val liveThemePreviewLabel = "Vista previa en vivo"
    override val resetCustomizationAction = "Restablecer personalización"
    override val resetCustomizationBanner = "Personalización restablecida a valores por defecto"
    override val settingsSearchHint = "Filtrar secciones de ajustes..."
    override val onlineEnrichmentTitle = "Enciclopedia e Investigación Inteligente"
    override val onlineEnrichmentDesc = "Busca fotos de artistas, biografías, Corán, Hadices, letras y lugares GPS"
    override val autoFetchLyricsTitle = "Buscar letras sincronizadas automáticamente"
    override val autoFetchLyricsDesc = "Descarga letras .LRC sincronizadas en vivo al reproducir"
    override val autoFetchArtistPhotoTitle = "Retratos reales de artistas"
    override val autoFetchArtistPhotoDesc = "Muestra fotos reales y perfiles biográficos del artista"
    override val clearKnowledgeCacheAction = "Limpiar caché enciclopédica"
    override val knowledgeCacheClearedBanner = "Caché de conocimiento borrada"
    override val fetchingKnowledgeLabel = "Investigando en la enciclopedia en vivo..."
    override val refreshKnowledgeAction = "Actualizar"
    override val fetchLyricsOnlineAction = "Buscar letras"
    override val openEncyclopediaAction = "Leer artículo completo"
    override val trackEnergyLabel = "Energía del tema"
    override val trackBpmLabel = "BPM"
    override val trackMoodLabel = "Ambiente"
    override val artistBioTitle = "Perfil del Artista y Enciclopedia"
    override val quranSurahBadge = "Sagrado Corán"
    override val quranAyahsCountLabel: (Int) -> String = { "$it Aleyas" }
    override val quranVersesPreviewTitle = "Versículos Uthmani verificados"
    override val quranTranslationTitle = "Significado y Traducción"
    override val hadithReferenceTitle = "Referencia de Hadiz Auténtico"
    override val encyclopediaSectionTitle = "Enciclopedia y Contexto en vivo"
    override val photoLocationLabel = "Ubicación de captura"
    override val knowledgeSourcePrefix = "Fuentes:"
    override val noOnlineInfoFound = "Conéctate a Internet o pulsa Actualizar para investigar este archivo."
    override val fetchCoverAndLyricsAction = "Carátula y Letras"
    override val batchEnrichLibraryAction = "Descargar carátulas y letras de la biblioteca"
    override val batchEnrichProgressLabel: (Int, Int) -> String = { done, total -> "Obteniendo datos $done / $total..." }
    override val batchEnrichCompletedBanner: (Int, Int) -> String = { covers, lyrics ->
        "Actualizadas $covers carátulas HD y $lyrics letras"
    }
    override val coverAndLyricsUpdatedBanner = "Carátula HD y letras actualizadas"
    override val gaplessPlaybackTitle = "Reproducción sin pausas (Gapless)"
    override val gaplessPlaybackDesc = "Elimina silencios entre pistas consecutivas"
    override val audioNormalizationTitle = "Normalización de volumen"
    override val audioNormalizationDesc = "Equilibra el volumen entre pistas suaves y fuertes"
    override val headsetUnplugPauseTitle = "Pausar al desconectar auriculares"
    override val headsetUnplugPauseDesc = "Pausa automáticamente al desconectar cable o Bluetooth"
    override val videoGesturesTitle = "Gestos de deslizamiento (Brillo y Volumen)"
    override val videoGesturesDesc = "Desliza verticalmente a izquierda o derecha en el vídeo"
    override val videoDoubleTapTitle = "Doble toque para avanzar/retroceder"
    override val videoDoubleTapDesc = "Toca dos veces a izquierda o derecha para saltar rápido"
    override val videoRememberPosTitle = "Recordar posición del vídeo"
    override val videoRememberPosDesc = "Reanuda el vídeo exactamente donde lo dejaste"
    override val videoSubtitleSizeTitle = "Tamaño de fuente de subtítulos"
    override val photoAutoExifTitle = "Mostrar EXIF y GPS automáticamente"
    override val photoAutoExifDesc = "Lee metadatos de cámara y ubica la foto por GPS"
    override val photoHighResTitle = "Decodificación en máxima resolución"
    override val photoHighResDesc = "Muestra máxima nitidez nativa al hacer zoom"
    override val photoSlideshowTransitionTitle = "Efecto de transición en presentación"
    override val slideshowTransitionName: (SlideshowTransitionStyle) -> String = {
        when (it) {
            SlideshowTransitionStyle.FADE -> "Desvanecido"
            SlideshowTransitionStyle.ZOOM -> "Zoom cine"
            SlideshowTransitionStyle.SLIDE -> "Deslizamiento"
        }
    }
    override val surfaceDepthTitle = "Profundidad y relieve de tarjetas"
    override val surfaceDepthName: (SurfaceDepthStyle) -> String = {
        when (it) {
            SurfaceDepthStyle.FLAT -> "Plano mínimo"
            SurfaceDepthStyle.ELEVATED -> "Relieve equilibrado"
            SurfaceDepthStyle.EXPRESSIVE -> "Expresivo M3"
        }
    }
    override val tabDisplayName: (MediaTab) -> String = {
        when (it) {
            MediaTab.MUSIC -> tabMusic
            MediaTab.VIDEOS -> tabVideos
            MediaTab.PHOTOS -> tabPhotos
            MediaTab.SETTINGS -> tabSettings
        }
    }
    override val contentBlockedBanner = "Bloqueado: Prism rechaza estrictamente contenido explícito o indecente."
    override val lyricsBlockedBanner = "Letras vulgares o explícitas fueron bloqueadas por el Escudo de Pureza."
    override val taqwaSectionTitle = "Súplica (Du'a) y Recordatorio de Taqwa"
    override val taqwaSectionSubtitle = "Súplica sincera • Advertencia moral • Escudo de pureza"
    override val taqwaDuaHeading = "Súplica sincera (Du'a)"
    override val taqwaDuaBody = "Oh Allah, Te pido guía, piedad, castidad y suficiencia. Protege nuestro oído, nuestra vista y nuestros corazones de todo lo que Te desagrada, bendice esta aplicación para el bien y aléjanos de toda indecencia. Perdónanos a nosotros, a nuestros padres y a todos los creyentes. Amín."
    override val taqwaWarningHeading = "Advertencia a quien no teme a Allah"
    override val taqwaWarningVerse = "«Ciertamente el oído, la vista y el corazón: por todo ello se pedirá cuenta.» (Sura Al-Isra 17:36)"
    override val taqwaWarningBody = "Teme a Allah en privado y en público. Allah observa y escucha todo lo que ves y oyes. No permitas que tu teléfono testifique contra ti con contenido indecente o ilícito; hazlo testigo del bien, del conocimiento útil y del Sagrado Corán. El creador se desentiende ante Allah de todo mal uso."
    override val taqwaPurityShieldTitle = "Escudo de pureza integrado siempre activo"
    override val taqwaPurityShieldBody = "El contenido explícito y las letras vulgares se rechazan automáticamente en el núcleo de la app."
    override val taqwaPurityBadgeExplicit = "Explícito bloqueado"
    override val taqwaPurityBadgeLyrics = "Letras limpias"
    override val quranHubTitle = "Mushaf Audio y Khatma"
    override val quranHubSubtitle = "Seguimiento de 114 suras, repetición Hifz y modo sereno"
    override val khushuModeTitle = "Modo Khushu (Sin distracciones)"
    override val tasbihTitle = "Adhkar y Contador Tasbih"
    override val morningAlarmTitle = "Alarma Matutina de Corán"
    override val audioCutterTitle = "Recortador de Audio y Tonos"
    override val listeningStatsTitle = "Estadísticas de Escucha"
    override val lectureModeTitle = "Modo Conferencias y Podcasts"
    override val skipSilenceTitle = "Omitir silencios"
    override val stereoBalanceTitle = "Balance Estéreo (I / D)"
    override val audioMonoTitle = "Audio Mono"
    override val shakeToShuffleTitle = "Agitar para siguiente pista"
    override val batchTagTitle = "Editor de etiquetas por lote"
    override val privateVaultTitle = "Bóveda Privada Cifrada"
    override val stripExifShareTitle = "Borrar EXIF al compartir"
    override val collageStudioTitle = "Estudio Collage y Marca de Agua"
    override val customSeedTitle = "Color Material You libre"
    override val appIconSwitcherTitle = "Ícono de App Dinámico"
    override val themeCodeTitle = "Código de Tema Compartible"
    override val wifiTransferTitle = "Transferencia Wi-Fi Local"
    override val videoColorCalibTitle = "Calibración de Color Video"
    override val videoChaptersTitle = "Capítulos y Marcadores"
    override val smartMixRecentlyAdded = "Añadidos recientemente"
    override val smartMixLongLectures = "Conferencias largas"
    override val smartMixHiRes = "Hi-Res sin pérdida"
    override val smartMixQuran = "Corán y Recitaciones"
    override val proToolsHubTitle = "Centro Estudio y Espiritual"
    override val settingsLayoutModeTitle = "Estilo de paneles de ajustes"
    override val settingsLayoutHorizontal = "Deslizar ⇄"
    override val settingsLayoutVertical = "Lista vertical ↕"
    override val collapseSectionAction = "Contraer sección"
    override val collapseAllSectionsAction = "Contraer todo"
    override val swipeHintLabel = "Deslizar izquierda / derecha ⇄"
    override val tuckToEdgeAction = "Ocultar al borde"
    override val restoreFromEdgeAction = "Traer reproductor"
    override val edgeDockSideTitle = "Lado de anclaje lateral"
    override val edgeDockRight = "Borde derecho"
    override val edgeDockLeft = "Borde izquierdo"
    override val floatPhotoAction = "Minimizar en foto flotante"
    override val floatingPhotoBadge = "Foto flotante"
    override val widgetsSixGalleryTitle = "6 Widgets de pantalla de inicio"
    override val widgetsSixGallerySubtitle = "Desliza para previsualizar y fijar los 6 widgets Material You"
    override val proAudioDeckTitle = "Estudio de Sonido y DSP Pro"
    override val driveModeTitle = "Modo Conducción (Botones grandes)"
    override val muteAudioAction = "Silenciar"
    override val unmuteAudioAction = "Activar sonido"
    override val spectrumStyleTitle = "Estilo del espectro en vivo"
    override val swipeArtworkTitle = "Deslizar portada para cambiar"
    override val videoMirrorTitle = "Espejo de vídeo horizontal"
    override val videoAutoHideTitle = "Ocultar controles automáticamente"
    override val quickPresetsTitle = "Estilos estéticos en 1 toque"
    override val globalSearchTitle = "Búsqueda global"
    override val globalSearchHint = "Buscar música, vídeos, fotos, suras, notas..."
    override val recycleBinTitle = "Papelera inteligente"
    override val mediaNoteTitle = "Nota personal"
    override val reverbTitle = "Espacializador 3D y Reverberación"
    override val tapSyncLyricsTitle = "Sincronizar letras LRC"
    override val browseTracksTab = "Pistas"
    override val browseArtistsTab = "Artistas"
    override val browseAlbumsTab = "Álbumes"
    override val extractAudioAction = "Extraer audio (M4A)"
    override val photoEditStudioTitle = "Editor de fotos Studio"
    override val photoExportPdfAction = "Exportar a PDF"
    override val photoSetWallpaperAction = "Fondo de pantalla"
    override val photoCompareTitle = "Comparador de fotos"
    override val adhkarTitle = "Adhkar Mañana y Tarde"
    override val audioProfilesTitle = "Perfiles de audio por dispositivo"
    override val customTabOrderTitle = "Orden de pestañas"
    override val onboardingWelcomeTitle = "Bienvenido a Prism Media"
    override val onboardingWelcomeSubtitle = "Un santuario soñado para música Hi-Fi, cine en vídeo, edición fotográfica y Corán."
    override val onboardingFeaturesTitle = "Un Estudio de Ensueño"
    override val onboardingFeaturesSubtitle = "Todas tus herramientas creativas unificadas en Material 3 expresivo y fluido."
    override val onboardingSetupTitle = "Crea tu Atmósfera"
    override val onboardingSetupSubtitle = "Activa tu estilo visual y funciones inteligentes desde tu primer inicio."
    override val onboardingLibraryTitle = "Conecta tu Biblioteca"
    override val onboardingNextAction = "Siguiente"
    override val onboardingBackAction = "Atrás"
    override val onboardingSkipAction = "Omitir"
    override val onboardingEnterPrismAction = "Entrar a Prism"
    override val replayOnboardingAction = "Ver guía de bienvenida"
    override val trimVideoAction = "Recortar vídeo (MP4)"
    override val videoTrimmerTitle = "Recortador de Vídeo MP4 sin pérdida"
    override val videoTrimStartLabel = "Inicio"
    override val videoTrimEndLabel = "Fin"
    override val videoTrimSuccessBanner = "¡Clip MP4 recortado guardado en Vídeos!"
    override val photoArtFilterLabel = "Filtro artístico"
    override val photoWatermarkHint = "Firma o marca de agua de texto (opcional)"
    override val recycleDaysLeft: (Int) -> String = { "$it días rest." }
    override val quickTileLabel = "Prism Play"
    override val sharedToPrismBanner = "Multimedia compartida importada en Prism"
    override val smartImportTitle = "Importar carpetas y archivos"
    override val smartImportSubtitle = "Entra directo a carpetas permitidas o elige archivos"
    override val smartImportSafTip = "Android bloquea la raíz y Descargas. Elige una subcarpeta (Music/Movies/Pictures) o usa Importar archivos directos."
    override val smartImportQuickFoldersLabel = "Carpetas directas permitidas"
    override val smartImportDirectFilesAction = "Elegir archivos directos (funciona en Descargas)"
    override val smartImportBrowseOther = "Explorar carpetas"
    override val settingsPlayerCard = "Tarjeta Reproductor y Tech Pro"
    override val settingsPlayerCardDesc = "Formas expresivas, colores por género, DSP y vista previa en vivo"
    override val playerGenreColorsTitle = "Colores dinámicos por género"
    override val playerGenreColorsDesc = "Cambia los tonos automáticamente según el género de la canción"
    override val playerTechTelemetryTitle = "Insignia técnica de audio"
    override val playerTechTelemetryDesc = "Muestra formato, frecuencia y estado del DSP de 32 bits"
    override val playerExtrasSubtleTitle = "Botón de extras discreto"
    override val playerExtrasSubtleDesc = "Mantiene la pantalla del reproductor limpia y minimalista"
    override val playerBitExactDspTitle = "Modo puro Bit-Exact"
    override val playerBitExactDspDesc = "Omite efectos para una salida de audio directa"
    override val playerExpressivePlayMorphTitle = "Botón de reproducción expresivo"
    override val playerExtrasStudioTitle = "Herramientas del reproductor"
    override val livePreviewBadge = "VISTA EN VIVO"
    override val settingsLockScreen = "Pantalla de bloqueo e Identidad"
    override val settingsLockScreenDesc = "Identidad de Prism, reloj, carátula y controles en bloqueo"
    override val lockScreenControlsTitle = "Tarjeta multimedia en bloqueo"
    override val lockScreenControlsDesc = "Muestra Prism y controles de reproducción con pantalla bloqueada"
    override val lockScreenShowArtworkTitle = "Mostrar carátula en bloqueo"
    override val lockScreenShowIdentityTitle = "Insignia de identidad Prism Media"
    override val lockScreenShowIdentityDesc = "Destaca la identidad de Prism y el género activo"
    override val lockScreenFullScreenTitle = "Reproductor en pantalla de bloqueo"
    override val lockScreenFullScreenDesc = "Muestra el reproductor expresivo al encender la pantalla bloqueada"
    override val lockScreenShowClockTitle = "Reloj expresivo en bloqueo"
    override val lockScreenShowLyricsTitle = "Línea de letra sincronizada"
    override val lockScreenBlurBackdropTitle = "Fondo aurora difuminado"
    override val lockScreenCompactModeTitle = "Tarjeta compacta en bloqueo"
    override val lockScreenPreviewAction = "Probar pantalla de bloqueo"
    override val lockScreenUnlockAction = "Abrir Prism Media"
    override val pipMusicTitle = "Imagen en imagen (Música)"
    override val pipMusicDesc = "Ventana flotante opcional al salir (desactivado mantiene el audio en segundo plano)"
    override val pipVideoTitle = "Imagen en imagen (Vídeos)"
    override val pipVideoDesc = "Ventana flotante opcional al salir del reproductor de vídeo"
    override val pipPhotoTitle = "Imagen en imagen (Fotos)"
    override val pipPhotoDesc = "Vista previa flotante opcional al salir del visor de fotos"
    override val pipPhotoBadge = "Foto PiP Prism"
    override val filtersAndToolsAction = "Filtros y herramientas"
    override val clearFiltersAction = "Restablecer"
    override val moreHeaderActionsDesc = "Más acciones"
    override val importDownloadsDirectAction = "Elegir archivos desde Descargas"
    override val importDownloadsPrivacyExplainer = "Android restringe el acceso a toda la carpeta raíz Descargas por privacidad. Usa el botón superior para importar archivos directamente o elige una subcarpeta."
    override val quickStudioTitle = "Estudio de personalización rápida"
    override val quickStudioSubtitle = "Ajusta tema, tarjetas, fondo y movimiento al instante"
    override val cardStyleTitle = "Estilo de tarjetas multimedia"
    override val cardStyleGlass = "Vidrio líquido"
    override val cardStyleElevated = "Elevado M3"
    override val cardStyleMinimal = "Contorno mínimo"
    override val cardStyleVibrant = "Tonal vibrante"
    override val backdropStyleTitle = "Estilo de fondo ambiental"
    override val backdropAurora = "Orbes aurora"
    override val backdropMist = "Niebla suave"
    override val backdropRibbon = "Cintas prisma"
    override val backdropClean = "Puro limpio"
    override val motionFeelTitle = "Física táctil y movimiento"
    override val motionBouncy = "Rebote expresivo"
    override val motionSmooth = "Fluido suave"
    override val motionSnappy = "Ultra rápido"
    override val motionReduced = "Calma mínima"
    override val headerStyleTitle = "Estilo del encabezado"
    override val headerBold = "Expresivo audaz"
    override val headerCompact = "Compacto simple"
    override val headerCentered = "Centrado elegante"
    override val swipeGesturesTitle = "Deslizamiento interactivo"
    override val swipeGesturesDesc = "Desliza pistas a la derecha para favorito o izquierda para lista"
    override val showFormatBadgesTitle = "Mostrar insignias de formato"
    override val showFormatBadgesDesc = "Ver etiquetas MP3/FLAC y tamaño en las tarjetas"
    override val accentStrengthTitle = "Intensidad y viveza de color"
    override val swipeFavoriteHint = "Favorito"
    override val swipePlaylistHint = "Lista"
    override val appVersionLabel = "beta 4.4"
    override val proStudioTitle = "Estudio de Masterización Pro"
    override val proStudioSubtitle = "Ecualizador de 10 bandas, Auto-Mastering y 15+ efectos DSP en tiempo real"
    override val proStudioOpenAction = "Abrir Estudio Audio Pro"
    override val studioAutoTitle = "Auto-Mastering de Estudio"
    override val studioAutoDesc = "Detecta el estilo de la pista y ajusta EQ, calidez, amplitud y dinámica"
    override val studioAutoAnalyzeNow = "Masterizar ahora"
    override val studioBypassOriginal = "Original (A)"
    override val studioBypassMastered = "Masterizado (B)"
    override val playerToolsDrawerOpen = "Menú y Estudio"
    override val playerToolsDrawerClose = "Ocultar menú"
    override val defaultPlayerTitle = "Reproductor predeterminado"
    override val defaultPlayerDesc = "Configura Prism como reproductor principal de música, vídeos y fotos"
    override val defaultPlayerAction = "Establecer"
    override val storageAccessHubTitle = "Acceso a almacenamiento y carpetas"
    override val storageAccessHubDesc = "Concede acceso persistente y seguro a Música, Vídeos, Fotos y Descargas"
    override val storageGrantFolderAction = "Autorizar carpeta"
    override val storagePickDownloadsAction = "Archivos de Descargas"
    override val storagePickGalleryAction = "Acceso a Galería"
    override val selectiveFolderChecklistTitle = "Elegir carpetas para escanear"
    override val selectiveFolderChecklistHint = "Marca solo las carpetas deseadas — Prism nunca escaneará carpetas sin marcar."
    override val selectiveConfirmScanBtn: (Int) -> String = { n -> "Confirmar y escanear ($n)" }
    override val selectiveSelectAll = "Marcar todo"
    override val selectiveClearAll = "Limpiar"
    override val selectiveScanningOnlyCheckedHint = "Alcance estricto: solo se escanean las carpetas marcadas y los tipos elegidos."
    override val selectiveDirectFilesBadge: (Int) -> String = { n -> "$n archivos" }
    override val selectiveMediaTypesLabel = "Tipos de medios a escanear"
    override val selectiveEmptyChecklistHint = "Elige cualquier carpeta existente arriba para ver sus subcarpetas con casillas."
    override val selectiveInspectingFoldersLabel = "Leyendo carpetas existentes…"
    override val selectiveScannedConfirmedBanner: (Int, Int) -> String = { folders, files ->
        "$files elementos escaneados desde $folders carpeta(s) marcada(s)"
    }
    override val settingsStudioHubSectionTitle = "Estudio y Herramientas Prism"
    override val settingsStudioHubSectionSubtitle = "Corán, Adhkar, Bóveda, Estadísticas, Collage, Wi-Fi y Perfiles de Audio"
}

fun stringsFor(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.EN -> EnglishStrings
    AppLanguage.AR -> ArabicStrings
    AppLanguage.FR -> FrenchStrings
    AppLanguage.ES -> SpanishStrings
}
