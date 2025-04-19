package com.ylabz.basepro.applications.bike.database

// com.ylabz.basepro.applications.bike.database.repository.BikeRideRepo.kt

import com.ylabz.basepro.applications.bike.database.mapper.BikeRide
import kotlinx.coroutines.flow.Flow

interface BikeRideRepo {
    /** Stream all rides (no locations). */
    fun getAllRides(): Flow<List<BikeRide>>

    /** Insert a bikeRide summary; locations handled separately if needed. */
    suspend fun insert(bikeRide: BikeRide)

    /** Delete a bikeRide by domain object. */
    suspend fun delete(bikeRide: BikeRide)

    /** Delete a ride by its ID. */
    suspend fun deleteById(rideId: String)

    /** Load a single ride (ignores locations in this example). */
    suspend fun getRideById(rideId: String): BikeRide?

    /** Wipe entire rides table. */
    suspend fun deleteAll()
}



/** Insert the ride, then any location points.
suspend fun saveRide(
    ride: BikeRideEntity,
    locations: List<RideLocationEntity> = emptyList()
) {
    bikeRideDao.insertRide(ride)
    if (locations.isNotEmpty()) {
        bikeRideDao.insertLocations(locations)
    }
}
        */