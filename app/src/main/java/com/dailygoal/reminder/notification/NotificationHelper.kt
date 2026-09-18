package com.dailygoal.reminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dailygoal.reminder.MainActivity
import com.dailygoal.reminder.R
import com.dailygoal.reminder.data.local.preferences.PreferencesManager
import com.dailygoal.reminder.util.SmartMessageGenerator

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val prefsManager = PreferencesManager(context)

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                enableVibration(prefsManager.isVibrationEnabled)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showGoalNotification(
        goalId: Long,
        title: String,
        category: String,
        targetCount: Int,
        unit: String
    ) {
        if (!prefsManager.isGlobalNotificationsEnabled) return

        createNotificationChannel()

        // Tap notification to open main activity
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigation_goal_id", goalId)
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            goalId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Mark Done directly from notification bar
        val markDoneIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_DONE
            putExtra(EXTRA_GOAL_ID, goalId)
        }
        val markDonePendingIntent = PendingIntent.getBroadcast(
            context,
            (goalId * 10 + 1).toInt(),
            markDoneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Action: Snooze (e.g., 10 mins)
        val snoozeIntent = Intent(context, SnoozeReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_GOAL_ID, goalId)
            putExtra(EXTRA_GOAL_TITLE, title)
            putExtra(EXTRA_GOAL_CATEGORY, category)
            putExtra(EXTRA_GOAL_TARGET, targetCount)
            putExtra(EXTRA_GOAL_UNIT, unit)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            (goalId * 10 + 2).toInt(),
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val messageText = SmartMessageGenerator.generateMessage(title, category, targetCount, unit)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⏰ Reminder: $title")
            .setContentText(messageText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(0, "✅ Mark Done", markDonePendingIntent)
            .addAction(0, "💤 Snooze", snoozePendingIntent)

        if (prefsManager.isVibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 300, 200, 300))
        }

        notificationManager.notify(goalId.toInt(), builder.build())
    }

    fun dismissNotification(goalId: Long) {
        notificationManager.cancel(goalId.toInt())
    }

    companion object {
        const val CHANNEL_ID = "daily_goal_reminder_channel"
        const val ACTION_MARK_DONE = "com.dailygoal.reminder.ACTION_MARK_DONE"
        const val ACTION_SNOOZE = "com.dailygoal.reminder.ACTION_SNOOZE"
        const val EXTRA_GOAL_ID = "extra_goal_id"
        const val EXTRA_GOAL_TITLE = "extra_goal_title"
        const val EXTRA_GOAL_CATEGORY = "extra_goal_category"
        const val EXTRA_GOAL_TARGET = "extra_goal_target"
        const val EXTRA_GOAL_UNIT = "extra_goal_unit"
    }
}
