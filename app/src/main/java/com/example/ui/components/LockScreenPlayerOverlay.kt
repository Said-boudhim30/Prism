package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MediaScanner
import com.example.localization.AppStrings
import com.example.model.AppSettings
import com.example.model.AudioTrack
import com.example.model.MediaKnowledgeReport
import com.example.model.PlaybackUiState
import com.example.ui.theme.AlbumArtPalette
import com.example.ui.theme.AlbumArtPaletteExtractor
import com.example.ui.theme.TrackGenrePaletteEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Full-Screen Lock-Screen Experience & Live Test Overlay.
 * Displays Prism Media's branded identity, Expressive Material 3 Clock,
 * genre-reactive backdrop, Expressive Artwork Shape, live synced lyric line,
 * and direct playback transport controls while the device is locked.
 */
@Composable
fun LockScreenPlayerOverlay(
    playbackState: PlaybackUiState,
    positionMsFlow: StateFlow<Long>,
    settings: AppSettings,
    lyricsText: String?,
    knowledgeReport: MediaKnowledgeReport?,
    strings: AppStrings,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleFavorite: (AudioTrack) -> Unit,
    onDismissToApp: () -> Unit,
) {
    BackHandler(onBack = onDismissToApp)
    val context = LocalContext.current
    val cs = MaterialTheme.colorScheme
    val track = playbackState.currentTrack

    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            nowMs = System.currentTimeMillis()
            delay(15_000L)
        }
    }
    val timeText = remember(nowMs) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(nowMs))
    }
    val dateText = remember(nowMs) {
        SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(Date(nowMs))
    }

    var artPalette by remember(track?.id) { mutableStateOf<AlbumArtPalette?>(null) }
    LaunchedEffect(track?.id, settings.albumArtTintEnabled) {
        artPalette = if (track != null && settings.albumArtTintEnabled) {
            AlbumArtPaletteExtractor.extractForTrack(context, track)
        } else null
    }
    val genrePal = remember(track?.id, knowledgeReport?.mediaId, artPalette, settings.darkTheme, settings.playerGenreDynamicColors, settings.albumArtTintEnabled) {
        TrackGenrePaletteEngine.resolveCombinedPalette(
            track = track,
            report = knowledgeReport,
            artPalette = artPalette,
            isDark = true,
            genreColorsEnabled = settings.playerGenreDynamicColors,
            albumTintEnabled = settings.albumArtTintEnabled,
            fallbackPrimary = cs.primary,
            fallbackSecondary = cs.tertiary,
            fallbackDeep = Color(0xFF0A0D18),
        )
    }
    val accentColor by animateColorAsState(genrePal.primaryAccent, tween(600), label = "lockAccent")
    val deepColor by animateColorAsState(genrePal.deepBackdrop, tween(600), label = "lockDeep")

    val positionMs by positionMsFlow.collectAsStateWithLifecycle()
    val durationMs = playbackState.durationMs.coerceAtLeast(1L)
    val progress = (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

    // Extract active lyric line if lyrics are synced or plain
    val activeLyricLine = remember(lyricsText, positionMs / 800L) {
        if (lyricsText.isNullOrBlank()) null
        else {
            val parsed = LrcLyricsParser.parse(lyricsText)
            if (parsed.isNotEmpty()) {
                val idx = LrcLyricsParser.activeLineIndex(parsed, positionMs).coerceAtLeast(0)
                parsed.getOrNull(idx)?.text ?: parsed.first().text
            } else {
                lyricsText.lines().firstOrNull { it.isNotBlank() }?.trim()
            }
        }
    }

    val backdropBrush = if (settings.lockScreenBlurBackdrop) {
        Brush.verticalGradient(
            colors = listOf(
                deepColor.copy(alpha = 0.96f),
                accentColor.copy(alpha = 0.28f),
                Color(0xFF06070D).copy(alpha = 0.98f),
            )
        )
    } else {
        Brush.verticalGradient(listOf(Color(0xFF07080E), Color(0xFF0B0E17)))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backdropBrush)
            .testTag("lock_screen_player_overlay")
    ) {
        if (settings.lockScreenBlurBackdrop) {
            MaterialLiquidAmbientBackdrop(
                isDarkTheme = true,
                amoledBlack = false,
                isPlaying = playbackState.isPlaying,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .displayCutoutPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Prism Identity Pill + Expressive Lock-Screen Clock
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (settings.lockScreenShowPrismIdentity) {
                    Surface(
                        shape = CircleShape,
                        color = accentColor.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f)),
                        modifier = Modifier.testTag("lock_screen_prism_identity")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Prism Media • ${strings.genreMoodName(genrePal.mood)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                if (settings.lockScreenShowClock) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = timeText,
                        fontSize = if (settings.lockScreenCompactMode) 56.sp else 72.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-2).sp,
                        modifier = Modifier.testTag("lock_screen_clock")
                    )
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.78f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Center / Bottom Card: Full Expressive OR Compact Sleek Lock-Screen Player
            Surface(
                shape = RoundedCornerShape(32.dp),
                color = Color(0xFF111524).copy(alpha = 0.78f),
                border = BorderStroke(1.2.dp, accentColor.copy(alpha = 0.35f)),
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (settings.lockScreenCompactMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (settings.lockScreenShowArtwork && track != null) {
                                val shape = PrismExpressiveShapes.artworkShapeFor(settings.playerArtworkStyle, settings.shapeRoundness)
                                TrackArtworkImage(
                                    track = track,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(shape)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track?.title ?: strings.nowPlaying,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = track?.artist ?: "Prism Media",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.74f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (track != null) {
                                IconButton(onClick = { onToggleFavorite(track) }) {
                                    Icon(
                                        imageVector = if (track.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = strings.menuAddFavorite,
                                        tint = if (track.isFavorite) accentColor else Color.White.copy(alpha = 0.72f)
                                    )
                                }
                            }
                        }
                    } else {
                        if (settings.lockScreenShowArtwork && track != null) {
                            val artShape = PrismExpressiveShapes.artworkShapeFor(settings.playerArtworkStyle, settings.shapeRoundness)
                            Box(
                                modifier = Modifier
                                    .size(164.dp)
                                    .clip(artShape)
                                    .background(accentColor.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                TrackArtworkImage(
                                    track = track,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(artShape)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = track?.title ?: strings.nowPlaying,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = track?.artist ?: "Prism Media",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.74f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (settings.lockScreenShowLyrics && !activeLyricLine.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = accentColor.copy(alpha = 0.14f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.GraphicEq,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = activeLyricLine,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.92f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Seek Bar + Timecodes
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SquigglyAudioProgressBar(
                            progress = progress,
                            isPlaying = playbackState.isPlaying,
                            squigglyEnabled = settings.squigglyProgress,
                            onSeekFraction = { frac -> onSeek((frac * durationMs).toLong()) },
                            activeColor = accentColor,
                            inactiveColor = Color.White.copy(alpha = 0.22f),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = MediaScanner.formatDuration(positionMs),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = MediaScanner.formatDuration(durationMs),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Bottom Transport Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPrevious,
                            modifier = Modifier
                                .size(50.dp)
                                .testTag("lock_screen_prev")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipPrevious,
                                contentDescription = "Previous",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        MorphingPlayPauseButton(
                            isPlaying = playbackState.isPlaying,
                            onClick = onTogglePlayPause,
                            size = 68.dp,
                            iconSize = 34.dp,
                            modifier = Modifier.testTag("lock_screen_play_pause")
                        )

                        IconButton(
                            onClick = onNext,
                            modifier = Modifier
                                .size(50.dp)
                                .testTag("lock_screen_next")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipNext,
                                contentDescription = "Next",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Unlock / Return to Prism Pill
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onDismissToApp)
                    .testTag("lock_screen_unlock_pill")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LockOpen,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = strings.lockScreenUnlockAction,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
