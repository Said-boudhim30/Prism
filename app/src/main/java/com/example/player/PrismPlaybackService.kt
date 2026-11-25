package com.example.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.model.AudioTrack

/**
 * Dedicated Android Foreground Media Playback Service with native system MediaSession.
 *
 * Keeps music alive reliably when the screen is off or the user switches apps, and bridges:
 * - Android 13-17 Material You Lock-Screen Media Card & Squiggly Seek Bar
 * - Bluetooth Headset hardware buttons (Play / Pause / Next / Prev)
 * - Car Bluetooth / Smartwatch Now-Playing metadata & artwork
 */
class PrismPlaybackService : Service() {

    companion object {
        const val CHANNEL_ID = "prism_media_playback"
        const val NOTIFICATION_ID = 4201

        const val ACTION_START_OR_UPDATE = "com.example.service.ACTION_START_OR_UPDATE"
        const val ACTION_STOP_SERVICE = "com.example.service.ACTION_STOP_SERVICE"

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_ALBUM = "extra_album"
        const val EXTRA_EXT = "extra_ext"
        const val EXTRA_URI = "extra_uri"
        const val EXTRA_ART_RES = "extra_art_res"
        const val EXTRA_IS_PLAYING = "extra_is_playing"
        const val EXTRA_POSITION_MS = "extra_position_ms"
        const val EXTRA_DURATION_MS = "extra_duration_ms"
        const val EXTRA_SPEED = "extra_speed"
        const val EXTRA_LOCK_CONTROLS_ENABLED = "extra_lock_controls_enabled"
        const val EXTRA_LOCK_SHOW_ARTWORK = "extra_lock_show_artwork"
        const val EXTRA_LOCK_SHOW_IDENTITY = "extra_lock_show_identity"
        const val EXTRA_GENRE_BADGE = "extra_genre_badge"

        fun syncPlayback(
            context: Context,
            track: AudioTrack?,
            isPlaying: Boolean,
            positionMs: Long,
            durationMs: Long,
            speed: Float = 1.0f,
            notificationsEnabled: Boolean = true,
            lockScreenControlsEnabled: Boolean = true,
            lockScreenShowArtwork: Boolean = true,
            lockScreenShowIdentity: Boolean = true,
            genreBadge: String = "",
        ) {
            if (track == null || (!notificationsEnabled && !isPlaying)) {
                stopService(context)
                return
            }
            val intent = Intent(context, PrismPlaybackService::class.java).apply {
                action = ACTION_START_OR_UPDATE
                putExtra(EXTRA_TITLE, track.title)
                putExtra(EXTRA_ARTIST, track.artist)
                putExtra(EXTRA_ALBUM, track.album)
                putExtra(EXTRA_EXT, track.extension)
                putExtra(EXTRA_URI, track.uriString)
                putExtra(EXTRA_ART_RES, track.artworkResId ?: 0)
                putExtra(EXTRA_IS_PLAYING, isPlaying)
                putExtra(EXTRA_POSITION_MS, positionMs)
                putExtra(EXTRA_DURATION_MS, durationMs.coerceAtLeast(1L))
                putExtra(EXTRA_SPEED, speed)
                putExtra(EXTRA_LOCK_CONTROLS_ENABLED, lockScreenControlsEnabled)
                putExtra(EXTRA_LOCK_SHOW_ARTWORK, lockScreenShowArtwork)
                putExtra(EXTRA_LOCK_SHOW_IDENTITY, lockScreenShowIdentity)
                putExtra(EXTRA_GENRE_BADGE, genreBadge)
            }
            runCatching {
                if (isPlaying && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }
            }
        }

        fun stopService(context: Context) {
            runCatching {
                context.stopService(Intent(context, PrismPlaybackService::class.java))
            }
        }
    }

    private var mediaSession: MediaSession? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var cachedArtUri: String? = null
    private var cachedArtResId: Int = 0
    private var cachedBitmap: Bitmap? = null

    private fun acquirePlaybackWakeLock() {
        runCatching {
            val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return
            if (wakeLock == null) {
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PrismMedia:PlaybackWakeLock").apply {
                    setReferenceCounted(false)
                }
            }
            if (wakeLock?.isHeld == false) {
                // Max 6 hours safety guard; refreshed on track changes and released immediately on pause/stop
                wakeLock?.acquire(6L * 60L * 60L * 1000L)
            }
        }
    }

    private fun releasePlaybackWakeLock() {
        runCatching {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        ensureChannel()
        val session = MediaSession(this, "PrismMediaSession").apply {
            setCallback(object : MediaSession.Callback() {
                override fun onPlay() {
                    MediaNotificationBridge.onPlayPause?.invoke()
                }

                override fun onPause() {
                    MediaNotificationBridge.onPlayPause?.invoke()
                }

                override fun onSkipToNext() {
                    MediaNotificationBridge.onNext?.invoke()
                }

                override fun onSkipToPrevious() {
                    MediaNotificationBridge.onPrev?.invoke()
                }

                override fun onSeekTo(pos: Long) {
                    MediaNotificationBridge.onSeekTo?.invoke(pos.coerceAtLeast(0L))
                }

                override fun onStop() {
                    MediaNotificationBridge.onStop?.invoke()
                    stopForegroundSafely()
                    stopSelf()
                }
            })
            isActive = true
        }
        mediaSession = session
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null || intent.action == ACTION_STOP_SERVICE) {
            stopForegroundSafely()
            stopSelf()
            return START_NOT_STICKY
        }

        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty().ifBlank { getString(R.string.app_name) }
        val artist = intent.getStringExtra(EXTRA_ARTIST).orEmpty()
        val album = intent.getStringExtra(EXTRA_ALBUM).orEmpty()
        val ext = intent.getStringExtra(EXTRA_EXT).orEmpty()
        val uriStr = intent.getStringExtra(EXTRA_URI).orEmpty()
        val artResId = intent.getIntExtra(EXTRA_ART_RES, 0)
        val isPlaying = intent.getBooleanExtra(EXTRA_IS_PLAYING, false)
        val positionMs = intent.getLongExtra(EXTRA_POSITION_MS, 0L).coerceAtLeast(0L)
        val durationMs = intent.getLongExtra(EXTRA_DURATION_MS, 1L).coerceAtLeast(1L)
        val speed = intent.getFloatExtra(EXTRA_SPEED, 1.0f)
        val lockControls = intent.getBooleanExtra(EXTRA_LOCK_CONTROLS_ENABLED, true)
        val lockShowArt = intent.getBooleanExtra(EXTRA_LOCK_SHOW_ARTWORK, true)
        val lockShowIdentity = intent.getBooleanExtra(EXTRA_LOCK_SHOW_IDENTITY, true)
        val genreBadge = intent.getStringExtra(EXTRA_GENRE_BADGE).orEmpty()

        val artBitmap = if (lockShowArt) resolveTrackArtwork(uriStr, artResId) else null
        val displayAlbum = if (lockShowIdentity && genreBadge.isNotBlank()) {
            "Prism Media • $genreBadge"
        } else if (lockShowIdentity) {
            "Prism Media • ${album.ifBlank { ext }}"
        } else {
            album
        }
        updateMediaSession(title, artist, displayAlbum, durationMs, positionMs, isPlaying, speed, artBitmap)

        val notification = buildMediaStyleNotification(
            title = title,
            artist = artist,
            ext = ext,
            isPlaying = isPlaying,
            artwork = artBitmap,
            lockControlsEnabled = lockControls,
            lockShowIdentity = lockShowIdentity,
            genreBadge = genreBadge,
        )

        if (isPlaying) {
            acquirePlaybackWakeLock()
            startForegroundWithType(notification)
            return START_STICKY
        } else {
            releasePlaybackWakeLock()
            // Keep notification dismissible when paused while maintaining MediaSession metadata
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_DETACH)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(false)
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            runCatching { nm?.notify(NOTIFICATION_ID, notification) }
            return START_NOT_STICKY
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val state = mediaSession?.controller?.playbackState?.state
        if (state != PlaybackState.STATE_PLAYING) {
            releasePlaybackWakeLock()
            stopForegroundSafely()
            stopSelf()
        }
    }

    override fun onDestroy() {
        releasePlaybackWakeLock()
        mediaSession?.isActive = false
        mediaSession?.release()
        mediaSession = null
        cachedBitmap = null
        super.onDestroy()
    }

    private fun startForegroundWithType(notification: Notification) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        }
    }

    private fun stopForegroundSafely() {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                @Suppress("DEPRECATION")
                stopForeground(true)
            }
        }
    }

    private fun updateMediaSession(
        title: String,
        artist: String,
        album: String,
        durationMs: Long,
        positionMs: Long,
        isPlaying: Boolean,
        speed: Float,
        artwork: Bitmap?,
    ) {
        val session = mediaSession ?: return
        val metadataBuilder = MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, title)
            .putString(MediaMetadata.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadata.METADATA_KEY_ALBUM, album)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, durationMs)
        if (artwork != null) {
            metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, artwork)
        }
        session.setMetadata(metadataBuilder.build())

        val actions = PlaybackState.ACTION_PLAY or
            PlaybackState.ACTION_PAUSE or
            PlaybackState.ACTION_PLAY_PAUSE or
            PlaybackState.ACTION_SKIP_TO_NEXT or
            PlaybackState.ACTION_SKIP_TO_PREVIOUS or
            PlaybackState.ACTION_SEEK_TO or
            PlaybackState.ACTION_STOP

        val playbackState = PlaybackState.Builder()
            .setActions(actions)
            .setState(
                if (isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED,
                positionMs,
                if (isPlaying) speed else 0f
            )
            .build()
        session.setPlaybackState(playbackState)
    }

    private fun buildMediaStyleNotification(
        title: String,
        artist: String,
        ext: String,
        isPlaying: Boolean,
        artwork: Bitmap?,
        lockControlsEnabled: Boolean = true,
        lockShowIdentity: Boolean = true,
        genreBadge: String = "",
    ): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPending = PendingIntent.getActivity(
            this,
            400,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        val mediaStyle = Notification.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
        mediaSession?.sessionToken?.let { token ->
            mediaStyle.setMediaSession(token)
        }

        val subtitle = buildString {
            append(artist)
            if (ext.isNotBlank()) append(" • ").append(ext)
            if (lockShowIdentity && genreBadge.isNotBlank()) append(" • ").append(genreBadge)
        }
        val identityText = if (lockShowIdentity) "Prism Media" else getString(R.string.app_name)

        builder
            .setSmallIcon(R.drawable.ic_stat_prism_music)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setSubText(identityText)
            .setContentIntent(contentPending)
            .setVisibility(if (lockControlsEnabled) Notification.VISIBILITY_PUBLIC else Notification.VISIBILITY_SECRET)
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .setCategory(Notification.CATEGORY_TRANSPORT)
            .setStyle(mediaStyle)
            .addAction(
                Notification.Action.Builder(
                    android.graphics.drawable.Icon.createWithResource(this, R.drawable.ic_widget_prev),
                    getString(R.string.widget_prev_desc),
                    broadcastAction(MediaNotificationManager.ACTION_PREV, 401)
                ).build()
            )
            .addAction(
                Notification.Action.Builder(
                    android.graphics.drawable.Icon.createWithResource(
                        this,
                        if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
                    ),
                    getString(R.string.widget_play_pause_desc),
                    broadcastAction(MediaNotificationManager.ACTION_PLAY_PAUSE, 402)
                ).build()
            )
            .addAction(
                Notification.Action.Builder(
                    android.graphics.drawable.Icon.createWithResource(this, R.drawable.ic_widget_next),
                    getString(R.string.widget_next_desc),
                    broadcastAction(MediaNotificationManager.ACTION_NEXT, 403)
                ).build()
            )
            .addAction(
                Notification.Action.Builder(
                    android.graphics.drawable.Icon.createWithResource(this, android.R.drawable.ic_menu_close_clear_cancel),
                    getString(R.string.notif_stop_desc),
                    broadcastAction(MediaNotificationManager.ACTION_STOP, 404)
                ).build()
            )

        if (artwork != null) {
            builder.setLargeIcon(artwork)
        }

        return builder.build()
    }

    private fun resolveTrackArtwork(uriStr: String, artResId: Int): Bitmap? {
        if (uriStr == cachedArtUri && artResId == cachedArtResId && cachedBitmap != null) {
            return cachedBitmap
        }
        val decoded = runCatching {
            val raw = when {
                artResId != 0 -> BitmapFactory.decodeResource(resources, artResId)
                uriStr.isNotBlank() -> {
                    val retriever = MediaMetadataRetriever()
                    try {
                        retriever.setDataSource(this, Uri.parse(uriStr))
                        val bytes = retriever.embeddedPicture
                        if (bytes != null) {
                            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
                            var sample = 1
                            val maxDim = maxOf(opts.outWidth, opts.outHeight)
                            while (maxDim / sample > 512) sample *= 2
                            BitmapFactory.decodeByteArray(
                                bytes,
                                0,
                                bytes.size,
                                BitmapFactory.Options().apply { inSampleSize = sample }
                            )
                        } else null
                    } finally {
                        runCatching { retriever.release() }
                    }
                }
                else -> null
            }
            if (raw != null && (raw.width > 320 || raw.height > 320)) {
                Bitmap.createScaledBitmap(raw, 256, 256, true).also {
                    if (it !== raw) raw.recycle()
                }
            } else raw
        }.getOrNull() ?: runCatching {
            BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        }.getOrNull()

        cachedArtUri = uriStr
        cachedArtResId = artResId
        cachedBitmap = decoded
        return decoded
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.notif_channel_playback),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = getString(R.string.notif_channel_playback_desc)
                    setShowBadge(false)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                nm.createNotificationChannel(channel)
            }
        }
    }

    private fun broadcastAction(action: String, reqCode: Int): PendingIntent {
        val intent = Intent(this, MediaNotificationReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            this,
            reqCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
