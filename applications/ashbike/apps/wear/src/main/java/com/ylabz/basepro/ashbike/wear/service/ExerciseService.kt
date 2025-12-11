package com.ylabz.basepro.ashbike.wear.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import androidx.health.services.client.endExercise
import androidx.health.services.client.startExercise
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExerciseService : LifecycleService() {

    private val binder = LocalBinder()

    // Live Data for the UI
    private val _exerciseMetrics = MutableStateFlow(ExerciseMetrics())
    val exerciseMetrics = _exerciseMetrics.asStateFlow()

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): ExerciseService = this@ExerciseService
    }

    fun startExercise() {
        val healthClient = HealthServices.getClient(this)
        val exerciseClient = healthClient.exerciseClient

        val config = ExerciseConfig.builder(ExerciseType.BIKING)
            .setDataTypes(setOf(
                DataType.HEART_RATE_BPM,
                DataType.CALORIES_TOTAL,
                DataType.DISTANCE_TOTAL, // Watch handles GPS distance for us!
                DataType.SPEED
            ))
            .setIsAutoPauseAndResumeEnabled(false)
            .build()

        lifecycleScope.launch {
            try {
                exerciseClient.startExercise(config)
                Log.d("AshBikeWear", "Exercise started")

                // Start listening to the stream
                exerciseClient.setUpdateCallback(object : androidx.health.services.client.ExerciseUpdateCallback {
                    override fun onAvailabilityChanged(
                        dataType: DataType<*, *>,
                        availability: Availability
                    ) {
                        // TODO("Not yet implemented")
                    }

                    override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                        processUpdate(update)
                    }

                    override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {}
                    override fun onRegistered() {}
                    override fun onRegistrationFailed(throwable: Throwable) {}
                    //override fun onAvailabilityChanged(dataType: DataType<*, *>, availability: LocationAvailability) {}
                })
            } catch (e: Exception) {
                Log.e("AshBikeWear", "Failed to start exercise", e)
            }
        }
    }

    fun stopExercise() {
        lifecycleScope.launch {
            try {
                HealthServices.getClient(this@ExerciseService)
                    .exerciseClient
                    .endExercise()
                _exerciseMetrics.value = ExerciseMetrics() // Reset
            } catch (e: Exception) {
                Log.e("AshBikeWear", "Failed to stop exercise", e)
            }
        }
    }

    private fun processUpdate(update: ExerciseUpdate) {
        val latestMetrics = update.latestMetrics
        val hr = latestMetrics.getData(DataType.HEART_RATE_BPM).lastOrNull()?.value ?: 0.0
        val dist = latestMetrics.getData(DataType.DISTANCE_TOTAL)?.total ?: 0.0
        val cals = latestMetrics.getData(DataType.CALORIES_TOTAL)?.total ?: 0.0
        val speed = latestMetrics.getData(DataType.SPEED).lastOrNull()?.value ?: 0.0

        _exerciseMetrics.value = _exerciseMetrics.value.copy(
            heartRate = hr,
            distance = dist,
            calories = cals,
            speed = speed
        )
    }
}

// Simple data class to hold UI state
data class ExerciseMetrics(
    val heartRate: Double = 0.0,
    val distance: Double = 0.0,
    val calories: Double = 0.0,
    val speed: Double = 0.0
)