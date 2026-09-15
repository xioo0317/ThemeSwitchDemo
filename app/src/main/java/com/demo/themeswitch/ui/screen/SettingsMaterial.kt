package com.demo.themeswitch.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.demo.themeswitch.BuildConfig
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.LocaleHelper
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.component.MiuixOptionDialog
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import kotlinx.coroutines.launch

@Composable
fun SettingsMaterialScreen(
    onOpenAppearance: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()

    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }
    var showUiModeDialog by rememberSaveable { mutableStateOf(false) }

    // M3 模式也用 MIUI 大标题顶栏（颜色跟 M3 主题协调），内容保持 M3 卡片分组
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.nav_settings),
                color = MaterialTheme.colorScheme.surface,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = LocalScaffoldBottomPadding.current + 12.dp,
            ),
        ) {
            // 外观卡：UI 模式 / 主题（跳外观页）
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    SettingsItem(
                        icon = Icons.Filled.Style,
                        title = stringResource(R.string.settings_ui_mode),
                        subtitle = stringResource(R.string.settings_ui_mode_summary),
                        onClick = { showUiModeDialog = true },
                    )
                    CardDivider()
                    SettingsItem(
                        icon = Icons.Filled.Colorize,
                        title = stringResource(R.string.settings_theme),
                        subtitle = stringResource(R.string.settings_theme_summary),
                        onClick = onOpenAppearance,
                        chevron = true,
                    )
                }
            }

            // 通用卡：语言
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    SettingsItem(
                        icon = Icons.Filled.Language,
                        title = stringResource(R.string.settings_language),
                        subtitle = stringResource(R.string.settings_language_summary),
                        onClick = { showLanguageDialog = true },
                    )
                }
            }

            // 关于卡：一行入口，跳转关于页
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    SettingsItem(
                        icon = Icons.Filled.Info,
                        title = stringResource(R.string.nav_about),
                        subtitle = "v" + BuildConfig.VERSION_NAME,
                        onClick = onOpenAbout,
                        chevron = true,
                    )
                }
            }
        }
    }

    // MIUI 风格弹窗（M3 模式同样使用，miuix OverlayDialog 组合层渲染）。树内切换，不 recreate
    MiuixOptionDialog(
        show = showUiModeDialog,
        title = stringResource(R.string.settings_ui_mode),
        options = listOf(
            stringResource(R.string.mode_miuix),
            stringResource(R.string.mode_material),
        ),
        selectedIndex = if (preferences.uiMode == "miuix") 0 else 1,
        onSelect = { index ->
            scope.launch {
                repository.setUiMode(if (index == 0) "miuix" else "material")
            }
        },
        onDismiss = { showUiModeDialog = false },
    )
    MiuixOptionDialog(
        show = showLanguageDialog,
        title = stringResource(R.string.settings_language),
        options = LocaleHelper.supportedLanguages.map { it.nativeName },
        selectedIndex = LocaleHelper.supportedLanguages
            .indexOfFirst { it.code == preferences.language }
            .coerceIn(0, LocaleHelper.supportedLanguages.lastIndex),
        onSelect = { index ->
            val lang = LocaleHelper.supportedLanguages.getOrNull(index)
            if (lang != null && lang.code != preferences.language) {
                scope.launch { repository.setLanguage(lang.code) }
            }
        },
        onDismiss = { showLanguageDialog = false },
    )
}

/** 卡片内行分隔线 */
@Composable
private fun CardDivider() {
    androidx.compose.material3.HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    chevron: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (chevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
