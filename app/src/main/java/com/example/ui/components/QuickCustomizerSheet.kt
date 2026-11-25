package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Contrast
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.Swipe
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.ViewHeadline
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.localization.AppStrings
import com.example.model.AccentPalette
import com.example.model.AppSettings
import com.example.model.BackdropAnimationStyle
import com.example.model.HeaderTitleStyle
import com.example.model.LiquidGlassStyle
import com.example.model.MediaCardStyle
import com.example.model.MotionFeelStyle
import com.example.ui.theme.palettePreviewColor
import kotlin.math.roundToInt

/**
 * 1-Tap Live Interactive Customization Studio Sheet.
 * Accessible from tapping the Prism Emblem on ANY screen (Music, Videos, Photos, Settings)
 * so users can craft their exact aesthetic, cards, backdrop, motion physics, and vibrancy live!
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuickCustomizerSheet(
    settings: AppSettings,
    strings: AppStrings,
    onDismiss: () -> Unit,
    onDarkThemeChange: (Boolean) -> Unit,
    onAmoledBlackChange: (Boolean) -> Unit,
    onAccentPaletteChange: (AccentPalette) -> Unit,
    onLiquidGlassStyleChange: (LiquidGlassStyle) -> Unit,
    onCardStyleChange: (MediaCardStyle) -> Unit,
    onBackdropStyleChange: (BackdropAnimationStyle) -> Unit,
    onMotionFeelChange: (MotionFeelStyle) -> Unit,
    onHeaderStyleChange: (HeaderTitleStyle) -> Unit,
    onShapeRoundnessChange: (Float) -> Unit,
    onAccentStrengthChange: (Float) -> Unit,
    onSwipeGesturesChange: (Boolean) -> Unit,
    onShowFormatBadgesChange: (Boolean) -> Unit,
    onApplyQuickThemePreset: (String) -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val haptics = LocalHapticFeedback.current
    val isPureM3 = settings.liquidGlassStyle == LiquidGlassStyle.SOLID_M3

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
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .testTag("quick_customizer_sheet"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Sheet Title Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = cs.primaryContainer,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = cs.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.quickStudioTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = strings.quickStudioSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                }
            }

            // Live Interactive Preview Card
            LiveAppearancePreviewCard(
                settings = settings,
                strings = strings
            )

            // Quick Mode Toggles: Dark / Light / AMOLED / Liquid Glass vs Material You
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickPillToggle(
                    icon = if (settings.darkTheme) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                    label = strings.darkMode,
                    active = settings.darkTheme,
                    modifier = Modifier.weight(1f).testTag("qc_dark_toggle"),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDarkThemeChange(!settings.darkTheme)
                    }
                )
                QuickPillToggle(
                    icon = Icons.Rounded.Contrast,
                    label = "AMOLED",
                    active = settings.darkTheme && settings.amoledBlack,
                    modifier = Modifier.weight(1f).testTag("qc_amoled_toggle"),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        if (!settings.darkTheme) onDarkThemeChange(true)
                        onAmoledBlackChange(!settings.amoledBlack)
                    }
                )
                QuickPillToggle(
                    icon = if (isPureM3) Icons.Rounded.Palette else Icons.Rounded.BlurOn,
                    label = if (isPureM3) "M3" else "Glass",
                    active = !isPureM3,
                    modifier = Modifier.weight(1f).testTag("qc_glass_toggle"),
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onLiquidGlassStyleChange(
                            if (isPureM3) LiquidGlassStyle.MATERIAL_LIQUID else LiquidGlassStyle.SOLID_M3
                        )
                    }
                )
            }

            // 1-Tap Curated Aesthetic Presets
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                val presets = listOf(
                    Triple("crystal", "Prism Crystal", Icons.Rounded.AutoAwesome),
                    Triple("obsidian", "AMOLED Gold", Icons.Rounded.Contrast),
                    Triple("material_you", "Pure M3", Icons.Rounded.Palette),
                    Triple("emerald", "Emerald Serenity", Icons.Rounded.Tune),
                )
                items(presets, key = { it.first }) { (key, label, icon) ->
                    val inter = remember { MutableInteractionSource() }
                    Surface(
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onApplyQuickThemePreset(key)
                        },
                        interactionSource = inter,
                        shape = CircleShape,
                        color = cs.secondaryContainer,
                        modifier = Modifier
                            .expressivePressScale(inter, pressedScale = 0.93f)
                            .testTag("qc_preset_$key")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(icon, contentDescription = null, tint = cs.onSecondaryContainer, modifier = Modifier.size(16.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = cs.onSecondaryContainer
                            )
                        }
                    }
                }
            }

            // Accent Color Swatches Strip
            SectionLabel(Icons.Rounded.Palette, strings.dynamicPalette)
            val orderedPalettes = remember {
                listOf(AccentPalette.SYSTEM) + AccentPalette.entries.filterNot { it == AccentPalette.SYSTEM }
            }
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(orderedPalettes, key = { it.name }) { pal ->
                    val selected = settings.accentPalette == pal
                    val swatchColor = if (pal == AccentPalette.SYSTEM) cs.primary else palettePreviewColor(pal)
                    val inter = remember { MutableInteractionSource() }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .expressivePressScale(inter, pressedScale = 0.9f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(interactionSource = inter, indication = null) {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onAccentPaletteChange(pal)
                            }
                            .padding(2.dp)
                            .testTag("qc_palette_${pal.name.lowercase()}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(swatchColor)
                                .border(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) cs.onSurface else cs.outlineVariant.copy(alpha = 0.4f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = strings.paletteName(pal),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            HorizontalDivider(color = cs.outlineVariant.copy(alpha = 0.35f))

            // Media Card Style Selector
            SectionLabel(Icons.Rounded.Style, strings.cardStyleTitle)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MediaCardStyle.entries.forEach { style ->
                    FilterChip(
                        selected = settings.cardStyle == style,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onCardStyleChange(style)
                        },
                        label = { Text(strings.cardStyleName(style)) },
                        shape = CircleShape,
                        modifier = Modifier.testTag("qc_card_${style.name.lowercase()}")
                    )
                }
            }

            // Ambient Backdrop Style Selector
            SectionLabel(Icons.Rounded.Wallpaper, strings.backdropStyleTitle)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BackdropAnimationStyle.entries.forEach { style ->
                    FilterChip(
                        selected = settings.backdropStyle == style,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onBackdropStyleChange(style)
                        },
                        label = { Text(strings.backdropStyleName(style)) },
                        shape = CircleShape,
                        modifier = Modifier.testTag("qc_backdrop_${style.name.lowercase()}")
                    )
                }
            }

            // Motion Physics Selector
            SectionLabel(Icons.Rounded.Animation, strings.motionFeelTitle)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MotionFeelStyle.entries.forEach { feel ->
                    FilterChip(
                        selected = settings.motionFeelStyle == feel,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onMotionFeelChange(feel)
                        },
                        label = { Text(strings.motionFeelName(feel)) },
                        shape = CircleShape,
                        modifier = Modifier.testTag("qc_motion_${feel.name.lowercase()}")
                    )
                }
            }

            // Header Title Style Selector
            SectionLabel(Icons.Rounded.ViewHeadline, strings.headerStyleTitle)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                HeaderTitleStyle.entries.forEach { style ->
                    FilterChip(
                        selected = settings.headerTitleStyle == style,
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onHeaderStyleChange(style)
                        },
                        label = { Text(strings.headerStyleName(style)) },
                        shape = CircleShape,
                        modifier = Modifier.testTag("qc_header_${style.name.lowercase()}")
                    )
                }
            }

            HorizontalDivider(color = cs.outlineVariant.copy(alpha = 0.35f))

            // Corner Roundness & Color Vibrancy Live Sliders
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionLabel(Icons.Rounded.RoundedCorner, strings.shapeRoundnessTitle)
                    Text(
                        text = "${(settings.shapeRoundness * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = cs.primary
                    )
                }
                Slider(
                    value = settings.shapeRoundness,
                    onValueChange = onShapeRoundnessChange,
                    valueRange = 0.6f..1.4f
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionLabel(Icons.Rounded.AutoAwesome, strings.accentStrengthTitle)
                    Text(
                        text = "${(settings.accentStrength * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = cs.primary
                    )
                }
                Slider(
                    value = settings.accentStrength,
                    onValueChange = onAccentStrengthChange,
                    valueRange = 0.5f..1.4f
                )
            }

            // Interactive Swipes + Format Badges Switches
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Swipe, contentDescription = null, tint = cs.primary, modifier = Modifier.size(20.dp))
                    Column {
                        Text(strings.swipeGesturesTitle, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(strings.swipeGesturesDesc, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                    }
                }
                Switch(
                    checked = settings.swipeGesturesEnabled,
                    onCheckedChange = onSwipeGesturesChange,
                    modifier = Modifier.testTag("qc_swipe_toggle")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.Tune, contentDescription = null, tint = cs.primary, modifier = Modifier.size(20.dp))
                    Column {
                        Text(strings.showFormatBadgesTitle, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(strings.showFormatBadgesDesc, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                    }
                }
                Switch(
                    checked = settings.showFormatBadges,
                    onCheckedChange = onShowFormatBadgesChange,
                    modifier = Modifier.testTag("qc_badges_toggle")
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SectionLabel(icon: ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun QuickPillToggle(
    icon: ImageVector,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val inter = remember { MutableInteractionSource() }
    Surface(
        onClick = onClick,
        interactionSource = inter,
        shape = RoundedCornerShape(18.dp),
        color = if (active) cs.primaryContainer else cs.surfaceContainerHigh,
        border = BorderStroke(
            width = if (active) 1.5.dp else 1.dp,
            color = if (active) cs.primary else cs.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .height(48.dp)
            .expressivePressScale(inter, pressedScale = 0.94f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (active) cs.onPrimaryContainer else cs.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (active) cs.onPrimaryContainer else cs.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
