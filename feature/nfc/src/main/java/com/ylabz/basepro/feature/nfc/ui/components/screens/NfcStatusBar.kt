package com.ylabz.basepro.feature.nfc.ui.components.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
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
        is NfcUiState.NfcNotSupported -> "NFC Not Supported"
        is NfcUiState.NfcDisabled -> "NFC Disabled"
        is NfcUiState.Stopped -> "NFC Ready (Not Scanning)"
        is NfcUiState.WaitingForTag -> "Scanning: Waiting for Tag"
        is NfcUiState.TagScanned -> "Tag Scanned"
        is NfcUiState.Loading -> "Loading..."
        is NfcUiState.Error -> "Error: ${uiState.message}"
        is NfcUiState.WriteError -> "Write Error: ${uiState.error}"
        is NfcUiState.WriteSuccess -> "Write Success: ${uiState.message}"
        NfcUiState.Writing -> "Writing to Tag..."
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "NFC Status: ", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = statusMessage, style = MaterialTheme.typography.bodyMedium)
    }
}


@Preview
@Composable
fun NfcStatusBarPreview() {
    val uiState = NfcUiState.Stopped
    NfcStatusBar(uiState = uiState)
}

