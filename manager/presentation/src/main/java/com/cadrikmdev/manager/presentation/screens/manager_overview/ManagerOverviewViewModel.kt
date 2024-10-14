package com.cadrikmdev.manager.presentation.screens.manager_overview

import android.Manifest
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cadrikmdev.core.domain.service.BluetoothService
import com.cadrikmdev.intercom.domain.client.BluetoothClientService
import com.cadrikmdev.intercom.domain.client.DeviceType
import com.cadrikmdev.intercom.domain.message.TrackerAction
import com.cadrikmdev.permissions.domain.PermissionHandler
import com.cadrikmdev.permissions.presentation.appPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber


class ManagerOverviewViewModel(
    private val appContext: Context,
    private val applicationScope: CoroutineScope,
    private val bluetoothService: BluetoothClientService,
    private val androidBluetoothService: BluetoothService,
    private val permissionHandler: PermissionHandler,
) : ViewModel() {

    var state by mutableStateOf(ManagerOverviewState())
        private set


    init {
        permissionHandler.setPermissionsNeeded(
            appPermissions
        )

        manageBluetoothDevices()


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

    private fun playAlertSound() {
        val alert: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone: Ringtone = RingtoneManager.getRingtone(appContext, alert)
        ringtone.play()
    }

    private fun manageBluetoothDevices() {
        if (permissionHandler.isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT) && state.isBluetoothAdapterEnabled) {
            bluetoothService.observeConnectedDevices(DeviceType.TRACKER)
                .onEach { devices ->
                    state = state.copy(
                        managedDevices = devices.values.toList()
                    )
                }
                .launchIn(viewModelScope)

            bluetoothService.trackingDevices
                .onEach { devices ->

                    val previousState = state.copy()
                    val newErrorDevices = previousState.managedDevices.filter { prevDeviceState ->
                        devices.filter { currentDeviceState ->
                            currentDeviceState.value.isStateChangedOnTheSameDevice(prevDeviceState) && currentDeviceState.value.isErrorState()
                        }.isNotEmpty()
                    }
                    if (newErrorDevices.isNotEmpty()) {
                        playAlertSound()
                    }
                    state = state.copy(
                        managedDevices = devices.values.toList()
                    )
                }
                .launchIn(viewModelScope)
        }
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
            ManagerOverviewAction.OnOpenBluetoothSettingsClick -> {
                androidBluetoothService.openBluetoothSettings()
            }
            else -> Unit
        }
    }

    fun onEvent(event: ManagerOverviewEvent) {
        when (event) {
            is ManagerOverviewEvent.OnResumed -> {
                updateBluetoothAdapterState()
                updatePermissionsState()
                manageBluetoothDevices()
            }
            is ManagerOverviewEvent.OnMeasurementError -> {
                playAlertSound()
            }
        }
    }

    private fun updatePermissionsState() {
        state = state.copy(
            isPermissionRequired = permissionHandler.getNotGrantedPermissionList().isNotEmpty(),
        )
    }

    private fun updateBluetoothAdapterState() {
        state = state.copy(
            isBluetoothAdapterEnabled = androidBluetoothService.isBluetoothEnabled(),
        )
    }
}