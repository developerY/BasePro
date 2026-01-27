import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    // ✅ REMOVED: 'kotlin.compose' handles the compiler now
    // alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mapsplatform.secrets)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

// FIX: Use strict configuration for AGP 9.0
extensions.configure<ApplicationExtension> {
    namespace = "com.ylabz.basepro.applications.photodo"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ylabz.basepro.applications.photodo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = providers.gradleProperty("isMinifyForRelease").get().toBoolean()
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// FIX: Use 'java' block for Toolchain
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// FIX: Secrets must be OUTSIDE the android block
secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {
    // Project Modules
    implementation(project(":core:util"))
    implementation(project(":core:ui"))
    implementation(project(":applications:photodo:db"))
    implementation(project(":applications:photodo:core"))
    implementation(project(":applications:photodo:features:home"))
    implementation(project(":applications:photodo:features:photodolist"))
    implementation(project(":applications:photodo:features:settings"))

    // AndroidX + Compose (Core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.expressive)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.android)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Navigation (Compose + Nav3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Serialization (Required for Nav3 Type Safety)
    implementation(libs.kotlinx.serialization.json)

    // Window Size
    implementation(libs.androidx.material3.window.size)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}