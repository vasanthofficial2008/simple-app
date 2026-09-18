package com.dailygoal.reminder.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dailygoal.reminder.data.local.AppDatabase
import com.dailygoal.reminder.data.local.repository.GoalRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val scheduler = AlarmScheduler(context)
                    val repository = GoalRepository(db.goalDao(), db.goalCompletionDao(), scheduler)
                    repository.rescheduleAllActiveAlarms()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
