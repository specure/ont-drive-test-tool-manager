package com.cadrikmdev.manager.presentation.manager_overview

sealed interface ManagerOverviewEvent {
    data object OnUpdatePermissionStatus : ManagerOverviewEvent
}