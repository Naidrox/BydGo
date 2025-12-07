plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.hacknation_bydgo"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.hacknation_bydgo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("org.mapsforge:mapsforge-map-android:0.20.0")
    implementation("org.mapsforge:mapsforge-map:0.20.0")
    implementation("org.mapsforge:mapsforge-core:0.20.0")
    implementation("org.mapsforge:mapsforge-map-reader:0.20.0")
    implementation("org.mapsforge:mapsforge-themes:0.20.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("org.pytorch:pytorch_android:2.1.0")
    implementation("org.pytorch:pytorch_android_torchvision:2.1.0")
}