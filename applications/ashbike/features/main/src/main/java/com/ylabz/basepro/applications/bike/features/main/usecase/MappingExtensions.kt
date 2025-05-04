package com.ylabz.basepro.applications.bike.features.main.usecase

// in RideSession.kt or MappingExtensions.kt
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import android.location.Location

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
