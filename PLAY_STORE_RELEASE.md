# Google Play Store Release Guide for Aimly

This document outlines the step-by-step procedure to build, sign, and release **Aimly** to the Google Play Console.

---

## 1. Prerequisites & Environment Setup

### Release Signing Credentials
Aimly uses environment variables for secure release signing in `app/build.gradle.kts`:

| Environment Variable | Description | Example |
| :--- | :--- | :--- |
| `KEYSTORE_FILE` | Absolute path to production `.jks` or `.keystore` file | `/etc/secrets/aimly-release.jks` |
| `KEYSTORE_PASSWORD` | Password for the keystore file | `SecretKeystorePassword123` |
| `KEY_ALIAS` | Alias name of the release signing key | `aimly_release_key` |
| `KEY_PASSWORD` | Password for the release key alias | `SecretKeyPassword123` |

If these environment variables are not set on a local development machine, `app/build.gradle.kts` gracefully falls back to debug signing to enable local testing without failing compilation.

---

## 2. Automated Build & Verification Commands

Run the following commands in the workspace root terminal:

```bash
# 1. Clean build cache
./gradlew clean

# 2. Run unit tests
./gradlew test

# 3. Assemble Debug APK for local QA testing
./gradlew assembleDebug

# 4. Generate Production Android App Bundle (AAB) with R8 Minification
./gradlew bundleRelease
```

---

## 3. Output Artifact Location

After `bundleRelease` completes successfully, the signed Android App Bundle is generated at:

```
app/build/outputs/bundle/release/app-release.aab
```

---

## 4. Google Play Console Deployment Steps

1. Log into [Google Play Console](https://play.google.com/console).
2. Select **Aimly** (or click **Create app**).
3. **App Details**:
   - **App Name**: Aimly
   - **Default Language**: English (US)
   - **App or Game**: App
   - **Free or Paid**: Free
4. **Complete Store Listing**:
   - Copy text from `STORE_LISTING.md`.
   - Upload app icon (512x512 PNG).
   - Upload feature graphic (1024x500 PNG).
   - Upload phone screenshots (minimum 2).
5. **App Content & Declarations**:
   - **Privacy Policy**: Provide URL or content from `PRIVACY_POLICY.md`.
   - **Data Safety**: Declare local storage (Room), FCM token collection, and analytics (if enabled).
   - **Target Audience**: 13+ (or General Audience).
   - **Exact Alarm Declaration**: Declare `SCHEDULE_EXACT_ALARM` usage for daily goal reminders.
6. **Release Management**:
   - Go to **Production** (or **Testing > Internal testing**).
   - Click **Create new release**.
   - Upload `app/build/outputs/bundle/release/app-release.aab`.
   - Review release notes and click **Save & Review Release**.
