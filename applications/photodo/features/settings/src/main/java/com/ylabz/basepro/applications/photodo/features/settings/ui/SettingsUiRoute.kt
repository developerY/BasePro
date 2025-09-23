package com.ylabz.basepro.applications.photodo.features.settings.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

internal const val ROUTE_NAME =
    "settings_ui_route" // Assuming this was intended to be here or is defined elsewhere for navigation
internal const val ARG_CARD_TO_EXPAND = "cardToExpandArg" // Added argument name

@Composable
fun SettingsUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    //nfcUiState : NfcUiState,
    //nfcEvent : (NfcRwEvent) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
    initialCardKeyToExpand: String? // Added parameter
) {
    val uiState = viewModel.uiState.collectAsState().value
    // val showGpsCountdown by viewModel.showGpsCountdown.collectAsState() // Collect the new state

    when (val state = uiState) {
        is SettingsUiState.Loading -> {
            LoadingScreen()
        }

        is SettingsUiState.Error -> {
            // ErrorScreen(errorMessage = state.message)
        }

        is SettingsUiState.Success -> {
            SettingsScreenEx(
                modifier = modifier,
                uiState = state,
                onEvent = viewModel::onEvent,
                navTo = navTo,
                initialCardKeyToExpand = initialCardKeyToExpand // Passed to SettingsScreenEx
            )
        }
    }
}
// TODO: NEED to replace with Materail 3 loading screen
@Composable
fun LoadingScreen() {
    Text(
        text = "Loading... Not sure what ... how did you get here?",
        modifier = Modifier.fillMaxSize()
    )
}