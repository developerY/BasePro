package com.ylabz.basepro.feature.gbird.ui

import android.location.Location

sealed class GbirdUiState {
    object Loading : GbirdUiState()
    data class Success(
        val settings: Map<String, List<String>>,
        val location: Location? = null,
        val currentSpeed: Double = 0.0,     // current speed (km/h)
        val currentDistance: Double = 0.0, // current trip distance (km)
        val totalDistance: Double = 50.0,   // total trip distance (km)
        val locationString :String = "Santa Barbara, US",
        val averageSpeed : Double = 25.0,
        val elevation : Double = 150.0,
        val heading : Float = 0f,

        // Just a place holder
        val speedKmh : Float = 0.0f,
        // Just a place holder
        val remainingDistance : Float = 0.0f,

        val rideDuration : String = "0h 0m"

    ) : GbirdUiState()
    data class Error(val message: String) : GbirdUiState()
}


/*
"Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
 */
