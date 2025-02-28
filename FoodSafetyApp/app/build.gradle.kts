plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    id ("kotlinx-serialization")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.foodsafetyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.foodsafetyapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
//    implementation ("com.google.firebase:firebase-bom:32.7.0")
//
//    // Firebase Authentication
//    implementation ("com.google.firebase:firebase-auth-ktx")
//
//    // Firebase Firestore (for user data)
//    implementation ("com.google.firebase:firebase-firestore-ktx")
    //implementation(libs.androidx.camera.view)
    implementation(libs.vision.common)
    implementation ("androidx.camera:camera-core:1.4.1")
//    //implementation (libs.androidx.camera.camera2.v130)
//    //implementation (libs.androidx.camera.lifecycle.v130)
    implementation ("androidx.camera:camera-view:1.4.1")
    implementation ("com.google.mlkit:image-labeling:17.0.9")
    implementation ("org.tensorflow:tensorflow-lite-task-vision:0.4.0")
    implementation ("com.google.mlkit:object-detection:17.0.0")
    implementation ("com.google.mlkit:object-detection-custom:17.0.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation(libs.androidx.camera.lifecycle)
//    implementation(libs.image.labeling.default.common)
   implementation (libs.androidx.camera.camera2)
    implementation(libs.androidx.room.ktx)
    implementation(project(":opencv"))
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    //implementation ("com.google.firebase:firebase-bom:33.9.0")

    // Declare the dependencies for the desired Firebase products without specifying versions
    // For example, declare the dependencies for Firebase Authentication and Cloud Firestore
//    implementation ("com.google.firebase:firebase-auth")
//    implementation ("com.google.firebase:firebase-firestore")
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))

    // Now add Firebase Firestore without an explicit version:
    implementation("com.google.firebase:firebase-firestore")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    // Add other Firebase libraries the same way:
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation(libs.androidx.work.runtime.ktx)
//    implementation (libs.camera.lifecycle.v110)
//    implementation (libs.androidx.camera.view.v100alpha31) // or the latest version if available
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}