package com.ylabz.basepro.applications.bike.features.main.ui

sealed class BikeEvent {
    object StartRide                : BikeEvent()
    object StopRide                 : BikeEvent()
    data class SetTotalDistance(val distanceKm: Float) : BikeEvent()
}

