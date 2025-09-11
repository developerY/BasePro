package com.ylabz.basepro.core.model.location

/**
 * Represents a single GPS fix with essential data.
 *
 * @property lat Latitude in degrees.
 * @property lng Longitude in degrees.
 * @property timeMs Timestamp of the fix in milliseconds since epoch.
 * @property altitude Optional altitude in meters.
 * @property speed Optional speed in meters/second.
 * @property accuracy Optional accuracy of the fix in meters.
 */
data class GpsFix(
    val lat: Double,
    val lng: Double,
    val timeMs: Long,
    val altitude: Double? = null,
    val speed: Float? = null, // meters/second
    val accuracy: Float? = null
)
//Old
/*
data class GpsFix(
    val lat: Double,
    val lng: Double,
    val elevation: Float,
    val timeMs: Long,
    val speedMps: Float
)
*/