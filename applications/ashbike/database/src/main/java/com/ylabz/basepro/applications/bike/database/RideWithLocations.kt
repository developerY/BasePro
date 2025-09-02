package com.ylabz.basepro.applications.bike.database

import androidx.room.Embedded
import androidx.room.Relation

data class RideWithLocations(
    @Embedded val bikeRideEnt: BikeRideEntity,
    @Relation(
        parentColumn = "rideId",
        entityColumn = "rideId",//"rideOwnerId"
        entity = RideLocationEntity::class
    )
    val locations: List<RideLocationEntity>
)
