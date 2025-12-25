package com.ylabz.basepro.applications.bike.features.main.ui

sealed class BikeEvent {
    // Ride Controls
    object StartRide : BikeEvent()
    object StopRide : BikeEvent()

    // Bike Dashboard Interactions
    object OnBikeClick : BikeEvent()
    data class SetTotalDistance(val distanceKm: Float) : BikeEvent()
    object DismissSetDistanceDialog : BikeEvent()

    // 1. ADD THE TOGGLE EVENT
    // Glass / XR Controls
    object ToggleGlassProjection : BikeEvent()



    // Added for Option 2: Semantic Event for navigations
    // Navigation Events
    data class NavigateToSettingsRequested(val cardKey: String?) : BikeEvent()
}
