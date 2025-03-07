package com.ylabz.basepro.core.data.repository.speed

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeedRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SpeedRepository {

    // Provide a Flow that emits speed in km/h
    @SuppressLint("MissingPermission")
    override val speedFlow: Flow<Float> = callbackFlow {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L // 1-second interval
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // location.speed is in m/s
                    val speedMps = location.speed
                    val speedKmh = speedMps * 3.6f
                    trySend(speedKmh)
                }
            }
        }

        // Start location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        // Cleanup when the flow collector is cancelled
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
        .onStart {
            // (Optional) emit some initial value or handle logging
        }
        .onCompletion {
            // (Optional) handle completion or cleanup
        }
}
