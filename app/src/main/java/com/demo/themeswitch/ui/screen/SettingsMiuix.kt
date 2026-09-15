package com.demo.themeswitch.ui.screen

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Style
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.LocaleHelper
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.UiMode
import com.demo.themeswitch.ui.component.MiuixLanguageDialog
import com.demo.themeswitch.ui.component.MiuixThemeModeDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun SettingsMiuixScreen() {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()
    val colorScheme = MiuixTheme.colorScheme

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.nav_settings),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
            ),
        ) {
            item {
                Text(
                    text = stringResource(R.string.settings_section_appearance),
                    color = colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp),
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                ) {
                    OverlayDropdownPreference(
                        title = stringResource(R.string.settings_ui_mode),
                        summary = if (preferences.uiMode == UiMode.Miuix.value) {
                            stringResource(R.string.mode_miuix)
                        } else {
                            stringResource(R.string.mode_material)
                        },
                        startAction = {
                            Icon(
                                imageVector = Icons.Filled.Style,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                        },
                        items = listOf(
                            stringResource(R.string.mode_miuix),
                            stringResource(R.string.mode_material),
                        ),
                        selectedIndex = if (preferences.uiMode == UiMode.Miuix.value) 0 else 1,
                        onSelectedIndexChange = { index ->
                            val mode = if (index == 0) UiMode.Miuix.value else UiMode.Material.value
                            scope.launch {
                                // 闪退修复：OverlayDropdownPreference 选中项时会先播放弹层
                                // 收起动画，此刻立即 recreate 会与 Miuix 弹层窗口销毁竞争。
                                // 先等弹层完全收起，再写入 UI 模式并重建 Activity，
                                // Miuix→M3 切换不再闪退。
                                delay(500)
                                repository.setUiMode(mode)
                                (context as? Activity)?.recreate()
                            }
                        },
                    )
                    ArrowPreference(
                        title = stringResource(R.string.settings_theme),
                        summary = when (preferences.themeMode) {
                            1 -> stringResource(R.string.theme_light)
                            2 -> stringResource(R.string.theme_dark)
                            else -> stringResource(R.string.theme_system)
                        },
                        startAction = {
                            Icon(
                                imageVector = Icons.Filled.Palette,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                        },
                        onClick = { showThemeDialog = true },
                    )
                    SwitchPreference(
                        title = stringResource(R.string.settings_monet),
                        summary = stringResource(R.string.settings_monet_summary),
                        startAction = {
                            Icon(
                                imageVector = Icons.Filled.Colorize,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                        },
                        checked = preferences.isMiuixMonet,
                        onCheckedChange = { enabled ->
                            scope.launch { repository.setMiuixMonet(enabled) }
                        },
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.settings_section_general),
                    color = colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp),
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                ) {
                    ArrowPreference(
                        title = stringResource(R.string.settings_language),
                        summary = LocaleHelper.supportedLanguages
                            .find { it.code == preferences.language }?.nativeName
                            ?: stringResource(R.string.lang_system),
                        startAction = {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                        },
                        onClick = { showLanguageDialog = true },
                    )
                }
            }
        }
    }

    // 语言弹窗：Miuix 版（原先误用了 Material 侧的 M3 AlertDialog）
    MiuixLanguageDialog(
        show = showLanguageDialog,
        currentLanguage = preferences.language,
        onDismiss = { showLanguageDialog = false },
        onSelect = { code ->
            // Persist synchronously first so attachBaseContext picks it up on recreate.
            LocaleHelper.persistLanguage(context, code)
            scope.launch {
                repository.setLanguage(code)
                showLanguageDialog = false
                // 等弹窗收起动画结束后再重建，避免窗口销毁竞争
                delay(400)
                (context as? Activity)?.recreate()
            }
        },
    )

    // 主题弹窗：Miuix 版
    MiuixThemeModeDialog(
        show = showThemeDialog,
        currentMode = preferences.themeMode,
        onDismiss = { showThemeDialog = false },
        onSelect = { mode ->
            showThemeDialog = false
            scope.launch { repository.setThemeMode(mode) }
        },
    )
}
