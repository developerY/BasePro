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

    // Flag to indicate whether we're in write mode.
    var isWritingMode: Boolean = false
        private set

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

    fun onEvent(event: NfcReadEvent) {
        when (event) {
            NfcReadEvent.StartScan -> {
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
            NfcReadEvent.StartWrite -> {
                // Cancel any active scanning and clear previous tag data.
                scanningJob?.cancel()
                nfcRepository.clearScannedData()
                // Enter write mode.
                isWritingMode = true
                Log.d("NFC", "StartWrite event: isWritingMode set to true.")
                _uiState.value = NfcUiState.Writing
            }
            NfcReadEvent.StopWrite -> {
                // Exit write mode.
                isWritingMode = false
                _uiState.value = NfcUiState.Stopped
            }
            is NfcReadEvent.UpdateWriteText -> {
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
        Log.d("NFC", "onNfcWriteTag: Received tag for writing: ${tag.id.joinToString("") { "%02x".format(it) }}")
        viewModelScope.launch {
            _uiState.value = NfcUiState.Writing
            val success = nfcRepository.writeTag(tag, textToWrite)
            if (success) {
                _uiState.value = NfcUiState.WriteSuccess("Write successful!")
                Log.d("NFC", "onNfcWriteTag: Write successful!")
            } else {
                _uiState.value = NfcUiState.WriteError("Failed to write to the tag.")
                Log.e("NFC", "onNfcWriteTag: Write failed.")
            }
        }
    }


    // Methods to control writing mode.
    fun startWriting(text: String) {
        isWritingMode = true
        textToWrite = text
        _uiState.value = NfcUiState.Stopped // Clear previous write states.
    }

    fun stopWriting() {
        isWritingMode = false
        _uiState.value = NfcUiState.Stopped
    }

    fun updateTextToWrite(text: String) {
        textToWrite = text
    }
}
