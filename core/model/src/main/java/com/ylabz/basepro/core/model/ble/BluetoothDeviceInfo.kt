package com.ylabz.basepro.core.model.ble

// Data Model for a BLE Device
// Data Model for a BLE Device with GATT Support
data class BluetoothDeviceInfo(
    val name: String,
    val address: String,
    val rssi: Int,
    val gattCharacteristics: List<DeviceService> = emptyList() // Default to an empty list
)

data class DeviceService(
    val uuid: String,
    val name: String,
    val characteristics: List<DeviceCharacteristic>
)

data class DeviceCharacteristic(
    val uuid: String,
    val name: String,
    val isReadable: Boolean,
    val isWritable: Boolean,
    val isNotifiable: Boolean,
    var value: String // Add value to hold the current data (nullable for unknown values)
)

