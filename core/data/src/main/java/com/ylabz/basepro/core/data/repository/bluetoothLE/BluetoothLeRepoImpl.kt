package com.ylabz.basepro.core.data.repository.bluetoothLE

import android.Manifest
import android.R.attr.data
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject
import kotlin.math.pow

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

// [Barometer-related UUIDs remain the same...]
private val BARO_CONFIG_UUID = UUID.fromString("f000aa42-0451-4000-b000-000000000000")
private val BARO_CALIB_UUID  = UUID.fromString("f000aa43-0451-4000-b000-000000000000")
private val BARO_DATA_UUID   = UUID.fromString("f000aa41-0451-4000-b000-000000000000")

// [ADDED for LUX] - The CC2650 Luxometer (Light) UUIDs:
private val LUX_DATA_UUID  = UUID.fromString("f000aa71-0451-4000-b000-000000000000")
private val LUX_CONF_UUID  = UUID.fromString("f000aa72-0451-4000-b000-000000000000") // 0: disable, 1: enable
private val LUX_PERI_UUID  = UUID.fromString("f000aa73-0451-4000-b000-000000000000") // Period

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

    private val _currentDevice = MutableStateFlow<BluetoothDeviceInfo?>(null)
    val currentDevice: StateFlow<BluetoothDeviceInfo?> = _currentDevice

    private val _scanState = MutableStateFlow(ScanState.NOT_SCANNING)
    override val scanState: StateFlow<ScanState> = _scanState

    private val _gattCharacteristicList =
        MutableStateFlow<List<GattCharacteristicValue>>(emptyList())
    override val gattCharacteristicList: StateFlow<List<GattCharacteristicValue>> = _gattCharacteristicList

    private val _gattServicesList = MutableStateFlow<List<DeviceService>>(emptyList())
    override val gattServicesList: StateFlow<List<DeviceService>> = _gattServicesList

    private val _gattConnectionState =
        MutableStateFlow<GattConnectionState>(GattConnectionState.Disconnected)
    override val gattConnectionState: StateFlow<GattConnectionState> = _gattConnectionState

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    var tagSensorFound: BluetoothDeviceInfo? = null
    private val scanStateMutex = Mutex()

    // Storage for barometer calibration bytes (older firmware only)
    private var barometerCalibrationData: ByteArray? = null

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

                    coroutineScope.launch {
                        try {
                            Log.d(TAG, "Activating GATT services...")
                            activateGattServices() // Activates all required services
                            delay(1000) // Short delay to ensure services have stabilized

                            Log.d(TAG, "Activating temperature-specific configuration...")
                            activateTemperatureSensor() // Activates temperature config and period
                            delay(1000) // Allow the GATT queue to clear

                            // [ADDED for LUX] Enable the luxometer sensor as well (optional)
                            activateLuxometerSensor()
                            delay(1000)

                            val deviceName = tagSensorFound?.name ?: "Unknown"
                            if (deviceName.contains("CC2650 SensorTag", ignoreCase = true)) {
                                // For CC2650 firmware >= 1.50, there's no manual baro calibration char
                                Log.d(TAG, "Skipping manual barometer calibration for CC2650.")
                            } else {
                                Log.d(TAG, "Requesting barometer calibration (writing 0x02 to Barometer Config).")
                                requestBarometerCalibration()
                                delay(1000)
                            }

                            Log.d(TAG, "Ready to read all characteristics...")
                            //readAllCharacteristics() // Reads all sensor data
                        } catch (e: Exception) {
                            Log.e(TAG, "Error during GATT operations: ${e.message}", e)
                        }
                    }

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

                    // debug temp
                    if (charUUID == "f000aa01-0451-4000-b000-000000000000") {
                        Log.d(TAG, "Reading Temperature Data Characteristic...")

                        val rawValue = characteristic.value ?: ByteArray(0)
                        Log.d(TAG, "Raw Value Length: ${rawValue.size} bytes")
                        rawValue.forEachIndexed { index, byte ->
                            Log.d(TAG, "Byte $index: ${byte.toInt() and 0xFF}")
                        }

                        val parsedValue = characteristicParsers[charUUID]?.invoke(rawValue) ?: "Unknown Value"
                        Log.d(TAG, "Parsed Temperature Value: $parsedValue")
                    } else {
                        Log.d(TAG, "Non-temperature characteristic read: $charUUID")
                    }

                    // If we just read the barometer calibration characteristic, store it
                    if (characteristic.uuid == BARO_CALIB_UUID) {
                        barometerCalibrationData = rawValue.copyOf()
                        Log.d(TAG, "Barometer calibration data received (length=${rawValue.size}): $hexValue")
                    }

                } else {
                    Log.e(TAG, "Failed to read characteristic. Status: $status")
                }
            }

            // Called when a characteristic has changed (notification/indication)
            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val rawValue = characteristic.value ?: ByteArray(0)
                val charUUID = characteristic.uuid.toString()

                // If it's the barometer data characteristic and we have calibration data, parse with calibration
                if (characteristic.uuid == BARO_DATA_UUID && barometerCalibrationData != null) {
                    val debugHex = rawValue.joinToString(" ") { "%02X".format(it) }
                    Log.d(TAG, "onCharacteristicChanged (Barometer) - raw: $debugHex")

                    val calibratedString = parseBarometerDataWithCalibration(rawValue, barometerCalibrationData!!)
                    Log.d(TAG, "Barometer with calibration: $calibratedString")

                    updateCharacteristicValue(characteristic.service.uuid.toString(), charUUID, calibratedString)
                } else {
                    // Otherwise, use the normal parse logic
                    val parsedValue = characteristicParsers[charUUID]?.invoke(rawValue) ?: "Unknown Value"
                    Log.d(TAG, "onCharacteristicChanged -> $charUUID, Value: $parsedValue")

                    updateCharacteristicValue(characteristic.service.uuid.toString(), charUUID, parsedValue)
                }
            }
        })
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    override suspend fun fetchBluetoothDevice(): StateFlow<BluetoothDeviceInfo?> {
        return currentDevice
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override suspend fun startScan() {
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
            Log.d(TAG, "Service: ${getHumanReadableName(service.uuid.toString())} (${service.uuid})")
            for (characteristic in service.characteristics) {
                if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {

                    delay(1000)
                    Log.d(TAG, "Attempting delay to read characteristic: ${characteristic.uuid}")
                    val result = try {
                        val success = gatt.readCharacteristic(characteristic)
                        if (!success) {
                            Log.e(TAG, "Failed to initiate read for characteristic: ${characteristic.uuid}")
                        } else {
                            Log.d(TAG, "Read request sent successfully for characteristic: ${characteristic.uuid}")
                        }
                    } catch (e: Exception) {
                        "Error: ${e.message}"
                    }
                    Log.d(TAG, "Characteristic read complete: ${characteristic.uuid} = $result \n")
                } else {
                    Log.d(TAG, "Characteristic: ${getHumanReadableName(characteristic.uuid.toString())} (${characteristic.uuid}) is not readable.")
                }
            }
        }
        Log.d(TAG, "Finished reading all characteristics.")
        delay(5000)
        // start polling for temperature data
        Log.d(TAG, "~~~~~~~~~~~~~~~~~~~Starting to poll temperature data.")
        pollTemperatureData()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun activateGattServices() {
        val gatt = gatt ?: run {
            Log.e(TAG, "No GATT connection.")
            return
        }

        // [MODIFIED for LUX / fixed Gyro?]
        // Check that these UUIDs match TI's actual GATT docs for each sensor.
        // For Luxometer: f000aa72 => write 0x01 (enable)
        // For Gyro: f000aa52 => write 0x07, etc.
        val activationMap = mapOf(
            "f000aa02-0451-4000-b000-000000000000" to byteArrayOf(0x01), // Temperature Enable
            "f000aa22-0451-4000-b000-000000000000" to byteArrayOf(0x01), // Humidity Enable
            "f000aa42-0451-4000-b000-000000000000" to byteArrayOf(0x01), // Barometer Enable
            "f000ac02-0451-4000-b000-000000000000" to byteArrayOf(0x01), // Accelerometer Enable
            "f000aa82-0451-4000-b000-000000000000" to byteArrayOf(0x01), // Magnetometer Enable
            "f000aa72-0451-4000-b000-000000000000" to byteArrayOf(0x01)  // [ADDED for LUX] Luxometer Enable
            // If you have Gyro: "f000aa52-0451-4000-b000-000000000000" => byteArrayOf(0x07)
        )

        for ((uuid, value) in activationMap) {
            val characteristic = gatt.services
                .flatMap { it.characteristics }
                .find { it.uuid == UUID.fromString(uuid) }

            if (characteristic != null) {
                try {
                    if (Build.VERSION.SDK_INT >= 33) {
                        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                        val success = gatt.writeCharacteristic(characteristic, value, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
                        if (success == BluetoothGatt.GATT_SUCCESS) {
                            Log.d(TAG, "Activated service: $uuid with value: ${value.joinToString(", ") { "0x%02X".format(it) }}")
                        } else {
                            Log.e(TAG, "Failed to activate service: $uuid")
                        }
                    } else {
                        characteristic.value = value
                        val success = gatt.writeCharacteristic(characteristic)
                        if (success) {
                            Log.d(TAG, "Activated service (deprecated write): $uuid with value: ${value.joinToString(", ") { "0x%02X".format(it) }}")
                        } else {
                            Log.e(TAG, "Failed to activate service (deprecated write): $uuid")
                        }
                    }
                    delay(1000)
                } catch (e: Exception) {
                    Log.e(TAG, "Error writing to characteristic: $uuid. ${e.message}", e)
                }
            } else {
                Log.e(TAG, "Characteristic not found for UUID: $uuid")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun activateTemperatureSensor() {
        val gatt = gatt ?: run {
            Log.e(TAG, "No GATT connection.")
            return
        }

        val temperatureConfigUUID = UUID.fromString("f000aa02-0451-4000-b000-000000000000") // Configuration
        val temperaturePeriodUUID = UUID.fromString("f000aa03-0451-4000-b000-000000000000") // Period
        val temperatureDataUUID = UUID.fromString("f000aa01-0451-4000-b000-000000000000")   // Data

        val configCharacteristic = gatt.services
            .flatMap { it.characteristics }
            .find { it.uuid == temperatureConfigUUID }

        if (configCharacteristic != null) {
            configCharacteristic.value = byteArrayOf(0x01) // Enable temperature
            val successConfig = gatt.writeCharacteristic(configCharacteristic)
            if (successConfig) {
                Log.d(TAG, "Temperature sensor enabled (Configuration Write Successful).")
            } else {
                Log.e(TAG, "Failed to write to Temperature Config Characteristic.")
            }
        } else {
            Log.e(TAG, "Temperature Configuration Characteristic not found.")
        }

        delay(1000)

        // Set sampling period (100ms)
        val periodCharacteristic = gatt.services
            .flatMap { it.characteristics }
            .find { it.uuid == temperaturePeriodUUID }

        if (periodCharacteristic != null) {
            Log.d(TAG, "Temperature Period Characteristic found. Props: ${periodCharacteristic.properties}")
            periodCharacteristic.value = byteArrayOf(0x64) // 100ms
            val successPeriod = gatt.writeCharacteristic(periodCharacteristic)
            if (successPeriod) {
                Log.d(TAG, "Temperature sampling period set successfully.")
            } else {
                Log.e(TAG, "Failed to write to Temperature Period Characteristic.")
            }
        } else {
            Log.e(TAG, "Temperature Period Characteristic not found.")
        }

        delay(1000)

        // Enable notifications for data
        val temperatureCharacteristic = gatt.services
            .flatMap { it.characteristics }
            .find { it.uuid == temperatureDataUUID }

        if (temperatureCharacteristic != null) {
            enableCharacteristicNotification(gatt, temperatureCharacteristic)
        } else {
            Log.e(TAG, "Temperature Data Characteristic not found.")
        }
    }

    // [ADDED for LUX] Enable the Luxometer
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun activateLuxometerSensor() {
        val gatt = gatt ?: run {
            Log.e(TAG, "No GATT connection for luxometer.")
            return
        }

        // 1) Write 0x01 to config f000aa72
        val luxConfig = gatt.services
            .flatMap { it.characteristics }
            .find { it.uuid == LUX_CONF_UUID }

        if (luxConfig != null) {
            luxConfig.value = byteArrayOf(0x01) // enable
            val ok = gatt.writeCharacteristic(luxConfig)
            if (ok) {
                Log.d(TAG, "Luxometer enabled successfully (config write).")
            } else {
                Log.e(TAG, "Failed to write to Luxometer config characteristic.")
            }
        } else {
            Log.e(TAG, "Luxometer config characteristic not found.")
        }

        delay(1000)

        // 2) Set the period (optionally, e.g. 100ms)
        val luxPeriod = gatt.services
            .flatMap { it.characteristics }
            .find { it.uuid == LUX_PERI_UUID }

        if (luxPeriod != null) {
            luxPeriod.value = byteArrayOf(0x64) // 100ms
            val perOk = gatt.writeCharacteristic(luxPeriod)
            if (perOk) {
                Log.d(TAG, "Luxometer sampling period set to 100ms.")
            } else {
                Log.e(TAG, "Failed to write Luxometer period characteristic.")
            }
        } else {
            Log.e(TAG, "Luxometer period characteristic not found.")
        }

        delay(500)

        // 3) Enable notifications on data f000aa71
        val luxData = gatt.services
            .flatMap { it.characteristics }
            .find { it.uuid == LUX_DATA_UUID }

        if (luxData != null) {
            enableCharacteristicNotification(gatt, luxData)
        } else {
            Log.e(TAG, "Luxometer data characteristic not found.")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun requestBarometerCalibration() {
        val gatt = gatt ?: run {
            Log.e(TAG, "No GATT connection to request barometer calibration.")
            return
        }

        val baroConfigChar = gatt.services
            .flatMap { it.characteristics }
            .find { it.uuid == BARO_CONFIG_UUID }

        if (baroConfigChar == null) {
            Log.e(TAG, "Barometer Configuration Characteristic not found.")
            return
        }

        // Write 0x02 to trigger calibration (older firmware)
        baroConfigChar.value = byteArrayOf(0x02)
        val ok = gatt.writeCharacteristic(baroConfigChar)
        if (ok) {
            Log.d(TAG, "Requested barometer calibration (0x02). Will read calibration char next.")
        } else {
            Log.e(TAG, "Failed to write 0x02 to barometer config for calibration.")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun enableCharacteristicNotification(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic
    ) {
        gatt.setCharacteristicNotification(characteristic, true)
        val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
        if (descriptor != null) {
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        } else {
            Log.e(TAG, "No CCCD descriptor for ${characteristic.uuid}")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private suspend fun pollTemperatureData() {
        val gatt = gatt ?: run {
            Log.e(TAG, "No GATT connection.")
            return
        }

        val temperatureDataUUID = UUID.fromString("f000aa01-0451-4000-b000-000000000000")

        for (i in 1..10) {
            Log.d(TAG, "Temperature read Start Polling.")
            val temperatureCharacteristic = gatt.services
                .flatMap { it.characteristics }
                .find { it.uuid == temperatureDataUUID }

            if (temperatureCharacteristic != null) {
                val readSuccess = gatt.readCharacteristic(temperatureCharacteristic)
                if (readSuccess) {
                    Log.d(TAG, "Temperature read request sent successfully.")
                } else {
                    Log.e(TAG, "Failed to read Temperature Data Characteristic.")
                }
            } else {
                Log.e(TAG, "Temperature Data Characteristic not found.")
            }
            delay(1000)
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
private val characteristicParsers = mapOf(
    "00002a00-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) },
    "00002a01-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> parseAppearance(raw) },
    "00002a23-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> parseSystemId(raw) },
    "00002a04-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> parseConnectionParameters(raw) },
    "00002a19-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> "${raw.getOrNull(0)?.toInt() ?: 0}%" },
    "00002a24-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) },
    "00002a25-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) },
    "00002a26-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) },
    "00002a27-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) },
    "00002a28-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) },
    "00002a29-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> raw.toString(Charsets.UTF_8) },
    "00002a2a-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> "Experimental Certification Data: ${raw.toString(Charsets.UTF_8)}" },
    "00002a50-0000-1000-8000-00805f9b34fb" to { raw: ByteArray -> parsePnPId(raw) },

    // Temperature Data
    "f000aa01-0451-4000-b000-000000000000" to { raw: ByteArray -> parseTemperature(raw) },
    // Humidity Data
    "f000aa21-0451-4000-b000-000000000000" to { raw: ByteArray -> parseHumidity(raw) },
    // Barometer Data
    "f000aa41-0451-4000-b000-000000000000" to { raw: ByteArray -> parseBarometerData(raw) },
    // Accelerometer Data
    "f000ac01-0451-4000-b000-000000000000" to { raw: ByteArray -> parseAccelerometerData(raw) },
    // Magnetometer Data
    "f000aa81-0451-4000-b000-000000000000" to { raw: ByteArray -> parseMagnetometerData(raw) },
    // Gyroscope Data
    //"f000aa71-0451-4000-b000-000000000000" to { raw: ByteArray -> parseGyroscopeData(raw) },

    // [ADDED for LUX] Actually, the official TI docs say LUX data is f000aa71,
    // but sometimes your code uses that for Gyro. Make sure to fix conflicts.
    // We'll assume 'f000aa71' is the lux data:
    // You can do something like:
    "f000aa71-0451-4000-b000-000000000000" to { raw: ByteArray -> parseLuxometerData(raw) }
)

/**
 * Add a real parse function for the CC2650 Lux sensor (OPT3001) if needed:
 *
 * e.g.:
 * private fun parseLuxometerData(raw: ByteArray): String {
 *     if (raw.size < 2) return "Invalid Lux Data"
 *     // TI code for the TSL/OPT sensor often extracts exponents, etc.
 *     // For a simple example, see TI "Sensor.LUXOMETER.convert(...)"
 *     val luxValue = someLuxFormula(raw)
 *     return "Lux: %.1f".format(luxValue)
 * }
 */


// [ADDED for LUX] Example placeholder parse:
private fun parseLuxometerData(raw: ByteArray): String {
    // If the sensor is an OPT3001, the TI formula extracts exponent & mantissa from 16 bits
    // This is a rough example:
    if (raw.size < 2) return "Invalid Lux Data"
    val mantissa = (raw[0].toInt() and 0xFF) or ((raw[1].toInt() and 0x0F) shl 8)
    val exponent = (raw[1].toInt() and 0xF0) shr 4
    val magnitude = (2.0).pow(exponent.toDouble())
    val lux = mantissa * (magnitude / 100.0)
    return "Lux: %.1f".format(lux)
}

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
        val pressure = (
                (raw[2].toInt() shl 16) or
                        (raw[1].toInt() shl 8) or
                        (raw[0].toInt() and 0xFF)
                )
        "Uncalibrated Pressure: ${pressure / 100.0} hPa"
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

// [ADDED] Example function to parse barometer data with calibration (legacy approach)
private fun parseBarometerDataWithCalibration(raw: ByteArray, calibration: ByteArray): String {
    // If your device is actually auto-calibrated in firmware v1.50, you won't need this.
    if (raw.size < 3 || calibration.isEmpty()) {
        return "Invalid Barometer or Calibration Data"
    }

    // Example partial approach, same as before
    val rawPressure = (
            (raw[2].toInt() shl 16) or
                    (raw[1].toInt() shl 8) or
                    (raw[0].toInt() and 0xFF)
            )
    val offset = (calibration[0].toInt() and 0xFF) + ((calibration[1].toInt() and 0xFF) shl 8)
    val gain   = (calibration[2].toInt() and 0xFF) + ((calibration[3].toInt() and 0xFF) shl 8)

    val corrected = (rawPressure - offset) * (gain / 100.0)
    val finalHpa = corrected / 100.0

    return "Calibrated Pressure: %.2f hPa".format(finalHpa)
}

