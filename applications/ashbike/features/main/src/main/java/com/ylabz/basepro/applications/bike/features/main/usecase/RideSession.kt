package com.ylabz.basepro.applications.bike.features.main.usecase

import android.location.Location

/**
 * A snapshot of one ride session, updated in real time.
 */

data class RideSession(
    val path: List<Location>,        // All raw GPS fixes so far
    val totalDistanceM: Float,       // Meters traveled
    val averageSpeedKmh: Double,     // km/h
    val maxSpeedKmh: Float,          // km/h
    val elevationGainM: Float,       // Meters climbed
    val elevationLossM: Float,       // Meters descended
    val calories: Int,               // kcal burned
    val durationMs: Long             // Milliseconds since start
)