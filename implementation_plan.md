# Implementation Plan - Aimly: Complete Production / Play Store / FCM Upgrade

This document outlines the step-by-step engineering plan to transform the existing native Android application into **Aimly** — a production-ready daily goal, habit, progress, streak, and reminder application suitable for Google Play Store release.

---

## Technical Audit & Key Architectural Findings

1. **Gradle & Build Tooling Incompatibility**:
   - The project currently uses AGP `8.2.2` with Gradle Wrapper `9.2.0-bin.zip`. Gradle 9.2.0 breaks AGP 8.2.2 configuration resolution (`:app:processDebugResources` failure).
   - **Fix**: Downgrade Gradle wrapper to `8.7-bin.zip` or update AGP to compatible version (`8.5.2`) and Kotlin `1.9.24` / `2.0.0` with compatible KSP.
   - **Target SDK**: Standard targetSdk `34` (Android 14) and compileSdk `34` or `35` satisfy current Google Play requirements.
2. **Branding Transition**:
   - Current app label: "Daily Goal & Reminder" / "Daily Goal Reminder".
   - **Target Brand**: **Aimly**.
   - Package / Application ID: `com.dailygoal.reminder` (preserved as permanent production identifier to avoid data migration issues).
3. **Dual Notification Architecture**:
   - **Local Scheduled Reminders**: `AlarmManager` + `PendingIntent` + `BroadcastReceiver` (`AlarmReceiver`, `BootReceiver`, `NotificationActionReceiver`, `SnoozeReceiver`). Continues running offline, on doze mode, reboot, or app update.
   - **Remote Push Notifications**: Firebase Cloud Messaging (FCM) via `AimlyFirebaseMessagingService`. Handles server-side announcements, weekly challenges, motivation messages, feature updates.
   - **Deduplication & Channel Separation**: Local reminders and remote push notifications use separate channels (`aimly_reminders_channel`, `aimly_updates_channel`, `aimly_promotions_channel`) and distinct notification ID ranges.

---

## User Review Required

> [!IMPORTANT]
> - **Firebase Configuration**: The Android app will include the complete FCM service (`AimlyFirebaseMessagingService`), token management (`PushTokenRepository`), notification handlers, and topic subscriptions. The actual `google-services.json` file can be added by the developer following `FIREBASE_SETUP.md`.
> - **Release Signing**: Release build configuration in `app/build.gradle.kts` will read environment variables (`KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`) and fallback gracefully to debug signing if release signing environment variables are not supplied.
> - **Exact Alarm Policy**: Uses `SCHEDULE_EXACT_ALARM` with runtime check `alarmManager.canScheduleExactAlarms()`. If exact alarm permission is denied, it falls back to `setAndAllowWhileIdle()` and displays a helpful prompt in `NotificationSettingsScreen` to open Android system settings.

---

## Proposed Changes

### Phase 1: Build Tooling & Infrastructure Upgrade
#### [MODIFY] [gradle-wrapper.properties](file:///c:/ll/simple-app/gradle/wrapper/gradle-wrapper.properties)
- Downgrade/adjust Gradle version to `8.7` or `8.9` for AGP 8.2.2 / 8.5 compatibility.

#### [MODIFY] [libs.versions.toml](file:///c:/ll/simple-app/gradle/libs.versions.toml)
- Add Firebase BoM (`33.1.0`), Firebase Messaging, Firebase Analytics.
- Update Google Services plugin alias.

#### [MODIFY] [build.gradle.kts](file:///c:/ll/simple-app/build.gradle.kts)
- Add `com.google.gms.google-services` plugin.

#### [MODIFY] [app/build.gradle.kts](file:///c:/ll/simple-app/app/build.gradle.kts)
- Apply Google Services plugin.
- Add Firebase Messaging and Analytics dependencies.
- Add `signingConfigs` block reading environment variables for production AAB release.
- Enable `isMinifyEnabled = true` and `isShrinkResources = true` for release build.

#### [MODIFY] [proguard-rules.pro](file:///c:/ll/simple-app/app/proguard-rules.pro)
- Add ProGuard/R8 keep rules for Room DB, Firebase Cloud Messaging, Kotlin Coroutines, Jetpack Compose.

---

### Phase 2: Branding Upgrade to "Aimly"
#### [MODIFY] [strings.xml](file:///c:/ll/simple-app/app/src/main/res/values/strings.xml)
- Update `app_name` to `Aimly`.
- Update strings across settings, onboarding, splash screen, notification channels.

#### [MODIFY] UI Screens ([SplashScreen.kt](file:///c:/ll/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/SplashScreen.kt), [OnboardingScreen.kt](file:///c:/ll/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/OnboardingScreen.kt), [SettingsScreen.kt](file:///c:/ll/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/SettingsScreen.kt))
- Replace all references of "Daily Goal & Reminder" with **Aimly**.

#### [NEW] [ic_launcher.xml / Vector Resources](file:///c:/ll/simple-app/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml)
- Create modern adaptive launcher icon vector assets matching Aimly brand aesthetics.

---

### Phase 3: Firebase Cloud Messaging & Remote Push Architecture
#### [NEW] [PushTokenRepository.kt](file:///c:/ll/simple-app/app/src/main/java/com/dailygoal/reminder/data/repository/PushTokenRepository.kt)
- Repository to handle FCM token retrieval, local token caching, topic subscriptions (`aimly_updates`, `aimly_announcements`, `aimly_challenges`), and backend synchronization interface.

#### [NEW] [AimlyFirebaseMessagingService.kt](file:///c:/ll/simple-app/app/src/main/java/com/dailygoal/reminder/notification/AimlyFirebaseMessagingService.kt)
- FCM service listening to `onNewToken` and `onMessageReceived`.
- Parses payload JSON/data (`type`, `title`, `body`, `deepLink`, `notificationId`, `campaignId`).
- Safely validates payloads and triggers `NotificationHelper`.

#### [MODIFY] [NotificationHelper.kt](file:///c:/ll/simple-app/app/src/main/java/com/dailygoal/reminder/notification/NotificationHelper.kt)
- Add notification channels:
  - `aimly_reminders_channel` (Local goal reminders - High Importance)
  - `aimly_updates_channel` (Remote updates & announcements - Default Importance)
  - `aimly_promotions_channel` (Remote challenges & motivation - Default Importance)
- Add `showRemoteNotification()` method supporting deep links into Aimly screens.

---

### Phase 4: Local Reminder System & Exact Alarm Hardening
#### [MODIFY] [AlarmScheduler.kt](file:///c:/ll/simple-app/app/src/main/java/com/dailygoal/reminder/notification/AlarmScheduler.kt)
- Check `alarmManager.canScheduleExactAlarms()` on Android 12+ (API 31+).
- Fallback to `setAndAllowWhileIdle()` when exact alarms are restricted.

#### [MODIFY] [DateUtils.kt](file:///c:/ll/simple-app/app/src/main/java/com/dailygoal/reminder/util/DateUtils.kt)
- Fix date formatting using `Locale.US` for standard ISO strings (`yyyy-MM-dd`) to guarantee cross-locale consistency.

#### [MODIFY] [NotificationSettingsScreen.kt](file:///c:/ll/simple-app/app/src/main/java/com/dailygoal/reminder/ui/screens/NotificationSettingsScreen.kt)
- Add exact alarm status indicator and button to open System Alarm & Reminder Settings if permission is missing.

---

### Phase 5: Deep Link Navigation & Activity Integration
#### [MODIFY] [MainActivity.kt](file:///c:/ll/simple-app/app/src/main/java/com/dailygoal/reminder/MainActivity.kt)
- Parse incoming deep links (`aimly://...` or intent extras) and route to specific screens (`Home`, `GoalDetail`, `History`, `Statistics`, `NotificationSettings`).

---

### Phase 6: Documentation, Security & Play Store Assets
#### [NEW] [FIREBASE_SETUP.md](file:///c:/ll/simple-app/FIREBASE_SETUP.md)
- Complete guide on setting up Firebase Console, downloading `google-services.json`, server-side Admin SDK push delivery.

#### [NEW] [PLAY_STORE_RELEASE.md](file:///c:/ll/simple-app/PLAY_STORE_RELEASE.md)
- Guide on environment variable signing, building AAB via `./gradlew bundleRelease`, uploading to Play Console.

#### [NEW] [AIMLY_ARCHITECTURE.md](file:///c:/ll/simple-app/AIMLY_ARCHITECTURE.md)
- Complete system architecture documentation (Local AlarmManager + Remote FCM + Room DB + Clean Architecture).

#### [NEW] [PRIVACY_POLICY.md](file:///c:/ll/simple-app/PRIVACY_POLICY.md)
- Production privacy policy draft covering local data, FCM tokens, notifications, third-party analytics.

#### [NEW] [STORE_LISTING.md](file:///c:/ll/simple-app/STORE_LISTING.md)
- Production-ready Google Play Store metadata draft (Title, Short Description, Full Description, Keywords).

#### [MODIFY] [.gitignore](file:///c:/ll/simple-app/.gitignore)
- Ensure keystores, `google-services.json`, credentials, local property files are protected.

---

## Verification Plan

### Automated Tests
- `./gradlew test` - Unit test execution for `DateUtilsTest`, `GoalRepositoryTest`, `FcmPayloadParserTest`.
- `./gradlew assembleDebug` - Validate debug APK compilation.
- `./gradlew bundleRelease` - Validate production release AAB compilation with R8 minification.

### Manual / Integration Verification
- Verify app name displayed as **Aimly**.
- Verify FCM token generation and topic subscription handling.
- Verify exact alarm fallback handling without crashes.
- Verify generated AAB output path (`app/build/outputs/bundle/release/app-release.aab`).
