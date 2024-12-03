package com.ylabz.basepro.feature.ble.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothLeRepository
import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothLeViewModel @Inject constructor(
    private val bleRepository: BluetoothLeRepository, // BLE repository
    private val classicRepository: BluetoothRepository // Classic Bluetooth repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BluetoothLeUiState>(BluetoothLeUiState.PermissionsRequired)
    val uiState: StateFlow<BluetoothLeUiState> = _uiState.asStateFlow()

    fun onEvent(event: BluetoothLeEvent) {
        when (event) {
            is BluetoothLeEvent.RequestPermissions -> _uiState.value = BluetoothLeUiState.PermissionsRequired
            is BluetoothLeEvent.PermissionsGranted -> fetchBleDevices() // 👇🏽 Moved to BluetoothViewModel
            //is BluetoothLeEvent.PermissionsGranted -> fetchDevices() Moved to BluetoothViewModel
            is BluetoothLeEvent.PermissionsDenied -> _uiState.value = BluetoothLeUiState.PermissionsDenied
            is BluetoothLeEvent.FetchDevices -> fetchBleDevices() // Handle BLE
            is BluetoothLeEvent.FetchClassicDevices -> fetchClassicDevices() // Handle Classic Bluetooth
        }
    }


    private fun fetchBleDevices() {
        viewModelScope.launch {
            _uiState.value = BluetoothLeUiState.Loading
            try {
                val devices = bleRepository.fetchBluetoothDevices()
                _uiState.value = BluetoothLeUiState.Success(devices)
            } catch (e: Exception) {
                _uiState.value = BluetoothLeUiState.Error("Failed to fetch BLE devices: ${e.message}")
            }
        }
    }

    private fun fetchClassicDevices() {
        viewModelScope.launch {
            _uiState.value = BluetoothLeUiState.Loading
            try {
                val devices = classicRepository.fetchBluetoothDevices()
                _uiState.value = BluetoothLeUiState.ClassicSuccess(devices)
            } catch (e: Exception) {
                _uiState.value = BluetoothLeUiState.Error("Failed to fetch Classic devices: ${e.message}")
            }
        }
    }
}

