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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
        Log.d("WearHealthRepo", "Initializing Heart Rate Flow via Health Services")

        // Safety Check: If this code accidentally runs on a Phone, this might throw.
        // In a real app, you might wrap this in a try-catch or hardware check.
        val healthClient = HealthServices.getClient(context)
        val measureClient = healthClient.measureClient

        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(dataType: DeltaDataType<*, *>, availability: Availability) {
                // Handle availability (e.g. sensor off wrist)
            }

            override fun onDataReceived(data: DataPointContainer) {
                val heartRateSamples = data.getData(DataType.HEART_RATE_BPM)
                val latest = heartRateSamples.lastOrNull()
                if (latest != null) {
                    val bpm = latest.value.toInt()
                    trySend(bpm)
                }
            }
        }

        Log.d("WearHealthRepo", "Registering Callback...")
        try {
            measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)
        } catch (e: Exception) {
            Log.e("WearHealthRepo", "Failed to register listener", e)
            close(e)
        }

        awaitClose {
            Log.d("WearHealthRepo", "Unregistering Callback")
            // 2. FIX: Use runBlocking to call the suspend unregister function inside the non-suspend awaitClose block.
            // Since we are on Dispatchers.IO (see below), this is safe and won't block the UI.
            runBlocking {
                try {
                    measureClient.unregisterMeasureCallback(DataType.HEART_RATE_BPM, callback)
                } catch (e: Exception) {
                    Log.e("WearHealthRepo", "Unregister failed", e)
                }
            }
        }
    }
}