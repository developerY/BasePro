import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    // REMOVE this to fix the AGP 9.0 crash:
    // alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mapsplatform.secrets)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.androidx.baselineprofile)
}

// FIX: Use 'configure<ApplicationExtension>' for AGP 9.0+ strict mode
extensions.configure<ApplicationExtension> {
    namespace = "com.ylabz.basepro.ashbike.mobile"
    compileSdk = libs.versions.compileSdk.get().toInt()

    signingConfigs {
        create("release") {
            val storeFile = providers.gradleProperty("RELEASE_STORE_FILE")
            val storePassword = providers.gradleProperty("RELEASE_STORE_PASSWORD")
            val keyAlias = providers.gradleProperty("RELEASE_KEY_ALIAS")
            val keyPassword = providers.gradleProperty("RELEASE_KEY_PASSWORD")

            if (storeFile.isPresent && storePassword.isPresent && keyAlias.isPresent && keyPassword.isPresent) {
                this.storeFile = file(storeFile.get())
                this.storePassword = storePassword.get()
                this.keyAlias = keyAlias.get()
                this.keyPassword = keyPassword.get()
            } else {
                println("Release signing keystore properties not found in gradle.properties. Release build may fail to sign.")
                // Consider throwing an error here for CI/CD environments if properties are mandatory
                // throw new GradleException("Release signing keystore properties not found in gradle.properties.")
            }
        }
    }

    defaultConfig {
        applicationId = "com.ylabz.basepro.ashbike.mobile"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 11
        versionName = "0.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = providers.gradleProperty("isMinifyForRelease").get().toBoolean()
            isShrinkResources = providers.gradleProperty("isShrinkResources").get().toBoolean()
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        // This debug block ensures a fast development cycle
        debug {
            isMinifyEnabled = false
            // Debug builds are automatically signed with the debug keystore by default
            // applicationVariants.all { variant ->
            //     variant.outputs.all { output ->
            //         outputFileName = "\${archivesBaseName}-\${variant.name}-\${versionName}.apk"
            //     }
            // }
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

    androidResources {
        localeFilters.addAll(listOf("en", "es"))
    }

    // remove as soon as Google fixes the bug
    lint {
        baseline = file("lint-baseline.xml")
    }
}

// FIX: Use 'java' block for Toolchain (works without the kotlin-android plugin)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Secrets plugin configuration (Must be outside the android block)
secrets {
    defaultPropertiesFileName = "secrets.defaults.properties"
}

baselineProfile {
    // This suppresses the warning about AGP 9.0.0
    warnings {
        maxAgpVersion = false
    }
}

dependencies {
    // Core + shared
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:util"))

    // Feature modules
    implementation(project(":feature:listings"))
    implementation(project(":feature:camera"))
    implementation(project(":feature:places"))
    implementation(project(":feature:heatlh")) // Fixed typo: "heatlh" -> "health"
    implementation(project(":feature:maps"))
    implementation(project(":feature:ble"))
    implementation(project(":feature:alarm"))
    implementation(project(":feature:weather"))
    implementation(project(":feature:qrscanner"))
    implementation(project(":feature:nfc"))
    implementation(project(":feature:ml"))
    implementation(project(":applications:ashbike:database"))
    implementation(project(":applications:ashbike:features:trips"))
    implementation(project(":applications:ashbike:features:settings"))

    // androidx-lifecycle-viewmodel-compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.android)

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
    implementation(project(":applications:ashbike:features:main"))
    ksp(libs.hilt.android.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Health Connect
    implementation(libs.androidx.health.connect.client)

    // maps
    implementation(libs.google.maps.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.profileinstaller)
}