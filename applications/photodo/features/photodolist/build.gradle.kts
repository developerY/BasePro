import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Added Kotlin Compose Plugin
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
}

extensions.configure<LibraryExtension> {
    namespace = "com.ylabz.vbasepro.applications.photodo.photodolist"
    compileSdk = libs.versions.compileSdk.get().toInt()


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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

// 2. Configure Kotlin (Separately)
// Use specific task configuration which is the safest way in 2024+
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":applications:photodo:db"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material.legacy)

    // Permissions
    implementation(libs.google.accompanist.permissions)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    // --- ADD THIS LINE ---
    implementation(libs.androidx.lifecycle.runtime.compose)
    debugImplementation(libs.androidx.ui.tooling)

    // Add Camera
    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.compose)

    // coil
    implementation(libs.coil.compose)

    // Hilt
    implementation(libs.hilt.android)
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