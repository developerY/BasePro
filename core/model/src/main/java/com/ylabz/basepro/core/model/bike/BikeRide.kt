package com.ylabz.basepro.core.model.bike

/**
 * Represents a completed bike ride. This is the primary domain model for historical trip data,
 * used for business logic, syncing, and interacting with repositories.
 */
data class BikeRide(
    val rideId: String,
    val startTime: Long,
    val endTime: Long,
    val totalDistance: Float,
    val averageSpeed: Float,
    val maxSpeed: Float,
    val elevationGain: Float,
    val elevationLoss: Float,
    val caloriesBurned: Int,
    val avgHeartRate: Int?,
    val maxHeartRate: Int?,
    val isHealthDataSynced: Boolean,
    val healthConnectRecordId: String?,
    val weatherCondition: String?,
    val rideType: String?,
    val notes: String?,
    val rating: Int?,
    val bikeId: String?,
    val batteryStart: Int?,
    val batteryEnd: Int?,
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double,
    val locations: List<LocationPoint> // List of all GPS points for the ride
)

/**
 * Represents a single point in a ride's location history.
 */
data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val altitude: Float?,
    val timestamp: Long
)
