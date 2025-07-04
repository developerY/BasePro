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
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent
import com.ylabz.basepro.applications.bike.features.trips.ui.model.BikeRideUiModel
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState

@Composable
fun BikeTripsCompose(
    modifier: Modifier = Modifier,
    bikeRides: List<BikeRideUiModel>,
    bikeEvent: (TripsEvent) -> Unit,
    syncedIds: Set<String>,
    healthEvent: (HealthEvent) -> Unit,
    onDeleteClick: (String) -> Unit,
    onSyncClick: (String) -> Unit,
    healthUiState: HealthUiState,
    navTo: (String) -> Unit
) {

    // derive a simple “connected?” flag
    val connected = healthUiState is HealthUiState.Success
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

            item {
                TripSectionHeader(
                    onEvent = bikeEvent,
                    title = "Bike Rides",
                    bgColor = MaterialTheme.colorScheme.surfaceVariant,
                    healthConnected = connected,
                    onHealthToggle = {
                        healthEvent(HealthEvent.RequestPermissions)
                    }
                )
            }

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
