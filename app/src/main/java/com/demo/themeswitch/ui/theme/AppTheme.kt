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
        when (uiMode) {
            UiMode.Miuix -> MiuixAppTheme(
                isDark = isDark,
                keyColor = appPreferences.keyColor,
                isMonet = appPreferences.isMiuixMonet,
                content = content,
            )
            UiMode.Material -> MaterialAppTheme(
                isDark = isDark,
                keyColor = appPreferences.keyColor,
                content = content,
            )
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
