package com.ylabz.basepro.feature.weather.ui.components.combine

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.weather.ui.components.main.WeatherCondition
import com.ylabz.basepro.feature.weather.ui.components.rain.RainVolumeCard
import com.ylabz.basepro.feature.weather.ui.components.snow.BetterSnowVolumeCardAI
import com.ylabz.basepro.feature.weather.ui.components.sun.TemperatureCardAI
import com.ylabz.basepro.feature.weather.ui.components.wind.WindCard

// Assume these composables are defined in your project:
// TemperatureCardAI(temp: Double)
// RainVolumeCardAI(volume: Double)
// BetterSnowVolumeCardAI(volume: Double)
// ImprovedWindCard(windDegree: Int)



/**
 * A composite card that shows the primary weather card (temperature, rain, or snow)
 * on the left and the wind card on the right.
 */
@Composable
fun WeatherCardWithWind(
    weatherCondition: WeatherCondition,
    temperature: Double,
    rainVolume: Double,
    snowVolume: Double,
    windDegree: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Primary weather card occupies most of the width.
        Box(modifier = Modifier.weight(1f)) {
            when (weatherCondition) {
                WeatherCondition.RAINY -> RainVolumeCard(volume = rainVolume)
                WeatherCondition.SNOWY -> BetterSnowVolumeCardAI(volume = snowVolume)
                else -> TemperatureCardAI(temp = temperature)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        // Wind card on the right side, fixed width.
        Box(modifier = Modifier.width(200.dp)) {
            WindCard(windDegree = windDegree)
        }
    }
}

/**
 * WeatherScreenWithWind composes the above card into a full screen with a top bar.
 * Replace the sample values with real data from your ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreenWithWind(
    weatherCondition: WeatherCondition,
    temperature: Double,
    rainVolume: Double,
    snowVolume: Double,
    windDegree: Int
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Current Weather") }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                WeatherCardWithWind(
                    weatherCondition = weatherCondition,
                    temperature = temperature,
                    rainVolume = rainVolume,
                    snowVolume = snowVolume,
                    windDegree = windDegree,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenWithWindPreview() {
    MaterialTheme {
        WeatherScreenWithWind(
            weatherCondition = WeatherCondition.CLEAR,
            temperature = 25.0,
            rainVolume = 10.0,
            snowVolume = 5.0,
            windDegree = 60
        )
    }
}
