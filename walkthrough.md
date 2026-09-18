# Daily Goal & Reminder - Android App Walkthrough

The **Daily Goal & Reminder** app is a modern, production-ready Android application designed to help users establish daily habits, set custom goals, track progress & streaks, and receive exact scheduled local push notifications (even when the app is minimized, closed, or after device reboot).

---

## 📱 App Highlights & Architecture

### 1. Technology Stack
- **Language**: Kotlin 1.9.22
- **UI Framework**: Jetpack Compose with Material 3 Design System
- **Database**: Room Database (SQLite) with StateFlow / Coroutines
- **Navigation**: Jetpack Navigation Compose
- **Alarm & Notification Engine**: Android `AlarmManager` (`RTC_WAKEUP`), `NotificationManager`, `PendingIntent`, `BroadcastReceiver` (`BootReceiver`, `NotificationActionReceiver`, `SnoozeReceiver`)
- **Target SDK**: Android 14 (API 34), Minimum SDK: API 26 (Android 8.0)

---

## 📁 Code Base Directory Structure

```
c:\simple-app\
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   ├── libs.versions.toml (Gradle Version Catalog)
│   └── wrapper/
│       └── gradle-wrapper.properties
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/dailygoal/reminder/
        │   │   ├── DailyGoalApp.kt (Application Class & Notification Channel Init)
        │   │   ├── MainActivity.kt (ComponentActivity with Theme State)
        │   │   ├── data/
        │   │   │   ├── local/
        │   │   │   │   ├── AppDatabase.kt (Room Database & Initial Pre-populated Goals)
        │   │   │   │   ├── dao/GoalDao.kt, GoalCompletionDao.kt
        │   │   │   │   ├── entity/GoalEntity.kt, GoalCompletionEntity.kt
        │   │   │   │   └── preferences/PreferencesManager.kt
        │   │   │   ├── model/GoalCategory.kt, RepeatSchedule.kt, GoalWithProgress.kt, StatsSummary.kt
        │   │   │   └── repository/GoalRepository.kt
        │   │   ├── notification/
        │   │   │   ├── AlarmScheduler.kt (AlarmManager scheduling & cancel routines)
        │   │   │   ├── NotificationHelper.kt (Notification builder with actions)
        │   │   │   ├── AlarmReceiver.kt (Alarm trigger broadcast receiver)
        │   │   │   ├── BootReceiver.kt (Reschedules active alarms on phone reboot)
        │   │   │   ├── NotificationActionReceiver.kt (Mark Done directly from notification bar)
        │   │   │   └── SnoozeReceiver.kt (Snoozes reminders for 5m, 10m, 30m, 1h)
        │   │   ├── ui/
        │   │   │   ├── theme/ (Color.kt, Type.kt, Theme.kt - Material 3 Dark/Light)
        │   │   │   ├── navigation/ (Screen.kt, NavGraph.kt)
        │   │   │   ├── components/ (ProgressRing, GoalCard, CategoryChip, DaySelector, StreakBadge, BottomNavBar, ActionDialogs)
        │   │   │   ├── viewmodel/GoalViewModel.kt
        │   │   │   └── screens/
        │   │   │       ├── SplashScreen.kt
        │   │   │       ├── OnboardingScreen.kt
        │   │   │       ├── HomeScreen.kt (Today's Checklist & Dashboard)
        │   │   │       ├── AddEditGoalScreen.kt (Goal Creator & Editor)
        │   │   │       ├── GoalDetailScreen.kt (Goal Performance & Controls)
        │   │   │       ├── HistoryScreen.kt (Calendar View & Daily Logs)
        │   │   │       ├── StatisticsScreen.kt (Streak Insights & Progress Charts)
        │   │   │       ├── NotificationSettingsScreen.kt (Global Alerts, Snooze, Test Push)
        │   │   │       └── SettingsScreen.kt (Theme Mode, Restore Defaults, Reset Data)
        │   │   └── util/
        │   │       ├── DateUtils.kt
        │   │       ├── SmartMessageGenerator.kt
        │   │       └── PermissionUtils.kt
        │   └── res/
        │       ├── drawable/ic_notification.xml
        │       ├── values/strings.xml, colors.xml, themes.xml
        │       └── xml/backup_rules.xml, data_extraction_rules.xml
        └── test/java/com/dailygoal/reminder/DateUtilsTest.kt
```

---

## 🚀 Key Features Implemented

### 1. Scheduled Push Notifications & Lock Screen Actions
- Uses `AlarmManager.setExactAndAllowWhileIdle()` to guarantee notification triggering even when device is in Doze mode or app is closed.
- **Mark Done Action**: Users can complete a goal directly from the notification shade without opening the application.
- **Snooze Action**: Re-schedules the alarm automatically for 5, 10, 30 minutes, or 1 hour based on user preferences.
- **Boot Receiver**: Subscribes to `RECEIVE_BOOT_COMPLETED` so all active alarms are automatically restored when the phone restarts.

### 2. Dashboard & Progress Tracking
- **Animated Circular Progress Ring**: Displays real-time completion percentage.
- **Streak Protection**: Calculates consecutive-day streaks (`🔥 5 Day Streak`) and total active completion days.
- **Incremental Goal Support**: Goals like "Drink 2L Water" or "Read 10 Pages" support progressive increments (`+1`) towards daily completion.

### 3. All 9 App Screens
1. **Splash Screen**: Animated logo transition.
2. **Onboarding Screen**: Carousel introduction and Android 13+ runtime notification permission request (`POST_NOTIFICATIONS`).
3. **Home Screen**: Today's checklist, progress ring, streak badge, and upcoming reminder banner.
4. **Add/Edit Goal Screen**: Category presets, custom target/units, time picker, and day repeat schedule selector.
5. **Goal Detail Screen**: Comprehensive view, pause/resume toggle, edit, and delete options.
6. **History Screen**: Interactive monthly calendar with daily completion logs.
7. **Statistics Screen**: Streak breakdown, 7-day progress bar chart, and completion metrics.
8. **Notification Settings**: Global toggle, default snooze duration picker, sound/vibration toggles, and "Send Test Notification Now" button.
9. **Settings Screen**: Dark / Light / System theme selector, sample goal restorer, and complete data reset option.

---

## 🛠️ How to Open and Run in Android Studio

1. **Launch Android Studio** (Hedgehog 2023.1.1 or newer recommended).
2. Click **Open** and select the root directory: `c:\simple-app`.
3. Allow Gradle to sync dependencies.
4. Select an Android Emulator or connected device running API 26 or higher.
5. Click **Run 'app'** (`Shift + F10`).

---

## 🧪 Verification Plan Completed

- **Unit Tests**: Included in [`DateUtilsTest.kt`](file:///c:/simple-app/app/src/test/java/com/dailygoal/reminder/DateUtilsTest.kt) testing streak calculation algorithms, date formatting, and day-of-week bitmasks.
- **Code Inspection**: Verified Kotlin syntax, Room Entity schemas, DAO queries, AlarmManager PendingIntents, and Manifest Broadcast Receivers.
