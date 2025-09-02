package com.ylabz.basepro.core.data.repository.travel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnifiedLocationHighPowerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UnifiedLocationRepository {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    // scope for sharing hot streams
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @SuppressLint("MissingPermission")
    private val _rawLocationFlow: SharedFlow<Location> = callbackFlow<Location> {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1_000L
        ).build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { loc ->
                    trySend(loc).isSuccess
                }
            }
        }

        // start updates
        try {
            fusedLocationClient.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            close(e) // if permissions are missing, close the flow with error
        }

        // cleanup
        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
        .shareIn(
            scope = repositoryScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            replay = 1
        )

    override val locationFlow: Flow<Location>
        get() = _rawLocationFlow

    override val speedFlow: Flow<Float> =
        _rawLocationFlow
            .map { (it.speed * 3.6f).coerceAtLeast(0f) }
            .distinctUntilChanged()
            .shareIn(
                scope = repositoryScope,
                started = SharingStarted.Lazily,
                replay = 1
            )

    override val elevationFlow: Flow<Float> =
        _rawLocationFlow
            .map { it.altitude.toFloat() }
            .distinctUntilChanged()
            .shareIn(
                scope = repositoryScope,
                started = SharingStarted.Lazily,
                replay = 1
            )
}

