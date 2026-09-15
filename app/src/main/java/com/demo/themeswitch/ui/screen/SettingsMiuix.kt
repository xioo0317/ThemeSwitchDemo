package com.demo.themeswitch.ui.screen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.MainActivity
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.LocaleHelper
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.UiMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

// 等下拉弹层收起动画完全结束后再重启，避免窗口销毁竞争导致闪退
private const val RESTART_DELAY_MS = 600L

private tailrec fun Context.findActivity(): Activity? {
    var current: Context = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return null
}

// 用 finish + startActivity 完全重建 Activity：
// recreate() 在 Miuix UI 上会与弹层/窗口销毁竞争导致闪退，
// 重启方式走两个独立事务，旧窗口随 finish 正常销毁，稳定得多
private fun restartActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val activity = context.findActivity()
    if (activity != null) {
        activity.startActivity(intent)
        activity.finish()
    } else {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

@Composable
fun SettingsMiuixScreen() {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()
    val colorScheme = MiuixTheme.colorScheme
    val languages = LocaleHelper.supportedLanguages

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
                                // 先等下拉弹层收起动画完全结束，再写入并重启，
                                // 避免 Miuix→Material 切换闪退
                                delay(RESTART_DELAY_MS)
                                repository.setUiMode(mode)
                                restartActivity(context)
                            }
                        },
                    )
                    OverlayDropdownPreference(
                        title = stringResource(R.string.settings_theme),
                        startAction = {
                            Icon(
                                imageVector = Icons.Filled.Palette,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                        },
                        items = listOf(
                            stringResource(R.string.theme_system),
                            stringResource(R.string.theme_light),
                            stringResource(R.string.theme_dark),
                        ),
                        selectedIndex = preferences.themeMode.coerceIn(0, 2),
                        onSelectedIndexChange = { mode ->
                            // 主题模式由 AppTheme 响应式更新，无需重建
                            scope.launch { repository.setThemeMode(mode) }
                        },
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
                    OverlayDropdownPreference(
                        title = stringResource(R.string.settings_language),
                        startAction = {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = null,
                                tint = colorScheme.onBackground,
                                modifier = Modifier.padding(end = 6.dp),
                            )
                        },
                        items = languages.map { it.nativeName },
                        selectedIndex = languages
                            .indexOfFirst { it.code == preferences.language }
                            .coerceIn(0, languages.lastIndex),
                        maxHeight = 420.dp,
                        onSelectedIndexChange = { index ->
                            val lang = languages.getOrNull(index)
                            if (lang != null) {
                                // 先同步写 SharedPreferences，保证重启后 attachBaseContext 能读到新语言
                                LocaleHelper.persistLanguage(context, lang.code)
                                scope.launch {
                                    delay(RESTART_DELAY_MS)
                                    repository.setLanguage(lang.code)
                                    restartActivity(context)
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
