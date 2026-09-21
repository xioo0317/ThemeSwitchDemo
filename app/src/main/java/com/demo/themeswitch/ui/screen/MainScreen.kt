package com.demo.themeswitch.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.demo.themeswitch.R
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode
import com.demo.themeswitch.ui.component.FloatingBottomBar
import com.demo.themeswitch.ui.component.FloatingBottomBarItem
import com.demo.themeswitch.ui.component.bottombar.LocalMainPagerState
import com.demo.themeswitch.ui.component.bottombar.rememberMainPagerState
import com.demo.themeswitch.ui.navigation.LocalNavigator
import com.demo.themeswitch.ui.navigation.Navigator
import com.demo.themeswitch.ui.navigation.Route
import com.demo.themeswitch.ui.theme.LocalEnableFloatingBottomBar
import com.demo.themeswitch.ui.theme.LocalEnableFloatingBottomBarBlur
import top.yukonga.miuix.kmp.theme.MiuixTheme
import com.demo.themeswitch.util.BlurredBar
import com.demo.themeswitch.util.rememberBlurBackdrop
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.utils.PagerGestureNestedScrollConnection
import top.yukonga.miuix.kmp.utils.PagerInterceptionMode
import top.yukonga.miuix.kmp.utils.pagerGestureOverride
import top.yukonga.miuix.kmp.blur.layerBackdrop

private enum class MainTab(val titleResId: Int) {
    HOME(R.string.nav_home),
    API(R.string.nav_api),
    SETTINGS(R.string.nav_settings),
}

private val TABS = MainTab.entries
const val MAIN_TAB_COUNT = 3

@Composable
fun MainScreen() {
    val navigator = LocalNavigator.current
    val uiMode = LocalUiMode.current
    val isMaterial = uiMode == UiMode.Material
    val enableFloating = LocalEnableFloatingBottomBar.current
    val enableFloatingBlur = LocalEnableFloatingBottomBarBlur.current

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { TABS.size })
    val mainPagerState = rememberMainPagerState(pagerState = pagerState)

    // 主页预测性返回：非首页时滑回第 0 页；默认开启（KSU 同款）
    val pagerBackEnabled = mainPagerState.selectedPage != 0
    val navEventState = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationBackHandler(
        state = navEventState,
        isBackEnabled = pagerBackEnabled,
        onBackCompleted = { mainPagerState.animateToPage(0) },
    )

    // Miuix 毛玻璃取景层
    val blurBackdrop = rememberBlurBackdrop(enableBlur = com.demo.themeswitch.ui.theme.LocalEnableBlur.current)
    val lensBackdrop = rememberLayerBackdrop { drawContent() }
    val useLensLayer = enableFloating && enableFloatingBlur

    CompositionLocalProvider(LocalMainPagerState provides mainPagerState) {
        Scaffold(
            containerColor = if (isMaterial) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                MainBottomBar(
                    isMaterial = isMaterial,
                    enableFloating = enableFloating,
                    lensBackdrop = lensBackdrop,
                    blurBackdrop = blurBackdrop,
                )
            },
        ) { innerPadding ->
            val bottomPad = innerPadding.calculateBottomPadding()
            CompositionLocalProvider(
                LocalScaffoldBottomPadding provides bottomPad,
                LocalBlurBackdrop provides blurBackdrop,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (!isMaterial) Modifier.background(MiuixTheme.colorScheme.surface) else Modifier)
                        .then(if (useLensLayer) Modifier.layerBackdrop(lensBackdrop) else Modifier),
                ) {
                    // 默认使用可滑动 Pager（KSU 同款手势切页）
                    HorizontalPager(
                        state = mainPagerState.pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .pagerGestureOverride(
                                pagerState = mainPagerState.pagerState,
                                mode = PagerInterceptionMode.CrossAxisInterceptor,
                            ),
                        beyondViewportPageCount = TABS.size - 1,
                        overscrollEffect = null,
                        // CrossAxisInterceptor 模式必须关闭 Pager 原生手势，横向翻页由
                        // pagerGestureOverride 拦截器 + PagerGestureNestedScrollConnection 接管。
                        // 若置为 true，Pager 原生拖拽检测会与 miuix 拦截器竞争同一指针序列，
                        // 取消页内子项的 tap，导致「界面风格 / 关于 / 语言」点击无反应。
                        userScrollEnabled = false,
                        pageNestedScrollConnection = PagerGestureNestedScrollConnection,
                    ) { page ->
                        MainPage(page, navigator)
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
private fun MainPage(page: Int, navigator: Navigator) {
    when (page) {
        0 -> if (LocalUiMode.current == UiMode.Material) HomeMaterialScreen() else HomeMiuixScreen()
        1 -> if (LocalUiMode.current == UiMode.Material) ApiMaterialScreen() else ApiMiuixScreen()
        2 -> if (LocalUiMode.current == UiMode.Material) {
            SettingsMaterialScreen(
                onOpenAppearance = { navigator.push(Route.Appearance) },
                onOpenAbout = { navigator.push(Route.About) },
            )
        } else {
            SettingsMiuixScreen(
                onOpenAppearance = { navigator.push(Route.Appearance) },
                onOpenAbout = { navigator.push(Route.About) },
            )
        }
    }
}

@Composable
private fun MainBottomBar(
    isMaterial: Boolean,
    enableFloating: Boolean,
    lensBackdrop: top.yukonga.miuix.kmp.blur.LayerBackdrop,
    blurBackdrop: top.yukonga.miuix.kmp.blur.LayerBackdrop?,
) {
    val state = LocalMainPagerState.current
    Box(modifier = Modifier.fillMaxWidth()) {
        if (enableFloating) {
            val inset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            val bottom = if (inset != 0.dp) 8.dp + inset else 28.dp
            FloatingBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 28.dp, end = 28.dp, bottom = bottom),
                selectedIndex = state.selectedPage,
                onSelected = { state.animateToPage(it) },
                backdrop = lensBackdrop,
                tabsCount = TABS.size,
                isBlurEnabled = LocalEnableFloatingBottomBarBlur.current,
            ) { activate ->
                TABS.forEachIndexed { index, tab ->
                    val selected = state.selectedPage == index
                    FloatingBottomBarItem(
                        selected = selected,
                        onClick = { activate(index) },
                        modifier = Modifier.defaultMinSize(minWidth = 76.dp),
                    ) {
                        MiuixIcon(
                            imageVector = if (selected) filled(tab) else outlined(tab),
                            contentDescription = stringResource(tab.titleResId),
                        )
                        MiuixText(
                            text = stringResource(tab.titleResId),
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
                TABS.forEachIndexed { index, tab ->
                    val selected = state.selectedPage == index
                    MaterialShortNavigationBarItem(
                        selected = selected,
                        onClick = { if (!selected) state.animateToPage(index) },
                        icon = {
                            MaterialIcon(
                                imageVector = if (selected) filled(tab) else outlined(tab),
                                contentDescription = stringResource(tab.titleResId),
                            )
                        },
                        label = { MaterialText(stringResource(tab.titleResId)) },
                    )
                }
            }
        } else {
            BlurredBar(backdrop = blurBackdrop, modifier = Modifier.align(Alignment.BottomCenter)) {
                NavigationBar(color = Color.Transparent) {
                    TABS.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = state.selectedPage == index,
                            onClick = { state.animateToPage(index) },
                            icon = if (state.selectedPage == index) filled(tab) else outlined(tab),
                            label = stringResource(tab.titleResId),
                        )
                    }
                }
            }
        }
    }
}

private fun filled(tab: MainTab) = when (tab) {
    MainTab.HOME -> Icons.Filled.Home
    MainTab.API -> Icons.Filled.Apps
    MainTab.SETTINGS -> Icons.Filled.Settings
}

private fun outlined(tab: MainTab) = when (tab) {
    MainTab.HOME -> Icons.Outlined.Home
    MainTab.API -> Icons.Outlined.Apps
    MainTab.SETTINGS -> Icons.Outlined.Settings
}
