package com.ylabz.basepro.applications.bike.features.main.ui.components.home.unused.unused

//import androidx.compose.ui.tooling.preview.Preview
// Assume these imports bring in your unified weather card and related data classes:
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.ylabz.basepro.core.model.weather.Clouds
import com.ylabz.basepro.core.model.weather.Coord
import com.ylabz.basepro.core.model.weather.Main
import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import com.ylabz.basepro.core.model.weather.Rain
import com.ylabz.basepro.core.model.weather.Snow
import com.ylabz.basepro.core.model.weather.Sys
import com.ylabz.basepro.core.model.weather.WeatherOne
import com.ylabz.basepro.core.model.weather.Wind
import com.ylabz.basepro.feature.weather.ui.UnifiedDynamicWeatherCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BikeAppScreenWithWeather(
    navTo: (String) -> Unit = {}
) {
    // Create a camera position state for the map.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.4219999, -122.0862462), 14f)
    }

    // Create a dummy OpenWeatherResponse to simulate rainy weather.
    val openWeatherResponse = OpenWeatherResponse(
        coord = Coord(lon = 139.0, lat = 35.0),
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
            temp = 20.5,
            feels_like = 20.0,
            temp_min = 18.0,
            temp_max = 22.0,
            pressure = 1010,
            humidity = 80
        ),
        visibility = 10000,
        wind = Wind(speed = 5.0, deg = 180, gust = 6.0),
        clouds = Clouds(all = 90),
        rain = Rain(`1h` = 2.5, `3h` = 5.0),
        snow = Snow(`1h` = null, `3h` = null),
        dt = 1678886400,
        sys = Sys(type = 1, id = 8000, country = "JP", sunrise = 1678876800, sunset = 1678920000),
        timezone = 32400,
        id = 1851632,
        name = "Shuzenji",
        cod = 200
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bike App") }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 1. Map as the background content.
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true)
                )
                // 2. Overlay the weather card (here, our rainy weather card)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    UnifiedDynamicWeatherCard(response = openWeatherResponse)
                }
            }
        }
    )
}

/*
@Preview(showBackground = true)
@Composable
fun BikeAppScreenWithWeatherPreview() {
    MaterialTheme {
        BikeAppScreenWithWeather()
    }
}
*/