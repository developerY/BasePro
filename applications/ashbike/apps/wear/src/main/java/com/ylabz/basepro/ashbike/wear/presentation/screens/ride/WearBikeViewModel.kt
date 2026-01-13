package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import com.ylabz.basepro.applications.bike.features.main.service.BikeServiceManager
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class WearBikeViewModel @Inject constructor(
    private val bikeServiceManager: BikeServiceManager
) : ViewModel() {

    // 1. The Raw Data
    val state: StateFlow<BikeRideInfo> = bikeServiceManager.rideInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BikeRideInfo.initial()
        )

    // 2. The Derived Boolean (Clean & Reactive)
    val isRecording: StateFlow<Boolean> = state
        .map { it.state == RideState.Riding } // Assuming you have this Enum
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun startRide() = bikeServiceManager.sendCommand(BikeForegroundService.ACTION_START_RIDE)
    fun stopRide() = bikeServiceManager.sendCommand(BikeForegroundService.ACTION_STOP_RIDE)
}