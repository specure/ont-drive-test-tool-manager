package com.cadrikmdev.manager.presentation.screens.manager_overview.mappers

import com.cadrikmdev.intercom.domain.client.DeviceNode
import com.cadrikmdev.intercom.domain.client.TrackingDevice

fun DeviceNode.toTrackingDeviceUI(): TrackingDevice {
    return TrackingDevice(
        address = this.address,
        status = this.status.toString(),
        name = this.displayName,
        connected = this.connected,
        updateTimestamp = lastUpdatedTimestamp
    )
}