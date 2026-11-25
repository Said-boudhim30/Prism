package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.model.MediaViewMode

data class ShareableMediaPayload(
    val title: String,
    val uriString: String,
    val mimeType: String,
)

/**
 * Shares 1 or many media items using standard Android ACTION_SEND / ACTION_SEND_MULTIPLE.
 */
fun shareMultipleMediaItems(
    context: Context,
    items: List<ShareableMediaPayload>,
    chooserTitle: String,
) {
    if (items.isEmpty()) return
    if (items.size == 1) {
        val single = items.first()
        shareMediaItem(context, single.title, single.uriString, single.mimeType)
        return
    }

    val contentUris = ArrayList<Uri>()
    items.forEach { item ->
        val parsed = runCatching { Uri.parse(item.uriString) }.getOrNull()
        if (parsed != null && parsed.scheme == "content") {
            contentUris.add(parsed)
        }
    }

    val intent = if (contentUris.isNotEmpty()) {
        val commonType = items.map { it.mimeType.substringBefore('/') }.distinct().let { types ->
            if (types.size == 1 && types.first().isNotBlank()) "${types.first()}/*" else "*/*"
        }
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = commonType
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, contentUris)
            putExtra(Intent.EXTRA_SUBJECT, chooserTitle)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    } else {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, chooserTitle)
            putExtra(Intent.EXTRA_TEXT, items.joinToString("\n") { "• ${it.title}" })
        }
    }

    runCatching {
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }
}

/**
 * Reusable View Mode Picker button (List / Compact / Grid) for Music, Videos, and Photos tabs.
 */
@Composable
fun MediaViewModeButton(
    currentMode: MediaViewMode,
    strings: AppStrings,
    testTag: String,
    onSelectMode: (MediaViewMode) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val activeIcon = when (currentMode) {
        MediaViewMode.LIST -> Icons.AutoMirrored.Filled.ViewList
        MediaViewMode.COMPACT -> Icons.Filled.ViewAgenda
        MediaViewMode.GRID -> Icons.Filled.GridView
    }

    Box {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    expanded = true
                }
                .testTag(testTag)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = activeIcon,
                    contentDescription = strings.viewModeTitle,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(21.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(22.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            MediaViewMode.entries.forEach { mode ->
                val selected = mode == currentMode
                val icon = when (mode) {
                    MediaViewMode.LIST -> Icons.AutoMirrored.Filled.ViewList
                    MediaViewMode.COMPACT -> Icons.Filled.ViewAgenda
                    MediaViewMode.GRID -> Icons.Filled.GridView
                }
                val label = when (mode) {
                    MediaViewMode.LIST -> strings.viewModeList
                    MediaViewMode.COMPACT -> strings.viewModeCompact
                    MediaViewMode.GRID -> strings.viewModeGrid
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = if (selected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else null,
                    onClick = {
                        expanded = false
                        onSelectMode(mode)
                    }
                )
            }
        }
    }
}

/**
 * Material 3 Expressive Contextual Multi-Selection Bar with:
 * Select All, Deselect All, Invert Selection, Share, Favorite, Add to Playlist (optional), and Delete.
 */
@Composable
fun MediaSelectionActionBar(
    visible: Boolean,
    selectedCount: Int,
    totalCount: Int,
    strings: AppStrings,
    onCloseSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onInvertSelection: () -> Unit,
    onShareSelected: () -> Unit,
    onFavoriteSelected: () -> Unit,
    onDeleteSelected: () -> Unit,
    onAddSelectedToPlaylist: (() -> Unit)? = null,
    extraActionIcon: ImageVector? = null,
    extraActionLabel: String? = null,
    extraActionTag: String = "selection_extra",
    onExtraAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.92f),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("multi_select_action_bar")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header Row: Close + Count + Quick Selection Controls (All / None / Invert)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = onCloseSelection,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("selection_close")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = strings.close,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = strings.selectedCountLabel(selectedCount, totalCount),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        SelectionChipButton(
                            icon = Icons.Filled.SelectAll,
                            label = strings.selectAllAction,
                            tag = "selection_select_all",
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSelectAll()
                            }
                        )
                        SelectionChipButton(
                            icon = Icons.Filled.Deselect,
                            label = strings.deselectAllAction,
                            tag = "selection_deselect_all",
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onDeselectAll()
                            }
                        )
                        SelectionChipButton(
                            icon = Icons.Filled.Flip,
                            label = strings.invertSelectionAction,
                            tag = "selection_invert",
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onInvertSelection()
                            }
                        )
                    }
                }

                // Batch Operations Row: Share, Favorite, Add to Playlist, Delete
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BatchActionPill(
                        icon = Icons.Filled.Share,
                        label = strings.menuShare,
                        enabled = selectedCount > 0,
                        isDestructive = false,
                        tag = "selection_share",
                        onClick = onShareSelected
                    )
                    BatchActionPill(
                        icon = Icons.Filled.Favorite,
                        label = strings.favoritesOnly,
                        enabled = selectedCount > 0,
                        isDestructive = false,
                        tag = "selection_favorite",
                        onClick = onFavoriteSelected
                    )
                    if (onAddSelectedToPlaylist != null) {
                        BatchActionPill(
                            icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                            label = strings.playlistsChip,
                            enabled = selectedCount > 0,
                            isDestructive = false,
                            tag = "selection_playlist",
                            onClick = onAddSelectedToPlaylist
                        )
                    }
                    if (onExtraAction != null && extraActionIcon != null && extraActionLabel != null) {
                        BatchActionPill(
                            icon = extraActionIcon,
                            label = extraActionLabel,
                            enabled = selectedCount > 0,
                            isDestructive = false,
                            tag = extraActionTag,
                            onClick = onExtraAction
                        )
                    }
                    BatchActionPill(
                        icon = Icons.Filled.DeleteOutline,
                        label = strings.deleteAction,
                        enabled = selectedCount > 0,
                        isDestructive = true,
                        tag = "selection_delete",
                        onClick = { showDeleteConfirm = true }
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    text = strings.confirmDeleteTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = strings.confirmDeleteMessage(selectedCount),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteSelected()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = CircleShape,
                    modifier = Modifier.testTag("selection_delete_confirm")
                ) {
                    Text(strings.deleteAction, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
private fun SelectionChipButton(
    icon: ImageVector,
    label: String,
    tag: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        modifier = Modifier.testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun BatchActionPill(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    isDestructive: Boolean,
    tag: String,
    onClick: () -> Unit,
) {
    val bg = when {
        !enabled -> MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.45f)
        isDestructive -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val fg = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        isDestructive -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.primary
    }
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = bg,
        modifier = Modifier.testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = fg,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = fg
            )
        }
    }
}
