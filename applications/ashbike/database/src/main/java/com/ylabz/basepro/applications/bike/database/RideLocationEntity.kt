package com.ylabz.basepro.applications.bike.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

// 1) Ride‐point entity
@Entity(
    tableName = "ride_locations",
    foreignKeys = [
        ForeignKey(
            entity        = BikeRideEntity::class,
            parentColumns = ["rideId"],
            childColumns  = ["rideId"],
            onDelete      = ForeignKey.CASCADE
        )
    ],
    indices = [ Index("rideId") ]
)
data class RideLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rideId: String,      // ← FK → BikeRideEntity.rideId
    val timestamp: Long,     // epoch millis
    val lat: Double,
    val lng: Double,
    val elevation: Float? = null
)
/*@Entity(
    tableName = "ride_locations",
    foreignKeys = [
        ForeignKey(
            entity = BikeRideEntity::class,
            parentColumns = ["rideId"],
            childColumns = ["rideOwnerId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("rideOwnerId"), Index("timestamp") ]
)
data class RideLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val rideOwnerId: String,       // FK → BikeRideEntity.rideId
    val timestamp: Long,           // epoch millis
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double? = null,
    val speedMetersPerSec: Double? = null,
    val headingDegrees: Float? = null
)*/