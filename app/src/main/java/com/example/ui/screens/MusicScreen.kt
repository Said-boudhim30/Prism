package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.AccessAlarm
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.MediaScanner
import com.example.localization.AppStrings
import com.example.model.AudioTrack
import com.example.model.FolderCustomization
import com.example.model.MediaDetailsInfo
import com.example.model.MediaNote
import com.example.model.MediaSortOption
import com.example.model.MediaViewMode
import com.example.model.MusicBrowseMode
import com.example.model.SmartMusicMix
import com.example.model.UserPlaylist
import com.example.ui.components.FolderFilterChipsRow
import com.example.ui.components.MediaOverflowMenuButton
import com.example.ui.components.MediaSelectionActionBar
import com.example.ui.components.MediaViewModeButton
import com.example.ui.components.OnboardingEmptyState
import com.example.ui.components.ShareableMediaPayload
import com.example.ui.components.SmartMusicMixesRow
import com.example.ui.components.TrackArtworkImage
import com.example.ui.components.shareMediaItem
import com.example.ui.components.shareMultipleMediaItems
import com.example.ui.components.expressivePressScale

private val AUDIO_FORMAT_FILTERS = listOf("All", "MP3", "FLAC", "WAV", "OGG", "M4A", "AAC", "OPUS", "WEBM")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MusicScreen(
    tracks: List<AudioTrack>,
    hasAnyMedia: Boolean,
    currentTrackId: String?,
    isPlaying: Boolean,
    searchQuery: String,
    selectedFormat: String,
    favoritesOnly: Boolean,
    sortOption: MediaSortOption,
    viewMode: MediaViewMode = MediaViewMode.LIST,
    playlists: List<UserPlaylist>,
    selectedPlaylistId: String?,
    availableFolders: List<String> = emptyList(),
    selectedFolder: String? = null,
    recentlyPlayed: List<AudioTrack> = emptyList(),
    selectedSmartMix: SmartMusicMix = SmartMusicMix.ALL_TRACKS,
    strings: AppStrings,
    bottomContentPadding: Dp,
    onSearchChange: (String) -> Unit,
    onSelectFormat: (String) -> Unit,
    onSelectSmartMix: (SmartMusicMix) -> Unit = {},
    onToggleFavoritesFilter: () -> Unit,
    onSelectSortOption: (MediaSortOption) -> Unit,
    onSelectViewMode: (MediaViewMode) -> Unit = {},
    onSelectFolderFilter: (String?) -> Unit = {},
    onSelectPlaylist: (String?) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onAddTrackToPlaylist: (AudioTrack) -> Unit,
    onAddSelectedToPlaylist: (List<String>) -> Unit = {},
    onPlayTrack: (AudioTrack) -> Unit,
    onToggleFavorite: (AudioTrack) -> Unit,
    onFavoriteSelected: (Set<String>) -> Unit = {},
    onRenameTrack: (AudioTrack) -> Unit,
    onRemoveTrack: (AudioTrack) -> Unit,
    onRemoveSelected: (Set<String>) -> Unit = {},
    onShowDetails: (MediaDetailsInfo) -> Unit,
    onSelectFolder: () -> Unit,
    onImportFiles: () -> Unit,
    onLoadDemoMedia: () -> Unit,
    folderCustomizations: Map<String, FolderCustomization> = emptyMap(),
    onLongPressFolder: ((String) -> Unit)? = null,
    onBatchEditTags: (Set<String>) -> Unit = {},
    onOpenQuranHub: () -> Unit = {},
    onOpenMorningAlarm: () -> Unit = {},
    onOpenListeningStats: () -> Unit = {},
    onOpenTasbih: () -> Unit = {},
    browseMode: MusicBrowseMode = MusicBrowseMode.TRACKS,
    onSelectBrowseMode: (MusicBrowseMode) -> Unit = {},
    allAudioTracks: List<AudioTrack> = tracks,
    onMovePlaylistTrack: (String, String, Int) -> Unit = { _, _, _ -> },
    onExportPlaylistM3u: (String) -> Unit = {},
    onImportPlaylistM3u: () -> Unit = {},
    mediaNotes: Map<String, MediaNote> = emptyMap(),
    onOpenNote: (AudioTrack) -> Unit = {},
    swipeGesturesEnabled: Boolean = true,
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    var showFiltersSheet by remember { mutableStateOf(false) }
    var showPlaylistsSheet by remember { mutableStateOf(false) }
    var newPlaylistInput by remember { mutableStateOf("") }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(tracks) {
        val validIds = tracks.map { it.id }.toSet()
        selectedIds = selectedIds.intersect(validIds)
        if (tracks.isEmpty()) selectionMode = false
    }

    BackHandler(enabled = selectionMode) {
        selectionMode = false
        selectedIds = emptySet()
    }

    fun toggleSelection(id: String) {
        selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id
    }

    if (!hasAnyMedia) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = bottomContentPadding)
        ) {
            item {
                OnboardingEmptyState(
                    strings = strings,
                    onSelectFolder = onSelectFolder,
                    onImportFiles = onImportFiles,
                    onLoadDemoMedia = onLoadDemoMedia
                )
            }
        }
        return
    }

    val activePlaylist = playlists.firstOrNull { it.id == selectedPlaylistId }
    val itemSpacing = when (com.example.ui.theme.LocalUiDensity.current) {
        com.example.model.UiDensity.COMFORTABLE -> 14.dp
        com.example.model.UiDensity.BALANCED -> 10.dp
        com.example.model.UiDensity.COMPACT -> 6.dp
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = bottomContentPadding
        ),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Calm Single-Row Search + Filters & Tools Sheet Trigger + Playlists Button
        item(key = "music_calm_top_bar", contentType = "top_bar") {
            val activeFiltersCount = remember(
                favoritesOnly,
                selectedFormat,
                selectedSmartMix,
                selectedFolder,
                sortOption,
                browseMode
            ) {
                var count = 0
                if (favoritesOnly) count++
                if (!selectedFormat.equals("All", ignoreCase = true)) count++
                if (selectedSmartMix != SmartMusicMix.ALL_TRACKS) count++
                if (selectedFolder != null) count++
                if (sortOption != MediaSortOption.NEWEST) count++
                if (browseMode != MusicBrowseMode.TRACKS) count++
                count
            }

            Row(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = strings.searchAudioHint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Rounded.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Rounded.Close, contentDescription = strings.close)
                            }
                        }
                    },
                    singleLine = true,
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("music_search_input")
                )

                // Clean Filters & Tools Modal Trigger (consolidates Sort, Favs, Mixes, Formats, Folders, Browse, Studio)
                Surface(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        showFiltersSheet = true
                    },
                    shape = CircleShape,
                    color = if (activeFiltersCount > 0) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    },
                    border = BorderStroke(
                        1.dp,
                        if (activeFiltersCount > 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        }
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("music_filters_sheet_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = if (activeFiltersCount > 0) 14.dp else 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = strings.filtersAndToolsAction,
                            tint = if (activeFiltersCount > 0) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        if (activeFiltersCount > 0) {
                            Text(
                                text = "$activeFiltersCount",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Direct 1-Tap Playlists Button
                Surface(
                    onClick = { showPlaylistsSheet = true },
                    shape = CircleShape,
                    color = if (activePlaylist != null) {
                        MaterialTheme.colorScheme.tertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    },
                    border = BorderStroke(
                        1.dp,
                        if (activePlaylist != null) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        }
                    ),
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("playlists_chip")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                            contentDescription = strings.playlistsTitle,
                            tint = if (activePlaylist != null) {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }

        // Slim 1-Line Summary Bar (Count + Active Filter Reset Pill + Subtle Select Toggle)
        item(key = "music_summary_bar", contentType = "summary") {
            val hasActiveFilters = favoritesOnly ||
                !selectedFormat.equals("All", ignoreCase = true) ||
                selectedSmartMix != SmartMusicMix.ALL_TRACKS ||
                selectedFolder != null ||
                sortOption != MediaSortOption.NEWEST ||
                browseMode != MusicBrowseMode.TRACKS ||
                activePlaylist != null

            Row(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = strings.tracksCount(tracks.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (activePlaylist != null) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onSelectPlaylist(null) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = activePlaylist.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = strings.clearPlaylistFilter,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Surface(
                            onClick = { onExportPlaylistM3u(activePlaylist.id) },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.testTag("active_playlist_export_m3u")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FileUpload,
                                    contentDescription = strings.exportM3uAction,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "M3U",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    } else if (hasActiveFilters) {
                        Surface(
                            onClick = {
                                if (favoritesOnly) onToggleFavoritesFilter()
                                onSelectFormat("All")
                                onSelectSmartMix(SmartMusicMix.ALL_TRACKS)
                                onSelectFolderFilter(null)
                                onSelectSortOption(MediaSortOption.NEWEST)
                                onSelectBrowseMode(MusicBrowseMode.TRACKS)
                            },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.testTag("music_clear_filters_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = strings.clearFiltersAction,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = strings.clearFiltersAction,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectionMode = !selectionMode
                        if (!selectionMode) selectedIds = emptySet()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("music_select_mode_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Checklist,
                        contentDescription = strings.selectModeAction,
                        tint = if (selectionMode) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Contextual Multi-Selection Action Bar
        if (selectionMode) {
            item {
                MediaSelectionActionBar(
                    visible = true,
                    selectedCount = selectedIds.size,
                    totalCount = tracks.size,
                    strings = strings,
                    onCloseSelection = {
                        selectionMode = false
                        selectedIds = emptySet()
                    },
                    onSelectAll = { selectedIds = tracks.map { it.id }.toSet() },
                    onDeselectAll = { selectedIds = emptySet() },
                    onInvertSelection = {
                        selectedIds = tracks.map { it.id }.filterNot { it in selectedIds }.toSet()
                    },
                    onShareSelected = {
                        val chosen = tracks.filter { it.id in selectedIds }.map {
                            ShareableMediaPayload(it.title, it.uriString, it.mimeType)
                        }
                        shareMultipleMediaItems(context, chosen, strings.menuShare)
                    },
                    onFavoriteSelected = {
                        onFavoriteSelected(selectedIds)
                        selectionMode = false
                        selectedIds = emptySet()
                    },
                    onAddSelectedToPlaylist = {
                        onAddSelectedToPlaylist(selectedIds.toList())
                        selectionMode = false
                        selectedIds = emptySet()
                    },
                    extraActionIcon = Icons.Filled.Edit,
                    extraActionLabel = strings.batchTagTitle,
                    extraActionTag = "selection_batch_tags",
                    onExtraAction = {
                        onBatchEditTags(selectedIds)
                        selectionMode = false
                        selectedIds = emptySet()
                    },
                    onDeleteSelected = {
                        onRemoveSelected(selectedIds)
                        selectionMode = false
                        selectedIds = emptySet()
                    },
                    modifier = Modifier.widthIn(max = 720.dp)
                )
            }
        }

        if (tracks.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MusicOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = strings.emptyMusicFiltered,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else if (browseMode == MusicBrowseMode.ARTISTS) {
            val groupedArtists = tracks.groupBy { it.artist.ifBlank { "Unknown Artist" } }
                .entries.sortedByDescending { it.value.size }
            items(groupedArtists, key = { "artist_${it.key}" }) { (artistName, artistTracks) ->
                Surface(
                    onClick = {
                        onSearchChange(artistName)
                        onSelectBrowseMode(MusicBrowseMode.TRACKS)
                    },
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .testTag("music_artist_card_${artistName.hashCode()}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                        ) {
                            TrackArtworkImage(track = artistTracks.first(), modifier = Modifier.fillMaxSize())
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = artistName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = strings.playlistTrackCount(artistTracks.size),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { onPlayTrack(artistTracks.first()) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = strings.menuPlayNow,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        } else if (browseMode == MusicBrowseMode.ALBUMS) {
            val groupedAlbums = tracks.groupBy { it.album.ifBlank { it.folderName.ifBlank { "Singles" } } }
                .entries.sortedByDescending { it.value.size }
            items(groupedAlbums, key = { "album_${it.key}" }) { (albumName, albumTracks) ->
                Surface(
                    onClick = {
                        onSearchChange(albumName)
                        onSelectBrowseMode(MusicBrowseMode.TRACKS)
                    },
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .testTag("music_album_card_${albumName.hashCode()}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PlaylistMosaicThumbnail(
                            tracks = albumTracks.take(4),
                            size = 54.dp
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = albumName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${albumTracks.first().artist} • ${strings.playlistTrackCount(albumTracks.size)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = { onPlayTrack(albumTracks.first()) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = strings.menuPlayNow,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        } else if (viewMode == MediaViewMode.GRID) {
            val rows = tracks.chunked(2)
            items(rows, key = { row -> row.joinToString("_") { it.id } }, contentType = { "music_grid_row" }) { pair ->
                Row(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pair.forEach { track ->
                        val isCurrent = track.id == currentTrackId
                        val isSelected = track.id in selectedIds
                        AudioTrackGridCard(
                            track = track,
                            isCurrent = isCurrent,
                            isPlaying = isCurrent && isPlaying,
                            selectionMode = selectionMode,
                            isSelected = isSelected,
                            hasNote = track.id in mediaNotes,
                            strings = strings,
                            onClick = {
                                if (selectionMode) toggleSelection(track.id) else onPlayTrack(track)
                            },
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectionMode = true
                                selectedIds = selectedIds + track.id
                            },
                            onToggleFavorite = { onToggleFavorite(track) },
                            onAddToPlaylist = { onAddTrackToPlaylist(track) },
                            onOpenNote = { onOpenNote(track) },
                            onShowDetails = {
                                onShowDetails(
                                    MediaDetailsInfo(
                                        title = track.title,
                                        extension = track.extension,
                                        mimeType = track.mimeType,
                                        sizeBytes = track.sizeBytes,
                                        extraMeta = "${track.artist} • ${MediaScanner.formatDuration(track.durationMs)}",
                                        uriString = track.uriString,
                                        mediaId = track.id,
                                        artist = track.artist,
                                        mediaType = com.example.model.RenameMediaType.AUDIO,
                                    )
                                )
                            },
                            onRename = { onRenameTrack(track) },
                            onShare = { shareMediaItem(context, track.title, track.uriString, track.mimeType) },
                            onRemove = { onRemoveTrack(track) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            items(tracks, key = { it.id }, contentType = { viewMode }) { track ->
                val isCurrent = track.id == currentTrackId
                val isSelected = track.id in selectedIds
                val handleClick = {
                    if (selectionMode) toggleSelection(track.id) else onPlayTrack(track)
                }
                val handleLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    selectionMode = true
                    selectedIds = selectedIds + track.id
                }
                val handleDetails = {
                    onShowDetails(
                        MediaDetailsInfo(
                            title = track.title,
                            extension = track.extension,
                            mimeType = track.mimeType,
                            sizeBytes = track.sizeBytes,
                            extraMeta = "${track.artist} • ${MediaScanner.formatDuration(track.durationMs)}",
                            uriString = track.uriString,
                            mediaId = track.id,
                            artist = track.artist,
                            mediaType = com.example.model.RenameMediaType.AUDIO,
                        )
                    )
                }
                val onMoveUpAction: (() -> Unit)? = if (activePlaylist != null && !selectionMode) {
                    { onMovePlaylistTrack(activePlaylist.id, track.id, -1) }
                } else null
                val onMoveDownAction: (() -> Unit)? = if (activePlaylist != null && !selectionMode) {
                    { onMovePlaylistTrack(activePlaylist.id, track.id, +1) }
                } else null

                if (viewMode == MediaViewMode.COMPACT) {
                    AudioTrackCompactCard(
                        track = track,
                        isCurrent = isCurrent,
                        isPlaying = isCurrent && isPlaying,
                        selectionMode = selectionMode,
                        isSelected = isSelected,
                        hasNote = track.id in mediaNotes,
                        strings = strings,
                        onClick = handleClick,
                        onLongClick = handleLongClick,
                        onToggleFavorite = { onToggleFavorite(track) },
                        onAddToPlaylist = { onAddTrackToPlaylist(track) },
                        onOpenNote = { onOpenNote(track) },
                        onShowDetails = handleDetails,
                        onRename = { onRenameTrack(track) },
                        onShare = { shareMediaItem(context, track.title, track.uriString, track.mimeType) },
                        onRemove = { onRemoveTrack(track) },
                        onMoveUp = onMoveUpAction,
                        onMoveDown = onMoveDownAction,
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .animateItem()
                    )
                } else {
                    AudioTrackCard(
                        track = track,
                        isCurrent = isCurrent,
                        isPlaying = isCurrent && isPlaying,
                        selectionMode = selectionMode,
                        isSelected = isSelected,
                        hasNote = track.id in mediaNotes,
                        swipeGesturesEnabled = swipeGesturesEnabled,
                        strings = strings,
                        onClick = handleClick,
                        onLongClick = handleLongClick,
                        onToggleFavorite = { onToggleFavorite(track) },
                        onAddToPlaylist = { onAddTrackToPlaylist(track) },
                        onOpenNote = { onOpenNote(track) },
                        onShowDetails = handleDetails,
                        onRename = { onRenameTrack(track) },
                        onShare = { shareMediaItem(context, track.title, track.uriString, track.mimeType) },
                        onRemove = { onRemoveTrack(track) },
                        onMoveUp = onMoveUpAction,
                        onMoveDown = onMoveDownAction,
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .animateItem()
                    )
                }
            }
        }
    }

    // Organized Material 3 Filters & Tools Sheet (Zero Clutter on Main Screen!)
    if (showFiltersSheet) {
        @OptIn(ExperimentalLayoutApi::class)
        ModalBottomSheet(
            onDismissRequest = { showFiltersSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .testTag("music_filters_sheet"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.filtersAndToolsAction,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    TextButton(
                        onClick = {
                            if (favoritesOnly) onToggleFavoritesFilter()
                            onSelectFormat("All")
                            onSelectSmartMix(SmartMusicMix.ALL_TRACKS)
                            onSelectFolderFilter(null)
                            onSelectSortOption(MediaSortOption.NEWEST)
                            onSelectBrowseMode(MusicBrowseMode.TRACKS)
                            onSelectPlaylist(null)
                        }
                    ) {
                        Text(strings.clearFiltersAction, fontWeight = FontWeight.Bold)
                    }
                }

                // Quick Toggles: Favorites + View Mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = favoritesOnly,
                        onClick = onToggleFavoritesFilter,
                        leadingIcon = {
                            Icon(
                                imageVector = if (favoritesOnly) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = { Text(strings.favoritesOnly, fontWeight = FontWeight.Bold) },
                        shape = CircleShape,
                        modifier = Modifier.testTag("music_favorites_filter")
                    )
                    MediaViewModeButton(
                        currentMode = viewMode,
                        strings = strings,
                        testTag = "music_view_mode_button",
                        onSelectMode = onSelectViewMode
                    )
                }

                // Browse Mode (Tracks / Artists / Albums)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple(MusicBrowseMode.TRACKS, Icons.Rounded.MusicNote, strings.browseTracksTab),
                        Triple(MusicBrowseMode.ARTISTS, Icons.Rounded.Person, strings.browseArtistsTab),
                        Triple(MusicBrowseMode.ALBUMS, Icons.Rounded.Album, strings.browseAlbumsTab),
                    ).forEach { (mode, icon, label) ->
                        val selected = browseMode == mode
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectBrowseMode(mode) },
                            leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("music_browse_${mode.name.lowercase()}")
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                // Sort Options
                Text(
                    text = strings.sortTitle,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MediaSortOption.entries.forEach { option ->
                        FilterChip(
                            selected = sortOption == option,
                            onClick = { onSelectSortOption(option) },
                            leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Sort, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(strings.sortOptionName(option)) },
                            shape = CircleShape
                        )
                    }
                }

                // Smart Mixes
                SmartMusicMixesRow(
                    selectedMix = selectedSmartMix,
                    strings = strings,
                    onSelectMix = onSelectSmartMix,
                    modifier = Modifier.fillMaxWidth()
                )

                // Audio Formats
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AUDIO_FORMAT_FILTERS.forEach { format ->
                        val selected = selectedFormat.equals(format, ignoreCase = true)
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectFormat(format) },
                            label = { Text(if (format == "All") strings.filterAll else format) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("format_chip_${format.lowercase()}")
                        )
                    }
                }

                // Folders (when 2+ folders exist)
                if (availableFolders.size >= 2) {
                    FolderFilterChipsRow(
                        folders = availableFolders,
                        selectedFolder = selectedFolder,
                        strings = strings,
                        onSelectFolder = onSelectFolderFilter,
                        folderCustomizations = folderCustomizations,
                        onLongPressFolder = onLongPressFolder,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                // Studio & Spiritual Quick Tools
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple(Icons.Rounded.MenuBook, strings.quranHubTitle, onOpenQuranHub),
                        Triple(Icons.Rounded.AccessAlarm, strings.morningAlarmTitle, onOpenMorningAlarm),
                        Triple(Icons.Rounded.BarChart, strings.listeningStatsTitle, onOpenListeningStats),
                        Triple(Icons.Rounded.AutoAwesome, strings.tasbihTitle, onOpenTasbih),
                    ).forEachIndexed { idx, (icon, label, action) ->
                        Surface(
                            onClick = {
                                showFiltersSheet = false
                                action()
                            },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.testTag("music_quick_studio_$idx")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                                Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                }

                // Recently Played Quick-Resume inside Tools if available
                if (recentlyPlayed.isNotEmpty()) {
                    Text(
                        text = strings.recentlyPlayedTitle,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(recentlyPlayed, key = { "recent_${it.id}" }) { recent ->
                            Surface(
                                onClick = {
                                    showFiltersSheet = false
                                    onPlayTrack(recent)
                                },
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier.width(168.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TrackArtworkImage(
                                        track = recent,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(recent.title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(recent.artist, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (showPlaylistsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPlaylistsSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.playlistsTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        onClick = {
                            showPlaylistsSheet = false
                            onImportPlaylistM3u()
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.testTag("playlists_import_m3u_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FileDownload,
                                contentDescription = strings.importM3uAction,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = strings.importM3uAction,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                // Create New Playlist Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newPlaylistInput,
                        onValueChange = { newPlaylistInput = it },
                        placeholder = { Text(strings.playlistNameHint) },
                        singleLine = true,
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("new_playlist_input")
                    )
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .clickable(enabled = newPlaylistInput.isNotBlank()) {
                                onCreatePlaylist(newPlaylistInput)
                                newPlaylistInput = ""
                            }
                            .testTag("create_playlist_button")
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

                // "All Tracks" reset row
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedPlaylistId == null) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            onSelectPlaylist(null)
                            showPlaylistsSheet = false
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.clearPlaylistFilter,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        if (selectedPlaylistId == null) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    }
                }

                if (playlists.isEmpty()) {
                    Text(
                        text = strings.noPlaylistsYet,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    playlists.forEach { pl ->
                        val selected = selectedPlaylistId == pl.id
                        val plTracks = pl.trackIds.mapNotNull { id -> allAudioTracks.firstOrNull { it.id == id } }
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    onSelectPlaylist(pl.id)
                                    showPlaylistsSheet = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PlaylistMosaicThumbnail(tracks = plTracks.take(4), size = 46.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pl.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = strings.playlistTrackCount(pl.trackIds.size),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { onExportPlaylistM3u(pl.id) },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .testTag("playlist_export_m3u_${pl.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.FileUpload,
                                        contentDescription = strings.exportM3uAction,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = { onDeletePlaylist(pl.id) },
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteOutline,
                                        contentDescription = strings.menuRemove,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AudioTrackCard(
    track: AudioTrack,
    isCurrent: Boolean,
    isPlaying: Boolean,
    selectionMode: Boolean,
    isSelected: Boolean,
    hasNote: Boolean = false,
    strings: AppStrings,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onOpenNote: () -> Unit = {},
    onShowDetails: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onRemove: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    swipeGesturesEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val cardStyle = com.example.ui.theme.LocalMediaCardStyle.current
    val showFormatBadges = com.example.ui.theme.LocalShowFormatBadges.current
    val glassCfg = com.example.ui.components.LocalLiquidGlassConfig.current
    val cardCorner = com.example.ui.components.scaledCornerDp(24.dp, glassCfg.shapeRoundness)
    val cardShape = RoundedCornerShape(cardCorner)
    val haptics = LocalHapticFeedback.current
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

    val cardColor = when {
        isSelected -> cs.primaryContainer.copy(alpha = 0.65f)
        isCurrent -> cs.primaryContainer.copy(alpha = 0.44f)
        cardStyle == com.example.model.MediaCardStyle.GLASS_CARD -> cs.surfaceContainer.copy(alpha = 0.78f)
        cardStyle == com.example.model.MediaCardStyle.ELEVATED_M3 -> cs.surfaceContainerHigh
        cardStyle == com.example.model.MediaCardStyle.MINIMAL_OUTLINE -> cs.surface.copy(alpha = 0.92f)
        cardStyle == com.example.model.MediaCardStyle.VIBRANT_TONAL -> cs.secondaryContainer.copy(alpha = 0.36f)
        else -> cs.surfaceContainer
    }
    val cardBorder = when {
        isSelected -> BorderStroke(2.dp, cs.primary)
        isCurrent -> BorderStroke(1.5.dp, cs.primary.copy(alpha = 0.7f))
        cardStyle == com.example.model.MediaCardStyle.GLASS_CARD -> BorderStroke(1.1.dp, cs.primary.copy(alpha = 0.22f))
        cardStyle == com.example.model.MediaCardStyle.MINIMAL_OUTLINE -> BorderStroke(1.dp, cs.outline.copy(alpha = 0.38f))
        cardStyle == com.example.model.MediaCardStyle.VIBRANT_TONAL -> BorderStroke(1.2.dp, cs.secondary.copy(alpha = 0.35f))
        else -> BorderStroke(1.dp, cs.outlineVariant.copy(alpha = 0.22f))
    }
    val cardElevation = when {
        isCurrent || isSelected -> 6.dp
        cardStyle == com.example.model.MediaCardStyle.ELEVATED_M3 -> 4.dp
        else -> 1.dp
    }

    val cardContent: @Composable () -> Unit = {
        Surface(
            shape = cardShape,
            color = cardColor,
            tonalElevation = cardElevation,
            border = cardBorder,
            modifier = Modifier
                .fillMaxWidth()
                .expressivePressScale(interaction, pressedScale = 0.97f)
                .clip(cardShape)
                .combinedClickable(
                    interactionSource = interaction,
                    indication = androidx.compose.material3.ripple(),
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .testTag("audio_card_${track.id}")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectionMode) {
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = strings.selectModeAction,
                        tint = if (isSelected) cs.primary else cs.onSurfaceVariant,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(com.example.ui.components.scaledCornerDp(16.dp, glassCfg.shapeRoundness)))
                ) {
                    TrackArtworkImage(track = track, modifier = Modifier.fillMaxSize())
                    if (isCurrent) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(cs.scrim.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedEqualizerBars(isPlaying = isPlaying)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) cs.primary else cs.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (track.isFavorite) {
                            Icon(
                                imageVector = Icons.Rounded.Favorite,
                                contentDescription = strings.favoritesOnly,
                                tint = cs.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        if (hasNote) {
                            Icon(
                                imageVector = Icons.Filled.EditNote,
                                contentDescription = strings.mediaNoteTitle,
                                tint = cs.tertiary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (showFormatBadges) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = cs.secondaryContainer
                            ) {
                                Text(
                                    text = track.extension,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = cs.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = MediaScanner.formatDuration(track.durationMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.onSurfaceVariant
                        )
                        if (showFormatBadges) {
                            Text(
                                text = "• ${MediaScanner.formatFileSize(track.sizeBytes)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = cs.onSurfaceVariant
                            )
                        }
                    }
                }

                if (onMoveUp != null && onMoveDown != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = onMoveUp, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Move up", modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onMoveDown, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Move down", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (!selectionMode) {
                    MediaOverflowMenuButton(
                        isFavorite = track.isFavorite,
                        playLabel = strings.menuPlayNow,
                        strings = strings,
                        testTagPrefix = "audio_${track.id}",
                        onPlayNow = onClick,
                        onToggleFavorite = onToggleFavorite,
                        onShowDetails = onShowDetails,
                        onRename = onRename,
                        onShare = onShare,
                        onRemove = onRemove,
                        onAddToPlaylist = onAddToPlaylist,
                        onOpenNote = onOpenNote
                    )
                }
            }
        }
    }

    if (swipeGesturesEnabled && !selectionMode) {
        val dismissState = androidx.compose.material3.rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                when (value) {
                    androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd -> {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggleFavorite()
                        false
                    }
                    androidx.compose.material3.SwipeToDismissBoxValue.EndToStart -> {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onAddToPlaylist()
                        false
                    }
                    else -> false
                }
            }
        )
        androidx.compose.material3.SwipeToDismissBox(
            state = dismissState,
            modifier = modifier.fillMaxWidth(),
            backgroundContent = {
                val dir = dismissState.dismissDirection
                val isFavSwipe = dir == androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
                val bg = if (isFavSwipe) cs.primaryContainer else cs.tertiaryContainer
                val fg = if (isFavSwipe) cs.onPrimaryContainer else cs.onTertiaryContainer
                Surface(
                    shape = cardShape,
                    color = bg,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isFavSwipe) Arrangement.Start else Arrangement.End
                    ) {
                        Icon(
                            imageVector = if (isFavSwipe) {
                                if (track.isFavorite) Icons.Rounded.FavoriteBorder else Icons.Rounded.Favorite
                            } else {
                                Icons.AutoMirrored.Rounded.QueueMusic
                            },
                            contentDescription = null,
                            tint = fg,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isFavSwipe) strings.swipeFavoriteHint else strings.swipePlaylistHint,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = fg
                        )
                    }
                }
            },
            content = { cardContent() }
        )
    } else {
        Box(modifier = modifier.fillMaxWidth()) {
            cardContent()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AudioTrackCompactCard(
    track: AudioTrack,
    isCurrent: Boolean,
    isPlaying: Boolean,
    selectionMode: Boolean,
    isSelected: Boolean,
    hasNote: Boolean = false,
    strings: AppStrings,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onOpenNote: () -> Unit = {},
    onShowDetails: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onRemove: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
            isCurrent -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
            else -> MaterialTheme.colorScheme.surfaceContainer
        },
        border = when {
            isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            isCurrent -> BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .testTag("audio_compact_${track.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 6.dp, bottom = 6.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectionMode) {
                Icon(
                    imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = strings.selectModeAction,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(22.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                TrackArtworkImage(track = track, modifier = Modifier.fillMaxSize())
                if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedEqualizerBars(isPlaying = isPlaying)
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${track.artist} • ${MediaScanner.formatDuration(track.durationMs)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (hasNote) {
                Icon(
                    imageVector = Icons.Filled.EditNote,
                    contentDescription = strings.mediaNoteTitle,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
            if (onMoveUp != null && onMoveDown != null) {
                IconButton(onClick = onMoveUp, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Move up", modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onMoveDown, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Move down", modifier = Modifier.size(18.dp))
                }
            }

            if (!selectionMode) {
                MediaOverflowMenuButton(
                    isFavorite = track.isFavorite,
                    playLabel = strings.menuPlayNow,
                    strings = strings,
                    testTagPrefix = "audio_${track.id}",
                    onPlayNow = onClick,
                    onToggleFavorite = onToggleFavorite,
                    onShowDetails = onShowDetails,
                    onRename = onRename,
                    onShare = onShare,
                    onRemove = onRemove,
                    onAddToPlaylist = onAddToPlaylist,
                    onOpenNote = onOpenNote
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AudioTrackGridCard(
    track: AudioTrack,
    isCurrent: Boolean,
    isPlaying: Boolean,
    selectionMode: Boolean,
    isSelected: Boolean,
    hasNote: Boolean = false,
    strings: AppStrings,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onOpenNote: () -> Unit = {},
    onShowDetails: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
            isCurrent -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
            else -> MaterialTheme.colorScheme.surfaceContainer
        },
        border = when {
            isSelected -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            isCurrent -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.65f))
            else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
        },
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .testTag("audio_grid_${track.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                TrackArtworkImage(track = track, modifier = Modifier.fillMaxSize())
                if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.42f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedEqualizerBars(isPlaying = isPlaying)
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "${track.extension} • ${MediaScanner.formatDuration(track.durationMs)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                if (selectionMode) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.55f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.RadioButtonUnchecked,
                                contentDescription = strings.selectModeAction,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 6.dp, bottom = 6.dp, end = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
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
                if (!selectionMode) {
                    MediaOverflowMenuButton(
                        isFavorite = track.isFavorite,
                        playLabel = strings.menuPlayNow,
                        strings = strings,
                        testTagPrefix = "audio_${track.id}",
                        onPlayNow = onClick,
                        onToggleFavorite = onToggleFavorite,
                        onShowDetails = onShowDetails,
                        onRename = onRename,
                        onShare = onShare,
                        onRemove = onRemove,
                        onAddToPlaylist = onAddToPlaylist,
                        onOpenNote = onOpenNote
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaylistMosaicThumbnail(
    tracks: List<AudioTrack>,
    size: Dp,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.size(size)
    ) {
        when {
            tracks.isEmpty() -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            tracks.size < 4 -> {
                TrackArtworkImage(track = tracks.first(), modifier = Modifier.fillMaxSize())
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        TrackArtworkImage(track = tracks[0], modifier = Modifier.weight(1f).fillMaxSize())
                        TrackArtworkImage(track = tracks[1], modifier = Modifier.weight(1f).fillMaxSize())
                    }
                    Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        TrackArtworkImage(track = tracks[2], modifier = Modifier.weight(1f).fillMaxSize())
                        TrackArtworkImage(track = tracks[3], modifier = Modifier.weight(1f).fillMaxSize())
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedEqualizerBars(isPlaying: Boolean) {
    val transition = rememberInfiniteTransition(label = "eqBars")
    val bar1 by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(420), RepeatMode.Reverse),
        label = "b1"
    )
    val bar2 by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.30f,
        animationSpec = infiniteRepeatable(tween(340), RepeatMode.Reverse),
        label = "b2"
    )
    val bar3 by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "b3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(20.dp)
    ) {
        listOf(
            if (isPlaying) bar1 else 0.4f,
            if (isPlaying) bar2 else 0.7f,
            if (isPlaying) bar3 else 0.5f,
        ).forEach { heightFraction ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp * heightFraction)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}
