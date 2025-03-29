package com.ylabz.basepro.feature.nfc.ui.components.parts.unused

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NfcTagScannedScreen(tagInfo: String, onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "NFC Tag Scanned Successfully!")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Tag Info: $tagInfo")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onDone) {
            Text("Done")
        }
    }
}

@Preview
@Composable
fun NfcTagScannedScreenPreview() {
    NfcTagScannedScreen(tagInfo = "Sample Tag Info", onDone = {})
}
