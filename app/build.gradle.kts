plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
}

android {
    // ✅ Namespace must match package of MainActivity
    namespace = "com.supplylens.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.supplylens.app"
        buildConfigField("String", "BASE_URL", "\"https://your-api-url.com/\"")
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // For testing purposes
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // ✅ Local backend for development (Android emulator)
            // IP 10.0.2.2 is how Android emulator accesses host machine's localhost
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8000/\"")
            isMinifyEnabled = false
            var debuggable = true
        }
        release {
            // ✅ Production backend URL - UPDATE THIS
            buildConfigField("String", "BASE_URL", "\"https://api.supplylens.com/\"")
            isMinifyEnabled = false
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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ── Core Android ──
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // ── Compose UI ──
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // ── Navigation ──
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ── Networking (Retrofit + OkHttp) ──
    implementation("com.squareup.retrofit2:retrofit:2.10.0")
    implementation("com.squareup.retrofit2:converter-gson:2.10.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // ── Coroutines ──
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // ── Hilt Dependency Injection ──
    implementation("com.google.dagger:hilt-android:2.56.2")
    kapt("com.google.dagger:hilt-compiler:2.56.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // ── Image Loading ──
    implementation("io.coil-kt:coil-compose:2.5.0")

    // ── Testing ──
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // ── Debug ──
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation("androidx.compose.ui:ui-test-manifest")


    // Google Maps (if you go Option B)
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Lottie for AI cursor animation
    implementation("com.airbnb.android:lottie-compose:6.4.0")

    // Accompanist for permissions/webview
    implementation("com.google.accompanist:accompanist-webview:0.34.0")

}

kapt {
    correctErrorTypes = true
}