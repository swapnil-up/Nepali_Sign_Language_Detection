plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.nsl_mini"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.nsl_mini"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Enable view binding
    viewBinding {
        enable = true
    }
    buildFeatures {
        dataBinding =true
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
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    // Firebase Realtime Database library
    implementation("com.google.firebase:firebase-database-ktx")

    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.mediapipe:tasks-vision:latest.release")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // navigation drawer
    implementation("com.google.android.material:material:1.5.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")
    implementation("androidx.appcompat:appcompat:1.4.1")

    implementation("androidx.core:core:1.6.0")

    implementation ("androidx.activity:activity-ktx:1.5.1")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.google.firebase:firebase-storage:20.0.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")

    // For loading images
    implementation ("com.google.android.material:material:1.5.0")
    implementation ("com.google.firebase:firebase-database:20.0.5")
    implementation ("com.google.firebase:firebase-storage:20.0.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("com.google.firebase:firebase-database:20.0.4")
    implementation ("com.google.firebase:firebase-storage:20.0.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

}
