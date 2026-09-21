package com.demo.themeswitch.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.data.SseClient
import com.demo.themeswitch.ui.screen.LocalScaffoldBottomPadding
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Button as MiuixButton
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TextField as MiuixTextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/** 与 C++ 后端约定的 action 列表（POST /api/v1/execute） */
private val API_ACTIONS = listOf("list_items", "create_item", "update_item", "delete_item")

/** 执行页共享状态：SSE 帧列表 + 状态机（双 UI 模式共用） */
class ApiExecutor {
    enum class Status { IDLE, RUNNING, DONE, ERROR }

    /** 已收到的 SSE 帧载荷（已去 "data: " 前缀，含 [DONE]） */
    val frames = mutableStateListOf<String>()

    var status by mutableStateOf(Status.IDLE)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun failLocal(message: String) {
        frames.clear()
        errorMessage = message
        status = Status.ERROR
    }

    suspend fun run(baseUrl: String, body: String) {
        if (status == Status.RUNNING) return
        frames.clear()
        errorMessage = null
        status = Status.RUNNING
        SseClient.execute(baseUrl, body) { payload ->
            frames.add(payload)
        }.fold(
            onSuccess = { status = Status.DONE },
            onFailure = { e ->
                errorMessage = e.message ?: "unknown error"
                status = Status.ERROR
            },
        )
    }
}

/** 执行页（Material 风格） */
@Composable
fun ApiMaterialScreen() {
    val scrollBehavior = MiuixScrollBehavior()
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.nav_api),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        ApiContent(
            topPadding = innerPadding.calculateTopPadding(),
            useMiuixInput = false,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        )
    }
}

/** 执行页（MIUI 风格） */
@Composable
fun ApiMiuixScreen() {
    val scrollBehavior = MiuixScrollBehavior()
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.nav_api),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        ApiContent(
            topPadding = innerPadding.calculateTopPadding(),
            useMiuixInput = true,
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ApiContent(
    topPadding: Dp,
    useMiuixInput: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val prefs by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()
    val executor = remember { ApiExecutor() }

    var serverUrl by remember(prefs.serverUrl) { mutableStateOf(prefs.serverUrl) }
    var selectedAction by remember { mutableStateOf(API_ACTIONS.first()) }
    var customBody by remember { mutableStateOf("") }

    val invalidUrlMsg = stringResource(R.string.api_invalid_url)
    val running = executor.status == ApiExecutor.Status.RUNNING

    val saveServerUrl: () -> Unit = {
        val trimmed = serverUrl.trim().trimEnd('/')
        if (trimmed.isNotEmpty() && trimmed != prefs.serverUrl) {
            serverUrl = trimmed
            scope.launch { repository.setServerUrl(trimmed) }
        }
    }

    val onRun: () -> Unit = {
        val url = serverUrl.trim().trimEnd('/')
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            executor.failLocal(invalidUrlMsg)
        } else {
            val body = customBody.trim().ifEmpty { "{\"action\":\"$selectedAction\"}" }
            scope.launch { executor.run(url, body) }
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(executor.frames.size, executor.status) {
        val target = listState.layoutInfo.totalItemsCount - 1
        if (target > 0) listState.animateScrollToItem(target)
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .overScrollVertical()
            .scrollEndHaptic()
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(
            top = topPadding + 12.dp,
            bottom = LocalScaffoldBottomPadding.current + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // ── 服务器地址 ─────────────────────────────────────
        item {
            SectionCard(title = stringResource(R.string.api_server_address), useMiuix = useMiuixInput) {
                if (useMiuixInput) {
                    MiuixTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { if (!it.isFocused) saveServerUrl() },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { saveServerUrl() }),
                    )
                } else {
                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { if (!it.isFocused) saveServerUrl() },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { saveServerUrl() }),
                    )
                }
            }
        }

        // ── 执行 ──────────────────────────────────────────
        item {
            SectionCard(title = stringResource(R.string.api_action_hint), useMiuix = useMiuixInput) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    API_ACTIONS.forEach { action ->
                        FilterChip(
                            selected = action == selectedAction,
                            onClick = { if (!running) selectedAction = action },
                            label = { Text(action, fontFamily = FontFamily.Monospace) },
                        )
                    }
                }
                Spacer(Modifier.padding(top = 4.dp))
                if (useMiuixInput) {
                    MiuixTextField(
                        value = customBody,
                        onValueChange = { customBody = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.api_custom_body),
                    )
                } else {
                    OutlinedTextField(
                        value = customBody,
                        onValueChange = { customBody = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.api_custom_body)) },
                    )
                }
                Spacer(Modifier.padding(top = 8.dp))
                if (useMiuixInput) {
                    MiuixButton(
                        onClick = onRun,
                        enabled = !running,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            if (running) stringResource(R.string.api_running)
                            else stringResource(R.string.api_run),
                        )
                    }
                } else {
                    Button(
                        onClick = onRun,
                        enabled = !running,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            if (running) stringResource(R.string.api_running)
                            else stringResource(R.string.api_run),
                        )
                    }
                }
            }
        }

        // ── 响应流 ────────────────────────────────────────
        item {
            SectionCard(title = stringResource(R.string.api_response), useMiuix = useMiuixInput) {
                StatusLine(executor = executor)
                Spacer(Modifier.padding(top = 8.dp))
                if (executor.frames.isEmpty() && executor.status != ApiExecutor.Status.ERROR) {
                    Text(
                        text = stringResource(R.string.api_empty_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    executor.frames.forEach { frame ->
                        Text(
                            text = "data: $frame",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (frame == "[DONE]") FontWeight.Bold else FontWeight.Normal,
                            color = if (frame == "[DONE]") {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            modifier = Modifier.padding(vertical = 2.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusLine(executor: ApiExecutor) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        when (executor.status) {
            ApiExecutor.Status.IDLE -> Unit
            ApiExecutor.Status.RUNNING -> {
                CircularProgressIndicator(modifier = Modifier.width(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.api_running),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            ApiExecutor.Status.DONE -> {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.api_status_done),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            ApiExecutor.Status.ERROR -> {
                Icon(
                    Icons.Rounded.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.width(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.api_status_error) + (executor.errorMessage?.let { ": $it" } ?: ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    useMiuix: Boolean,
    content: @Composable () -> Unit,
) {
    if (useMiuix) {
        MiuixCard(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Column(Modifier.padding(12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MiuixTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.padding(top = 8.dp))
                content()
            }
        }
    } else {
        androidx.compose.material3.Card(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Column(Modifier.padding(12.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.padding(top = 8.dp))
                content()
            }
        }
    }
}
