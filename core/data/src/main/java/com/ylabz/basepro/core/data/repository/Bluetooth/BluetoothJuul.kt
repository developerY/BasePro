package com.ylabz.basepro.core.data.repository.Bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import javax.inject.Inject

data class Advertisement(
    val name: String?,
    val address: String
)

interface BluetoothJuul {
    fun advertisements(): Flow<Advertisement>
}

@Suppress("MissingPermission")
class BluetoothJuulImpl @Inject constructor(
    private val context: Context
) : BluetoothJuul {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun advertisements(): Flow<Advertisement> = flow {
        val scanner = bluetoothAdapter?.bluetoothLeScanner
            ?: throw IllegalStateException("BluetoothLeScanner is unavailable")

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.device?.let { device ->
                    /*emit(
                        Advertisement(
                            name = device.name,
                            address = device.address
                        )
                    )*/
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { result ->
                    result.device?.let { device ->
                        /*emit(
                            Advertisement(
                                name = device.name,
                                address = device.address
                            )
                        )*/
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                throw Exception("BLE scan failed with error code $errorCode")
            }
        }

        try {
            scanner.startScan(callback)
            //awaitClose { scanner.stopScan(callback) }
        } catch (e: Exception) {
            scanner.stopScan(callback)
            throw e
        }
    }
}
