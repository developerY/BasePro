package com.ylabz.basepro.core.data.repository.travel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class DistanceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DistanceRepository {

    // Suppose we have a route of 50 km
    private val totalRouteDistanceKm = 50f

    // We'll store the last location to measure incremental distance
    private var lastLocation: Location? = null

    @SuppressLint("MissingPermission")
    override val remainingDistanceFlow: Flow<Float> = callbackFlow {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // We'll request location updates every second at high accuracy.
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).build()

        var traveledDistance = 0f // total traveled distance in km

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (loc in locationResult.locations) {
                    // If we have a previous location, accumulate distance
                    lastLocation?.let { previous ->
                        val distanceBetween = previous.distanceTo(loc) // in meters
                        val distanceKm = distanceBetween / 1000f
                        traveledDistance += distanceKm
                    }
                    lastLocation = loc

                    // Compute remaining distance
                    val remaining = max(0f, totalRouteDistanceKm - traveledDistance)
                    trySend(remaining)
                }
            }
        }

        // Start location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        // Cleanup
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
