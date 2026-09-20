package com.demo.themeswitch.ui.screen

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun HomeMaterialScreen() {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scrollBehavior = MiuixScrollBehavior()
    val colorScheme = MaterialTheme.colorScheme

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

    // 状态卡三态配色（Material 风格）：检测中中性灰、在线绿、离线柔和红
    val dark = isInDarkTheme()
    val cardColor = when {
        probing -> colorScheme.surfaceContainerHigh
        online -> if (dark) Color(0xFF1A3825) else Color(0xFFDFFAE4)
        // 优化：降低离线卡片饱和度
        else -> if (dark) Color(0xFF2A2223) else Color(0xFFF0EAEA)
    }
    val accentColor = when {
        probing -> colorScheme.onSurfaceVariant
        online -> Color(0xFF36D167)
        // 优化：离线图标颜色更柔和
        else -> Color(0xFFB07070)
    }

    Scaffold(
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
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = LocalScaffoldBottomPadding.current + 16.dp,
            ),
        ) {
            item {
                // KSU 同款大状态卡：右下大图标、左上标题/副标题、底部服务器地址
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                ) {
                    Box(modifier = Modifier.height(160.dp)) {
                        // 右下大图标
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(x = 27.dp, y = 31.dp),
                            contentAlignment = Alignment.BottomEnd,
                        ) {
                            Icon(
                                imageVector = when {
                                    probing -> Icons.Rounded.HourglassTop
                                    online -> Icons.Rounded.CheckCircleOutline
                                    else -> Icons.Rounded.Block
                                },
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(110.dp),
                            )
                        }
                        // 底部服务器地址
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp, 10.dp),
                            contentAlignment = Alignment.BottomStart,
                        ) {
                            Text(
                                text = preferences.serverUrl,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface,
                            )
                        }
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
                                    color = colorScheme.onSurface,
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = when {
                                        probing -> stringResource(R.string.home_probing_hint)
                                        online -> stringResource(R.string.home_latency_ms, latencyMs)
                                        else -> preferences.serverUrl
                                    },
                                    fontSize = 15.sp,
                                    color = colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.section_info),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp),
                )
            }

            item {
                // 信息卡片：所有行放在一个大卡片里
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainer),
                ) {
                    Column {
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
}

@Composable
private fun MaterialInfoItem(icon: ImageVector, titleResId: Int, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(titleResId),
            color = colorScheme.onSurfaceVariant,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
