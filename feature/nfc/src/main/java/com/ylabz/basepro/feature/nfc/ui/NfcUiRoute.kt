package com.ylabz.basepro.feature.nfc.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.nfc.ui.components.NfcAppScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.ErrorScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.LoadingScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcDisabledScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcNotSupportedScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcTagScannedScreen
import com.ylabz.basepro.feature.nfc.ui.components.parts.NfcWaitingScreen



@Composable
fun NfcUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: NfcViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is NfcUiState.Error -> {
            ErrorScreen(
                message = (uiState as NfcUiState.Error).message,
                onRetry = { viewModel.onEvent(NfcReadEvent.Retry) }
            )
        }
        is NfcUiState.Loading -> {
            LoadingScreen()
        }
        // For all other states, show the main NfcAppScreen
        is NfcUiState.NfcNotSupported,
        is NfcUiState.NfcDisabled,
        is NfcUiState.Stopped,       // <-- Newly added (replaces Idle)
        is NfcUiState.WaitingForTag,
        is NfcUiState.TagScanned -> {
            NfcAppScreen(
                modifier = modifier,
                uiState = uiState,
                navTo = navTo,
                onEvent = { event -> viewModel.onEvent(event) }
            )
        }
    }
}
