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
import com.ylabz.basepro.applications.bike.database.BikeProEntity


@Composable
fun BikeTripsCompose(
    modifier: Modifier = Modifier,
    data: List<BikeProEntity>,
    onEvent: (TripsEvent) -> Unit,
    navTo: (String) -> Unit
) {
    var newItemName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "List of Items",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { onEvent(TripsEvent.DeleteAll) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Delete All!")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable list of items inside LazyColumn
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(data) { item ->
                //BikeTripCard(bikeTrip = item, )
                Column {
                    BikeTripCard(item = item, onEvent = onEvent, navTo = navTo)
                    CamItemRow(item = item, onEvent = onEvent, navTo = navTo)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input for new item
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newItemName,
                onValueChange = { newItemName = it },
                label = { Text("New Item") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if (newItemName.isNotBlank()) {
                        onEvent(TripsEvent.AddItem(newItemName))
                        newItemName = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Add")
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
    val sampleData = listOf(
        BikeProEntity(
            startTime = System.currentTimeMillis() - 3600000,
            endTime = System.currentTimeMillis(),
            totalDistance = 15000f,
            averageSpeed = 25f,
            maxSpeed = 40f,
            elevationGain = 200f,
            elevationLoss = 180f,
            caloriesBurned = 500,
            startLat = 37.7749,
            startLng = -122.4194,
            endLat = 37.7739,
            endLng = -122.4312
        ),
        BikeProEntity(
            startTime = System.currentTimeMillis() - 7200000,
            endTime = System.currentTimeMillis() - 3600000,
            totalDistance = 20000f,
            averageSpeed = 30f,
            maxSpeed = 45f,
            elevationGain = 300f,
            elevationLoss = 250f,
            caloriesBurned = 700,
            startLat = 34.0522,
            startLng = -118.2437,
            endLat = 34.0622,
            endLng = -118.2537
        )
    )
    BikeTripsCompose(
        data = sampleData,
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
