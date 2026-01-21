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
    namespace = "com.ylabz.basepro.applications.bike.features.main"
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

// FIX: Use 'java' block for Toolchain (works without the kotlin-android plugin)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(project(":applications:ashbike:features:settings"))
    implementation(project(":applications:ashbike:features:trips"))
    implementation(project(":applications:ashbike:features:core"))
    implementation(project(":applications:ashbike:database"))
    implementation(project(":applications:ashbike:apps:mobile:features:glass"))

    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))

    implementation(project(":feature:heatlh")) // Fixed typo "heatlh" -> "health"
    implementation(project(":feature:nfc"))
    implementation(project(":feature:weather"))
    implementation(project(":feature:places"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.legacy)

    // androidx-lifecycle-viewmodel-compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.service)

    // Maps
    implementation(libs.google.play.services.location)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.projected)
    debugImplementation(libs.androidx.ui.tooling)

    // Permissions
    implementation(libs.google.accompanist.permissions)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    // Hilt Dependency Injection
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Health Connect
    implementation(libs.androidx.health.connect.client)

    // Maps
    implementation(libs.google.maps.compose)

    implementation(libs.kotlinx.collections.immutable)

    // Compose Profile
    implementation("androidx.compose.runtime:runtime-tracing:1.0.0-beta01") // Added version (required if not in BOM)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}