package com.ylabz.basepro.core.data.repository.travel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnifiedLocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UnifiedLocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Total route distance in km; adjust as needed.
    private val totalRouteDistanceKm = 50f

    // Create a repository scope for shared flows.
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Create the raw location flow via callbackFlow and share it.
    @SuppressLint("MissingPermission")
    private val _rawLocationFlow: SharedFlow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L // 1-second interval
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    trySend(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }.shareIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        replay = 1
    )

    override val locationFlow: Flow<Location>
        get() = _rawLocationFlow

    // Derived flow for speed (convert m/s to km/h).
    override val speedFlow: Flow<Float> = _rawLocationFlow.map { location ->
        location.speed * 3.6f
    }

    // Derived flow for elevation (in meters).
    override val elevationFlow: Flow<Float> = _rawLocationFlow.map { location ->
        location.altitude.toFloat()
    }

    // Derived flow for remaining distance.
    // We use scan to accumulate traveled distance from consecutive location updates.
    override val remainingDistanceFlow: Flow<Float> = _rawLocationFlow
        .scan(Pair<Location?, Float>(null, 0f)) { (prevLocation, totalTraveled), location ->
            val additionalDistance = prevLocation?.distanceTo(location)?.div(1000f) ?: 0f
            Pair(location, totalTraveled + additionalDistance)
        }
        .map { (_, traveledDistance) ->
            // Remaining distance is total minus traveled, not going below 0.
            (totalRouteDistanceKm - traveledDistance).coerceAtLeast(0f)
        }
}