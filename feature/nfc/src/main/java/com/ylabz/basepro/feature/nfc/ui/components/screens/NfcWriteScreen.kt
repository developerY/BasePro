package com.ylabz.basepro.feature.nfc.ui.components.screens

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.nfc.ui.NfcReadEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState

@Composable
fun NfcWriteScreen(
    state: NfcUiState,
    onEvent: (NfcReadEvent) -> Unit
) {
    // We'll use a local text state to hold the value to write.
    // In a real app, you might drive this from your ViewModel instead.
    var text by rememberSaveable { mutableStateOf("") }

    // Determine if we're in writing mode based on the state.
    // For simplicity, assume that if the state is one of these, we're in write mode.
    val isWriting = state is NfcUiState.Writing ||
            state is NfcUiState.WriteSuccess ||
            state is NfcUiState.WriteError

    val activity = LocalContext.current as? Activity
    val nfcAdapter = remember { NfcAdapter.getDefaultAdapter(activity) }

    // Enable or disable NFC foreground dispatch based on isWriting.
    DisposableEffect(isWriting) {
        if (isWriting) {
            activity?.enableForegroundDispatch(nfcAdapter)
            Log.d("NFC", "NfcWriteScreen: Enabled foreground dispatch for writing.")
        }
        onDispose {
            if (isWriting) {
                activity?.disableForegroundDispatch(nfcAdapter)
                Log.d("NFC", "NfcWriteScreen: Disabled foreground dispatch for writing.")
            }
        }
    }

    // Build the UI.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Write to NFC Tag", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                onEvent(NfcReadEvent.UpdateWriteText(newText))
            },
            label = { Text("Text to Write") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isWriting) {
            Text("Approach an NFC tag to write the above text.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onEvent(NfcReadEvent.StopWrite) }) {
                Text("Stop Write")
            }
        } else {
            Button(onClick = { onEvent(NfcReadEvent.StartWrite) }) {
                Text("Start Write")
            }
        }
    }
}

