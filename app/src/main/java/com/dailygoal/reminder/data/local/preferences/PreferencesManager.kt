package com.dailygoal.reminder.data.local.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("daily_goal_prefs", Context.MODE_PRIVATE)

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    var isGlobalNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_GLOBAL_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_GLOBAL_NOTIFICATIONS, value).apply()

    var snoozeDurationMinutes: Int
        get() = prefs.getInt(KEY_SNOOZE_DURATION, 10)
        set(value) = prefs.edit().putInt(KEY_SNOOZE_DURATION, value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    var themeMode: String // "SYSTEM", "LIGHT", "DARK"
        get() = prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_GLOBAL_NOTIFICATIONS = "global_notifications"
        private const val KEY_SNOOZE_DURATION = "snooze_duration"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
