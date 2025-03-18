package com.ylabz.basepro.feature.nfc.ui.components.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun NfcSettingsScreenPreview() {
    NfcSettingsScreen()
}


@Composable
fun NfcSettingsScreen(modifier: Modifier = Modifier) {
    // This is a stub. Add any NFC-related settings or app preferences here.
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings Screen")
    }
}