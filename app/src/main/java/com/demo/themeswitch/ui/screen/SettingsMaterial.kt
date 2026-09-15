package com.demo.themeswitch.ui.screen

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.LocaleHelper
import com.demo.themeswitch.data.SettingsRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMaterialScreen() {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showUiModeDialog by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_settings)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }

        item {
            Text(
                text = stringResource(R.string.settings_section_appearance),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
            )
        }

        // UI Mode switch
        item {
            SettingsItem(
                icon = Icons.Filled.Style,
                title = stringResource(R.string.settings_ui_mode),
                subtitle = if (preferences.uiMode == "miuix") stringResource(R.string.mode_miuix) else stringResource(R.string.mode_material),
                onClick = { showUiModeDialog = true },
            )
        }

        // Theme mode
        item {
            SettingsItem(
                icon = Icons.Filled.Palette,
                title = stringResource(R.string.settings_theme),
                subtitle = when (preferences.themeMode) {
                    0 -> stringResource(R.string.theme_system)
                    1 -> stringResource(R.string.theme_light)
                    2 -> stringResource(R.string.theme_dark)
                    else -> stringResource(R.string.theme_system)
                },
                onClick = { showThemeDialog = true },
            )
        }

        // Language
        item {
            SettingsItem(
                icon = Icons.Filled.Language,
                title = stringResource(R.string.settings_language),
                subtitle = LocaleHelper.supportedLanguages.find { it.code == preferences.language }?.nativeName
                    ?: stringResource(R.string.lang_system),
                onClick = { showLanguageDialog = true },
            )
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            Text(
                text = stringResource(R.string.settings_section_general),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
            )
        }

        item {
            SettingsToggleItem(
                icon = Icons.Filled.Palette,
                title = stringResource(R.string.settings_monet),
                subtitle = stringResource(R.string.settings_monet_summary),
                checked = preferences.isMiuixMonet,
                onCheckedChange = { enabled ->
                    scope.launch { repository.setMiuixMonet(enabled) }
                },
            )
        }
    }

    // Language selection dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = preferences.language,
            onDismiss = { showLanguageDialog = false },
            onSelect = { code ->
                // Persist synchronously first so attachBaseContext picks it up on recreate.
                LocaleHelper.persistLanguage(context, code)
                scope.launch { repository.setLanguage(code) }
                showLanguageDialog = false
                (context as? Activity)?.recreate()
            },
        )
    }

    // Theme mode dialog
    if (showThemeDialog) {
        ThemeModeDialog(
            currentMode = preferences.themeMode,
            onDismiss = { showThemeDialog = false },
            onSelect = { mode ->
                scope.launch {
                    repository.setThemeMode(mode)
                    showThemeDialog = false
                }
            },
        )
    }

    // UI Mode dialog
    if (showUiModeDialog) {
        UiModeDialog(
            currentMode = preferences.uiMode,
            onDismiss = { showUiModeDialog = false },
            onSelect = { mode ->
                scope.launch {
                    repository.setUiMode(mode)
                    showUiModeDialog = false
                    // recreate 而非树内切换：避免 NavHost 销毁重建导致的崩溃
                    (context as? Activity)?.recreate()
                }
            },
        )
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
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
    }
}

@Composable
private fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// 以下三个 M3 弹窗仅限 Material 模式使用（private），
// 防止再被 MIUI 模式借用；Miuix 版弹窗见 ui/component/MiuixDialogs.kt
@Composable
private fun LanguageSelectionDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_language)) },
        text = {
            LazyColumn {
                items(LocaleHelper.supportedLanguages) { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(lang.code) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = currentLanguage == lang.code,
                            onClick = { onSelect(lang.code) },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = lang.nativeName, fontWeight = FontWeight.Medium)
                            Text(
                                text = lang.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
    )
}

@Composable
private fun ThemeModeDialog(
    currentMode: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    val modes = listOf(
        0 to stringResource(R.string.theme_system),
        1 to stringResource(R.string.theme_light),
        2 to stringResource(R.string.theme_dark),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_theme)) },
        text = {
            Column {
                modes.forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(mode) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = currentMode == mode,
                            onClick = { onSelect(mode) },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
    )
}

@Composable
private fun UiModeDialog(
    currentMode: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    val modes = listOf(
        "material" to stringResource(R.string.mode_material),
        "miuix" to stringResource(R.string.mode_miuix),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_ui_mode)) },
        text = {
            Column {
                modes.forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(mode) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = currentMode == mode,
                            onClick = { onSelect(mode) },
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
    )
}
