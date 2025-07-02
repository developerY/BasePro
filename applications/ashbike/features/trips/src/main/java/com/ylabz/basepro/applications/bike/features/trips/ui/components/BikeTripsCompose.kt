package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.trips.ui.model.BikeRideUiModel

@Composable
fun BikeTripsCompose(
    modifier: Modifier = Modifier,
    bikeRides: List<BikeRideUiModel>,
    onDeleteClick: (String) -> Unit,
    onSyncClick: (String) -> Unit,
    navTo: (String) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (bikeRides.isEmpty()) {
                item {
                    Text(
                        text = "You haven’t recorded any rides yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(bikeRides, key = { it.rideId }) { rideModel ->
                    BikeRideCard(
                        modifier = Modifier.fillMaxWidth(),
                        ride = rideModel,
                        onDeleteClick = { onDeleteClick(rideModel.rideId) },
                        onSyncClick = { onSyncClick(rideModel.rideId) },
                        onNavigate = { navTo(rideModel.rideId) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BikeTripsComposePreview() {
    val previewRides = listOf(
        BikeRideUiModel(
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
        ),
        BikeRideUiModel(
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
    )
    BikeTripsCompose(
        bikeRides = previewRides,
        onDeleteClick = {},
        onSyncClick = {},
        navTo = {}
    )
}