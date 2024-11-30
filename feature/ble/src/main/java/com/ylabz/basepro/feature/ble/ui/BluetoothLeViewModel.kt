package com.ylabz.basepro.feature.ble.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.BluetoothLeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BluetoothLeViewModel @Inject constructor(
    private val repository: BluetoothLeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BluetoothLeUiState>(BluetoothLeUiState.Loading)
    val uiState: StateFlow<BluetoothLeUiState> = _uiState.asStateFlow()

    fun handleEvent(event: BluetoothLeEvent) {
        when (event) {
            BluetoothLeEvent.FetchDevices -> fetchDevices()
        }
    }

    private fun fetchDevices() {
        viewModelScope.launch {
            _uiState.value = BluetoothLeUiState.Loading
            try {
                val devices = repository.fetchBluetoothDevices()
                _uiState.value = BluetoothLeUiState.Success(devices)
            } catch (e: Exception) {
                _uiState.value = BluetoothLeUiState.Error("Failed to fetch devices: ${e.message}")
            }
        }
    }
}
