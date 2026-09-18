package com.dailygoal.reminder.notification

import android.util.Log
import com.dailygoal.reminder.data.local.preferences.PreferencesManager
import com.dailygoal.reminder.data.repository.PushTokenRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.concurrent.atomic.AtomicInteger

class AimlyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "New FCM Token received: $token")
        val tokenRepo = PushTokenRepository(applicationContext)
        tokenRepo.saveToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message received from: ${remoteMessage.from}")

        val prefsManager = PreferencesManager(applicationContext)
        if (!prefsManager.isGlobalNotificationsEnabled || !prefsManager.isRemoteNotificationsEnabled) {
            Log.d(TAG, "Remote notifications are disabled in app settings. Skipping message display.")
            return
        }

        val data = remoteMessage.data
        val notification = remoteMessage.notification

        // Extract structured notification payload
        val type = data["type"] ?: notification?.title?.let { "ANNOUNCEMENT" } ?: "SYSTEM"
        val rawTitle = data["title"] ?: notification?.title ?: "Aimly Notification"
        val rawBody = data["body"] ?: notification?.body ?: ""
        val deepLink = data["deepLink"] ?: data["url"]
        val notificationIdStr = data["notificationId"] ?: data["id"]
        val campaignId = data["campaignId"]

        // Validate payload fields safely
        val title = if (rawTitle.length > MAX_TITLE_LENGTH) rawTitle.take(MAX_TITLE_LENGTH) else rawTitle
        val body = if (rawBody.length > MAX_BODY_LENGTH) rawBody.take(MAX_BODY_LENGTH) else rawBody
        val notificationId = notificationIdStr?.toIntOrNull() ?: notificationIdCounter.incrementAndGet()

        Log.d(TAG, "Processing FCM message: type=$type, title=$title, deepLink=$deepLink, campaignId=$campaignId")

        // Map notification type to channel
        val channelId = when (type.uppercase()) {
            "ANNOUNCEMENT", "FEATURE_UPDATE", "SYSTEM" -> NotificationHelper.CHANNEL_UPDATES_ID
            "WEEKLY_CHALLENGE", "MOTIVATION", "PROMOTION" -> NotificationHelper.CHANNEL_PROMOTIONS_ID
            else -> NotificationHelper.CHANNEL_REMINDERS_ID
        }

        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showRemoteNotification(
            notificationId = notificationId,
            channelId = channelId,
            title = title,
            body = body,
            deepLink = deepLink
        )
    }

    companion object {
        private const val TAG = "AimlyFcmService"
        private const val MAX_TITLE_LENGTH = 120
        private const val MAX_BODY_LENGTH = 500
        private val notificationIdCounter = AtomicInteger(100000)
    }
}
