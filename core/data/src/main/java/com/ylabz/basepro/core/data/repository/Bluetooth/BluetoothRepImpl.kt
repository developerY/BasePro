package com.ylabz.basepro.core.data.repository.Bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BluetoothRepImpl @Inject constructor(
    private val context: Context
) : BluetoothRepository {

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        BluetoothAdapter.getDefaultAdapter()
    }
    @SuppressLint("MissingPermission")
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    override suspend fun fetchBluetoothDevices(): List<BluetoothDeviceInfo> {
        if (bluetoothAdapter?.isEnabled != true) {
            throw IllegalStateException("Bluetooth is not enabled")
        }

        return suspendCancellableCoroutine { continuation ->
            val devices = mutableSetOf<BluetoothDeviceInfo>()

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val action = intent.action
                    if (action == BluetoothDevice.ACTION_FOUND) {
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        device?.let {
                            devices.add(
                                BluetoothDeviceInfo(
                                    name = it.name ?: "Unknown",
                                    address = it.address
                                )
                            )
                        }
                    } else if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                        context.unregisterReceiver(this)
                        continuation.resume(devices.toList())
                    }
                }
            }

            try {
                val filter = IntentFilter(BluetoothDevice.ACTION_FOUND).apply {
                    addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                }
                context.registerReceiver(receiver, filter)

                if (bluetoothAdapter?.startDiscovery() != true) {
                    context.unregisterReceiver(receiver)
                    continuation.resumeWithException(IllegalStateException("Failed to start discovery"))
                }
            } catch (e: Exception) {
                context.unregisterReceiver(receiver)
                continuation.resumeWithException(e)
            }
        }
    }
}
