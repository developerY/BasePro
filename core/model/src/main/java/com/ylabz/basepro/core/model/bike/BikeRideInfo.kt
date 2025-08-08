package com.ylabz.basepro.core.model.bike

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

enum class RideState {
    NotStarted,
    Riding,
    //Paused,
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


data class BikeRideInfo(
    val location: LatLng?,
    val currentSpeed: Double,
    val averageSpeed: Double,
    val maxSpeed: Double,
    val currentTripDistance: Float,
    val totalTripDistance: Float?,
    val remainingDistance: Float?,
    val elevationGain: Double,
    val elevationLoss: Double,
    val caloriesBurned: Int,
    val rideDuration: String,
    val settings: ImmutableMap<String, ImmutableList<String>>,
    val heading: Float,
    val elevation: Double,
    val isBikeConnected: Boolean,
    val heartbeat: Int?,
    val batteryLevel: Int?,
    val motorPower: Float?,
    val rideState: RideState = RideState.NotStarted,
    val bikeWeatherInfo: BikeWeatherInfo? = null,
    val lastGpsUpdateTime: Long = 0L,
    val gpsUpdateIntervalMillis: Long = 0L
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
