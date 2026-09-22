package com.demo.themeswitch.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme

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

    // 对齐 KernelSU rememberKernelSUColorScheme：
    // keyColor==0 → 取系统壁纸 Monet 动态主题的 primary 作为种子色，再统一经 materialkolor 生成，
    // 让"壁纸取色"与"自定义种子"走同一套 HCT 调色板（paletteStyle + colorSpec + effectiveFor），
    // 消除 M3 与 KSU 之间的取色差异，并保持与 Miuix 主题取色一致。
    val seed = if (keyColor != 0) {
        Color(keyColor)
    } else {
        (if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)).primary
    }
    val base = rememberDynamicColorScheme(
        seedColor = seed,
        isDark = isDark,
        style = paletteStyle,
        specVersion = colorSpec.effectiveFor(paletteStyle),
    )

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
