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

object WidgetPlaybackBridge {
    @Volatile
    var onPlayPause: (() -> Unit)? = null

    @Volatile
    var onNext: (() -> Unit)? = null

    @Volatile
    var onPrev: (() -> Unit)? = null

    @Volatile
    var lastTitle: String? = null

    @Volatile
    var lastSubtitle: String? = null

    @Volatile
    var lastIsPlaying: Boolean = false

    @Volatile
    var lastCompletedSurahs: Int = 0

    @Volatile
    var lastKhatmaPercent: Int = 0

    @Volatile
    var lastTasbihCount: Int = 0

    @Volatile
    var lastEqPresetLabel: String = "EQ: Flat"

    @Volatile
    var lastSpeedLabel: String = "1.0x"

    fun syncAllWidgets(context: Context) {
        PrismMediaWidgetProvider.syncWidget(context, lastTitle, lastSubtitle, lastIsPlaying)
        PrismExpressiveCardWidgetProvider.syncWidget(context)
        PrismVinylSquareWidgetProvider.syncWidget(context)
        PrismQuranWidgetProvider.syncWidget(context)
        PrismAudioDeckWidgetProvider.syncWidget(context)
        PrismQuickStudioWidgetProvider.syncWidget(context)
    }
}

class PrismMediaWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.widget.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.widget.ACTION_NEXT"
        const val ACTION_PREV = "com.example.widget.ACTION_PREV"

        fun syncWidget(
            context: Context,
            title: String?,
            subtitle: String?,
            isPlaying: Boolean,
        ) {
            WidgetPlaybackBridge.lastTitle = title
            WidgetPlaybackBridge.lastSubtitle = subtitle
            WidgetPlaybackBridge.lastIsPlaying = isPlaying

            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, PrismMediaWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isEmpty()) return

            for (id in ids) {
                updateAppWidget(context, manager, id)
            }
        }

        fun requestPinWidget(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
            val manager = AppWidgetManager.getInstance(context)
            if (!manager.isRequestPinAppWidgetSupported) return false
            val provider = ComponentName(context, PrismMediaWidgetProvider::class.java)
            return manager.requestPinAppWidget(provider, null, null)
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_prism_media)

            val title = WidgetPlaybackBridge.lastTitle ?: context.getString(R.string.app_name)
            val subtitle = WidgetPlaybackBridge.lastSubtitle ?: context.getString(R.string.widget_idle_subtitle)
            val isPlaying = WidgetPlaybackBridge.lastIsPlaying

            views.setTextViewText(R.id.widget_track_title, title)
            views.setTextViewText(R.id.widget_track_subtitle, subtitle)
            views.setImageViewResource(
                R.id.widget_btn_play_pause,
                if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
            )
            views.setInt(
                R.id.widget_btn_play_pause,
                "setBackgroundResource",
                if (isPlaying) R.drawable.widget_play_btn_playing_bg else R.drawable.widget_play_btn_bg
            )

            // Open app on container click
            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openPending = PendingIntent.getActivity(
                context,
                100,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openPending)
            views.setOnClickPendingIntent(R.id.widget_icon_container, openPending)

            // Media control broadcasts
            views.setOnClickPendingIntent(
                R.id.widget_btn_play_pause,
                controlPendingIntent(context, ACTION_PLAY_PAUSE, 101)
            )
            views.setOnClickPendingIntent(
                R.id.widget_btn_next,
                controlPendingIntent(context, ACTION_NEXT, 102)
            )
            views.setOnClickPendingIntent(
                R.id.widget_btn_prev,
                controlPendingIntent(context, ACTION_PREV, 103)
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun controlPendingIntent(context: Context, action: String, reqCode: Int): PendingIntent {
            val intent = Intent(context, PrismMediaWidgetProvider::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                reqCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (id in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                val callback = WidgetPlaybackBridge.onPlayPause
                if (callback != null) callback() else launchMainActivity(context)
            }
            ACTION_NEXT -> {
                val callback = WidgetPlaybackBridge.onNext
                if (callback != null) callback() else launchMainActivity(context)
            }
            ACTION_PREV -> {
                val callback = WidgetPlaybackBridge.onPrev
                if (callback != null) callback() else launchMainActivity(context)
            }
        }
    }

    private fun launchMainActivity(context: Context) {
        val launch = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(launch)
    }
}
