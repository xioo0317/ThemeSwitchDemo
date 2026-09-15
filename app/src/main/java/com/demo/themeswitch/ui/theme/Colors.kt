package com.demo.themeswitch.ui.theme

import com.demo.themeswitch.R

/**
 * 强调色色板（与 KSU ColorPalette 对齐的 15 色，含樱花粉）。
 * 顺序与 R.string.color_* 文案一一对应。
 */
val keyColorOptions: List<Int> = listOf(
    0xFFF44336, // red
    0xFFE91E63, // pink
    0xFF9C27B0, // purple
    0xFF673AB7, // deep purple
    0xFF3F51B5, // indigo
    0xFF2196F3, // blue
    0xFF00BCD4, // cyan
    0xFF009688, // teal
    0xFF4CAF50, // green
    0xFFFFEB3B, // yellow
    0xFFFFC107, // amber
    0xFFFF9800, // orange
    0xFF795548, // brown
    0xFF607D8B, // blue grey
    0xFFFF9CA8, // sakura
)

/** 强调色名称资源，顺序与 [keyColorOptions] 一致 */
val colorNameResIds: List<Int> = listOf(
    R.string.color_red,
    R.string.color_pink,
    R.string.color_purple,
    R.string.color_deep_purple,
    R.string.color_indigo,
    R.string.color_blue,
    R.string.color_cyan,
    R.string.color_teal,
    R.string.color_green,
    R.string.color_yellow,
    R.string.color_amber,
    R.string.color_orange,
    R.string.color_brown,
    R.string.color_blue_grey,
    R.string.color_sakura,
)

/** Material 3 调色风格（与 miuix ThemePaletteStyle 同名，保证双主题一致） */
val paletteStyleOptions: List<String> = listOf(
    "TonalSpot",
    "Neutral",
    "Vibrant",
    "Expressive",
)

/** Material 3 色彩规格版本 */
val colorSpecOptions: List<String> = listOf(
    "SPEC_2021",
    "SPEC_2025",
)
