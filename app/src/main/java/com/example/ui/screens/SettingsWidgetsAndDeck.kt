package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.model.AppSettings
import com.example.model.PlaybackUiState
import com.example.model.SettingsLayoutMode
import com.example.widget.PrismAudioDeckWidgetProvider
import com.example.widget.PrismExpressiveCardWidgetProvider
import com.example.widget.PrismMediaWidgetProvider
import com.example.widget.PrismQuickStudioWidgetProvider
import com.example.widget.PrismQuranWidgetProvider
import com.example.widget.PrismVinylSquareWidgetProvider
import com.example.widget.WidgetPlaybackBridge
import kotlinx.coroutines.launch

/**
 * Reusable Settings Content Organizer:
 * - In HORIZONTAL_PAGES mode: renders compact left/right swipeable cards with quick sub-tab chips
 *   and page dots so opening a settings section NEVER creates an endless vertical scroll!
 * - In VERTICAL_LIST mode: renders all sub-sections stacked cleanly vertically.
 */
@Composable
fun SettingsPagedDeck(
    layoutMode: SettingsLayoutMode,
    pages: List<Pair<String, @Composable () -> Unit>>,
    strings: AppStrings,
    tagPrefix: String,
    modifier: Modifier = Modifier,
) {
    if (pages.isEmpty()) return

    if (layoutMode == SettingsLayoutMode.VERTICAL_LIST || pages.size == 1) {
        Column(modifier = modifier.fillMaxWidth()) {
            pages.forEachIndexed { idx, (_, pageContent) ->
                if (idx > 0) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }
                pageContent()
            }
        }
        return
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Horizontal sub-category pills + Swipe hint
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            pages.forEachIndexed { idx, (label, _) ->
                FilterChip(
                    selected = pagerState.currentPage == idx,
                    onClick = { scope.launch { pagerState.animateScrollToPage(idx) } },
                    label = {
                        Text(
                            text = "${idx + 1}. $label",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    shape = CircleShape,
                    modifier = Modifier.testTag("${tagPrefix}_tab_$idx")
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) { pageIdx ->
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.72f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(22.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    pages[pageIdx].second()
                }
            }
        }

        // Crisp page dot indicator + swipe direction hint
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.swipeHintLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { dotIdx ->
                    val selected = pagerState.currentPage == dotIdx
                    Box(
                        modifier = Modifier
                            .height(7.dp)
                            .width(if (selected) 20.dp else 7.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }
        }
    }
}

/**
 * 1-Tap Aesthetic Theme Presets Row (Pure Material You, Liquid Crystal, AMOLED Midnight, Emerald Serenity)
 */
@Composable
fun QuickAestheticPresetsRow(
    strings: AppStrings,
    onApplyPreset: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val presets = listOf(
        Triple("material_you", strings.presetMaterialYou, Color(0xFF6750A4)),
        Triple("liquid_crystal", strings.presetLiquidCrystal, Color(0xFF0077B6)),
        Triple("amoled_midnight", strings.presetAmoledMidnight, Color(0xFF141218)),
        Triple("emerald_mosque", strings.presetEmeraldMosque, Color(0xFF0D7350)),
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = strings.quickPresetsTitle,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { (key, title, swatch) ->
                Surface(
                    onClick = { onApplyPreset(key) },
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(1.dp, swatch.copy(alpha = 0.55f)),
                    modifier = Modifier.testTag("theme_preset_$key")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(swatch)
                        )
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive 6-Widget Home Screen Showcase Carousel!
 * Displays live Material You previews for ALL 6 real Android AppWidgetProviders with 1-tap Pin buttons.
 */
@Composable
fun SixWidgetsShowcaseSection(
    settings: AppSettings,
    playbackState: PlaybackUiState,
    strings: AppStrings,
    onPostBanner: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 6 })
    val scope = rememberCoroutineScope()

    val widgetNames = listOf(
        "1. ${strings.widgetCompactTitle}",
        "2. ${strings.widgetHubTitle}",
        "3. ${strings.widget3Title}",
        "4. ${strings.widget4Title}",
        "5. ${strings.widget5Title}",
        "6. ${strings.widget6Title}",
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = strings.widgetsSixGallerySubtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Quick Widget Selector Pills (1..6)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            widgetNames.forEachIndexed { idx, label ->
                FilterChip(
                    selected = pagerState.currentPage == idx,
                    onClick = { scope.launch { pagerState.animateScrollToPage(idx) } },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    shape = CircleShape,
                    modifier = Modifier.testTag("widget_tab_${idx + 1}")
                )
            }
        }

        // Swipeable 6-Widget Showcase
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val trackTitle = playbackState.currentTrack?.title ?: strings.appTitle
            val trackArtist = playbackState.currentTrack?.artist ?: strings.nowPlaying

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (page) {
                        0 -> {
                            // Widget 1: Classic 4x1 Pill Bar
                            WidgetMetaHeader(strings.widgetCompactTitle, "4×1 • Material You Pill")
                            Surface(
                                shape = RoundedCornerShape(28.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Filled.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(trackTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(trackArtist, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Icon(Icons.Filled.SkipPrevious, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(38.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Filled.SkipNext, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                                }
                            }
                            PinWidgetButton(
                                label = strings.addWidgetAction,
                                tag = "settings_pin_widget"
                            ) {
                                val ok = PrismMediaWidgetProvider.requestPinWidget(context)
                                onPostBanner(if (ok) strings.widgetPinnedBanner else strings.widgetManualHint)
                            }
                        }

                        1 -> {
                            // Widget 2: Expressive 4x2 Media Hub
                            WidgetMetaHeader(strings.widgetHubTitle, strings.widgetHubDesc)
                            Surface(
                                shape = RoundedCornerShape(26.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(42.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Filled.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(trackTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(trackArtist, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                                        }
                                        Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(38.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                                            }
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(strings.tabMusic, strings.tabVideos, strings.tabPhotos).forEach { tab ->
                                            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f), modifier = Modifier.weight(1f)) {
                                                Box(modifier = Modifier.padding(vertical = 7.dp), contentAlignment = Alignment.Center) {
                                                    Text(tab, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            PinWidgetButton(
                                label = strings.addHubWidgetAction,
                                tag = "settings_pin_hub_widget"
                            ) {
                                val ok = PrismExpressiveCardWidgetProvider.requestPinWidget(context)
                                onPostBanner(if (ok) strings.widgetPinnedBanner else strings.widgetManualHint)
                            }
                        }

                        2 -> {
                            // Widget 3: Vinyl Square (2x2)
                            WidgetMetaHeader(strings.widget3Title, strings.widget3Desc)
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Surface(
                                    shape = RoundedCornerShape(28.dp),
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                    modifier = Modifier.size(164.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(66.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Filled.Album, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
                                            }
                                        }
                                        Text(trackTitle, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                            PinWidgetButton(
                                label = "${strings.addWidgetAction} • ${strings.widget3Title}",
                                tag = "settings_pin_vinyl_widget"
                            ) {
                                val ok = PrismVinylSquareWidgetProvider.requestPinWidget(context)
                                onPostBanner(if (ok) strings.widgetPinnedBanner else strings.widgetManualHint)
                            }
                        }

                        3 -> {
                            // Widget 4: Quran Khatma & Tasbih (4x2)
                            WidgetMetaHeader(strings.widget4Title, strings.widget4Desc)
                            Surface(
                                shape = RoundedCornerShape(26.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = BorderStroke(1.dp, Color(0xFF0D7350).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color(0xFF0D7350).copy(alpha = 0.16f), Color.Transparent)
                                            )
                                        )
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Filled.MenuBook, contentDescription = null, tint = Color(0xFF0D7350))
                                        Text(
                                            text = "${strings.quranHubTitle} • ${WidgetPlaybackBridge.lastKhatmaPercent}%",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = "${WidgetPlaybackBridge.lastCompletedSurahs}/114 • ${strings.tasbihTitle}: ${settings.tasbihDailyCount}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            PinWidgetButton(
                                label = "${strings.addWidgetAction} • ${strings.widget4Title}",
                                tag = "settings_pin_quran_widget"
                            ) {
                                val ok = PrismQuranWidgetProvider.requestPinWidget(context)
                                onPostBanner(if (ok) strings.widgetPinnedBanner else strings.widgetManualHint)
                            }
                        }

                        4 -> {
                            // Widget 5: Pro Audio Deck (4x2)
                            WidgetMetaHeader(strings.widget5Title, strings.widget5Desc)
                            Surface(
                                shape = RoundedCornerShape(26.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.45f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(trackTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text("${strings.eqPresetName(settings.eqPreset)} • ${playbackState.playbackSpeed}x", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                                        }
                                        Icon(Icons.Filled.Equalizer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.Equalizer, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Text(strings.eqPresetName(settings.eqPreset), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Text("${playbackState.playbackSpeed}x", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                            PinWidgetButton(
                                label = "${strings.addWidgetAction} • ${strings.widget5Title}",
                                tag = "settings_pin_audio_deck_widget"
                            ) {
                                val ok = PrismAudioDeckWidgetProvider.requestPinWidget(context)
                                onPostBanner(if (ok) strings.widgetPinnedBanner else strings.widgetManualHint)
                            }
                        }

                        else -> {
                            // Widget 6: Quick Studio Dock (4x1)
                            WidgetMetaHeader(strings.widget6Title, strings.widget6Desc)
                            Surface(
                                shape = RoundedCornerShape(28.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    listOf(
                                        Icons.Filled.MusicNote,
                                        Icons.Filled.VideoLibrary,
                                        Icons.Filled.PhotoLibrary,
                                        Icons.Filled.MenuBook,
                                        if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow
                                    ).forEachIndexed { idx, ico ->
                                        Surface(
                                            shape = CircleShape,
                                            color = if (idx == 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = ico,
                                                    contentDescription = null,
                                                    tint = if (idx == 4) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            PinWidgetButton(
                                label = "${strings.addWidgetAction} • ${strings.widget6Title}",
                                tag = "settings_pin_quick_studio_widget"
                            ) {
                                val ok = PrismQuickStudioWidgetProvider.requestPinWidget(context)
                                onPostBanner(if (ok) strings.widgetPinnedBanner else strings.widgetManualHint)
                            }
                        }
                    }
                }
            }
        }

        // Dot indicator for the 6 widgets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.swipeHintLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(6) { idx ->
                    val sel = pagerState.currentPage == idx
                    Box(
                        modifier = Modifier
                            .height(7.dp)
                            .width(if (sel) 20.dp else 7.dp)
                            .clip(CircleShape)
                            .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetMetaHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PinWidgetButton(
    label: String,
    tag: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag(tag)
    ) {
        Icon(Icons.Filled.Widgets, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
