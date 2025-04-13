package com.ylabz.basepro.core.data.repository.travel

import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Demo implementation of UnifiedLocationRepository that emits fake location data.
 */
@Singleton
class DemoUnifiedLocationRepositoryImpl @Inject constructor() : UnifiedLocationRepository {

    override val locationFlow: Flow<Location> = flow {
        // Create a base demo location.
        val demoLocation = Location("demo").apply {
            latitude = 37.4219999
            longitude = -122.0862462
            accuracy = 10f
            time = System.currentTimeMillis()
            speed = 0f
        }
        while (true) {
            // Simulate small movement by updating latitude and longitude.
            demoLocation.latitude += 0.00001  // small change simulating movement
            demoLocation.longitude += 0.00001
            demoLocation.time = System.currentTimeMillis()
            emit(demoLocation)
            delay(500L)
        }
    }.flowOn(Dispatchers.Default)

    override val speedFlow: Flow<Float> = flow {
        var speed = 10f  // start at 10 km/h
        while (true) {
            // Increase speed gradually until reaching 25 km/h, then reset to 10 km/h.
            speed += 0.5f
            if (speed >= 25f) {
                speed = 10f
            }
            emit(speed)
            delay(500L)
        }
    }

    override val elevationFlow: Flow<Float> = flow {
        // For demo purposes, emit a constant elevation of 12 meters.
        val elevation = 12f
        while (true) {
            emit(elevation)
            delay(500L)
        }
    }

    override val traveledDistanceFlow: Flow<Float> = flow {
        // Simulate a ride: increase traveled distance at a realistic rate.
        // With an increment of about 0.003 km every 500ms,
        // in one second you'll cover roughly 0.006 km (6 meters) which is in line with ~21.6 km/h.
        var traveled = 0f
        while (true) {
            traveled += 0.003f
            emit(traveled)
            delay(500L)
        }
    }
}
