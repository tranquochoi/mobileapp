import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.example.myaiapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myaiapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation ("org.jsoup:jsoup:1.14.3")
    implementation ("androidx.navigation:navigation-compose:2.4.0-alpha07")
    implementation ("androidx.compose.animation:animation:1.2.0")
    implementation ("io.coil-kt:coil-compose:1.4.0")

    implementation ("androidx.media3:media3-exoplayer:1.3.1") // Cho phát media sử dụng ExoPlayer
    implementation ("androidx.media3:media3-exoplayer-dash:1.3.1") // Hỗ trợ phát DASH với ExoPlayer
    implementation ("androidx.media3:media3-exoplayer-hls:1.3.1") // Hỗ trợ phát HLS với ExoPlayer
    implementation ("androidx.media3:media3-exoplayer-smoothstreaming:1.3.1") // Hỗ trợ phát SmoothStreaming với ExoPlayer
    implementation ("androidx.media3:media3-datasource-cronet:1.3.1") // Tải dữ liệu sử dụng Cronet network stack
    implementation ("androidx.media3:media3-datasource-okhttp:1.3.1") // Tải dữ liệu sử dụng OkHttp network stack
    implementation ("androidx.media3:media3-datasource-rtmp:1.3.1") // Tải dữ liệu sử dụng librtmp
    implementation ("androidx.media3:media3-ui:1.3.1") // Xây dựng giao diện phát media
    implementation ("androidx.media3:media3-ui-leanback:1.3.1") // Xây dựng giao diện phát media cho Android TV sử dụng Jetpack Leanback library
    implementation ("androidx.media3:media3-session:1.3.1") // Tiết lộ và điều khiển phiên media
    implementation ("androidx.media3:media3-extractor:1.3.1") // Trích xuất dữ liệu từ các định dạng media
    implementation ("androidx.media3:media3-cast:1.3.1") // Tích hợp với Cast


    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("androidx.compose.material:material-icons-extended:1.0.5")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("androidx.media:media:1.4.1")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0-alpha01")

    implementation ("io.coil-kt:coil-compose:1.4.0")

    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")

    implementation ("androidx.activity:activity-compose:1.3.0")
    implementation ("androidx.compose.foundation:foundation:1.0.5")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")

    implementation ("androidx.compose.material:material:1.0.5")
    implementation ("androidx.activity:activity-compose:1.3.1")

    implementation ("androidx.cardview:cardview:1.0.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    implementation ("androidx.navigation:navigation-compose:2.4.0-alpha04")


    implementation ("androidx.compose.ui:ui:1.5.4")
    implementation ("androidx.compose.material3:material3:1.1.2")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.5")
    implementation("com.google.firebase:firebase-crashlytics:18.6.0")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.10.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation("com.google.firebase:firebase-analytics")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}