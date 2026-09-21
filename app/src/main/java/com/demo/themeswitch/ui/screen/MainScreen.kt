package com.demo.themeswitch.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge as MaterialBadge
import androidx.compose.material3.BadgedBox as MaterialBadgedBox
import androidx.compose.material3.Icon as MaterialIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBar as MaterialShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem as MaterialShortNavigationBarItem
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.R
import com.demo.themeswitch.data.BackendMonitor
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode
import com.demo.themeswitch.ui.component.FloatingBottomBar
import com.demo.themeswitch.ui.component.FloatingBottomBarItem
import com.demo.themeswitch.ui.component.bottombar.LocalMainPagerState
import com.demo.themeswitch.ui.component.bottombar.MainPagerState
import com.demo.themeswitch.ui.component.bottombar.rememberMainPagerState
import com.demo.themeswitch.ui.theme.LocalEnableFloatingBottomBar
import com.demo.themeswitch.ui.theme.LocalEnableFloatingBottomBarBlur
import com.demo.themeswitch.ui.theme.LocalEnableNavigationBadge
import com.demo.themeswitch.ui.theme.LocalScrollAnimation
import top.yukonga.miuix.kmp.basic.Badge as MiuixBadge
import top.yukonga.miuix.kmp.basic.BadgedBox as MiuixBadgedBox
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PagerGestureNestedScrollConnection
import top.yukonga.miuix.kmp.utils.PagerInterceptionMode
import top.yukonga.miuix.kmp.utils.pagerGestureOverride

sealed class Screen(val route: String, val titleResId: Int) {
    data object Home : Screen("home", R.string.nav_home)
    data object Api : Screen("api", R.string.nav_api)
    data object Settings : Screen("settings", R.string.nav_settings)
    // 子页面：从设置页进入，不显示底栏
    data object Appearance : Screen("appearance", R.string.settings_section_appearance)
    data object About : Screen("about", R.string.nav_about)
}

/** 主界面 3 个可左右滑动的页面 */
private val MAIN_PAGES = listOf(Screen.Home, Screen.Api, Screen.Settings)

/**
 * 底栏总高度（含系统手势区）。页面用它在底部留出避让空间，
 * 防止最后一项被底栏遮住。
 */
val LocalScaffoldBottomPadding = staticCompositionLocalOf { 0.dp }

@Composable
fun MainScreen() {
    val scrollAnimation = LocalScrollAnimation.current
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { MAIN_PAGES.size })
    val mainPagerState = rememberMainPagerState(
        pagerState = pagerState,
        animatePageChanges = true,
        initialPage = 0,
    )
    mainPagerState.usePager = scrollAnimation

    // 子页面（外观/关于）：简单状态栈，替代原 NavHost
    var subRoute by rememberSaveable { mutableStateOf<String?>(null) }

    // KSU 同款返回键：子页面返回上一级，主界面非首页先回首页
    BackHandler(enabled = subRoute != null || mainPagerState.selectedPage != 0) {
        when {
            subRoute != null -> subRoute = null
            else -> mainPagerState.animateToPage(0)
        }
    }

    val uiMode = LocalUiMode.current
    val isMaterial = uiMode == UiMode.Material
    val enableFloating = LocalEnableFloatingBottomBar.current
    val enableFloatingBlur = LocalEnableFloatingBottomBarBlur.current
    val enableNavigationBadge = LocalEnableNavigationBadge.current

    // KSU 导航栏角标同款：开启后，执行页 tab 在后端离线时显示红点提醒
    val backendOffline = BackendMonitor.lastResult?.online == false
    val showApiBadge = enableNavigationBadge && backendOffline

    // 悬浮底栏液态玻璃的取景层：记录内容区（含背景色），底栏用它做背景折射
    val containerColor = if (isMaterial) {
        MaterialTheme.colorScheme.surface
    } else {
        MiuixTheme.colorScheme.background
    }
    val layerBackdrop = rememberLayerBackdrop {
        drawRect(containerColor)
        drawContent()
    }
    val useBackdropLayer = enableFloating && enableFloatingBlur

    CompositionLocalProvider(LocalMainPagerState provides mainPagerState) {
        Scaffold(
            containerColor = containerColor,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (subRoute == null) {
                    MainBottomBar(
                        isMaterial = isMaterial,
                        enableFloating = enableFloating,
                        blurEnabled = enableFloatingBlur,
                        backdrop = layerBackdrop,
                        showApiBadge = showApiBadge,
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
                        .then(if (useBackdropLayer) Modifier.layerBackdrop(layerBackdrop) else Modifier),
                ) {
                    if (subRoute != null) {
                        when (subRoute) {
                            Screen.Appearance.route -> {
                                if (isMaterial) {
                                    AppearanceMaterialScreen(onBack = { subRoute = null })
                                } else {
                                    AppearanceMiuixScreen(onBack = { subRoute = null })
                                }
                            }
                            Screen.About.route -> {
                                if (isMaterial) {
                                    AboutMaterialScreen(onBack = { subRoute = null })
                                } else {
                                    AboutMiuixScreen(onBack = { subRoute = null })
                                }
                            }
                        }
                    } else if (scrollAnimation) {
                        // 「页面切换动画」开：HorizontalPager，支持左右滑动 + 手势拦截（列表竖滑不误触横滑）
                        HorizontalPager(
                            state = mainPagerState.pagerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .pagerGestureOverride(
                                    pagerState = mainPagerState.pagerState,
                                    mode = PagerInterceptionMode.CrossAxisInterceptor,
                                ),
                            beyondViewportPageCount = MAIN_PAGES.size - 1,
                            overscrollEffect = null,
                            userScrollEnabled = false,
                            pageNestedScrollConnection = PagerGestureNestedScrollConnection,
                        ) { page ->
                            MainPage(
                                page = page,
                                isMaterial = isMaterial,
                                onOpenAppearance = { subRoute = Screen.Appearance.route },
                                onOpenAbout = { subRoute = Screen.About.route },
                            )
                        }
                    } else {
                        // 「页面切换动画」关：KSU 同款淡入淡出
                        AnimatedContent(
                            targetState = mainPagerState.selectedPage,
                            transitionSpec = {
                                fadeIn(tween(340)) togetherWith fadeOut(tween(340))
                            },
                            label = "MainScreenTransition",
                        ) { page ->
                            MainPage(
                                page = page,
                                isMaterial = isMaterial,
                                onOpenAppearance = { subRoute = Screen.Appearance.route },
                                onOpenAbout = { subRoute = Screen.About.route },
                            )
                        }
                    }
                }
            }
        }

        // 手动滑动结束时，把 pager 当前页同步回 selectedPage（底栏选中态跟随）
        LaunchedEffect(mainPagerState) {
            snapshotFlow { mainPagerState.pagerState.currentPage }.collect {
                mainPagerState.syncPage()
            }
        }
    }
}

@Composable
private fun MainPage(
    page: Int,
    isMaterial: Boolean,
    onOpenAppearance: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    when (page) {
        0 -> if (isMaterial) HomeMaterialScreen() else HomeMiuixScreen()
        1 -> if (isMaterial) ApiMaterialScreen() else ApiMiuixScreen()
        2 -> if (isMaterial) {
            SettingsMaterialScreen(onOpenAppearance = onOpenAppearance, onOpenAbout = onOpenAbout)
        } else {
            SettingsMiuixScreen(onOpenAppearance = onOpenAppearance, onOpenAbout = onOpenAbout)
        }
    }
}

@Composable
private fun MainBottomBar(
    isMaterial: Boolean,
    enableFloating: Boolean,
    blurEnabled: Boolean,
    backdrop: LayerBackdrop,
    showApiBadge: Boolean,
) {
    val mainPagerState = LocalMainPagerState.current

    if (enableFloating) {
        // KSU 同款液态玻璃悬浮胶囊底栏（lens 折射 + vibrancy + 内阴影 + 可拖拽指示器）
        FloatingBottomBar(
            selectedIndex = mainPagerState.selectedPage,
            onSelected = { index -> mainPagerState.animateToPage(index) },
            backdrop = backdrop,
            tabsCount = MAIN_PAGES.size,
            isBlurEnabled = blurEnabled,
        ) { activateTab ->
            MAIN_PAGES.forEachIndexed { index, screen ->
                val selected = mainPagerState.selectedPage == index
                FloatingBottomBarItem(
                    selected = selected,
                    onClick = { activateTab(index) },
                    modifier = Modifier.defaultMinSize(minWidth = 76.dp),
                ) {
                    val icon: @Composable () -> Unit = {
                        MiuixIcon(
                            imageVector = if (selected) tabFilledIcon(screen.route) else tabOutlinedIcon(screen.route),
                            contentDescription = stringResource(screen.titleResId),
                        )
                    }
                    if (showApiBadge && screen == Screen.Api && !selected) {
                        MiuixBadgedBox(badge = { MiuixBadge() }) { icon() }
                    } else {
                        icon()
                    }
                    MiuixText(
                        text = stringResource(screen.titleResId),
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Visible,
                    )
                }
            }
        }
    } else if (isMaterial) {
        // M3 Expressive 底栏：ShortNavigationBar + filled/outlined 双态图标
        MaterialShortNavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainer) {
            MAIN_PAGES.forEachIndexed { index, screen ->
                val selected = mainPagerState.selectedPage == index
                MaterialShortNavigationBarItem(
                    selected = selected,
                    onClick = { if (!selected) mainPagerState.animateToPage(index) },
                    icon = {
                        if (showApiBadge && screen == Screen.Api && !selected) {
                            MaterialBadgedBox(badge = { MaterialBadge() }) {
                                MaterialIcon(
                                    imageVector = if (selected) tabFilledIcon(screen.route) else tabOutlinedIcon(screen.route),
                                    contentDescription = stringResource(screen.titleResId),
                                )
                            }
                        } else {
                            MaterialIcon(
                                imageVector = if (selected) tabFilledIcon(screen.route) else tabOutlinedIcon(screen.route),
                                contentDescription = stringResource(screen.titleResId),
                            )
                        }
                    },
                    label = { MaterialText(stringResource(screen.titleResId)) },
                )
            }
        }
    } else {
        val surfaceColor = MiuixTheme.colorScheme.surface
        NavigationBar(color = surfaceColor) {
            MAIN_PAGES.forEachIndexed { index, screen ->
                NavigationBarItem(
                    selected = mainPagerState.selectedPage == index,
                    onClick = { mainPagerState.animateToPage(index) },
                    icon = if (mainPagerState.selectedPage == index) tabFilledIcon(screen.route) else tabOutlinedIcon(screen.route),
                    label = stringResource(screen.titleResId),
                )
            }
        }
    }
}

private fun tabFilledIcon(route: String) = when (route) {
    Screen.Home.route -> Icons.Filled.Home
    Screen.Api.route -> Icons.Filled.Bolt
    else -> Icons.Filled.Settings
}

private fun tabOutlinedIcon(route: String) = when (route) {
    Screen.Home.route -> Icons.Outlined.Home
    Screen.Api.route -> Icons.Outlined.Bolt
    else -> Icons.Outlined.Settings
}
