package com.ylabz.basepro.applications.bike.features.trips.ui.components





import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import com.ylabz.basepro.applications.bike.database.mapper.BikeRide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.isNotBlank
import android.text.format.DateUtils
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.TextFieldValue
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent

import androidx.compose.runtime.saveable.rememberSaveable

data class LocationDto(val lat: Double, val lng: Double)

// --- add this to your TripsEvent sealed class: ---
// data class UpdateNotes(val rideId: String, val notes: String): TripsEvent()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideDetailScreen(
    rideWithLocs: RideWithLocations?,
    onEvent: (TripsEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDirty by remember { mutableStateOf(false) }

    Box(modifier) {
        if (rideWithLocs == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        val ride = rideWithLocs.bikeRideEnt

        // editable notes state
        var notesState by remember {
            mutableStateOf(TextFieldValue(ride.notes.orEmpty()))
        }

        // detect when in @Preview so we skip the map block
        val isPreview = LocalInspectionMode.current

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1) Stats row
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatCard("Distance", "%.1f km".format(ride.totalDistance / 1000f))
                StatCard("Avg Speed", "%.1f km/h".format(ride.averageSpeed))
                StatCard("Max Speed", "%.1f km/h".format(ride.maxSpeed))
            }

            // 2) Time range
            Text(
                "From ${java.text.SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(ride.startTime)}\n" +
                        "To   ${java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(ride.endTime)}",
                style = MaterialTheme.typography.bodyMedium
            )

            // 3) Map preview (skipped in @Preview)
            if (!isPreview) {
                val path = remember(rideWithLocs.locations) {
                    rideWithLocs.locations.map { LatLng(it.lat, it.lng) }
                }
                val cameraState = rememberCameraPositionState {
                    position = path.firstOrNull()
                        ?.let { CameraPosition.fromLatLngZoom(it, 15f) }
                        ?: CameraPosition.fromLatLngZoom(
                            LatLng(ride.startLat, ride.startLng), 12f
                        )
                }

                Box(Modifier.fillMaxWidth().height(240.dp)) {
                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraState,
                        uiSettings = MapUiSettings(zoomControlsEnabled = false)
                    ) {
                        if (path.size >= 2) {
                            Polyline(points = path, color = MaterialTheme.colorScheme.primary, width = 6f)
                            Marker(state = MarkerState(path.first()), title = "Start")
                            Marker(state = MarkerState(path.last()),  title = "End")
                        }
                    }
                }
            }

            // 4) Elevation / Duration / Calories
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatCard(
                    "Elevation",
                    "${ride.elevationGain.toInt()}↑/${ride.elevationLoss.toInt()}↓ m"
                )

                val durationSecs = ((ride.endTime - ride.startTime) / 1000).coerceAtLeast(0L)
                val hours = durationSecs / 3600
                val mins = (durationSecs % 3600) / 60
                val durationText = if (hours > 0) "%d:%02d".format(hours, mins)
                else DateUtils.formatElapsedTime(durationSecs)

                StatCard("Duration", durationText)
                StatCard("Calories", "${ride.caloriesBurned} kcal")
            }

            // 5) Editable Notes
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Notes", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(4.dp))
                    TextField(
                        value = notesState,
                        onValueChange = {
                            notesState = it
                            isDirty = it.text != ride.notes.orEmpty()
                        },
                        trailingIcon = {
                            if (isDirty) {
                                IconButton(onClick = {
                                    onEvent(TripsEvent.UpdateRideNotes(ride.rideId, notesState.text))
                                    isDirty = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Save notes"
                                    )
                                }
                            }
                        },
                        placeholder = { Text("No Ride Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            // containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }

            // 6) Weather (if any)
            ride.weatherCondition?.let {
                Text("Weather: $it", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(
        Modifier.size(100.dp, 80.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RideDetailScreen_Preview() {
    val sampleRide = BikeRideEntity(
        rideId = "R1",
        startTime = 0L,
        endTime = 5_400_000L,      // 1h30m
        totalDistance = 20000f,
        averageSpeed = 22f,
        maxSpeed = 35f,
        elevationGain = 150f,
        elevationLoss = 120f,
        caloriesBurned = 600,
        notes = "Lovely afternoon ride",
        startLat = 37.77,
        startLng = -122.42,
        endLat = 37.78,
        endLng = -122.41
    )
    RideDetailScreen(
        rideWithLocs = RideWithLocations(sampleRide, emptyList()),
        onEvent = {}
    )
}


@Preview
@Composable
fun RideDetailScreenPreview() {
    val bikeRide = BikeRideEntity(
        startTime = 1678886400000L, // Example timestamp
        endTime = 1678890000000L,
        totalDistance = 15000f,
        averageSpeed = 25f,
        maxSpeed = 35f,
        elevationGain = 200f,
        elevationLoss = 150f,
        caloriesBurned = 500,
        avgHeartRate = 140,
        maxHeartRate = 160,
        weatherCondition = "Sunny",
        rideType = "Commute",
        notes = "Great ride!",
        rating = 5,
        bikeId = "Bike123",
        batteryStart = 80,
        batteryEnd = 60,
        startLat = 37.7749,
        startLng = -122.4194,
        endLat = 37.7850,
        endLng = -122.4060
    )

    val path = listOf(
        LatLng(bikeRide.startLat, bikeRide.startLng),
        LatLng(bikeRide.endLat, bikeRide.endLng)
    )
    val centerLat = (bikeRide.startLat + bikeRide.endLat) / 2
    val centerLng = (bikeRide.startLng + bikeRide.endLng) / 2

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(centerLat, centerLng),
            13f
        )
    }

    /*GoogleMap(
        modifier             = Modifier.fillMaxSize(),
        cameraPositionState  = cameraState,
        uiSettings           = MapUiSettings(zoomControlsEnabled = true),
        properties           = MapProperties(mapType = MapType.NORMAL)
    ) {
        Polyline(points = path, width = 5f)
        Marker(state = MarkerState(path.first()), title = "Start")
        Marker(state = MarkerState(path.last()),  title = "End")    }*/
}

@Preview(showBackground = true)
@Composable
fun RideStatsRowPreview() {
    // sample ride lasting 1h 23m 45s, gain 200m, loss 150m, 500 kcal
    val sample = BikeRideEntity(
        startTime = 0L,
        endTime   = 1_000L * (60 * 60 + 23 * 60 + 45),
        elevationGain = 200f,
        elevationLoss = 150f,
        caloriesBurned = 500,
        startLat = 0.0,
        startLng = 0.0,
        endLat = 0.0,
        endLng = 0.0,
        totalDistance = 0f,
        averageSpeed = 0f,
        maxSpeed = 0f,
        avgHeartRate = 0,
        maxHeartRate = 0,
        weatherCondition = null,
        rideType = null,
        notes = null,
        rating = null,
        bikeId = null,
        batteryStart = null,
        batteryEnd = null


    )

    // compute duration text
    val durationSeconds = ((sample.endTime - sample.startTime) / 1000).coerceAtLeast(0L)
    val durationText = DateUtils.formatElapsedTime(durationSeconds)

    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatCard(
            label = "Elevation",
            value = "${sample.elevationGain.toInt()}↑/${sample.elevationLoss.toInt()}↓ m"
        )
        StatCard(
            label = "Duration",
            value = durationText
        )
        StatCard(
            label = "Calories",
            value = "${sample.caloriesBurned} kcal"
        )
    }
}
