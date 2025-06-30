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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.ui.theme.iconColorAvgSpeed
import com.ylabz.basepro.core.ui.theme.iconColorDistance
import com.ylabz.basepro.core.ui.theme.iconColorDuration
import com.ylabz.basepro.core.ui.theme.iconColorElevation

data class StatItem(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val activeColor: Color? = null // New property for active state color
)

@Composable
fun StatsRow(
    bikeRideInfo: BikeRideInfo,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color, // Default content color when not active
    isBikeComputerOn: Boolean // To determine which color to use
) {
    val distance = bikeRideInfo.currentTripDistance
    val duration = bikeRideInfo.rideDuration
    val avgSpeed = bikeRideInfo.averageSpeed
    val elevation: Double? = bikeRideInfo.elevation.takeIf { it > 0.0 }
    val isBikeConnected = bikeRideInfo.isBikeConnected

    val stats = mutableListOf(
        StatItem(
            icon = Icons.Filled.Straight,
            label = "Distance",
            value = "%.1f km".format(distance),
            activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorDistance else null
        ),
        StatItem(
            icon = Icons.Filled.Timer,
            label = "Duration",
            value = duration,
            activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorDuration else null
        ),
        StatItem(
            icon = Icons.Filled.Speed,
            label = "Avg Speed",
            value = "%.1f km/h".format(avgSpeed),
            activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorAvgSpeed else null
        )
    ).apply {
        elevation?.let {
            add(
                StatItem(
                    icon = Icons.Filled.Terrain,
                    label = "Elevation",
                    value = "${"%.0f".format(it)} m",
                    activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorElevation else null
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
                cardColor = cardColor,
                // Use activeColor if available and bike computer is on, otherwise default contentColor
                tint = stat.activeColor ?: contentColor,
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
        location            = LatLng(37.4219999, -122.0862462),
        currentSpeed        = 0.0,
        averageSpeed        = 0.0,
        maxSpeed            = 0.0,
        currentTripDistance = 0.0f,
        totalTripDistance   = null,
        remainingDistance   = null,
        elevationGain       = 0.0,
        elevationLoss       = 0.0,
        caloriesBurned      = 0,
        rideDuration        = "00:00",
        settings            = mapOf(),
        heading             = 0f,
        elevation           = 0.0,
        isBikeConnected     = false,
        batteryLevel        = null,
        motorPower          = null
    )
    StatsRow(
        bikeRideInfo = demoInfo,
        contentColor = MaterialTheme.colorScheme.onSurface,
        isBikeComputerOn = true // Preview with active colors
    )
}

@Preview(showBackground = true)
@Composable
fun StatsRowPreviewOff() {
    val demoInfo = BikeRideInfo(
        location            = LatLng(37.4219999, -122.0862462),
        currentSpeed        = 0.0,
        averageSpeed        = 0.0,
        maxSpeed            = 0.0,
        currentTripDistance = 0.0f,
        totalTripDistance   = null,
        remainingDistance   = null,
        elevationGain       = 0.0,
        elevationLoss       = 0.0,
        caloriesBurned      = 0,
        rideDuration        = "00:00",
        settings            = mapOf(),
        heading             = 0f,
        elevation           = 0.0,
        isBikeConnected     = false,
        batteryLevel        = null,
        motorPower          = null
    )
    StatsRow(
        bikeRideInfo = demoInfo,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant, // Or a more muted color for off state
        isBikeComputerOn = false // Preview with inactive colors
    )
}
