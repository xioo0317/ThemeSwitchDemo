package com.demo.themeswitch.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme

private val LightFallback = lightColorScheme(
    primary = Color(0xFF3A6AE0),
    secondary = Color(0xFF575E71),
    tertiary = Color(0xFF755165),
)

private val DarkFallback = darkColorScheme(
    primary = Color(0xFFB3C5FF),
    secondary = Color(0xFFBFC6DC),
    tertiary = Color(0xFFE3BACC),
)

/** AMOLED 纯黑：把所有 surface/background/container 替换为黑色。 */
private fun amoled(
    base: androidx.compose.material3.ColorScheme,
): androidx.compose.material3.ColorScheme = base.copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color(0xFF050505),
    surfaceContainer = Color(0xFF0A0A0A),
    surfaceContainerHigh = Color(0xFF121212),
    surfaceContainerHighest = Color(0xFF1B1B1B),
    surfaceBright = Color(0xFF1B1B1B),
    surfaceDim = Color.Black,
)

@Composable
fun MaterialAppTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    isMonet: Boolean = true,
    keyColor: Int = 0,
    paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    colorSpec: ColorSpec.SpecVersion = ColorSpec.SpecVersion.SPEC_2025,
    isAmoled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val base = when {
        keyColor != 0 -> rememberDynamicColorScheme(
            seedColor = Color(keyColor),
            isDark = isDark,
            style = paletteStyle,
            specVersion = colorSpec,
        )
        isMonet && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        else -> if (isDark) DarkFallback else LightFallback
    }

    val colorScheme = if (isDark && isAmoled) amoled(base) else base

    LaunchedEffect(isDark) {
        val window = (context as? Activity)?.window ?: return@LaunchedEffect
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
