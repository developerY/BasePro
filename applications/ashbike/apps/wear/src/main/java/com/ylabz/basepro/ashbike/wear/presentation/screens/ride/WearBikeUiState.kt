package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState

data class WearBikeUiState(
    val rideInfo: BikeRideInfo = BikeRideInfo.initial(),

    // NEW: Store state separately here, since it isn't in BikeRideInfo
    val rideState: RideState = RideState.NotStarted,

    val isServiceBound: Boolean = false,
    val errorMessage: String? = null
) {
    // Now this works because we reference the local 'rideState'
    val isRecording: Boolean
        get() = rideState == RideState.Riding

    val formattedSpeed: String
        get() = String.format("%.1f", rideInfo.currentSpeed)
}