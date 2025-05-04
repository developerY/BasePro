package com.ylabz.basepro.applications.bike.features.main.usecase

import com.ylabz.basepro.applications.bike.database.repository.UserProfileRepository
import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepository
import android.location.Location
import com.ylabz.basepro.core.data.di.LowPower
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@ActivityRetainedScoped
class RideTracker @Inject constructor(
    @LowPower private val lowPowerRepo: UnifiedLocationRepository, // provides locationFlow & speedFlow
    //private val compassRepo: CompassRepository,                    // provides headingFlow
    @Named("real") private val compassRepo: CompassRepository,
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
    val sessionFlow: StateFlow<RideSession> = statsUseCase.sessionFlow(
        resetSignal    = resetSignal,
        locationFlow   = lowPowerRepo.locationFlow,
        speedFlow      = lowPowerRepo.speedFlow,
        headingFlow    = compassRepo.headingFlow,
        userStatsFlow  = userStatsFlow
    )

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
        // Kick the sessionFlow so it’s hot from the get-go
        CoroutineScope(Dispatchers.Default).launch {
            sessionFlow.collect()
        }
    }
}
