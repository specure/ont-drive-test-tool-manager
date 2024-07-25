@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package com.cadrikmdev.manager.presentation.manager_overview.components

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
import com.cadrikmdev.manager.presentation.manager_overview.model.TrackingDeviceUi
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun ManagedDeviceListItem(
    trackingDeviceUi: TrackingDeviceUi,
    onDeleteClick: (id: String) -> Unit,
    onStartClick: (id: String) -> Unit,
    onStopClick: (id: String) -> Unit,
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
                text = trackingDeviceUi.id,
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
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
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
            SignalTrackerManagerActionButton(text = stringResource(id = R.string.start), modifier = Modifier.weight(1f), isLoading = false) {
                onStartClick(trackingDeviceUi.id)
            }
            SignalTrackerManagerActionButton(text = stringResource(id = R.string.stop), modifier = Modifier.weight(1f), isLoading = false) {
                onStopClick(trackingDeviceUi.id)
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
                onDeleteClick(trackingDeviceUi.id)
            },
        )
    }
}

@Preview
@Composable
private fun RunListItemPreview() {
    SignalTrackerManagerTheme {
        ManagedDeviceListItem(
            trackingDeviceUi = TrackingDeviceUi(
                id = "Telephone model name",
                status = "running",
                updateTimestamp = 15616561513
            ),
            onDeleteClick = { },
            onStartClick = { },
            onStopClick = { },
        )
    }
}