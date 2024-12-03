package com.ylabz.basepro.feature.ble.ui

import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothDeviceInfo

sealed interface BluetoothLeUiState {
    object Stopped : BluetoothLeUiState
    object Loading : BluetoothLeUiState
    object PermissionsRequired : BluetoothLeUiState
    object PermissionsDenied : BluetoothLeUiState
    data class Success(val devices: List<BluetoothDeviceInfo>) : BluetoothLeUiState
    data class ClassicSuccess(val devices: List<BluetoothDeviceInfo>) : BluetoothLeUiState // Classic Bluetooth success state
    data class Error(val message: String) : BluetoothLeUiState
}
