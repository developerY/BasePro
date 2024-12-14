package com.ylabz.basepro.core.data.repository.bluetoothLE

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.provider.Settings.Global.DEVICE_NAME
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class BluetoothLeRepImpl @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val context: Context
) : BluetoothLeRepository {

    private val TAG = "BluetoothLeRepImpl"

    private var gatt: BluetoothGatt? = null
    private var isScanning = false

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanCallback = object : ScanCallback() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d(TAG, "onScanResult - callbackType: $callbackType, result: $result")
            val deviceName = result.device.name ?: "Unknown Device"
            val deviceAddress = result.device.address
            Log.d(TAG, "Device found - Name: $deviceName, Address: $deviceAddress, RSSI: ${result.rssi}")
        }

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            Log.d(TAG, "onBatchScanResults - Total devices found: ${results.size}")
            results.forEach { result ->
                val deviceName = result.device.name ?: "Unknown Device"
                val deviceAddress = result.device.address
                Log.d(TAG, "Batch Device - Name: $deviceName, Address: $deviceAddress, RSSI: ${result.rssi}")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            val errorMessage = when (errorCode) {
                ScanCallback.SCAN_FAILED_ALREADY_STARTED -> "Scan already started."
                ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "Application registration failed."
                ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> "Internal error occurred."
                ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> "Feature unsupported."
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

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override suspend fun fetchBluetoothDevices(): List<BluetoothDeviceInfo> {
        Log.d(TAG, "fetchBluetoothDevices - Starting scan...")
        try {
            bleScanner.startScan(null, scanSettings, scanCallback)
            isScanning = true
            Log.d(TAG, "fetchBluetoothDevices - Scan initiated successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "fetchBluetoothDevices - Error starting scan: ${e.message}", e)
        }
        return emptyList() // Return devices if required
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
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
