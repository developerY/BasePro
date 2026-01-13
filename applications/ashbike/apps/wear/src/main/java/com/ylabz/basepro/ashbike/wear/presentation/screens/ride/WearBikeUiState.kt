package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState

data class WearBikeUiState(
    // The raw domain data
    val rideInfo: BikeRideInfo = BikeRideInfo.initial(),

    // UI-specific flags (e.g., service connection, permissions)
    val isServiceBound: Boolean = false,
    val errorMessage: String? = null
) {
    // Helper: The UI just calls "state.isRecording" without knowing about enums
    val isRecording: Boolean
        get() = rideInfo.state == RideState.Riding

    // Helper: Formatted speed for display (Optional, but clean)
    val formattedSpeed: String
        get() = String.format("%.1f", rideInfo.currentSpeed)
}