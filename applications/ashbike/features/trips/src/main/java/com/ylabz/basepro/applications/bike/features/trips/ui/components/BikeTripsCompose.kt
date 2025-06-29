package com.ylabz.basepro.applications.bike.features.trips.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthEvent
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import kotlin.random.Random
import androidx.health.connect.client.records.Record
import kotlin.String

// —————————————————————————————————————————————————————————
//  BIKE TRIPS SCREEN
// —————————————————————————————————————————————————————————
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeTripsCompose(
    modifier: Modifier = Modifier,
    bikeRides: List<RideWithLocations>,
    syncedIds: Set<String>,
    bikeEvent: (TripsEvent) -> Unit,
    healthEvent: (HealthEvent) -> Unit,
    bikeToHealthConnectRecords: (BikeRideEntity) -> List<Record>,
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
                items(bikeRides, key = { it.bikeRideEnt.rideId }) { rideWithLoc ->
                    BikeRideCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        ride = rideWithLoc.bikeRideEnt,
                        syncedIds = syncedIds,
                        bikeEvent = bikeEvent,
                        healthEvent = healthEvent,
                        bikeToHealthConnectRecords = bikeToHealthConnectRecords,
                        navTo = navTo,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BikeTripsComposePreview() {
    val bikeRides = listOf(
        RideWithLocations(
            BikeRideEntity(
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis() + 3600000,
                totalDistance = 10000f,
                averageSpeed = 15f,
                maxSpeed = 30f,
                elevationGain = 100f,
                elevationLoss = 50f,
                caloriesBurned = 500,
                startLat = 40.7128,
                startLng = -74.0060,
                endLat = 40.7580,
                endLng = -73.9855
            ),
            listOf(
                RideLocationEntity(
                    rideId = "1",
                    timestamp = System.currentTimeMillis(),
                    lat = 40.7128,
                    lng = -74.0060
                ),
                RideLocationEntity(
                    rideId = "1",
                    timestamp = System.currentTimeMillis() + 1800000,
                    lat = 40.7354,
                    lng = -73.9980
                ),
                RideLocationEntity(
                    rideId = "1",
                    timestamp = System.currentTimeMillis() + 3600000,
                    lat = 40.7580,
                    lng = -73.9855
                )
            )
        )
    )
    BikeTripsCompose(
        bikeRides = bikeRides,
        bikeEvent = {},
        syncedIds = emptySet(),
        healthEvent = {},
        healthUiState = HealthUiState.Loading,
        bikeToHealthConnectRecords = { listOf() },
        navTo = {}
    )
}

// —————————————————————————————————————————————————————————
//  IMAGE PREVIEW, LOADING & ERROR SCREENS (unchanged)
// —————————————————————————————————————————————————————————
@Composable
fun CapturedImagePreview(imageUri: Uri) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(imageUri) {
        context.contentResolver.openInputStream(imageUri)?.use { stream ->
            bitmap = BitmapFactory.decodeStream(stream)
        }
    }

    bitmap?.let {
        Image(
            painter = BitmapPainter(it.asImageBitmap()),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            contentScale = ContentScale.Crop
        )
    }
}
