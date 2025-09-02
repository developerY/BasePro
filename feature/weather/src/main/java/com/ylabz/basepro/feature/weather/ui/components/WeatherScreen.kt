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
// //import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.core.model.weather.Clouds
import com.ylabz.basepro.core.model.weather.Coord
import com.ylabz.basepro.core.model.weather.Main
import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import com.ylabz.basepro.core.model.weather.Rain
import com.ylabz.basepro.core.model.weather.Snow
import com.ylabz.basepro.core.model.weather.Sys
import com.ylabz.basepro.core.model.weather.WeatherOne
import com.ylabz.basepro.core.model.weather.Wind
import com.ylabz.basepro.feature.weather.ui.WeatherEvent
import com.ylabz.basepro.feature.weather.ui.components.combine.UnifiedWeatherCard
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif

/*
@Preview
@Composable
fun WeatherScreenPreview() {
    WeatherScreen(
        weather = generateDummyWeatherResponse(),
        settings = emptyMap(),
        location = null,
        onEvent = {}, navTo = {}
    )
}
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    weather: OpenWeatherResponse?,
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

fun generateDummyWeatherResponse(): OpenWeatherResponse {
    return OpenWeatherResponse(
        coord = Coord(lon = -118.2437, lat = 34.0522),
        weather = listOf(
            WeatherOne(
                id = 500,
                main = "Rain",
                description = "light rain",
                icon = "10d"
            )
        ),
        base = "stations",
        main = Main(
            temp = 293.15,
            feels_like = 293.15,
            temp_min = 291.48,
            temp_max = 294.82,
            pressure = 1012,
            humidity = 77
        ),
        visibility = 10000,
        wind = Wind(speed = 2.06, deg = 290, gust = 3.6),
        clouds = Clouds(all = 75),
        rain = Rain(0.25, 0.25),
        snow = Snow(.025, 9.25),
        dt = 1603068800,
        sys = Sys(type = 1, id = 5122, country = "US", sunrise = 1603047908, sunset = 1603087667),
        timezone = -25200, id = 5368361, name = "Los Angeles", cod = 200
    )
}