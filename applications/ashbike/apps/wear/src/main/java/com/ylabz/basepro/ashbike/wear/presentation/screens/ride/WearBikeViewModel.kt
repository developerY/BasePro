package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import com.ylabz.basepro.applications.bike.features.main.service.BikeServiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class WearBikeViewModel @Inject constructor(
    private val bikeServiceManager: BikeServiceManager
) : ViewModel() {

    // Combine the two streams: Info + State
    val uiState: StateFlow<WearBikeUiState> = combine(
        bikeServiceManager.rideInfo,
        bikeServiceManager.rideState // <--- You need to expose this from Manager
    ) { info, state ->
        WearBikeUiState(
            rideInfo = info,
            rideState = state, // Pass the separate state
            isServiceBound = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WearBikeUiState()
    )

    fun startRide() = bikeServiceManager.sendCommand(BikeForegroundService.ACTION_START_RIDE)
    fun stopRide() = bikeServiceManager.sendCommand(BikeForegroundService.ACTION_STOP_RIDE)
}