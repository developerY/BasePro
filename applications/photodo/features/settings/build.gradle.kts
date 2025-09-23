plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Added Kotlin Compose Plugin
    alias(libs.plugins.ksp)
    alias(libs.plugins.mapsplatform.secrets)
}

android {
    namespace = "com.ylabz.basepro.applications.photodo.features.settings"
    compileSdk = 36

    defaultConfig {
        minSdk =  libs.versions.minSdk.get().toInt() // UPDATED

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
        sourceCompatibility = JavaVersion.VERSION_21 // Aligned with feature:nav3
        targetCompatibility = JavaVersion.VERSION_21 // Aligned with feature:nav3
    }
    kotlinOptions {
        jvmTarget = "21" // Aligned with feature:nav3 (will use jvmToolchain if this causes issues)
    }
}

dependencies {
    implementation(project("::core:ui"))
    implementation(project("::core:model"))
    implementation(project("::core:util"))

    implementation(project(":feature:qrscanner"))
    implementation(project(":applications:photodo:db"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.legacy)


    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    // implementation(project(":applications:ashbike:features:main")) // Commented out, seems unrelated to PhotoDo settings
    implementation(project(":core:ui"))
    ksp(libs.hilt.android.compiler)   // Hilt compiler dependency for annotation processing
    // Hilt Dependency Injection
    // kapt(libs.hilt.compiler)

    // Navigation
    implementation(libs.androidx.navigation3.runtime) // Added Nav3 Runtime
    // libs.androidx.navigation.compose might be legacy if Nav3 is used for invocation
    implementation(libs.androidx.navigation.compose) 
    implementation(libs.hilt.navigation.compose)

    // Icons
    implementation(libs.androidx.material.icons.extended)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}