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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.demo.themeswitch.BuildConfig
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppConfig
import com.demo.themeswitch.data.BackendMonitor
import com.demo.themeswitch.data.ConfigRepository
import com.demo.themeswitch.ui.component.material.SegmentedColumn
import com.demo.themeswitch.ui.component.material.SegmentedListItem
import com.demo.themeswitch.ui.navigation.LocalNavigator
import com.demo.themeswitch.ui.navigation.Route

/** 工作中状态色：M3 风格绿色（亮/深主题通用，保证在白色卡片上可读） */
private val StatusOnlineColor = androidx.compose.ui.graphics.Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMaterialScreen() {
    val navigator = LocalNavigator.current
    val context = LocalContext.current
    val configRepository = remember { ConfigRepository(context) }
    val config by configRepository.configFlow.collectAsState(initial = AppConfig())
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var online by remember { mutableStateOf(false) }
    var latencyMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(config.backendUrl) {
        val result = BackendMonitor.refresh(config.backendUrl)
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
            StatusCard(
                online = online,
                latencyMs = latencyMs,
                onClick = { navigator.push(Route.ServerAddress) },
            )
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
 * M3 状态卡片：白色分组卡片（surfaceBright），与「应用版本」卡片同一视觉体系。
 * - 工作中：绿色 CheckCircle
 * - 未工作：红色 ErrorOutline
 * 任意状态下整卡可点击，进入服务器地址二级页面；右侧箭头给出明确的跳转暗示。
 */
@Composable
private fun StatusCard(
    online: Boolean,
    latencyMs: Long,
    onClick: () -> Unit,
) {
    val accentColor = if (online) StatusOnlineColor else MaterialTheme.colorScheme.error
    val statusIcon = if (online) Icons.Rounded.CheckCircle else Icons.Rounded.ErrorOutline
    val statusTitle = stringResource(if (online) R.string.home_working else R.string.home_not_working)
    val statusSummary = if (online) {
        stringResource(R.string.home_latency_ms, latencyMs)
    } else {
        stringResource(R.string.home_not_working_hint)
    }

    SegmentedColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            SegmentedListItem(
                onClick = onClick,
                leadingContent = {
                    Icon(statusIcon, contentDescription = statusTitle, tint = accentColor)
                },
                headlineContent = {
                    Text(statusTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                },
                supportingContent = {
                    Text(
                        statusSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingContent = {
                    Icon(
                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
    }
}

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
        onClick = { },
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
