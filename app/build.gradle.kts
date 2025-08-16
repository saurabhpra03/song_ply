plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.dagger.hilt.android)
}

android {
    namespace = "com.song.ply"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.song.ply"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "DB_NAME", "\"myPly.db\"")
        }
        release {

            buildConfigField("String", "DB_NAME", "\"myPly.db\"")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // Material Icons
    implementation(libs.androidx.compose.material.icons.extended)

    // Hilt
    implementation(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.android.compiler)

    // Hilt Navigation
    implementation(libs.androidx.hilt.navigation.compose)

    // Windows Size Class
    implementation(libs.androidx.compose.material3.window.size)

    // ConstraintLayout
    implementation(libs.androidx.constraintlayout.compose)

    // Media
    implementation(libs.androidx.media.legacy)

    // Media3 Exoplayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)

    // Media3 Session
    implementation(libs.androidx.media3.session)

    // Reordering by drag and drop in a LazyList and LazyGrid
    implementation(libs.org.burnoutcrew.composereorderable)

    // Coil
    implementation(libs.io.coil.compose)

    // Room DB
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Kotlinx Coroutines
    implementation(libs.org.jetbrains.kotlinx.coroutines.android)

    // Gson
    implementation(libs.google.code.gson)

    // Lottie animation
    implementation(libs.airbnb.android.lottie)
}