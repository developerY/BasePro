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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.database.BikeProEntity
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
    item: BikeProEntity,
    onEvent: (TripsEvent) -> Unit,
    navTo: (String) -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val dateString = dateFormatter.format(Date(item.startTime))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navTo("details/${item.id}") },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = getPastelColor(item.id.hashCode())
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row with title and delete
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.id.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = { onEvent(TripsEvent.DeleteItem(item.id)) }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Date: $dateString",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Distance: ${item.totalDistance} km",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Avg Speed: ${item.averageSpeed} km/h",
                style = MaterialTheme.typography.bodySmall
            )

            if (item.caloriesBurned > 0) {
                Text(
                    text = "Calories: ${item.caloriesBurned}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
