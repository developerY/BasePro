package com.ylabz.basepro.applications.bike.features.trips.ui.components

import android.R.attr.onClick
import android.util.Log
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
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import com.ylabz.basepro.core.ui.*
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds
import androidx.health.connect.client.records.Record


@Composable
fun BikeRideCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    ride: BikeRideEntity,
    syncedIds: Set<String>,
    bikeEvent: (TripsEvent) -> Unit,
    healthEvent: (HealthEvent) -> Unit,
    bikeToHealthConnectRecords: (BikeRideEntity) -> List<Record>,
    navTo: (String) -> Unit
) {

    val isSynced = ride.rideId in syncedIds

    // Date formatter for start/end times
    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    }

    // 1) Compute Kotlin Duration from millis difference
    val duration = (ride.endTime - ride.startTime).milliseconds

    // 2) Extract minutes (and seconds, if you like)
    val minutes = duration.inWholeMinutes
    val seconds = duration.inWholeSeconds % 60

    // 3) Format your start/end times
    val start = Instant.ofEpochMilli(ride.startTime)
        .atZone(ZoneId.systemDefault())
    val end = Instant.ofEpochMilli(ride.endTime)
        .atZone(ZoneId.systemDefault())


    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                // use the sealed‑class helper to build the correct route
                // navTo(BikeScreen.RideDetailScreen.createRoute(ride.rideId))
                navTo(ride.rideId)
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box {
            // Accent stripe…
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color(0xFFD2D9DE))
            )
            Column(modifier = Modifier.padding(16.dp)) {
                // Header: start – end
                val startText = dateFormatter.format(Date(ride.startTime))
                val endText = SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(ride.endTime))


                // Header: start – end + delete icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$startText – $endText",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "(${minutes} min${if (seconds > 0) " ${seconds}s" else ""})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = {
                        bikeEvent(TripsEvent.DeleteItem(ride.rideId))
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete ride"
                        )
                    }
                }


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

                Spacer(Modifier.height(8.dp))

                // ─── Bottom Action Row ────────────────────────────────────
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Sync button: only enabled if not already synced
                    IconButton(
                        onClick = {
                            // 1) Build the Health Connect records from your domain ride
                            val rideInfo: List<Record> = bikeToHealthConnectRecords(ride)
                            Log.d("BikeRideCard", "rideInfo: $rideInfo")
                            // 2) Tell the HealthViewModel to insert them
                            healthEvent(HealthEvent.Insert(rideInfo))
                        },
                        enabled = isSynced
                    ) {
                        Icon(
                            imageVector     = Icons.Default.Sync,
                            contentDescription = if (isSynced) "Already synced" else "Sync to Health",
                            tint = if (isSynced)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    }

                    /* Delete button (again, for convenience)
                IconButton(onClick = { onEvent(TripsEvent.DeleteItem(ride.rideId)) }) {
                    Icon(
                        imageVector     = Icons.Default.Delete,
                        contentDescription = "Delete ride"
                    )
                }*/
                }
            }
        }
    }
}


