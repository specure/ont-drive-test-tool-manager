@file:OptIn(ExperimentalMaterial3Api::class)

package com.cadrikmdev.manager.presentation.manager_overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.currentStateAsState
import com.cadrikmdev.core.presentation.designsystem.SignalTrackerManagerTheme
import com.cadrikmdev.core.presentation.designsystem.components.SignalTrackerManagerOutlinedActionButton
import com.cadrikmdev.core.presentation.designsystem.components.SignalTrackerManagerScaffold
import com.cadrikmdev.core.presentation.designsystem.components.SignalTrackerManagerToolbar
import com.cadrikmdev.manager.presentation.R
import com.cadrikmdev.manager.presentation.manager_overview.components.ManagedDeviceListItem
import com.cadrikmdev.manager.presentation.manager_overview.model.TrackingDeviceUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManagerOverviewScreenRoot(
    onResolvePermissionClick: () -> Unit,
    viewModel: ManagerOverviewViewModel = koinViewModel(),
) {
    ManagerOverviewScreen(
        state = viewModel.state,
        onAction = { action ->
            when (action) {
                ManagerOverviewAction.OnResolvePermissionClick -> onResolvePermissionClick()
                else -> Unit
            }
            viewModel.onAction(action)
        },
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun ManagerOverviewScreen(
    state: ManagerOverviewState,
    onAction: (ManagerOverviewAction) -> Unit,
    onEvent: (ManagerOverviewEvent) -> Unit
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
                onEvent(ManagerOverviewEvent.OnUpdatePermissionStatus)
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
                showBackButton = false,
                title = stringResource(id = R.string.signal_tracker_manager),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isPermissionRequired) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = stringResource(id = R.string.permission_required))
                    if (state.isPermissionRequired) {
                        SignalTrackerManagerOutlinedActionButton(
                            modifier = Modifier.padding(start = 16.dp),
                            text = stringResource(id = R.string.resolve),
                            isLoading = false
                        ) {
                            onAction(ManagerOverviewAction.OnResolvePermissionClick)
                        }
                    }
                }
            }
            if (state.managedDevices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = stringResource(id = R.string.please_connect_signal_tracking_device))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .padding(horizontal = 16.dp),
                    contentPadding = padding,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(
                        items = state.managedDevices,
                        key = {
                            it.id
                        }
                    ) {
                        ManagedDeviceListItem(
                            trackingDeviceUi = it,
                            onDeleteClick = { id -> onAction(ManagerOverviewAction.DeleteManager(id)) },
                            onStopClick = { id -> onAction(ManagerOverviewAction.OnStopClick(id)) },
                            onStartClick = { id -> onAction(ManagerOverviewAction.OnStartClick(id)) },
                        )
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
        ManagerOverviewScreen(
            state = ManagerOverviewState(
                isPermissionRequired = true
            ),
            onAction = {},
            onEvent = {},
        )
    }
}
