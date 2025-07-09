package com.ylabz.basepro.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.settings.ui.SettingsViewModel
import com.ylabz.basepro.settings.ui.components.ErrorScreen
import com.ylabz.basepro.settings.ui.components.LoadingScreen
import com.ylabz.basepro.settings.ui.components.SettingsCompose

@Composable
fun SettingsUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    when (uiState) {
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
