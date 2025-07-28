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
import com.ylabz.basepro.core.model.bike.BikeRideInfo // Keep for Preview's dummy data
import com.ylabz.basepro.core.ui.theme.iconColorAvgSpeed
import com.ylabz.basepro.core.ui.theme.iconColorDistance
import com.ylabz.basepro.core.ui.theme.iconColorDuration
import com.ylabz.basepro.core.ui.theme.iconColorElevation
import androidx.compose.ui.res.stringResource
import com.ylabz.basepro.applications.bike.features.main.R

// StatItem data class remains the same
data class StatItem(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val activeColor: Color? = null
)

// Define the UiState for StatsRow
data class StatsRowUiState(
    val currentTripDistance: Float,
    val rideDuration: String,
    val averageSpeed: Double,
    val elevation: Double,
    val isBikeComputerOn: Boolean,
    val cardColor: Color,
    val contentColor: Color // This will be the base content color
)

// Define the Events for StatsRow (currently none)
sealed interface StatsRowEvent {
    // No events defined for StatsRow yet
}

@Composable
fun StatsRow(
    modifier: Modifier = Modifier,
    uiState: StatsRowUiState,
    onEvent: (StatsRowEvent) -> Unit // Included for architectural consistency, though unused for now
) {
    val distance = uiState.currentTripDistance
    val duration = uiState.rideDuration
    val avgSpeed = uiState.averageSpeed
    val elevationValue: Double? = uiState.elevation.takeIf { it > 0.0 }

    val stats = mutableListOf(
        StatItem(
            icon = Icons.Filled.Straight,
            label = stringResource(R.string.feature_main_stats_label_distance),
            value = stringResource(R.string.feature_main_stats_value_km_format, distance),
            activeColor = if (uiState.isBikeComputerOn) MaterialTheme.colorScheme.iconColorDistance else null
        ),
        StatItem(
            icon = Icons.Filled.Timer,
            label = stringResource(R.string.feature_main_stats_label_duration),
            value = duration,
            activeColor = if (uiState.isBikeComputerOn) MaterialTheme.colorScheme.iconColorDuration else null
        ),
        StatItem(
            icon = Icons.Filled.Speed,
            label = stringResource(R.string.feature_main_stats_label_avg_speed),
            value = stringResource(R.string.feature_main_stats_value_kmh_format, avgSpeed),
            activeColor = if (uiState.isBikeComputerOn) MaterialTheme.colorScheme.iconColorAvgSpeed else null
        )
    ).apply {
        elevationValue?.let {
            add(
                StatItem(
                    icon = Icons.Filled.Terrain,
                    label = stringResource(R.string.feature_main_stats_label_elevation),
                    value = stringResource(R.string.feature_main_stats_value_meters_format, it),
                    activeColor = if (uiState.isBikeComputerOn) MaterialTheme.colorScheme.iconColorElevation else null
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
                cardColor = uiState.cardColor,
                tint = stat.activeColor ?: uiState.contentColor, // Use activeColor or fallback to base contentColor from uiState
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
    val demoInfo = BikeRideInfo( // Still using BikeRideInfo to create dummy data for the preview
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
        settings            = mapOf(),
        heading             = 0f,
        elevation           = 25.0,
        isBikeConnected     = false, // Not directly used by StatsRowUiState, but influences isBikeComputerOn
        batteryLevel        = null,
        motorPower          = null
    )

    val previewUiState = StatsRowUiState(
        currentTripDistance = demoInfo.currentTripDistance,
        rideDuration = demoInfo.rideDuration,
        averageSpeed = demoInfo.averageSpeed,
        elevation = demoInfo.elevation,
        isBikeComputerOn = true, // Preview with active colors
        cardColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    )

    StatsRow(
        uiState = previewUiState,
        onEvent = {}
    )
}

@Preview(showBackground = true)
@Composable
fun StatsRowPreviewOff() {
    val demoInfo = BikeRideInfo(
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
        settings            = mapOf(),
        heading             = 0f,
        elevation           = 0.0, 
        isBikeConnected     = false,
        batteryLevel        = null,
        motorPower          = null
    )

    val previewUiStateOff = StatsRowUiState(
        currentTripDistance = demoInfo.currentTripDistance,
        rideDuration = demoInfo.rideDuration,
        averageSpeed = demoInfo.averageSpeed,
        elevation = demoInfo.elevation,
        isBikeComputerOn = false, // Preview with inactive colors
        cardColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant // Or a more muted color for off state
    )

    StatsRow(
        uiState = previewUiStateOff,
        onEvent = {}
    )
}
