package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Podcasts
import androidx.compose.material.icons.rounded.SubdirectoryArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.model.DiscoveredSubfolder
import com.example.model.MediaTab
import com.example.model.WatchedFolder

/**
 * Builds valid Android Storage Access Framework (SAF) initial URIs.
 * Note: Android 11-16 strictly blocks ACTION_OPEN_DOCUMENT_TREE on the root Download folder
 * ("Can't use this folder — To protect your privacy"), whereas ACTION_OPEN_DOCUMENT with
 * EXTRA_INITIAL_URI pointing to primary:Download lets users pick multiple files inside Downloads
 * with ZERO privacy restriction!
 */
object SmartSafFolderNavigator {
    private const val EXTERNAL_STORAGE_AUTHORITY = "com.android.externalstorage.documents"

    const val SUBPATH_MUSIC = "primary:Music"
    const val SUBPATH_MOVIES = "primary:Movies"
    const val SUBPATH_PICTURES = "primary:Pictures"
    const val SUBPATH_DCIM = "primary:DCIM"
    const val SUBPATH_RECORDINGS = "primary:Recordings"
    const val SUBPATH_PODCASTS = "primary:Podcasts"
    const val SUBPATH_DOWNLOADS = "primary:Download"

    fun buildInitialUri(subpath: String): Uri? = runCatching {
        DocumentsContract.buildDocumentUri(EXTERNAL_STORAGE_AUTHORITY, subpath)
    }.getOrNull()

    fun defaultSubpathForTab(tab: MediaTab): String = when (tab) {
        MediaTab.MUSIC -> SUBPATH_MUSIC
        MediaTab.VIDEOS -> SUBPATH_MOVIES
        MediaTab.PHOTOS -> SUBPATH_PICTURES
        MediaTab.SETTINGS -> SUBPATH_MUSIC
    }
}

/**
 * Custom ActivityResultContract for ACTION_OPEN_DOCUMENT + EXTRA_ALLOW_MULTIPLE + EXTRA_INITIAL_URI.
 * Opens directly inside Downloads (or any subpath) so the user can import audio, video, and photo
 * files from Downloads without ever encountering Android's "Can't use this folder" restriction!
 */
class OpenMultipleDocumentsInSubpath :
    ActivityResultContract<Pair<Uri?, Array<String>>, List<Uri>>() {

    override fun createIntent(context: Context, input: Pair<Uri?, Array<String>>): Intent {
        val (initialUri, mimeTypes) = input
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            if (initialUri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri)
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        if (resultCode != Activity.RESULT_OK || intent == null) return emptyList()
        val results = linkedSetOf<Uri>()
        intent.data?.let { results.add(it) }
        val clip = intent.clipData
        if (clip != null) {
            for (i in 0 until clip.itemCount) {
                clip.getItemAt(i)?.uri?.let { results.add(it) }
            }
        }
        return results.toList()
    }
}

/**
 * Famous-Player-Style Selective Folder & Media Import Sheet (100% Google Play Policy Compliant):
 * 1. Pick existing device folders (Music, Movies, Pictures, DCIM, Recordings, Podcasts, or any folder).
 * 2. See all discovered existing folders & subfolders with interactive Material 3 Checkboxes + file counts.
 * 3. Choose optional media types (Music / Videos / Photos) and tap "Confirm & Scan Selected (N)" —
 *    Prism strictly scans ONLY the checked folders and NEVER scans beyond what the user confirmed!
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SmartMediaImportSheet(
    activeTab: MediaTab,
    strings: AppStrings,
    discoveredFolders: List<DiscoveredSubfolder> = emptyList(),
    watchedFolders: List<WatchedFolder> = emptyList(),
    isInspectingFolders: Boolean = false,
    onDismiss: () -> Unit,
    onLaunchFolderWithSubpath: (String) -> Unit,
    onLaunchDirectFilesPicker: () -> Unit,
    onLaunchDownloadsFilesPicker: () -> Unit = onLaunchDirectFilesPicker,
    onLaunchVisualGalleryPicker: () -> Unit,
    onConfirmSelectedFolders: (List<DiscoveredSubfolder>, Boolean, Boolean, Boolean) -> Unit = { _, _, _, _ -> },
) {
    val cs = MaterialTheme.colorScheme
    val checkedKeys = remember { mutableStateMapOf<String, Boolean>() }
    var includeAudio by remember { mutableStateOf(true) }
    var includeVideo by remember { mutableStateOf(true) }
    var includePhoto by remember { mutableStateOf(true) }

    // Synchronize newly discovered folders into the checkbox map:
    // - If a tree was already saved with specific selectedSubfolderDocIds, restore those exact checks.
    // - If a folder was just picked for the first time, pre-check its nodes that contain media (or root) so user can adjust & confirm.
    LaunchedEffect(discoveredFolders, watchedFolders) {
        val savedByUri = watchedFolders.associateBy { it.uriString }
        val grouped = discoveredFolders.groupBy { it.treeUriString }
        grouped.forEach { (treeUri, nodes) ->
            val saved = savedByUri[treeUri]
            val savedIds = saved?.selectedSubfolderDocIds?.toSet().orEmpty()
            nodes.forEach { node ->
                if (node.selectionKey !in checkedKeys) {
                    checkedKeys[node.selectionKey] = when {
                        saved != null && !saved.enabled -> false
                        savedIds.isNotEmpty() -> node.docId in savedIds
                        else -> node.isRoot || node.directMediaCount > 0
                    }
                }
            }
        }
    }

    val selectedNodes = discoveredFolders.filter { checkedKeys[it.selectionKey] == true }
    val hasMediaType = includeAudio || includeVideo || includePhoto

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = cs.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("smart_media_import_sheet"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Sheet Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = cs.primaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.FolderOpen,
                            contentDescription = null,
                            tint = cs.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.selectiveFolderChecklistTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = strings.selectiveFolderChecklistHint,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // STEP 1: Pick Existing Device Folders (Opens directly inside existing folders — never asks to create one)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = cs.surfaceContainer,
                border = BorderStroke(1.dp, cs.outlineVariant.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = strings.smartImportQuickFoldersLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = cs.primary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SmartFolderChip(
                            icon = Icons.Rounded.MusicNote,
                            label = strings.tabMusic,
                            highlighted = activeTab == MediaTab.MUSIC,
                            tag = "smart_folder_music",
                            onClick = { onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_MUSIC) }
                        )
                        SmartFolderChip(
                            icon = Icons.Rounded.Movie,
                            label = strings.tabVideos,
                            highlighted = activeTab == MediaTab.VIDEOS,
                            tag = "smart_folder_movies",
                            onClick = { onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_MOVIES) }
                        )
                        SmartFolderChip(
                            icon = Icons.Rounded.PhotoLibrary,
                            label = strings.tabPhotos,
                            highlighted = activeTab == MediaTab.PHOTOS,
                            tag = "smart_folder_pictures",
                            onClick = { onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_PICTURES) }
                        )
                        SmartFolderChip(
                            icon = Icons.Rounded.CameraAlt,
                            label = "DCIM",
                            highlighted = false,
                            tag = "smart_folder_dcim",
                            onClick = { onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_DCIM) }
                        )
                        SmartFolderChip(
                            icon = Icons.Rounded.Mic,
                            label = "Recordings",
                            highlighted = false,
                            tag = "smart_folder_recordings",
                            onClick = { onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_RECORDINGS) }
                        )
                        SmartFolderChip(
                            icon = Icons.Rounded.Podcasts,
                            label = "Podcasts",
                            highlighted = false,
                            tag = "smart_folder_podcasts",
                            onClick = { onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_PODCASTS) }
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            onLaunchFolderWithSubpath(SmartSafFolderNavigator.defaultSubpathForTab(activeTab))
                        },
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("smart_import_other_folder")
                    ) {
                        Icon(Icons.Rounded.CreateNewFolder, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = strings.smartImportBrowseOther,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // STEP 2: Interactive Checkbox Checklist of Existing Granted Folders & Subfolders
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = cs.surfaceContainer,
                border = BorderStroke(
                    width = if (selectedNodes.isNotEmpty()) 1.5.dp else 1.dp,
                    color = if (selectedNodes.isNotEmpty()) cs.primary.copy(alpha = 0.5f)
                    else cs.outlineVariant.copy(alpha = 0.45f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("selective_folder_checklist_card")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = cs.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = strings.selectiveFolderChecklistTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (discoveredFolders.isNotEmpty()) {
                            val allSelected = selectedNodes.size == discoveredFolders.size
                            TextButton(
                                onClick = {
                                    val next = !allSelected
                                    discoveredFolders.forEach { checkedKeys[it.selectionKey] = next }
                                },
                                modifier = Modifier.testTag("selective_toggle_all_btn")
                            ) {
                                Text(
                                    text = if (allSelected) strings.selectiveClearAll else strings.selectiveSelectAll,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    if (isInspectingFolders) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = strings.selectiveInspectingFoldersLabel,
                                style = MaterialTheme.typography.bodySmall,
                                color = cs.onSurfaceVariant
                            )
                        }
                    } else if (discoveredFolders.isEmpty()) {
                        Text(
                            text = strings.selectiveEmptyChecklistHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = cs.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        // Media Type Scope Chips (strictly scan only what the user wants)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = strings.selectiveMediaTypesLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = cs.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = includeAudio,
                                    onClick = { includeAudio = !includeAudio },
                                    label = { Text(strings.tabMusic, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("selective_type_audio")
                                )
                                FilterChip(
                                    selected = includeVideo,
                                    onClick = { includeVideo = !includeVideo },
                                    label = { Text(strings.tabVideos, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.Movie, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("selective_type_video")
                                )
                                FilterChip(
                                    selected = includePhoto,
                                    onClick = { includePhoto = !includePhoto },
                                    label = { Text(strings.tabPhotos, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("selective_type_photo")
                                )
                            }
                        }

                        HorizontalDivider(color = cs.outlineVariant.copy(alpha = 0.35f))

                        // Discovered Existing Folders & Subfolders with Material 3 Checkboxes
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            discoveredFolders.forEachIndexed { idx, node ->
                                val isChecked = checkedKeys[node.selectionKey] == true
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isChecked) cs.primaryContainer.copy(alpha = 0.45f)
                                    else cs.surfaceContainerLow,
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isChecked) cs.primary.copy(alpha = 0.45f)
                                        else cs.outlineVariant.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = if (node.isRoot) 0.dp else 14.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { checkedKeys[node.selectionKey] = !isChecked }
                                        .testTag("selective_folder_row_$idx")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checkedKeys[node.selectionKey] = it },
                                            modifier = Modifier.testTag("selective_folder_checkbox_$idx")
                                        )
                                        Icon(
                                            imageVector = if (node.isRoot) Icons.Rounded.Folder else Icons.Rounded.SubdirectoryArrowRight,
                                            contentDescription = null,
                                            tint = if (isChecked) cs.primary else cs.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = node.displayName,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = node.parentPath,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = cs.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Surface(
                                            shape = CircleShape,
                                            color = if (node.directMediaCount > 0) cs.secondaryContainer
                                            else cs.surfaceContainerHigh
                                        ) {
                                            Text(
                                                text = strings.selectiveDirectFilesBadge(node.directMediaCount),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (node.directMediaCount > 0) cs.onSecondaryContainer
                                                else cs.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = strings.selectiveScanningOnlyCheckedHint,
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.primary
                        )

                        Button(
                            onClick = {
                                onConfirmSelectedFolders(
                                    selectedNodes,
                                    includeAudio,
                                    includeVideo,
                                    includePhoto
                                )
                                onDismiss()
                            },
                            enabled = selectedNodes.isNotEmpty() && hasMediaType,
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("selective_confirm_scan_btn")
                        ) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = strings.selectiveConfirmScanBtn(selectedNodes.size),
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // STEP 3: Direct Files & Downloads + Zero-Permission Gallery Picker (bypasses Android Download root lock!)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = cs.surfaceContainer,
                border = BorderStroke(1.dp, cs.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            onDismiss()
                            onLaunchDownloadsFilesPicker()
                        },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("smart_import_downloads_direct")
                    ) {
                        Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = strings.importDownloadsDirectAction,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onLaunchDirectFilesPicker()
                            },
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("smart_import_direct_files")
                        ) {
                            Icon(Icons.Rounded.AudioFile, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(strings.importFiles, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onLaunchVisualGalleryPicker()
                            },
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("smart_import_gallery_picker")
                        ) {
                            Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(strings.pickGalleryMedia, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = cs.secondary,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .size(16.dp)
                        )
                        Text(
                            text = strings.importDownloadsPrivacyExplainer,
                            style = MaterialTheme.typography.labelSmall,
                            color = cs.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SmartFolderChip(
    icon: ImageVector,
    label: String,
    highlighted: Boolean,
    tag: String,
    onClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (highlighted) cs.primaryContainer else cs.surfaceContainerHigh,
        border = BorderStroke(
            width = if (highlighted) 1.5.dp else 1.dp,
            color = if (highlighted) cs.primary else cs.outlineVariant.copy(alpha = 0.45f)
        ),
        modifier = Modifier.testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlighted) cs.onPrimaryContainer else cs.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (highlighted) cs.onPrimaryContainer else cs.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
