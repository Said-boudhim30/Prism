package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
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
import coil.size.Size
import com.example.data.MediaScanner
import com.example.data.MediaToolsEngine
import com.example.data.PhotoExifInfo
import com.example.data.PhotoExifStudioHelper
import com.example.localization.AppStrings
import com.example.model.FolderCustomization
import com.example.model.MediaDetailsInfo
import com.example.model.MediaSortOption
import com.example.model.MediaViewMode
import com.example.model.PhotoItem
import com.example.ui.components.FolderFilterChipsRow
import com.example.ui.components.MediaOverflowMenuButton
import com.example.ui.components.MediaSelectionActionBar
import com.example.ui.components.MediaViewModeButton
import com.example.ui.components.OnboardingEmptyState
import com.example.ui.components.PhotoCompareSliderDialog
import com.example.ui.components.PhotoStudioEditorDialog
import com.example.ui.components.ShareableMediaPayload
import com.example.ui.components.shareMediaItem
import com.example.ui.components.shareMultipleMediaItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class PhotoStudioFilter { ORIGINAL, VIVID, WARM, NOIR, COOL }

private fun colorMatrixValuesFor(filter: PhotoStudioFilter): FloatArray? = when (filter) {
    PhotoStudioFilter.ORIGINAL -> null
    PhotoStudioFilter.VIVID -> ColorMatrix().apply { setToSaturation(1.45f) }.values
    PhotoStudioFilter.WARM -> floatArrayOf(
        1.12f, 0f, 0f, 0f, 10f,
        0f, 1.03f, 0f, 0f, 4f,
        0f, 0f, 0.88f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
    PhotoStudioFilter.NOIR -> ColorMatrix().apply { setToSaturation(0f) }.values
    PhotoStudioFilter.COOL -> floatArrayOf(
        0.90f, 0f, 0f, 0f, 0f,
        0f, 1.02f, 0f, 0f, 4f,
        0f, 0f, 1.16f, 0f, 12f,
        0f, 0f, 0f, 1f, 0f
    )
}

private fun colorFilterFor(filter: PhotoStudioFilter): ColorFilter? =
    colorMatrixValuesFor(filter)?.let { ColorFilter.colorMatrix(ColorMatrix(it)) }

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    photos: List<PhotoItem>,
    hasAnyMedia: Boolean,
    searchQuery: String,
    favoritesOnly: Boolean,
    sortOption: MediaSortOption,
    viewMode: MediaViewMode = MediaViewMode.GRID,
    availableFolders: List<String> = emptyList(),
    selectedFolder: String? = null,
    strings: AppStrings,
    bottomContentPadding: Dp,
    gridColumns: Int = 0,
    showTitles: Boolean = true,
    onSearchChange: (String) -> Unit,
    onToggleFavoritesFilter: () -> Unit,
    onSelectSortOption: (MediaSortOption) -> Unit,
    onSelectViewMode: (MediaViewMode) -> Unit = {},
    onSelectFolderFilter: (String?) -> Unit = {},
    onOpenPhoto: (Int) -> Unit,
    onToggleFavorite: (PhotoItem) -> Unit,
    onFavoriteSelected: (Set<String>) -> Unit = {},
    onRenamePhoto: (PhotoItem) -> Unit,
    onRemovePhoto: (PhotoItem) -> Unit,
    onRemoveSelected: (Set<String>) -> Unit = {},
    onShowDetails: (MediaDetailsInfo) -> Unit,
    onSelectFolder: () -> Unit,
    onImportFiles: () -> Unit,
    onLoadDemoMedia: () -> Unit,
    stripExifOnShare: Boolean = true,
    folderCustomizations: Map<String, FolderCustomization> = emptyMap(),
    onLongPressFolder: ((String) -> Unit)? = null,
    onOpenPrivateVault: () -> Unit = {},
    onOpenCollageStudio: (Set<String>) -> Unit = {},
    onOpenNote: (PhotoItem) -> Unit = {},
    onShowBanner: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    var showFiltersSheet by remember { mutableStateOf(false) }

    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(photos) {
        val validIds = photos.map { it.id }.toSet()
        selectedIds = selectedIds.intersect(validIds)
        if (photos.isEmpty()) selectionMode = false
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

    val gridCells = when (viewMode) {
        MediaViewMode.LIST -> StaggeredGridCells.Fixed(1)
        MediaViewMode.COMPACT -> StaggeredGridCells.Fixed(3)
        MediaViewMode.GRID -> when (gridColumns) {
            2 -> StaggeredGridCells.Fixed(2)
            3 -> StaggeredGridCells.Fixed(3)
            else -> StaggeredGridCells.Adaptive(minSize = 160.dp)
        }
    }

    LazyVerticalStaggeredGrid(
        columns = gridCells,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = bottomContentPadding
        ),
        horizontalArrangement = Arrangement.spacedBy(if (viewMode == MediaViewMode.COMPACT) 8.dp else 12.dp),
        verticalItemSpacing = if (viewMode == MediaViewMode.COMPACT) 8.dp else 12.dp
    ) {
        // Calm Single-Row Search + Filters & Tools Sheet Trigger
        item(key = "photo_calm_top_bar", span = StaggeredGridItemSpan.FullLine, contentType = "top_bar") {
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
                            text = strings.searchPhotosHint,
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
                        .testTag("photo_search_input")
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
                        .testTag("photo_filters_sheet_button")
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
        item(key = "photo_summary_bar", span = StaggeredGridItemSpan.FullLine, contentType = "summary") {
            val hasActiveFilters = favoritesOnly || selectedFolder != null || sortOption != MediaSortOption.NEWEST
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.photosCount(photos.size),
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
                        .testTag("photo_select_mode_button")
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
            item(span = StaggeredGridItemSpan.FullLine) {
                MediaSelectionActionBar(
                    visible = true,
                    selectedCount = selectedIds.size,
                    totalCount = photos.size,
                    strings = strings,
                    onCloseSelection = {
                        selectionMode = false
                        selectedIds = emptySet()
                    },
                    onSelectAll = { selectedIds = photos.map { it.id }.toSet() },
                    onDeselectAll = { selectedIds = emptySet() },
                    onInvertSelection = {
                        selectedIds = photos.map { it.id }.filterNot { it in selectedIds }.toSet()
                    },
                    onShareSelected = {
                        val chosen = photos.filter { it.id in selectedIds }.map {
                            ShareableMediaPayload(it.title, it.uriString, it.mimeType)
                        }
                        shareMultipleMediaItems(context, chosen, strings.menuShare)
                    },
                    onFavoriteSelected = {
                        onFavoriteSelected(selectedIds)
                        selectionMode = false
                        selectedIds = emptySet()
                    },
                    extraActionIcon = Icons.Filled.GridView,
                    extraActionLabel = strings.collageStudioTitle,
                    extraActionTag = "selection_collage",
                    onExtraAction = {
                        onOpenCollageStudio(selectedIds)
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

        if (photos.isEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
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
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = strings.emptyPhotos,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            itemsIndexed(photos, key = { _, item -> item.id }, contentType = { _, _ -> viewMode }) { index, photo ->
                val isSelected = photo.id in selectedIds
                val handleClick = {
                    if (selectionMode) toggleSelection(photo.id) else onOpenPhoto(index)
                }
                val handleLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    selectionMode = true
                    selectedIds = selectedIds + photo.id
                }
                val handleDetails = {
                    onShowDetails(
                        MediaDetailsInfo(
                            title = photo.title,
                            extension = photo.extension,
                            mimeType = photo.mimeType,
                            sizeBytes = photo.sizeBytes,
                            extraMeta = if (photo.width > 0) "${photo.width}x${photo.height}" else "Image",
                            uriString = photo.uriString,
                            mediaId = photo.id,
                            mediaType = com.example.model.RenameMediaType.PHOTO,
                        )
                    )
                }

                val handleSharePhoto: () -> Unit = {
                    if (stripExifOnShare) {
                        coroutineScope.launch {
                            val cleanUri = PhotoExifStudioHelper.createCleanShareUri(context, photo)
                            if (cleanUri != null) {
                                shareMediaItem(context, photo.title, cleanUri.toString(), "image/jpeg")
                            } else {
                                shareMediaItem(context, photo.title, photo.uriString, photo.mimeType)
                            }
                        }
                    } else {
                        shareMediaItem(context, photo.title, photo.uriString, photo.mimeType)
                    }
                }

                when (viewMode) {
                    MediaViewMode.LIST -> PhotoListRowCard(
                        photo = photo,
                        selectionMode = selectionMode,
                        isSelected = isSelected,
                        strings = strings,
                        onClick = handleClick,
                        onLongClick = handleLongClick,
                        onToggleFavorite = { onToggleFavorite(photo) },
                        onShowDetails = handleDetails,
                        onRename = { onRenamePhoto(photo) },
                        onShare = handleSharePhoto,
                        onRemove = { onRemovePhoto(photo) },
                        onOpenNote = { onOpenNote(photo) }
                    )
                    MediaViewMode.COMPACT -> PhotoCompactSquareCard(
                        photo = photo,
                        selectionMode = selectionMode,
                        isSelected = isSelected,
                        strings = strings,
                        onClick = handleClick,
                        onLongClick = handleLongClick
                    )
                    MediaViewMode.GRID -> {
                        val ratio = if (photo.width > 0 && photo.height > 0) {
                            (photo.width.toFloat() / photo.height.toFloat()).coerceIn(0.65f, 1.75f)
                        } else {
                            if (index % 2 == 0) 0.85f else 1.25f
                        }

                        PhotoMasonryCard(
                            photo = photo,
                            aspectRatio = ratio,
                            showTitleBar = showTitles,
                            selectionMode = selectionMode,
                            isSelected = isSelected,
                            strings = strings,
                            onClick = handleClick,
                            onLongClick = handleLongClick,
                            onToggleFavorite = { onToggleFavorite(photo) },
                            onShowDetails = handleDetails,
                            onRename = { onRenamePhoto(photo) },
                            onShare = handleSharePhoto,
                            onRemove = { onRemovePhoto(photo) },
                            onOpenNote = { onOpenNote(photo) }
                        )
                    }
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
                    .testTag("photo_filters_sheet"),
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
                        modifier = Modifier.testTag("photo_favorites_filter")
                    )
                    MediaViewModeButton(
                        currentMode = viewMode,
                        strings = strings,
                        testTag = "photo_view_mode_button",
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
                    listOf(MediaSortOption.NEWEST, MediaSortOption.NAME, MediaSortOption.SIZE).forEach { option ->
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

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                // Photo Studio Tools (Private Vault, Collage Studio, Export PDF)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = {
                            showFiltersSheet = false
                            onOpenPrivateVault()
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.testTag("photos_quick_vault")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.Shield, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Text(strings.privateVaultTitle, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                    Surface(
                        onClick = {
                            showFiltersSheet = false
                            onOpenCollageStudio(emptySet())
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.testTag("photos_quick_collage")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.GridView, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                            Text(strings.collageStudioTitle, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        }
                    }
                    if (photos.isNotEmpty()) {
                        Surface(
                            onClick = {
                                showFiltersSheet = false
                                val targets = if (selectedIds.isNotEmpty()) {
                                    photos.filter { it.id in selectedIds }
                                } else {
                                    photos.take(20)
                                }
                                coroutineScope.launch {
                                    val file = MediaToolsEngine.exportPhotosToPdf(context, targets)
                                    if (file != null) {
                                        onShowBanner(strings.photoPdfReadyBanner)
                                        MediaToolsEngine.shareFile(context, file, "application/pdf", strings.photoExportPdfAction)
                                    } else {
                                        onShowBanner(strings.photoSaveFailedBanner)
                                    }
                                }
                            },
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.testTag("photos_quick_export_pdf")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text(strings.photoExportPdfAction, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoMasonryCard(
    photo: PhotoItem,
    aspectRatio: Float,
    showTitleBar: Boolean,
    selectionMode: Boolean,
    isSelected: Boolean,
    strings: AppStrings,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShowDetails: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onRemove: () -> Unit,
    onOpenNote: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp,
        border = if (isSelected) {
            BorderStroke(2.5.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .testTag("photo_card_${photo.id}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
            ) {
                PhotoImageContent(
                    photo = photo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = photo.extension,
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
                            .padding(10.dp)
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
                } else if (photo.isFavorite) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = strings.favoritesOnly,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (showTitleBar) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(start = 12.dp, top = 4.dp, bottom = 4.dp, end = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = photo.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (!selectionMode) {
                        MediaOverflowMenuButton(
                            isFavorite = photo.isFavorite,
                            playLabel = strings.menuViewNow,
                            strings = strings,
                            testTagPrefix = "photo_${photo.id}",
                            onPlayNow = onClick,
                            onToggleFavorite = onToggleFavorite,
                            onShowDetails = onShowDetails,
                            onRename = onRename,
                            onShare = onShare,
                            onRemove = onRemove,
                            onOpenNote = onOpenNote
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoCompactSquareCard(
    photo: PhotoItem,
    selectionMode: Boolean,
    isSelected: Boolean,
    strings: AppStrings,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = if (isSelected) {
            BorderStroke(2.5.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .testTag("photo_compact_${photo.id}")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PhotoImageContent(
                photo = photo,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            if (selectionMode) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.55f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isSelected) Icons.Filled.Check else Icons.Filled.RadioButtonUnchecked,
                            contentDescription = strings.selectModeAction,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PhotoListRowCard(
    photo: PhotoItem,
    selectionMode: Boolean,
    isSelected: Boolean,
    strings: AppStrings,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShowDetails: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onRemove: () -> Unit,
    onOpenNote: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f) else MaterialTheme.colorScheme.surfaceContainer,
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .testTag("photo_list_${photo.id}")
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
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                PhotoImageContent(
                    photo = photo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                val dims = if (photo.width > 0) "${photo.width}×${photo.height} • " else ""
                Text(
                    text = "$dims${photo.extension} • ${MediaScanner.formatFileSize(photo.sizeBytes)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!selectionMode) {
                MediaOverflowMenuButton(
                    isFavorite = photo.isFavorite,
                    playLabel = strings.menuViewNow,
                    strings = strings,
                    testTagPrefix = "photo_${photo.id}",
                    onPlayNow = onClick,
                    onToggleFavorite = onToggleFavorite,
                    onShowDetails = onShowDetails,
                    onRename = onRename,
                    onShare = onShare,
                    onRemove = onRemove,
                    onOpenNote = onOpenNote
                )
            }
        }
    }
}

@Composable
fun PhotoLightboxViewer(
    photos: List<PhotoItem>,
    initialIndex: Int,
    strings: AppStrings,
    slideshowSeconds: Int = 3,
    stripExifOnShare: Boolean = true,
    onMoveToVault: ((PhotoItem) -> Unit)? = null,
    onMinimizeToFloating: ((PhotoItem) -> Unit)? = null,
    onRequestSystemPip: (() -> Unit)? = null,
    onClose: () -> Unit,
    onToggleFavorite: (PhotoItem) -> Unit,
    onOpenNote: (PhotoItem) -> Unit = {},
) {
    if (photos.isEmpty()) return
    BackHandler(onBack = onClose)

    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val coroutineScope = rememberCoroutineScope()
    val safeStart = initialIndex.coerceIn(0, photos.lastIndex)
    val pagerState = rememberPagerState(initialPage = safeStart, pageCount = { photos.size })
    val currentPhoto = photos[pagerState.currentPage]

    var hudVisible by remember { mutableStateOf(true) }
    var slideshowActive by remember { mutableStateOf(false) }
    var rotationDegrees by remember(pagerState.currentPage) { mutableIntStateOf(0) }
    var isMirrored by remember(pagerState.currentPage) { mutableStateOf(false) }
    var maxBrightBoost by remember { mutableStateOf(false) }
    var activeFilter by remember { mutableStateOf(PhotoStudioFilter.ORIGINAL) }
    var showExifDialog by remember { mutableStateOf(false) }
    var showEditStudio by remember { mutableStateOf(false) }
    var showCompareSlider by remember { mutableStateOf(false) }
    var exifInfo by remember(currentPhoto.id) { mutableStateOf<PhotoExifInfo?>(null) }
    var saveStatusBanner by remember { mutableStateOf<String?>(null) }

    DisposableEffect(maxBrightBoost) {
        val window = activity?.window
        val prev = window?.attributes?.screenBrightness
        if (maxBrightBoost && window != null) {
            val lp = window.attributes
            lp.screenBrightness = 1.0f
            window.attributes = lp
        }
        onDispose {
            if (maxBrightBoost && window != null && prev != null) {
                val lp = window.attributes
                lp.screenBrightness = prev
                window.attributes = lp
            }
        }
    }

    LaunchedEffect(currentPhoto.id) {
        exifInfo = PhotoExifStudioHelper.readExif(context, currentPhoto)
    }

    LaunchedEffect(saveStatusBanner) {
        if (saveStatusBanner != null) {
            delay(2500L)
            saveStatusBanner = null
        }
    }

    val animatedRotation by animateFloatAsState(
        targetValue = rotationDegrees.toFloat(),
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "photoRotate"
    )

    // Smooth auto-slideshow advancing using the user's configured interval
    LaunchedEffect(slideshowActive, photos.size, slideshowSeconds) {
        val intervalMs = slideshowSeconds.coerceIn(2, 10) * 1000L
        while (slideshowActive && photos.size > 1) {
            delay(intervalMs)
            val next = (pagerState.currentPage + 1) % photos.size
            pagerState.animateScrollToPage(next)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 1,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                ZoomableLightboxImage(
                    photo = photos[page],
                    rotationDegrees = if (page == pagerState.currentPage) animatedRotation else 0f,
                    mirrored = if (page == pagerState.currentPage) isMirrored else false,
                    colorFilter = colorFilterFor(activeFilter),
                    onSingleTap = { hudVisible = !hudVisible }
                )
            }

            // Top Floating Controls
            AnimatedVisibility(
                visible = hudVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .displayCutoutPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("lightbox_close")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = strings.close,
                                tint = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "${pagerState.currentPage + 1} / ${photos.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Pro Photo Studio Editor (Crop, Rotate, Adjust, Compress, PDF, Wallpaper)
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f)
                        ) {
                            IconButton(
                                onClick = { showEditStudio = true },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("lightbox_edit_studio_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = strings.photoEditStudioTitle,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // Before/After Compare Slider
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f)
                        ) {
                            IconButton(
                                onClick = { showCompareSlider = true },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("lightbox_compare_slider_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Compare,
                                    contentDescription = strings.photoCompareTitle,
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }

                        // Personal Media Note
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            IconButton(
                                onClick = { onOpenNote(currentPhoto) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("lightbox_note_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.EditNote,
                                    contentDescription = strings.mediaNoteTitle,
                                    tint = Color.White
                                )
                            }
                        }

                        // Minimize Photo to Floating Window (with sticky edge-tuck handle!)
                        if (onMinimizeToFloating != null) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
                            ) {
                                IconButton(
                                    onClick = { onMinimizeToFloating(currentPhoto) },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .testTag("lightbox_minimize_floating")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PictureInPictureAlt,
                                        contentDescription = strings.floatPhotoAction,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }

                        // Optional Android System Photo Picture-in-Picture (shown when enabled in Photos Settings)
                        if (onRequestSystemPip != null) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f)
                            ) {
                                IconButton(
                                    onClick = onRequestSystemPip,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .testTag("lightbox_system_pip_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PictureInPictureAlt,
                                        contentDescription = strings.pipPhotoTitle,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        // Slideshow Play/Pause
                        Surface(
                            shape = CircleShape,
                            color = if (slideshowActive) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f)
                        ) {
                            IconButton(
                                onClick = { slideshowActive = !slideshowActive },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("lightbox_slideshow")
                            ) {
                                Icon(
                                    imageVector = if (slideshowActive) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = strings.photoSlideshow,
                                    tint = if (slideshowActive) MaterialTheme.colorScheme.onPrimary else Color.White
                                )
                            }
                        }

                        // Rotate 90°
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            IconButton(
                                onClick = { rotationDegrees += 90 },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("lightbox_rotate")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.RotateRight,
                                    contentDescription = strings.photoRotate,
                                    tint = Color.White
                                )
                            }
                        }

                        // Horizontal Flip / Mirror
                        Surface(
                            shape = CircleShape,
                            color = if (isMirrored) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f)
                        ) {
                            IconButton(
                                onClick = { isMirrored = !isMirrored },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("lightbox_flip")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Flip,
                                    contentDescription = strings.photoFlipAction,
                                    tint = if (isMirrored) MaterialTheme.colorScheme.onPrimary else Color.White
                                )
                            }
                        }

                        // Max Brightness Boost for crisp outdoor/detail viewing
                        Surface(
                            shape = CircleShape,
                            color = if (maxBrightBoost) MaterialTheme.colorScheme.tertiary else Color.Black.copy(alpha = 0.6f)
                        ) {
                            IconButton(
                                onClick = { maxBrightBoost = !maxBrightBoost },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("lightbox_bright_boost")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Brightness7,
                                    contentDescription = strings.photoBrightBoostAction,
                                    tint = if (maxBrightBoost) MaterialTheme.colorScheme.onTertiary else Color.White
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            IconButton(
                                onClick = { onToggleFavorite(currentPhoto) },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = if (currentPhoto.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = strings.favoritesOnly,
                                    tint = if (currentPhoto.isFavorite) MaterialTheme.colorScheme.primary else Color.White
                                )
                            }
                        }

                        // Photo EXIF Camera Info Button
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            IconButton(
                                onClick = { showExifDialog = true },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("lightbox_exif_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = strings.photoExifTitle,
                                    tint = Color.White
                                )
                            }
                        }

                        // Save Edited Copy (Rotation + Color Filter)
                        Surface(
                            shape = CircleShape,
                            color = if (rotationDegrees % 360 != 0 || activeFilter != PhotoStudioFilter.ORIGINAL) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f)
                            } else {
                                Color.Black.copy(alpha = 0.6f)
                            }
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val ok = PhotoExifStudioHelper.saveEditedCopy(
                                            context = context,
                                            photo = currentPhoto,
                                            rotationDegrees = rotationDegrees,
                                            colorMatrixValues = colorMatrixValuesFor(activeFilter)
                                        )
                                        saveStatusBanner = if (ok) {
                                            strings.photoSavedCopyBanner
                                        } else {
                                            strings.photoSaveFailedBanner
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("lightbox_save_copy_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SaveAlt,
                                    contentDescription = strings.photoSaveCopyAction,
                                    tint = if (rotationDegrees % 360 != 0 || activeFilter != PhotoStudioFilter.ORIGINAL) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        Color.White
                                    }
                                )
                            }
                        }

                        if (onMoveToVault != null && currentPhoto.uriString.isNotBlank()) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Black.copy(alpha = 0.6f)
                            ) {
                                IconButton(
                                    onClick = {
                                        onMoveToVault(currentPhoto)
                                        saveStatusBanner = strings.privateVaultTitle
                                    },
                                    modifier = Modifier
                                        .size(44.dp)
                                        .testTag("lightbox_vault_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = strings.privateVaultTitle,
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            IconButton(
                                onClick = {
                                    if (stripExifOnShare) {
                                        coroutineScope.launch {
                                            val cleanUri = PhotoExifStudioHelper.createCleanShareUri(context, currentPhoto)
                                            if (cleanUri != null) {
                                                saveStatusBanner = strings.stripExifShareTitle
                                                shareMediaItem(context, currentPhoto.title, cleanUri.toString(), "image/jpeg")
                                            } else {
                                                shareMediaItem(context, currentPhoto.title, currentPhoto.uriString, currentPhoto.mimeType)
                                            }
                                        }
                                    } else {
                                        shareMediaItem(context, currentPhoto.title, currentPhoto.uriString, currentPhoto.mimeType)
                                    }
                                },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = strings.menuShare,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Save Copy Confirmation Pill
            AnimatedVisibility(
                visible = saveStatusBanner != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .displayCutoutPadding()
                    .padding(top = 72.dp)
            ) {
                saveStatusBanner?.let { msg ->
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                        shadowElevation = 6.dp
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                        )
                    }
                }
            }

            // Bottom Live Filter Strip + Caption Pill
            AnimatedVisibility(
                visible = hudVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Live Color Filter Pills
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PhotoStudioFilter.entries.forEach { filter ->
                            val selected = activeFilter == filter
                            val label = when (filter) {
                                PhotoStudioFilter.ORIGINAL -> strings.photoFilterOriginal
                                PhotoStudioFilter.VIVID -> strings.photoFilterVivid
                                PhotoStudioFilter.WARM -> strings.photoFilterWarm
                                PhotoStudioFilter.NOIR -> strings.photoFilterNoir
                                PhotoStudioFilter.COOL -> strings.photoFilterCool
                            }
                            Surface(
                                onClick = { activeFilter = filter },
                                shape = CircleShape,
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.65f),
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.25f)
                                ),
                                modifier = Modifier.testTag("photo_filter_${filter.name.lowercase()}")
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else Color.White,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Black.copy(alpha = 0.68f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = currentPhoto.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val dimsText = if (currentPhoto.width > 0) "${currentPhoto.width}×${currentPhoto.height} • " else ""
                            Text(
                                text = "$dimsText${currentPhoto.extension} • ${MediaScanner.formatFileSize(currentPhoto.sizeBytes)} • ${strings.photoNativeQualityBadge}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Photo EXIF & Camera Metadata Dialog
            if (showExifDialog) {
                val info = exifInfo
                val exposureSummary = listOfNotNull(
                    info?.aperture,
                    info?.exposureTime,
                    info?.iso,
                    info?.focalLength
                ).joinToString(" • ").takeIf { it.isNotBlank() }

                AlertDialog(
                    onDismissRequest = { showExifDialog = false },
                    shape = RoundedCornerShape(28.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    title = {
                        Text(
                            text = strings.photoExifTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = currentPhoto.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            info?.dimensions?.let { dims ->
                                Text(
                                    text = "${strings.photoExifDims}: $dims",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            if (info != null && info.hasCameraData) {
                                info.cameraMakeModel?.let { cam ->
                                    Text(
                                        text = "${strings.photoExifCamera}: $cam",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                info.dateTime?.let { dt ->
                                    Text(
                                        text = "${strings.photoExifDate}: $dt",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                exposureSummary?.let { exp ->
                                    Text(
                                        text = "${strings.photoExifSettings}: $exp",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
                                Text(
                                    text = strings.photoExifNoData,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showExifDialog = false }) {
                            Text(strings.close)
                        }
                    }
                )
            }

            if (showEditStudio) {
                PhotoStudioEditorDialog(
                    photo = currentPhoto,
                    onDismiss = { showEditStudio = false },
                    onShowMessage = { msg -> saveStatusBanner = msg }
                )
            }

            if (showCompareSlider) {
                PhotoCompareSliderDialog(
                    primaryPhoto = currentPhoto,
                    allPhotos = photos,
                    onDismiss = { showCompareSlider = false }
                )
            }
        }
    }
}

@Composable
private fun ZoomableLightboxImage(
    photo: PhotoItem,
    rotationDegrees: Float,
    mirrored: Boolean = false,
    colorFilter: ColorFilter?,
    onSingleTap: () -> Unit,
) {
    var scale by remember(photo.id) { mutableFloatStateOf(1f) }
    var offset by remember(photo.id) { mutableStateOf(Offset.Zero) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(dampingRatio = 0.85f),
        label = "lightboxZoom"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(photo.id) {
                detectTapGestures(
                    onTap = { onSingleTap() },
                    onDoubleTap = {
                        if (scale > 1.2f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 2.5f
                        }
                    }
                )
            }
            // Smart pinch/pan that NEVER blocks HorizontalPager swipes when unzoomed (scale == 1f)!
            .pointerInput(photo.id) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    do {
                        val event = awaitPointerEvent()
                        val zoomChange = event.calculateZoom()
                        val panChange = event.calculatePan()
                        val pointerCount = event.changes.count { it.pressed }

                        if (pointerCount >= 2 || scale > 1.03f) {
                            val newScale = (scale * zoomChange).coerceIn(1f, 4f)
                            scale = newScale
                            if (newScale > 1.03f) {
                                offset = Offset(
                                    x = (offset.x + panChange.x).coerceIn(-650f, 650f),
                                    y = (offset.y + panChange.y).coerceIn(-650f, 650f)
                                )
                                event.changes.forEach { it.consume() }
                            } else {
                                offset = Offset.Zero
                            }
                        }
                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.Center
    ) {
        PhotoImageContent(
            photo = photo,
            contentScale = ContentScale.Fit,
            colorFilter = colorFilter,
            fullResolution = true,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = if (mirrored) -animatedScale else animatedScale,
                    scaleY = animatedScale,
                    translationX = offset.x,
                    translationY = offset.y,
                    rotationZ = rotationDegrees
                )
        )
    }
}

@Composable
private fun PhotoImageContent(
    photo: PhotoItem,
    contentScale: ContentScale,
    colorFilter: ColorFilter? = null,
    fullResolution: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    if (photo.resId != null) {
        Image(
            painter = painterResource(id = photo.resId),
            contentDescription = photo.title,
            contentScale = contentScale,
            colorFilter = colorFilter,
            modifier = modifier
        )
    } else {
        val request = remember(photo.uriString, fullResolution) {
            ImageRequest.Builder(context)
                .data(photo.uriString)
                .allowHardware(true)
                .crossfade(if (fullResolution) 100 else 160)
                .apply {
                    if (fullResolution) {
                        size(Size.ORIGINAL)
                        precision(Precision.EXACT)
                    } else {
                        size(420, 420)
                        precision(Precision.INEXACT)
                    }
                }
                .build()
        }
        AsyncImage(
            model = request,
            contentDescription = photo.title,
            contentScale = contentScale,
            colorFilter = colorFilter,
            modifier = modifier
        )
    }
}
