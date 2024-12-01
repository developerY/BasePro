package com.ylabz.basepro.core.data.repository.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import javax.inject.Inject

class BluetoothLeRepImpl @Inject constructor(
    private val context: Context
) : BluetoothLeRepository {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    @RequiresPermission(allOf = [android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT])
    override suspend fun fetchBluetoothDevices(): List<BluetoothDeviceInfo> {
        // Ensure Bluetooth is enabled
        if (bluetoothAdapter?.isEnabled != true) {
            throw IllegalStateException("Bluetooth is not enabled")
        }

        val scanner = bluetoothAdapter?.bluetoothLeScanner
            ?: throw IllegalStateException("BluetoothLeScanner is unavailable")

        return scanForDevices(scanner)
    }

    @RequiresPermission(allOf = [android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT])
    private suspend fun scanForDevices(scanner: BluetoothLeScanner): List<BluetoothDeviceInfo> {
        return suspendCancellableCoroutine { continuation ->
            val devices = mutableSetOf<BluetoothDeviceInfo>()

            @SuppressLint("MissingPermission")
            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    result?.device?.let { device ->
                        devices.add(
                            BluetoothDeviceInfo(
                                name = device.name ?: "Unknown",
                                address = device.address
                            )
                        )
                    }
                }

                override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                    results?.forEach { result ->
                        result.device?.let { device ->
                            devices.add(
                                BluetoothDeviceInfo(
                                    name = device.name ?: "Unknown",
                                    address = device.address
                                )
                            )
                        }
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    Log.e("BluetoothLeRepImpl", "Scan failed with error: $errorCode")
                    continuation.resumeWithException(Exception("Scan failed with error code $errorCode"))
                }
            }

            try {
                scanner.startScan(scanCallback)
                continuation.invokeOnCancellation {
                    scanner.stopScan(scanCallback) // Stop scanning on cancellation
                }

                continuation.resume(devices.toList())
            } catch (e: Exception) {
                scanner.stopScan(scanCallback)
                continuation.resumeWithException(e)
            }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}

// Fake Implementation for Testing
class BluetoothLeRepImplFake @Inject constructor() : BluetoothLeRepository {
    override suspend fun fetchBluetoothDevices(): List<BluetoothDeviceInfo> {
        return listOf(
            BluetoothDeviceInfo(name = "Device 1", address = "00:11:22:33:44:55"),
            BluetoothDeviceInfo(name = "Device 2", address = "AA:BB:CC:DD:EE:FF")
        )
    }
}

// Data Model for a BLE Device
data class BluetoothDeviceInfo(val name: String, val address: String)
