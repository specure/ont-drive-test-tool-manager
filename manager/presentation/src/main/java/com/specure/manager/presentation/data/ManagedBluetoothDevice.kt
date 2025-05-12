package com.specure.manager.presentation.data

import com.cadrikmdev.intercom.domain.data.BluetoothDevice
import com.specure.track.domain.intercom.data.MeasurementState
import com.specure.track.domain.intercom.data.TestError
import kotlinx.serialization.Serializable

@Serializable
data class ManagedBluetoothDevice(
    val device: BluetoothDevice,
    val status: MeasurementState,
    val errors: List<TestError>?,
    val deviceAppVersion: String,
) {
    fun isStateChangedOnTheSameDevice(otherDevice: ManagedBluetoothDevice?): Boolean {
        return isTheSameDevice(otherDevice) && !isTheSameStatus(otherDevice)
    }

    fun isTheSameDevice(otherDevice: ManagedBluetoothDevice?): Boolean {
        return device.isTheSameDevice(otherDevice?.device)
    }

    fun isErrorState(): Boolean {
        return this.status in listOf(MeasurementState.ERROR)
    }

    fun isSpeedTestErrorState(): Boolean {
        return this.status in listOf(MeasurementState.SPEEDTEST_ERROR)
    }

    fun isTheSameStatus(otherDevice: ManagedBluetoothDevice?): Boolean {
        return this.status == otherDevice?.status
    }
}

