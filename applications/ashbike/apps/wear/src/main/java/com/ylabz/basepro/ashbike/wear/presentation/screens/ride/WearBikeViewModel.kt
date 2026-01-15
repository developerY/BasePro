package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import com.ylabz.basepro.applications.bike.features.main.service.BikeServiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// WearBikeViewModel.kt
@HiltViewModel
class WearBikeViewModel @Inject constructor(
    private val bikeServiceManager: BikeServiceManager
) : ViewModel() {

    // 1. Single Source of Truth: The Service
    // We map the incoming data directly to our UI State.
    val uiState: StateFlow<WearBikeUiState> = bikeServiceManager.rideInfo
        .map { info ->
            WearBikeUiState(
                rideInfo = info,
                isServiceBound = true
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WearBikeUiState()
        )

    // 2. Actions just send commands (The Service will update the state, which updates the UI)
    fun onEvent(event: WearBikeEvent) {
        when (event) {
            is WearBikeEvent.StartRide -> {
                bikeServiceManager.sendCommand(BikeForegroundService.ACTION_START_RIDE)
            }
            is WearBikeEvent.StopRide -> {
                bikeServiceManager.sendCommand(BikeForegroundService.ACTION_STOP_RIDE)
            }
            is WearBikeEvent.OnHistoryClick -> {
                // Handle nav
            }
        }
    }
}