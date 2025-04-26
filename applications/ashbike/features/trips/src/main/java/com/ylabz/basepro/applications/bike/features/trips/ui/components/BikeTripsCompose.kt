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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent


@Composable
fun BikeTripsCompose(
    modifier: Modifier = Modifier,
    bikeRides: List<RideWithLocations>,        // now the full ride+points
    onEvent: (TripsEvent) -> Unit,
    navTo: (String) -> Unit
) {
    var newItemName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- Header + Delete All ---
        Row(
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier             = Modifier.fillMaxWidth()
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

        // --- The two‐section LazyColumn ---
        LazyColumn(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // (Section 1 removed)

            item {
                Spacer(Modifier.height(16.dp))
            }

            // --- Section 2: Bike Rides ---
            item {
                Text(
                    text  = "Bike Rides",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(bikeRides) { ride ->
                // replace with your Ride‑specific card
                BikeRideCard(ride = ride.bikeRideEnt, onEvent = onEvent, navTo = navTo)
            }
        }
        Spacer(Modifier.height(16.dp))
        // --- New‐Ride Input / Button ---
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
    BikeTripsCompose(bikeRides = bikeRides, onEvent = {}, navTo = {})
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

@Preview
@Composable
fun CapturedImagePreviewPreview() {
    // Create a dummy bitmap
    val dummyBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    dummyBitmap.eraseColor(Color.Blue.toArgb())
    CapturedImagePreview(imageUri = Uri.EMPTY)
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


@Preview
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}

@Preview
@Composable
fun ErrorScreenPreview() {
    ErrorScreen("Preview Error") {}
}
