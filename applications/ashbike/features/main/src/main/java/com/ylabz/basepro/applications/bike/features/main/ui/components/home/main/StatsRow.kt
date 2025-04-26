package com.ylabz.basepro.applications.bike.features.main.ui.components.home.main

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straight
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.components.unused.StatCard
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState


data class StatItem(
    val icon: ImageVector,
    val tint: Color = Color.Gray,
    val label: String,
    val value: String
)

@Composable
fun StatsRow(
    bikeRideInfo: BikeRideInfo,
    modifier: Modifier = Modifier
) {
    val distance = bikeRideInfo.currentTripDistance
    val duration = bikeRideInfo.rideDuration
    val avgSpeed = bikeRideInfo.averageSpeed
    // elevation is non-null in BikeRideInfo, but you can choose to hide if zero
    val elevation: Double? = bikeRideInfo.elevation.takeIf { it > 0.0 }
    val rideState = bikeRideInfo.rideState

    // Build the list of stats
    val stats = mutableListOf(
        StatItem(
            icon = Icons.Filled.Straight,
            tint = if(rideState == RideState.Riding) Color(0xFF4FA252) else Gray,
            label = "Distance",
            value = "%.1f km".format(distance)
        ),
        StatItem(
            icon = Icons.Filled.Timer,
            tint = if(rideState == RideState.Riding) Color(0xFF4FA252) else Gray,
            label = "Duration",
            value = duration
        ),
        StatItem(
            icon = Icons.Filled.Speed,
            tint = if(rideState == RideState.Riding) Color(0xFF4FA252) else Gray,
            label = "Avg Speed",
            value = "%.1f km/h".format(avgSpeed)
        )
    ).apply {
        elevation?.let {
            add(
                StatItem(
                    icon = Icons.Filled.Terrain,
                    label = "Elevation",
                    value = "${"%.0f".format(it)} m"
                )
            )
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        stats.forEach { stat ->
            StatCard(
                icon = stat.icon,
                tint = stat.tint,
                label = stat.label,
                value = stat.value,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatsRowPreview() {
    val demoInfo = BikeRideInfo(
        // Core location & speeds
        location            = LatLng(37.4219999, -122.0862462),
        currentSpeed        = 0.0,
        averageSpeed        = 0.0,
        maxSpeed            = 0.0,

        // Distances (km)
        currentTripDistance = 0.0f,
        totalTripDistance   = null,
        remainingDistance   = null,

        // Elevation (m)
        elevationGain       = 0.0,
        elevationLoss       = 0.0,

        // Calories
        caloriesBurned      = 0,

        // UI state
        rideDuration        = "00:00",
        settings            = mapOf(
            "Theme" to listOf("Light", "Dark", "System Default"),
            "Language" to listOf("English", "Spanish", "French"),
            "Notifications" to listOf("Enabled", "Disabled")
        ),
        heading             = 0f,
        elevation           = 0.0,

        // Bike connectivity
        isBikeConnected     = false,
        batteryLevel        = null,
        motorPower          = null,

        // rideState & weatherInfo use their defaults
    )
    StatsRow(bikeRideInfo = demoInfo)
}

