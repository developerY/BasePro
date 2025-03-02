plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)  // Apply the KSP plugin
    alias(libs.plugins.hilt.gradle)  // Added Hilt plugin
    // alias(libs.plugins.kotlin.kapt) need to
    alias(libs.plugins.kotlin.serialization)  // Added Kotlin serialization plugin)
    alias(libs.plugins.mapsplatform.secrets)

}

android {
    namespace = "com.ylabz.basepro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ylabz.basepro"
        minSdk = 31
        targetSdk = 34
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

    secrets {
        defaultPropertiesFileName = "secrets.defaults.properties"
    }

}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":feature:listings"))
    implementation(project(":feature:home"))
    implementation(project(":feature:camera"))
    implementation(project(":feature:places"))
    implementation(project(":feature:heatlh"))
    implementation(project(":feature:maps"))
    implementation(project(":feature:ble"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:shotime"))
    implementation(project(":feature:alarm"))
    implementation(project(":feature:bike"))
    implementation(project(":feature:weather"))

    // feature module  dependency
    implementation(project(":feature:settings"))  // feature module  dependency
    implementation(project(":core:ui"))
    implementation(project(":core:model")) // feature module  dependency

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)   // Hilt compiler dependency for annotation processing

    // Compose Navigation
    implementation(libs.androidx.navigation.compose) // Added Compose Navigation dependency with safe args plugin
    implementation(libs.kotlinx.serialization.json) // Added Kotlin serialization dependency
    implementation(libs.hilt.navigation.compose)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}
