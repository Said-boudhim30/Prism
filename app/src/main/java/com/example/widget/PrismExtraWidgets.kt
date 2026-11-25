package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.DynamicShortcutsHelper

/**
 * Widget 3: 2x2 Material You Vinyl Square Media Card.
 */
class PrismVinylSquareWidgetProvider : AppWidgetProvider() {
    companion object {
        fun syncWidget(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, PrismVinylSquareWidgetProvider::class.java))
            if (ids.isEmpty()) return
            for (id in ids) updateAppWidget(context, manager, id)
        }

        fun requestPinWidget(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
            val manager = AppWidgetManager.getInstance(context)
            if (!manager.isRequestPinAppWidgetSupported) return false
            return manager.requestPinAppWidget(ComponentName(context, PrismVinylSquareWidgetProvider::class.java), null, null)
        }

        private fun updateAppWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_prism_vinyl_square)
            val title = WidgetPlaybackBridge.lastTitle ?: context.getString(R.string.app_name)
            val subtitle = WidgetPlaybackBridge.lastSubtitle ?: context.getString(R.string.widget_idle_subtitle)
            val isPlaying = WidgetPlaybackBridge.lastIsPlaying

            views.setTextViewText(R.id.widget_vinyl_title, title)
            views.setTextViewText(R.id.widget_vinyl_artist, subtitle)
            views.setImageViewResource(
                R.id.widget_vinyl_btn_play,
                if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
            )
            views.setInt(
                R.id.widget_vinyl_btn_play,
                "setBackgroundResource",
                if (isPlaying) R.drawable.widget_play_btn_playing_bg else R.drawable.widget_play_btn_bg
            )

            val launchPending = openAppPendingIntent(context, null, null, 300)
            views.setOnClickPendingIntent(R.id.widget_vinyl_root, launchPending)
            views.setOnClickPendingIntent(R.id.widget_vinyl_disc, launchPending)
            views.setOnClickPendingIntent(R.id.widget_vinyl_btn_play, broadcastPending(context, PrismVinylSquareWidgetProvider::class.java, PrismMediaWidgetProvider.ACTION_PLAY_PAUSE, 301))
            views.setOnClickPendingIntent(R.id.widget_vinyl_btn_next, broadcastPending(context, PrismVinylSquareWidgetProvider::class.java, PrismMediaWidgetProvider.ACTION_NEXT, 302))
            views.setOnClickPendingIntent(R.id.widget_vinyl_btn_prev, broadcastPending(context, PrismVinylSquareWidgetProvider::class.java, PrismMediaWidgetProvider.ACTION_PREV, 303))
            manager.updateAppWidget(widgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) updateAppWidget(context, appWidgetManager, id)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        handleMediaBroadcastAction(context, intent.action)
    }
}

/**
 * Widget 4: 4x2 Quran Khatma Progress & Daily Tasbih Counter Widget.
 */
class PrismQuranWidgetProvider : AppWidgetProvider() {
    companion object {
        fun syncWidget(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, PrismQuranWidgetProvider::class.java))
            if (ids.isEmpty()) return
            for (id in ids) updateAppWidget(context, manager, id)
        }

        fun requestPinWidget(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
            val manager = AppWidgetManager.getInstance(context)
            if (!manager.isRequestPinAppWidgetSupported) return false
            return manager.requestPinAppWidget(ComponentName(context, PrismQuranWidgetProvider::class.java), null, null)
        }

        private fun updateAppWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_prism_quran)
            val completed = WidgetPlaybackBridge.lastCompletedSurahs.coerceIn(0, 114)
            val percent = WidgetPlaybackBridge.lastKhatmaPercent.coerceIn(0, 100)
            val tasbih = WidgetPlaybackBridge.lastTasbihCount.coerceAtLeast(0)

            views.setTextViewText(R.id.widget_quran_surah_summary, "$completed / 114 • $percent%")
            views.setTextViewText(R.id.widget_quran_tasbih_badge, "📿 $tasbih")
            views.setProgressBar(R.id.widget_quran_progress, 100, percent, false)

            val hubIntent = openAppPendingIntent(context, null, DynamicShortcutsHelper.ACTION_QURAN_HUB, 401)
            val tasbihIntent = openAppPendingIntent(context, null, "tasbih_counter", 402)
            views.setOnClickPendingIntent(R.id.widget_quran_root, hubIntent)
            views.setOnClickPendingIntent(R.id.widget_quran_btn_hub, hubIntent)
            views.setOnClickPendingIntent(R.id.widget_quran_btn_tasbih, tasbihIntent)
            views.setOnClickPendingIntent(R.id.widget_quran_tasbih_badge, tasbihIntent)
            manager.updateAppWidget(widgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) updateAppWidget(context, appWidgetManager, id)
    }
}

/**
 * Widget 5: 4x2 Pro Audio Studio Control Deck (Live EQ preset, Speed, and Listening Stats shortcut).
 */
class PrismAudioDeckWidgetProvider : AppWidgetProvider() {
    companion object {
        fun syncWidget(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, PrismAudioDeckWidgetProvider::class.java))
            if (ids.isEmpty()) return
            for (id in ids) updateAppWidget(context, manager, id)
        }

        fun requestPinWidget(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
            val manager = AppWidgetManager.getInstance(context)
            if (!manager.isRequestPinAppWidgetSupported) return false
            return manager.requestPinAppWidget(ComponentName(context, PrismAudioDeckWidgetProvider::class.java), null, null)
        }

        private fun updateAppWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_prism_audio_deck)
            val title = WidgetPlaybackBridge.lastTitle ?: context.getString(R.string.app_name)
            val subtitle = WidgetPlaybackBridge.lastSubtitle ?: context.getString(R.string.widget_idle_subtitle)
            val isPlaying = WidgetPlaybackBridge.lastIsPlaying

            views.setTextViewText(R.id.widget_deck_title, title)
            views.setTextViewText(R.id.widget_deck_artist, subtitle)
            views.setTextViewText(R.id.widget_deck_eq_badge, WidgetPlaybackBridge.lastEqPresetLabel)
            views.setTextViewText(R.id.widget_deck_speed_badge, WidgetPlaybackBridge.lastSpeedLabel)
            views.setImageViewResource(
                R.id.widget_deck_btn_play,
                if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
            )
            views.setInt(
                R.id.widget_deck_btn_play,
                "setBackgroundResource",
                if (isPlaying) R.drawable.widget_play_btn_playing_bg else R.drawable.widget_play_btn_bg
            )

            views.setOnClickPendingIntent(R.id.widget_deck_root, openAppPendingIntent(context, "MUSIC", null, 500))
            views.setOnClickPendingIntent(R.id.widget_deck_eq_badge, openAppPendingIntent(context, "MUSIC", "open_eq", 501))
            views.setOnClickPendingIntent(R.id.widget_deck_stats_btn, openAppPendingIntent(context, null, DynamicShortcutsHelper.ACTION_LISTENING_STATS, 502))
            views.setOnClickPendingIntent(R.id.widget_deck_btn_play, broadcastPending(context, PrismAudioDeckWidgetProvider::class.java, PrismMediaWidgetProvider.ACTION_PLAY_PAUSE, 503))
            views.setOnClickPendingIntent(R.id.widget_deck_btn_next, broadcastPending(context, PrismAudioDeckWidgetProvider::class.java, PrismMediaWidgetProvider.ACTION_NEXT, 504))
            views.setOnClickPendingIntent(R.id.widget_deck_btn_prev, broadcastPending(context, PrismAudioDeckWidgetProvider::class.java, PrismMediaWidgetProvider.ACTION_PREV, 505))
            manager.updateAppWidget(widgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) updateAppWidget(context, appWidgetManager, id)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        handleMediaBroadcastAction(context, intent.action)
    }
}

/**
 * Widget 6: 4x1 Quick Launch Studio Dock (Music, Videos, Private Vault, Local Wi-Fi Transfer).
 */
class PrismQuickStudioWidgetProvider : AppWidgetProvider() {
    companion object {
        fun syncWidget(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, PrismQuickStudioWidgetProvider::class.java))
            if (ids.isEmpty()) return
            for (id in ids) updateAppWidget(context, manager, id)
        }

        fun requestPinWidget(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
            val manager = AppWidgetManager.getInstance(context)
            if (!manager.isRequestPinAppWidgetSupported) return false
            return manager.requestPinAppWidget(ComponentName(context, PrismQuickStudioWidgetProvider::class.java), null, null)
        }

        private fun updateAppWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_prism_quick_studio)
            views.setOnClickPendingIntent(R.id.widget_dock_music, openAppPendingIntent(context, "MUSIC", null, 601))
            views.setOnClickPendingIntent(R.id.widget_dock_videos, openAppPendingIntent(context, "VIDEOS", null, 602))
            views.setOnClickPendingIntent(R.id.widget_dock_vault, openAppPendingIntent(context, null, DynamicShortcutsHelper.ACTION_PRIVATE_VAULT, 603))
            views.setOnClickPendingIntent(R.id.widget_dock_wifi, openAppPendingIntent(context, null, DynamicShortcutsHelper.ACTION_WIFI_TRANSFER, 604))
            manager.updateAppWidget(widgetId, views)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) updateAppWidget(context, appWidgetManager, id)
    }
}

private fun openAppPendingIntent(
    context: Context,
    tabName: String?,
    quickAction: String?,
    reqCode: Int,
): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        if (tabName != null) putExtra(PrismExpressiveCardWidgetProvider.EXTRA_OPEN_TAB, tabName)
        if (quickAction != null) putExtra(DynamicShortcutsHelper.EXTRA_QUICK_ACTION, quickAction)
    }
    return PendingIntent.getActivity(
        context,
        reqCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

private fun broadcastPending(
    context: Context,
    receiverClass: Class<*>,
    action: String,
    reqCode: Int,
): PendingIntent {
    val intent = Intent(context, receiverClass).apply { this.action = action }
    return PendingIntent.getBroadcast(
        context,
        reqCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

private fun handleMediaBroadcastAction(context: Context, action: String?) {
    when (action) {
        PrismMediaWidgetProvider.ACTION_PLAY_PAUSE -> {
            val cb = WidgetPlaybackBridge.onPlayPause
            if (cb != null) cb() else launchPrismMain(context)
        }
        PrismMediaWidgetProvider.ACTION_NEXT -> {
            val cb = WidgetPlaybackBridge.onNext
            if (cb != null) cb() else launchPrismMain(context)
        }
        PrismMediaWidgetProvider.ACTION_PREV -> {
            val cb = WidgetPlaybackBridge.onPrev
            if (cb != null) cb() else launchPrismMain(context)
        }
    }
}

private fun launchPrismMain(context: Context) {
    val launch = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    context.startActivity(launch)
}
