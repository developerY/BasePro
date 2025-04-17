package com.ylabz.basepro.feature.weather.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToLong
import com.ylabz.basepro.core.model.weather.Clouds
import com.ylabz.basepro.core.model.weather.Coord
import com.ylabz.basepro.core.model.weather.Main
import com.ylabz.basepro.core.model.weather.Rain
import com.ylabz.basepro.core.model.weather.Snow
import com.ylabz.basepro.core.model.weather.Sys
import com.ylabz.basepro.core.model.weather.WeatherOne
import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import com.ylabz.basepro.core.model.weather.Wind
import com.ylabz.basepro.feature.weather.ui.components.WeatherBackground
import com.ylabz.basepro.feature.weather.ui.components.WeatherIcon
import com.ylabz.basepro.feature.weather.ui.components.backgrounds.WeatherBackgroundAnimation
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherConditionUnif
import com.ylabz.basepro.feature.weather.ui.components.combine.WindDirectionDialWithSpeed

// Assume these composables are imported from their respective files:

@Composable
fun UnifiedDynamicWeatherCard(
    response: OpenWeatherResponse,
    modifier: Modifier = Modifier
) {
    // Determine the primary condition.
    val conditionText = response.weatherOne.firstOrNull()?.main ?: "Clear"
    val weatherCondition = when {
        response.rain != null -> WeatherConditionUnif.RAINY
        response.snow != null -> WeatherConditionUnif.SNOWY
        conditionText.equals("Clouds", ignoreCase = true) -> WeatherConditionUnif.CLOUDY
        conditionText.equals("Clear", ignoreCase = true) -> WeatherConditionUnif.SUNNY
        else -> WeatherConditionUnif.CLEAR
    }
    val temperature = response.main.temp           // in °C
    val location = "${response.name}, ${response.sys.country}"
    val windDegree = response.wind.deg
    val windSpeed = response.wind.speed

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Add the dynamic background animation layer
            WeatherBackgroundAnimation(
                weatherCondition = weatherCondition,
                modifier = Modifier.fillMaxSize()
            )
            // Other layers: icon, center text, wind dial
            // Weather icon in the top-left.
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                WeatherIcon(condition = conditionText)
            }
            // Wind dial in bottom-left.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                WindDirectionDialWithSpeed(
                    degree = windDegree,
                    speed = windSpeed
                )
            }
            // Center text with temperature, condition, location.
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${temperature.roundToLong()}°C",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 36.sp,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = conditionText,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}


@Preview
@Composable
fun UnifiedDynamicWeatherCardPreview() {
    val openWeatherResponse = OpenWeatherResponse(
        coord = Coord(lon = 139.0, lat = 35.0),
        weatherOne = listOf(
            WeatherOne(
                id = 800,
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        ),
        base = "stations",
        main = Main(
            temp = 20.5,
            feels_like = 20.0,
            temp_min = 18.0,
            temp_max = 22.0,
            pressure = 1010,
            humidity = 60
        ),
        visibility = 10000,
        wind = com.ylabz.basepro.core.model.weather.Wind(speed = 5.0, deg = 180, gust = 6.0),
        clouds = Clouds(all = 0),
        rain = Rain(null, null),
        snow = Snow(null, null),
        dt = 1678886400,
        sys = Sys(type = 1, id = 8000, country = "JP", sunrise = 1678876800, sunset = 1678920000),
        timezone = 32400,
        id = 1851632,
        name = "Shuzenji",
        cod = 200
    )

    UnifiedDynamicWeatherCard(response = openWeatherResponse)
}


@Preview(showBackground = true)
@Composable
fun UnifiedDynamicWeatherCardRainyPreview() {
    val openWeatherResponse = OpenWeatherResponse(
        coord = Coord(lon = 139.0, lat = 35.0),
        weatherOne = listOf(
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

    // This unified card will choose the rainy layout (rain effect, etc.)
    UnifiedDynamicWeatherCard(response = openWeatherResponse)
}
