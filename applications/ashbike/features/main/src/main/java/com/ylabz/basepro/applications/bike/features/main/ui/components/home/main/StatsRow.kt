package com.ylabz.basepro.applications.bike.features.main.ui.components.home.main

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straight
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.R
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.ui.theme.iconColorAvgSpeed
import com.ylabz.basepro.core.ui.theme.iconColorDistance
import com.ylabz.basepro.core.ui.theme.iconColorDuration
import com.ylabz.basepro.core.ui.theme.iconColorElevation

// StatItem data class remains the same
data class StatItem(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val activeColor: Color? = null
)

// StatsRowEvent is no longer needed here if StatsRow takes BikeUiState and doesn't emit its own specific events
// sealed interface StatsRowEvent {
    // No events defined for StatsRow yet
// }

@Composable
fun StatsRow(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success, // Changed from StatsRowUiState
    //onEvent: (BikeDashboardEvent) -> Unit // Changed to BikeDashboardEvent, or a more general event type if needed
) {
    val bikeData = uiState.bikeData
    val isBikeComputerOn = bikeData.rideState == RideState.Riding
    val cardColor = if (isBikeComputerOn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isBikeComputerOn) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    val distance = bikeData.currentTripDistance
    val duration = bikeData.rideDuration
    val avgSpeed = bikeData.averageSpeed
    val elevationValue: Double? = bikeData.elevation.takeIf { it > 0.0 }

    val stats = mutableListOf(
        StatItem(
            icon = Icons.Filled.Straight,
            label = stringResource(R.string.feature_main_stats_label_distance),
            value = stringResource(R.string.feature_main_stats_value_km_format, distance),
            activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorDistance else null
        ),
        StatItem(
            icon = Icons.Filled.Timer,
            label = stringResource(R.string.feature_main_stats_label_duration),
            value = duration,
            activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorDuration else null
        ),
        StatItem(
            icon = Icons.Filled.Speed,
            label = stringResource(R.string.feature_main_stats_label_avg_speed),
            value = stringResource(R.string.feature_main_stats_value_kmh_format, avgSpeed),
            activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorAvgSpeed else null
        )
    ).apply {
        elevationValue?.let {
            add(
                StatItem(
                    icon = Icons.Filled.Terrain,
                    label = stringResource(R.string.feature_main_stats_label_elevation),
                    value = stringResource(R.string.feature_main_stats_value_meters_format, it),
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
                cardColor = cardColor, // Use derived cardColor
                tint = stat.activeColor ?: contentColor, // Use activeColor or fallback to derived contentColor
                label = stat.label,
                value = stat.value,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun StatsRowPreview() {
    val demoInfoOn = BikeRideInfo(
        location = LatLng(37.4219999, -122.0862462),
        currentSpeed = 0.0,
        averageSpeed = 18.5,
        maxSpeed = 35.2,
        currentTripDistance = 12.3f,
        totalTripDistance   = null,
        remainingDistance   = null,
        elevationGain       = 123.0,
        elevationLoss       = 0.0,
        caloriesBurned      = 0,
        rideDuration        = "01:15:30",
        settings            = persistentMapOf(),
        heading             = 0f,
        elevation           = 25.0,
        isBikeConnected     = true,
        batteryLevel        = 80,
        motorPower          = 100f,
        rideState           = RideState.Riding, // Simulates BikeComputerOn
        heartbeat           = null,
    )

    val previewUiStateOn = BikeUiState.Success(demoInfoOn)

    StatsRow(
        uiState = previewUiStateOn,
        onEvent = {}
    )
}
*/

/*
@Preview(showBackground = true)
@Composable
fun StatsRowPreviewOff() {
    val demoInfoOff = BikeRideInfo(
        location            = LatLng(37.4219999, -122.0862462),
        currentSpeed        = 0.0,
        averageSpeed        = 18.5,
        maxSpeed            = 35.2,
        currentTripDistance = 12.3f,
        totalTripDistance   = null,
        remainingDistance   = null,
        elevationGain       = 123.0,
        elevationLoss       = 0.0,
        caloriesBurned      = 0,
        rideDuration        = "01:15:30",
        settings            = persistentMapOf(),
        heading             = 0f,
        elevation           = 0.0,
        isBikeConnected     = false,
        batteryLevel        = null,
        motorPower          = null,
        heartbeat           = null,
    )

     val previewUiStateOff = BikeUiState.Success(demoInfoOff)

    StatsRow(
        uiState = previewUiStateOff,
        onEvent = {}
    )
}

// Dummy BikeDashboardEvent for preview, replace with your actual event if different
sealed interface BikeDashboardEvent {
    // Define actual events if needed by StatsRow or its children for the preview
}
*/