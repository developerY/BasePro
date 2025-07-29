package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BatteryUnknown
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.R // Ensure this is your correct R file
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent // Assuming onEvent is of this type
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatCard
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatItem
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.ui.theme.AshBikeTheme
import com.ylabz.basepro.core.ui.theme.iconColorBikeActive // Example, adjust as needed
import com.ylabz.basepro.core.ui.theme.iconColorCalories // Example, adjust as needed
import com.ylabz.basepro.core.ui.theme.iconColorHeartRate // Example, adjust as needed
import com.ylabz.basepro.core.ui.theme.iconColorSpeed
import java.util.Locale

enum class StatsSectionType {
    HEALTH, EBIKE
}

@Composable
fun StatsSection(
    uiState: BikeUiState.Success,
    sectionType: StatsSectionType,
    onEvent: (BikeEvent) -> Unit, // Assuming onEvent is needed and of this type
    modifier: Modifier = Modifier
) {
    val bikeData = uiState.bikeData
    val context = LocalContext.current // For string resources with formatting

    val isBikeComputerOn = bikeData.rideState == RideState.Riding
    val currentCardColor = if (isBikeComputerOn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val currentContentColor = if (isBikeComputerOn) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    val currentStats: List<StatItem> = when (sectionType) {
        StatsSectionType.HEALTH -> listOfNotNull(
            bikeData.heartbeat?.takeIf { it > 0 }?.let { hr ->
                StatItem(
                    icon = Icons.Filled.Favorite,
                    label = stringResource(R.string.feature_main_label_heart_rate), // Replace with actual R.string ID
                    value = if (bikeData.heartbeat != null) "$bikeData.heartbeat bpm" else "-- bpm",
                    //value = String.format(Locale.getDefault(), stringResource(R.string.feature_main_label_heart_rate), hr.toDouble()), // e.g. "%.0f bpm"
                    activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorHeartRate else null // Or your specific theme color
                )
            },
            bikeData.caloriesBurned.takeIf { it > 0 }?.let { calories ->
                StatItem(
                    icon = Icons.Filled.LocalFireDepartment,
                    label = stringResource(R.string.feature_main_label_calories), // Replace with actual R.string ID
                    value = calories.toString(),
                    activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorCalories else null // Or your specific theme color
                )
            }
        )
        StatsSectionType.EBIKE -> listOfNotNull(
            bikeData.batteryLevel?.let { battery ->
                StatItem(
                    icon = Icons.AutoMirrored.Filled.BatteryUnknown,
                    label = stringResource(R.string.feature_main_label_battery), // Replace with actual R.string ID
                    value = if (bikeData.isBikeConnected && bikeData.batteryLevel != null) "$bikeData.batteryLevel%" else "--%",
                    // value = String.format(Locale.getDefault(), stringResource(R.string.feature_main_value_percentage_format), battery), // e.g., "%d %%"
                    activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.primary else null // Or your specific theme color
                )
            },
            bikeData.motorPower?.takeIf { it > 0f }?.let { power ->
                StatItem(
                    icon = Icons.Filled.ElectricBike,
                    label = stringResource(R.string.feature_main_label_motor), // Replace with actual R.string ID
                    value = if (bikeData.isBikeConnected && bikeData.motorPower != null) "$bikeData.motorPower W" else "-- W",
                    //value = String.format(Locale.getDefault(), stringResource(R.string.feature_main_value_watts_format), power), // e.g. "%.0f W"
                    activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorBikeActive else null // Or your specific theme color
                )
            }
            // Add other eBike specific stats here if any
        )
    }

    if (currentStats.isEmpty()) {
        StatItem(
            icon = Icons.AutoMirrored.Filled.BatteryUnknown,
            label = stringResource(R.string.feature_main_label_battery), // Replace with actual R.string ID
            value = if (bikeData.isBikeConnected && bikeData.batteryLevel != null) "$bikeData.batteryLevel%" else "--%",
            // value = String.format(Locale.getDefault(), stringResource(R.string.feature_main_value_percentage_format), battery), // e.g., "%d %%"
            activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.primary else null // Or your specific theme color
        )
        StatItem(
            icon = Icons.Filled.ElectricBike,
            label = stringResource(R.string.feature_main_label_motor), // Replace with actual R.string ID
            value = if (bikeData.isBikeConnected && bikeData.motorPower != null) "$bikeData.motorPower W" else "-- W",
            //value = String.format(Locale.getDefault(), stringResource(R.string.feature_main_value_watts_format), power), // e.g. "%.0f W"
            activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorBikeActive else null // Or your specific theme color
        )

    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Add some vertical padding for the section itself
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        currentStats.forEach { stat ->
            StatCard(
                modifier = Modifier.weight(1f),
                // StatCard should internally use statItem.activeColor if present,
                // or fallback to a general content color.
                // If StatCard needs explicit tint, adjust here:
                icon = stat.icon,
                label = stat.label,
                value = stat.value,
                cardColor = currentCardColor,
                )
        }
    }
}


