package com.ylabz.basepro.applications.bike.features.trips.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@Composable
fun BikeRideCardMap(
    ride: BikeRideEntity,
    onEvent: (TripsEvent) -> Unit,
    navTo: (String) -> Unit
) {
    // 1) parse your JSON‐serialized route into LatLngs
    val pathPoints: List<LatLng> = emptyList()
        // remember(ride.routeJson) {
        /*try {
            // ride.routeJson was produced with Gson().toJson(listOf { "lat"->..., "lng"->... })
            val list = Gson().fromJson(
                ride.routeJson,
                Array<Map<String, Double>>::class.java
            ).toList()
            list.map { LatLng(it["lat"]!!, it["lng"]!!) }
        } catch (t: Throwable) {
            Log.e("BikeRideCard", "Failed to parse routeJson", t)
            emptyList()
        }
    }*/

    // 2) pick a camera that shows the start point (or the midpoint if you like)
    val start = pathPoints.firstOrNull() ?: LatLng(ride.startLat, ride.startLng)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(start, 14f)
    }

    // date formatting
    val startFmt = remember {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    }.format(Date(ride.startTime))
    val endFmt = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault())
    }.format(Date(ride.endTime))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navTo(ride.rideId) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            // Header
            Text("$startFmt – $endFmt", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            // Metrics
            Text("Distance: ${"%.1f".format(ride.totalDistance / 1000)} km",
                style = MaterialTheme.typography.bodyMedium)
            Text("Avg: ${"%.1f".format(ride.averageSpeed)} km/h",
                style = MaterialTheme.typography.bodyMedium)
            Text("Max: ${"%.1f".format(ride.maxSpeed)} km/h",
                style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(8.dp))

            // 3) Map preview
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false)
                ) {
                    if (pathPoints.isNotEmpty()) {
                        // draw the route
                        Polyline(
                            points = pathPoints,
                            color = MaterialTheme.colorScheme.primary,
                            width = 6f
                        )
                        // start marker
                        Marker(
                            state = MarkerState(position = pathPoints.first()),
                            title = "Start"
                        )
                        // end marker
                        Marker(
                            state = MarkerState(position = pathPoints.last()),
                            title = "End"
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // optional rideType / weather
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                ride.rideType?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                ride.weatherCondition?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }

            // optional notes
            ride.notes?.takeIf(String::isNotBlank)?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
