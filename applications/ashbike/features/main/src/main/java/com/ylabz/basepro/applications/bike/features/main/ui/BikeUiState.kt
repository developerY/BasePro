package com.ylabz.basepro.applications.bike.features.main.ui

import com.ylabz.basepro.core.model.bike.BikeRideInfo

sealed class BikeUiState {
    object Loading : BikeUiState()
    object Idle    : BikeUiState()
    data class Success(val bikeData: BikeRideInfo) : BikeUiState()
    data class Error(val message: String)             : BikeUiState()
}


/*
"Theme" to listOf("Light", "Dark", "System Default"),
        "Language" to listOf("English", "Spanish", "French"),
        "Notifications" to listOf("Enabled", "Disabled")
 */
/*
val location: Location? = null,
        val currentSpeed: Double = 0.0,     // current speed (km/h)
        val currentDistance: Double = 0.0, // current trip distance (km)
        val totalDistance: Double = 50.0,   // total trip distance (km)
        val locationString :String = "Santa Barbara, US",
        val averageSpeed : Double = 25.0,
        val elevation : Double = 150.0,
        val heading : Float = 0f,
        val bikeID : String? = null,
        val batteryLevel : Int? = null,
        val motorPower : Float? = null,
        val isBikeConnected : Boolean = false,

        // Just a place holder
        val speedKmh : Float = 0.0f,
        // Just a place holder
        val remainingDistance : Float = 0.0f,

        val rideDuration : String = "0h 0m"
 */