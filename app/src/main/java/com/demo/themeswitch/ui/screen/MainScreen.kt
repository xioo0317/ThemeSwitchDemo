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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurDefaults
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.isRuntimeShaderSupported
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.theme.MiuixTheme

sealed class Screen(val route: String, val titleResId: Int) {
    data object Home : Screen("home", R.string.nav_home)
    data object Settings : Screen("settings", R.string.nav_settings)
    data object About : Screen("about", R.string.nav_about)
}

/**
 * 底栏总高度（含系统手势区）。内容全屏铺在底栏后方（液态玻璃采样需要），
 * 页面用它在底部留出避让空间，防止最后一项被底栏遮住。
 */
val LocalScaffoldBottomPadding = staticCompositionLocalOf { 0.dp }

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
    val isMaterial = uiMode == UiMode.Material

    // 液态玻璃（miuix example 同款）：内容层绘制记录进 backdrop，底栏 textureBlur 实时采样。
    // RuntimeShader 需 API 33+，低版本自动回退纯色底栏
    val blurSupported = isRuntimeShaderSupported()
    val contentBaseColor = if (isMaterial) {
        MaterialTheme.colorScheme.surface
    } else {
        MiuixTheme.colorScheme.background
    }
    val backdrop = if (blurSupported) {
        rememberLayerBackdrop {
            drawRect(contentBaseColor)
            drawContent()
        }
    } else {
        null
    }

    // 单一 Scaffold：UI 模式只影响颜色与底栏内容，NavHost 永远保持同一调用位置，
    // 树内切换 UI 模式/语言时导航状态不丢，停留在当前页面
    Scaffold(
        containerColor = contentBaseColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            MainBottomBar(
                isMaterial = isMaterial,
                currentRoute = currentRoute,
                items = items,
                backdrop = backdrop,
                onSelect = { navigateTo(navController, it) },
            )
        },
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalScaffoldBottomPadding provides innerPadding.calculateBottomPadding(),
        ) {
            // 内容全屏铺展（不避让底栏）：底栏玻璃才能模糊到其后方的内容；
            // 各页面通过 LocalScaffoldBottomPadding 自行避让
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier),
            ) {
                MainNavHost(navController, Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun MainBottomBar(
    isMaterial: Boolean,
    currentRoute: String,
    items: List<Pair<Screen, Pair<ImageVector, ImageVector>>>,
    backdrop: LayerBackdrop?,
    onSelect: (String) -> Unit,
) {
    val surfaceColor = if (isMaterial) {
        MaterialTheme.colorScheme.surface
    } else {
        MiuixTheme.colorScheme.surface
    }
    val blurActive = backdrop != null
    val barColor = if (blurActive) Color.Transparent else surfaceColor

    // 玻璃容器：blur 激活时 textureBlur 采样内容层，bar 自身透明；
    // 低版本/关闭时回退纯色（miuix example BlurredBar 同款结构）
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (blurActive) {
                    Modifier.textureBlur(
                        backdrop = backdrop,
                        shape = RectangleShape,
                        blurRadius = 25f,
                        colors = BlurDefaults.blurColors(
                            blendColors = listOf(
                                BlendColorEntry(color = surfaceColor.copy(alpha = 0.8f)),
                            ),
                        ),
                    )
                } else {
                    Modifier
                },
            )
            .background(barColor),
    ) {
        if (isMaterial) {
            MaterialNavigationBar(containerColor = Color.Transparent) {
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
            NavigationBar(color = Color.Transparent) {
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
            AboutScreen(navController)
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
