package com.ylabz.basepro.applications.bike.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.ui.BikeEvent
import com.ylabz.basepro.applications.bike.ui.components.home.dials.AnimatedHeartRateCard
import com.ylabz.basepro.applications.bike.ui.components.home.dials.BikeBatteryCharge
import com.ylabz.basepro.applications.bike.ui.components.home.dials.StatsSection
import com.ylabz.basepro.applications.bike.ui.components.home.main.SpeedAndProgressCard
import com.ylabz.basepro.applications.bike.ui.components.home.main.StatItem
import com.ylabz.basepro.applications.bike.ui.components.home.main.StatsRow
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.feature.weather.ui.components.combine.UnifiedWeatherCard
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif

// Combine with BikeDashboardExample --

@Composable
fun BikeDashboardContent(
    modifier: Modifier = Modifier,
    bikeRideInfo : BikeRideInfo,
    onBikeEvent : (BikeEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    val isBikeConnected = bikeRideInfo.isBikeConnected

    val currentSpeed = bikeRideInfo.currentSpeed
    val currentTripDistance = bikeRideInfo.currentTripDistance
    val totalDistance = bikeRideInfo.totalDistance
    val tripDuration =  bikeRideInfo.rideDuration
    val averageSpeed = bikeRideInfo.averageSpeed
    val elevation = bikeRideInfo.elevation
    val heading : Float = bikeRideInfo.heading
    val batteryLevel = bikeRideInfo.batteryLevel
    val motorPower = bikeRideInfo.motorPower
    val heartRate = null
    val calories = null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Enables vertical scrolling
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) Current Speed Card
       SpeedAndProgressCard(
            currentSpeed = currentSpeed,
            currentTripDistance = currentTripDistance,
            totalDistance = totalDistance,
            windDegree = 120f,
            windSpeed = 5.0f,
            weatherConditionText = WeatherConditionUnif.RAINY.name,
            heading = heading,
            modifier = Modifier.fillMaxWidth()
        )

        BikeBatteryCharge(
            isConnected = isBikeConnected,
            batteryLevel = batteryLevel,
            onConnectClick = { onBikeEvent(BikeEvent.Connect) }
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

        // 2) Trip stats row: Distance, Duration, Avg Speed
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatsRow(
                distance = 12.5,
                duration = tripDuration,
                avgSpeed = 8.3,
                elevation = 150.0,
            )
        }
        /*
        modifier: Modifier = Modifier,
            healthPermState: HealthScreenState,
            sessionsList: List<ExerciseSessionRecord>,
            scope: CoroutineScope = rememberCoroutineScope(),
            onEvent: (HealthEvent) -> Unit,
            onPermissionsLaunch: (Set<String>) -> Unit,
            navTo: (String) -> Unit,
         */
        /*HealthStartScreen(
            modifier = modifier,
            healthPermState = bundledState,
            sessionsList = (healthUiState as HealthUiState.Success).healthData,
            onPermissionsLaunch = { values ->
                permissionsLauncher.launch(values)
            },
            onEvent = { event -> viewModel.onEvent(event) },
            navTo = navTo,
        )*/

        AnimatedHeartRateCard(heartRate = 70)

        // 4) Unified Weather Card (Rainy example)
        Spacer(modifier = Modifier.height(16.dp))
        UnifiedWeatherCard(
            modifier = Modifier
                .shadow(4.dp, shape = MaterialTheme.shapes.medium),
            weatherCondition = WeatherConditionUnif.RAINY,  // or your dynamic condition
            temperature = 25.0,
            conditionText = "Rain",
            location = "Los Angeles, CA",
            windDegree = 120,
        )

        // 3) Full-width elevation stat card
        /*StatCard(
            label = "Elevation",
            value = "${elevation.roundToLong()} m",
            modifier = Modifier.fillMaxWidth()
        )*/


    }
}

@Preview(showBackground = true)
@Composable
fun BikeDashboardContentPreview() {

    val dummyBikeRideInfo = BikeRideInfo(
        isBikeConnected = true,
        location = LatLng(34.0522, -118.2437), // Dummy location
        currentSpeed = 28.0,
        totalDistance = 12.5,
        currentTripDistance = 7.2,  // current progress (km)
        rideDuration = "00:45:30",
        averageSpeed = 25.0,
        elevation = 150.0,
        settings = emptyMap(), // You may set appropriate values
        heading = 45f,
        batteryLevel = 80,
        motorPower = 1500f,
    )

    MaterialTheme {
        BikeDashboardContent(
            bikeRideInfo = dummyBikeRideInfo,
            onBikeEvent = { /*TODO*/ },
            navTo = { /*TODO*/ }
        )
    }
}
