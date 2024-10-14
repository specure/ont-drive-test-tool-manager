package com.cadrikmdev.manager.presentation.screens.manager_overview

sealed interface ManagerOverviewEvent {
    data object OnResumed : ManagerOverviewEvent
    data object OnMeasurementError : ManagerOverviewEvent
}