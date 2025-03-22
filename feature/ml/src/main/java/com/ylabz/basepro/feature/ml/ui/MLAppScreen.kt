package com.ylabz.basepro.feature.ml.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MLAppScreen(
    modifier: Modifier = Modifier,
    //uiState: NfcUiState,
    //navTo: (String) -> Unit,
    //onEvent: (NfcReadEvent) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(text = "ML App")
        MLKitTextReaderApp()
    }
}