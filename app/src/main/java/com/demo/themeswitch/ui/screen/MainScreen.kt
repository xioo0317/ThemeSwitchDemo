package com.demo.themeswitch.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.demo.themeswitch.R
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode
import androidx.compose.material3.Icon as MaterialIcon
import androidx.compose.material3.NavigationBar as MaterialNavigationBar
import androidx.compose.material3.NavigationBarItem as MaterialNavigationBarItem
import androidx.compose.material3.Scaffold as MaterialScaffold
import androidx.compose.material3.Text as MaterialText
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold

sealed class Screen(val route: String, val titleResId: Int) {
    data object Home : Screen("home", R.string.nav_home)
    data object Settings : Screen("settings", R.string.nav_settings)
    data object About : Screen("about", R.string.nav_about)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val items = listOf(
        Screen.Home to (Icons.Filled.Home to Icons.Outlined.Home),
        Screen.Settings to (Icons.Filled.Settings to Icons.Outlined.Settings),
        Screen.About to (Icons.Filled.Info to Icons.Outlined.Info),
    )

    val uiMode = LocalUiMode.current
    when (uiMode) {
        UiMode.Material -> MaterialScaffold(
            bottomBar = {
                MaterialNavigationBar {
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
                            onClick = { navigateTo(navController, screen.first.route) },
                        )
                    }
                }
            },
        ) { innerPadding ->
            MainNavHost(navController, Modifier.padding(innerPadding))
        }

        UiMode.Miuix -> Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                NavigationBar {
                    items.forEach { screen ->
                        val (filledIcon, outlinedIcon) = screen.second
                        val selected = currentRoute == screen.first.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigateTo(navController, screen.first.route) },
                            icon = if (selected) filledIcon else outlinedIcon,
                            label = stringResource(screen.first.titleResId),
                        )
                    }
                }
            },
        ) { innerPadding ->
            MainNavHost(navController, Modifier.padding(innerPadding))
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
                UiMode.Material -> SettingsMaterialScreen()
                UiMode.Miuix -> SettingsMiuixScreen()
            }
        }
        composable(Screen.About.route) {
            AboutScreen()
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
