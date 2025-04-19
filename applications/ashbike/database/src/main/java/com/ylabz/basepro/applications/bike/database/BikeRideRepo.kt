package com.ylabz.basepro.applications.bike.database

import javax.inject.Inject

class RideRepository @Inject constructor(
    private val rideDao: RideDao
) {
    /** Insert the ride, then any location points. */
    suspend fun saveRide(
        ride: RideEntity,
        locations: List<RideLocationEntity> = emptyList()
    ) {
        rideDao.insertRide(ride)
        if (locations.isNotEmpty()) {
            rideDao.insertLocations(locations)
        }
    }
}
