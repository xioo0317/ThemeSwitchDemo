package com.demo.themeswitch.ui.screen.appearance

import androidx.compose.runtime.Composable
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode

@Composable
fun AppearanceScreen() {
    when (LocalUiMode.current) {
        UiMode.Miuix -> AppearanceMiuix()
        UiMode.Material -> AppearanceMaterial()
    }
}
