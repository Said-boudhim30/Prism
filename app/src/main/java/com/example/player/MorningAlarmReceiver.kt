package com.example.player

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.model.MorningAlarmConfig
import java.util.Calendar

/**
 * Permission-free Morning Quran/Nasheed Alarm scheduler and BroadcastReceiver.
 * Uses AlarmManager.setAndAllowWhileIdle / set() so it never requires restricted
 * SCHEDULE_EXACT_ALARM permissions under Google Play policy.
 */
object MorningAlarmScheduler {
    const val ACTION_MORNING_ALARM_TRIGGER = "com.example.alarm.ACTION_MORNING_ALARM_TRIGGER"
    const val EXTRA_ALARM_TRACK_ID = "extra_alarm_track_id"
    const val EXTRA_ALARM_FADE_SECONDS = "extra_alarm_fade_seconds"
    private const val REQUEST_CODE = 5201

    fun scheduleOrCancel(context: Context, config: MorningAlarmConfig) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, MorningAlarmReceiver::class.java).apply {
            action = ACTION_MORNING_ALARM_TRIGGER
            putExtra(EXTRA_ALARM_TRACK_ID, config.trackId.orEmpty())
            putExtra(EXTRA_ALARM_FADE_SECONDS, config.fadeInSeconds)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!config.enabled) {
            runCatching { alarmManager.cancel(pendingIntent) }
            return
        }

        val triggerAtMillis = computeNextTriggerMillis(config.hour, config.minute)
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        }
    }

    fun computeNextTriggerMillis(hour: Int, minute: Int, nowMs: Long = System.currentTimeMillis()): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = nowMs
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
            set(Calendar.MINUTE, minute.coerceIn(0, 59))
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= nowMs) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return cal.timeInMillis
    }
}

class MorningAlarmReceiver : BroadcastReceiver() {
    companion object {
        private const val CHANNEL_ID = "prism_morning_alarm_channel"
        private const val NOTIFICATION_ID = 5202
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != MorningAlarmScheduler.ACTION_MORNING_ALARM_TRIGGER) return
        val trackId = intent.getStringExtra(MorningAlarmScheduler.EXTRA_ALARM_TRACK_ID).orEmpty()
        val fadeSec = intent.getIntExtra(MorningAlarmScheduler.EXTRA_ALARM_FADE_SECONDS, 30)

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.morning_alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            nm.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(MorningAlarmScheduler.EXTRA_ALARM_TRACK_ID, trackId)
            putExtra(MorningAlarmScheduler.EXTRA_ALARM_FADE_SECONDS, fadeSec)
            action = MorningAlarmScheduler.ACTION_MORNING_ALARM_TRIGGER
        }
        val contentPending = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_widget_play)
            .setContentTitle(context.getString(R.string.morning_alarm_title))
            .setContentText(context.getString(R.string.morning_alarm_body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPending)
            .build()

        runCatching { nm.notify(NOTIFICATION_ID, notification) }
    }
}
