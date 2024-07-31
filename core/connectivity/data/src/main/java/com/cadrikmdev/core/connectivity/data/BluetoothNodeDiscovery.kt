package com.cadrikmdev.core.connectivity.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.cadrikmdev.core.connectivity.data.mappers.toDeviceNode
import com.cadrikmdev.core.connectivity.domain.BluetoothError
import com.cadrikmdev.core.connectivity.domain.DeviceNode
import com.cadrikmdev.core.connectivity.domain.DeviceType
import com.cadrikmdev.core.connectivity.domain.TrackerManagerDiscovery
import com.cadrikmdev.domain.util.Result
import com.cadrikmdev.manager.domain.ManagerControlServiceProtocol
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class BluetoothNodeDiscovery(
    private val context: Context,
    private val applicationScope: CoroutineScope,
) : TrackerManagerDiscovery {

    private var _pairedDevices = MutableStateFlow<Set<BluetoothDevice>>(setOf())
    val pairedDevices = _pairedDevices.asStateFlow()

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

                    val pairedNodes = pairedDevices.mapNotNull {
                        it.toDeviceNode()
                    }?.toSet() ?: setOf()
                    trySend(pairedNodes)

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
            val pairedDevices: Set<BluetoothDevice> = getPairedDevices(bluetoothAdapter)
//            val pairedDevicesWithSupportedService = pairedDevices.forEach { deviceNode ->
//                val device = bluetoothAdapter.getRemoteDevice(deviceNode.address)
//                connectToDevice(device) { connectedDeviceNode ->
//                    trySend(pairedDevices.filter { it.address == connectedDeviceNode.address }.toSet())
//                }
//            }
            Timber.d("Obtaining paired devices ${pairedDevices}")
            val pairedNodes = pairedDevices.mapNotNull {it.toDeviceNode()}.toSet()
            trySend(pairedNodes)
        } catch (e: ApiException) {
            awaitClose()
            return true
        }
        return false
    }

    private fun getPairedDevices(bluetoothAdapter: BluetoothAdapter): Set<BluetoothDevice> {
        val pairedDevices: Set<BluetoothDevice> = if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            setOf()
        } else {
            bluetoothAdapter.bondedDevices
        }
        applicationScope.launch {
            _pairedDevices.emit(pairedDevices)
        }
        return pairedDevices
    }

    override suspend fun connectToDevice(deviceAddress: String): Result<Boolean, BluetoothError> {
        // Ensure the location permission is granted (required for Bluetooth discovery from Android M+)
        if (isFineLocationPermissionGranted()) return Result.Error(BluetoothError.NO_FINE_LOCATION_PERMISSIONS)
        if (!isBluetoothConnectPermissionGranted()) {
            return Result.Error(BluetoothError.MISSING_BLUETOOTH_CONNECT_PERMISSION)
        }

        val bluetoothDevice = getBluetoothDeviceFromDeviceAddress(deviceAddress)
            ?: return Result.Error(BluetoothError.BLUETOOTH_DEVICE_NOT_FOUND)

        // Use CompletableDeferred to wait for the result
        val resultDeferred = CompletableDeferred<Result<Boolean, BluetoothError>>()

        // Connect to the device and discover services
        val callback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (!isBluetoothConnectPermissionGranted()) {
                    resultDeferred.complete(Result.Error(BluetoothError.MISSING_BLUETOOTH_CONNECT_PERMISSION))
                    Timber.e("Missing Bluetooth connect permissions")
                    return
                }

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Timber.d("Connected to GATT server.")
                    gatt?.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Timber.d("Disconnected from GATT server.")
                    gatt?.close()
                    resultDeferred.complete(Result.Error(BluetoothError.GATT_DISCONNECTED))
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                if (!isBluetoothConnectPermissionGranted()) {
                    resultDeferred.complete(Result.Error(BluetoothError.MISSING_BLUETOOTH_CONNECT_PERMISSION))
                    Timber.e("Missing Bluetooth connect permissions")
                    return
                }

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Timber.d("GATT services: ${gatt?.services}")
                    val service = gatt?.services?.firstOrNull {
                        it.uuid == ManagerControlServiceProtocol.customServiceUUID
                    }
                    if (service != null) {
                        Timber.d("Device supports custom service: ${bluetoothDevice.name}")
                        resultDeferred.complete(Result.Success(true))
                    } else {
                        resultDeferred.complete(Result.Error(BluetoothError.SERVICE_NOT_FOUND))
                    }
                    gatt?.close()
                } else {
                    Timber.e("onServicesDiscovered received: $status")
                    resultDeferred.complete(Result.Error(BluetoothError.SERVICE_DISCOVERY_FAILED))
                    gatt?.close()
                }
            }
            // Handle other callback methods as needed
        }

        val gatt = bluetoothDevice.connectGatt(context, true, callback)
            ?: return Result.Error(BluetoothError.GATT_CONNECTION_FAILED)
        // Handle the case if the connection fails to establish initially

        return  resultDeferred.await()
        // Wait for the connection and services discovery to complete
        // You may need to implement additional logic to handle timeouts or state changes
    }

    private fun isFineLocationPermissionGranted(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
    }


    private fun isBluetoothConnectPermissionGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    private fun getBluetoothDeviceFromDeviceAddress(deviceAddress: String): BluetoothDevice? {
        return try {
            _pairedDevices.value.first { it.address == deviceAddress }
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
            null
        }
    }
}