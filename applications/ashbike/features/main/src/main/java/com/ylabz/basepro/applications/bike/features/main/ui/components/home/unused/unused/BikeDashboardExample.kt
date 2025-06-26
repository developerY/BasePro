package com.ylabz.basepro.applications.bike.features.main.ui.components.home.unused.unused

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.StatsSection
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike.BikeBatteryLevels
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatItem
import com.ylabz.basepro.core.model.bike.BikeRideInfo


// Combine with BikeDashboardContent

@Composable
fun BikeDashboardExample(
    bikeRideInfo: BikeRideInfo,
    // If you have a separate field for connected status
    isBikeConnected: Boolean,
    batteryLevel: Int?, // pass null if not connected
    motorPower: Double?,
    heartRate: Int?,
    calories: Double?,
    onConnectClick: () -> Unit
) {
    // Example layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) Possibly a "SpeedAndProgressCard" or gauge at the top
        /*SpeedAndProgressCard(
            currentSpeed = bikeRideInfo.currentSpeed,
            currentTripDistance = bikeRideInfo.currentTripDistance,
            totalDistance = bikeRideInfo.totalTripDistance,
            windDegree = 120f,
            windSpeed = 5.0f,
            weatherConditionText = "RAINY",
            heading = bikeRideInfo.heading,
            isRiding = true,
            onStartPauseClicked = {  },
            onStopClicked = { },
        )*/

        // 2) Bike connection or battery card
        BikeBatteryLevels(
            isConnected = isBikeConnected,
            batteryLevel = batteryLevel,
            onConnectClick = onConnectClick
        )

        // 3) Stats row: e-bike stats
        // Only show motor if connected, or maybe show 0 W if not
        val eBikeStats = listOf(
            StatItem(
                icon = Icons.Filled.BatteryChargingFull,
                label = "Battery",
                value = if (isBikeConnected && batteryLevel != null) "$batteryLevel%" else "--%"
            ),
            StatItem(
                icon = Icons.AutoMirrored.Filled.DirectionsBike,
                label = "Motor",
                value = if (isBikeConnected && motorPower != null) "$motorPower W" else "-- W"
            )
        )
        StatsSection(stats = eBikeStats)

        // 4) Health stats row
        val healthStats = listOf(
            StatItem(
                icon = Icons.Filled.Favorite,
                label = "Heart Rate",
                value = if (heartRate != null) "$heartRate bpm" else "-- bpm"
            ),
            StatItem(
                icon = Icons.Filled.Fireplace,
                label = "Calories",
                value = if (calories != null) "$calories" else "--"
            )
        )
        StatsSection(stats = healthStats)
    }
}


private val bikeRideInfo =BikeRideInfo(
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

/*
    isBikeConnected: Boolean,
    batteryLevel: Int?, // pass null if not connected
    motorPower: Double?,
    heartRate: Int?,
    calories: Double?,
    onConnectClick: () -> Unit
 */


@Preview
@Composable
fun BikeDashboardScreenPreviewFalse() {
    BikeDashboardExample(
        bikeRideInfo = bikeRideInfo,
        batteryLevel = 75,
        motorPower = 100.0,
        heartRate = 70,
        calories = 500.0,
        isBikeConnected = false,
        onConnectClick = {}
    )
}

@Preview
@Composable
fun BikeDashboardScreenPreviewTrue() {
    BikeDashboardExample(
        bikeRideInfo = bikeRideInfo,
        batteryLevel = 75,
        motorPower = 100.0,
        heartRate = 70,
        calories = 500.0,
        isBikeConnected = true,
        onConnectClick = {}
    )
}
