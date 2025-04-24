package com.ylabz.basepro.applications.bike.database.repository

// com.ylabz.basepro.applications.bike.database.repository.BikeRideRepoImpl.kt

import androidx.annotation.WorkerThread
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.BikeRideDao
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.mapper.BikeRide
import com.ylabz.basepro.applications.bike.database.mapper.toEntity
import com.ylabz.basepro.applications.bike.database.mapper.toBikeRide
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class BikeRideRepoImpl @Inject constructor(
    private val bikeRideDao: BikeRideDao
) : BikeRideRepo {

    override fun getAllRidesWithLocations() = bikeRideDao.getAllRidesWithLocations()
    override fun getRideWithLocations(id: String) = bikeRideDao.getRideWithLocations(id)

    /** Convert the Room Flow<List<RideEntity>> into Flow<List<Ride>> */
    @WorkerThread
    override fun getAllRides(): Flow<List<BikeRide>> =
        bikeRideDao
            .getAllRidesWithLocations()     // this returns Flow<List<RideWithLocations>>
            .map { list ->
                list.map { it.bikeRideEnt.toBikeRide() } // drop locations for now
            }

    @WorkerThread
    override suspend fun insert(ride: BikeRide) {
        bikeRideDao.insertRide(ride.toEntity())
    }

    @WorkerThread
    override suspend fun delete(ride: BikeRide) {
        bikeRideDao.deleteRide(ride.rideId)
    }

    @WorkerThread
    override suspend fun deleteById(rideId: String) {
        bikeRideDao.deleteRide(rideId)
    }

    @WorkerThread
    override suspend fun getRideById(rideId: String): BikeRide? =
        bikeRideDao
            .getRideWithLocations(rideId)
            .firstOrNull()
            ?.bikeRideEnt
            ?.toBikeRide()

    @WorkerThread
    override suspend fun deleteAll() {
        // you’ll need to add this to BikeRideDao:
        // @Query("DELETE FROM rides") suspend fun deleteAllRides()
        bikeRideDao.deleteAllBikeRides()
    }

    @WorkerThread
    override suspend fun insertRideWithLocations(
        ride: BikeRideEntity,
        locations: List<RideLocationEntity>
    ) {
        bikeRideDao.insertRide(ride)
        bikeRideDao.insertLocations(locations)
    }
}
