package com.demo.themeswitch.ui.screen

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.HourglassTop
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.BuildConfig
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.BackendMonitor
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.theme.isInDarkTheme
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.PressFeedbackType
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun HomeMiuixScreen() {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scrollBehavior = MiuixScrollBehavior()
    val colorScheme = MiuixTheme.colorScheme

    // 连通性探测：进页自动探测，离线 8s 自动重试，不再手动点击刷新
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.nav_home),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = LocalScaffoldBottomPadding.current + 12.dp,
            ),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // KSU StatusCard 完全照搬
                    StatusCard(
                        probing = probing,
                        online = online,
                        latencyMs = latencyMs,
                        serverUrl = preferences.serverUrl,
                    )
                    // KSU InfoCard 完全照搬
                    InfoCard(
                        appVersion = "v" + BuildConfig.VERSION_NAME,
                        deviceModel = Build.MODEL,
                        androidVersion = "Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")",
                        kernelVersion = System.getProperty("os.version") ?: "—",
                    )
                }
            }
        }
    }
}

/**
 * KSU StatusCard 完全照搬：
 * - 右下大图标 110dp
 * - 左下底部文字
 * - 左上标题+副标题
 * - 按压倾斜反馈
 */
@Composable
private fun StatusCard(
    probing: Boolean,
    online: Boolean,
    latencyMs: Long,
    serverUrl: String,
) {
    val colorScheme = MiuixTheme.colorScheme
    val dark = isInDarkTheme()

    val cardColor = when {
        probing -> colorScheme.surfaceContainer
        online -> if (dark) Color(0xFF1A3825) else Color(0xFFDFFAE4)
        else -> if (dark) Color(0xFF2A2223) else Color(0xFFF0EAEA)
    }
    val iconTint = when {
        probing -> colorScheme.primary
        online -> Color(0xFF36D167)
        else -> Color(0xFFB07070)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.defaultColors(color = cardColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            // 右下大图标
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(27.dp, 31.dp),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Icon(
                    modifier = Modifier.size(110.dp),
                    imageVector = when {
                        probing -> Icons.Rounded.HourglassTop
                        online -> Icons.Rounded.CheckCircleOutline
                        else -> Icons.Rounded.Block
                    },
                    tint = iconTint,
                    contentDescription = null,
                )
            }
            // 左下底部文字 - 已移除，只保留状态和图标
            // 左上标题 + 副标题
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 14.dp),
                contentAlignment = Alignment.TopStart,
            ) {
                Column {
                    Text(
                        text = stringResource(
                            when {
                                probing -> R.string.home_probing
                                online -> R.string.home_working
                                else -> R.string.home_not_working
                            },
                        ),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = when {
                            probing -> stringResource(R.string.home_probing_hint)
                            online -> stringResource(R.string.home_latency_ms, latencyMs)
                            else -> ""
                        },
                        fontSize = 15.sp,
                    )
                }
            }
        }
    }
}

/**
 * KSU InfoCard 完全照搬：
 * - 图标 + 标题(上) + 内容(下) 两行布局
 * - 卡片内边距 16dp
 * - 行间距 24dp
 */
@Composable
private fun InfoCardItem(
    icon: ImageVector,
    title: String,
    content: String,
    bottomPadding: androidx.compose.ui.unit.Dp = 24.dp,
) {
    val colorScheme = MiuixTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = bottomPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp),
            tint = colorScheme.onSurface,
        )
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface,
            )
            Text(
                text = content,
                fontSize = 14.sp,
                color = colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(top = 2.dp),
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            InfoCardItem(
                icon = Icons.Filled.Widgets,
                title = stringResource(R.string.card_app_version),
                content = appVersion,
            )
            InfoCardItem(
                icon = Icons.Filled.DevicesOther,
                title = stringResource(R.string.card_device),
                content = deviceModel,
            )
            InfoCardItem(
                icon = Icons.Filled.Android,
                title = stringResource(R.string.card_android),
                content = androidVersion,
            )
            InfoCardItem(
                icon = Icons.Filled.Memory,
                title = stringResource(R.string.card_kernel),
                content = kernelVersion,
                bottomPadding = 0.dp,
            )
        }
    }
}
