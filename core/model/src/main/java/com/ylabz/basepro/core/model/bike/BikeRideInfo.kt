package com.ylabz.basepro.core.model.bike

import android.location.Location
import com.google.android.gms.maps.model.LatLng

enum class RideState {
    NotStarted,
    Riding,
    Paused,
    Ended
}

/**
 * Data class representing the bike's motor information.
 * Both batteryLevel and motorPower are nullable because not every bike
 * might have eBike capabilities.
 */
data class BikeMotorData(
    val batteryLevel: Int?,  // Null if unavailable (e.g. non-eBike)
    val motorPower: Float?     // Null if unavailable
)

// Define a data class to hold the combined sensor data
data class CombinedSensorData(
    val location: Location,
    val speedKmh: Float,
    val traveledDistance: Float,
    val totalDistance: Float?,
    val remainingDistance: Float?,
    val elevation: Float,
    val heading: Float,
)

data class BikeRideInfo(
    val location: LatLng?,
    val currentSpeed: Double,
    val averageSpeed: Double,
    val currentTripDistance: Float,
    val totalTripDistance: Float?,
    val remainingDistance: Float?,
    val rideDuration: String,
    val settings: Map<String, List<String>>,
    val heading : Float,
    val elevation : Double,
    // Connected bike state
    val isBikeConnected: Boolean,
    val batteryLevel: Int?,
    val motorPower: Float?,

    // New field:
    val rideState: RideState = RideState.NotStarted
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

