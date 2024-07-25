@file:OptIn(ExperimentalMaterial3Api::class)

package com.cadrikmdev.manager.presentation.manager_overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cadrikmdev.core.presentation.designsystem.SignalTrackerManagerTheme
import com.cadrikmdev.core.presentation.designsystem.components.SignalTrackerManagerScaffold
import com.cadrikmdev.core.presentation.designsystem.components.SignalTrackerManagerToolbar
import com.cadrikmdev.manager.presentation.R
import com.cadrikmdev.manager.presentation.manager_overview.components.ManagedDeviceListItem
import com.cadrikmdev.manager.presentation.manager_overview.model.TrackingDeviceUi
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManagerOverviewScreenRoot(
    viewModel: ManagerOverviewViewModel = koinViewModel(),
) {
    ManagerOverviewScreen(
        state = viewModel.state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun ManagerOverviewScreen(
    state: ManagerOverviewState,
    onAction: (ManagerOverviewAction) -> Unit,
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = topAppBarState
    )
    SignalTrackerManagerScaffold(
        topAppBar = {
            SignalTrackerManagerToolbar(
                showBackButton = false,
                title = stringResource(id = R.string.signal_tracker_manager),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        if (state.managedDevices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
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

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    SignalTrackerManagerTheme {
        ManagerOverviewScreen(
            state = ManagerOverviewState(),
            onAction = {}
        )
    }
}
