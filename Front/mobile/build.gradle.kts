import java.util.Properties
import java.io.FileInputStream
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    // response 데이터
    id("kotlin-parcelize")
}

android {
    namespace = "com.eeos.rocatrun"
    compileSdk = 34

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    defaultConfig {
        applicationId = "com.eeos.rocatrun"
        minSdk = 31
        targetSdk = 34
        versionCode = 340070102
        versionName = "0.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
//        getByName("debug") { isMinifyEnabled = false }
//        getByName("release") {
//            isMinifyEnabled = true
//            isShrinkResources = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//            signingConfig = signingConfigs.getByName("release")
//            ndk {
//                debugSymbolLevel = "FULL"
//            }
//        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
        compose = true
    }
}

dependencies {
    // 카카오 SDK
    implementation(libs.kakao.sdk)

    // google 로그인 관련
    implementation(libs.firebase.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.play.services.auth)
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

//    // 네이버 SDK
//    implementation("com.navercorp.nid:oauth:5.10.0")
//    implementation("com.navercorp.nid:oauth-jdk8:5.10.0")

    //Mapbox
    implementation("com.mapbox.maps:android:11.9.0")

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)


    // Data Layer API
    implementation("com.google.android.gms:play-services-wearable:19.0.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.wearable)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.ui.tooling.preview.android)

    // Jetpack Compose 추가
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // coil 추가
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-gif:3.0.4")
    implementation(libs.androidx.runtime.livedata)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // 통계 차트
    implementation ("io.github.ehsannarmani:compose-charts:0.1.1")

    // composable에서 viewModel 이용
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.3.0")
    implementation(libs.ui.graphics.android)


    // socket.io -
    implementation ("io.socket:socket.io-client:2.0.0")

    // composable에서 viewModel 이용
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.3.0")
    implementation(libs.ui.graphics.android)


    // socket.io -
    implementation ("io.socket:socket.io-client:2.0.0")

    // 캡쳐 라이브러리
    implementation ("dev.shreyaspatil:capturable:2.1.0")
}