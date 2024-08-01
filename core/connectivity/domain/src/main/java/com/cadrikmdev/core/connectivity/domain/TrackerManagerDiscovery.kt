package com.cadrikmdev.core.connectivity.domain

import com.cadrikmdev.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface TrackerManagerDiscovery {

    fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Set<DeviceNode>>

    suspend fun connectToDevice(deviceAddress: String): Result<Boolean, BluetoothError>

    /**
     * Returns true if disconnecting was successfully completed or device is already disconnected
     */
    fun disconnectFromDevice(deviceAddress: String): Boolean
}