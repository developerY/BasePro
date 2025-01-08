package com.ylabz.basepro.core.data.repository.bluetoothLE

import android.Manifest
import android.R.attr.data
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.DeviceCharacteristic
import com.ylabz.basepro.core.model.ble.DeviceService
import com.ylabz.basepro.core.model.ble.GattCharacteristicValue
import com.ylabz.basepro.core.model.ble.GattConnectionState
import com.ylabz.basepro.core.model.ble.ScanState
import com.ylabz.basepro.core.model.ble.tools.getHumanReadableName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject


/*
ID : 87:78:86:00:00:F2:F8:F0
CC2650 SensorTag

Device found - Name: CC2650 SensorTag, Address: F0:F8:F2:86:78:87, RSSI: -46
onScanResult - callbackType: 1, result: ScanResult{device=F0:F8:F2:86:78:87,
scanRecord=ScanRecord [mAdvertiseFlags=5, mServiceUuids=[0000aa80-0000-1000-8000-00805f9b34fb],
mServiceSolicitationUuids=[], mManufacturerSpecificData={13=[3, 0, 0]}, mServiceData={},
mTxPowerLevel=0, mDeviceName=CC2650 SensorTag], rssi=-46, timestampNanos=56301926474451,
eventType=27, primaryPhy=1, secondaryPhy=0, advertisingSid=255, txPower=127,
periodicAdvertisingInterval=0}
 */

class BluetoothLeRepImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) : BluetoothLeRepository {
    private val TAG = "BluetoothLeRepImpl"

    private var gatt: BluetoothGatt? = null

    // Cache the connected GATT for reference
    fun getCachedGatt(): BluetoothGatt? = gatt


    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val currentFilter = "SensorTag"

    private val _currentDevice: MutableStateFlow<BluetoothDeviceInfo?> = MutableStateFlow(null)
    val currentDevice: StateFlow<BluetoothDeviceInfo?> = _currentDevice

    private val _scanState = MutableStateFlow(ScanState.NOT_SCANNING)
    override val scanState: StateFlow<ScanState> = _scanState

    // StateFlow to hold battery level percentage
    private val _gattCharacteristicList =
        MutableStateFlow<List<GattCharacteristicValue>>(emptyList())
    override val gattCharacteristicList: StateFlow<List<GattCharacteristicValue>> =
        _gattCharacteristicList

    private val _gattServicesList = MutableStateFlow<List<DeviceService>>(emptyList())
    override val gattServicesList: StateFlow<List<DeviceService>> get() = _gattServicesList

    private val _gattConnectionState =
        MutableStateFlow<GattConnectionState>(GattConnectionState.Disconnected)
    override val gattConnectionState: StateFlow<GattConnectionState> = _gattConnectionState

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    var tagSensorFound: BluetoothDeviceInfo? = null
    private val scanStateMutex = Mutex()

    private val scanCallback = object : ScanCallback() {

        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val deviceName = result.device.name ?: "Unknown Device"
            val deviceAddress = result.device.address ?: "Unknown Address"
            val deviceRssi = result.rssi

            coroutineScope.launch {
                var shouldStopScan = false
                var deviceToEmit: BluetoothDeviceInfo? = null

                scanStateMutex.withLock {
                    if (deviceName.contains("CC2650 SensorTag", ignoreCase = true) &&
                        _scanState.value != ScanState.STOPPING
                    ) {
                        // Update the state and store the SensorTag device
                        _scanState.value = ScanState.STOPPING
                        tagSensorFound = BluetoothDeviceInfo(
                            name = deviceName,
                            address = deviceAddress,
                            rssi = deviceRssi
                        )
                        shouldStopScan = true
                    }

                    // Prepare the device to emit
                    deviceToEmit = tagSensorFound ?: BluetoothDeviceInfo(
                        name = deviceName,
                        address = deviceAddress,
                        rssi = deviceRssi
                    )
                }

                // Execute suspend functions outside the critical section
                if (shouldStopScan) {
                    try {
                        stopScan() // Stop scanning
                        Log.d(TAG, "Stopping scan after detecting SensorTag.")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error stopping scan: ${e.message}", e)
                    } finally {
                        scanStateMutex.withLock {
                            _scanState.value = ScanState.NOT_SCANNING
                        }
                    }
                }

                deviceToEmit?.let {
                    _currentDevice.emit(it)
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun connectToDevice() {
        val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(tagSensorFound?.address)
        _gattConnectionState.value = GattConnectionState.Connecting
        gatt = device.connectGatt(context, false, object : BluetoothGattCallback() {

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "Connected to GATT server.")
                    _gattConnectionState.value = GattConnectionState.Connected
                    gatt.discoverServices()
                    this@BluetoothLeRepImpl.gatt = gatt
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG, "Disconnected from GATT server.")
                    _gattConnectionState.value = GattConnectionState.Disconnected
                }
            }

            // Called when services are discovered
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "GATT services discovered. Caching services and characteristics...")

                    val services = gatt.services.map { service ->
                        val serviceUUID = service.uuid.toString()
                        val serviceName = getHumanReadableName(serviceUUID)

                        val characteristics = service.characteristics.map { characteristic ->
                            val charUUID = characteristic.uuid.toString()
                            val charName = getHumanReadableName(charUUID)
                            DeviceCharacteristic(
                                uuid = charUUID,
                                name = charName,
                                isReadable = characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0,
                                isWritable = characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0,
                                isNotifiable = characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0,
                                value = "Not Set"
                            )
                        }

                        DeviceService(
                            uuid = serviceUUID,
                            name = serviceName,
                            characteristics = characteristics
                        )
                    }

                    _gattServicesList.value = services

                    Log.d(TAG, "Cached ${_gattServicesList.value.size} services.")
                } else {
                    Log.e(TAG, "Failed to discover GATT services. Status: $status")
                }
            }

            // Called when a characteristic is read
            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                if (status == BluetoothGatt.GATT_SUCCESS && characteristic != null) {
                    val description = getHumanReadableName(characteristic.uuid.toString())
                    val rawValue = characteristic.value ?: ByteArray(0)  // Get the raw bytes

                    val stringValue = characteristic.getStringValue(0) ?: "N/A"
                    val intValue = rawValue.getOrNull(0)?.toInt() ?: -1
                    val hexValue = rawValue.joinToString(" ") { byte -> "%02X".format(byte) }
                    val length = rawValue.size

                    Log.d(TAG, "Characteristic read: ${characteristic.uuid}, $description")
                    Log.d(TAG, "Raw Value (Hex): $hexValue")
                    Log.d(TAG, "String Value: $stringValue")
                    Log.d(TAG, "First Byte as Int: $intValue")
                    Log.d(TAG, "Length: $length bytes")

                    // Add the characteristic to the list with a summary of all values
                    val summary = """
            Description: $description
            Hex Value: $hexValue
            String Value: $stringValue
            First Byte (Int): $intValue
            Length: $length bytes
        """.trimIndent()
                    Log.d(TAG, "Starting to add characteristic to list")
                    coroutineScope.launch {
                        _gattCharacteristicList.update { currentList ->
                            currentList + GattCharacteristicValue(description, summary)
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to read characteristic. Status: $status")
                }
            }

        })
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    override suspend fun fetchBluetoothDevice(): StateFlow<BluetoothDeviceInfo?> {
        return currentDevice// Return devices if required
    }

    /*fun disconnectFromDevice() {
        gatt?.close()
        gatt = null
        _gattConnectionState.value = GattConnectionState.Disconnected
    }*/

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override suspend fun startScan() {
        // Reset the current device to ensure clean feedback
        // Create the device info object
        synchronized(_scanState) {
            if (_scanState.value == ScanState.NOT_SCANNING) {
                tagSensorFound = null
                _scanState.value = ScanState.SCANNING
                try {
                    bleScanner.startScan(null, scanSettings, scanCallback)
                    Log.d(TAG, "startScan - Scan started successfully.")
                } catch (e: Exception) {
                    _scanState.value = ScanState.NOT_SCANNING
                    Log.e(TAG, "startScan - Error starting scan: ${e.message}", e)
                }
            } else {
                Log.d(TAG, "startScan - Scan already running or stopping.")
            }
        }
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override suspend fun stopScan() {
        synchronized(_scanState) {
            if (_scanState.value == ScanState.NOT_SCANNING) {
                Log.d(TAG, "stopScan - No active scan to stop.")
                return
            }
            try {
                bleScanner.stopScan(scanCallback)
                Log.d(TAG, "stopScan - Scan stopped successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "stopScan - Error stopping scan: ${e.message}", e)
            } finally {
                _scanState.value = ScanState.NOT_SCANNING
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun readAllCharacteristics() {
        val gatt = gatt ?: run {
            Log.e(TAG, "No GATT connection.")
            return
        }
        Log.d(TAG, "Starting to read all characteristics. new ")
        Log.d(TAG, "number of services: ${gatt.services.size}")
        for (service in gatt.services) {
            Log.d(
                TAG,
                "Service: ${getHumanReadableName(service.uuid.toString())} (${service.uuid})"
            )
            for (characteristic in service.characteristics) {
                if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {

                    delay(1000)  // Add a short delay to allow the device to process requests
                    Log.d(TAG, "Attempting delay to read characteristic: ${characteristic.uuid}")
                    val result = try {
                        val success = gatt.readCharacteristic(characteristic)
                        //val success = true
                        if (!success) {
                            Log.e(
                                TAG,
                                "Failed to initiate read for characteristic: ${characteristic.uuid}"
                            )
                        } else {
                            Log.d(
                                TAG,
                                "Read request sent successfully for characteristic: ${characteristic.uuid}"
                            )
                        }
                    } catch (e: Exception) {
                        "Error: ${e.message}"
                    }
                    Log.d(TAG, "Characteristic read complete: ${characteristic.uuid} = $result \n")

                    // Small delay to ensure GATT queue has time to clear
                } else {
                    Log.d(
                        TAG,
                        "Characteristic: ${getHumanReadableName(characteristic.uuid.toString())} (${characteristic.uuid}) is not readable."
                    )
                }
            }
        }

        Log.d(TAG, "Finished reading all characteristics.")
    }
}