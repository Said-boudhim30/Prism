package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppIconSwitcher
import com.example.data.CachedMediaLookup
import com.example.data.DecodedThemePreset
import com.example.data.DynamicShortcutsHelper
import com.example.data.LocalWifiTransferServer
import com.example.data.MediaKnowledgeEngine
import com.example.data.MediaScanner
import com.example.data.MediaToolsEngine
import com.example.data.QuranHadithCatalog
import com.example.data.ScannedMediaResult
import com.example.data.SettingsRepository
import com.example.data.WifiTransferStatus
import com.example.localization.stringsFor
import com.example.model.AccentPalette
import com.example.model.AppIconVariant
import com.example.model.AppLanguage
import com.example.model.AppSettings
import com.example.model.AudioDeviceProfile
import com.example.model.AudioOutputProfileType
import com.example.model.AudioTrack
import com.example.model.DuplicateGroup
import com.example.model.DuplicateMediaType
import com.example.model.EdgeDockSide
import com.example.model.EqPreset
import com.example.model.FolderCustomization
import com.example.model.IconPackStyle
import com.example.model.IntruderAttempt
import com.example.model.LiquidGlassStyle
import com.example.model.ListeningStatsSnapshot
import com.example.model.MaterialContrastLevel
import com.example.model.MaterialFontStyle
import com.example.model.MediaBookmark
import com.example.model.MediaDetailsInfo
import com.example.model.MediaKnowledgeReport
import com.example.model.MediaNote
import com.example.model.MediaSortOption
import com.example.model.MediaTab
import com.example.model.MediaViewMode
import com.example.model.MorningAlarmConfig
import com.example.model.MusicBrowseMode
import com.example.model.NavBarStyle
import com.example.model.NavTransitionStyle
import com.example.model.PhotoItem
import com.example.model.PlaybackUiState
import com.example.model.PlayerArtworkStyle
import com.example.model.QueueSortOption
import com.example.model.QuranKhatmaProgress
import com.example.model.RenameMediaType
import com.example.model.RenameTarget
import com.example.model.RepeatMode
import com.example.model.ReverbEnvironment
import com.example.model.ScanProgressState
import com.example.model.SecondaryPaneMode
import com.example.model.SettingsLayoutMode
import com.example.model.SlideshowTransitionStyle
import com.example.model.SmartMusicMix
import com.example.model.SpectrumVisualStyle
import com.example.model.SubtitleColorStyle
import com.example.model.SurfaceDepthStyle
import com.example.model.TrackTagOverride
import com.example.model.TrashedMediaItem
import com.example.model.UiDensity
import com.example.model.UserPlaylist
import com.example.model.VaultItem
import com.example.model.VideoItem
import com.example.model.WatchedFolder
import com.example.player.AudioPlaybackController
import com.example.player.DeviceDriverProfile
import com.example.player.DeviceMediaDriverEngine
import com.example.player.MediaNotificationBridge
import com.example.player.MediaNotificationManager
import com.example.player.MorningAlarmScheduler
import com.example.security.AppSecurityVerifier
import com.example.security.ContentPurityGuard
import com.example.security.PinCrypto
import com.example.security.PrivateVaultManager
import com.example.security.SecurityAuditInfo
import com.example.widget.PrismExpressiveCardWidgetProvider
import com.example.widget.PrismMediaWidgetProvider
import com.example.widget.WidgetPlaybackBridge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MediaViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepo = SettingsRepository(application)

    private val _selectedTab = MutableStateFlow(MediaTab.MUSIC)
    val selectedTab: StateFlow<MediaTab> = _selectedTab.asStateFlow()

    private val _audioSearchQuery = MutableStateFlow("")
    val audioSearchQuery: StateFlow<String> = _audioSearchQuery.asStateFlow()

    private val _selectedAudioFormat = MutableStateFlow("All")
    val selectedAudioFormat: StateFlow<String> = _selectedAudioFormat.asStateFlow()

    private val _favoritesOnly = MutableStateFlow(false)
    val favoritesOnly: StateFlow<Boolean> = _favoritesOnly.asStateFlow()

    private val _audioSortOption = MutableStateFlow(MediaSortOption.NEWEST)
    val audioSortOption: StateFlow<MediaSortOption> = _audioSortOption.asStateFlow()

    private val _selectedSmartMix = MutableStateFlow(SmartMusicMix.ALL_TRACKS)
    val selectedSmartMix: StateFlow<SmartMusicMix> = _selectedSmartMix.asStateFlow()

    private val _discoverySeed = MutableStateFlow(System.currentTimeMillis())

    private val _videoSearchQuery = MutableStateFlow("")
    val videoSearchQuery: StateFlow<String> = _videoSearchQuery.asStateFlow()

    private val _videoFavoritesOnly = MutableStateFlow(false)
    val videoFavoritesOnly: StateFlow<Boolean> = _videoFavoritesOnly.asStateFlow()

    private val _videoSortOption = MutableStateFlow(MediaSortOption.NEWEST)
    val videoSortOption: StateFlow<MediaSortOption> = _videoSortOption.asStateFlow()

    private val _photoSearchQuery = MutableStateFlow("")
    val photoSearchQuery: StateFlow<String> = _photoSearchQuery.asStateFlow()

    private val _photoFavoritesOnly = MutableStateFlow(false)
    val photoFavoritesOnly: StateFlow<Boolean> = _photoFavoritesOnly.asStateFlow()

    private val _photoSortOption = MutableStateFlow(MediaSortOption.NEWEST)
    val photoSortOption: StateFlow<MediaSortOption> = _photoSortOption.asStateFlow()

    private val _selectedPlaylistId = MutableStateFlow<String?>(null)
    val selectedPlaylistId: StateFlow<String?> = _selectedPlaylistId.asStateFlow()

    private val _playlistTrackTarget = MutableStateFlow<AudioTrack?>(null)
    val playlistTrackTarget: StateFlow<AudioTrack?> = _playlistTrackTarget.asStateFlow()

    private val _renameTarget = MutableStateFlow<RenameTarget?>(null)
    val renameTarget: StateFlow<RenameTarget?> = _renameTarget.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(ScanProgressState())
    val scanProgress: StateFlow<ScanProgressState> = _scanProgress.asStateFlow()

    private val _showFirstLaunchFolderWizard = MutableStateFlow(false)
    val showFirstLaunchFolderWizard: StateFlow<Boolean> = _showFirstLaunchFolderWizard.asStateFlow()

    private val _selectedMusicFolder = MutableStateFlow<String?>(null)
    val selectedMusicFolder: StateFlow<String?> = _selectedMusicFolder.asStateFlow()

    private val _selectedVideoFolder = MutableStateFlow<String?>(null)
    val selectedVideoFolder: StateFlow<String?> = _selectedVideoFolder.asStateFlow()

    private val _selectedPhotoFolder = MutableStateFlow<String?>(null)
    val selectedPhotoFolder: StateFlow<String?> = _selectedPhotoFolder.asStateFlow()

    private val _editTagTarget = MutableStateFlow<AudioTrack?>(null)
    val editTagTarget: StateFlow<AudioTrack?> = _editTagTarget.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _rawAudio = MutableStateFlow<List<AudioTrack>>(emptyList())
    private val _rawVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    private val _rawPhotos = MutableStateFlow<List<PhotoItem>>(emptyList())

    private val _activeVideo = MutableStateFlow<VideoItem?>(null)
    val activeVideo: StateFlow<VideoItem?> = _activeVideo.asStateFlow()

    private val _activeVideoInitialPosMs = MutableStateFlow(0L)
    val activeVideoInitialPosMs: StateFlow<Long> = _activeVideoInitialPosMs.asStateFlow()

    private val _floatingVideo = MutableStateFlow<VideoItem?>(null)
    val floatingVideo: StateFlow<VideoItem?> = _floatingVideo.asStateFlow()

    private val _floatingVideoPosMs = MutableStateFlow(0L)
    val floatingVideoPosMs: StateFlow<Long> = _floatingVideoPosMs.asStateFlow()

    private val _floatingPhoto = MutableStateFlow<PhotoItem?>(null)
    val floatingPhoto: StateFlow<PhotoItem?> = _floatingPhoto.asStateFlow()

    private val _isVideoPlaying = MutableStateFlow(false)
    val isVideoPlaying: StateFlow<Boolean> = _isVideoPlaying.asStateFlow()

    private val _multiWindowEnabled = MutableStateFlow(false)
    val multiWindowEnabled: StateFlow<Boolean> = _multiWindowEnabled.asStateFlow()

    private val _secondaryPaneMode = MutableStateFlow(SecondaryPaneMode.EQUALIZER)
    val secondaryPaneMode: StateFlow<SecondaryPaneMode> = _secondaryPaneMode.asStateFlow()

    private val _shuffleEnabled = MutableStateFlow(false)
    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)

    private val _activePhotoIndex = MutableStateFlow<Int?>(null)
    val activePhotoIndex: StateFlow<Int?> = _activePhotoIndex.asStateFlow()

    private val _detailsInfo = MutableStateFlow<MediaDetailsInfo?>(null)
    val detailsInfo: StateFlow<MediaDetailsInfo?> = _detailsInfo.asStateFlow()

    private val _isPlayerExpanded = MutableStateFlow(false)
    private val _showEqSheet = MutableStateFlow(false)
    val showEqSheet: StateFlow<Boolean> = _showEqSheet.asStateFlow()

    private val savedPositionsCache = mutableMapOf<String, Long>()
    private var nextAddedOrder = 1L

    val settings: StateFlow<AppSettings> = settingsRepo.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    val playlists: StateFlow<List<UserPlaylist>> = settingsRepo.playlistsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val customLyrics: StateFlow<Map<String, String>> = settingsRepo.customLyricsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyMap()
    )

    val mediaNotes: StateFlow<Map<String, MediaNote>> = settingsRepo.mediaNotesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyMap()
    )

    val audioDeviceProfiles: StateFlow<Map<AudioOutputProfileType, AudioDeviceProfile>> =
        settingsRepo.audioDeviceProfilesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SettingsRepository.parseAudioDeviceProfilesJson("")
        )

    private val _activeAudioOutput = MutableStateFlow(MediaToolsEngine.detectCurrentAudioOutputType(application))
    val activeAudioOutput: StateFlow<AudioOutputProfileType> = _activeAudioOutput.asStateFlow()

    private val _trashedItems = MutableStateFlow<List<TrashedMediaItem>>(emptyList())
    val trashedItems: StateFlow<List<TrashedMediaItem>> = _trashedItems.asStateFlow()

    private val _musicBrowseMode = MutableStateFlow(MusicBrowseMode.TRACKS)
    val musicBrowseMode: StateFlow<MusicBrowseMode> = _musicBrowseMode.asStateFlow()

    // Real Online Knowledge & Encyclopedia Reports (keyed by mediaId or uriString)
    private val _knowledgeReports = MutableStateFlow<Map<String, MediaKnowledgeReport>>(emptyMap())
    val knowledgeReports: StateFlow<Map<String, MediaKnowledgeReport>> = _knowledgeReports.asStateFlow()

    private val _enrichingIds = MutableStateFlow<Set<String>>(emptySet())
    val enrichingIds: StateFlow<Set<String>> = _enrichingIds.asStateFlow()

    private val _securityAudit = MutableStateFlow(AppSecurityVerifier.inspect(application))
    val securityAudit: StateFlow<SecurityAuditInfo> = _securityAudit.asStateFlow()

    private val _driverProfile = MutableStateFlow(DeviceMediaDriverEngine.probe(application))
    val driverProfile: StateFlow<DeviceDriverProfile> = _driverProfile.asStateFlow()

    private val _showTamperAlertDialog = MutableStateFlow(false)
    val showTamperAlertDialog: StateFlow<Boolean> = _showTamperAlertDialog.asStateFlow()

    // Live Reorderable Active Playing Queue (track IDs in custom order while playing)
    private val _customQueueIds = MutableStateFlow<List<String>>(emptyList())

    // Batch Playlist Picker Target (when adding multiple selected tracks to a playlist)
    private val _batchPlaylistTrackIds = MutableStateFlow<List<String>>(emptyList())
    val batchPlaylistTrackIds: StateFlow<List<String>> = _batchPlaylistTrackIds.asStateFlow()

    // Anti-Intruder App Lock PIN Shield state
    val intruderLog: StateFlow<List<IntruderAttempt>> = settingsRepo.intruderLogFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )
    private var cachedPinCredentials: Pair<String, String>? = null
    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _failedPinAttempts = MutableStateFlow(0)
    val failedPinAttempts: StateFlow<Int> = _failedPinAttempts.asStateFlow()

    private val _lockoutUntilMs = MutableStateFlow(0L)
    val lockoutUntilMs: StateFlow<Long> = _lockoutUntilMs.asStateFlow()

    val watchedFolders: StateFlow<List<WatchedFolder>> = settingsRepo.watchedFoldersFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val bookmarks: StateFlow<List<MediaBookmark>> = settingsRepo.bookmarksFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val audioTracks: StateFlow<List<AudioTrack>> = combine(
        _rawAudio,
        settingsRepo.favoriteIdsFlow,
        settingsRepo.customTitlesFlow,
        settingsRepo.tagOverridesFlow,
        settingsRepo.playStatsFlow,
        settings,
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val tracks = values[0] as List<AudioTrack>
        @Suppress("UNCHECKED_CAST")
        val favs = values[1] as Set<String>
        @Suppress("UNCHECKED_CAST")
        val customTitles = values[2] as Map<String, String>
        @Suppress("UNCHECKED_CAST")
        val tagOverrides = values[3] as Map<String, TrackTagOverride>
        @Suppress("UNCHECKED_CAST")
        val playStats = values[4] as Map<String, Pair<Int, Long>>
        val currentSettings = values[5] as AppSettings

        tracks
            .filter { !currentSettings.filterShortAudio || it.durationMs == 0L || it.durationMs >= 5_000L }
            .map { track ->
                val tag = tagOverrides[track.id]
                val renamed = tag?.title?.takeIf { it.isNotBlank() }
                    ?: customTitles[track.id]?.takeIf { it.isNotBlank() }
                val stats = playStats[track.id]
                track.copy(
                    title = renamed ?: track.title,
                    artist = tag?.artist?.takeIf { it.isNotBlank() } ?: track.artist,
                    album = tag?.album?.takeIf { it.isNotBlank() } ?: track.album,
                    artworkUriString = tag?.artworkUriString?.takeIf { it.isNotBlank() } ?: track.artworkUriString,
                    isFavorite = favs.contains(track.id),
                    playCount = stats?.first ?: track.playCount,
                    lastPlayedMs = stats?.second ?: track.lastPlayedMs,
                )
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val folderCustomizations: StateFlow<Map<String, FolderCustomization>> =
        settingsRepo.folderCustomizationsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val quranKhatmaProgress: StateFlow<QuranKhatmaProgress> =
        settingsRepo.quranKhatmaFlow.stateIn(viewModelScope, SharingStarted.Eagerly, QuranKhatmaProgress())

    val morningAlarmConfig: StateFlow<MorningAlarmConfig> =
        settingsRepo.morningAlarmFlow.stateIn(viewModelScope, SharingStarted.Eagerly, MorningAlarmConfig())

    private val _vaultItems = MutableStateFlow<List<VaultItem>>(emptyList())
    val vaultItems: StateFlow<List<VaultItem>> = _vaultItems.asStateFlow()

    val listeningStats: StateFlow<ListeningStatsSnapshot> = combine(
        audioTracks,
        settingsRepo.dailyListeningMinutesFlow,
    ) { tracks, dailyMap ->
        val totalPlays = tracks.sumOf { it.playCount }
        val dailyTotal = dailyMap.values.sum()
        val estimatedMins = dailyTotal.coerceAtLeast(
            tracks.sumOf { ((it.durationMs / 60_000L).toInt().coerceAtLeast(1)) * it.playCount }
        )
        val topTracks = tracks.filter { it.playCount > 0 }
            .sortedByDescending { it.playCount }
            .take(5)
            .map { it to it.playCount }
        val topArtists = tracks.groupBy { it.artist.ifBlank { "Local Artist" } }
            .mapValues { (_, list) -> list.sumOf { it.playCount.coerceAtLeast(1) } }
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key to it.value }
        val formatCounts = tracks.groupingBy { it.extension.uppercase().ifBlank { "AUDIO" } }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .associate { it.key to it.value }
        val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
        val cal = java.util.Calendar.getInstance()
        val last7 = (6 downTo 0).map { offset ->
            val c = (cal.clone() as java.util.Calendar).apply { add(java.util.Calendar.DAY_OF_YEAR, -offset) }
            val key = String.format(
                java.util.Locale.US,
                "%04d-%02d-%02d",
                c.get(java.util.Calendar.YEAR),
                c.get(java.util.Calendar.MONTH) + 1,
                c.get(java.util.Calendar.DAY_OF_MONTH)
            )
            val labelIdx = (c.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
            dayLabels[labelIdx] to (dailyMap[key] ?: 0)
        }
        ListeningStatsSnapshot(
            totalPlays = totalPlays,
            totalMinutes = estimatedMins,
            topTracks = topTracks,
            topArtists = topArtists,
            formatCounts = formatCounts,
            dailyMinutes = last7,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ListeningStatsSnapshot())

    val availableMusicFolders: StateFlow<List<String>> = combine(_rawAudio, folderCustomizations) { list, customs ->
        list.mapNotNull { it.folderName.takeIf(String::isNotBlank) }
            .distinct()
            .sortedWith(compareByDescending<String> { customs[it]?.pinned == true }.thenBy { it.lowercase() })
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val recentlyPlayedTracks: StateFlow<List<AudioTrack>> = combine(audioTracks, settings) { list, _ ->
        list.filter { it.lastPlayedMs > 0L }
            .sortedByDescending { it.lastPlayedMs }
            .take(10)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val filteredAudioTracks: StateFlow<List<AudioTrack>> = combine(
        audioTracks,
        _audioSearchQuery,
        _selectedAudioFormat,
        _favoritesOnly,
        _audioSortOption,
        _selectedPlaylistId,
        playlists,
        _selectedMusicFolder,
        _selectedSmartMix,
        _discoverySeed,
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val tracks = values[0] as List<AudioTrack>
        val query = values[1] as String
        val format = values[2] as String
        val favOnly = values[3] as Boolean
        val sortOption = values[4] as MediaSortOption
        val playlistId = values[5] as String?
        @Suppress("UNCHECKED_CAST")
        val allPlaylists = values[6] as List<UserPlaylist>
        val folderFilter = values[7] as String?
        val smartMix = values[8] as SmartMusicMix
        val seed = values[9] as Long

        val playlistTrackSet = playlistId?.let { id ->
            allPlaylists.firstOrNull { it.id == id }?.trackIds?.toSet()
        }

        val q = query.trim().lowercase()
        val filtered = tracks.filter { track ->
            val matchesFormat = format == "All" || track.extension.equals(format, ignoreCase = true)
            val matchesFav = (!favOnly && smartMix != SmartMusicMix.FAVORITES_MIX) || track.isFavorite
            val matchesPlaylist = playlistTrackSet == null || track.id in playlistTrackSet
            val matchesFolder = folderFilter == null || track.folderName == folderFilter
            val matchesSmart = when (smartMix) {
                SmartMusicMix.LONG_LECTURES -> track.durationMs >= 600_000L ||
                    track.title.contains("درس", ignoreCase = true) ||
                    track.title.contains("محاضرة", ignoreCase = true) ||
                    track.title.contains("lecture", ignoreCase = true) ||
                    track.title.contains("podcast", ignoreCase = true)
                SmartMusicMix.HI_RES_LOSSLESS -> track.extension.uppercase() in setOf("FLAC", "WAV", "ALAC", "AIFF", "DSF")
                SmartMusicMix.QURAN_RECITATIONS -> QuranHadithCatalog.detectQuran(track.title, track.artist, track.album).isQuranic
                else -> true
            }
            val matchesQuery = q.isEmpty() ||
                track.title.lowercase().contains(q) ||
                track.artist.lowercase().contains(q) ||
                track.album.lowercase().contains(q) ||
                track.folderName.lowercase().contains(q) ||
                track.extension.lowercase().contains(q)
            matchesFormat && matchesFav && matchesPlaylist && matchesFolder && matchesSmart && matchesQuery
        }

        val activePlaylistOrder = playlistId?.let { id ->
            allPlaylists.firstOrNull { it.id == id }?.trackIds
        }

        if (activePlaylistOrder != null && smartMix == SmartMusicMix.ALL_TRACKS) {
            val orderIndex = activePlaylistOrder.withIndex().associate { it.value to it.index }
            filtered.sortedBy { orderIndex[it.id] ?: Int.MAX_VALUE }
        } else when (smartMix) {
            SmartMusicMix.MOST_PLAYED_MIX -> filtered.sortedWith(
                compareByDescending<AudioTrack> { it.playCount }.thenByDescending { it.lastPlayedMs }
            )
            SmartMusicMix.RECENTLY_ADDED -> filtered.sortedByDescending { maxOf(it.dateModifiedMs, it.addedOrder) }
            SmartMusicMix.QUICK_DISCOVERY -> filtered.shuffled(java.util.Random(seed))
            else -> when (sortOption) {
                MediaSortOption.NEWEST -> filtered.sortedByDescending { it.addedOrder }
                MediaSortOption.NAME -> filtered.sortedBy { it.title.lowercase() }
                MediaSortOption.DURATION -> filtered.sortedByDescending { it.durationMs }
                MediaSortOption.SIZE -> filtered.sortedByDescending { it.sizeBytes }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Live Active Playing Queue that supports real-time manual reordering (Up/Down),
     * live sorting (Name/Artist/Duration/Reverse/Shuffle), and removal while playing.
     */
    val activeQueueTracks: StateFlow<List<AudioTrack>> = combine(
        _customQueueIds,
        filteredAudioTracks,
        audioTracks,
    ) { customIds, filtered, all ->
        val byId = all.associateBy { it.id }
        if (customIds.isNotEmpty()) {
            val resolved = customIds.mapNotNull { byId[it] }
            if (resolved.isNotEmpty()) resolved else filtered.ifEmpty { all }
        } else {
            filtered.ifEmpty { all }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val availableVideoFolders: StateFlow<List<String>> = combine(_rawVideos, settings) { list, _ ->
        list.mapNotNull { it.folderName.takeIf(String::isNotBlank) }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val videos: StateFlow<List<VideoItem>> = combine(
        _rawVideos,
        settingsRepo.favoriteIdsFlow,
        settingsRepo.customTitlesFlow,
        _videoSortOption,
        _videoSearchQuery,
        _videoFavoritesOnly,
        _selectedVideoFolder,
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val list = values[0] as List<VideoItem>
        @Suppress("UNCHECKED_CAST")
        val favs = values[1] as Set<String>
        @Suppress("UNCHECKED_CAST")
        val customTitles = values[2] as Map<String, String>
        val sortOption = values[3] as MediaSortOption
        val query = (values[4] as String).trim().lowercase()
        val favOnly = values[5] as Boolean
        val folderFilter = values[6] as String?

        val mapped = list.map { video ->
            val renamed = customTitles[video.id]?.takeIf { it.isNotBlank() }
            video.copy(
                title = renamed ?: video.title,
                isFavorite = favs.contains(video.id)
            )
        }.filter { video ->
            val matchesFav = !favOnly || video.isFavorite
            val matchesFolder = folderFilter == null || video.folderName == folderFilter
            val matchesQ = query.isEmpty() ||
                video.title.lowercase().contains(query) ||
                video.folderName.lowercase().contains(query) ||
                video.extension.lowercase().contains(query) ||
                video.resolution.lowercase().contains(query)
            matchesFav && matchesFolder && matchesQ
        }
        when (sortOption) {
            MediaSortOption.NEWEST -> mapped.sortedByDescending { it.addedOrder }
            MediaSortOption.NAME -> mapped.sortedBy { it.title.lowercase() }
            MediaSortOption.DURATION -> mapped.sortedByDescending { it.durationMs }
            MediaSortOption.SIZE -> mapped.sortedByDescending { it.sizeBytes }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val availablePhotoFolders: StateFlow<List<String>> = combine(_rawPhotos, settings) { list, _ ->
        list.mapNotNull { it.folderName.takeIf(String::isNotBlank) }.distinct().sorted()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val photos: StateFlow<List<PhotoItem>> = combine(
        _rawPhotos,
        settingsRepo.favoriteIdsFlow,
        settingsRepo.customTitlesFlow,
        _photoSearchQuery,
        _photoFavoritesOnly,
        _photoSortOption,
        _selectedPhotoFolder,
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val list = values[0] as List<PhotoItem>
        @Suppress("UNCHECKED_CAST")
        val favs = values[1] as Set<String>
        @Suppress("UNCHECKED_CAST")
        val customTitles = values[2] as Map<String, String>
        val query = (values[3] as String).trim().lowercase()
        val favOnly = values[4] as Boolean
        val sortOption = values[5] as MediaSortOption
        val folderFilter = values[6] as String?

        val mapped = list.map { photo ->
            val renamed = customTitles[photo.id]?.takeIf { it.isNotBlank() }
            photo.copy(
                title = renamed ?: photo.title,
                isFavorite = favs.contains(photo.id)
            )
        }.filter { photo ->
            val matchesFav = !favOnly || photo.isFavorite
            val matchesFolder = folderFilter == null || photo.folderName == folderFilter
            val matchesQ = query.isEmpty() ||
                photo.title.lowercase().contains(query) ||
                photo.folderName.lowercase().contains(query) ||
                photo.extension.lowercase().contains(query)
            matchesFav && matchesFolder && matchesQ
        }
        when (sortOption) {
            MediaSortOption.NEWEST -> mapped
            MediaSortOption.NAME -> mapped.sortedBy { it.title.lowercase() }
            MediaSortOption.DURATION -> mapped
            MediaSortOption.SIZE -> mapped.sortedByDescending { it.sizeBytes }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val duplicateGroups: StateFlow<List<DuplicateGroup>> = combine(
        _rawAudio,
        _rawVideos,
        _rawPhotos,
    ) { a, v, p ->
        MediaScanner.findDuplicateGroups(a, v, p)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val rawAudioList: StateFlow<List<AudioTrack>> = _rawAudio.asStateFlow()
    val rawVideosList: StateFlow<List<VideoItem>> = _rawVideos.asStateFlow()
    val rawPhotosList: StateFlow<List<PhotoItem>> = _rawPhotos.asStateFlow()

    val hasAnyMedia: StateFlow<Boolean> = combine(
        _rawAudio,
        _rawVideos,
        _rawPhotos,
    ) { a, v, p -> a.isNotEmpty() || v.isNotEmpty() || p.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val audioController = AudioPlaybackController(
        context = application,
        scope = viewModelScope,
        onTrackEnded = { handleTrackEnded() },
        onSavePosition = { id, pos ->
            savedPositionsCache[id] = pos
            viewModelScope.launch { settingsRepo.savePlaybackPosition(id, pos) }
        },
        onSleepTimerFinished = {
            _statusBanner.value = stringsFor(settings.value.language).timerExpiredToast
        }
    )

    // Isolated high-frequency flows so playing audio NEVER recomposes the main tabs or navigation!
    val playbackPositionMs: StateFlow<Long> = audioController.positionMs
    val spectrumLevels: StateFlow<List<Float>> = audioController.spectrumLevels
    val vuLevels: StateFlow<Pair<Float, Float>> = audioController.vuLevels

    val customStudioPresets: StateFlow<List<com.example.model.CustomStudioPreset>> = settingsRepo.customStudioPresetsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _detectedAutoStudioProfile = MutableStateFlow<com.example.model.AutoStudioProfile?>(null)
    val detectedAutoStudioProfile: StateFlow<com.example.model.AutoStudioProfile?> = _detectedAutoStudioProfile.asStateFlow()

    val playbackUiState: StateFlow<PlaybackUiState> = combine(
        audioController.currentTrack,
        audioController.isPlaying,
        audioController.durationMs,
        _isPlayerExpanded,
        audioController.sleepTimerRemainingSec,
        audioController.sleepTimerTotalMin,
        settingsRepo.customTitlesFlow,
        settingsRepo.favoriteIdsFlow,
        _shuffleEnabled,
        _repeatMode,
        audioController.playbackSpeed,
        audioController.abLoopStartMs,
        audioController.abLoopEndMs,
        audioController.hifzRemainingLoops,
        audioController.isMuted,
    ) { values ->
        val rawTrack = values[0] as AudioTrack?
        val isPlaying = values[1] as Boolean
        val dur = values[2] as Long
        val expanded = values[3] as Boolean
        val remaining = values[4] as Int
        val totalMin = values[5] as Int
        @Suppress("UNCHECKED_CAST")
        val customTitles = values[6] as Map<String, String>
        @Suppress("UNCHECKED_CAST")
        val favs = values[7] as Set<String>
        val shuffle = values[8] as Boolean
        val repeat = values[9] as RepeatMode
        val speed = values[10] as Float
        val abStart = values[11] as Long?
        val abEnd = values[12] as Long?
        val hifzRem = values[13] as Int
        val muted = values[14] as Boolean

        val syncedTrack = rawTrack?.let { t ->
            val renamed = customTitles[t.id]?.takeIf { it.isNotBlank() }
            t.copy(
                title = renamed ?: t.title,
                isFavorite = favs.contains(t.id)
            )
        }

        PlaybackUiState(
            currentTrack = syncedTrack,
            isPlaying = isPlaying,
            durationMs = dur,
            isPlayerExpanded = expanded,
            sleepTimerRemainingSec = remaining,
            sleepTimerTotalMin = totalMin,
            shuffleEnabled = shuffle,
            repeatMode = repeat,
            playbackSpeed = speed,
            abLoopStartMs = abStart,
            abLoopEndMs = abEnd,
            hifzRemainingLoops = hifzRem,
            isMuted = muted,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PlaybackUiState())

    init {
        WidgetPlaybackBridge.onPlayPause = {
            if (playbackUiState.value.currentTrack != null) {
                togglePlayPause()
            } else {
                audioTracks.value.firstOrNull()?.let { playAudio(it) }
            }
        }
        WidgetPlaybackBridge.onNext = { playNextAudio() }
        WidgetPlaybackBridge.onPrev = { playPreviousAudio() }

        MediaNotificationBridge.onPlayPause = { togglePlayPause() }
        MediaNotificationBridge.onNext = { playNextAudio() }
        MediaNotificationBridge.onPrev = { playPreviousAudio() }
        MediaNotificationBridge.onStop = { stopAllPlaybackAndClosePip() }
        MediaNotificationBridge.onSeekTo = { posMs -> seekAudio(posMs) }

        viewModelScope.launch {
            combine(
                audioController.currentTrack,
                audioController.isPlaying,
                settingsRepo.customTitlesFlow,
                settings,
                audioController.playbackSpeed,
                quranKhatmaProgress,
            ) { values ->
                val track = values[0] as AudioTrack?
                val isPlaying = values[1] as Boolean
                @Suppress("UNCHECKED_CAST")
                val customTitles = values[2] as Map<String, String>
                val currentSettings = values[3] as AppSettings
                val speed = values[4] as Float
                val khatma = values[5] as QuranKhatmaProgress
                val syncedTrack = track?.let { t ->
                    val renamed = customTitles[t.id]?.takeIf { it.isNotBlank() }
                    t.copy(title = renamed ?: t.title)
                }
                WidgetSyncSnapshot(syncedTrack, isPlaying, currentSettings, speed, khatma)
            }.collect { snap ->
                val resolvedTitle = snap.track?.title
                val resolvedSubtitle = snap.track?.let { "${it.artist} • ${it.extension}" }
                WidgetPlaybackBridge.lastCompletedSurahs = snap.khatma.completedSurahs.size
                WidgetPlaybackBridge.lastKhatmaPercent = ((snap.khatma.completedSurahs.size * 100) / 114).coerceIn(0, 100)
                WidgetPlaybackBridge.lastTasbihCount = snap.settings.tasbihDailyCount
                WidgetPlaybackBridge.lastEqPresetLabel = if (snap.settings.eqEnabled) {
                    stringsFor(snap.settings.language).eqPresetName(snap.settings.eqPreset)
                } else {
                    stringsFor(snap.settings.language).eqBypassedStatus
                }
                WidgetPlaybackBridge.lastSpeedLabel = "${snap.speed}x"
                PrismMediaWidgetProvider.syncWidget(
                    context = getApplication(),
                    title = resolvedTitle,
                    subtitle = resolvedSubtitle,
                    isPlaying = snap.isPlaying
                )
                WidgetPlaybackBridge.syncAllWidgets(getApplication())
                val activeMood = com.example.ui.theme.TrackGenrePaletteEngine.classifyMood(
                    track = snap.track,
                    report = snap.track?.let { _knowledgeReports.value[it.id] }
                )
                val genreBadge = if (snap.track != null) {
                    stringsFor(snap.settings.language).genreMoodName(activeMood)
                } else ""
                MediaNotificationManager.updatePlaybackNotification(
                    context = getApplication(),
                    track = snap.track,
                    isPlaying = snap.isPlaying,
                    enabled = snap.settings.notificationsEnabled,
                    positionMs = audioController.positionMs.value,
                    durationMs = audioController.durationMs.value.coerceAtLeast(snap.track?.durationMs ?: 1L),
                    speed = snap.speed,
                    lockScreenControlsEnabled = snap.settings.lockScreenControlsEnabled,
                    lockScreenShowArtwork = snap.settings.lockScreenShowArtwork,
                    lockScreenShowIdentity = snap.settings.lockScreenShowPrismIdentity,
                    genreBadge = genreBadge,
                )
            }
        }

        viewModelScope.launch {
            savedPositionsCache.putAll(settingsRepo.getSavedPositions())
            val currentSettings = settingsRepo.settingsFlow.first()
            val currentKhatma = settingsRepo.quranKhatmaFlow.first()
            if (_selectedTab.value == MediaTab.MUSIC && currentSettings.startupTab != MediaTab.MUSIC) {
                _selectedTab.value = currentSettings.startupTab
            }
            audioController.setEqualizerEnabled(currentSettings.eqEnabled && !currentSettings.playerDirectBitExactDsp)
            audioController.setBassBoost(if (currentSettings.playerDirectBitExactDsp) 0f else currentSettings.bassBoost)
            audioController.applyEqualizerGains(currentSettings.eqGainsDb)
            audioController.setPreampPercent(if (currentSettings.playerDirectBitExactDsp) 100 else currentSettings.audioPreampPercent)
            audioController.setPitch(currentSettings.audioPitch)
            audioController.setCrossfadeSeconds(currentSettings.crossfadeSeconds)
            audioController.setStereoBalance(currentSettings.audioBalance)
            audioController.setMonoEnabled(currentSettings.audioMonoEnabled)
            audioController.setSkipSilenceEnabled(currentSettings.skipSilenceEnabled)
            audioController.setLoudnessNormalizationEnabled(currentSettings.audioNormalization)
            audioController.setLectureVocalBoostEnabled(currentSettings.lectureModeEnabled)
            audioController.setPauseOnHeadsetUnplug(currentSettings.pauseOnHeadsetUnplug)
            audioController.setReverbEnvironment(
                if (currentSettings.playerDirectBitExactDsp) ReverbEnvironment.NONE else currentSettings.reverbEnvironment
            )
            audioController.applyStudioConfig(
                com.example.model.StudioAudioConfig(
                    proEqEnabled = currentSettings.proEqEnabled && !currentSettings.playerDirectBitExactDsp,
                    tenBandGainsDb = currentSettings.proTenBandGainsDb,
                    tubeWarmth = if (currentSettings.playerDirectBitExactDsp) 0f else currentSettings.tubeWarmth,
                    stereoWidth = if (currentSettings.playerDirectBitExactDsp) 1.0f else currentSettings.stereoWidth,
                    crossfeedEnabled = !currentSettings.playerDirectBitExactDsp && currentSettings.crossfeedEnabled,
                    subBassPunch = if (currentSettings.playerDirectBitExactDsp) 0f else currentSettings.subBassPunch,
                    airBrilliance = if (currentSettings.playerDirectBitExactDsp) 0f else currentSettings.airBrilliance,
                    vocalClarity = if (currentSettings.playerDirectBitExactDsp) 0f else currentSettings.vocalClarity,
                    compressorStrength = if (currentSettings.playerDirectBitExactDsp) 0f else currentSettings.compressorStrength,
                    truePeakLimiterEnabled = currentSettings.truePeakLimiterEnabled,
                    nightModeEnabled = !currentSettings.playerDirectBitExactDsp && currentSettings.nightModeEnabled,
                    rumbleFilterHz = if (currentSettings.playerDirectBitExactDsp) 0 else currentSettings.rumbleFilterHz,
                    channelSwapEnabled = currentSettings.channelSwapEnabled,
                    headphoneProfile = if (currentSettings.playerDirectBitExactDsp) {
                        com.example.model.HeadphoneTuningProfile.NEUTRAL_STUDIO
                    } else currentSettings.headphoneProfile,
                    bypassEnabled = currentSettings.dspBypassEnabled || currentSettings.playerDirectBitExactDsp,
                )
            )
            audioController.setHifzLoopTarget(currentKhatma.hifzRepeatTarget)
            refreshDetectedAudioOutput(applyIfAuto = currentSettings.autoAudioDeviceProfiles)
            DynamicShortcutsHelper.publishShortcuts(getApplication(), stringsFor(currentSettings.language))
            _trashedItems.value = settingsRepo.trashedItemsFlow.first()
            refreshVaultItems()
            startDailyStatsAndAlarmTicker()

            // Always-On Built-In Anti-Tamper Certificate & DEX Bytecode Pinning Check
            val initialAudit = AppSecurityVerifier.inspect(getApplication())
            val pinnedSha = settingsRepo.getOrPinApkSha256(initialAudit.sha256Full)
            val pinnedDex = settingsRepo.getOrPinDexSha256(initialAudit.dexSha256)
            val verifiedAudit = AppSecurityVerifier.inspect(
                context = getApplication(),
                pinnedSha256 = pinnedSha,
                pinnedDexSha256 = pinnedDex,
            )
            _securityAudit.value = verifiedAudit
            if (verifiedAudit.tamperDetected) {
                _showTamperAlertDialog.value = true
            }

            // Initialize App Lock PIN Shield if configured
            cachedPinCredentials = settingsRepo.getPinCredentials()
            if (currentSettings.appLockEnabled && cachedPinCredentials != null) {
                _isAppLocked.value = true
            }

            restoreSavedLibrary()
        }
    }

    private suspend fun restoreSavedLibrary(forceFullRescan: Boolean = false) {
        val currentSettings = settingsRepo.settingsFlow.first()
        val (legacyTrees, files, demoLoaded) = settingsRepo.getSavedLibrarySources()
        var folders = settingsRepo.getWatchedFolders()

        // Migrate any legacy tree URIs into WatchedFolder records automatically
        if (folders.isEmpty() && legacyTrees.isNotEmpty()) {
            folders = legacyTrees.map { uriStr ->
                WatchedFolder(
                    uriString = uriStr,
                    displayName = MediaScanner.resolveFolderDisplayName(getApplication(), Uri.parse(uriStr)),
                    enabled = true,
                )
            }
            settingsRepo.saveWatchedFolders(folders)
        }

        // 1. Instant cold-start hydration from local disk cache (<50ms)
        val cachedDisk = settingsRepo.loadLibraryCache()
        val favs = settingsRepo.favoriteIdsFlow.first()
        if (cachedDisk != null && !forceFullRescan) {
            val initial = if (demoLoaded) {
                mergeResults(MediaScanner.loadDemoMedia(getApplication(), favs), cachedDisk)
            } else cachedDisk
            applyScannedMedia(initial)
        } else if (demoLoaded && folders.isEmpty() && files.isEmpty()) {
            applyScannedMedia(MediaScanner.loadDemoMedia(getApplication(), favs))
        }

        // First-launch Smart Auto-Watch Folder Setup prompt if user hasn't configured folders yet
        val hasSources = folders.isNotEmpty() || files.isNotEmpty() || demoLoaded
        if (!hasSources && !currentSettings.folderSetupCompleted) {
            _showFirstLaunchFolderWizard.value = true
            return
        }

        if (!hasSources) return
        if (!forceFullRescan && !currentSettings.autoScanWatchedFolders && cachedDisk != null) return

        // 2. Fast 2-Phase Progressive Background Auto-Sync over enabled watched folders
        val cacheLookup = CachedMediaLookup(
            audioById = (cachedDisk?.audioTracks ?: _rawAudio.value).associateBy { it.id },
            videoById = (cachedDisk?.videos ?: _rawVideos.value).associateBy { it.id },
            photoById = (cachedDisk?.photos ?: _rawPhotos.value).associateBy { it.id },
        )

        _isScanning.value = true
        _scanProgress.value = ScanProgressState(isScanning = true, discoveredCount = 0)
        var discoveredTotal = ScannedMediaResult()
        if (demoLoaded) {
            discoveredTotal = mergeResults(discoveredTotal, MediaScanner.loadDemoMedia(getApplication(), favs))
        }

        val updatedFolders = folders.toMutableList()
        var runningCount = 0

        for (i in updatedFolders.indices) {
            val folder = updatedFolders[i]
            if (!folder.enabled) continue
            _scanProgress.value = _scanProgress.value.copy(currentFolderName = folder.displayName)
            runCatching {
                val res = MediaScanner.scanFolderTreeFast(
                    context = getApplication(),
                    treeUri = Uri.parse(folder.uriString),
                    favoriteIds = favs,
                    cacheLookup = cacheLookup,
                    onBatchDiscovered = { batch, _ ->
                        runningCount += batch.audioTracks.size + batch.videos.size + batch.photos.size
                        _scanProgress.value = _scanProgress.value.copy(discoveredCount = runningCount)
                        appendScannedMedia(batch)
                    }
                )
                discoveredTotal = mergeResults(discoveredTotal, res)
                updatedFolders[i] = folder.copy(
                    audioCount = res.audioTracks.size,
                    videoCount = res.videos.size,
                    photoCount = res.photos.size,
                    lastScannedMs = System.currentTimeMillis()
                )
            }
        }

        if (files.isNotEmpty()) {
            runCatching {
                val res = MediaScanner.scanPickedUris(getApplication(), files.map { Uri.parse(it) }, favs)
                discoveredTotal = mergeResults(discoveredTotal, res)
            }
        }

        // Apply exact synced set (removes files deleted from disk while preserving newly found ones)
        applyScannedMedia(discoveredTotal)
        if (updatedFolders != folders) {
            settingsRepo.saveWatchedFolders(updatedFolders)
        }

        // Phase 2: Parallel lazy metadata enrichment for any brand-new/uncached audio/video files
        val enriched = MediaScanner.enrichMediaParallel(
            context = getApplication(),
            discovered = discoveredTotal,
            onPartialUpdate = { audioChunk, videoChunk, enrichedCount ->
                _scanProgress.value = _scanProgress.value.copy(enrichedCount = enrichedCount)
                mergeEnrichedChunk(audioChunk, videoChunk)
            }
        )
        applyScannedMedia(enriched)
        settingsRepo.saveLibraryCache(enriched)
        _isScanning.value = false
        _scanProgress.value = ScanProgressState(isScanning = false)
    }

    fun selectTab(tab: MediaTab) {
        _selectedTab.value = tab
    }

    fun setAudioSearchQuery(query: String) {
        _audioSearchQuery.value = query
    }

    fun setSelectedAudioFormat(format: String) {
        _selectedAudioFormat.value = format
    }

    fun toggleFavoritesFilter() {
        _favoritesOnly.value = !_favoritesOnly.value
    }

    fun setAudioSortOption(option: MediaSortOption) {
        _audioSortOption.value = option
    }

    fun selectSmartMix(mix: SmartMusicMix) {
        _selectedSmartMix.value = mix
        if (mix == SmartMusicMix.QUICK_DISCOVERY) {
            _discoverySeed.value = System.currentTimeMillis()
        }
    }

    fun setVideoSearchQuery(query: String) {
        _videoSearchQuery.value = query
    }

    fun toggleVideoFavoritesFilter() {
        _videoFavoritesOnly.value = !_videoFavoritesOnly.value
    }

    fun setVideoSortOption(option: MediaSortOption) {
        _videoSortOption.value = option
    }

    fun setPhotoSearchQuery(query: String) {
        _photoSearchQuery.value = query
    }

    fun togglePhotoFavoritesFilter() {
        _photoFavoritesOnly.value = !_photoFavoritesOnly.value
    }

    fun setPhotoSortOption(option: MediaSortOption) {
        _photoSortOption.value = option
    }

    fun playStreamUrl(rawUrl: String, customTitle: String, isVideo: Boolean): Boolean {
        val url = rawUrl.trim()
        if (!AppSecurityVerifier.isSafeHttpsStreamUrl(url)) {
            _statusBanner.value = stringsFor(settings.value.language).invalidHttpsUrlBanner
            return false
        }
        if (ContentPurityGuard.isBlockedStreamUrl(url) || ContentPurityGuard.isExplicitOrAdult(customTitle)) {
            _statusBanner.value = stringsFor(settings.value.language).contentBlockedBanner
            return false
        }
        val inferredExt = url.substringBefore('?').substringAfterLast('.', "").uppercase()
            .takeIf { it.length in 2..5 && it.all(Char::isLetterOrDigit) }
            ?: if (isVideo) "STREAM" else "AUDIO"
        val resolvedTitle = customTitle.trim().ifBlank {
            url.substringBefore('?').substringAfterLast('/').substringBeforeLast('.')
                .ifBlank { if (isVideo) "Online Video Stream" else "Online Audio Stream" }
        }
        if (isVideo) {
            val streamVideo = VideoItem(
                id = "stream_vid_${UUID.randomUUID()}",
                title = resolvedTitle,
                uriString = url,
                extension = inferredExt,
                mimeType = "video/*",
                durationMs = 0L,
                sizeBytes = 0L,
                resolution = "HTTPS Stream",
                addedOrder = nextAddedOrder++
            )
            _rawVideos.value = listOf(streamVideo) + _rawVideos.value
            openVideo(streamVideo)
        } else {
            val streamTrack = AudioTrack(
                id = "stream_aud_${UUID.randomUUID()}",
                title = resolvedTitle,
                artist = "HTTPS Live Stream",
                album = "Prism Network",
                uriString = url,
                extension = inferredExt,
                mimeType = "audio/*",
                durationMs = 0L,
                sizeBytes = 0L,
                addedOrder = nextAddedOrder++
            )
            _rawAudio.value = listOf(streamTrack) + _rawAudio.value
            playAudio(streamTrack)
        }
        return true
    }

    fun selectPlaylist(playlistId: String?) {
        _selectedPlaylistId.value = playlistId
    }

    fun createPlaylist(name: String, initialTrackId: String? = null) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            val newPl = UserPlaylist(
                id = UUID.randomUUID().toString(),
                name = trimmed,
                trackIds = if (initialTrackId != null) listOf(initialTrackId) else emptyList()
            )
            val updated = playlists.value + newPl
            settingsRepo.savePlaylists(updated)
        }
    }

    fun deletePlaylist(playlistId: String) {
        viewModelScope.launch {
            if (_selectedPlaylistId.value == playlistId) {
                _selectedPlaylistId.value = null
            }
            val updated = playlists.value.filterNot { it.id == playlistId }
            settingsRepo.savePlaylists(updated)
        }
    }

    fun toggleTrackInPlaylist(playlistId: String, trackId: String) {
        viewModelScope.launch {
            val updated = playlists.value.map { pl ->
                if (pl.id != playlistId) pl
                else {
                    val nextIds = if (trackId in pl.trackIds) {
                        pl.trackIds - trackId
                    } else {
                        pl.trackIds + trackId
                    }
                    pl.copy(trackIds = nextIds)
                }
            }
            settingsRepo.savePlaylists(updated)
        }
    }

    fun openPlaylistPickerForTrack(track: AudioTrack) {
        _playlistTrackTarget.value = track
    }

    fun dismissPlaylistPicker() {
        _playlistTrackTarget.value = null
    }

    fun openRenameDialog(id: String, currentTitle: String, mediaType: RenameMediaType) {
        _renameTarget.value = RenameTarget(id = id, currentTitle = currentTitle, mediaType = mediaType)
    }

    fun dismissRenameDialog() {
        _renameTarget.value = null
    }

    fun confirmRename(newTitle: String) {
        val target = _renameTarget.value ?: return
        val trimmed = newTitle.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            settingsRepo.saveCustomTitle(target.id, trimmed)
            _renameTarget.value = null
        }
    }

    fun postBanner(message: String) {
        _statusBanner.value = message
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    fun importFolder(treeUri: Uri) {
        viewModelScope.launch {
            _isScanning.value = true
            runCatching {
                getApplication<Application>().contentResolver.takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            val uriStr = treeUri.toString()
            val folderName = MediaScanner.resolveFolderDisplayName(getApplication(), treeUri)
            _scanProgress.value = ScanProgressState(isScanning = true, currentFolderName = folderName)

            val cacheLookup = CachedMediaLookup(
                audioById = _rawAudio.value.associateBy { it.id },
                videoById = _rawVideos.value.associateBy { it.id },
                photoById = _rawPhotos.value.associateBy { it.id },
            )
            val favs = settingsRepo.favoriteIdsFlow.first()
            var runningFound = 0
            val discovered = MediaScanner.scanFolderTreeFast(
                context = getApplication(),
                treeUri = treeUri,
                favoriteIds = favs,
                cacheLookup = cacheLookup,
                onBatchDiscovered = { batch, _ ->
                    runningFound += batch.audioTracks.size + batch.videos.size + batch.photos.size
                    _scanProgress.value = _scanProgress.value.copy(discoveredCount = runningFound)
                    appendScannedMedia(batch)
                }
            )

            // Save permanently into watchedFolders until user edits/removes it
            val currentFolders = settingsRepo.getWatchedFolders().toMutableList()
            val newEntry = WatchedFolder(
                uriString = uriStr,
                displayName = folderName,
                enabled = true,
                audioCount = discovered.audioTracks.size,
                videoCount = discovered.videos.size,
                photoCount = discovered.photos.size,
                lastScannedMs = System.currentTimeMillis()
            )
            val existingIdx = currentFolders.indexOfFirst { it.uriString == uriStr }
            if (existingIdx >= 0) {
                currentFolders[existingIdx] = newEntry
            } else {
                currentFolders.add(newEntry)
            }
            settingsRepo.saveWatchedFolders(currentFolders)
            settingsRepo.setFolderSetupCompleted(true)

            val total = discovered.audioTracks.size + discovered.videos.size + discovered.photos.size
            _statusBanner.value = stringsFor(settings.value.language).folderAddedBanner(folderName, total)

            // Phase 2: Parallel metadata enrichment in background so UI stays instant & fluid
            val enriched = MediaScanner.enrichMediaParallel(
                context = getApplication(),
                discovered = discovered,
                onPartialUpdate = { audioChunk, videoChunk, enrichedCount ->
                    _scanProgress.value = _scanProgress.value.copy(enrichedCount = enrichedCount)
                    mergeEnrichedChunk(audioChunk, videoChunk)
                }
            )
            appendScannedMedia(enriched)
            persistCurrentCache()
            _isScanning.value = false
            _scanProgress.value = ScanProgressState(isScanning = false)
        }
    }

    fun toggleWatchedFolder(uriString: String, enabled: Boolean) {
        viewModelScope.launch {
            val updated = settingsRepo.getWatchedFolders().map {
                if (it.uriString == uriString) it.copy(enabled = enabled) else it
            }
            settingsRepo.saveWatchedFolders(updated)
            if (!enabled) {
                _rawAudio.value = _rawAudio.value.filterNot { it.sourceFolderUri == uriString }
                _rawVideos.value = _rawVideos.value.filterNot { it.sourceFolderUri == uriString }
                _rawPhotos.value = _rawPhotos.value.filterNot { it.sourceFolderUri == uriString }
                persistCurrentCache()
            } else {
                rescanSingleFolder(uriString)
            }
        }
    }

    fun removeWatchedFolder(uriString: String) {
        viewModelScope.launch {
            val updated = settingsRepo.getWatchedFolders().filterNot { it.uriString == uriString }
            settingsRepo.saveWatchedFolders(updated)
            _rawAudio.value = _rawAudio.value.filterNot { it.sourceFolderUri == uriString }
            _rawVideos.value = _rawVideos.value.filterNot { it.sourceFolderUri == uriString }
            _rawPhotos.value = _rawPhotos.value.filterNot { it.sourceFolderUri == uriString }
            persistCurrentCache()
            _statusBanner.value = stringsFor(settings.value.language).folderRemovedBanner
        }
    }

    fun rescanSingleFolder(uriString: String) {
        importFolder(Uri.parse(uriString))
    }

    fun setAutoScanWatchedFolders(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAutoScanWatchedFolders(enabled) }
    }

    fun completeFirstLaunchSetup(loadDemo: Boolean = false) {
        viewModelScope.launch {
            settingsRepo.setFolderSetupCompleted(true)
            _showFirstLaunchFolderWizard.value = false
            if (loadDemo) {
                loadDemoMedia()
            }
        }
    }

    fun reopenFolderSetupWizard() {
        _showFirstLaunchFolderWizard.value = true
    }

    fun reopenOnboardingWizard() {
        _showFirstLaunchFolderWizard.value = true
    }

    fun setMusicFolderFilter(folderName: String?) {
        _selectedMusicFolder.value = folderName
    }

    fun setVideoFolderFilter(folderName: String?) {
        _selectedVideoFolder.value = folderName
    }

    fun setPhotoFolderFilter(folderName: String?) {
        _selectedPhotoFolder.value = folderName
    }

    fun openTagEditor(track: AudioTrack) {
        _editTagTarget.value = track
    }

    fun dismissTagEditor() {
        _editTagTarget.value = null
    }

    fun saveTrackTags(trackId: String, title: String, artist: String, album: String) {
        viewModelScope.launch {
            settingsRepo.saveTrackTagOverride(trackId, title, artist, album)
            _editTagTarget.value = null
            _statusBanner.value = stringsFor(settings.value.language).tagsSavedBanner
        }
    }

    fun addAudioBookmark(mediaId: String, positionMs: Long, customLabel: String) {
        viewModelScope.launch {
            val label = customLabel.trim().ifBlank { MediaScanner.formatDuration(positionMs) }
            val next = MediaBookmark(
                id = UUID.randomUUID().toString(),
                mediaId = mediaId,
                positionMs = positionMs,
                label = label,
                createdAtMs = System.currentTimeMillis()
            )
            settingsRepo.saveBookmarks(listOf(next) + bookmarks.value)
            _statusBanner.value = stringsFor(settings.value.language).bookmarkSavedBanner
        }
    }

    fun removeBookmark(bookmarkId: String) {
        viewModelScope.launch {
            settingsRepo.saveBookmarks(bookmarks.value.filterNot { it.id == bookmarkId })
        }
    }

    fun cleanDuplicateGroup(group: DuplicateGroup) {
        val idsToRemove = group.itemIds.drop(1).toSet()
        if (idsToRemove.isEmpty()) return
        when (group.mediaType) {
            DuplicateMediaType.AUDIO -> removeAudioByIds(idsToRemove)
            DuplicateMediaType.VIDEO -> removeVideosByIds(idsToRemove)
            DuplicateMediaType.PHOTO -> removePhotosByIds(idsToRemove)
        }
        viewModelScope.launch { persistCurrentCache() }
        _statusBanner.value = stringsFor(settings.value.language).duplicatesCleanedBanner(idsToRemove.size)
    }

    fun cleanAllDuplicates() {
        val groups = duplicateGroups.value
        if (groups.isEmpty()) return
        val audioDrop = mutableSetOf<String>()
        val videoDrop = mutableSetOf<String>()
        val photoDrop = mutableSetOf<String>()
        groups.forEach { g ->
            val extras = g.itemIds.drop(1)
            when (g.mediaType) {
                DuplicateMediaType.AUDIO -> audioDrop.addAll(extras)
                DuplicateMediaType.VIDEO -> videoDrop.addAll(extras)
                DuplicateMediaType.PHOTO -> photoDrop.addAll(extras)
            }
        }
        val total = audioDrop.size + videoDrop.size + photoDrop.size
        if (audioDrop.isNotEmpty()) _rawAudio.value = _rawAudio.value.filterNot { it.id in audioDrop }
        if (videoDrop.isNotEmpty()) _rawVideos.value = _rawVideos.value.filterNot { it.id in videoDrop }
        if (photoDrop.isNotEmpty()) _rawPhotos.value = _rawPhotos.value.filterNot { it.id in photoDrop }
        viewModelScope.launch { persistCurrentCache() }
        _statusBanner.value = stringsFor(settings.value.language).duplicatesCleanedBanner(total)
    }

    fun importFiles(uris: List<Uri>) {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            _isScanning.value = true
            uris.forEach { uri ->
                runCatching {
                    getApplication<Application>().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
            settingsRepo.addPickedFileUris(uris.map { it.toString() })
            settingsRepo.setFolderSetupCompleted(true)
            val favs = settingsRepo.favoriteIdsFlow.first()
            val scanned = MediaScanner.scanPickedUris(getApplication(), uris, favs)
            appendScannedMedia(scanned)
            persistCurrentCache()
            val total = scanned.audioTracks.size + scanned.videos.size + scanned.photos.size
            _statusBanner.value = stringsFor(settings.value.language).importedCountBanner(total)
            _isScanning.value = false
        }
    }

    fun loadDemoMedia() {
        viewModelScope.launch {
            _isScanning.value = true
            settingsRepo.setDemoLoaded(true)
            settingsRepo.setFolderSetupCompleted(true)
            val favs = settingsRepo.favoriteIdsFlow.first()
            val demo = MediaScanner.loadDemoMedia(getApplication(), favs)
            appendScannedMedia(demo)
            _statusBanner.value = stringsFor(settings.value.language).demoLoadedBanner
            _isScanning.value = false
        }
    }

    fun rescanSavedFolders() {
        viewModelScope.launch {
            restoreSavedLibrary(forceFullRescan = true)
        }
    }

    fun clearLibrary() {
        viewModelScope.launch {
            audioController.stopAndClear()
            _rawAudio.value = emptyList()
            _rawVideos.value = emptyList()
            _rawPhotos.value = emptyList()
            _isPlayerExpanded.value = false
            _activeVideo.value = null
            _activePhotoIndex.value = null
            _selectedPlaylistId.value = null
            _selectedMusicFolder.value = null
            _selectedVideoFolder.value = null
            _selectedPhotoFolder.value = null
            settingsRepo.clearLibrarySources()
            _statusBanner.value = stringsFor(settings.value.language).libraryClearedBanner
        }
    }

    fun playAudio(track: AudioTrack) {
        if (ContentPurityGuard.isExplicitOrAdult(track.title, track.artist, track.album, track.uriString)) {
            _statusBanner.value = stringsFor(settings.value.language).contentBlockedBanner
            return
        }
        if (_customQueueIds.value.isEmpty() || track.id !in _customQueueIds.value) {
            val baseList = filteredAudioTracks.value.ifEmpty { audioTracks.value }
            if (baseList.isNotEmpty()) {
                val ids = baseList.map { it.id }.toMutableList()
                if (track.id !in ids) ids.add(0, track.id)
                _customQueueIds.value = ids
            }
        }
        val resumeMs = if (settings.value.resumePlaybackPosition) {
            savedPositionsCache[track.id] ?: 0L
        } else 0L
        audioController.playTrack(track, resumeMs)
        viewModelScope.launch { settingsRepo.recordTrackPlay(track.id) }
        if (settings.value.autoStudioMasteringEnabled && !settings.value.playerDirectBitExactDsp) {
            applyAutoStudioMasteringForTrack(track, showBanner = false)
        }
        if (settings.value.autoOnlineEnrichment) {
            enrichAudioTrack(track, forceRefresh = false)
        }
    }

    fun moveQueueTrackUp(trackId: String) {
        val currentIds = activeQueueTracks.value.map { it.id }.toMutableList()
        val idx = currentIds.indexOf(trackId)
        if (idx > 0) {
            val item = currentIds.removeAt(idx)
            currentIds.add(idx - 1, item)
            _customQueueIds.value = currentIds
        }
    }

    fun moveQueueTrackDown(trackId: String) {
        val currentIds = activeQueueTracks.value.map { it.id }.toMutableList()
        val idx = currentIds.indexOf(trackId)
        if (idx in 0 until currentIds.lastIndex) {
            val item = currentIds.removeAt(idx)
            currentIds.add(idx + 1, item)
            _customQueueIds.value = currentIds
        }
    }

    fun removeTrackFromQueue(trackId: String) {
        val currentIds = activeQueueTracks.value.map { it.id }.toMutableList()
        if (currentIds.size <= 1) return
        currentIds.remove(trackId)
        _customQueueIds.value = currentIds
    }

    fun sortActiveQueue(option: QueueSortOption) {
        val current = activeQueueTracks.value
        if (current.size <= 1) return
        val currentId = playbackUiState.value.currentTrack?.id
        val sorted = when (option) {
            QueueSortOption.CUSTOM -> current
            QueueSortOption.NAME -> current.sortedBy { it.title.lowercase() }
            QueueSortOption.ARTIST -> current.sortedBy { it.artist.lowercase() }
            QueueSortOption.DURATION -> current.sortedByDescending { it.durationMs }
            QueueSortOption.REVERSE -> current.reversed()
            QueueSortOption.SHUFFLE -> {
                val active = current.firstOrNull { it.id == currentId }
                val others = current.filterNot { it.id == currentId }.shuffled()
                if (active != null) listOf(active) + others else others
            }
        }
        _customQueueIds.value = sorted.map { it.id }
        _statusBanner.value = stringsFor(settings.value.language).queueReorderedBanner
    }

    fun togglePlayPause() {
        audioController.togglePlayPause()
    }

    fun toggleAudioMute() {
        audioController.toggleMute()
    }

    fun seekAudio(positionMs: Long) {
        audioController.seekTo(positionMs)
    }

    fun seekRelative(deltaMs: Long) {
        audioController.seekRelative(deltaMs)
    }

    fun cyclePlaybackSpeed() {
        val speeds = AudioPlaybackController.SPEED_STEPS
        val cur = audioController.playbackSpeed.value
        val idx = speeds.indexOfFirst { kotlin.math.abs(it - cur) < 0.05f }
        val next = speeds[(idx + 1) % speeds.size]
        audioController.setPlaybackSpeed(next)
    }

    fun toggleShuffle() {
        _shuffleEnabled.value = !_shuffleEnabled.value
    }

    fun cycleRepeatMode() {
        _repeatMode.value = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun toggleAbLoopPoint() {
        audioController.toggleAbLoopPoint()
    }

    fun clearAbLoop() {
        audioController.clearAbLoop()
    }

    fun playNextAudio() {
        val list = activeQueueTracks.value
        if (list.isEmpty()) return
        val current = playbackUiState.value.currentTrack
        val next = if (_shuffleEnabled.value && list.size > 1) {
            list.filterNot { it.id == current?.id }.random()
        } else {
            val idx = list.indexOfFirst { it.id == current?.id }
            if (idx == -1 || idx + 1 >= list.size) list.first() else list[idx + 1]
        }
        playAudio(next)
    }

    fun playPreviousAudio() {
        val list = activeQueueTracks.value
        if (list.isEmpty()) return
        val current = playbackUiState.value.currentTrack
        val idx = list.indexOfFirst { it.id == current?.id }
        val prev = if (idx <= 0) list.last() else list[idx - 1]
        playAudio(prev)
    }

    private fun handleTrackEnded() {
        val current = playbackUiState.value.currentTrack
        when (_repeatMode.value) {
            RepeatMode.ONE -> {
                if (current != null) {
                    audioController.playTrack(current, 0L)
                }
            }
            RepeatMode.ALL -> {
                playNextAudio()
            }
            RepeatMode.OFF -> {
                if (settings.value.autoPlayNext) {
                    val list = activeQueueTracks.value
                    val idx = list.indexOfFirst { it.id == current?.id }
                    if (_shuffleEnabled.value || (idx in 0 until list.lastIndex)) {
                        playNextAudio()
                    }
                }
            }
        }
    }

    fun setPlayerExpanded(expanded: Boolean) {
        _isPlayerExpanded.value = expanded
    }

    fun setShowEqSheet(show: Boolean) {
        _showEqSheet.value = show
    }

    fun toggleMultiWindow() {
        val next = !_multiWindowEnabled.value
        _multiWindowEnabled.value = next
        val s = stringsFor(settings.value.language)
        _statusBanner.value = if (next) s.splitScreenBannerOn else s.splitScreenBannerOff
    }

    fun setSecondaryPaneMode(mode: SecondaryPaneMode) {
        _secondaryPaneMode.value = mode
    }

    fun openVideo(video: VideoItem, initialPosMs: Long = 0L) {
        if (ContentPurityGuard.isExplicitOrAdult(video.title, video.uriString)) {
            _statusBanner.value = stringsFor(settings.value.language).contentBlockedBanner
            return
        }
        audioController.pause()
        _floatingVideo.value = null
        _activeVideoInitialPosMs.value = initialPosMs
        _activeVideo.value = video
        _isVideoPlaying.value = true
    }

    /**
     * Audio-Only background playback mode: plays any video file's soundtrack inside the
     * full background audio engine with 5-band EQ, 200% Pre-Amp Boost, Pitch, and Sleep Timer.
     */
    fun playVideoAsAudio(video: VideoItem, initialPosMs: Long = 0L) {
        _activeVideo.value = null
        _floatingVideo.value = null
        _isVideoPlaying.value = false
        val audioFromVideo = AudioTrack(
            id = "video_audio_${video.id}",
            title = video.title,
            artist = "Video Soundtrack • ${video.resolution}",
            album = video.folderName.ifBlank { "Video Audio Mode" },
            uriString = video.uriString,
            extension = video.extension,
            mimeType = "audio/*",
            durationMs = video.durationMs,
            sizeBytes = video.sizeBytes,
            artworkResId = video.thumbnailResId,
            isFavorite = video.isFavorite,
            isDemo = video.isDemo,
        )
        audioController.playTrack(audioFromVideo, initialPosMs)
        _statusBanner.value = stringsFor(settings.value.language).playingVideoAsAudioBanner
    }

    fun closeVideo() {
        _activeVideo.value = null
        _activeVideoInitialPosMs.value = 0L
        _isVideoPlaying.value = _floatingVideo.value != null
    }

    fun minimizeVideoToFloating(positionMs: Long) {
        val video = _activeVideo.value ?: return
        _activeVideo.value = null
        _floatingVideoPosMs.value = positionMs
        _floatingVideo.value = video
        _isVideoPlaying.value = true
    }

    fun expandFloatingVideo(positionMs: Long) {
        val video = _floatingVideo.value ?: return
        _floatingVideo.value = null
        _activeVideoInitialPosMs.value = positionMs
        _activeVideo.value = video
        _isVideoPlaying.value = true
    }

    fun closeFloatingVideo() {
        _floatingVideo.value = null
        _floatingVideoPosMs.value = 0L
        _isVideoPlaying.value = _activeVideo.value != null
    }

    fun setVideoPlayingActive(playing: Boolean) {
        _isVideoPlaying.value = playing
    }

    fun openPhoto(index: Int) {
        _floatingPhoto.value = null
        _activePhotoIndex.value = index
    }

    fun closePhoto() {
        _activePhotoIndex.value = null
    }

    fun minimizePhotoToFloating(photo: PhotoItem) {
        _activePhotoIndex.value = null
        _floatingPhoto.value = photo
    }

    fun expandFloatingPhoto() {
        val photo = _floatingPhoto.value ?: return
        _floatingPhoto.value = null
        val idx = photos.value.indexOfFirst { it.id == photo.id }
        if (idx >= 0) {
            _activePhotoIndex.value = idx
        }
    }

    fun closeFloatingPhoto() {
        _floatingPhoto.value = null
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            settingsRepo.toggleFavorite(id)
        }
    }

    private fun persistRecycleBin() {
        val snapshot = _trashedItems.value
        viewModelScope.launch { settingsRepo.saveTrashedItems(snapshot) }
    }

    fun removeAudio(track: AudioTrack) {
        if (playbackUiState.value.currentTrack?.id == track.id) {
            audioController.stopAndClear()
            _isPlayerExpanded.value = false
        }
        val trashEntry = TrashedMediaItem(
            id = track.id,
            title = track.title,
            subtitle = "${track.artist} • ${track.extension}",
            mediaType = RenameMediaType.AUDIO,
            audioTrack = track,
        )
        _trashedItems.value = (listOf(trashEntry) + _trashedItems.value.filterNot { it.id == track.id }).take(60)
        persistRecycleBin()
        _rawAudio.value = _rawAudio.value.filterNot { it.id == track.id }
        _customQueueIds.value = _customQueueIds.value.filterNot { it == track.id }
        _statusBanner.value = stringsFor(settings.value.language).movedToBinBanner
    }

    fun removeAudioByIds(ids: Set<String>) {
        if (ids.isEmpty()) return
        if (playbackUiState.value.currentTrack?.id in ids) {
            audioController.stopAndClear()
            _isPlayerExpanded.value = false
        }
        val removed = _rawAudio.value.filter { it.id in ids }.map { track ->
            TrashedMediaItem(
                id = track.id,
                title = track.title,
                subtitle = "${track.artist} • ${track.extension}",
                mediaType = RenameMediaType.AUDIO,
                audioTrack = track,
            )
        }
        _trashedItems.value = (removed + _trashedItems.value.filterNot { it.id in ids }).take(60)
        persistRecycleBin()
        _rawAudio.value = _rawAudio.value.filterNot { it.id in ids }
        _customQueueIds.value = _customQueueIds.value.filterNot { it in ids }
        _statusBanner.value = stringsFor(settings.value.language).deletedCountBanner(ids.size)
    }

    fun removeVideo(video: VideoItem) {
        if (_activeVideo.value?.id == video.id) _activeVideo.value = null
        if (_floatingVideo.value?.id == video.id) _floatingVideo.value = null
        val trashEntry = TrashedMediaItem(
            id = video.id,
            title = video.title,
            subtitle = "${video.resolution} • ${video.extension}",
            mediaType = RenameMediaType.VIDEO,
            videoItem = video,
        )
        _trashedItems.value = (listOf(trashEntry) + _trashedItems.value.filterNot { it.id == video.id }).take(60)
        persistRecycleBin()
        _rawVideos.value = _rawVideos.value.filterNot { it.id == video.id }
        _statusBanner.value = stringsFor(settings.value.language).movedToBinBanner
    }

    fun removeVideosByIds(ids: Set<String>) {
        if (ids.isEmpty()) return
        if (_activeVideo.value?.id in ids) _activeVideo.value = null
        if (_floatingVideo.value?.id in ids) _floatingVideo.value = null
        val removed = _rawVideos.value.filter { it.id in ids }.map { video ->
            TrashedMediaItem(
                id = video.id,
                title = video.title,
                subtitle = "${video.resolution} • ${video.extension}",
                mediaType = RenameMediaType.VIDEO,
                videoItem = video,
            )
        }
        _trashedItems.value = (removed + _trashedItems.value.filterNot { it.id in ids }).take(60)
        persistRecycleBin()
        _rawVideos.value = _rawVideos.value.filterNot { it.id in ids }
        _statusBanner.value = stringsFor(settings.value.language).deletedCountBanner(ids.size)
    }

    fun removePhoto(photo: PhotoItem) {
        val trashEntry = TrashedMediaItem(
            id = photo.id,
            title = photo.title,
            subtitle = "${photo.width}×${photo.height} • ${photo.extension}",
            mediaType = RenameMediaType.PHOTO,
            photoItem = photo,
        )
        _trashedItems.value = (listOf(trashEntry) + _trashedItems.value.filterNot { it.id == photo.id }).take(60)
        persistRecycleBin()
        _rawPhotos.value = _rawPhotos.value.filterNot { it.id == photo.id }
        _statusBanner.value = stringsFor(settings.value.language).movedToBinBanner
    }

    fun removePhotosByIds(ids: Set<String>) {
        if (ids.isEmpty()) return
        val removed = _rawPhotos.value.filter { it.id in ids }.map { photo ->
            TrashedMediaItem(
                id = photo.id,
                title = photo.title,
                subtitle = "${photo.width}×${photo.height} • ${photo.extension}",
                mediaType = RenameMediaType.PHOTO,
                photoItem = photo,
            )
        }
        _trashedItems.value = (removed + _trashedItems.value.filterNot { it.id in ids }).take(60)
        persistRecycleBin()
        _rawPhotos.value = _rawPhotos.value.filterNot { it.id in ids }
        _activePhotoIndex.value = null
        _statusBanner.value = stringsFor(settings.value.language).deletedCountBanner(ids.size)
    }

    fun restoreTrashedItem(id: String) {
        val item = _trashedItems.value.firstOrNull { it.id == id } ?: return
        item.audioTrack?.let { t ->
            if (_rawAudio.value.none { it.id == t.id }) _rawAudio.value = listOf(t) + _rawAudio.value
        }
        item.videoItem?.let { v ->
            if (_rawVideos.value.none { it.id == v.id }) _rawVideos.value = listOf(v) + _rawVideos.value
        }
        item.photoItem?.let { p ->
            if (_rawPhotos.value.none { it.id == p.id }) _rawPhotos.value = listOf(p) + _rawPhotos.value
        }
        _trashedItems.value = _trashedItems.value.filterNot { it.id == id }
        persistRecycleBin()
        _statusBanner.value = stringsFor(settings.value.language).restoredFromBinBanner
    }

    fun restoreAllTrashed() {
        val current = _trashedItems.value
        if (current.isEmpty()) return
        val audioRestore = current.mapNotNull { it.audioTrack }
        val videoRestore = current.mapNotNull { it.videoItem }
        val photoRestore = current.mapNotNull { it.photoItem }
        if (audioRestore.isNotEmpty()) _rawAudio.value = (audioRestore + _rawAudio.value).distinctBy { it.id }
        if (videoRestore.isNotEmpty()) _rawVideos.value = (videoRestore + _rawVideos.value).distinctBy { it.id }
        if (photoRestore.isNotEmpty()) _rawPhotos.value = (photoRestore + _rawPhotos.value).distinctBy { it.id }
        _trashedItems.value = emptyList()
        persistRecycleBin()
        _statusBanner.value = stringsFor(settings.value.language).restoredFromBinBanner
    }

    fun emptyRecycleBin() {
        _trashedItems.value = emptyList()
        persistRecycleBin()
    }

    fun favoriteItemsByIds(ids: Set<String>) {
        if (ids.isEmpty()) return
        viewModelScope.launch {
            settingsRepo.addFavorites(ids)
            _statusBanner.value = stringsFor(settings.value.language).favoritedCountBanner(ids.size)
        }
    }

    fun openBatchPlaylistPicker(trackIds: List<String>) {
        if (trackIds.isNotEmpty()) {
            _batchPlaylistTrackIds.value = trackIds
        }
    }

    fun dismissBatchPlaylistPicker() {
        _batchPlaylistTrackIds.value = emptyList()
    }

    fun addBatchTracksToPlaylist(playlistId: String, trackIds: List<String>) {
        if (trackIds.isEmpty()) return
        viewModelScope.launch {
            val updated = playlists.value.map { pl ->
                if (pl.id != playlistId) pl
                else pl.copy(trackIds = (pl.trackIds + trackIds).distinct())
            }
            settingsRepo.savePlaylists(updated)
            _batchPlaylistTrackIds.value = emptyList()
            _statusBanner.value = stringsFor(settings.value.language).addedToPlaylistBanner(trackIds.size)
        }
    }

    fun createPlaylistWithTracks(name: String, trackIds: List<String>) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            val newPl = UserPlaylist(
                id = UUID.randomUUID().toString(),
                name = trimmed,
                trackIds = trackIds.distinct()
            )
            settingsRepo.savePlaylists(playlists.value + newPl)
            _batchPlaylistTrackIds.value = emptyList()
            _statusBanner.value = stringsFor(settings.value.language).addedToPlaylistBanner(trackIds.size)
        }
    }

    fun playNextVideo() {
        val list = videos.value
        if (list.size <= 1) return
        val cur = _activeVideo.value ?: return
        val idx = list.indexOfFirst { it.id == cur.id }
        val next = if (idx == -1 || idx + 1 >= list.size) list.first() else list[idx + 1]
        openVideo(next, 0L)
    }

    fun playPreviousVideo() {
        val list = videos.value
        if (list.size <= 1) return
        val cur = _activeVideo.value ?: return
        val idx = list.indexOfFirst { it.id == cur.id }
        val prev = if (idx <= 0) list.last() else list[idx - 1]
        openVideo(prev, 0L)
    }

    fun setMusicViewMode(mode: MediaViewMode) {
        viewModelScope.launch { settingsRepo.setMusicViewMode(mode) }
    }

    fun setVideoViewMode(mode: MediaViewMode) {
        viewModelScope.launch { settingsRepo.setVideoViewMode(mode) }
    }

    fun setPhotoViewMode(mode: MediaViewMode) {
        viewModelScope.launch { settingsRepo.setPhotoViewMode(mode) }
    }

    fun setScreenPrivacyEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setScreenPrivacyEnabled(enabled) }
    }

    fun setAppLockOnBackground(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAppLockOnBackground(enabled) }
    }

    fun setupAppLockPin(rawPin: String) {
        val clean = rawPin.trim()
        if (clean.length !in 4..6 || !clean.all(Char::isDigit)) return
        viewModelScope.launch {
            val salt = PinCrypto.generateSaltHex()
            val hash = PinCrypto.hashPin(clean, salt)
            settingsRepo.saveAppLockPin(salt, hash)
            cachedPinCredentials = salt to hash
            _failedPinAttempts.value = 0
            _lockoutUntilMs.value = 0L
            _isAppLocked.value = false
            _statusBanner.value = stringsFor(settings.value.language).appLockSavedBanner
        }
    }

    fun disableAppLock() {
        viewModelScope.launch {
            settingsRepo.disableAppLock()
            cachedPinCredentials = null
            _isAppLocked.value = false
            _failedPinAttempts.value = 0
            _lockoutUntilMs.value = 0L
            _statusBanner.value = stringsFor(settings.value.language).appLockDisabledBanner
        }
    }

    fun lockAppIfConfigured() {
        if (settings.value.appLockEnabled && settings.value.appLockOnBackground && cachedPinCredentials != null) {
            _isAppLocked.value = true
        }
    }

    fun verifyUnlockPin(candidatePin: String): Boolean {
        val now = System.currentTimeMillis()
        if (now < _lockoutUntilMs.value) return false
        val creds = cachedPinCredentials ?: run {
            _isAppLocked.value = false
            return true
        }
        val valid = PinCrypto.verifyPin(candidatePin, creds.first, creds.second)
        if (valid) {
            _isAppLocked.value = false
            _failedPinAttempts.value = 0
            _lockoutUntilMs.value = 0L
            return true
        }
        val nextCount = _failedPinAttempts.value + 1
        _failedPinAttempts.value = nextCount
        val cooldownSec = when {
            nextCount >= 6 -> 120L
            nextCount >= 4 -> 60L
            nextCount >= 3 -> 30L
            else -> 0L
        }
        if (cooldownSec > 0L) {
            _lockoutUntilMs.value = now + cooldownSec * 1000L
        }
        val formatted = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(now))
        val attempt = IntruderAttempt(
            timestampMs = now,
            formattedTime = formatted,
            failedCount = nextCount,
            note = if (cooldownSec > 0L) {
                "Failed PIN #$nextCount • Cooldown ${cooldownSec}s triggered"
            } else {
                "Failed PIN attempt #$nextCount"
            }
        )
        viewModelScope.launch { settingsRepo.appendIntruderAttempt(attempt) }
        return false
    }

    fun clearIntruderLog() {
        viewModelScope.launch { settingsRepo.clearIntruderLog() }
    }

    fun showDetails(details: MediaDetailsInfo) {
        _detailsInfo.value = details
        if (settings.value.autoOnlineEnrichment) {
            enrichMediaDetails(details, forceRefresh = false)
        }
    }

    fun dismissDetails() {
        _detailsInfo.value = null
    }

    fun enrichAudioTrack(track: AudioTrack, forceRefresh: Boolean = false) {
        val key = track.id
        if (!forceRefresh && _knowledgeReports.value.containsKey(key)) return
        if (key in _enrichingIds.value) return
        val currentSettings = settings.value
        _enrichingIds.value = _enrichingIds.value + key
        viewModelScope.launch {
            try {
                val report = MediaKnowledgeEngine.enrichAudioTrack(
                    context = getApplication(),
                    track = track,
                    language = currentSettings.language,
                    fetchLyrics = currentSettings.autoFetchLyrics || forceRefresh,
                    fetchArtistPhoto = currentSettings.autoFetchArtistPhotos,
                    forceRefresh = forceRefresh,
                )
                _knowledgeReports.value = _knowledgeReports.value + (key to report)
                // Auto-populate synced/plain lyrics if found and not already customized (or forced)
                val fetchedLyrics = report.fetchedLyrics
                if (!fetchedLyrics.isNullOrBlank()) {
                    val existing = customLyrics.value[track.id]
                    if (existing.isNullOrBlank() || forceRefresh) {
                        settingsRepo.saveTrackLyrics(track.id, fetchedLyrics)
                    }
                }
            } finally {
                _enrichingIds.value = _enrichingIds.value - key
            }
        }
    }

    fun fetchLyricsOnlineForTrack(track: AudioTrack) {
        val key = track.id
        if (key in _enrichingIds.value) return
        val currentSettings = settings.value
        _enrichingIds.value = _enrichingIds.value + key
        viewModelScope.launch {
            try {
                val report = MediaKnowledgeEngine.enrichAudioTrack(
                    context = getApplication(),
                    track = track,
                    language = currentSettings.language,
                    fetchLyrics = true,
                    fetchArtistPhoto = currentSettings.autoFetchArtistPhotos,
                    forceRefresh = true,
                )
                _knowledgeReports.value = _knowledgeReports.value + (key to report)
                val fetchedLyrics = report.fetchedLyrics
                if (!fetchedLyrics.isNullOrBlank()) {
                    settingsRepo.saveTrackLyrics(track.id, fetchedLyrics)
                    _statusBanner.value = stringsFor(currentSettings.language).lyricsSavedBanner
                } else {
                    _statusBanner.value = stringsFor(currentSettings.language).noOnlineInfoFound
                }
            } finally {
                _enrichingIds.value = _enrichingIds.value - key
            }
        }
    }

    fun enrichMediaDetails(details: MediaDetailsInfo, forceRefresh: Boolean = false) {
        val key = details.mediaId.ifBlank { details.uriString }
        if (!forceRefresh && _knowledgeReports.value.containsKey(key)) return
        if (key in _enrichingIds.value) return
        val currentSettings = settings.value
        _enrichingIds.value = _enrichingIds.value + key
        viewModelScope.launch {
            try {
                val report = MediaKnowledgeEngine.enrichMediaDetails(
                    context = getApplication(),
                    details = details,
                    language = currentSettings.language,
                    forceRefresh = forceRefresh,
                )
                _knowledgeReports.value = _knowledgeReports.value + (key to report)
            } finally {
                _enrichingIds.value = _enrichingIds.value - key
            }
        }
    }

    fun clearKnowledgeCache() {
        viewModelScope.launch {
            MediaKnowledgeEngine.clearCache(getApplication())
            _knowledgeReports.value = emptyMap()
            _statusBanner.value = stringsFor(settings.value.language).knowledgeCacheClearedBanner
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setDarkTheme(enabled) }
    }

    fun setAmoledBlack(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAmoledBlack(enabled) }
    }

    fun setAccentPalette(palette: AccentPalette) {
        viewModelScope.launch { settingsRepo.setAccentPalette(palette) }
    }

    fun setIconStyle(style: IconPackStyle) {
        viewModelScope.launch { settingsRepo.setIconStyle(style) }
    }

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch { settingsRepo.setLanguage(language) }
    }

    fun setAutoPlayNext(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAutoPlayNext(enabled) }
    }

    fun setResumePlayback(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setResumePlayback(enabled) }
    }

    fun setFilterShortAudio(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setFilterShortAudio(enabled) }
    }

    fun setAutoPipEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAutoPipEnabled(enabled) }
    }

    fun setEqEnabled(enabled: Boolean) {
        audioController.setEqualizerEnabled(enabled)
        viewModelScope.launch { settingsRepo.setEqEnabled(enabled) }
    }

    fun setBassBoost(strength: Float) {
        val clamped = strength.coerceIn(0f, 1f)
        audioController.setBassBoost(clamped)
        viewModelScope.launch { settingsRepo.setBassBoost(clamped) }
    }

    fun selectEqPreset(preset: EqPreset) {
        val gains = preset.gainsDb
        audioController.setEqualizerEnabled(true)
        audioController.applyEqualizerGains(gains)
        viewModelScope.launch {
            settingsRepo.setEqEnabled(true)
            settingsRepo.setEqualizer(preset, gains)
        }
    }

    fun resetEqualizer() {
        audioController.setBassBoost(0f)
        selectEqPreset(EqPreset.FLAT)
        viewModelScope.launch { settingsRepo.setBassBoost(0f) }
    }

    fun updateEqBand(bandIndex: Int, gainDb: Float) {
        val updated = settings.value.eqGainsDb.toMutableList()
        if (bandIndex in updated.indices) {
            updated[bandIndex] = gainDb.coerceIn(-12f, 12f)
            audioController.setEqualizerEnabled(true)
            audioController.applyEqualizerGains(updated)
            viewModelScope.launch {
                settingsRepo.setEqEnabled(true)
                settingsRepo.setEqualizer(EqPreset.CUSTOM, updated)
            }
        }
    }

    fun startSleepTimer(minutes: Int) {
        audioController.startSleepTimer(minutes)
    }

    fun cancelSleepTimer() {
        audioController.cancelSleepTimer()
    }

    /**
     * Immediately stops all audio & video playback (called when PiP or Floating window is dragged onto X).
     */
    fun stopAllPlaybackAndClosePip() {
        audioController.stopAndClear()
        _isPlayerExpanded.value = false
        _activeVideo.value = null
        _activeVideoInitialPosMs.value = 0L
        _floatingVideo.value = null
        _floatingVideoPosMs.value = 0L
        _isVideoPlaying.value = false
    }

    fun setSquigglyProgress(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setSquigglyProgress(enabled) }
    }

    fun setMiniPlayerCompact(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setMiniPlayerCompact(enabled) }
    }

    fun setMiniPlayerFloating(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setMiniPlayerFloating(enabled) }
    }

    fun setBarAddon(addonKey: String, enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setBarAddon(addonKey, enabled) }
    }

    fun setFontStyle(style: MaterialFontStyle) {
        viewModelScope.launch { settingsRepo.setFontStyle(style) }
    }

    fun setFontScaleMultiplier(scale: Float) {
        viewModelScope.launch { settingsRepo.setFontScaleMultiplier(scale) }
    }

    fun setHardwareVideoAcceleration(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setHardwareVideoAcceleration(enabled) }
    }

    fun refreshDeviceDrivers() {
        _driverProfile.value = DeviceMediaDriverEngine.probe(getApplication())
        _statusBanner.value = stringsFor(settings.value.language).driversRescannedBanner
    }

    fun setLiquidGlassStyle(style: LiquidGlassStyle) {
        viewModelScope.launch { settingsRepo.setLiquidGlassStyle(style) }
    }

    fun setLiquidGlassIntensity(intensity: Float) {
        viewModelScope.launch { settingsRepo.setLiquidGlassIntensity(intensity) }
    }

    fun setShapeRoundness(roundness: Float) {
        viewModelScope.launch { settingsRepo.setShapeRoundness(roundness) }
    }

    fun setNavTransitionStyle(style: NavTransitionStyle) {
        viewModelScope.launch { settingsRepo.setNavTransitionStyle(style) }
    }

    fun setContrastLevel(level: MaterialContrastLevel) {
        viewModelScope.launch { settingsRepo.setContrastLevel(level) }
    }

    fun setEdgeGlowEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setEdgeGlowEnabled(enabled) }
    }

    fun setNavBarStyle(style: NavBarStyle) {
        viewModelScope.launch { settingsRepo.setNavBarStyle(style) }
    }

    fun setStartupTab(tab: MediaTab) {
        viewModelScope.launch { settingsRepo.setStartupTab(tab) }
    }

    fun setUiDensity(density: UiDensity) {
        viewModelScope.launch { settingsRepo.setUiDensity(density) }
    }

    fun setPlayerArtworkStyle(style: PlayerArtworkStyle) {
        viewModelScope.launch { settingsRepo.setPlayerArtworkStyle(style) }
    }

    fun setAlbumArtTintEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAlbumArtTintEnabled(enabled) }
    }

    fun setShowFullPlayerSpectrum(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setShowFullPlayerSpectrum(enabled) }
    }

    fun setSeekStepSeconds(seconds: Int) {
        viewModelScope.launch { settingsRepo.setSeekStepSeconds(seconds) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setHapticsEnabled(enabled) }
    }

    fun setAutoOnlineEnrichment(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAutoOnlineEnrichment(enabled) }
    }

    fun setAutoFetchLyrics(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAutoFetchLyrics(enabled) }
    }

    fun setAutoFetchArtistPhotos(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAutoFetchArtistPhotos(enabled) }
    }

    fun setGaplessPlayback(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setGaplessPlayback(enabled) }
    }

    fun setAudioNormalization(enabled: Boolean) {
        audioController.setLoudnessNormalizationEnabled(enabled)
        viewModelScope.launch { settingsRepo.setAudioNormalization(enabled) }
    }

    fun setPauseOnHeadsetUnplug(enabled: Boolean) {
        audioController.setPauseOnHeadsetUnplug(enabled)
        viewModelScope.launch { settingsRepo.setPauseOnHeadsetUnplug(enabled) }
    }

    fun setVideoGesturesEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setVideoGesturesEnabled(enabled) }
    }

    fun setVideoDoubleTapSeekEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setVideoDoubleTapSeekEnabled(enabled) }
    }

    fun setVideoRememberPosition(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setVideoRememberPosition(enabled) }
    }

    fun setVideoSubtitleSizeSp(sizeSp: Int) {
        viewModelScope.launch { settingsRepo.setVideoSubtitleSizeSp(sizeSp) }
    }

    fun setPhotoAutoShowExif(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPhotoAutoShowExif(enabled) }
    }

    fun setPhotoHighResDecode(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPhotoHighResDecode(enabled) }
    }

    fun setPhotoSlideshowTransition(style: SlideshowTransitionStyle) {
        viewModelScope.launch { settingsRepo.setPhotoSlideshowTransition(style) }
    }

    fun setSurfaceDepthStyle(style: SurfaceDepthStyle) {
        viewModelScope.launch { settingsRepo.setSurfaceDepthStyle(style) }
    }

    fun resetCustomizationDefaults() {
        viewModelScope.launch {
            settingsRepo.resetCustomizationDefaults()
            _statusBanner.value = stringsFor(settings.value.language).resetCustomizationBanner
        }
    }

    fun setAudioPreampPercent(percent: Int) {
        val clamped = percent.coerceIn(100, 200)
        audioController.setPreampPercent(clamped)
        viewModelScope.launch { settingsRepo.setAudioPreampPercent(clamped) }
    }

    fun setAudioPitch(pitch: Float) {
        val clamped = pitch.coerceIn(0.75f, 1.50f)
        audioController.setPitch(clamped)
        viewModelScope.launch { settingsRepo.setAudioPitch(clamped) }
    }

    fun setCrossfadeSeconds(seconds: Int) {
        val clamped = seconds.coerceIn(0, 12)
        audioController.setCrossfadeSeconds(clamped)
        viewModelScope.launch { settingsRepo.setCrossfadeSeconds(clamped) }
    }

    fun setShowLyricsInPlayer(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setShowLyricsInPlayer(enabled) }
    }

    fun setVideoDefaultAspect(aspectIndex: Int) {
        viewModelScope.launch { settingsRepo.setVideoDefaultAspect(aspectIndex) }
    }

    fun setVideoDefaultSpeed(speed: Float) {
        viewModelScope.launch { settingsRepo.setVideoDefaultSpeed(speed) }
    }

    fun setVideoDefaultLoop(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setVideoDefaultLoop(enabled) }
    }

    fun setPhotoGridColumns(columns: Int) {
        viewModelScope.launch { settingsRepo.setPhotoGridColumns(columns) }
    }

    fun setPhotoSlideshowSeconds(seconds: Int) {
        viewModelScope.launch { settingsRepo.setPhotoSlideshowSeconds(seconds) }
    }

    fun setPhotoShowTitles(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPhotoShowTitles(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setNotificationsEnabled(enabled) }
    }

    fun setTamperGuardEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setTamperGuardEnabled(enabled) }
    }

    fun setBiometricUnlockEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setBiometricUnlockEnabled(enabled) }
    }

    fun unlockWithBiometric() {
        _failedPinAttempts.value = 0
        _lockoutUntilMs.value = 0L
        _isAppLocked.value = false
    }

    /**
     * Handles direct Android "Open With Prism" (ACTION_VIEW) intents for audio, video, or photo URIs.
     */
    fun openExternalMediaUri(uri: Uri, explicitMimeType: String?) {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            val scanned = MediaScanner.scanPickedUris(ctx, listOf(uri))
            appendScannedMedia(scanned)
            val s = stringsFor(settings.value.language)
            val resolvedMime = explicitMimeType
                ?: runCatching { ctx.contentResolver.getType(uri) }.getOrNull().orEmpty()

            when {
                scanned.audioTracks.isNotEmpty() -> {
                    val track = scanned.audioTracks.first()
                    _selectedTab.value = MediaTab.MUSIC
                    playAudio(track)
                    setPlayerExpanded(true)
                    _statusBanner.value = s.openedExternalMediaBanner(track.title)
                }
                scanned.videos.isNotEmpty() -> {
                    val video = scanned.videos.first()
                    _selectedTab.value = MediaTab.VIDEOS
                    openVideo(video)
                    _statusBanner.value = s.openedExternalMediaBanner(video.title)
                }
                scanned.photos.isNotEmpty() -> {
                    val photo = scanned.photos.first()
                    _selectedTab.value = MediaTab.PHOTOS
                    val idx = photos.value.indexOfFirst { it.id == photo.id }.coerceAtLeast(0)
                    openPhoto(idx)
                    _statusBanner.value = s.openedExternalMediaBanner(photo.title)
                }
                resolvedMime.startsWith("audio/") -> {
                    val fallback = AudioTrack(
                        id = "ext_audio_${uri.toString().hashCode()}",
                        title = uri.lastPathSegment?.substringAfterLast('/') ?: "Audio",
                        artist = "External Media",
                        album = "Open With Prism",
                        uriString = uri.toString(),
                        extension = uri.lastPathSegment?.substringAfterLast('.', "AUDIO")?.uppercase() ?: "AUDIO",
                        mimeType = resolvedMime,
                        durationMs = 0L,
                        sizeBytes = 0L,
                    )
                    appendScannedMedia(ScannedMediaResult(audioTracks = listOf(fallback)))
                    _selectedTab.value = MediaTab.MUSIC
                    playAudio(fallback)
                    setPlayerExpanded(true)
                    _statusBanner.value = s.openedExternalMediaBanner(fallback.title)
                }
                resolvedMime.startsWith("video/") -> {
                    val fallback = VideoItem(
                        id = "ext_video_${uri.toString().hashCode()}",
                        title = uri.lastPathSegment?.substringAfterLast('/') ?: "Video",
                        uriString = uri.toString(),
                        extension = uri.lastPathSegment?.substringAfterLast('.', "MP4")?.uppercase() ?: "MP4",
                        mimeType = resolvedMime,
                        durationMs = 0L,
                        sizeBytes = 0L,
                        resolution = "HD",
                    )
                    appendScannedMedia(ScannedMediaResult(videos = listOf(fallback)))
                    _selectedTab.value = MediaTab.VIDEOS
                    openVideo(fallback)
                    _statusBanner.value = s.openedExternalMediaBanner(fallback.title)
                }
                resolvedMime.startsWith("image/") -> {
                    val fallback = PhotoItem(
                        id = "ext_photo_${uri.toString().hashCode()}",
                        title = uri.lastPathSegment?.substringAfterLast('/') ?: "Photo",
                        uriString = uri.toString(),
                        extension = uri.lastPathSegment?.substringAfterLast('.', "JPG")?.uppercase() ?: "JPG",
                        mimeType = resolvedMime,
                        sizeBytes = 0L,
                        width = 0,
                        height = 0,
                    )
                    appendScannedMedia(ScannedMediaResult(photos = listOf(fallback)))
                    _selectedTab.value = MediaTab.PHOTOS
                    val idx = photos.value.indexOfFirst { it.id == fallback.id }.coerceAtLeast(0)
                    openPhoto(idx)
                    _statusBanner.value = s.openedExternalMediaBanner(fallback.title)
                }
            }
        }
    }

    fun exportBackupToUri(destUri: Uri) {
        viewModelScope.launch {
            val s = stringsFor(settings.value.language)
            val ok = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                runCatching {
                    val json = settingsRepo.exportBackupJson()
                    getApplication<Application>().contentResolver.openOutputStream(destUri)?.use { out ->
                        out.write(json.toByteArray(Charsets.UTF_8))
                        out.flush()
                        true
                    } ?: false
                }.getOrDefault(false)
            }
            _statusBanner.value = if (ok) s.backupExportSuccess else s.backupFailedBanner
        }
    }

    fun importBackupFromUri(sourceUri: Uri) {
        viewModelScope.launch {
            val s = stringsFor(settings.value.language)
            val ok = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                runCatching {
                    val raw = getApplication<Application>().contentResolver.openInputStream(sourceUri)
                        ?.bufferedReader(Charsets.UTF_8)
                        ?.use { it.readText() }
                        ?: return@runCatching false
                    settingsRepo.restoreBackupJson(raw)
                }.getOrDefault(false)
            }
            if (ok) {
                val restored = settingsRepo.settingsFlow.first()
                audioController.setEqualizerEnabled(restored.eqEnabled)
                audioController.setBassBoost(restored.bassBoost)
                audioController.applyEqualizerGains(restored.eqGainsDb)
                audioController.setPreampPercent(restored.audioPreampPercent)
                audioController.setPitch(restored.audioPitch)
                audioController.setCrossfadeSeconds(restored.crossfadeSeconds)
                audioController.setLoudnessNormalizationEnabled(restored.audioNormalization)
                audioController.setLectureVocalBoostEnabled(restored.lectureModeEnabled)
                audioController.setPauseOnHeadsetUnplug(restored.pauseOnHeadsetUnplug)
                audioController.setStereoBalance(restored.audioBalance)
                audioController.setMonoEnabled(restored.audioMonoEnabled)
                audioController.setSkipSilenceEnabled(restored.skipSilenceEnabled)
            }
            _statusBanner.value = if (ok) s.backupImportSuccess else s.backupFailedBanner
        }
    }

    fun dismissTamperAlertDialog() {
        _showTamperAlertDialog.value = false
    }

    fun saveTrackLyrics(trackId: String, lyrics: String) {
        viewModelScope.launch {
            if (lyrics.isNotBlank()) {
                val filtered = ContentPurityGuard.filterLyricsStrictly(lyrics)
                if (filtered == null) {
                    _statusBanner.value = stringsFor(settings.value.language).lyricsBlockedBanner
                    return@launch
                }
                settingsRepo.saveTrackLyrics(trackId, filtered)
            } else {
                settingsRepo.saveTrackLyrics(trackId, "")
            }
            _statusBanner.value = stringsFor(settings.value.language).lyricsSavedBanner
        }
    }

    fun resolveLyricsForTrack(track: AudioTrack, map: Map<String, String>): String? {
        val custom = map[track.id]?.takeIf { it.isNotBlank() }
        if (custom != null) return ContentPurityGuard.filterLyricsStrictly(custom)
        if (!track.isDemo) return null
        return when {
            track.title.contains("Aurora", ignoreCase = true) ->
                "[00:00.00] ♪ Colors drifting across the quiet sky\n" +
                    "[00:03.50] Waves of crystal light, lifting us high\n" +
                    "[00:07.00] Feel the rhythm echo in the midnight air\n" +
                    "[00:10.50] Prism studio sound everywhere ♪"
            track.title.contains("Velvet", ignoreCase = true) ->
                "[00:00.00] ♪ Soft bass rolling through the evening glow\n" +
                    "[00:03.50] Every chord breathing slow and low\n" +
                    "[00:07.00] Step into the groove, let the stereo shine\n" +
                    "[00:10.50] Timeless melody on the waveform line ♪"
            else ->
                "[00:00.00] ♪ Pure acoustic resonance, clear and bright\n" +
                    "[00:03.50] Crafted in high-definition studio light\n" +
                    "[00:07.00] Every note alive with balanced warmth and space\n" +
                    "[00:10.50] Prism Media • Studio Reference Showcase ♪"
        }
    }

    private fun assignOrders(result: ScannedMediaResult): ScannedMediaResult {
        val orderedAudio = result.audioTracks.map {
            if (it.addedOrder == 0L) it.copy(addedOrder = nextAddedOrder++) else it
        }
        val orderedVideos = result.videos.map {
            if (it.addedOrder == 0L) it.copy(addedOrder = nextAddedOrder++) else it
        }
        return result.copy(audioTracks = orderedAudio, videos = orderedVideos)
    }

    private fun appendScannedMedia(incoming: ScannedMediaResult) {
        val ordered = assignOrders(incoming)
        val audioMap = LinkedHashMap<String, AudioTrack>()
        _rawAudio.value.forEach { audioMap[it.id] = it }
        ordered.audioTracks.forEach { audioMap[it.id] = it }
        _rawAudio.value = audioMap.values.toList()

        val videoMap = LinkedHashMap<String, VideoItem>()
        _rawVideos.value.forEach { videoMap[it.id] = it }
        ordered.videos.forEach { videoMap[it.id] = it }
        _rawVideos.value = videoMap.values.toList()

        val photoMap = LinkedHashMap<String, PhotoItem>()
        _rawPhotos.value.forEach { photoMap[it.id] = it }
        ordered.photos.forEach { photoMap[it.id] = it }
        _rawPhotos.value = photoMap.values.toList()
    }

    private fun mergeEnrichedChunk(audioChunk: List<AudioTrack>, videoChunk: List<VideoItem>) {
        if (audioChunk.isNotEmpty()) {
            val byId = audioChunk.associateBy { it.id }
            _rawAudio.value = _rawAudio.value.map { byId[it.id] ?: it }
        }
        if (videoChunk.isNotEmpty()) {
            val byId = videoChunk.associateBy { it.id }
            _rawVideos.value = _rawVideos.value.map { byId[it.id] ?: it }
        }
    }

    private suspend fun persistCurrentCache() {
        settingsRepo.saveLibraryCache(
            ScannedMediaResult(
                audioTracks = _rawAudio.value,
                videos = _rawVideos.value,
                photos = _rawPhotos.value,
            )
        )
    }

    private fun applyScannedMedia(result: ScannedMediaResult) {
        val ordered = assignOrders(result)
        _rawAudio.value = ordered.audioTracks.distinctBy { it.id }
        _rawVideos.value = ordered.videos.distinctBy { it.id }
        _rawPhotos.value = ordered.photos.distinctBy { it.id }
    }

    private fun mergeResults(a: ScannedMediaResult, b: ScannedMediaResult): ScannedMediaResult {
        return ScannedMediaResult(
            audioTracks = (a.audioTracks + b.audioTracks).distinctBy { it.id },
            videos = (a.videos + b.videos).distinctBy { it.id },
            photos = (a.photos + b.photos).distinctBy { it.id },
        )
    }

    // --- 24-Feature Studio, Spiritual, Vault, Theme & Wi-Fi Transfer Actions ---

    private val wifiTransferServer by lazy {
        LocalWifiTransferServer(
            context = getApplication(),
            scope = viewModelScope,
            mediaProvider = { Triple(audioTracks.value, videos.value, photos.value) },
            onUploadedFile = { uri, _ -> importFiles(listOf(uri)) }
        )
    }

    val wifiTransferStatus: StateFlow<WifiTransferStatus>
        get() = wifiTransferServer.status

    fun startWifiTransfer() {
        wifiTransferServer.start()
    }

    fun stopWifiTransfer() {
        wifiTransferServer.stop()
    }

    private var lastTriggeredAlarmKey: String = ""

    private fun startDailyStatsAndAlarmTicker() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60_000L)
                if (audioController.isPlaying.value) {
                    val cal = java.util.Calendar.getInstance()
                    val dayKey = String.format(
                        java.util.Locale.US,
                        "%04d-%02d-%02d",
                        cal.get(java.util.Calendar.YEAR),
                        cal.get(java.util.Calendar.MONTH) + 1,
                        cal.get(java.util.Calendar.DAY_OF_MONTH)
                    )
                    settingsRepo.addDailyListeningMinutes(dayKey, 1)
                }
                val alarm = morningAlarmConfig.value
                if (alarm.enabled) {
                    val now = java.util.Calendar.getInstance()
                    val h = now.get(java.util.Calendar.HOUR_OF_DAY)
                    val m = now.get(java.util.Calendar.MINUTE)
                    val fireKey = "${now.get(java.util.Calendar.DAY_OF_YEAR)}-$h-$m"
                    if (h == alarm.hour && m == alarm.minute && fireKey != lastTriggeredAlarmKey) {
                        lastTriggeredAlarmKey = fireKey
                        val targetTrack = audioTracks.value.firstOrNull { it.id == alarm.trackId }
                            ?: audioTracks.value.firstOrNull()
                        if (targetTrack != null) {
                            previewMorningAlarmFadeIn(targetTrack, alarm.fadeInSeconds)
                        }
                    }
                }
            }
        }
    }

    fun setKhushuModeEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setKhushuModeEnabled(enabled) }
    }

    fun setLectureModeEnabled(enabled: Boolean) {
        audioController.setLectureVocalBoostEnabled(enabled)
        viewModelScope.launch {
            settingsRepo.setLectureModeEnabled(enabled)
            if (enabled) {
                settingsRepo.setResumePlayback(true)
                settingsRepo.setSkipSilenceEnabled(true)
                audioController.setSkipSilenceEnabled(true)
            }
        }
    }

    fun setSkipSilenceEnabled(enabled: Boolean) {
        audioController.setSkipSilenceEnabled(enabled)
        viewModelScope.launch { settingsRepo.setSkipSilenceEnabled(enabled) }
    }

    fun setAudioBalance(balance: Float) {
        val clamped = balance.coerceIn(-1f, 1f)
        audioController.setStereoBalance(clamped)
        viewModelScope.launch { settingsRepo.setAudioBalance(clamped) }
    }

    fun setAudioMonoEnabled(enabled: Boolean) {
        audioController.setMonoEnabled(enabled)
        viewModelScope.launch { settingsRepo.setAudioMonoEnabled(enabled) }
    }

    fun setShakeToShuffleEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setShakeToShuffleEnabled(enabled) }
    }

    fun setStripExifOnShare(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setStripExifOnShare(enabled) }
    }

    fun setCustomSeedColorArgb(argb: Int?) {
        viewModelScope.launch { settingsRepo.setCustomSeedColorArgb(argb) }
    }

    fun setAppIconVariant(variant: AppIconVariant) {
        AppIconSwitcher.applyAppIcon(getApplication(), variant)
        viewModelScope.launch { settingsRepo.setAppIconVariant(variant) }
    }

    fun setVideoSubtitleColor(color: SubtitleColorStyle) {
        viewModelScope.launch { settingsRepo.setVideoSubtitleColor(color) }
    }

    fun setVideoSubtitleBgOpacity(opacity: Float) {
        viewModelScope.launch { settingsRepo.setVideoSubtitleBgOpacity(opacity) }
    }

    fun setVideoSubtitleBottomOffsetDp(offsetDp: Int) {
        viewModelScope.launch { settingsRepo.setVideoSubtitleBottomOffsetDp(offsetDp) }
    }

    fun setTasbihDailyCount(count: Int) {
        viewModelScope.launch { settingsRepo.setTasbihDailyCount(count) }
    }

    fun setSettingsLayoutMode(mode: SettingsLayoutMode) {
        viewModelScope.launch { settingsRepo.setSettingsLayoutMode(mode) }
    }

    fun setMiniPlayerEdgeDockSide(side: EdgeDockSide) {
        viewModelScope.launch { settingsRepo.setMiniPlayerEdgeDockSide(side) }
    }

    fun setSpectrumVisualStyle(style: SpectrumVisualStyle) {
        viewModelScope.launch { settingsRepo.setSpectrumVisualStyle(style) }
    }

    fun setLyricsFontSizeSp(sizeSp: Int) {
        viewModelScope.launch { settingsRepo.setLyricsFontSizeSp(sizeSp) }
    }

    fun setDriveModeEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setDriveModeEnabled(enabled) }
    }

    fun setPlayerGestureSwipeArt(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPlayerGestureSwipeArt(enabled) }
    }

    fun setVideoMirrorEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setVideoMirrorEnabled(enabled) }
    }

    fun setVideoAutoHideSeconds(seconds: Int) {
        viewModelScope.launch { settingsRepo.setVideoAutoHideSeconds(seconds) }
    }

    fun applyQuickThemePreset(presetKey: String) {
        viewModelScope.launch {
            when (presetKey) {
                "material_you" -> {
                    settingsRepo.setLiquidGlassStyle(LiquidGlassStyle.SOLID_M3)
                    settingsRepo.setAmoledBlack(false)
                    settingsRepo.setAccentPalette(AccentPalette.SYSTEM)
                    settingsRepo.setCustomSeedColorArgb(null)
                    settingsRepo.setSurfaceDepthStyle(SurfaceDepthStyle.EXPRESSIVE)
                    settingsRepo.setNavBarStyle(NavBarStyle.DOCKED_MATERIAL_YOU)
                }
                "liquid_crystal" -> {
                    settingsRepo.setDarkTheme(true)
                    settingsRepo.setAmoledBlack(false)
                    settingsRepo.setLiquidGlassStyle(LiquidGlassStyle.CRYSTAL_TONAL)
                    settingsRepo.setLiquidGlassIntensity(0.72f)
                    settingsRepo.setEdgeGlowEnabled(true)
                    settingsRepo.setAccentPalette(AccentPalette.OCEAN)
                    settingsRepo.setCustomSeedColorArgb(null)
                    settingsRepo.setNavBarStyle(NavBarStyle.FLOATING_PILL)
                }
                "amoled_midnight" -> {
                    settingsRepo.setDarkTheme(true)
                    settingsRepo.setAmoledBlack(true)
                    settingsRepo.setLiquidGlassStyle(LiquidGlassStyle.MATERIAL_LIQUID)
                    settingsRepo.setLiquidGlassIntensity(0.45f)
                    settingsRepo.setAccentPalette(AccentPalette.LAVENDER)
                    settingsRepo.setCustomSeedColorArgb(null)
                    settingsRepo.setNavBarStyle(NavBarStyle.COMPACT_DOCK)
                }
                "emerald_mosque" -> {
                    settingsRepo.setDarkTheme(true)
                    settingsRepo.setAmoledBlack(false)
                    settingsRepo.setLiquidGlassStyle(LiquidGlassStyle.MATERIAL_LIQUID)
                    settingsRepo.setLiquidGlassIntensity(0.55f)
                    settingsRepo.setAccentPalette(AccentPalette.EMERALD)
                    settingsRepo.setCustomSeedColorArgb(null)
                    settingsRepo.setPlayerArtworkStyle(PlayerArtworkStyle.ROUNDED_CARD)
                }
            }
            _statusBanner.value = stringsFor(settings.value.language).presetAppliedBanner
        }
    }

    fun toggleQuranSurahCompleted(surahNumber: Int) {
        viewModelScope.launch {
            val cur = quranKhatmaProgress.value
            val nextSet = if (surahNumber in cur.completedSurahs) {
                cur.completedSurahs - surahNumber
            } else {
                cur.completedSurahs + surahNumber
            }
            settingsRepo.saveQuranKhatma(cur.copy(completedSurahs = nextSet, lastSurahNumber = surahNumber))
        }
    }

    fun setQuranHifzRepeat(targetCount: Int) {
        audioController.setHifzLoopTarget(targetCount)
        viewModelScope.launch {
            settingsRepo.saveQuranKhatma(quranKhatmaProgress.value.copy(hifzRepeatTarget = targetCount))
        }
    }

    fun saveMorningAlarm(config: MorningAlarmConfig) {
        MorningAlarmScheduler.scheduleOrCancel(getApplication(), config)
        viewModelScope.launch { settingsRepo.saveMorningAlarm(config) }
    }

    fun previewMorningAlarmFadeIn(track: AudioTrack, fadeSeconds: Int) {
        if (ContentPurityGuard.isExplicitOrAdult(track.title, track.artist, track.album, track.uriString)) {
            postBanner(stringsFor(settings.value.language).contentBlockedBanner)
            return
        }
        audioController.playWithGentleFadeIn(track, fadeSeconds)
    }

    fun saveFolderCustomization(custom: FolderCustomization) {
        viewModelScope.launch { settingsRepo.saveFolderCustomization(custom) }
    }

    fun saveBatchTrackTags(trackIds: Set<String>, artist: String?, album: String?, artworkUri: String?) {
        viewModelScope.launch {
            settingsRepo.saveBatchTrackTags(trackIds, artist, album, artworkUri)
        }
    }

    fun applyDecodedThemePreset(preset: DecodedThemePreset) {
        viewModelScope.launch { settingsRepo.applyDecodedThemePreset(preset) }
    }

    fun refreshVaultItems() {
        viewModelScope.launch {
            _vaultItems.value = PrivateVaultManager.listVaultItems(getApplication())
        }
    }

    fun importIntoVaultUri(uri: Uri) {
        viewModelScope.launch {
            val resolver = getApplication<Application>().contentResolver
            val mime = resolver.getType(uri).orEmpty()
            val mediaType = if (mime.startsWith("video/")) RenameMediaType.VIDEO else RenameMediaType.PHOTO
            val name = uri.lastPathSegment?.substringAfterLast('/') ?: "Private_${System.currentTimeMillis()}"
            val added = PrivateVaultManager.importIntoVault(getApplication(), name, uri.toString(), mediaType, mime)
            if (added == null) {
                postBanner(stringsFor(settings.value.language).contentBlockedBanner)
            } else {
                refreshVaultItems()
            }
        }
    }

    fun movePhotoToVault(photo: PhotoItem) {
        viewModelScope.launch {
            val added = PrivateVaultManager.importIntoVault(
                context = getApplication(),
                title = photo.title,
                uriString = photo.uriString,
                mediaType = RenameMediaType.PHOTO,
                mimeType = photo.mimeType,
                fallbackDrawableResId = photo.resId,
            )
            if (added != null) {
                removePhoto(photo)
                refreshVaultItems()
            }
        }
    }

    fun restoreVaultItem(item: VaultItem) {
        viewModelScope.launch {
            PrivateVaultManager.restorePhotoToGallery(getApplication(), item)
            refreshVaultItems()
        }
    }

    fun deleteVaultItem(item: VaultItem) {
        viewModelScope.launch {
            PrivateVaultManager.removeVaultItem(getApplication(), item.id)
            refreshVaultItems()
        }
    }

    fun addVideoChapterBookmark(videoId: String, positionMs: Long, label: String) {
        viewModelScope.launch {
            val next = MediaBookmark(
                id = java.util.UUID.randomUUID().toString(),
                mediaId = videoId,
                positionMs = positionMs.coerceAtLeast(0L),
                label = label.ifBlank { MediaScanner.formatDuration(positionMs) },
                createdAtMs = System.currentTimeMillis(),
            )
            settingsRepo.saveBookmarks((bookmarks.value + next).takeLast(120))
        }
    }

    // --- 17-Upgrade Suite Actions ---

    fun setMusicBrowseMode(mode: MusicBrowseMode) {
        _musicBrowseMode.value = mode
    }

    fun setReverbEnvironment(env: ReverbEnvironment) {
        audioController.setReverbEnvironment(env)
        viewModelScope.launch { settingsRepo.setReverbEnvironment(env) }
    }

    fun setAutoAudioDeviceProfiles(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setAutoAudioDeviceProfiles(enabled)
            if (enabled) refreshDetectedAudioOutput(applyIfAuto = true)
        }
    }

    fun refreshDetectedAudioOutput(applyIfAuto: Boolean = false) {
        val detected = MediaToolsEngine.detectCurrentAudioOutputType(getApplication())
        _activeAudioOutput.value = detected
        if (applyIfAuto && settings.value.autoAudioDeviceProfiles) {
            applyAudioDeviceProfile(detected, showBanner = false)
        }
    }

    fun saveAudioDeviceProfile(profile: AudioDeviceProfile) {
        viewModelScope.launch {
            settingsRepo.saveAudioDeviceProfile(profile)
            if (_activeAudioOutput.value == profile.type) {
                applyAudioDeviceProfile(profile.type, showBanner = true, overrideProfile = profile)
            }
        }
    }

    fun applyAudioDeviceProfile(
        type: AudioOutputProfileType,
        showBanner: Boolean = true,
        overrideProfile: AudioDeviceProfile? = null,
    ) {
        val profile = overrideProfile ?: audioDeviceProfiles.value[type] ?: return
        _activeAudioOutput.value = type
        selectEqPreset(profile.eqPreset)
        setBassBoost(profile.bassBoost)
        setReverbEnvironment(profile.reverb)
        if (showBanner) {
            val s = stringsFor(settings.value.language)
            val label = when (type) {
                AudioOutputProfileType.SPEAKER -> s.audioOutputSpeaker
                AudioOutputProfileType.WIRED_HEADSET -> s.audioOutputWired
                AudioOutputProfileType.BLUETOOTH -> s.audioOutputBluetooth
            }
            _statusBanner.value = s.audioProfileAppliedBanner(label)
        }
    }

    fun saveMediaNote(
        mediaId: String,
        mediaTitle: String,
        mediaType: RenameMediaType,
        noteText: String,
        timestampMs: Long = 0L,
    ) {
        viewModelScope.launch {
            val cleaned = noteText.trim()
            if (cleaned.isEmpty()) {
                settingsRepo.deleteMediaNote(mediaId)
            } else {
                settingsRepo.saveMediaNote(
                    MediaNote(
                        mediaId = mediaId,
                        mediaTitle = mediaTitle,
                        mediaType = mediaType,
                        noteText = cleaned,
                        timestampMs = timestampMs,
                        updatedAtMs = System.currentTimeMillis(),
                    )
                )
                _statusBanner.value = stringsFor(settings.value.language).noteSavedBanner
            }
        }
    }

    fun deleteMediaNote(mediaId: String) {
        viewModelScope.launch { settingsRepo.deleteMediaNote(mediaId) }
    }

    fun movePlaylistTrack(playlistId: String, trackId: String, delta: Int) {
        viewModelScope.launch {
            val updated = playlists.value.map { pl ->
                if (pl.id != playlistId) pl
                else {
                    val list = pl.trackIds.toMutableList()
                    val idx = list.indexOf(trackId)
                    val target = (idx + delta).coerceIn(0, list.lastIndex.coerceAtLeast(0))
                    if (idx >= 0 && idx != target) {
                        val moved = list.removeAt(idx)
                        list.add(target, moved)
                        pl.copy(trackIds = list)
                    } else pl
                }
            }
            settingsRepo.savePlaylists(updated)
        }
    }

    fun exportPlaylistToM3uUri(playlistId: String, destUri: Uri) {
        viewModelScope.launch {
            val pl = playlists.value.firstOrNull { it.id == playlistId } ?: return@launch
            val m3u = MediaToolsEngine.exportPlaylistToM3u(pl, audioTracks.value)
            val ok = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                runCatching {
                    getApplication<Application>().contentResolver.openOutputStream(destUri)?.use { out ->
                        out.write(m3u.toByteArray(Charsets.UTF_8))
                        out.flush()
                        true
                    } ?: false
                }.getOrDefault(false)
            }
            if (ok) {
                _statusBanner.value = stringsFor(settings.value.language).m3uExportedBanner
            }
        }
    }

    fun importPlaylistFromM3uUri(sourceUri: Uri) {
        viewModelScope.launch {
            val raw = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                runCatching {
                    getApplication<Application>().contentResolver.openInputStream(sourceUri)
                        ?.bufferedReader(Charsets.UTF_8)
                        ?.use { it.readText() }
                }.getOrNull()
            } ?: return@launch
            val fallbackName = sourceUri.lastPathSegment
                ?.substringAfterLast('/')
                ?.substringBeforeLast('.')
                ?.ifBlank { "Imported M3U" }
                ?: "Imported M3U"
            val (name, matchedIds) = MediaToolsEngine.parseM3uPlaylist(raw, fallbackName, audioTracks.value)
            val created = UserPlaylist(
                id = UUID.randomUUID().toString(),
                name = name,
                trackIds = matchedIds,
            )
            settingsRepo.savePlaylists(playlists.value + created)
            _selectedPlaylistId.value = created.id
            _statusBanner.value = stringsFor(settings.value.language).m3uImportedBanner(matchedIds.size)
        }
    }

    fun extractAudioFromVideo(video: VideoItem) {
        val s = stringsFor(settings.value.language)
        _statusBanner.value = s.extractingAudioBanner
        viewModelScope.launch {
            val res = MediaToolsEngine.extractAudioFromVideo(getApplication(), video)
            val track = res.extractedTrack
            if (res.success && track != null) {
                val ordered = track.copy(addedOrder = nextAddedOrder++)
                _rawAudio.value = listOf(ordered) + _rawAudio.value
                _statusBanner.value = s.audioExtractedBanner
            } else {
                _statusBanner.value = res.error ?: s.noOnlineInfoFound
            }
        }
    }

    fun setVideoAutoNextEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setVideoAutoNextEnabled(enabled) }
    }

    fun moveNavTab(tab: MediaTab, delta: Int) {
        viewModelScope.launch {
            val cur = SettingsRepository.normalizeNavTabOrder(settings.value.navTabOrder).toMutableList()
            val idx = cur.indexOf(tab)
            val target = (idx + delta).coerceIn(0, cur.lastIndex)
            if (idx >= 0 && idx != target) {
                val moved = cur.removeAt(idx)
                cur.add(target, moved)
                settingsRepo.setNavTabOrder(cur)
            }
        }
    }

    fun resetNavTabOrder() {
        viewModelScope.launch { settingsRepo.setNavTabOrder(MediaTab.entries.toList()) }
    }

    fun setHeaderShowStreamBtn(show: Boolean) {
        viewModelScope.launch { settingsRepo.setHeaderShowStreamBtn(show) }
    }

    fun setHeaderShowMultiWindowBtn(show: Boolean) {
        viewModelScope.launch { settingsRepo.setHeaderShowMultiWindowBtn(show) }
    }

    fun toggleAdhkarCompleted(id: String) {
        viewModelScope.launch {
            val cur = settings.value.adhkarCompletedKeys
            val next = if (id in cur) cur - id else cur + id
            settingsRepo.setAdhkarCompletedKeys(next)
        }
    }

    fun resetDailyAdhkar() {
        viewModelScope.launch { settingsRepo.setAdhkarCompletedKeys(emptySet()) }
    }

    fun addTrimmedVideo(video: VideoItem) {
        val ordered = video.copy(addedOrder = nextAddedOrder++)
        _rawVideos.value = listOf(ordered) + _rawVideos.value
        _statusBanner.value = stringsFor(settings.value.language).videoTrimSuccessBanner
    }

    fun handleSharedIntentUris(uris: List<Uri>, sharedText: String?) {
        val s = stringsFor(settings.value.language)
        if (uris.isNotEmpty()) {
            importFiles(uris)
            _statusBanner.value = s.sharedToPrismBanner
            return
        }
        val cleaned = sharedText?.trim().orEmpty()
        if (cleaned.isNotEmpty()) {
            val urlMatch = Regex("https://\\S+").find(cleaned)?.value
            if (urlMatch != null) {
                val lower = urlMatch.lowercase()
                val looksLikeVideo = lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".webm") || lower.contains(".m3u8")
                playStreamUrl(urlMatch, "", isVideo = looksLikeVideo)
            }
        }
    }

    fun triggerMorningAlarmPlayback(trackId: String?, fadeSeconds: Int) {
        val target = trackId?.takeIf { it.isNotBlank() }?.let { id ->
            audioTracks.value.firstOrNull { it.id == id }
        } ?: audioTracks.value.firstOrNull()
        if (target != null) {
            previewMorningAlarmFadeIn(target, fadeSeconds.coerceIn(5, 120))
        }
    }

    fun setPlayerGenreDynamicColors(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPlayerGenreDynamicColors(enabled) }
    }

    fun setPlayerShowTechTelemetry(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPlayerShowTechTelemetry(enabled) }
    }

    fun setPlayerExtrasButtonSubtle(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPlayerExtrasButtonSubtle(enabled) }
    }

    fun setPlayerDirectBitExactDsp(enabled: Boolean) {
        val cur = settings.value
        audioController.setEqualizerEnabled(!enabled && cur.eqEnabled)
        audioController.setBassBoost(if (enabled) 0f else cur.bassBoost)
        audioController.setPreampPercent(if (enabled) 100 else cur.audioPreampPercent)
        audioController.setReverbEnvironment(if (enabled) ReverbEnvironment.NONE else cur.reverbEnvironment)
        audioController.setDspBypassEnabled(enabled || cur.dspBypassEnabled)
        viewModelScope.launch { settingsRepo.setPlayerDirectBitExactDsp(enabled) }
    }

    fun setPlayerExpressivePlayMorph(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPlayerExpressivePlayMorph(enabled) }
    }

    fun setLockScreenControlsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setLockScreenControlsEnabled(enabled) }
    }

    fun setLockScreenShowArtwork(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setLockScreenShowArtwork(enabled) }
    }

    fun setLockScreenShowPrismIdentity(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setLockScreenShowPrismIdentity(enabled) }
    }

    fun setLockScreenFullScreenEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setLockScreenFullScreenEnabled(enabled) }
    }

    fun setLockScreenShowClock(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setLockScreenShowClock(enabled) }
    }

    fun setLockScreenShowLyrics(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setLockScreenShowLyrics(enabled) }
    }

    fun setLockScreenBlurBackdrop(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setLockScreenBlurBackdrop(enabled) }
    }

    fun setLockScreenCompactMode(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setLockScreenCompactMode(enabled) }
    }

    fun setPipMusicEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPipMusicEnabled(enabled) }
    }

    fun setPipVideoEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPipVideoEnabled(enabled) }
    }

    fun setPipPhotoEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setPipPhotoEnabled(enabled) }
    }

    fun setCardStyle(style: com.example.model.MediaCardStyle) {
        viewModelScope.launch { settingsRepo.setCardStyle(style) }
    }

    fun setBackdropStyle(style: com.example.model.BackdropAnimationStyle) {
        viewModelScope.launch { settingsRepo.setBackdropStyle(style) }
    }

    fun setMotionFeelStyle(style: com.example.model.MotionFeelStyle) {
        viewModelScope.launch { settingsRepo.setMotionFeelStyle(style) }
    }

    fun setHeaderTitleStyle(style: com.example.model.HeaderTitleStyle) {
        viewModelScope.launch { settingsRepo.setHeaderTitleStyle(style) }
    }

    fun setSwipeGesturesEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setSwipeGesturesEnabled(enabled) }
    }

    fun setShowFormatBadges(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setShowFormatBadges(enabled) }
    }

    fun setAccentStrength(strength: Float) {
        viewModelScope.launch { settingsRepo.setAccentStrength(strength) }
    }

    // --- Beta 4.4 Pro Studio 10-Band EQ, Auto Studio Mastering & 15+ Real DSP Controls ---
    fun setProEqEnabled(enabled: Boolean) {
        audioController.setProEqEnabled(enabled)
        viewModelScope.launch { settingsRepo.setProEqEnabled(enabled) }
    }

    fun selectProEqPreset(preset: com.example.model.ProEqPreset) {
        val gains = preset.gainsDb
        audioController.setProEqEnabled(true)
        audioController.setTenBandGainsDb(gains)
        audioController.setDspBypassEnabled(false)
        viewModelScope.launch {
            settingsRepo.setProEqEnabled(true)
            settingsRepo.setDspBypassEnabled(false)
            settingsRepo.setProTenBandEqualizer(preset, gains)
        }
    }

    fun updateProTenBand(bandIndex: Int, gainDb: Float) {
        val updated = settings.value.proTenBandGainsDb.toMutableList()
        if (updated.size != 10) return
        if (bandIndex in updated.indices) {
            updated[bandIndex] = gainDb.coerceIn(-15f, 15f)
            audioController.setProEqEnabled(true)
            audioController.setTenBandGainsDb(updated)
            audioController.setDspBypassEnabled(false)
            viewModelScope.launch {
                settingsRepo.setProEqEnabled(true)
                settingsRepo.setDspBypassEnabled(false)
                settingsRepo.setProTenBandEqualizer(com.example.model.ProEqPreset.CUSTOM, updated)
            }
        }
    }

    fun setAutoStudioMasteringEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setAutoStudioMasteringEnabled(enabled) }
        if (enabled) {
            val activeTrack = audioController.currentTrack.value ?: audioTracks.value.firstOrNull()
            if (activeTrack != null) {
                applyAutoStudioMasteringForTrack(activeTrack, showBanner = true)
            }
        } else {
            _detectedAutoStudioProfile.value = null
        }
    }

    fun analyzeAndApplyAutoStudioNow() {
        val activeTrack = audioController.currentTrack.value ?: audioTracks.value.firstOrNull()
        if (activeTrack == null) {
            _statusBanner.value = stringsFor(settings.value.language).studioAutoNoTrackHint
            return
        }
        applyAutoStudioMasteringForTrack(activeTrack, showBanner = true)
    }

    private fun applyAutoStudioMasteringForTrack(track: AudioTrack, showBanner: Boolean) {
        val knowledge = _knowledgeReports.value[track.id]
        val liveSpec = if (audioController.isPlaying.value) audioController.spectrumLevels.value else emptyList()
        val profile = com.example.player.AutoStudioMasteringEngine.analyzeTrack(
            track = track,
            knowledgeReport = knowledge,
            liveSpectrum = liveSpec,
        )
        _detectedAutoStudioProfile.value = profile
        audioController.setProEqEnabled(true)
        audioController.setTenBandGainsDb(profile.tenBandGainsDb)
        audioController.setTubeWarmth(profile.tubeWarmth)
        audioController.setStereoWidth(profile.stereoWidth)
        audioController.setSubBassPunch(profile.subBassPunch)
        audioController.setAirBrilliance(profile.airBrilliance)
        audioController.setVocalClarity(profile.vocalClarity)
        audioController.setCompressorStrength(profile.compressorStrength)
        audioController.setCrossfeedEnabled(profile.crossfeedEnabled)
        audioController.setRumbleFilterHz(profile.rumbleFilterHz)
        audioController.setReverbEnvironment(profile.reverbEnvironment)
        audioController.setDspBypassEnabled(false)

        viewModelScope.launch {
            settingsRepo.applyStudioSnapshotBatch(
                tenBandGainsDb = profile.tenBandGainsDb,
                preset = com.example.model.ProEqPreset.STUDIO_MASTER,
                tubeWarmth = profile.tubeWarmth,
                stereoWidth = profile.stereoWidth,
                subBassPunch = profile.subBassPunch,
                airBrilliance = profile.airBrilliance,
                vocalClarity = profile.vocalClarity,
                compressorStrength = profile.compressorStrength,
                crossfeedEnabled = profile.crossfeedEnabled,
                rumbleFilterHz = profile.rumbleFilterHz,
                reverbEnvironment = profile.reverbEnvironment,
            )
        }
        if (showBanner) {
            val s = stringsFor(settings.value.language)
            _statusBanner.value = "${s.studioAutoAppliedBanner}: ${s.autoStudioCategoryName(profile.category)} (${profile.confidencePercent}%)"
        }
    }

    fun setTubeWarmth(amount: Float) {
        val c = amount.coerceIn(0f, 1f)
        audioController.setTubeWarmth(c)
        viewModelScope.launch { settingsRepo.setTubeWarmth(c) }
    }

    fun setStereoWidth(width: Float) {
        val c = width.coerceIn(0.5f, 2.0f)
        audioController.setStereoWidth(c)
        viewModelScope.launch { settingsRepo.setStereoWidth(c) }
    }

    fun setCrossfeedEnabled(enabled: Boolean) {
        audioController.setCrossfeedEnabled(enabled)
        viewModelScope.launch { settingsRepo.setCrossfeedEnabled(enabled) }
    }

    fun setSubBassPunch(amount: Float) {
        val c = amount.coerceIn(0f, 1f)
        audioController.setSubBassPunch(c)
        viewModelScope.launch { settingsRepo.setSubBassPunch(c) }
    }

    fun setAirBrilliance(amount: Float) {
        val c = amount.coerceIn(0f, 1f)
        audioController.setAirBrilliance(c)
        viewModelScope.launch { settingsRepo.setAirBrilliance(c) }
    }

    fun setVocalClarity(amount: Float) {
        val c = amount.coerceIn(0f, 1f)
        audioController.setVocalClarity(c)
        viewModelScope.launch { settingsRepo.setVocalClarity(c) }
    }

    fun setCompressorStrength(strength: Float) {
        val c = strength.coerceIn(0f, 1f)
        audioController.setCompressorStrength(c)
        viewModelScope.launch { settingsRepo.setCompressorStrength(c) }
    }

    fun setTruePeakLimiterEnabled(enabled: Boolean) {
        audioController.setTruePeakLimiterEnabled(enabled)
        viewModelScope.launch { settingsRepo.setTruePeakLimiterEnabled(enabled) }
    }

    fun setNightModeEnabled(enabled: Boolean) {
        audioController.setNightModeEnabled(enabled)
        viewModelScope.launch { settingsRepo.setNightModeEnabled(enabled) }
    }

    fun setRumbleFilterHz(hz: Int) {
        audioController.setRumbleFilterHz(hz)
        viewModelScope.launch { settingsRepo.setRumbleFilterHz(hz) }
    }

    fun setChannelSwapEnabled(enabled: Boolean) {
        audioController.setChannelSwapEnabled(enabled)
        viewModelScope.launch { settingsRepo.setChannelSwapEnabled(enabled) }
    }

    fun setHeadphoneProfile(profile: com.example.model.HeadphoneTuningProfile) {
        audioController.setHeadphoneProfile(profile)
        viewModelScope.launch { settingsRepo.setHeadphoneProfile(profile) }
    }

    fun setDspBypassEnabled(bypass: Boolean) {
        audioController.setDspBypassEnabled(bypass)
        viewModelScope.launch { settingsRepo.setDspBypassEnabled(bypass) }
    }

    fun resetProStudioAll() {
        val flat = com.example.model.ProEqPreset.FLAT.gainsDb
        _detectedAutoStudioProfile.value = null
        audioController.applyStudioConfig(com.example.model.StudioAudioConfig())
        audioController.setBassBoost(0f)
        audioController.setReverbEnvironment(ReverbEnvironment.NONE)
        viewModelScope.launch {
            settingsRepo.setAutoStudioMasteringEnabled(false)
            settingsRepo.setBassBoost(0f)
            settingsRepo.setChannelSwapEnabled(false)
            settingsRepo.setTruePeakLimiterEnabled(true)
            settingsRepo.applyStudioSnapshotBatch(
                tenBandGainsDb = flat,
                preset = com.example.model.ProEqPreset.FLAT,
                tubeWarmth = 0f,
                stereoWidth = 1.0f,
                subBassPunch = 0f,
                airBrilliance = 0f,
                vocalClarity = 0f,
                compressorStrength = 0f,
                crossfeedEnabled = false,
                rumbleFilterHz = 0,
                reverbEnvironment = ReverbEnvironment.NONE,
                nightModeEnabled = false,
                headphoneProfile = com.example.model.HeadphoneTuningProfile.NEUTRAL_STUDIO,
            )
        }
    }

    fun saveCurrentAsCustomStudioPreset(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        val s = settings.value
        val preset = com.example.model.CustomStudioPreset(
            id = "preset_${System.currentTimeMillis()}",
            name = trimmed.take(36),
            tenBandGainsDb = s.proTenBandGainsDb,
            tubeWarmth = s.tubeWarmth,
            stereoWidth = s.stereoWidth,
            subBassPunch = s.subBassPunch,
            airBrilliance = s.airBrilliance,
            vocalClarity = s.vocalClarity,
            compressorStrength = s.compressorStrength,
            crossfeedEnabled = s.crossfeedEnabled,
            nightModeEnabled = s.nightModeEnabled,
            rumbleFilterHz = s.rumbleFilterHz,
            headphoneProfile = s.headphoneProfile,
            reverbEnvironment = s.reverbEnvironment,
        )
        viewModelScope.launch { settingsRepo.saveCustomStudioPreset(preset) }
        _statusBanner.value = "${stringsFor(s.language).studioPresetSavedBanner}: $trimmed"
    }

    fun applyCustomStudioPreset(preset: com.example.model.CustomStudioPreset) {
        audioController.applyStudioConfig(
            com.example.model.StudioAudioConfig(
                proEqEnabled = true,
                tenBandGainsDb = preset.tenBandGainsDb,
                tubeWarmth = preset.tubeWarmth,
                stereoWidth = preset.stereoWidth,
                crossfeedEnabled = preset.crossfeedEnabled,
                subBassPunch = preset.subBassPunch,
                airBrilliance = preset.airBrilliance,
                vocalClarity = preset.vocalClarity,
                compressorStrength = preset.compressorStrength,
                truePeakLimiterEnabled = settings.value.truePeakLimiterEnabled,
                nightModeEnabled = preset.nightModeEnabled,
                rumbleFilterHz = preset.rumbleFilterHz,
                channelSwapEnabled = settings.value.channelSwapEnabled,
                headphoneProfile = preset.headphoneProfile,
                bypassEnabled = false,
            )
        )
        audioController.setReverbEnvironment(preset.reverbEnvironment)
        viewModelScope.launch {
            settingsRepo.applyStudioSnapshotBatch(
                tenBandGainsDb = preset.tenBandGainsDb,
                preset = com.example.model.ProEqPreset.CUSTOM,
                tubeWarmth = preset.tubeWarmth,
                stereoWidth = preset.stereoWidth,
                subBassPunch = preset.subBassPunch,
                airBrilliance = preset.airBrilliance,
                vocalClarity = preset.vocalClarity,
                compressorStrength = preset.compressorStrength,
                crossfeedEnabled = preset.crossfeedEnabled,
                rumbleFilterHz = preset.rumbleFilterHz,
                reverbEnvironment = preset.reverbEnvironment,
                nightModeEnabled = preset.nightModeEnabled,
                headphoneProfile = preset.headphoneProfile,
            )
        }
        _statusBanner.value = "${stringsFor(settings.value.language).studioPresetLoadedBanner}: ${preset.name}"
    }

    fun deleteCustomStudioPreset(presetId: String) {
        viewModelScope.launch { settingsRepo.deleteCustomStudioPreset(presetId) }
    }

    override fun onCleared() {
        super.onCleared()
        wifiTransferServer.stop()
        WidgetPlaybackBridge.onPlayPause = null
        WidgetPlaybackBridge.onNext = null
        WidgetPlaybackBridge.onPrev = null
        MediaNotificationBridge.onPlayPause = null
        MediaNotificationBridge.onNext = null
        MediaNotificationBridge.onPrev = null
        MediaNotificationBridge.onStop = null
        MediaNotificationBridge.onSeekTo = null
        MediaNotificationManager.cancel(getApplication())
        audioController.release()
    }
}

private data class WidgetSyncSnapshot(
    val track: AudioTrack?,
    val isPlaying: Boolean,
    val settings: AppSettings,
    val speed: Float,
    val khatma: QuranKhatmaProgress,
)
