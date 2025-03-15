package com.ylabz.basepro.feature.nfc.ui.components

import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel
import com.ylabz.basepro.feature.nfc.ui.NfcEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NfcReaderScreen(viewModel: NfcViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState) {
            is NfcUiState.Idle -> {
                Text(text = "Tap 'Start Scan' to scan an NFC tag.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.onEvent(NfcEvent.StartScan) }) {
                    Text("Start Scan")
                }
            }
            is NfcUiState.Loading -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Scanning for NFC tag...")
            }
            is NfcUiState.Success -> {
                Text(
                    text = "Scanned Data: ${(uiState as NfcUiState.Success).data}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.onEvent(NfcEvent.Retry) }) {
                    Text("Retry")
                }
            }
            is NfcUiState.Error -> {
                Text(
                    text = "Error: ${(uiState as NfcUiState.Error).error}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.onEvent(NfcEvent.Retry) }) {
                    Text("Retry")
                }
            }
        }
    }
}
