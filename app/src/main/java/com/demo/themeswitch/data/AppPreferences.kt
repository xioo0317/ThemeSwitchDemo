package com.demo.themeswitch.data

data class AppPreferences(
    // KSU ColorMode: 0 跟随系统 / 1 浅色 / 2 深色 / 3 Monet跟随 / 4 Monet浅 / 5 Monet深 / 6 AMOLED深色
    val colorMode: Int = 0,
    // 默认 UI：miui 开头（MiuiX）
    val uiMode: String = "miuix",
    val language: String = "system",
    // 默认 keyColor=0 → 走壁纸 Monet 取色（与 KernelSU 默认一致）；非 0 才用固定种子色
    val keyColor: Int = 0,
    val colorStyle: String = "TonalSpot",
    val colorSpec: String = "SPEC_2025",
    // Miuix 是否启用 Monet 动态取色（独立于 keyColor）
    val miuixMonet: Boolean = false,
    val enableBlur: Boolean = false,
    val enableFloatingBottomBar: Boolean = false,
    val enableFloatingBottomBarBlur: Boolean = false,
    val enableScrollAnimation: Boolean = false,
    val enablePredictiveBack: Boolean = true,
    val enableSwipeDismiss: Boolean = true,
    val pageScale: Float = 1.0f,
    val serverUrl: String = "http://127.0.0.1:8080",
)
