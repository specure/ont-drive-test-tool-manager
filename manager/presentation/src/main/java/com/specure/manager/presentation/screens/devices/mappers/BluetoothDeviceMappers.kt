package com.specure.manager.presentation.screens.devices.mappers

import com.specure.intercom.domain.data.BluetoothDevice
import com.specure.manager.presentation.screens.devices.data.BluetoothDeviceUi

fun BluetoothDevice.toBluetoothDeviceUi() : BluetoothDeviceUi {
    return BluetoothDeviceUi(
        name = this.name,
        address = this.address,
        addedToBeManaged = false // todo change accordingly
    )
}