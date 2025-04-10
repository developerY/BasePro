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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Displays a single bike trip as a beautiful card.
 *
 * @param bikeTrip the bike trip item to display.
 * @param onItemClicked callback invoked when the user taps the card.
 * @param modifier for styling or further customization.
 */
@Composable
fun BikeTripCard(
    bikeTrip: BikeProEntity,
    onItemClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Format the timestamp into a readable date.
    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val dateString = dateFormatter.format(Date(bikeTrip.startTime))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClicked(bikeTrip.id) }, // Use your unique ID field
        //elevation = 8.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title
            Text(
                text = bikeTrip.id.toString(),
                //style = MaterialTheme.typography.bodyLarge,
                //color = MaterialTheme.colorScheme
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Date and Distance row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${bikeTrip.totalDistance} km",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Additional details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Avg: ${bikeTrip.averageSpeed} km/h",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Calories: ${bikeTrip.caloriesBurned}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}