package com.specure.manager.presentation.mappers

import com.cadrikmdev.intercom.domain.data.BluetoothDevice
import com.specure.manager.presentation.data.ManagedBluetoothDevice
import com.specure.track.domain.intercom.data.MeasurementState


fun BluetoothDevice.toManagedBluetoothDevice(): ManagedBluetoothDevice {
    return ManagedBluetoothDevice(
        device = this,
        status = MeasurementState.UNKNOWN,
        errors = null,
        deviceAppVersion = "",
    )
}