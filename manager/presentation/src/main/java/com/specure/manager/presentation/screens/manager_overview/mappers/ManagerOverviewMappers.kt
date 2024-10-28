package com.specure.manager.presentation.screens.manager_overview.mappers

import com.specure.intercom.domain.client.DeviceNode
import com.specure.intercom.domain.client.TrackingDevice

fun DeviceNode.toTrackingDeviceUI(): TrackingDevice {
    return TrackingDevice(
        address = this.address,
        status = this.status.toString(),
        name = this.displayName,
        connected = this.connected,
        deviceAppVersion = this.deviceAppVersion,
        updateTimestamp = lastUpdatedTimestamp
    )
}