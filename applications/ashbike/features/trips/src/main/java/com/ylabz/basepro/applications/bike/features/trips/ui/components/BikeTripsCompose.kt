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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.RideWithLocations
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent
import kotlin.random.Random


// —————————————————————————————————————————————————————————
//  PASTEL COLORS
// —————————————————————————————————————————————————————————
private val PastelLavender = Color(0x6DB6CFE1)
private val PastelBlue     = Color(0xFFDCEEFB)


/**
 * Generate a soft pastel color by picking:
 *   • random hue (0..360°)
 *   • low-to-mid saturation (0.2–0.4)
 *   • high lightness (0.85–0.95)
 */
fun randomPastelColor(): Color {
    val hue        = Random.nextFloat() * 360f
    val saturation = 0.2f + Random.nextFloat() * 0.2f
    val lightness  = 0.85f + Random.nextFloat() * 0.1f
    return Color.hsl(hue, saturation, lightness)
}
// —————————————————————————————————————————————————————————
//  SECTION HEADER (Rounded + Elevation)
// —————————————————————————————————————————————————————————
@Composable
fun SectionHeader(
    title: String,
    bgColor: Color
) {
    Surface(
        tonalElevation  = 4.dp,
        shadowElevation = 4.dp,
        shape           = RoundedCornerShape(8.dp),
        color           = bgColor,
        modifier        = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text  = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 12.dp)
        )
    }
}

// —————————————————————————————————————————————————————————
//  BIKE TRIPS SCREEN
// —————————————————————————————————————————————————————————
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeTripsCompose(
    modifier: Modifier = Modifier,
    bikeRides: List<RideWithLocations>,
    onEvent: (TripsEvent) -> Unit,
    navTo: (String) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        //containerColor = PastelLavender,
    ) {
        LazyColumn(
            modifier = Modifier
                //.padding(innerPadding)
                .fillMaxSize()
                .background(PastelLavender)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SectionHeader(title = "Bike Rides", bgColor = PastelBlue)
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
                    // each ride keeps its own pastel color
                    val bgColor = remember(rideWithLoc.bikeRideEnt.rideId) { randomPastelColor() }

                    BikeRideCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        backgroundColor = bgColor,        // pass it in, or wrap your Card in a Surface
                        ride = rideWithLoc.bikeRideEnt,
                        onEvent = onEvent,
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
    BikeTripsCompose(bikeRides = bikeRides, onEvent = {}, navTo = {})
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

@Composable
fun LoadingScreen() {
    Text(text = "Loading...", modifier = Modifier.fillMaxSize())
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
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
@Composable fun CapturedImagePreviewPreview() { CapturedImagePreview(imageUri = Uri.EMPTY) }
@Preview @Composable fun LoadingScreenPreview()   { LoadingScreen() }
@Preview @Composable fun ErrorScreenPreview()     { ErrorScreen("Preview Error") {} }
