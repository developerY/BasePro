package com.ylabz.basepro.feature.ble.ui

import com.ylabz.basepro.core.data.repository.BluetoothDeviceInfo

sealed interface BluetoothLeUiState {
    object Loading : BluetoothLeUiState
    data class Success(val devices: List<com.ylabz.basepro.core.data.repository.BluetoothDeviceInfo>) : BluetoothLeUiState
    data class Error(val message: String) : BluetoothLeUiState
}