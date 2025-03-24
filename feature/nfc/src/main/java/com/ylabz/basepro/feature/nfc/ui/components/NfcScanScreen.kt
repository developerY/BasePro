package com.ylabz.basepro.feature.nfc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.components.screens.ErrorScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.LoadingScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcDisabledScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcNotSupportedScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcScanScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcStatusBar
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcStoppedScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.TagScanned

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcScanScreen(
    modifier: Modifier = Modifier,
    uiState: NfcUiState,
    onEvent: (NfcRwEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    val innerPadding = PaddingValues(0.dp)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // Status bar showing current NFC status.
        NfcStatusBar(uiState = uiState)
        Spacer(modifier = Modifier.height(16.dp))
        when (uiState) {
            is NfcUiState.NfcNotSupported -> {
                NfcNotSupportedScreen(
                    onRetry = { onEvent(NfcRwEvent.Retry) }
                )
            }

            is NfcUiState.NfcDisabled -> {
                NfcDisabledScreen(
                    onEnableNfc = { onEvent(NfcRwEvent.EnableNfc) }
                )
            }

            is NfcUiState.Stopped -> {
                NfcStoppedScreen(
                    onEvent = { onEvent(NfcRwEvent.StartScan) }
                )
            }

            is NfcUiState.WaitingForTag -> {
                NfcScanScreen(
                    state = uiState as NfcUiState.WaitingForTag,
                    onEvent = { event -> onEvent(event) }
                )
            }

            is NfcUiState.TagScanned -> {
                TagScanned(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }

            is NfcUiState.Loading -> {
                LoadingScreen()
            }

            is NfcUiState.Error -> {
                ErrorScreen(
                    message = uiState.message,
                    onRetry = { onEvent(NfcRwEvent.Retry) }
                )
            }

            is NfcUiState.Writing,
            is NfcUiState.WriteSuccess,
            is NfcUiState.WriteError -> {
                // When in any write-related state while on the scan tab,
                // you can decide how to handle it. For now, we'll simply show Loading.
                LoadingScreen()
            }
        }
    }
}


@Preview
@Composable
fun NfcScanScreenPreview() {
    // Sample data for the preview
    val sampleUiState =
        NfcUiState.Stopped // or any other state like NfcUiState.TagScanned("Sample Tag Info")
    val sampleNavTo: (String) -> Unit = { route -> println("Navigating to $route") }
    val sampleOnEvent: (NfcRwEvent) -> Unit = { event -> println("Event: $event") }

    // Use a Box to provide a background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray) // Example background color
    ) {
        NfcScanScreen(uiState = sampleUiState, navTo = sampleNavTo, onEvent = sampleOnEvent)
    }
}

