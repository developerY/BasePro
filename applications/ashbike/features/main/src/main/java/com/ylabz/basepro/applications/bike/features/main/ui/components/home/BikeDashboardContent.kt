package com.ylabz.basepro.applications.bike.features.main.ui.components.home

//import androidx.compose.ui.tooling.preview.Preview
// StatItem no longer needed here as StatsSection builds its own
// import com.ylabz.basepro.core.ui.theme.iconColorElevation // Not used directly here
// import com.ylabz.basepro.core.ui.theme.iconColorSpeed // Not used directly here
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.applications.bike.features.main.R
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.BikeUiState
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.StatsSection
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.StatsSectionType
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike.BikeBatteryLevels
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.SpeedAndProgressCard
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatsRow
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.ui.NavigationCommand

@Composable
fun BikeDashboardContent(
    modifier: Modifier = Modifier,
    uiState: BikeUiState.Success, // Changed parameter
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (NavigationCommand) -> Unit, // <<< MODIFIED LINE
) {
    val bikeRideInfo = uiState.bikeData // Access bikeData from uiState
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    val isBikeConnected = bikeRideInfo.isBikeConnected
    val batteryLevel = bikeRideInfo.batteryLevel
    val motorPower = bikeRideInfo.motorPower
    // heartRate and calories are now handled within StatsSection based on uiState
    val rideState = bikeRideInfo.rideState
    val currRiding = rideState == RideState.Riding

    val containerColor = if (currRiding) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (currRiding) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val cardColor = if (currRiding) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpeedAndProgressCard(
            uiState = uiState, // Pass the full uiState
            onBikeEvent = onBikeEvent,
            navTo = navTo, // Pass down the updated navTo
            containerColor = containerColor,
            contentColor = contentColor
        )

        StatsRow(
            uiState = uiState,
            // onEvent = { /* No events from StatsRow to handle for now */ }
        )

        // Health Stats Section - uses uiState directly
        StatsSection(
            uiState = uiState,
            sectionType = StatsSectionType.HEALTH,
            onEvent = onBikeEvent
        )

        var expanded by rememberSaveable { mutableStateOf(false) }
        Card(
            modifier = Modifier // Corrected to use a local Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp), // Consider if this padding is needed or if StatsSection handles it
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) // Consistent card bg
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.feature_main_ebike_stats_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface // Ensure text color is from theme
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) stringResource(R.string.feature_main_action_collapse) else stringResource(R.string.feature_main_action_expand),
                        tint = MaterialTheme.colorScheme.onSurface // Ensure icon color is from theme
                    )
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp), // Consider padding with StatsSection
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // EBike Stats Section - uses uiState directly
                        StatsSection(
                            uiState = uiState,
                            sectionType = StatsSectionType.EBIKE,
                            onEvent = onBikeEvent
                        )

                        BikeBatteryLevels(
                            isConnected = isBikeConnected,
                            batteryLevel = batteryLevel,
                            onConnectClick = { 
                                // This would typically trigger an event to connect the bike
                                // For preview, you might toggle a state if this were in a ViewModel
                            }
                        )
                    }
                }
            }
        }
    }
}
/*
@Preview(showBackground = true, name = "Bike Dashboard Content - Connected")
@Composable
fun BikeDashboardContentPreviewConnected() {
    val dummyBikeRideInfo = BikeRideInfo(
        location = LatLng(37.4219999, -122.0862462),
        currentSpeed = 25.5,
        averageSpeed = 18.2,
        maxSpeed = 40.0,
        currentTripDistance = 10.5f,
        totalTripDistance = 20.0f, // Added for preview consistency
        remainingDistance = null,
        elevationGain = 120.0,
        elevationLoss = 30.0,
        caloriesBurned = 350,
        rideDuration = "00:45:30",
        settings = persistentMapOf(),
        heading = 90f, // Added for preview consistency
        elevation = 150.0,
        isBikeConnected = true, // Bike computer is ON
        batteryLevel = 85,
        motorPower = 250f,
        rideState = RideState.Riding, // Changed to Riding for a more active preview
        bikeWeatherInfo = null, // Placeholder, can be filled if needed
        heartbeat = 78 // Added for preview consistency
    )
    val uiState = BikeUiState.Success(
        dummyBikeRideInfo
    ) // Wrap in Success state

    AshBikeTheme {
        BikeDashboardContent(
            uiState = uiState, // Pass uiState
            onBikeEvent = { },
            navTo = { }
        )
    }
}

@Preview(showBackground = true, name = "Bike Dashboard Content - Disconnected")
@Composable
fun BikeDashboardContentPreviewDisconnected() {
    val dummyBikeRideInfo = BikeRideInfo(
        location = LatLng(37.4219999, -122.0862462),
        currentSpeed = 0.0,
        averageSpeed = 0.0,
        maxSpeed = 0.0,
        currentTripDistance = 0.0f,
        totalTripDistance = null,
        remainingDistance = null,
        elevationGain = 0.0,
        elevationLoss = 0.0,
        caloriesBurned = 0,
        rideDuration = "00:00",
        settings = persistentMapOf(),
        heading = 0f,
        elevation = 0.0,
        isBikeConnected = false, // Bike computer is OFF
        batteryLevel = null,
        motorPower = null,
        rideState = RideState.NotStarted,
        bikeWeatherInfo = null, // Placeholder
        heartbeat = null // Added for preview consistency
    )
    val uiState = BikeUiState.Success(dummyBikeRideInfo) // Wrap in Success state

    AshBikeTheme {
        BikeDashboardContent(
            uiState = uiState, // Pass uiState
            onBikeEvent = { },
            navTo = { }
        )
    }
}
*/