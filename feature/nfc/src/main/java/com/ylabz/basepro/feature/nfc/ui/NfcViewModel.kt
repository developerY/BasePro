package com.ylabz.basepro.feature.nfc.ui

import android.nfc.Tag
import android.util.Log
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

    // Replace plain isWritingMode with a StateFlow.
    private val _isWritingMode = MutableStateFlow(false)
    val isWritingMode: StateFlow<Boolean> get() = _isWritingMode.asStateFlow()

    // Text that will be written to the tag.
    var textToWrite: String = ""
        private set

    private var scanningJob: Job? = null

    init {
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

    fun onEvent(event: NfcRwEvent) {
        when (event) {
            NfcRwEvent.StartScan -> {
                scanningJob?.cancel()
                nfcRepository.clearScannedData()
                _uiState.value = NfcUiState.Stopped
                viewModelScope.launch {
                    delay(50)
                    if (_uiState.value is NfcUiState.Stopped) {
                        startScanning()
                    }
                }
            }
            NfcRwEvent.Retry -> {
                checkNfcCapabilities()
                if (_uiState.value is NfcUiState.Stopped) {
                    startScanning()
                }
            }
            NfcRwEvent.EnableNfc -> {
                checkNfcCapabilities()
            }
            NfcRwEvent.StopScan -> {
                scanningJob?.cancel()
                _uiState.value = NfcUiState.Stopped
                nfcRepository.clearScannedData()
            }
            NfcRwEvent.StartWrite -> {
                scanningJob?.cancel()
                nfcRepository.clearScannedData()
                //_isWritingMode.value = true
                Log.d("NFC", "StartWrite event: isWritingMode set to true.")
                _uiState.value = NfcUiState.Writing
            }
            NfcRwEvent.StopWrite -> {
                scanningJob?.cancel()
                _isWritingMode.value = false
                _uiState.value = NfcUiState.Stopped
                nfcRepository.clearScannedData()
            }
            is NfcRwEvent.UpdateWriteText -> {
                updateTextToWrite(event.text)
            }
        }
    }

    // Called when a tag is detected in read mode.
    fun onNfcTagScanned(tag: Tag) {
        nfcRepository.onTagScanned(tag)
    }

    // Called when a tag is detected in write mode.
    fun onNfcWriteTag(tag: Tag) {
        viewModelScope.launch {
            _uiState.value = NfcUiState.Writing
            val success = nfcRepository.writeTag(tag, textToWrite)
            if (success) {
                _uiState.value = NfcUiState.WriteSuccess("Write successful!")
            } else {
                _uiState.value = NfcUiState.WriteError("Failed to write to the tag.")
            }
        }
    }

    fun startWriting(text: String) {
        _isWritingMode.value = true
        textToWrite = text
        _uiState.value = NfcUiState.Stopped // Clear previous write states.
    }

    fun stopWriting() {
        _isWritingMode.value = false
        _uiState.value = NfcUiState.Stopped
    }

    fun updateTextToWrite(text: String) {
        textToWrite = text
    }
}
