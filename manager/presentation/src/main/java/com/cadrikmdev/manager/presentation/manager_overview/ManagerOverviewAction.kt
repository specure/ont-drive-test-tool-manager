package com.cadrikmdev.manager.presentation.manager_overview

sealed interface ManagerOverviewAction {
    data object OnResolvePermissionClick : ManagerOverviewAction
    data class OnStartClick(val id: String) : ManagerOverviewAction
    data class OnStopClick(val id: String) : ManagerOverviewAction
    data class DeleteManager(val id: String) : ManagerOverviewAction
}