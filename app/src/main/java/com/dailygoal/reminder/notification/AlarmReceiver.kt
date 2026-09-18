package com.dailygoal.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dailygoal.reminder.data.local.AppDatabase
import com.dailygoal.reminder.data.local.repository.GoalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val goalId = intent.getLongExtra(AlarmScheduler.EXTRA_GOAL_ID, -1L)
        val title = intent.getStringExtra(AlarmScheduler.EXTRA_GOAL_TITLE) ?: "Daily Goal"
        val category = intent.getStringExtra(AlarmScheduler.EXTRA_GOAL_CATEGORY) ?: "CUSTOM"
        val targetCount = intent.getIntExtra(AlarmScheduler.EXTRA_GOAL_TARGET, 1)
        val unit = intent.getStringExtra(AlarmScheduler.EXTRA_GOAL_UNIT) ?: "Times"

        if (goalId != -1L) {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showGoalNotification(goalId, title, category, targetCount, unit)

            // Reschedule next recurrence in background
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val scheduler = AlarmScheduler(context)
                    val repository = GoalRepository(db.goalDao(), db.goalCompletionDao(), scheduler)
                    val goal = db.goalDao().getGoalByIdSync(goalId)
                    if (goal != null && goal.isReminderEnabled && !goal.isPaused) {
                        scheduler.scheduleGoalReminder(goal)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
