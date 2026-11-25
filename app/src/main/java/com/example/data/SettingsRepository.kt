package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.AccentPalette
import com.example.model.AppIconVariant
import com.example.model.AppLanguage
import com.example.model.AppSettings
import com.example.model.AudioTrack
import com.example.model.EdgeDockSide
import com.example.model.EqPreset
import com.example.model.FolderCustomization
import com.example.model.IconPackStyle
import com.example.model.IntruderAttempt
import com.example.model.LiquidGlassStyle
import com.example.model.MaterialContrastLevel
import com.example.model.MaterialFontStyle
import com.example.model.MediaBookmark
import com.example.model.MediaTab
import com.example.model.MediaViewMode
import com.example.model.MorningAlarmConfig
import com.example.model.NavBarStyle
import com.example.model.NavTransitionStyle
import com.example.model.PhotoItem
import com.example.model.PlayerArtworkStyle
import com.example.model.QuranKhatmaProgress
import com.example.model.SettingsLayoutMode
import com.example.model.SlideshowTransitionStyle
import com.example.model.SpectrumVisualStyle
import com.example.model.SubtitleColorStyle
import com.example.model.SurfaceDepthStyle
import com.example.model.TrackTagOverride
import com.example.model.UiDensity
import com.example.model.UserPlaylist
import com.example.model.VideoItem
import com.example.model.AudioDeviceProfile
import com.example.model.AudioOutputProfileType
import com.example.model.MediaNote
import com.example.model.RenameMediaType
import com.example.model.ReverbEnvironment
import com.example.model.WatchedFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "prism_media_prefs")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val AMOLED_BLACK = booleanPreferencesKey("amoled_black")
        val ACCENT_PALETTE = stringPreferencesKey("accent_palette")
        val ICON_STYLE = stringPreferencesKey("icon_style")
        val LANGUAGE = stringPreferencesKey("language")
        val AUTO_PLAY_NEXT = booleanPreferencesKey("auto_play_next")
        val RESUME_PLAYBACK = booleanPreferencesKey("resume_playback")
        val FILTER_SHORT_AUDIO = booleanPreferencesKey("filter_short_audio")
        val AUTO_PIP_ENABLED = booleanPreferencesKey("auto_pip_enabled")
        val EQ_ENABLED = booleanPreferencesKey("eq_enabled")
        val BASS_BOOST = floatPreferencesKey("bass_boost")
        val EQ_PRESET = stringPreferencesKey("eq_preset")
        val EQ_GAINS = stringPreferencesKey("eq_gains")
        val FAVORITE_IDS = stringSetPreferencesKey("favorite_ids")
        val SAVED_POSITIONS = stringPreferencesKey("saved_positions")
        val CUSTOM_TITLES = stringPreferencesKey("custom_titles")
        val PLAYLISTS_JSON = stringPreferencesKey("playlists_json")
        val TREE_URIS = stringSetPreferencesKey("tree_uris")
        val PICKED_FILE_URIS = stringSetPreferencesKey("picked_file_uris")
        val DEMO_LOADED = booleanPreferencesKey("demo_loaded")
        val SQUIGGLY_PROGRESS = booleanPreferencesKey("squiggly_progress")
        val MINI_PLAYER_COMPACT = booleanPreferencesKey("mini_player_compact")
        val MINI_PLAYER_FLOATING = booleanPreferencesKey("mini_player_floating")
        val BAR_SHOW_SPECTRUM = booleanPreferencesKey("bar_show_spectrum")
        val BAR_SHOW_SEEK10 = booleanPreferencesKey("bar_show_seek10")
        val BAR_SHOW_FAVORITE = booleanPreferencesKey("bar_show_favorite")
        val BAR_SHOW_SPEED = booleanPreferencesKey("bar_show_speed")
        val BAR_SHOW_AB_LOOP = booleanPreferencesKey("bar_show_ab_loop")
        val BAR_SHOW_EQ = booleanPreferencesKey("bar_show_eq")
        val BAR_SHOW_TIMER = booleanPreferencesKey("bar_show_timer")
        val BAR_SHOW_QUEUE = booleanPreferencesKey("bar_show_queue")
        val FONT_SCALE = floatPreferencesKey("font_scale_multiplier")
        val SHOW_LYRICS = booleanPreferencesKey("show_lyrics_in_player")
        val VIDEO_DEFAULT_ASPECT = intPreferencesKey("video_default_aspect")
        val VIDEO_DEFAULT_SPEED = floatPreferencesKey("video_default_speed")
        val VIDEO_DEFAULT_LOOP = booleanPreferencesKey("video_default_loop")
        val PHOTO_GRID_COLUMNS = intPreferencesKey("photo_grid_columns")
        val PHOTO_SLIDESHOW_SEC = intPreferencesKey("photo_slideshow_seconds")
        val PHOTO_SHOW_TITLES = booleanPreferencesKey("photo_show_titles")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val TAMPER_GUARD_ENABLED = booleanPreferencesKey("tamper_guard_enabled")
        val TRACK_LYRICS_JSON = stringPreferencesKey("track_lyrics_json")
        val PINNED_APK_SHA256 = stringPreferencesKey("pinned_apk_sha256")
        val MUSIC_VIEW_MODE = stringPreferencesKey("music_view_mode")
        val VIDEO_VIEW_MODE = stringPreferencesKey("video_view_mode")
        val PHOTO_VIEW_MODE = stringPreferencesKey("photo_view_mode")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val APP_LOCK_ON_BG = booleanPreferencesKey("app_lock_on_background")
        val SCREEN_PRIVACY_ENABLED = booleanPreferencesKey("screen_privacy_enabled")
        val APP_LOCK_PIN_HASH = stringPreferencesKey("app_lock_pin_hash")
        val APP_LOCK_PIN_SALT = stringPreferencesKey("app_lock_pin_salt")
        val INTRUDER_LOG_JSON = stringPreferencesKey("intruder_log_json")
        val WATCHED_FOLDERS_JSON = stringPreferencesKey("watched_folders_json")
        val FOLDER_SETUP_COMPLETED = booleanPreferencesKey("folder_setup_completed")
        val AUTO_SCAN_WATCHED_FOLDERS = booleanPreferencesKey("auto_scan_watched_folders")
        val PLAY_STATS_JSON = stringPreferencesKey("play_stats_json")
        val TAG_OVERRIDES_JSON = stringPreferencesKey("tag_overrides_json")
        val BOOKMARKS_JSON = stringPreferencesKey("bookmarks_json")
        val LIQUID_GLASS_STYLE = stringPreferencesKey("liquid_glass_style")
        val LIQUID_GLASS_INTENSITY = floatPreferencesKey("liquid_glass_intensity")
        val SHAPE_ROUNDNESS = floatPreferencesKey("shape_roundness")
        val NAV_TRANSITION_STYLE = stringPreferencesKey("nav_transition_style")
        val CONTRAST_LEVEL = stringPreferencesKey("contrast_level")
        val EDGE_GLOW_ENABLED = booleanPreferencesKey("edge_glow_enabled")
        val AUDIO_PREAMP_PERCENT = intPreferencesKey("audio_preamp_percent")
        val AUDIO_PITCH = floatPreferencesKey("audio_pitch")
        val CROSSFADE_SECONDS = intPreferencesKey("crossfade_seconds")
        val FONT_STYLE = stringPreferencesKey("material_font_style")
        val HARDWARE_VIDEO_ACCEL = booleanPreferencesKey("hardware_video_accel")
        val PINNED_DEX_SHA256 = stringPreferencesKey("pinned_dex_sha256")
        val BIOMETRIC_UNLOCK_ENABLED = booleanPreferencesKey("biometric_unlock_enabled")
        val NAV_BAR_STYLE = stringPreferencesKey("nav_bar_style")
        val STARTUP_TAB = stringPreferencesKey("startup_tab")
        val UI_DENSITY = stringPreferencesKey("ui_density")
        val PLAYER_ARTWORK_STYLE = stringPreferencesKey("player_artwork_style")
        val ALBUM_ART_TINT_ENABLED = booleanPreferencesKey("album_art_tint_enabled")
        val FULL_PLAYER_SPECTRUM = booleanPreferencesKey("full_player_spectrum")
        val SEEK_STEP_SECONDS = intPreferencesKey("seek_step_seconds")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val AUTO_ONLINE_ENRICHMENT = booleanPreferencesKey("auto_online_enrichment")
        val AUTO_FETCH_LYRICS = booleanPreferencesKey("auto_fetch_lyrics")
        val AUTO_FETCH_ARTIST_PHOTOS = booleanPreferencesKey("auto_fetch_artist_photos")
        val GAPLESS_PLAYBACK = booleanPreferencesKey("gapless_playback")
        val AUDIO_NORMALIZATION = booleanPreferencesKey("audio_normalization")
        val PAUSE_ON_HEADSET_UNPLUG = booleanPreferencesKey("pause_on_headset_unplug")
        val VIDEO_GESTURES_ENABLED = booleanPreferencesKey("video_gestures_enabled")
        val VIDEO_DOUBLE_TAP_SEEK = booleanPreferencesKey("video_double_tap_seek")
        val VIDEO_REMEMBER_POSITION = booleanPreferencesKey("video_remember_position")
        val VIDEO_SUBTITLE_SIZE_SP = intPreferencesKey("video_subtitle_size_sp")
        val PHOTO_AUTO_SHOW_EXIF = booleanPreferencesKey("photo_auto_show_exif")
        val PHOTO_HIGH_RES_DECODE = booleanPreferencesKey("photo_high_res_decode")
        val PHOTO_SLIDESHOW_TRANSITION = stringPreferencesKey("photo_slideshow_transition")
        val SURFACE_DEPTH_STYLE = stringPreferencesKey("surface_depth_style")
        val KHUSHU_MODE = booleanPreferencesKey("khushu_mode_enabled")
        val LECTURE_MODE = booleanPreferencesKey("lecture_mode_enabled")
        val SKIP_SILENCE = booleanPreferencesKey("skip_silence_enabled")
        val AUDIO_BALANCE = floatPreferencesKey("audio_balance")
        val AUDIO_MONO = booleanPreferencesKey("audio_mono_enabled")
        val SHAKE_TO_SHUFFLE = booleanPreferencesKey("shake_to_shuffle_enabled")
        val STRIP_EXIF_ON_SHARE = booleanPreferencesKey("strip_exif_on_share")
        val CUSTOM_SEED_COLOR = intPreferencesKey("custom_seed_color_argb")
        val APP_ICON_VARIANT = stringPreferencesKey("app_icon_variant")
        val VIDEO_SUB_COLOR = stringPreferencesKey("video_sub_color")
        val VIDEO_SUB_BG_OPACITY = floatPreferencesKey("video_sub_bg_opacity")
        val VIDEO_SUB_BOTTOM_OFFSET = intPreferencesKey("video_sub_bottom_offset")
        val TASBIH_DAILY_COUNT = intPreferencesKey("tasbih_daily_count")
        val QURAN_KHATMA_JSON = stringPreferencesKey("quran_khatma_json")
        val MORNING_ALARM_JSON = stringPreferencesKey("morning_alarm_json")
        val FOLDER_CUSTOM_JSON = stringPreferencesKey("folder_custom_json")
        val DAILY_MINUTES_JSON = stringPreferencesKey("daily_minutes_json")
        val SETTINGS_LAYOUT_MODE = stringPreferencesKey("settings_layout_mode")
        val MINI_PLAYER_EDGE_DOCK_SIDE = stringPreferencesKey("mini_player_edge_dock_side")
        val SPECTRUM_VISUAL_STYLE = stringPreferencesKey("spectrum_visual_style")
        val LYRICS_FONT_SIZE_SP = intPreferencesKey("lyrics_font_size_sp")
        val DRIVE_MODE_ENABLED = booleanPreferencesKey("drive_mode_enabled")
        val PLAYER_GESTURE_SWIPE_ART = booleanPreferencesKey("player_gesture_swipe_art")
        val VIDEO_MIRROR_ENABLED = booleanPreferencesKey("video_mirror_enabled")
        val VIDEO_AUTO_HIDE_SECONDS = intPreferencesKey("video_auto_hide_seconds")
        val REVERB_ENVIRONMENT = stringPreferencesKey("reverb_environment")
        val AUTO_AUDIO_DEVICE_PROFILES = booleanPreferencesKey("auto_audio_device_profiles")
        val VIDEO_AUTO_NEXT_ENABLED = booleanPreferencesKey("video_auto_next_enabled")
        val NAV_TAB_ORDER = stringPreferencesKey("nav_tab_order")
        val HEADER_SHOW_STREAM_BTN = booleanPreferencesKey("header_show_stream_btn")
        val HEADER_SHOW_MULTI_WINDOW_BTN = booleanPreferencesKey("header_show_multi_window_btn")
        val ADHKAR_COMPLETED_KEYS = stringSetPreferencesKey("adhkar_completed_keys")
        val MEDIA_NOTES_JSON = stringPreferencesKey("media_notes_json")
        val DEVICE_AUDIO_PROFILES_JSON = stringPreferencesKey("device_audio_profiles_json")
        val RECYCLE_BIN_JSON = stringPreferencesKey("recycle_bin_json")
        val PLAYER_GENRE_DYNAMIC_COLORS = booleanPreferencesKey("player_genre_dynamic_colors")
        val PLAYER_SHOW_TECH_TELEMETRY = booleanPreferencesKey("player_show_tech_telemetry")
        val PLAYER_EXTRAS_BUTTON_SUBTLE = booleanPreferencesKey("player_extras_button_subtle")
        val PLAYER_DIRECT_BIT_EXACT_DSP = booleanPreferencesKey("player_direct_bit_exact_dsp")
        val PLAYER_EXPRESSIVE_PLAY_MORPH = booleanPreferencesKey("player_expressive_play_morph")
        val LOCK_SCREEN_CONTROLS_ENABLED = booleanPreferencesKey("lock_screen_controls_enabled")
        val LOCK_SCREEN_SHOW_ARTWORK = booleanPreferencesKey("lock_screen_show_artwork")
        val LOCK_SCREEN_SHOW_IDENTITY = booleanPreferencesKey("lock_screen_show_identity")
        val LOCK_SCREEN_FULL_SCREEN_ENABLED = booleanPreferencesKey("lock_screen_full_screen_enabled")
        val LOCK_SCREEN_SHOW_CLOCK = booleanPreferencesKey("lock_screen_show_clock")
        val LOCK_SCREEN_SHOW_LYRICS = booleanPreferencesKey("lock_screen_show_lyrics")
        val LOCK_SCREEN_BLUR_BACKDROP = booleanPreferencesKey("lock_screen_blur_backdrop")
        val LOCK_SCREEN_COMPACT_MODE = booleanPreferencesKey("lock_screen_compact_mode")
        val PIP_MUSIC_ENABLED = booleanPreferencesKey("pip_music_enabled")
        val PIP_VIDEO_ENABLED = booleanPreferencesKey("pip_video_enabled")
        val PIP_PHOTO_ENABLED = booleanPreferencesKey("pip_photo_enabled")
        val CARD_STYLE = stringPreferencesKey("card_style")
        val BACKDROP_STYLE = stringPreferencesKey("backdrop_style")
        val MOTION_FEEL_STYLE = stringPreferencesKey("motion_feel_style")
        val HEADER_TITLE_STYLE = stringPreferencesKey("header_title_style")
        val SWIPE_GESTURES_ENABLED = booleanPreferencesKey("swipe_gestures_enabled")
        val SHOW_FORMAT_BADGES = booleanPreferencesKey("show_format_badges")
        val ACCENT_STRENGTH = floatPreferencesKey("accent_strength")
        // Beta 4.4 Pro Studio Mastering Engine
        val PRO_EQ_ENABLED = booleanPreferencesKey("pro_eq_enabled")
        val PRO_EQ_PRESET = stringPreferencesKey("pro_eq_preset")
        val PRO_TEN_BAND_GAINS = stringPreferencesKey("pro_ten_band_gains")
        val AUTO_STUDIO_MASTERING = booleanPreferencesKey("auto_studio_mastering")
        val TUBE_WARMTH = floatPreferencesKey("tube_warmth")
        val STEREO_WIDTH = floatPreferencesKey("stereo_width")
        val CROSSFEED_ENABLED = booleanPreferencesKey("crossfeed_enabled")
        val SUB_BASS_PUNCH = floatPreferencesKey("sub_bass_punch")
        val AIR_BRILLIANCE = floatPreferencesKey("air_brilliance")
        val VOCAL_CLARITY = floatPreferencesKey("vocal_clarity")
        val COMPRESSOR_STRENGTH = floatPreferencesKey("compressor_strength")
        val TRUE_PEAK_LIMITER = booleanPreferencesKey("true_peak_limiter")
        val NIGHT_MODE_ENABLED = booleanPreferencesKey("night_mode_enabled")
        val RUMBLE_FILTER_HZ = intPreferencesKey("rumble_filter_hz")
        val CHANNEL_SWAP_ENABLED = booleanPreferencesKey("channel_swap_enabled")
        val HEADPHONE_PROFILE = stringPreferencesKey("headphone_profile")
        val DSP_BYPASS_ENABLED = booleanPreferencesKey("dsp_bypass_enabled")
        val CUSTOM_STUDIO_PRESETS_JSON = stringPreferencesKey("custom_studio_presets_json")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val preset = runCatching {
            EqPreset.valueOf(prefs[Keys.EQ_PRESET] ?: EqPreset.FLAT.name)
        }.getOrDefault(EqPreset.FLAT)

        val gainsRaw = prefs[Keys.EQ_GAINS]
        val gains = if (!gainsRaw.isNullOrBlank()) {
            gainsRaw.split(",").mapNotNull { it.toFloatOrNull() }.takeIf { it.size == 5 } ?: preset.gainsDb
        } else {
            preset.gainsDb
        }

        AppSettings(
            darkTheme = prefs[Keys.DARK_THEME] ?: true,
            amoledBlack = prefs[Keys.AMOLED_BLACK] ?: false,
            accentPalette = runCatching {
                AccentPalette.valueOf(prefs[Keys.ACCENT_PALETTE] ?: AccentPalette.SYSTEM.name)
            }.getOrDefault(AccentPalette.SYSTEM),
            iconStyle = runCatching {
                IconPackStyle.valueOf(prefs[Keys.ICON_STYLE] ?: IconPackStyle.GOOGLE_ROUNDED.name)
            }.getOrDefault(IconPackStyle.GOOGLE_ROUNDED),
            language = runCatching {
                AppLanguage.valueOf(prefs[Keys.LANGUAGE] ?: AppLanguage.EN.name)
            }.getOrDefault(AppLanguage.EN),
            fontStyle = runCatching {
                MaterialFontStyle.valueOf(prefs[Keys.FONT_STYLE] ?: MaterialFontStyle.MATERIAL_EXPRESSIVE.name)
            }.getOrDefault(MaterialFontStyle.MATERIAL_EXPRESSIVE),
            fontScaleMultiplier = (prefs[Keys.FONT_SCALE] ?: 1.0f).coerceIn(0.85f, 1.30f),
            hardwareVideoAcceleration = prefs[Keys.HARDWARE_VIDEO_ACCEL] ?: true,
            musicViewMode = runCatching {
                MediaViewMode.valueOf(prefs[Keys.MUSIC_VIEW_MODE] ?: MediaViewMode.LIST.name)
            }.getOrDefault(MediaViewMode.LIST),
            videoViewMode = runCatching {
                MediaViewMode.valueOf(prefs[Keys.VIDEO_VIEW_MODE] ?: MediaViewMode.GRID.name)
            }.getOrDefault(MediaViewMode.GRID),
            photoViewMode = runCatching {
                MediaViewMode.valueOf(prefs[Keys.PHOTO_VIEW_MODE] ?: MediaViewMode.GRID.name)
            }.getOrDefault(MediaViewMode.GRID),
            autoPlayNext = prefs[Keys.AUTO_PLAY_NEXT] ?: true,
            resumePlaybackPosition = prefs[Keys.RESUME_PLAYBACK] ?: true,
            filterShortAudio = prefs[Keys.FILTER_SHORT_AUDIO] ?: true,
            showLyricsInPlayer = prefs[Keys.SHOW_LYRICS] ?: true,
            eqEnabled = prefs[Keys.EQ_ENABLED] ?: true,
            bassBoost = (prefs[Keys.BASS_BOOST] ?: 0f).coerceIn(0f, 1f),
            eqPreset = preset,
            eqGainsDb = gains,
            autoPipEnabled = prefs[Keys.AUTO_PIP_ENABLED] ?: true,
            videoDefaultAspect = (prefs[Keys.VIDEO_DEFAULT_ASPECT] ?: 0).coerceIn(0, 2),
            videoDefaultSpeed = (prefs[Keys.VIDEO_DEFAULT_SPEED] ?: 1.0f).coerceIn(0.75f, 2.0f),
            videoDefaultLoop = prefs[Keys.VIDEO_DEFAULT_LOOP] ?: false,
            photoGridColumns = (prefs[Keys.PHOTO_GRID_COLUMNS] ?: 0).coerceIn(0, 3),
            photoSlideshowSeconds = (prefs[Keys.PHOTO_SLIDESHOW_SEC] ?: 3).coerceIn(2, 10),
            photoShowTitles = prefs[Keys.PHOTO_SHOW_TITLES] ?: true,
            folderSetupCompleted = prefs[Keys.FOLDER_SETUP_COMPLETED] ?: false,
            autoScanWatchedFolders = prefs[Keys.AUTO_SCAN_WATCHED_FOLDERS] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            tamperGuardEnabled = prefs[Keys.TAMPER_GUARD_ENABLED] ?: true,
            appLockEnabled = prefs[Keys.APP_LOCK_ENABLED] ?: false,
            appLockOnBackground = prefs[Keys.APP_LOCK_ON_BG] ?: true,
            biometricUnlockEnabled = prefs[Keys.BIOMETRIC_UNLOCK_ENABLED] ?: false,
            screenPrivacyEnabled = prefs[Keys.SCREEN_PRIVACY_ENABLED] ?: false,
            squigglyProgress = prefs[Keys.SQUIGGLY_PROGRESS] ?: true,
            miniPlayerCompact = prefs[Keys.MINI_PLAYER_COMPACT] ?: false,
            miniPlayerFloating = prefs[Keys.MINI_PLAYER_FLOATING] ?: false,
            barShowSpectrum = prefs[Keys.BAR_SHOW_SPECTRUM] ?: true,
            barShowSeek10 = prefs[Keys.BAR_SHOW_SEEK10] ?: true,
            barShowFavorite = prefs[Keys.BAR_SHOW_FAVORITE] ?: true,
            barShowSpeed = prefs[Keys.BAR_SHOW_SPEED] ?: true,
            barShowAbLoop = prefs[Keys.BAR_SHOW_AB_LOOP] ?: true,
            barShowEq = prefs[Keys.BAR_SHOW_EQ] ?: true,
            barShowTimer = prefs[Keys.BAR_SHOW_TIMER] ?: true,
            barShowQueue = prefs[Keys.BAR_SHOW_QUEUE] ?: true,
            liquidGlassStyle = runCatching {
                LiquidGlassStyle.valueOf(prefs[Keys.LIQUID_GLASS_STYLE] ?: LiquidGlassStyle.MATERIAL_LIQUID.name)
            }.getOrDefault(LiquidGlassStyle.MATERIAL_LIQUID),
            liquidGlassIntensity = (prefs[Keys.LIQUID_GLASS_INTENSITY] ?: 0.55f).coerceIn(0f, 1f),
            shapeRoundness = (prefs[Keys.SHAPE_ROUNDNESS] ?: 1.0f).coerceIn(0.6f, 1.4f),
            navTransitionStyle = runCatching {
                NavTransitionStyle.valueOf(prefs[Keys.NAV_TRANSITION_STYLE] ?: NavTransitionStyle.EXPRESSIVE_SPRING.name)
            }.getOrDefault(NavTransitionStyle.EXPRESSIVE_SPRING),
            contrastLevel = runCatching {
                MaterialContrastLevel.valueOf(prefs[Keys.CONTRAST_LEVEL] ?: MaterialContrastLevel.STANDARD.name)
            }.getOrDefault(MaterialContrastLevel.STANDARD),
            edgeGlowEnabled = prefs[Keys.EDGE_GLOW_ENABLED] ?: true,
            audioPreampPercent = (prefs[Keys.AUDIO_PREAMP_PERCENT] ?: 100).coerceIn(100, 200),
            audioPitch = (prefs[Keys.AUDIO_PITCH] ?: 1.0f).coerceIn(0.75f, 1.50f),
            crossfadeSeconds = (prefs[Keys.CROSSFADE_SECONDS] ?: 0).coerceIn(0, 12),
            navBarStyle = runCatching {
                NavBarStyle.valueOf(prefs[Keys.NAV_BAR_STYLE] ?: NavBarStyle.FLOATING_PILL.name)
            }.getOrDefault(NavBarStyle.FLOATING_PILL),
            startupTab = runCatching {
                MediaTab.valueOf(prefs[Keys.STARTUP_TAB] ?: MediaTab.MUSIC.name)
            }.getOrDefault(MediaTab.MUSIC),
            uiDensity = runCatching {
                UiDensity.valueOf(prefs[Keys.UI_DENSITY] ?: UiDensity.BALANCED.name)
            }.getOrDefault(UiDensity.BALANCED),
            playerArtworkStyle = runCatching {
                PlayerArtworkStyle.valueOf(prefs[Keys.PLAYER_ARTWORK_STYLE] ?: PlayerArtworkStyle.VINYL_DISC.name)
            }.getOrDefault(PlayerArtworkStyle.VINYL_DISC),
            albumArtTintEnabled = prefs[Keys.ALBUM_ART_TINT_ENABLED] ?: true,
            showFullPlayerSpectrum = prefs[Keys.FULL_PLAYER_SPECTRUM] ?: true,
            seekStepSeconds = (prefs[Keys.SEEK_STEP_SECONDS] ?: 10).coerceIn(5, 30),
            hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true,
            autoOnlineEnrichment = prefs[Keys.AUTO_ONLINE_ENRICHMENT] ?: true,
            autoFetchLyrics = prefs[Keys.AUTO_FETCH_LYRICS] ?: true,
            autoFetchArtistPhotos = prefs[Keys.AUTO_FETCH_ARTIST_PHOTOS] ?: true,
            gaplessPlayback = prefs[Keys.GAPLESS_PLAYBACK] ?: true,
            audioNormalization = prefs[Keys.AUDIO_NORMALIZATION] ?: false,
            pauseOnHeadsetUnplug = prefs[Keys.PAUSE_ON_HEADSET_UNPLUG] ?: true,
            videoGesturesEnabled = prefs[Keys.VIDEO_GESTURES_ENABLED] ?: true,
            videoDoubleTapSeekEnabled = prefs[Keys.VIDEO_DOUBLE_TAP_SEEK] ?: true,
            videoRememberPosition = prefs[Keys.VIDEO_REMEMBER_POSITION] ?: true,
            videoSubtitleSizeSp = (prefs[Keys.VIDEO_SUBTITLE_SIZE_SP] ?: 18).coerceIn(14, 28),
            photoAutoShowExif = prefs[Keys.PHOTO_AUTO_SHOW_EXIF] ?: true,
            photoHighResDecode = prefs[Keys.PHOTO_HIGH_RES_DECODE] ?: true,
            photoSlideshowTransition = runCatching {
                SlideshowTransitionStyle.valueOf(
                    prefs[Keys.PHOTO_SLIDESHOW_TRANSITION] ?: SlideshowTransitionStyle.FADE.name
                )
            }.getOrDefault(SlideshowTransitionStyle.FADE),
            surfaceDepthStyle = runCatching {
                SurfaceDepthStyle.valueOf(
                    prefs[Keys.SURFACE_DEPTH_STYLE] ?: SurfaceDepthStyle.EXPRESSIVE.name
                )
            }.getOrDefault(SurfaceDepthStyle.EXPRESSIVE),
            khushuModeEnabled = prefs[Keys.KHUSHU_MODE] ?: false,
            lectureModeEnabled = prefs[Keys.LECTURE_MODE] ?: false,
            skipSilenceEnabled = prefs[Keys.SKIP_SILENCE] ?: false,
            audioBalance = (prefs[Keys.AUDIO_BALANCE] ?: 0f).coerceIn(-1f, 1f),
            audioMonoEnabled = prefs[Keys.AUDIO_MONO] ?: false,
            shakeToShuffleEnabled = prefs[Keys.SHAKE_TO_SHUFFLE] ?: false,
            stripExifOnShare = prefs[Keys.STRIP_EXIF_ON_SHARE] ?: true,
            customSeedColorArgb = prefs[Keys.CUSTOM_SEED_COLOR],
            appIconVariant = runCatching {
                AppIconVariant.valueOf(prefs[Keys.APP_ICON_VARIANT] ?: AppIconVariant.CLASSIC.name)
            }.getOrDefault(AppIconVariant.CLASSIC),
            videoSubtitleColor = runCatching {
                SubtitleColorStyle.valueOf(prefs[Keys.VIDEO_SUB_COLOR] ?: SubtitleColorStyle.WHITE.name)
            }.getOrDefault(SubtitleColorStyle.WHITE),
            videoSubtitleBgOpacity = (prefs[Keys.VIDEO_SUB_BG_OPACITY] ?: 0.72f).coerceIn(0f, 1f),
            videoSubtitleBottomOffsetDp = (prefs[Keys.VIDEO_SUB_BOTTOM_OFFSET] ?: 24).coerceIn(8, 96),
            tasbihDailyCount = (prefs[Keys.TASBIH_DAILY_COUNT] ?: 0).coerceAtLeast(0),
            settingsLayoutMode = runCatching {
                SettingsLayoutMode.valueOf(
                    prefs[Keys.SETTINGS_LAYOUT_MODE] ?: SettingsLayoutMode.HORIZONTAL_PAGES.name
                )
            }.getOrDefault(SettingsLayoutMode.HORIZONTAL_PAGES),
            miniPlayerEdgeDockSide = runCatching {
                EdgeDockSide.valueOf(
                    prefs[Keys.MINI_PLAYER_EDGE_DOCK_SIDE] ?: EdgeDockSide.RIGHT.name
                )
            }.getOrDefault(EdgeDockSide.RIGHT),
            spectrumVisualStyle = runCatching {
                SpectrumVisualStyle.valueOf(
                    prefs[Keys.SPECTRUM_VISUAL_STYLE] ?: SpectrumVisualStyle.CLASSIC_BARS.name
                )
            }.getOrDefault(SpectrumVisualStyle.CLASSIC_BARS),
            lyricsFontSizeSp = (prefs[Keys.LYRICS_FONT_SIZE_SP] ?: 16).coerceIn(13, 24),
            driveModeEnabled = prefs[Keys.DRIVE_MODE_ENABLED] ?: false,
            playerGestureSwipeArt = prefs[Keys.PLAYER_GESTURE_SWIPE_ART] ?: true,
            videoMirrorEnabled = prefs[Keys.VIDEO_MIRROR_ENABLED] ?: false,
            videoAutoHideSeconds = (prefs[Keys.VIDEO_AUTO_HIDE_SECONDS] ?: 4).coerceIn(0, 10),
            reverbEnvironment = runCatching {
                ReverbEnvironment.valueOf(prefs[Keys.REVERB_ENVIRONMENT] ?: ReverbEnvironment.NONE.name)
            }.getOrDefault(ReverbEnvironment.NONE),
            autoAudioDeviceProfiles = prefs[Keys.AUTO_AUDIO_DEVICE_PROFILES] ?: true,
            videoAutoNextEnabled = prefs[Keys.VIDEO_AUTO_NEXT_ENABLED] ?: true,
            navTabOrder = parseNavTabOrder(prefs[Keys.NAV_TAB_ORDER]),
            headerShowStreamBtn = prefs[Keys.HEADER_SHOW_STREAM_BTN] ?: true,
            headerShowMultiWindowBtn = prefs[Keys.HEADER_SHOW_MULTI_WINDOW_BTN] ?: true,
            adhkarCompletedKeys = prefs[Keys.ADHKAR_COMPLETED_KEYS] ?: emptySet(),
            playerGenreDynamicColors = prefs[Keys.PLAYER_GENRE_DYNAMIC_COLORS] ?: true,
            playerShowTechTelemetry = prefs[Keys.PLAYER_SHOW_TECH_TELEMETRY] ?: true,
            playerExtrasButtonSubtle = prefs[Keys.PLAYER_EXTRAS_BUTTON_SUBTLE] ?: true,
            playerDirectBitExactDsp = prefs[Keys.PLAYER_DIRECT_BIT_EXACT_DSP] ?: false,
            playerExpressivePlayMorph = prefs[Keys.PLAYER_EXPRESSIVE_PLAY_MORPH] ?: true,
            lockScreenControlsEnabled = prefs[Keys.LOCK_SCREEN_CONTROLS_ENABLED] ?: true,
            lockScreenShowArtwork = prefs[Keys.LOCK_SCREEN_SHOW_ARTWORK] ?: true,
            lockScreenShowPrismIdentity = prefs[Keys.LOCK_SCREEN_SHOW_IDENTITY] ?: true,
            lockScreenFullScreenEnabled = prefs[Keys.LOCK_SCREEN_FULL_SCREEN_ENABLED] ?: true,
            lockScreenShowClock = prefs[Keys.LOCK_SCREEN_SHOW_CLOCK] ?: true,
            lockScreenShowLyrics = prefs[Keys.LOCK_SCREEN_SHOW_LYRICS] ?: true,
            lockScreenBlurBackdrop = prefs[Keys.LOCK_SCREEN_BLUR_BACKDROP] ?: true,
            lockScreenCompactMode = prefs[Keys.LOCK_SCREEN_COMPACT_MODE] ?: false,
            pipMusicEnabled = prefs[Keys.PIP_MUSIC_ENABLED] ?: false,
            pipVideoEnabled = prefs[Keys.PIP_VIDEO_ENABLED] ?: true,
            pipPhotoEnabled = prefs[Keys.PIP_PHOTO_ENABLED] ?: false,
            cardStyle = runCatching {
                com.example.model.MediaCardStyle.valueOf(
                    prefs[Keys.CARD_STYLE] ?: com.example.model.MediaCardStyle.GLASS_CARD.name
                )
            }.getOrDefault(com.example.model.MediaCardStyle.GLASS_CARD),
            backdropStyle = runCatching {
                com.example.model.BackdropAnimationStyle.valueOf(
                    prefs[Keys.BACKDROP_STYLE] ?: com.example.model.BackdropAnimationStyle.AURORA_ORBS.name
                )
            }.getOrDefault(com.example.model.BackdropAnimationStyle.AURORA_ORBS),
            motionFeelStyle = runCatching {
                com.example.model.MotionFeelStyle.valueOf(
                    prefs[Keys.MOTION_FEEL_STYLE] ?: com.example.model.MotionFeelStyle.BOUNCY_EXPRESSIVE.name
                )
            }.getOrDefault(com.example.model.MotionFeelStyle.BOUNCY_EXPRESSIVE),
            headerTitleStyle = runCatching {
                com.example.model.HeaderTitleStyle.valueOf(
                    prefs[Keys.HEADER_TITLE_STYLE] ?: com.example.model.HeaderTitleStyle.EXPRESSIVE_BOLD.name
                )
            }.getOrDefault(com.example.model.HeaderTitleStyle.EXPRESSIVE_BOLD),
            swipeGesturesEnabled = prefs[Keys.SWIPE_GESTURES_ENABLED] ?: true,
            showFormatBadges = prefs[Keys.SHOW_FORMAT_BADGES] ?: true,
            accentStrength = (prefs[Keys.ACCENT_STRENGTH] ?: 1.0f).coerceIn(0.5f, 1.4f),
            proEqEnabled = prefs[Keys.PRO_EQ_ENABLED] ?: true,
            proEqPreset = runCatching {
                com.example.model.ProEqPreset.valueOf(
                    prefs[Keys.PRO_EQ_PRESET] ?: com.example.model.ProEqPreset.FLAT.name
                )
            }.getOrDefault(com.example.model.ProEqPreset.FLAT),
            proTenBandGainsDb = prefs[Keys.PRO_TEN_BAND_GAINS]
                ?.split(",")
                ?.mapNotNull { it.toFloatOrNull()?.coerceIn(-15f, 15f) }
                ?.takeIf { it.size == 10 }
                ?: com.example.model.ProEqPreset.FLAT.gainsDb,
            autoStudioMasteringEnabled = prefs[Keys.AUTO_STUDIO_MASTERING] ?: false,
            tubeWarmth = (prefs[Keys.TUBE_WARMTH] ?: 0f).coerceIn(0f, 1f),
            stereoWidth = (prefs[Keys.STEREO_WIDTH] ?: 1.0f).coerceIn(0.5f, 2.0f),
            crossfeedEnabled = prefs[Keys.CROSSFEED_ENABLED] ?: false,
            subBassPunch = (prefs[Keys.SUB_BASS_PUNCH] ?: 0f).coerceIn(0f, 1f),
            airBrilliance = (prefs[Keys.AIR_BRILLIANCE] ?: 0f).coerceIn(0f, 1f),
            vocalClarity = (prefs[Keys.VOCAL_CLARITY] ?: 0f).coerceIn(0f, 1f),
            compressorStrength = (prefs[Keys.COMPRESSOR_STRENGTH] ?: 0f).coerceIn(0f, 1f),
            truePeakLimiterEnabled = prefs[Keys.TRUE_PEAK_LIMITER] ?: true,
            nightModeEnabled = prefs[Keys.NIGHT_MODE_ENABLED] ?: false,
            rumbleFilterHz = (prefs[Keys.RUMBLE_FILTER_HZ] ?: 0).coerceIn(0, 80),
            channelSwapEnabled = prefs[Keys.CHANNEL_SWAP_ENABLED] ?: false,
            headphoneProfile = runCatching {
                com.example.model.HeadphoneTuningProfile.valueOf(
                    prefs[Keys.HEADPHONE_PROFILE] ?: com.example.model.HeadphoneTuningProfile.NEUTRAL_STUDIO.name
                )
            }.getOrDefault(com.example.model.HeadphoneTuningProfile.NEUTRAL_STUDIO),
            dspBypassEnabled = prefs[Keys.DSP_BYPASS_ENABLED] ?: false,
        )
    }

    val mediaNotesFlow: Flow<Map<String, MediaNote>> = context.dataStore.data.map { prefs ->
        parseMediaNotesJson(prefs[Keys.MEDIA_NOTES_JSON].orEmpty())
    }

    val audioDeviceProfilesFlow: Flow<Map<AudioOutputProfileType, AudioDeviceProfile>> = context.dataStore.data.map { prefs ->
        parseAudioDeviceProfilesJson(prefs[Keys.DEVICE_AUDIO_PROFILES_JSON].orEmpty())
    }

    val intruderLogFlow: Flow<List<IntruderAttempt>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.INTRUDER_LOG_JSON] ?: return@map emptyList()
        try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    add(
                        IntruderAttempt(
                            timestampMs = obj.optLong("ts", 0L),
                            formattedTime = obj.optString("time", ""),
                            failedCount = obj.optInt("count", 1),
                            note = obj.optString("note", "")
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    val customLyricsFlow: Flow<Map<String, String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.TRACK_LYRICS_JSON] ?: return@map emptyMap()
        try {
            val json = JSONObject(raw)
            buildMap {
                for (key in json.keys()) {
                    put(key, json.optString(key, ""))
                }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    val favoriteIdsFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.FAVORITE_IDS] ?: emptySet()
    }

    val customTitlesFlow: Flow<Map<String, String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.CUSTOM_TITLES] ?: return@map emptyMap()
        try {
            val json = JSONObject(raw)
            buildMap {
                for (key in json.keys()) {
                    put(key, json.optString(key, ""))
                }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    val playlistsFlow: Flow<List<UserPlaylist>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.PLAYLISTS_JSON] ?: return@map emptyList()
        try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    val id = obj.optString("id")
                    val name = obj.optString("name")
                    val idsArr = obj.optJSONArray("trackIds")
                    val trackIds = buildList {
                        if (idsArr != null) {
                            for (j in 0 until idsArr.length()) {
                                add(idsArr.optString(j))
                            }
                        }
                    }
                    if (id.isNotBlank() && name.isNotBlank()) {
                        add(UserPlaylist(id = id, name = name, trackIds = trackIds))
                    }
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getSavedPositions(): Map<String, Long> {
        val raw = context.dataStore.data.first()[Keys.SAVED_POSITIONS] ?: return emptyMap()
        return try {
            val json = JSONObject(raw)
            buildMap {
                for (key in json.keys()) {
                    put(key, json.optLong(key, 0L))
                }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    suspend fun savePlaybackPosition(trackId: String, positionMs: Long) {
        context.dataStore.edit { prefs ->
            val current = try {
                JSONObject(prefs[Keys.SAVED_POSITIONS] ?: "{}")
            } catch (_: Exception) {
                JSONObject()
            }
            current.put(trackId, positionMs)
            prefs[Keys.SAVED_POSITIONS] = current.toString()
        }
    }

    suspend fun saveCustomTitle(mediaId: String, newTitle: String) {
        context.dataStore.edit { prefs ->
            val current = try {
                JSONObject(prefs[Keys.CUSTOM_TITLES] ?: "{}")
            } catch (_: Exception) {
                JSONObject()
            }
            current.put(mediaId, newTitle)
            prefs[Keys.CUSTOM_TITLES] = current.toString()
        }
    }

    suspend fun savePlaylists(playlists: List<UserPlaylist>) {
        context.dataStore.edit { prefs ->
            val arr = JSONArray()
            playlists.forEach { pl ->
                val obj = JSONObject()
                obj.put("id", pl.id)
                obj.put("name", pl.name)
                val idsArr = JSONArray()
                pl.trackIds.forEach { idsArr.put(it) }
                obj.put("trackIds", idsArr)
                arr.put(obj)
            }
            prefs[Keys.PLAYLISTS_JSON] = arr.toString()
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setAmoledBlack(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AMOLED_BLACK] = enabled }
    }

    suspend fun setAccentPalette(palette: AccentPalette) {
        context.dataStore.edit { it[Keys.ACCENT_PALETTE] = palette.name }
    }

    suspend fun setIconStyle(style: IconPackStyle) {
        context.dataStore.edit { it[Keys.ICON_STYLE] = style.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE] = language.name }
    }

    suspend fun setAutoPlayNext(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_PLAY_NEXT] = enabled }
    }

    suspend fun setResumePlayback(enabled: Boolean) {
        context.dataStore.edit { it[Keys.RESUME_PLAYBACK] = enabled }
    }

    suspend fun setFilterShortAudio(enabled: Boolean) {
        context.dataStore.edit { it[Keys.FILTER_SHORT_AUDIO] = enabled }
    }

    suspend fun setAutoPipEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_PIP_ENABLED] = enabled }
    }

    suspend fun setEqEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.EQ_ENABLED] = enabled }
    }

    suspend fun setBassBoost(strength: Float) {
        context.dataStore.edit { it[Keys.BASS_BOOST] = strength.coerceIn(0f, 1f) }
    }

    suspend fun setEqualizer(preset: EqPreset, gains: List<Float>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.EQ_PRESET] = preset.name
            prefs[Keys.EQ_GAINS] = gains.joinToString(",")
        }
    }

    suspend fun setSquigglyProgress(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SQUIGGLY_PROGRESS] = enabled }
    }

    suspend fun setMiniPlayerCompact(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MINI_PLAYER_COMPACT] = enabled }
    }

    suspend fun setMiniPlayerFloating(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MINI_PLAYER_FLOATING] = enabled }
    }

    suspend fun setBarAddon(addonKey: String, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            val key = when (addonKey) {
                "spectrum" -> Keys.BAR_SHOW_SPECTRUM
                "seek10" -> Keys.BAR_SHOW_SEEK10
                "favorite" -> Keys.BAR_SHOW_FAVORITE
                "speed" -> Keys.BAR_SHOW_SPEED
                "ab_loop" -> Keys.BAR_SHOW_AB_LOOP
                "eq" -> Keys.BAR_SHOW_EQ
                "timer" -> Keys.BAR_SHOW_TIMER
                "queue" -> Keys.BAR_SHOW_QUEUE
                else -> return@edit
            }
            prefs[key] = enabled
        }
    }

    suspend fun setFontScaleMultiplier(scale: Float) {
        context.dataStore.edit { it[Keys.FONT_SCALE] = scale.coerceIn(0.85f, 1.30f) }
    }

    suspend fun setLiquidGlassStyle(style: LiquidGlassStyle) {
        context.dataStore.edit { it[Keys.LIQUID_GLASS_STYLE] = style.name }
    }

    suspend fun setLiquidGlassIntensity(intensity: Float) {
        context.dataStore.edit { it[Keys.LIQUID_GLASS_INTENSITY] = intensity.coerceIn(0f, 1f) }
    }

    suspend fun setShapeRoundness(roundness: Float) {
        context.dataStore.edit { it[Keys.SHAPE_ROUNDNESS] = roundness.coerceIn(0.6f, 1.4f) }
    }

    suspend fun setNavTransitionStyle(style: NavTransitionStyle) {
        context.dataStore.edit { it[Keys.NAV_TRANSITION_STYLE] = style.name }
    }

    suspend fun setContrastLevel(level: MaterialContrastLevel) {
        context.dataStore.edit { it[Keys.CONTRAST_LEVEL] = level.name }
    }

    suspend fun setEdgeGlowEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.EDGE_GLOW_ENABLED] = enabled }
    }

    suspend fun setAudioPreampPercent(percent: Int) {
        context.dataStore.edit { it[Keys.AUDIO_PREAMP_PERCENT] = percent.coerceIn(100, 200) }
    }

    suspend fun setAudioPitch(pitch: Float) {
        context.dataStore.edit { it[Keys.AUDIO_PITCH] = pitch.coerceIn(0.75f, 1.50f) }
    }

    suspend fun setCrossfadeSeconds(seconds: Int) {
        context.dataStore.edit { it[Keys.CROSSFADE_SECONDS] = seconds.coerceIn(0, 12) }
    }

    suspend fun setShowLyricsInPlayer(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_LYRICS] = enabled }
    }

    suspend fun setVideoDefaultAspect(aspectIndex: Int) {
        context.dataStore.edit { it[Keys.VIDEO_DEFAULT_ASPECT] = aspectIndex.coerceIn(0, 2) }
    }

    suspend fun setVideoDefaultSpeed(speed: Float) {
        context.dataStore.edit { it[Keys.VIDEO_DEFAULT_SPEED] = speed.coerceIn(0.75f, 2.0f) }
    }

    suspend fun setVideoDefaultLoop(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIDEO_DEFAULT_LOOP] = enabled }
    }

    suspend fun setPhotoGridColumns(columns: Int) {
        context.dataStore.edit { it[Keys.PHOTO_GRID_COLUMNS] = columns.coerceIn(0, 3) }
    }

    suspend fun setPhotoSlideshowSeconds(seconds: Int) {
        context.dataStore.edit { it[Keys.PHOTO_SLIDESHOW_SEC] = seconds.coerceIn(2, 10) }
    }

    suspend fun setPhotoShowTitles(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PHOTO_SHOW_TITLES] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setTamperGuardEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.TAMPER_GUARD_ENABLED] = enabled }
    }

    suspend fun saveTrackLyrics(trackId: String, lyrics: String) {
        context.dataStore.edit { prefs ->
            val current = try {
                JSONObject(prefs[Keys.TRACK_LYRICS_JSON] ?: "{}")
            } catch (_: Exception) {
                JSONObject()
            }
            if (lyrics.isBlank()) {
                current.remove(trackId)
            } else {
                current.put(trackId, lyrics.trim())
            }
            prefs[Keys.TRACK_LYRICS_JSON] = current.toString()
        }
    }

    /**
     * Pins the APK's SHA-256 certificate on first install so any later re-signing/repackaging is caught.
     */
    suspend fun getOrPinApkSha256(currentSha256: String): String? {
        val existing = context.dataStore.data.first()[Keys.PINNED_APK_SHA256]
        if (!existing.isNullOrBlank()) return existing
        if (currentSha256.isNotBlank() && currentSha256 != "UNKNOWN") {
            context.dataStore.edit { it[Keys.PINNED_APK_SHA256] = currentSha256 }
            return currentSha256
        }
        return null
    }

    suspend fun getOrPinDexSha256(currentDexSha256: String): String? {
        val existing = context.dataStore.data.first()[Keys.PINNED_DEX_SHA256]
        if (!existing.isNullOrBlank()) return existing
        if (currentDexSha256.isNotBlank() && currentDexSha256 != "UNAVAILABLE") {
            context.dataStore.edit { it[Keys.PINNED_DEX_SHA256] = currentDexSha256 }
            return currentDexSha256
        }
        return null
    }

    suspend fun setFontStyle(style: MaterialFontStyle) {
        context.dataStore.edit { it[Keys.FONT_STYLE] = style.name }
    }

    suspend fun setHardwareVideoAcceleration(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HARDWARE_VIDEO_ACCEL] = enabled }
    }

    suspend fun toggleFavorite(id: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.FAVORITE_IDS] ?: emptySet()).toMutableSet()
            if (!current.add(id)) current.remove(id)
            prefs[Keys.FAVORITE_IDS] = current
        }
    }

    suspend fun addFavorites(ids: Collection<String>) {
        if (ids.isEmpty()) return
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.FAVORITE_IDS] ?: emptySet()).toMutableSet()
            current.addAll(ids)
            prefs[Keys.FAVORITE_IDS] = current
        }
    }

    suspend fun setMusicViewMode(mode: MediaViewMode) {
        context.dataStore.edit { it[Keys.MUSIC_VIEW_MODE] = mode.name }
    }

    suspend fun setVideoViewMode(mode: MediaViewMode) {
        context.dataStore.edit { it[Keys.VIDEO_VIEW_MODE] = mode.name }
    }

    suspend fun setPhotoViewMode(mode: MediaViewMode) {
        context.dataStore.edit { it[Keys.PHOTO_VIEW_MODE] = mode.name }
    }

    suspend fun setScreenPrivacyEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SCREEN_PRIVACY_ENABLED] = enabled }
    }

    suspend fun setAppLockOnBackground(enabled: Boolean) {
        context.dataStore.edit { it[Keys.APP_LOCK_ON_BG] = enabled }
    }

    suspend fun saveAppLockPin(saltHex: String, hashHex: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.APP_LOCK_PIN_SALT] = saltHex
            prefs[Keys.APP_LOCK_PIN_HASH] = hashHex
            prefs[Keys.APP_LOCK_ENABLED] = true
        }
    }

    suspend fun disableAppLock() {
        context.dataStore.edit { prefs ->
            prefs[Keys.APP_LOCK_ENABLED] = false
            prefs[Keys.BIOMETRIC_UNLOCK_ENABLED] = false
            prefs.remove(Keys.APP_LOCK_PIN_SALT)
            prefs.remove(Keys.APP_LOCK_PIN_HASH)
        }
    }

    suspend fun setBiometricUnlockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BIOMETRIC_UNLOCK_ENABLED] = enabled }
    }

    suspend fun getPinCredentials(): Pair<String, String>? {
        val prefs = context.dataStore.data.first()
        val salt = prefs[Keys.APP_LOCK_PIN_SALT]
        val hash = prefs[Keys.APP_LOCK_PIN_HASH]
        return if (!salt.isNullOrBlank() && !hash.isNullOrBlank()) salt to hash else null
    }

    suspend fun appendIntruderAttempt(attempt: IntruderAttempt) {
        context.dataStore.edit { prefs ->
            val existing = runCatching { JSONArray(prefs[Keys.INTRUDER_LOG_JSON] ?: "[]") }
                .getOrElse { JSONArray() }
            val next = JSONArray()
            next.put(
                JSONObject().apply {
                    put("ts", attempt.timestampMs)
                    put("time", attempt.formattedTime)
                    put("count", attempt.failedCount)
                    put("note", attempt.note)
                }
            )
            // Keep latest 25 intruder records
            for (i in 0 until minOf(existing.length(), 24)) {
                next.put(existing.get(i))
            }
            prefs[Keys.INTRUDER_LOG_JSON] = next.toString()
        }
    }

    suspend fun clearIntruderLog() {
        context.dataStore.edit { it.remove(Keys.INTRUDER_LOG_JSON) }
    }

    suspend fun addTreeUri(uriString: String) {
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.TREE_URIS] ?: emptySet()).toMutableSet()
            current.add(uriString)
            prefs[Keys.TREE_URIS] = current
        }
    }

    suspend fun addPickedFileUris(uris: Collection<String>) {
        context.dataStore.edit { prefs ->
            val current = (prefs[Keys.PICKED_FILE_URIS] ?: emptySet()).toMutableSet()
            current.addAll(uris)
            prefs[Keys.PICKED_FILE_URIS] = current
        }
    }

    suspend fun setDemoLoaded(loaded: Boolean) {
        context.dataStore.edit { it[Keys.DEMO_LOADED] = loaded }
    }

    suspend fun getSavedLibrarySources(): Triple<Set<String>, Set<String>, Boolean> {
        val prefs = context.dataStore.data.first()
        return Triple(
            prefs[Keys.TREE_URIS] ?: emptySet(),
            prefs[Keys.PICKED_FILE_URIS] ?: emptySet(),
            prefs[Keys.DEMO_LOADED] ?: false,
        )
    }

    val watchedFoldersFlow: Flow<List<WatchedFolder>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.WATCHED_FOLDERS_JSON] ?: return@map emptyList()
        parseWatchedFoldersJson(raw)
    }

    val playStatsFlow: Flow<Map<String, Pair<Int, Long>>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.PLAY_STATS_JSON] ?: return@map emptyMap()
        try {
            val json = JSONObject(raw)
            buildMap {
                for (key in json.keys()) {
                    val obj = json.optJSONObject(key) ?: continue
                    put(key, obj.optInt("count", 0) to obj.optLong("last", 0L))
                }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    val tagOverridesFlow: Flow<Map<String, TrackTagOverride>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.TAG_OVERRIDES_JSON] ?: return@map emptyMap()
        try {
            val json = JSONObject(raw)
            buildMap {
                for (key in json.keys()) {
                    val obj = json.optJSONObject(key) ?: continue
                    put(
                        key,
                        TrackTagOverride(
                            title = obj.optString("title", ""),
                            artist = obj.optString("artist", ""),
                            album = obj.optString("album", ""),
                            artworkUriString = obj.optString("art", "").takeIf { it.isNotBlank() },
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    val bookmarksFlow: Flow<List<MediaBookmark>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.BOOKMARKS_JSON] ?: return@map emptyList()
        try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    val id = obj.optString("id")
                    val mediaId = obj.optString("mediaId")
                    if (id.isNotBlank() && mediaId.isNotBlank()) {
                        add(
                            MediaBookmark(
                                id = id,
                                mediaId = mediaId,
                                positionMs = obj.optLong("pos", 0L),
                                label = obj.optString("label", ""),
                                createdAtMs = obj.optLong("ts", 0L),
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun saveWatchedFolders(folders: List<WatchedFolder>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.WATCHED_FOLDERS_JSON] = encodeWatchedFoldersJson(folders)
            prefs[Keys.TREE_URIS] = folders.filter { it.enabled }.map { it.uriString }.toSet()
        }
    }

    suspend fun getWatchedFolders(): List<WatchedFolder> {
        val raw = context.dataStore.data.first()[Keys.WATCHED_FOLDERS_JSON] ?: return emptyList()
        return parseWatchedFoldersJson(raw)
    }

    suspend fun setFolderSetupCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.FOLDER_SETUP_COMPLETED] = completed }
    }

    suspend fun setAutoScanWatchedFolders(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_SCAN_WATCHED_FOLDERS] = enabled }
    }

    suspend fun recordTrackPlay(trackId: String, timestampMs: Long = System.currentTimeMillis()) {
        context.dataStore.edit { prefs ->
            val current = runCatching { JSONObject(prefs[Keys.PLAY_STATS_JSON] ?: "{}") }
                .getOrElse { JSONObject() }
            val existing = current.optJSONObject(trackId)
            val prevCount = existing?.optInt("count", 0) ?: 0
            val next = JSONObject().apply {
                put("count", prevCount + 1)
                put("last", timestampMs)
            }
            current.put(trackId, next)
            prefs[Keys.PLAY_STATS_JSON] = current.toString()
        }
    }

    suspend fun saveTrackTagOverride(trackId: String, title: String, artist: String, album: String) {
        context.dataStore.edit { prefs ->
            val current = runCatching { JSONObject(prefs[Keys.TAG_OVERRIDES_JSON] ?: "{}") }
                .getOrElse { JSONObject() }
            current.put(
                trackId,
                JSONObject().apply {
                    put("title", title.trim())
                    put("artist", artist.trim())
                    put("album", album.trim())
                }
            )
            prefs[Keys.TAG_OVERRIDES_JSON] = current.toString()
        }
    }

    suspend fun saveBookmarks(bookmarks: List<MediaBookmark>) {
        context.dataStore.edit { prefs ->
            val arr = JSONArray()
            bookmarks.forEach { bm ->
                arr.put(
                    JSONObject().apply {
                        put("id", bm.id)
                        put("mediaId", bm.mediaId)
                        put("pos", bm.positionMs)
                        put("label", bm.label)
                        put("ts", bm.createdAtMs)
                    }
                )
            }
            prefs[Keys.BOOKMARKS_JSON] = arr.toString()
        }
    }

    suspend fun clearLibrarySources() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.TREE_URIS)
            prefs.remove(Keys.PICKED_FILE_URIS)
            prefs.remove(Keys.WATCHED_FOLDERS_JSON)
            prefs[Keys.DEMO_LOADED] = false
        }
        clearLibraryCache()
    }

    // --- Fast Local Disk Cache (<50ms Cold Start Library Hydration) ---

    private val cacheFile: File
        get() = File(context.filesDir, "prism_library_cache_v1.json")

    suspend fun saveLibraryCache(result: ScannedMediaResult) = withContext(Dispatchers.IO) {
        runCatching {
            val root = JSONObject()
            val audioArr = JSONArray()
            result.audioTracks.filterNot { it.isDemo }.forEach { t ->
                audioArr.put(
                    JSONObject().apply {
                        put("id", t.id)
                        put("title", t.title)
                        put("artist", t.artist)
                        put("album", t.album)
                        put("uri", t.uriString)
                        put("ext", t.extension)
                        put("mime", t.mimeType)
                        put("dur", t.durationMs)
                        put("size", t.sizeBytes)
                        if (t.artworkUriString != null) put("art", t.artworkUriString)
                        put("folder", t.folderName)
                        put("src", t.sourceFolderUri)
                        put("mod", t.dateModifiedMs)
                    }
                )
            }
            val videoArr = JSONArray()
            result.videos.filterNot { it.isDemo }.forEach { v ->
                videoArr.put(
                    JSONObject().apply {
                        put("id", v.id)
                        put("title", v.title)
                        put("uri", v.uriString)
                        put("ext", v.extension)
                        put("mime", v.mimeType)
                        put("dur", v.durationMs)
                        put("size", v.sizeBytes)
                        put("res", v.resolution)
                        put("folder", v.folderName)
                        put("src", v.sourceFolderUri)
                        put("mod", v.dateModifiedMs)
                    }
                )
            }
            val photoArr = JSONArray()
            result.photos.filterNot { it.isDemo }.forEach { p ->
                photoArr.put(
                    JSONObject().apply {
                        put("id", p.id)
                        put("title", p.title)
                        put("uri", p.uriString)
                        put("ext", p.extension)
                        put("mime", p.mimeType)
                        put("size", p.sizeBytes)
                        put("w", p.width)
                        put("h", p.height)
                        put("folder", p.folderName)
                        put("src", p.sourceFolderUri)
                        put("mod", p.dateModifiedMs)
                    }
                )
            }
            root.put("audio", audioArr)
            root.put("videos", videoArr)
            root.put("photos", photoArr)
            val tmp = File(context.filesDir, "prism_library_cache_v1.tmp")
            tmp.writeText(root.toString())
            tmp.renameTo(cacheFile)
        }
    }

    suspend fun loadLibraryCache(): ScannedMediaResult? = withContext(Dispatchers.IO) {
        val file = cacheFile
        if (!file.exists()) return@withContext null
        runCatching {
            val root = JSONObject(file.readText())
            val audioArr = root.optJSONArray("audio") ?: JSONArray()
            val audio = buildList {
                for (i in 0 until audioArr.length()) {
                    val o = audioArr.optJSONObject(i) ?: continue
                    val id = o.optString("id")
                    val uri = o.optString("uri")
                    if (id.isBlank() || uri.isBlank()) continue
                    add(
                        AudioTrack(
                            id = id,
                            title = o.optString("title", "Audio"),
                            artist = o.optString("artist", "Local Artist"),
                            album = o.optString("album", "Device Library"),
                            uriString = uri,
                            extension = o.optString("ext", "MP3"),
                            mimeType = o.optString("mime", "audio/*"),
                            durationMs = o.optLong("dur", 0L),
                            sizeBytes = o.optLong("size", 0L),
                            artworkUriString = o.optString("art", "").takeIf { it.isNotBlank() },
                            folderName = o.optString("folder", ""),
                            sourceFolderUri = o.optString("src", ""),
                            dateModifiedMs = o.optLong("mod", 0L),
                        )
                    )
                }
            }
            val videoArr = root.optJSONArray("videos") ?: JSONArray()
            val videos = buildList {
                for (i in 0 until videoArr.length()) {
                    val o = videoArr.optJSONObject(i) ?: continue
                    val id = o.optString("id")
                    val uri = o.optString("uri")
                    if (id.isBlank() || uri.isBlank()) continue
                    add(
                        VideoItem(
                            id = id,
                            title = o.optString("title", "Video"),
                            uriString = uri,
                            extension = o.optString("ext", "MP4"),
                            mimeType = o.optString("mime", "video/*"),
                            durationMs = o.optLong("dur", 0L),
                            sizeBytes = o.optLong("size", 0L),
                            resolution = o.optString("res", "HD"),
                            folderName = o.optString("folder", ""),
                            sourceFolderUri = o.optString("src", ""),
                            dateModifiedMs = o.optLong("mod", 0L),
                        )
                    )
                }
            }
            val photoArr = root.optJSONArray("photos") ?: JSONArray()
            val photos = buildList {
                for (i in 0 until photoArr.length()) {
                    val o = photoArr.optJSONObject(i) ?: continue
                    val id = o.optString("id")
                    val uri = o.optString("uri")
                    if (id.isBlank() || uri.isBlank()) continue
                    add(
                        PhotoItem(
                            id = id,
                            title = o.optString("title", "Photo"),
                            uriString = uri,
                            extension = o.optString("ext", "JPG"),
                            mimeType = o.optString("mime", "image/jpeg"),
                            sizeBytes = o.optLong("size", 0L),
                            width = o.optInt("w", 0),
                            height = o.optInt("h", 0),
                            folderName = o.optString("folder", ""),
                            sourceFolderUri = o.optString("src", ""),
                            dateModifiedMs = o.optLong("mod", 0L),
                        )
                    )
                }
            }
            ScannedMediaResult(audio, videos, photos)
        }.getOrNull()
    }

    suspend fun clearLibraryCache() = withContext(Dispatchers.IO) {
        runCatching { if (cacheFile.exists()) cacheFile.delete() }
    }

    suspend fun exportBackupJson(): String {
        val currentSettings = settingsFlow.first()
        val playlists = playlistsFlow.first()
        val folders = watchedFoldersFlow.first()
        val bookmarks = bookmarksFlow.first()
        val lyrics = customLyricsFlow.first()
        val tags = tagOverridesFlow.first()
        val favs = favoriteIdsFlow.first()
        return PrismBackupManager.encodeBackup(
            PrismBackupPayload(
                settingsJson = PrismBackupManager.settingsToJson(currentSettings),
                playlists = playlists,
                watchedFolders = folders,
                bookmarks = bookmarks,
                customLyrics = lyrics,
                tagOverrides = tags,
                favorites = favs
            )
        )
    }

    suspend fun restoreBackupJson(rawJson: String): Boolean {
        val payload = PrismBackupManager.decodeAndVerifyBackup(rawJson) ?: return false
        val s = payload.settingsJson
        context.dataStore.edit { prefs ->
            if (s.has("darkTheme")) prefs[Keys.DARK_THEME] = s.optBoolean("darkTheme", true)
            if (s.has("amoledBlack")) prefs[Keys.AMOLED_BLACK] = s.optBoolean("amoledBlack", false)
            s.optString("accentPalette").takeIf { it.isNotBlank() }?.let { prefs[Keys.ACCENT_PALETTE] = it }
            s.optString("iconStyle").takeIf { it.isNotBlank() }?.let { prefs[Keys.ICON_STYLE] = it }
            s.optString("language").takeIf { it.isNotBlank() }?.let { prefs[Keys.LANGUAGE] = it }
            s.optString("fontStyle").takeIf { it.isNotBlank() }?.let { prefs[Keys.FONT_STYLE] = it }
            if (s.has("fontScaleMultiplier")) prefs[Keys.FONT_SCALE] = s.optDouble("fontScaleMultiplier", 1.0).toFloat()
            s.optString("liquidGlassStyle").takeIf { it.isNotBlank() }?.let { prefs[Keys.LIQUID_GLASS_STYLE] = it }
            if (s.has("liquidGlassIntensity")) prefs[Keys.LIQUID_GLASS_INTENSITY] = s.optDouble("liquidGlassIntensity", 0.55).toFloat()
            if (s.has("shapeRoundness")) prefs[Keys.SHAPE_ROUNDNESS] = s.optDouble("shapeRoundness", 1.0).toFloat()
            s.optString("contrastLevel").takeIf { it.isNotBlank() }?.let { prefs[Keys.CONTRAST_LEVEL] = it }
            s.optString("navTransitionStyle").takeIf { it.isNotBlank() }?.let { prefs[Keys.NAV_TRANSITION_STYLE] = it }
            if (s.has("edgeGlowEnabled")) prefs[Keys.EDGE_GLOW_ENABLED] = s.optBoolean("edgeGlowEnabled", true)
            if (s.has("eqEnabled")) prefs[Keys.EQ_ENABLED] = s.optBoolean("eqEnabled", true)
            s.optString("eqPreset").takeIf { it.isNotBlank() }?.let { prefs[Keys.EQ_PRESET] = it }
            s.optString("eqGainsDb").takeIf { it.isNotBlank() }?.let { prefs[Keys.EQ_GAINS] = it }
            if (s.has("bassBoost")) prefs[Keys.BASS_BOOST] = s.optDouble("bassBoost", 0.0).toFloat()
            if (s.has("audioPreampPercent")) prefs[Keys.AUDIO_PREAMP_PERCENT] = s.optInt("audioPreampPercent", 100)
            if (s.has("audioPitch")) prefs[Keys.AUDIO_PITCH] = s.optDouble("audioPitch", 1.0).toFloat()
            if (s.has("crossfadeSeconds")) prefs[Keys.CROSSFADE_SECONDS] = s.optInt("crossfadeSeconds", 0)
            if (s.has("hardwareVideoAcceleration")) prefs[Keys.HARDWARE_VIDEO_ACCEL] = s.optBoolean("hardwareVideoAcceleration", true)
            if (s.has("autoPlayNext")) prefs[Keys.AUTO_PLAY_NEXT] = s.optBoolean("autoPlayNext", true)
            if (s.has("resumePlaybackPosition")) prefs[Keys.RESUME_PLAYBACK] = s.optBoolean("resumePlaybackPosition", true)
            if (s.has("showLyricsInPlayer")) prefs[Keys.SHOW_LYRICS] = s.optBoolean("showLyricsInPlayer", true)
            if (s.has("autoPipEnabled")) prefs[Keys.AUTO_PIP_ENABLED] = s.optBoolean("autoPipEnabled", true)
            if (s.has("squigglyProgress")) prefs[Keys.SQUIGGLY_PROGRESS] = s.optBoolean("squigglyProgress", true)
            if (s.has("biometricUnlockEnabled")) prefs[Keys.BIOMETRIC_UNLOCK_ENABLED] = s.optBoolean("biometricUnlockEnabled", false)
            if (s.has("navBarStyle")) prefs[Keys.NAV_BAR_STYLE] = s.optString("navBarStyle", NavBarStyle.FLOATING_PILL.name)
            if (s.has("startupTab")) prefs[Keys.STARTUP_TAB] = s.optString("startupTab", MediaTab.MUSIC.name)
            if (s.has("uiDensity")) prefs[Keys.UI_DENSITY] = s.optString("uiDensity", UiDensity.BALANCED.name)
            if (s.has("playerArtworkStyle")) prefs[Keys.PLAYER_ARTWORK_STYLE] = s.optString("playerArtworkStyle", PlayerArtworkStyle.VINYL_DISC.name)
            if (s.has("albumArtTintEnabled")) prefs[Keys.ALBUM_ART_TINT_ENABLED] = s.optBoolean("albumArtTintEnabled", true)
            if (s.has("showFullPlayerSpectrum")) prefs[Keys.FULL_PLAYER_SPECTRUM] = s.optBoolean("showFullPlayerSpectrum", true)
            if (s.has("seekStepSeconds")) prefs[Keys.SEEK_STEP_SECONDS] = s.optInt("seekStepSeconds", 10).coerceIn(5, 30)
            if (s.has("hapticsEnabled")) prefs[Keys.HAPTICS_ENABLED] = s.optBoolean("hapticsEnabled", true)
            if (s.has("autoOnlineEnrichment")) prefs[Keys.AUTO_ONLINE_ENRICHMENT] = s.optBoolean("autoOnlineEnrichment", true)
            if (s.has("autoFetchLyrics")) prefs[Keys.AUTO_FETCH_LYRICS] = s.optBoolean("autoFetchLyrics", true)
            if (s.has("autoFetchArtistPhotos")) prefs[Keys.AUTO_FETCH_ARTIST_PHOTOS] = s.optBoolean("autoFetchArtistPhotos", true)
            if (s.has("gaplessPlayback")) prefs[Keys.GAPLESS_PLAYBACK] = s.optBoolean("gaplessPlayback", true)
            if (s.has("audioNormalization")) prefs[Keys.AUDIO_NORMALIZATION] = s.optBoolean("audioNormalization", false)
            if (s.has("pauseOnHeadsetUnplug")) prefs[Keys.PAUSE_ON_HEADSET_UNPLUG] = s.optBoolean("pauseOnHeadsetUnplug", true)
            if (s.has("videoGesturesEnabled")) prefs[Keys.VIDEO_GESTURES_ENABLED] = s.optBoolean("videoGesturesEnabled", true)
            if (s.has("videoDoubleTapSeekEnabled")) prefs[Keys.VIDEO_DOUBLE_TAP_SEEK] = s.optBoolean("videoDoubleTapSeekEnabled", true)
            if (s.has("videoRememberPosition")) prefs[Keys.VIDEO_REMEMBER_POSITION] = s.optBoolean("videoRememberPosition", true)
            if (s.has("videoSubtitleSizeSp")) prefs[Keys.VIDEO_SUBTITLE_SIZE_SP] = s.optInt("videoSubtitleSizeSp", 18).coerceIn(14, 28)
            if (s.has("photoAutoShowExif")) prefs[Keys.PHOTO_AUTO_SHOW_EXIF] = s.optBoolean("photoAutoShowExif", true)
            if (s.has("photoHighResDecode")) prefs[Keys.PHOTO_HIGH_RES_DECODE] = s.optBoolean("photoHighResDecode", true)
            s.optString("photoSlideshowTransition").takeIf { it.isNotBlank() }?.let { prefs[Keys.PHOTO_SLIDESHOW_TRANSITION] = it }
            s.optString("surfaceDepthStyle").takeIf { it.isNotBlank() }?.let { prefs[Keys.SURFACE_DEPTH_STYLE] = it }

            prefs[Keys.PLAYLISTS_JSON] = encodePlaylistsJson(payload.playlists)
            prefs[Keys.WATCHED_FOLDERS_JSON] = encodeWatchedFoldersJson(payload.watchedFolders)
            prefs[Keys.BOOKMARKS_JSON] = encodeBookmarksJson(payload.bookmarks)
            prefs[Keys.TRACK_LYRICS_JSON] = JSONObject().apply {
                payload.customLyrics.forEach { (k, v) -> put(k, v) }
            }.toString()
            prefs[Keys.TAG_OVERRIDES_JSON] = encodeTagOverridesJson(payload.tagOverrides)
            prefs[Keys.FAVORITE_IDS] = payload.favorites
        }
        return true
    }

    suspend fun setNavBarStyle(style: NavBarStyle) {
        context.dataStore.edit { it[Keys.NAV_BAR_STYLE] = style.name }
    }

    suspend fun setStartupTab(tab: MediaTab) {
        context.dataStore.edit { it[Keys.STARTUP_TAB] = tab.name }
    }

    suspend fun setUiDensity(density: UiDensity) {
        context.dataStore.edit { it[Keys.UI_DENSITY] = density.name }
    }

    suspend fun setPlayerArtworkStyle(style: PlayerArtworkStyle) {
        context.dataStore.edit { it[Keys.PLAYER_ARTWORK_STYLE] = style.name }
    }

    suspend fun setAlbumArtTintEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.ALBUM_ART_TINT_ENABLED] = enabled }
    }

    suspend fun setShowFullPlayerSpectrum(enabled: Boolean) {
        context.dataStore.edit { it[Keys.FULL_PLAYER_SPECTRUM] = enabled }
    }

    suspend fun setSeekStepSeconds(seconds: Int) {
        context.dataStore.edit { it[Keys.SEEK_STEP_SECONDS] = seconds.coerceIn(5, 30) }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.HAPTICS_ENABLED] = enabled }
    }

    suspend fun setAutoOnlineEnrichment(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_ONLINE_ENRICHMENT] = enabled }
    }

    suspend fun setAutoFetchLyrics(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_FETCH_LYRICS] = enabled }
    }

    suspend fun setAutoFetchArtistPhotos(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_FETCH_ARTIST_PHOTOS] = enabled }
    }

    suspend fun setGaplessPlayback(enabled: Boolean) {
        context.dataStore.edit { it[Keys.GAPLESS_PLAYBACK] = enabled }
    }

    suspend fun setAudioNormalization(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUDIO_NORMALIZATION] = enabled }
    }

    suspend fun setPauseOnHeadsetUnplug(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PAUSE_ON_HEADSET_UNPLUG] = enabled }
    }

    suspend fun setVideoGesturesEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIDEO_GESTURES_ENABLED] = enabled }
    }

    suspend fun setVideoDoubleTapSeekEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIDEO_DOUBLE_TAP_SEEK] = enabled }
    }

    suspend fun setVideoRememberPosition(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIDEO_REMEMBER_POSITION] = enabled }
    }

    suspend fun setVideoSubtitleSizeSp(sizeSp: Int) {
        context.dataStore.edit { it[Keys.VIDEO_SUBTITLE_SIZE_SP] = sizeSp.coerceIn(14, 28) }
    }

    suspend fun setPhotoAutoShowExif(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PHOTO_AUTO_SHOW_EXIF] = enabled }
    }

    suspend fun setPhotoHighResDecode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PHOTO_HIGH_RES_DECODE] = enabled }
    }

    suspend fun setPhotoSlideshowTransition(style: SlideshowTransitionStyle) {
        context.dataStore.edit { it[Keys.PHOTO_SLIDESHOW_TRANSITION] = style.name }
    }

    suspend fun setSurfaceDepthStyle(style: SurfaceDepthStyle) {
        context.dataStore.edit { it[Keys.SURFACE_DEPTH_STYLE] = style.name }
    }

    suspend fun resetCustomizationDefaults() {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = true
            prefs[Keys.AMOLED_BLACK] = false
            prefs[Keys.ACCENT_PALETTE] = AccentPalette.SYSTEM.name
            prefs[Keys.ICON_STYLE] = IconPackStyle.GOOGLE_ROUNDED.name
            prefs[Keys.FONT_STYLE] = MaterialFontStyle.MATERIAL_EXPRESSIVE.name
            prefs[Keys.FONT_SCALE] = 1.0f
            prefs[Keys.LIQUID_GLASS_STYLE] = LiquidGlassStyle.SOLID_M3.name
            prefs[Keys.LIQUID_GLASS_INTENSITY] = 0.55f
            prefs[Keys.SHAPE_ROUNDNESS] = 1.0f
            prefs[Keys.CONTRAST_LEVEL] = MaterialContrastLevel.STANDARD.name
            prefs[Keys.NAV_TRANSITION_STYLE] = NavTransitionStyle.EXPRESSIVE_SPRING.name
            prefs[Keys.EDGE_GLOW_ENABLED] = true
            prefs[Keys.NAV_BAR_STYLE] = NavBarStyle.FLOATING_PILL.name
            prefs[Keys.STARTUP_TAB] = MediaTab.MUSIC.name
            prefs[Keys.UI_DENSITY] = UiDensity.BALANCED.name
            prefs[Keys.PLAYER_ARTWORK_STYLE] = PlayerArtworkStyle.VINYL_DISC.name
            prefs[Keys.ALBUM_ART_TINT_ENABLED] = true
            prefs[Keys.FULL_PLAYER_SPECTRUM] = true
            prefs[Keys.SEEK_STEP_SECONDS] = 10
            prefs[Keys.HAPTICS_ENABLED] = true
            prefs[Keys.SQUIGGLY_PROGRESS] = true
            prefs[Keys.MINI_PLAYER_COMPACT] = false
            prefs[Keys.MINI_PLAYER_FLOATING] = false
            prefs[Keys.SURFACE_DEPTH_STYLE] = SurfaceDepthStyle.EXPRESSIVE.name
            prefs.remove(Keys.CUSTOM_SEED_COLOR)
        }
    }

    // --- New 24-Feature Persistence Flows & Setters ---

    val quranKhatmaFlow: Flow<QuranKhatmaProgress> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.QURAN_KHATMA_JSON] ?: return@map QuranKhatmaProgress()
        runCatching {
            val j = JSONObject(raw)
            val arr = j.optJSONArray("done") ?: JSONArray()
            val set = buildSet {
                for (i in 0 until arr.length()) {
                    val n = arr.optInt(i, -1)
                    if (n in 1..114) add(n)
                }
            }
            QuranKhatmaProgress(
                completedSurahs = set,
                lastSurahNumber = j.optInt("lastSurah", 1).coerceIn(1, 114),
                lastPositionMs = j.optLong("lastPos", 0L).coerceAtLeast(0L),
                hifzRepeatTarget = j.optInt("hifz", 3).coerceIn(0, 20),
                totalQuranMinutes = j.optInt("mins", 0).coerceAtLeast(0),
            )
        }.getOrDefault(QuranKhatmaProgress())
    }

    val morningAlarmFlow: Flow<MorningAlarmConfig> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.MORNING_ALARM_JSON] ?: return@map MorningAlarmConfig()
        runCatching {
            val j = JSONObject(raw)
            MorningAlarmConfig(
                enabled = j.optBoolean("enabled", false),
                hour = j.optInt("hour", 5).coerceIn(0, 23),
                minute = j.optInt("minute", 30).coerceIn(0, 59),
                fadeInSeconds = j.optInt("fade", 30).coerceIn(5, 60),
                trackId = j.optString("trackId", "").takeIf { it.isNotBlank() },
                trackTitle = j.optString("trackTitle", ""),
                repeatDaily = j.optBoolean("daily", true),
            )
        }.getOrDefault(MorningAlarmConfig())
    }

    val folderCustomizationsFlow: Flow<Map<String, FolderCustomization>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.FOLDER_CUSTOM_JSON] ?: return@map emptyMap()
        runCatching {
            val j = JSONObject(raw)
            buildMap {
                for (key in j.keys()) {
                    val o = j.optJSONObject(key) ?: continue
                    put(
                        key,
                        FolderCustomization(
                            folderName = key,
                            pinned = o.optBoolean("pinned", false),
                            accentArgb = if (o.has("color")) o.optInt("color") else null,
                            badgeSymbol = o.optString("badge", ""),
                        )
                    )
                }
            }
        }.getOrDefault(emptyMap())
    }

    val dailyListeningMinutesFlow: Flow<Map<String, Int>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.DAILY_MINUTES_JSON] ?: return@map emptyMap()
        runCatching {
            val j = JSONObject(raw)
            buildMap {
                for (k in j.keys()) {
                    put(k, j.optInt(k, 0).coerceAtLeast(0))
                }
            }
        }.getOrDefault(emptyMap())
    }

    suspend fun saveQuranKhatma(progress: QuranKhatmaProgress) {
        context.dataStore.edit { prefs ->
            val arr = JSONArray()
            progress.completedSurahs.sorted().forEach { arr.put(it) }
            val j = JSONObject().apply {
                put("done", arr)
                put("lastSurah", progress.lastSurahNumber)
                put("lastPos", progress.lastPositionMs)
                put("hifz", progress.hifzRepeatTarget)
                put("mins", progress.totalQuranMinutes)
            }
            prefs[Keys.QURAN_KHATMA_JSON] = j.toString()
        }
    }

    suspend fun saveMorningAlarm(config: MorningAlarmConfig) {
        context.dataStore.edit { prefs ->
            val j = JSONObject().apply {
                put("enabled", config.enabled)
                put("hour", config.hour)
                put("minute", config.minute)
                put("fade", config.fadeInSeconds)
                put("trackId", config.trackId.orEmpty())
                put("trackTitle", config.trackTitle)
                put("daily", config.repeatDaily)
            }
            prefs[Keys.MORNING_ALARM_JSON] = j.toString()
        }
    }

    suspend fun saveFolderCustomization(custom: FolderCustomization) {
        context.dataStore.edit { prefs ->
            val j = runCatching { JSONObject(prefs[Keys.FOLDER_CUSTOM_JSON] ?: "{}") }.getOrElse { JSONObject() }
            j.put(
                custom.folderName,
                JSONObject().apply {
                    put("pinned", custom.pinned)
                    if (custom.accentArgb != null) put("color", custom.accentArgb)
                    put("badge", custom.badgeSymbol)
                }
            )
            prefs[Keys.FOLDER_CUSTOM_JSON] = j.toString()
        }
    }

    suspend fun addDailyListeningMinutes(dayKey: String, deltaMinutes: Int) {
        if (deltaMinutes <= 0) return
        context.dataStore.edit { prefs ->
            val j = runCatching { JSONObject(prefs[Keys.DAILY_MINUTES_JSON] ?: "{}") }.getOrElse { JSONObject() }
            val prev = j.optInt(dayKey, 0)
            j.put(dayKey, prev + deltaMinutes)
            prefs[Keys.DAILY_MINUTES_JSON] = j.toString()
        }
    }

    suspend fun saveBatchTrackTags(
        trackIds: Collection<String>,
        artist: String?,
        album: String?,
        artworkUriString: String?,
    ) {
        if (trackIds.isEmpty()) return
        context.dataStore.edit { prefs ->
            val current = runCatching { JSONObject(prefs[Keys.TAG_OVERRIDES_JSON] ?: "{}") }
                .getOrElse { JSONObject() }
            trackIds.forEach { id ->
                val existing = current.optJSONObject(id) ?: JSONObject()
                if (!artist.isNullOrBlank()) existing.put("artist", artist.trim())
                if (!album.isNullOrBlank()) existing.put("album", album.trim())
                if (!artworkUriString.isNullOrBlank()) existing.put("art", artworkUriString.trim())
                current.put(id, existing)
            }
            prefs[Keys.TAG_OVERRIDES_JSON] = current.toString()
        }
    }

    suspend fun applyDecodedThemePreset(preset: DecodedThemePreset) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = preset.darkTheme
            prefs[Keys.AMOLED_BLACK] = preset.amoledBlack
            prefs[Keys.ACCENT_PALETTE] = preset.accentPalette.name
            if (preset.customSeedColorArgb != null) {
                prefs[Keys.CUSTOM_SEED_COLOR] = preset.customSeedColorArgb
            } else {
                prefs.remove(Keys.CUSTOM_SEED_COLOR)
            }
            prefs[Keys.LIQUID_GLASS_STYLE] = preset.liquidGlassStyle.name
            prefs[Keys.LIQUID_GLASS_INTENSITY] = preset.liquidGlassIntensity
            prefs[Keys.SHAPE_ROUNDNESS] = preset.shapeRoundness
            prefs[Keys.FONT_STYLE] = preset.fontStyle.name
            prefs[Keys.ICON_STYLE] = preset.iconStyle.name
            prefs[Keys.CONTRAST_LEVEL] = preset.contrastLevel.name
            prefs[Keys.NAV_BAR_STYLE] = preset.navBarStyle.name
            prefs[Keys.NAV_TRANSITION_STYLE] = preset.navTransitionStyle.name
            prefs[Keys.PLAYER_ARTWORK_STYLE] = preset.playerArtworkStyle.name
            prefs[Keys.SURFACE_DEPTH_STYLE] = preset.surfaceDepthStyle.name
            prefs[Keys.UI_DENSITY] = preset.uiDensity.name
            prefs[Keys.EDGE_GLOW_ENABLED] = preset.edgeGlowEnabled
        }
    }

    suspend fun setKhushuModeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.KHUSHU_MODE] = enabled }
    }

    suspend fun setLectureModeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LECTURE_MODE] = enabled }
    }

    suspend fun setSkipSilenceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SKIP_SILENCE] = enabled }
    }

    suspend fun setAudioBalance(balance: Float) {
        context.dataStore.edit { it[Keys.AUDIO_BALANCE] = balance.coerceIn(-1f, 1f) }
    }

    suspend fun setAudioMonoEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUDIO_MONO] = enabled }
    }

    suspend fun setShakeToShuffleEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SHAKE_TO_SHUFFLE] = enabled }
    }

    suspend fun setStripExifOnShare(enabled: Boolean) {
        context.dataStore.edit { it[Keys.STRIP_EXIF_ON_SHARE] = enabled }
    }

    suspend fun setCustomSeedColorArgb(argb: Int?) {
        context.dataStore.edit { prefs ->
            if (argb == null) prefs.remove(Keys.CUSTOM_SEED_COLOR)
            else prefs[Keys.CUSTOM_SEED_COLOR] = argb
        }
    }

    suspend fun setAppIconVariant(variant: AppIconVariant) {
        context.dataStore.edit { it[Keys.APP_ICON_VARIANT] = variant.name }
    }

    suspend fun setVideoSubtitleColor(color: SubtitleColorStyle) {
        context.dataStore.edit { it[Keys.VIDEO_SUB_COLOR] = color.name }
    }

    suspend fun setVideoSubtitleBgOpacity(opacity: Float) {
        context.dataStore.edit { it[Keys.VIDEO_SUB_BG_OPACITY] = opacity.coerceIn(0f, 1f) }
    }

    suspend fun setVideoSubtitleBottomOffsetDp(offsetDp: Int) {
        context.dataStore.edit { it[Keys.VIDEO_SUB_BOTTOM_OFFSET] = offsetDp.coerceIn(8, 96) }
    }

    suspend fun setTasbihDailyCount(count: Int) {
        context.dataStore.edit { it[Keys.TASBIH_DAILY_COUNT] = count.coerceAtLeast(0) }
    }

    suspend fun setSettingsLayoutMode(mode: SettingsLayoutMode) {
        context.dataStore.edit { it[Keys.SETTINGS_LAYOUT_MODE] = mode.name }
    }

    suspend fun setMiniPlayerEdgeDockSide(side: EdgeDockSide) {
        context.dataStore.edit { it[Keys.MINI_PLAYER_EDGE_DOCK_SIDE] = side.name }
    }

    suspend fun setSpectrumVisualStyle(style: SpectrumVisualStyle) {
        context.dataStore.edit { it[Keys.SPECTRUM_VISUAL_STYLE] = style.name }
    }

    suspend fun setLyricsFontSizeSp(sizeSp: Int) {
        context.dataStore.edit { it[Keys.LYRICS_FONT_SIZE_SP] = sizeSp.coerceIn(13, 24) }
    }

    suspend fun setDriveModeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DRIVE_MODE_ENABLED] = enabled }
    }

    suspend fun setPlayerGestureSwipeArt(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PLAYER_GESTURE_SWIPE_ART] = enabled }
    }

    suspend fun setVideoMirrorEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIDEO_MIRROR_ENABLED] = enabled }
    }

    suspend fun setVideoAutoHideSeconds(seconds: Int) {
        context.dataStore.edit { it[Keys.VIDEO_AUTO_HIDE_SECONDS] = seconds.coerceIn(0, 10) }
    }

    suspend fun setReverbEnvironment(env: ReverbEnvironment) {
        context.dataStore.edit { it[Keys.REVERB_ENVIRONMENT] = env.name }
    }

    suspend fun setAutoAudioDeviceProfiles(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_AUDIO_DEVICE_PROFILES] = enabled }
    }

    suspend fun setVideoAutoNextEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIDEO_AUTO_NEXT_ENABLED] = enabled }
    }

    suspend fun setNavTabOrder(order: List<MediaTab>) {
        val normalized = normalizeNavTabOrder(order)
        context.dataStore.edit { it[Keys.NAV_TAB_ORDER] = normalized.joinToString(",") { t -> t.name } }
    }

    suspend fun setHeaderShowStreamBtn(show: Boolean) {
        context.dataStore.edit { it[Keys.HEADER_SHOW_STREAM_BTN] = show }
    }

    suspend fun setHeaderShowMultiWindowBtn(show: Boolean) {
        context.dataStore.edit { it[Keys.HEADER_SHOW_MULTI_WINDOW_BTN] = show }
    }

    suspend fun setAdhkarCompletedKeys(keys: Set<String>) {
        context.dataStore.edit { it[Keys.ADHKAR_COMPLETED_KEYS] = keys }
    }

    suspend fun saveMediaNote(note: MediaNote) {
        context.dataStore.edit { prefs ->
            val current = parseMediaNotesJson(prefs[Keys.MEDIA_NOTES_JSON].orEmpty()).toMutableMap()
            if (note.noteText.isBlank()) {
                current.remove(note.mediaId)
            } else {
                current[note.mediaId] = note
            }
            prefs[Keys.MEDIA_NOTES_JSON] = encodeMediaNotesJson(current)
        }
    }

    suspend fun deleteMediaNote(mediaId: String) {
        context.dataStore.edit { prefs ->
            val current = parseMediaNotesJson(prefs[Keys.MEDIA_NOTES_JSON].orEmpty()).toMutableMap()
            current.remove(mediaId)
            prefs[Keys.MEDIA_NOTES_JSON] = encodeMediaNotesJson(current)
        }
    }

    suspend fun saveAudioDeviceProfile(profile: AudioDeviceProfile) {
        context.dataStore.edit { prefs ->
            val current = parseAudioDeviceProfilesJson(prefs[Keys.DEVICE_AUDIO_PROFILES_JSON].orEmpty()).toMutableMap()
            current[profile.type] = profile
            prefs[Keys.DEVICE_AUDIO_PROFILES_JSON] = encodeAudioDeviceProfilesJson(current)
        }
    }

    suspend fun setPlayerGenreDynamicColors(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PLAYER_GENRE_DYNAMIC_COLORS] = enabled }
    }

    suspend fun setPlayerShowTechTelemetry(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PLAYER_SHOW_TECH_TELEMETRY] = enabled }
    }

    suspend fun setPlayerExtrasButtonSubtle(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PLAYER_EXTRAS_BUTTON_SUBTLE] = enabled }
    }

    suspend fun setPlayerDirectBitExactDsp(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PLAYER_DIRECT_BIT_EXACT_DSP] = enabled }
    }

    suspend fun setPlayerExpressivePlayMorph(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PLAYER_EXPRESSIVE_PLAY_MORPH] = enabled }
    }

    suspend fun setLockScreenControlsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LOCK_SCREEN_CONTROLS_ENABLED] = enabled }
    }

    suspend fun setLockScreenShowArtwork(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LOCK_SCREEN_SHOW_ARTWORK] = enabled }
    }

    suspend fun setLockScreenShowPrismIdentity(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LOCK_SCREEN_SHOW_IDENTITY] = enabled }
    }

    suspend fun setLockScreenFullScreenEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LOCK_SCREEN_FULL_SCREEN_ENABLED] = enabled }
    }

    suspend fun setLockScreenShowClock(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LOCK_SCREEN_SHOW_CLOCK] = enabled }
    }

    suspend fun setLockScreenShowLyrics(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LOCK_SCREEN_SHOW_LYRICS] = enabled }
    }

    suspend fun setLockScreenBlurBackdrop(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LOCK_SCREEN_BLUR_BACKDROP] = enabled }
    }

    suspend fun setLockScreenCompactMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.LOCK_SCREEN_COMPACT_MODE] = enabled }
    }

    suspend fun setPipMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PIP_MUSIC_ENABLED] = enabled }
    }

    suspend fun setPipVideoEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PIP_VIDEO_ENABLED] = enabled }
    }

    suspend fun setPipPhotoEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PIP_PHOTO_ENABLED] = enabled }
    }

    suspend fun setCardStyle(style: com.example.model.MediaCardStyle) {
        context.dataStore.edit { it[Keys.CARD_STYLE] = style.name }
    }

    suspend fun setBackdropStyle(style: com.example.model.BackdropAnimationStyle) {
        context.dataStore.edit { it[Keys.BACKDROP_STYLE] = style.name }
    }

    suspend fun setMotionFeelStyle(style: com.example.model.MotionFeelStyle) {
        context.dataStore.edit { it[Keys.MOTION_FEEL_STYLE] = style.name }
    }

    suspend fun setHeaderTitleStyle(style: com.example.model.HeaderTitleStyle) {
        context.dataStore.edit { it[Keys.HEADER_TITLE_STYLE] = style.name }
    }

    suspend fun setSwipeGesturesEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SWIPE_GESTURES_ENABLED] = enabled }
    }

    suspend fun setShowFormatBadges(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SHOW_FORMAT_BADGES] = enabled }
    }

    suspend fun setAccentStrength(strength: Float) {
        context.dataStore.edit { it[Keys.ACCENT_STRENGTH] = strength.coerceIn(0.5f, 1.4f) }
    }

    // Beta 4.4 Pro Studio Mastering Engine Persistence
    suspend fun setProEqEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PRO_EQ_ENABLED] = enabled }
    }

    suspend fun setProTenBandEqualizer(preset: com.example.model.ProEqPreset, gainsDb: List<Float>) {
        if (gainsDb.size != 10) return
        context.dataStore.edit {
            it[Keys.PRO_EQ_PRESET] = preset.name
            it[Keys.PRO_TEN_BAND_GAINS] = gainsDb.joinToString(",") { g -> "%.2f".format(java.util.Locale.US, g.coerceIn(-15f, 15f)) }
        }
    }

    suspend fun setAutoStudioMasteringEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_STUDIO_MASTERING] = enabled }
    }

    suspend fun setTubeWarmth(amount: Float) {
        context.dataStore.edit { it[Keys.TUBE_WARMTH] = amount.coerceIn(0f, 1f) }
    }

    suspend fun setStereoWidth(width: Float) {
        context.dataStore.edit { it[Keys.STEREO_WIDTH] = width.coerceIn(0.5f, 2.0f) }
    }

    suspend fun setCrossfeedEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.CROSSFEED_ENABLED] = enabled }
    }

    suspend fun setSubBassPunch(amount: Float) {
        context.dataStore.edit { it[Keys.SUB_BASS_PUNCH] = amount.coerceIn(0f, 1f) }
    }

    suspend fun setAirBrilliance(amount: Float) {
        context.dataStore.edit { it[Keys.AIR_BRILLIANCE] = amount.coerceIn(0f, 1f) }
    }

    suspend fun setVocalClarity(amount: Float) {
        context.dataStore.edit { it[Keys.VOCAL_CLARITY] = amount.coerceIn(0f, 1f) }
    }

    suspend fun setCompressorStrength(strength: Float) {
        context.dataStore.edit { it[Keys.COMPRESSOR_STRENGTH] = strength.coerceIn(0f, 1f) }
    }

    suspend fun setTruePeakLimiterEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.TRUE_PEAK_LIMITER] = enabled }
    }

    suspend fun setNightModeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NIGHT_MODE_ENABLED] = enabled }
    }

    suspend fun setRumbleFilterHz(hz: Int) {
        context.dataStore.edit { it[Keys.RUMBLE_FILTER_HZ] = hz.coerceIn(0, 80) }
    }

    suspend fun setChannelSwapEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.CHANNEL_SWAP_ENABLED] = enabled }
    }

    suspend fun setHeadphoneProfile(profile: com.example.model.HeadphoneTuningProfile) {
        context.dataStore.edit { it[Keys.HEADPHONE_PROFILE] = profile.name }
    }

    suspend fun setDspBypassEnabled(bypass: Boolean) {
        context.dataStore.edit { it[Keys.DSP_BYPASS_ENABLED] = bypass }
    }

    suspend fun applyStudioSnapshotBatch(
        tenBandGainsDb: List<Float>,
        preset: com.example.model.ProEqPreset,
        tubeWarmth: Float,
        stereoWidth: Float,
        subBassPunch: Float,
        airBrilliance: Float,
        vocalClarity: Float,
        compressorStrength: Float,
        crossfeedEnabled: Boolean,
        rumbleFilterHz: Int,
        reverbEnvironment: ReverbEnvironment,
        nightModeEnabled: Boolean? = null,
        headphoneProfile: com.example.model.HeadphoneTuningProfile? = null,
    ) {
        context.dataStore.edit {
            it[Keys.PRO_EQ_ENABLED] = true
            it[Keys.PRO_EQ_PRESET] = preset.name
            if (tenBandGainsDb.size == 10) {
                it[Keys.PRO_TEN_BAND_GAINS] = tenBandGainsDb.joinToString(",") { g ->
                    "%.2f".format(java.util.Locale.US, g.coerceIn(-15f, 15f))
                }
            }
            it[Keys.TUBE_WARMTH] = tubeWarmth.coerceIn(0f, 1f)
            it[Keys.STEREO_WIDTH] = stereoWidth.coerceIn(0.5f, 2.0f)
            it[Keys.SUB_BASS_PUNCH] = subBassPunch.coerceIn(0f, 1f)
            it[Keys.AIR_BRILLIANCE] = airBrilliance.coerceIn(0f, 1f)
            it[Keys.VOCAL_CLARITY] = vocalClarity.coerceIn(0f, 1f)
            it[Keys.COMPRESSOR_STRENGTH] = compressorStrength.coerceIn(0f, 1f)
            it[Keys.CROSSFEED_ENABLED] = crossfeedEnabled
            it[Keys.RUMBLE_FILTER_HZ] = rumbleFilterHz.coerceIn(0, 80)
            it[Keys.REVERB_ENVIRONMENT] = reverbEnvironment.name
            if (nightModeEnabled != null) it[Keys.NIGHT_MODE_ENABLED] = nightModeEnabled
            if (headphoneProfile != null) it[Keys.HEADPHONE_PROFILE] = headphoneProfile.name
            it[Keys.DSP_BYPASS_ENABLED] = false
        }
    }

    val customStudioPresetsFlow: Flow<List<com.example.model.CustomStudioPreset>> = context.dataStore.data.map { prefs ->
        parseCustomStudioPresetsJson(prefs[Keys.CUSTOM_STUDIO_PRESETS_JSON].orEmpty())
    }

    suspend fun saveCustomStudioPreset(preset: com.example.model.CustomStudioPreset) {
        context.dataStore.edit { prefs ->
            val current = parseCustomStudioPresetsJson(prefs[Keys.CUSTOM_STUDIO_PRESETS_JSON].orEmpty()).toMutableList()
            current.removeAll { it.id == preset.id || it.name.equals(preset.name, ignoreCase = true) }
            current.add(0, preset)
            prefs[Keys.CUSTOM_STUDIO_PRESETS_JSON] = encodeCustomStudioPresetsJson(current.take(24))
        }
    }

    suspend fun deleteCustomStudioPreset(id: String) {
        context.dataStore.edit { prefs ->
            val current = parseCustomStudioPresetsJson(prefs[Keys.CUSTOM_STUDIO_PRESETS_JSON].orEmpty())
                .filterNot { it.id == id }
            prefs[Keys.CUSTOM_STUDIO_PRESETS_JSON] = encodeCustomStudioPresetsJson(current)
        }
    }

    private fun encodeCustomStudioPresetsJson(presets: List<com.example.model.CustomStudioPreset>): String {
        val arr = JSONArray()
        presets.forEach { p ->
            arr.put(
                JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("gains", p.tenBandGainsDb.joinToString(",") { g -> "%.2f".format(java.util.Locale.US, g) })
                    put("warmth", p.tubeWarmth.toDouble())
                    put("width", p.stereoWidth.toDouble())
                    put("subBass", p.subBassPunch.toDouble())
                    put("air", p.airBrilliance.toDouble())
                    put("vocal", p.vocalClarity.toDouble())
                    put("comp", p.compressorStrength.toDouble())
                    put("crossfeed", p.crossfeedEnabled)
                    put("night", p.nightModeEnabled)
                    put("rumble", p.rumbleFilterHz)
                    put("hp", p.headphoneProfile.name)
                    put("reverb", p.reverbEnvironment.name)
                    put("ts", p.createdAtMs)
                }
            )
        }
        return arr.toString()
    }

    private fun parseCustomStudioPresetsJson(raw: String): List<com.example.model.CustomStudioPreset> {
        if (raw.isBlank()) return emptyList()
        return try {
            val arr = JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val o = arr.optJSONObject(i) ?: continue
                    val id = o.optString("id")
                    val name = o.optString("name")
                    val gains = o.optString("gains")
                        .split(",")
                        .mapNotNull { it.toFloatOrNull()?.coerceIn(-15f, 15f) }
                        .takeIf { it.size == 10 }
                        ?: com.example.model.ProEqPreset.FLAT.gainsDb
                    if (id.isNotBlank() && name.isNotBlank()) {
                        add(
                            com.example.model.CustomStudioPreset(
                                id = id,
                                name = name,
                                tenBandGainsDb = gains,
                                tubeWarmth = o.optDouble("warmth", 0.0).toFloat().coerceIn(0f, 1f),
                                stereoWidth = o.optDouble("width", 1.0).toFloat().coerceIn(0.5f, 2.0f),
                                subBassPunch = o.optDouble("subBass", 0.0).toFloat().coerceIn(0f, 1f),
                                airBrilliance = o.optDouble("air", 0.0).toFloat().coerceIn(0f, 1f),
                                vocalClarity = o.optDouble("vocal", 0.0).toFloat().coerceIn(0f, 1f),
                                compressorStrength = o.optDouble("comp", 0.0).toFloat().coerceIn(0f, 1f),
                                crossfeedEnabled = o.optBoolean("crossfeed", false),
                                nightModeEnabled = o.optBoolean("night", false),
                                rumbleFilterHz = o.optInt("rumble", 0).coerceIn(0, 80),
                                headphoneProfile = runCatching {
                                    com.example.model.HeadphoneTuningProfile.valueOf(
                                        o.optString("hp", com.example.model.HeadphoneTuningProfile.NEUTRAL_STUDIO.name)
                                    )
                                }.getOrDefault(com.example.model.HeadphoneTuningProfile.NEUTRAL_STUDIO),
                                reverbEnvironment = runCatching {
                                    ReverbEnvironment.valueOf(o.optString("reverb", ReverbEnvironment.NONE.name))
                                }.getOrDefault(ReverbEnvironment.NONE),
                                createdAtMs = o.optLong("ts", 0L),
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    val trashedItemsFlow: Flow<List<com.example.model.TrashedMediaItem>> = context.dataStore.data.map { prefs ->
        parseTrashedItemsJson(prefs[Keys.RECYCLE_BIN_JSON].orEmpty())
    }

    suspend fun saveTrashedItems(items: List<com.example.model.TrashedMediaItem>) {
        context.dataStore.edit { prefs ->
            prefs[Keys.RECYCLE_BIN_JSON] = encodeTrashedItemsJson(items)
        }
    }

    companion object {
        const val RECYCLE_BIN_RETENTION_MS: Long = 30L * 24L * 60L * 60L * 1000L

        fun encodeTrashedItemsJson(items: List<com.example.model.TrashedMediaItem>): String {
            val arr = JSONArray()
            items.forEach { t ->
                arr.put(
                    JSONObject().apply {
                        put("id", t.id)
                        put("title", t.title)
                        put("subtitle", t.subtitle)
                        put("type", t.mediaType.name)
                        put("deletedAt", t.deletedAtMs)
                        t.audioTrack?.let { a ->
                            put("audio", JSONObject().apply {
                                put("id", a.id)
                                put("title", a.title)
                                put("artist", a.artist)
                                put("album", a.album)
                                put("uri", a.uriString)
                                put("ext", a.extension)
                                put("mime", a.mimeType)
                                put("dur", a.durationMs)
                                put("size", a.sizeBytes)
                                put("folder", a.folderName)
                                put("fav", a.isFavorite)
                                put("demo", a.isDemo)
                            })
                        }
                        t.videoItem?.let { v ->
                            put("video", JSONObject().apply {
                                put("id", v.id)
                                put("title", v.title)
                                put("uri", v.uriString)
                                put("ext", v.extension)
                                put("mime", v.mimeType)
                                put("dur", v.durationMs)
                                put("size", v.sizeBytes)
                                put("res", v.resolution)
                                put("folder", v.folderName)
                                put("fav", v.isFavorite)
                                put("demo", v.isDemo)
                            })
                        }
                        t.photoItem?.let { p ->
                            put("photo", JSONObject().apply {
                                put("id", p.id)
                                put("title", p.title)
                                put("uri", p.uriString)
                                put("ext", p.extension)
                                put("mime", p.mimeType)
                                put("size", p.sizeBytes)
                                put("w", p.width)
                                put("h", p.height)
                                put("folder", p.folderName)
                                put("fav", p.isFavorite)
                                put("demo", p.isDemo)
                            })
                        }
                    }
                )
            }
            return arr.toString()
        }

        fun parseTrashedItemsJson(
            raw: String,
            nowMs: Long = System.currentTimeMillis(),
        ): List<com.example.model.TrashedMediaItem> {
            if (raw.isBlank()) return emptyList()
            return try {
                val arr = JSONArray(raw)
                val cutoff = nowMs - RECYCLE_BIN_RETENTION_MS
                buildList {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        val deletedAt = o.optLong("deletedAt", nowMs)
                        if (deletedAt < cutoff) continue
                        val id = o.optString("id")
                        if (id.isBlank()) continue
                        val type = runCatching {
                            RenameMediaType.valueOf(o.optString("type", RenameMediaType.AUDIO.name))
                        }.getOrDefault(RenameMediaType.AUDIO)
                        val audioObj = o.optJSONObject("audio")
                        val videoObj = o.optJSONObject("video")
                        val photoObj = o.optJSONObject("photo")
                        add(
                            com.example.model.TrashedMediaItem(
                                id = id,
                                title = o.optString("title", ""),
                                subtitle = o.optString("subtitle", ""),
                                mediaType = type,
                                deletedAtMs = deletedAt,
                                audioTrack = audioObj?.let { a ->
                                    com.example.model.AudioTrack(
                                        id = a.optString("id", id),
                                        title = a.optString("title", ""),
                                        artist = a.optString("artist", ""),
                                        album = a.optString("album", ""),
                                        uriString = a.optString("uri", ""),
                                        extension = a.optString("ext", "MP3"),
                                        mimeType = a.optString("mime", "audio/mpeg"),
                                        durationMs = a.optLong("dur", 0L),
                                        sizeBytes = a.optLong("size", 0L),
                                        folderName = a.optString("folder", ""),
                                        isFavorite = a.optBoolean("fav", false),
                                        isDemo = a.optBoolean("demo", false),
                                    )
                                },
                                videoItem = videoObj?.let { v ->
                                    com.example.model.VideoItem(
                                        id = v.optString("id", id),
                                        title = v.optString("title", ""),
                                        uriString = v.optString("uri", ""),
                                        extension = v.optString("ext", "MP4"),
                                        mimeType = v.optString("mime", "video/mp4"),
                                        durationMs = v.optLong("dur", 0L),
                                        sizeBytes = v.optLong("size", 0L),
                                        resolution = v.optString("res", "HD"),
                                        folderName = v.optString("folder", ""),
                                        isFavorite = v.optBoolean("fav", false),
                                        isDemo = v.optBoolean("demo", false),
                                    )
                                },
                                photoItem = photoObj?.let { p ->
                                    com.example.model.PhotoItem(
                                        id = p.idOrFallback(id),
                                        title = p.optString("title", ""),
                                        uriString = p.optString("uri", ""),
                                        extension = p.optString("ext", "JPG"),
                                        mimeType = p.optString("mime", "image/jpeg"),
                                        sizeBytes = p.optLong("size", 0L),
                                        width = p.optInt("w", 0),
                                        height = p.optInt("h", 0),
                                        folderName = p.optString("folder", ""),
                                        isFavorite = p.optBoolean("fav", false),
                                        isDemo = p.optBoolean("demo", false),
                                    )
                                }
                            )
                        )
                    }
                }
            } catch (_: Exception) {
                emptyList()
            }
        }

        private fun JSONObject.idOrFallback(fallback: String): String = optString("id").ifBlank { fallback }
        fun normalizeNavTabOrder(order: List<MediaTab>): List<MediaTab> {
            val clean = order.distinct().toMutableList()
            MediaTab.entries.forEach { if (it !in clean) clean.add(it) }
            return clean
        }

        fun parseNavTabOrder(raw: String?): List<MediaTab> {
            if (raw.isNullOrBlank()) return MediaTab.entries.toList()
            val parsed = raw.split(",").mapNotNull { token ->
                runCatching { MediaTab.valueOf(token.trim()) }.getOrNull()
            }
            return normalizeNavTabOrder(parsed)
        }

        fun encodeMediaNotesJson(notes: Map<String, MediaNote>): String {
            val arr = JSONArray()
            notes.values.forEach { n ->
                arr.put(
                    JSONObject().apply {
                        put("id", n.mediaId)
                        put("title", n.mediaTitle)
                        put("type", n.mediaType.name)
                        put("text", n.noteText)
                        put("pos", n.timestampMs)
                        put("ts", n.updatedAtMs)
                    }
                )
            }
            return arr.toString()
        }

        fun parseMediaNotesJson(raw: String): Map<String, MediaNote> {
            if (raw.isBlank()) return emptyMap()
            return try {
                val arr = JSONArray(raw)
                buildMap {
                    for (i in 0 until arr.length()) {
                        val obj = arr.optJSONObject(i) ?: continue
                        val id = obj.optString("id")
                        val text = obj.optString("text")
                        if (id.isNotBlank() && text.isNotBlank()) {
                            put(
                                id,
                                MediaNote(
                                    mediaId = id,
                                    mediaTitle = obj.optString("title", ""),
                                    mediaType = runCatching {
                                        RenameMediaType.valueOf(obj.optString("type", RenameMediaType.AUDIO.name))
                                    }.getOrDefault(RenameMediaType.AUDIO),
                                    noteText = text,
                                    timestampMs = obj.optLong("pos", 0L),
                                    updatedAtMs = obj.optLong("ts", 0L),
                                )
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                emptyMap()
            }
        }

        fun encodeAudioDeviceProfilesJson(profiles: Map<AudioOutputProfileType, AudioDeviceProfile>): String {
            val arr = JSONArray()
            profiles.values.forEach { p ->
                arr.put(
                    JSONObject().apply {
                        put("type", p.type.name)
                        put("eq", p.eqPreset.name)
                        put("bass", p.bassBoost.toDouble())
                        put("reverb", p.reverb.name)
                    }
                )
            }
            return arr.toString()
        }

        fun parseAudioDeviceProfilesJson(raw: String): Map<AudioOutputProfileType, AudioDeviceProfile> {
            val defaults = mutableMapOf(
                AudioOutputProfileType.SPEAKER to AudioDeviceProfile(
                    type = AudioOutputProfileType.SPEAKER,
                    eqPreset = EqPreset.VOCAL_BOOST,
                    bassBoost = 0.15f,
                    reverb = ReverbEnvironment.NONE
                ),
                AudioOutputProfileType.WIRED_HEADSET to AudioDeviceProfile(
                    type = AudioOutputProfileType.WIRED_HEADSET,
                    eqPreset = EqPreset.FLAT,
                    bassBoost = 0.25f,
                    reverb = ReverbEnvironment.STUDIO
                ),
                AudioOutputProfileType.BLUETOOTH to AudioDeviceProfile(
                    type = AudioOutputProfileType.BLUETOOTH,
                    eqPreset = EqPreset.DEEP_BASS,
                    bassBoost = 0.35f,
                    reverb = ReverbEnvironment.SPATIAL_3D
                ),
            )
            if (raw.isBlank()) return defaults
            return try {
                val arr = JSONArray(raw)
                for (i in 0 until arr.length()) {
                    val o = arr.optJSONObject(i) ?: continue
                    val t = runCatching { AudioOutputProfileType.valueOf(o.optString("type")) }.getOrNull() ?: continue
                    defaults[t] = AudioDeviceProfile(
                        type = t,
                        eqPreset = runCatching { EqPreset.valueOf(o.optString("eq")) }.getOrDefault(EqPreset.FLAT),
                        bassBoost = o.optDouble("bass", 0.0).toFloat().coerceIn(0f, 1f),
                        reverb = runCatching { ReverbEnvironment.valueOf(o.optString("reverb")) }.getOrDefault(ReverbEnvironment.NONE),
                    )
                }
                defaults
            } catch (_: Exception) {
                defaults
            }
        }
        fun encodePlaylistsJson(playlists: List<UserPlaylist>): String {
            val arr = JSONArray()
            playlists.forEach { pl ->
                val obj = JSONObject()
                obj.put("id", pl.id)
                obj.put("name", pl.name)
                val idsArr = JSONArray()
                pl.trackIds.forEach { idsArr.put(it) }
                obj.put("trackIds", idsArr)
                arr.put(obj)
            }
            return arr.toString()
        }

        fun parsePlaylistsJson(raw: String): List<UserPlaylist> {
            return try {
                val arr = JSONArray(raw)
                buildList {
                    for (i in 0 until arr.length()) {
                        val obj = arr.optJSONObject(i) ?: continue
                        val id = obj.optString("id")
                        val name = obj.optString("name")
                        val idsArr = obj.optJSONArray("trackIds")
                        val trackIds = buildList {
                            if (idsArr != null) {
                                for (j in 0 until idsArr.length()) {
                                    add(idsArr.optString(j))
                                }
                            }
                        }
                        if (id.isNotBlank() && name.isNotBlank()) {
                            add(UserPlaylist(id = id, name = name, trackIds = trackIds))
                        }
                    }
                }
            } catch (_: Exception) {
                emptyList()
            }
        }

        fun encodeBookmarksJson(bookmarks: List<MediaBookmark>): String {
            val arr = JSONArray()
            bookmarks.forEach { b ->
                arr.put(
                    JSONObject().apply {
                        put("id", b.id)
                        put("mediaId", b.mediaId)
                        put("pos", b.positionMs)
                        put("label", b.label)
                        put("ts", b.createdAtMs)
                    }
                )
            }
            return arr.toString()
        }

        fun parseBookmarksJson(raw: String): List<MediaBookmark> {
            return try {
                val arr = JSONArray(raw)
                buildList {
                    for (i in 0 until arr.length()) {
                        val obj = arr.optJSONObject(i) ?: continue
                        val id = obj.optString("id")
                        val mediaId = obj.optString("mediaId")
                        if (id.isNotBlank() && mediaId.isNotBlank()) {
                            add(
                                MediaBookmark(
                                    id = id,
                                    mediaId = mediaId,
                                    positionMs = obj.optLong("pos", 0L),
                                    label = obj.optString("label", ""),
                                    createdAtMs = obj.optLong("ts", 0L),
                                )
                            )
                        }
                    }
                }
            } catch (_: Exception) {
                emptyList()
            }
        }

        fun encodeTagOverridesJson(overrides: Map<String, TrackTagOverride>): String {
            val json = JSONObject()
            overrides.forEach { (k, v) ->
                json.put(
                    k,
                    JSONObject().apply {
                        put("title", v.title)
                        put("artist", v.artist)
                        put("album", v.album)
                    }
                )
            }
            return json.toString()
        }

        fun parseTagOverridesJson(raw: String): Map<String, TrackTagOverride> {
            return try {
                val json = JSONObject(raw)
                buildMap {
                    for (key in json.keys()) {
                        val obj = json.optJSONObject(key) ?: continue
                        put(
                            key,
                            TrackTagOverride(
                                title = obj.optString("title", ""),
                                artist = obj.optString("artist", ""),
                                album = obj.optString("album", "")
                            )
                        )
                    }
                }
            } catch (_: Exception) {
                emptyMap()
            }
        }

        fun encodeWatchedFoldersJson(folders: List<WatchedFolder>): String {
            val arr = JSONArray()
            folders.forEach { f ->
                arr.put(
                    JSONObject().apply {
                        put("uri", f.uriString)
                        put("name", f.displayName)
                        put("enabled", f.enabled)
                        put("audio", f.audioCount)
                        put("video", f.videoCount)
                        put("photo", f.photoCount)
                        put("scanned", f.lastScannedMs)
                    }
                )
            }
            return arr.toString()
        }

        fun parseWatchedFoldersJson(raw: String): List<WatchedFolder> {
            return try {
                val arr = JSONArray(raw)
                buildList {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        val uri = o.optString("uri")
                        if (uri.isBlank()) continue
                        add(
                            WatchedFolder(
                                uriString = uri,
                                displayName = o.optString("name", "Folder"),
                                enabled = o.optBoolean("enabled", true),
                                audioCount = o.optInt("audio", 0),
                                videoCount = o.optInt("video", 0),
                                photoCount = o.optInt("photo", 0),
                                lastScannedMs = o.optLong("scanned", 0L),
                            )
                        )
                    }
                }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }
}
