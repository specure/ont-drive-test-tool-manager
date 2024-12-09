package com.specure.intercom.domain

import com.specure.intercom.domain.client.DeviceType
import com.specure.intercom.domain.data.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothDevicesProvider<T> {

    val pairedDevices: StateFlow<Map<String, BluetoothDevice>>

//    val nearbyPairedDevices: StateFlow<Map<String, BluetoothDevice>>

    val nativePairedDevices: StateFlow<Map<String, T>>

//    val nativeNearbyPairedDevices: StateFlow<Map<String, T>>

    fun getPairedDevices(): Map<String, BluetoothDevice>

    fun getNativeBluetoothDeviceFromDeviceAddress(deviceAddress: String): T?

    fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Map<String, BluetoothDevice>>

//    fun startDiscovery()

}