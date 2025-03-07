package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToLong
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.bike.ui.components.path.TripProgressIndicator
import com.ylabz.basepro.feature.weather.ui.components.combine.UnifiedWeatherCard
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif

@Composable
fun BikeDashboardContent(
    modifier: Modifier = Modifier,
    currentSpeed: Double,
    currentTripDistance: Double,  // current progress (km)
    totalDistance: Double,
    tripDuration: String,
    averageSpeed: Double,
    elevation: Double
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
            StatCard(label = "Distance", value = "${totalDistance} km")
            StatCard(label = "Duration", value = tripDuration)
            StatCard(label = "Avg Speed", value = "${averageSpeed.roundToLong()} km/h")
        }

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
            elevation = 150.0
        )
    }
}
