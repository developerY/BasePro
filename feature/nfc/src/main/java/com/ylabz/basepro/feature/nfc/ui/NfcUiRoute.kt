package com.ylabz.basepro.feature.nfc.ui


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.nfc.ui.components.NfcAppScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.ErrorScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.LoadingScreen


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

        is NfcUiState.WriteError -> TODO()
        is NfcUiState.WriteSuccess -> TODO()
        NfcUiState.Writing -> TODO()
    }
}
