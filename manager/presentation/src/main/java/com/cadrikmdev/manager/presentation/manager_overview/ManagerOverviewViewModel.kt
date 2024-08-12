package com.cadrikmdev.manager.presentation.manager_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cadrikmdev.intercom.domain.client.BluetoothClientService
import com.cadrikmdev.intercom.domain.message.TrackerAction
import com.cadrikmdev.manager.presentation.manager_overview.mappers.toTrackingDeviceUI
import com.cadrikmdev.permissions.domain.PermissionHandler
import com.cadrikmdev.permissions.presentation.appPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber


class ManagerOverviewViewModel(
    private val applicationScope: CoroutineScope,
    private val bluetoothService: BluetoothClientService,
    private val permissionHandler: PermissionHandler,
) : ViewModel() {

    var state by mutableStateOf(ManagerOverviewState())
        private set


    init {
        permissionHandler.setPermissionsNeeded(
            appPermissions
        )

        bluetoothService.observeConnectedDevices(com.cadrikmdev.intercom.domain.client.DeviceType.TRACKER)
            .onEach { devices ->
                state = state.copy(
                    managedDevices = devices.values.toList()
                )
            }
            .launchIn(viewModelScope)

        bluetoothService.trackingDevices
            .onEach { devices ->
                state = state.copy(
                    managedDevices = devices.values.toList()
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

            is ManagerOverviewAction.OnConnectClick -> {
                viewModelScope.launch {
                    val result = bluetoothService.connectToDevice(action.address)
                    Timber.d("Connect result: $result")
                }
            }

            is ManagerOverviewAction.OnStartClick -> {
                viewModelScope.launch {
                    bluetoothService.sendActionFlow.emit(TrackerAction.StartTest(action.address))
                }
            }

            is ManagerOverviewAction.OnStopClick -> {
                viewModelScope.launch {
                    bluetoothService.sendActionFlow.emit(TrackerAction.StopTest(action.address))
                }
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