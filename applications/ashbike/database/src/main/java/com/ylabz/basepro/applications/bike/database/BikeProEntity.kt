package com.ylabz.basepro.applications.bike.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ylabz.basepro.applications.bike.database.converter.Converters

@Entity(tableName = "bikepro_table")
data class Ride(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Core Ride Information
    val startTime: Long,  // Epoch time in milliseconds
    val endTime: Long,

    // Distance and Speed Metrics
    val totalDistance: Float,   // in meters
    val averageSpeed: Float,    // km/h
    val maxSpeed: Float,        // km/h

    // Elevation Data
    val elevationGain: Float,   // in meters
    val elevationLoss: Float,   // in meters

    // Fitness-related Data
    val caloriesBurned: Int,
    val avgHeartRate: Int?,     // Optional if sensor available
    val maxHeartRate: Int?,     // Optional if sensor available

    // Location and Route Data
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double,
    // Optionally store the route as a JSON string or manage in a separate table
    val routeJson: String? = null,

    // Environmental and Contextual Data
    val weatherCondition: String?,  // e.g., "Sunny, 25°C"
    val rideType: String?,          // e.g., "Commute", "Training"

    // User Feedback
    val notes: String?,
    val rating: Int?,               // e.g., 1 to 5

    // Technical/Sync Metadata
    val isSynced: Boolean = false,
    val bikeId: String? = null,     // If multiple bikes are supported
    val batteryStart: Int?,         // Percentage at start, if relevant
    val batteryEnd: Int?            // Percentage at end, if relevant
)
data class BikeProUpdate(
    val id: Int,
    //val alarmOn: Boolean
)