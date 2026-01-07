plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Add Hilt and KSP
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ylabz.basepro.ashbike.wear"
    compileSdk = libs.versions.compileSdk.get().toInt() // Match your mobile sdk version

    defaultConfig {
        applicationId = "com.ylabz.basepro.ashbike.wear"
        minSdk = 35 // Health Services requires Min SDK 30
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

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

    // REPLACE kotlinOptions with the Toolchain block
    kotlin {
        jvmToolchain(21)
    }

    useLibrary("wear-sdk")
    buildFeatures {
        compose = true
    }
}

dependencies {
    // 1. Core Modules - Share logic with Mobile!
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:util"))
    implementation(project(":applications:ashbike:features:main"))
    implementation(project(":applications:ashbike:database")) // Optional: for local DB



    // 2. Hilt (Dependency Injection)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // 3. Health Services (The MAD way to track rides on Wear)
    // Health Services (The MAD way to track rides)
    implementation(libs.androidx.health.services.client)

    // Ongoing Activity (The icon at the bottom of the watch face)
    implementation(libs.androidx.wear.ongoing)

    // Lifecycle Service (Required for the service to bind to lifecycle)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.wear.compose.navigation)

    // 4. Standard Wear OS & Compose UI
    implementation(libs.google.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.wear.compose.material3)
    implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Permission
    implementation(libs.google.accompanist.permissions)


    // 5. Horologist (Google's best practices library for Wear)
    implementation(libs.horologist.compose.tools)
    implementation(libs.horologist.tiles)
    implementation(libs.androidx.watchface.complications.data.source.ktx)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.tiles.tooling)
}