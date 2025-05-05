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

    // A dedicated background scope—instead of ProcessLifecycleOwner—
    // so we fully control when the flow lives and dies.
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @SuppressLint("MissingPermission")
    private val _rawLocationFlow: SharedFlow<Location> = callbackFlow<Location> {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            /* desired interval = */ 5_000L
        )
            // batch up to 15 s so the radio can sleep
            .setMaxUpdateDelayMillis(15_000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                // push only the most recent fix
                result.lastLocation?.let { trySend(it).isSuccess }
            }
        }

        // start listening
        fusedLocationClient.requestLocationUpdates(
            request, callback, Looper.getMainLooper()
        )

        // cleanup when no one is subscribed for > stopTimeoutMillis
        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
        // no aggressive filtering—leave that to your UI if you need it
        .shareIn(
            scope   = repositoryScope,
            // keep GPS alive for 5 s after the last unsubscribe
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
                // lazy start: begin only when someone collects, then stay alive
                started = SharingStarted.Lazily,
                replay  = 1
            )

    override val elevationFlow: SharedFlow<Float> =
        _rawLocationFlow
            .map { it.altitude.toFloat() }
            .distinctUntilChanged()
            .shareIn(
                scope   = repositoryScope,
                started = SharingStarted.Lazily,
                replay  = 1
            )
}
