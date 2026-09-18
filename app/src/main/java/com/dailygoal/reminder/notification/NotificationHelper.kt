package com.dailygoal.reminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
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

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                enableVibration(prefsManager.isVibrationEnabled)
            }

            val updatesChannel = NotificationChannel(
                CHANNEL_UPDATES_ID,
                context.getString(R.string.notification_channel_updates_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_updates_desc)
            }

            val promotionsChannel = NotificationChannel(
                CHANNEL_PROMOTIONS_ID,
                context.getString(R.string.notification_channel_promotions_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_promotions_desc)
            }

            notificationManager.createNotificationChannels(
                listOf(remindersChannel, updatesChannel, promotionsChannel)
            )
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

        createNotificationChannels()

        // Tap notification to open main activity
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigation_goal_id", goalId)
            putExtra("deep_link", "aimly://goal/$goalId")
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

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS_ID)
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

    fun showRemoteNotification(
        notificationId: Int,
        channelId: String,
        title: String,
        body: String,
        deepLink: String?
    ) {
        if (!prefsManager.isGlobalNotificationsEnabled || !prefsManager.isRemoteNotificationsEnabled) return

        createNotificationChannels()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (!deepLink.isNull_orEmpty()) {
                putExtra("deep_link", deepLink)
                data = Uri.parse(deepLink)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (prefsManager.isVibrationEnabled) {
            builder.setVibrate(longArrayOf(0, 200, 100, 200))
        }

        notificationManager.notify(notificationId, builder.build())
    }

    fun dismissNotification(goalId: Long) {
        notificationManager.cancel(goalId.toInt())
    }

    companion object {
        const val CHANNEL_REMINDERS_ID = "aimly_reminders_channel"
        const val CHANNEL_UPDATES_ID = "aimly_updates_channel"
        const val CHANNEL_PROMOTIONS_ID = "aimly_promotions_channel"
        
        // Backward compatibility alias
        const val CHANNEL_ID = CHANNEL_REMINDERS_ID

        const val ACTION_MARK_DONE = "com.dailygoal.reminder.ACTION_MARK_DONE"
        const val ACTION_SNOOZE = "com.dailygoal.reminder.ACTION_SNOOZE"
        const val EXTRA_GOAL_ID = "extra_goal_id"
        const val EXTRA_GOAL_TITLE = "extra_goal_title"
        const val EXTRA_GOAL_CATEGORY = "extra_goal_category"
        const val EXTRA_GOAL_TARGET = "extra_goal_target"
        const val EXTRA_GOAL_UNIT = "extra_goal_unit"
    }
}

private fun String?.isNull_orEmpty(): Boolean = this == null || this.trim().isEmpty()
