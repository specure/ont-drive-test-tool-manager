package com.specure.manager.presentation.screens.manager_overview

sealed interface ManagerOverviewAction {
    data object OnResolvePermissionClick : ManagerOverviewAction
    data object OnOpenBluetoothSettingsClick : ManagerOverviewAction
    data class OnStartClick(val address: String) : ManagerOverviewAction
    data class OnStopClick(val address: String) : ManagerOverviewAction
    data class OnConnectClick(val address: String) : ManagerOverviewAction
    data class DeleteManager(val address: String) : ManagerOverviewAction
    data object OnCheckTrackerLatestVersionClick : ManagerOverviewAction
    data object OnDevicesClick : ManagerOverviewAction
    data object OnSettingsClick : ManagerOverviewAction
    data object OnAboutClick : ManagerOverviewAction
}