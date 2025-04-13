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
            // Update timestamp and emit a demo location.
            demoLocation.time = System.currentTimeMillis()
            emit(demoLocation)
            delay(500L)
        }
    }.flowOn(Dispatchers.Default)

    override val speedFlow: Flow<Float> = flow {
        var speed = 0f
        while (true) {
            // Increase speed by 4 km/h until reaching 60 km/h, then reset to 0.
            speed = (speed + 4f)
            if (speed >= 60f) {
                speed = 0f
            }
            emit(speed)
            delay(500L)
        }
    }

    override val elevationFlow: Flow<Float> = flow {
        // For demo purposes, emit a constant elevation.
        val elevation = 12f
        while (true) {
            emit(elevation)
            delay(500L)
        }
    }

    override val traveledDistanceFlow: Flow<Float> = flow {
        // Simulate a ride with totalDistance of 50 km.
        var traveled = 0f
        while (true) {
            traveled += 0.4f
            // Emit the remaining distance.
            emit(traveled)
            delay(500L)
        }
    }
}
