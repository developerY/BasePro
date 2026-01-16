package com.ylabz.basepro.feature.nfc.ui


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ylabz.basepro.feature.nfc.ui.components.screens.ErrorScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.LoadingScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.NfcAppScreen


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
                onRetry = { viewModel.onEvent(NfcRwEvent.Retry) }
            )
        }

        is NfcUiState.Loading -> {
            LoadingScreen()
        }
        // For all other states—read and write—we show the main NfcAppScreen.
        is NfcUiState.NfcNotSupported,
        is NfcUiState.NfcDisabled,
        is NfcUiState.Stopped,
        is NfcUiState.WaitingForTag,
        is NfcUiState.TagScanned,
        is NfcUiState.Writing,
        is NfcUiState.WriteError,
        is NfcUiState.WriteSuccess -> {
            NfcAppScreen(
                modifier = modifier,
                uiState = uiState,
                navTo = navTo,
                onEvent = { event -> viewModel.onEvent(event) }
            )
        }
    }
}

