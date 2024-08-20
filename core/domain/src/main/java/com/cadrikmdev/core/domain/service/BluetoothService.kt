package com.cadrikmdev.core.domain.service

interface BluetoothService {
    fun isBluetoothEnabled(): Boolean

    fun openBluetoothSettings()
}