package com.ylabz.basepro.core.data.repository.health

import kotlinx.coroutines.flow.Flow

interface CalorieRepository {
    /**
     * Returns the calories burned for a ride.
     *
     * @param durationMillis Duration of the ride in milliseconds.
     * @param weightKg Rider's weight in kilograms.
     * @param speedKmh Average speed during the ride (km/h).
     */
    suspend fun getCaloriesBurned(durationMillis: Long, weightKg: Float, speedKmh: Float): Float
}
