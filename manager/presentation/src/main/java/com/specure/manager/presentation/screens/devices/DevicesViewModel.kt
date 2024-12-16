package com.specure.manager.presentation.screens.devices

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.specure.core.domain.config.Config
import com.specure.intercom.domain.BluetoothDevicesProvider
import com.specure.intercom.domain.client.DeviceType
import com.specure.manager.presentation.screens.devices.mappers.toBluetoothDeviceUi
import com.specure.permissions.domain.PermissionHandler
import com.specure.permissions.presentation.appPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber


class DevicesViewModel(
    private val appContext: Context,
    private val appConfig: Config,
    private val applicationScope: CoroutineScope,
    private val bluetoothDevicesProvider: BluetoothDevicesProvider<BluetoothDevice>,
    private val permissionHandler: PermissionHandler,
) : ViewModel() {

    var state by mutableStateOf(DevicesState())
        private set

    init {
        permissionHandler.setPermissionsNeeded(
            appPermissions
        )
    }

    private fun manageBluetoothDevices() {
        if (permissionHandler.isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)) {

            val pairedDevicesFlow = bluetoothDevicesProvider.observePairedDevices(DeviceType.TRACKER)

            pairedDevicesFlow.onEach { pairedDevices ->
                val selectedDevicesAddresses = appConfig.getSelectedDevicesAddress()

                Timber.d("getting pairedDevices tracking pairedDevices ALONE: ${pairedDevices.values}")
                state = state.copy(
                    pairedDevices = pairedDevices.values.map { it.toBluetoothDeviceUi(selectedDevicesAddresses.contains(it.address)) }.toList()
                )
            }.launchIn(viewModelScope)

            val appConfigChangesFlow = appConfig.listenToPreferencesChanges()
            appConfigChangesFlow.combineTransform(pairedDevicesFlow) { _, pairedDevices ->
                    val selectedDevicesAddresses = appConfig.getSelectedDevicesAddress()

                    Timber.d("getting pairedDevices tracking pairedDevices: ${pairedDevices.values}")
                    state = state.copy(
                        pairedDevices = pairedDevices.values.map { it.toBluetoothDeviceUi(selectedDevicesAddresses.contains(it.address)) }.toList()
                    )
                    emit(state)

            }.launchIn(viewModelScope)

        }
    }

    fun onAction(action: DevicesAction) {
        when (action) {
            is DevicesAction.OnDeviceAdded -> {
                appConfig.setSelectedDevicesAddresses(appConfig.getSelectedDevicesAddress().plus(action.address))
            }
            is DevicesAction.OnDeviceRemoved -> {
                appConfig.setSelectedDevicesAddresses(appConfig.getSelectedDevicesAddress().minus(action.address))
            }
            else -> Unit
        }
    }

    fun onEvent(event: DevicesEvent) {
        when (event) {
            is DevicesEvent.OnResumed -> {
                manageBluetoothDevices()
            }
        }
    }
}