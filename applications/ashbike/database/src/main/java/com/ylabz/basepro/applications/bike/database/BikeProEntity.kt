package com.ylabz.basepro.applications.bike.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ylabz.basepro.applications.bike.database.converter.Converters

@Entity(tableName = "basepro_table")
@TypeConverters(Converters::class)
data class BaseProEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    // Core Ride Information
    val startTime: Long, // Epoch time in milliseconds
    val endTime: Long,
    val totalDistance: Float, // in meters
    val averageSpeed: Float, // km/h
    val maxSpeed: Float, // km/h

    // Elevation and Fitness Data
    val elevationGain: Float, // in meters
    val elevationLoss: Float, // in meters
    val caloriesBurned: Int, // if available from local sensors or Health Connect

    // Optional Health Data (from Health Connect)
    val avgHeartRate: Int? = null,  // Nullable: provided if Health Connect is enabled
    val maxHeartRate: Int? = null,  // Nullable: provided if Health Connect is enabled
    // Health Connect metadata
    val healthConnectRecordId: String? = null,
    val isHealthDataSynced: Boolean = false,

    // Location and Route Data
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double,
    // Route data can be stored as JSON string or via a relationship with a separate table.
    val routeJson: String? = null,

    // Environmental and Contextual Data
    val weatherCondition: String? = null,
    val rideType: String? = null,

    // User Feedback and Sync Details
    val notes: String? = null,
    val rating: Int? = null,
    val isSynced: Boolean = false,

    // Optional Bike Data
    val bikeId: String? = null,
    val batteryStart: Int? = null,
    val batteryEnd: Int? = null
)

data class BikeProUpdate(
    val id: Int,
    //val alarmOn: Boolean
)

/*
data class Ride(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val startTime: Long,
    val endTime: Long,
    val totalDistance: Float,
    val averageSpeed: Float,
    val maxSpeed: Float,
    val elevationGain: Float,
    val elevationLoss: Float,
    val caloriesBurned: Int,

    // Health Connect Optional Fields
    val avgHeartRate: Int? = null,
    val maxHeartRate: Int? = null,
    val healthConnectRecordId: String? = null,
    val isHealthDataSynced: Boolean = false,

    // Other ride metadata
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double,
    val routeJson: String? = null,
    val weatherCondition: String? = null,
    val rideType: String? = null,
    val notes: String? = null,
    val rating: Int? = null,
    val isSynced: Boolean = false,

    // Bike related
    val bikeId: String? = null,
    val batteryStart: Int? = null,
    val batteryEnd: Int? = null
)
 */