package com.demo.themeswitch.ui.screen.appearance

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuOpen
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.CallToAction
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material.icons.rounded.DesignServices
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.Swipe
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.navigation.LocalNavigator
import com.demo.themeswitch.ui.theme.ColorMode
import com.demo.themeswitch.ui.theme.LocalEnableBlur
import com.demo.themeswitch.ui.theme.colorNameResIds
import com.demo.themeswitch.ui.theme.keyColorOptions
import com.demo.themeswitch.util.BlurredBar
import com.demo.themeswitch.util.rememberBlurBackdrop
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun AppearanceMiuix() {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val prefs by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.current

    val scrollBehavior = MiuixScrollBehavior()
    val enableBlur = LocalEnableBlur.current
    val backdrop = rememberBlurBackdrop(enableBlur)
    val barColor = if (backdrop != null) Color.Transparent else colorScheme.surface

    val colorMode = ColorMode.fromValue(prefs.colorMode)
    val isDark = colorMode.isDark || (colorMode.isSystem && isSystemInDarkTheme())

    Scaffold(
        topBar = {
            BlurredBar(backdrop) {
                TopAppBar(
                    color = barColor,
                    title = stringResource(R.string.settings_theme),
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            val dir = LocalLayoutDirection.current
                            Icon(
                                modifier = Modifier.graphicsLayer {
                                    if (dir == LayoutDirection.Rtl) scaleX = -1f
                                },
                                imageVector = MiuixIcons.Back,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        popupHost = { },
        contentWindowInsets = WindowInsets.systemBars
            .add(WindowInsets.displayCutout)
            .only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Box(modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 12.dp),
                contentPadding = innerPadding,
                overscrollEffect = null,
            ) {
                item {
                    Spacer(Modifier.height(32.dp))
                    ThemePreviewCardMiuix(
                        keyColor = prefs.keyColor,
                        isDark = isDark,
                        monet = colorMode.isMonet,
                        enableFloatingBottomBar = prefs.enableFloatingBottomBar,
                        enableFloatingBottomBarBlur = prefs.enableFloatingBottomBarBlur,
                        paletteStyle = prefs.colorStyle,
                        colorSpec = prefs.colorSpec,
                    )
                    Spacer(Modifier.height(72.dp))

                    val modes = listOf(
                        stringResource(R.string.theme_system),
                        stringResource(R.string.theme_light),
                        stringResource(R.string.theme_dark),
                    )
                    // TabRow 选中索引：Monet 系列也映射回 0/1/2
                    val baseIndex = if (colorMode.value >= 3) colorMode.value - 3 else colorMode.value
                    TabRow(
                        tabs = modes,
                        selectedTabIndex = baseIndex.coerceIn(0, 2),
                        onTabSelected = { idx ->
                            // 保留当前 Monet 状态
                            val value = if (colorMode.isMonet) idx + 3 else idx
                            scope.launch { repository.setColorMode(value) }
                        },
                    )

                    // ── Monet 卡 ──
                    Card(
                        modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
                    ) {
                        SwitchPreference(
                            title = stringResource(R.string.settings_monet),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Wallpaper,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(R.string.settings_monet),
                                    tint = colorScheme.onBackground,
                                )
                            },
                            checked = colorMode.isMonet,
                            onCheckedChange = { monet ->
                                val next = if (monet) colorMode.toMonetMode()
                                else colorMode.toNonMonetMode()
                                scope.launch { repository.setColorMode(next) }
                            },
                        )
                        AnimatedVisibility(visible = colorMode.isMonet) {
                            Column {
                                val values = listOf(0) + keyColorOptions
                                val items = listOf(stringResource(R.string.settings_key_color_default)) +
                                    colorNameResIds.map { stringResource(it) }
                                OverlayDropdownPreference(
                                    title = stringResource(R.string.settings_key_color),
                                    items = items,
                                    startAction = {
                                        Icon(
                                            Icons.Rounded.Colorize,
                                            modifier = Modifier.padding(end = 6.dp),
                                            contentDescription = stringResource(R.string.settings_key_color),
                                            tint = colorScheme.onBackground,
                                        )
                                    },
                                    selectedIndex = values.indexOf(prefs.keyColor).coerceAtLeast(0),
                                    onSelectedIndexChange = { i ->
                                        scope.launch { repository.setKeyColor(values[i]) }
                                    },
                                )
                                AnimatedVisibility(visible = prefs.keyColor != 0) {
                                    Column {
                                        val styles = PaletteStyle.entries
                                        OverlayDropdownPreference(
                                            title = stringResource(R.string.settings_color_style),
                                            startAction = {
                                                Icon(
                                                    Icons.Rounded.Style,
                                                    modifier = Modifier.padding(end = 6.dp),
                                                    contentDescription = stringResource(R.string.settings_color_style),
                                                    tint = colorScheme.onBackground,
                                                )
                                            },
                                            items = styles.map { it.name },
                                            selectedIndex = styles.indexOfFirst { it.name == prefs.colorStyle }
                                                .coerceAtLeast(0),
                                            onSelectedIndexChange = { i ->
                                                scope.launch { repository.setColorStyle(styles[i].name) }
                                            },
                                        )
                                        val specs = ColorSpec.SpecVersion.entries
                                        OverlayDropdownPreference(
                                            title = stringResource(R.string.settings_color_spec),
                                            startAction = {
                                                Icon(
                                                    Icons.Rounded.DesignServices,
                                                    modifier = Modifier.padding(end = 6.dp),
                                                    contentDescription = stringResource(R.string.settings_color_spec),
                                                    tint = colorScheme.onBackground,
                                                )
                                            },
                                            items = specs.map { it.name },
                                            selectedIndex = specs.indexOfFirst { it.name == prefs.colorSpec }
                                                .coerceAtLeast(0),
                                            onSelectedIndexChange = { i ->
                                                scope.launch { repository.setColorSpec(specs[i].name) }
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── 模糊/悬浮底栏卡 ──
                    Card(modifier = Modifier.padding(top = 12.dp).fillMaxWidth()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            SwitchPreference(
                                title = stringResource(R.string.settings_enable_blur),
                                summary = stringResource(R.string.settings_enable_blur_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.BlurOn,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(R.string.settings_enable_blur),
                                        tint = colorScheme.onBackground,
                                    )
                                },
                                checked = prefs.enableBlur,
                                onCheckedChange = { scope.launch { repository.setEnableBlur(it) } },
                            )
                        }
                        SwitchPreference(
                            title = stringResource(R.string.settings_floating_bottom_bar),
                            summary = stringResource(R.string.settings_floating_bottom_bar_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.CallToAction,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(R.string.settings_floating_bottom_bar),
                                    tint = colorScheme.onBackground,
                                )
                            },
                            checked = prefs.enableFloatingBottomBar,
                            onCheckedChange = {
                                scope.launch { repository.setEnableFloatingBottomBar(it) }
                            },
                        )
                        AnimatedVisibility(
                            visible = prefs.enableFloatingBottomBar &&
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU,
                        ) {
                            SwitchPreference(
                                title = stringResource(R.string.settings_enable_glass),
                                summary = stringResource(R.string.settings_enable_glass_summary),
                                startAction = {
                                    Icon(
                                        Icons.Rounded.WaterDrop,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(R.string.settings_enable_glass),
                                        tint = colorScheme.onBackground,
                                    )
                                },
                                checked = prefs.enableFloatingBottomBarBlur,
                                onCheckedChange = {
                                    scope.launch { repository.setEnableFloatingBottomBarBlur(it) }
                                },
                            )
                        }
                    }

                    // ── 返回手势卡 ──
                    Card(modifier = Modifier.padding(top = 12.dp).fillMaxWidth()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            SwitchPreference(
                                title = stringResource(R.string.settings_predictive_back),
                                summary = stringResource(R.string.settings_predictive_back_summary),
                                startAction = {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.MenuOpen,
                                        modifier = Modifier.padding(end = 6.dp),
                                        contentDescription = stringResource(R.string.settings_predictive_back),
                                        tint = colorScheme.onBackground,
                                    )
                                },
                                checked = prefs.enablePredictiveBack,
                                onCheckedChange = {
                                    scope.launch { repository.setEnablePredictiveBack(it) }
                                },
                            )
                        }
                        SwitchPreference(
                            title = stringResource(R.string.settings_enable_swipe_dismiss),
                            summary = stringResource(R.string.settings_enable_swipe_dismiss_summary),
                            startAction = {
                                Icon(
                                    Icons.Rounded.Swipe,
                                    modifier = Modifier.padding(end = 6.dp),
                                    contentDescription = stringResource(R.string.settings_enable_swipe_dismiss),
                                    tint = colorScheme.onBackground,
                                )
                            },
                            checked = prefs.enableSwipeDismiss,
                            onCheckedChange = {
                                scope.launch { repository.setEnableSwipeDismiss(it) }
                            },
                        )
                    }
                }
                item {
                    Spacer(
                        Modifier.height(
                            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() +
                                WindowInsets.captionBar.asPaddingValues().calculateBottomPadding() + 12.dp,
                        ),
                    )
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun ThemePreviewCardMiuix(
    keyColor: Int,
    isDark: Boolean,
    monet: Boolean,
    enableFloatingBottomBar: Boolean = false,
    enableFloatingBottomBarBlur: Boolean = false,
    paletteStyle: String = "TonalSpot",
    colorSpec: String = "SPEC_2021",
) {
    val configuration = LocalConfiguration.current
    val ratio = configuration.screenWidthDp.toFloat() / configuration.screenHeightDp.toFloat()

    val style = try { PaletteStyle.valueOf(paletteStyle) } catch (_: Exception) { PaletteStyle.TonalSpot }
    val spec = if (colorSpec == "SPEC_2025") ColorSpec.SpecVersion.SPEC_2025
    else ColorSpec.SpecVersion.SPEC_2021

    val seedColor = if (keyColor == 0) colorScheme.primary else Color(keyColor)
    val effectiveStyle = if (keyColor == 0) PaletteStyle.TonalSpot else style
    val effectiveSpec = if (keyColor == 0) ColorSpec.SpecVersion.Default else spec
    val dynamicCs = rememberDynamicColorScheme(
        seedColor = seedColor,
        isDark = isDark,
        style = effectiveStyle,
        specVersion = effectiveSpec,
    )

    val bgColor = if (monet) dynamicCs.background else colorScheme.surface
    val textColor = if (monet) dynamicCs.onSurface else colorScheme.onBackground
    val accentCardColor = when {
        monet -> dynamicCs.secondaryContainer
        isDark -> Color(0xFF1A3825)
        else -> Color(0xFFDFFAE4)
    }
    val cardColor = if (monet) dynamicCs.surfaceContainerHighest else colorScheme.surfaceVariant
    val navBarColor = if (monet) dynamicCs.surfaceContainer else colorScheme.surface
    val iconColor = if (monet) dynamicCs.primary else colorScheme.primary
    val navSelected = colorScheme.onSurfaceContainer
    val navUnselected = colorScheme.onSurfaceContainer.copy(alpha = 0.5f)

    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .aspectRatio(ratio)
                .clip(RoundedCornerShape(20.dp))
                .background(bgColor)
                .border(1.dp, colorScheme.outline, RoundedCornerShape(20.dp)),
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .padding(start = 12.dp, top = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 12.sp,
                        color = textColor,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentCardColor),
                )
                BoxWithConstraints(modifier = Modifier.weight(1f)) {
                    val count = when {
                        maxHeight >= 96.dp -> 2
                        maxHeight >= 72.dp -> 1
                        else -> 0
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            Modifier.fillMaxWidth().weight(1f)
                                .clip(RoundedCornerShape(6.dp)).background(cardColor),
                        )
                        repeat(count) {
                            Box(
                                Modifier.fillMaxWidth().height(12.dp)
                                    .clip(RoundedCornerShape(6.dp)).background(cardColor),
                            )
                        }
                    }
                }
            }

            if (enableFloatingBottomBar) {
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .height(28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (enableFloatingBottomBarBlur) navBarColor.copy(alpha = 0.5f)
                                else navBarColor,
                            )
                            .border(0.5.dp, textColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(3) {
                            Box(
                                Modifier.size(13.dp).clip(RoundedCornerShape(2.dp))
                                    .background(if (it == 0) iconColor else textColor),
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                ) {
                    Box(
                        Modifier.fillMaxWidth().height(0.5.dp)
                            .background(textColor.copy(alpha = 0.1f)),
                    )
                    Row(
                        modifier = Modifier
                            .height(36.dp).fillMaxWidth().background(navBarColor)
                            .padding(top = 2.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(3) {
                            Box(
                                Modifier.size(15.dp).clip(RoundedCornerShape(3.dp))
                                    .background(if (it == 0) navSelected else navUnselected),
                            )
                        }
                    }
                }
            }
        }
    }
}
