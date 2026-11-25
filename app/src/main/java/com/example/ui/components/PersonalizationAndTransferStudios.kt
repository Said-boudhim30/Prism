package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.DecodedThemePreset
import com.example.data.ThemeCodeCodec
import com.example.data.WifiTransferStatus
import com.example.localization.AppStrings
import com.example.model.AppSettings
import com.example.model.FolderCustomization
import com.example.ui.theme.SeedColorSchemeGenerator
import kotlinx.coroutines.launch

/**
 * 9) Custom Monet Seed Color Picker & Photo Color Extractor Dialog.
 */
@Composable
fun CustomSeedColorDialog(
    currentSeedArgb: Int?,
    strings: AppStrings,
    onSelectSeedArgb: (Int?) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val initHsv = remember(currentSeedArgb) {
        FloatArray(3).also {
            android.graphics.Color.colorToHSV(currentSeedArgb ?: Color(0xFF0F6B41).toArgb(), it)
        }
    }
    var hue by remember { mutableFloatStateOf(initHsv[0]) }
    var sat by remember { mutableFloatStateOf(initHsv[1].coerceIn(0.35f, 0.95f)) }
    val previewColor = remember(hue, sat) {
        Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, sat, 0.78f)))
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val extracted = SeedColorSchemeGenerator.extractSeedFromPhotoUri(context, uri)
                if (extracted != null) {
                    val hsv = FloatArray(3)
                    android.graphics.Color.colorToHSV(extracted, hsv)
                    hue = hsv[0]
                    sat = hsv[1].coerceIn(0.35f, 0.95f)
                }
            }
        }
    }

    val quickSwatches = remember {
        listOf(
            Color(0xFF006D39), Color(0xFF006497), Color(0xFF6750A4), Color(0xFF9C4332),
            Color(0xFFB45309), Color(0xFF0E7490), Color(0xFFBE185D), Color(0xFF4338CA),
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("custom_seed_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.ColorLens, contentDescription = null, tint = previewColor)
                Column {
                    Text(strings.customSeedTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.customSeedSubtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Live Preview Banner
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = previewColor,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "#${Integer.toHexString(previewColor.toArgb()).uppercase().takeLast(6)}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Quick Material You Swatches
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    quickSwatches.forEach { swatch ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(swatch)
                                .clickable {
                                    val hsv = FloatArray(3)
                                    android.graphics.Color.colorToHSV(swatch.toArgb(), hsv)
                                    hue = hsv[0]
                                    sat = hsv[1].coerceIn(0.35f, 0.95f)
                                }
                        )
                    }
                }

                Text("Hue (${hue.toInt()}°)", style = MaterialTheme.typography.labelMedium)
                Slider(value = hue, onValueChange = { hue = it }, valueRange = 0f..360f)

                Text("Vibrancy (${(sat * 100).toInt()}%)", style = MaterialTheme.typography.labelMedium)
                Slider(value = sat, onValueChange = { sat = it }, valueRange = 0.25f..1f)

                FilledTonalButton(
                    onClick = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    modifier = Modifier.fillMaxWidth().testTag("seed_extract_photo_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(strings.customSeedFromPhoto)
                }

                if (currentSeedArgb != null) {
                    OutlinedButton(
                        onClick = {
                            onSelectSeedArgb(null)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(strings.customSeedClear)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSelectSeedArgb(previewColor.toArgb())
                    onDismiss()
                },
                modifier = Modifier.testTag("seed_apply_btn")
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}

/**
 * 10) Shareable Theme Code Export/Import Sheet.
 */
@Composable
fun ThemeCodeDialog(
    settings: AppSettings,
    strings: AppStrings,
    onApplyThemePreset: (DecodedThemePreset) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val currentCode = remember(settings) { ThemeCodeCodec.encodeThemeCode(settings) }
    var importText by remember { mutableStateOf("") }
    var statusMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("theme_code_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(strings.themeCodeTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.themeCodeSubtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        currentCode,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 3,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    FilledTonalButton(
                        onClick = {
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("Prism Theme Code", currentCode))
                            statusMsg = "✓ Copied!"
                        },
                        modifier = Modifier.weight(1f).testTag("theme_code_copy_btn")
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(strings.themeCodeCopy, maxLines = 1)
                    }
                    OutlinedButton(
                        onClick = {
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, currentCode)
                            }
                            runCatching { context.startActivity(Intent.createChooser(share, strings.menuShare)) }
                        }
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = strings.menuShare, modifier = Modifier.size(18.dp))
                    }
                }

                OutlinedTextField(
                    value = importText,
                    onValueChange = { importText = it },
                    label = { Text(strings.themeCodeApply) },
                    placeholder = { Text("PRISM-THEME:...") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                if (statusMsg != null) {
                    Text(statusMsg!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = importText.isNotBlank(),
                onClick = {
                    val decoded = ThemeCodeCodec.decodeThemeCode(importText)
                    if (decoded != null) {
                        onApplyThemePreset(decoded)
                        onDismiss()
                    } else {
                        statusMsg = "Invalid PRISM-THEME code"
                    }
                },
                modifier = Modifier.testTag("theme_code_import_btn")
            ) {
                Text(strings.themeCodeApply)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.close) }
        }
    )
}

/**
 * 11) Local Wi-Fi File Transfer Dialog.
 */
@Composable
fun WifiTransferDialog(
    status: WifiTransferStatus,
    strings: AppStrings,
    onStartServer: () -> Unit,
    onStopServer: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("wifi_transfer_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Wifi, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(strings.wifiTransferTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.wifiTransferSubtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (status.isRunning && status.url.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                status.url,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "${status.lastActivity} • ${status.transferredCount} files",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                            FilledTonalButton(
                                onClick = {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("Prism Wi-Fi URL", status.url))
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(status.url)
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            strings.wifiTransferSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                Button(
                    onClick = { if (status.isRunning) onStopServer() else onStartServer() },
                    modifier = Modifier.fillMaxWidth().testTag("wifi_transfer_toggle_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(if (status.isRunning) strings.wifiTransferStop else strings.wifiTransferStart)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(strings.close) }
        }
    )
}

/**
 * 12) Folder Pinning & Custom Cover Tint Dialog.
 */
@Composable
fun FolderCustomizeDialog(
    folderName: String,
    existing: FolderCustomization?,
    strings: AppStrings,
    onSave: (FolderCustomization) -> Unit,
    onDismiss: () -> Unit,
) {
    var pinned by remember(existing) { mutableStateOf(existing?.pinned ?: false) }
    var selectedColor by remember(existing) { mutableStateOf(existing?.accentArgb) }
    var badge by remember(existing) { mutableStateOf(existing?.badgeSymbol.orEmpty()) }
    val swatches = remember {
        listOf(
            null,
            Color(0xFF006D39).toArgb(),
            Color(0xFF006497).toArgb(),
            Color(0xFF6750A4).toArgb(),
            Color(0xFF9C4332).toArgb(),
            Color(0xFFB45309).toArgb(),
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("folder_customize_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.FolderSpecial, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("${strings.folderCustomizeTitle}: $folderName", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.PushPin, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(if (pinned) strings.folderPinLabel else strings.folderUnpinLabel, fontWeight = FontWeight.SemiBold)
                    }
                    Switch(checked = pinned, onCheckedChange = { pinned = it })
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    swatches.forEach { argb ->
                        val isSel = selectedColor == argb
                        Surface(
                            shape = CircleShape,
                            color = argb?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceContainerHighest,
                            border = BorderStroke(if (isSel) 3.dp else 1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.size(34.dp).clickable { selectedColor = argb }
                        ) {}
                    }
                }

                OutlinedTextField(
                    value = badge,
                    onValueChange = { badge = it.take(4) },
                    singleLine = true,
                    label = { Text("Badge (e.g. ★ / HQ / Q)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(FolderCustomization(folderName, pinned, selectedColor, badge.trim()))
                    onDismiss()
                }
            ) {
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        }
    )
}
