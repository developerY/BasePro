package com.ylabz.basepro.core.data.repository.travel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
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

    // Total route distance in kilometers.
    private val totalRouteDistanceKm = 50f

    // Use a dedicated coroutine scope for location updates.
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @SuppressLint("MissingPermission")
    private val _rawLocationFlow: SharedFlow<Location> = callbackFlow {
        // Build the location request with high accuracy and a 1-second update interval.
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L // 1-second interval
        ).build()

        // Create a callback to emit each location update.
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    trySend(location).isSuccess
                }
            }
        }

        // Request location updates on the main looper to ensure thread safety.
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        // Remove location updates when the flow is closed.
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }.shareIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    // Expose the raw location flow.
    override val locationFlow: Flow<Location>
        get() = _rawLocationFlow

    // Calculate speed by converting m/s to km/h.
    override val speedFlow: Flow<Float> = _rawLocationFlow.map { location ->
        (location.speed * 3.6f).coerceAtLeast(0f)
    }

    // Expose elevation (altitude) from the location updates.
    override val elevationFlow: Flow<Float> = _rawLocationFlow.map { location ->
        location.altitude.toFloat()
    }

    // Internal flow that accumulates the distance traveled.
    // It maintains a Pair of the previous location and the running total (in km).
    private val distanceAccumulatorFlow: Flow<Pair<Location?, Float>> = _rawLocationFlow
        .scan(initial = Pair<Location?, Float>(null, 0f)) { (prev, total), curr ->
            // Calculate the additional distance in km.
            val additionalDistance = prev?.distanceTo(curr)?.div(1000f) ?: 0f
            // Emit the new state with the current location and updated total.
            Pair(curr, total + additionalDistance)
        }
        // Optionally, filter out duplicate values.
        .distinctUntilChanged()

    // Expose the total distance traveled as a Flow (in kilometers).
    override val traveledDistanceFlow: Flow<Float> = distanceAccumulatorFlow
        .map { (_, traveled) -> traveled }

    // Calculate the remaining distance based on the total route.
    override val remainingDistanceFlow: Flow<Float> = traveledDistanceFlow
        .map { traveled ->
            (totalRouteDistanceKm - traveled).coerceAtLeast(0f)
        }
}
