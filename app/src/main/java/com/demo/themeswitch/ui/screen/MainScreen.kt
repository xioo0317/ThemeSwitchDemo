package com.demo.themeswitch.ui.screen

import androidx.activity.compose.BackHandler
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.R
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode
import com.demo.themeswitch.ui.component.FloatingBottomBar
import com.demo.themeswitch.ui.component.FloatingBottomBarItem
import com.demo.themeswitch.ui.component.bottombar.LocalMainPagerState
import com.demo.themeswitch.ui.component.bottombar.rememberMainPagerState
import com.demo.themeswitch.ui.theme.LocalEnableFloatingBottomBar
import com.demo.themeswitch.ui.theme.LocalEnableFloatingBottomBarBlur
import com.demo.themeswitch.ui.theme.LocalEnableBlur
import com.demo.themeswitch.ui.theme.LocalEnablePredictiveBack
import com.demo.themeswitch.ui.theme.LocalScrollAnimation
import com.demo.themeswitch.util.BlurredBar
import com.demo.themeswitch.util.rememberBlurBackdrop
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
import com.demo.themeswitch.ui.navigation.Route
import top.yukonga.miuix.kmp.nav.core.NavDisplay
import top.yukonga.miuix.kmp.nav.core.rememberNavBackStack
import top.yukonga.miuix.kmp.utils.pagerGestureOverride

sealed class Screen(val route: String, val titleResId: Int) {
    data object Home : Screen("home", R.string.nav_home)
    data object Api : Screen("api", R.string.nav_api)
    data object Settings : Screen("settings", R.string.nav_settings)
    data object Appearance : Screen("appearance", R.string.settings_section_appearance)
    data object About : Screen("about", R.string.nav_about)
}

private val MAIN_PAGES = listOf(Screen.Home, Screen.Api, Screen.Settings)

val LocalScaffoldBottomPadding = staticCompositionLocalOf { 0.dp }
val LocalBlurBackdrop = staticCompositionLocalOf<top.yukonga.miuix.kmp.blur.LayerBackdrop?> { null }

@Composable
fun MainScreen() {
    val scrollAnimation = LocalScrollAnimation.current
    val enableBlur = LocalEnableBlur.current
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { MAIN_PAGES.size })
    val mainPagerState = rememberMainPagerState(
        pagerState = pagerState,
        animatePageChanges = true,
        initialPage = 0,
    )
    mainPagerState.usePager = scrollAnimation

    // KSU 同款：miuix-nav 回退栈，支持预测性手势返回转场
    val backStack = rememberNavBackStack<Route>(Route.Main)
    val currentRoute = backStack.lastOrNull()
    val isMainRoute = currentRoute == Route.Main

    // KSU 同款：预测性手势返回——只在主页面且非首页时处理；二级页面返回由 NavDisplay 自带转场处理
    val enablePredictiveBack = LocalEnablePredictiveBack.current
    val navEventState = rememberNavigationEventState(NavigationEventInfo.None)
    val pagerBackEnabled = isMainRoute && mainPagerState.selectedPage != 0
    if (enablePredictiveBack) {
        NavigationBackHandler(
            state = navEventState,
            isBackEnabled = pagerBackEnabled,
            onBackCompleted = { mainPagerState.animateToPage(0) }
        )
    } else {
        BackHandler(enabled = pagerBackEnabled) { mainPagerState.animateToPage(0) }
    }

    val uiMode = LocalUiMode.current
    val isMaterial = uiMode == UiMode.Material
    val enableFloating = LocalEnableFloatingBottomBar.current
    val enableFloatingBlur = LocalEnableFloatingBottomBarBlur.current

    // KSU 同款：Scaffold 背景透明，让窗口背景墙透出来给液态玻璃折射
    val blurBackdrop = rememberBlurBackdrop(enableBlur)
    val layerBackdrop = rememberLayerBackdrop {
        drawContent()
    }
    val useBackdropLayer = enableFloating && enableFloatingBlur

    CompositionLocalProvider(LocalMainPagerState provides mainPagerState) {
        // KSU 同款分流：Material 模式不透明 surfaceContainer，Miuix 模式透明（液态玻璃透背景墙）
        Scaffold(
            containerColor = if (isMaterial) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (isMainRoute) {
                    MainBottomBar(
                        isMaterial = isMaterial,
                        enableFloating = enableFloating,
                        blurEnabled = enableFloatingBlur,
                        backdrop = layerBackdrop,
                        blurBackdrop = blurBackdrop,
                    )
                }
            },
        ) { innerPadding ->
            CompositionLocalProvider(
                LocalScaffoldBottomPadding provides innerPadding.calculateBottomPadding(),
                LocalBlurBackdrop provides blurBackdrop,
            ) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                ) {
                    entry<Route.Main> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(if (!isMaterial) Modifier.background(MiuixTheme.colorScheme.surface) else Modifier)
                                .then(if (useBackdropLayer) Modifier.layerBackdrop(layerBackdrop) else Modifier),
                        ) {
                            if (scrollAnimation) {
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
                                        onOpenAppearance = { backStack.add(Route.Appearance) },
                                        onOpenAbout = { backStack.add(Route.About) },
                                    )
                                }
                            } else {
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
                                        onOpenAppearance = { backStack.add(Route.Appearance) },
                                        onOpenAbout = { backStack.add(Route.About) },
                                    )
                                }
                            }
                        }
                    }
                    entry<Route.Appearance> {
                        if (isMaterial) {
                            AppearanceMaterialScreen(onBack = { backStack.removeLastOrNull() })
                        } else {
                            AppearanceMiuixScreen(onBack = { backStack.removeLastOrNull() })
                        }
                    }
                    entry<Route.About> {
                        if (isMaterial) {
                            AboutMaterialScreen(onBack = { backStack.removeLastOrNull() })
                        } else {
                            AboutMiuixScreen(onBack = { backStack.removeLastOrNull() })
                        }
                    }
                }
            }
        }

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
    blurBackdrop: LayerBackdrop?,
) {
    val mainPagerState = LocalMainPagerState.current

    // KSU 同款：外层 fillMaxWidth，内层 BottomCenter 居中
    Box(modifier = Modifier.fillMaxWidth()) {
        if (enableFloating) {
            // KSU 同款底部内边距：有导航手势条时 8dp+inset，否则 28dp
            val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            val bottomPadding = if (bottomInset != 0.dp) 8.dp + bottomInset else 28.dp

            FloatingBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 28.dp, end = 28.dp, bottom = bottomPadding),
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
                        MiuixIcon(
                            imageVector = if (selected) tabFilledIcon(screen.route) else tabOutlinedIcon(screen.route),
                            contentDescription = stringResource(screen.titleResId),
                        )
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
            MaterialShortNavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                MAIN_PAGES.forEachIndexed { index, screen ->
                    val selected = mainPagerState.selectedPage == index
                    MaterialShortNavigationBarItem(
                        selected = selected,
                        onClick = { if (!selected) mainPagerState.animateToPage(index) },
                        icon = {
                            MaterialIcon(
                                imageVector = if (selected) tabFilledIcon(screen.route) else tabOutlinedIcon(screen.route),
                                contentDescription = stringResource(screen.titleResId),
                            )
                        },
                        label = { MaterialText(stringResource(screen.titleResId)) },
                    )
                }
            }
        } else {
            // KSU 同款普通底栏：BlurredBar 包裹，开启模糊时透明背景 + textureBlur
            val surfaceColor = MiuixTheme.colorScheme.surface
            if (blurBackdrop != null) {
                BlurredBar(
                    backdrop = blurBackdrop,
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    NavigationBar(color = Color.Transparent) {
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
            } else {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
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
        }
    }
}

private fun tabFilledIcon(route: String) = when (route) {
    Screen.Home.route -> Icons.Filled.Home
    Screen.Api.route -> Icons.Filled.Apps
    else -> Icons.Filled.Settings
}

private fun tabOutlinedIcon(route: String) = when (route) {
    Screen.Home.route -> Icons.Outlined.Home
    Screen.Api.route -> Icons.Outlined.Apps
    else -> Icons.Outlined.Settings
}
