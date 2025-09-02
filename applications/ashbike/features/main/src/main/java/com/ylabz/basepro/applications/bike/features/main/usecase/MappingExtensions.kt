package com.ylabz.basepro.applications.bike.features.main.usecase

// in RideSession.kt or MappingExtensions.kt
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import kotlinx.collections.immutable.persistentMapOf

/**
 * Convert a RideSession into your Room summary entity.
 */
fun RideSession.toBikeRideEntity(): BikeRideEntity =
    BikeRideEntity(
        startTime = startTimeMs,
        endTime = startTimeMs + elapsedMs,
        // ✔ multiply your km value by 1 000 to get meters
        totalDistance = (totalDistanceKm * 1_000f),
        averageSpeed = averageSpeedKmh.toFloat(),
        maxSpeed = maxSpeedKmh,
        elevationGain = elevationGainM,
        elevationLoss = elevationLossM,
        caloriesBurned = caloriesBurned,
        // use first/last GPS fix for quick queries (or 0.0 if empty)
        startLat = path.firstOrNull()?.latitude ?: 0.0,
        startLng = path.firstOrNull()?.longitude ?: 0.0,
        endLat = path.lastOrNull()?.latitude ?: 0.0,
        endLng = path.lastOrNull()?.longitude ?: 0.0
    )

// MappingExtensions.kt

fun RideSession.toBikeRideInfo(
    weather: BikeWeatherInfo?,
    totalDistance: Float?
): BikeRideInfo {
    // pick the “current” location if we have one
    val lastLoc = path.lastOrNull()
    val latLng = lastLoc?.let { LatLng(it.latitude, it.longitude) }

    return BikeRideInfo(
        location = latLng,
        currentSpeed = if (path.isNotEmpty()) maxSpeedKmh.toDouble() else 0.0,
        averageSpeed = averageSpeedKmh,
        maxSpeed = maxSpeedKmh.toDouble(),
        currentTripDistance = totalDistance ?: totalDistanceKm,
        totalTripDistance = totalDistance,
        remainingDistance = null,
        elevationGain = elevationGainM.toDouble(),
        elevationLoss = elevationLossM.toDouble(),
        caloriesBurned = caloriesBurned,
        rideDuration = formatDuration(elapsedMs),
        settings = persistentMapOf(),
        heading = lastLoc?.bearingTo(lastLoc) ?: heading, // or just heading
        elevation = lastLoc?.altitude ?: 0.0,
        isBikeConnected = false,
        batteryLevel = null,
        motorPower = null,
        rideState = if (totalDistance != null) RideState.Ended else RideState.Riding,
        bikeWeatherInfo = weather,
        heartbeat = null
    )
}


private fun formatDuration(ms: Long): String {
    val totalMin = (ms / 1000 / 60).toInt()
    val h = totalMin / 60
    val m = totalMin % 60
    return if (h > 0) "$h h $m m" else "$m m"
}


/**
 * Convert a raw Location to its child‐table entity.
 */
fun Location.toRideLocationEntity(rideId: String): RideLocationEntity =
    RideLocationEntity(
        rideId = rideId,
        timestamp = time,
        lat = latitude,
        lng = longitude,
        elevation = altitude.toFloat()
    )
