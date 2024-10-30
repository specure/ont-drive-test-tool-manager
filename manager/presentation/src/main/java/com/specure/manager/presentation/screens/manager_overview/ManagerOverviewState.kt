package com.specure.manager.presentation.screens.manager_overview

import com.specure.intercom.domain.client.TrackingDevice

data class ManagerOverviewState(
    val isPermissionRequired: Boolean = true,
    val isBluetoothAdapterEnabled: Boolean = false,
    val managedDevices: List<TrackingDevice> = emptyList(),
    val lastTrackerVersion: String? = null,
)
