package com.ylabz.basepro.applications.bike.features.trips.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent

import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.mapper.BikeRide
import java.time.LocalDateTime.now


@Composable
fun BikeTripsCompose(
    modifier: Modifier = Modifier,
    bikeRides:  List<BikeRide>,
    onEvent:    (TripsEvent) -> Unit,
    navTo:      (String) -> Unit
) {
    var newItemName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header + Delete All
        Row(
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier            = Modifier.fillMaxWidth()
        ) {
            Text("List of Items", style = MaterialTheme.typography.titleLarge)
            Button(
                onClick = { onEvent(TripsEvent.DeleteAll) },
                colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete All!")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Single LazyColumn, two sections
        LazyColumn(
            modifier           = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // --- Section 1: BikePro --- REMOVED
            // --- Section Separator ---
            item {
                Spacer(Modifier.height(16.dp))
            }

            // --- Section 2: BikeRides ---
            item {
                Text(
                    text  = "Bike Rides",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(bikeRides) { ride ->
                // replace with your Ride‑specific card
                BikeRideCard(ride = ride, onEvent = onEvent, navTo = navTo)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Inputs for adding new items + rides
        Row(
            modifier           = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value       = newItemName,
                onValueChange = { newItemName = it },
                label       = { Text("New Item") },
                modifier    = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    onEvent(TripsEvent.AddBikeRide)
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("New Ride")
            }
        }
    }
}



@Composable
fun CapturedImagePreview(imageUri: Uri) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Load the image from the file
    LaunchedEffect(imageUri) {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        bitmap = BitmapFactory.decodeStream(inputStream)
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

@Preview(showBackground = true)
@Composable
fun BikeTripsComposePreview() {
    // ——— Sample BikeRide data ———
    val sampleBikeRides = listOf(
        BikeRideEntity(
            // Core Information
            startTime = System.currentTimeMillis() - 3_600_000L,  // 1 hr ago
            endTime = System.currentTimeMillis(),
            totalDistance = 10_000f,           // 10 km
            averageSpeed = 2.8f,              // m/s (~10 km/h)
            maxSpeed = 5.0f,              // m/s (~18 km/h)

            // Elevation & Calories
            elevationGain = 80f,
            elevationLoss = 75f,
            caloriesBurned = 400,

            // Health Connect (optional)
            avgHeartRate = 120,
            maxHeartRate = 150,
            healthConnectRecordId = null,
            isHealthDataSynced = false,

            // Context
            weatherCondition = "Sunny, 20°C",
            rideType = "Road",

            // Feedback
            notes = "Morning loop in the park",
            rating = 4,
            isSynced = false,

            // Bike & Battery
            bikeId = "RB‑01",
            batteryStart = 100,
            batteryEnd = 95,

            // Quick coords
            startLat = 37.7749,
            startLng = -122.4194,
            endLat = 37.7849,
            endLng = -122.4094
        ),
        BikeRide(
            startTime = System.currentTimeMillis() - 7_200_000L,  // 2 hrs ago
            endTime = System.currentTimeMillis() - 3_600_000L,  // 1 hr ago
            totalDistance = 8_000f,            // 8 km
            averageSpeed = 2.2f,              // m/s (~8 km/h)
            maxSpeed = 4.5f,              // m/s (~16 km/h)

            elevationGain = 50f,
            elevationLoss = 45f,
            caloriesBurned = 350,

            avgHeartRate = 115,
            maxHeartRate = 140,
            healthConnectRecordId = null,
            isHealthDataSynced = false,

            weatherCondition = "Cloudy, 18°C",
            rideType = "Mountain",

            notes = "Trail ride",
            rating = 5,
            isSynced = false,

            bikeId = "MTB‑02",
            batteryStart = 100,
            batteryEnd = 90,

            startLat = 34.0522,
            startLng = -118.2437,
            endLat = 34.0622,
            endLng = -118.2537
        )
    )
    BikeTripsCompose(
        bikeRides = sampleBikeRides,
        onEvent = { },
        navTo = { }
    )
}

@Preview(showBackground = true)
@Composable
fun CapturedImagePreviewPreview() {
    CapturedImagePreview(imageUri = Uri.EMPTY) // Provide a valid Uri for a real preview
}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    ErrorScreen(errorMessage = "Failed to load data") {
        // Retry action for preview
    }
}

// These will be move to a common directory.
@Composable
fun LoadingScreen() {
    Text(text = "Loading...", modifier = Modifier.fillMaxSize())
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Error: $errorMessage",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Retry",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clickable { onRetry() }
                .padding(vertical = 8.dp)
        )
    }
}
