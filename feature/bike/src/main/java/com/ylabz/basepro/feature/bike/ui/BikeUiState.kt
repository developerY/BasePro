package com.ylabz.basepro.feature.bike.ui

import com.google.android.gms.maps.model.LatLng

sealed class BikeUiState {
    object Loading : BikeUiState()
    data class Success(
        val settings: Map<String, List<String>>,
        val location: LatLng? = null
    ) : BikeUiState()
    data class Error(val message: String) : BikeUiState()
}


/*
"Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
 */
