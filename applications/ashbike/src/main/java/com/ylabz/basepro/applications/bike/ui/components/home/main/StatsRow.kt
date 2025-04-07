package com.ylabz.basepro.applications.bike.ui.components.home.main

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BatteryUnknown
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straight
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.applications.bike.ui.components.unused.StatCard


@Composable
fun StatsRow(
    distance: Double,
    duration: String,
    avgSpeed: Double,
    elevation: Double? = null, // optional metric
    modifier: Modifier = Modifier
) {
    // Build a list of stat items (icon, label, value)
    val stats = mutableListOf(
        StatItem(
            icon = Icons.Filled.Straight, //Straight,//DirectionsRun,
            label = "Distance",
            value = "${distance} km"
        ),
        StatItem(
            icon = Icons.Filled.Timer,
            label = "Duration",
            value = duration
        ),
        StatItem(
            icon = Icons.Filled.Speed,
            label = "Avg Speed",
            value = "${avgSpeed} km/h"
        )
    )
    // If elevation is provided, add it
    if (elevation != null) {
        stats.add(
            StatItem(
                icon = Icons.Filled.Terrain,
                label = "Elevation",
                value = "${elevation} m"
            )
        )
    }

    // Display the stats in a row, spaced evenly
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stats.forEach { stat ->

            // Create a horizontal gradient brush for the background
            val gradientBrush = Brush.horizontalGradient(
                colors = listOf(Color.Cyan, Color.Magenta)
            )

            //Column(modifier = Modifier.background(gradientBrush)) {
            StatCard(
                icon = stat.icon,
                label = stat.label,
                value = stat.value,
                modifier = Modifier.weight(1f, fill = false)
            )
            //}
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Preview
@Composable
fun StatsRowPreview() {
    StatsRow(
        distance = 10.5,
        duration = "00:30:00",
        avgSpeed = 21.0,
        elevation = 150.0
    )
}
data class StatItem(
    val icon: ImageVector,
    val label: String,
    val value: String
)
