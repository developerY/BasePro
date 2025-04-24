package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent
import java.time.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.core.ui.*



@Composable
fun BikeRideCard(
    ride: BikeRideEntity,
    onEvent: (TripsEvent) -> Unit,
    navTo: (String) -> Unit
) {
    // Date formatter for start/end times
    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // use the sealed‑class helper to build the correct route
                // navTo(BikeScreen.RideDetailScreen.createRoute(ride.rideId))
                navTo(ride.rideId)
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x45FFEB3B)//getPastelColor(item.id.hashCode())
        )
        // elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: start – end
            val startText = dateFormatter.format(Date(ride.startTime))
            val endText   = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(ride.endTime))
            Text(
                text = "$startText – $endText",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            // Key metrics
            Text(
                text = "Distance: ${"%.1f".format(ride.totalDistance / 1000)} km",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Avg: ${"%.1f".format(ride.averageSpeed)} km/h",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Max: ${"%.1f".format(ride.maxSpeed)} km/h",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            // Optional context row
            Row(verticalAlignment = Alignment.CenterVertically) {
                ride.rideType?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.width(8.dp))
                ride.weatherCondition?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
            }

            // Optional notes
            ride.notes?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


@Preview
@Composable
fun BikeRideCardPreview() {
    val ride = BikeRideEntity(
        startTime = Instant.now().toEpochMilli(),
        endTime = Instant.now().plusSeconds(3600).toEpochMilli(),
        totalDistance = 10000f,
        averageSpeed = 20f,
        maxSpeed = 30f,
        elevationGain = 100f,
        elevationLoss = 50f,
        caloriesBurned = 500,
        weatherCondition = "Sunny",
        rideType = "Road",
        notes = "Great ride!",
        startLat = 40.7128,
        startLng = -74.0060,
        endLat = 40.7580,
        endLng = -73.9855
    )
    BikeRideCard(ride = ride, onEvent = {}, navTo = {})
}
