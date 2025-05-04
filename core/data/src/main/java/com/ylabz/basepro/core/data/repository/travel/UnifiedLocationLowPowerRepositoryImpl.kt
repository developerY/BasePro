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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    // A dedicated scope that lives for the app process, but doesn't auto-restart on lifecycle changes
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @SuppressLint("MissingPermission")
    private val _rawLocationFlow: SharedFlow<Location> = callbackFlow {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            5_000L                       // update every 5s
        )
            // let the radio sleep up to 15s
            .setMaxUpdateDelayMillis(15_000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it).isSuccess }
            }
        }

        // start requesting
        fusedLocationClient.requestLocationUpdates(
            request, callback, Looper.getMainLooper()
        )

        // when nobody’s collecting, stop immediately
        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
        .filter { it.hasAccuracy() && it.accuracy <= 50f }
        .filter { abs(System.currentTimeMillis() - it.time) < 10_000L }
        .shareIn(
            scope   = repositoryScope,
            // STOP as soon as the last collector disappears
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 0),
            replay  = 1
        )

    override val locationFlow: Flow<Location> = _rawLocationFlow

    override val speedFlow: SharedFlow<Float> =
        _rawLocationFlow
            .map { (it.speed * 3.6f).coerceAtLeast(0f) }
            .distinctUntilChanged()
            .shareIn(
                scope   = repositoryScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 0),
                replay  = 1
            )

    override val elevationFlow: SharedFlow<Float> =
        _rawLocationFlow
            .map { it.altitude.toFloat() }
            .distinctUntilChanged()
            .shareIn(
                scope   = repositoryScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 0),
                replay  = 1
            )
}
