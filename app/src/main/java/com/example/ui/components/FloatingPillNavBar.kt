package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.localization.AppStrings
import com.example.model.IconPackStyle
import com.example.model.LiquidGlassStyle
import com.example.model.MediaTab
import com.example.model.NavBarStyle
import com.example.ui.theme.LocalHapticsEnabled

private data class NavTabItem(
    val tab: MediaTab,
    val label: String,
    val roundedIcon: ImageVector,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val tag: String,
)

private fun NavTabItem.resolveIcon(selected: Boolean, style: IconPackStyle): ImageVector = when (style) {
    IconPackStyle.GOOGLE_ROUNDED -> if (selected) roundedIcon else outlinedIcon
    IconPackStyle.FILLED_EXPRESSIVE -> filledIcon
    IconPackStyle.OUTLINED_MINIMAL -> outlinedIcon
    IconPackStyle.DUOTONE_PILL -> if (selected) roundedIcon else filledIcon
}

@Composable
fun FloatingPillNavBar(
    selectedTab: MediaTab,
    strings: AppStrings,
    iconStyle: IconPackStyle = IconPackStyle.GOOGLE_ROUNDED,
    navBarStyle: NavBarStyle = NavBarStyle.FLOATING_PILL,
    tabOrder: List<MediaTab> = MediaTab.entries,
    onSelectTab: (MediaTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val items = remember(strings, tabOrder) {
        val all = mapOf(
            MediaTab.MUSIC to NavTabItem(MediaTab.MUSIC, strings.tabMusic, Icons.Rounded.MusicNote, Icons.Filled.MusicNote, Icons.Outlined.MusicNote, "nav_music"),
            MediaTab.VIDEOS to NavTabItem(MediaTab.VIDEOS, strings.tabVideos, Icons.Rounded.VideoLibrary, Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary, "nav_videos"),
            MediaTab.PHOTOS to NavTabItem(MediaTab.PHOTOS, strings.tabPhotos, Icons.Rounded.PhotoLibrary, Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary, "nav_photos"),
            MediaTab.SETTINGS to NavTabItem(MediaTab.SETTINGS, strings.tabSettings, Icons.Rounded.Settings, Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings"),
        )
        val ordered = (tabOrder + MediaTab.entries).distinct()
        ordered.mapNotNull { all[it] }
    }

    val glassCfg = LocalLiquidGlassConfig.current

    if (navBarStyle == NavBarStyle.DOCKED_MATERIAL_YOU) {
        val barContainer = if (glassCfg.style == LiquidGlassStyle.SOLID_M3) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainer.copy(
                alpha = (0.96f - glassCfg.intensity * 0.14f).coerceIn(0.82f, 0.98f)
            )
        }
        NavigationBar(
            modifier = modifier
                .fillMaxWidth()
                .testTag("docked_m3_nav_bar"),
            containerColor = barContainer,
            tonalElevation = 3.dp,
        ) {
            items.forEach { item ->
                val selected = selectedTab == item.tab
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (hapticsEnabled) {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        onSelectTab(item.tab)
                    },
                    icon = {
                        Icon(
                            imageVector = item.resolveIcon(selected, iconStyle),
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    modifier = Modifier.testTag(item.tag)
                )
            }
        }
        return
    }

    val isCompactDock = navBarStyle == NavBarStyle.COMPACT_DOCK
    val barShape = if (iconStyle == IconPackStyle.DUOTONE_PILL) {
        RoundedCornerShape(scaledCornerDp(28.dp, glassCfg.shapeRoundness))
    } else {
        CircleShape
    }

    Box(
        modifier = modifier
            .navigationBarsPadding()
            .displayCutoutPadding()
            .padding(
                horizontal = if (isCompactDock) 12.dp else 16.dp,
                vertical = if (isCompactDock) 8.dp else 12.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        MaterialLiquidGlassSurface(
            shape = barShape,
            shadowElevation = if (isCompactDock) 10.dp else 14.dp,
            tonalBoost = 1.15f
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = if (isCompactDock) 6.dp else 8.dp,
                    vertical = if (isCompactDock) 4.dp else 6.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(if (isCompactDock) 6.dp else 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = selectedTab == item.tab
                    val containerColor by animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0f)
                        },
                        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        label = "navPillColor"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (selected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        label = "navContentColor"
                    )
                    val horizontalPadding by animateDpAsState(
                        targetValue = when {
                            isCompactDock && selected -> 16.dp
                            isCompactDock -> 12.dp
                            selected -> 18.dp
                            else -> 14.dp
                        },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "navPillWidth"
                    )
                    val iconScale by animateFloatAsState(
                        targetValue = if (selected) 1.12f else 0.96f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "navIconScale"
                    )
                    val interactionSource = remember { MutableInteractionSource() }

                    Surface(
                        shape = barShape,
                        color = containerColor,
                        modifier = Modifier
                            .height(48.dp)
                            .expressivePressScale(interactionSource, pressedScale = 0.91f)
                            .clip(barShape)
                            .minimumInteractiveComponentSize()
                            .testTag(item.tag)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = ripple(bounded = true),
                                onClick = {
                                    if (hapticsEnabled) {
                                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    onSelectTab(item.tab)
                                }
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = horizontalPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            val icon = item.resolveIcon(selected, iconStyle)
                            if (iconStyle == IconPackStyle.DUOTONE_PILL && !selected) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = item.label,
                                        tint = contentColor,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .graphicsLayer {
                                                scaleX = iconScale
                                                scaleY = iconScale
                                            }
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = item.label,
                                    tint = contentColor,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        }
                                )
                            }
                            if (!isCompactDock) {
                                AnimatedVisibility(
                                    visible = selected,
                                    enter = expandHorizontally(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    ) + fadeIn(),
                                    exit = shrinkHorizontally() + fadeOut()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = item.label,
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = contentColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrismNavigationRail(
    selectedTab: MediaTab,
    strings: AppStrings,
    iconStyle: IconPackStyle = IconPackStyle.GOOGLE_ROUNDED,
    tabOrder: List<MediaTab> = MediaTab.entries,
    onSelectTab: (MediaTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val hapticsEnabled = LocalHapticsEnabled.current
    val glassCfg = LocalLiquidGlassConfig.current
    val items = remember(strings, tabOrder) {
        val all = mapOf(
            MediaTab.MUSIC to NavTabItem(MediaTab.MUSIC, strings.tabMusic, Icons.Rounded.MusicNote, Icons.Filled.MusicNote, Icons.Outlined.MusicNote, "rail_music"),
            MediaTab.VIDEOS to NavTabItem(MediaTab.VIDEOS, strings.tabVideos, Icons.Rounded.VideoLibrary, Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary, "rail_videos"),
            MediaTab.PHOTOS to NavTabItem(MediaTab.PHOTOS, strings.tabPhotos, Icons.Rounded.PhotoLibrary, Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary, "rail_photos"),
            MediaTab.SETTINGS to NavTabItem(MediaTab.SETTINGS, strings.tabSettings, Icons.Rounded.Settings, Icons.Filled.Settings, Icons.Outlined.Settings, "rail_settings"),
        )
        val ordered = (tabOrder + MediaTab.entries).distinct()
        ordered.mapNotNull { all[it] }
    }

    val railContainer = if (glassCfg.style == LiquidGlassStyle.SOLID_M3) {
        MaterialTheme.colorScheme.surfaceContainerLow
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow.copy(
            alpha = (0.94f - glassCfg.intensity * 0.2f).coerceIn(0.72f, 0.96f)
        )
    }

    NavigationRail(
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .displayCutoutPadding(),
        containerColor = railContainer
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        items.forEach { item ->
            val selected = selectedTab == item.tab
            NavigationRailItem(
                selected = selected,
                onClick = {
                    if (hapticsEnabled) {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    onSelectTab(item.tab)
                },
                icon = {
                    Icon(
                        imageVector = item.resolveIcon(selected, iconStyle),
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}
