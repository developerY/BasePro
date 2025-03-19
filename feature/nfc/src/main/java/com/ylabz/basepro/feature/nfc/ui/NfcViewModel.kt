package com.ylabz.basepro.feature.nfc.ui

import android.nfc.Tag
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.repository.nfc.NfcRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private fun checkNfcCapabilities() {
        if (!nfcRepository.isNfcSupported()) {
            _uiState.value = NfcUiState.NfcNotSupported
        } else if (!nfcRepository.isNfcEnabled()) {
            _uiState.value = NfcUiState.NfcDisabled
        } else {
            _uiState.value = NfcUiState.Stopped
        }
    }

    private fun startScanning() {
        scanningJob?.cancel()
        scanningJob = viewModelScope.launch {
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

    fun onEvent(event: NfcReadEvent) {
        when (event) {
            NfcReadEvent.StartScan -> {
                // Always cancel any active scanning job and clear old tag data.
                scanningJob?.cancel()
                nfcRepository.clearScannedData()
                _uiState.value = NfcUiState.Stopped

                // Delay briefly to allow the state change (and UI recomposition) to take effect.
                viewModelScope.launch {
                    delay(50) // Adjust delay if necessary.
                    // Now start scanning afresh.
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
            }
            NfcReadEvent.StopScan -> {
                scanningJob?.cancel()
                _uiState.value = NfcUiState.Stopped
                nfcRepository.clearScannedData()
            }
        }
    }


    fun onNfcTagScanned(tag: Tag) {
        nfcRepository.onTagScanned(tag)
    }
}
