package com.dailygoal.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dailygoal.reminder.data.local.preferences.PreferencesManager

class SnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val goalId = intent.getLongExtra(NotificationHelper.EXTRA_GOAL_ID, -1L)
        val title = intent.getStringExtra(NotificationHelper.EXTRA_GOAL_TITLE) ?: "Daily Goal"
        val category = intent.getStringExtra(NotificationHelper.EXTRA_GOAL_CATEGORY) ?: "CUSTOM"
        val targetCount = intent.getIntExtra(NotificationHelper.EXTRA_GOAL_TARGET, 1)
        val unit = intent.getStringExtra(NotificationHelper.EXTRA_GOAL_UNIT) ?: "Times"

        if (goalId != -1L) {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.dismissNotification(goalId)

            val prefsManager = PreferencesManager(context)
            val snoozeMinutes = prefsManager.snoozeDurationMinutes

            val scheduler = AlarmScheduler(context)
            scheduler.scheduleSnoozeAlarm(goalId, title, category, targetCount, unit, snoozeMinutes)
        }
    }
}
