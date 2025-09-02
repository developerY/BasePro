package com.ylabz.basepro.feature.weather.ui.components.main

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.feature.weather.ui.components.rain.RainVolumeCard
import com.ylabz.basepro.feature.weather.ui.components.snow.BetterSnowVolumeCardAI
import com.ylabz.basepro.feature.weather.ui.components.sun.TemperatureCardAI
import com.ylabz.basepro.feature.weather.ui.components.wind.WindCard

// Define a simple enum to represent the current weather condition.
enum class WeatherCondition {
    CLEAR, RAINY, SNOWY
}

/**
 * WeatherScreen that displays one primary weather card based on the current condition,
 * and always shows the wind card.
 *
 * @param weatherCondition The current weather condition.
 * @param temperature The current temperature in °C.
 * @param rainVolume The current rain volume (e.g., in mm).
 * @param snowVolume The current snow volume (e.g., in mm).
 * @param windDegree The wind direction (in degrees).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreenCard(
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
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display the primary weather card based on the condition.
                when (weatherCondition) {
                    WeatherCondition.RAINY -> {
                        // Display the Rain card
                        RainVolumeCard(volume = rainVolume)
                    }

                    WeatherCondition.SNOWY -> {
                        // Display the Snow card
                        BetterSnowVolumeCardAI(volume = snowVolume)
                    }

                    else -> {
                        // For CLEAR or any other condition, display the Temperature card.
                        TemperatureCardAI(temp = temperature)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Always display the Wind card.
                WindCard(windDegree = windDegree)
            }
        }
    )
}

/*
@Preview(showBackground = true)
@Composable
fun WeatherScreenCardPreview() {
    MaterialTheme {
        // Change the parameters below to simulate different weather conditions.
        WeatherScreenCard(
            weatherCondition = WeatherCondition.RAINY,
            temperature = 22.0,
            rainVolume = 12.0,
            snowVolume = 8.0,
            windDegree = 30
        )
    }
}
*/