import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    // ✅ REMOVED: 'kotlin.compose' handles the compiler now
    // alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}

// FIX: Use strict configuration for AGP 9.0
extensions.configure<LibraryExtension> {
    namespace = "com.ylabz.basepro.ashbike.mobile.features.glass"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        // If this module is actually an Application module (generates an APK),
        // uncomment the targetSdk line below:
        // targetSdk = libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

// FIX: Use 'java' block for Toolchain
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:data"))

    // --- Core Android ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Jetpack Compose (BOM) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    // Note: Glimmer handles its own Material-like components, but if you need standard M3:
    implementation(libs.androidx.material3)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    // --- XR / Glass (Glimmer) ---
    // Specifically required for GlimmerTheme, Button, Card, surface, etc.
    implementation(libs.androidx.glimmer)
    // Usually required for the projection/session capability in XR
    implementation(libs.androidx.projected)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)

    // --- Debugging ---
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}