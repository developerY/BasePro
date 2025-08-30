package com.ylabz.basepro.feature.nfc.ui.components.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
////import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.nfc.R
import com.ylabz.basepro.feature.nfc.ui.NfcUiState

/**
 * A simple status bar to show current NFC status.
 * Customize the layout, icons, or text as desired.
 */
/**
 * A simple status bar showing current NFC status.
 */
@Composable
fun NfcStatusBar(uiState: NfcUiState) {
    val statusMessage = when (uiState) {
        is NfcUiState.NfcNotSupported -> stringResource(R.string.nfc_status_not_supported)
        is NfcUiState.NfcDisabled -> stringResource(R.string.nfc_status_disabled)
        is NfcUiState.Stopped -> stringResource(R.string.nfc_status_ready_not_scanning)
        is NfcUiState.WaitingForTag -> stringResource(R.string.nfc_status_waiting_for_tag)
        is NfcUiState.TagScanned -> stringResource(R.string.nfc_status_tag_scanned)
        is NfcUiState.Loading -> stringResource(R.string.nfc_status_loading)
        is NfcUiState.Error -> stringResource(R.string.nfc_status_error, uiState.message)
        is NfcUiState.WriteError -> stringResource(R.string.nfc_status_write_error, uiState.error)
        is NfcUiState.WriteSuccess -> stringResource(R.string.nfc_status_write_success, uiState.message)
        NfcUiState.Writing -> stringResource(R.string.nfc_status_writing_to_tag)
    }

    Row(modifier = Modifier.padding(8.dp)) {
        Text(
            text = stringResource(R.string.nfc_status_label),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = statusMessage,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/*
@Preview
@Composable
fun NfcStatusBarPreview() {
    val uiState = NfcUiState.Stopped
    NfcStatusBar(uiState = uiState)
}
*/

