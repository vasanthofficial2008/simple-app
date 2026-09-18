# Firebase Setup Guide for Aimly

This document explains how to configure **Firebase Cloud Messaging (FCM)** and Firebase services for Aimly.

---

## 1. Firebase Console Setup

1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Click **Add Project** (or select your existing Firebase project).
3. Name your project (e.g. `Aimly Production`).
4. (Optional) Enable Google Analytics for crash and engagement insights.
5. Click **Create Project**.

---

## 2. Register Android Application

1. In the project overview, click the **Android** icon to add an Android app.
2. **Android Package Name**: `com.dailygoal.reminder` (must match `applicationId` in `app/build.gradle.kts`).
3. **App Nickname**: `Aimly`.
4. **Debug signing certificate SHA-1**:
   - Run `./gradlew signingReport` in terminal to generate SHA-1 fingerprint.
   - Paste the SHA-1 fingerprint into Firebase Console.
5. Click **Register App**.

---

## 3. Configuration File (`google-services.json`)

1. Download the generated `google-services.json` file from Firebase Console.
2. Replace the placeholder file at:
   ```
   app/google-services.json
   ```
3. Ensure `google-services.json` is added to `.gitignore` if it contains environment-specific private identifiers.

---

## 4. Firebase Cloud Messaging (FCM) Topics

Aimly automatically subscribes users to default product topics upon launch:
- `aimly_all_users`: Global announcements
- `aimly_updates`: Product updates and feature announcements
- `aimly_challenges`: Weekly motivation and habit challenges

---

## 5. Server-Side Remote Notification Payload Schema

To send push notifications from your backend server or Firebase Console using FCM Admin SDK, send a JSON payload with the following structure:

```json
{
  "message": {
    "topic": "aimly_updates",
    "data": {
      "type": "ANNOUNCEMENT",
      "title": "Aimly Update v1.1",
      "body": "New habit streak insights are now available in your dashboard!",
      "deepLink": "aimly://statistics",
      "notificationId": "2001",
      "campaignId": "update_v1_1"
    }
  }
}
```

### Supported Payload Fields

| Field | Type | Description | Example |
| :--- | :--- | :--- | :--- |
| `type` | String | `ANNOUNCEMENT`, `MOTIVATION`, `WEEKLY_CHALLENGE`, `FEATURE_UPDATE`, `SYSTEM` | `ANNOUNCEMENT` |
| `title` | String | Notification header text | `Aimly Challenge` |
| `body` | String | Detailed notification message | `Complete 3 goals today to keep your streak!` |
| `deepLink` | String | Navigation route (`aimly://home`, `aimly://goal/{id}`, `aimly://history`, `aimly://statistics`, `aimly://settings`) | `aimly://goal/1` |
| `notificationId` | String | Unique numeric string for deduplication | `5001` |
| `campaignId` | String | Campaign tracking identifier | `campaign_2026_09` |

---

## 6. FCM Security & Architecture Rules

> [!CAUTION]
> - **NEVER** include Firebase Service Account Private Keys (`service-account.json`) or Admin SDK credentials inside the Android APK/AAB bundle.
> - FCM sending authority must reside strictly on a trusted server backend (e.g. Node.js server, Firebase Cloud Functions, Go/Python backend).
