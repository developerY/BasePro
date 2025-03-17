package com.ylabz.basepro.feature.nfc.ui.components.parts

import android.app.Activity
import android.nfc.NfcAdapter
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.nfc.ui.components.disableForegroundDispatch
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.nfc.ui.components.enableForegroundDispatch

@Composable
fun NfcWriteScreen(
    isWriting: Boolean,                     // Whether the app is in write mode.
    textToWrite: String,                    // The text to be written to the NFC tag.
    onTextChange: (String) -> Unit,         // Callback when the user types in the text field.
    onStartWrite: () -> Unit,               // Called when the user taps "Start Write".
    onStopWrite: () -> Unit                 // Called when the user taps "Stop Write".
) {
    // Obtain the current Activity and NFC adapter.
    val activity = LocalContext.current as? Activity
    val nfcAdapter = remember { NfcAdapter.getDefaultAdapter(activity) }

    // When isWriting is true, enable foreground dispatch.
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
            value = textToWrite,
            onValueChange = onTextChange,
            label = { Text("Text to Write") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isWriting) {
            Text("Approach an NFC tag to write the above text.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onStopWrite) {
                Text("Stop Write")
            }
        } else {
            Button(onClick = onStartWrite) {
                Text("Start Write")
            }
        }
    }
}

@Preview
@Composable
fun NfcWriteScreenPreview() {
    var isWriting = false
    var textToWrite = ""

    NfcWriteScreen(
        isWriting = isWriting,
        textToWrite = textToWrite,
        onTextChange = { text ->
            textToWrite = text
        },
        onStartWrite = {
            isWriting = true
        },
        onStopWrite = {
            isWriting = false
        })
}
