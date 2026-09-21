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
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.BuildConfig
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.BackendMonitor
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.theme.isInDarkTheme
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun HomeMiuixScreen() {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scrollBehavior = MiuixScrollBehavior()
    val colorScheme = MiuixTheme.colorScheme

    // 每次进首页探测一次后端状态，只探测一次，不循环重试
    var online by remember { mutableStateOf(false) }
    var latencyMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(preferences.serverUrl) {
        val result = BackendMonitor.refresh(preferences.serverUrl)
        online = result.online
        latencyMs = result.latencyMs
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
                    StatusCard(
                        online = online,
                        latencyMs = latencyMs,
                    )
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
 * KSU 同款状态卡片：
 * - 工作中：浅绿色底色 + 右下角大对勾
 * - 未工作：纯白底色 + 左侧感叹号 + 右侧双行文字（完全复刻 KSU 未安装样式）
 */
@Composable
private fun StatusCard(
    online: Boolean,
    latencyMs: Long,
) {
    val colorScheme = MiuixTheme.colorScheme
    val dark = isInDarkTheme()

    val cardColor = if (online) {
        if (dark) Color(0xFF1A3825) else Color(0xFFDFFAE4)
    } else {
        colorScheme.surfaceContainer
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.defaultColors(color = cardColor),
    ) {
        if (online) {
            // 工作中：右下角大图标布局
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(27.dp, 31.dp),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    Icon(
                        modifier = Modifier.size(110.dp),
                        imageVector = Icons.Rounded.CheckCircleOutline,
                        tint = Color(0xFF36D167),
                        contentDescription = null,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp, 14.dp),
                    contentAlignment = Alignment.TopStart,
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.home_working),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(1.dp))
                        Text(
                            text = stringResource(R.string.home_latency_ms, latencyMs),
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        } else {
            // 未工作：完全复刻 KSU 未安装样式 - 左图标 + 右侧双行文字
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.ErrorOutline,
                    contentDescription = null,
                    tint = colorScheme.onSurface,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.home_not_working),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.home_not_working_hint),
                        fontSize = 14.sp,
                        color = colorScheme.onSurfaceVariantSummary,
                    )
                }
            }
        }
    }
}

/**
 * KSU InfoCard 同款：图标 + 标题(上) + 内容(下) 两行布局
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
