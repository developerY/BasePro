package com.ylabz.basepro.feature.nfc.ui

import android.nfc.Tag
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.nfc.NfcRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NfcViewModel @Inject constructor(
    private val nfcRepository: NfcRepository
) : ViewModel() {

    // Backing property for UI state, starting with Loading.
    private val _uiState = MutableStateFlow<NfcUiState>(NfcUiState.Loading)
    val uiState: StateFlow<NfcUiState> = _uiState.asStateFlow()

    // Keep a reference to the current scanning job so we can cancel/restart if needed.
    private var scanningJob: Job? = null

    init {
        // Check NFC capabilities and set the appropriate UI state.
        checkNfcCapabilities()
        // If NFC is available and enabled, start scanning.
        if (_uiState.value is NfcUiState.WaitingForTag) {
            startScanning()
        }
    }

    /**
     * Check if NFC is supported and enabled.
     * If not, update the UI state accordingly.
     */
    private fun checkNfcCapabilities() {
        if (!nfcRepository.isNfcSupported()) {
            _uiState.value = NfcUiState.NfcNotSupported
        } else if (!nfcRepository.isNfcEnabled()) {
            _uiState.value = NfcUiState.NfcDisabled
        } else {
            _uiState.value = NfcUiState.WaitingForTag
        }
    }

    /**
     * Starts (or restarts) collecting NFC data from the repository.
     */
    private fun startScanning() {
        // Cancel any existing scanning job to avoid multiple collectors.
        scanningJob?.cancel()
        scanningJob = viewModelScope.launch {
            _uiState.value = NfcUiState.Loading
            nfcRepository.scannedDataFlow
                .catch { e ->
                    _uiState.value = NfcUiState.Error("Error scanning NFC tag: ${e.message}")
                }
                .collectLatest { data ->
                    if (data.isNotEmpty()) {
                        _uiState.value = NfcUiState.TagScanned(tagInfo = data)
                    } else {
                        _uiState.value = NfcUiState.Error("Empty NFC tag read.")
                    }
                }
        }
    }

    /**
     * Handles UI events.
     */
    fun onEvent(event: NfcReadEvent) {
        when (event) {
            NfcReadEvent.StartScan -> {
                checkNfcCapabilities()
                if (_uiState.value is NfcUiState.WaitingForTag) {
                    startScanning()
                }
            }
            NfcReadEvent.Retry -> {
                checkNfcCapabilities()
                if (_uiState.value is NfcUiState.WaitingForTag) {
                    startScanning()
                }
            }
            NfcReadEvent.EnableNfc -> {
                // Instruct the user to enable NFC in settings.
                // After user action, re-check the NFC state.
                checkNfcCapabilities()
                if (_uiState.value is NfcUiState.WaitingForTag) {
                    startScanning()
                }
            }
        }
    }

    /**
     * Called from the Activity's onNewIntent when an NFC tag is detected.
     * The repository will process the tag and emit scanned data.
     */
    fun onNfcTagScanned(tag: Tag) {
        nfcRepository.onTagScanned(tag)
    }
}



