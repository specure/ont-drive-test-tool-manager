package com.specure.manager.presentation.screens.devices

import com.specure.manager.presentation.screens.devices.data.BluetoothDeviceUi

data class DevicesState(
    val isPermissionRequired: Boolean = true,
    val pairedDevices: List<BluetoothDeviceUi> = emptyList(),
)
