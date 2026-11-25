package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BluetoothAudio
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RestoreFromTrash
import androidx.compose.material.icons.rounded.RotateRight
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.MediaScanner
import com.example.data.MediaToolsEngine
import com.example.data.QuranHadithCatalog
import com.example.localization.LocalAppStrings
import com.example.model.AppLanguage
import com.example.model.AudioDeviceProfile
import com.example.model.AudioOutputProfileType
import com.example.model.AudioTrack
import com.example.model.EqPreset
import com.example.model.MediaNote
import com.example.model.PhotoArtFilter
import com.example.model.PhotoCropAspect
import com.example.model.PhotoItem
import com.example.model.RenameMediaType
import com.example.model.ReverbEnvironment
import com.example.model.TrashedMediaItem
import com.example.model.UserPlaylist
import com.example.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.roundToInt

// ============================================================================
// 1. GLOBAL SPOTLIGHT SEARCH SHEET
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSpotlightSearchSheet(
    audioTracks: List<AudioTrack>,
    videos: List<VideoItem>,
    photos: List<PhotoItem>,
    playlists: List<UserPlaylist>,
    mediaNotes: Map<String, MediaNote>,
    onDismiss: () -> Unit,
    onPlayAudio: (AudioTrack) -> Unit,
    onOpenVideo: (VideoItem) -> Unit,
    onOpenPhoto: (Int) -> Unit,
    onSelectPlaylist: (String) -> Unit,
    onOpenQuranHub: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }
    val trimmed = query.trim().lowercase()

    val matchedAudio = remember(trimmed, audioTracks) {
        if (trimmed.isEmpty()) audioTracks.take(4)
        else audioTracks.filter {
            it.title.lowercase().contains(trimmed) ||
                it.artist.lowercase().contains(trimmed) ||
                it.album.lowercase().contains(trimmed)
        }.take(8)
    }
    val matchedVideos = remember(trimmed, videos) {
        if (trimmed.isEmpty()) videos.take(3)
        else videos.filter {
            it.title.lowercase().contains(trimmed) || it.folderName.lowercase().contains(trimmed)
        }.take(6)
    }
    val matchedPhotos = remember(trimmed, photos) {
        if (trimmed.isEmpty()) photos.take(4)
        else photos.filter {
            it.title.lowercase().contains(trimmed) || it.folderName.lowercase().contains(trimmed)
        }.take(6)
    }
    val matchedPlaylists = remember(trimmed, playlists) {
        if (trimmed.isEmpty()) playlists.take(3)
        else playlists.filter { it.name.lowercase().contains(trimmed) }.take(5)
    }
    val matchedSurahs = remember(trimmed) {
        if (trimmed.isEmpty()) emptyList()
        else QuranHadithCatalog.surahs.filter {
            it.arabicName.contains(trimmed) ||
                it.englishName.lowercase().contains(trimmed) ||
                it.number.toString() == trimmed
        }.take(5)
    }
    val matchedNotes = remember(trimmed, mediaNotes) {
        if (trimmed.isEmpty()) mediaNotes.values.take(3)
        else mediaNotes.values.filter {
            it.noteText.lowercase().contains(trimmed) || it.mediaTitle.lowercase().contains(trimmed)
        }.take(5)
    }

    val totalHits = matchedAudio.size + matchedVideos.size + matchedPhotos.size +
        matchedPlaylists.size + matchedSurahs.size + matchedNotes.size

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .heightIn(max = 580.dp)
                .testTag("global_spotlight_sheet"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = strings.globalSearchTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("spotlight_close_btn")) {
                    Icon(Icons.Rounded.Close, contentDescription = strings.close)
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text(strings.globalSearchHint, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Rounded.Close, contentDescription = strings.close)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_spotlight_input")
            )

            if (totalHits == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = strings.globalSearchNoResults,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    if (matchedAudio.isNotEmpty()) {
                        item {
                            SpotlightCategoryHeader(strings.tabMusic, matchedAudio.size)
                        }
                        items(matchedAudio, key = { "a_${it.id}" }) { track ->
                            SpotlightResultRow(
                                icon = Icons.Rounded.Audiotrack,
                                title = track.title,
                                subtitle = "${track.artist} • ${track.extension}",
                                onClick = {
                                    onPlayAudio(track)
                                    onDismiss()
                                }
                            )
                        }
                    }
                    if (matchedVideos.isNotEmpty()) {
                        item {
                            SpotlightCategoryHeader(strings.tabVideos, matchedVideos.size)
                        }
                        items(matchedVideos, key = { "v_${it.id}" }) { video ->
                            SpotlightResultRow(
                                icon = Icons.Rounded.Movie,
                                title = video.title,
                                subtitle = "${video.resolution} • ${MediaScanner.formatDuration(video.durationMs)}",
                                onClick = {
                                    onOpenVideo(video)
                                    onDismiss()
                                }
                            )
                        }
                    }
                    if (matchedPhotos.isNotEmpty()) {
                        item {
                            SpotlightCategoryHeader(strings.tabPhotos, matchedPhotos.size)
                        }
                        items(matchedPhotos, key = { "p_${it.id}" }) { photo ->
                            SpotlightResultRow(
                                icon = Icons.Rounded.Image,
                                title = photo.title,
                                subtitle = "${photo.width}×${photo.height} • ${photo.extension}",
                                onClick = {
                                    val idx = photos.indexOfFirst { it.id == photo.id }
                                    if (idx >= 0) onOpenPhoto(idx)
                                    onDismiss()
                                }
                            )
                        }
                    }
                    if (matchedPlaylists.isNotEmpty()) {
                        item {
                            SpotlightCategoryHeader(strings.playlistsTitle, matchedPlaylists.size)
                        }
                        items(matchedPlaylists, key = { "pl_${it.id}" }) { pl ->
                            SpotlightResultRow(
                                icon = Icons.Rounded.PlaylistPlay,
                                title = pl.name,
                                subtitle = "${pl.trackIds.size} ${strings.browseTracksTab}",
                                onClick = {
                                    onSelectPlaylist(pl.id)
                                    onDismiss()
                                }
                            )
                        }
                    }
                    if (matchedSurahs.isNotEmpty()) {
                        item {
                            SpotlightCategoryHeader(strings.quranHubTitle, matchedSurahs.size)
                        }
                        items(matchedSurahs, key = { "q_${it.number}" }) { surah ->
                            SpotlightResultRow(
                                icon = Icons.Rounded.MenuBook,
                                title = "${surah.number}. ${surah.arabicName} • ${surah.englishName}",
                                subtitle = strings.quranAyahsCountLabel(surah.ayahCount),
                                onClick = {
                                    onOpenQuranHub()
                                    onDismiss()
                                }
                            )
                        }
                    }
                    if (matchedNotes.isNotEmpty()) {
                        item {
                            SpotlightCategoryHeader(strings.mediaNoteTitle, matchedNotes.size)
                        }
                        items(matchedNotes, key = { "n_${it.mediaId}" }) { note ->
                            SpotlightResultRow(
                                icon = Icons.Rounded.EditNote,
                                title = note.mediaTitle.ifBlank { strings.mediaNoteTitle },
                                subtitle = note.noteText,
                                onClick = onDismiss
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpotlightCategoryHeader(title: String, count: Int) {
    Text(
        text = "$title ($count)",
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
    )
}

@Composable
private fun SpotlightResultRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ============================================================================
// 2. SMART RECYCLE BIN SHEET
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecycleBinSheet(
    trashedItems: List<TrashedMediaItem>,
    onDismiss: () -> Unit,
    onRestoreItem: (String) -> Unit,
    onRestoreAll: () -> Unit,
    onEmptyBin: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .heightIn(max = 520.dp)
                .testTag("recycle_bin_sheet"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${strings.recycleBinTitle} (${trashedItems.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.recycleBinSubtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = strings.close)
                }
            }

            if (trashedItems.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onRestoreAll,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("recycle_restore_all_btn")
                    ) {
                        Icon(Icons.Rounded.RestoreFromTrash, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.restoreAllAction, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    OutlinedButton(
                        onClick = onEmptyBin,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("recycle_empty_btn")
                    ) {
                        Icon(Icons.Rounded.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.emptyBinAction, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            if (trashedItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            Icons.Rounded.DeleteOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = strings.recycleBinEmpty,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(trashedItems, key = { it.id }) { item ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val icon = when (item.mediaType) {
                                    RenameMediaType.AUDIO -> Icons.Rounded.Audiotrack
                                    RenameMediaType.VIDEO -> Icons.Rounded.Movie
                                    RenameMediaType.PHOTO -> Icons.Rounded.Image
                                }
                                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val elapsedMs = (System.currentTimeMillis() - item.deletedAtMs).coerceAtLeast(0L)
                                    val daysLeft = ((com.example.data.SettingsRepository.RECYCLE_BIN_RETENTION_MS - elapsedMs) / 86_400_000L)
                                        .toInt()
                                        .coerceIn(1, 30)
                                    Text(
                                        text = "${item.subtitle} • ${strings.recycleDaysLeft(daysLeft)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                FilledTonalButton(
                                    onClick = { onRestoreItem(item.id) },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(strings.restoreItemAction, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 3. PERSONAL MEDIA NOTE DIALOG
// ============================================================================

@Composable
fun MediaNoteDialog(
    mediaId: String,
    mediaTitle: String,
    mediaType: RenameMediaType,
    existingNote: MediaNote?,
    currentPositionMs: Long = 0L,
    onDismiss: () -> Unit,
    onSave: (String, Long) -> Unit,
    onDelete: () -> Unit,
) {
    val strings = LocalAppStrings.current
    var text by remember(mediaId) { mutableStateOf(existingNote?.noteText.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Rounded.EditNote, contentDescription = null) },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(strings.mediaNoteTitle, fontWeight = FontWeight.Bold)
                Text(
                    text = mediaTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (currentPositionMs > 0L) {
                    Text(
                        text = "@ ${MediaScanner.formatDuration(currentPositionMs)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text(strings.mediaNoteHint) },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("media_note_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(text, currentPositionMs)
                    onDismiss()
                },
                modifier = Modifier.testTag("media_note_save_btn")
            ) {
                Text(strings.saveNoteAction)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (existingNote != null) {
                    TextButton(
                        onClick = {
                            onDelete()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("media_note_delete_btn")
                    ) {
                        Text(strings.deleteNoteAction, color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(strings.cancel)
                }
            }
        }
    )
}

// ============================================================================
// 4. TAP-TO-SYNC KARAOKE LRC LYRICS STUDIO DIALOG
// ============================================================================

@Composable
fun TapToSyncLyricsDialog(
    trackTitle: String,
    initialRawLyrics: String,
    currentPositionMs: Long,
    onDismiss: () -> Unit,
    onSaveSyncedLrc: (String) -> Unit,
) {
    val strings = LocalAppStrings.current
    val initialPlain = remember(initialRawLyrics) {
        initialRawLyrics.lines()
            .map { it.replace(Regex("^\\[\\d{1,2}:\\d{2}(?:\\.\\d{1,3})?\\]\\s*"), "").trim() }
            .filter { it.isNotEmpty() }
    }
    var rawText by remember { mutableStateOf(initialPlain.joinToString("\n")) }
    var isSyncingStep by remember { mutableStateOf(initialPlain.isNotEmpty()) }
    val lines = remember(rawText) {
        rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
    }
    val stampedMap = remember { mutableStateMapOf<Int, Long>() }
    var activeLineIndex by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 580.dp)
                .testTag("tap_sync_lyrics_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(strings.tapSyncLyricsTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "$trackTitle • ${formatLrcStamp(currentPositionMs)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = strings.close)
                    }
                }

                Text(
                    text = strings.tapSyncLyricsHint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!isSyncingStep || lines.isEmpty()) {
                    OutlinedTextField(
                        value = rawText,
                        onValueChange = { rawText = it },
                        minLines = 6,
                        maxLines = 10,
                        placeholder = { Text(strings.lyricsPlaceholder) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                    )
                    Button(
                        onClick = {
                            if (lines.isNotEmpty()) {
                                stampedMap.clear()
                                activeLineIndex = 0
                                isSyncingStep = true
                            }
                        },
                        enabled = lines.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.tapSyncStampLine)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(lines.size) { idx ->
                            val stamp = stampedMap[idx]
                            val isActive = idx == activeLineIndex
                            Surface(
                                onClick = { activeLineIndex = idx },
                                shape = RoundedCornerShape(12.dp),
                                color = when {
                                    isActive -> MaterialTheme.colorScheme.primaryContainer
                                    stamp != null -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                                    else -> MaterialTheme.colorScheme.surfaceContainerHigh
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = stamp?.let { formatLrcStamp(it) } ?: "[--:--.--]",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (stamp != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = lines[idx],
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (activeLineIndex in lines.indices) {
                                stampedMap[activeLineIndex] = currentPositionMs
                                if (activeLineIndex < lines.lastIndex) activeLineIndex++
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("tap_sync_stamp_btn")
                    ) {
                        Icon(Icons.Rounded.Timer, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("${strings.tapSyncStampLine} (${formatLrcStamp(currentPositionMs)})")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { isSyncingStep = false }) {
                            Text(strings.editLyricsAction)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = {
                                stampedMap.clear()
                                activeLineIndex = 0
                            }) {
                                Text(strings.eqReset)
                            }
                            FilledTonalButton(
                                onClick = {
                                    val built = lines.mapIndexed { idx, line ->
                                        val ts = stampedMap[idx]
                                        if (ts != null) "${formatLrcStamp(ts)} $line" else line
                                    }.joinToString("\n")
                                    onSaveSyncedLrc(built)
                                    onDismiss()
                                },
                                modifier = Modifier.testTag("tap_sync_save_btn")
                            ) {
                                Text(strings.save)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatLrcStamp(ms: Long): String {
    val safe = ms.coerceAtLeast(0L)
    val mins = safe / 60_000L
    val secs = (safe % 60_000L) / 1000L
    val hundredths = (safe % 1000L) / 10L
    return String.format(Locale.US, "[%02d:%02d.%02d]", mins, secs, hundredths)
}

// ============================================================================
// 5. REAL PHOTO STUDIO EDITOR DIALOG (CROP, ROTATE, ADJUST, COMPRESS, PDF, WALLPAPER)
// ============================================================================

@Composable
fun PhotoStudioEditorDialog(
    photo: PhotoItem,
    onDismiss: () -> Unit,
    onShowMessage: (String) -> Unit,
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()

    var rotationDeg by remember { mutableFloatStateOf(0f) }
    var cropAspect by remember { mutableStateOf(PhotoCropAspect.ORIGINAL) }
    var artFilter by remember { mutableStateOf(PhotoArtFilter.ORIGINAL) }
    var watermarkText by remember { mutableStateOf("") }
    var brightness by remember { mutableFloatStateOf(0f) }
    var contrast by remember { mutableFloatStateOf(1f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var compressQuality by remember { mutableIntStateOf(72) }
    var isBusy by remember { mutableStateOf(false) }

    val colorFilter = remember(artFilter, brightness, contrast, saturation) {
        val base = when (artFilter) {
            PhotoArtFilter.ORIGINAL -> ColorMatrix()
            PhotoArtFilter.VIVID -> ColorMatrix().apply { setToSaturation(1.45f) }
            PhotoArtFilter.WARM -> ColorMatrix(
                floatArrayOf(
                    1.12f, 0f, 0f, 0f, 10f,
                    0f, 1.03f, 0f, 0f, 4f,
                    0f, 0f, 0.88f, 0f, -6f,
                    0f, 0f, 0f, 1f, 0f,
                )
            )
            PhotoArtFilter.COOL -> ColorMatrix(
                floatArrayOf(
                    0.90f, 0f, 0f, 0f, -6f,
                    0f, 1.02f, 0f, 0f, 4f,
                    0f, 0f, 1.15f, 0f, 12f,
                    0f, 0f, 0f, 1f, 0f,
                )
            )
            PhotoArtFilter.NOIR -> ColorMatrix().apply { setToSaturation(0f) }
            PhotoArtFilter.EMERALD -> ColorMatrix(
                floatArrayOf(
                    0.92f, 0f, 0f, 0f, -4f,
                    0f, 1.12f, 0f, 0f, 8f,
                    0f, 0f, 0.98f, 0f, 4f,
                    0f, 0f, 0f, 1f, 0f,
                )
            )
        }
        val satM = ColorMatrix().apply { setToSaturation(saturation.coerceIn(0f, 2f)) }
        val c = contrast.coerceIn(0.5f, 1.8f)
        val t = (1f - c) * 128f + brightness.coerceIn(-100f, 100f)
        val bcM = ColorMatrix(
            floatArrayOf(
                c, 0f, 0f, 0f, t,
                0f, c, 0f, 0f, t,
                0f, 0f, c, 0f, t,
                0f, 0f, 0f, 1f, 0f,
            )
        )
        base.timesAssign(satM)
        base.timesAssign(bcM)
        ColorFilter.colorMatrix(base)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("photo_studio_editor_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.photoEditStudioTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                rotationDeg = 0f
                                cropAspect = PhotoCropAspect.ORIGINAL
                                artFilter = PhotoArtFilter.ORIGINAL
                                watermarkText = ""
                                brightness = 0f
                                contrast = 1f
                                saturation = 1f
                            },
                            modifier = Modifier.testTag("photo_editor_reset_btn")
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = strings.eqReset)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Rounded.Close, contentDescription = strings.close)
                        }
                    }
                }

                // Live Preview Stage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest),
                    contentAlignment = Alignment.Center
                ) {
                    val previewModifier = when (cropAspect) {
                        PhotoCropAspect.ORIGINAL -> Modifier.fillMaxSize()
                        PhotoCropAspect.SQUARE_1_1 -> Modifier.fillMaxHeight().aspectRatio(1f)
                        PhotoCropAspect.CLASSIC_4_3 -> Modifier.fillMaxHeight().aspectRatio(4f / 3f)
                        PhotoCropAspect.WIDE_16_9 -> Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                    }
                    AsyncImage(
                        model = photo.resId ?: photo.uriString,
                        contentDescription = photo.title,
                        contentScale = if (cropAspect == PhotoCropAspect.ORIGINAL) ContentScale.Fit else ContentScale.Crop,
                        colorFilter = colorFilter,
                        modifier = previewModifier
                            .clip(RoundedCornerShape(12.dp))
                            .graphicsLayer { rotationZ = rotationDeg }
                    )
                }

                // Scrollable controls
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Crop & Rotate Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(strings.photoCropLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        FilledTonalButton(
                            onClick = { rotationDeg = (rotationDeg + 90f) % 360f },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("photo_rotate_90_btn")
                        ) {
                            Icon(Icons.Rounded.RotateRight, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(strings.photoRotate90Action)
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(PhotoCropAspect.entries) { aspect ->
                            val label = when (aspect) {
                                PhotoCropAspect.ORIGINAL -> "Original"
                                PhotoCropAspect.SQUARE_1_1 -> "1:1"
                                PhotoCropAspect.CLASSIC_4_3 -> "4:3"
                                PhotoCropAspect.WIDE_16_9 -> "16:9"
                            }
                            FilterChip(
                                selected = cropAspect == aspect,
                                onClick = { cropAspect = aspect },
                                label = { Text(label) },
                                leadingIcon = if (cropAspect == aspect) {
                                    { Icon(Icons.Rounded.Crop, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null
                            )
                        }
                    }

                    // Artistic Filter Presets Row
                    Text(strings.photoArtFilterLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(PhotoArtFilter.entries) { preset ->
                            val label = when (preset) {
                                PhotoArtFilter.ORIGINAL -> strings.photoFilterOriginal
                                PhotoArtFilter.VIVID -> strings.photoFilterVivid
                                PhotoArtFilter.WARM -> strings.photoFilterWarm
                                PhotoArtFilter.COOL -> strings.photoFilterCool
                                PhotoArtFilter.NOIR -> strings.photoFilterNoir
                                PhotoArtFilter.EMERALD -> "Emerald"
                            }
                            FilterChip(
                                selected = artFilter == preset,
                                onClick = { artFilter = preset },
                                label = { Text(label) },
                                modifier = Modifier.testTag("photo_filter_${preset.name}")
                            )
                        }
                    }

                    // Custom Signature / Watermark Input
                    OutlinedTextField(
                        value = watermarkText,
                        onValueChange = { watermarkText = it.take(40) },
                        placeholder = { Text(strings.photoWatermarkHint, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("photo_watermark_input")
                    )

                    // Brightness / Contrast / Saturation Sliders
                    Text("${strings.photoBrightnessLabel}: ${brightness.roundToInt()}", style = MaterialTheme.typography.labelMedium)
                    Slider(value = brightness, onValueChange = { brightness = it }, valueRange = -80f..80f)

                    Text("${strings.photoContrastLabel}: ${String.format(Locale.US, "%.2fx", contrast)}", style = MaterialTheme.typography.labelMedium)
                    Slider(value = contrast, onValueChange = { contrast = it }, valueRange = 0.6f..1.6f)

                    Text("${strings.photoSaturationLabel}: ${(saturation * 100).roundToInt()}%", style = MaterialTheme.typography.labelMedium)
                    Slider(value = saturation, onValueChange = { saturation = it }, valueRange = 0f..2f)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Quick Pro Actions: Wallpaper, PDF Export, JPEG Compressor
                    Text("${strings.photoCompressAction} ($compressQuality%)", style = MaterialTheme.typography.labelMedium)
                    Slider(
                        value = compressQuality.toFloat(),
                        onValueChange = { compressQuality = it.roundToInt() },
                        valueRange = 25f..90f
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (isBusy) return@OutlinedButton
                                isBusy = true
                                scope.launch {
                                    val res = MediaToolsEngine.compressPhoto(context, photo, compressQuality)
                                    isBusy = false
                                    if (res.success && res.savedFile != null) {
                                        onShowMessage(
                                            strings.photoCompressedBanner(
                                                MediaScanner.formatFileSize(res.originalBytes),
                                                MediaScanner.formatFileSize(res.compressedBytes),
                                            )
                                        )
                                        MediaToolsEngine.shareFile(context, res.savedFile, "image/jpeg", strings.photoCompressAction)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("photo_compress_btn"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Rounded.Compress, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(strings.photoCompressAction, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                if (isBusy) return@OutlinedButton
                                isBusy = true
                                scope.launch {
                                    val pdf = MediaToolsEngine.exportPhotosToPdf(context, listOf(photo), photo.title)
                                    isBusy = false
                                    if (pdf != null) {
                                        onShowMessage(strings.photoPdfReadyBanner)
                                        MediaToolsEngine.shareFile(context, pdf, "application/pdf", strings.photoExportPdfAction)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("photo_export_pdf_btn"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(strings.photoExportPdfAction, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            if (isBusy) return@OutlinedButton
                            isBusy = true
                            scope.launch {
                                val ok = MediaToolsEngine.setPhotoAsWallpaper(context, photo)
                                isBusy = false
                                if (ok) onShowMessage(strings.photoWallpaperSuccess)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("photo_set_wallpaper_btn")
                    ) {
                        Icon(Icons.Rounded.Wallpaper, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.photoSetWallpaperAction)
                    }
                }

                // Primary Save Button
                Button(
                    onClick = {
                        if (isBusy) return@Button
                        isBusy = true
                        scope.launch {
                            val saved = MediaToolsEngine.editAndSavePhoto(
                                context = context,
                                photo = photo,
                                rotationDegrees = rotationDeg,
                                cropAspect = cropAspect,
                                brightness = brightness,
                                contrast = contrast,
                                saturation = saturation,
                                artFilter = artFilter,
                                watermarkText = watermarkText,
                            )
                            isBusy = false
                            if (saved) {
                                onShowMessage(strings.save)
                                onDismiss()
                            }
                        }
                    },
                    enabled = !isBusy,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("photo_editor_save_btn")
                ) {
                    Icon(Icons.Rounded.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(strings.save)
                }
            }
        }
    }
}

// ============================================================================
// 6. PHOTO COMPARE SLIDER DIALOG (DRAGGABLE VERTICAL SPLIT DIVIDER)
// ============================================================================

@Composable
fun PhotoCompareSliderDialog(
    primaryPhoto: PhotoItem,
    allPhotos: List<PhotoItem>,
    onDismiss: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val otherPhotos = remember(allPhotos, primaryPhoto.id) { allPhotos.filterNot { it.id == primaryPhoto.id } }
    var secondPhoto by remember { mutableStateOf(otherPhotos.firstOrNull() ?: primaryPhoto) }
    var splitFraction by remember { mutableFloatStateOf(0.5f) }
    var enhanceRightPreview by remember { mutableStateOf(otherPhotos.isEmpty()) }

    val enhancedColorFilter = remember {
        val sat = ColorMatrix().apply { setToSaturation(1.35f) }
        val c = 1.15f
        val t = (1f - c) * 128f + 10f
        sat.timesAssign(
            ColorMatrix(
                floatArrayOf(
                    c, 0f, 0f, 0f, t,
                    0f, c, 0f, 0f, t,
                    0f, 0f, c, 0f, t,
                    0f, 0f, 0f, 1f, 0f,
                )
            )
        )
        ColorFilter.colorMatrix(sat)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 620.dp)
                .testTag("photo_compare_slider_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.Compare, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(strings.photoCompareTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = strings.close)
                    }
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, dragAmount ->
                                change.consume()
                                val widthPx = size.width.toFloat().coerceAtLeast(1f)
                                splitFraction = (splitFraction + dragAmount / widthPx).coerceIn(0.08f, 0.92f)
                            }
                        }
                ) {
                    val fullW = maxWidth
                    val density = LocalDensity.current

                    // Right / After Photo
                    AsyncImage(
                        model = secondPhoto.resId ?: secondPhoto.uriString,
                        contentDescription = secondPhoto.title,
                        contentScale = ContentScale.Crop,
                        colorFilter = if (enhanceRightPreview) enhancedColorFilter else null,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Left / Before Photo clipped by splitFraction
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(fullW * splitFraction)
                            .clipToBounds()
                    ) {
                        AsyncImage(
                            model = primaryPhoto.resId ?: primaryPhoto.uriString,
                            contentDescription = primaryPhoto.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(fullW)
                                .fillMaxHeight()
                        )
                    }

                    // Labels
                    Surface(
                        color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = strings.photoCompareBefore,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = strings.photoCompareAfter,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Draggable Vertical Divider & Handle
                    val lineOffsetPx = with(density) { (fullW * splitFraction).toPx().roundToInt() }
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(lineOffsetPx - 14, 0) }
                            .fillMaxHeight()
                            .width(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(2.5.dp)
                                .background(Color.White)
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Rounded.Compare,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Second photo selector strip
                if (otherPhotos.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = enhanceRightPreview && secondPhoto.id == primaryPhoto.id,
                                onClick = {
                                    secondPhoto = primaryPhoto
                                    enhanceRightPreview = true
                                },
                                label = { Text("HDR Preview") }
                            )
                        }
                        items(otherPhotos.take(12), key = { it.id }) { candidate ->
                            val selected = !enhanceRightPreview && secondPhoto.id == candidate.id
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (selected) 2.5.dp else 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        secondPhoto = candidate
                                        enhanceRightPreview = false
                                    }
                            ) {
                                AsyncImage(
                                    model = candidate.resId ?: candidate.uriString,
                                    contentDescription = candidate.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 7. FULL MORNING & EVENING ADHKAR STUDIO SHEET
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorningEveningAdhkarSheet(
    language: AppLanguage,
    completedKeys: Set<String>,
    onToggleCompleted: (String) -> Unit,
    onResetAll: () -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Morning, 1 = Evening
    val tapCounts = remember { mutableStateMapOf<String, Int>() }

    val items = remember(selectedTab) {
        MediaToolsEngine.adhkarCatalog.filter { if (selectedTab == 0) it.isMorning else !it.isMorning }
    }
    val completedCount = items.count { it.id in completedKeys }
    val progress = if (items.isEmpty()) 0f else completedCount.toFloat() / items.size.toFloat()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .heightIn(max = 600.dp)
                .testTag("adhkar_studio_sheet"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        if (selectedTab == 0) Icons.Rounded.WbSunny else Icons.Rounded.NightsStay,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(strings.adhkarTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = {
                            tapCounts.clear()
                            onResetAll()
                        },
                        modifier = Modifier.testTag("adhkar_reset_btn")
                    ) {
                        Text(strings.adhkarResetAction)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = strings.close)
                    }
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(strings.adhkarMorningTab) },
                    icon = { Icon(Icons.Rounded.WbSunny, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(strings.adhkarEveningTab) },
                    icon = { Icon(Icons.Rounded.NightsStay, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(items, key = { it.id }) { dhikr ->
                    val isDone = dhikr.id in completedKeys
                    val currentTap = if (isDone) dhikr.repeatTarget else (tapCounts[dhikr.id] ?: 0)
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isDone) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        },
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
                                    text = if (language == AppLanguage.AR) dhikr.titleAr else dhikr.titleEn,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isDone) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                        Text(strings.adhkarCompletedBadge, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Text(
                                text = dhikr.arabicText,
                                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (language != AppLanguage.AR) {
                                Text(
                                    text = dhikr.translationEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                FilledTonalButton(
                                    onClick = {
                                        if (isDone) {
                                            tapCounts[dhikr.id] = 0
                                            onToggleCompleted(dhikr.id)
                                        } else {
                                            val next = currentTap + 1
                                            tapCounts[dhikr.id] = next
                                            if (next >= dhikr.repeatTarget) {
                                                onToggleCompleted(dhikr.id)
                                            }
                                        }
                                    },
                                    modifier = Modifier.testTag("adhkar_count_${dhikr.id}")
                                ) {
                                    Text("$currentTap / ${dhikr.repeatTarget}", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 8. AUDIO OUTPUT DEVICE PROFILES SHEET (SPEAKER / WIRED / BLUETOOTH)
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioDeviceProfilesSheet(
    activeOutput: AudioOutputProfileType,
    profiles: Map<AudioOutputProfileType, AudioDeviceProfile>,
    autoSwitchEnabled: Boolean,
    onToggleAutoSwitch: (Boolean) -> Unit,
    onSaveProfile: (AudioDeviceProfile) -> Unit,
    onApplyProfile: (AudioOutputProfileType) -> Unit,
    onRefreshOutput: () -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = LocalAppStrings.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editingType by remember(activeOutput) { mutableStateOf(activeOutput) }
    val currentProfile = profiles[editingType] ?: AudioDeviceProfile(editingType)

    var selectedPreset by remember(editingType, currentProfile) { mutableStateOf(currentProfile.eqPreset) }
    var bassValue by remember(editingType, currentProfile) { mutableFloatStateOf(currentProfile.bassBoost) }
    var reverbValue by remember(editingType, currentProfile) { mutableStateOf(currentProfile.reverb) }

    fun outputLabel(type: AudioOutputProfileType): String = when (type) {
        AudioOutputProfileType.SPEAKER -> strings.audioOutputSpeaker
        AudioOutputProfileType.WIRED_HEADSET -> strings.audioOutputWired
        AudioOutputProfileType.BLUETOOTH -> strings.audioOutputBluetooth
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .testTag("audio_device_profiles_sheet"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(strings.audioProfilesTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = strings.audioProfilesDesc,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRefreshOutput) {
                    Icon(Icons.Rounded.Refresh, contentDescription = strings.eqReset)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = strings.close)
                }
            }

            // Auto-switch row
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(strings.audioProfilesTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = outputLabel(activeOutput),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Switch(
                        checked = autoSwitchEnabled,
                        onCheckedChange = onToggleAutoSwitch,
                        modifier = Modifier.testTag("audio_profiles_auto_switch")
                    )
                }
            }

            // Output Type Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AudioOutputProfileType.entries.forEach { type ->
                    val icon = when (type) {
                        AudioOutputProfileType.SPEAKER -> Icons.Rounded.Speaker
                        AudioOutputProfileType.WIRED_HEADSET -> Icons.Rounded.Headphones
                        AudioOutputProfileType.BLUETOOTH -> Icons.Rounded.BluetoothAudio
                    }
                    FilterChip(
                        selected = editingType == type,
                        onClick = { editingType = type },
                        label = { Text(outputLabel(type), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // EQ Preset selector for this output
            Text(strings.equalizerTitle, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(EqPreset.entries.filter { it != EqPreset.CUSTOM }) { preset ->
                    FilterChip(
                        selected = selectedPreset == preset,
                        onClick = { selectedPreset = preset },
                        label = { Text(strings.eqPresetName(preset)) }
                    )
                }
            }

            // Bass boost slider
            Text("${strings.bassBoostLabel}: ${(bassValue * 100).roundToInt()}%", style = MaterialTheme.typography.labelMedium)
            Slider(value = bassValue, onValueChange = { bassValue = it }, valueRange = 0f..1f)

            // 3D Reverb selector
            Text(strings.reverbTitle, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(ReverbEnvironment.entries) { env ->
                    val label = when (env) {
                        ReverbEnvironment.NONE -> strings.reverbNone
                        ReverbEnvironment.STUDIO -> strings.reverbStudio
                        ReverbEnvironment.HALL -> strings.reverbHall
                        ReverbEnvironment.MOSQUE -> strings.reverbMosque
                        ReverbEnvironment.SPATIAL_3D -> strings.reverbSpatial3d
                    }
                    FilterChip(
                        selected = reverbValue == env,
                        onClick = { reverbValue = env },
                        label = { Text(label) }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onSaveProfile(AudioDeviceProfile(editingType, selectedPreset, bassValue, reverbValue))
                        onApplyProfile(editingType)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(strings.themeCodeApply)
                }
                Button(
                    onClick = {
                        onSaveProfile(AudioDeviceProfile(editingType, selectedPreset, bassValue, reverbValue))
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("audio_profile_save_btn")
                ) {
                    Text(strings.save)
                }
            }
        }
    }
}

// ============================================================================
// 9. REAL LOSSLESS MP4 VIDEO TRIMMER DIALOG (MediaExtractor + MediaMuxer)
// ============================================================================

@Composable
fun VideoTrimmerDialog(
    video: VideoItem,
    onDismiss: () -> Unit,
    onTrimmed: (VideoItem) -> Unit,
    onError: (String) -> Unit,
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val scope = rememberCoroutineScope()
    val totalSec = (video.durationMs / 1000f).coerceAtLeast(2f)
    var range by remember(video.id) { mutableStateOf(0f..totalSec) }
    var isTrimming by remember { mutableStateOf(false) }

    val startMs = (range.start * 1000f).toLong().coerceAtLeast(0L)
    val endMs = (range.endInclusive * 1000f).toLong().coerceAtLeast(startMs + 1000L)
    val clipDurationMs = (endMs - startMs).coerceAtLeast(1000L)

    AlertDialog(
        onDismissRequest = { if (!isTrimming) onDismiss() },
        shape = RoundedCornerShape(28.dp),
        icon = { Icon(Icons.Rounded.ContentCut, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(strings.videoTrimmerTitle, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("video_trimmer_dialog"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${strings.videoTrimStartLabel}: ${MediaScanner.formatDuration(startMs)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${strings.videoTrimEndLabel}: ${MediaScanner.formatDuration(endMs)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                RangeSlider(
                    value = range,
                    onValueChange = { updated ->
                        if (updated.endInclusive - updated.start >= 1f) {
                            range = updated
                        }
                    },
                    valueRange = 0f..totalSec,
                    enabled = !isTrimming,
                    modifier = Modifier.testTag("video_trim_range_slider")
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${MediaScanner.formatDuration(clipDurationMs)} • MP4",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (isTrimming) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isTrimming) return@Button
                    isTrimming = true
                    scope.launch {
                        val result = MediaToolsEngine.trimVideoMp4(context, video, startMs, endMs)
                        isTrimming = false
                        val trimmed = result.trimmedVideo
                        if (result.success && trimmed != null) {
                            onTrimmed(trimmed)
                            onDismiss()
                        } else {
                            onError(result.error ?: "Unable to trim this video format")
                        }
                    }
                },
                enabled = !isTrimming,
                shape = CircleShape,
                modifier = Modifier.testTag("video_trim_confirm_btn")
            ) {
                Icon(Icons.Rounded.ContentCut, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(strings.trimVideoAction, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isTrimming) {
                Text(strings.cancel)
            }
        }
    )
}

