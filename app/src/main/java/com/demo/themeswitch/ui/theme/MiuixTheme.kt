package com.demo.themeswitch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MiuixLightColorScheme = lightColorScheme(
    primary = Color(0xFFFF6A00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFFE65100),
    secondary = Color(0xFFFF8F00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFECB3),
    tertiary = Color(0xFFFFB300),
    surface = Color(0xFFF5F5F5),
    onSurface = Color(0xFF212121),
    background = Color(0xFFF0F0F0),
    onBackground = Color(0xFF212121),
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF424242),
)

private val MiuixDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFAB40),
    onPrimary = Color(0xFF3E2723),
    primaryContainer = Color(0xFFE65100),
    onPrimaryContainer = Color(0xFFFFE0B2),
    secondary = Color(0xFFFFD54F),
    onSecondary = Color(0xFF3E2723),
    secondaryContainer = Color(0xFFFF8F00),
    tertiary = Color(0xFFFFCA28),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD),
)

private val MiuixShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

private val MiuixTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
)

@Composable
fun MiuixAppTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    keyColor: Int,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (isDark) MiuixDarkColorScheme else MiuixLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MiuixTypography,
        shapes = MiuixShapes,
        content = content,
    )
}
