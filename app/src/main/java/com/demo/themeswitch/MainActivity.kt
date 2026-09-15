package com.demo.themeswitch

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowInsetsControllerCompat
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.LocaleHelper
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode
import com.demo.themeswitch.ui.screen.MainScreen
import com.demo.themeswitch.ui.theme.AppTheme
import com.demo.themeswitch.ui.theme.ColorMode

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = SettingsRepository(applicationContext)
        setContent {
            val appPreferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
            val uiMode = UiMode.fromValue(appPreferences.uiMode)
            val colorMode = ColorMode.fromValue(appPreferences.themeMode)
            val systemDark = isSystemInDarkTheme()
            val isDark = when {
                colorMode.isDark -> true
                colorMode.isSystem -> systemDark
                else -> false
            }
            LaunchedEffect(isDark) {
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }
            CompositionLocalProvider(LocalUiMode provides uiMode) {
                AppTheme(appPreferences = appPreferences) {
                    MainScreen()
                }
            }
        }
    }
}
