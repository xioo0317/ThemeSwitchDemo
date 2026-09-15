package com.demo.themeswitch.ui.screen

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.demo.themeswitch.R
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode

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

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    val (filledIcon, outlinedIcon) = screen.second
                    val selected = currentRoute == screen.first.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) filledIcon else outlinedIcon,
                                contentDescription = stringResource(screen.first.titleResId),
                            )
                        },
                        label = { Text(stringResource(screen.first.titleResId)) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.first.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoRoom(AnimatedContentTransitionScope.SlideDirection.Start, tween(300))
            },
            exitTransition = {
                slideOutOfRoom(AnimatedContentTransitionScope.SlideDirection.Start, tween(300))
            },
            popEnterTransition = {
                slideIntoRoom(AnimatedContentTransitionScope.SlideDirection.End, tween(300))
            },
            popExitTransition = {
                slideOutOfRoom(AnimatedContentTransitionScope.SlideDirection.End, tween(300))
            },
        ) {
            composable(Screen.Home.route) {
                when (uiMode) {
                    UiMode.Material -> HomeMaterialScreen()
                    UiMode.Miuix -> HomeMiuixScreen()
                }
            }
            composable(Screen.Settings.route) {
                when (uiMode) {
                    UiMode.Material -> SettingsMaterialScreen()
                    UiMode.Miuix -> SettingsMiuixScreen()
                }
            }
            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}
