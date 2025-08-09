package com.ylabz.basepro.applications.bike.features.settings.ui

import com.ylabz.basepro.applications.bike.database.ProfileData
import com.ylabz.basepro.core.model.bike.LocationEnergyLevel

// 3a) Extend your UiState to carry both the *options* and the *current selection*
/**
 * Combine app-wide options with current selections AND rider profile
 */
sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class Success(
        val options: Map<String, List<String>>,
        val selections: Map<String, String>,
        val profile: ProfileData? = null, // Make nullable and provide default
        val isProfileIncomplete: Boolean = true, // Add this field
        val currentEnergyLevel: LocationEnergyLevel = LocationEnergyLevel.BALANCED, // Added field
        val gpsAccuracy: String = "Balanced"
    ) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

/*
"Theme" to listOf("Light", "Dark", "System Default"),
"Language" to listOf("English", "Spanish", "French"),
"Notifications" to listOf("Enabled", "Disabled"),
"Units" to listOf("Imperial (English)", "Metric (SI)")
 */
