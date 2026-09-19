package com.demo.themeswitch.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.demo.themeswitch.BuildConfig
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.BackendMonitor
import com.demo.themeswitch.data.SettingsRepository
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar

@Composable
fun HomeMaterialScreen() {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scrollBehavior = MiuixScrollBehavior()

    // 连通性探测：进页自动探测，离线时每 8 秒自动重试，点击卡片立即重探
    var probing by rememberSaveable { mutableStateOf(true) }
    var online by rememberSaveable { mutableStateOf(false) }
    var latencyMs by remember { mutableLongStateOf(0L) }
    var probeTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(preferences.serverUrl, probeTick) {
        while (true) {
            probing = true
            val result = BackendMonitor.refresh(preferences.serverUrl)
            probing = false
            online = result.online
            latencyMs = result.latencyMs
            if (result.online) break
            delay(8000)
        }
    }

    Scaffold(
        // 外层 MainScreen Scaffold 统一处理窗口 insets，页面只管顶栏
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.nav_home),
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
                top = innerPadding.calculateTopPadding(),
                bottom = LocalScaffoldBottomPadding.current + 16.dp,
            ),
        ) {
            item {
                // KSU StatusCard 同款状态映射：
                // 检测中→中性容器；在线→secondaryContainer + CheckCircle + 延迟徽章；
                // 离线→errorContainer + Block，点击卡片重新探测
                val containerColor = when {
                    probing -> MaterialTheme.colorScheme.surfaceContainerHigh
                    online -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.errorContainer
                }
                val contentColor = when {
                    probing -> MaterialTheme.colorScheme.onSurfaceVariant
                    online -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onErrorContainer
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .clickable { probeTick++ },
                    shape = RoundedCornerShape(24.dp),
                    color = containerColor,
                ) {
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = containerColor,
                            headlineColor = contentColor,
                            supportingColor = contentColor,
                            leadingIconColor = contentColor,
                            trailingIconColor = contentColor,
                        ),
                        leadingContent = {
                            Icon(
                                imageVector = when {
                                    probing -> Icons.Rounded.HourglassTop
                                    online -> Icons.Rounded.CheckCircle
                                    else -> Icons.Rounded.Block
                                },
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                            )
                        },
                        headlineContent = {
                            Text(
                                text = stringResource(
                                    when {
                                        probing -> R.string.home_probing
                                        online -> R.string.home_working
                                        else -> R.string.home_not_working
                                    },
                                ),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        supportingContent = {
                            Text(
                                text = when {
                                    probing -> stringResource(R.string.home_probing_hint)
                                    online -> preferences.serverUrl
                                    else -> stringResource(R.string.home_tap_to_retry)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        },
                        trailingContent = {
                            if (!probing && online) {
                                // KSU StatusTag 同款小徽章：显示探测延迟
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                ) {
                                    Text(
                                        text = stringResource(R.string.home_latency_ms, latencyMs),
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    )
                                }
                            }
                        },
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.section_info),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp),
                )
            }

            item {
                // KSU SegmentedColumn 同款：独立小圆角行 + 2dp 间隔，真实设备信息
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    MaterialInfoItem(Icons.Filled.Widgets, R.string.card_app_version, "v" + BuildConfig.VERSION_NAME)
                    MaterialInfoItem(Icons.Filled.DevicesOther, R.string.card_device, Build.MODEL)
                    MaterialInfoItem(
                        Icons.Filled.Android,
                        R.string.card_android,
                        "Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")",
                    )
                    MaterialInfoItem(Icons.Filled.Memory, R.string.card_kernel, System.getProperty("os.version") ?: "—")
                    MaterialInfoItem(Icons.Filled.Dns, R.string.card_server, preferences.serverUrl)
                }
            }
        }
    }
}

/** KSU InfoCardItem 同款：独立圆角行 + leading 图标 + 标题 + 值 */
@Composable
private fun MaterialInfoItem(icon: ImageVector, titleResId: Int, value: String) {
    ListItem(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        headlineContent = {
            Text(stringResource(titleResId), style = MaterialTheme.typography.bodyLarge)
        },
        trailingContent = {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
    )
}
