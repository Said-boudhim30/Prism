package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localization.AppStrings
import com.example.model.EqPreset
import com.example.player.AudioPlaybackController
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun LiveAudioSpectrumBars(
    levels: List<Float>,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 12,
) {
    val safeLevels = remember(levels, barCount) {
        if (levels.size >= barCount) levels.take(barCount)
        else List(barCount) { idx -> levels.getOrElse(idx) { 0.08f } }
    }
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom
    ) {
        safeLevels.forEachIndexed { idx, rawLevel ->
            val target = if (isPlaying) rawLevel.coerceIn(0.10f, 1.0f) else 0.12f
            val animatedHeight by animateFloatAsState(
                targetValue = target,
                animationSpec = tween(durationMillis = 95),
                label = "spectrumBar_$idx"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(animatedHeight)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(tertiary, primary)
                        )
                    )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EqualizerCard(
    gainsDb: List<Float>,
    selectedPreset: EqPreset,
    strings: AppStrings,
    onSelectPreset: (EqPreset) -> Unit,
    onBandChange: (Int, Float) -> Unit,
    onReset: () -> Unit = { onSelectPreset(EqPreset.FLAT) },
    eqEnabled: Boolean = true,
    bassBoost: Float = 0f,
    spectrumLevelsFlow: StateFlow<List<Float>>? = null,
    isPlaying: Boolean = false,
    onToggleEq: (Boolean) -> Unit = {},
    onBassBoostChange: (Float) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp,
        border = BorderStroke(
            1.dp,
            if (eqEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (eqEnabled) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Equalizer,
                            contentDescription = strings.equalizerTitle,
                            tint = if (eqEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = strings.equalizerTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = CircleShape,
                            color = if (eqEnabled) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceContainerHighest
                        ) {
                            Text(
                                text = if (eqEnabled) strings.eqActiveStatus else strings.eqBypassedStatus,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (eqEnabled) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = strings.equalizerSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = eqEnabled,
                    onCheckedChange = onToggleEq,
                    modifier = Modifier.testTag("eq_master_switch")
                )
            }

            // Live PCM Spectrum Strip inside EQ Card (isolated collection for 120Hz fluidity)
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
            ) {
                val liveLevels = spectrumLevelsFlow?.collectAsStateWithLifecycle()?.value ?: emptyList()
                LiveAudioSpectrumBars(
                    levels = liveLevels,
                    isPlaying = isPlaying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(EqPreset.FLAT, EqPreset.DEEP_BASS, EqPreset.VOCAL_BOOST, EqPreset.CLUB).forEach { preset ->
                    val selected = selectedPreset == preset && eqEnabled
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectPreset(preset) },
                        label = {
                            Text(
                                text = strings.eqPresetName(preset),
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("eq_preset_${preset.name.lowercase()}")
                    )
                }
                if (selectedPreset == EqPreset.CUSTOM) {
                    FilterChip(
                        selected = eqEnabled,
                        onClick = {},
                        label = { Text(strings.presetCustom, fontWeight = FontWeight.Bold) },
                        shape = CircleShape
                    )
                }

                // Compact Reset button next to Equalizer presets
                OutlinedButton(
                    onClick = onReset,
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .align(Alignment.CenterVertically)
                        .testTag("eq_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.RestartAlt,
                        contentDescription = strings.eqReset,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = strings.eqReset,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(186.dp)
                    .alpha(if (eqEnabled) 1f else 0.45f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AudioPlaybackController.BAND_LABELS.forEachIndexed { idx, freqLabel ->
                    val gain = gainsDb.getOrElse(idx) { 0f }
                    VerticalEqSlider(
                        label = freqLabel,
                        gainDb = gain,
                        onValueChange = { onBandChange(idx, it) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("eq_band_$idx")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real DSP Bass Boost Slider
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (eqEnabled) 1f else 0.45f)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GraphicEq,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = strings.bassBoostLabel,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(
                                text = "${(bassBoost * 100).roundToInt()}%",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Slider(
                        value = bassBoost,
                        onValueChange = onBassBoostChange,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                        modifier = Modifier.testTag("eq_bass_boost_slider")
                    )
                }
            }
        }
    }
}

@Composable
private fun VerticalEqSlider(
    label: String,
    gainDb: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val minDb = -12f
    val maxDb = 12f
    val normalized = ((gainDb - minDb) / (maxDb - minDb)).coerceIn(0f, 1f)
    val roundedGain = gainDb.roundToInt()
    val formattedDb = when {
        roundedGain > 0 -> "\u200E+${roundedGain}dB"
        roundedGain < 0 -> "\u200E${roundedGain}dB"
        else -> "\u200E0dB"
    }

    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Force strict LTR so +12dB never reverses to 12dB+ in Arabic RTL
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
                text = formattedDb,
                style = MaterialTheme.typography.labelSmall,
                color = if (roundedGain != 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .width(48.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val fraction = (1f - (offset.y / size.height.toFloat())).coerceIn(0f, 1f)
                        val newGain = (minDb + fraction * (maxDb - minDb)).roundToInt().toFloat()
                        onValueChange(newGain)
                    }
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, _ ->
                        change.consume()
                        val fraction = (1f - (change.position.y / size.height.toFloat())).coerceIn(0f, 1f)
                        val newGain = (minDb + fraction * (maxDb - minDb)).roundToInt().toFloat()
                        onValueChange(newGain)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val density = LocalDensity.current
            val thumbSize = 22.dp
            val maxTravelPx = with(density) { (maxHeight - thumbSize).toPx().coerceAtLeast(1f) }
            val thumbOffsetYPx = ((1f - normalized) * maxTravelPx).roundToInt()

            Box(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(10.dp)
                    .fillMaxHeight(normalized.coerceAtLeast(0.05f))
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset { IntOffset(0, thumbOffsetYPx) }
                    .size(thumbSize)
            ) {}
        }

        Spacer(modifier = Modifier.height(8.dp))

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SleepTimerCard(
    remainingSec: Int,
    totalMin: Int,
    strings: AppStrings,
    onStartTimer: (Int) -> Unit,
    onCancelTimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    var customInput by remember { mutableStateOf("20") }

    val pulseTransition = rememberInfiniteTransition(label = "timerPulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulseDot"
    )

    val presetMinutes = listOf(15, 30, 45, 60)
    val isCustomActive = remainingSec > 0 && totalMin !in presetMinutes

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp,
        border = BorderStroke(
            1.dp,
            if (remainingSec > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = strings.sleepTimerTitle,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.sleepTimerTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (remainingSec > 0) {
                            strings.timerActiveRemaining(formatSecondsClock(remainingSec))
                        } else {
                            strings.sleepTimerSubtitle
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (remainingSec > 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (remainingSec > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }

                // Live Remaining Time Badge inside the card
                AnimatedVisibility(
                    visible = remainingSec > 0,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("sleep_timer_live_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .alpha(pulseAlpha)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Text(
                                    text = formatSecondsClock(remainingSec),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetMinutes.forEach { mins ->
                    val active = remainingSec > 0 && totalMin == mins
                    FilterChip(
                        selected = active,
                        onClick = { onStartTimer(mins) },
                        label = {
                            Text(
                                text = strings.minutesShort(mins),
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        shape = CircleShape,
                        modifier = Modifier.testTag("sleep_timer_$mins")
                    )
                }

                // Custom minutes pill button
                FilterChip(
                    selected = isCustomActive,
                    onClick = { showCustomDialog = true },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = {
                        Text(
                            text = if (isCustomActive) "${strings.customTimer} (${strings.minutesShort(totalMin)})" else strings.customTimer,
                            fontWeight = if (isCustomActive) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    shape = CircleShape,
                    modifier = Modifier.testTag("sleep_timer_custom")
                )

                if (remainingSec > 0) {
                    OutlinedButton(
                        onClick = onCancelTimer,
                        shape = CircleShape,
                        modifier = Modifier.testTag("sleep_timer_cancel")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = strings.cancelTimer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.cancelTimer)
                    }
                }
            }
        }
    }

    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = {
                Text(
                    text = strings.customTimerDialogTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = customInput,
                    onValueChange = { input ->
                        customInput = input.filter { it.isDigit() }.take(3)
                    },
                    label = { Text(strings.customTimerHint) },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sleep_timer_custom_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mins = customInput.toIntOrNull()?.coerceIn(1, 360)
                        if (mins != null) {
                            onStartTimer(mins)
                            showCustomDialog = false
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier.testTag("sleep_timer_custom_confirm")
                ) {
                    Text(strings.startTimer, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

fun formatSecondsClock(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", m, s)
}
