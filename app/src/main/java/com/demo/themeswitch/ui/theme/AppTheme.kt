package com.demo.themeswitch.ui.theme

import android.os.Build
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

/**
 * KSU 同款：双 UI 单选一分流。Miuix 模式只套 MiuixAppTheme，
 * Material 模式只套 MaterialAppTheme，不再嵌套共存。
 */
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

    // 毛玻璃依赖 Android 13 的 RenderEffect，低版本全局视为不支持
    val blurSupported = appPreferences.enableBlur && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    // KSU 同款：液态玻璃（悬浮底栏 lens 折射）独立于毛玻璃总开关，只要 SDK 支持就生效
    val glassSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    CompositionLocalProvider(
        LocalColorMode provides colorMode.value,
        LocalEnableBlur provides blurSupported,
        LocalEnableFloatingBottomBar provides appPreferences.enableFloatingBottomBar,
        LocalEnableFloatingBottomBarBlur provides (glassSupported && appPreferences.enableFloatingBottomBarBlur),
        LocalScrollAnimation provides appPreferences.enableScrollAnimation,
    ) {
        when (uiMode) {
            UiMode.Miuix -> MiuixAppTheme(
                isDark = isDark,
                isMonet = appPreferences.enableMonet,
                keyColor = appPreferences.keyColor,
                colorStyle = appPreferences.colorStyle,
                colorSpec = appPreferences.colorSpec,
            ) { content() }

            UiMode.Material -> MaterialAppTheme(
                isDark = isDark,
                isMonet = appPreferences.enableMonet,
                keyColor = appPreferences.keyColor,
                colorStyle = appPreferences.colorStyle,
                colorSpec = appPreferences.colorSpec,
            ) { content() }
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
val LocalEnableBlur = staticCompositionLocalOf { false }
val LocalEnableFloatingBottomBar = staticCompositionLocalOf { false }
val LocalEnableFloatingBottomBarBlur = staticCompositionLocalOf { false }
val LocalScrollAnimation = staticCompositionLocalOf { true }
