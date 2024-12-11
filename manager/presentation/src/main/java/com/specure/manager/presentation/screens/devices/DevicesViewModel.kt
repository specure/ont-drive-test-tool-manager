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

            bluetoothDevicesProvider.observePairedDevices(DeviceType.TRACKER)
                .onEach { devices ->
                    Timber.d("getting devices tracking devices: ${devices.values}")
                    state = state.copy(
                        pairedDevices = devices.values.map { it.toBluetoothDeviceUi() }.toList()
                    )
                }
                .launchIn(viewModelScope)
        }
    }

    fun onAction(action: DevicesAction) {
        when (action) {
            is DevicesAction.OnDeviceAdded -> {
                action.address
            }
            is DevicesAction.OnDeviceRemoved -> {

                action.address
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