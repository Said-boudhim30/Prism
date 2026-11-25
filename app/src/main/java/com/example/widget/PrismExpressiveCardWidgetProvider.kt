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

/**
 * Second Material You Home Screen Widget: 4x2 Prism Expressive Hub.
 * Combines live Now Playing controls (Prev / Morphing Play-Pause / Next)
 * with direct quick-launch shortcuts to Music, Videos, and Photos tabs.
 */
class PrismExpressiveCardWidgetProvider : AppWidgetProvider() {

    companion object {
        const val EXTRA_OPEN_TAB = "com.example.extra.OPEN_TAB"

        fun syncWidget(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, PrismExpressiveCardWidgetProvider::class.java)
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
            val provider = ComponentName(context, PrismExpressiveCardWidgetProvider::class.java)
            return manager.requestPinAppWidget(provider, null, null)
        }

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_prism_expressive)

            val title = WidgetPlaybackBridge.lastTitle ?: context.getString(R.string.app_name)
            val subtitle = WidgetPlaybackBridge.lastSubtitle ?: context.getString(R.string.widget_idle_subtitle)
            val isPlaying = WidgetPlaybackBridge.lastIsPlaying

            views.setTextViewText(R.id.widget_hub_title, title)
            views.setTextViewText(R.id.widget_hub_subtitle, subtitle)
            views.setImageViewResource(
                R.id.widget_hub_btn_play_pause,
                if (isPlaying) R.drawable.ic_widget_pause else R.drawable.ic_widget_play
            )
            views.setInt(
                R.id.widget_hub_btn_play_pause,
                "setBackgroundResource",
                if (isPlaying) R.drawable.widget_play_btn_playing_bg else R.drawable.widget_play_btn_bg
            )

            // Main container opens app
            views.setOnClickPendingIntent(R.id.widget_hub_root, openTabPendingIntent(context, null, 200))
            views.setOnClickPendingIntent(R.id.widget_hub_icon_container, openTabPendingIntent(context, null, 201))

            // Quick tab shortcuts
            views.setOnClickPendingIntent(R.id.widget_hub_tab_music, openTabPendingIntent(context, "MUSIC", 210))
            views.setOnClickPendingIntent(R.id.widget_hub_tab_videos, openTabPendingIntent(context, "VIDEOS", 211))
            views.setOnClickPendingIntent(R.id.widget_hub_tab_photos, openTabPendingIntent(context, "PHOTOS", 212))

            // Media controls
            views.setOnClickPendingIntent(
                R.id.widget_hub_btn_play_pause,
                controlPendingIntent(context, PrismMediaWidgetProvider.ACTION_PLAY_PAUSE, 220)
            )
            views.setOnClickPendingIntent(
                R.id.widget_hub_btn_next,
                controlPendingIntent(context, PrismMediaWidgetProvider.ACTION_NEXT, 221)
            )
            views.setOnClickPendingIntent(
                R.id.widget_hub_btn_prev,
                controlPendingIntent(context, PrismMediaWidgetProvider.ACTION_PREV, 222)
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun openTabPendingIntent(context: Context, tabName: String?, reqCode: Int): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                if (tabName != null) {
                    putExtra(EXTRA_OPEN_TAB, tabName)
                }
            }
            return PendingIntent.getActivity(
                context,
                reqCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun controlPendingIntent(context: Context, action: String, reqCode: Int): PendingIntent {
            val intent = Intent(context, PrismExpressiveCardWidgetProvider::class.java).apply {
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
            PrismMediaWidgetProvider.ACTION_PLAY_PAUSE -> {
                val cb = WidgetPlaybackBridge.onPlayPause
                if (cb != null) cb() else launchMain(context)
            }
            PrismMediaWidgetProvider.ACTION_NEXT -> {
                val cb = WidgetPlaybackBridge.onNext
                if (cb != null) cb() else launchMain(context)
            }
            PrismMediaWidgetProvider.ACTION_PREV -> {
                val cb = WidgetPlaybackBridge.onPrev
                if (cb != null) cb() else launchMain(context)
            }
        }
    }

    private fun launchMain(context: Context) {
        val launch = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(launch)
    }
}
