package com.ylabz.basepro.feature.ble.ui

import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.DeviceCharacteristic
import com.ylabz.basepro.core.model.ble.DeviceService
import com.ylabz.basepro.core.model.ble.GattCharacteristicValue


sealed interface BluetoothLeUiState {
    object ShowBluetoothDialog : BluetoothLeUiState
    object Loading : BluetoothLeUiState
    object PermissionsRequired : BluetoothLeUiState
    object PermissionsDenied : BluetoothLeUiState
    data class ScanDevices(val devices: BluetoothDeviceInfo?) : BluetoothLeUiState // This might be legacy and can be reviewed later
    data class Error(val message: String) : BluetoothLeUiState

    // Unified "success" state holding all BLE device data
    data class DataLoaded(
        val activeDevice: BluetoothDeviceInfo? = null,  // Holds the currently active/selected device details
        val discoveredDevices: List<BluetoothDeviceInfo> = emptyList(), // Holds all devices found when scanAllDevices is true
        val services: List<DeviceService> = emptyList(),  // Discovered GATT services for the activeDevice
        val selectedService: DeviceService? = null,  // Selected service for the activeDevice
        val selectedCharacteristic: DeviceCharacteristic? = null,  // Selected characteristic for the activeDevice
        val characteristicValues: Map<String, String?> = emptyMap(),  // Cached values for characteristics of the activeDevice
        val scanAllDevices: Boolean = false // Controls scan mode
    ) : BluetoothLeUiState
}
