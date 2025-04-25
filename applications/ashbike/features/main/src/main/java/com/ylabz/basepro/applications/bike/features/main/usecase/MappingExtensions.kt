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
        // Room will auto-generate rideId for you, or you can pass one here
        startTime      = startTimeMs,
        endTime        = startTimeMs + durationMs,
        totalDistance  = totalDistanceM,
        averageSpeed   = averageSpeedKmh.toFloat(),
        maxSpeed       = maxSpeedKmh,
        elevationGain  = elevationGainM,
        elevationLoss  = elevationLossM,
        caloriesBurned = calories,
        // use first/last GPS fix for quick queries:
        startLat       = path.first().latitude,
        startLng       = path.first().longitude,
        endLat         = path.last().latitude,
        endLng         = path.last().longitude
    )

/**
 * Convert a raw Location to its child-table entity.
 */
fun Location.toRideLocationEntity(rideId: String): RideLocationEntity =
    RideLocationEntity(
        rideId    = rideId,
        timestamp = time,
        lat       = latitude,
        lng       = longitude,
        elevation = altitude.toFloat()
    )
