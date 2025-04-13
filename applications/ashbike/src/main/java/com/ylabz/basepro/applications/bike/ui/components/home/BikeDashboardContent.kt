package com.ylabz.basepro.applications.bike.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.LocalFireDepartment
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
import com.ylabz.basepro.applications.bike.ui.components.home.dials.StatsSection
import com.ylabz.basepro.applications.bike.ui.components.home.main.SpeedAndProgressCard
import com.ylabz.basepro.applications.bike.ui.components.home.main.StatItem
import com.ylabz.basepro.applications.bike.ui.components.home.main.StatsRow
import com.ylabz.basepro.applications.bike.ui.components.unused.BikeBatteryLevels
import com.ylabz.basepro.core.model.bike.BikeRideInfo
import com.ylabz.basepro.feature.weather.ui.components.combine.UnifiedWeatherCard
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif

// Combine with BikeDashboardExample --

@Composable
fun BikeDashboardContent(
    modifier: Modifier = Modifier,
    bikeRideInfo: BikeRideInfo,
    onBikeEvent: (BikeEvent) -> Unit,
    navTo: (String) -> Unit,
) {
    val isBikeConnected = bikeRideInfo.isBikeConnected

    // Extract values from bikeRideInfo (if needed)
    val currentSpeed = bikeRideInfo.currentSpeed
    val currentTripDistance = bikeRideInfo.currentTripDistance
    val totalDistance = bikeRideInfo.totalTripDistance
    val tripDuration = bikeRideInfo.rideDuration
    val averageSpeed = bikeRideInfo.averageSpeed
    val elevation = bikeRideInfo.elevation
    val heading: Float = bikeRideInfo.heading
    val batteryLevel = bikeRideInfo.batteryLevel
    val motorPower = bikeRideInfo.motorPower
    val heartRate = null // bikeRideInfo.heartRate  // May be null
    val calories = null // bikeRideInfo.calories    // May be null

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
            currentSpeed = currentSpeed,
            currentTripDistance = currentTripDistance,
            totalDistance = totalDistance,
            windDegree = 120f,
            windSpeed = 5.0f,
            weatherConditionText = WeatherConditionUnif.RAINY.name,
            heading = heading
        )

        // 2) Trip Stats Row: Distance, Duration, Avg Speed, Elevation
        StatsRow(
            distance = currentTripDistance,
            duration = tripDuration,
            avgSpeed = averageSpeed,
            elevation = elevation
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
        isBikeConnected = false,
        location = LatLng(34.0522, -118.2437), // Dummy location
        currentSpeed = 28.0,
        totalTripDistance = 12.5f,
        currentTripDistance = 7.2f,  // current progress (km)
        remainingDistance = 10.5f,
        rideDuration = "00:45:30",
        averageSpeed = 15.2,
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

val dummyBikeRideInfo = BikeRideInfo(
    isBikeConnected = false,
    location = LatLng(34.0522, -118.2437), // Dummy location
    currentSpeed = 28.0,
    totalTripDistance = 2.5f,
    currentTripDistance = 7.2f,  // current progress (km)
    remainingDistance = 10.5f,
    rideDuration = "00:45:30",
    averageSpeed = 25.0,
    elevation = 150.0,
    settings = emptyMap(), // You may set appropriate values
    heading = 45f,
    batteryLevel = 80,
    motorPower = 1500f,
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun BikeDashboardScaffoldContentPreview() {


     @androidx.compose.runtime.Composable {
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

