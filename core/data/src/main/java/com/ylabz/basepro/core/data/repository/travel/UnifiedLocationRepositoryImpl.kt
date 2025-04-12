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

    private val totalRouteDistanceKm = 50f

    // Scope used for sharing the location stream
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Corrected: use main looper instead of null to avoid crash
    @SuppressLint("MissingPermission")
    private val _rawLocationFlow: SharedFlow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L // 1-second interval
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    trySend(location).isSuccess // Use isSuccess to avoid crash if closed
                }
            }
        }

        // ✅ FIX: Use main looper to avoid NullPointerException
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }.shareIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )

    override val locationFlow: Flow<Location>
        get() = _rawLocationFlow

    override val speedFlow: Flow<Float> = _rawLocationFlow.map { location ->
        (location.speed * 3.6f).coerceAtLeast(0f) // Convert from m/s to km/h
    }

    override val elevationFlow: Flow<Float> = _rawLocationFlow.map { location ->
        location.altitude.toFloat()
    }

    override val remainingDistanceFlow: Flow<Float> = _rawLocationFlow
        .scan(Pair<Location?, Float>(null, 0f)) { (prev, total), curr ->
            val additional = prev?.distanceTo(curr)?.div(1000f) ?: 0f
            Pair(curr, total + additional)
        }
        .map { (_, traveled) ->
            (totalRouteDistanceKm - traveled).coerceAtLeast(0f)
        }
}
