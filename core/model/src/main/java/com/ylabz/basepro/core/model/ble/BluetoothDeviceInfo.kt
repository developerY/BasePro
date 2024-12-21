package com.ylabz.basepro.core.model.ble

// Data Model for a BLE Device
data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val rssi: Int
)
