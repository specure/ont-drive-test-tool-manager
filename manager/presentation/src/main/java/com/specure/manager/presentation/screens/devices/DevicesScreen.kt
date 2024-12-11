@file:OptIn(ExperimentalMaterial3Api::class)

package com.specure.manager.presentation.screens.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import com.specure.core.presentation.designsystem.DevicesIcon
import com.specure.core.presentation.designsystem.InfoIcon
import com.specure.core.presentation.designsystem.RefreshIcon
import com.specure.core.presentation.designsystem.SettingsIcon
import com.specure.core.presentation.designsystem.SignalTrackerManagerTheme
import com.specure.core.presentation.designsystem.components.SignalTrackerManagerOutlinedActionButton
import com.specure.core.presentation.designsystem.components.SignalTrackerManagerScaffold
import com.specure.core.presentation.designsystem.components.SignalTrackerManagerToolbar
import com.specure.core.presentation.designsystem.components.util.BaseToolbarItem
import com.specure.core.presentation.designsystem.components.util.DropDownItem
import com.specure.core.presentation.ui.KeepScreenOn
import com.specure.manager.presentation.R
import com.specure.manager.presentation.screens.devices.components.PairedDeviceListItem
import com.specure.manager.presentation.screens.manager_overview.components.ManagedDeviceListItem
import com.specure.updater.domain.UpdatingStatus
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun DevicesScreenRoot(
    onBackClick: () -> Unit,
    viewModel: DevicesViewModel = koinViewModel(),
) {
    DevicesScreen(
        state = viewModel.state,
        onBackClick = onBackClick,
        onAction = { action ->
            viewModel.onAction(action)
        },
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DevicesScreen(
    state: DevicesState,
    onBackClick: () -> Unit,
    onAction: (DevicesAction) -> Unit,
    onEvent: (DevicesEvent) -> Unit
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                onEvent(DevicesEvent.OnResumed)
            }

            Lifecycle.State.DESTROYED,
            Lifecycle.State.INITIALIZED,
            Lifecycle.State.CREATED,
            Lifecycle.State.STARTED -> { // nothing to do }
            }
        }
    }

    SignalTrackerManagerScaffold(
        topAppBar = {
            SignalTrackerManagerToolbar(
                showBackButton = true,
                onBackClick = onBackClick,
                title = stringResource(id = R.string.paired_devices),
                scrollBehavior = scrollBehavior,
                menuItems = listOf(
                    BaseToolbarItem(
                        item = DropDownItem(
                            icon = RefreshIcon,
                            title = stringResource(id = R.string.refresh)
                        ),
                        action = {
                            onAction(DevicesAction.OnRefreshClick)
                        }
                    ),
                ),
            )
        },
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top
        ) {
            if (state.pairedDevices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = stringResource(id = R.string.please_pair_tracking_device))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(
                            items = state.pairedDevices,
                            key = {
                                it.address
                            }
                        ) {
                            PairedDeviceListItem(
                                bluetoothDevice = it,
                                onDeviceAddedClick = { address ->
                                    onAction(
                                        DevicesAction.OnDeviceAdded(
                                            address
                                        )
                                    )
                                },
                                onDeviceRemovedClick = { address ->
                                    onAction(
                                        DevicesAction.OnDeviceRemoved(
                                            address
                                        )
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    SignalTrackerManagerTheme {
        DevicesScreen(
            state = DevicesState(
                isPermissionRequired = true
            ),
            onBackClick = {},
            onAction = {},
            onEvent = {},
        )
    }
}
