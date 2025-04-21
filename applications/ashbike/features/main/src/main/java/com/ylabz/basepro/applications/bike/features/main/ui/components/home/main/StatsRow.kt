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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.applications.bike.features.main.ui.components.unused.StatCard
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState


data class StatItem(
    val icon: ImageVector,
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

    // Build the list of stats
    val stats = mutableListOf(
        StatItem(
            icon = Icons.Filled.Straight,
            label = "Distance",
            value = "%.1f km".format(distance)
        ),
        StatItem(
            icon = Icons.Filled.Timer,
            label = "Duration",
            value = duration
        ),
        StatItem(
            icon = Icons.Filled.Speed,
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
        location = null,
        currentSpeed = 0.0,
        averageSpeed = 21.0,
        currentTripDistance = 10.5f,
        totalTripDistance = null,
        remainingDistance = null,
        rideDuration = "00:30:00",
        settings = emptyMap(),
        heading = 0f,
        elevation = 150.0,
        isBikeConnected = false,
        batteryLevel = null,
        motorPower = null,
        rideState = RideState.NotStarted
    )
    StatsRow(bikeRideInfo = demoInfo)
}

