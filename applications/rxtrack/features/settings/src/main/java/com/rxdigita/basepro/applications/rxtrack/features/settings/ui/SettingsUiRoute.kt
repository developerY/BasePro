package com.rxdigita.basepro.applications.rxtrack.features.settings.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

// TODO: You'''ll need to define the properties for Success if it'''s a data class, or adjust its usage.
sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Error(val message: String) : SettingsUiState
    data class Success(val data: String) : SettingsUiState // Example: Added data field
}

@Composable
fun SettingsUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
    // Add other ViewModels like NfcViewModel, BluetoothLeViewModel if needed for rxdigita settings
) {
    val uiState = viewModel.uiState.collectAsState().value

    when (uiState) {
        is SettingsUiState.Loading -> {
            Text(modifier = modifier, text = "Loading Settings for RxDigita...")
        }
        is SettingsUiState.Error -> {
            Text(modifier = modifier, text = "Error in RxDigita Settings: ${uiState.message}")
        }
        is SettingsUiState.Success -> {
            SettingsScreen(
                onNavigateBack = { navTo("rxdigita_main") },
            )
        }
    }
}
