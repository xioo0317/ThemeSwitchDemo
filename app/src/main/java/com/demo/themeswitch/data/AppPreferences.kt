package com.demo.themeswitch.data

data class AppPreferences(
    val themeMode: Int = 0,
    val uiMode: String = SettingsRepository.UI_MODE_MATERIAL,
    val language: String = "system",
    // 取色：Monet 跟随系统 / keyColor 自定义种子色（0 表示默认） / PaletteStyle 与 ColorSpec
    val enableMonet: Boolean = true,
    val keyColor: Int = 0,
    val colorStyle: String = "TonalSpot",
    val colorSpec: String = "SPEC_2021",
    // 毛玻璃效果（Android 12+）
    val enableBlur: Boolean = true,
    // KSU 风格悬浮底栏
    val enableFloatingBottomBar: Boolean = false,
    val enableFloatingBottomBarBlur: Boolean = false,
    // 全局界面缩放
    val pageScale: Float = 1.0f,
)
