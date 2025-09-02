package com.ylabz.basepro.applications.bike.features.main.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Calculates calories burned based on:
 *  - distance (km)
 *  - speed (km/h)
 *  - user weight (kg)
 * Uses a simple MET × weight × duration model.
 */

/**
 * Simple MET-based calories calculator.
 */
class CalculateCaloriesUseCase @Inject constructor() {
    operator fun invoke(
        distanceKmFlow: Flow<Float>,
        speedKmhFlow: Flow<Float>,
        userStatsFlow: Flow<UserStats>
    ): Flow<Float> = combine(
        distanceKmFlow,
        speedKmhFlow,
        userStatsFlow
    ) { distanceKm, speedKmh, userStats ->
        val durationH = if (speedKmh > 0f) distanceKm / speedKmh else 0f
        val met = when {
            speedKmh < 16f -> 4f
            speedKmh < 19f -> 6f
            speedKmh < 22f -> 8f
            speedKmh < 25f -> 10f
            else -> 12f
        }
        met * userStats.weightKg * durationH
    }
}
