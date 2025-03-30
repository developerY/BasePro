package com.ylabz.basepro.core.model.bike

import com.google.android.gms.maps.model.LatLng

data class BikeRideInfo(
    val location: LatLng?,
    val currentSpeed: Double,
    val currentTripDistance: Double,
    val totalDistance: Double,
    val rideDuration: String,
    val settings: Map<String, List<String>>,
    val averageSpeed: Double,
    val elevation : Double,
    val heading : Float,
    val batteryLevel: Int?,
    val motorPower: Float?
)
