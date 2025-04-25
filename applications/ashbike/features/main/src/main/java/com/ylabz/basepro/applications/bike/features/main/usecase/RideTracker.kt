package com.ylabz.basepro.applications.bike.features.main.usecase

import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton



/**
 * RideTracker: single responsibility for gathering and summarizing everything
 * about a ride.  You call `start()`, it resets, then you read from [sessionFlow].
 * When the user hits “stop”, call [stopAndGetSession] to get the final values.
 */
@Singleton
class RideTracker @Inject constructor(
    // Inject your raw sensor repos:
    private val locationFlow: Flow<Location>,
    private val speedFlow:    Flow<Float>
) {
    companion object {
        // Simple calorie constant; pull into config if you like
        private const val CALORIES_PER_KM = 50
    }

    // -----------------------------------
    // INTERNAL ACCUMULATOR TYPE & STATE
    // -----------------------------------

    // Emit once on every start() to reset the scan operator
    private val resetSignal = MutableSharedFlow<Unit>(replay = 1)

    // Record the wall‐clock time when start() is called
    private var startTimeMs: Long = 0L

    // An internal data class to hold intermediate accumulations
    private data class Acc(
        val startTimeMs: Long,
        val lastLocation: Location?     = null,
        val path: List<Location>        = emptyList(),
        val totalDistanceM: Float       = 0f,
        val maxSpeedKmh: Float          = 0f,
        val elevationGainM: Float       = 0f,
        val elevationLossM: Float       = 0f
    )

    // -----------------------------------
    // PUBLIC SESSION FLOW
    // -----------------------------------

    /**
     * A hot StateFlow you can collect in your ViewModel.  Anytime you call [start()],
     * it resets to zero and begins accumulating.  If you never call start() it will
     * just stay at the "empty" session.
     */
    val sessionFlow: StateFlow<RideSession> = resetSignal
        .flatMapLatest {
            // capture the startTime for this ride
            val sessionStart = startTimeMs

            // combine raw location+speed into one flow of pairs
            combine(locationFlow, speedFlow) { loc, spd ->
                loc to spd
            }
                // scan over each fix to update our Acc
                .scan(Acc(startTimeMs = sessionStart)) { acc, (loc, spd) ->
                    // 1) Distance since last fix
                    val d = acc.lastLocation?.distanceTo(loc) ?: 0f
                    // 2) Updated total distance
                    val totalDist = acc.totalDistanceM + d
                    // 3) Updated max speed
                    val maxSpd = maxOf(acc.maxSpeedKmh, spd)
                    // 4) Elevation delta
                    val elevDelta = ((loc.altitude - (acc.lastLocation?.altitude ?: loc.altitude))
                        .toFloat())
                    val gain = acc.elevationGainM + maxOf(0f, elevDelta)
                    val loss = acc.elevationLossM + maxOf(0f, -elevDelta)

                    Acc(
                        startTimeMs    = acc.startTimeMs,
                        lastLocation   = loc,
                        path           = acc.path + loc,
                        totalDistanceM = totalDist,
                        maxSpeedKmh    = maxSpd,
                        elevationGainM = gain,
                        elevationLossM = loss
                    )
                }
                // on each Acc, map to a public RideSession
                .map { acc ->
                    // how long has it been?
                    val duration = System.currentTimeMillis() - acc.startTimeMs
                    // average speed: (km/h) = (totalDist_m/1000) / (hrs)
                    val hours = duration / 3_600_000.0
                    val avgSpd = if (hours > 0) (acc.totalDistanceM / 1000f) / hours else 0.0
                    // simple calories: km * CALORIES_PER_KM
                    val cals = ((acc.totalDistanceM / 1000f) * CALORIES_PER_KM).toInt()

                    RideSession(
                        path             = acc.path,
                        totalDistanceM   = acc.totalDistanceM,
                        averageSpeedKmh  = avgSpd,
                        maxSpeedKmh      = acc.maxSpeedKmh,
                        elevationGainM   = acc.elevationGainM,
                        elevationLossM   = acc.elevationLossM,
                        calories         = cals,
                        durationMs       = duration
                    )
                }
        }
        // keep the latest session in memory; replay 1 so new collectors get it immediately
        .stateIn(
            scope        = CoroutineScope(Dispatchers.Default),
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = RideSession(
                path            = emptyList(),
                totalDistanceM  = 0f,
                averageSpeedKmh = 0.0,
                maxSpeedKmh     = 0f,
                elevationGainM  = 0f,
                elevationLossM  = 0f,
                calories        = 0,
                durationMs      = 0L
            )
        )

    // -----------------------------------
    // PUBLIC API
    // -----------------------------------

    /** Call from your ViewModel when the user taps “Start.” */
    fun start() {
        startTimeMs = System.currentTimeMillis()
        // trigger a reset inside the flatMapLatest
        CoroutineScope(Dispatchers.Default).launch {
            resetSignal.emit(Unit)
        }
    }

    /**
     * Call from your ViewModel when the user taps “Stop.”
     * Returns the final RideSession snapshot, which you can persist.
     */
    fun stopAndGetSession(): RideSession {
        return sessionFlow.value
    }
}
