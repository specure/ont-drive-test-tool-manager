package com.specure.manager.presentation.screens.devices

sealed interface DevicesAction {
    data class OnDeviceAdded(val address: String) : DevicesAction
    data class OnDeviceRemoved(val address: String) : DevicesAction
    data object OnRefreshClick : DevicesAction
}