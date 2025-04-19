package com.ylabz.basepro.applications.bike.database

import androidx.room.Embedded
import androidx.room.Relation

data class RideWithLocations(
    @Embedded val ride: RideEntity,
    @Relation(
        parentColumn = "rideId",
        entityColumn = "rideOwnerId"
    )
    val locations: List<RideLocationEntity>
)
