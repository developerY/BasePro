package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

// WearBikeEvent.kt
sealed class WearBikeEvent {
    object StartRide : WearBikeEvent()
    object StopRide : WearBikeEvent()
    data class OnHistoryClick(val rideId: String) : WearBikeEvent()
    // Add more as needed, e.g., object OnPauseRide : WearBikeEvent()
}