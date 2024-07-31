package com.cadrikmdev.core.connectivity.data.mappers

import android.bluetooth.BluetoothDevice
import com.cadrikmdev.core.connectivity.domain.DeviceNode
import timber.log.Timber

fun BluetoothDevice.toDeviceNode(): DeviceNode? {
    return try {
        DeviceNode(
            address = this.address,
            displayName = this.name,
            isPaired = this.bondState == BluetoothDevice.BOND_BONDED,
            type = this.type
        )
    } catch (e: SecurityException) {
        e.printStackTrace()
        Timber.e(e.message)
        null
    }
}