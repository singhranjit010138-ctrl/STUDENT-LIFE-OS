# Student Life OS — Android Studio project

This project wraps the supplied `Student_Life_OS_FIXED.html` in an Android WebView. The original HTML is stored at `app/src/main/assets/index.html` and is not rewritten.

## Open in Android Studio
1. Extract this ZIP to a folder.
2. Open Android Studio → **Open** → select the `StudentLifeOS_AndroidStudio` folder.
3. Allow Gradle sync and install the Android SDK Platform 35 if prompted.
4. Connect an Android phone with USB debugging enabled, or start an emulator.
5. Press **Run ▶**.
6. To create an APK: **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

The debug APK is normally created at `app/build/outputs/apk/debug/app-debug.apk`.

## Current implementation
- Kotlin Android launcher activity with WebView.
- Local HTML served using AndroidX WebKit `WebViewAssetLoader` on a stable HTTPS-style app origin.
- JavaScript and DOM storage enabled for the existing app and its LocalStorage database.
- External links are opened outside the app; mixed content and direct file access are disabled.
- JSON import file selection is wired through the Android file picker.

## Important notes
- This is the first Android wrapper build, not yet the Firebase/Global Connect release.
- The current HTML uses Chart.js and Google Fonts from CDNs, so charts/fonts need internet access. The core LocalStorage-based features work offline after the app has loaded, but CDN-dependent items may not.
- HTML Blob-based **Export Backup** may need a native Android save-file bridge in a later pass; JSON import file selection is supported.
- Firebase configuration, anonymous auth, Firestore chat, groups, image uploads and push notifications have not been added yet.
- LocalStorage is app-local. It does not automatically sync to another phone. Export a backup before uninstalling or clearing app data.

## App identity
- Application ID: `com.studentlifeos.app`
- Version: `1.0.0`
