package com.cadrikmdev.manager.presentation.manager_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope

class ManagerOverviewViewModel(
    private val applicationScope: CoroutineScope,
) : ViewModel() {

    var state by mutableStateOf(ManagerOverviewState())
        private set

    fun onAction(action: ManagerOverviewAction) {
        when (action) {
            ManagerOverviewAction.OnStartClick -> Unit
            ManagerOverviewAction.OnStopClick -> Unit
            is ManagerOverviewAction.DeleteManager -> {

            }
            else -> Unit
        }
    }

}