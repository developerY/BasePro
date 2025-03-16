package com.ylabz.basepro.feature.nfc.ui

/**
 * NfcNotSupported: The device has no NFC hardware.
 * NfcDisabled: NFC is off in device settings.
 * WaitingForTag: The app is actively waiting for an NFC tag to be scanned.
 * TagScanned: A tag has been successfully scanned.
 * Loading: Some in-progress operation.
 * Error: Show an error message.
 */

sealed class NfcUiStateOld {
    object NfcNotSupported : NfcUiStateOld()
    object NfcDisabled : NfcUiStateOld()
    object WaitingForTag : NfcUiStateOld()
    data class TagScanned(val tagInfo: String) : NfcUiStateOld()
    object Loading : NfcUiStateOld()
    data class Error(val message: String) : NfcUiStateOld()
}


sealed class NfcUiState {
    object Idle : NfcUiState()
    object Loading : NfcUiState()
    data class Error(val message: String) : NfcUiState()
    object NfcNotSupported : NfcUiState()
    object NfcDisabled : NfcUiState()
    object WaitingForTag : NfcUiState()
    data class TagScanned(val tagInfo: String) : NfcUiState()
}


