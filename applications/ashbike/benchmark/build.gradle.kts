plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.baselineprofile) // Make sure this alias is in your TOML's [plugins] section
}

android {
    namespace = "com.ylabz.basepro.applications.bike.benchmark" // As you specified
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = 32 // Or your preferred minSdk for benchmarks (API 28+ for generation)
        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    }

    buildTypes {
        create("benchmark") {
            isDebuggable = false
            signingConfig = getByName("debug").signingConfig
            matchingFallbacks += listOf("release")
            enableAndroidTestCoverage = false
        }
    }

    targetProjectPath = ":applications:ashbike"
}

dependencies {
    implementation(libs.androidx.junit) // Assuming this points to androidx.test.ext:junit or :junit-ktx
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.runner)
    // implementation(libs.androidx.espresso.core) // Optional, if needed
}

androidComponents {
    beforeVariants(selector().all()) {
        it.enable = it.buildType == "benchmark"
    }
}
