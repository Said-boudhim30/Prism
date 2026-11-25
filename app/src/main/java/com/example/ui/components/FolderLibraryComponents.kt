package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.MediaScanner
import com.example.localization.AppStrings
import com.example.model.AudioTrack
import com.example.model.DuplicateGroup
import com.example.model.DuplicateMediaType
import com.example.model.FolderCustomization
import com.example.model.MediaBookmark
import com.example.model.PhotoItem
import com.example.model.ScanProgressState
import com.example.model.VideoItem
import com.example.model.WatchedFolder

/**
 * First-Launch Smart Auto-Watch Folders Setup Overlay.
 * Appears once when the user opens Prism Media for the first time with no folders saved yet,
 * letting them pick one or multiple device folders that will be persisted and auto-scanned on launch.
 */
@Composable
fun FirstLaunchFolderSetupOverlay(
    watchedFolders: List<WatchedFolder>,
    autoScanEnabled: Boolean,
    scanProgress: ScanProgressState,
    strings: AppStrings,
    onAddFolder: () -> Unit,
    onRemoveFolder: (String) -> Unit,
    onAutoScanChange: (Boolean) -> Unit,
    onFinishSetup: (loadDemo: Boolean) -> Unit,
) {
    val backdrop = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceContainerLow,
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("first_launch_folder_wizard"),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backdrop)
                .statusBarsPadding()
                .displayCutoutPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.FolderCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Text(
                    text = strings.firstLaunchTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = strings.firstLaunchSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Pick Folder CTA Button
                Button(
                    onClick = onAddFolder,
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("wizard_add_folder_btn")
                ) {
                    Icon(Icons.Filled.CreateNewFolder, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.addWatchFolderAction,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Live fast scan progress pill
                AnimatedVisibility(visible = scanProgress.isScanning) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth()
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
                                Text(
                                    text = scanProgress.currentFolderName.ifBlank { strings.scanningLibrary },
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = strings.scanningLiveCount(scanProgress.discoveredCount),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Selected Folders Card List
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = strings.selectedFoldersLabel(watchedFolders.size),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (watchedFolders.isEmpty()) {
                            Text(
                                text = strings.noFoldersPickedYet,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            watchedFolders.forEach { folder ->
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Folder,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = folder.displayName,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = strings.folderItemsSummary(
                                                    folder.audioCount,
                                                    folder.videoCount,
                                                    folder.photoCount
                                                ),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(
                                            onClick = { onRemoveFolder(folder.uriString) },
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.DeleteOutline,
                                                contentDescription = strings.deleteAction,
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.firstLaunchAutoScanHint,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = autoScanEnabled,
                                onCheckedChange = onAutoScanChange,
                                modifier = Modifier.testTag("wizard_autoscan_switch")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f, fill = false))

                // Primary Finish Action (when folders are added)
                if (watchedFolders.isNotEmpty()) {
                    Button(
                        onClick = { onFinishSetup(false) },
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("wizard_start_exploring_btn")
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.startExploringAction,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onFinishSetup(true) },
                        shape = CircleShape,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("wizard_demo_btn")
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.loadDemoMedia, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    OutlinedButton(
                        onClick = { onFinishSetup(false) },
                        shape = CircleShape,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("wizard_skip_btn")
                    ) {
                        Text(strings.skipForNowAction, maxLines = 1)
                    }
                }
            }
        }
    }
}

/**
 * Saved Watch Folders Manager Bottom Sheet Content.
 * Lets the user add, enable/disable, rescan, or remove persistent device folders at any time.
 */
@Composable
fun WatchedFoldersManagerSheetContent(
    folders: List<WatchedFolder>,
    autoScanEnabled: Boolean,
    isScanning: Boolean,
    strings: AppStrings,
    onAddFolder: () -> Unit,
    onToggleFolder: (String, Boolean) -> Unit,
    onRescanFolder: (String) -> Unit,
    onRemoveFolder: (String) -> Unit,
    onRescanAll: () -> Unit,
    onAutoScanChange: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.watchedFoldersTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = strings.watchedFoldersDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Auto-scan toggle
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.autoScanStartupTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = strings.autoScanStartupDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = autoScanEnabled,
                    onCheckedChange = onAutoScanChange,
                    modifier = Modifier.testTag("folders_manager_autoscan_switch")
                )
            }
        }

        // Action row: Add Folder + Fast Rescan All
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onAddFolder,
                shape = CircleShape,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("folders_manager_add_btn")
            ) {
                Icon(Icons.Filled.CreateNewFolder, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(strings.addWatchFolderAction, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            if (folders.isNotEmpty()) {
                FilledTonalButton(
                    onClick = onRescanAll,
                    enabled = !isScanning,
                    shape = CircleShape,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("folders_manager_rescan_all")
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.rescanAllAction, maxLines = 1)
                }
            }
        }

        if (folders.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = strings.noFoldersPickedYet,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(18.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(folders, key = { it.uriString }) { folder ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        border = BorderStroke(
                            1.dp,
                            if (folder.enabled) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                            } else {
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FolderOpen,
                                contentDescription = null,
                                tint = if (folder.enabled) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = folder.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = strings.folderItemsSummary(
                                        folder.audioCount,
                                        folder.videoCount,
                                        folder.photoCount
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = folder.enabled,
                                onCheckedChange = { onToggleFolder(folder.uriString, it) }
                            )
                            IconButton(
                                onClick = { onRescanFolder(folder.uriString) },
                                enabled = !isScanning && folder.enabled,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = strings.rescanAllAction,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { onRemoveFolder(folder.uriString) },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteOutline,
                                    contentDescription = strings.deleteAction,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Reusable Smart Folder Filter Chips Bar for Music, Videos & Photos tabs.
 * Only renders when media spans 2+ folders so the UI stays clean and minimal.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderFilterChipsRow(
    folders: List<String>,
    selectedFolder: String?,
    strings: AppStrings,
    onSelectFolder: (String?) -> Unit,
    modifier: Modifier = Modifier,
    folderCustomizations: Map<String, FolderCustomization> = emptyMap(),
    onLongPressFolder: ((String) -> Unit)? = null,
) {
    if (folders.size < 2) return

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        item(key = "folder_all") {
            FilterChip(
                selected = selectedFolder == null,
                onClick = { onSelectFolder(null) },
                label = { Text(strings.allFoldersChip) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.FolderCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                shape = CircleShape,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
                modifier = Modifier.testTag("folder_chip_all")
            )
        }
        items(folders, key = { "folder_$it" }) { folderName ->
            val custom = folderCustomizations[folderName]
            val selected = selectedFolder == folderName
            val tintColor = custom?.accentArgb?.let { Color(it) }
            val containerColor = when {
                selected -> tintColor?.copy(alpha = 0.28f) ?: MaterialTheme.colorScheme.tertiaryContainer
                tintColor != null -> tintColor.copy(alpha = 0.14f)
                else -> MaterialTheme.colorScheme.surfaceContainerLow
            }
            val borderColor = when {
                selected -> tintColor ?: MaterialTheme.colorScheme.tertiary
                tintColor != null -> tintColor.copy(alpha = 0.55f)
                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
            }
            val labelText = buildString {
                if (!custom?.badgeSymbol.isNullOrBlank()) {
                    append(custom!!.badgeSymbol)
                    append(" ")
                }
                append(folderName)
            }
            Surface(
                shape = CircleShape,
                color = containerColor,
                border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor),
                modifier = Modifier
                    .clip(CircleShape)
                    .combinedClickable(
                        onClick = {
                            onSelectFolder(if (selected) null else folderName)
                        },
                        onLongClick = {
                            onLongPressFolder?.invoke(folderName)
                        }
                    )
                    .testTag("folder_chip_$folderName")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = if (custom?.pinned == true) Icons.Filled.PushPin else Icons.Filled.Folder,
                        contentDescription = null,
                        tint = tintColor ?: if (selected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = labelText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selected || custom?.pinned == true) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Storage Insights & Duplicate File Finder Dialog.
 */
@Composable
fun StorageInsightsDialog(
    audioTracks: List<AudioTrack>,
    videos: List<VideoItem>,
    photos: List<PhotoItem>,
    duplicateGroups: List<DuplicateGroup>,
    strings: AppStrings,
    onCleanGroup: (DuplicateGroup) -> Unit,
    onCleanAll: () -> Unit,
    onDismiss: () -> Unit,
) {
    val audioBytes = remember(audioTracks) { audioTracks.sumOf { it.sizeBytes.coerceAtLeast(0L) } }
    val videoBytes = remember(videos) { videos.sumOf { it.sizeBytes.coerceAtLeast(0L) } }
    val photoBytes = remember(photos) { photos.sumOf { it.sizeBytes.coerceAtLeast(0L) } }
    val totalBytes = (audioBytes + videoBytes + photoBytes).coerceAtLeast(1L)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        icon = {
            Icon(
                imageVector = Icons.Filled.Storage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = strings.storageInsightsTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Size & Segmented Breakdown Bar
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.totalLibrarySizeLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = MediaScanner.formatFileSize(audioBytes + videoBytes + photoBytes),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Segmented color bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val aWeight = (audioBytes.toFloat() / totalBytes).coerceAtLeast(if (audioBytes > 0) 0.04f else 0f)
                            val vWeight = (videoBytes.toFloat() / totalBytes).coerceAtLeast(if (videoBytes > 0) 0.04f else 0f)
                            val pWeight = (photoBytes.toFloat() / totalBytes).coerceAtLeast(if (photoBytes > 0) 0.04f else 0f)
                            if (aWeight > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(aWeight)
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                            if (vWeight > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(vWeight)
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.tertiary)
                                )
                            }
                            if (pWeight > 0f) {
                                Box(
                                    modifier = Modifier
                                        .weight(pWeight)
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.secondary)
                                )
                            }
                        }

                        StorageRowStat(
                            icon = Icons.Filled.MusicNote,
                            label = "${strings.tabMusic} (${audioTracks.size})",
                            value = MediaScanner.formatFileSize(audioBytes),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        StorageRowStat(
                            icon = Icons.Filled.Videocam,
                            label = "${strings.tabVideos} (${videos.size})",
                            value = MediaScanner.formatFileSize(videoBytes),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        StorageRowStat(
                            icon = Icons.Filled.Image,
                            label = "${strings.tabPhotos} (${photos.size})",
                            value = MediaScanner.formatFileSize(photoBytes),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // Duplicate File Finder section
                Text(
                    text = strings.duplicatesFoundTitle(duplicateGroups.size),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (duplicateGroups.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = strings.noDuplicatesFound,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onCleanAll,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("storage_clean_all_duplicates")
                    ) {
                        Icon(Icons.Filled.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.cleanAllDuplicatesAction, fontWeight = FontWeight.Bold)
                    }

                    duplicateGroups.take(15).forEach { group ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val typeIcon = when (group.mediaType) {
                                    DuplicateMediaType.AUDIO -> Icons.Filled.MusicNote
                                    DuplicateMediaType.VIDEO -> Icons.Filled.Videocam
                                    DuplicateMediaType.PHOTO -> Icons.Filled.Image
                                }
                                Icon(
                                    imageVector = typeIcon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = group.displayTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${strings.duplicateCopiesBadge(group.itemIds.size)} • ${MediaScanner.formatFileSize(group.sizeBytes)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                TextButton(onClick = { onCleanGroup(group) }) {
                                    Text(strings.cleanDuplicateAction, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.close)
            }
        }
    )
}

@Composable
private fun StorageRowStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: androidx.compose.ui.graphics.Color,
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
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Audio Tag / Metadata Editor Dialog (Title, Artist, Album).
 */
@Composable
fun EditAudioTagsDialog(
    track: AudioTrack,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onSave: (title: String, artist: String, album: String) -> Unit,
) {
    var title by remember(track.id) { mutableStateOf(track.title) }
    var artist by remember(track.id) { mutableStateOf(track.artist) }
    var album by remember(track.id) { mutableStateOf(track.album) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        icon = {
            Icon(
                imageVector = Icons.Filled.EditNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = strings.editTagsTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(strings.tagTitleLabel) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text(strings.tagArtistLabel) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = album,
                    onValueChange = { album = it },
                    label = { Text(strings.tagAlbumLabel) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, artist, album) },
                enabled = title.isNotBlank(),
                shape = CircleShape,
                modifier = Modifier.testTag("save_audio_tags_btn")
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}

/**
 * Timestamp Bookmarks Sheet Content for active audio tracks/podcasts/mixes.
 */
@Composable
fun TrackBookmarksSheetContent(
    trackTitle: String,
    currentPositionMs: Long,
    bookmarks: List<MediaBookmark>,
    strings: AppStrings,
    onAddBookmark: (String) -> Unit,
    onJumpToBookmark: (Long) -> Unit,
    onDeleteBookmark: (String) -> Unit,
) {
    var noteInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = strings.bookmarksTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = trackTitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = noteInput,
                onValueChange = { noteInput = it },
                placeholder = { Text(strings.bookmarkLabelHint) },
                singleLine = true,
                shape = CircleShape,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    onAddBookmark(noteInput)
                    noteInput = ""
                },
                shape = CircleShape,
                modifier = Modifier
                    .height(50.dp)
                    .testTag("add_bookmark_btn")
            ) {
                Icon(Icons.Filled.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(MediaScanner.formatDuration(currentPositionMs), fontWeight = FontWeight.Bold)
            }
        }

        if (bookmarks.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = strings.noBookmarksYet,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookmarks, key = { it.id }) { bm ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onJumpToBookmark(bm.positionMs) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bm.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = MediaScanner.formatDuration(bm.positionMs),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            IconButton(
                                onClick = { onDeleteBookmark(bm.id) },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteOutline,
                                    contentDescription = strings.deleteAction,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
