# Implementation Plan - "Daily Goal & Reminder" Android App

A modern, production-ready native Android application built with Kotlin, Jetpack Compose, Material 3, Room Database, AlarmManager, WorkManager, and Notification APIs. The app empowers users to track daily habits/goals, view progress & streaks, receive scheduled local notifications (even when closed or after device reboot), snooze/complete reminders directly from notifications, and manage custom schedule settings.

---

## User Review Required

> [!IMPORTANT]
> - **SDK Targets & Tooling**: The app is built targeting Android 14 (API level 34) with compatibility down to Android 8.0 (API 26).
> - **Actual Scheduled Notifications**: Uses Android `AlarmManager` with `PendingIntent` and `BroadcastReceiver` (`BootReceiver` + `NotificationActionReceiver` + `SnoozeReceiver`) to guarantee alarms trigger when the app is minimized, closed, or after a system restart.
> - **Room Database Persistence**: All goals, category metadata, daily completion history, and app preferences are stored locally using Room and SharedPreferences.

---

## Architecture & Project Structure

```
c:\simple-app\
├── build.gradle.kts (Project-level build config)
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
└── app/
    ├── build.gradle.kts (App-level build config with Compose, Room, Navigation, etc.)
    ├── proguard-rules.pro
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/dailygoal/reminder/
        │   │   ├── DailyGoalApp.kt (Application class, Notification Channel setup)
        │   │   ├── data/
        │   │   │   ├── local/
        │   │   │   │   ├── AppDatabase.kt (Room database definition)
        │   │   │   │   ├── dao/GoalDao.kt, GoalCompletionDao.kt
        │   │   │   │   ├── entity/GoalEntity.kt, GoalCompletionEntity.kt
        │   │   │   │   └── preferences/PreferencesManager.kt
        │   │   │   ├── model/GoalCategory.kt, RepeatSchedule.kt, GoalWithProgress.kt, StatsSummary.kt
        │   │   │   └── repository/GoalRepository.kt
        │   │   ├── notification/
        │   │   │   ├── AlarmScheduler.kt (AlarmManager scheduling & cancel logic)
        │   │   │   ├── NotificationHelper.kt (Notification Builder & Channels)
        │   │   │   ├── AlarmReceiver.kt (Triggered by AlarmManager to fire notifications)
        │   │   │   ├── BootReceiver.kt (Reschedules alarms on system reboot)
        │   │   │   ├── NotificationActionReceiver.kt (Mark done / Snooze handler)
        │   │   │   └── SnoozeReceiver.kt
        │   │   ├── ui/
        │   │   │   ├── theme/
        │   │   │   │   ├── Color.kt, Type.kt, Theme.kt (Material 3 Dark/Light palette)
        │   │   │   ├── navigation/
        │   │   │   │   ├── Screen.kt (Navigation routes)
        │   │   │   │   └── NavGraph.kt (Jetpack Compose Navigation Graph)
        │   │   │   ├── components/
        │   │   │   │   ├── ProgressRing.kt, GoalCard.kt, CategoryChip.kt, DaySelector.kt
        │   │   │   │   ├── StreakBadge.kt, ActionDialogs.kt, BottomNavBar.kt
        │   │   │   └── screens/
        │   │   │       ├── SplashScreen.kt
        │   │   │       ├── OnboardingScreen.kt
        │   │   │       ├── HomeScreen.kt (Today's Goals dashboard & checklist)
        │   │   │       ├── AddEditGoalScreen.kt (Create & update goals)
        │   │   │       ├── GoalDetailScreen.kt (History, stats per goal, pause/delete)
        │   │   │       ├── HistoryScreen.kt (Calendar & daily logs)
        │   │   │       ├── StatisticsScreen.kt (Completion rates, streaks, charts)
        │   │   │       ├── NotificationSettingsScreen.kt (Global alerts, sounds, snooze duration)
        │   │   │       └── SettingsScreen.kt (Theme, sample data restore, reset)
        │   │   └── util/
        │   │       ├── DateUtils.kt
        │   │       ├── SmartMessageGenerator.kt
        │   │       └── PermissionUtils.kt
        │   └── res/
        │       ├── drawable/ (App icons, category vectors, launcher icons)
        │       ├── values/ (Strings, colors, themes)
        │       └── xml/ (Backup rules, notification metadata)
        └── test/ & androidTest/ (Unit test cases for repositories, DAO, streak logic)
```

---

## Proposed Changes

### Build Infrastructure & Configuration
#### [NEW] [settings.gradle.kts](file:///c:/simple-app/settings.gradle.kts)
#### [NEW] [build.gradle.kts](file:///c:/simple-app/build.gradle.kts)
#### [NEW] [app/build.gradle.kts](file:///c:/simple-app/app/build.gradle.kts)
#### [NEW] [app/src/main/AndroidManifest.xml](file:///c:/simple-app/app/src/main/AndroidManifest.xml)
- Standard Android manifest with permissions: `POST_NOTIFICATIONS`, `SCHEDULE_EXACT_ALARM`, `USE_EXACT_ALARM`, `RECEIVE_BOOT_COMPLETED`, `VIBRATE`, `WAKE_LOCK`.
- Registered receivers: `AlarmReceiver`, `BootReceiver`, `NotificationActionReceiver`, `SnoozeReceiver`.

---

### Data Layer (Room Database & Repositories)
#### [NEW] [GoalEntity.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/data/local/entity/GoalEntity.kt)
- Represents a user's goal with fields: `id`, `title`, `description`, `category` (WATER, EXERCISE, READING, STUDY, MEDICINE, WORK, CUSTOM), `targetCount`, `unit`, `reminderHour`, `reminderMinute`, `repeatDaysMask` (bitmask for 7 days), `colorHex`, `iconName`, `isPaused`, `createdAt`.

#### [NEW] [GoalCompletionEntity.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/data/local/entity/GoalCompletionEntity.kt)
- Stores completion events: `id`, `goalId`, `dateString` (YYYY-MM-DD), `completedCount`, `isCompleted`, `timestamp`.

#### [NEW] [GoalDao.kt & GoalCompletionDao.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/data/local/dao/GoalDao.kt)
- Room DAOs supporting coroutines `Flow` for real-time UI updates, date filtering, streak counting, and batch completion queries.

#### [NEW] [AppDatabase.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/data/local/AppDatabase.kt)
- Singleton Room database with pre-packaged sample goals ("Drink 2L Water", "Exercise 30 mins", "Read 10 Pages", "Study 1 Hour", "Take Medicine") pre-populated on first launch.

#### [NEW] [GoalRepository.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/data/repository/GoalRepository.kt)
- Business repository managing goal CRUD operations, daily progress calculations, streak calculation logic (consecutive days with completed goals), and date-range queries for statistics.

---

### Notification & Alarm System
#### [NEW] [NotificationHelper.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/notification/NotificationHelper.kt)
- Initializes Android Notification Channels (`channel_daily_reminders`).
- Builds rich Material 3 notifications with custom titles, smart motivational body text (e.g. "💧 Don't forget to drink water!"), icon, sound/vibration config, and Action Buttons:
  - **"Mark Done"**: Calls `NotificationActionReceiver` to update Room database immediately without opening the app.
  - **"Snooze (10m)"**: Calls `SnoozeReceiver` to re-trigger notification after snooze duration.

#### [NEW] [AlarmScheduler.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/notification/AlarmScheduler.kt)
- Calculates next trigger time based on selected hour/minute and repeat days.
- Schedules alarms via `AlarmManager.setExactAndAllowWhileIdle()` or `setAlarmClock()`.
- Provides helper functions to schedule, update, or cancel alarms per goal.

#### [NEW] [AlarmReceiver.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/notification/AlarmReceiver.kt)
- Receives scheduled alarm intent and fires the notification via `NotificationHelper`.
- Automatically schedules the next recurring alarm for the next active repeat day.

#### [NEW] [BootReceiver.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/notification/BootReceiver.kt)
- Listens to `ACTION_BOOT_COMPLETED` and `ACTION_MY_PACKAGE_REPLACED`.
- Restores all scheduled alarms from Room database into `AlarmManager`.

---

### Presentation Layer (Jetpack Compose UI)
#### [NEW] [Theme.kt, Color.kt, Type.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/theme/Theme.kt)
- Vibrant Material 3 color system supporting dynamic dark and light mode, custom category gradients, and crisp typography.

#### [NEW] [HomeScreen.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/HomeScreen.kt)
- Dashboard displaying current date, circular progress ring with percentage, completed/remaining counts, current streak badge with fire emoji 🔥, upcoming reminder card, and filterable today's goal list with quick toggle & incremental progress buttons.

#### [NEW] [AddEditGoalScreen.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/AddEditGoalScreen.kt)
- Form to create or edit goals: Preset category selector, title, description, target count/unit, time picker dialog for reminders, repeat days selector (Mon-Sun), color & icon choices.

#### [NEW] [GoalDetailScreen.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/GoalDetailScreen.kt)
- Deep view into individual goal performance, completion history log, total completed count, current streak, pause/resume toggle, edit, and delete options.

#### [NEW] [HistoryScreen.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/HistoryScreen.kt)
- Interactive monthly/weekly calendar view showing completion dots per day, total goals completed per date, and detailed breakdown list.

#### [NEW] [StatisticsScreen.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/StatisticsScreen.kt)
- High-level insights screen with daily completion rates, weekly bar visualization, current vs best streaks, and category distribution.

#### [NEW] [NotificationSettingsScreen.kt & SettingsScreen.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/SettingsScreen.kt)
- Global notification toggle, sound/vibration preferences, default snooze duration (5, 10, 30, 60 mins), "Test Notification Now" button, dark mode selection, reset data, and sample goal reloading.

#### [NEW] [OnboardingScreen.kt & SplashScreen.kt](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/OnboardingScreen.kt)
- Guided welcome slides highlighting app features, quick goal selection, and notification permission request dialog.

---

## Verification Plan

### Automated Tests
- Unit tests for `StreakCalculator` & `DateUtils` verifying consecutive-day streak computations, day bitmask calculations, and date parsing.
- Repository unit tests using Room In-Memory Database to verify goal creation, completion updates, and date filtering.

### Manual / Integration Verification
- Verify code structure, Android Manifest declarations, Receiver intents, Room Entities, and Compose Navigation wiring.
- Create verification scripts / gradle build validation.
