package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

//import androidx.compose.ui.tooling.preview.Preview
// import com.ylabz.basepro.core.ui.theme.iconColorSpeed // Not used in this version
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
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.R
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatCard
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatItem
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.ui.theme.iconColorBikeActive
import com.ylabz.basepro.core.ui.theme.iconColorCalories

enum class StatsSectionType {
    HEALTH, EBIKE
}

@Composable
fun StatsSection(
    uiState: BikeUiState.Success,
    sectionType: StatsSectionType,
    onEvent: (BikeEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val bikeData = uiState.bikeData
    LocalContext.current

    val isBikeComputerOn = bikeData.rideState == RideState.Riding
    val currentCardColor =
        if (isBikeComputerOn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val currentContentColor =
        if (isBikeComputerOn) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    val currentStats: List<StatItem> = when (sectionType) {
        StatsSectionType.HEALTH -> {
            // --- DYNAMIC HEART RATE COLOR LOGIC ---
            val hr = bikeData.heartbeat
            val heartRateColor = if (hr != null && isBikeComputerOn) {
                when {
                    hr < 100 -> Color(0xFF4CAF50) // Green (Warm up)
                    hr < 130 -> Color(0xFFFFC107) // Yellow (Fat Burn)
                    hr < 160 -> Color(0xFFFF9800) // Orange (Cardio)
                    else -> Color(0xFFF44336)     // Red (Peak)
                }
            } else {
                // Fallback if null or bike computer is off
                if (isBikeComputerOn) Color.Gray else null
            }
            // --------------------------------------

            listOf(
                StatItem(
                    icon = Icons.Filled.Favorite,
                    label = stringResource(R.string.feature_main_label_heart_rate),
                    value = if (hr != null) "$hr bpm" else "-- bpm",
                    // Apply dynamic color here
                    activeColor = heartRateColor
                ),
                StatItem(
                    icon = Icons.Filled.LocalFireDepartment,
                    label = stringResource(R.string.feature_main_label_calories),
                    value = bikeData.caloriesBurned.toString(),
                    activeColor = if (isBikeComputerOn) MaterialTheme.colorScheme.iconColorCalories else null
                )
            )
        }

        StatsSectionType.EBIKE -> listOf(
            StatItem(
                icon = Icons.AutoMirrored.Filled.BatteryUnknown,
                label = stringResource(R.string.feature_main_label_battery),
                value = if (bikeData.isBikeConnected && bikeData.batteryLevel != null) "${bikeData.batteryLevel}%" else "--%",
                activeColor = if (isBikeComputerOn && bikeData.isBikeConnected) MaterialTheme.colorScheme.primary else null
            ),
            StatItem(
                icon = Icons.Filled.ElectricBike,
                label = stringResource(R.string.feature_main_label_motor),
                value = if (bikeData.isBikeConnected && bikeData.motorPower != null) "${bikeData.motorPower} W" else "-- W",
                activeColor = if (isBikeComputerOn && bikeData.isBikeConnected) MaterialTheme.colorScheme.iconColorBikeActive else null
            )
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        currentStats.forEach { stat ->
            StatCard(
                modifier = Modifier.weight(1f),
                icon = stat.icon,
                // Use the calculated activeColor, or fallback to default content color
                tint = stat.activeColor ?: currentContentColor,
                label = stat.label,
                value = stat.value,
                cardColor = currentCardColor,
            )
        }
    }
}
/*
@Preview
@Composable
fun StatsSectionPreview() {
    val bikeData = BikeRideInfo(
        location = null,
        currentSpeed = 0.0,
        averageSpeed = 0.0,
        maxSpeed = 0.0,
        currentTripDistance = 0f,
        totalTripDistance = null,
        remainingDistance = null,
        elevationGain = 0.0,
        elevationLoss = 0.0,
        caloriesBurned = 150,
        rideDuration = "0h 0m",
        settings = persistentMapOf(),
        heading = 0f,
        elevation = 0.0,
        isBikeConnected = true,
        heartbeat = 75,
        batteryLevel = 80,
        motorPower = 250f,
        rideState = RideState.Riding,
        bikeWeatherInfo = null
    )
    val uiState = BikeUiState.Success(bikeData = bikeData)
    StatsSection(
        uiState = uiState,
        sectionType = StatsSectionType.HEALTH,
        onEvent = {}
    )
}
*/
