package com.ylabz.basepro.core.data.repository.bluetoothLE

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BikeBleRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BikeBleRepository {

    private val bluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    private val bluetoothAdapter by lazy { bluetoothManager.adapter }

    // Example: the service/characteristic your bike sensor uses
    // Replace with the actual UUID from your BLE device.
    private val BIKE_SERVICE_UUID = "00001816-0000-1000-8000-00805f9b34fb"
    private val BIKE_CHAR_UUID = "00002a5b-0000-1000-8000-00805f9b34fb"



    @SuppressLint("MissingPermission")
    override fun connectToBike(deviceName: String): Flow<BikeBleRepository.BikeBleData> = callbackFlow {
        // 1) Check if BLE is supported
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            close(Exception("Bluetooth not available or disabled"))
            return@callbackFlow
        }

        // 5) Inner function to connect & discover GATT
        @SuppressLint("MissingPermission")
        fun connectToDevice(device: BluetoothDevice) {
            device.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d("BikeBleRepository", "Connected to GATT server.")
                        gatt.discoverServices()
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.d("BikeBleRepository", "Disconnected from GATT server.")
                        close() // end the flow
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        val service = gatt.getService(java.util.UUID.fromString(BIKE_SERVICE_UUID))
                        if (service != null) {
                            val characteristic = service.getCharacteristic(java.util.UUID.fromString(BIKE_CHAR_UUID))
                            if (characteristic != null) {
                                // Enable notifications if the characteristic supports it
                                gatt.setCharacteristicNotification(characteristic, true)
                                val descriptor = characteristic.getDescriptor(
                                    characteristic.uuid // or the CCC descriptor
                                )
                                descriptor?.let {
                                    it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                    gatt.writeDescriptor(it)
                                }
                                Log.d("BikeBleRepository", "Bike characteristic found, notifications enabled.")
                            } else {
                                close(Exception("Bike characteristic not found."))
                            }
                        } else {
                            close(Exception("Bike service not found."))
                        }
                    } else {
                        close(Exception("Service discovery failed with status $status"))
                    }
                }

                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic
                ) {
                    // parse the data from the bike sensor
                    val rawData = characteristic.value
                    val speedKmh = parseSpeed(rawData)
                    val batteryPercent = parseBattery(rawData)

                    trySend(
                        BikeBleRepository.BikeBleData(
                            speedKmh = speedKmh,
                            batteryPercent = batteryPercent,
                            rawData = rawData
                        )
                    )
                }
            })
        }

        // 2) Build a BLE scanner
        val scanner = bluetoothAdapter.bluetoothLeScanner
        if (scanner == null) {
            close(Exception("BluetoothLeScanner not available"))
            return@callbackFlow
        }

        // 3) Create a ScanCallback to find the target device
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                if (device.name == deviceName) {
                    Log.d("BikeBleRepository", "Found target device: $deviceName")
                    // Stop scanning
                    scanner.stopScan(this)
                    // Connect to the device
                    connectToDevice(device)
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { result ->
                    if (result.device.name == deviceName) {
                        scanner.stopScan(this)
                        connectToDevice(result.device)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                close(Exception("BLE Scan failed with code $errorCode"))
            }
        }

        // 4) Start scanning for the target device
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        val filters = listOf<ScanFilter>() // or filter by service UUID if needed
        scanner.startScan(filters, scanSettings, scanCallback)



        // 6) Provide a way to close the flow
        awaitClose {
            try {
                scanner.stopScan(scanCallback)
            } catch (_: Exception) {}
            Log.d("BikeBleRepository", "Flow closed, scanning stopped.")
        }
    }

    // Example parse functions. Adjust to your device's protocol.
    private fun parseSpeed(data: ByteArray?): Float? {
        // e.g., data[0..1] might hold speed in some custom format
        if (data == null || data.size < 2) return null
        // Fake parse: just interpret data[0] as speed in km/h
        return data[0].toUByte().toFloat()
    }

    private fun parseBattery(data: ByteArray?): Int? {
        // e.g., data[2] might be battery percent
        if (data == null || data.size < 3) return null
        return data[2].toInt()
    }
}
