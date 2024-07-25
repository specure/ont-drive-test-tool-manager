package com.cadrikmdev.manager.presentation.manager_overview

import com.cadrikmdev.manager.presentation.manager_overview.model.TrackingDeviceUi

data class ManagerOverviewState(
    val managedDevices: List<TrackingDeviceUi> = emptyList(),
)
