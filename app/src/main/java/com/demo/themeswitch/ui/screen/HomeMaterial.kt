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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
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
import com.demo.themeswitch.ui.component.material.ExpressiveScaffold
import com.demo.themeswitch.ui.component.material.SegmentedColumn
import com.demo.themeswitch.ui.component.material.SegmentedListItem
import com.demo.themeswitch.ui.component.material.expressiveTopAppBarColors

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

    // KSU 同款：Expressive Scaffold + 可折叠大标题
    ExpressiveScaffold(
        topBar = { HomeTopBar(scrollBehavior = scrollBehavior) },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
        ) {
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

@Composable
private fun HomeTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    LargeFlexibleTopAppBar(
        title = { Text(stringResource(R.string.nav_home)) },
        colors = expressiveTopAppBarColors(),
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior,
    )
}

/**
 * KSU 同款状态卡：
 * - 工作中：secondaryContainer 底 + 最左 CheckCircle
 * - 未工作：errorContainer 底 + 最左 Warning（感叹号）
 */
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
            supportingContent = {
                Text(
                    text = statusSummary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            verticalAlignment = Alignment.CenterVertically,
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = contentColor,
                leadingContentColor = contentColor,
                trailingContentColor = contentColor,
                supportingContentColor = contentColor.copy(alpha = 0.7f),
            ),
            headlineContent = {
                Text(
                    text = statusTitle,
                    style = MaterialTheme.typography.titleMediumEmphasized,
                )
            },
        )
    }
}

/**
 * KSU 同款信息卡：SegmentedColumn 分组列表，
 * 行与行之间的圆角/分隔由 M3 Expressive SegmentedListItem 统一处理。
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
            InfoEntry(
                icon = Icons.Filled.Widgets,
                label = stringResource(R.string.card_app_version),
                content = appVersion,
            )
        }
        item {
            InfoEntry(
                icon = Icons.Filled.DevicesOther,
                label = stringResource(R.string.card_device),
                content = deviceModel,
            )
        }
        item {
            InfoEntry(
                icon = Icons.Filled.Android,
                label = stringResource(R.string.card_android),
                content = androidVersion,
            )
        }
        item {
            InfoEntry(
                icon = Icons.Filled.Memory,
                label = stringResource(R.string.card_kernel),
                content = kernelVersion,
            )
        }
    }
}

@Composable
private fun InfoEntry(
    icon: ImageVector,
    label: String,
    content: String,
) {
    SegmentedListItem(
        headlineContent = {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
        },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = label)
        },
        supportingContent = {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    )
}
