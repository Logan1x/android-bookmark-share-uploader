# Build APK (Debug) — Bookmark Uploader

I can’t generate an APK on this machine right now because it doesn’t have the Android toolchain installed (Java/Gradle/Android SDK).

But the project is ready to build locally in **Android Studio**.

## Steps (Android Studio)
1) Open Android Studio
2) File → Open → select this folder:
   `android-bookmark-share-uploader/android`
3) Let Gradle sync
4) Build → Build Bundle(s) / APK(s) → Build APK(s)
5) Install the APK on your phone and test:
   - Open Chrome → Share any link → choose **Bookmark Uploader**

## Notes
- Endpoint is configurable in app:
  - Open **Bookmark Uploader** from launcher.
  - Enter your full endpoint URL (for example `http://192.168.31.176:8787/v1/items`) and tap **Save Endpoint**.
  - If nothing is saved, app uses default: `http://192.168.31.176:8787/v1/items`.
- Bearer token is optional and configurable in app:
  - Leave token empty to send requests without auth.
  - If token is set, app sends `Authorization: Bearer <token>` on every request.
- MVP behavior: takes the **first URL** in shared text.

## Next improvement (I can implement)
- Offline queue + retry (WorkManager + Room)
- Better URL parsing + multiple URLs
- Optional tiny settings screen (hidden) to change endpoint without rebuilding
