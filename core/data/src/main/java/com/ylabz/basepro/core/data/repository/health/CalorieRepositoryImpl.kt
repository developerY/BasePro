package com.ylabz.basepro.core.data.repository.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalorieRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CalorieRepository {

    override suspend fun getCaloriesBurned(durationMillis: Long, weightKg: Float, speedKmh: Float): Float {
        return if (HealthConnectClient.isAvailable(context)) {
            // If Health Connect is available, use it to fetch calories.
            // Pseudo-code: Replace with actual Health Connect API calls.
            val healthClient = HealthConnectClient.getOrCreate(context)
            // For example, assume there's a suspend function that returns calories burned.
            healthClient.getCaloriesBurnedForDuration(durationMillis)
        } else {
            // Fallback: Use a simple MET-based calculation.
            // Convert duration from milliseconds to hours.
            val hours = durationMillis / 3600000f
            // Choose a MET value based on speed (these values are approximate).
            val MET = when {
                speedKmh < 16f -> 4f
                speedKmh < 20f -> 8f
                speedKmh < 23f -> 10f
                else -> 12f
            }
            // Calories burned = MET * weight (kg) * duration (hr)
            MET * weightKg * hours
        }
    }
}
