package com.demo.themeswitch.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.LocaleHelper
import com.demo.themeswitch.data.SettingsRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsMiuixScreen() {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showUiModeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.nav_settings),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp, top = 8.dp),
        )

        // Appearance section - MIUI style grouped card
        MiuixSectionHeader(stringResource(R.string.settings_section_appearance))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            MiuixSettingsItem(
                icon = Icons.Filled.Style,
                title = stringResource(R.string.settings_ui_mode),
                value = if (preferences.uiMode == "miuix") stringResource(R.string.mode_miuix) else stringResource(R.string.mode_material),
                onClick = { showUiModeDialog = true },
                showDivider = true,
            )
            MiuixSettingsItem(
                icon = Icons.Filled.Palette,
                title = stringResource(R.string.settings_theme),
                value = when (preferences.themeMode) {
                    0 -> stringResource(R.string.theme_system)
                    1 -> stringResource(R.string.theme_light)
                    2 -> stringResource(R.string.theme_dark)
                    else -> stringResource(R.string.theme_system)
                },
                onClick = { showThemeDialog = true },
                showDivider = true,
            )
            MiuixSettingsItem(
                icon = Icons.Filled.Language,
                title = stringResource(R.string.settings_language),
                value = LocaleHelper.supportedLanguages.find { it.code == preferences.language }?.nativeName
                    ?: stringResource(R.string.lang_system),
                onClick = { showLanguageDialog = true },
                showDivider = false,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        MiuixSectionHeader(stringResource(R.string.settings_section_general))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 16.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_monet),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                    )
                    Text(
                        text = stringResource(R.string.settings_monet_summary),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = preferences.isMiuixMonet,
                    onCheckedChange = { scope.launch { repository.setMiuixMonet(it) } },
                )
            }
        }
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = preferences.language,
            onDismiss = { showLanguageDialog = false },
            onSelect = { code ->
                scope.launch {
                    repository.setLanguage(code)
                    showLanguageDialog = false
                }
            },
        )
    }

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

    if (showUiModeDialog) {
        UiModeDialog(
            currentMode = preferences.uiMode,
            onDismiss = { showUiModeDialog = false },
            onSelect = { mode ->
                scope.launch {
                    repository.setUiMode(mode)
                    showUiModeDialog = false
                }
            },
        )
    }
}

@Composable
private fun MiuixSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 4.dp),
    )
}

@Composable
private fun MiuixSettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    showDivider: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            )
        }
    }
}
