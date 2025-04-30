package com.ylabz.basepro.applications.bike.features.main.usecase

import android.location.Location
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * RideTracker: single responsibility for gathering and summarizing everything
 * about a ride.  You call `start()`, it resets, then you read from [sessionFlow].
 * When the user hits “stop”, call [stopAndGetSession] to get the final values.
 */
@Singleton
class RideTracker @Inject constructor(
    // Inject your raw sensor repos:
    @Named("real") private val realLocationRepository: UnifiedLocationRepository,
) {

    private val locationFlow: Flow<Location>      =  realLocationRepository.locationFlow
    private val speedFlow: Flow<Float>      = realLocationRepository.speedFlow

    companion object {
        // Simple calorie constant; pull into config if you like
        private const val CALORIES_PER_KM = 50
    }

    // -----------------------------------
    // INTERNAL ACCUMULATOR TYPE & STATE
    // -----------------------------------

    // Emit once on every start() to reset the scan operator
    private val resetSignal = MutableSharedFlow<Unit>(replay = 1)
    private val pausedSignal = MutableStateFlow(false)

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
            val sessionStart = startTimeMs

            // Combine paused flag, location, and speed
            combine(pausedSignal, locationFlow, speedFlow) { paused, loc, spd ->
                Triple(paused, loc, spd)
            }
                .scan(Acc(sessionStart, null, emptyList(), 0f, 0f, 0f, 0f)) { acc, (paused, loc, spd) ->
                    // always update lastLocation
                    val lastLoc = acc.lastLocation

                    // 1) Distance: only if not paused
                    val deltaM = if (!paused) (lastLoc?.distanceTo(loc) ?: 0f) else 0f
                    val totalDist = acc.totalDistanceM + deltaM

                    // 2) Max speed: only if not paused
                    val maxSpd = if (!paused) maxOf(acc.maxSpeedKmh, spd) else acc.maxSpeedKmh

                    // 3) Elevation gain/loss: only if not paused, but update last altitude
                    val elevDelta = if (!paused) ((loc.altitude - (lastLoc?.altitude ?: loc.altitude)).toFloat()) else 0f
                    val gain = acc.elevationGainM + maxOf(0f, elevDelta)
                    val loss = acc.elevationLossM + maxOf(0f, -elevDelta)

                    Acc(
                        startTimeMs    = acc.startTimeMs,
                        lastLocation   = loc,
                        path           = if (!paused) acc.path + loc else acc.path,
                        totalDistanceM = totalDist,
                        maxSpeedKmh    = maxSpd,
                        elevationGainM = gain,
                        elevationLossM = loss
                    )
                }
                .map { acc ->
                    val durationMs = System.currentTimeMillis() - acc.startTimeMs
                    val hours = durationMs / 3_600_000.0
                    val avgSpd = if (hours > 0) (acc.totalDistanceM / 1000f) / hours else 0.0
                    val calories = ((acc.totalDistanceM / 1000f) * CALORIES_PER_KM).toInt()

                    RideSession(
                        startTimeMs     = acc.startTimeMs,
                        path            = acc.path,
                        totalDistanceM  = acc.totalDistanceM,
                        averageSpeedKmh = avgSpd,
                        maxSpeedKmh     = acc.maxSpeedKmh,
                        elevationGainM  = acc.elevationGainM,
                        elevationLossM  = acc.elevationLossM,
                        calories        = calories,
                        durationMs      = durationMs
                    )
                }
        }
        .stateIn(
            scope        = CoroutineScope(Dispatchers.Default),
            started      = SharingStarted.WhileSubscribed(5_000),
            initialValue = RideSession(
                startTimeMs     = 0L,
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
        pausedSignal.value = false
        CoroutineScope(Dispatchers.Default).launch {
            resetSignal.emit(Unit)
        }
    }

    /** Call from your ViewModel to pause mid‐ride. */
    fun pauseRide() {
        pausedSignal.value = true
    }

    /** Call from your ViewModel to resume after a pause. */
    fun resumeRide() {
        pausedSignal.value = false
    }

    /**
     * Call from your ViewModel when the user taps “Stop.”
     * Returns the final RideSession snapshot, which you can persist.
     */
    fun stopAndGetSession(): RideSession {
        // Ensure we are no longer paused
        pausedSignal.value = false
        return sessionFlow.value
    }

}
