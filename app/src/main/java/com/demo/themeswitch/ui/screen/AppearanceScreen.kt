package com.demo.themeswitch.ui.screen

// 复刻 KernelSU 调色板界面（学习 KOWX712/KernelSU ui/screen/colorpalette，双 UI 模式各一套）：
// 主题预览卡（app 缩略图）+ 主题模式选择。
// 悬浮底栏/液态玻璃/预测返回/Monet/强调色/界面缩放等项按要求不做

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.theme.isInDarkTheme
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun AppearanceMiuixScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()
    val colorScheme = MiuixTheme.colorScheme
    val isDark = isInDarkTheme()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.settings_section_appearance),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = colorScheme.onBackground,
                        )
                    }
                },
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
                bottom = LocalScaffoldBottomPadding.current + 24.dp,
            ),
        ) {
            item {
                ThemePreviewCard(
                    backgroundColor = colorScheme.surface,
                    textColor = colorScheme.onBackground,
                    accentCardColor = if (isDark) Color(0xFF1A3825) else Color(0xFFDFFAE4),
                    cardColor = colorScheme.surfaceVariant,
                    navBarColor = colorScheme.surface,
                    dividerColor = colorScheme.onBackground.copy(alpha = 0.1f),
                    iconColor = colorScheme.primary,
                    outlineColor = colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 24.dp),
                )
            }
            item {
                val themeItems = listOf(
                    stringResource(R.string.theme_system),
                    stringResource(R.string.theme_light),
                    stringResource(R.string.theme_dark),
                )
                TabRow(
                    tabs = themeItems,
                    selectedTabIndex = preferences.themeMode.coerceIn(0, 2),
                    onTabSelected = { mode ->
                        scope.launch { repository.setThemeMode(mode) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                )
            }
        }
    }
}

@Composable
fun AppearanceMaterialScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val preferences by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val scrollBehavior = MiuixScrollBehavior()
    val isDark = isInDarkTheme()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.settings_section_appearance),
                color = MaterialTheme.colorScheme.surface,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = LocalScaffoldBottomPadding.current + 24.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ThemePreviewCard(
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                textColor = MaterialTheme.colorScheme.onSurface,
                accentCardColor = MaterialTheme.colorScheme.secondaryContainer,
                cardColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                navBarColor = MaterialTheme.colorScheme.surfaceContainer,
                dividerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                iconColor = MaterialTheme.colorScheme.primary,
                outlineColor = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                val options = listOf(
                    Triple(Icons.Filled.Brightness4, stringResource(R.string.theme_system), 0),
                    Triple(Icons.Filled.Brightness7, stringResource(R.string.theme_light), 1),
                    Triple(Icons.Filled.Brightness3, stringResource(R.string.theme_dark), 2),
                )
                options.forEachIndexed { index, (icon, label, mode) ->
                    SegmentedButton(
                        selected = preferences.themeMode == mode,
                        onClick = {
                            scope.launch { repository.setThemeMode(mode) }
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                        icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        label = { Text(label) },
                    )
                }
            }
        }
    }
}

/**
 * 主题预览卡（KSU ThemePreviewCard 同款布局）：40% 屏宽、与屏幕等比的圆角缩略图，
 * 内部模拟应用界面：顶栏应用名 + 状态卡 + 内容卡 + 底栏。
 * 颜色由调用方传入（Miuix/M3 各自主题色系）。
 */
@Composable
private fun ThemePreviewCard(
    backgroundColor: Color,
    textColor: Color,
    accentCardColor: Color,
    cardColor: Color,
    navBarColor: Color,
    dividerColor: Color,
    iconColor: Color,
    outlineColor: Color,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val screenRatio = configuration.screenWidthDp.toFloat() / configuration.screenHeightDp.toFloat()

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.4f)
                .aspectRatio(screenRatio)
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundColor)
                .border(1.dp, outlineColor, RoundedCornerShape(20.dp)),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 预览顶栏：应用名
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth()
                        .padding(start = 12.dp, top = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 12.sp,
                        color = textColor,
                    )
                }
                // 状态卡
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .height(45.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentCardColor),
                )
                // 内容卡
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(cardColor),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(cardColor),
                    )
                }
                // 预览底栏
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(dividerColor),
                    )
                    Row(
                        modifier = Modifier
                            .height(36.dp)
                            .fillMaxWidth()
                            .background(navBarColor)
                            .padding(top = 2.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(15.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (index == 0) iconColor else textColor.copy(alpha = 0.5f)),
                            )
                        }
                    }
                }
            }
        }
    }
}
