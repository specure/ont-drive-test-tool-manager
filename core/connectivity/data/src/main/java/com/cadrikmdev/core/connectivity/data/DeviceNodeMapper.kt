package com.cadrikmdev.core.connectivity.data

import com.cadrikmdev.core.connectivity.domain.DeviceNode
import com.google.android.gms.wearable.Node

fun Node.toDeviceNode(): DeviceNode {
    return DeviceNode(
        address = id,
        displayName = displayName,
        isNearby = isNearby
    )
}