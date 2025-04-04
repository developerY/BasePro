plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)

    alias(libs.plugins.mapsplatform.secrets)
}

android {
    namespace = "com.ylabz.basepro.applications.bike"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ylabz.basepro.applications.bike"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core + shared
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))

    // Feature modules
    implementation(project(":feature:listings"))
    implementation(project(":feature:camera"))
    implementation(project(":feature:places"))
    implementation(project(":feature:heatlh"))
    implementation(project(":feature:maps"))
    implementation(project(":feature:ble"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:alarm"))
    implementation(project(":feature:weather"))
    implementation(project(":feature:nfc"))
    implementation(project(":feature:ml"))

    // AndroidX + Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)   // Hilt compiler dependency for annotation processing

    // Compose Navigation
    implementation(libs.androidx.navigation.compose) // Added Compose Navigation dependency with safe args plugin
    implementation(libs.hilt.navigation.compose)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)

}