package com.cadrikmdev.core.connectivity.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
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
import java.io.IOException

class BluetoothNodeDiscovery(
    private val context: Context,
    private val applicationScope: CoroutineScope,
) : TrackerManagerDiscovery {

    private var _pairedDevices = MutableStateFlow<Set<BluetoothDevice>>(setOf())
    val pairedDevices = _pairedDevices.asStateFlow()

    private var gattServer: BluetoothGattServer? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothServerSocket: BluetoothSocket? = null
    private var bluetoothClientSocket: BluetoothSocket? = null
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    @SuppressLint("MissingPermission")
    override fun observeConnectedDevices(localDeviceType: DeviceType): Flow<Set<DeviceNode>> {
        return callbackFlow {
            val remoteCapability = when (localDeviceType) {
                DeviceType.MANAGER -> "signal_tracker_manager_app"
                DeviceType.TRACKER -> "signal_tracker_tracker_app"
            }

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                // Device doesn't support Bluetooth
                Timber.e("Device doesn't support Bluetooth")
                send(setOf())
                return@callbackFlow
            }

            if (bluetoothAdapter?.isEnabled != true) {
                // Bluetooth is not enabled
                Timber.d("Bluetooth is not enabled")
                // You can request user to enable Bluetooth here
                send(setOf())
                return@callbackFlow
            }

            bluetoothAdapter?.let {
                if (getPairedDevicesEndedWithError(it)) return@callbackFlow
            }


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

                    val pairedDevices = bluetoothAdapter?.let {
                        getPairedDevices(it)
                    }
                    Timber.d("Updating paired devices: $pairedDevices")

                    val pairedNodes = pairedDevices?.mapNotNull {
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
            val pairedNodes = pairedDevices.mapNotNull { it.toDeviceNode() }.toSet()
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

        Timber.d("Connecting to device with address: ${deviceAddress}")
        Timber.d("Service uuid: ${ManagerControlServiceProtocol.customServiceUUID}")
        Timber.d("Connecting to device with supportedServices; ${bluetoothDevice.uuids.forEach { "${it.uuid}, " }}")
        bluetoothDevice.createRfcommSocketToServiceRecord(ManagerControlServiceProtocol.customServiceUUID)
        bluetoothDevice?.let {
            val clientSocket: BluetoothSocket =
                it.createRfcommSocketToServiceRecord(
                    ManagerControlServiceProtocol.customServiceUUID
                )
            // Accept connections from clients (running in a separate thread)
            Thread {
                clientSocket?.let { socket ->
                    // Connect to the remote device through the socket. This call blocks
                    // until it succeeds or throws an exception.
                    try {
                        socket.connect()
                        Timber.d("Connected to server socket successfully on ${bluetoothDevice.address}")
                    } catch (e: IOException) {
                        Timber.e("Unable to connect to server socket ${e.printStackTrace()}")
                    }

                    // The connection attempt succeeded. Perform work associated with
                    // the connection in a separate thread.
                    manageConnectedClientSocket(socket)
                }

            }.start()
        }

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

        gattServer = bluetoothManager.openGattServer(context, gattServerCallback)
        // Create the GATT service
        val gattService =
            BluetoothGattService(
                ManagerControlServiceProtocol.customServiceUUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY
            )

        // Create the GATT characteristic
        val characteristic = BluetoothGattCharacteristic(
            ManagerControlServiceProtocol.customCharacteristicServiceUUID,
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY or BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        // Optionally add a descriptor
        val descriptor = BluetoothGattDescriptor(
            ManagerControlServiceProtocol.customCharacteristicServiceUUID, // Client Characteristic Configuration UUID
            BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE
        )
        characteristic.addDescriptor(descriptor)

        // Add the characteristic to the service
        gattService.addCharacteristic(characteristic)

        // TODO: Add your service to a Bluetooth GATT server here (Not available in Android SDK directly, usually part of peripheral devices)


        gattServer?.addService(gattService)
//        bluetoothAdapter?.let {
//            startAdvertising(it)
//        }

        // You can set up Bluetooth classic (RFCOMM) or use BLE advertising for discovery
        // For RFCOMM, you would use something like the following:
        bluetoothAdapter?.let {
            val serverSocket: BluetoothServerSocket? =
                it.listenUsingRfcommWithServiceRecord(
                    "MyService",
                    ManagerControlServiceProtocol.customServiceUUID
                )
            // Accept connections from clients (running in a separate thread)
            Thread {
                var shouldLoop = true
                while (shouldLoop) {
                    try {
                        val socket: BluetoothSocket? = serverSocket?.accept()
                        socket?.let {
                            // Handle the connection in a separate thread
                            manageConnectedServerSocket(it)
                        }
                    } catch (e: IOException) {
                        Timber.e("Socket's accept() method failed", e)
                        shouldLoop = false
                    }
                }
            }.start()
        }

        val gatt = bluetoothDevice.connectGatt(context, true, callback)
            ?: return Result.Error(BluetoothError.GATT_CONNECTION_FAILED)
        // Handle the case if the connection fails to establish initially

        return resultDeferred.await()
        // Wait for the connection and services discovery to complete
        // You may need to implement additional logic to handle timeouts or state changes

    }


    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // Device connected
                Timber.d("GATT connected successfully")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Device disconnected
                Timber.d("GATT disconnected")
            }
        }

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice, requestId: Int,
            offset: Int, characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            if (characteristic.uuid == ManagerControlServiceProtocol.customCharacteristicServiceUUID) {
                val value = byteArrayOf(0x01, 0x02) // Example value
                gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value)
            }
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice, requestId: Int,
            characteristic: BluetoothGattCharacteristic, preparedWrite: Boolean,
            responseNeeded: Boolean, offset: Int, value: ByteArray
        ) {
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
            if (characteristic.uuid == ManagerControlServiceProtocol.customCharacteristicServiceUUID) {
                // Handle the write request
                if (responseNeeded) {
                    gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                }
            }
        }

        override fun onDescriptorWriteRequest(
            device: BluetoothDevice, requestId: Int,
            descriptor: BluetoothGattDescriptor, preparedWrite: Boolean,
            responseNeeded: Boolean, offset: Int, value: ByteArray
        ) {
            super.onDescriptorWriteRequest(
                device,
                requestId,
                descriptor,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )
            if (descriptor.uuid == ManagerControlServiceProtocol.customCharacteristicServiceUUID) {
                // Handle descriptor write request
                if (responseNeeded) {
                    gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                }
            }
        }
    }

    private fun manageConnectedServerSocket(socket: BluetoothSocket) {
        // Implement logic for communication with the connected client
        bluetoothServerSocket = socket
        // Read/write data using socket.inputStream and socket.outputStream
    }

    private fun manageConnectedClientSocket(socket: BluetoothSocket) {
        // Implement logic for communication with the connected server
        bluetoothClientSocket = socket
        // Read/write data using socket.inputStream and socket.outputStream
    }

    private fun isFineLocationPermissionGranted(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
    }


    private fun isBluetoothConnectPermissionGranted() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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

    fun closeClientSocket() {
        try {
            bluetoothClientSocket?.close()
        } catch (e: IOException) {
            Timber.e("Could not close the client socket", e)
        }
    }

    fun closeServerSocket() {
        try {
            bluetoothServerSocket?.close()
        } catch (e: IOException) {
            Timber.e("Could not close the server socket", e)
        }
    }

}