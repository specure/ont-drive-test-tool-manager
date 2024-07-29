package com.cadrikmdev.core.connectivity.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cadrikmdev.core.connectivity.domain.DeviceNode
import com.cadrikmdev.core.connectivity.domain.DeviceType
import com.cadrikmdev.core.connectivity.domain.TrackerManagerDiscovery
import com.cadrikmdev.manager.domain.ManagerControlServiceProtocol
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

class BluetoothNodeDiscovery(
    private val context: Context
) : TrackerManagerDiscovery {

    @SuppressLint("MissingPermission")
    override fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Set<DeviceNode>> {
        return callbackFlow {
            val remoteCapability = when (localDeviceType) {
                DeviceType.MANAGER -> "signal_tracker_manager_app"
                DeviceType.TRACKER -> "signal_tracker_tracker_app"
            }

            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                // Device doesn't support Bluetooth
                Timber.e("Device doesn't support Bluetooth")
                send(setOf())
                return@callbackFlow
            }

            if (!bluetoothAdapter.isEnabled) {
                // Bluetooth is not enabled
                Timber.d("Bluetooth is not enabled")
                // You can request user to enable Bluetooth here
                send(setOf())
                return@callbackFlow
            }

            if (getPairedDevicesEndedWithError(bluetoothAdapter)) return@callbackFlow


//            val serviceListener = object : BluetoothProfile.ServiceListener {
//                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
//
//                    val connectedDevices = proxy.connectedDevices
//                    connectedDevices.forEach { device ->
//                        val deviceNode = if (ActivityCompat.checkSelfPermission(
//                                context,
//                                Manifest.permission.BLUETOOTH_CONNECT
//                            ) != PackageManager.PERMISSION_GRANTED
//                        ) {
//                            // TODO: Consider calling
//                            //    ActivityCompat#requestPermissions
//                            // here to request the missing permissions, and then overriding
//                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                            //                                          int[] grantResults)
//                            // to handle the case where the user grants the permission. See the documentation
//                            // for ActivityCompat#requestPermissions for more details.
//                            return
//                        } else {
//                            DeviceNode(
//                                address = device.address,
//                                displayName = device.name,
//                                isNearby = device.bondState == BluetoothDevice.BOND_BONDED,
//                                type = device.type,
//                            )
//                        }
//
//                    }
//                }
//
//                override fun onServiceDisconnected(profile: Int) {
//                    // Handle profile disconnection
//                }
//            }

//            bluetoothAdapter?.getProfileProxy(context, serviceListener, BluetoothProfile.STATE_CONNECTED)
//            val listener: (CapabilityInfo) -> Unit = {
//                trySend(it.nodes.map { it.toDeviceNode() }.toSet())
//            }

            // BroadcastReceiver for changes in bonded state
            val bondStateReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    val pairedDevices = getPairedDevices(bluetoothAdapter)
                    Timber.d("Updating paired devices: $pairedDevices")
                    trySend(pairedDevices)

                    if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                        val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
//                        when (bondState) {
//                            BluetoothDevice.BOND_BONDED -> {
//                                Timber.d( "Paired with device: ${device?.name}")
//                            }
//                            BluetoothDevice.BOND_NONE -> {
//                                Timber.d("Unpaired with device: ${device?.name}")
//                            }
//                        }

                    }
                }
            }

            val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            context.registerReceiver(bondStateReceiver, filter)

            awaitClose {
                context.unregisterReceiver(bondStateReceiver)
                Timber.d("Unregistered bluetooth change receiver")
            }
        }
    }

    private suspend fun ProducerScope<Set<DeviceNode>>.getPairedDevicesEndedWithError(
        bluetoothAdapter: BluetoothAdapter
    ): Boolean {
        try {
            val pairedDevices: Set<DeviceNode> = getPairedDevices(bluetoothAdapter)
            send(pairedDevices)
        } catch (e: ApiException) {
            awaitClose()
            return true
        }
        return false
    }

    private fun getPairedDevices(bluetoothAdapter: BluetoothAdapter): Set<DeviceNode> {
        val pairedDevices: Set<DeviceNode> = if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            setOf()
        } else {
            bluetoothAdapter?.bondedDevices?.map {
                DeviceNode(
                    address = it.address,
                    displayName = it.name,
                    isNearby = it.bondState == BluetoothDevice.BOND_BONDED,
                    type = it.type
                )
            }?.toSet() ?: setOf()
        }
        return pairedDevices
    }

    private fun connectToDevice(device: BluetoothDevice) {
        // Ensure the location permission is granted (required for Bluetooth discovery from Android M+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        // Connect to the device and discover services
        device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    gatt?.services?.forEach { service: BluetoothGattService ->
                        if (service.uuid == ManagerControlServiceProtocol.customServiceUUID) {
                            Timber.d("Device supports custom service: ${device.name}")
                            // Device supports the custom service - you can proceed with communication
                        }
                    }
                }
            }

            // Handle other callback methods as needed
        })
    }
}