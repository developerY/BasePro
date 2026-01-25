import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android) // ✅ KEEP THIS (Ignore the warning)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)    // ✅ UNCOMMENTED (Required for Hilt)
    alias(libs.plugins.ksp)
}

// FIX: Use strict configuration to avoid deprecation warnings
extensions.configure<LibraryExtension> {
    namespace = "com.zoewave.basepro.applications.rxdigita.features.settings"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

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
}

// FIX: Use 'java' block for Toolchain (works better with built-in Kotlin)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.legacy)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    // Note: This dependency looks like it belongs to a different app ("ashbike").
    // If this is a copy-paste error, you might want to remove it.
    // If you are sharing code between apps, keep it.
    implementation(project(":applications:ashbike:features:main"))

    implementation(project(":core:ui"))
    ksp(libs.hilt.android.compiler)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}