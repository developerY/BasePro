package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


import com.ylabz.basepro.core.model.weather.BikeWeatherInfo

// 5) The fully featured, animated badge
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherBadgeWithDetails(
    weatherInfo: BikeWeatherInfo?,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableStateOf(0L) }

    val DOUBLE_CLICK_THRESHOLD = 300L // ms for double click

    Box(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime < DOUBLE_CLICK_THRESHOLD) {
                        // Double click
                        showDialog = true
                        expanded = false
                    } else {
                        // Single click
                        expanded = !expanded
                    }
                    lastClickTime = currentTime
                }
            )
    ) {
        if (weatherInfo == null) {
            SimpleShimmer(
                Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        } else {
            WeatherBadgeContent(weatherInfo, expanded)
        }

        if (weatherInfo != null && showDialog) {
            WeatherDetailDialog(weatherInfo) { showDialog = false }
        }
    }
}


// 3) Shimmer placeholder for loading
@Composable
fun SimpleShimmer(
    modifier: Modifier = Modifier,
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    highlightColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
) {
    // animate a float from 0→1 forever
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing))
    )

    // moving gradient
    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start  = Offset(x = progress * 200f, y = 0f),
        end    = Offset(x = progress * 200f + 200f, y = 200f)
    )

    Box(modifier.background(brush, RoundedCornerShape(16.dp)))
}



@Preview(showBackground = true)
@Composable
fun PreviewWeatherBadgeAll() {
    MaterialTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherBadgeWithDetails(
                weatherInfo = BikeWeatherInfo(
                    windDegree           = 45,                // wind from the NE
                    windSpeed            = 15.0,              // km/h
                    conditionText        = "Sunny",
                    conditionDescription = "Clear sky",
                    conditionIcon        = "01d",             // OWM “clear sky (day)” icon
                    temperature          = 25.0,              // °C
                    feelsLike            = 27.0,              // °C
                    humidity             = 40                 // %
                ),
                modifier = Modifier.fillMaxWidth()
            )
            WeatherBadgeWithDetails(
                weatherInfo = BikeWeatherInfo(
                    windDegree           = 180,               // wind from the S
                    windSpeed            = 8.5,               // km/h
                    conditionText        = "Cloudy",
                    conditionDescription = "Overcast clouds",
                    conditionIcon        = "03d",             // OWM “scattered clouds” icon
                    temperature          = 18.0,              // °C
                    feelsLike            = 18.0,              // °C
                    humidity             = 65                 // %
                ),
                modifier = Modifier.fillMaxWidth()
            )
            WeatherBadgeWithDetails(
                weatherInfo = BikeWeatherInfo(
                    windDegree           = 220,               // wind from the SW
                    windSpeed            = 14.0,              // km/h
                    conditionText        = "Light Rain",
                    conditionDescription = "Light rain showers",
                    conditionIcon        = "10d",             // OWM “light rain” icon
                    temperature          = 12.0,              // °C
                    feelsLike            = 11.0,              // °C
                    humidity             = 85                 // %
                ),
                modifier = Modifier.fillMaxWidth()
            )

            WeatherBadgeWithDetails(
                weatherInfo = null,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}