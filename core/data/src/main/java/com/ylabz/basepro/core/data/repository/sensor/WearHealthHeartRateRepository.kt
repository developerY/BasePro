package com.ylabz.basepro.core.data.repository.sensor

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DeltaDataType
import androidx.health.services.client.unregisterMeasureCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shared implementation for accessing Wear OS wrist sensors.
 * This lives in core:data so ANY Wear app (AshBike, DrunkWatch, etc) can use it.
 */
@Singleton
class WearHealthHeartRateRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : HeartRateRepository {

    override val heartRate: Flow<Int> = callbackFlow {
        Log.d("WearHealthRepo", "Initializing Heart Rate Flow")

        // Safety Check: If this code accidentally runs on a Phone, this might throw.
        // In a real app, you might wrap this in a try-catch or hardware check.
        val healthClient = HealthServices.getClient(context)
        val measureClient = healthClient.measureClient

        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(dataType: DeltaDataType<*, *>, availability: Availability) {
                // Handle sensor availability changes (e.g. ACQUIRING vs ACQUIRED)
            }

            override fun onDataReceived(data: DataPointContainer) {
                val heartRateSamples = data.getData(DataType.HEART_RATE_BPM)
                // Get the newest sample
                val latest = heartRateSamples.lastOrNull()
                if (latest != null) {
                    val bpm = latest.value.toInt()
                    // Send to the flow
                    trySend(bpm)
                }
            }
        }

        Log.d("WearHealthRepo", "Registering Listener...")

        // 1. Registration is a suspend function, so we simply call it.
        // We catch errors here in case the device doesn't support the sensor.
        try {
            measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)
            Log.d("WearHealthRepo", "Listener Registered")
        } catch (e: Exception) {
            Log.e("WearHealthRepo", "Registration failed", e)
            close(e) // Close flow if we can't register
        }

        // 2. awaitClose keeps the flow alive until the collector stops listening.
        awaitClose {
            Log.d("WearHealthRepo", "Unregistering Listener")

            // 3. CRITICAL FIX: 'awaitClose' is synchronous, but 'unregister' is suspend.
            // We use runBlocking here to bridge the gap.
            // This is safe because we force this whole flow to run on Dispatchers.IO below.
            runBlocking {
                try {
                    measureClient.unregisterMeasureCallback(DataType.HEART_RATE_BPM, callback)
                } catch (e: Exception) {
                    Log.e("WearHealthRepo", "Unregister failed", e)
                }
            }
        }
    }
        // 4. Force execution on IO thread so runBlocking doesn't freeze the main UI
        .flowOn(Dispatchers.IO)
}