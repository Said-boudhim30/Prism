package com.example.ui.components

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.MediaScanner
import com.example.data.PhotoExifStudioHelper
import com.example.localization.AppStrings
import com.example.model.CollageLayoutMode
import com.example.model.ListeningStatsSnapshot
import com.example.model.PhotoItem
import com.example.model.RenameMediaType
import com.example.model.VaultItem
import com.example.security.PrivateVaultManager
import kotlinx.coroutines.launch

/**
 * 5) Listening Stats & Monthly Recap Dashboard Sheet.
 */
@Composable
fun ListeningStatsSheet(
    stats: ListeningStatsSnapshot,
    strings: AppStrings,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("listening_stats_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(strings.listeningStatsTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.listeningStatsSubtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        StatMetricCard(strings.totalPlaysLabel, "${stats.totalPlays}", Modifier.weight(1f))
                        StatMetricCard(strings.totalMinutesLabel, "${stats.totalMinutes}", Modifier.weight(1f))
                    }
                }

                // 7-day listening activity bar chart
                if (stats.dailyMinutes.isNotEmpty()) {
                    item {
                        val barColor = MaterialTheme.colorScheme.primary
                        val trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Canvas(modifier = Modifier.fillMaxWidth().height(84.dp)) {
                                    val maxVal = (stats.dailyMinutes.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
                                    val count = stats.dailyMinutes.size.coerceAtLeast(1)
                                    val slotW = size.width / count
                                    val barW = (slotW * 0.55f).coerceAtLeast(10f)
                                    stats.dailyMinutes.forEachIndexed { idx, (_, mins) ->
                                        val left = idx * slotW + (slotW - barW) / 2f
                                        drawRoundRect(
                                            color = trackColor,
                                            topLeft = Offset(left, 0f),
                                            size = Size(barW, size.height),
                                            cornerRadius = CornerRadius(10f, 10f)
                                        )
                                        val h = (size.height * (mins.toFloat() / maxVal.toFloat())).coerceAtLeast(6f)
                                        drawRoundRect(
                                            color = barColor,
                                            topLeft = Offset(left, size.height - h),
                                            size = Size(barW, h),
                                            cornerRadius = CornerRadius(10f, 10f)
                                        )
                                    }
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                    stats.dailyMinutes.forEach { (day, _) ->
                                        Text(day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                // Format Breakdown Pills
                if (stats.formatCounts.isNotEmpty()) {
                    item {
                        Text(strings.formatBreakdownLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            stats.formatCounts.entries.take(5).forEach { (ext, count) ->
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                                    Text(
                                        "$ext: $count",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Top Artists / Reciters
                if (stats.topArtists.isNotEmpty()) {
                    item {
                        Text(strings.topArtistsLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }
                    items(stats.topArtists.take(5)) { (artist, plays) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(artist, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            Text("$plays×", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                // Top Tracks
                if (stats.topTracks.isNotEmpty()) {
                    item {
                        Text(strings.topTracksLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }
                    items(stats.topTracks.take(5)) { (track, plays) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(track.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                            Text("$plays×", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(strings.close) }
        }
    )
}

@Composable
private fun StatMetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * 6) Private AES-256 Encrypted Vault Sheet.
 */
@Composable
fun PrivateVaultSheet(
    vaultItems: List<VaultItem>,
    strings: AppStrings,
    onImportUri: (android.net.Uri) -> Unit,
    onRestoreItem: (VaultItem) -> Unit,
    onDeleteItem: (VaultItem) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) onImportUri(uri)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("private_vault_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(strings.privateVaultTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.privateVaultLockedHint, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton(
                    onClick = {
                        mediaPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                    },
                    modifier = Modifier.fillMaxWidth().testTag("vault_add_file_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(strings.privateVaultAddBtn)
                }

                if (vaultItems.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                            Text(strings.privateVaultEmpty, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(vaultItems, key = { it.id }) { item ->
                            var previewBmp by remember(item.id) { mutableStateOf<Bitmap?>(null) }
                            LaunchedEffect(item.id) {
                                if (item.mediaType == RenameMediaType.PHOTO) {
                                    previewBmp = PrivateVaultManager.decryptPhotoBitmap(context, item)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (previewBmp != null) {
                                            Image(
                                                bitmap = previewBmp!!.asImageBitmap(),
                                                contentDescription = item.originalTitle,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.size(48.dp)
                                            )
                                        } else {
                                            Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.originalTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(MediaScanner.formatFileSize(item.sizeBytes), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    IconButton(onClick = { onRestoreItem(item) }) {
                                        Icon(Icons.Filled.Restore, contentDescription = strings.privateVaultRestoreBtn, tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { onDeleteItem(item) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = strings.deleteAction, tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(strings.close) }
        }
    )
}

/**
 * 7) HD Photo Collage & Custom Watermark Studio Dialog.
 */
@Composable
fun PhotoCollageStudioDialog(
    photos: List<PhotoItem>,
    initialSelectedIds: Set<String> = emptySet(),
    strings: AppStrings,
    onCollageSaved: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pickedIds by remember(initialSelectedIds, photos) {
        val seed = initialSelectedIds.take(4).toSet()
        mutableStateOf(if (seed.isNotEmpty()) seed else photos.take(2).map { it.id }.toSet())
    }
    var layout by remember { mutableStateOf(CollageLayoutMode.GRID_2X2) }
    var watermark by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("collage_studio_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.GridView, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(strings.collageStudioTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.collageStudioSubtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        CollageLayoutMode.SIDE_BY_SIDE to "1×2",
                        CollageLayoutMode.STACKED_2 to "2×1",
                        CollageLayoutMode.HERO_TRIO to "1+2",
                        CollageLayoutMode.GRID_2X2 to "2×2",
                    ).forEach { (mode, label) ->
                        FilterChip(
                            selected = layout == mode,
                            onClick = { layout = mode },
                            label = { Text(label) }
                        )
                    }
                }

                OutlinedTextField(
                    value = watermark,
                    onValueChange = { watermark = it },
                    singleLine = true,
                    placeholder = { Text(strings.collageWatermarkHint) },
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(photos.take(24), key = { it.id }) { photo ->
                        val selected = photo.id in pickedIds
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.fillMaxWidth().clickable {
                                pickedIds = when {
                                    selected -> pickedIds - photo.id
                                    pickedIds.size < 4 -> pickedIds + photo.id
                                    else -> pickedIds
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp))) {
                                    if (photo.resId != null) {
                                        Image(painterResource(photo.resId), contentDescription = photo.title, contentScale = ContentScale.Crop, modifier = Modifier.size(38.dp))
                                    } else {
                                        AsyncImage(model = photo.uriString, contentDescription = photo.title, contentScale = ContentScale.Crop, modifier = Modifier.size(38.dp))
                                    }
                                }
                                Text(photo.title, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                if (selected) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = pickedIds.isNotEmpty() && !saving,
                onClick = {
                    saving = true
                    val selectedPhotos = photos.filter { it.id in pickedIds }
                    scope.launch {
                        val ok = PhotoExifStudioHelper.createCollageAndSave(context, selectedPhotos, layout, watermark)
                        saving = false
                        onCollageSaved(ok)
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("collage_save_btn")
            ) {
                if (saving) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                else Text(strings.collageSaveBtn)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

/**
 * 8) Batch Audio Tag & Cover Art Editor Dialog.
 */
@Composable
fun BatchTagEditorDialog(
    selectedCount: Int,
    strings: AppStrings,
    onApply: (artist: String?, album: String?, artworkUri: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var artist by remember { mutableStateOf("") }
    var album by remember { mutableStateOf("") }
    var coverUri by remember { mutableStateOf<String?>(null) }

    val coverPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) coverUri = uri.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("batch_tag_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text("${strings.batchTagTitle} ($selectedCount)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.batchTagSubtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    singleLine = true,
                    label = { Text(strings.tagArtistLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = album,
                    onValueChange = { album = it },
                    singleLine = true,
                    label = { Text(strings.tagAlbumLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
                FilledTonalButton(
                    onClick = { coverPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (coverUri != null) "✓ ${strings.customSeedFromPhoto}" else strings.customSeedFromPhoto)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = artist.isNotBlank() || album.isNotBlank() || !coverUri.isNullOrBlank(),
                onClick = {
                    onApply(artist.takeIf { it.isNotBlank() }, album.takeIf { it.isNotBlank() }, coverUri)
                    onDismiss()
                },
                modifier = Modifier.testTag("batch_tag_apply_btn")
            ) {
                Text(strings.batchTagApply)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}
