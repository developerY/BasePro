plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ylabz.basepro.feature.wearos.home"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ylabz.basepro.feature.wearos.home"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = providers.gradleProperty("isMinifyForRelease").get().toBoolean()
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        // This debug block ensures a fast development cycle
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":feature:wearos:health"))
    implementation(project(":feature:wearos:sleepwatch"))
    implementation(project(":feature:wearos:drunkwatch"))


    implementation(libs.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.wear.compose.material3)
    implementation(libs.androidx.wear.compose.navigation)
    //implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.activity.compose)

    implementation(libs.horologist.compose.tools)
    implementation(libs.horologist.compose.layout)
    implementation(libs.horologist.tiles)
    implementation(libs.horologist.composables)
    implementation(libs.horologist.compose.material)


    // Hilt Dependency Injection
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    // kapt(libs.hilt.compiler)


    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.tiles)
    implementation(libs.androidx.tiles.material)
    implementation(libs.androidx.tiles.tooling.preview)
    implementation(libs.horologist.compose.tools)
    implementation(libs.horologist.tiles)
    implementation(libs.androidx.watchface.complications.data.source.ktx)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.tiles.tooling)
}