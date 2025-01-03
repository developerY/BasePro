package com.ylabz.basepro.core.data.repository.bluetoothLE

import android.Manifest
import android.R.attr.data
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.GattConnectionState
import com.ylabz.basepro.core.model.ble.ScanState
import com.ylabz.basepro.core.model.ble.tools.getHumanReadableName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val currentFilter = "SensorTag"

    private val _currentDevice: MutableStateFlow<BluetoothDeviceInfo?> = MutableStateFlow(null)
    val currentDevice: StateFlow<BluetoothDeviceInfo?> = _currentDevice

    private val _scanState = MutableStateFlow(ScanState.NOT_SCANNING)
    override val scanState: StateFlow<ScanState> = _scanState

    private val _gattConnectionState = MutableStateFlow<GattConnectionState>(GattConnectionState.Disconnected)
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
                        _scanState.value != ScanState.STOPPING) {
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
    private val BATTERY_SERVICE_UUID = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb")
    private val BATTERY_LEVEL_CHARACTERISTIC_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb")

    // StateFlow to hold battery level percentage
    private val _batteryLevel = MutableStateFlow<Int?>(null)
    val batteryLevelFlow: StateFlow<Int?> = _batteryLevel

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
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG, "Disconnected from GATT server.")
                    _gattConnectionState.value = GattConnectionState.Disconnected
                    _batteryLevel.value = null // Clear battery level on disconnect
                }
            }

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            fun onServicesDiscoveredBATT(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val deviceInfoService = gatt.getService(UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"))
                    val manufacturerNameChar = deviceInfoService?.getCharacteristic(UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb"))
                    manufacturerNameChar?.let {
                        gatt.readCharacteristic(it)
                    }
                }
            }

            /*override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (characteristic.uuid == UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb")) {
                        val manufacturerName = characteristic.getStringValue(0) // UTF-8 string
                        Log.d("BluetoothGatt", "Manufacturer Name: $manufacturerName")
                    }
                }
            }*/

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "Characteristic read: ${characteristic?.uuid} ,${getHumanReadableName(characteristic?.uuid.toString())}")

                    if (characteristic?.uuid == BATTERY_LEVEL_CHARACTERISTIC_UUID) {
                        val value = characteristic.value // `ByteArray` of characteristic
                        val batteryLevel = value?.getOrNull(0)?.toInt() ?: -1 // First byte is the battery level
                        Log.d(TAG, "Battery Level: $batteryLevel%")
                        coroutineScope.launch {
                            //batteryLevelFlow.emit(batteryLevel)
                        }
                        if (characteristic?.uuid == UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb")) {
                            val manufacturerName = characteristic.getStringValue(0) // UTF-8 string
                            Log.d("BluetoothGatt", "Manufacturer Name: $manufacturerName")
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to read battery level. Status: $status")
                }
            }


            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "GATT services discovered.")
                    // Iterate over available services and characteristics
                    gatt.services.forEach { service ->
                        Log.d(TAG, "Service: ${service.uuid}")
                        service.characteristics.forEach { characteristic ->
                            Log.d(TAG, "Characteristic read: ${characteristic?.uuid} ,${getHumanReadableName(characteristic?.uuid.toString())}")
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to discover GATT services. Status: $status")
                }
            }

            fun onCharacteristicReadOrig(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val data = characteristic.value
                    Log.d(TAG, "Characteristic read: ${characteristic.uuid}, Value: ${data?.contentToString()}")
                } else {
                    Log.e(TAG, "Failed to read characteristic. Status: $status")
                }
            }
        })
    }



    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun readBatteryLevel(): StateFlow<Int?> {
        val gattInstance = gatt ?: return MutableStateFlow(null)

        val batteryService = gattInstance.getService(BATTERY_SERVICE_UUID)
        val batteryLevelFlow = MutableStateFlow<Int?>(null)

        if (batteryService != null) {
            val batteryLevelCharacteristic = batteryService.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC_UUID)
            if (batteryLevelCharacteristic != null) {
                if (batteryLevelCharacteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
                    val success = gattInstance.readCharacteristic(batteryLevelCharacteristic)
                    if (success) {
                        Log.d(TAG, "Battery level read request sent.")
                        // The actual battery level will be emitted from the callback
                    } else {
                        Log.e(TAG, "Failed to send battery level read request.")
                    }
                } else {
                    Log.e(TAG, "Battery Level Characteristic is not readable.")
                }
            } else {
                Log.e(TAG, "Battery Level Characteristic not found.")
            }
        } else {
            Log.e(TAG, "Battery Service not found.")
        }

        return batteryLevelFlow
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
}