package com.demo.themeswitch

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
            // 等 DataStore 首个真实值到达再渲染，避免初始默认值与真实 UI 模式之间
            // 在组合树内发生一次 when 分支切换（NavHost 销毁重建，易崩溃）
            val appPreferences: AppPreferences? by produceState<AppPreferences?>(initialValue = null) {
                repository.preferencesFlow.collect { value = it }
            }
            val prefs = appPreferences ?: return@setContent
            val uiMode = UiMode.fromValue(prefs.uiMode)
            val colorMode = ColorMode.fromValue(prefs.themeMode)
            val systemDark = isSystemInDarkTheme()
            val isDark = when {
                colorMode.isDark -> true
                colorMode.isSystem -> systemDark
                else -> false
            }
            // 组合期本地化上下文：语言切换只换 context，UI 原地刷新，无需重启 Activity。
            // attachBaseContext + persistLanguage 仍保留，负责下次冷启动
            val localizedContext = remember(prefs.language) {
                LocaleHelper.localizedContext(this@MainActivity, prefs.language)
            }
            LaunchedEffect(isDark) {
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }
            CompositionLocalProvider(
                LocalUiMode provides uiMode,
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration,
            ) {
                AppTheme(appPreferences = prefs) {
                    MainScreen()
                }
            }
        }
    }
}
