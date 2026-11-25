package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.MediaScanner
import com.example.localization.AppStrings
import com.example.model.AudioTrack
import com.example.model.MediaDetailsInfo
import com.example.model.RenameTarget
import com.example.model.UserPlaylist

@Composable
fun MediaOverflowMenuButton(
    isFavorite: Boolean,
    playLabel: String,
    strings: AppStrings,
    testTagPrefix: String,
    onPlayNow: () -> Unit,
    onToggleFavorite: () -> Unit,
    onShowDetails: () -> Unit,
    onRename: () -> Unit,
    onShare: () -> Unit,
    onRemove: () -> Unit,
    onAddToPlaylist: (() -> Unit)? = null,
    onPlayAsAudio: (() -> Unit)? = null,
    onExtractAudio: (() -> Unit)? = null,
    onTrimVideo: (() -> Unit)? = null,
    onOpenNote: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val triggerInteraction = remember { MutableInteractionSource() }

    Box(modifier = modifier) {
        IconButton(
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                expanded = true
            },
            interactionSource = triggerInteraction,
            modifier = Modifier
                .size(48.dp)
                .expressivePressScale(triggerInteraction, pressedScale = 0.88f)
                .testTag("${testTagPrefix}_more")
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "More options",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(26.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 10.dp,
            shadowElevation = 14.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
        ) {
            DropdownMenuItem(
                text = { Text(playLabel, fontWeight = FontWeight.Bold) },
                leadingIcon = {
                    MenuTonalIconBadge(
                        icon = Icons.Filled.PlayArrow,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    expanded = false
                    onPlayNow()
                }
            )
            if (onPlayAsAudio != null) {
                DropdownMenuItem(
                    text = { Text(strings.playVideoAsAudioAction, fontWeight = FontWeight.SemiBold) },
                    leadingIcon = {
                        MenuTonalIconBadge(
                            icon = Icons.Filled.Headphones,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    },
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        expanded = false
                        onPlayAsAudio()
                    }
                )
            }
            if (onExtractAudio != null) {
                DropdownMenuItem(
                    text = { Text(strings.extractAudioAction, fontWeight = FontWeight.SemiBold) },
                    leadingIcon = {
                        MenuTonalIconBadge(
                            icon = Icons.Filled.MusicNote,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        expanded = false
                        onExtractAudio()
                    }
                )
            }
            if (onTrimVideo != null) {
                DropdownMenuItem(
                    text = { Text(strings.trimVideoAction, fontWeight = FontWeight.SemiBold) },
                    leadingIcon = {
                        MenuTonalIconBadge(
                            icon = Icons.Filled.ContentCut,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    },
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        expanded = false
                        onTrimVideo()
                    }
                )
            }
            if (onOpenNote != null) {
                DropdownMenuItem(
                    text = { Text(strings.mediaNoteTitle) },
                    leadingIcon = {
                        MenuTonalIconBadge(
                            icon = Icons.Filled.EditNote,
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        expanded = false
                        onOpenNote()
                    }
                )
            }
            DropdownMenuItem(
                text = {
                    Text(if (isFavorite) strings.menuRemoveFavorite else strings.menuAddFavorite)
                },
                leadingIcon = {
                    MenuTonalIconBadge(
                        icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        containerColor = if (isFavorite) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = if (isFavorite) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                    )
                },
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    expanded = false
                    onToggleFavorite()
                }
            )
            if (onAddToPlaylist != null) {
                DropdownMenuItem(
                    text = { Text(strings.menuAddToPlaylist) },
                    leadingIcon = {
                        MenuTonalIconBadge(
                            icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    onClick = {
                        expanded = false
                        onAddToPlaylist()
                    }
                )
            }
            DropdownMenuItem(
                text = { Text(strings.menuFileDetails) },
                leadingIcon = {
                    MenuTonalIconBadge(
                        icon = Icons.Filled.Info,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = {
                    expanded = false
                    onShowDetails()
                }
            )
            DropdownMenuItem(
                text = { Text(strings.menuRename) },
                leadingIcon = {
                    MenuTonalIconBadge(
                        icon = Icons.Filled.DriveFileRenameOutline,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = {
                    expanded = false
                    onRename()
                }
            )
            DropdownMenuItem(
                text = { Text(strings.menuShare) },
                leadingIcon = {
                    MenuTonalIconBadge(
                        icon = Icons.Filled.Share,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                onClick = {
                    expanded = false
                    onShare()
                }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp))
            DropdownMenuItem(
                text = {
                    Text(
                        text = strings.menuRemove,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                leadingIcon = {
                    MenuTonalIconBadge(
                        icon = Icons.Filled.DeleteOutline,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                },
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    expanded = false
                    onRemove()
                }
            )
        }
    }
}

@Composable
private fun MenuTonalIconBadge(
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerColor,
        modifier = Modifier.size(32.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun RenameMediaDialog(
    target: RenameTarget,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var text by remember(target.id) { mutableStateOf(target.currentTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        title = {
            Text(
                text = strings.renameDialogTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(strings.renameHint) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rename_media_input")
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank(),
                shape = CircleShape,
                modifier = Modifier.testTag("rename_media_save")
            ) {
                Text(strings.save, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
            }
        }
    )
}

@Composable
fun PlaylistPickerDialog(
    track: AudioTrack,
    playlists: List<UserPlaylist>,
    strings: AppStrings,
    onToggleTrackInPlaylist: (String, String) -> Unit,
    onCreatePlaylist: (String, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var newPlaylistName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        title = {
            Column {
                Text(
                    text = strings.menuAddToPlaylist,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        placeholder = { Text(strings.playlistNameHint) },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(enabled = newPlaylistName.isNotBlank()) {
                                onCreatePlaylist(newPlaylistName, track.id)
                                newPlaylistName = ""
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

                if (playlists.isEmpty()) {
                    Text(
                        text = strings.noPlaylistsYet,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    playlists.forEach { playlist ->
                        val included = track.id in playlist.trackIds
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (included) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            else MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onToggleTrackInPlaylist(playlist.id, track.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (included) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (included) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = playlist.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = strings.playlistTrackCount(playlist.trackIds.size),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.close, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun MediaDetailsDialog(
    details: MediaDetailsInfo,
    strings: AppStrings,
    onDismiss: () -> Unit,
    knowledgeReport: com.example.model.MediaKnowledgeReport? = null,
    isEnriching: Boolean = false,
    onRefreshKnowledge: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        title = {
            Text(
                text = strings.fileDetailsTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DetailRow(strings.detailName, details.title)
                DetailRow(strings.detailFormat, details.extension)
                DetailRow(strings.detailMime, details.mimeType)
                DetailRow(strings.detailSize, MediaScanner.formatFileSize(details.sizeBytes))
                DetailRow(strings.detailMetadata, details.extraMeta)
                DetailRow(strings.detailLocation, details.uriString)
                UniversalMediaKnowledgeSection(
                    report = knowledgeReport,
                    isEnriching = isEnriching,
                    strings = strings,
                    onRefresh = onRefreshKnowledge
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.close, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

fun shareMediaItem(
    context: Context,
    title: String,
    uriString: String,
    mimeType: String,
) {
    val parsedUri = Uri.parse(uriString)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        if (parsedUri.scheme == "content") {
            type = mimeType.ifBlank { "*/*" }
            putExtra(Intent.EXTRA_STREAM, parsedUri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, title)
        }
    }
    context.startActivity(Intent.createChooser(shareIntent, title))
}
