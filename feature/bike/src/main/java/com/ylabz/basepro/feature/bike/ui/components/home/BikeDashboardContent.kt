package com.ylabz.basepro.feature.bike.ui.components.home

import androidx.compose.foundation.layout.*
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
import com.ylabz.basepro.feature.weather.ui.components.combine.UnifiedWeatherCard
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif

@Composable
fun BikeDashboardContent(
    modifier: Modifier = Modifier,
    currentSpeed: Double,
    totalDistance: Double,
    tripDuration: String,
    averageSpeed: Double,
    elevation: Double
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) Current Speed Card
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
        StatCard(
            label = "Elevation",
            value = "${elevation.roundToLong()} m",
            modifier = Modifier.fillMaxWidth()
        )


    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .height(120.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF90CAF9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BikeDashboardContentPreview() {
    MaterialTheme {
        BikeDashboardContent(
            currentSpeed = 28.0,
            totalDistance = 12.5,
            tripDuration = "00:45:30",
            averageSpeed = 25.0,
            elevation = 150.0
        )
    }
}
