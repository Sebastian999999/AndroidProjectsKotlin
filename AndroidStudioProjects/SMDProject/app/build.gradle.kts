plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googleGmsGoogleServices)
}

android {
    namespace = "com.hammadirfan.smdproject"
    compileSdk = 34
    packaging {
        resources.excludes.add ("META-INF/DEPENDENCIES")
    }
    defaultConfig {
        applicationId = "com.hammadirfan.smdproject"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
apply(plugin = "com.google.gms.google-services") // Add this line
}
buildscript {
    dependencies {
        // Add this line
        classpath ("com.google.gms:google-services:4.3.10")  // Check for the latest version
    }
}
configurations {
    all {
        exclude(group = "com.google.protobuf", module = "protobuf-java") // Globally exclude protobuf-java
    }
}
//apply ("com.android.application")
//apply ("com.google.gms.google-services") // Add this line

dependencies {
    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx") {
        exclude(group = "com.google.protobuf", module = "protobuf-java") // Ensure exclusion here too for clarity
    }
    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx") {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }


    // Explicitly use protobuf-javalite
    implementation("com.google.protobuf:protobuf-javalite:3.22.3")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("mysql:mysql-connector-java:8.0.23")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.google.firebase:firebase-messaging:23.0.0")
    implementation ("com.google.firebase:firebase-messaging")
    implementation ("com.google.firebase:firebase-messaging:22.0.0")
    implementation ("com.google.auth:google-auth-library-oauth2-http:0.20.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.firebaseui:firebase-ui-storage:7.2.0")
    implementation ("com.firebaseui:firebase-ui-database:7.2.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.cardview)
    implementation(libs.car.ui.lib)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.play.services.analytics.impl)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

apply(plugin ="com.android.application")
apply( plugin="com.google.gms.google-services")  // Add this line at the bottom



