package com.cadrikmdev.manager.presentation.manager_overview

import android.Manifest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cadrikmdev.core.connectivity.domain.DeviceNode
import com.cadrikmdev.core.connectivity.domain.DeviceType
import com.cadrikmdev.core.connectivity.domain.TrackerManagerDiscovery
import com.cadrikmdev.manager.presentation.manager_overview.model.TrackingDeviceUi
import com.cadrikmdev.permissions.domain.PermissionHandler
import com.cadrikmdev.permissions.presentation.appPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private fun DeviceNode.toTrackingDeviceUI(): TrackingDeviceUi {
    return TrackingDeviceUi(
        status = this.type.toString(),
        id = this.displayName,
        updateTimestamp = System.currentTimeMillis()
    )
}

class ManagerOverviewViewModel(
    private val applicationScope: CoroutineScope,
    private val trackerManagerDiscovery: TrackerManagerDiscovery,
    private val permissionHandler: PermissionHandler,
) : ViewModel() {

    var state by mutableStateOf(ManagerOverviewState())
        private set


    init {
        permissionHandler.setPermissionsNeeded(
            appPermissions
        )

        trackerManagerDiscovery.observeConnectedDevices(DeviceType.TRACKER)
            .onEach { devices ->
                state = state.copy(
                    managedDevices = devices.map { it.toTrackingDeviceUI() }
                )
            }
            .launchIn(viewModelScope)

//        viewModelScope.launch {
//            delay(2000)
//            state = state.copy(
//                managedDevices = listOf(
//                    TrackingDeviceUi(
//                        "Samsung A52",
//                        "running",
//                        System.currentTimeMillis()
//                    )
//                )
//            )
//        }
    }

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

            ManagerOverviewAction.OnResolvePermissionClick -> {

            }
            else -> Unit
        }
    }

    fun onEvent(event: ManagerOverviewEvent) {
        when (event) {
            is ManagerOverviewEvent.OnUpdatePermissionStatus -> {
                updatePermissionsState()
            }
        }
    }

    private fun updatePermissionsState() {
        state = state.copy(
            isPermissionRequired = permissionHandler.getNotGrantedPermissionList().isNotEmpty(),
        )
    }
}