package com.example.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.example.MainActivity
import com.example.R
import com.example.widget.WidgetPlaybackBridge

/**
 * Android Quick Settings Tile for instant one-tap Play/Pause or Prism Media launch
 * directly from the system Quick Settings shade.
 */
@RequiresApi(Build.VERSION_CODES.N)
class PrismQuickTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        syncTileState()
    }

    override fun onClick() {
        super.onClick()
        val toggle = WidgetPlaybackBridge.onPlayPause
        if (toggle != null) {
            toggle.invoke()
            syncTileState()
        } else {
            val launchIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    4101,
                    launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                runCatching { startActivityAndCollapse(pendingIntent) }
            } else {
                @Suppress("DEPRECATION")
                runCatching { startActivityAndCollapse(launchIntent) }
            }
        }
    }

    private fun syncTileState() {
        val tile = qsTile ?: return
        val isPlaying = WidgetPlaybackBridge.lastIsPlaying
        val title = WidgetPlaybackBridge.lastTitle?.takeIf { it.isNotBlank() }

        tile.state = if (isPlaying) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = title ?: getString(R.string.quick_tile_label)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (isPlaying) {
                WidgetPlaybackBridge.lastSubtitle ?: getString(R.string.app_name)
            } else {
                getString(R.string.app_name)
            }
        }
        tile.updateTile()
    }
}
