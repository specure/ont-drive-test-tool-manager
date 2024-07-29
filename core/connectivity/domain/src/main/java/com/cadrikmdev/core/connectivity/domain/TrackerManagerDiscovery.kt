package com.cadrikmdev.core.connectivity.domain

import kotlinx.coroutines.flow.Flow

interface TrackerManagerDiscovery {

    fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Set<DeviceNode>>
}