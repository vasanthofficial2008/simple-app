# Aimly ProGuard / R8 Rules

# Keep Room entities & DAOs
-keep class com.dailygoal.reminder.data.local.entity.** { *; }
-keep class com.dailygoal.reminder.data.local.dao.** { *; }
-keep class com.dailygoal.reminder.data.model.** { *; }
-dontwarn androidx.room.paging.**

# Keep Notification Receivers & Services
-keep class com.dailygoal.reminder.notification.** { *; }

# Keep Firebase Cloud Messaging
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.messaging.**

# Keep Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }

# Keep Jetpack Compose
-keepclassmembers class **.R$* {
    public static <fields>;
}
