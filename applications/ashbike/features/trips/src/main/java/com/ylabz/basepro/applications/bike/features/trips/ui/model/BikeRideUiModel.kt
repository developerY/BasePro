package com.ylabz.basepro.applications.bike.features.trips.ui.model

import com.ylabz.basepro.applications.bike.database.RideWithLocations
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale

/**
 * A data class representing the formatted, display-ready information for a bike ride.
 * This acts as a data transfer object (DTO) specifically for the UI layer.
 */
data class BikeRideUiModel(
    val rideId: String,
    val dateRange: String,
    val duration: String,
    val distance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val rideType: String?,
    val weatherCondition: String?,
    val notes: String?,
    val isSynced: Boolean
)

/**
 * Maps a [RideWithLocations] object from the database layer to a UI-friendly [BikeRideUiModel].
 * All data formatting logic is centralized here.
 */
fun RideWithLocations.toUiModel(): BikeRideUiModel {
    val ride = this.bikeRideEnt

    // Date and duration formatting
    val dateFormatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val endFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startDate = Date(ride.startTime)
    val endDate = Date(ride.endTime)
    val rideDuration = Duration.ofMillis(ride.endTime - ride.startTime)
    val minutes = rideDuration.toMinutes()
    val seconds = rideDuration.seconds % 60

    return BikeRideUiModel(
        rideId = ride.rideId,
        dateRange = "${dateFormatter.format(startDate)} – ${endFormatter.format(endDate)}",
        duration = "(${minutes} min${if (seconds > 0) " ${seconds}s" else ""})",
        distance = "Distance: ${"%.1f".format(ride.totalDistance / 1000)} km",
        avgSpeed = "Avg: ${"%.1f".format(ride.averageSpeed)} km/h",
        maxSpeed = "Max: ${"%.1f".format(ride.maxSpeed)} km/h",
        rideType = ride.rideType,
        weatherCondition = ride.weatherCondition,
        notes = ride.notes?.takeIf { it.isNotBlank() },
        isSynced = ride.isHealthDataSynced
    )
}
