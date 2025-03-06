package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ylabz.basepro.feature.weather.ui.components.combine.UnifiedWeatherCard
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif
import kotlin.math.roundToLong

@Composable
fun BikeDashboardContentOld(
    modifier: Modifier = Modifier,
    currentSpeed: Double,   // in km/h
    totalDistance: Double,  // in km
    tripDuration: String,   // formatted as "HH:mm:ss"
    averageSpeed: Double,   // in km/h
    elevation: Double       // in m
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Current Speed Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shadow(8.dp, shape = MaterialTheme.shapes.large),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${currentSpeed.roundToLong()} km/h",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    ),
                    color = Color.White
                )
            }
        }

        // Row of stat cards: Distance, Duration, Average Speed
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(label = "Distance", value = "${totalDistance} km")
            StatCard(label = "Duration", value = tripDuration)
            StatCard(label = "Avg Speed", value = "${averageSpeed.roundToLong()} km/h")
        }

        // Additional stat card for Elevation (full-width)
        StatCard(
            label = "Elevation",
            value = "${elevation.roundToLong()} m",
            modifier = Modifier.fillMaxWidth()
        )

        UnifiedWeatherCard(
            weatherCondition = WeatherConditionUnif.RAINY,
            temperature = 25.0,
            conditionText = "Rain",
            location = "Los Angeles, CA",
            windDegree = 120
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BikeDashboardContentOldPreview() {
    MaterialTheme {
        BikeDashboardContentOld(
            currentSpeed = 28.3,
            totalDistance = 12.5,
            tripDuration = "00:45:30",
            averageSpeed = 25.0,
            elevation = 150.0
        )
    }
}
