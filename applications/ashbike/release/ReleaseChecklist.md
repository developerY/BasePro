AshBike App Release Checklist
This document outlines the essential steps required to prepare the AshBike app for a production
release on the Google Play Store.

1. Code Review and Finalization
   [x] All @Preview annotations have been removed from the UI Composables.

[x] All @VisibleForTesting annotations have been removed from classes and methods that are not
intended for testing in the release build.

[ ] The BikeForegroundService.kt logging has been updated. Specifically, all Log.d calls should be
conditional to a debug flag.

[ ] All TODO comments have been resolved or re-evaluated for the release.

[ ] Ensure that a final code review has been conducted by at least one other developer.

2. Configuration and Build Settings
   [ ] In build.gradle.kts, the versionCode has been incremented.

[ ] In build.gradle.kts, the versionName has been updated (e.g., from 1.0.0-beta to 1.0.0).

[ ] The proguard-rules.pro file has been reviewed to ensure proper obfuscation and minification
without breaking app functionality.

[ ] The AndroidManifest.xml file has been reviewed for any debug flags (android:debuggable), and
these are set to false for the release build type.

3. Localization and Resources
   [ ] All strings in strings.xml have been reviewed for correctness and clarity.

[ ] All necessary translations for supported languages have been provided.

[ ] All drawable resources (icons, images) are optimized and correctly sized for different screen
densities.

4. Testing
   [ ] The app has been thoroughly tested on a range of physical devices, including various screen
   sizes and Android versions.

   [ ] All core functionalities (start/stop ride, data reset, data saving) work as expected.

   [ ] The foreground service notification appears and functions correctly during a ride.

   [ ] The app handles location permission changes gracefully.

   [ ] Battery usage has been profiled and is within acceptable limits for both active and passive
   modes.

5. App Signing and Bundle Generation
   [ ] The app is signed with the official release keystore.

[ ] A final Release AAB file has been generated for the AshBike module.

[ ] The .aab file has been analyzed using the APK Analyzer in Android Studio to confirm:

[ ] The package name is com.ylabz.basepro.applications.bike.

[ ] The classes.dex files only contain the code for the AshBike app and its core dependencies.

[ ] The AndroidManifest.xml correctly declares the app's components and permissions.

6. Store Listing and Metadata
   [ ] App screenshots and a promotional video have been prepared.

[ ] The app description, title, and keywords are finalized for the Play Store listing.

[ ] The privacy policy URL is updated and accessible.

[ ] Content ratings and target audience are correctly configured.

7. Rollout
   [ ] The AAB file is uploaded to the Google Play Console.

[ ] A phased rollout is configured (e.g., 5% rollout) to monitor for crashes or issues.

[ ] App vitals and crash reports are actively monitored during the rollout.