package com.ylabz.basepro.feature.ble.ui

// import android.bluetooth.BluetoothDevice // No longer directly used here
import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepository
import com.ylabz.basepro.core.model.ble.ScanState
import com.ylabz.basepro.core.util.Logging
import com.ylabz.basepro.feature.ble.ui.BluetoothLeUiState.DataLoaded
import com.ylabz.basepro.feature.ble.ui.BluetoothLeUiState.Error
import com.ylabz.basepro.feature.ble.ui.BluetoothLeUiState.PermissionsDenied
import com.ylabz.basepro.feature.ble.ui.BluetoothLeUiState.PermissionsRequired
import com.ylabz.basepro.feature.ble.ui.BluetoothLeUiState.ShowBluetoothDialog
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

    private val _uiState = MutableStateFlow<BluetoothLeUiState>(PermissionsRequired)
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
                // Ensure we have a DataLoaded state to work with, defaulting to scanAllDevices = true
                if (_uiState.value !is DataLoaded) {
                    _uiState.value = DataLoaded(
                        scanAllDevices = true, // MODIFIED: Default to true
                        activeDevice = null,
                        discoveredDevices = emptyList()
                    )
                }
                fetchBleDevices() // Start collecting device updates
            }

            is BluetoothLeEvent.PermissionsDenied -> _uiState.value = PermissionsDenied
            is BluetoothLeEvent.FetchDevices -> fetchBleDevices()
            is BluetoothLeEvent.ConnectToSensorTag -> connectToActiveDevice() // "ConnectToSensorTag" is now a misnomer, consider renaming event if it's generic connect
            BluetoothLeEvent.StartScan -> scanning()
            BluetoothLeEvent.StopScan -> stopping()
            BluetoothLeEvent.GattCharacteristicList -> gattCharacteristicList
            is BluetoothLeEvent.ReadCharacteristics -> readAllCharacteristics()
            // BluetoothLeEvent.ToggleScanMode -> { // REMOVED: No longer needed
            // }
            is BluetoothLeEvent.SetActiveDevice -> {
                _uiState.update { currentState ->
                    if (currentState is DataLoaded) {
                        currentState.copy(activeDevice = event.device)
                    } else {
                        currentState
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
            // Ensure DataLoaded state and scanAllDevices is true
            if (currentState is DataLoaded) {
                currentState.copy(
                    scanAllDevices = true, // MODIFIED: Ensure true
                    activeDevice = null,
                    discoveredDevices = emptyList() // Clear previous results
                )
            } else {
                DataLoaded(
                    scanAllDevices = true, // MODIFIED: Ensure true
                    activeDevice = null,
                    discoveredDevices = emptyList()
                )
            }
        }

        viewModelScope.launch {
            try {
                // MODIFIED: Explicitly pass true for scanning all devices
                bleRepository.startScan(true)
                Logging.d(TAG, "Scan started (scanAll=true)")
            } catch (e: Exception) {
                Logging.e(TAG, "Failed to start scan: ${e.message}", e)
                _uiState.value = Error("Failed to start scan: ${e.message}")
                _isStartButtonEnabled.value = true
            }
            // Consider if this delay is always needed, or if scan should run until explicitly stopped.
            // For continuous discovery, you might rely on bleRepository.scanState.
            // For now, keeping original delay.
            delay(5000L)
            // If scan is meant to be continuous, this auto-stop and button re-enable might be an issue.
            // But if it's a timed scan, it's okay.
            if (scanState.value == ScanState.SCANNING) { // Only stop if it was successfully started
                stopping() // Automatically stop after delay, consider if this is desired.
            }
            _isStartButtonEnabled.value = true // Re-enable button
        }
    }

    private fun stopping() {
        viewModelScope.launch {
            try {
                bleRepository.stopScan()
            } catch (e: Exception) {
                _uiState.value = Error("Failed to stop scan: ${e.message}")
            }
        }
    }

    private fun checkBluetoothState() {
        if (!bluetoothAdapter.isEnabled) {
            if (!isBluetoothDialogAlreadyShown) {
                _uiState.value = ShowBluetoothDialog
                isBluetoothDialogAlreadyShown = true
            }
        } else {
            if (_uiState.value !is DataLoaded) {
                _uiState.value = DataLoaded(
                    scanAllDevices = true, // MODIFIED: Default to true
                    activeDevice = null,
                    discoveredDevices = emptyList()
                )
            }
            // If permissions are also granted, implicitly, one might call fetchBleDevices here or rely on PermissionsGranted event
        }
    }

    private fun connectToActiveDevice() {
        viewModelScope.launch {
            val currentActiveDevice = (_uiState.value as? DataLoaded)?.activeDevice
            if (currentActiveDevice != null) {
                // Assuming bleRepository.connectToDevice() uses an internally managed device
                // or needs the device info. The current repo takes no args.
                bleRepository.connectToDevice() // If it connects to its 'currentItem'
            } else {
                Log.w(TAG, "ConnectToActiveDevice called but no active device set in UIState")
                _uiState.value = Error("No device selected or found to connect.")
            }
        }
    }

    private fun fetchBleDevices() {
        viewModelScope.launch {
            Logging.d(TAG, "Starting to collect devices from repository")
            bleRepository.fetchBluetoothDevice()
                .collect { deviceFromRepo -> // deviceFromRepo is BluetoothDeviceInfo?
                    Logging.d(TAG, "Device from repo: ${deviceFromRepo?.address ?: "null"}")
                    _uiState.update { currentUiState ->
                        if (currentUiState is DataLoaded) {
                            // The scanAllDevices flag in currentUiState should now reliably be true
                            // if a scan was started via scanning()
                            if (deviceFromRepo == null) {
                                // If scanning all and repo emits null, it might mean end of a specific device emission,
                                // or simply no new device. If scanAllDevices is true, we usually accumulate.
                                // The original logic for specific device (scanAllDevices=false) is less relevant now.
                                if (!currentUiState.scanAllDevices) { // This branch becomes less likely if scanAllDevices is always true
                                    currentUiState.copy(activeDevice = null)
                                } else {
                                    currentUiState // No change if scanning for all and a null comes through
                                }
                            } else {
                                // Always accumulate if scanAllDevices is true (which should be the case now for scans)
                                val updatedDiscoveredDevices = currentUiState.discoveredDevices
                                    .filterNot { it.address == deviceFromRepo.address } + deviceFromRepo
                                Logging.d(
                                    TAG,
                                    "Updating discovered devices. New count: ${updatedDiscoveredDevices.size}"
                                )
                                currentUiState.copy(discoveredDevices = updatedDiscoveredDevices)
                            }
                        } else {
                            currentUiState
                        }
                    }
                }
        }
    }
}
