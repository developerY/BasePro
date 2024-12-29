package com.ylabz.basepro.feature.ble.ui

import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo


sealed interface BluetoothLeUiState {
    object ShowBluetoothDialog : BluetoothLeUiState
    object Stopped : BluetoothLeUiState
    object Scanning : BluetoothLeUiState
    object Loading : BluetoothLeUiState
    object PermissionsRequired : BluetoothLeUiState
    object PermissionsDenied : BluetoothLeUiState
    data class ScanDevices(val devices: BluetoothDeviceInfo?) : BluetoothLeUiState
    data class Error(val message: String) : BluetoothLeUiState
    //data class TiTagSensorFound(val device: BluetoothDeviceInfo) : BluetoothLeUiState
}
