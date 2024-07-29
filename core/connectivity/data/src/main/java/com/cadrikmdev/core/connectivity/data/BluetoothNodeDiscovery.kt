package com.cadrikmdev.core.connectivity.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.cadrikmdev.core.connectivity.domain.DeviceNode
import com.cadrikmdev.core.connectivity.domain.DeviceType
import com.cadrikmdev.core.connectivity.domain.TrackerManagerDiscovery
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BluetoothNodeDiscovery(
    private val context: Context
) : TrackerManagerDiscovery {

    private val capabilityClient = Wearable.getCapabilityClient(context)

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    @SuppressLint("MissingPermission")



    override fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Set<DeviceNode>> {
        return callbackFlow {
            val remoteCapability = when (localDeviceType) {
                DeviceType.MANAGER -> "signal_tracker_manager_app"
                DeviceType.TRACKER -> "signal_tracker_tracker_app"
            }
            try {
                val pairedDevices: Set<DeviceNode> = if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    setOf()
                } else {
                    bluetoothAdapter?.bondedDevices.map {
                        DeviceNode(
                            address = it.address,
                            displayName = it.name,
                            isNearby = it.bondState == BluetoothDevice.BOND_BONDED,
                            type = it.type
                        )
                    }.toSet()
                }
                send(pairedDevices)
            } catch (e: ApiException) {
                awaitClose()
                return@callbackFlow
            }

            val serviceListener = object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {

                    val connectedDevices = proxy.connectedDevices
                    connectedDevices.forEach { device ->
                        val deviceNode = if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return
                        } else {
                            DeviceNode(
                                address = device.address,
                                displayName = device.name,
                                isNearby = device.bondState == BluetoothDevice.BOND_BONDED,
                                type = device.type,
                            )
                        }

                    }
                }

                override fun onServiceDisconnected(profile: Int) {
                    // Handle profile disconnection
                }
            }

            bluetoothAdapter?.getProfileProxy(context, serviceListener, BluetoothProfile.STATE_CONNECTED)
            val listener: (CapabilityInfo) -> Unit = {
                trySend(it.nodes.map { it.toDeviceNode() }.toSet())
            }
            capabilityClient.addListener(listener, remoteCapability)

            awaitClose {
                capabilityClient.removeListener(listener)
            }
        }
    }
}