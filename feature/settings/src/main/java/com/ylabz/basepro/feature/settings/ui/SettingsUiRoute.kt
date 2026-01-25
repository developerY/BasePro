package com.ylabz.basepro.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ylabz.basepro.feature.settings.ui.components.ErrorScreen
import com.ylabz.basepro.feature.settings.ui.components.LoadingScreen
import com.ylabz.basepro.feature.settings.ui.components.SettingsCompose


@Composable
fun SettingsUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    when (val uiState = viewModel.uiState.collectAsState().value) {
        is SettingsUiState.Loading -> {
            LoadingScreen()
        }

        is SettingsUiState.Error -> {
            ErrorScreen(errorMessage = uiState.message) {
                viewModel.onEvent(SettingsEvent.LoadSettings)
            }
        }

        is SettingsUiState.Success -> {
            SettingsCompose(
                modifier = modifier,
                settings = uiState.settings,
                onEvent = { event -> viewModel.onEvent(event) },
                navTo = navTo
            )
        }
    }
}
