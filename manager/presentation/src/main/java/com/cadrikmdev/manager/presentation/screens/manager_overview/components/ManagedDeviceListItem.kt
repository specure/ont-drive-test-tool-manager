@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package com.cadrikmdev.manager.presentation.screens.manager_overview.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cadrikmdev.core.presentation.ui.toLocalTime
import com.cadrikmdev.core.presentation.designsystem.SignalTrackerManagerTheme
import com.cadrikmdev.core.presentation.designsystem.components.SignalTrackerManagerActionButton
import com.cadrikmdev.manager.presentation.R
import com.cadrikmdev.intercom.domain.client.TrackingDevice
import com.cadrikmdev.intercom.domain.data.MeasurementState
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun ManagedDeviceListItem(
    trackingDeviceUi: TrackingDevice,
    onDeleteClick: (address: String) -> Unit,
    onStartClick: (address: String) -> Unit,
    onStopClick: (address: String) -> Unit,
    onConnectClick: (address: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.surface)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    showDropDown = true
                }
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = trackingDeviceUi.name,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (trackingDeviceUi.connected) {
                    stringResource(id = R.string.connected)
                } else {
                    stringResource(id = R.string.disconnected)
                },
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.updated_at),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = trackingDeviceUi.updateTimestamp.toDuration(DurationUnit.MILLISECONDS)
                    .toLocalTime().toString() ?: "-",
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.status),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = trackingDeviceUi.status,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            SignalTrackerManagerActionButton(
                text = if (trackingDeviceUi.connected) {
                    stringResource(id = R.string.disconnect)
                } else {
                    stringResource(id = R.string.connect)
                },
                modifier = Modifier.weight(1.5f),
                isLoading = false
            ) {
                onConnectClick(trackingDeviceUi.address)
            }
            SignalTrackerManagerActionButton(
                text = stringResource(id = R.string.start),
                modifier = Modifier.weight(1f),
                enabled = (trackingDeviceUi.connected && trackingDeviceUi.status != MeasurementState.RUNNING.toString() && trackingDeviceUi.status != MeasurementState.NOT_ACTIVATED.toString()),
                isLoading = false
            ) {
                onStartClick(trackingDeviceUi.address)
            }
            SignalTrackerManagerActionButton(
                text = stringResource(id = R.string.stop),
                modifier = Modifier.weight(1f),
                enabled = (trackingDeviceUi.connected && trackingDeviceUi.status == MeasurementState.RUNNING.toString() && trackingDeviceUi.status != MeasurementState.NOT_ACTIVATED.toString()),
                isLoading = false
            ) {
                onStopClick(trackingDeviceUi.address)
            }
        }
    }
    DropdownMenu(
        expanded = showDropDown,
        onDismissRequest = {
            showDropDown = false
        },
    ) {
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.delete))
            },
            onClick = {
                showDropDown = false
                onDeleteClick(trackingDeviceUi.name)
            },
        )
    }
}

@Preview
@Composable
private fun RunListItemPreview() {
    SignalTrackerManagerTheme {
        ManagedDeviceListItem(
            trackingDeviceUi = TrackingDevice(
                name = "Telephone model name",
                address = "47:51:53:55:88:56:FE",
                status = "idle",
                connected = false,
                updateTimestamp = 15616561513
            ),
            onDeleteClick = { },
            onStartClick = { },
            onStopClick = { },
            onConnectClick = { },
        )
    }
}