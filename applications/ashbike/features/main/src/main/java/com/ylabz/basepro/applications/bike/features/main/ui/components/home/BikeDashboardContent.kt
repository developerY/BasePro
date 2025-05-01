package com.ylabz.basepro.applications.bike.features.main.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.applications.bike.features.main.ui.BikeEvent
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.StatsSection
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.SpeedAndProgressCard
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatItem
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.main.StatsRow
import com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike.BikeBatteryLevels
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.core.model.bike.RideState

// Combine with BikeDashboardExample --

@Composable
fun BikeDashboardContent(
    modifier: Modifier = Modifier,
    bikeRideInfo: BikeRideInfo,
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    val isBikeConnected = bikeRideInfo.isBikeConnected

    val batteryLevel = bikeRideInfo.batteryLevel
    val motorPower = bikeRideInfo.motorPower
    val heartRate = null // bikeRideInfo.heartRate  // May be null
    val calories = bikeRideInfo.caloriesBurned    // May be null

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) Current Speed Card (assumed to be a custom composable)
        SpeedAndProgressCard(
            bikeRideInfo = bikeRideInfo,
            onBikeEvent = onBikeEvent,
            navTo = navTo,
            /*currentSpeed = currentSpeed,
            currentTripDistance = currentTripDistance,
            totalDistance = totalDistance,
            windDegree = 120f,
            windSpeed = 5.0f,
            weatherConditionText = WeatherConditionUnif.RAINY.name,
            heading = heading,*/

            /*isRiding = false, // NOTE: need to fix it
            onStartPauseClicked = { onBikeEvent(BikeEvent.StartPauseRide) },
            onStopClicked = { onBikeEvent(BikeEvent.StopRide) },*/
        )

        // 2) Trip Stats Row: Distance, Duration, Avg Speed, Elevation
        StatsRow(
            bikeRideInfo = bikeRideInfo,
            /*distance = currentTripDistance,
            duration = tripDuration,
            avgSpeed = averageSpeed,
            elevation = elevation*/
        )


        // 3) Health Stats Section: Heart Rate, Calories
        val healthStats = listOf(
            StatItem(
                icon = Icons.Filled.Favorite,
                label = "Heart Rate",
                value = if (heartRate != null) "$heartRate bpm" else "-- bpm"
            ),
            StatItem(
                icon = Icons.Filled.LocalFireDepartment, // Using a standard flame icon
                tint = if (bikeRideInfo.rideState == RideState.Riding)  Color(0xFF811038) else Gray,
                label = "Calories",
                value = if (calories != null) "$calories" else "--"
            )
        )
        StatsSection(stats = healthStats)

        // 4) Grouped E-bike Stats & Connect Bike Button in a Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // E-bike Stats Section: Battery and Motor Power
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

                // Connect Bike Button / Status
                BikeBatteryLevels(
                    isConnected = isBikeConnected,
                    batteryLevel = batteryLevel,
                    onConnectClick = { onBikeEvent(BikeEvent.Connect) }
                )
            }
        }
    }
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

        // AnimatedHeartRateCard(heartRate = 70)

        // 4) Unified Weather Card (Rainy example)
        /*Spacer(modifier = Modifier.height(16.dp))
        UnifiedWeatherCard(
            modifier = modifier
                .shadow(4.dp, shape = MaterialTheme.shapes.medium),
            weatherCondition = WeatherConditionUnif.RAINY,  // or your dynamic condition
            temperature = 25.0,
            conditionText = "Rain",
            location = "Los Angeles, CA",
            windDegree = 120,
        )*/

        // 3) Full-width elevation stat card
        /*StatCard(
            label = "Elevation",
            value = "${elevation.roundToLong()} m",
            modifier = Modifier.fillMaxWidth()
        )*/



@Preview(showBackground = true)
@Composable
fun BikeDashboardContentPreview() {

    val dummyBikeRideInfo = BikeRideInfo(
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

    MaterialTheme {
        BikeDashboardContent(
            bikeRideInfo = dummyBikeRideInfo,
            onBikeEvent = { /*TODO*/ },
            navTo = { /*TODO*/ }
        )
    }
}

val dummyBikeRideInfo = BikeRideInfo(
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BikeDashboardScaffoldContentPreview() {


     @Composable {
        Scaffold(
            topBar = { TopAppBar(title = { Text("AshBike") }) },
            bottomBar = { /* Example bottom bar */ }
        ) { innerPadding ->
            BikeDashboardContent(
                modifier = Modifier.padding(innerPadding),
                bikeRideInfo = dummyBikeRideInfo,
                onBikeEvent = { /*TODO*/ },
                navTo = { /*TODO*/ }
            )
        }
    }
}

