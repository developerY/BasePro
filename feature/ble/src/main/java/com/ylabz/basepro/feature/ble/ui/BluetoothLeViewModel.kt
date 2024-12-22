package com.ylabz.basepro.feature.ble.ui

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepository
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.util.Logging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothLeViewModel @Inject constructor(
    private val bleRepository: BluetoothLeRepository, // BLE repository
    private val bluetoothAdapter: BluetoothAdapter,
) : ViewModel() {
    private val TAG = Logging.getTag(this::class.java)

    private val _uiState = MutableStateFlow<BluetoothLeUiState>(BluetoothLeUiState.PermissionsRequired)
    val uiState = _uiState.asStateFlow()

    // StateFlow for detecting the TI Tag Sensor
    private val _isTiTagSensorFound = MutableStateFlow(false)
    val isTiTagSensorFound = _isTiTagSensorFound.asStateFlow()

    private var isBluetoothDialogAlreadyShown = false

    init {
        Logging.d(TAG, "init: ViewModel initialized ")
        Log.d("TEST", "init: ViewModel initialized ")
        // Automatically trigger Bluetooth state check
        onEvent(BluetoothLeEvent.RequestEnableBluetooth)
    }


    fun onEvent(event: BluetoothLeEvent) {
        when (event) {
            is BluetoothLeEvent.RequestEnableBluetooth -> checkBluetoothState()
            is BluetoothLeEvent.RequestPermissions -> _uiState.value = BluetoothLeUiState.PermissionsRequired
            is BluetoothLeEvent.PermissionsGranted -> fetchBleDevices() // 👇🏽 Moved to BluetoothViewModel
            //is BluetoothLeEvent.PermissionsGranted -> fetchDevices() Moved to BluetoothViewModel
            is BluetoothLeEvent.PermissionsDenied -> _uiState.value = BluetoothLeUiState.PermissionsDenied
            is BluetoothLeEvent.FetchDevices -> fetchBleDevices() // Handle BLE
            BluetoothLeEvent.StartScan -> scanning()
            BluetoothLeEvent.StopScan -> stopping()
            is BluetoothLeEvent.TiTagSensorDetected -> {
                _isTiTagSensorFound.value = true
                _uiState.value = BluetoothLeUiState.TiTagSensorFound(event.device)
            }
        }
    }

    private fun startScanning() {
        viewModelScope.launch {
            try {
                _uiState.value = BluetoothLeUiState.Scanning
                bleRepository.startScan() /*{ result ->
                    val deviceName = result.device.name ?: "Unknown Device"
                    if (deviceName.contains("CC2650 SensorTag", ignoreCase = true)) {
                        val device = BluetoothDeviceInfo(
                            name = deviceName,
                            address = result.device.address,
                            rssi = result.rssi
                        )
                        onEvent(BluetoothLeEvent.TiTagSensorDetected(device))
                        bleRepository.stopScan() // Optionally stop scanning after detecting the sensor
                    }
                }*/
            } catch (e: Exception) {
                _uiState.value = BluetoothLeUiState.Error("Failed to scan: ${e.message}")
            }
        }
    }

    private fun stopScanning() {
        viewModelScope.launch {
            try {
                bleRepository.stopScan()
                _uiState.value = BluetoothLeUiState.Stopped
            } catch (e: Exception) {
                _uiState.value = BluetoothLeUiState.Error("Failed to stop scanning: ${e.message}")
            }
        }
    }

    private fun scanning() {
        //_uiState.value = BluetoothLeUiState.Scanning
        viewModelScope.launch {
            try {
                bleRepository.startScan()
            } catch (e: Exception) {
                _uiState.value = BluetoothLeUiState.Error("Failed to fetch BLE devices: ${e.message}")
            }
        }
    }

    private fun stopping() {
        //_uiState.value = BluetoothLeUiState.Stopped
        viewModelScope.launch {
            try {
                bleRepository.stopScan()
            } catch (e: Exception) {
                _uiState.value = BluetoothLeUiState.Error("Failed to fetch BLE devices: ${e.message}")
            }
        }
    }

    private fun checkBluetoothState() {
        if (!bluetoothAdapter.isEnabled && !isBluetoothDialogAlreadyShown) {
            _uiState.value = BluetoothLeUiState.ShowBluetoothDialog
            isBluetoothDialogAlreadyShown = true
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
}

