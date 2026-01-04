package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.features.main.service.BikeForegroundService
import com.ylabz.basepro.applications.bike.features.main.service.BikeServiceManager
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class WearBikeViewModel @Inject constructor(
    private val bikeServiceManager: BikeServiceManager, // Injected from :features:main
    // private val healthServicesManager: HealthServicesManager // If you have one
) : ViewModel() {

    // Expose a clean StateFlow for the UI
    val state: StateFlow<BikeRideInfo> = bikeServiceManager.rideInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BikeRideInfo.initial()
        )

    fun startRide() = bikeServiceManager.sendCommand(BikeForegroundService.ACTION_START_RIDE)
    fun stopRide() = bikeServiceManager.sendCommand(BikeForegroundService.ACTION_STOP_RIDE)
}