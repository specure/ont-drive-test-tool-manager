package com.specure.manager.presentation.screens.devices.mappers

import com.cadrikmdev.intercom.domain.data.BluetoothDevice
import com.specure.manager.presentation.screens.devices.data.BluetoothDeviceUi

fun BluetoothDevice.toBluetoothDeviceUi(addedToBeManaged: Boolean = false) : BluetoothDeviceUi {
    return BluetoothDeviceUi(
        name = this.displayName,
        address = this.address,
        addedToBeManaged = addedToBeManaged
    )
}