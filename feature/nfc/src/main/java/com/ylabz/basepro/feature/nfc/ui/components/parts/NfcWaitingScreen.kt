package com.ylabz.basepro.feature.nfc.ui.components.parts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NfcWaitingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Waiting for an NFC tag...\nPlease tap your NFC tag now.")
    }
}

@Preview
@Composable
fun NfcWaitingScreenPreview() {
    NfcWaitingScreen()
}

