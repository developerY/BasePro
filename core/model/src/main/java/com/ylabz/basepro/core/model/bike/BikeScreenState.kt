package com.ylabz.basepro.core.model.bike

import com.google.android.gms.maps.model.LatLng

data class BikeScreenState(
    val currentSpeed: Double,
    val currentTripDistance: Double,
    val totalDistance: Double,
    val rideDuration: String,
    val settings: Map<String, List<String>>,
    val location: LatLng?
)
