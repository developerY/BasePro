## Docs / Articels
[Android project modular with convention plugins](https://michiganlabs.com/news/making-your-android-project-modular-with-convention-plugins)   
[Gradle Docs](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html)  
[Using Convention Plugins](https://docs.gradle.org/current/samples/sample_convention_plugins.html)  
[Medium - Articel](https://medium.com/@yudistirosaputro/gradle-convention-plugins-a-powerful-tool-for-reusing-build-configuration-ba2b250d9063)      
[Vid](https://www.youtube.com/watch?v=2oYjAD1xxvo)


***Still testing ... This might not be complete***


### Step 1: Create the `build-logic` Directory

1. **Create the Directory Structure**:
    - At the root of your BasePro project, create a new directory named `build-logic`.
    - Inside `build-logic`, create the following structure:

    ```
    BasePro/
    ├── app/
    ├── data/
    ├── feature/
    ├── build-logic/
    │   ├── gradle.properties
    │   ├── settings.gradle.kts
    │   └── convention/
    │       ├── build.gradle.kts
    │       └── src/
    │           └── main/
    │               └── kotlin/
    │                   ├── AndroidConventionPlugin.kt
    │                   ├── ComposeConventionPlugin.kt
    │                   └── OtherConventionPlugins.kt
    ├── build.gradle.kts
    └── settings.gradle.kts
    ```

2. **Create the `gradle.properties` file**:
    - This file might be empty or contain specific properties related to the build logic.

    ```properties
    # Example properties
    kotlin.code.style=official
    ```

3. **Create the `settings.gradle.kts`**:
    - Configure the settings for the `build-logic` module:

    ```kotlin
    rootProject.name = "build-logic"
    
    include(":convention")
    ```

### Step 2: Set Up `build-logic/convention/build.gradle.kts`

In the `build-logic/convention/build.gradle.kts` file, configure the convention plugin module:

```kotlin
plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.7.0-alpha07")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")
}
```

### Step 3: Create Convention Plugins

In `build-logic/convention/src/main/kotlin/`, create Kotlin files for your convention plugins.

#### **AndroidConventionPlugin.kt**

```kotlin
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("kotlin-android")

            extensions.configure<CommonExtension<*, *, *, *, *>> {
                compileSdk = 34

                defaultConfig {
                    minSdk = 31
                    targetSdk = 34
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }

                kotlinOptions {
                    jvmTarget = "21"
                }
            }
        }
    }
}
```

#### **ComposeConventionPlugin.kt**

```kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("androidx.compose")

            dependencies {
                add("implementation", "androidx.compose.ui:ui")
                add("implementation", "androidx.compose.material3:material3")
                add("implementation", "androidx.compose.ui:ui-tooling-preview")
                add("implementation", "androidx.compose.runtime:runtime")
            }
        }
    }
}
```

### Step 4: Apply Plugins in Your Modules

Now that your convention plugins are set up, you can apply them in your module `build.gradle.kts` files.

#### Example for the `app` Module:

```kotlin
plugins {
    id("com.yourdomain.android.application") // Custom plugin for Android application
    id("com.yourdomain.android.application.compose") // Custom plugin for Compose
}

android {
    namespace = "com.ylabz.probase"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ylabz.probase"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    // Other configurations...
}
```

#### Example for the `data` Module:

```kotlin
plugins {
    id("com.yourdomain.android.library") // Custom plugin for Android library
}

android {
    namespace = "com.ylabz.probase.data"
    // Other configurations...
}
```

### Step 5: Configure the Root `settings.gradle.kts`

In your root `settings.gradle.kts`, include the `build-logic` project:

```kotlin
pluginManagement {
    includeBuild("build-logic")
}

include(":app", ":data", ":feature:cam", ":feature:settings")
```

### Step 6: Verify the Setup

Sync your project in Android Studio to ensure everything is set up correctly. Your custom convention plugins should now be available for all your modules, and your configurations are centralized in the `build-logic` directory.

This setup ensures that you avoid duplication, keep your build scripts clean, and maintain a single source of truth for all common configurations across your modules in the BasePro project.