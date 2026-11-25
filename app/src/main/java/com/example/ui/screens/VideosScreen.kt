package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.example.data.MediaScanner
import com.example.localization.AppStrings
import com.example.model.FolderCustomization
import com.example.model.MediaDetailsInfo
import com.example.model.MediaSortOption
import com.example.model.MediaViewMode
import com.example.model.VideoItem
import com.example.ui.components.FolderFilterChipsRow
import com.example.ui.components.MediaOverflowMenuButton
import com.example.ui.components.MediaSelectionActionBar
import com.example.ui.components.MediaViewModeButton
import com.example.ui.components.OnboardingEmptyState
import com.example.ui.components.ShareableMediaPayload
import com.example.ui.components.shareMediaItem
import com.example.ui.components.shareMultipleMediaItems

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideosScreen(
    videos: List<VideoItem>,
    hasAnyMedia: Boolean,
    searchQuery: String,
    favoritesOnly: Boolean,
    sortOption: MediaSortOption,
    viewMode: MediaViewMode = MediaViewMode.GRID,
    availableFolders: List<String> = emptyList(),
    selectedFolder: String? = null,
    strings: AppStrings,
    bottomContentPadding: Dp,
    onSearchChange: (String) -> Unit,
    onToggleFavoritesFilter: () -> Unit,
    onSelectSortOption: (MediaSortOption) -> Unit,
    onSelectViewMode: (MediaViewMode) -> Unit = {},
    onSelectFolderFilter: (String?) -> Unit = {},
    onOpenVideo: (VideoItem) -> Unit,
    onPlayVideoAsAudio: (VideoItem) -> Unit = {},
    onToggleFavorite: (VideoItem) -> Unit,
    onFavoriteSelected: (Set<String>) -> Unit = {},
    onRenameVideo: (VideoItem) -> Unit,
    onRemoveVideo: (VideoItem) -> Unit,
    onRemoveSelected: (Set<String>) -> Unit = {},
    onShowDetails: (MediaDetailsInfo) -> Unit,
    onSelectFolder: () -> Unit,
    onImportFiles: () -> Unit,
    onLoadDemoMedia: () -> Unit,
    folderCustomizations: Map<String, FolderCustomization> = emptyMap(),
    onLongPressFolder: ((String) -> Unit)? = null,
    onExtractAudio: (VideoItem) -> Unit = {},
    onTrimVideo: (VideoItem) -> Unit = {},
    onOpenNote: (VideoItem) -> Unit = {},
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    var showFiltersSheet by remember { mutableStateOf(false) }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(videos) {
        val validIds = videos.map { it.id }.toSet()
        selectedIds = selectedIds.intersect(validIds)
        if (videos.isEmpty()) selectionMode = false
    }

    BackHandler(enabled = selectionMode) {
        selectionMode = false
        selectedIds = emptySet()
    }

    fun toggleSelection(id: String) {
        selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id
    }

    if (!hasAnyMedia) {
        androidx.compose.foundation.lazy.LazyColumn(
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

    val columns = when (viewMode) {
        MediaViewMode.LIST -> GridCells.Fixed(1)
        MediaViewMode.COMPACT -> GridCells.Fixed(2)
        MediaViewMode.GRID -> GridCells.Adaptive(minSize = 280.dp)
    }

    val gridSpacing = when (com.example.ui.theme.LocalUiDensity.current) {
        com.example.model.UiDensity.COMFORTABLE -> 16.dp
        com.example.model.UiDensity.BALANCED -> 12.dp
        com.example.model.UiDensity.COMPACT -> 8.dp
    }

    LazyVerticalGrid(
        columns = columns,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = bottomContentPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
        verticalArrangement = Arrangement.spacedBy(gridSpacing)
    ) {
        // Calm Single-Row Search + Filters & Tools Sheet Trigger
        item(key = "video_calm_top_bar", span = { GridItemSpan(maxLineSpan) }, contentType = "top_bar") {
            val activeFiltersCount = remember(favoritesOnly, selectedFolder, sortOption) {
                var count = 0
                if (favoritesOnly) count++
                if (selectedFolder != null) count++
                if (sortOption != MediaSortOption.NEWEST) count++
                count
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            text = strings.searchVideosHint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
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
                        .testTag("video_search_input")
                )

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
                        if (activeFiltersCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("video_filters_sheet_button")
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
            }
        }

        // Slim 1-Line Summary Bar (Count + Active Filter Reset + Select Icon)
        item(key = "video_summary_bar", span = { GridItemSpan(maxLineSpan) }, contentType = "summary") {
            val hasActiveFilters = favoritesOnly || selectedFolder != null || sortOption != MediaSortOption.NEWEST
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.videosCount(videos.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (hasActiveFilters) {
                        Surface(
                            onClick = {
                                if (favoritesOnly) onToggleFavoritesFilter()
                                onSelectFolderFilter(null)
                                onSelectSortOption(MediaSortOption.NEWEST)
                            },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer
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
                        .testTag("video_select_mode_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Checklist,
                        contentDescription = strings.selectModeAction,
                        tint = if (selectionMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (selectionMode) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                MediaSelectionActionBar(
                    visible = true,
                    selectedCount = selectedIds.size,
                    totalCount = videos.size,
                    strings = strings,
                    onCloseSelection = {
                        selectionMode = false
                        selectedIds = emptySet()
                    },
                    onSelectAll = { selectedIds = videos.map { it.id }.toSet() },
                    onDeselectAll = { selectedIds = emptySet() },
                    onInvertSelection = {
                        selectedIds = videos.map { it.id }.filterNot { it in selectedIds }.toSet()
                    },
                    onShareSelected = {
                        val chosen = videos.filter { it.id in selectedIds }.map {
                            ShareableMediaPayload(it.title, it.uriString, it.mimeType)
                        }
                        shareMultipleMediaItems(context, chosen, strings.menuShare)
                    },
                    onFavoriteSelected = {
                        onFavoriteSelected(selectedIds)
                        selectionMode = false
                        selectedIds = emptySet()
                    },
                    onDeleteSelected = {
                        onRemoveSelected(selectedIds)
                        selectionMode = false
                        selectedIds = emptySet()
                    }
                )
            }
        }

        if (videos.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VideocamOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = strings.emptyVideos,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(videos, key = { it.id }, contentType = { viewMode }) { video ->
                val isSelected = video.id in selectedIds
                val handleClick = {
                    if (selectionMode) toggleSelection(video.id) else onOpenVideo(video)
                }
                val handleLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    selectionMode = true
                    selectedIds = selectedIds + video.id
                }
                val handleDetails = {
                    onShowDetails(
                        MediaDetailsInfo(
                            title = video.title,
                            extension = video.extension,
                            mimeType = video.mimeType,
                            sizeBytes = video.sizeBytes,
                            extraMeta = "${video.resolution} • ${MediaScanner.formatDuration(video.durationMs)}",
                            uriString = video.uriString,
                            mediaId = video.id,
                            mediaType = com.example.model.RenameMediaType.VIDEO,
                        )
                    )
                }

                when (viewMode) {
                    MediaViewMode.LIST -> VideoListRowCard(
                        video = video,
                        selectionMode = selectionMode,
                        isSelected = isSelected,
                        strings = strings,
                        onClick = handleClick,
                        onLongClick = handleLongClick,
                        onPlayAsAudio = { onPlayVideoAsAudio(video) },
                        onExtractAudio = { onExtractAudio(video) },
                        onTrimVideo = { onTrimVideo(video) },
                        onOpenNote = { onOpenNote(video) },
                        onToggleFavorite = { onToggleFavorite(video) },
                        onShowDetails = handleDetails,
                        onRename = { onRenameVideo(video) },
                        onShare = { shareMediaItem(context, video.title, video.uriString, video.mimeType) },
                        onRemove = { onRemoveVideo(video) },
                        modifier = Modifier.animateItem()
                    )
                    MediaViewMode.COMPACT -> VideoCompactGridCard(
                        video = video,
                        selectionMode = selectionMode,
                        isSelected = isSelected,
                        strings = strings,
                        onClick = handleClick,
                        onLongClick = handleLongClick,
                        modifier = Modifier.animateItem()
                    )
                    MediaViewMode.GRID -> VideoCard(
                        video = video,
                        selectionMode = selectionMode,
                        isSelected = isSelected,
                        strings = strings,
                        onClick = handleClick,
                        onLongClick = handleLongClick,
                        onPlayAsAudio = { onPlayVideoAsAudio(video) },
                        onExtractAudio = { onExtractAudio(video) },
                        onTrimVideo = { onTrimVideo(video) },
                        onOpenNote = { onOpenNote(video) },
                        onToggleFavorite = { onToggleFavorite(video) },
                        onShowDetails = handleDetails,
                        onRename = { onRenameVideo(video) },
                        onShare = { shareMediaItem(context, video.title, video.uriString, video.mimeType) },
                        onRemove = { onRemoveVideo(video) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }

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
                    .testTag("video_filters_sheet"),
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
                            onSelectFolderFilter(null)
                            onSelectSortOption(MediaSortOption.NEWEST)
                        }
                    ) {
                        Text(strings.clearFiltersAction, fontWeight = FontWeight.Bold)
                    }
                }

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
                        modifier = Modifier.testTag("video_favorites_filter")
                    )
                    MediaViewModeButton(
                        currentMode = viewMode,
                        strings = strings,
                        testTag = "video_view_mode_button",
                        onSelectMode = onSelectViewMode
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

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

                if (availableFolders.size >= 2) {
                    FolderFilterChipsRow(
                        folders = availableFolders,
                        selectedFolder = selectedFolder,
                        strings = strings,
                        onSelectFolder = onSelectFolderFilter,
                        folderCustomizations = folderCustomizations,
                        onLongPressFolder = onLongPressFolder
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VideoCard(
    video: VideoItem,
    selectionMode: Boolean,
    isSelected: Boolean,
    strings: AppStrings,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPlayAsAudio: () -> Unit,
    onExtractAudio: () -> Unit,
    onTrimVideo: () -> Unit,
    onOpenNote: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShowDetails: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f) else MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = if (isSelected) 6.dp else 2.dp,
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .testTag("video_card_${video.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                VideoThumbnailImage(video = video, modifier = Modifier.fillMaxSize())

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                            )
                        )
                )

                if (!selectionMode) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.92f),
                        shadowElevation = 8.dp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = strings.menuPlayNow,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                if (selectionMode) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(30.dp)
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

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = video.extension,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = MediaScanner.formatDuration(video.durationMs),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        else MaterialTheme.colorScheme.surfaceContainer
                    )
                    .padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (video.isFavorite) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = strings.favoritesOnly,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${video.resolution} • ${MediaScanner.formatFileSize(video.sizeBytes)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                if (!selectionMode) {
                    MediaOverflowMenuButton(
                        isFavorite = video.isFavorite,
                        playLabel = strings.menuPlayNow,
                        strings = strings,
                        testTagPrefix = "video_${video.id}",
                        onPlayNow = onClick,
                        onPlayAsAudio = onPlayAsAudio,
                        onExtractAudio = onExtractAudio,
                        onTrimVideo = onTrimVideo,
                        onOpenNote = onOpenNote,
                        onToggleFavorite = onToggleFavorite,
                        onShowDetails = onShowDetails,
                        onRename = onRename,
                        onShare = onShare,
                        onRemove = onRemove
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VideoListRowCard(
    video: VideoItem,
    selectionMode: Boolean,
    isSelected: Boolean,
    strings: AppStrings,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPlayAsAudio: () -> Unit,
    onExtractAudio: () -> Unit,
    onTrimVideo: () -> Unit,
    onOpenNote: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShowDetails: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f) else MaterialTheme.colorScheme.surfaceContainer,
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .testTag("video_list_${video.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
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
                    .width(116.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                VideoThumbnailImage(video = video, modifier = Modifier.fillMaxSize())
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.72f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = MediaScanner.formatDuration(video.durationMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${video.extension} • ${video.resolution} • ${MediaScanner.formatFileSize(video.sizeBytes)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!selectionMode) {
                MediaOverflowMenuButton(
                    isFavorite = video.isFavorite,
                    playLabel = strings.menuPlayNow,
                    strings = strings,
                    testTagPrefix = "video_${video.id}",
                    onPlayNow = onClick,
                    onPlayAsAudio = onPlayAsAudio,
                    onExtractAudio = onExtractAudio,
                    onTrimVideo = onTrimVideo,
                    onOpenNote = onOpenNote,
                    onToggleFavorite = onToggleFavorite,
                    onShowDetails = onShowDetails,
                    onRename = onRename,
                    onShare = onShare,
                    onRemove = onRemove
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VideoCompactGridCard(
    video: VideoItem,
    selectionMode: Boolean,
    isSelected: Boolean,
    strings: AppStrings,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceContainer,
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .testTag("video_compact_${video.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                VideoThumbnailImage(video = video, modifier = Modifier.fillMaxSize())
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = MediaScanner.formatDuration(video.durationMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                if (selectionMode) {
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.RadioButtonUnchecked,
                                contentDescription = strings.selectModeAction,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = video.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun VideoThumbnailImage(
    video: VideoItem,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    if (video.thumbnailResId != null) {
        Image(
            painter = painterResource(id = video.thumbnailResId),
            contentDescription = video.title,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        val request = remember(video.uriString) {
            ImageRequest.Builder(context)
                .data(video.uriString)
                .size(480, 320)
                .precision(Precision.INEXACT)
                .crossfade(160)
                .allowHardware(true)
                .build()
        }
        AsyncImage(
            model = request,
            contentDescription = video.title,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}
