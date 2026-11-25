package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Hearing
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.RecordVoiceOver
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SpatialAudioOff
import androidx.compose.material.icons.rounded.Speaker
import androidx.compose.material.icons.rounded.SurroundSound
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localization.AppStrings
import com.example.model.AppSettings
import com.example.model.AutoStudioProfile
import com.example.model.CustomStudioPreset
import com.example.model.HeadphoneTuningProfile
import com.example.model.ProEqPreset
import com.example.model.ReverbEnvironment
import com.example.player.FiveBandBiquadAudioProcessor
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Beta 4.4 Material 3 Expressive Pro Audio Mastering Studio Modal Sheet:
 * - Auto-Studio Mastering Detection Hero Card (1-tap + automatic per-track analysis)
 * - Live Stereo L/R VU Meter + Instant A/B Bypass Comparison
 * - 4 Clean Collapsible Material You Studio Racks:
 *   1. 10-Band Pro Biquad Equalizer (31Hz..16kHz, ±15dB) + Live Frequency Response Curve + 10 Presets
 *   2. Analog Tube Warmth, Sub-Bass 48Hz, Vocal Clarity 3.2kHz, 12kHz Air, Mid-Side Stereo Width & 3D Reverb
 *   3. Dynamic Compressor, Pre-Amp Gain, High-Pass Rumble Filter, Crossfeed, True-Peak Limiter, Night Mode, Channel Swap, Balance & Mono
 *   4. 5-Mode Headphone Acoustic Calibration + Custom User Saved Presets Manager
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProAudioStudioModalSheet(
    settings: AppSettings,
    vuLevelsFlow: StateFlow<Pair<Float, Float>>,
    detectedAutoProfile: AutoStudioProfile?,
    customPresets: List<CustomStudioPreset>,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onToggleProEq: (Boolean) -> Unit,
    onSelectProPreset: (ProEqPreset) -> Unit,
    onUpdateTenBand: (Int, Float) -> Unit,
    onToggleAutoStudio: (Boolean) -> Unit,
    onAnalyzeAutoNow: () -> Unit,
    onTubeWarmthChange: (Float) -> Unit,
    onStereoWidthChange: (Float) -> Unit,
    onCrossfeedChange: (Boolean) -> Unit,
    onSubBassPunchChange: (Float) -> Unit,
    onAirBrillianceChange: (Float) -> Unit,
    onVocalClarityChange: (Float) -> Unit,
    onCompressorChange: (Float) -> Unit,
    onTruePeakLimiterChange: (Boolean) -> Unit,
    onNightModeChange: (Boolean) -> Unit,
    onRumbleFilterChange: (Int) -> Unit,
    onChannelSwapChange: (Boolean) -> Unit,
    onHeadphoneProfileChange: (HeadphoneTuningProfile) -> Unit,
    onBypassChange: (Boolean) -> Unit,
    onReverbChange: (ReverbEnvironment) -> Unit,
    onPreampChange: (Int) -> Unit,
    onNormalizationChange: (Boolean) -> Unit,
    onStereoBalanceChange: (Float) -> Unit,
    onMonoChange: (Boolean) -> Unit,
    onResetAll: () -> Unit,
    onSaveCustomPreset: (String) -> Unit,
    onApplyCustomPreset: (CustomStudioPreset) -> Unit,
    onDeleteCustomPreset: (String) -> Unit,
) {
    var eqRackExpanded by remember { mutableStateOf(true) }
    var toneRackExpanded by remember { mutableStateOf(false) }
    var dynamicsRackExpanded by remember { mutableStateOf(false) }
    var calibrationRackExpanded by remember { mutableStateOf(false) }
    var showSavePresetDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 6.dp)
                .testTag("pro_audio_studio_sheet"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. EXPRESSIVE HEADER + INSTANT A/B BYPASS + RESET
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.GraphicEq,
                            contentDescription = strings.proStudioTitle,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = strings.proStudioTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = strings.appVersionLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = strings.proStudioSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp).testTag("pro_studio_close")
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = strings.close)
                }
            }

            // A/B Instant Bypass Bar + Reset Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val bypassBg by animateColorAsState(
                    targetValue = if (settings.dspBypassEnabled) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                    label = "bypassBg"
                )
                val bypassFg by animateColorAsState(
                    targetValue = if (settings.dspBypassEnabled) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    label = "bypassFg"
                )
                Surface(
                    onClick = { onBypassChange(!settings.dspBypassEnabled) },
                    shape = CircleShape,
                    color = bypassBg,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("pro_studio_ab_bypass")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CompareArrows,
                            contentDescription = null,
                            tint = bypassFg,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (settings.dspBypassEnabled) strings.studioBypassOriginal else strings.studioBypassMastered,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = bypassFg,
                            maxLines = 1
                        )
                    }
                }
                OutlinedButton(
                    onClick = onResetAll,
                    shape = CircleShape,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("pro_studio_reset_all")
                ) {
                    Icon(Icons.Rounded.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(strings.studioResetAll, maxLines = 1)
                }
            }

            // 2. ISOLATED LIVE STEREO L/R VU PEAK METER LEAF
            StudioLiveVuMeterLeaf(
                vuLevelsFlow = vuLevelsFlow,
                label = strings.studioVuMeterLabel,
                bypassed = settings.dspBypassEnabled,
            )

            // 3. AUTO-STUDIO MASTERING HERO CARD
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f),
                border = BorderStroke(
                    1.2.dp,
                    if (settings.autoStudioMasteringEnabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.studioAutoTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = strings.studioAutoDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.autoStudioMasteringEnabled,
                            onCheckedChange = onToggleAutoStudio,
                            modifier = Modifier.testTag("pro_studio_auto_toggle")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = onAnalyzeAutoNow,
                            shape = CircleShape,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("pro_studio_analyze_now")
                        ) {
                            Icon(Icons.Rounded.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(strings.studioAutoAnalyzeNow, maxLines = 1)
                        }
                        if (detectedAutoProfile != null) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${strings.autoStudioCategoryName(detectedAutoProfile.category)} • ${detectedAutoProfile.confidencePercent}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. RACK 1 (COLLAPSIBLE): 10-BAND PRO BIQUAD EQUALIZER + LIVE CURVE
            StudioCollapsibleRack(
                title = strings.studioRackEqTitle,
                icon = Icons.Rounded.Equalizer,
                badgeText = strings.proEqPresetName(settings.proEqPreset),
                expanded = eqRackExpanded,
                onToggle = { eqRackExpanded = !eqRackExpanded },
                testTag = "pro_rack_eq_toggle",
                strings = strings,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (settings.proEqEnabled && !settings.dspBypassEnabled) strings.eqActiveStatus else strings.eqBypassedStatus,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (settings.proEqEnabled && !settings.dspBypassEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Switch(
                        checked = settings.proEqEnabled,
                        onCheckedChange = onToggleProEq,
                        modifier = Modifier.testTag("pro_eq_master_switch")
                    )
                }

                // Live Interactive Bezier Frequency Curve Canvas
                ProEqCurveCanvas(
                    gainsDb = settings.proTenBandGainsDb,
                    active = settings.proEqEnabled && !settings.dspBypassEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp)
                )

                // 10 Studio Pro Presets Horizontal Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProEqPreset.entries.filterNot { it == ProEqPreset.CUSTOM }.forEach { preset ->
                        FilterChip(
                            selected = settings.proEqPreset == preset,
                            onClick = { onSelectProPreset(preset) },
                            leadingIcon = {
                                Icon(
                                    imageVector = presetIconFor(preset),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(strings.proEqPresetName(preset), maxLines = 1) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("pro_eq_preset_${preset.name.lowercase()}")
                        )
                    }
                }

                // 10 Precision Band Sliders (31Hz..16kHz, -15dB..+15dB)
                val labels = FiveBandBiquadAudioProcessor.PRO_TEN_BAND_LABELS
                val gains = if (settings.proTenBandGainsDb.size == 10) settings.proTenBandGainsDb else ProEqPreset.FLAT.gainsDb
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    labels.forEachIndexed { idx, freqLabel ->
                        val db = gains[idx]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                modifier = Modifier.width(50.dp)
                            ) {
                                Text(
                                    text = "${freqLabel}Hz",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                            Slider(
                                value = db,
                                onValueChange = { onUpdateTenBand(idx, (it * 2f).roundToInt() / 2f) },
                                valueRange = -15f..15f,
                                enabled = settings.proEqEnabled && !settings.dspBypassEnabled,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("pro_eq_band_$idx")
                            )
                            Text(
                                text = String.format(Locale.US, "%+.1f", db),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = when {
                                    db > 0.2f -> MaterialTheme.colorScheme.primary
                                    db < -0.2f -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.width(42.dp)
                            )
                        }
                    }
                }
            }

            // 5. RACK 2 (COLLAPSIBLE): ANALOG WARMTH, TONE SHAPERS & SOUNDSTAGE
            StudioCollapsibleRack(
                title = strings.studioRackToneTitle,
                icon = Icons.Rounded.Whatshot,
                badgeText = "${(settings.stereoWidth * 100).roundToInt()}%",
                expanded = toneRackExpanded,
                onToggle = { toneRackExpanded = !toneRackExpanded },
                testTag = "pro_rack_tone_toggle",
                strings = strings,
            ) {
                StudioSliderControl(
                    icon = Icons.Rounded.Whatshot,
                    title = strings.studioTubeWarmth,
                    valueLabel = "${(settings.tubeWarmth * 100).roundToInt()}%",
                    value = settings.tubeWarmth,
                    range = 0f..1f,
                    onValueChange = onTubeWarmthChange,
                    testTag = "pro_slider_tube_warmth"
                )
                StudioSliderControl(
                    icon = Icons.Rounded.Speaker,
                    title = strings.studioSubBassPunch,
                    valueLabel = "${(settings.subBassPunch * 100).roundToInt()}%",
                    value = settings.subBassPunch,
                    range = 0f..1f,
                    onValueChange = onSubBassPunchChange,
                    testTag = "pro_slider_sub_bass"
                )
                StudioSliderControl(
                    icon = Icons.Rounded.RecordVoiceOver,
                    title = strings.studioVocalClarity,
                    valueLabel = "${(settings.vocalClarity * 100).roundToInt()}%",
                    value = settings.vocalClarity,
                    range = 0f..1f,
                    onValueChange = onVocalClarityChange,
                    testTag = "pro_slider_vocal_clarity"
                )
                StudioSliderControl(
                    icon = Icons.Rounded.Air,
                    title = strings.studioAirBrilliance,
                    valueLabel = "${(settings.airBrilliance * 100).roundToInt()}%",
                    value = settings.airBrilliance,
                    range = 0f..1f,
                    onValueChange = onAirBrillianceChange,
                    testTag = "pro_slider_air_brilliance"
                )
                StudioSliderControl(
                    icon = Icons.Rounded.SurroundSound,
                    title = strings.studioStereoWidth,
                    valueLabel = "${(settings.stereoWidth * 100).roundToInt()}%",
                    value = settings.stereoWidth,
                    range = 0.5f..2.0f,
                    onValueChange = onStereoWidthChange,
                    testTag = "pro_slider_stereo_width"
                )

                // 3D Acoustic Reverb Room Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Rounded.SpatialAudioOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.reverbTitle,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ReverbEnvironment.entries.forEach { env ->
                        val envLabel = when (env) {
                            ReverbEnvironment.NONE -> strings.reverbNone
                            ReverbEnvironment.STUDIO -> strings.reverbStudio
                            ReverbEnvironment.HALL -> strings.reverbHall
                            ReverbEnvironment.MOSQUE -> strings.reverbMosque
                            ReverbEnvironment.SPATIAL_3D -> strings.reverbSpatial3d
                        }
                        FilterChip(
                            selected = settings.reverbEnvironment == env,
                            onClick = { onReverbChange(env) },
                            label = { Text(envLabel, maxLines = 1) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("pro_reverb_${env.name.lowercase()}")
                        )
                    }
                }
            }

            // 6. RACK 3 (COLLAPSIBLE): DYNAMICS, PROTECTION & ACOUSTIC FILTERS
            StudioCollapsibleRack(
                title = strings.studioRackDynamicsTitle,
                icon = Icons.Rounded.Compress,
                badgeText = if (settings.nightModeEnabled) strings.studioNightModeTitle else "${settings.audioPreampPercent}%",
                expanded = dynamicsRackExpanded,
                onToggle = { dynamicsRackExpanded = !dynamicsRackExpanded },
                testTag = "pro_rack_dynamics_toggle",
                strings = strings,
            ) {
                StudioSliderControl(
                    icon = Icons.Rounded.Compress,
                    title = strings.studioCompressor,
                    valueLabel = "${(settings.compressorStrength * 100).roundToInt()}%",
                    value = settings.compressorStrength,
                    range = 0f..1f,
                    onValueChange = onCompressorChange,
                    testTag = "pro_slider_compressor"
                )
                StudioSliderControl(
                    icon = Icons.Rounded.VolumeUp,
                    title = strings.preampBoostTitle,
                    valueLabel = "${settings.audioPreampPercent}%",
                    value = settings.audioPreampPercent.toFloat(),
                    range = 100f..200f,
                    onValueChange = { onPreampChange(it.roundToInt()) },
                    testTag = "pro_slider_preamp"
                )

                // High-Pass Sub-Rumble Filter Chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Rounded.FilterAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(strings.studioRumbleFilterTitle, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0 to "OFF", 30 to "30Hz", 60 to "60Hz", 80 to "80Hz").forEach { (hz, label) ->
                        FilterChip(
                            selected = settings.rumbleFilterHz == hz,
                            onClick = { onRumbleFilterChange(hz) },
                            label = { Text(label) },
                            shape = CircleShape,
                            modifier = Modifier.weight(1f).testTag("pro_rumble_$hz")
                        )
                    }
                }

                StudioToggleRow(
                    icon = Icons.Rounded.Headphones,
                    title = strings.studioCrossfeedTitle,
                    subtitle = strings.studioCrossfeedDesc,
                    checked = settings.crossfeedEnabled,
                    onCheckedChange = onCrossfeedChange,
                    testTag = "pro_toggle_crossfeed"
                )
                StudioToggleRow(
                    icon = Icons.Rounded.Shield,
                    title = strings.studioTruePeakTitle,
                    subtitle = strings.studioTruePeakDesc,
                    checked = settings.truePeakLimiterEnabled,
                    onCheckedChange = onTruePeakLimiterChange,
                    testTag = "pro_toggle_limiter"
                )
                StudioToggleRow(
                    icon = Icons.Rounded.NightsStay,
                    title = strings.studioNightModeTitle,
                    subtitle = strings.studioNightModeDesc,
                    checked = settings.nightModeEnabled,
                    onCheckedChange = onNightModeChange,
                    testTag = "pro_toggle_night_mode"
                )
                StudioToggleRow(
                    icon = Icons.Rounded.AutoFixHigh,
                    title = strings.audioNormalizationTitle,
                    subtitle = strings.audioNormalizationDesc,
                    checked = settings.audioNormalization,
                    onCheckedChange = onNormalizationChange,
                    testTag = "pro_toggle_normalization"
                )
                StudioToggleRow(
                    icon = Icons.Rounded.SwapHoriz,
                    title = strings.studioChannelSwapTitle,
                    subtitle = strings.studioChannelSwapDesc,
                    checked = settings.channelSwapEnabled,
                    onCheckedChange = onChannelSwapChange,
                    testTag = "pro_toggle_channel_swap"
                )
                StudioToggleRow(
                    icon = Icons.Rounded.Hearing,
                    title = strings.audioMonoTitle,
                    subtitle = strings.audioMonoDesc,
                    checked = settings.audioMonoEnabled,
                    onCheckedChange = onMonoChange,
                    testTag = "pro_toggle_mono"
                )
                StudioSliderControl(
                    icon = Icons.Rounded.Balance,
                    title = strings.stereoBalanceTitle,
                    valueLabel = when {
                        settings.audioBalance < -0.05f -> "L ${(-settings.audioBalance * 100).roundToInt()}%"
                        settings.audioBalance > 0.05f -> "R ${(settings.audioBalance * 100).roundToInt()}%"
                        else -> "C"
                    },
                    value = settings.audioBalance,
                    range = -1f..1f,
                    onValueChange = onStereoBalanceChange,
                    testTag = "pro_slider_balance"
                )
            }

            // 7. RACK 4 (COLLAPSIBLE): HEADPHONE CALIBRATION & SAVED CUSTOM STUDIO PRESETS
            StudioCollapsibleRack(
                title = strings.studioRackHeadphoneTitle,
                icon = Icons.Rounded.Headphones,
                badgeText = strings.headphoneProfileName(settings.headphoneProfile),
                expanded = calibrationRackExpanded,
                onToggle = { calibrationRackExpanded = !calibrationRackExpanded },
                testTag = "pro_rack_calibration_toggle",
                strings = strings,
            ) {
                Text(
                    text = strings.studioHeadphoneProfileTitle,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HeadphoneTuningProfile.entries.forEach { profile ->
                        FilterChip(
                            selected = settings.headphoneProfile == profile,
                            onClick = { onHeadphoneProfileChange(profile) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (profile == HeadphoneTuningProfile.SPEAKER_COMPENSATION) Icons.Rounded.Speaker else Icons.Rounded.Headphones,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(strings.headphoneProfileName(profile), maxLines = 1) },
                            shape = CircleShape,
                            modifier = Modifier.testTag("pro_hp_${profile.name.lowercase()}")
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = strings.studioCustomPresetsTitle,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    FilledTonalButton(
                        onClick = { showSavePresetDialog = true },
                        shape = CircleShape,
                        modifier = Modifier.height(40.dp).testTag("pro_studio_save_preset_btn")
                    ) {
                        Icon(Icons.Rounded.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.studioSavePresetAction, maxLines = 1)
                    }
                }

                if (customPresets.isEmpty()) {
                    Text(
                        text = strings.studioNoCustomPresets,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        customPresets.forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onApplyCustomPreset(preset) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.Tune,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = preset.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "W:${(preset.stereoWidth * 100).roundToInt()}% • Tube:${(preset.tubeWarmth * 100).roundToInt()}% • Sub:${(preset.subBassPunch * 100).roundToInt()}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteCustomPreset(preset.id) },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(
                                            Icons.Rounded.DeleteOutline,
                                            contentDescription = strings.menuRemove,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
        }
    }

    if (showSavePresetDialog) {
        var presetName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSavePresetDialog = false },
            icon = { Icon(Icons.Rounded.BookmarkAdd, contentDescription = null) },
            title = { Text(strings.studioSavePresetAction, fontWeight = FontWeight.ExtraBold) },
            text = {
                OutlinedTextField(
                    value = presetName,
                    onValueChange = { presetName = it.take(36) },
                    label = { Text(strings.studioSavePresetHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("pro_studio_preset_name_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (presetName.isNotBlank()) {
                            onSaveCustomPreset(presetName)
                            showSavePresetDialog = false
                        }
                    },
                    enabled = presetName.isNotBlank()
                ) {
                    Text(strings.save)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSavePresetDialog = false }) {
                    Text(strings.cancel)
                }
            }
        )
    }
}

@Composable
private fun StudioCollapsibleRack(
    title: String,
    icon: ImageVector,
    badgeText: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    testTag: String,
    strings: AppStrings,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(
            1.dp,
            if (expanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .testTag(testTag),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (expanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (expanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                            contentDescription = if (expanded) strings.collapseSectionDesc else strings.expandSectionDesc,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = spring(dampingRatio = 0.85f)) + fadeIn(),
                exit = shrinkVertically(animationSpec = spring(dampingRatio = 0.85f)) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun StudioLiveVuMeterLeaf(
    vuLevelsFlow: StateFlow<Pair<Float, Float>>,
    label: String,
    bypassed: Boolean,
) {
    val vu by vuLevelsFlow.collectAsStateWithLifecycle()
    val animL by animateFloatAsState(targetValue = vu.first.coerceIn(0f, 1f), label = "vuL")
    val animR by animateFloatAsState(targetValue = vu.second.coerceIn(0f, 1f), label = "vuR")
    val trackBg = MaterialTheme.colorScheme.surfaceContainerHighest
    val primaryColor = if (bypassed) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
    val secondaryColor = if (bypassed) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.tertiary
    val peakColor = MaterialTheme.colorScheme.error

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "L ${(animL * 100).roundToInt()}%  •  R ${(animR * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )
            }
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            ) {
                val barH = 6.dp.toPx()
                val gap = 4.dp.toPx()
                val radius = CornerRadius(barH / 2f, barH / 2f)
                val meterBrush = Brush.horizontalGradient(listOf(primaryColor, secondaryColor, peakColor))
                drawRoundRect(color = trackBg, topLeft = Offset(0f, 0f), size = Size(size.width, barH), cornerRadius = radius)
                drawRoundRect(color = trackBg, topLeft = Offset(0f, barH + gap), size = Size(size.width, barH), cornerRadius = radius)
                if (animL > 0.01f) {
                    drawRoundRect(
                        brush = meterBrush,
                        topLeft = Offset(0f, 0f),
                        size = Size((size.width * animL).coerceAtLeast(barH), barH),
                        cornerRadius = radius
                    )
                }
                if (animR > 0.01f) {
                    drawRoundRect(
                        brush = meterBrush,
                        topLeft = Offset(0f, barH + gap),
                        size = Size((size.width * animR).coerceAtLeast(barH), barH),
                        cornerRadius = radius
                    )
                }
            }
        }
    }
}

@Composable
private fun ProEqCurveCanvas(
    gainsDb: List<Float>,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val curveColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
    val bgColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.55f)
    val safeGains = if (gainsDb.size == 10) gainsDb else ProEqPreset.FLAT.gainsDb

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bgColor)
    ) {
        val w = size.width
        val h = size.height
        val padX = 14.dp.toPx()
        val padY = 14.dp.toPx()
        val usableW = (w - padX * 2).coerceAtLeast(1f)
        val usableH = (h - padY * 2).coerceAtLeast(1f)
        val midY = padY + usableH / 2f

        // Reference horizontal dB lines (+15, 0, -15)
        drawLine(gridColor, Offset(padX, padY), Offset(w - padX, padY), strokeWidth = 1f)
        drawLine(gridColor.copy(alpha = 0.85f), Offset(padX, midY), Offset(w - padX, midY), strokeWidth = 1.6f)
        drawLine(gridColor, Offset(padX, h - padY), Offset(w - padX, h - padY), strokeWidth = 1f)

        val points = safeGains.mapIndexed { idx, db ->
            val x = padX + (usableW * idx / 9f)
            val norm = (db.coerceIn(-15f, 15f) / 15f)
            val y = midY - norm * (usableH * 0.46f)
            Offset(x, y)
        }

        val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.lastIndex) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val cx = (p0.x + p1.x) / 2f
                cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
            }
        }
        val fillPath = Path().apply {
            addPath(strokePath)
            lineTo(points.last().x, midY)
            lineTo(points.first().x, midY)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    curveColor.copy(alpha = 0.34f),
                    curveColor.copy(alpha = 0.05f),
                )
            )
        )
        drawPath(
            path = strokePath,
            color = curveColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        points.forEach { pt ->
            drawCircle(color = curveColor, radius = 4.dp.toPx(), center = pt)
        }
    }
}

@Composable
private fun StudioSliderControl(
    icon: ImageVector,
    title: String,
    valueLabel: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    testTag: String,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = valueLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        Slider(
            value = value.coerceIn(range.start, range.endInclusive),
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth().testTag(testTag)
        )
    }
}

@Composable
private fun StudioToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (checked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}

private fun presetIconFor(preset: ProEqPreset): ImageVector = when (preset) {
    ProEqPreset.FLAT -> Icons.Rounded.Tune
    ProEqPreset.STUDIO_MASTER -> Icons.Rounded.AutoAwesome
    ProEqPreset.QURAN_CRYSTAL -> Icons.Rounded.MenuBook
    ProEqPreset.DEEP_SUB_CINEMA -> Icons.Rounded.Movie
    ProEqPreset.ACOUSTIC_WARMTH -> Icons.Rounded.MusicNote
    ProEqPreset.VOCAL_AIR -> Icons.Rounded.RecordVoiceOver
    ProEqPreset.CONCERT_HALL -> Icons.Rounded.SpatialAudioOff
    ProEqPreset.LATE_NIGHT -> Icons.Rounded.NightsStay
    ProEqPreset.ELECTRONIC_PUNCH -> Icons.Rounded.GraphicEq
    ProEqPreset.LOFI_VELVET -> Icons.Rounded.LibraryMusic
    ProEqPreset.CUSTOM -> Icons.Rounded.Equalizer
}
