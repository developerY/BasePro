package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.applications.bike.features.trips.ui.model.BikeRideUiModel

@Preview
@Composable
fun BikeRideCardPreview() {
    val previewRide = BikeRideUiModel(
        rideId = "123",
        dateRange = "Oct 26, 10:00 – 11:30",
        duration = "(90 min)",
        distance = "Distance: 25.1 km",
        avgSpeed = "Avg: 16.7 km/h",
        maxSpeed = "Max: 35.2 km/h",
        rideType = "Road",
        weatherCondition = "Sunny",
        notes = "A beautiful morning ride through the park.",
        isSynced = false
    )
    BikeRideCard(
        ride = previewRide,
        onDeleteClick = {},
        onSyncClick = {},
        onNavigate = {}
    )
}

@Preview
@Composable
fun BikeRideCardSyncedPreview() {
    val previewRide = BikeRideUiModel(
        rideId = "124",
        dateRange = "Oct 25, 18:00 – 19:00",
        duration = "(60 min)",
        distance = "Distance: 15.5 km",
        avgSpeed = "Avg: 15.5 km/h",
        maxSpeed = "Max: 28.0 km/h",
        rideType = "Commute",
        weatherCondition = "Cloudy",
        notes = null,
        isSynced = true
    )
    BikeRideCard(
        ride = previewRide,
        onDeleteClick = {},
        onSyncClick = {},
        onNavigate = {}
    )
}
