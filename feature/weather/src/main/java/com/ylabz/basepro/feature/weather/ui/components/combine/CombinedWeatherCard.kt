package com.ylabz.basepro.feature.weather.ui.components.combine

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.weather.ui.components.main.WeatherCondition
import com.ylabz.basepro.feature.weather.ui.components.rain.RainVolumeCard
import com.ylabz.basepro.feature.weather.ui.components.snow.BetterSnowVolumeCardAI
import com.ylabz.basepro.feature.weather.ui.components.sun.TemperatureCardAI
import com.ylabz.basepro.feature.weather.ui.components.wind.WindCard

// Import your existing cards:
// TemperatureCardAI(temp: Double)
// RainVolumeCardAI(volume: Double)
// BetterSnowVolumeCardAI(volume: Double)
// ImprovedWindCard(windDegree: Int)

@Composable
fun CombinedWeatherCard(
    weatherCondition: WeatherCondition,
    temperature: Double,
    rainVolume: Double,
    snowVolume: Double,
    windDegree: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp) // for a tall, narrow card
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1) Primary weather section based on the condition.
            when (weatherCondition) {
                WeatherCondition.RAINY -> {
                    RainVolumeCard(volume = rainVolume)
                }
                WeatherCondition.SNOWY -> {
                    BetterSnowVolumeCardAI(volume = snowVolume)
                }
                else -> {
                    // CLEAR or default => show Temperature
                    TemperatureCardAI(temp = temperature)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2) Wind Direction always shown at the bottom
            Text(
                text = "Wind Direction",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            // You could adapt 'ImprovedWindCard' to show just the dial content,
            // but for simplicity we call it directly here:
            WindCard(windDegree = windDegree)
        }
    }
}


@Preview(device = "id:Nexus S")
@Composable
fun CombinedWeatherCardPreview() {
    CombinedWeatherCard(
        weatherCondition = WeatherCondition.CLEAR,
        temperature = 25.0,
        rainVolume = 0.0,
        snowVolume = 0.0,
        windDegree = 180
    )
}
