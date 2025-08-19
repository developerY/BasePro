# Comprehensive Performance Release Checklist

This document outlines key steps and considerations to ensure optimal performance, stability, and efficiency for a release build of your Android application.

## 0. Foundational Setup
- [ ] **Version Control:** Ensure all changes are committed and the repository is clean before starting the release process.
- [ ] **Target SDK Updated:** Verify the `targetSdk` is set to a recent API level.
- [ ] **Dependencies Updated:** Review and update critical dependencies, testing thoroughly for regressions.
- [ ] **Build Signing & Keystore Security:**
    - [ ] Ensure release builds are signed with your official upload key.
    - [ ] **Keystore Security:** Store your keystore file and its credentials securely. **Never** commit them to version control.
    - [ ] For teams, use a shared, secure vault or a CI/CD system with secure credential management for keystores.

## 1. Baseline Profile Generation & Application

Baseline Profiles guide ART (Android Runtime) to optimize critical user journeys (CUJs) ahead of time, significantly improving app startup and runtime performance, especially when `isMinifyEnabled = true`.

**For each application module (e.g., `:applications:ashbike`):**

### A. Initial Setup (One-time or when dependencies change):
- [ ] **Benchmark Module Configuration:**
    - [ ] Ensure a dedicated benchmark module exists (e.g., `:applications:ashbike:benchmark`).
    - [ ] `build.gradle.kts` for benchmark module:
        - [ ] Uses `com.android.test` plugin.
        - [ ] Defines `android.defaultConfig.targetProjectPath = ":your-app-module"`.
        - [ ] Includes dependencies: `androidx.benchmark.macro.junit4`, `androidx.test.uiautomator`, `androidx.test.ext:junit`, `androidx.test:runner`.
        - [ ] Has a `benchmark` build type (e.g., `create("benchmark") { isDebuggable = false; signingConfig = debug.signingConfig; matchingFallbacks += "release"; enableAndroidTestCoverage = false; }`).
- [ ] **Application Module Configuration (`:your-app-module`):**
    - [ ] `build.gradle.kts` for the app module:
        - [ ] Applies the `androidx.baselineprofile` plugin (e.g., `alias(libs.plugins.androidx.baselineprofile)`).
        - [ ] Includes `implementation(libs.androidx.profileinstaller)` dependency.
- [ ] **Project Root `build.gradle.kts`:**
    - [ ] Applies the `androidx.baselineprofile` plugin (e.g., `alias(libs.plugins.androidx.baselineprofile) apply false`).
- [ ] **Compose UI Tooling Dependencies (for modules using `@Preview`):**
    - [ ] Ensure any library/feature module that uses `@Preview` and is a dependency of the app includes:
        
```kotlin
        // In the feature/library module's build.gradle.kts
        dependencies {
            // ...
            debugImplementation(libs.androidx.compose.ui.tooling)
            debugImplementation(libs.androidx.compose.ui.tooling.preview)
        }
        
```
        - [ ] This ensures the app build (especially `benchmark` type) can resolve these symbols.

### B. Generating/Updating the Profile:
- [ ] **Create/Update Baseline Profile Generator Test:**
    - [ ] Located in the benchmark module (e.g., `benchmark_module/src/androidTest/java/com/your/package/BaselineProfileGenerator.kt`).
    - [ ] **Crucial:** Set `packageName = "your.app.applicationId"` correctly in the `collect()` method.
    - [ ] **Crucial:** Define comprehensive Critical User Journeys (CUJs) in the `profileBlock{}`. This should cover common startup flows and interactions (scrolling, navigation, etc.).
- [ ] **Prepare for Generation:**
    - [ ] Use a **rooted physical device** or an **emulator with a userdebug/eng AOSP image** (API 28+).
- [ ] **Run the Generator:**
    - [ ] From IDE: Right-click `BaselineProfileGenerator.kt` -> Run.
    - [ ] From Terminal: `./gradlew :your-app-module:benchmark:generateBaselineProfile` (or similar).
- [ ] **Verify Output:**
    - [ ] Check run logs for the path to `baseline-prof.txt`. Location: `your-app-module/benchmark/build/outputs/baseline_profile/benchmark/baseline-prof.txt` (path may vary).

### C. Applying the Profile to the App:
- [ ] **Copy `baseline-prof.txt`:**
    - [ ] From the benchmark module's output to `your-app-module/src/main/baseline-prof.txt`.
    - [ ] Commit this file to version control.
- [ ] **Profile Regeneration Cadence:** Regenerate profiles when:
    - [ ] Significant UI or navigation changes occur in critical user journeys.
    - [ ] Major library versions are updated (especially UI libraries like Compose, Navigation).
    - [ ] Periodically (e.g., quarterly or before major releases) to catch regressions or incorporate benefits from toolchain updates.
- [ ] **Verify Profile Installation (Development):**
    - [ ] For `profileinstaller` to work during development, ensure your app's `AndroidManifest.xml` (for `debug` or custom profileable builds) has `<profileable android:shell="true" tools:targetApi="q" />`.

## 2. Release Build Configuration (R8/ProGuard)

Optimizing your code with R8 is critical for size reduction and performance.

- [ ] **Enable Minification and Shrinking:**
    - [ ] In your app module's `build.gradle.kts` under `buildTypes.release`:
        
```kotlin
        isMinifyEnabled = true  // Enables R8 for code shrinking, obfuscation, and optimization
        isShrinkResources = true // Removes unused resources after code shrinking
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        
```
        - [ ] **`isMinifyEnabled = true` is ESSENTIAL.** Do not set to `false` for release. Baseline Profiles are most effective with minification.
- [ ] **ProGuard Rules (`proguard-rules.pro`):**
    - [ ] **Thoroughly Test:** After any code change or library update, test extensively to ensure ProGuard rules are correct. Missing rules are a common source of release build crashes.
    - [ ] **Keep Necessary Code:** Ensure rules preserve classes/members accessed via reflection, JNI, or that are entry points (Activities, Services, etc. are usually handled by `proguard-android-optimize.txt`).
    - [ ] **Specificity:** Be as specific as possible with ProGuard keep rules to maximize shrinking and avoid unintended side effects. For example, prefer keeping specific class members over entire classes if possible.
    - [ ] **Library Rules:** Include ProGuard rules provided by third-party libraries.
    - [ ] **Data Classes/Serialization:** Pay special attention to rules for data classes used with serialization libraries (e.g., Gson, Moshi, KotlinX Serialization).
    - [ ] **Rule Validation:** Use tools to validate ProGuard rules:
        - [ ] Android Studio's "Analyze APK" feature: Inspect the resulting DEX files to ensure expected classes/methods are present and that shrinking is effective.
        - [ ] Consider R8's mapping output file (`mapping.txt`) to understand renaming and removal decisions.
    - [ ] **Remove Unused Rules:** Periodically review and remove outdated or unnecessary ProGuard rules.
- [ ] **R8 Full Mode (Consider for Advanced Optimization):**
    - [ ] If not enabled by `proguard-android-optimize.txt`, consider adding `android.enableR8.fullMode=true` in `gradle.properties` for potentially better optimizations, but test even more rigorously.
- [ ] **Disable Debugging:**
    - [ ] Ensure `android.buildTypes.release.isDebuggable = false`.

## 3. Jetpack Compose Performance

- [ ] **Stability (`@Stable` / `@Immutable`):**
    - [ ] Ensure Compose compiler can infer stability for your classes or explicitly annotate them with `@Stable` or `@Immutable` where applicable. Unstable classes can lead to excessive recompositions.
        - [ ] `@Immutable`: Guarantees that all public properties are `val` and of immutable types. Once constructed, the object's state will not change.
        - [ ] `@Stable`: A weaker guarantee. It implies that if any public property of the type changes, recomposition will be triggered for composables observing it. The type must also ensure that all public methods produce consistent results if their parameters are the same.
    - [ ] Use immutable collections (e.g., `kotlinx.collections.immutable`) for collections passed to Composables.
    - [ ] Primitives, functional types, and certain Compose types are inherently stable.
    - [ ] **Lambda Stability:** Be mindful of lambda stability. Non-capturing lambdas are generally stable. Lambdas that capture unstable variables can make a composable unstable or prevent it from being skippable. Use `remember` for lambdas that capture changing variables if the lambda itself should not cause recomposition when those variables change.
- [ ] **Minimize Recompositions:**
    - [ ] Use `remember` wisely for expensive calculations or object allocations.
    - [ ] Defer reading `State` objects as late as possible in your Composables. Pass lambdas that read state rather than passing state directly if it can prevent recomposition of parent Composables.
    - [ ] Use derived states like `derivedStateOf` for values that change only when their inputs change.
    - [ ] Profile recompositions using Layout Inspector in Android Studio ("Recomposition Counts").
- [ ] **Lazy Layouts (`LazyColumn`, `LazyRow`, etc.):**
    - [ ] Provide `key`s for items, especially if the list can change dynamically, to help Compose optimize item handling and preserve state.
    - [ ] Use `contentType` if you have different types of items for better recycling and performance.
- [ ] **Custom Layouts:** Profile and optimize custom `Layout` Composables if they are complex.
- [ ] **Avoid Unnecessary Allocations:** Be mindful of allocations (e.g., new Modifiers, lambdas without `remember`) within Composable functions that are frequently recomposed.

## 4. App Startup Optimization
- [ ] **App Startup Library:** Use the `androidx.startup` library to initialize components at app startup more efficiently and explicitly.
- [ ] **Defer Non-Critical Initialization:** Move any initialization not essential for the first frame off the main thread or delay it.
- [ ] **Cold Start Time:** Profile and optimize cold start time (Time To Initial Display - TTID, Time To Full Display - TTFD).

## 5. Memory Management
- [ ] **Leak Detection:**
    - [ ] Use LeakCanary in debug builds to detect memory leaks.
    - [ ] **Staging/Internal Builds:** For final internal testing, consider creating a "staging" or "internal release" build type that is release-like (minified) but might still include LeakCanary. This must be handled with extreme care and never be used for the actual Play Store release.
    - [ ] Profile memory usage with Android Studio's Memory Profiler, looking for unexpected growth or retained objects.
- [ ] **Bitmap Optimization:**
    - [ ] Load bitmaps at the correct size for the display area.
    - [ ] Use efficient image loading libraries (e.g., Coil, Glide) that handle caching and bitmap pooling.
    - [ ] Choose appropriate bitmap configurations (e.g., `RGB_565` if opacity is not needed and color fidelity allows).
- [ ] **Object Pooling:** For frequently created, short-lived objects, consider object pooling if profiling shows significant GC pressure.
- [ ] **Caching Strategy:** Implement sensible caching for data and resources, but also ensure caches are properly managed to avoid excessive memory use.

<strike>
## 6. UI Performance & Rendering
</strike>
**Note for Compose-Only Project:** As this project primarily uses Jetpack Compose, the points below related to XML Views (Overdraw, Efficient Layouts, RecyclerView) are largely historical context. They serve as a good reminder of the UI challenges Compose helps address. However, **Jank Detection** remains a critical aspect for all UI development, including Compose.

- [ ] **Overdraw (XML Views):** Use "Debug GPU Overdraw" developer option to identify and reduce overdraw.
- [ ] **Jank Detection:**
    - [ ] Use Android Studio Profilers (CPU, JankStats, System Trace/Perfetto).
    - [ ] Aim for smooth frame rates (typically 60 FPS or higher depending on device capabilities).
- [ ] **Efficient Layouts (XML):**
    - [ ] Use `ConstraintLayout` for flat view hierarchies.
    - [ ] Avoid deep or nested layouts.
    - [ ] Use `<merge>` and `<include>` tags effectively.
- [ ] **RecyclerView (XML):**
    - [ ] Ensure `ViewHolder` pattern is correctly implemented.
    - [ ] Use `DiffUtil` or `AsyncListDiffer` for efficient list updates.
    - [ ] Optimize item view layouts.

## 7. Network Performance
- [ ] **Efficient Data Formats:** Use efficient data formats like Protocol Buffers (protobuf) or FlatBuffers over JSON where applicable, especially for large or frequent requests.
- [ ] **Caching:** Implement HTTP caching (e.g., ETag, Cache-Control headers) and local caching of network responses.
- [ ] **Request Batching/Collapsing:** Batch multiple small requests or collapse redundant requests.
- [ ] **Minimize Data Transferred:** Only request the data you need. Use pagination.
- [ ] **Connection Pooling:** Ensure your HTTP client is configured for efficient connection pooling.
- [ ] **Background Sync Strategy:** Use WorkManager for deferrable background syncs, respecting battery-saving modes.

## 8. Concurrency & Threading
- [ ] **Main Thread Protection:** Never perform blocking operations (network, disk I/O, heavy computation) on the main thread.
- [ ] **Coroutines/RxJava:** Use structured concurrency with Kotlin Coroutines or manage subscriptions carefully with RxJava.
- [ ] **Thread Pools:** Use appropriate `Dispatchers` (Coroutines) or Schedulers (RxJava) for different types of work. Avoid creating raw threads.

## 9. APK Size Reduction
- [ ] **Analyze APK:** Use Android Studio's "Analyze APK" feature regularly.
- [ ] **App Bundles (.aab):** Publish using Android App Bundles to leverage Google Play's Dynamic Delivery.
- [ ] **Remove Unused Code/Resources:** `isMinifyEnabled = true` and `isShrinkResources = true` are the primary tools.
- [ ] **Optimize Images:** Use WebP format for images. Compress PNGs and JPEGs.
- [ ] **Vector Graphics:** Use VectorDrawables for simple icons instead of multiple PNGs.
- [ ] **Modularization:** Consider dynamic feature modules for features not needed by all users at install time.
- [ ] **Library Review:** Periodically review dependencies for unused or overly large libraries.

## 10. Pre-Release Testing & Verification
- [ ] **Static Analysis:**
    - [ ] Run Android Lint on the release variant: `./gradlew :your-app-module:lintRelease`.
    - [ ] For larger projects, consider establishing a lint baseline (`lintOptions { baseline = file("lint-baseline.xml") }`) to manage existing warnings while preventing new ones.
- [ ] **Test on Diverse Devices:** Test on a range of devices (different API levels, screen sizes, manufacturers).
- [ ] **Test on Release Builds:** Install the release-configured build (minified, profiled) on target devices.
- [ ] **Manual CUJ Testing:** Manually go through all Critical User Journeys.
- [ ] **Automated Testing:** Ensure UI tests (Espresso, UI Automator) and unit tests pass on release-like configurations.
- [ ] **Macrobenchmarks (Beyond Baseline Profile):**
    - [ ] Use `androidx.benchmark.macro` for measuring app startup, scrolling, and other interactions on release builds to track performance regressions or improvements.
- [ ] **Performance Profiling & Monitoring Tools:**
    - [ ] Android Studio Profilers (CPU, Memory, Network, Energy). Focus on:
        - [ ] App startup (cold, warm, hot).
        - [ ] Scrolling performance in complex lists or views.
        - [ ] Screen transitions and animations.
    - [ ] Perfetto / Systrace for in-depth system-level tracing.
- [ ] **Firebase Performance Monitoring (or similar):** Integrate SDKs to monitor performance in the wild.
- [ ] **Google Play Console Pre-Launch Report:**
    - [ ] Thoroughly review for crashes, performance issues, and display problems.
    - [ ] **Device Variety:** Pay close attention to results across different Android versions, form factors (phones, tablets, foldables), and manufacturers.
- [ ] **Review ANR & Crash Reports:** Check for any new ANRs or crashes introduced, using Play Console or other crash reporting tools.

## 11. Post-Release Monitoring
- [ ] **Monitor Vitals:** Keep a close eye on Android Vitals in the Google Play Console (ANRs, crashes, stuck wake-locks, excessive wakeups).
- [ ] **User Feedback:** Monitor user feedback channels for performance-related complaints.
- [ ] **Performance Regression Tracking:** Continuously track key performance metrics release over release.

---
Remember to update this document as your performance strategies, tools, and application evolve.
