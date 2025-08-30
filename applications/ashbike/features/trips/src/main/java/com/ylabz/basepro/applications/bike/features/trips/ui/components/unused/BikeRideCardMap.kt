package com.ylabz.basepro.applications.bike.features.trips.ui.components.unused

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.res.stringResource
import com.ylabz.basepro.applications.bike.features.trips.R // Added R class import


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
            Text(stringResource(R.string.feature_trips_card_header_format, startFmt, endFmt), style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(8.dp))

            // Metrics
            Text(stringResource(R.string.feature_trips_card_label_distance) + stringResource(R.string.feature_trips_card_value_km_format, ride.totalDistance / 1000),
                style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.feature_trips_card_label_avg_speed) + stringResource(R.string.feature_trips_card_value_kmh_format, ride.averageSpeed),
                style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.feature_trips_card_label_max_speed) + stringResource(R.string.feature_trips_card_value_kmh_format, ride.maxSpeed),
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
                            title = stringResource(R.string.feature_trips_card_map_marker_start)
                        )
                        // end marker
                        Marker(
                            state = MarkerState(position = pathPoints.last()),
                            title = stringResource(R.string.feature_trips_card_map_marker_end)
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
/*
@Preview
@Composable
fun BikeRideCardMapPreview() {
    val ride = BikeRideEntity(
        startTime = System.currentTimeMillis() - 3600000,
        endTime = System.currentTimeMillis(),
        totalDistance = 15000f,
        averageSpeed = 18f,
        maxSpeed = 30f,
        elevationGain = 50f,
        elevationLoss = 30f,
        caloriesBurned = 450,
        avgHeartRate = 120,
        maxHeartRate = 160,
        // healthConnectRecordId = "health-connect-id", // Not used
        isHealthDataSynced = true,
        weatherCondition = "Sunny",
        rideType = "Road",
        notes = "Nice ride!",
        rating = 4,
        bikeId = "bike-id-1",
        batteryStart = 100,
        batteryEnd = 85,
        startLat = 40.7128,
        startLng = -74.0060,
        endLat = 40.7589,
        endLng = -73.9851
    )
    BikeRideCardMap(ride = ride, onEvent = {}, navTo = {})
}
*/