package com.specure.manager.presentation.screens.manager_overview

import com.cadrikmdev.intercom.domain.data.BluetoothDevice

sealed interface ManagerOverviewAction {
    data object OnResolvePermissionClick : ManagerOverviewAction
    data object OnOpenBluetoothSettingsClick : ManagerOverviewAction
    data class OnStartClick(val device: BluetoothDevice) : ManagerOverviewAction
    data class OnStopClick(val device: BluetoothDevice) : ManagerOverviewAction
    data class OnConnectClick(val device: BluetoothDevice) : ManagerOverviewAction
    data class DeleteManager(val device: BluetoothDevice) : ManagerOverviewAction
    data object OnCheckTrackerLatestVersionClick : ManagerOverviewAction
    data object OnDevicesClick : ManagerOverviewAction
    data object OnSettingsClick : ManagerOverviewAction
    data object OnAboutClick : ManagerOverviewAction
}