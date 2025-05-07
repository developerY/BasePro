package com.ylabz.basepro.applications.bike.features.trips.ui.components





import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalInspectionMode
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent

import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Duration

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
    if (rideWithLocs == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val ride = rideWithLocs.bikeRideEnt
    var notesState by remember { mutableStateOf(ride.notes.orEmpty()) }
    var isDirty    by remember { mutableStateOf(false) }
    val dateTimeFmt= remember { DateTimeFormatter.ofPattern("MMM dd, HH:mm") }
    val timeFmt    = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val startZoned = Instant.ofEpochMilli(ride.startTime)
        .atZone(ZoneId.systemDefault())
    val endZoned   = Instant.ofEpochMilli(ride.endTime)
        .atZone(ZoneId.systemDefault())
    val duration   = Duration.ofMillis(ride.endTime - ride.startTime)
    val minutes    = duration.toMinutes()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1) Top metrics row
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(label = "Distance", value = "%.1f km".format(ride.totalDistance / 1000f), modifier = Modifier.weight(1f))
            StatCard(label = "Avg Speed", value = "%.1f km/h".format(ride.averageSpeed), modifier = Modifier.weight(1f))
            StatCard(label = "Max Speed", value = "%.1f km/h".format(ride.maxSpeed), modifier = Modifier.weight(1f))
        }

        // 2) Single-line timestamp + duration
        Text(
            text = "${startZoned.format(dateTimeFmt)} — ${endZoned.format(timeFmt)}  ($minutes min)",
            style = MaterialTheme.typography.bodyMedium
        )

        // 3) Map in a rounded, elevated card
        if (!LocalInspectionMode.current) {
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

            //RidePathCard(path = path)
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                shape     = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                MapPathScreen(locations = path, placeName = "San Francisco")
                /*GoogleMap(
                    modifier             = Modifier.fillMaxSize(),//.matchParentSize(),
                    cameraPositionState  = cameraState,
                    uiSettings           = MapUiSettings(zoomControlsEnabled = false)
                ) {
                    if (path.size >= 2) {
                        Polyline(points = path, color = MaterialTheme.colorScheme.primary, width = 6f)
                        Marker(state = MarkerState(path.first()), title = "Start")
                        Marker(state = MarkerState(path.last()),  title = "End")
                    }
                }*/
            }
        }

        // 4) Bottom metrics row (Elevation / Duration / Calories)
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label    = "Elevation",
                value    = "${ride.elevationGain.toInt()}↑/${ride.elevationLoss.toInt()}↓ m",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label    = "Duration",
                value    = "%02d:%02d".format(minutes / 60, minutes % 60),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label    = "Calories",
                value    = "${ride.caloriesBurned} kcal",
                modifier = Modifier.weight(1f)
            )
        }

        // 5) Notes field using OutlinedTextField
        OutlinedTextField(
            value          = notesState,
            onValueChange  = {
                notesState = it
                isDirty    = it != ride.notes.orEmpty()
            },
            label          = { Text("Notes") },
            modifier       = Modifier.fillMaxWidth(),
            trailingIcon   = {
                if (isDirty) {
                    IconButton(onClick = {
                        onEvent(TripsEvent.UpdateRideNotes(ride.rideId, notesState))
                        isDirty = false
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save notes")
                    }
                }
            },
            /*colors = TextFieldDefaults.out(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )*/
        )

        // 6) Optional weather
        ride.weatherCondition?.let {
            Text("Weather: $it", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
fun RideDetailScreenPreview() {
    val sampleRide = BikeRideEntity(
        rideId = "sample-ride-id",
        startTime = System.currentTimeMillis() - 30 * 60 * 1000, // 30 minutes ago
        endTime = System.currentTimeMillis(),
        totalDistance = 10500f, // 10.5 km
        averageSpeed = 21.0f,
        maxSpeed = 35.5f,
        elevationGain = 150f,
        elevationLoss = 100f,
        caloriesBurned = 500,
        startLat = 37.7749,
        startLng = -122.4194,
        endLat = 37.7898,
        endLng = -122.4031,
        notes = "A nice ride through the city."
    )

    val sampleLocations = listOf(
        RideLocationEntity(
            rideId = sampleRide.rideId,
            timestamp = sampleRide.startTime,
            lat = sampleRide.startLat,
            lng = sampleRide.startLng
        ),
        RideLocationEntity(
            rideId = sampleRide.rideId,
            timestamp = sampleRide.startTime + 5 * 60 * 1000,
            lat = 37.7799,
            lng = -122.4150
        ),
        RideLocationEntity(
            rideId = sampleRide.rideId,
            timestamp = sampleRide.startTime + 10 * 60 * 1000,
            lat = 37.7845,
            lng = -122.4100
        ),
        RideLocationEntity(
            rideId = sampleRide.rideId,
            timestamp = sampleRide.endTime - 5 * 60 * 1000,
            lat = 37.7880,
            lng = -122.4050
        ),
        RideLocationEntity(
            rideId = sampleRide.rideId,
            timestamp = sampleRide.endTime,
            lat = sampleRide.endLat,
            lng = sampleRide.endLng
        )
    )

    val rideWithLocs = RideWithLocations(
        bikeRideEnt = sampleRide,
        locations = sampleLocations
    )

    RideDetailScreen(
        rideWithLocs = rideWithLocs,
        onEvent = {} // Dummy event handler
    )
}

@Preview
@Composable
fun StatCardPreview() {
    StatCard(
        label = "Distance",
        value = "10.5 km"
    )
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .height(80.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape     = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.titleMedium)
        }
    }
}
