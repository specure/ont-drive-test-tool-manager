package com.cadrikmdev.manager.presentation.manager_overview.mappers

import com.cadrikmdev.core.connectivity.domain.DeviceNode
import com.cadrikmdev.manager.presentation.manager_overview.model.TrackingDeviceUi

fun DeviceNode.toTrackingDeviceUI(): TrackingDeviceUi {
    return TrackingDeviceUi(
        address = this.address,
        status = this.type.toString(),
        name = this.displayName,
        updateTimestamp = System.currentTimeMillis()
    )
}