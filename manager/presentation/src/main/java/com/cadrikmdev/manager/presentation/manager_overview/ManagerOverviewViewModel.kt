package com.cadrikmdev.manager.presentation.manager_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cadrikmdev.manager.presentation.manager_overview.model.TrackingDeviceUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ManagerOverviewViewModel(
    private val applicationScope: CoroutineScope,
) : ViewModel() {


    init {
        viewModelScope.launch {
            delay(2000)
            state = state.copy(
                managedDevices = listOf(TrackingDeviceUi("Samsung A52", "running", System.currentTimeMillis()))
            )
        }
    }

    var state by mutableStateOf(ManagerOverviewState())
        private set

    fun onAction(action: ManagerOverviewAction) {
        when (action) {
            is ManagerOverviewAction.DeleteManager -> {
                TODO()
            }
            is ManagerOverviewAction.OnStartClick -> {
                TODO()
            }
            is ManagerOverviewAction.OnStopClick -> {
                TODO()
            }
            else -> Unit
        }
    }

}