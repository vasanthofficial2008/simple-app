package com.dailygoal.reminder

import android.app.Application
import com.dailygoal.reminder.data.repository.PushTokenRepository
import com.dailygoal.reminder.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyGoalApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannels()

        // Fetch/refresh FCM Token in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tokenRepo = PushTokenRepository(this@DailyGoalApp)
                tokenRepo.fetchAndSaveToken()
                // Default subscription to announcements & updates
                tokenRepo.subscribeToTopic("aimly_all_users")
                tokenRepo.subscribeToTopic("aimly_updates")
            } catch (e: Exception) {
                // Graceful fallback if Firebase is not yet fully configured
            }
        }
    }
}
