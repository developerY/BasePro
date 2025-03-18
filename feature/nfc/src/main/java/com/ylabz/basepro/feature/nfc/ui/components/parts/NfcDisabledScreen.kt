package com.ylabz.basepro.feature.nfc.ui.components.parts

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
fun NfcDisabledScreen(onEnableNfc: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("NFC is disabled. Please enable NFC in your device settings.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onEnableNfc) {
            Text("Enable NFC")
        }
    }
}

@Preview
@Composable
fun NfcDisabledScreenPreview() {
    NfcDisabledScreen(onEnableNfc = {
        // Handle NFC enabling here if needed for preview
    })
}