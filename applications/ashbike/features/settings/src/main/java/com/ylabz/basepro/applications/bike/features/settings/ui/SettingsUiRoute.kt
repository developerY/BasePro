package com.ylabz.basepro.applications.bike.features.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ylabz.basepro.feature.ble.ui.BluetoothLeViewModel
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel
import com.ylabz.basepro.feature.nfc.ui.components.screens.ErrorScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.LoadingScreen

internal const val ROUTE_NAME = "settings_ui_route" // Assuming this was intended to be here or is defined elsewhere for navigation
internal const val ARG_CARD_TO_EXPAND = "cardToExpandArg" // Added argument name

@Composable
fun SettingsUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    //nfcUiState : NfcUiState,
    //nfcEvent : (NfcRwEvent) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
    nfcViewModel: NfcViewModel = hiltViewModel(),
    bleViewModel: BluetoothLeViewModel = hiltViewModel(),
    initialCardKeyToExpand: String? // Added parameter
) {
    val uiState = viewModel.uiState.collectAsState().value
    val nfcUiState = nfcViewModel.uiState.collectAsState().value
    val bleUiState = bleViewModel.uiState.collectAsState().value
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
                nfcUiState = nfcUiState,
                nfcEvent = nfcViewModel::onEvent,
                bleUiState = bleUiState,
                bleEvent = bleViewModel::onEvent,
                navTo = navTo,
                initialCardKeyToExpand = initialCardKeyToExpand // Passed to SettingsScreenEx
            )
        }
    }
}
