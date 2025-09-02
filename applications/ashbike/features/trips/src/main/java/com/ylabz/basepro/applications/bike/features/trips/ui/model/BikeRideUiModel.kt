package com.ylabz.basepro.applications.bike.features.trips.ui.model

import android.content.res.Resources
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import com.ylabz.basepro.applications.bike.features.trips.R
import java.text.SimpleDateFormat
import java.time.Duration
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
fun RideWithLocations.toUiModel(resources: Resources): BikeRideUiModel {
    val ride = this.bikeRideEnt

    // Date and duration formatting
    val dateFormatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val endFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startDate = Date(ride.startTime)
    val endDate = Date(ride.endTime)
    val rideDuration = Duration.ofMillis(ride.endTime - ride.startTime)
    val minutes = rideDuration.toMinutes()
    val seconds = rideDuration.seconds % 60

    val durationText = if (seconds > 0) {
        resources.getString(R.string.feature_trips_model_duration_min_sec_format, minutes, seconds)
    } else {
        resources.getString(R.string.feature_trips_model_duration_min_only_format, minutes)
    }

    return BikeRideUiModel(
        rideId = ride.rideId,
        dateRange = resources.getString(
            R.string.feature_trips_model_daterange_format,
            dateFormatter.format(startDate),
            endFormatter.format(endDate)
        ),
        duration = durationText,
        distance = resources.getString(
            R.string.feature_trips_model_distance_format,
            ride.totalDistance / 1000.0
        ),
        avgSpeed = resources.getString(
            R.string.feature_trips_model_avg_speed_format,
            ride.averageSpeed
        ),
        maxSpeed = resources.getString(
            R.string.feature_trips_model_max_speed_format,
            ride.maxSpeed
        ),
        rideType = ride.rideType,
        weatherCondition = ride.weatherCondition,
        notes = ride.notes?.takeIf { it.isNotBlank() },
        isSynced = ride.isHealthDataSynced
    )
}

