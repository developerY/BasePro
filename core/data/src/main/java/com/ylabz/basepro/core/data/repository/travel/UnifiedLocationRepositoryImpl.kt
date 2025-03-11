package com.ylabz.basepro.core.data.repository.travel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnifiedLocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UnifiedLocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Create a scope for sharing our location updates.
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Create a raw location flow using callbackFlow
    @SuppressLint("MissingPermission")
    private val _rawLocationFlow: SharedFlow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L // 1-second interval
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // Emit all received locations
                locationResult.locations.forEach { location ->
                    trySend(location).isSuccess
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }.shareIn(
        scope = repositoryScope,
        started = SharingStarted.Companion.WhileSubscribed(stopTimeoutMillis = 5000),
        replay = 1
    )

    override val locationFlow: Flow<Location>
        get() = _rawLocationFlow

    // Derived flow: convert location.speed (m/s) to km/h.
    override val speedFlow: Flow<Float> = _rawLocationFlow.map { location ->
        location.speed * 3.6f
    }

    // Derived flow: extract elevation (altitude) in meters.
    override val elevationFlow: Flow<Float> = _rawLocationFlow.map { location ->
        location.altitude.toFloat()
    }
}