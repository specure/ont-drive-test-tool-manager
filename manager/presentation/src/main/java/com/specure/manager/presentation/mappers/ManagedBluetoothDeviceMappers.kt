package com.specure.manager.presentation.mappers

import com.cadrikmdev.intercom.domain.data.BluetoothDevice
import com.specure.manager.domain.intercom.data.MeasurementState
import com.specure.manager.presentation.data.ManagedBluetoothDevice


fun BluetoothDevice.toManagedBluetoothDevice(): ManagedBluetoothDevice {
    return ManagedBluetoothDevice(
        device = this,
        status = MeasurementState.UNKNOWN,
        deviceAppVersion = "",
    )
}