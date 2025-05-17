package com.ylabz.basepro.applications.bike.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "bike_rides_table")
data class BikeRideEntity(
    @PrimaryKey val rideId: String = UUID.randomUUID().toString(),

    // Core BikeRide Information
    val startTime: Long,
    val endTime: Long,
    val totalDistance: Float,
    val averageSpeed: Float,
    val maxSpeed: Float,

    // Elevation and Fitness Data
    val elevationGain: Float,
    val elevationLoss: Float,
    val caloriesBurned: Int,

    // Optional Health Connect
    val avgHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val healthConnectRecordId: String? = null,
    val isHealthDataSynced: Boolean = false,

    // Environmental & Context
    val weatherCondition: String? = null,
    val rideType: String? = null,

    // User Feedback
    val notes: String? = null,
    val rating: Int? = null,
    //val isSynced: Boolean = false,

    // Bike & Battery
    val bikeId: String? = null,
    val batteryStart: Int? = null,
    val batteryEnd: Int? = null,

    // Start/End coords for quick queries (route itself lives in child table)
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double
)
