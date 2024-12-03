package com.ylabz.basepro.feature.ble.ui

import androidx.lifecycle.ViewModel
import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothDeviceInfo
import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothJuul
import com.ylabz.basepro.core.data.repository.Bluetooth.BluetoothLeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class BluetoothVMJuul @Inject constructor(
    private val repository: BluetoothJuul
) : ViewModel() {

    private val _uiState = MutableStateFlow<BluetoothLeUiState>(BluetoothLeUiState.Loading)
    val uiState: StateFlow<BluetoothLeUiState> = _uiState.asStateFlow()

    private val foundDevices = mutableMapOf<String, BluetoothDeviceInfo>()
    private val _devices = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    val devices: StateFlow<List<BluetoothDeviceInfo>> = _devices.asStateFlow()

    private var scanScope: CoroutineScope? = null

    fun startScan() {
        if (scanScope != null) return // Scan already in progress.

        scanScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        scanScope?.launch {
            withTimeoutOrNull(SCAN_DURATION_MILLIS) {
                repository
                    .advertisements() // Returns a Flow of BLE advertisements
                    .catch { cause ->
                        _uiState.value = BluetoothLeUiState.Error(cause.message ?: "Unknown error")
                    }
                    .onCompletion { cause ->
                        scanScope = null
                        if (cause == null) {
                            _uiState.value = BluetoothLeUiState.Success(foundDevices.values.toList())
                        }
                    }
                    .collect { advertisement ->
                        val deviceInfo = BluetoothDeviceInfo(
                            name = advertisement.name ?: "Unknown",
                            address = advertisement.address
                        )
                        foundDevices[advertisement.address] = deviceInfo
                        _devices.value = foundDevices.values.toList()
                    }
            }
        }
    }

    fun stopScan() {
        scanScope?.cancel()
        scanScope = null
        _uiState.value = BluetoothLeUiState.Stopped
    }

    companion object {
        private const val SCAN_DURATION_MILLIS = 10_000L // 10 seconds
    }
}
