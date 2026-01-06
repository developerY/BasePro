package com.ylabz.basepro.ashbike.wear.presentation.screens.ride

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.ylabz.basepro.core.model.bike.BikeRideInfo

// --- PAGE 1: The New Stats Grid ---
@Composable
fun StatsGridPage(info: BikeRideInfo) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // Padding to avoid round edges clipping
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ROW 1: Avg Speed & Elevation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GridStatItem(
                label = "AVG SPD",
                // Ensure BikeRideInfo has 'avgSpeed', or remove this line
                value = String.format("%.1f", info.averageSpeed ?: 0.0),
                unit = "km/h"
            )
            GridStatItem(
                label = "ELEV",
                // Ensure BikeRideInfo has 'elevation', or remove this line
                value = "${info.elevation ?: 0}",
                unit = "m"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ROW 2: Calories & Duration
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GridStatItem(
                label = "CAL",
                // Ensure BikeRideInfo has 'calories', or remove this line
                value = "${info.caloriesBurned ?: 0}",
                unit = "kcal"
            )
            GridStatItem(
                label = "TIME",
                value = formatDuration(info.rideDuration.toLong() ?: 0L),
                unit = "min"
            )
        }
    }
}

// Helper for the Grid Items
@Composable
fun GridStatItem(label: String, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

// Simple Helper: Seconds -> MM:SS
fun formatDuration(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}