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
import androidx.compose.foundation.layout.width
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.ylabz.basepro.applications.bike.database.BikeRideEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BikeRideCard(
    ride: BikeRideEntity,
    onEvent: (TripsEvent) -> Unit,
    navTo: (String) -> Unit
) {
    // Date formatter for start/end times
    val dateFormatter = remember {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navTo(ride.rideId) },
        shape = RoundedCornerShape(8.dp),
        // elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: start – end
            val startText = dateFormatter.format(Date(ride.startTime))
            val endText   = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(ride.endTime))
            Text(
                text = "$startText – $endText",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            // Key metrics
            Text(
                text = "Distance: ${"%.1f".format(ride.totalDistance / 1000)} km",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Avg: ${"%.1f".format(ride.averageSpeed)} km/h",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Max: ${"%.1f".format(ride.maxSpeed)} km/h",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            // Optional context row
            Row(verticalAlignment = Alignment.CenterVertically) {
                ride.rideType?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.width(8.dp))
                ride.weatherCondition?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
            }

            // Optional notes
            ride.notes?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
