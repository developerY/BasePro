package com.ylabz.basepro.feature.bike.ui

import com.google.android.gms.maps.model.LatLng

sealed class BikeUiState {
    object Loading : BikeUiState()
    data class Success(
        val settings: Map<String, List<String>>,
        val location: LatLng? = null,
        val currentSpeed: Double = 0.0,     // current speed (km/h)
        val currentDistance: Double = 0.0, // current trip distance (km)
        val totalDistance: Double = 50.0,   // total trip distance (km)
        val locationString :String = "Santa Barbara, US"
    ) : BikeUiState()
    data class Error(val message: String) : BikeUiState()
}


/*
"Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
 */
