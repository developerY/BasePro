package com.ylabz.basepro.applications.bike.database.repository

import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import com.ylabz.basepro.applications.bike.database.mapper.BikeRide
import com.ylabz.basepro.applications.bike.database.mapper.toDomain
import com.ylabz.basepro.applications.bike.database.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


// Demo in-memory implementation of BikeRideRepo
@Singleton
@Named("demo")
class DemoBikeRideRepo @Inject constructor() : BikeRideRepo {
    // Backing state for rides (only the header); we ignore locations for demo
    private val _rides = MutableStateFlow<List<BikeRideEntity>>(emptyList())
    private val mutex = Mutex()

    override fun getAllRides(): Flow<List<BikeRide>> =
        _rides.map { list -> list.map { it.toDomain() } }

    override fun getAllRidesWithLocations(): Flow<List<RideWithLocations>> {
        TODO("Not yet implemented")
    }

    override fun getRideWithLocations(rideId: String): Flow<RideWithLocations?> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(ride: BikeRide) {
        val entity = ride.toEntity()
        mutex.withLock {
            _rides.value = _rides.value + entity
        }
    }

    // For consistency with your real repo interface
    override suspend fun insertRideWithLocations(
        ride: BikeRideEntity,
        locations: List<RideLocationEntity>
    ) {
        mutex.withLock {
            _rides.value = _rides.value + ride
        }
    }

    override suspend fun delete(ride: BikeRide) {
        mutex.withLock {
            _rides.value = _rides.value.filterNot { it.rideId == ride.rideId }
        }
    }

    override suspend fun deleteById(rideId: String) {
        mutex.withLock {
            _rides.value = _rides.value.filterNot { it.rideId == rideId }
        }
    }

    override suspend fun getRideById(rideId: String): BikeRide? =
        _rides.value.firstOrNull { it.rideId == rideId }?.toDomain()

    override suspend fun deleteAll() {
        mutex.withLock {
            _rides.value = emptyList()
        }
    }
}

