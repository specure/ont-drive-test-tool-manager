package com.specure.manager.presentation.screens.manager_overview

import com.specure.manager.presentation.data.ManagedBluetoothDevice


data class ManagerOverviewState(
    val isPermissionRequired: Boolean = true,
    val isBluetoothAdapterEnabled: Boolean = false,
    val managedDevices: List<ManagedBluetoothDevice> = emptyList(),
    val lastTrackerVersion: String? = null,
    val keepScreenOn: Boolean = false
) {

}
