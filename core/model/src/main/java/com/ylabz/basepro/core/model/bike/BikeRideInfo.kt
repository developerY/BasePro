package com.ylabz.basepro.core.model.bike

import com.google.android.gms.maps.model.LatLng

data class BikeRideInfo(

    val location: LatLng?,
    val currentSpeed: Double,
    val currentTripDistance: Double,
    val totalDistance: Double,
    val rideDuration: String,
    val settings: Map<String, List<String>>,
    val averageSpeed: Double,
    val elevation : Double,
    val heading : Float,

    // Connected bike state
    val isBikeConnected: Boolean,
    val batteryLevel: Int?,
    val motorPower: Float?
)

// Represents basic health information (e.g., from Google Health Connect).
data class HealthStats(
    val heartRate: Int,
    val calories: Double
    // Add other health-related fields if needed, e.g.:
    // val steps: Int,
    // val activeMinutes: Int
)

// Represents NFC data extracted from a scanned tag.
data class NfcData(
    val tagInfo: String,
    // Optionally, you could also include a timestamp or other metadata:
    val timestamp: Long = System.currentTimeMillis()
)

data class ConnectionStatus(
    val isConnected: Boolean,
    val batteryLevel: Int? = null // Battery level percentage, or null if not connected.
)

/*
val bikeRideInfo = BikeRideInfo(
    isBikeConnected = bikeState.isBikeConnected,
    currentSpeed = bikeState.currentSpeed,
    currentTripDistance = bikeState.currentDistance,
    totalDistance = bikeState.totalDistance,
    rideDuration = bikeState.rideDuration,
    settings = bikeState.settings,
    location = bikeState.location.let { LatLng(it?.longitude ?: 0.0, it?.longitude ?: 0.0) },
    heading = bikeState.heading,
    averageSpeed = bikeState.averageSpeed,
    elevation = bikeState.elevation,
    batteryLevel = bikeState.batteryLevel,
    motorPower = bikeState.motorPower
)
*/

