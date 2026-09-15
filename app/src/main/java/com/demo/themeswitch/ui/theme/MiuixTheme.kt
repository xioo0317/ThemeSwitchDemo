package com.demo.themeswitch.ui.theme

import android.app.Activity
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
 * Signature kept compatible with [AppTheme] dispatch.
 */
@Composable
fun MiuixAppTheme(
    isDark: Boolean,
    keyColor: Int,
    isMonet: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val resolvedKeyColor: Color? = when {
        keyColor != 0 -> Color(keyColor)
        isMonet ->
            if (isDark) dynamicDarkColorScheme(context).primary
            else dynamicLightColorScheme(context).primary

        else -> null
    }

    val controller = ThemeController(
        if (isDark) ColorSchemeMode.Dark else ColorSchemeMode.Light,
        keyColor = resolvedKeyColor,
        isDark = isDark,
        paletteStyle = ThemePaletteStyle.TonalSpot,
        colorSpec = ThemeColorSpec.Spec2021,
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
