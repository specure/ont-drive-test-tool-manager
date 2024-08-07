package com.cadrikmdev.manager.presentation.manager_overview.mappers

import com.cadrikmdev.intercom.domain.client.DeviceNode
import com.cadrikmdev.manager.presentation.manager_overview.model.TrackingDeviceUi

fun com.cadrikmdev.intercom.domain.client.DeviceNode.toTrackingDeviceUI(): TrackingDeviceUi {
    return TrackingDeviceUi(
        address = this.address,
        status = this.type.toString(),
        name = this.displayName,
        updateTimestamp = System.currentTimeMillis()
    )
}