package com.demo.themeswitch.ui.screen

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.demo.themeswitch.BuildConfig
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.BackendMonitor
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.component.material.SegmentedColumn
import com.demo.themeswitch.ui.component.material.SegmentedListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMaterialScreen() {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var online by remember { mutableStateOf(false) }
    var latencyMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(preferences.serverUrl) {
        val result = BackendMonitor.refresh(preferences.serverUrl)
        online = result.online
        latencyMs = result.latencyMs
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(R.string.nav_home)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            StatusCard(online = online, latencyMs = latencyMs)
            InfoCard(
                appVersion = "v" + BuildConfig.VERSION_NAME,
                deviceModel = Build.MODEL,
                androidVersion = "Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")",
                kernelVersion = System.getProperty("os.version") ?: "—",
            )
            Spacer(Modifier.height(LocalScaffoldBottomPadding.current + 16.dp))
        }
    }
}

/**
 * KSU 同款状态卡片（HomeMaterial.kt#StatusCard）：
 * 无论"工作中"还是"未工作"，容器色都不写死，全部取自当前 colorScheme——
 * 而 Material 模式的 colorScheme 由系统壁纸 Monet primary（或自定义种子色）经
 * materialkolor 同一路径派生（见 MaterialAppTheme），因此卡片配色随壁纸/种子联动。
 *
 * - 工作中：secondaryContainer 底 + CheckCircle
 * - 未工作：errorContainer 底（同为 HCT/Monet 派生，非固定死红）+ Warning
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatusCard(online: Boolean, latencyMs: Long) {
    val containerColor = if (online) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = MaterialTheme.colorScheme.contentColorFor(containerColor)
    val statusIcon = if (online) Icons.Rounded.CheckCircle else Icons.Rounded.Warning
    val statusTitle = stringResource(if (online) R.string.home_working else R.string.home_not_working)
    val statusSummary = if (online) {
        stringResource(R.string.home_latency_ms, latencyMs)
    } else {
        stringResource(R.string.home_not_working_hint)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.large,
    ) {
        ListItem(
            leadingContent = {
                Icon(statusIcon, contentDescription = statusTitle)
            },
            headlineContent = {
                Text(statusTitle, style = MaterialTheme.typography.titleMediumEmphasized)
            },
            supportingContent = {
                Text(
                    statusSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.7f),
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = contentColor,
                leadingContentColor = contentColor,
                headlineColor = contentColor,
                supportingColor = contentColor.copy(alpha = 0.7f),
            ),
        )
    }
}

/**
 * KSU 同款信息卡：SegmentedColumn 分段列表，行间圆角/分割感由 M3 Expressive 处理。
 * 调用方式对齐已验证可编译的 SettingsMaterial（显式传入 onClick）。
 */
@Composable
private fun InfoCard(
    appVersion: String,
    deviceModel: String,
    androidVersion: String,
    kernelVersion: String,
) {
    SegmentedColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            InfoEntry(Icons.Filled.Widgets, stringResource(R.string.card_app_version), appVersion)
        }
        item {
            InfoEntry(Icons.Filled.DevicesOther, stringResource(R.string.card_device), deviceModel)
        }
        item {
            InfoEntry(Icons.Filled.Android, stringResource(R.string.card_android), androidVersion)
        }
        item {
            InfoEntry(Icons.Filled.Memory, stringResource(R.string.card_kernel), kernelVersion)
        }
    }
}

@Composable
private fun InfoEntry(
    icon: ImageVector,
    label: String,
    value: String,
) {
    SegmentedListItem(
        onClick = { /* 信息展示项，不可点击 */ },
        leadingContent = {
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        headlineContent = { Text(label, style = MaterialTheme.typography.bodyLarge) },
        supportingContent = {
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    )
}
