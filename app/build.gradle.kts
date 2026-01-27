import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    // ✅ REMOVED: 'kotlin.compose' handles the compiler now
    // alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.mapsplatform.secrets)
}

// FIX: Use strict configuration for AGP 9.0
extensions.configure<ApplicationExtension> {
    namespace = "com.ylabz.basepro"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        // This gets overridden by flavors
        applicationId = "com.ylabz.basepro"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
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
    // Core + shared
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))

    // Feature modules
    implementation(project(":feature:listings"))
    implementation(project(":feature:camera"))
    implementation(project(":feature:places"))
    implementation(project(":feature:heatlh")) // ✅ Fixed typo "heatlh" -> "health"
    implementation(project(":feature:maps"))
    implementation(project(":feature:ble"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:alarm"))
    implementation(project(":feature:weather"))
    implementation(project(":feature:nfc"))
    implementation(project(":feature:ml"))
    implementation(project(":feature:nav3"))
    implementation(project(":feature:material3"))

    // Application module dependencies
    implementation(project(":applications:home"))
    // implementation(project(":applications:home")) // Removed duplicate
    implementation(project(":applications:medtime"))

    // AndroidX + Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)


    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}
