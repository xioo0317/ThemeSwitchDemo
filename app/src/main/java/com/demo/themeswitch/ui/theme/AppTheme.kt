package com.demo.themeswitch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode

enum class ColorMode(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromValue(value: Int) = entries.find { it.value == value } ?: SYSTEM
    }

    val isSystem: Boolean get() = value == 0
    val isDark: Boolean get() = value == 2
}

@Composable
fun AppTheme(
    appPreferences: AppPreferences,
    uiMode: UiMode = LocalUiMode.current,
    content: @Composable () -> Unit,
) {
    val colorMode = ColorMode.fromValue(appPreferences.themeMode)
    val isDark = when {
        colorMode.isDark -> true
        colorMode.isSystem -> isSystemInDarkTheme()
        else -> false
    }

    CompositionLocalProvider(
        LocalColorMode provides colorMode.value,
        LocalKeyColor provides appPreferences.keyColor,
    ) {
        // 双主题嵌套：外层 miuix 主题让 miuix 组件（OverlayDialog 弹窗/MIUI 顶栏/底栏）
        // 在任何 UI 模式下都可用；内层 M3 主题继续供 Material 页面使用。
        // 动态取色只在 MIUI 模式下作用于 miuix 主题，M3 保持自己的 Material You 逻辑
        MiuixAppTheme(
            isDark = isDark,
            keyColor = appPreferences.keyColor,
            isMonet = appPreferences.isMiuixMonet && uiMode == UiMode.Miuix,
        ) {
            MaterialAppTheme(isDark = isDark, keyColor = appPreferences.keyColor) {
                content()
            }
        }
    }
}

@Composable
fun isInDarkTheme(): Boolean {
    val colorMode = LocalColorMode.current
    return when (colorMode) {
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }
}

val LocalColorMode = staticCompositionLocalOf { 0 }
val LocalKeyColor = staticCompositionLocalOf { 0xFF1976D2.toInt() }
