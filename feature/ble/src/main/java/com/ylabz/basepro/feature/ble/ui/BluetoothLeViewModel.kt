package com.ylabz.basepro.feature.ble.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepository
import com.ylabz.basepro.core.model.ble.BluetoothDeviceInfo
import com.ylabz.basepro.core.model.ble.ScanState
import com.ylabz.basepro.core.util.Logging
import com.ylabz.basepro.feature.ble.ui.BluetoothLeUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow<BluetoothLeUiState>(BluetoothLeUiState.PermissionsRequired)
    val uiState: StateFlow<BluetoothLeUiState> = _uiState

    private var isBluetoothDialogAlreadyShown = false

    init {
        Logging.d(TAG, "init: ViewModel initialized ")
        // Initialize with a default DataLoaded state after checking permissions
        // The actual transition to DataLoaded will happen after permissions are confirmed and BT is on.
        // For now, RequestEnableBluetooth will handle initial state setting if BT is off.
        onEvent(BluetoothLeEvent.RequestEnableBluetooth)
    }

    fun onEvent(event: BluetoothLeEvent) {
        when (event) {
            is BluetoothLeEvent.RequestEnableBluetooth -> checkBluetoothState()
            is BluetoothLeEvent.RequestPermissions -> _uiState.value = PermissionsRequired
            is BluetoothLeEvent.PermissionsGranted -> {
                // Ensure we have a DataLoaded state to work with, defaulting scanAllDevices
                if (_uiState.value !is DataLoaded) {
                    _uiState.value = DataLoaded(
                        scanAllDevices = false, // Default to specific scan
                        activeDevice = null,
                        discoveredDevices = emptyList()
                    )
                }
                fetchBleDevices() // Start collecting device updates
            }
            is BluetoothLeEvent.PermissionsDenied -> _uiState.value = PermissionsDenied
            is BluetoothLeEvent.FetchDevices -> fetchBleDevices()
            is BluetoothLeEvent.ConnectToSensorTag -> connectToActiveDevice()
            BluetoothLeEvent.StartScan -> scanning()
            BluetoothLeEvent.StopScan -> stopping()
            BluetoothLeEvent.GattCharacteristicList -> gattCharacteristicList // This is a StateFlow, direct access is fine
            is BluetoothLeEvent.ReadCharacteristics -> readAllCharacteristics()
            BluetoothLeEvent.ToggleScanMode -> {
                _uiState.update { currentState ->
                    if (currentState is DataLoaded) {
                        currentState.copy(
                            scanAllDevices = !currentState.scanAllDevices,
                            activeDevice = null, // Reset active device on mode toggle
                            discoveredDevices = emptyList() // Clear previous scan results
                        )
                    } else {
                        // If not DataLoaded, initialize with the new scan mode
                        DataLoaded(
                            scanAllDevices = true, // Default to true if current state was not DataLoaded
                            activeDevice = null,
                            discoveredDevices = emptyList()
                        )
                    }
                }
            }

            is BluetoothLeEvent.SetActiveDevice -> {
                _uiState.update { currentState ->
                    if (currentState is BluetoothLeUiState.DataLoaded) {
                        currentState.copy(activeDevice = event.device)
                    } else {
                        currentState // Or log an error if not expected
                    }
                }
            }
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

        _uiState.update { currentState ->
            val currentScanAllDevices = (currentState as? BluetoothLeUiState.DataLoaded)?.scanAllDevices ?: false
            if (currentState is BluetoothLeUiState.DataLoaded) {
                currentState.copy(activeDevice = null, discoveredDevices = emptyList()) // Clear previous results
            } else {
                // Ensure DataLoaded state before scan if somehow not set
                BluetoothLeUiState.DataLoaded(
                    scanAllDevices = currentScanAllDevices,
                    activeDevice = null,
                    discoveredDevices = emptyList()
                )
            }
        }

        viewModelScope.launch {
            try {
                // Get the scanAll flag from the potentially updated state
                val scanAll = (_uiState.value as? BluetoothLeUiState.DataLoaded)?.scanAllDevices ?: false
                bleRepository.startScan(scanAll)
            } catch (e: Exception) {
                _uiState.value = BluetoothLeUiState.Error("Failed to start scan: ${e.message}")
                _isStartButtonEnabled.value = true // Re-enable button on error
            }
            delay(5000L) // Scan duration, consider making this configurable or based on scanState
            _isStartButtonEnabled.value = true
        }
    }

    private fun stopping() {
        viewModelScope.launch {
            try {
                bleRepository.stopScan()
            } catch (e: Exception) {
                _uiState.value = BluetoothLeUiState.Error("Failed to stop scan: ${e.message}")
            }
        }
    }

    private fun checkBluetoothState() {
        if (!bluetoothAdapter.isEnabled) {
            if (!isBluetoothDialogAlreadyShown) {
                _uiState.value = BluetoothLeUiState.ShowBluetoothDialog
                isBluetoothDialogAlreadyShown = true
            }
        } else {
            // Bluetooth is enabled, ensure we are in a DataLoaded state if coming from PermissionsRequired/ShowBluetoothDialog
            if (_uiState.value !is BluetoothLeUiState.DataLoaded) {
                 _uiState.value = BluetoothLeUiState.DataLoaded(
                    scanAllDevices = false, // Default
                    activeDevice = null,
                    discoveredDevices = emptyList()
                )
            }
            // If permissions are also granted, implicitly, one might call fetchBleDevices here or rely on PermissionsGranted event
        }
    }

    private fun connectToActiveDevice() {
        viewModelScope.launch {
            val currentActiveDevice = (_uiState.value as? BluetoothLeUiState.DataLoaded)?.activeDevice
            if (currentActiveDevice != null) {
                // Assuming bleRepository.connectToDevice() uses an internally managed device
                // or needs the device info. The current repo takes no args.
                bleRepository.connectToDevice() // If it connects to its 'currentItem'
            } else {
                Log.w(TAG, "ConnectToActiveDevice called but no active device set in UIState")
                _uiState.value = BluetoothLeUiState.Error("No device selected or found to connect.")
            }
        }
    }

    private fun fetchBleDevices() {
        viewModelScope.launch {
            bleRepository.fetchBluetoothDevice().collect { deviceFromRepo -> // deviceFromRepo is BluetoothDeviceInfo?
                _uiState.update { currentUiState ->
                    if (currentUiState is BluetoothLeUiState.DataLoaded) {
                        if (deviceFromRepo == null) {
                            // If scanAll is false and repo emits null, it means the specific device was lost or not found
                            if (!currentUiState.scanAllDevices) {
                                currentUiState.copy(activeDevice = null)
                            } else {
                                currentUiState // No change if scanning for all and a null comes through (should ideally not happen)
                            }
                        } else {
                            if (currentUiState.scanAllDevices) {
                                val updatedDiscoveredDevices = currentUiState.discoveredDevices
                                    .filterNot { it.address == deviceFromRepo.address } + deviceFromRepo
                                currentUiState.copy(discoveredDevices = updatedDiscoveredDevices)
                            } else {
                                // If not scanning for all, this device is the one we were looking for.
                                currentUiState.copy(
                                    activeDevice = deviceFromRepo,
                                    // Optionally, also set it as the only discovered device for UI consistency
                                    discoveredDevices = listOf(deviceFromRepo) 
                                )
                            }
                        }
                    } else {
                        currentUiState // Not DataLoaded, so no place to put discovered devices yet
                    }
                }
            }
        }
    }
}
