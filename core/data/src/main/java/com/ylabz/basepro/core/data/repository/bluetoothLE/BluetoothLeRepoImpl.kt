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

                    // Activate the sensors
                    coroutineScope.launch {
                        activateGattServices()
                    }

                    // Automatically read all characteristics after discovery
                    /*coroutineScope.launch {
                        readAllCharacteristics()
                    }*/

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


                    val serviceUUID = characteristic.service.uuid.toString()
                    val charUUID = characteristic.uuid.toString()
                    val parsedValue = characteristicParsers[charUUID]?.invoke(rawValue) ?: "Unknown Value"

                    Log.d(TAG, "Characteristic read: $charUUID, Parsed Value: $parsedValue")

                    updateCharacteristicValue(characteristic.service.uuid.toString(), charUUID, parsedValue)

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
        _gattServicesList.value = emptyList<DeviceService>()
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun activateGattServices() {
        val gatt = gatt ?: run {
            Log.e(TAG, "No GATT connection.")
            return
        }

        val activationMap = mapOf(
            "f000aa02-0451-4000-b000-000000000000" to byteArrayOf(0x01), // Temperature Enable
            "f000aa22-0451-4000-b000-000000000000" to byteArrayOf(0x01), // Humidity Enable
            "f000aa42-0451-4000-b000-000000000000" to byteArrayOf(0x01), // Barometer Enable
            "f000ac02-0451-4000-b000-000000000000" to byteArrayOf(0x01), // Accelerometer Enable
            "f000aa82-0451-4000-b000-000000000000" to byteArrayOf(0x01), // Magnetometer Enable
            "f000aa72-0451-4000-b000-000000000000" to byteArrayOf(0x07)  // Gyroscope Enable (0x07 for all axes)
        )

        for ((uuid, value) in activationMap) {
            val characteristic = gatt.services
                .flatMap { it.characteristics }
                .find { it.uuid == UUID.fromString(uuid) }

            if (characteristic != null) {
                try {
                    characteristic.value = value
                    val success = gatt.writeCharacteristic(characteristic)
                    if (success) {
                        Log.d(TAG, "Activated service: $uuid with value: ${value.joinToString(", ") { "0x%02X".format(it) }}")
                    } else {
                        Log.e(TAG, "Failed to activate service: $uuid")
                    }
                    // Add delay to ensure GATT queue clears before sending the next write
                    delay(1000) // 500ms delay to prevent overwhelming the GATT server
                } catch (e: Exception) {
                    Log.e(TAG, "Error writing to characteristic: $uuid. ${e.message}", e)
                }
            } else {
                Log.e(TAG, "Characteristic not found for UUID: $uuid")
            }
        }
    }



    // Update the value of a specific characteristic
    private fun updateCharacteristicValue(serviceUUID: String, charUUID: String, value: String) {
        coroutineScope.launch {
            _gattServicesList.update { currentServices ->
                currentServices.map { service ->
                    if (service.uuid == serviceUUID) {
                        service.copy(
                            characteristics = service.characteristics.map { char ->
                                if (char.uuid == charUUID) {
                                    char.copy(value = value)
                                } else {
                                    char
                                }
                            }
                        )
                    } else {
                        service
                    }
                }
            }
        }
    }
}

// Make everything human readable
// Map of characteristic UUIDs to their corresponding parsers
// Updated map of characteristic UUIDs to their corresponding parsers
private val characteristicParsers = mapOf(
    "00002a00-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) }, // Device Name (String)
    "00002a01-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> parseAppearance(raw) }, // Appearance (Int)
    "00002a23-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> parseSystemId(raw) }, // System ID (Hex)
    "00002a04-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> parseConnectionParameters(raw) }, // Connection Params
    "00002a19-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> "${raw.getOrNull(0)?.toInt() ?: 0}%" }, // Battery Level (Percentage)
    "00002a24-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) }, // Model Number String (String)
    "00002a25-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) }, // Serial Number String (String)
    "00002a26-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) }, // Firmware Revision String (String)
    "00002a27-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) }, // Hardware Revision String (String)
    "00002a28-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) }, // Software Revision String (String)
    "00002a29-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) }, // Manufacturer Name String (String)
    "00002a2a-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> "Experimental Certification Data: ${raw.toString(Charsets.UTF_8)}" }, // IEEE 11073-20601 Regulatory Certification Data List
    "00002a50-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> parsePnPId(raw) }, // PnP ID
    "f000aa01-0451-4000-b000-000000000000" to { raw: ByteArray -> parseTemperature(raw) }, // Temperature Data
    "f000aa21-0451-4000-b000-000000000000" to { raw: ByteArray -> parseHumidity(raw) }, // Humidity Data
    "f000aa41-0451-4000-b000-000000000000" to { raw: ByteArray -> parseBarometerData(raw) }, // Barometer Data
    "f000ac01-0451-4000-b000-000000000000" to { raw: ByteArray -> parseAccelerometerData(raw) }, // Accelerometer Data
    "f000aa81-0451-4000-b000-000000000000" to { raw: ByteArray -> parseMagnetometerData(raw) }, // Magnetometer Data
    "f000aa71-0451-4000-b000-000000000000" to { raw: ByteArray -> parseGyroscopeData(raw) } // Gyroscope Data
)

// Example parser for PnP ID
private fun parsePnPId(raw: ByteArray): String {
    return if (raw.size >= 7) {
        val vendorIdSource = raw[0].toInt()
        val vendorId = (raw[2].toInt() shl 8) or (raw[1].toInt() and 0xFF)
        val productId = (raw[4].toInt() shl 8) or (raw[3].toInt() and 0xFF)
        val productVersion = (raw[6].toInt() shl 8) or (raw[5].toInt() and 0xFF)
        "Vendor ID Source: $vendorIdSource, Vendor ID: $vendorId, Product ID: $productId, Product Version: $productVersion"
    } else {
        "Invalid PnP ID"
    }
}


private fun parseSystemId(raw: ByteArray): String {
    return if (raw.size >= 8) {
        val manufacturerId = raw.copyOfRange(0, 5).joinToString(":") { "%02X".format(it) }
        val organizationId = raw.copyOfRange(5, 8).joinToString(":") { "%02X".format(it) }
        "Manufacturer ID: $manufacturerId, Organization ID: $organizationId"
    } else {
        "Invalid System ID"
    }
}


private fun parseAppearance(raw: ByteArray): String {
    return if (raw.size >= 2) {
        val appearance = (raw[1].toInt() shl 8) or (raw[0].toInt() and 0xFF)
        "Appearance Code: $appearance"
    } else {
        "Unknown Appearance"
    }
}

private fun parseConnectionParameters(raw: ByteArray): String {
    return if (raw.size >= 8) {
        val minConnInterval = (raw[1].toInt() shl 8) or (raw[0].toInt() and 0xFF)
        val maxConnInterval = (raw[3].toInt() shl 8) or (raw[2].toInt() and 0xFF)
        val slaveLatency = (raw[5].toInt() shl 8) or (raw[4].toInt() and 0xFF)
        "Min Interval: ${minConnInterval * 1.25} ms, Max Interval: ${maxConnInterval * 1.25} ms, Slave Latency: $slaveLatency"
    } else {
        "Invalid Connection Parameters"
    }
}

private fun parseTemperature(raw: ByteArray): String {
    return if (raw.size >= 4) {
        val ambientTemp = (raw[2].toInt() shl 8 or (raw[3].toInt() and 0xFF)) / 128.0
        "Temperature: %.2f°C".format(ambientTemp)
    } else {
        "Invalid Temperature Data"
    }
}

private fun parseHumidity(raw: ByteArray): String {
    return if (raw.size >= 4) {
        val humidity = ((raw[2].toInt() shl 8) or (raw[3].toInt() and 0xFF)) / 65536.0 * 100
        "Humidity: %.1f%%".format(humidity)
    } else {
        "Invalid Humidity Data"
    }
}

private fun parseBarometerData(raw: ByteArray): String {
    return if (raw.size >= 6) {
        val pressure = ((raw[2].toInt() shl 16) or (raw[1].toInt() shl 8) or (raw[0].toInt() and 0xFF))
        "Pressure: ${pressure / 100.0} hPa"
    } else {
        "Invalid Barometer Data"
    }
}

private fun parseAccelerometerData(raw: ByteArray): String {
    return if (raw.size >= 6) {
        val x = raw[0].toInt()
        val y = raw[1].toInt()
        val z = raw[2].toInt()
        "X: $x, Y: $y, Z: $z"
    } else {
        "Invalid Accelerometer Data"
    }
}

private fun parseMagnetometerData(raw: ByteArray): String {
    return if (raw.size >= 6) {
        val x = (raw[1].toInt() shl 8) or (raw[0].toInt() and 0xFF)
        val y = (raw[3].toInt() shl 8) or (raw[2].toInt() and 0xFF)
        val z = (raw[5].toInt() shl 8) or (raw[4].toInt() and 0xFF)
        "Magnetometer X: $x, Y: $y, Z: $z"
    } else {
        "Invalid Magnetometer Data"
    }
}

private fun parseGyroscopeData(raw: ByteArray): String {
    return if (raw.size >= 6) {
        val x = (raw[1].toInt() shl 8) or (raw[0].toInt() and 0xFF)
        val y = (raw[3].toInt() shl 8) or (raw[2].toInt() and 0xFF)
        val z = (raw[5].toInt() shl 8) or (raw[4].toInt() and 0xFF)
        "Gyroscope X: $x, Y: $y, Z: $z"
    } else {
        "Invalid Gyroscope Data"
    }
}

