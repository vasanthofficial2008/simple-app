package com.dailygoal.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dailygoal.reminder.data.local.AppDatabase
import com.dailygoal.reminder.data.local.repository.GoalRepository
import com.dailygoal.reminder.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val goalId = intent.getLongExtra(NotificationHelper.EXTRA_GOAL_ID, -1L)
        if (goalId != -1L) {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.dismissNotification(goalId)

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val scheduler = AlarmScheduler(context)
                    val repository = GoalRepository(db.goalDao(), db.goalCompletionDao(), scheduler)
                    repository.toggleGoalCompletion(goalId, DateUtils.getTodayDateString())
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
