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

    private val _uiState = MutableStateFlow<NfcUiState>(NfcUiState.Loading)
    val uiState: StateFlow<NfcUiState> = _uiState.asStateFlow()

    private var scanningJob: Job? = null

    init {
        // Check NFC capabilities immediately, but do NOT start scanning.
        checkNfcCapabilities()
    }

    /**
     * Check if NFC is supported and enabled; update state accordingly.
     */
    private fun checkNfcCapabilities() {
        if (!nfcRepository.isNfcSupported()) {
            _uiState.value = NfcUiState.NfcNotSupported
        } else if (!nfcRepository.isNfcEnabled()) {
            _uiState.value = NfcUiState.NfcDisabled
        } else {
            // NFC is supported and enabled; start in a Stopped state until the user triggers scanning.
            _uiState.value = NfcUiState.Stopped
        }
    }

    /**
     * Start scanning by collecting data from scannedDataFlow.
     */
    private fun startScanning() {
        scanningJob?.cancel()
        scanningJob = viewModelScope.launch {
            // Transition to actively scanning
            _uiState.value = NfcUiState.WaitingForTag

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
     * Handle user-driven events from the UI layer.
     */
    fun onEvent(event: NfcReadEvent) {
        when (event) {
            NfcReadEvent.StartScan -> {
                checkNfcCapabilities()
                if (_uiState.value is NfcUiState.Stopped) {
                    startScanning()
                }
            }
            NfcReadEvent.Retry -> {
                checkNfcCapabilities()
                if (_uiState.value is NfcUiState.Stopped) {
                    startScanning()
                }
            }
            NfcReadEvent.EnableNfc -> {
                checkNfcCapabilities()
                // Remain in Stopped state until user taps StartScan.
            }
            NfcReadEvent.StopScan -> {
                // Cancel scanning and update the state to Stopped.
                scanningJob?.cancel()
                _uiState.value = NfcUiState.Stopped
            }
        }
    }

    /**
     * Called by the Activity's onNewIntent when an NFC tag is detected.
     */
    fun onNfcTagScanned(tag: Tag) {
        nfcRepository.onTagScanned(tag)
    }
}
