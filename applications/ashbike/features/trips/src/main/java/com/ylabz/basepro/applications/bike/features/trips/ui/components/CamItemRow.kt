package com.ylabz.basepro.applications.bike.features.trips.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import com.ylabz.basepro.applications.bike.database.BikeProEntity
import com.ylabz.basepro.applications.bike.features.trips.ui.TripsEvent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun CamItemRow(
    item: BikeProEntity,
    onEvent: (TripsEvent) -> Unit,
    navTo: (String) -> Unit
) {
    // Format the start time to a more readable date string.
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top row: Title and delete icon.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.id.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { onEvent(TripsEvent.DeleteItem(item.id)) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Item"
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Date row
            Text(
                text = dateString,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Distance (and any other metric you want to show).
            Text(
                text = "Distance: ${item.totalDistance} km",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * Utility function that returns a pastel color based on the seed value.
 */
fun getPastelColorOne(seed: Int): Color {
    val pastelColors = listOf(
        Color(0xFFFFC1CC), // pastel pink
        Color(0xFFFFE5B4), // pastel yellow
        Color(0xFFB2F2BB), // pastel green
        Color(0xFFC0E9FF)  // pastel blue
    )
    return pastelColors[abs(seed) % pastelColors.size]
}

// Helper function to get a pastel color based on the index
@Composable
fun getPastelColor(index: Int): Color {
    val pastelColors = listOf(
        Color(0xFFFFF0F5), // LavenderBlush
        Color(0xFFF0FFF0), // Honeydew
        Color(0xFFFFF5E6), // Seashell
        Color(0xFFE0FFFF), // LightCyan
        Color(0xFFFFE4E1)  // MistyRose
    )
    return pastelColors[index % pastelColors.size]
}
