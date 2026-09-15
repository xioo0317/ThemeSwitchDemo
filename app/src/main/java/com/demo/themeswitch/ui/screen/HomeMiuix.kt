package com.demo.themeswitch.ui.screen

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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.R
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

private const val KERNEL_VERSION = "5.15.104-gki"
private const val WORKING_MODE = "GKI"

@Composable
fun HomeMiuixScreen() {
    val scrollBehavior = MiuixScrollBehavior()
    val colorScheme = MiuixTheme.colorScheme

    Scaffold(
        // 外层 MainScreen Scaffold 统一处理窗口 insets，页面只管顶栏
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
                // KernelSU 风格工作状态卡：无固定高度，高度由 110dp 对勾内容决定；
                // 右下大对勾（offset 后被卡片圆角裁切）、左下模式、左上标题/版本
                val cardColor = if (isInDarkTheme()) Color(0xFF1A3825) else Color(0xFFDFFAE4)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    colors = CardDefaults.defaultColors(color = cardColor),
                ) {
                    Box {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(x = 27.dp, y = 31.dp),
                            contentAlignment = Alignment.BottomEnd,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircleOutline,
                                contentDescription = null,
                                tint = Color(0xFF36D167),
                                modifier = Modifier.size(110.dp),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp, 10.dp),
                            contentAlignment = Alignment.BottomStart,
                        ) {
                            Text(
                                text = WORKING_MODE,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
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
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = stringResource(R.string.home_working_version, KERNEL_VERSION),
                                    fontSize = 15.sp,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.section_info),
                    color = colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp),
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                ) {
                    MiuixInfoItem(Icons.Filled.Security, R.string.card_security, stringResource(R.string.status_working))
                    MiuixInfoItem(Icons.Filled.DevicesOther, R.string.card_device, "Pixel 7 Pro")
                    MiuixInfoItem(Icons.Filled.Memory, R.string.card_kernel, KERNEL_VERSION)
                    MiuixInfoItem(Icons.Filled.Android, R.string.card_android, "Android 14")
                    MiuixInfoItem(Icons.Filled.Storage, R.string.card_storage, "256 GB")
                    MiuixInfoItem(Icons.Filled.BatteryChargingFull, R.string.card_battery, "85%")
                }
            }
        }
    }
}

@Composable
private fun MiuixInfoItem(icon: ImageVector, titleResId: Int, value: String) {
    val colorScheme = MiuixTheme.colorScheme
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
            color = colorScheme.onSurfaceVariantSummary,
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
