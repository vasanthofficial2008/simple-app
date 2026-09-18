package com.dailygoal.reminder.data.repository

import android.content.Context
import android.util.Log
import com.dailygoal.reminder.data.local.preferences.PreferencesManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class PushTokenRepository(private val context: Context) {

    private val prefsManager = PreferencesManager(context)
    private val firebaseMessaging = FirebaseMessaging.getInstance()

    private val _fcmToken = MutableStateFlow<String?>(prefsManager.fcmToken)
    val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    suspend fun fetchAndSaveToken(): String? = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            firebaseMessaging.token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    if (continuation.isActive) continuation.resume(null)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d(TAG, "FCM Token retrieved successfully: $token")
                saveToken(token)
                if (continuation.isActive) continuation.resume(token)
            }
        }
    }

    fun saveToken(token: String) {
        prefsManager.fcmToken = token
        prefsManager.fcmTokenLastUpdated = System.currentTimeMillis()
        _fcmToken.value = token
        // Interface hook for backend synchronization
        syncTokenWithBackend(token)
    }

    fun subscribeToTopic(topic: String, onResult: (Boolean) -> Unit = {}) {
        firebaseMessaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                val isSuccess = task.isSuccessful
                if (isSuccess) {
                    Log.d(TAG, "Successfully subscribed to topic: $topic")
                } else {
                    Log.w(TAG, "Failed to subscribe to topic: $topic", task.exception)
                }
                onResult(isSuccess)
            }
    }

    fun unsubscribeFromTopic(topic: String, onResult: (Boolean) -> Unit = {}) {
        firebaseMessaging.unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                val isSuccess = task.isSuccessful
                if (isSuccess) {
                    Log.d(TAG, "Successfully unsubscribed from topic: $topic")
                } else {
                    Log.w(TAG, "Failed to unsubscribe from topic: $topic", task.exception)
                }
                onResult(isSuccess)
            }
    }

    /**
     * Clean backend interface abstraction.
     * When a server backend is deployed, this method sends the token securely to POST /api/v1/push-tokens.
     */
    fun syncTokenWithBackend(token: String) {
        Log.i(TAG, "FCM Token ready for backend sync: $token")
    }

    companion object {
        private const val TAG = "PushTokenRepository"
    }
}
