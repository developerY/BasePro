package com.ylabz.twincam.settings.ui

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settings: Map<String, String>) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}
