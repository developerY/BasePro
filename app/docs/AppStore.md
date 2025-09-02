It’s not terribly difficult to configure Gradle so that you can **publish exactly one app** (with
its own icon, code, etc.)
to the Play Store—even if you have multiple apps in the same codebase. Below are some common
approaches:

---

## 1. **Separate Application Modules**

If you have **one module per app**, each with its own `build.gradle` (and its own `applicationId`,
icon, etc.),
you can simply build and upload the module you want. The other apps won’t be included in that final
APK or AAB because
they’re separate Gradle modules.

**How it works**:

1. **Multiple application modules** under something like `applications/bike`, `applications/home`,
   etc.
2. Each module applies the `com.android.application` plugin, has its own `AndroidManifest.xml`, and
   references the feature modules it needs.
3. To build a single app, run (for example) `./gradlew :applications:bike:assembleRelease`.
4. That produces an APK/AAB that includes only the code used by the `bike` module (plus whatever
   libraries/features it depends on).

This approach is **very straightforward**—you have a 1:1 mapping of Gradle application modules to
apps.

---

## 2. **Single App Module with Product Flavors**

Alternatively, if you want to keep just **one** application module but produce multiple “apps” from
it
(different icons, app IDs, etc.), you can use **Gradle product flavors**. Each flavor can define:

- A unique **applicationId** (so they install as separate apps if needed).
- Different icons, resources, or even code.
- Different feature inclusion (via flavor dimensions or build-type checks).

**How it works**:

1. In your `app/build.gradle`, define flavors:
   ```groovy
   android {
       flavorDimensions "default"
       productFlavors {
           bike {
               dimension "default"
               applicationId "com.example.bike"
               versionNameSuffix "-bike"
           }
           home {
               dimension "default"
               applicationId "com.example.home"
               versionNameSuffix "-home"
           }
       }
   }
   ```
2. Place app-specific resources (icons, strings) in flavor-specific folders:
    - `src/bike/res/mipmap-anydpi-v26/ic_launcher.xml`
    - `src/home/res/mipmap-anydpi-v26/ic_launcher.xml`
3. Build just the flavor you want with:
   ```bash
   ./gradlew assembleBikeRelease
   ```
   or
   ```bash
   ./gradlew assembleHomeRelease
   ```
4. The generated APK/AAB includes only the code/resources for that flavor.

This approach can be more **complex** to maintain, especially if each “app” is quite different.
However, it’s convenient if they share most code and differ only in branding, icons, or minimal
feature toggles.

---

## 3. **Exclude Unused Features with Gradle Dependencies**

Even if you have many feature modules, you can ensure only the needed features get packaged by
controlling **which modules** your “app” depends on. Gradle won’t bundle modules that aren’t
referenced.

- For example, in `app/build.gradle`:
  ```kotlin
  dependencies {
      // Only include camera and maps for this flavor
      implementation(project(":features:camera"))
      implementation(project(":features:maps"))
      // ...
  }
  ```
- If you omit `implementation(project(":features:alarm"))`, that alarm code never ends up in your
  final APK.

This can be combined with product flavors (Approach #2) if you want certain flavors to include
certain features.

---

## 4. **How Difficult Is It?**

- **Separate application modules** is typically the simplest if each app is truly distinct. You just
  build whichever app module you want for the Play Store, and that’s it.
- **Product flavors** require a bit more initial setup, especially if each app has a different icon,
  name, or dependencies. But once configured, it’s a single module that can produce many different
  APKs.

In both cases, Gradle (and the Android Gradle Plugin) make it relatively straightforward to *
*exclude**
code that’s not referenced by the chosen module/flavor.

**Bottom line**: It’s not hard to set up once you pick the approach that fits your project
structure.
If each app is in its own Gradle module already, you can just build that module. If you want
multiple
“apps” from a single module, use product flavors. Either way, you can produce an APK/AAB containing
only
the code and icon for the specific app you want to ship.