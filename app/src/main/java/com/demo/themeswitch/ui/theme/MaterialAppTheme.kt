package com.demo.themeswitch.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme

private val M3LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF004BA0),
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFF7D5260),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
)

private val M3DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A4458),
    tertiary = Color(0xFFEFB8C8),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
)

@Composable
fun MaterialAppTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    isMonet: Boolean = true,
    keyColor: Int = 0,
    colorStyle: String = "TonalSpot",
    colorSpec: String = "SPEC_2021",
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    // 取色策略：自定义强调色（material-kolor 生成色板） > 系统动态取色（Monet） > 静态配色
    val colorScheme = when {
        keyColor != 0 -> {
            val style = try {
                PaletteStyle.valueOf(colorStyle)
            } catch (_: Exception) {
                PaletteStyle.TonalSpot
            }
            val spec = if (colorSpec == "SPEC_2025") {
                ColorSpec.SpecVersion.SPEC_2025
            } else {
                ColorSpec.SpecVersion.SPEC_2021
            }
            rememberDynamicColorScheme(
                seedColor = Color(keyColor),
                isDark = isDark,
                style = style,
                specVersion = spec,
            )
        }
        isMonet && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        else -> if (isDark) M3DarkColorScheme else M3LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
