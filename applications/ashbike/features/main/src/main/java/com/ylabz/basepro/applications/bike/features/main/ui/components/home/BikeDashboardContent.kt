package com.ylabz.basepro.applications.bike.features.main.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.BatteryUnknown
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.R
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.StatsSection
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike.BikeBatteryLevels
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.SpeedAndProgressCard
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatItem
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatsRow
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState
import com.ylabz.basepro.core.ui.theme.AshBikeTheme
import com.ylabz.basepro.core.ui.theme.iconColorBikeActive
import com.ylabz.basepro.core.ui.theme.iconColorCalories
// import com.ylabz.basepro.core.ui.theme.iconColorElevation // Not used directly here
// import com.ylabz.basepro.core.ui.theme.iconColorSpeed // Not used directly here
import androidx.compose.ui.res.stringResource

@Composable
fun BikeDashboardContent(
    modifier: Modifier = Modifier,
    bikeRideInfo: BikeRideInfo,
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

    val isBikeConnected = bikeRideInfo.isBikeConnected
    val batteryLevel = bikeRideInfo.batteryLevel
    val motorPower = bikeRideInfo.motorPower
    val heartRate = null // Replace with actual heart rate data if available
    val calories = bikeRideInfo.caloriesBurned
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
            getCurrentSpeed = { bikeRideInfo.currentSpeed },
            getHeading = { bikeRideInfo.heading },
            getBikeWeatherInfo = { bikeRideInfo.bikeWeatherInfo },
            getRideState = { bikeRideInfo.rideState },
            getCurrentTripDistance = { bikeRideInfo.currentTripDistance },
            getTotalTripDistance = { bikeRideInfo.totalTripDistance },
            onBikeEvent = onBikeEvent,
            navTo = navTo,
            containerColor = containerColor,
            contentColor = contentColor
        )

        StatsRow(
            getCurrentTripDistance = { bikeRideInfo.currentTripDistance },
            getRideDuration = { bikeRideInfo.rideDuration },
            getAverageSpeed = { bikeRideInfo.averageSpeed },
            getElevation = { bikeRideInfo.elevation },
            cardColor = cardColor,
            contentColor = contentColor,
            isBikeComputerOn = currRiding
        )

        val healthStats = listOf(
            StatItem(
                icon = Icons.Filled.Favorite,
                label = stringResource(R.string.feature_main_label_heart_rate),
                value = if (heartRate != null) "$heartRate bpm" else "-- bpm",
                activeColor = if (currRiding) MaterialTheme.colorScheme.error else null // Assuming IconBlue for HR
            ),
            StatItem(
                icon = Icons.Filled.LocalFireDepartment,
                label = stringResource(R.string.feature_main_label_calories),
                value = if (calories != null) "$calories" else "--",
                activeColor = if (currRiding) MaterialTheme.colorScheme.iconColorCalories else null
            )
        )
        StatsSection(stats = healthStats, cardColor = cardColor, contentColor = contentColor)

        var expanded by rememberSaveable { mutableStateOf(false) }
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
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
                        text = stringResource(com.ylabz.basepro.applications.bike.features.main.R.string.feature_main_ebike_stats_title),
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
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val eBikeStats = listOf(
                            StatItem(
                                icon = Icons.AutoMirrored.Filled.BatteryUnknown,
                                label = stringResource(R.string.feature_main_label_battery),
                                value = if (isBikeConnected && batteryLevel != null) "$batteryLevel%" else "--%",
                                activeColor = if (isBikeConnected) MaterialTheme.colorScheme.primary else null // Example active color
                            ),
                            StatItem(
                                icon = Icons.Filled.ElectricBike,
                                label = stringResource(R.string.feature_main_label_motor),
                                value = if (isBikeConnected && motorPower != null) "$motorPower W" else "-- W",
                                activeColor = if (isBikeConnected) MaterialTheme.colorScheme.iconColorBikeActive else null
                            )
                        )
                        StatsSection(stats = eBikeStats, contentColor = contentColor)

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
        settings = mapOf(),
        heading = 90f, // Added for preview consistency
        elevation = 150.0,
        isBikeConnected = true, // Bike computer is ON
        batteryLevel = 85,
        motorPower = 250f,
        rideState = RideState.Riding, // Changed to Riding for a more active preview
        bikeWeatherInfo = null // Placeholder, can be filled if needed
    )

    AshBikeTheme {
        BikeDashboardContent(
            bikeRideInfo = dummyBikeRideInfo,
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
        settings = mapOf(),
        heading = 0f,
        elevation = 0.0,
        isBikeConnected = false, // Bike computer is OFF
        batteryLevel = null,
        motorPower = null,
        rideState = RideState.NotStarted,
        bikeWeatherInfo = null // Placeholder
    )

    AshBikeTheme {
        BikeDashboardContent(
            bikeRideInfo = dummyBikeRideInfo,
            onBikeEvent = { },
            navTo = { }
        )
    }
}
