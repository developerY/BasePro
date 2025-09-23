package com.ylabz.basepro.applications.photodo.features.settings.ui

// Represents the different states for the PhotoDo Settings screen
sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val options: Map<String, List<String>>, // All available options for settings
        val selections: Map<String, String>    // Currently selected values for each setting
    ) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}
