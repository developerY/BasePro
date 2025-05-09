package com.ylabz.basepro.applications.bike.features.main.usecase

// in RideSession.kt or MappingExtensions.kt
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import android.location.Location
import androidx.core.util.TimeUtils.formatDuration
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo

/**
 * Convert a RideSession into your Room summary entity.
 */
fun RideSession.toBikeRideEntity(): BikeRideEntity =
    BikeRideEntity(
        startTime       = startTimeMs,
        endTime         = startTimeMs + elapsedMs,
        totalDistance   = totalDistanceKm,
        averageSpeed    = averageSpeedKmh.toFloat(),
        maxSpeed        = maxSpeedKmh,
        elevationGain   = elevationGainM,
        elevationLoss   = elevationLossM,
        caloriesBurned  = caloriesBurned,
        // use first/last GPS fix for quick queries (or 0.0 if empty)
        startLat        = path.firstOrNull()?.latitude  ?: 0.0,
        startLng        = path.firstOrNull()?.longitude ?: 0.0,
        endLat          = path.lastOrNull()?.latitude   ?: 0.0,
        endLng          = path.lastOrNull()?.longitude  ?: 0.0
    )

fun RideSession.toBikeRideInfo(
    weather: BikeWeatherInfo?,
    totalDistance: Float?
): BikeRideInfo = BikeRideInfo(
    location            = LatLng(path.last().latitude, path.last().longitude),
    currentSpeed        = maxSpeedKmh.toDouble(),
    averageSpeed        = averageSpeedKmh,
    maxSpeed            = maxSpeedKmh.toDouble(),
    currentTripDistance = totalDistance ?: totalDistanceKm,
    totalTripDistance   = totalDistance,
    remainingDistance   = null,
    elevationGain       = elevationGainM.toDouble(),
    elevationLoss       = elevationLossM.toDouble(),
    caloriesBurned      = caloriesBurned,
    rideDuration        = formatDuration(elapsedMs),
    settings            = emptyMap(),
    heading             = heading,
    elevation           = path.last().altitude.toDouble(),
    isBikeConnected     = false,
    batteryLevel        = null,
    motorPower          = null,
    rideState           = if (totalDistance != null) RideState.Ended else RideState.Riding,
    bikeWeatherInfo     = weather
)


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
        rideId    = rideId,
        timestamp = time,
        lat       = latitude,
        lng       = longitude,
        elevation = altitude.toFloat()
    )
