package com.dailygoal.reminder

import android.app.Application
import com.dailygoal.reminder.notification.NotificationHelper

class DailyGoalApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()
    }
}
