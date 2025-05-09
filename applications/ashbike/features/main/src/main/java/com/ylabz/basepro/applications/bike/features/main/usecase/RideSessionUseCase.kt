package com.ylabz.basepro.applications.bike.features.main.usecase

import com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository
import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepository
import com.ylabz.basepro.core.data.di.LowPower
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * The flow
 *
 *  location/speed/heading/userStats
 *         ↓
 *    RideStatsUseCase  (pure math)
 *         ↓
 *    RideSessionUseCase (reset/pause lifecycle)
 *         ↓
 *      ViewModel → UI
 *
 */


@ActivityRetainedScoped
class RideSessionUseCase @Inject constructor(
    @LowPower private val lowPowerRepo: UnifiedLocationRepository, // provides locationFlow & speedFlow
    private val compassRepo: CompassRepository,                    // provides headingFlow
    private val statsUseCase: RideStatsUseCase,                    // our new unified stats use-case
    private val userProfileRepo: UserProfileRepository             // to build UserStats
) {
    // 1) Reset & pause signals
    private val resetSignal  = MutableSharedFlow<Unit>(replay = 1)
    private val pausedSignal = MutableStateFlow(false)

    // 2) Build a Flow<UserStats> from your DataStore repo
    private val userStatsFlow: Flow<UserStats> = combine(
        userProfileRepo.heightFlow,
        userProfileRepo.weightFlow
    ) { heightStr, weightStr ->
        UserStats(
            heightCm = heightStr.toFloatOrNull() ?: 0f,
            weightKg = weightStr.toFloatOrNull() ?: 0f
        )
    }

    // 3) Delegate to RideStatsUseCase.sessionFlow() for a single, resettable StateFlow<RideSession>
    /** One single, resettable session of EVERYTHING (distance, elevation, calories, path, heading) */
    val sessionFlow: StateFlow<RideSession> = statsUseCase.sessionFlow(
        //pausedSignal   = pausedSignal,
        resetSignal    = resetSignal,
        locationFlow   = lowPowerRepo.locationFlow,
        speedFlow      = lowPowerRepo.speedFlow,
        headingFlow    = compassRepo.headingFlow,
        userStatsFlow  = userStatsFlow
    )

    /** Convenience flow for just the distance. */
    val distanceFlow: Flow<Float> = sessionFlow
        .map { it.totalDistanceKm }
        .distinctUntilChanged()

    // -----------------------------------
    // PUBLIC API
    // -----------------------------------

    /** Call from your ViewModel when the user taps “Start.” */
    /** Call from your ViewModel when the user taps “Start.” */
    fun start() {
        // 2) reset all accumulators
        CoroutineScope(Dispatchers.Default).launch { resetSignal.emit(Unit) }
        pausedSignal.value = false
    }

    /** Pause mid-ride. */
    fun pause() {
        pausedSignal.value = true
    }

    /** Resume after a pause. */
    fun resume() {
        pausedSignal.value = false
    }

    /**
     * Call when the user taps “Stop.” Returns the final snapshot,
     * which you can persist.
     */
    fun stopAndGetSession(): RideSession {
        // make sure we're un-paused so duration keeps ticking to the end
        // 1) ensure we’re unpaused so duration counts to the end
        pausedSignal.value = false
        return sessionFlow.value
    }

    init {
        // 1) Kick everything off immediately with an initial reset
        CoroutineScope(Dispatchers.Default).launch { resetSignal.emit(Unit) }
        // 2) And keep it hot
        // Kick the sessionFlow so it’s hot from the get-go
        CoroutineScope(Dispatchers.Default).launch { sessionFlow.collect() }
    }
}
