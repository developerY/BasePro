package com.ylabz.basepro.applications.photodo.features.settings.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

/**
 * The route for the Settings screen.
 * This composable is stateless and driven by the provided ViewModel.
 */
@Composable
fun SettingsUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    // The ViewModel is now a required parameter.
    viewModel: SettingsViewModel,
    initialCardKeyToExpand: String? // Added parameter
) {
    val uiState = viewModel.uiState.collectAsState().value

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

// TODO: NEED to replace with Material 3 loading screen
@Composable
fun LoadingScreen() {
    Text(
        text = "Loading... Not sure what ... how did you get here?",
        modifier = Modifier.fillMaxSize()
    )
}
