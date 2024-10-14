package com.cadrikmdev.manager.presentation.screens.manager_overview

import com.cadrikmdev.intercom.domain.client.TrackingDevice

data class ManagerOverviewState(
    val isPermissionRequired: Boolean = true,
    val isBluetoothAdapterEnabled: Boolean = false,
    val managedDevices: List<TrackingDevice> = emptyList(),
)
