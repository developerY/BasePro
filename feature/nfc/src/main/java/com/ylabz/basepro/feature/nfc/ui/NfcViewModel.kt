package com.ylabz.basepro.feature.nfc.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.nfc.NfcRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NfcViewModel @Inject constructor(
    private val nfcRepository: NfcRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NfcUiState>(NfcUiState.Idle)
    val uiState: StateFlow<NfcUiState> = _uiState

    init {
        viewModelScope.launch {
            nfcRepository.scannedDataFlow.collect { data ->
                _uiState.value = NfcUiState.Success(data)
            }
        }
    }

    fun onEvent(event: NfcEvent) {
        when (event) {
            is NfcEvent.StartScan -> {
                // In a real app, this could trigger UI logic to initiate scanning.
                _uiState.value = NfcUiState.Loading
                // The actual scanning is triggered externally (e.g., by the Activity receiving an NFC intent)
            }
            is NfcEvent.Retry -> {
                _uiState.value = NfcUiState.Loading
                // Optionally reset or re-trigger scanning logic.
            }
        }
    }
}
