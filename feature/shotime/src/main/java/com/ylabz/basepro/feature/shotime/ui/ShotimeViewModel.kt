package com.ylabz.basepro.feature.shotime.ui

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.bluetoothLE.BluetoothLeRepository
import com.ylabz.basepro.core.model.ble.ScanState
import com.ylabz.basepro.core.model.shotime.ShotimeSessionData
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
class ShotimeViewModel @Inject constructor(
) : ViewModel() {
    private val TAG = Logging.getTag(this::class.java)

    // StateFlow for detecting the TI Tag Sensor
    private val _uiState = MutableStateFlow<ShotimeUiState>(ShotimeUiState.Loading)
    val uiState: StateFlow<ShotimeUiState> = _uiState


    private var isBluetoothDialogAlreadyShown = false

    init {
        loadShotimes() // SF latitude and longitude
    }

    private fun loadShotimes() {
        _uiState.value = ShotimeUiState.Loading
        viewModelScope.launch {
            try {
                val dat : ShotimeSessionData = ShotimeSessionData(
                    shot = "one"
                )
                val listDat = listOf(dat)
                _uiState.value = ShotimeUiState.Success(listDat)
            } catch (e: Exception) {
                _uiState.value = ShotimeUiState.Error("Failed to load coffee shops")
            }
        }
    }


    fun onEvent(event: ShotimeEvent) {
        when (event) {
            ShotimeEvent.Shotime -> getShotimes()
        }
    }

    private fun getShotimes() {
        viewModelScope.launch {

        }
    }
}

