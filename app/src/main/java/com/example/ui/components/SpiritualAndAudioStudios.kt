package com.example.ui.components

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AccessAlarm
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MediaScanner
import com.example.data.QuranHadithCatalog
import com.example.localization.AppStrings
import com.example.model.AudioTrack
import com.example.model.MorningAlarmConfig
import com.example.model.QuranKhatmaProgress
import com.example.player.AudioCutterEngine
import com.example.player.AudioTrimResult
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * 1) Quran Mushaf Listening & Khatma Tracker Sheet.
 */
@Composable
fun QuranMushafKhatmaSheet(
    progress: QuranKhatmaProgress,
    khushuModeEnabled: Boolean,
    audioTracks: List<AudioTrack>,
    strings: AppStrings,
    onToggleSurahCompleted: (Int) -> Unit,
    onSetHifzRepeat: (Int) -> Unit,
    onToggleKhushuMode: (Boolean) -> Unit,
    onPlayMatchingTrack: (AudioTrack) -> Unit,
    onOpenTasbih: () -> Unit,
    onDismiss: () -> Unit,
) {
    var search by remember { mutableStateOf("") }
    val filteredSurahs = remember(search) {
        val q = search.trim()
        if (q.isBlank()) QuranHadithCatalog.surahs
        else QuranHadithCatalog.surahs.filter {
            it.arabicName.contains(q, ignoreCase = true) ||
                it.englishName.contains(q, ignoreCase = true) ||
                it.number.toString() == q
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("quran_khatma_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column(modifier = Modifier.weight(1f)) {
                    Text(strings.quranHubTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.quranHubSubtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Khatma Progress Banner
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(strings.quranKhatmaProgressLabel, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${progress.completedSurahs.size}/114 (${progress.completionPercent}%)",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (progress.completedSurahs.size / 114f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(strings.khushuModeTitle, style = MaterialTheme.typography.labelLarge)
                            Switch(
                                checked = khushuModeEnabled,
                                onCheckedChange = onToggleKhushuMode,
                                modifier = Modifier.testTag("khushu_mode_switch")
                            )
                        }
                    }
                }

                // Hifz Repeat Target Chips
                Text(strings.quranHifzRepeatLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(3 to "3×", 5 to "5×", 10 to "10×", 0 to "∞").forEach { (count, label) ->
                        FilterChip(
                            selected = progress.hifzRepeatTarget == count,
                            onClick = { onSetHifzRepeat(count) },
                            label = { Text(label) }
                        )
                    }
                }

                // Quick Tasbih shortcut
                FilledTonalButton(
                    onClick = onOpenTasbih,
                    modifier = Modifier.fillMaxWidth().testTag("open_tasbih_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(strings.tasbihTitle)
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    singleLine = true,
                    placeholder = { Text("1..114 / الفاتحة / Al-Kahf") },
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 260.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredSurahs, key = { it.number }) { surah ->
                        val isDone = surah.number in progress.completedSurahs
                        val matchingTrack = remember(audioTracks, surah.number) {
                            audioTracks.firstOrNull { t ->
                                val hay = QuranHadithCatalog.normalizeArabic("${t.title} ${t.album}")
                                hay.contains(QuranHadithCatalog.normalizeArabic(surah.arabicName)) ||
                                    t.title.contains(surah.englishName, ignoreCase = true)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isDone) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                            else MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.fillMaxWidth().clickable { onToggleSurahCompleted(surah.number) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${surah.number}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "${surah.arabicName} • ${surah.englishName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "${surah.ayahCount} آية • ${if (surah.isMeccan) "مكية" else "مدنية"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (matchingTrack != null) {
                                    IconButton(onClick = {
                                        onToggleKhushuMode(true)
                                        onPlayMatchingTrack(matchingTrack)
                                        onDismiss()
                                    }) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = strings.menuPlayNow, tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Icon(
                                    imageVector = if (isDone) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = strings.quranMarkSurahDone,
                                    tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
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
 * 2) Adhkar & Tasbih Counter Dialog (Bonus Spiritual Companion).
 */
@Composable
fun TasbihCounterDialog(
    dailyCount: Int,
    strings: AppStrings,
    onIncrementDaily: (Int) -> Unit,
    onResetDaily: () -> Unit,
    onDismiss: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val presets = remember {
        listOf(
            "سبحان الله" to 33,
            "الحمد لله" to 33,
            "الله أكبر" to 33,
            "لا إله إلا الله وحده لا شريك له" to 100,
            "أستغفر الله العظيم وأتوب إليه" to 100,
            "اللهم صلّ وسلّم على نبينا محمد" to 100,
        )
    }
    var selectedIdx by remember { mutableIntStateOf(0) }
    var sessionCount by remember { mutableIntStateOf(0) }
    val (dhikrText, target) = presets[selectedIdx]

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("tasbih_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(strings.tasbihTitle, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${strings.tasbihTodayLabel}: $dailyCount", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = {
                        sessionCount = 0
                        onResetDaily()
                    }) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(strings.tasbihReset)
                    }
                }

                // Dhikr Selector Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    presets.chunked(2).forEachIndexed { rowIdx, pair ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            pair.forEachIndexed { colIdx, (text, _) ->
                                val idx = rowIdx * 2 + colIdx
                                FilterChip(
                                    selected = selectedIdx == idx,
                                    onClick = {
                                        selectedIdx = idx
                                        sessionCount = 0
                                    },
                                    label = { Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Giant Tactile Tasbih Orb
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .size(168.dp)
                        .testTag("tasbih_tap_orb")
                        .clickable {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            sessionCount = if (sessionCount >= target) 1 else sessionCount + 1
                            onIncrementDaily(dailyCount + 1)
                        }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Text(
                            dhikrText,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "$sessionCount / $target",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
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
 * 3) Morning Quran / Calm Nasheed Alarm Scheduler Sheet.
 */
@Composable
fun MorningAlarmSheet(
    config: MorningAlarmConfig,
    audioTracks: List<AudioTrack>,
    strings: AppStrings,
    onSaveConfig: (MorningAlarmConfig) -> Unit,
    onPreviewFadeIn: (AudioTrack, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var enabled by remember(config) { mutableStateOf(config.enabled) }
    var hour by remember(config) { mutableIntStateOf(config.hour) }
    var minute by remember(config) { mutableIntStateOf(config.minute) }
    var fadeSec by remember(config) { mutableIntStateOf(config.fadeInSeconds) }
    var repeatDaily by remember(config) { mutableStateOf(config.repeatDaily) }
    var selectedTrackId by remember(config) {
        mutableStateOf(config.trackId ?: audioTracks.firstOrNull()?.id)
    }
    val selectedTrack = audioTracks.firstOrNull { it.id == selectedTrackId } ?: audioTracks.firstOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("morning_alarm_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.AccessAlarm, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(strings.morningAlarmTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(strings.morningAlarmSubtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(strings.morningAlarmTitle, fontWeight = FontWeight.SemiBold)
                    Switch(checked = enabled, onCheckedChange = { enabled = it }, modifier = Modifier.testTag("alarm_enable_switch"))
                }

                // Time Picker Stepper
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { hour = (hour + 1) % 24 }) { Icon(Icons.Filled.Add, contentDescription = "+1h") }
                            Text(String.format("%02d", hour), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { hour = (hour + 23) % 24 }) { Icon(Icons.Filled.Remove, contentDescription = "-1h") }
                        }
                        Text(":", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(onClick = { minute = (minute + 5) % 60 }) { Icon(Icons.Filled.Add, contentDescription = "+5m") }
                            Text(String.format("%02d", minute), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { minute = (minute + 55) % 60 }) { Icon(Icons.Filled.Remove, contentDescription = "-5m") }
                        }
                    }
                }

                // Gentle Fade-In Chips
                Text("${strings.morningAlarmFadeLabel}: ${fadeSec}s", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(15, 30, 45, 60).forEach { sec ->
                        FilterChip(
                            selected = fadeSec == sec,
                            onClick = { fadeSec = sec },
                            label = { Text("${sec}s") }
                        )
                    }
                }

                // Track Picker
                if (audioTracks.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(audioTracks.take(20), key = { it.id }) { track ->
                            val isSel = track.id == selectedTrack?.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                                modifier = Modifier.fillMaxWidth().clickable { selectedTrackId = track.id }
                            ) {
                                Text(
                                    "${track.title} — ${track.artist}",
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                if (selectedTrack != null) {
                    OutlinedButton(
                        onClick = { onPreviewFadeIn(selectedTrack, fadeSec) },
                        modifier = Modifier.fillMaxWidth().testTag("alarm_preview_btn")
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.morningAlarmTestNow)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveConfig(
                        MorningAlarmConfig(
                            enabled = enabled,
                            hour = hour,
                            minute = minute,
                            fadeInSeconds = fadeSec,
                            trackId = selectedTrack?.id,
                            trackTitle = selectedTrack?.title.orEmpty(),
                            repeatDaily = repeatDaily,
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier.testTag("alarm_save_btn")
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
 * 4) Audio Trimmer & Ringtone Cutter Studio Sheet.
 */
@Composable
fun AudioCutterSheet(
    track: AudioTrack,
    strings: AppStrings,
    onPreviewRange: (Long) -> Unit,
    onTrimCompleted: (AudioTrimResult) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val totalMs = track.durationMs.coerceAtLeast(5_000L)
    var startFrac by remember(track.id) { mutableFloatStateOf(0.05f) }
    var endFrac by remember(track.id) { mutableFloatStateOf(0.45f) }
    var outputTitle by remember(track.id) { mutableStateOf("${track.title}_ringtone") }
    var isExporting by remember { mutableStateOf(false) }
    var lastResult by remember { mutableStateOf<AudioTrimResult?>(null) }

    val startMs = (totalMs * startFrac).toLong()
    val endMs = (totalMs * endFrac).toLong().coerceAtLeast(startMs + 1000L)

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("audio_cutter_dialog"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.ContentCut, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(strings.audioCutterTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(track.title, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Visual Waveform with highlighted trim region
                val primaryColor = MaterialTheme.colorScheme.primary
                val dimColor = MaterialTheme.colorScheme.outlineVariant
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth().height(72.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 10.dp, vertical = 12.dp)) {
                        val bars = 42
                        val step = size.width / bars
                        val midY = size.height / 2f
                        for (i in 0 until bars) {
                            val f = i.toFloat() / bars.toFloat()
                            val active = f in startFrac..endFrac
                            val amp = (0.28f + 0.65f * kotlin.math.abs(sin((i * 0.72 + track.id.hashCode() % 7).toDouble())).toFloat())
                            val halfH = (size.height * 0.45f) * amp
                            drawLine(
                                color = if (active) primaryColor else dimColor.copy(alpha = 0.45f),
                                start = Offset(i * step + step * 0.5f, midY - halfH),
                                end = Offset(i * step + step * 0.5f, midY + halfH),
                                strokeWidth = (step * 0.52f).coerceAtLeast(3f),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("A: ${MediaScanner.formatDuration(startMs)}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Δ ${MediaScanner.formatDuration(endMs - startMs)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("B: ${MediaScanner.formatDuration(endMs)}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }

                Slider(
                    value = startFrac,
                    onValueChange = { startFrac = it.coerceIn(0f, (endFrac - 0.04f).coerceAtLeast(0f)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Slider(
                    value = endFrac,
                    onValueChange = { endFrac = it.coerceIn((startFrac + 0.04f).coerceAtMost(1f), 1f) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = outputTitle,
                    onValueChange = { outputTitle = it },
                    singleLine = true,
                    label = { Text(strings.tagTitleLabel) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { onPreviewRange(startMs) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(strings.menuPlayNow)
                    }
                    if (lastResult?.shareUri != null) {
                        FilledTonalButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "audio/*"
                                    putExtra(Intent.EXTRA_STREAM, lastResult!!.shareUri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                runCatching { context.startActivity(Intent.createChooser(intent, strings.menuShare)) }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(strings.menuShare)
                        }
                    }
                }

                if (lastResult != null) {
                    Text(
                        text = if (lastResult!!.success) strings.audioCutterSavedToast else (lastResult!!.errorMessage ?: "Error"),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (lastResult!!.success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isExporting,
                onClick = {
                    isExporting = true
                    scope.launch {
                        val res = AudioCutterEngine.trimAndExport(context, track, startMs, endMs, outputTitle)
                        isExporting = false
                        lastResult = res
                        onTrimCompleted(res)
                    }
                },
                modifier = Modifier.testTag("audio_cutter_export_btn")
            ) {
                if (isExporting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text(strings.audioCutterExportBtn)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.close) }
        }
    )
}
