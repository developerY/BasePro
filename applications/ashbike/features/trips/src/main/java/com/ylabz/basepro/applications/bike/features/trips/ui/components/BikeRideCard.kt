package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.trips.ui.model.BikeRideUiModel
import com.ylabz.basepro.core.ui.theme.BikeIconGreen

@Composable
fun BikeRideCard(
    modifier: Modifier = Modifier,
    ride: BikeRideUiModel,
    onDeleteClick: () -> Unit,
    onSyncClick: () -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onNavigate),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                // Header: Date Range and Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = ride.dateRange,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = ride.duration,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete ride",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Key metrics
                Text(text = ride.distance, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = ride.avgSpeed, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = ride.maxSpeed, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)

                Spacer(Modifier.height(8.dp))

                // Optional context row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ride.rideType?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(Modifier.width(8.dp))
                    ride.weatherCondition?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // Optional notes
                ride.notes?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }

                Spacer(Modifier.height(8.dp))

                // Bottom Action Row
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onSyncClick,
                        enabled = !ride.isSynced
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = if (ride.isSynced) "Already synced" else "Sync to Health",
                            tint = if (ride.isSynced) BikeIconGreen else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

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
