package com.cadrikmdev.core.connectivity.domain

data class DeviceNode(
    val address: String,
    val displayName: String,
    val isNearby: Boolean,
    val type: Int = -1,
)
