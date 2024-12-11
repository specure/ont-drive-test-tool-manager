package com.specure.manager.presentation.screens.devices

sealed interface DevicesEvent {
    data object OnResumed : DevicesEvent
}
