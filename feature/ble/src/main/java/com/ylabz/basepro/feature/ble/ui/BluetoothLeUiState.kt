package com.ylabz.basepro.feature.ble.ui

import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.DeviceCharacteristic
import com.ylabz.basepro.core.model.ble.DeviceService
import com.ylabz.basepro.core.model.ble.GattCharacteristicValue


sealed interface BluetoothLeUiState {
    object ShowBluetoothDialog : BluetoothLeUiState
    object Stopped : BluetoothLeUiState
    object Scanning : BluetoothLeUiState
    object Loading : BluetoothLeUiState
    object PermissionsRequired : BluetoothLeUiState
    object PermissionsDenied : BluetoothLeUiState
    data class ScanDevices(val devices: BluetoothDeviceInfo?) : BluetoothLeUiState
    data class Error(val message: String) : BluetoothLeUiState

    // Unified "success" state holding all BLE device data
    data class DataLoaded(
        val scannedDevice: BluetoothDeviceInfo? = null,  // Holds discovered device details
        val services: List<DeviceService> = emptyList(),  // Discovered GATT services
        val selectedService: DeviceService? = null,  // Selected service
        val selectedCharacteristic: DeviceCharacteristic? = null,  // Selected characteristic
        val characteristicValues: Map<String, String?> = emptyMap()  // Cached values for each characteristic UUID
    ) : BluetoothLeUiState
}


