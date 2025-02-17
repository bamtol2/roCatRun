import java.util.Properties
import java.io.FileInputStream
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
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
        versionCode = 340090101
        versionName = "0.0.9"

    }

    buildFeatures {
        buildConfig = true // BuildConfig 생성 활성화
    }

    buildTypes {
//        getByName("debug") { isMinifyEnabled = false }
//        getByName("release") {
//            isDebuggable = true
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"  // Compose와 Accompanist 호환성에 맞는 최신 버전
    }
}

dependencies {
    // 위치
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // 추가
    implementation("androidx.wear:wear:1.3.0")
    implementation("androidx.appcompat:appcompat:1.7.0-alpha03")
    implementation("androidx.appcompat:appcompat-resources:1.7.0-alpha03")
    implementation("androidx.compose.material3:material3:1.2.1")
    // Compose 공식 Pager 라이브러리 추가
    implementation("androidx.compose.foundation:foundation:1.6.2")
    implementation("androidx.compose.foundation:foundation-layout:1.6.2")
    // Data Layer API
    implementation("com.google.android.gms:play-services-wearable:19.0.0")
    // GPX 파일 관련 의존성
    implementation("androidx.wear:wear-ongoing:1.0.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.36.0")
    implementation(libs.androidx.lifecycle.livedata.core)
    implementation(libs.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.runtime.livedata)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}