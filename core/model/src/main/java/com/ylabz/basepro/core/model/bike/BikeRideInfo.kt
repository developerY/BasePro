package com.ylabz.basepro.core.model.bike

import com.google.android.gms.maps.model.LatLng

data class BikeRideInfo(
    val currentSpeed: Double,
    val currentTripDistance: Double,
    val totalDistance: Double,
    val rideDuration: String,
    val settings: Map<String, List<String>>,
    val location: LatLng?,
    val averageSpeed: Double = 25.0,
    val elevation : Double = 150.0,
    val heading : Float = 45f
)
