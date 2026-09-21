package com.demo.themeswitch.ui.screen.about

import androidx.compose.runtime.Composable
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode
import com.demo.themeswitch.ui.navigation.LocalNavigator
import com.demo.themeswitch.ui.screen.AboutMaterialScreen
import com.demo.themeswitch.ui.screen.AboutMiuixScreen

@Composable
fun AboutScreen() {
    val navigator = LocalNavigator.current
    val onBack = { navigator.pop() }
    when (LocalUiMode.current) {
        UiMode.Miuix -> AboutMiuixScreen(onBack = onBack)
        UiMode.Material -> AboutMaterialScreen(onBack = onBack)
    }
}
