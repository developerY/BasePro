package com.ylabz.basepro.applications.bike.database.repository

// com.ylabz.basepro.applications.bike.database.repository.BikeRideRepoImpl.kt

import androidx.annotation.WorkerThread
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.BikeRideDao
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.mapper.toEntity
import com.ylabz.basepro.applications.bike.database.mapper.toBikeRide
import com.ylabz.basepro.core.model.bike.BikeRide
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

    /** New: update just the notes text */
    @WorkerThread
    override suspend fun updateRideNotes(rideId: String, notes: String) {
        bikeRideDao.updateNotes(rideId, notes)
    }

    /** Marks a ride as synced to Health Connect and stores the Health Connect ID. */
    @WorkerThread
    override suspend fun markRideAsSyncedToHealthConnect(rideId: String, healthConnectId: String?) {
        bikeRideDao.markRideAsSyncedToHealthConnect(rideId, healthConnectId)
    }

    /** Gets the count of rides that are not yet synced to Health Connect. */
    @WorkerThread // Added @WorkerThread for consistency, though Flow might not strictly need it here
    override fun getUnsyncedRidesCount(): Flow<Int> {
        return bikeRideDao.getUnsyncedRidesCount()
    }
}
