package com.demo.themeswitch.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeColorSpec
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.ThemePaletteStyle

/**
 * MIUI style theme powered by the real Miuix library.
 * 取色策略与 KSU 对齐：自定义强调色 > 系统动态取色（Monet） > miuix 默认。
 */
@Composable
fun MiuixAppTheme(
    isDark: Boolean,
    isMonet: Boolean = true,
    keyColor: Int = 0,
    colorStyle: String = "TonalSpot",
    colorSpec: String = "SPEC_2021",
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val paletteStyle = try {
        ThemePaletteStyle.valueOf(colorStyle)
    } catch (_: Exception) {
        ThemePaletteStyle.TonalSpot
    }
    val spec = if (colorSpec == "SPEC_2025") ThemeColorSpec.Spec2025 else ThemeColorSpec.Spec2021

    val resolvedKeyColor: Color? = when {
        keyColor != 0 -> Color(keyColor)
        isMonet && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (isDark) dynamicDarkColorScheme(context).primary else dynamicLightColorScheme(context).primary
        else -> null
    }

    // miuix 的 Dark/Light 是静态色板（keyColor/style/spec 均不生效）；
    // 只有 Monet* 模式才会用 keyColor 走 materialkolor 动态生成（KSU 同款行为）
    val useMonet = resolvedKeyColor != null
    val controller = ThemeController(
        if (useMonet) {
            if (isDark) ColorSchemeMode.MonetDark else ColorSchemeMode.MonetLight
        } else {
            if (isDark) ColorSchemeMode.Dark else ColorSchemeMode.Light
        },
        keyColor = resolvedKeyColor,
        isDark = isDark,
        paletteStyle = paletteStyle,
        colorSpec = spec,
    )

    MiuixTheme(
        controller = controller,
        content = {
            LaunchedEffect(isDark) {
                val window = (context as? Activity)?.window ?: return@LaunchedEffect
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }
            content()
        },
    )
}
