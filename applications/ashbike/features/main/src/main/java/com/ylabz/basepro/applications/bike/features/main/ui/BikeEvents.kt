package com.ylabz.basepro.applications.bike.features.main.ui

sealed class BikeEvent {
    object StartRide : BikeEvent()
    object StopRide : BikeEvent()
    object OnBikeClick : BikeEvent()
    data class SetTotalDistance(val distanceKm: Float) : BikeEvent()
    object DismissSetDistanceDialog : BikeEvent()

    // 1. ADD THE TOGGLE EVENT
    object ToggleGlassProjection : BikeEvent()

    // Not used anymore -- object SimulatedConnection : BikeEvent()


    // Added for Option 2: Semantic Event for navigation
    data class NavigateToSettingsRequested(val cardKey: String?) : BikeEvent()
}

// 2. DEFINE SIDE EFFECTS (For one-off actions like "Start Activity")
sealed interface BikeSideEffect {
    object LaunchGlassProjection : BikeSideEffect
    object StopGlassProjection : BikeSideEffect
}