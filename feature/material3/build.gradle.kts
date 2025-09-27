plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    // Optional, provides the @Serialize annotation for autogeneration of Serializers.
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ylabz.basepro.feature.material3"
    compileSdk = 36

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt() // UPDATED

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
            consumerProguardFiles("proguard-rules.pro") // Added this line
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
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:util"))



    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    // androidx-lifecycle-viewmodel-compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.android)

    // Hilt Dependency Injection
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    // kapt(libs.hilt.compiler)

    // Compose
    implementation(platform(libs.androidx.compose.bom))

    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.material3:material3-window-size-class:1.3.2")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.5.0-alpha04")

    /*implementation("androidx.compose.material3.adaptive:adaptive:1.2.0-beta01")
    implementation("androidx.compose.material3.adaptive:adaptive-layout:1.2.0-beta01")
    implementation("androidx.compose.material3.adaptive:adaptive-navigation:1.2.0-beta01")*/

    // implementation(libs.androidx.material3)
    // implementation(libs.androidx.material3.adaptive) // <-- This line is now active
    implementation(libs.androidx.compose.material3.adaptive)
    debugImplementation(libs.androidx.ui.tooling)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.legacy)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}