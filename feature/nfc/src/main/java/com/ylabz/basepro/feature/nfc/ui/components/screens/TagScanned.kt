package com.ylabz.basepro.feature.nfc.ui.components.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
////import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState

@Composable
fun TagScanned(
    uiState: NfcUiState.TagScanned,
    onEvent: (NfcRwEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("NFC Tag Scanned Successfully!")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tag Info: ${uiState.tagInfo}")
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { onEvent(NfcRwEvent.StopScan) }) {
                Text("Stop Scan")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { onEvent(NfcRwEvent.StartScan) }) {
                Text("Scan Again")
            }
        }
    }
}

/* Preview
@Composable
@Preview
fun TagScannedPreview() {
    val sampleTagInfo = "Sample Tag Info"
    val sampleUiState = NfcUiState.TagScanned(sampleTagInfo)
    TagScanned(uiState = sampleUiState, onEvent = {})
}
*/