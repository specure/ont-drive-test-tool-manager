package com.cadrikmdev.manager.presentation.manager_overview

sealed interface ManagerOverviewAction {
    data object OnStartClick : ManagerOverviewAction
    data object OnStopClick : ManagerOverviewAction
    data object DeleteManager : ManagerOverviewAction
}