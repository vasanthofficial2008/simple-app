# Aimly System Architecture

Aimly is designed using modern Android Architecture Components, Clean Architecture principles, and a dual-notification system balancing offline personal reminders with remote cloud messaging.

---

## 1. System Architecture Diagram

```
                              ┌────────────────────────┐
                              │     Jetpack Compose    │
                              │        UI Layer        │
                              └───────────┬────────────┘
                                          │
                                          ▼
                              ┌────────────────────────┐
                              │     GoalViewModel      │
                              └───────────┬────────────┘
                                          │
                                          ▼
                              ┌────────────────────────┐
                              │     GoalRepository     │
                              └─────┬──────────────┬───┘
                                    │              │
           ┌────────────────────────┘              └────────────────────────┐
           ▼                                                                ▼
┌──────────────────────┐                                        ┌──────────────────────┐
│    Room Database     │                                        │    AlarmScheduler    │
│ (AppDatabase + DAOs) │                                        │ (AlarmManager System)│
└──────────────────────┘                                        └───────────┬──────────┘
                                                                            │
                                                                            ▼
                                                                ┌──────────────────────┐
                                                                │    AlarmReceiver     │
                                                                └───────────┬──────────┘
                                                                            │
                                                                            ▼
┌────────────────────────┐                                      ┌──────────────────────┐
│  FCM Remote Push Server│                                      │  NotificationHelper  │
│  (Firebase Admin SDK)  │                                      │(NotificationManager) │
└───────────┬────────────┘                                      └───────────▲──────────┘
            │                                                               │
            ▼                                                               │
┌────────────────────────┐                                                  │
│AimlyFirebaseMessaging  │──────────────────────────────────────────────────┘
│        Service         │
└────────────────────────┘
```

---

## 2. Dual Notification System

### A. Local Goal Reminders (Offline-First)
- **Engine**: Android `AlarmManager` + `PendingIntent` + `BroadcastReceiver`.
- **Triggers**: Scheduled time of day + repeat day bitmask.
- **Persistence**: Goal configurations stored in Room DB.
- **Reboot Recovery**: `BootReceiver` listens to `BOOT_COMPLETED`, `MY_PACKAGE_REPLACED`, `QUICKBOOT_POWERON` and reschedules active reminders without duplicates.
- **Doze / Battery Handling**: Uses `setExactAndAllowWhileIdle()` when `SCHEDULE_EXACT_ALARM` access is granted, with graceful fallback to `setAndAllowWhileIdle()`.

### B. Remote Push Notifications (Server Admin)
- **Engine**: Firebase Cloud Messaging (FCM) via `AimlyFirebaseMessagingService`.
- **Use Cases**: Product announcements, weekly motivation, new feature updates, community challenges.
- **Channels**: Separate `aimly_updates_channel` and `aimly_promotions_channel` allow users to customize notification preferences via Android system settings.
- **Deep Linking**: Remote payloads carry URI targets (`aimly://...`) routed directly by `MainActivity`.

---

## 3. Storage Layer

- **Database**: SQLite / Room (`daily_goal_database`, version 1).
- **Entities**:
  - `GoalEntity`: Goal properties, repeat schedule mask, reminder time, icon, color, active state.
  - `GoalCompletionEntity`: Historical completion logs per date string (`yyyy-MM-dd`), target counts, status.
- **Preferences**: SharedPreferences (`daily_goal_prefs`) storing theme, onboarding status, global notification toggle, snooze duration, FCM registration token.
