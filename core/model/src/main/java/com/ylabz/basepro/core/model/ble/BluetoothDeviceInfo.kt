package com.ylabz.basepro.core.model.ble

// Data Model for a BLE Device
// Data Model for a BLE Device with GATT Support
data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val rssi: Int,
    val gattCharacteristics: List<GattCharacteristicValue> = emptyList() // Default to an empty list
)