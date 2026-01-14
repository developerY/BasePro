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

// WearBikeViewModel.kt
@HiltViewModel
class WearBikeViewModel @Inject constructor(
    private val bikeServiceManager: BikeServiceManager
) : ViewModel() {

    // 1. Internal State
    private val _localRideState = MutableStateFlow(RideState.NotStarted)

    // 2. Exposed UI State (Merged)
    val uiState: StateFlow<WearBikeUiState> = combine(
        bikeServiceManager.rideInfo,
        _localRideState
    ) { info, state ->
        WearBikeUiState(
            rideInfo = info,
            rideState = state,
            isServiceBound = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WearBikeUiState()
    )

    // 3. The Single Entry Point for Actions
    fun onEvent(event: WearBikeEvent) {
        when (event) {
            is WearBikeEvent.StartRide -> {
                _localRideState.value = RideState.Riding
                bikeServiceManager.sendCommand(BikeForegroundService.ACTION_START_RIDE)
            }
            is WearBikeEvent.StopRide -> {
                _localRideState.value = RideState.Ended // Or NotStarted
                bikeServiceManager.sendCommand(BikeForegroundService.ACTION_STOP_RIDE)
            }
            is WearBikeEvent.OnHistoryClick -> {
                // Navigation is usually a "One-time Event" (Effect),
                // but for simplicity here we assume the UI observes this or handles nav
                // via a callback.
                // Log.d("VM", "Navigate to ${event.rideId}")
            }
        }
    }
}