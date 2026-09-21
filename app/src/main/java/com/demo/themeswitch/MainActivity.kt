package com.demo.themeswitch

import android.content.Context
import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.nav.core.NavDisplay
import top.yukonga.miuix.kmp.nav.core.NavDisplayEffects
import top.yukonga.miuix.kmp.nav.core.rememberNavSystemCornerRadius
import top.yukonga.miuix.kmp.nav.transition.NavSwipeDirection
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.LocaleHelper
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode
import com.demo.themeswitch.ui.navigation.LocalNavigator
import com.demo.themeswitch.ui.navigation.Navigator
import com.demo.themeswitch.ui.navigation.Route
import com.demo.themeswitch.ui.navigation.rememberNavigator
import com.demo.themeswitch.ui.screen.MainScreen
import com.demo.themeswitch.ui.screen.appearance.AppearanceScreen
import com.demo.themeswitch.ui.screen.about.AboutScreen
import com.demo.themeswitch.ui.theme.AppTheme
import com.demo.themeswitch.ui.theme.ColorMode

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
        )
        window.isNavigationBarContrastEnforced = false
        val repository = SettingsRepository(applicationContext)

        setContent {
            val appPreferences: AppPreferences? by produceState<AppPreferences?>(null) {
                repository.preferencesFlow.collect { value = it }
            }
            val prefs = appPreferences ?: return@setContent
            val uiMode = UiMode.fromValue(prefs.uiMode)

            val colorMode = ColorMode.fromValue(prefs.colorMode)
            val isDark = colorMode.isDark || (colorMode.isSystem && isSystemInDarkTheme())

            val localizedContext = remember(prefs.language) {
                LocaleHelper.localizedContext(this@MainActivity, prefs.language)
            }

            val systemDensity = LocalDensity.current
            val scaledDensity = remember(systemDensity, prefs.pageScale) {
                Density(
                    density = systemDensity.density * prefs.pageScale,
                    fontScale = systemDensity.fontScale,
                )
            }

            val navigator = rememberNavigator(Route.Main)

            CompositionLocalProvider(
                LocalUiMode provides uiMode,
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration,
                LocalDensity provides scaledDensity,
                LocalNavigator provides navigator,
            ) {
                AppTheme(appPreferences = prefs) {
                    val swipeDismiss = if (prefs.enableSwipeDismiss) {
                        NavSwipeDirection.LeftToRight
                    } else {
                        NavSwipeDirection.None
                    }

                    val navDisplay: @androidx.compose.runtime.Composable () -> Unit = {
                        NavDisplay(
                            backStack = navigator.backStack,
                            effects = NavDisplayEffects(
                                cornerClipRadius = rememberNavSystemCornerRadius(),
                            ),
                            onBack = { navigator.pop() },
                        ) {
                            entry<Route.Main>(swipeDismiss = swipeDismiss) { MainScreen() }
                            entry<Route.Appearance>(swipeDismiss = swipeDismiss) {
                                AppearanceScreen()
                            }
                            entry<Route.About>(swipeDismiss = swipeDismiss) { AboutScreen() }
                        }
                    }

                    when (uiMode) {
                        UiMode.Material -> androidx.compose.material3.Scaffold(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ) { navDisplay() }

                        UiMode.Miuix -> MiuixScaffold { navDisplay() }
                    }
                    SideEffect { }
                }
            }
        }
    }
}
