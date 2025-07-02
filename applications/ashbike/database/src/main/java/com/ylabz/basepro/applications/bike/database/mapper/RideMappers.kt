package com.ylabz.basepro.applications.bike.database.mapper

import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import com.ylabz.basepro.core.model.bike.BikeRide
import com.ylabz.basepro.core.model.bike.LocationPoint

/**
 * Converts a database [RideWithLocations] object (entity + locations) to a [BikeRide] domain model.
 */
fun RideWithLocations.toBikeRide(): BikeRide {
    // Reuse the existing mapper for the main entity and append the locations.
    return this.bikeRideEnt.toBikeRide().copy(
        locations = this.locations.map { it.toLocationPoint() }
    )
}

/**
 * Converts a database [BikeRideEntity] to a [BikeRide] domain model.
 */
fun BikeRideEntity.toBikeRide(): BikeRide {
    return BikeRide(
        rideId = this.rideId,
        startTime = this.startTime,
        endTime = this.endTime,
        totalDistance = this.totalDistance,
        averageSpeed = this.averageSpeed,
        maxSpeed = this.maxSpeed,
        elevationGain = this.elevationGain,
        elevationLoss = this.elevationLoss,
        caloriesBurned = this.caloriesBurned,
        avgHeartRate = this.avgHeartRate,
        maxHeartRate = this.maxHeartRate,
        isHealthDataSynced = this.isHealthDataSynced,
        healthConnectRecordId = this.healthConnectRecordId,
        weatherCondition = this.weatherCondition,
        rideType = this.rideType,
        notes = this.notes,
        rating = this.rating,
        bikeId = this.bikeId,
        batteryStart = this.batteryStart,
        batteryEnd = this.batteryEnd,
        startLat = this.startLat,
        startLng = this.startLng,
        endLat = this.endLat,
        endLng = this.endLng,
        locations = emptyList() // Locations are handled by the RideWithLocations mapper
    )
}

/**
 * Converts a [BikeRide] domain model to a database [BikeRideEntity].
 */
fun BikeRide.toEntity(): BikeRideEntity {
    return BikeRideEntity(
        rideId = this.rideId,
        startTime = this.startTime,
        endTime = this.endTime,
        totalDistance = this.totalDistance,
        averageSpeed = this.averageSpeed,
        maxSpeed = this.maxSpeed,
        elevationGain = this.elevationGain,
        elevationLoss = this.elevationLoss,
        caloriesBurned = this.caloriesBurned,
        avgHeartRate = this.avgHeartRate,
        maxHeartRate = this.maxHeartRate,
        isHealthDataSynced = this.isHealthDataSynced,
        healthConnectRecordId = this.healthConnectRecordId,
        weatherCondition = this.weatherCondition,
        rideType = this.rideType,
        notes = this.notes,
        rating = this.rating,
        bikeId = this.bikeId,
        batteryStart = this.batteryStart,
        batteryEnd = this.batteryEnd,
        startLat = this.startLat,
        startLng = this.startLng,
        endLat = this.endLat,
        endLng = this.endLng
    )
}

/**
 * Converts a database [RideLocationEntity] to a domain [LocationPoint].
 * Corrected to match the actual fields in RideLocationEntity.
 */
fun RideLocationEntity.toLocationPoint(): LocationPoint {
    return LocationPoint(
        latitude = this.lat,
        longitude = this.lng,
        altitude = this.elevation,
        timestamp = this.timestamp
    )
}
