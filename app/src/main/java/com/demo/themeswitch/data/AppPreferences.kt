package com.demo.themeswitch.data

data class AppPreferences(
    val themeMode: Int = 0,
    val uiMode: String = SettingsRepository.UI_MODE_MIUIX,
    val language: String = "system",
    // 取色：Monet 跟随系统 / keyColor 自定义种子色（0 表示默认） / PaletteStyle 与 ColorSpec
    val enableMonet: Boolean = true,
    val keyColor: Int = 0,
    val colorStyle: String = "TonalSpot",
    val colorSpec: String = "SPEC_2021",
    // 毛玻璃效果（Android 12+）
    val enableBlur: Boolean = true,
    // KSU 风格悬浮底栏
    val enableFloatingBottomBar: Boolean = true,
    val enableFloatingBottomBarBlur: Boolean = true,
    // KSU 对齐偏好：页面切换动画
    val enableScrollAnimation: Boolean = true,
    // 预测性手势返回（Android 13+）
    val enablePredictiveBack: Boolean = true,
    // 全局界面缩放
    val pageScale: Float = 1.0f,
    // C++ 本地后端地址（POST /api/v1/execute，SSE 流式）
    val serverUrl: String = "http://127.0.0.1:8080",
)
