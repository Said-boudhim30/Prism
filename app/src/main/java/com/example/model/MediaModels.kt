package com.example.model

import androidx.annotation.DrawableRes

enum class MediaTab {
    MUSIC, VIDEOS, PHOTOS, SETTINGS
}

enum class AccentPalette {
    LAVENDER, EMERALD, OCEAN, AMBER, ROSE, CORAL, MIDNIGHT, SYSTEM
}

enum class IconPackStyle {
    GOOGLE_ROUNDED, FILLED_EXPRESSIVE, OUTLINED_MINIMAL, DUOTONE_PILL
}

enum class MediaSortOption {
    NEWEST, NAME, DURATION, SIZE
}

enum class MediaViewMode {
    LIST, COMPACT, GRID
}

enum class QueueSortOption {
    CUSTOM, NAME, ARTIST, DURATION, REVERSE, SHUFFLE
}

enum class LiquidGlassStyle {
    MATERIAL_LIQUID, CRYSTAL_TONAL, VELVET_FROST, SOLID_M3
}

enum class NavTransitionStyle {
    EXPRESSIVE_SPRING, PARALLAX_DEPTH, LIQUID_MORPH, SHARED_AXIS
}

enum class MaterialContrastLevel {
    STANDARD, MEDIUM, HIGH
}

enum class MaterialFontStyle {
    MATERIAL_EXPRESSIVE, GOOGLE_CLEAN, EDITORIAL_STUDIO
}

enum class SmartMusicMix {
    ALL_TRACKS,
    FAVORITES_MIX,
    MOST_PLAYED_MIX,
    RECENTLY_ADDED,
    LONG_LECTURES,
    HI_RES_LOSSLESS,
    QURAN_RECITATIONS,
    QUICK_DISCOVERY,
}

enum class AppIconVariant {
    CLASSIC, OBSIDIAN, CRYSTAL, EMERALD, GOLD
}

enum class VideoColorPreset {
    NATURAL, CINEMA, EYE_COMFORT, VIVID, NIGHT
}

data class VideoColorCalibration(
    val preset: VideoColorPreset = VideoColorPreset.NATURAL,
    val brightness: Float = 0f,   // -0.35f..+0.35f
    val contrast: Float = 1.0f,   // 0.75f..1.35f
    val saturation: Float = 1.0f, // 0.5f..1.5f
    val warmth: Float = 0f,       // -0.3f..+0.4f
)

enum class SubtitleColorStyle(val argb: Long) {
    WHITE(0xFFFFFFFF),
    YELLOW(0xFFFFE57F),
    CYAN(0xFF80F5FF),
    AMBER(0xFFFFCC80),
}

enum class CollageLayoutMode {
    SIDE_BY_SIDE, STACKED_2, GRID_2X2, HERO_TRIO
}

data class FolderCustomization(
    val folderName: String,
    val pinned: Boolean = false,
    val accentArgb: Int? = null,
    val badgeSymbol: String = "",
)

data class QuranKhatmaProgress(
    val completedSurahs: Set<Int> = emptySet(),
    val lastSurahNumber: Int = 1,
    val lastPositionMs: Long = 0L,
    val hifzRepeatTarget: Int = 3, // 3, 5, 10, 0 = infinite
    val totalQuranMinutes: Int = 0,
) {
    val completionPercent: Int
        get() = ((completedSurahs.size.coerceIn(0, 114) * 100f) / 114f).toInt()
}

data class MorningAlarmConfig(
    val enabled: Boolean = false,
    val hour: Int = 5,
    val minute: Int = 30,
    val fadeInSeconds: Int = 30,
    val trackId: String? = null,
    val trackTitle: String = "",
    val repeatDaily: Boolean = true,
)

data class VaultItem(
    val id: String,
    val originalTitle: String,
    val mediaType: RenameMediaType,
    val encryptedFileName: String,
    val sizeBytes: Long,
    val addedAtMs: Long,
    val mimeType: String,
)

data class ListeningStatsSnapshot(
    val totalPlays: Int = 0,
    val totalMinutes: Int = 0,
    val topTracks: List<Pair<AudioTrack, Int>> = emptyList(),
    val topArtists: List<Pair<String, Int>> = emptyList(),
    val formatCounts: Map<String, Int> = emptyMap(),
    val dailyMinutes: List<Pair<String, Int>> = emptyList(),
)

enum class NavBarStyle {
    FLOATING_PILL, DOCKED_MATERIAL_YOU, COMPACT_DOCK
}

enum class PlayerArtworkStyle {
    SCALLOPED_COOKIE, SQUIRCLE_EXPRESSIVE, VINYL_DISC, SOFT_PILL_ARCH, CLOVER_FOUR, CIRCLE_GLOW, ROUNDED_CARD
}

enum class UiDensity {
    COMFORTABLE, BALANCED, COMPACT
}

data class LrcLine(
    val timeMs: Long,
    val text: String,
)

data class IntruderAttempt(
    val timestampMs: Long,
    val formattedTime: String,
    val failedCount: Int,
    val note: String,
)

enum class RenameMediaType {
    AUDIO, VIDEO, PHOTO
}

data class RenameTarget(
    val id: String,
    val currentTitle: String,
    val mediaType: RenameMediaType,
)

data class UserPlaylist(
    val id: String,
    val name: String,
    val trackIds: List<String> = emptyList(),
)

enum class AppLanguage {
    EN, AR, FR, ES
}

enum class MediaKnowledgeCategory {
    MUSIC, QURAN, HADITH, VIDEO, PHOTO, ENCYCLOPEDIA
}

enum class SlideshowTransitionStyle {
    FADE, ZOOM, SLIDE
}

enum class SurfaceDepthStyle {
    FLAT, ELEVATED, EXPRESSIVE
}

data class MediaKnowledgeReport(
    val mediaId: String,
    val category: MediaKnowledgeCategory = MediaKnowledgeCategory.MUSIC,
    val artistName: String = "",
    val artistPhotoUrl: String? = null,
    val albumCoverUrl: String? = null,
    val artistBio: String = "",
    val artistRole: String = "",
    val albumTitle: String = "",
    val releaseYear: String? = null,
    val genre: String? = null,
    val energyPercent: Int = 65,
    val bpm: Int = 100,
    val moodLabel: String = "",
    val audioQualityBadge: String = "",
    val fetchedLyrics: String? = null,
    val quranSurahNumber: Int? = null,
    val quranSurahNameAr: String? = null,
    val quranSurahNameEn: String? = null,
    val quranRevelationType: String? = null,
    val quranAyahCount: Int? = null,
    val quranSampleAyahs: String? = null,
    val quranTranslation: String? = null,
    val reciterName: String? = null,
    val hadithBook: String? = null,
    val hadithText: String? = null,
    val encyclopediaTitle: String? = null,
    val encyclopediaSummary: String? = null,
    val encyclopediaImageUrl: String? = null,
    val encyclopediaUrl: String? = null,
    val photoLocationLabel: String? = null,
    val sourceAttribution: String = "",
    val fetchedAtMs: Long = 0L,
)

enum class RepeatMode {
    OFF, ALL, ONE
}

enum class SecondaryPaneMode {
    MUSIC, VIDEOS, PHOTOS, EQUALIZER
}

enum class EqPreset(val gainsDb: List<Float>) {
    FLAT(listOf(0f, 0f, 0f, 0f, 0f)),
    DEEP_BASS(listOf(9.5f, 6f, -1f, 1.5f, 3f)),
    VOCAL_BOOST(listOf(-3.5f, 0.5f, 7f, 6.5f, 2f)),
    CLUB(listOf(7.5f, 3.5f, -2f, 4.5f, 8f)),
    CUSTOM(listOf(0f, 0f, 0f, 0f, 0f))
}

data class WatchedFolder(
    val uriString: String,
    val displayName: String,
    val enabled: Boolean = true,
    val audioCount: Int = 0,
    val videoCount: Int = 0,
    val photoCount: Int = 0,
    val lastScannedMs: Long = 0L,
    val selectedSubfolderDocIds: List<String> = emptyList(),
    val includeAudio: Boolean = true,
    val includeVideo: Boolean = true,
    val includePhoto: Boolean = true,
) {
    val totalCount: Int get() = audioCount + videoCount + photoCount
}

/**
 * Represents an existing device folder or subfolder inside a user-granted SAF tree
 * shown with an interactive Checkbox before confirming a strictly scoped scan.
 */
data class DiscoveredSubfolder(
    val treeUriString: String,
    val docId: String,
    val displayName: String,
    val parentPath: String,
    val isRoot: Boolean,
    val directMediaCount: Int = 0,
) {
    val selectionKey: String get() = "$treeUriString|$docId"
}

data class TrackTagOverride(
    val title: String,
    val artist: String,
    val album: String,
    val artworkUriString: String? = null,
)

data class MediaBookmark(
    val id: String,
    val mediaId: String,
    val positionMs: Long,
    val label: String,
    val createdAtMs: Long = 0L,
)

enum class DuplicateMediaType {
    AUDIO, VIDEO, PHOTO
}

data class DuplicateGroup(
    val key: String,
    val displayTitle: String,
    val sizeBytes: Long,
    val itemIds: List<String>,
    val mediaType: DuplicateMediaType,
)

data class ScanProgressState(
    val isScanning: Boolean = false,
    val discoveredCount: Int = 0,
    val enrichedCount: Int = 0,
    val currentFolderName: String = "",
)

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val uriString: String,
    val extension: String,
    val mimeType: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val artworkUriString: String? = null,
    @DrawableRes val artworkResId: Int? = null,
    val isFavorite: Boolean = false,
    val isDemo: Boolean = false,
    val addedOrder: Long = 0L,
    val folderName: String = "",
    val sourceFolderUri: String = "",
    val dateModifiedMs: Long = 0L,
    val playCount: Int = 0,
    val lastPlayedMs: Long = 0L,
) {
    /** Normalized creator/artist handle ID (e.g., @prism_studio) displayed on active music cards. */
    val artistHandle: String
        get() {
            val cleaned = artist.trim()
                .lowercase()
                .replace(Regex("[^a-z0-9\\u0600-\\u06FF]+"), "_")
                .trim('_')
            return "@${cleaned.ifBlank { "artist" }}"
        }
}

data class VideoItem(
    val id: String,
    val title: String,
    val uriString: String,
    val extension: String,
    val mimeType: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val resolution: String,
    @DrawableRes val thumbnailResId: Int? = null,
    val isFavorite: Boolean = false,
    val isDemo: Boolean = false,
    val addedOrder: Long = 0L,
    val folderName: String = "",
    val sourceFolderUri: String = "",
    val dateModifiedMs: Long = 0L,
)

data class PhotoItem(
    val id: String,
    val title: String,
    val uriString: String,
    val extension: String,
    val mimeType: String,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    @DrawableRes val resId: Int? = null,
    val isFavorite: Boolean = false,
    val isDemo: Boolean = false,
    val folderName: String = "",
    val sourceFolderUri: String = "",
    val dateModifiedMs: Long = 0L,
)

data class MediaDetailsInfo(
    val title: String,
    val extension: String,
    val mimeType: String,
    val sizeBytes: Long,
    val extraMeta: String,
    val uriString: String,
    val mediaId: String = "",
    val artist: String = "",
    val mediaType: RenameMediaType = RenameMediaType.AUDIO,
)

data class AppSettings(
    val darkTheme: Boolean = true,
    val amoledBlack: Boolean = false,
    val accentPalette: AccentPalette = AccentPalette.SYSTEM,
    val iconStyle: IconPackStyle = IconPackStyle.GOOGLE_ROUNDED,
    val language: AppLanguage = AppLanguage.EN,
    // Typography & Display Adaptation
    val fontStyle: MaterialFontStyle = MaterialFontStyle.MATERIAL_EXPRESSIVE,
    val fontScaleMultiplier: Float = 1.0f,
    // Hardware Media Drivers & Native Fidelity
    val hardwareVideoAcceleration: Boolean = true,
    // Per-Tab View Modes (List / Compact / Grid)
    val musicViewMode: MediaViewMode = MediaViewMode.LIST,
    val videoViewMode: MediaViewMode = MediaViewMode.GRID,
    val photoViewMode: MediaViewMode = MediaViewMode.GRID,
    // Music Settings
    val autoPlayNext: Boolean = true,
    val resumePlaybackPosition: Boolean = true,
    val filterShortAudio: Boolean = true,
    val showLyricsInPlayer: Boolean = true,
    val eqEnabled: Boolean = true,
    val bassBoost: Float = 0f,
    val eqPreset: EqPreset = EqPreset.FLAT,
    val eqGainsDb: List<Float> = EqPreset.FLAT.gainsDb,
    // Video Settings
    val autoPipEnabled: Boolean = true,
    val videoDefaultAspect: Int = 0, // 0=Fit, 1=Crop, 2=Fill
    val videoDefaultSpeed: Float = 1.0f,
    val videoDefaultLoop: Boolean = false,
    // Photo Gallery Settings
    val photoGridColumns: Int = 0, // 0=Auto (Screen-adapted), 2, 3
    val photoSlideshowSeconds: Int = 3, // 2, 3, 5, 8
    val photoShowTitles: Boolean = true,
    // Smart Auto-Watch Folders Setup & Sync
    val folderSetupCompleted: Boolean = false,
    val autoScanWatchedFolders: Boolean = true,
    // Notifications & Built-In Security Shield
    val notificationsEnabled: Boolean = true,
    val tamperGuardEnabled: Boolean = true,
    val appLockEnabled: Boolean = false,
    val appLockOnBackground: Boolean = true,
    val biometricUnlockEnabled: Boolean = false,
    val screenPrivacyEnabled: Boolean = false,
    // Material You & Customizable Player Bar preferences
    val squigglyProgress: Boolean = true,
    val miniPlayerCompact: Boolean = false,
    val miniPlayerFloating: Boolean = false,
    val barShowSpectrum: Boolean = true,
    val barShowSeek10: Boolean = true,
    val barShowFavorite: Boolean = true,
    val barShowSpeed: Boolean = true,
    val barShowAbLoop: Boolean = true,
    val barShowEq: Boolean = true,
    val barShowTimer: Boolean = true,
    val barShowQueue: Boolean = true,
    // Material You Tonal Surface, Motion Transitions & Studio Pro Audio
    val liquidGlassStyle: LiquidGlassStyle = LiquidGlassStyle.MATERIAL_LIQUID,
    val liquidGlassIntensity: Float = 0.55f,
    val shapeRoundness: Float = 1.0f,
    val navTransitionStyle: NavTransitionStyle = NavTransitionStyle.EXPRESSIVE_SPRING,
    val contrastLevel: MaterialContrastLevel = MaterialContrastLevel.STANDARD,
    val edgeGlowEnabled: Boolean = true,
    val audioPreampPercent: Int = 100, // 100..200% studio software volume boost
    val audioPitch: Float = 1.0f, // 0.75x..1.50x
    val crossfadeSeconds: Int = 0, // 0..12s smooth fade-in transition
    // Deep Interface, Navigation & Player Customization
    val navBarStyle: NavBarStyle = NavBarStyle.FLOATING_PILL,
    val startupTab: MediaTab = MediaTab.MUSIC,
    val uiDensity: UiDensity = UiDensity.BALANCED,
    val playerArtworkStyle: PlayerArtworkStyle = PlayerArtworkStyle.VINYL_DISC,
    val albumArtTintEnabled: Boolean = true,
    val showFullPlayerSpectrum: Boolean = true,
    val seekStepSeconds: Int = 10, // 5, 10, 15, 30
    val hapticsEnabled: Boolean = true,
    // Deep Online Knowledge & Encyclopedia Enrichment + Pro Music/Video/Photo/Surface Controls
    val autoOnlineEnrichment: Boolean = true,
    val autoFetchLyrics: Boolean = true,
    val autoFetchArtistPhotos: Boolean = true,
    val gaplessPlayback: Boolean = true,
    val audioNormalization: Boolean = false,
    val pauseOnHeadsetUnplug: Boolean = true,
    val videoGesturesEnabled: Boolean = true,
    val videoDoubleTapSeekEnabled: Boolean = true,
    val videoRememberPosition: Boolean = true,
    val videoSubtitleSizeSp: Int = 18,
    val photoAutoShowExif: Boolean = true,
    val photoHighResDecode: Boolean = true,
    val photoSlideshowTransition: SlideshowTransitionStyle = SlideshowTransitionStyle.FADE,
    val surfaceDepthStyle: SurfaceDepthStyle = SurfaceDepthStyle.EXPRESSIVE,
    // Spiritual, Pro Audio Accessibility, Video Studio, Privacy & Material You Extensions
    val khushuModeEnabled: Boolean = false,
    val lectureModeEnabled: Boolean = false,
    val skipSilenceEnabled: Boolean = false,
    val audioBalance: Float = 0f, // -1f (Left) .. 0f (Center) .. +1f (Right)
    val audioMonoEnabled: Boolean = false,
    val shakeToShuffleEnabled: Boolean = false,
    val stripExifOnShare: Boolean = true,
    val customSeedColorArgb: Int? = null,
    val appIconVariant: AppIconVariant = AppIconVariant.CLASSIC,
    val videoSubtitleColor: SubtitleColorStyle = SubtitleColorStyle.WHITE,
    val videoSubtitleBgOpacity: Float = 0.72f,
    val videoSubtitleBottomOffsetDp: Int = 24,
    val tasbihDailyCount: Int = 0,
    // Beauty, Simplicity, Horizontal Settings, Sticky Edge-Dock & Pro Player Extensions
    val settingsLayoutMode: SettingsLayoutMode = SettingsLayoutMode.HORIZONTAL_PAGES,
    val miniPlayerEdgeDockSide: EdgeDockSide = EdgeDockSide.RIGHT,
    val spectrumVisualStyle: SpectrumVisualStyle = SpectrumVisualStyle.CLASSIC_BARS,
    val lyricsFontSizeSp: Int = 16,
    val driveModeEnabled: Boolean = false,
    val playerGestureSwipeArt: Boolean = true,
    val videoMirrorEnabled: Boolean = false,
    val videoAutoHideSeconds: Int = 4,
    // 17-Upgrade Suite: 3D Reverb, Device Audio Profiles, Auto-Next Video, Custom Nav Order, Header Toggles & Adhkar
    val reverbEnvironment: ReverbEnvironment = ReverbEnvironment.NONE,
    val autoAudioDeviceProfiles: Boolean = true,
    val videoAutoNextEnabled: Boolean = true,
    val navTabOrder: List<MediaTab> = listOf(MediaTab.MUSIC, MediaTab.VIDEOS, MediaTab.PHOTOS, MediaTab.SETTINGS),
    val headerShowStreamBtn: Boolean = true,
    val headerShowMultiWindowBtn: Boolean = true,
    val adhkarCompletedKeys: Set<String> = emptySet(),
    // Dedicated Full-Screen Player Card & Pro Audio Tech Settings
    val playerGenreDynamicColors: Boolean = true,
    val playerShowTechTelemetry: Boolean = true,
    val playerExtrasButtonSubtle: Boolean = true,
    val playerDirectBitExactDsp: Boolean = false,
    val playerExpressivePlayMorph: Boolean = true,
    // Lock-Screen Identity & Playback Customization
    val lockScreenControlsEnabled: Boolean = true,
    val lockScreenShowArtwork: Boolean = true,
    val lockScreenShowPrismIdentity: Boolean = true,
    val lockScreenFullScreenEnabled: Boolean = true,
    val lockScreenShowClock: Boolean = true,
    val lockScreenShowLyrics: Boolean = true,
    val lockScreenBlurBackdrop: Boolean = true,
    val lockScreenCompactMode: Boolean = false,
    // Optional Per-Mode Picture-in-Picture (Music, Video, Photos independently configurable)
    val pipMusicEnabled: Boolean = false,
    val pipVideoEnabled: Boolean = true,
    val pipPhotoEnabled: Boolean = false,
    // Deep Beauty, Interactivity & Comprehensive Studio Customization
    val cardStyle: MediaCardStyle = MediaCardStyle.GLASS_CARD,
    val backdropStyle: BackdropAnimationStyle = BackdropAnimationStyle.AURORA_ORBS,
    val motionFeelStyle: MotionFeelStyle = MotionFeelStyle.BOUNCY_EXPRESSIVE,
    val headerTitleStyle: HeaderTitleStyle = HeaderTitleStyle.EXPRESSIVE_BOLD,
    val swipeGesturesEnabled: Boolean = true,
    val showFormatBadges: Boolean = true,
    val accentStrength: Float = 1.0f, // 0.5f..1.4f
    // Beta 4.4 Pro Studio Mastering Engine (10-Band EQ + Auto Studio Detection + 15+ Pro DSP Controls)
    val proEqEnabled: Boolean = true,
    val proEqPreset: ProEqPreset = ProEqPreset.FLAT,
    val proTenBandGainsDb: List<Float> = ProEqPreset.FLAT.gainsDb,
    val autoStudioMasteringEnabled: Boolean = false,
    val tubeWarmth: Float = 0f, // 0f..1f analog even-harmonic tube saturation
    val stereoWidth: Float = 1.0f, // 0.5f..2.0f Mid-Side soundstage width
    val crossfeedEnabled: Boolean = false, // Binaural headphone acoustic crossfeed
    val subBassPunch: Float = 0f, // 0f..1f 48Hz subharmonic shelf
    val airBrilliance: Float = 0f, // 0f..1f 12kHz studio air sheen
    val vocalClarity: Float = 0f, // 0f..1f 3.2kHz presence clarity
    val compressorStrength: Float = 0f, // 0f..1f studio dynamic glue compressor
    val truePeakLimiterEnabled: Boolean = true, // -0.5 dBFS brickwall peak safety ceiling
    val nightModeEnabled: Boolean = false, // Late-night dynamic whisper leveling
    val rumbleFilterHz: Int = 0, // 0 (OFF), 30, 60, 80 Hz high-pass sub-rumble filter
    val channelSwapEnabled: Boolean = false, // Instant L <-> R swap
    val headphoneProfile: HeadphoneTuningProfile = HeadphoneTuningProfile.NEUTRAL_STUDIO,
    val dspBypassEnabled: Boolean = false, // Instant A/B raw comparison bypass
)

enum class ProEqPreset(val gainsDb: List<Float>) {
    // 10 Bands: 31Hz, 62Hz, 125Hz, 250Hz, 500Hz, 1kHz, 2kHz, 4kHz, 8kHz, 16kHz
    FLAT(listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)),
    STUDIO_MASTER(listOf(1.5f, 2.2f, 1.0f, -0.8f, 0f, 0.8f, 1.8f, 2.5f, 2.2f, 3.0f)),
    QURAN_CRYSTAL(listOf(-2.0f, -1.0f, 0.5f, 1.5f, 2.0f, 3.2f, 4.0f, 3.0f, 1.5f, 1.8f)),
    DEEP_SUB_CINEMA(listOf(7.5f, 6.2f, 4.0f, 1.0f, -1.0f, 0f, 1.5f, 2.5f, 3.0f, 3.5f)),
    ACOUSTIC_WARMTH(listOf(1.0f, 2.5f, 3.0f, 1.5f, 0.5f, 1.0f, 2.0f, 1.8f, 1.2f, 2.0f)),
    VOCAL_AIR(listOf(-3.0f, -1.5f, 0f, 1.0f, 2.5f, 4.0f, 5.2f, 4.5f, 2.8f, 3.5f)),
    CONCERT_HALL(listOf(3.0f, 3.5f, 2.0f, 0.5f, -0.5f, 0.5f, 2.0f, 3.2f, 3.8f, 4.0f)),
    LATE_NIGHT(listOf(-2.5f, -1.0f, 1.0f, 2.0f, 2.2f, 2.0f, 1.5f, 0.5f, -1.5f, -3.0f)),
    ELECTRONIC_PUNCH(listOf(6.0f, 5.5f, 2.5f, -1.5f, -2.0f, 0.5f, 2.5f, 4.5f, 5.5f, 6.0f)),
    LOFI_VELVET(listOf(2.0f, 3.5f, 2.5f, 1.5f, 0.5f, -1.0f, -2.5f, -4.0f, -5.5f, -6.5f)),
    CUSTOM(listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)),
}

enum class HeadphoneTuningProfile {
    NEUTRAL_STUDIO, OPEN_BACK_HIFI, IEM_IN_EAR, BASS_HEAD_CLUB, SPEAKER_COMPENSATION
}

enum class AutoStudioCategory {
    QURAN_SANCTUARY,
    LOSSLESS_MASTERING,
    VOCAL_BROADCAST,
    ACOUSTIC_WARMTH,
    ELECTRONIC_STAGE,
    CINEMATIC_WIDE,
    BALANCED_STUDIO,
}

data class AutoStudioProfile(
    val category: AutoStudioCategory,
    val tenBandGainsDb: List<Float>,
    val tubeWarmth: Float,
    val stereoWidth: Float,
    val subBassPunch: Float,
    val airBrilliance: Float,
    val vocalClarity: Float,
    val compressorStrength: Float,
    val crossfeedEnabled: Boolean,
    val rumbleFilterHz: Int,
    val reverbEnvironment: ReverbEnvironment,
    val confidencePercent: Int = 94,
    val summaryReason: String = "",
)

data class CustomStudioPreset(
    val id: String,
    val name: String,
    val tenBandGainsDb: List<Float>,
    val tubeWarmth: Float = 0f,
    val stereoWidth: Float = 1.0f,
    val subBassPunch: Float = 0f,
    val airBrilliance: Float = 0f,
    val vocalClarity: Float = 0f,
    val compressorStrength: Float = 0f,
    val crossfeedEnabled: Boolean = false,
    val nightModeEnabled: Boolean = false,
    val rumbleFilterHz: Int = 0,
    val headphoneProfile: HeadphoneTuningProfile = HeadphoneTuningProfile.NEUTRAL_STUDIO,
    val reverbEnvironment: ReverbEnvironment = ReverbEnvironment.NONE,
    val createdAtMs: Long = System.currentTimeMillis(),
)

data class StudioAudioConfig(
    val proEqEnabled: Boolean = true,
    val tenBandGainsDb: List<Float> = ProEqPreset.FLAT.gainsDb,
    val tubeWarmth: Float = 0f,
    val stereoWidth: Float = 1.0f,
    val crossfeedEnabled: Boolean = false,
    val subBassPunch: Float = 0f,
    val airBrilliance: Float = 0f,
    val vocalClarity: Float = 0f,
    val compressorStrength: Float = 0f,
    val truePeakLimiterEnabled: Boolean = true,
    val nightModeEnabled: Boolean = false,
    val rumbleFilterHz: Int = 0,
    val channelSwapEnabled: Boolean = false,
    val headphoneProfile: HeadphoneTuningProfile = HeadphoneTuningProfile.NEUTRAL_STUDIO,
    val bypassEnabled: Boolean = false,
)

enum class MediaCardStyle {
    GLASS_CARD, ELEVATED_M3, MINIMAL_OUTLINE, VIBRANT_TONAL
}

enum class BackdropAnimationStyle {
    AURORA_ORBS, SUBTLE_MIST, PRISM_RIBBON, PURE_CLEAN
}

enum class MotionFeelStyle {
    BOUNCY_EXPRESSIVE, SMOOTH_FLUID, SNAPPY_FAST, REDUCED_MOTION
}

enum class HeaderTitleStyle {
    EXPRESSIVE_BOLD, MINIMAL_COMPACT, CENTERED_ELEGANT
}

enum class SettingsLayoutMode {
    HORIZONTAL_PAGES, VERTICAL_LIST
}

enum class EdgeDockSide {
    RIGHT, LEFT
}

enum class SpectrumVisualStyle {
    CLASSIC_BARS, WAVE_PULSE, MINIMAL_DOTS
}

enum class ReverbEnvironment {
    NONE, STUDIO, HALL, MOSQUE, SPATIAL_3D
}

enum class AudioOutputProfileType {
    SPEAKER, WIRED_HEADSET, BLUETOOTH
}

data class AudioDeviceProfile(
    val type: AudioOutputProfileType,
    val eqPreset: EqPreset = EqPreset.FLAT,
    val bassBoost: Float = 0f,
    val reverb: ReverbEnvironment = ReverbEnvironment.NONE,
)

enum class MusicBrowseMode {
    TRACKS, ARTISTS, ALBUMS
}

enum class PhotoCropAspect {
    ORIGINAL, SQUARE_1_1, CLASSIC_4_3, WIDE_16_9
}

enum class PhotoArtFilter {
    ORIGINAL, VIVID, WARM, COOL, NOIR, EMERALD
}

data class MediaNote(
    val mediaId: String,
    val mediaTitle: String,
    val mediaType: RenameMediaType,
    val noteText: String,
    val timestampMs: Long = 0L,
    val updatedAtMs: Long = System.currentTimeMillis(),
)

data class TrashedMediaItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val mediaType: RenameMediaType,
    val deletedAtMs: Long = System.currentTimeMillis(),
    val audioTrack: AudioTrack? = null,
    val videoItem: VideoItem? = null,
    val photoItem: PhotoItem? = null,
)

data class PlaybackUiState(
    val currentTrack: AudioTrack? = null,
    val isPlaying: Boolean = false,
    val durationMs: Long = 0L,
    val isPlayerExpanded: Boolean = false,
    val sleepTimerRemainingSec: Int = 0,
    val sleepTimerTotalMin: Int = 0,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val playbackSpeed: Float = 1.0f,
    val abLoopStartMs: Long? = null,
    val abLoopEndMs: Long? = null,
    val hifzRemainingLoops: Int = 0,
    val isMuted: Boolean = false,
)
