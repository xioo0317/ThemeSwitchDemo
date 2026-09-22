package com.demo.themeswitch.data

data class AppPreferences(
    // KSU ColorMode: 0 跟随系统 / 1 浅色 / 2 深色 / 3 Monet跟随 / 4 Monet浅 / 5 Monet深 / 6 AMOLED深色
    val colorMode: Int = 0,
    // 默认 UI：miui 开头（MiuiX）
    val uiMode: String = "miuix",
    val language: string = "system",
    // KSU 品牌绿（Teal #009688）作为默认种子色；0 才走壁纸取色
    val keyColor: Int = 0xFF009688.toInt(),
    val colorStyle: String = "TonalSpot",
    val colorSpec: String = "SPEC_2025",
    val enableBlur: Boolean = false,
    val enableFloatingBottomBar: Boolean = false,
    val enableFloatingBottomBarBlur: Boolean = false,
    val enableScrollAnimation: Boolean = false,
    val enablePredictiveBack: Boolean = true,
    val enableSwipeDismiss: Boolean = true,
    val pageScale: Float = 1.0f,
    val serverUrl: String = "http://127.0.0.1:8080",
)
