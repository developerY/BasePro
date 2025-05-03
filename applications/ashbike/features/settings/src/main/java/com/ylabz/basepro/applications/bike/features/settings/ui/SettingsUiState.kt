package com.ylabz.basepro.applications.bike.features.settings.ui

// 3a) Extend your UiState to carry both the *options* and the *current selection*
sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(
        val options: Map<String, List<String>>,
        val selections: Map<String, String>
    ) : SettingsUiState

    data class Error(val message: String) : SettingsUiState
}

/*
"Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
 */
