package com.ylabz.basepro.feature.bike.ui

sealed interface BikeUiState {
    object Loading : BikeUiState
    data class Success(val settings: Map<String, List<String>>) : BikeUiState
    data class Error(val message: String) : BikeUiState
}

/*
"Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
 */
