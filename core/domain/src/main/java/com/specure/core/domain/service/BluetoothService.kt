package com.specure.core.domain.service

interface BluetoothService {
    fun isBluetoothEnabled(): Boolean

    fun openBluetoothSettings()
}