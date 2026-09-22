package com.demo.themeswitch.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode
import com.demo.themeswitch.ui.theme.LocalEnableBlur
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/** 设置页（MIUI 风格，KSU 同款卡片行样式） */
@Composable
fun SettingsMiuixScreen(
    onOpenAppearance: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()
    val colorScheme = MiuixTheme.colorScheme
    val enableBlur = com.demo.themeswitch.ui.theme.LocalEnableBlur.current
    val backdrop = com.demo.themeswitch.util.rememberBlurBackdrop(enableBlur)
    val barColor = if (backdrop != null) androidx.compose.ui.graphics.Color.Transparent
    else colorScheme.surface

    val uiModeItems = listOf(
        stringResource(R.string.mode_miuix),
        stringResource(R.string.mode_material),
    )
    val languageItems = com.demo.themeswitch.data.LocaleHelper.supportedLanguages.map {
        if (it.code == "system") stringResource(R.string.lang_system) else it.nativeName
    }
    val languageIndex = com.demo.themeswitch.data.LocaleHelper.supportedLanguages
        .indexOfFirst { it.code == preferences.language }
        .coerceIn(0, com.demo.themeswitch.data.LocaleHelper.supportedLanguages.lastIndex)

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            com.demo.themeswitch.util.BlurredBar(backdrop) {
                TopAppBar(
                    title = stringResource(R.string.nav_settings),
                    color = barColor,
                    scrollBehavior = scrollBehavior,
                )
            }
        },
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier,
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 12.dp,
                    bottom = LocalScaffoldBottomPadding.current + 12.dp,
                ),
            ) {
            // 界面卡：风格下拉 + 主题入口
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    OverlayDropdownPreference(
                        title = stringResource(R.string.settings_ui_mode),
                        summary = stringResource(R.string.settings_ui_mode_summary),
                        items = uiModeItems,
                        startAction = {
                            Icon(
                                imageVector = Icons.Rounded.DisplaySettings,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                        },
                        selectedIndex = if (preferences.uiMode == UiMode.Miuix.value) 0 else 1,
                        onSelectedIndexChange = { index ->
                            scope.launch {
                                repository.setUiMode(
                                    if (index == 0) UiMode.Miuix.value else UiMode.Material.value,
                                )
                            }
                        },
                    )
                    ArrowPreference(
                        title = stringResource(R.string.settings_theme),
                        summary = when (com.demo.themeswitch.ui.theme.ColorMode.fromValue(preferences.colorMode)) {
                            com.demo.themeswitch.ui.theme.ColorMode.LIGHT,
                            com.demo.themeswitch.ui.theme.ColorMode.MONET_LIGHT -> stringResource(R.string.theme_light)
                            com.demo.themeswitch.ui.theme.ColorMode.DARK,
                            com.demo.themeswitch.ui.theme.ColorMode.MONET_DARK,
                            com.demo.themeswitch.ui.theme.ColorMode.DARK_AMOLED -> stringResource(R.string.theme_dark)
                            else -> stringResource(R.string.theme_system)
                        },
                        startAction = {
                            Icon(
                                imageVector = Icons.Rounded.Palette,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                        },
                        onClick = onOpenAppearance,
                    )
                }
            }
            // 通用卡：语言
            item {
                Card(modifier = Modifier.padding(top = 12.dp).fillMaxWidth()) {
                    OverlayDropdownPreference(
                        title = stringResource(R.string.settings_language),
                        summary = stringResource(R.string.settings_language_summary),
                        items = languageItems,
                        startAction = {
                            Icon(
                                imageVector = Icons.Rounded.Language,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                        },
                        selectedIndex = languageIndex,
                        onSelectedIndexChange = { index ->
                            val lang = com.demo.themeswitch.data.LocaleHelper.supportedLanguages.getOrNull(index)
                            if (lang != null && lang.code != preferences.language) {
                                scope.launch { repository.setLanguage(lang.code) }
                            }
                        },
                    )
                }
            }
            // 关于卡
            item {
                Card(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth()) {
                    ArrowPreference(
                        title = stringResource(R.string.nav_about),
                        startAction = {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                        },
                        onClick = onOpenAbout,
                    )
                }
            }
        }
        }
    }
}
