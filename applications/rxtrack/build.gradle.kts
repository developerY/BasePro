import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mapsplatform.secrets)
}

// FIX: Use strict configuration for AGP 9.0
extensions.configure<ApplicationExtension> {
    namespace = "com.rxdigita.basepro.applications.rxtrack"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.rxdigita.basepro.applications.rxtrack"
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

// FIX: Use 'java' block for Toolchain (works better with strict AGP 9)
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
    implementation(project(":applications:rxtrack:features:main"))
    implementation(project(":applications:rxtrack:features:medlist"))
    implementation(project(":applications:rxtrack:features:settings"))

    // ⚠️ CHECK THIS: This looks like a copy-paste error from your other app.
    // If RxTrack shouldn't depend on AshBike, remove this line:
    implementation(project(":applications:ashbike:features:main"))

    // AndroidX Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.android)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Removed duplicate preview (ui.tooling includes preview logic)
}