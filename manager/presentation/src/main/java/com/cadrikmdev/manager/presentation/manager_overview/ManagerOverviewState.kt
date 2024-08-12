package com.cadrikmdev.manager.presentation.manager_overview

import com.cadrikmdev.intercom.domain.client.TrackingDevice

data class ManagerOverviewState(
    val isPermissionRequired: Boolean = true,
    val managedDevices: List<TrackingDevice> = emptyList(),
)
