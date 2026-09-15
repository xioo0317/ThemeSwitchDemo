package com.demo.themeswitch.data

data class AppPreferences(
    val themeMode: Int = 0,
    val uiMode: String = SettingsRepository.UI_MODE_MATERIAL,
    val language: String = "system",
    val keyColor: Int = 0,
    val isMiuixMonet: Boolean = false,
)
