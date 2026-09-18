# 🎯 Daily Goal & Reminder - Android App Development Guide

![Android Studio](https://img.shields.io/badge/Android%20Studio-Hedgehog%2B-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Room DB](https://img.shields.io/badge/Room%20Database-2.6.1-4285F4?style=for-the-badge&logo=sqlite&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-9.2.0-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-orange?style=for-the-badge)
![Target SDK](https://img.shields.io/badge/Target%20SDK-34%20(Android%2014)-brightgreen?style=for-the-badge)

A modern, production-ready native Android application built with **Kotlin**, **Jetpack Compose**, **Material 3 Design System**, **Room Database**, and **Android AlarmManager**. The app enables users to set daily habits, schedule exact local push notifications (working even when minimized, completely closed, or after a system restart), snooze reminders, mark goals completed directly from the lock screen, track daily progress percentages, and monitor consecutive-day streaks.

---

## 📋 Table of Contents

1. [Overview & Core Features](#-overview--core-features)
2. [Tech Stack & Architecture](#-tech-stack--architecture)
3. [Prerequisites & Environment Setup](#-prerequisites--environment-setup)
4. [Project Directory Structure](#-project-directory-structure)
5. [Step-by-Step Installation & Build Guide](#-step-by-step-installation--build-guide)
   - [Method 1: Using Android Studio IDE](#method-1-using-android-studio-ide)
   - [Method 2: Using Command Line (Gradle Wrapper)](#method-2-using-command-line-gradle-wrapper)
   - [Setting local.properties for SDK Path](#setting-localproperties-for-sdk-path)
6. [Core Technical Implementations](#-core-technical-implementations)
   - [1. Room Database Schema & DAOs](#1-room-database-schema--daos)
   - [2. Notification & Exact Alarm Subsystem](#2-notification--exact-alarm-subsystem)
   - [3. Lock Screen Notification Actions](#3-lock-screen-notification-actions)
   - [4. Device Boot Rescheduler](#4-device-boot-rescheduler)
7. [Screens & UI Component Mapping](#-screens--ui-component-mapping)
8. [Testing & Verification](#-testing--verification)
9. [Troubleshooting & FAQs](#-troubleshooting--faqs)
10. [Future Roadmap & Extensibility](#-future-roadmap--extensibility)
11. [License](#-license)

---

## ✨ Overview & Core Features

### 🚀 Key Features

* **👋 Beginner-Friendly Onboarding**: Interactive welcome screen introducing app capabilities and guiding users through Android 13+ (`POST_NOTIFICATIONS`) permission setup.
* **🎯 Comprehensive Daily Goal Builder**:
  * Preset Goal Categories (*Water Hydration, Fitness & Exercise, Reading Books, Study & Learning, Health & Medicine, Work & Productivity, Custom Goal*).
  * Configurable Target Counts & Custom Units (e.g., *2 Liters, 30 Minutes, 10 Pages, 1 Dose*).
  * Custom reminder time selection with interactive TimePicker dialogs.
  * Flexible day-repeat schedule selectors (*Everyday, Weekdays, Weekends, or Custom Day Combinations*).
* **⏰ Actual Scheduled Push Notifications**:
  * Uses Android `AlarmManager` with `setExactAndAllowWhileIdle()` to guarantee notification delivery at exact scheduled times.
  * Works when app is **minimized**, **completely closed**, or **after device reboot**.
* **⚡ Interactive Lock Screen Notification Actions**:
  * **✅ Mark Done**: Allows completing a goal directly from the notification shade without launching the app.
  * **💤 Snooze**: Automatically reschedules the reminder for a configurable snooze interval (5 min, 10 min, 30 min, 1 hour).
* **📊 Home Dashboard & Progress Analytics**:
  * Animated circular progress ring displaying real-time completion percentage.
  * Today's completion count vs remaining count.
  * **🔥 Consecutive Day Streak Counter**: Tracks active daily completion streaks.
  * Banner displaying the next upcoming reminder time.
* **📅 Calendar & History View**:
  * Interactive monthly calendar displaying completion badges on days with logged goal accomplishments.
  * Historical log lookup by specific date.
* **📈 Insights & Statistics**:
  * Today's completion rate progress bar.
  * 7-day weekly habit tracking bar chart.
  * Total active completion days counter.
* **⚙️ Deep Customization**:
  * Light Mode, Dark Mode, and Follow System Theme support.
  * Global Notification Master Switch.
  * Custom default snooze duration selector.
  * "Send Test Notification Now" utility to verify lock screen alerts instantly.
  * Data management tools: Restore default sample goals & full database reset.

---

## 🛠️ Tech Stack & Architecture

| Layer | Technology Used |
| :--- | :--- |
| **Language** | [Kotlin 1.9.22](https://kotlinlang.org/) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/) |
| **Architecture** | MVVM (Model-View-ViewModel) + Repository Pattern + Clean Architecture |
| **Database** | [Room Database 2.6.1](https://developer.android.com/training/data-storage/room) (SQLite ORM) |
| **Asynchronous Stream** | Kotlin Coroutines & `StateFlow` / `Flow` |
| **Navigation** | [Jetpack Navigation Compose 2.7.7](https://developer.android.com/jetpack/compose/navigation) |
| **Notifications & Alarms** | `AlarmManager`, `NotificationManager`, `PendingIntent`, `BroadcastReceiver` |
| **Background Processing** | `WorkManager` & `BroadcastReceiver` |
| **Dependency Management** | Gradle Version Catalog (`gradle/libs.versions.toml`) |

---

## 💻 Prerequisites & Environment Setup

Before starting, ensure your local development system satisfies the following hardware and software requirements:

### Required Tools & Versions
1. **Operating System**: Windows 10/11, macOS (Intel/Apple Silicon), or Linux.
2. **Java Development Kit (JDK)**: OpenJDK 17 / 21 / 26 or Oracle JDK (Required by Android Gradle Plugin 8.2+).
3. **Android Studio**: Android Studio Hedgehog (2023.1.1) or higher (Iguana, Jellyfish, Ladybug, or Koala).
4. **Android SDK**:
   - `compileSdk`: **34** (Android 14)
   - `targetSdk`: **34** (Android 14)
   - `minSdk`: **26** (Android 8.0 Oreo)

### Setting Environment Variables (Optional for Command Line Builds)

#### On Windows (PowerShell):
```powershell
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "User")
[System.Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Users\<YourUsername>\AppData\Local\Android\Sdk", "User")
$env:Path += ";$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\tools"
```

#### On macOS / Linux (`~/.zshrc` or `~/.bashrc`):
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools
```

---

## 📂 Project Directory Structure

```
c:\simple-app\
├── README.md                           # Comprehensive Developer & Setup Guide
├── gradlew.bat                         # Windows Gradle Wrapper script
├── gradlew                             # Linux/macOS Gradle Wrapper script
├── build.gradle.kts                    # Root project Gradle configuration
├── settings.gradle.kts                 # Plugin & repository management settings
├── gradle.properties                   # JVM memory & AndroidX flags
├── local.properties                    # Android SDK path configuration (create if needed)
├── gradle/
│   ├── libs.versions.toml              # Centralized Version Catalog dependencies
│   └── wrapper/
│       ├── gradle-wrapper.jar          # Gradle wrapper executable binary
│       └── gradle-wrapper.properties   # Gradle 9.2.0 wrapper distribution spec
└── app/
    ├── build.gradle.kts                # App module build config & dependencies
    ├── proguard-rules.pro              # Proguard obfuscation & keep rules
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml     # Permissions, activities & broadcast receivers
        │   ├── java/com/dailygoal/reminder/
        │   │   ├── DailyGoalApp.kt     # Application class & Notification Channel setup
        │   │   ├── MainActivity.kt     # Main Activity hosting Compose NavGraph
        │   │   ├── data/
        │   │   │   ├── local/
        │   │   │   │   ├── AppDatabase.kt          # Room Database & prepopulate callback
        │   │   │   │   ├── dao/
        │   │   │   │   │   ├── GoalDao.kt          # Room DAO for goals
        │   │   │   │   │   └── GoalCompletionDao.kt # Room DAO for completion history
        │   │   │   │   ├── entity/
        │   │   │   │   │   ├── GoalEntity.kt       # Room Entity for goal definitions
        │   │   │   │   │   └── GoalCompletionEntity.kt # Room Entity for completion logs
        │   │   │   │   └── preferences/
        │   │   │       └── PreferencesManager.kt # SharedPreferences wrapper
        │   │   │   ├── model/
        │   │   │   │   ├── GoalCategory.kt         # Enum with colors, icons & defaults
        │   │   │   │   ├── RepeatSchedule.kt       # Bitmask repeat days helper
        │   │   │   │   ├── GoalWithProgress.kt     # UI domain model with progress ratios
        │   │   │   │   └── StatsSummary.kt         # Aggregated progress statistics
        │   │   │   └── repository/
        │   │   │       └── GoalRepository.kt       # Repository bridging Room & Alarms
        │   │   ├── notification/
        │   │   │   ├── AlarmScheduler.kt           # AlarmManager exact scheduling logic
        │   │   │   ├── NotificationHelper.kt       # Notification builder with action buttons
        │   │   │   ├── AlarmReceiver.kt            # Broadcast receiver when alarm triggers
        │   │   │   ├── BootReceiver.kt             # Broadcast receiver for device reboots
        │   │   │   ├── NotificationActionReceiver.kt # Broadcast receiver for "Mark Done"
        │   │   │   └── SnoozeReceiver.kt           # Broadcast receiver for "Snooze"
        │   │   ├── ui/
        │   │   │   ├── theme/
        │   │   │   │   ├── Color.kt                # Material 3 Color System
        │   │   │   │   ├── Type.kt                 # Material 3 Typography Specs
        │   │   │   │   └── Theme.kt                # Dynamic Dark/Light Theme Provider
        │   │   │   ├── navigation/
        │   │   │   │   ├── Screen.kt               # Type-safe Navigation routes
        │   │   │   │   └── NavGraph.kt             # Navigation Graph declaration
        │   │   │   ├── components/
        │   │   │   │   ├── ProgressRing.kt         # Custom Canvas Circular Progress
        │   │   │   │   ├── GoalCard.kt             # Goal checklist item card component
        │   │   │   │   ├── CategoryChip.kt         # Selectable goal category chip
        │   │   │   │   ├── DaySelector.kt          # Mon-Sun repeat day selector
        │   │   │   │   ├── StreakBadge.kt          # Flame emoji streak badge
        │   │   │   │   ├── BottomNavBar.kt         # Material 3 Bottom Navigation
        │   │   │   │   └── ActionDialogs.kt        # Delete dialog & TimePicker wrapper
        │   │   │   ├── viewmodel/
        │   │   │   │   └── GoalViewModel.kt        # ViewModel managing app state
        │   │   │   └── screens/
        │   │   │       ├── SplashScreen.kt         # Animated splash screen
        │   │   │       ├── OnboardingScreen.kt     # Welcome carousel & permissions
        │   │   │       ├── HomeScreen.kt           # Today's Checklist dashboard
        │   │   │       ├── AddEditGoalScreen.kt    # Form to create/edit goals
        │   │   │       ├── GoalDetailScreen.kt     # Goal details, pause/resume & delete
        │   │   │       ├── HistoryScreen.kt        # Calendar view & historical logs
        │   │   │       ├── StatisticsScreen.kt     # Streak charts & habit insights
        │   │   │       ├── NotificationSettingsScreen.kt # Alert & snooze settings
        │   │   │       └── SettingsScreen.kt       # Theme, sample reload & data reset
        │   │   └── util/
        │   │       ├── DateUtils.kt                # Date parsing, formatting & streaks
        │   │       ├── SmartMessageGenerator.kt    # Dynamic notification text engine
        │   │       └── PermissionUtils.kt          # Android 13+ permission checker
        │   └── res/
        │       ├── drawable/
        │       │   └── ic_notification.xml         # Notification small icon vector
        │       ├── values/
        │       │   ├── strings.xml                 # App string resources
        │       │   ├── colors.xml                  # System colors
        │       │   └── themes.xml                  # Base window theme styles
        │       └── xml/
        │           ├── backup_rules.xml            # Full backup configuration
        │           └── data_extraction_rules.xml   # Android 12+ transfer rules
        └── test/
            └── java/com/dailygoal/reminder/
                └── DateUtilsTest.kt                # Unit test suite for streak calculations
```

---

## 🔨 Step-by-Step Installation & Build Guide

### Method 1: Using Android Studio IDE (Recommended)

1. **Clone or Open the Repository**:
   Launch Android Studio, click **Open**, and navigate to the project directory:
   ```bash
   c:\simple-app
   ```

2. **Sync Project with Gradle Files**:
   Android Studio will automatically detect Gradle files and download Android SDK 34 automatically. Click **Sync Now** if prompted.

3. **Set Up an Android Emulator or Connect a Physical Device**:
   - **Emulator**: Open `Tools -> Device Manager`, create a Virtual Device (e.g., Pixel 7 running API level 34).
   - **Physical Device**: Enable **Developer Options** and **USB Debugging** on your Android phone, then connect it via USB.

4. **Run the Application**:
   Select the `app` configuration from the top toolbar and click the green **Run** button (`Shift + F10`).

---

### Method 2: Using Command Line (Gradle Wrapper)

If building from PowerShell or Terminal without Android Studio GUI:

1. **Navigate to Project Directory**:
   ```bash
   cd c:\simple-app
   ```

2. **Clean Project**:
   ```powershell
   # Windows PowerShell
   .\gradlew.bat clean

   # macOS / Linux
   ./gradlew clean
   ```

3. **Compile and Build Debug APK**:
   ```powershell
   # Windows PowerShell
   .\gradlew.bat assembleDebug

   # macOS / Linux
   ./gradlew assembleDebug
   ```
   *The compiled Debug APK file will be generated at:*  
   `app/build/outputs/apk/debug/app-debug.apk`

4. **Install APK to Connected Device/Emulator**:
   ```powershell
   # Windows PowerShell
   .\gradlew.bat installDebug

   # macOS / Linux
   ./gradlew installDebug
   ```

5. **Execute Unit Test Suite**:
   ```powershell
   # Windows PowerShell
   .\gradlew.bat test

   # macOS / Linux
   ./gradlew test
   ```

---

### Setting `local.properties` for SDK Path

If command line builds output `SDK location not found`, create a file named `local.properties` in the root folder `c:\simple-app\local.properties` specifying your local Android SDK location:

#### Windows Example (`local.properties`):
```properties
sdk.dir=C:/Users/YourUsername/AppData/Local/Android/Sdk
```

#### macOS / Linux Example (`local.properties`):
```properties
sdk.dir=/Users/YourUsername/Library/Android/sdk
```

---

## ⚡ Core Technical Implementations

### 1. Room Database Schema & DAOs

The app utilizes a relational local database powered by Room.

#### `GoalEntity` (`goals` table):
Stores goal definitions and user configuration settings.
```kotlin
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String, // WATER, EXERCISE, READING, STUDY, MEDICINE, WORK, CUSTOM
    val targetCount: Int = 1,
    val unit: String = "Times",
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val isReminderEnabled: Boolean = true,
    val repeatDaysMask: Int = RepeatSchedule.EVERYDAY, // Bitmask: Mon=1, Tue=2, Wed=4...
    val colorHex: String = "#00B4D8",
    val iconName: String = "Flag",
    val isPaused: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

#### `GoalCompletionEntity` (`goal_completions` table):
Stores individual completion events indexed by date string (`YYYY-MM-DD`).
```kotlin
@Entity(
    tableName = "goal_completions",
    indices = [Index(value = ["goalId", "dateString"], unique = true)]
)
data class GoalCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val dateString: String, // Format: YYYY-MM-DD
    val completedCount: Int = 0,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
```

---

### 2. Notification & Exact Alarm Subsystem

Alarms are scheduled via `AlarmScheduler.kt` using `AlarmManager.setExactAndAllowWhileIdle()`:

```kotlin
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

alarmManager.setExactAndAllowWhileIdle(
    AlarmManager.RTC_WAKEUP,
    triggerAtMillis,
    pendingIntent
)
```

---

### 3. Lock Screen Notification Actions

`NotificationHelper.kt` builds native notifications with 2 direct action buttons:

1. **"✅ Mark Done" Action**:
   Dispatches an intent to `NotificationActionReceiver.kt`, which immediately updates `GoalCompletionEntity` in Room without launching the activity, and dismisses the notification.

2. **"💤 Snooze" Action**:
   Dispatches an intent to `SnoozeReceiver.kt`, which schedules a temporary snooze alarm based on the user's preferred snooze duration (5 min, 10 min, 30 min, 1 hour).

---

### 4. Device Boot Rescheduler

When the phone restarts, Android clears all scheduled `AlarmManager` alarms. The app registers `BootReceiver.kt` in `AndroidManifest.xml`:

```xml
<receiver
    android:name=".notification.BootReceiver"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
        <action android:name="android.intent.action.MY_PACKAGE_REPLACED" />
        <action android:name="android.intent.action.QUICKBOOT_POWERON" />
    </intent-filter>
</receiver>
```

When `BOOT_COMPLETED` is received, `BootReceiver` fetches all active goals from Room DB asynchronously using Coroutines and reschedules their exact alarms.

---

## 📱 Screens & UI Component Mapping

| Screen | File Location | Key Functionality |
| :--- | :--- | :--- |
| **Splash Screen** | [`SplashScreen.kt`](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/SplashScreen.kt) | Animated scale transition; redirects to Onboarding or Home. |
| **Onboarding** | [`OnboardingScreen.kt`](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/OnboardingScreen.kt) | Welcome stepper slides & Android 13+ permission request. |
| **Home / Today** | [`HomeScreen.kt`](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/HomeScreen.kt) | Today's checklist, progress ring, streak badge, upcoming alert banner. |
| **Add / Edit Goal** | [`AddEditGoalScreen.kt`](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/AddEditGoalScreen.kt) | Category presets, target count/units, time picker, day mask selector. |
| **Goal Details** | [`GoalDetailScreen.kt`](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/GoalDetailScreen.kt) | Detailed stats, pause/resume toggle, edit, and deletion logic. |
| **History / Calendar**| [`HistoryScreen.kt`](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/HistoryScreen.kt) | Monthly calendar view with completion badges per date. |
| **Statistics** | [`StatisticsScreen.kt`](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/StatisticsScreen.kt) | Current vs best streaks, 7-day progress bar chart, total completed days. |
| **Notification Settings**| [`NotificationSettingsScreen.kt`](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/NotificationSettingsScreen.kt) | Global notifications switch, snooze duration, test notification button. |
| **App Settings** | [`SettingsScreen.kt`](file:///c:/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/SettingsScreen.kt) | Dark / Light / System theme toggle, restore defaults & reset data. |

---

## 🧪 Testing & Verification

The project includes unit tests located under `app/src/test/java/com/dailygoal/reminder/DateUtilsTest.kt`.

### Running Unit Tests:
From Terminal or PowerShell:
```powershell
.\gradlew.bat test
```

### What is Tested:
1. **`testDayOfWeekMasking()`**: Verifies bitmask evaluation for custom day choices.
2. **`testStreakCalculationWithConsecutiveDates()`**: Validates consecutive-day streak calculation logic.
3. **`testStreakCalculationWithBrokenChain()`**: Verifies that missing days correctly reset active streak counters.

---

## ❓ Troubleshooting & FAQs

### 1. Gradle download timeout error during `.\gradlew` execution.
- **Cause**: Default Gradle wrapper download timeout (10,000ms) interrupted remote zip download.
- **Fix**: Updated [`gradle/wrapper/gradle-wrapper.properties`](file:///c:/simple-app/gradle/wrapper/gradle-wrapper.properties) to use local pre-cached Gradle 9.2.0.

### 2. `SDK location not found` error.
- **Cause**: `local.properties` file or `ANDROID_HOME` environment variable is missing.
- **Fix**: Create `c:\simple-app\local.properties` and add: `sdk.dir=C:/Users/YourUsername/AppData/Local/Android/Sdk` (or open the project once in Android Studio to generate it automatically).

### 3. Notifications are not firing on Android 13 or Android 14.
- **Cause**: Android 13 (API 33+) requires runtime permission `Manifest.permission.POST_NOTIFICATIONS`.
- **Fix**: Open device `Settings -> Apps -> Daily Goal & Reminder -> Permissions` and enable **Notifications** and **Alarms & Reminders**.

---

## 🔮 Future Roadmap & Extensibility

This application has been modularly designed to support future expansion:

* **☁️ Firebase Cloud Sync**:
  Integrate Cloud Firestore by observing `GoalDao` changes and syncing `GoalEntity` / `GoalCompletionEntity` to a remote user account.
* **📱 Home Screen App Widgets**:
  Add Android Glance widgets (`androidx.glance`) to view today's checklist directly from the device home screen.
* **🔔 Firebase Cloud Messaging (FCM)**:
  Extend `NotificationHelper` to handle remote server push notifications for team or social habit challenges.

---

## 📄 License

```
Copyright (c) 2026 Daily Goal & Reminder. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
