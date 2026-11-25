package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.model.MediaTab

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
 * Zero-friction, 100% Google Play compliant Media & Folder Import Sheet.
 * Puts Direct Downloads File Import first (zero Android SAF restriction), alongside Photo Picker
 * and allowed Auto-Sync folder deep-jumps (Music, Movies, Pictures, DCIM, Recordings).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SmartMediaImportSheet(
    activeTab: MediaTab,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onLaunchFolderWithSubpath: (String) -> Unit,
    onLaunchDirectFilesPicker: () -> Unit,
    onLaunchDownloadsFilesPicker: () -> Unit = onLaunchDirectFilesPicker,
    onLaunchVisualGalleryPicker: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = cs.primaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FolderOpen,
                        contentDescription = null,
                        tint = cs.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.smartImportTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = strings.smartImportSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                }
            }

            // HERO #1: Direct Downloads Multi-File Import (100% bypasses Android's "Can't use Download folder" lock!)
            Button(
                onClick = {
                    onDismiss()
                    onLaunchDownloadsFilesPicker()
                },
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("smart_import_downloads_direct")
            ) {
                Icon(Icons.Rounded.Download, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = strings.importDownloadsDirectAction,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // HERO #2: Direct Multi-File Anywhere + Zero-Permission Gallery Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
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

                FilledTonalButton(
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

            // Clear Android Privacy Explanation (Why Download root shows "Can't use this folder")
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cs.secondaryContainer.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, cs.secondary.copy(alpha = 0.28f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = cs.secondary,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(18.dp)
                    )
                    Text(
                        text = strings.importDownloadsPrivacyExplainer,
                        style = MaterialTheme.typography.labelMedium,
                        color = cs.onSecondaryContainer
                    )
                }
            }

            // Quick Allowed Auto-Sync Subfolder Shortcuts (Never blocked by Android SAF!)
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
                    onClick = {
                        onDismiss()
                        onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_MUSIC)
                    }
                )
                SmartFolderChip(
                    icon = Icons.Rounded.Movie,
                    label = strings.tabVideos,
                    highlighted = activeTab == MediaTab.VIDEOS,
                    tag = "smart_folder_movies",
                    onClick = {
                        onDismiss()
                        onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_MOVIES)
                    }
                )
                SmartFolderChip(
                    icon = Icons.Rounded.PhotoLibrary,
                    label = strings.tabPhotos,
                    highlighted = activeTab == MediaTab.PHOTOS,
                    tag = "smart_folder_pictures",
                    onClick = {
                        onDismiss()
                        onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_PICTURES)
                    }
                )
                SmartFolderChip(
                    icon = Icons.Rounded.CameraAlt,
                    label = "DCIM / Camera",
                    highlighted = false,
                    tag = "smart_folder_dcim",
                    onClick = {
                        onDismiss()
                        onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_DCIM)
                    }
                )
                SmartFolderChip(
                    icon = Icons.Rounded.Mic,
                    label = "Recordings",
                    highlighted = false,
                    tag = "smart_folder_recordings",
                    onClick = {
                        onDismiss()
                        onLaunchFolderWithSubpath(SmartSafFolderNavigator.SUBPATH_RECORDINGS)
                    }
                )
            }

            OutlinedButton(
                onClick = {
                    onDismiss()
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
                Text(strings.smartImportBrowseOther, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                maxLines = 1
            )
        }
    }
}
