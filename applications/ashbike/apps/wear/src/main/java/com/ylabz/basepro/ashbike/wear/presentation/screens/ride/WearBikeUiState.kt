package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
data class WearBikeUiState(
    val rideInfo: BikeRideInfo = BikeRideInfo.initial(),
    val isServiceBound: Boolean = false
) {
    // FIX: Access 'rideState' (not 'state') from the domain model
    val isRecording: Boolean
        get() = rideInfo.rideState == RideState.Riding

    val formattedSpeed: String
        get() = String.format("%.1f", rideInfo.currentSpeed)
}