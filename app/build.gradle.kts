@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.demo.themeswitch"

    compileSdk {
        version = release(37) {
            minorApiLevel = 0
        }
    }

    defaultConfig {
        applicationId = "com.demo.themeswitch"
        minSdk = 26
        targetSdk = 37
        versionCode = 5
        versionName = "1.3.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    androidResources {
        generateLocaleConfig = true
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    // 与 navigationevent 1.1.2 对齐（miuix 弹层 Back 处理依赖 navigationevent）
    implementation("androidx.activity:activity-compose:1.13.0")

    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    // M3 Expressive（SegmentedListItem/ShortNavigationBar 等）：对齐 KSU 显式版本
    implementation("androidx.compose.material3:material3:1.5.0-alpha28")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // miuix 0.9.3 的弹层（PopupEntry）依赖 navigationevent-compose 做 Back 处理，
    // 缺失会导致点击弹窗类控件直接闪退
    implementation("androidx.navigationevent:navigationevent-compose:1.1.2")

    // Miuix: MIUI style Compose Multiplatform UI library
    implementation("top.yukonga.miuix.kmp:miuix-ui-android:0.9.4")
    implementation("top.yukonga.miuix.kmp:miuix-preference-android:0.9.4")
    // Miuix blur: 悬浮底栏液态玻璃（依赖 Android 12 RenderEffect，低版本运行时自动降级）
    implementation("top.yukonga.miuix.kmp:miuix-blur-android:0.9.4")
    // Material Kolor: 从种子色生成 Material 3 动态色板（强调色/色彩风格/色彩规格）
    implementation("com.materialkolor:material-kolor:5.0.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
