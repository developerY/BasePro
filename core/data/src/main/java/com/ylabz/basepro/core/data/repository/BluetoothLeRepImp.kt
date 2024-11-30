package com.ylabz.basepro.core.data.repository

import javax.inject.Inject

class BluetoothLeRepImpl @Inject constructor(): BluetoothLeRepository {
    // Simulate BLE data fetching
    override suspend fun fetchBluetoothDevices(): List<BluetoothDeviceInfo> {
        // Replace this with actual BLE data fetching logic
        return listOf(
            BluetoothDeviceInfo(name = "Device 1", address = "00:11:22:33:44:55"),
            BluetoothDeviceInfo(name = "Device 2", address = "AA:BB:CC:DD:EE:FF")
        )
    }
}

// Data model for a BLE device
data class BluetoothDeviceInfo(val name: String, val address: String)
