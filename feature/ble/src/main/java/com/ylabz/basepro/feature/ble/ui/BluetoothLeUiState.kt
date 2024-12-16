package com.ylabz.basepro.feature.ble.ui

import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothDeviceInfo

sealed interface BluetoothLeUiState {
    object ShowBluetoothDialog : BluetoothLeUiState
    object Stopped : BluetoothLeUiState
    object Loading : BluetoothLeUiState
    object PermissionsRequired : BluetoothLeUiState
    object PermissionsDenied : BluetoothLeUiState
    data class Success(val devices: List<BluetoothDeviceInfo>) : BluetoothLeUiState
    data class Error(val message: String) : BluetoothLeUiState
}
