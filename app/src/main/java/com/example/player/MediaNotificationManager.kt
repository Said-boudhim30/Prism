package com.example.player

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.model.AudioTrack

object MediaNotificationBridge {
    @Volatile
    var onPlayPause: (() -> Unit)? = null

    @Volatile
    var onNext: (() -> Unit)? = null

    @Volatile
    var onPrev: (() -> Unit)? = null

    @Volatile
    var onStop: (() -> Unit)? = null

    @Volatile
    var onSeekTo: ((Long) -> Unit)? = null
}

class MediaNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            MediaNotificationManager.ACTION_PLAY_PAUSE -> MediaNotificationBridge.onPlayPause?.invoke()
            MediaNotificationManager.ACTION_NEXT -> MediaNotificationBridge.onNext?.invoke()
            MediaNotificationManager.ACTION_PREV -> MediaNotificationBridge.onPrev?.invoke()
            MediaNotificationManager.ACTION_STOP -> MediaNotificationBridge.onStop?.invoke()
        }
    }
}

/**
 * Coordinates Prism's Background Foreground Media Service + Android System MediaSession
 * (Bluetooth headset controls, Lock-Screen Material You Media Card, Car/Watch metadata).
 */
object MediaNotificationManager {
    private const val CHANNEL_ID = PrismPlaybackService.CHANNEL_ID
    private const val NOTIFICATION_ID = PrismPlaybackService.NOTIFICATION_ID

    const val ACTION_PLAY_PAUSE = "com.example.notif.ACTION_PLAY_PAUSE"
    const val ACTION_NEXT = "com.example.notif.ACTION_NEXT"
    const val ACTION_PREV = "com.example.notif.ACTION_PREV"
    const val ACTION_STOP = "com.example.notif.ACTION_STOP"

    fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notif_channel_playback),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = context.getString(R.string.notif_channel_playback_desc)
                    setShowBadge(false)
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                }
                nm.createNotificationChannel(channel)
            }
        }
    }

    fun updatePlaybackNotification(
        context: Context,
        track: AudioTrack?,
        isPlaying: Boolean,
        enabled: Boolean,
        positionMs: Long = 0L,
        durationMs: Long = track?.durationMs ?: 1L,
        speed: Float = 1.0f,
        lockScreenControlsEnabled: Boolean = true,
        lockScreenShowArtwork: Boolean = true,
        lockScreenShowIdentity: Boolean = true,
        genreBadge: String = "",
    ) {
        if (track == null) {
            cancel(context)
            return
        }
        // CRITICAL: On Android 13-16, a mediaPlayback foreground service MUST stay active while playing
        // so background and screen-off audio never gets frozen/silenced, even if POST_NOTIFICATIONS is denied
        // or the user disabled standard notification banners.
        if (!isPlaying && (!enabled || !hasNotificationPermission(context))) {
            cancel(context)
            return
        }

        ensureChannel(context)
        // Drive the real Foreground Media Playback Service + MediaSession
        PrismPlaybackService.syncPlayback(
            context = context,
            track = track,
            isPlaying = isPlaying,
            positionMs = positionMs,
            durationMs = durationMs,
            speed = speed,
            notificationsEnabled = enabled || isPlaying,
            lockScreenControlsEnabled = lockScreenControlsEnabled,
            lockScreenShowArtwork = lockScreenShowArtwork,
            lockScreenShowIdentity = lockScreenShowIdentity,
            genreBadge = genreBadge,
        )
    }

    fun cancel(context: Context) {
        PrismPlaybackService.stopService(context)
        runCatching {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        }
    }
}
