package com.demo.themeswitch.ui.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode

/**
 * KSU 同款：双 UI 单选一分流。Miuix 模式只套 MiuixAppTheme，
 * Material 模式只套 MaterialAppTheme。ColorMode 统一编码深浅色/Monet/AMOLED。
 */
@Composable
fun AppTheme(
    appPreferences: AppPreferences,
    uiMode: UiMode = LocalUiMode.current,
    content: @Composable () -> Unit,
) {
    val settings = ThemeController.from(appPreferences)
    val colorMode = settings.colorMode
    val isDark = colorMode.isDark || (colorMode.isSystem && isSystemDark())

    // 毛玻璃依赖 Android 13 的 RenderEffect，低版本视为不支持
    val blurSupported = appPreferences.enableBlur &&
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val glassSupported =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    CompositionLocalProvider(
        LocalColorMode provides colorMode.value,
        LocalEnableBlur provides blurSupported,
        LocalEnableFloatingBottomBar provides appPreferences.enableFloatingBottomBar,
        LocalEnableFloatingBottomBarBlur provides (glassSupported && appPreferences.enableFloatingBottomBarBlur),
    ) {
        when (uiMode) {
            UiMode.Miuix -> MiuixAppTheme(
                isDark = isDark,
                isMonet = colorMode.isMonet,
                keyColor = settings.keyColor,
                paletteStyle = settings.paletteStyle,
                colorSpec = settings.colorSpec,
                content = content,
            )

            UiMode.Material -> MaterialAppTheme(
                isDark = isDark,
                isMonet = colorMode.isMonet,
                keyColor = settings.keyColor,
                paletteStyle = settings.paletteStyle,
                colorSpec = settings.colorSpec.effectiveFor(settings.paletteStyle),
                isAmoled = colorMode.isAmoled,
                content = content,
            )
        }
    }
}

@Composable
private fun isSystemDark() = androidx.compose.foundation.isSystemInDarkTheme()
