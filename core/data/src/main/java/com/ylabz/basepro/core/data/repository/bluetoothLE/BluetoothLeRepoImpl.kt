package com.ylabz.basepro.core.data.repository.bluetoothLE

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanCallback.SCAN_FAILED_ALREADY_STARTED
import android.bluetooth.le.ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED
import android.bluetooth.le.ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED
import android.bluetooth.le.ScanCallback.SCAN_FAILED_INTERNAL_ERROR
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.provider.Settings.Global.DEVICE_NAME
import android.util.Log
import androidx.annotation.RequiresPermission
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class BluetoothLeRepImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) : BluetoothLeRepository {

    /*D
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

    private val TAG = "BluetoothLeRepImpl"

    private var gatt: BluetoothGatt? = null
    private var isScanning = false
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val currentFilter = "SensorTag"

    private val _currentDevice: MutableStateFlow<BluetoothDeviceInfo?> = MutableStateFlow(null)
    val currentDevice: StateFlow<BluetoothDeviceInfo?> = _currentDevice


    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanCallback = object : ScanCallback() {

        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val deviceName = result.device.name ?: "Unknown Device"
            val deviceAddress = result.device.address ?: "Unknown Address"
            val deviceRssi = result.rssi // Include RSSI value from ScanResult
            Log.d(TAG, "Device found - Name: $deviceName, Address: $deviceAddress, RSSI: $deviceRssi")
            // Update the list reactively
            coroutineScope.launch {
                val newDevice = BluetoothDeviceInfo(name = deviceName, address = deviceAddress, rssi = deviceRssi)
                _currentDevice.emit(newDevice)
                // If the TI Tag Sensor is found, stop scanning
                if (deviceName.contains("CC2650 SensorTag", ignoreCase = true)) {
                    Log.d(TAG, "TI Tag Sensor Found - Name: $deviceName, Address: $deviceAddress")
                    stopScan() // Stop scanning
                }
            }
        }


        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            Log.d(TAG, "onBatchScanResults - Total devices found: ${results.size}")
            results.forEach { result ->
                if (result.device.name != null) {
                    val deviceName = result.device.name ?: "Unknown Device"
                    val deviceAddress = result.device.address
                    Log.d(
                        TAG,
                        "Batch Device - Name: $deviceName, Address: $deviceAddress, RSSI: ${result.rssi}"
                    )
                }
            }
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            val errorMessage = when (errorCode) {
                SCAN_FAILED_ALREADY_STARTED -> "Scan already started."
                SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "Application registration failed."
                SCAN_FAILED_INTERNAL_ERROR -> "Internal error occurred."
                SCAN_FAILED_FEATURE_UNSUPPORTED -> "Feature unsupported."
                else -> "Unknown error code: $errorCode"
            }
            Log.e(TAG, "onScanFailed - Error: $errorMessage (code $errorCode)")
        }

        // Add additional logs to understand the flow better
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        fun onScanStopped() {
            Log.d(TAG, "onScanStopped - Scan stopped manually or timed out.")
        }
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    override suspend fun fetchBluetoothDevice(): StateFlow<BluetoothDeviceInfo?> {
        return currentDevice// Return devices if required
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override suspend fun startScan() {
        if (!isScanning) {
            isScanning = true
            try {
                bleScanner.startScan(null, scanSettings, scanCallback)
                Log.d(TAG, "startScan - Scan started successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "startScan - Error starting scan: ${e.message}", e)
            }
        } else {
            Log.d(TAG, "stopScan - No active scan to stop.")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override suspend fun stopScan() {
        if (isScanning) {
            try {
                bleScanner.stopScan(scanCallback)
                isScanning = false
                Log.d(TAG, "stopScan - Scan stopped successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "stopScan - Error stopping scan: ${e.message}", e)
            }
        } else {
            Log.d(TAG, "stopScan - No active scan to stop.")
        }
    }
}