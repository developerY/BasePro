package com.ylabz.basepro.feature.ble.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepository
import com.ylabz.basepro.core.model.ble.ScanState
import com.ylabz.basepro.core.util.Logging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothLeViewModel @Inject constructor(
    private val bleRepository: BluetoothLeRepository, // BLE repository
    private val bluetoothAdapter: BluetoothAdapter,
) : ViewModel() {
    private val TAG = Logging.getTag(this::class.java)

    val scanState: StateFlow<ScanState> = bleRepository.scanState
    val gattConnectionState = bleRepository.gattConnectionState
    val gattCharacteristicList = bleRepository.gattCharacteristicList
    val gattServicesList = bleRepository.gattServicesList

    private val _isStartButtonEnabled = MutableStateFlow(true)
    val isStartButtonEnabled = _isStartButtonEnabled.asStateFlow()

    // StateFlow for detecting the TI Tag Sensor


    private val _uiState = MutableStateFlow<BluetoothLeUiState>(BluetoothLeUiState.PermissionsRequired)
    val uiState: StateFlow<BluetoothLeUiState> = _uiState


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
            is BluetoothLeEvent.PermissionsDenied -> _uiState.value = BluetoothLeUiState.PermissionsDenied
            is BluetoothLeEvent.FetchDevices -> fetchBleDevices() // Handle BLE
            is BluetoothLeEvent.ConnectToSensorTag -> connectToSensorTag()
            BluetoothLeEvent.StartScan -> scanning()
            BluetoothLeEvent.StopScan -> stopping()
            BluetoothLeEvent.GattCharacteristicList -> gattCharacteristicList

            is BluetoothLeEvent.ReadBatteryLevel -> readAllCharacteristics()//readBatteryLevel()
        }
    }

    private fun readAllCharacteristics() {
        viewModelScope.launch(Dispatchers.IO) {
            bleRepository.readAllCharacteristics()
        }
    }

    private fun scanning() {
        if (!_isStartButtonEnabled.value) return
        _isStartButtonEnabled.value = false
        viewModelScope.launch {
            try {
                bleRepository.startScan()
            } catch (e: Exception) {
                _uiState.value =
                    BluetoothLeUiState.Error("Failed to fetch BLE devices: ${e.message}")
                _isStartButtonEnabled.value = true
            }
            delay(5000L)
            _isStartButtonEnabled.value = true
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


    fun connectToSensorTag() {
        viewModelScope.launch {
            bleRepository.connectToDevice()
        }
    }



    private fun fetchBleDevices() {
        viewModelScope.launch {
            _uiState.value = BluetoothLeUiState.Loading
            // Collect devices reactively
            bleRepository.fetchBluetoothDevice().collect { devices ->
                _uiState.value = BluetoothLeUiState.ScanDevices(devices)
            }
        }
    }
}

