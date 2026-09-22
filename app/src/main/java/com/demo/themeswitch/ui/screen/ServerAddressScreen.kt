package com.demo.themeswitch.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.demo.themeswitch.R
import com.demo.themeswitch.data.AppPreferences
import com.demo.themeswitch.data.SettingsRepository
import com.demo.themeswitch.ui.LocalUiMode
import com.demo.themeswitch.ui.UiMode
import com.demo.themeswitch.ui.component.material.SegmentedColumn
import com.demo.themeswitch.ui.component.material.SegmentedListItem
import com.demo.themeswitch.ui.component.material.TopBarBackButton
import com.demo.themeswitch.ui.component.material.expressiveTopAppBarColors
import com.demo.themeswitch.ui.navigation.LocalNavigator
import com.demo.themeswitch.ui.screen.LocalScaffoldBottomPadding
import com.demo.themeswitch.ui.theme.LocalEnableBlur
import com.demo.themeswitch.util.BlurredBar
import com.demo.themeswitch.util.rememberBlurBackdrop
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.basic.TextField as MiuixTextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun ServerAddressScreen() {
    val uiMode = LocalUiMode.current
    if (uiMode == UiMode.Material) {
        ServerAddressMaterial()
    } else {
        ServerAddressMiuix()
    }
}

@Composable
private fun ServerAddressMaterial() {
    val navigator = LocalNavigator.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                navigationIcon = { TopBarBackButton(onClick = { navigator.pop() }) },
                title = { Text(stringResource(R.string.api_server_address)) },
                colors = expressiveTopAppBarColors(),
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            contentPadding = PaddingValues(bottom = LocalScaffoldBottomPadding.current + 16.dp),
        ) {
            item { ServerAddressContent(useMiuix = false) }
        }
    }
}

@Composable
private fun ServerAddressMiuix() {
    val navigator = LocalNavigator.current
    val scrollBehavior = MiuixScrollBehavior()
    val enableBlur = LocalEnableBlur.current
    val backdrop = rememberBlurBackdrop(enableBlur)
    val barColor = if (backdrop != null) androidx.compose.ui.graphics.Color.Transparent
    else MiuixTheme.colorScheme.surface

    MiuixScaffold(
        topBar = {
            BlurredBar(backdrop) {
                TopAppBar(
                    color = barColor,
                    title = stringResource(R.string.api_server_address),
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            MiuixIcon(
                                imageVector = MiuixIcons.Back,
                                contentDescription = null,
                                tint = MiuixTheme.colorScheme.onBackground,
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        popupHost = { },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier,
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollEndHaptic()
                    .overScrollVertical()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding(),
                    bottom = LocalScaffoldBottomPadding.current + 12.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { ServerAddressContent(useMiuix = true) }
            }
        }
    }
}

@Composable
private fun ServerAddressContent(useMiuix: Boolean) {
    val context = LocalContext.current
    val repository = remember { SettingsRepository(context) }
    val prefs by repository.preferencesFlow.collectAsState(initial = AppPreferences())
    val scope = rememberCoroutineScope()

    var serverUrl by remember(prefs.serverUrl) { mutableStateOf(prefs.serverUrl) }

    val save: () -> Unit = {
        val trimmed = serverUrl.trim().trimEnd('/')
        if (trimmed.isNotEmpty() && trimmed != prefs.serverUrl) {
            serverUrl = trimmed
            scope.launch { repository.setServerUrl(trimmed) }
        }
    }

    if (useMiuix) {
        top.yukonga.miuix.kmp.basic.Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                MiuixText(
                    text = stringResource(R.string.api_server_address),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    color = MiuixTheme.colorScheme.onBackground,
                )
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
                MiuixTextField(
                    value = serverUrl,
                    onValueChange = { serverUrl = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { if (!it.isFocused) save() },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { save() }),
                )
            }
        }
    } else {
        SegmentedColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            item {
                SegmentedListItem(
                    onClick = { },
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Dns,
                            contentDescription = stringResource(R.string.api_server_address),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    headlineContent = {
                        OutlinedTextField(
                            value = serverUrl,
                            onValueChange = { serverUrl = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { if (!it.isFocused) save() },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { save() }),
                            label = { Text(stringResource(R.string.api_server_address)) },
                        )
                    },
                )
            }
        }
    }
}
