package com.demo.themeswitch.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon as MaterialIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar as MaterialNavigationBar
import androidx.compose.material3.NavigationBarItem as MaterialNavigationBarItem
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.demo.themeswitch.R
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

sealed class Screen(val route: String, val titleResId: Int) {
    data object Home : Screen("home", R.string.nav_home)
    data object Settings : Screen("settings", R.string.nav_settings)
    // 子页面：从设置页进入，不进底栏
    data object Appearance : Screen("appearance", R.string.settings_section_appearance)
}

/**
 * 底栏总高度（含系统手势区）。页面用它在底部留出避让空间，
 * 防止最后一项被底栏遮住。
 */
val LocalScaffoldBottomPadding = staticCompositionLocalOf { 0.dp }

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    // 底栏两项：主页 / 设置（关于已并入设置页卡片）
    val items = listOf(
        Screen.Home to (Icons.Filled.Home to Icons.Outlined.Home),
        Screen.Settings to (Icons.Filled.Settings to Icons.Outlined.Settings),
    )

    val uiMode = LocalUiMode.current
    val isMaterial = uiMode == UiMode.Material

    // 单一 Scaffold：UI 模式只影响颜色与底栏内容，NavHost 永远保持同一调用位置，
    // 树内切换 UI 模式/语言时导航状态不丢，停留在当前页面
    Scaffold(
        containerColor = if (isMaterial) {
            MaterialTheme.colorScheme.surface
        } else {
            MiuixTheme.colorScheme.background
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            MainBottomBar(
                isMaterial = isMaterial,
                currentRoute = currentRoute,
                items = items,
                onSelect = { navigateTo(navController, it) },
            )
        },
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalScaffoldBottomPadding provides innerPadding.calculateBottomPadding(),
        ) {
            MainNavHost(navController, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun MainBottomBar(
    isMaterial: Boolean,
    currentRoute: String,
    items: List<Pair<Screen, Pair<ImageVector, ImageVector>>>,
    onSelect: (String) -> Unit,
) {
    val surfaceColor = if (isMaterial) {
        MaterialTheme.colorScheme.surface
    } else {
        MiuixTheme.colorScheme.surface
    }
    if (isMaterial) {
        MaterialNavigationBar(containerColor = surfaceColor) {
            items.forEach { screen ->
                val (filledIcon, outlinedIcon) = screen.second
                val selected = currentRoute == screen.first.route
                MaterialNavigationBarItem(
                    icon = {
                        MaterialIcon(
                            imageVector = if (selected) filledIcon else outlinedIcon,
                            contentDescription = stringResource(screen.first.titleResId),
                        )
                    },
                    label = { MaterialText(stringResource(screen.first.titleResId)) },
                    selected = selected,
                    onClick = { onSelect(screen.first.route) },
                )
            }
        }
    } else {
        NavigationBar(color = surfaceColor) {
            items.forEach { screen ->
                val (filledIcon, outlinedIcon) = screen.second
                val selected = currentRoute == screen.first.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelect(screen.first.route) },
                    icon = if (selected) filledIcon else outlinedIcon,
                    label = stringResource(screen.first.titleResId),
                )
            }
        }
    }
}

@Composable
private fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(animationSpec = tween(300)) { it / 4 } + fadeIn(tween(300))
        },
        exitTransition = {
            slideOutHorizontally(animationSpec = tween(300)) { -it / 4 } + fadeOut(tween(300))
        },
        popEnterTransition = {
            slideInHorizontally(animationSpec = tween(300)) { -it / 4 } + fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(animationSpec = tween(300)) { it / 4 } + fadeOut(tween(300))
        },
    ) {
        composable(Screen.Home.route) {
            when (LocalUiMode.current) {
                UiMode.Material -> HomeMaterialScreen()
                UiMode.Miuix -> HomeMiuixScreen()
            }
        }
        composable(Screen.Settings.route) {
            when (LocalUiMode.current) {
                UiMode.Material -> SettingsMaterialScreen(
                    onOpenAppearance = { navController.navigate(Screen.Appearance.route) },
                )
                UiMode.Miuix -> SettingsMiuixScreen(
                    onOpenAppearance = { navController.navigate(Screen.Appearance.route) },
                )
            }
        }
        composable(Screen.Appearance.route) {
            when (LocalUiMode.current) {
                UiMode.Material -> AppearanceMaterialScreen(onBack = { navController.popBackStack() })
                UiMode.Miuix -> AppearanceMiuixScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun navigateTo(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
