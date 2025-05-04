package com.ylabz.basepro.core.data.repository.travel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class UnifiedLocationLowPowerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UnifiedLocationRepository {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Tie our sharing scope to the app process lifecycle so updates stop
    // when the app goes to background and resume when it returns.
    private val repositoryScope: CoroutineScope =
        ProcessLifecycleOwner.get().lifecycleScope

    @SuppressLint("MissingPermission")
    private val _rawLocationFlow: SharedFlow<Location> = callbackFlow {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            5_000L
        )
            // deliver batches up to 15s at a time to let the radio sleep
            .setMaxUpdateDelayMillis(15_000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                // only send the most recent point
                result.lastLocation?.let { trySend(it).isSuccess }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                request, callback, Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            close(e)
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
        // drop anything with poor accuracy or too‐old timestamp
        .filter { it.hasAccuracy() && it.accuracy <= 50f }
        .filter { abs(System.currentTimeMillis() - it.time) < 10_000L }
        .shareIn(
            scope   = repositoryScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            replay  = 1
        )

    override val locationFlow: Flow<Location>
        get() = _rawLocationFlow

    override val speedFlow: SharedFlow<Float> =
        _rawLocationFlow
            .map { (it.speed * 3.6f).coerceAtLeast(0f) } // m/s → km/h
            .distinctUntilChanged()
            .shareIn(
                scope   = repositoryScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                replay  = 1
            )

    override val elevationFlow: SharedFlow<Float> =
        _rawLocationFlow
            .map { it.altitude.toFloat() }
            .distinctUntilChanged()
            .shareIn(
                scope   = repositoryScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                replay  = 1
            )
}
