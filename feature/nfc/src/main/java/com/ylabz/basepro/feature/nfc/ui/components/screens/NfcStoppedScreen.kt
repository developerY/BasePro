package com.ylabz.basepro.feature.nfc.ui.components.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.nfc.ui.NfcReadEvent

// NFC available but not scanning yet.
@Composable
fun NfcStoppedScreen(
    onEvent: (NfcReadEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("NFC is ready. Tap the button to start scanning.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onEvent(NfcReadEvent.StartScan) }) {
            Text("Start Scan")
        }
    }
}