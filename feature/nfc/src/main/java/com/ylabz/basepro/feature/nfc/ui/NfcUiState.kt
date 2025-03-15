package com.ylabz.basepro.feature.nfc.ui

sealed class NfcUiState {
    object Idle : NfcUiState()
    object Loading : NfcUiState()
    data class Success(val data: String) : NfcUiState()
    data class Error(val error: String) : NfcUiState()
}
