# Comprehensive Android App Release Checklist

This document outlines the key steps and considerations to ensure optimal performance, stability, and efficiency for a release build of your Android application. Follow this checklist before every major release.

---

## 0. Foundational Setup

- [ ] **Version Control:** Ensure all changes are committed, the working directory is clean, and you are on the correct release branch.
- [ ] **Target SDK Updated:** Verify the `targetSdkVersion` in `build.gradle.kts` is set to the latest stable API level to leverage modern platform security and performance features.
- [ ] **Dependencies Updated:** Review and update critical dependencies. Use a tool like the `gradle-versions-plugin` to identify outdated libraries and test thoroughly for regressions after updating.

---

## 1. Baseline Profile Generation & Application

Baseline Profiles guide the Android Runtime (ART) to Ahead-Of-Time (AOT) compile critical user journeys (CUJs), significantly improving app startup, reducing jank, and enhancing runtime performance. This is **especially effective** when code shrinking is enabled (`isMinifyEnabled = true`).

### A. Initial Setup (One-time or when dependencies change)

- [ ] **Benchmark Module:** Ensure a dedicated `:benchmark` module exists that uses the `com.android.test` plugin and targets your app module (`targetProjectPath = ":app"`).
- [ ] **App Module (`:app`):**
    - [ ] Apply the `androidx.baselineprofile` plugin.
    - [ ] Add the `androidx.profileinstaller` dependency to ensure profiles are correctly installed on user devices (especially for Android 9+).
- [ ] **Compose UI Tooling (If applicable):**
    - [ ] Ensure any library/feature module using `@Preview` includes `debugImplementation(libs.androidx.compose.ui.tooling)`. This prevents tooling from being bundled in the release APK.

### B. Generation & Verification

- [ ] **Write/Update Profile Generator Test:** In your `:benchmark` module, create or update a test class (e.g., `BaselineProfileGenerator.kt`) that defines the critical user journeys. This test should navigate through the most common and important flows of your app.

  ```kotlin
  // Example: benchmark/src/main/java/com/example/BaselineProfileGenerator.kt
  @RunWith(AndroidJUnit4::class)
  class BaselineProfileGenerator {
      @get:Rule
      val baselineProfileRule = BaselineProfileRule()

      @Test
      fun startupAndHomeScreen() {
          baselineProfileRule.collect(
              packageName = "com.your.package.name",
              // Launch the app and perform critical actions
              profileBlock = {
                  startActivityAndWait()
                  // TODO: Add interactions here to cover CUJs like scrolling,
                  // navigating to another screen, etc.
                  // device.findObject(By.text("My List")).click()
              }
          )
      }
  }```

- [ ] **Generate the Profile:** Run the Gradle task from your project's root directory. This task builds a release version of your app, runs the UI tests from the generator, and outputs the profile rules.

  ```shell
  # Run this command from the project root
  ./gradlew :app:generateReleaseBaselineProfile
  ```

- [ ] **Verify and Commit:** Check that a new file, `app/src/main/baseline-prof.txt`, has been created or updated. This file contains the method and class rules for ART. **Commit this file to version control.**

-----

## 2\. Code Shrinking & Optimization (R8) ⚙️

R8 is the default compiler for Android that shrinks, obfuscates, and optimizes your code. It's crucial for reducing app size and protecting your intellectual property.

- [ ] **Enable R8 for Release:** In your app's `build.gradle.kts` file, ensure the `release` build type is properly configured.

  ```kotlin
  buildTypes {
      getByName("release") {
          isMinifyEnabled = true // Enables code shrinking, obfuscation, and optimization
          isShrinkResources = true // Removes unused resources (requires isMinifyEnabled)
          proguardFiles(
              getDefaultProguardFile("proguard-android-optimize.txt"),
              "proguard-rules.pro"
          )
      }
  }
  ```

- [ ] **Configure ProGuard Rules:** R8 can sometimes remove code it *thinks* is unused, like code accessed via reflection.

    - [ ] **Add Keep Rules:** In `app/proguard-rules.pro`, add `@Keep` annotations or raw ProGuard rules for classes that are serialized (e.g., with Moshi/Gson), used in native code (JNI), or are part of dependency injection frameworks.
    - [ ] **Check Library Rules:** Ensure that any included third-party libraries have their necessary ProGuard rules automatically bundled or add them manually.

- [ ] **Test Thoroughly:** After enabling R8, perform a full regression test of the release build. R8 issues often manifest as `ClassNotFoundException` or `NoSuchMethodError` at runtime.

-----

## 3\. Jetpack Compose Optimization 🚀

The Compose compiler optimizes your UI by "skipping" recomposition of composables whose inputs have not changed. This relies on the **stability** of the parameters passed to them.

- [ ] **Enable Compose Compiler Metrics:** Add the following to your root `build.gradle.kts` to generate reports that detail stability and skippability issues.

  ```kotlin
  // In build.gradle.kts
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
      kotlinOptions {
          freeCompilerArgs += "-P"
          freeCompilerArgs += "plugin:androidx.compose.compiler.reports.dest=${project.buildDir}/compose_metrics"
      }
  }
  ```

- [ ] **Analyze the Reports:** After a build (`./gradlew assembleRelease`), check the generated files in each module's `build/compose_metrics/` directory.

    - `*_composables.txt`: Shows whether each composable is `skippable` and `restartable`.
    - `*_classes.txt`: Shows the stability of classes used in your composables (`Stable` or `Unstable`).

- [ ] **Fix Stability Issues:** An unstable parameter forces a composable to recompose every time, even if the data hasn't changed.

    - **Common Cause:** Using standard `List`, `Map`, or `Set` in a data class.
    - **Solution:** Use immutable collections from `kotlinx.collections.immutable` (e.g., `ImmutableList`) or annotate your classes with `@Immutable` or `@Stable` if you can guarantee their stability.

  <!-- end list -->

  ```kotlin
  // Unstable ❌
  data class MyViewModelState(val items: List<String>)

  // Stable ✅
  import kotlinx.collections.immutable.ImmutableList
  import androidx.compose.runtime.Immutable

  @Immutable // Explicitly mark as immutable
  data class MyViewModelState(val items: ImmutableList<String>)
  ```

-----

## 4\. Build Configuration & App Bundles

- [ ] **Use Android App Bundles (.aab):** Always publish your app as an Android App Bundle. Google Play will use the `.aab` to generate and serve optimized APKs for each user's device configuration (screen density, CPU architecture, language).

- [ ] **Configure Signing:** Set up a secure signing configuration for your release build in `build.gradle.kts` and store your keystore file securely. **Never** commit your keystore or its passwords to version control. Use environment variables or a secure properties file.

  ```kotlin
  // In app/build.gradle.kts
  signingConfigs {
      create("release") {
          // Load from secure properties file or environment variables
          storeFile = file(System.getenv("KEYSTORE_FILE") ?: "my-keystore.jks")
          storePassword = System.getenv("KEYSTORE_PASSWORD")
          keyAlias = System.getenv("KEY_ALIAS")
          keyPassword = System.getenv("KEY_PASSWORD")
      }
  }
  // ... then assign it to your release build type
  buildTypes {
      getByName("release") {
          signingConfig = signingConfigs.getByName("release")
      }
  }
  ```

-----

## 5\. Analysis & Final Checks ✅

- [ ] **Static Analysis:** Run the Android Lint checker on the release variant to catch potential bugs, performance issues, and security vulnerabilities.

  ```shell
  ./gradlew :app:lintRelease
  ```

- [ ] **Profile Release Build:** Use the Android Studio Profiler (CPU, Memory, Energy) on a release build to catch any final performance bottlenecks.

> **Tip:** For easier profiling, you can create a non-obfuscated but otherwise release-like build variant.

- [ ] **Memory Leak Detection:** Ensure that leak detection libraries like **LeakCanary** are configured as `debugImplementation` only and are not included in the release build.

- [ ] **Disable Debugging:** Verify that `android:debuggable="false"` is set in the `AndroidManifest.xml` for the release build. The build system handles this automatically for the `release` build type.

- [ ] **Update Versioning:** Increment the `versionCode` and update the `versionName` in `build.gradle.kts` according to your versioning scheme.

- [ ] **Google Play Pre-Launch Report:** Upload your app bundle to an internal testing track in the Google Play Console. Thoroughly review the pre-launch report for crashes, performance issues, and display problems on a wide range of real devices before promoting the build to production.

<!-- end list -->