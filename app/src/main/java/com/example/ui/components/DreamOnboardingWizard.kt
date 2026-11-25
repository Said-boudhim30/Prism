package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreateNewFolder
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material.icons.rounded.PictureInPictureAlt
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.RestoreFromTrash
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SurroundSound
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.localization.AppStrings
import com.example.model.AccentPalette
import com.example.model.AppLanguage
import com.example.model.AppSettings
import com.example.model.LiquidGlassStyle
import com.example.model.ScanProgressState
import com.example.model.WatchedFolder
import com.example.ui.theme.palettePreviewColor
import kotlinx.coroutines.launch

/**
 * Dreamlike 4-Page First-Launch Material 3 Expressive Onboarding Wizard.
 * Combines bespoke artwork, expressive Material icons, interactive feature showcase cards,
 * live first-time feature toggles & theme selector, and one-tap SAF folder connection.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DreamOnboardingWizard(
    settings: AppSettings,
    watchedFolders: List<WatchedFolder>,
    scanProgress: ScanProgressState,
    strings: AppStrings,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkThemeChange: (Boolean) -> Unit,
    onAmoledBlackChange: (Boolean) -> Unit,
    onLiquidGlassStyleChange: (LiquidGlassStyle) -> Unit,
    onPaletteChange: (AccentPalette) -> Unit,
    onAutoScanChange: (Boolean) -> Unit,
    onAutoPipChange: (Boolean) -> Unit,
    onAutoAudioProfilesChange: (Boolean) -> Unit,
    onOnlineEnrichmentChange: (Boolean) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onAddFolder: () -> Unit,
    onRemoveFolder: (String) -> Unit,
    onFinishSetup: (loadDemo: Boolean) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    val scope = rememberCoroutineScope()
    val cs = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
            .testTag("dream_onboarding_wizard")
    ) {
        // Dreamlike luminous aurora + stardust backdrop behind the wizard
        MaterialLiquidAmbientBackdrop(
            isDarkTheme = settings.darkTheme,
            amoledBlack = settings.amoledBlack,
            isPlaying = true,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .displayCutoutPadding()
                .navigationBarsPadding()
        ) {
            // Top bar with page progress pills & Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { idx ->
                        val active = pagerState.currentPage == idx
                        val width by animateDpAsState(
                            targetValue = if (active) 28.dp else 8.dp,
                            label = "pillWidth"
                        )
                        val color by animateColorAsState(
                            targetValue = if (active) cs.primary else cs.outlineVariant.copy(alpha = 0.65f),
                            label = "pillColor"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    scope.launch { pagerState.animateScrollToPage(idx) }
                                }
                        )
                    }
                }

                TextButton(
                    onClick = { onFinishSetup(false) },
                    modifier = Modifier.testTag("onboarding_skip_btn")
                ) {
                    Text(strings.onboardingSkipAction, fontWeight = FontWeight.SemiBold)
                }
            }

            // 4-Page Swipeable HorizontalPager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> OnboardingWelcomePage(
                        settings = settings,
                        strings = strings,
                        onLanguageChange = onLanguageChange
                    )
                    1 -> OnboardingFeatureCardsPage(strings = strings)
                    2 -> OnboardingFirstTimeSetupPage(
                        settings = settings,
                        strings = strings,
                        onDarkThemeChange = onDarkThemeChange,
                        onAmoledBlackChange = onAmoledBlackChange,
                        onLiquidGlassStyleChange = onLiquidGlassStyleChange,
                        onPaletteChange = onPaletteChange,
                        onAutoScanChange = onAutoScanChange,
                        onAutoPipChange = onAutoPipChange,
                        onAutoAudioProfilesChange = onAutoAudioProfilesChange,
                        onOnlineEnrichmentChange = onOnlineEnrichmentChange,
                        onNotificationsChange = onNotificationsChange,
                        onHapticsChange = onHapticsChange,
                    )
                    else -> OnboardingConnectLibraryPage(
                        watchedFolders = watchedFolders,
                        scanProgress = scanProgress,
                        strings = strings,
                        onAddFolder = onAddFolder,
                        onRemoveFolder = onRemoveFolder,
                        onFinishSetup = onFinishSetup
                    )
                }
            }

            // Bottom Expressive Navigation Footer
            MaterialLiquidGlassSurface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        OutlinedButton(
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                            },
                            shape = CircleShape,
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("onboarding_back_btn")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = strings.onboardingBackAction,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(strings.onboardingBackAction, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Spacer(Modifier.width(8.dp))
                    }

                    if (pagerState.currentPage < 3) {
                        Button(
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                            },
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 26.dp, vertical = 12.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("onboarding_next_btn")
                        ) {
                            Text(strings.onboardingNextAction, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = strings.onboardingNextAction,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { onFinishSetup(false) },
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("onboarding_enter_btn")
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(strings.onboardingEnterPrismAction, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// PAGE 1: DREAMLIKE WELCOME + INSTANT 4-LANGUAGE SWITCHER + 4 PILLARS
// ============================================================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OnboardingWelcomePage(
    settings: AppSettings,
    strings: AppStrings,
    onLanguageChange: (AppLanguage) -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val pulseTransition = rememberInfiniteTransition(label = "welcomePrismPulse")
    val emblemScale by pulseTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emblemScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dreamlike Hero Artwork Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(30.dp))
                .border(1.2.dp, cs.primary.copy(alpha = 0.35f), RoundedCornerShape(30.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_dream_prism_welcome),
                contentDescription = strings.onboardingWelcomeTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                cs.surface.copy(alpha = 0.25f),
                                cs.surface.copy(alpha = 0.85f)
                            )
                        )
                    )
            )
            // Breathing Luminous Prism Badge
            Surface(
                shape = CircleShape,
                color = cs.primaryContainer.copy(alpha = 0.92f),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp)
                    .size(58.dp)
                    .scale(emblemScale)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.GraphicEq,
                        contentDescription = strings.appTitle,
                        tint = cs.onPrimaryContainer,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        Text(
            text = strings.onboardingWelcomeTitle,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = cs.onSurface
        )

        Text(
            text = strings.onboardingWelcomeSubtitle,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = cs.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Instant 4-Language Selector Pill Card
        MaterialLiquidGlassSurface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Rounded.Language, contentDescription = null, tint = cs.primary, modifier = Modifier.size(18.dp))
                    Text(
                        text = strings.languageSetting,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        FilterChip(
                            selected = settings.language == lang,
                            onClick = { onLanguageChange(lang) },
                            label = {
                                Text(
                                    text = strings.languageDisplayName(lang),
                                    fontWeight = if (settings.language == lang) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = if (settings.language == lang) {
                                { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            modifier = Modifier.testTag("onboarding_lang_${lang.name}")
                        )
                    }
                }
            }
        }

        // 4 Media & Spiritual Pillars Grid (2x2)
        val pillars = listOf(
            Triple(Icons.Rounded.SurroundSound, strings.tabMusic, strings.reverbTitle),
            Triple(Icons.Rounded.Movie, strings.tabVideos, strings.trimVideoAction),
            Triple(Icons.Rounded.PhotoFilter, strings.tabPhotos, strings.photoEditStudioTitle),
            Triple(Icons.Rounded.MenuBook, strings.quranHubTitle, strings.adhkarTitle),
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            pillars.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { (icon, title, sub) ->
                        MaterialLiquidGlassSurface(
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = cs.primaryContainer,
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(icon, contentDescription = null, tint = cs.onPrimaryContainer, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = sub,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = cs.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

// ============================================================================
// PAGE 2: MATERIAL 3 FEATURE SHOWCASE CARDS WITH STUDIO ARTWORK
// ============================================================================

@Composable
private fun OnboardingFeatureCardsPage(strings: AppStrings) {
    val cs = MaterialTheme.colorScheme
    val features = listOf(
        Triple(Icons.Rounded.SurroundSound, strings.proAudioDeckTitle, strings.reverbTitle + " • " + strings.equalizerTitle + " • " + strings.audioProfilesTitle),
        Triple(Icons.Rounded.Movie, strings.settingsVideoSection, strings.videoTrimmerTitle + " • " + strings.extractAudioAction + " • PiP"),
        Triple(Icons.Rounded.PhotoFilter, strings.photoEditStudioTitle, strings.photoArtFilterLabel + " • " + strings.photoExportPdfAction + " • " + strings.photoCompareTitle),
        Triple(Icons.Rounded.Widgets, strings.widgetsSixGalleryTitle, strings.tuckToEdgeAction + " • " + strings.multiWindowTitle),
        Triple(Icons.Rounded.RestoreFromTrash, strings.recycleBinTitle, strings.recycleBinSubtitle + " • " + strings.privateVaultTitle),
        Triple(Icons.Rounded.Shield, strings.taqwaPurityShieldTitle, strings.quranHubTitle + " • " + strings.adhkarTitle),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Studio Showcase Artwork Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp)
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, cs.tertiary.copy(alpha = 0.35f), RoundedCornerShape(26.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_dream_studio_showcase),
                contentDescription = strings.onboardingFeaturesTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = strings.onboardingFeaturesTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = strings.onboardingFeaturesSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.88f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 6 Expressive Material Feature Explanation Cards
        features.forEachIndexed { idx, (icon, title, desc) ->
            val badgeBg = when (idx % 3) {
                0 -> cs.primaryContainer
                1 -> cs.secondaryContainer
                else -> cs.tertiaryContainer
            }
            val badgeFg = when (idx % 3) {
                0 -> cs.onPrimaryContainer
                1 -> cs.onSecondaryContainer
                else -> cs.onTertiaryContainer
            }
            MaterialLiquidGlassSurface(
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = badgeBg,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = null, tint = badgeFg, modifier = Modifier.size(24.dp))
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = cs.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

// ============================================================================
// PAGE 3: FIRST-TIME INTERACTIVE FEATURE & ATMOSPHERE ACTIVATION CARDS
// ============================================================================

@Composable
private fun OnboardingFirstTimeSetupPage(
    settings: AppSettings,
    strings: AppStrings,
    onDarkThemeChange: (Boolean) -> Unit,
    onAmoledBlackChange: (Boolean) -> Unit,
    onLiquidGlassStyleChange: (LiquidGlassStyle) -> Unit,
    onPaletteChange: (AccentPalette) -> Unit,
    onAutoScanChange: (Boolean) -> Unit,
    onAutoPipChange: (Boolean) -> Unit,
    onAutoAudioProfilesChange: (Boolean) -> Unit,
    onOnlineEnrichmentChange: (Boolean) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val isGlass = settings.liquidGlassStyle != LiquidGlassStyle.SOLID_M3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = strings.onboardingSetupTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = strings.onboardingSetupSubtitle,
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant
        )

        // Card A: Visual Engine Choice (Liquid Glass vs True Material You) + Dark/Light/AMOLED
        MaterialLiquidGlassSurface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.BlurOn, contentDescription = null, tint = cs.primary, modifier = Modifier.size(20.dp))
                    Text(strings.visualEngineTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isGlass,
                        onClick = { onLiquidGlassStyleChange(LiquidGlassStyle.MATERIAL_LIQUID) },
                        label = { Text(strings.visualEngineLiquidGlassTitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = if (isGlass) {
                            { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("onboarding_engine_glass")
                    )
                    FilterChip(
                        selected = !isGlass,
                        onClick = { onLiquidGlassStyleChange(LiquidGlassStyle.SOLID_M3) },
                        label = { Text(strings.visualEngineMaterialYouTitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = if (!isGlass) {
                            { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("onboarding_engine_m3")
                    )
                }

                // Light / Dark / AMOLED mode chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !settings.darkTheme,
                        onClick = {
                            onDarkThemeChange(false)
                            onAmoledBlackChange(false)
                        },
                        label = { Text("Light", maxLines = 1) },
                        leadingIcon = { Icon(Icons.Rounded.LightMode, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = settings.darkTheme && !settings.amoledBlack,
                        onClick = {
                            onDarkThemeChange(true)
                            onAmoledBlackChange(false)
                        },
                        label = { Text(strings.darkMode, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        leadingIcon = { Icon(Icons.Rounded.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = settings.darkTheme && settings.amoledBlack,
                        onClick = {
                            onDarkThemeChange(true)
                            onAmoledBlackChange(true)
                        },
                        label = { Text("AMOLED", maxLines = 1) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Live Accent Palette Swatches
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Rounded.Palette, contentDescription = null, tint = cs.primary, modifier = Modifier.size(18.dp))
                    Text(strings.dynamicPalette, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(AccentPalette.entries) { palette ->
                        val selected = settings.accentPalette == palette
                        val swatchColor = if (palette == AccentPalette.SYSTEM) cs.primary else palettePreviewColor(palette)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onPaletteChange(palette) }
                                .padding(4.dp)
                                .testTag("onboarding_palette_${palette.name}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(swatchColor)
                                    .border(
                                        width = if (selected) 3.dp else 1.dp,
                                        color = if (selected) cs.onSurface else cs.outlineVariant,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selected) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = strings.paletteName(palette),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Card B: First-Time Interactive Smart Feature Activations
        OnboardingToggleCard(
            icon = Icons.Rounded.Sync,
            title = strings.autoScanStartupTitle,
            subtitle = strings.firstLaunchAutoScanHint,
            checked = settings.autoScanWatchedFolders,
            onCheckedChange = onAutoScanChange,
            tag = "onboarding_toggle_autoscan"
        )
        OnboardingToggleCard(
            icon = Icons.Rounded.PictureInPictureAlt,
            title = strings.autoPipTitle,
            subtitle = strings.autoPipDesc,
            checked = settings.autoPipEnabled,
            onCheckedChange = onAutoPipChange,
            tag = "onboarding_toggle_autopip"
        )
        OnboardingToggleCard(
            icon = Icons.Rounded.Headphones,
            title = strings.audioProfilesTitle,
            subtitle = strings.audioProfilesDesc,
            checked = settings.autoAudioDeviceProfiles,
            onCheckedChange = onAutoAudioProfilesChange,
            tag = "onboarding_toggle_headset"
        )
        OnboardingToggleCard(
            icon = Icons.Rounded.Public,
            title = strings.onlineEnrichmentTitle,
            subtitle = strings.onlineEnrichmentDesc,
            checked = settings.autoOnlineEnrichment,
            onCheckedChange = onOnlineEnrichmentChange,
            tag = "onboarding_toggle_online"
        )
        OnboardingToggleCard(
            icon = Icons.Rounded.NotificationsActive,
            title = strings.notifSettingTitle,
            subtitle = strings.notifSettingDesc,
            checked = settings.notificationsEnabled,
            onCheckedChange = onNotificationsChange,
            tag = "onboarding_toggle_notif"
        )
        OnboardingToggleCard(
            icon = Icons.Rounded.Vibration,
            title = strings.hapticsTitle,
            subtitle = strings.hapticsDesc,
            checked = settings.hapticsEnabled,
            onCheckedChange = onHapticsChange,
            tag = "onboarding_toggle_haptics"
        )

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun OnboardingToggleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String,
) {
    val cs = MaterialTheme.colorScheme
    MaterialLiquidGlassSurface(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCheckedChange(!checked) }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (checked) cs.primaryContainer else cs.surfaceContainerHighest,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (checked) cs.onPrimaryContainer else cs.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = cs.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(tag)
            )
        }
    }
}

// ============================================================================
// PAGE 4: CONNECT YOUR MEDIA LIBRARY (SAF FOLDERS + DEMO EXPLORATION)
// ============================================================================

@Composable
private fun OnboardingConnectLibraryPage(
    watchedFolders: List<WatchedFolder>,
    scanProgress: ScanProgressState,
    strings: AppStrings,
    onAddFolder: () -> Unit,
    onRemoveFolder: (String) -> Unit,
    onFinishSetup: (loadDemo: Boolean) -> Unit,
) {
    val cs = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = cs.primaryContainer,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.CreateNewFolder,
                    contentDescription = strings.onboardingLibraryTitle,
                    tint = cs.onPrimaryContainer,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Text(
            text = strings.onboardingLibraryTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Text(
            text = strings.firstLaunchSubtitle,
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // Primary Add Folder Action Card
        Button(
            onClick = onAddFolder,
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("onboarding_add_folder_btn")
        ) {
            Icon(Icons.Rounded.CreateNewFolder, contentDescription = null)
            Spacer(Modifier.width(10.dp))
            Text(strings.addWatchFolderAction, fontWeight = FontWeight.Bold)
        }

        if (scanProgress.isScanning) {
            MaterialLiquidGlassSurface(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = strings.scanningLiveCount(scanProgress.discoveredCount),
                        style = MaterialTheme.typography.labelLarge,
                        color = cs.primary,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }

        // Selected Watched Folders List
        MaterialLiquidGlassSurface(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = strings.selectedFoldersLabel(watchedFolders.size),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = cs.primary
                )

                if (watchedFolders.isEmpty()) {
                    Text(
                        text = strings.noFoldersPickedYet,
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                } else {
                    watchedFolders.forEach { folder ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = cs.surfaceContainerHigh,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Rounded.Folder, contentDescription = null, tint = cs.primary)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = folder.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = strings.folderItemsSummary(folder.audioCount, folder.videoCount, folder.photoCount),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = cs.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { onRemoveFolder(folder.uriString) }) {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = strings.deleteAction)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Try Demo Media CTA
        FilledTonalButton(
            onClick = { onFinishSetup(true) },
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("onboarding_load_demo_btn")
        ) {
            Icon(Icons.Rounded.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(strings.loadDemoMedia, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(12.dp))
    }
}
