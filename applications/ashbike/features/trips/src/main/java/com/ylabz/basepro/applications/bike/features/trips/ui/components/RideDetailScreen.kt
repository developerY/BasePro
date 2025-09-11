package com.ylabz.basepro.applications.bike.features.trips.ui.components


////import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import com.ylabz.basepro.applications.bike.features.core.ui.components.RidePathMap
import com.ylabz.basepro.applications.bike.features.trips.R
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent
import com.ylabz.basepro.applications.bike.features.trips.ui.components.maps.lookupPlaceName
import com.ylabz.basepro.core.model.location.GpsFix
import com.ylabz.basepro.core.model.yelp.BusinessInfo
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class LocationDto(val lat: Double, val lng: Double)

// --- add this to your TripsEvent sealed class: ---
// data class UpdateNotes(val rideId: String, val notes: String): TripsEvent()

data class LatLngWithElev(
    val latLng: LatLng,
    val elevation: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideDetailScreen(
    modifier: Modifier = Modifier,
    rideWithLocs: RideWithLocations?,
    coffeeShops: List<BusinessInfo> = emptyList(),
    onFindCafes: () -> Unit = {},
    onEvent: (TripsEvent) -> Unit,
) {
    if (rideWithLocs == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val ctx = LocalContext.current
    val ride = rideWithLocs.bikeRideEnt
    var notesState by remember { mutableStateOf(ride.notes.orEmpty()) }
    var isDirty by remember { mutableStateOf(false) }
    val dateTimeFmt = remember { DateTimeFormatter.ofPattern("MMM dd, HH:mm") }
    val timeFmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val startZoned = Instant.ofEpochMilli(ride.startTime)
        .atZone(ZoneId.systemDefault())
    val endZoned = Instant.ofEpochMilli(ride.endTime)
        .atZone(ZoneId.systemDefault())
    val duration = Duration.ofMillis(ride.endTime - ride.startTime)
    val minutes = duration.toMinutes()

    var placeName by remember { mutableStateOf<String?>(null) }

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
            StatCard(
                label = stringResource(R.string.feature_trips_detail_label_distance),
                value = stringResource(
                    R.string.feature_trips_distance_km_format,
                    ride.totalDistance / 1000f
                ),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.feature_trips_detail_label_avg_speed),
                value = stringResource(
                    R.string.feature_trips_detail_value_kmh_format,
                    ride.averageSpeed
                ),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.feature_trips_detail_label_max_speed),
                value = stringResource(
                    R.string.feature_trips_detail_value_kmh_format,
                    ride.maxSpeed
                ),
                modifier = Modifier.weight(1f)
            )
        }

        // 2) Single-line timestamp + duration
        Text(
            text = stringResource(
                R.string.feature_trips_detail_full_timespan_format,
                startZoned.format(dateTimeFmt),
                endZoned.format(timeFmt),
                minutes.toLong()
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        val path = remember(rideWithLocs.locations) {
            rideWithLocs.locations.map { LatLng(it.lat, it.lng) }
        }

        LaunchedEffect(path.firstOrNull()) {
            path.firstOrNull()?.let {
                placeName = lookupPlaceName(ctx, it.latitude, it.longitude)
            }
        }


        // new: pair each LatLng with its elevation (default 0f if null)
        val elevPoints = remember(rideWithLocs.locations) {
            rideWithLocs.locations.map { locEnt ->
                LatLngWithElev(
                    latLng = LatLng(locEnt.lat, locEnt.lng),
                    elevation = locEnt.elevation ?: 0f,
                )
            }
        }

        val fixes: List<GpsFix> = rideWithLocs.locations.map { ent ->
            GpsFix(
                lat = ent.lat,
                lng = ent.lng,
                timeMs = ent.timestamp,
                altitude = ent.elevation?.toDouble() ?: 0.0,
                speed = 0f,
                accuracy = 0f
            )
        }

        // 3) Map in a rounded, elevated card
        //if (!LocalInspectionMode.current) {


        //RidePathCard(path = path)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            /*MapPathScreen(
                fixes = fixes,
                placeName = placeName ?: "",
                coffeeShops = coffeeShops,
                onFindCafes = onFindCafes
            )*/

            RidePathMap(
                fixes = fixes,
                placeName = placeName ?: "",
                coffeeShops = coffeeShops,
                onFindCafes = onFindCafes
            )

            //locations = path, placeName = placeName?: "")
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
        //} 

        ElevationProfileSection(elevPoints)

        // 4) Bottom metrics row (Elevation / Duration / Calories)
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = stringResource(R.string.feature_trips_detail_label_elevation),
                value = stringResource(
                    R.string.feature_trips_detail_value_elevation_format,
                    ride.elevationGain.toInt(),
                    ride.elevationLoss.toInt()
                ),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.feature_trips_detail_label_duration),
                value = stringResource(
                    R.string.feature_trips_detail_value_duration_format,
                    minutes / 60,
                    minutes % 60
                ),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = stringResource(R.string.feature_trips_detail_label_calories),
                value = stringResource(
                    R.string.feature_trips_detail_value_calories_format,
                    ride.caloriesBurned
                ),
                modifier = Modifier.weight(1f)
            )
        }

        // 5) Notes field using OutlinedTextField
        OutlinedTextField(
            value = notesState,
            onValueChange = {
                notesState = it
                isDirty = it != ride.notes.orEmpty()
            },
            label = { Text(stringResource(R.string.feature_trips_detail_label_notes)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (isDirty) {
                    IconButton(onClick = {
                        onEvent(TripsEvent.UpdateRideNotes(ride.rideId, notesState))
                        isDirty = false
                    }) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = stringResource(R.string.feature_trips_detail_cd_save_notes)
                        )
                    }
                }
            },
            /*colors = TextFieldDefaults.out(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )*/
        )

        // 6) Optional weather
        ride.weatherCondition?.let {
            Text(
                stringResource(R.string.feature_trips_detail_weather_format, it),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/*
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
*/
@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(8.dp),
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
