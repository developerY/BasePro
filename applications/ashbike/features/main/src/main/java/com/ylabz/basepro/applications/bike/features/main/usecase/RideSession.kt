package com.ylabz.basepro.applications.bike.features.main.usecase

import android.location.Location

/**
 * A snapshot of one ride session, updated in real time.
 */
/**
 * The complete snapshot of a ride, including the raw GPS path.
 */
data class RideSession(
    val startTimeMs:     Long,
    val path:            List<Location>,
    val elapsedMs:       Long,
    val totalDistanceKm: Float,
    val averageSpeedKmh: Double,
    val maxSpeedKmh:     Float,
    val elevationGainM:  Float,
    val elevationLossM:  Float,
    val caloriesBurned:  Int,
    val heading:         Float
)

data class RideSessionOld(
    val startTimeMs: Long,           // Wall-clock time when session started
    val path: List<Location>,        // All raw GPS fixes so far
    val totalDistanceM: Float,       // Meters traveled
    val averageSpeedKmh: Double,     // km/h
    val maxSpeedKmh: Float,          // km/h
    val elevationGainM: Float,       // Meters climbed
    val elevationLossM: Float,       // Meters descended
    val calories: Int,               // kcal burned
    val durationMs: Long             // Milliseconds since start
)