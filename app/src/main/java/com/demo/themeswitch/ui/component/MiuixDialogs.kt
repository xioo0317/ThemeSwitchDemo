package com.demo.themeswitch.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.R
import com.demo.themeswitch.data.LocaleHelper
import top.yukonga.miuix.kmp.basic.RadioButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowDialog

/**
 * MIUI 风格弹窗，基于 miuix WindowDialog 实现（参照 KernelSU 双主题对话框拆分模式）。
 *
 * 与 Material 侧的 M3 AlertDialog（SettingsMaterial.kt，已改为 private）严格分离：
 * Miuix 模式下不再借用 M3 弹窗，保证两种 UI 模式各自的视觉一致性。
 */

@Composable
fun MiuixLanguageDialog(
    show: Boolean,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
) {
    WindowDialog(
        show = show,
        title = stringResource(R.string.settings_language),
        onDismissRequest = onDismiss,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 420.dp),
        ) {
            items(LocaleHelper.supportedLanguages) { lang ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(lang.code) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = currentLanguage == lang.code,
                        onClick = { onSelect(lang.code) },
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = lang.nativeName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = lang.displayName,
                            fontSize = 13.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiuixThemeModeDialog(
    show: Boolean,
    currentMode: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    val modes = listOf(
        0 to stringResource(R.string.theme_system),
        1 to stringResource(R.string.theme_light),
        2 to stringResource(R.string.theme_dark),
    )
    WindowDialog(
        show = show,
        title = stringResource(R.string.settings_theme),
        onDismissRequest = onDismiss,
    ) {
        Column {
            modes.forEach { (mode, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(mode) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = currentMode == mode,
                        onClick = { onSelect(mode) },
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = label, fontSize = 16.sp)
                }
            }
        }
    }
}
