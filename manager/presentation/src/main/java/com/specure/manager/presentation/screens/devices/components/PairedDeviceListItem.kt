@file:OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)

package com.specure.manager.presentation.screens.devices.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.specure.core.presentation.designsystem.SignalTrackerManagerTheme
import com.specure.manager.presentation.screens.devices.data.BluetoothDeviceUi

@Composable
fun PairedDeviceListItem(
    bluetoothDevice: BluetoothDeviceUi,
    onDeviceAddedClick: (address: String) -> Unit,
    onDeviceRemovedClick: (address: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var checked by remember { mutableStateOf(bluetoothDevice.addedToBeManaged) }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = bluetoothDevice.name,
                color = MaterialTheme.colorScheme.onSurface
            )
            Checkbox(
                checked = bluetoothDevice.addedToBeManaged,
                onCheckedChange = {
                    checked = it
                    if (it) {
                        onDeviceAddedClick(bluetoothDevice.address)
                    } else {
                        onDeviceRemovedClick(bluetoothDevice.address)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun RunListItemPreview() {
    SignalTrackerManagerTheme {
        PairedDeviceListItem(
            bluetoothDevice = BluetoothDeviceUi(
                name = "Telephone model name",
                address = "47:51:53:55:88:56:FE",
                addedToBeManaged = true,
            ),
            onDeviceAddedClick = { },
            onDeviceRemovedClick = { },
        )
    }
}