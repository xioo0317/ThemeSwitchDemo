package com.demo.themeswitch.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import com.demo.themeswitch.ui.component.FloatingCapsuleBar
import com.demo.themeswitch.ui.component.FloatingTab
import com.demo.themeswitch.data.BackendMonitor
import com.demo.themeswitch.ui.theme.LocalEnableFloatingBottomBar
import com.demo.themeswitch.ui.theme.LocalEnableFloatingBottomBarBlur
import com.demo.themeswitch.ui.theme.LocalEnableNavigationBadge
import com.demo.themeswitch.ui.theme.LocalScrollAnimation
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.theme.MiuixTheme

sealed class Screen(val route: String, val titleResId: Int) {
    data object Home : Screen("home", R.string.nav_home)
    data object Api : Screen("api", R.string.nav_api)
    data object Settings : Screen("settings", R.string.nav_settings)
    // 子页面：从设置页进入，不显示底栏
    data object Appearance : Screen("appearance", R.string.settings_section_appearance)
    data object About : Screen("about", R.string.nav_about)
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

    // 底栏三项：主页 / 执行 / 设置（外观与关于从设置页进入）
    val items = listOf(
        Screen.Home to (Icons.Filled.Home to Icons.Outlined.Home),
        Screen.Api to (Icons.Filled.Bolt to Icons.Outlined.Bolt),
        Screen.Settings to (Icons.Filled.Settings to Icons.Outlined.Settings),
    )

    val uiMode = LocalUiMode.current
    val isMaterial = uiMode == UiMode.Material
    val enableFloating = LocalEnableFloatingBottomBar.current
    val enableFloatingBlur = LocalEnableFloatingBottomBarBlur.current
    val enableNavigationBadge = LocalEnableNavigationBadge.current

    // KSU 导航栏角标同款：开启后，执行页 tab 在后端离线时显示红点提醒
    val backendOffline = BackendMonitor.lastResult?.online == false
    val showApiBadge = enableNavigationBadge && backendOffline

    // 子页（外观/关于）隐藏底栏，只在首页/执行页/设置页显示
    val showBottomBar = currentRoute == Screen.Home.route ||
        currentRoute == Screen.Api.route || currentRoute == Screen.Settings.route

    // 悬浮底栏液态玻璃的取景层：记录内容区（含背景色），底栏用它做背景模糊
    val containerColor = if (isMaterial) {
        MaterialTheme.colorScheme.surface
    } else {
        MiuixTheme.colorScheme.background
    }
    val blurBackdrop: LayerBackdrop? = if (enableFloating && enableFloatingBlur) {
        rememberLayerBackdrop {
            drawRect(containerColor)
            drawContent()
        }
    } else {
        null
    }

    // 单一 Scaffold：UI 模式只影响颜色与底栏内容，NavHost 永远保持同一调用位置，
    // 树内切换 UI 模式/语言时导航状态不丢，停留在当前页面
    Scaffold(
        containerColor = containerColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                MainBottomBar(
                    isMaterial = isMaterial,
                    enableFloating = enableFloating,
                    showApiBadge = showApiBadge,
                    blurEnabled = blurBackdrop != null,
                    backdrop = blurBackdrop,
                    currentRoute = currentRoute,
                    items = items,
                    onSelect = { navigateTo(navController, it) },
                )
            }
        },
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalScaffoldBottomPadding provides innerPadding.calculateBottomPadding(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (blurBackdrop != null) Modifier.layerBackdrop(blurBackdrop) else Modifier),
            ) {
                MainNavHost(navController, Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun MainBottomBar(
    isMaterial: Boolean,
    enableFloating: Boolean,
    showApiBadge: Boolean,
    blurEnabled: Boolean,
    backdrop: LayerBackdrop?,
    currentRoute: String,
    items: List<Pair<Screen, Pair<ImageVector, ImageVector>>>,
    onSelect: (String) -> Unit,
) {
    if (enableFloating) {
        // KSU 风格悬浮胶囊底栏
        val accentColor = if (isMaterial) {
            MaterialTheme.colorScheme.primary
        } else {
            MiuixTheme.colorScheme.primary
        }
        val contentColor = if (isMaterial) {
            MaterialTheme.colorScheme.onSurfaceVariant
        } else {
            MiuixTheme.colorScheme.onBackground
        }
        val containerColor = if (isMaterial) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MiuixTheme.colorScheme.surface
        }
        val floatingTabs = items.map { (screen, icons) ->
            FloatingTab(
                filledIcon = icons.first,
                outlinedIcon = icons.second,
                label = stringResource(screen.titleResId),
                showBadge = showApiBadge && screen == Screen.Api,
            )
        }
        FloatingCapsuleBar(
            items = floatingTabs,
            selectedIndex = items.indexOfFirst { it.first.route == currentRoute }.coerceAtLeast(0),
            onSelected = { index ->
                items.getOrNull(index)?.let { onSelect(it.first.route) }
            },
            accentColor = accentColor,
            contentColor = contentColor,
            containerColor = containerColor,
            blurEnabled = blurEnabled,
            backdrop = backdrop,
        )
    } else if (isMaterial) {
        val surfaceColor = MaterialTheme.colorScheme.surface
        MaterialNavigationBar(containerColor = surfaceColor) {
            items.forEach { screen ->
                val (filledIcon, outlinedIcon) = screen.second
                val selected = currentRoute == screen.first.route
                MaterialNavigationBarItem(
                    icon = {
                        if (showApiBadge && screen == Screen.Api && !selected) {
                            BadgedBox(badge = { Badge() }) {
                                MaterialIcon(
                                    imageVector = if (selected) filledIcon else outlinedIcon,
                                    contentDescription = stringResource(screen.first.titleResId),
                                )
                            }
                        } else {
                            MaterialIcon(
                                imageVector = if (selected) filledIcon else outlinedIcon,
                                contentDescription = stringResource(screen.first.titleResId),
                            )
                        }
                    },
                    label = { MaterialText(stringResource(screen.first.titleResId)) },
                    selected = selected,
                    onClick = { onSelect(screen.first.route) },
                )
            }
        }
    } else {
        val surfaceColor = MiuixTheme.colorScheme.surface
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
    // KSU scrollAnimation 同款：关闭后页面切换退化为纯淡入淡出
    val scrollAnimation = LocalScrollAnimation.current
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            if (scrollAnimation) {
                slideInHorizontally(animationSpec = tween(300)) { it / 4 } + fadeIn(tween(300))
            } else {
                fadeIn(animationSpec = tween(150))
            }
        },
        exitTransition = {
            if (scrollAnimation) {
                slideOutHorizontally(animationSpec = tween(300)) { -it / 4 } + fadeOut(tween(300))
            } else {
                fadeOut(animationSpec = tween(150))
            }
        },
        popEnterTransition = {
            if (scrollAnimation) {
                slideInHorizontally(animationSpec = tween(300)) { -it / 4 } + fadeIn(tween(300))
            } else {
                fadeIn(animationSpec = tween(150))
            }
        },
        popExitTransition = {
            if (scrollAnimation) {
                slideOutHorizontally(animationSpec = tween(300)) { it / 4 } + fadeOut(tween(300))
            } else {
                fadeOut(animationSpec = tween(150))
            }
        },
    ) {
        composable(Screen.Home.route) {
            when (LocalUiMode.current) {
                UiMode.Material -> HomeMaterialScreen()
                UiMode.Miuix -> HomeMiuixScreen()
            }
        }
        composable(Screen.Api.route) {
            when (LocalUiMode.current) {
                UiMode.Material -> ApiMaterialScreen()
                UiMode.Miuix -> ApiMiuixScreen()
            }
        }
        composable(Screen.Settings.route) {
            when (LocalUiMode.current) {
                UiMode.Material -> SettingsMaterialScreen(
                    onOpenAppearance = { navController.navigate(Screen.Appearance.route) },
                    onOpenAbout = { navController.navigate(Screen.About.route) },
                )
                UiMode.Miuix -> SettingsMiuixScreen(
                    onOpenAppearance = { navController.navigate(Screen.Appearance.route) },
                    onOpenAbout = { navController.navigate(Screen.About.route) },
                )
            }
        }
        composable(Screen.Appearance.route) {
            when (LocalUiMode.current) {
                UiMode.Material -> AppearanceMaterialScreen(onBack = { navController.popBackStack() })
                UiMode.Miuix -> AppearanceMiuixScreen(onBack = { navController.popBackStack() })
            }
        }
        composable(Screen.About.route) {
            when (LocalUiMode.current) {
                UiMode.Material -> AboutMaterialScreen(onBack = { navController.popBackStack() })
                UiMode.Miuix -> AboutMiuixScreen(onBack = { navController.popBackStack() })
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
