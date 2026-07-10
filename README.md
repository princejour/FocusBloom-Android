# FocusBloom

FocusBloom is a polished, offline-first Android app for focus sessions, daily habits, tasks, breathing, and private progress insights. It is built with Kotlin and Jetpack Compose and targets Android 16 (API 36).

## What is included

- 25, 45, and 60-minute focus timer with pause and resume
- Daily habits with completion history and streak calculation
- Task list with open, completed, and all filters
- Seven-day focus chart and progress metrics
- Guided 4–4–6 breathing exercise
- English, Arabic (RTL), and French interfaces
- Light, dark, and system themes
- Phone, tablet, foldable, and desktop-window adaptive navigation
- Local-only productivity data with no account required
- AdMob banner integration using Google's test identifiers by default
- Google UMP consent flow and in-app privacy choices entry point
- Automated APK/AAB build and tests with GitHub Actions

## Technology

- Kotlin 2.3.21
- Jetpack Compose BOM 2026.06.00
- Android Gradle Plugin 9.2.1
- Gradle 9.4.1
- Google Mobile Ads SDK 25.4.0
- Google UMP SDK 4.0.0

## Build

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug bundleRelease
```

The debug APK is generated in `app/build/outputs/apk/debug/`. The release bundle is generated in `app/build/outputs/bundle/release/`.

## Production AdMob configuration

The source code intentionally uses Google's sample AdMob IDs, so test builds cannot create invalid traffic. Add these GitHub repository secrets before publishing:

- `ADMOB_APP_ID`
- `ADMOB_BANNER_ID`

Create privacy messages for the app in AdMob under **Privacy & messaging**. The UMP SDK requests the latest consent state at every app launch.

## Release signing

Add these GitHub repository secrets to receive signed release files from the workflow:

- `KEYSTORE_BASE64`: the release keystore encoded as Base64
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

Never commit a keystore or its passwords. Keep the same key for every future store update.

## Before store submission

1. Replace the support email placeholder in `PRIVACY_POLICY.md` and `docs/privacy-policy.html`.
2. Add production AdMob IDs as repository secrets.
3. Configure AdMob privacy messages.
4. Add release signing secrets.
5. Run the GitHub Actions workflow and download the signed AAB artifact.
6. Complete each store's data-safety and advertising declarations using your final configuration.

## Ownership

Copyright © 2026 Walid Ben Jemaa. All rights reserved. See `LICENSE.txt`.
