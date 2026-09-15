package com.demo.themeswitch.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.demo.themeswitch.ui.component.MiuixOptionDialog
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
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
    val languages = LocaleHelper.supportedLanguages

    var showUiModeDialog by rememberSaveable { mutableStateOf(false) }
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        // 外层 MainScreen Scaffold 统一处理窗口 insets，页面只管顶栏
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                bottom = LocalScaffoldBottomPadding.current + 16.dp,
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
                    MiuixSelectRow(
                        title = stringResource(R.string.settings_ui_mode),
                        valueText = if (preferences.uiMode == UiMode.Miuix.value) {
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
                        showDivider = true,
                        onClick = { showUiModeDialog = true },
                    )
                    MiuixSelectRow(
                        title = stringResource(R.string.settings_theme),
                        valueText = when (preferences.themeMode) {
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
                    MiuixSelectRow(
                        title = stringResource(R.string.settings_language),
                        valueText = languages
                            .find { it.code == preferences.language }
                            ?.nativeName
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

    // MIUI 弹窗（OverlayDialog，组合层渲染，无独立窗口）。
    // UI 模式与语言均为树内切换：setUiMode 直接换 CompositionLocal，
    // setLanguage 换 LocalContext，都停在当前页面，不再重启 Activity
    MiuixOptionDialog(
        show = showUiModeDialog,
        title = stringResource(R.string.settings_ui_mode),
        options = listOf(
            stringResource(R.string.mode_miuix),
            stringResource(R.string.mode_material),
        ),
        selectedIndex = if (preferences.uiMode == UiMode.Miuix.value) 0 else 1,
        onSelect = { index ->
            scope.launch {
                repository.setUiMode(if (index == 0) UiMode.Miuix.value else UiMode.Material.value)
            }
        },
        onDismiss = { showUiModeDialog = false },
    )
    MiuixOptionDialog(
        show = showThemeDialog,
        title = stringResource(R.string.settings_theme),
        options = listOf(
            stringResource(R.string.theme_system),
            stringResource(R.string.theme_light),
            stringResource(R.string.theme_dark),
        ),
        selectedIndex = preferences.themeMode.coerceIn(0, 2),
        onSelect = { mode ->
            scope.launch { repository.setThemeMode(mode) }
        },
        onDismiss = { showThemeDialog = false },
    )
    MiuixOptionDialog(
        show = showLanguageDialog,
        title = stringResource(R.string.settings_language),
        options = languages.map { it.nativeName },
        selectedIndex = languages
            .indexOfFirst { it.code == preferences.language }
            .coerceIn(0, languages.lastIndex),
        onSelect = { index ->
            val lang = languages.getOrNull(index)
            if (lang != null && lang.code != preferences.language) {
                // setLanguage 内部已同步 persistLanguage，冷启动 attachBaseContext 可读
                scope.launch { repository.setLanguage(lang.code) }
            }
        },
        onDismiss = { showLanguageDialog = false },
    )
}

/**
 * 选择行：点击弹 MIUI 弹窗（OverlayDialog）。行本身不再内联展开，
 * 也不再有重启逻辑。
 */
@Composable
private fun MiuixSelectRow(
    title: String,
    valueText: String,
    onClick: () -> Unit,
    startAction: (@Composable () -> Unit)? = null,
    showDivider: Boolean = false,
) {
    val colorScheme = MiuixTheme.colorScheme
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            startAction?.invoke()
            Text(
                text = title,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = valueText,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariantSummary,
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.size(24.dp),
            )
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp)
                    .height(0.5.dp)
                    .background(colorScheme.onSurfaceVariantSummary.copy(alpha = 0.25f)),
            )
        }
    }
}
