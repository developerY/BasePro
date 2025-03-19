package com.ylabz.basepro.feature.nfc.ui

/**
 * NfcNotSupported: The device has no NFC hardware.
 * NfcDisabled: NFC is off in device settings.
 * WaitingForTag: The app is actively waiting for an NFC tag to be scanned.
 * TagScanned: A tag has been successfully scanned.
 * Loading: Some in-progress operation.
 * Error: Show an error message.
 */

// Example of an updated NfcUiState
sealed class NfcUiState {
    object Stopped : NfcUiState()                // NFC is available but not scanning.
    object Loading : NfcUiState()
    data class Error(val message: String) : NfcUiState()
    object NfcNotSupported : NfcUiState()
    object NfcDisabled : NfcUiState()
    object WaitingForTag : NfcUiState()          // Actively scanning for reading.
    data class TagScanned(val tagInfo: String) : NfcUiState()
    object Writing : NfcUiState()                // Actively writing.
    data class WriteSuccess(val message: String) : NfcUiState()
    data class WriteError(val error: String) : NfcUiState()
}

sealed class NfcUiStateOld {
    object NfcNotSupported : NfcUiStateOld()
    object NfcDisabled : NfcUiStateOld()
    object WaitingForTag : NfcUiStateOld()
    data class TagScanned(val tagInfo: String) : NfcUiStateOld()
    object Loading : NfcUiStateOld()
    data class Error(val message: String) : NfcUiStateOld()
}


sealed class NfcUiStateHold {
    object Idle : NfcUiStateHold()
    object Loading : NfcUiStateHold()
    data class Error(val message: String) : NfcUiStateHold()
    object NfcNotSupported : NfcUiStateHold()
    object NfcDisabled : NfcUiStateHold()
    object WaitingForTag : NfcUiStateHold()
    data class TagScanned(val tagInfo: String) : NfcUiStateHold()
}


