package com.dailygoal.reminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.dailygoal.reminder.data.local.entity.GoalEntity
import com.dailygoal.reminder.util.DateUtils
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleGoalReminder(goal: GoalEntity) {
        if (!goal.isReminderEnabled || goal.isPaused) {
            cancelGoalReminder(goal.id)
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_GOAL_ID, goal.id)
            putExtra(EXTRA_GOAL_TITLE, goal.title)
            putExtra(EXTRA_GOAL_CATEGORY, goal.category)
            putExtra(EXTRA_GOAL_TARGET, goal.targetCount)
            putExtra(EXTRA_GOAL_UNIT, goal.unit)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            goal.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = calculateNextTriggerTime(
            goal.reminderHour,
            goal.reminderMinute,
            goal.repeatDaysMask
        )

        val triggerAtMillis = calendar.timeInMillis

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun scheduleSnoozeAlarm(goalId: Long, goalTitle: String, category: String, targetCount: Int, unit: String, snoozeMinutes: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_GOAL_ID, goalId)
            putExtra(EXTRA_GOAL_TITLE, goalTitle)
            putExtra(EXTRA_GOAL_CATEGORY, category)
            putExtra(EXTRA_GOAL_TARGET, targetCount)
            putExtra(EXTRA_GOAL_UNIT, unit)
            putExtra(EXTRA_IS_SNOOZED, true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (goalId + 99999).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun cancelGoalReminder(goalId: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            goalId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun calculateNextTriggerTime(hour: Int, minute: Int, repeatDaysMask: Int): Calendar {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If time is earlier today or target day is not enabled, find next matching day
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        if (repeatDaysMask != 0) {
            var attempts = 0
            while (attempts < 7) {
                val currentDayMask = DateUtils.getDayOfWeekBitmask(target)
                if ((repeatDaysMask and currentDayMask) != 0) {
                    break
                }
                target.add(Calendar.DAY_OF_YEAR, 1)
                attempts++
            }
        }

        return target
    }

    companion object {
        const val EXTRA_GOAL_ID = "extra_goal_id"
        const val EXTRA_GOAL_TITLE = "extra_goal_title"
        const val EXTRA_GOAL_CATEGORY = "extra_goal_category"
        const val EXTRA_GOAL_TARGET = "extra_goal_target"
        const val EXTRA_GOAL_UNIT = "extra_goal_unit"
        const val EXTRA_IS_SNOOZED = "extra_is_snoozed"
    }
}
