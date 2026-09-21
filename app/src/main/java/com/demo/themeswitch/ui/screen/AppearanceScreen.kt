package com.demo.themeswitch.ui.screen

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.CallToAction
import androidx.compose.material.icons.rounded.Colorize
import androidx.compose.material.icons.rounded.DesignServices
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Card as M3Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon as MaterialIcon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.R
import com.demo.themeswitch.ui.component.ExpressiveSwitch
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.theme.colorNameResIds
import com.demo.themeswitch.ui.theme.colorSpecOptions
import com.demo.themeswitch.ui.theme.isInDarkTheme
import com.demo.themeswitch.ui.theme.keyColorOptions
import com.demo.themeswitch.ui.theme.paletteStyleOptions
import com.demo.themeswitch.util.BlurredBar
import com.demo.themeswitch.util.rememberBlurBackdrop
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider as MiuixSlider
import top.yukonga.miuix.kmp.basic.SliderDefaults
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun AppearanceMiuixScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()
    val colorScheme = MiuixTheme.colorScheme

    // KSU 同款模糊
    val enableBlur = com.demo.themeswitch.ui.theme.LocalEnableBlur.current
    val blurBackdrop = rememberBlurBackdrop(enableBlur)
    val blurActive = blurBackdrop != null
    val barColor = if (blurActive) Color.Transparent else colorScheme.surface

    val keyColorItems = listOf(stringResource(R.string.settings_key_color_default)) +
        colorNameResIds.map { stringResource(it) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            BlurredBar(backdrop = blurBackdrop) {
                TopAppBar(
                    title = stringResource(R.string.settings_section_appearance),
                    color = barColor,
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                            )
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = if (blurBackdrop != null) Modifier.layerBackdrop(blurBackdrop) else Modifier) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = LocalScaffoldBottomPadding.current + 24.dp,
                ),
            ) {
                item {
                    ThemePreviewCard(
                        backgroundColor = colorScheme.surface,
                        textColor = colorScheme.onBackground,
                        accentCardColor = if (isInDarkTheme()) Color(0xFF1A3825) else Color(0xFFDFFAE4),
                        cardColor = colorScheme.surfaceVariant,
                        navBarColor = colorScheme.surface,
                        dividerColor = colorScheme.onBackground.copy(alpha = 0.1f),
                        iconColor = colorScheme.primary,
                        outlineColor = colorScheme.outline,
                        floatingBar = preferences.enableFloatingBottomBar,
                        modifier = Modifier.padding(vertical = 24.dp),
                    )
                }
                item {
                    val themeItems = listOf(
                        stringResource(R.string.theme_system),
                        stringResource(R.string.theme_light),
                        stringResource(R.string.theme_dark),
                    )
                    TabRow(
                        tabs = themeItems,
                        selectedTabIndex = preferences.themeMode.coerceIn(0, 2),
                        onTabSelected = { mode -> scope.launch { repository.setThemeMode(mode) } },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                item {
                    Card(modifier = Modifier.padding(top = 12.dp).fillMaxWidth()) {
                        // KSU 同款：启用 Monet 颜色，壁纸图标，无 summary
                        SwitchPreference(
                            title = stringResource(R.string.settings_monet),
                            startAction = {
                                Icon(
                                    imageVector = Icons.Rounded.Wallpaper,
                                    contentDescription = null,
                                    tint = colorScheme.onBackground,
                                    modifier = Modifier.padding(end = 6.dp),
                                )
                            },
                            checked = preferences.enableMonet,
                            onCheckedChange = { enabled ->
                                scope.launch { repository.setEnableMonet(enabled) }
                            }
                        )
                        AnimatedVisibility(visible = preferences.enableMonet) {
                            Column {
                                OverlayDropdownPreference(
                                    title = stringResource(R.string.settings_key_color),
                                    items = keyColorItems,
                                    startAction = {
                                        Icon(
                                            imageVector = Icons.Rounded.Colorize,
                                            contentDescription = null,
                                            tint = colorScheme.onBackground,
                                            modifier = Modifier.padding(end = 6.dp),
                                        )
                                    },
                                    selectedIndex = (keyColorOptions.indexOf(preferences.keyColor) + 1)
                                        .coerceIn(0, keyColorItems.lastIndex),
                                    onSelectedIndexChange = { index ->
                                        scope.launch {
                                            repository.setKeyColor(if (index == 0) 0 else keyColorOptions[index - 1])
                                        }
                                    },
                                )
                                AnimatedVisibility(visible = preferences.keyColor != 0) {
                                    Column {
                                        OverlayDropdownPreference(
                                            title = stringResource(R.string.settings_color_style),
                                            items = paletteStyleOptions,
                                            startAction = {
                                                Icon(
                                                    imageVector = Icons.Rounded.Style,
                                                    contentDescription = null,
                                                    tint = colorScheme.onBackground,
                                                    modifier = Modifier.padding(end = 6.dp),
                                                )
                                            },
                                            selectedIndex = paletteStyleOptions
                                                .indexOf(preferences.colorStyle)
                                                .coerceIn(0, paletteStyleOptions.lastIndex),
                                            onSelectedIndexChange = { index ->
                                                paletteStyleOptions.getOrNull(index)?.let { style ->
                                                    scope.launch { repository.setColorStyle(style) }
                                                }
                                            },
                                        )
                                        OverlayDropdownPreference(
                                            title = stringResource(R.string.settings_color_spec),
                                            items = colorSpecOptions,
                                            startAction = {
                                                Icon(
                                                    imageVector = Icons.Rounded.DesignServices,
                                                    contentDescription = null,
                                                    tint = colorScheme.onBackground,
                                                    modifier = Modifier.padding(end = 6.dp),
                                                )
                                            },
                                            selectedIndex = colorSpecOptions
                                                .indexOf(preferences.colorSpec)
                                                .coerceIn(0, colorSpecOptions.lastIndex),
                                            onSelectedIndexChange = { index ->
                                                colorSpecOptions.getOrNull(index)?.let { spec ->
                                                    scope.launch { repository.setColorSpec(spec) }
                                                }
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                // KSU 同款：界面设置合并在一个卡片
                item {
                    Card(modifier = Modifier.padding(top = 12.dp).fillMaxWidth()) {
                        if (Build.VERSION.SDK_INT >= 33) {
                            SwitchPreference(
                                title = stringResource(R.string.settings_enable_blur),
                                summary = stringResource(R.string.settings_enable_blur_summary),
                                startAction = {
                                    Icon(
                                        imageVector = Icons.Rounded.BlurOn,
                                        contentDescription = null,
                                        tint = colorScheme.onBackground,
                                        modifier = Modifier.padding(end = 6.dp),
                                    )
                                },
                                checked = preferences.enableBlur,
                                onCheckedChange = { enabled ->
                                    scope.launch { repository.setEnableBlur(enabled) }
                                },
                            )
                        }
                        // KSU 同款：CallToAction 图标
                        SwitchPreference(
                            title = stringResource(R.string.settings_floating_bottom_bar),
                            summary = stringResource(R.string.settings_floating_bottom_bar_summary),
                            startAction = {
                                Icon(
                                    imageVector = Icons.Rounded.CallToAction,
                                    contentDescription = null,
                                    tint = colorScheme.onBackground,
                                    modifier = Modifier.padding(end = 6.dp),
                                )
                            },
                            checked = preferences.enableFloatingBottomBar,
                            onCheckedChange = { enabled ->
                                scope.launch { repository.setEnableFloatingBottomBar(enabled) }
                            },
                        )
                        if (Build.VERSION.SDK_INT >= 33) {
                            AnimatedVisibility(visible = preferences.enableFloatingBottomBar) {
                                SwitchPreference(
                                    title = stringResource(R.string.settings_enable_glass),
                                    summary = stringResource(R.string.settings_enable_glass_summary),
                                    startAction = {
                                        Icon(
                                            imageVector = Icons.Rounded.WaterDrop,
                                            contentDescription = null,
                                            tint = colorScheme.onBackground,
                                            modifier = Modifier.padding(end = 6.dp),
                                        )
                                    },
                                    checked = preferences.enableFloatingBottomBarBlur,
                                    onCheckedChange = { enabled ->
                                        scope.launch { repository.setEnableFloatingBottomBarBlur(enabled) }
                                    },
                                )
                            }
                        }
                        // KSU 同款：ViewCarousel 图标
                        SwitchPreference(
                            title = stringResource(R.string.settings_scroll_animation),
                            summary = stringResource(R.string.settings_scroll_animation_summary),
                            startAction = {
                                Icon(
                                    imageVector = Icons.Rounded.ViewCarousel,
                                    contentDescription = null,
                                    tint = colorScheme.onBackground,
                                    modifier = Modifier.padding(end = 6.dp),
                                )
                            },
                            checked = preferences.enableScrollAnimation,
                            onCheckedChange = { enabled ->
                                scope.launch { repository.setEnableScrollAnimation(enabled) }
                            },
                        )
                    }
                }
                // KSU 同款：页面缩放用 ArrowPreference + bottomAction Slider
                item {
                    var pageScale by remember(preferences.pageScale) {
                        mutableFloatStateOf(preferences.pageScale)
                    }
                    var showScaleSlider by rememberSaveable { mutableStateOf(false) }
                    Card(modifier = Modifier.padding(top = 12.dp).fillMaxWidth()) {
                        ArrowPreference(
                            title = stringResource(R.string.settings_page_scale),
                            summary = stringResource(R.string.settings_page_scale_summary),
                            startAction = {
                                Icon(
                                    imageVector = Icons.Rounded.AspectRatio,
                                    contentDescription = null,
                                    tint = colorScheme.onBackground,
                                    modifier = Modifier.padding(end = 6.dp),
                                )
                            },
                            endActions = {
                                Text(
                                    text = "${(pageScale * 100).roundToInt()}%",
                                    color = colorScheme.onSurfaceVariantActions,
                                )
                            },
                            onClick = { showScaleSlider = !showScaleSlider },
                            holdDownState = showScaleSlider,
                            bottomAction = {
                                MiuixSlider(
                                    value = pageScale,
                                    onValueChange = { pageScale = it },
                                    onValueChangeFinished = {
                                        scope.launch { repository.setPageScale(pageScale) }
                                    },
                                    valueRange = 0.8f..1.1f,
                                    showKeyPoints = true,
                                    keyPoints = listOf(0.8f, 0.9f, 1f, 1.1f),
                                    magnetThreshold = 0.01f,
                                    hapticEffect = SliderDefaults.SliderHapticEffect.Step,
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppearanceMaterialScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.settings_section_appearance),
                color = MaterialTheme.colorScheme.surface,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = LocalScaffoldBottomPadding.current + 24.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ThemePreviewCard(
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                textColor = MaterialTheme.colorScheme.onSurface,
                accentCardColor = MaterialTheme.colorScheme.secondaryContainer,
                cardColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                navBarColor = MaterialTheme.colorScheme.surfaceContainer,
                dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                iconColor = MaterialTheme.colorScheme.primary,
                outlineColor = MaterialTheme.colorScheme.outlineVariant,
                floatingBar = preferences.enableFloatingBottomBar,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                val options = listOf(
                    Triple(Icons.Filled.Brightness4, stringResource(R.string.theme_system), 0),
                    Triple(Icons.Filled.Brightness7, stringResource(R.string.theme_light), 1),
                    Triple(Icons.Filled.Brightness3, stringResource(R.string.theme_dark), 2),
                )
                options.forEachIndexed { index, (icon, label, mode) ->
                    SegmentedButton(
                        selected = preferences.themeMode == mode,
                        onClick = { scope.launch { repository.setThemeMode(mode) } },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                        icon = {
                            MaterialIcon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        label = { MaterialText(label) },
                    )
                }
            }
            M3Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                MaterialSwitchRow(
                    icon = Icons.Rounded.Wallpaper,
                    title = stringResource(R.string.settings_monet),
                    checked = preferences.enableMonet,
                    onCheckedChange = { scope.launch { repository.setEnableMonet(it) } },
                )
                AnimatedVisibility(visible = preferences.enableMonet) {
                    Column {
                        CardDivider()
                        MaterialText(
                            text = stringResource(R.string.settings_key_color),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            item {
                                ColorButton(color = null, selected = preferences.keyColor == 0) {
                                    scope.launch { repository.setKeyColor(0) }
                                }
                            }
                            items(keyColorOptions) { color ->
                                ColorButton(color = color, selected = preferences.keyColor == color) {
                                    scope.launch { repository.setKeyColor(color) }
                                }
                            }
                        }
                        AnimatedVisibility(visible = preferences.keyColor != 0) {
                            Column {
                                CardDivider()
                                SettingDropdownRow(
                                    icon = Icons.Rounded.Style,
                                    title = stringResource(R.string.settings_color_style),
                                    value = preferences.colorStyle,
                                    options = paletteStyleOptions,
                                    onSelect = { index ->
                                        paletteStyleOptions.getOrNull(index)?.let { style ->
                                            scope.launch { repository.setColorStyle(style) }
                                        }
                                    },
                                )
                                SettingDropdownRow(
                                    icon = Icons.Rounded.DesignServices,
                                    title = stringResource(R.string.settings_color_spec),
                                    value = preferences.colorSpec,
                                    options = colorSpecOptions,
                                    onSelect = { index ->
                                        colorSpecOptions.getOrNull(index)?.let { spec ->
                                            scope.launch { repository.setColorSpec(spec) }
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
                CardDivider()
                if (Build.VERSION.SDK_INT >= 33) {
                    MaterialSwitchRow(
                        icon = Icons.Rounded.BlurOn,
                        title = stringResource(R.string.settings_enable_blur),
                        summary = stringResource(R.string.settings_enable_blur_summary),
                        checked = preferences.enableBlur,
                        onCheckedChange = { scope.launch { repository.setEnableBlur(it) } },
                    )
                    CardDivider()
                }
                MaterialSwitchRow(
                    icon = Icons.Rounded.CallToAction,
                    title = stringResource(R.string.settings_floating_bottom_bar),
                    summary = stringResource(R.string.settings_floating_bottom_bar_summary),
                    checked = preferences.enableFloatingBottomBar,
                    onCheckedChange = { scope.launch { repository.setEnableFloatingBottomBar(it) } },
                )
                if (Build.VERSION.SDK_INT >= 33) {
                    AnimatedVisibility(visible = preferences.enableFloatingBottomBar) {
                        Column {
                            CardDivider()
                            MaterialSwitchRow(
                                icon = Icons.Rounded.WaterDrop,
                                title = stringResource(R.string.settings_enable_glass),
                                summary = stringResource(R.string.settings_enable_glass_summary),
                                checked = preferences.enableFloatingBottomBarBlur,
                                onCheckedChange = {
                                    scope.launch { repository.setEnableFloatingBottomBarBlur(it) }
                                },
                            )
                        }
                    }
                }
                CardDivider()
                MaterialSwitchRow(
                    icon = Icons.Rounded.ViewCarousel,
                    title = stringResource(R.string.settings_scroll_animation),
                    summary = stringResource(R.string.settings_scroll_animation_summary),
                    checked = preferences.enableScrollAnimation,
                    onCheckedChange = { scope.launch { repository.setEnableScrollAnimation(it) } },
                )
            }

            M3Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                var pageScale by remember(preferences.pageScale) {
                    mutableFloatStateOf(preferences.pageScale)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MaterialIcon(
                        imageVector = Icons.Rounded.AspectRatio,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        MaterialText(
                            text = stringResource(R.string.settings_page_scale),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                        )
                        MaterialText(
                            text = stringResource(R.string.settings_page_scale_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    MaterialText(
                        text = "${(pageScale * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Slider(
                    value = pageScale,
                    onValueChange = { pageScale = it },
                    onValueChangeFinished = {
                        scope.launch { repository.setPageScale(pageScale) }
                    },
                    valueRange = 0.8f..1.1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun CardDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
    )
}

@Composable
private fun MaterialSwitchRow(
    icon: ImageVector,
    title: String,
    summary: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MaterialIcon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            MaterialText(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            if (summary != null) {
                MaterialText(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        ExpressiveSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingDropdownRow(
    icon: ImageVector,
    title: String,
    value: String,
    options: List<String>,
    onSelect: (Int) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MaterialIcon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                MaterialText(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                MaterialText(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            MaterialIcon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { MaterialText(option) },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun ColorButton(
    color: Int?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val container = if (color != null) Color(color) else MaterialTheme.colorScheme.surfaceContainerHighest
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(container)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (color == null) {
            MaterialIcon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
        } else if (selected) {
            MaterialIcon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

/** 主题预览缩略图 */
@Composable
private fun ThemePreviewCard(
    backgroundColor: Color,
    textColor: Color,
    accentCardColor: Color,
    cardColor: Color,
    navBarColor: Color,
    dividerColor: Color,
    iconColor: Color,
    outlineColor: Color,
    floatingBar: Boolean,
    modifier: Modifier = Modifier,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    Column(
        modifier = modifier
            .width(screenWidth * 0.4f)
            .aspectRatio(0.56f)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(16.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.2f)))
            Spacer(Modifier.width(6.dp))
            Box(
                Modifier
                    .width(56.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(textColor.copy(alpha = 0.35f)),
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentCardColor)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(20.dp).clip(CircleShape).background(iconColor))
            Spacer(Modifier.width(6.dp))
            Column {
                Box(
                    Modifier
                        .width(48.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(textColor.copy(alpha = 0.4f)),
                )
                Spacer(Modifier.height(3.dp))
                Box(
                    Modifier
                        .width(32.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(textColor.copy(alpha = 0.25f)),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(cardColor)
                .padding(8.dp),
        ) {
            repeat(3) { index ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(12.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.6f)))
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(textColor.copy(alpha = 0.3f)),
                    )
                    Box(
                        Modifier
                            .width(20.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(textColor.copy(alpha = 0.2f)),
                    )
                }
                if (index != 2) {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
        Spacer(Modifier.weight(1f))
        if (floatingBar) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier
                        .width(88.dp)
                        .height(16.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.25f)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (index == 0) iconColor else textColor.copy(alpha = 0.5f)),
                        )
                    }
                }
            }
        } else {
            HorizontalDivider(color = outlineColor, thickness = 0.5.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .background(navBarColor),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (index == 0) iconColor else textColor.copy(alpha = 0.3f)),
                    )
                }
            }
        }
    }
}
