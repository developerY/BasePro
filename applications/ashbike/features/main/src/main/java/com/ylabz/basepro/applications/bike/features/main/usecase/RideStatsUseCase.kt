package com.ylabz.basepro.applications.bike.features.main.usecase

import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Encapsulates all of the per‑ride statistics logic:
 *  – total distance (km)
 *  – max speed (km/h)
 *  – average speed (km/h)
 *  – elevation gain & loss (m)
 *  – calories burned
 *
 * Each flow “resets” whenever `resetSignal` emits Unit.
 */

/**
 * Holds the user’s anthropometric inputs.
 */
data class UserStats(
    val heightCm: Float,
    val weightKg: Float
)

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class RideStatsUseCase @Inject constructor(
    private val calculateCaloriesUseCase: CalculateCaloriesUseCase
) {
    companion object {
        // fallback constant if needed
        private const val CALORIES_PER_KM = 23
    }

    /**
     * Raw path of GPS points, resettable on [resetSignal].
     */
    fun pathFlow(
        resetSignal:  Flow<Unit>,
        locationFlow: Flow<Location>
    ): Flow<List<Location>> =
        resetSignal
            .onStart { emit(Unit) }
            .flatMapLatest {
                locationFlow
                    .scan(emptyList<Location>()) { acc, loc -> acc + loc }
            }
            .distinctUntilChanged()

    /** Total distance in km, resettable. */
    /**
     * Total distance in kilometers, resettable on [resetSignal].
     */
    fun distanceKmFlow(
        resetSignal:  Flow<Unit>,
        locationFlow: Flow<Location>
    ): Flow<Float> =
        resetSignal
            .onStart { emit(Unit) }
            .flatMapLatest<Unit, Float> {
                locationFlow
                    .scan<Location, Pair<Location?, Float>>(null to 0f) { (prev, total), curr ->
                        val deltaKm = prev
                            ?.distanceTo(curr)
                            ?.div(1_000f)
                            ?: 0f
                        curr to (total + deltaKm)
                    }
                    .map<Pair<Location?, Float>, Float> { it.second }
            }
            .distinctUntilChanged()

    /** Maximum instantaneous speed (km/h), resettable. */
    fun maxSpeedFlow(
        resetSignal: Flow<Unit>,
        speedFlow:   Flow<Float>
    ): Flow<Float> =
        resetSignal
            .onStart { emit(Unit) }
            .flatMapLatest<Unit, Float> {
                speedFlow
                    .scan<Float, Float>(0f) { maxSoFar, curr ->
                        maxOf(maxSoFar, curr)
                    }
            }
            .distinctUntilChanged()

    /** Average speed (km/h) as running mean, resettable. */
    fun averageSpeedFlow(
        resetSignal: Flow<Unit>,
        speedFlow:   Flow<Float>
    ): Flow<Double> =
        resetSignal
            .onStart { emit(Unit) }
            .flatMapLatest<Unit, Double> {
                speedFlow
                    .map<Float, Double> { it.toDouble() }
                    .scan<Double, Pair<Double, Int>>(0.0 to 0) { (sum, count), curr ->
                        (sum + curr) to (count + 1)
                    }
                    .map<Pair<Double, Int>, Double> { (sum, count) ->
                        if (count > 0) sum / count else 0.0
                    }
            }
            .distinctUntilChanged()

    /** Elevation gain in meters, resettable. */
    fun elevationGainFlow(
        resetSignal:  Flow<Unit>,
        locationFlow: Flow<Location>
    ): Flow<Float> =
        resetSignal
            .onStart { emit(Unit) }
            .flatMapLatest<Unit, Float> {
                locationFlow
                    .scan<Location, Pair<Location?, Float>>(null to 0f) { (prev, gain), curr ->
                        val delta = prev
                            ?.let {
                                if (curr.altitude > it.altitude)
                                    (curr.altitude - it.altitude).toFloat()
                                else 0f
                            }
                            ?: 0f
                        curr to (gain + delta)
                    }
                    .map<Pair<Location?, Float>, Float> { it.second }
            }
            .distinctUntilChanged()

    /** Elevation loss in meters, resettable. */
    fun elevationLossFlow(
        resetSignal:  Flow<Unit>,
        locationFlow: Flow<Location>
    ): Flow<Float> =
        resetSignal
            .onStart { emit(Unit) }
            .flatMapLatest<Unit, Float> {
                locationFlow
                    .scan<Location, Pair<Location?, Float>>(null to 0f) { (prev, loss), curr ->
                        val delta = prev
                            ?.let {
                                if (it.altitude > curr.altitude)
                                    (it.altitude - curr.altitude).toFloat()
                                else 0f
                            }
                            ?: 0f
                        curr to (loss + delta)
                    }
                    .map<Pair<Location?, Float>, Float> { it.second }
            }
            .distinctUntilChanged()

    /**
     * Resettable flow of RideSession carrying _all_ stats — path, distance, speed,
     * elevation, calories, heading, and elapsed time.
     */
    fun sessionFlow(
        resetSignal:      Flow<Unit>,
        locationFlow:     Flow<Location>,
        speedFlow:        Flow<Float>,
        headingFlow:      Flow<Float>,
        userStatsFlow:    Flow<UserStats>,
        clockMs:          () -> Long = { System.currentTimeMillis() }
    ): StateFlow<RideSession> {
        val upstream: Flow<RideSession> = resetSignal
            .onStart { emit(Unit) }
            .flatMapLatest {
                // snapshot start time
                val startMs = clockMs()

                // build each component flow
                val pFlow = pathFlow(resetSignal, locationFlow)
                val dFlow = distanceKmFlow(resetSignal, locationFlow)
                val mFlow = maxSpeedFlow(resetSignal, speedFlow)
                val aFlow = averageSpeedFlow(resetSignal, speedFlow)
                val gFlow = elevationGainFlow(resetSignal, locationFlow)
                val lFlow = elevationLossFlow(resetSignal, locationFlow)
                val cFlow = calculateCaloriesUseCase(dFlow, speedFlow, userStatsFlow)
                    .map { it.toInt() }

                // first combine path + dist + max + avg + gain
                data class Five(val path: List<Location>, val dist: Float, val max: Float, val avg: Double, val gain: Float)
                val five: Flow<Five> = combine(pFlow, dFlow, mFlow, aFlow, gFlow) { path, dist, max, avg, gain ->
                    Five(path, dist, max, avg, gain)
                }

                // then combine those five with loss, calories, and heading into RideSession
                combine(five, lFlow, cFlow, headingFlow) { fiveStats, loss, calories, heading ->
                    val elapsed = clockMs() - startMs
                    RideSession(
                        startTimeMs     = startMs,
                        path            = fiveStats.path,
                        elapsedMs       = elapsed,
                        totalDistanceKm = fiveStats.dist,
                        averageSpeedKmh = fiveStats.avg,
                        maxSpeedKmh     = fiveStats.max,
                        elevationGainM  = fiveStats.gain,
                        elevationLossM  = loss,
                        caloriesBurned  = calories,
                        heading         = heading
                    )
                }
            }

        return upstream
            .stateIn(
                scope        = CoroutineScope(Dispatchers.Default),
                started      = SharingStarted.WhileSubscribed(5_000),
                initialValue = RideSession(
                    startTimeMs     = 0L,
                    path            = emptyList(),
                    elapsedMs       = 0L,
                    totalDistanceKm = 0f,
                    averageSpeedKmh = 0.0,
                    maxSpeedKmh     = 0f,
                    elevationGainM  = 0f,
                    elevationLossM  = 0f,
                    caloriesBurned  = 0,
                    heading         = 0f
                )
            )
    }
}

