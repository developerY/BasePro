package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import com.ylabz.basepro.applications.bike.features.main.service.BikeServiceManager
import com.ylabz.basepro.core.model.bike.RideState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class WearBikeViewModel @Inject constructor(
    private val bikeServiceManager: BikeServiceManager
) : ViewModel() {

    // 1. Local Source of Truth for "Are we recording?"
    private val _localRideState = MutableStateFlow(RideState.NotStarted)

    // 2. Combine Service Data + Local State
    val uiState: StateFlow<WearBikeUiState> = combine(
        bikeServiceManager.rideInfo,
        _localRideState
    ) { info, state ->
        WearBikeUiState(
            rideInfo = info,
            rideState = state
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WearBikeUiState()
    )

    // 3. Update local state immediately when user clicks
    fun startRide() {
        _localRideState.value = RideState.Riding
        bikeServiceManager.sendCommand(BikeForegroundService.ACTION_START_RIDE)
    }

    fun stopRide() {
        _localRideState.value = RideState.Ended // Or NotStarted, depending on logic
        bikeServiceManager.sendCommand(BikeForegroundService.ACTION_STOP_RIDE)
    }
}