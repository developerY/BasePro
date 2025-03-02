package com.ylabz.basepro.feature.weather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.feature.weather.ui.WeatherEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    settings: Map<String, List<String>>,
    location: LatLng?,
    onEvent: (WeatherEvent) -> Unit,
    navTo: (String) -> Unit
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Display the unified weather card if data is available

                UnifiedWeatherCard(
                    weatherCondition = WeatherConditionUnif.RAINY,
                    temperature = 25.0,
                    conditionText = "Rain",
                    location = "Los Angeles, CA",
                    windDegree = 120
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Button to fetch weather from the API
                Button(onClick = { onEvent(WeatherEvent.FetchWeather) }) {
                    Text("Fetch Weather")
                }
            }
        }
    )
}