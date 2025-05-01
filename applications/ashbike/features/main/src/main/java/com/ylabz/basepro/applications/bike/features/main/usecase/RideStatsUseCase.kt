package com.ylabz.basepro.applications.bike.features.main.usecase

import android.location.Location
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
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
@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class RideStatsUseCase @Inject constructor() {

    companion object {
        // example conversion: 50 kcal per km
        private const val CALORIES_PER_KM = 23 // 50
    }

    /** Total distance in km, resettable. */
    fun distanceKmFlow(
        resetSignal: Flow<Unit>,
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
        speedFlow: Flow<Float>
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

    /** Average speed (km/h) as the running mean, resettable. */
    fun averageSpeedFlow(
        resetSignal: Flow<Unit>,
        speedFlow: Flow<Float>
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
        resetSignal: Flow<Unit>,
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
        resetSignal: Flow<Unit>,
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

    /** Simple calories estimation (kcal), resettable. */
    fun caloriesFlow(
        resetSignal: Flow<Unit>,
        distanceFlow: Flow<Float>
    ): Flow<Int> =
        resetSignal
            .onStart { emit(Unit) }
            .flatMapLatest<Unit, Int> {
                distanceFlow
                    .map<Float, Int> { (it * CALORIES_PER_KM).toInt() }
            }
            .distinctUntilChanged()
}

