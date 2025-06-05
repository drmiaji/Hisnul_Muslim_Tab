plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.drmiaji.hisnulmuslimtab"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.drmiaji.hisnulmuslimtab"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Optional: Configure KSP arguments if needed
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }
}

// This configuration is optional - KSP handles source directories automatically
// Only keep if you need custom source directory configuration
kotlin {
    sourceSets.configureEach {
        kotlin.srcDir("build/generated/ksp/${name}/kotlin")
    }
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.stdlib)

    // AndroidX Core and AppCompat
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // RecyclerView
    implementation(libs.androidx.recyclerview)

    // Lifecycle: ViewModel, LiveData, Runtime
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Navigation (Compose)
    implementation(libs.androidx.navigation.compose.jvmstubs)

    // Material Design
    implementation(libs.material3)
    implementation(libs.material)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    // Preferences
    implementation(libs.androidx.preference.ktx)

    // Async Layout Inflater
    implementation(libs.androidx.asynclayoutinflater)

    // Firebase Crashlytics
    implementation(libs.firebase.crashlytics.buildtools)

    // JSON Parsing
    implementation(libs.gson)

    // Room Database - ✅ KSP is correctly used here
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // ✅ This is correct for KSP

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.app.update)
}