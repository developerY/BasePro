plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.apollo.graphql)
    alias(libs.plugins.mapsplatform.secrets)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ylabz.basepro.core.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        aidl = false
        buildConfig = true
        renderScript = false
        shaders = false
    }

    apollo {
        // Define a named service to avoid using the default
        service("service") {
            // Set the package name for the generated classes
            packageName.set("com.ylabz.basepro.core.network")
        }
    }

    secrets {
        defaultPropertiesFileName = "secrets.defaults.properties"
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:util"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Datastore
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // GraphQL
    implementation(libs.okhttp)
    implementation(libs.apollo.graphql)
    implementation(libs.apollo.graphql.cache)

    // Health Connect
    implementation(libs.healthConnect)


    // kapt(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}