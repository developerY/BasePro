import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    // REMOVE this to fix the AGP 9.0 crash:
    // alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}

// FIX: Use strict configuration to avoid deprecation warnings
extensions.configure<LibraryExtension> {
    namespace = "com.ylabz.basepro.feature.wearos.sleepwatch"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = providers.gradleProperty("isMinifyForRelease").get().toBoolean()
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            consumerProguardFiles("proguard-rules.pro")
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
    }
}

// FIX: Use 'java' block for Toolchain (works without the kotlin-android plugin)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))

    implementation(libs.androidx.core.ktx)

    // androidx-lifecycle-viewmodel-compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.android)

    // Hilt Dependency Injection
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Health Connect
    implementation(libs.androidx.health.connect.client)

    // Wear OS System Dependencies
    implementation(libs.google.play.services.wearable)

    // --- COMPOSE FOR WEAR OS ---
    implementation(platform(libs.androidx.compose.bom))

    // UI Graphics/Tooling
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    debugImplementation(libs.androidx.ui.tooling)

    // Material 3 for Wear OS (The modern standard)
    implementation(libs.androidx.wear.compose.material3)

    // Wear specific tooling (Preview support)
    implementation(libs.androidx.wear.tooling.preview)

    // Tools
    implementation(libs.horologist.compose.tools)

    // Mobile Material 3 (Optional for Wear OS, but kept if you share UI components)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}