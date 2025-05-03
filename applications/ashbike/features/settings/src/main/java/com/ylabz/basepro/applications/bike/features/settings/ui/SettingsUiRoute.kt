package com.ylabz.basepro.applications.bike.features.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ylabz.basepro.feature.nfc.ui.NfcRwEvent
import com.ylabz.basepro.feature.nfc.ui.NfcUiState
import com.ylabz.basepro.feature.nfc.ui.NfcViewModel
import com.ylabz.basepro.feature.nfc.ui.components.screens.ErrorScreen
import com.ylabz.basepro.feature.nfc.ui.components.screens.LoadingScreen


@Composable
fun SettingsUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    //nfcUiState : NfcUiState,
    //nfcEvent : (NfcRwEvent) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
    nfcViewModel: NfcViewModel = hiltViewModel(),

    ) {
    val uiState = viewModel.uiState.collectAsState().value
    val nfcUiState = nfcViewModel.uiState.collectAsState().value
    when (uiState) {
        is SettingsUiState.Loading -> {
            LoadingScreen()
        }
        is SettingsUiState.Error -> {
            /*ErrorScreen(errorMessage = uiState.message) {
                viewModel.onEvent(SettingsEvent.LoadSettings)
            }*/
        }
        is SettingsUiState.Success -> {
            SettingsScreenEx(
                modifier = Modifier,
                nfcUiState = nfcUiState,
                nfcEvent = { event -> nfcViewModel.onEvent(event) },
                uiState = uiState,
                onEvent = { event -> viewModel.onEvent(event) },
                navTo = navTo
            )
        }
    }
}
