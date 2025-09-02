package com.ylabz.basepro.feature.settings.ui

sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(val settings: Map<String, List<String>>) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

/*
"Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
 */
