plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ylabz.basepro.applications.bike.features.main"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = 31

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
    implementation(project(":applications:ashbike:features:settings"))
    implementation(project(":applications:ashbike:features:trips"))
    implementation(project(":applications:ashbike:database"))

    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))


    implementation(project(":feature:heatlh"))
    implementation(project(":feature:nfc"))
    implementation(project(":feature:weather"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // androidx-lifecycle-viewmodel-compose
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.android)

    //lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.service)

    //Maps
    implementation(libs.play.services.location)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    // Permissions
    implementation(libs.google.accompanist.permissions)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    // Hilt Dependency Injection
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    // kapt(libs.hilt.compiler)

    // Health Connect
    implementation(libs.androidx.health.connect.client)

    // maps
    implementation(libs.google.maps.compose)

    implementation(libs.kotlinx.collections.immutable)

    // Compose Profile
    implementation("androidx.compose.runtime:runtime-tracing")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}