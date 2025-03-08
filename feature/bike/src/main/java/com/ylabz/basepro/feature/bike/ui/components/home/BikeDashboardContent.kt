package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.ylabz.basepro.feature.bike.ui.components.home.dials.AnimatedHeartRateCard
import com.ylabz.basepro.feature.bike.ui.components.home.dials.BikeDashboardScreen
import com.ylabz.basepro.feature.bike.ui.components.home.dials.SpeedAndProgressCard
import com.ylabz.basepro.feature.bike.ui.components.home.dials.StatsRow
import com.ylabz.basepro.feature.heatlh.ui.HealthUiState
import com.ylabz.basepro.feature.heatlh.ui.components.HealthStartScreen
import com.ylabz.basepro.feature.weather.ui.components.combine.UnifiedWeatherCard
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif
import kotlinx.coroutines.CoroutineScope

@Composable
fun BikeDashboardContent(
    modifier: Modifier = Modifier,
    currentSpeed: Double,
    currentTripDistance: Double,  // current progress (km)
    totalDistance: Double,
    tripDuration: String,
    averageSpeed: Double,
    elevation: Double,
    navTo: (String) -> Unit,
) {
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
            modifier = Modifier.fillMaxWidth()
        )



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

        BikeDashboardScreen(
            distance = 10.5,
            duration = "30:00",
            avgSpeed = 21.0,
            elevation = 150.0,
            battery = 80,
            motorPower = 250.0,
            heartRate = 120,
            calories = 300.0
        )



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
    MaterialTheme {
        BikeDashboardContent(
            currentSpeed = 28.0,
            totalDistance = 12.5,
            currentTripDistance =  7.2,  // current progress (km)
            tripDuration = "00:45:30",
            averageSpeed = 25.0,
            elevation = 150.0,
            navTo = {}
        )
    }
}
