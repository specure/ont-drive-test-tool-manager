package com.specure.intercom.data.client

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.specure.core.domain.util.Result
import com.specure.intercom.data.util.isBluetoothConnectPermissionGranted
import com.specure.intercom.data.util.isBluetoothScanDevicesPermissionGranted
import com.specure.intercom.data.util.isFineLocationPermissionGranted
import com.specure.intercom.domain.ManagerControlServiceProtocol
import com.specure.intercom.domain.client.BluetoothClientService
import com.specure.intercom.domain.client.BluetoothError
import com.specure.intercom.domain.client.DeviceType
import com.specure.intercom.domain.client.TrackingDevice
import com.specure.intercom.domain.data.MeasurementState
import com.specure.intercom.domain.message.MessageProcessor
import com.specure.intercom.domain.message.TrackerAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

class AndroidBluetoothBleClientService(
    private val context: Context,
    private val applicationScope: CoroutineScope,
    private val messageProcessor: MessageProcessor,
) : BluetoothClientService {

    override val sendActionFlow: MutableStateFlow<TrackerAction?> =
        MutableStateFlow<TrackerAction?>(null)

    private val serviceUUID: UUID = ManagerControlServiceProtocol.customServiceUUID

    override val trackingDevices = MutableStateFlow<Map<String, TrackingDevice>>(mapOf())
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
    }
    private val connectedDevices = mutableMapOf<String, BluetoothGatt>()
    private val handler = Handler(Looper.getMainLooper())

    private val scanCallback = object : android.bluetooth.le.ScanCallback() {
        override fun onScanResult(callbackType: Int, result: android.bluetooth.le.ScanResult?) {
            Timber.d("New devices found:")
            result?.device?.let { device ->
                if (!context.isBluetoothConnectPermissionGranted()) {
                    return
                }
                Timber.d("Discovered BLE device: ${device.name} (${device.address}) ${device.type}")
                device.fetchUuidsWithSdp()
                val deviceUuids = device.uuids?.map { it.uuid } ?: emptyList()
                Timber.d("BluetoothFilter - Device UUIDs: $deviceUuids")

                Timber.d("Checking if device uses service uuid: $serviceUUID")

                if (deviceUuids.contains(serviceUUID)) {

                    val deviceInfo = TrackingDevice(
                        address = device.address,
                        name = device.name ?: "Unknown",
                        connected = false,
                        status = MeasurementState.UNKNOWN,
                        updateTimestamp = System.currentTimeMillis(),
                        deviceAppVersion = ""
                    )
                    applicationScope.launch {
                        trackingDevices.emit(trackingDevices.value + (device.address to deviceInfo))
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.e("BLE scan failed with error code: $errorCode")
        }
    }

//    override fun observeConnectedDevices(localDeviceType: DeviceType) = trackingDevices

    override suspend fun connectToDevice(deviceAddress: String): Result<Boolean, BluetoothError> {
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
            ?: return Result.Error(BluetoothError.BLUETOOTH_DEVICE_NOT_FOUND)

        return try {
//            if (!context.isBluetoothConnectPermissionGranted()) {
//                throw Exception("Missing BLUETOOTH_CONNECT permission to connect")
//            }
//            device.connectGatt(context, false, gattCallback)?.let { gatt ->
//                connectedDevices[deviceAddress] = gatt
//                Result.Success(true)
//            } ?:
        Result.Error(BluetoothError.UNABLE_TO_CONNECT)
        } catch (e: Exception) {
            Timber.e(e, "Error connecting to device $deviceAddress")
            Result.Error(BluetoothError.UNABLE_TO_CONNECT)
        }
    }

    override fun disconnectFromDevice(deviceAddress: String): Boolean {
        connectedDevices[deviceAddress]?.close()
        connectedDevices.remove(deviceAddress)
        applicationScope.launch {
            val updatedDevices = trackingDevices.value.mapValues {
                if (it.key == deviceAddress) it.value.copy(connected = false) else it.value
            }
            trackingDevices.emit(updatedDevices)
        }
        return true
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            val deviceAddress = gatt?.device?.address ?: return
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Timber.d("Connected to GATT server: $deviceAddress")
                if (!context.isBluetoothConnectPermissionGranted()) {
                    return
                }
                gatt.discoverServices()
                updateStatus(deviceAddress, connected = true)
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Timber.d("Disconnected from GATT server: $deviceAddress")
                updateStatus(deviceAddress, connected = false)
                if (!context.isBluetoothConnectPermissionGranted()) {
                    return
                }
                gatt.close()
                connectedDevices.remove(deviceAddress)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.d("Services discovered for device: ${gatt?.device?.address}")
                // Implement further logic for service discovery if needed
            } else {
                Timber.e("Service discovery failed with status: $status")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                characteristic?.value?.let { value ->
                    Timber.d("Characteristic read: ${String(value)}")
                }
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.d("Characteristic written successfully")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            characteristic?.value?.let { value ->
                Timber.d("Characteristic changed: ${String(value)}")
            }
        }
    }

    private fun updateStatus(address: String, connected: Boolean) {
        applicationScope.launch {
            val updatedDevices = trackingDevices.value.mapValues {
                if (it.key == address) it.value.copy(connected = connected) else it.value
            }
            trackingDevices.emit(updatedDevices)
        }
    }

    fun startDeviceDiscovery() {
        if (!context.isBluetoothScanDevicesPermissionGranted()) {
            Timber.e("Missing permissions for BLE scan start")
            return
        }
        bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)
        Timber.d("Started BLE scanning")
        handler.postDelayed({
            stopDeviceDiscovery()
        }, SCAN_DURATION)
    }

    fun stopDeviceDiscovery() {
        if (!isBluetoothScanPermissionsGranted()) {
            Timber.e("Missing permissions for BLE scan stop")
            return
        }
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        Timber.d("Stopped BLE scanning")
    }

    private fun isBluetoothScanPermissionsGranted(): Boolean {
        return context.isFineLocationPermissionGranted() && context.isFineLocationPermissionGranted()
    }

    companion object {
        private const val SCAN_DURATION = 10000L // 10 seconds
    }
}
