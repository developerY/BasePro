package com.ylabz.basepro.applications.bike.ui.components.home.dials
import android.R.attr.contentDescription
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


import com.ylabz.basepro.core.model.weather.BikeWeatherInfo

// 5) The fully featured, animated badge
@Composable
fun WeatherBadgeWithDetails(
    weatherInfo: BikeWeatherInfo?,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        if (weatherInfo == null) {
            SimpleShimmer(
                Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        } else {
            // make the whole badge clickable & accessible
            AnimatedWeatherBadge(
                weatherInfo = weatherInfo,
                modifier = Modifier
                    .clickable { showDialog = true }
                    /*.semantics {
                        contentDescription = buildString {
                            append(stringResource(R.string.weather_content_desc,
                                weatherInfo.conditionText,
                                weatherInfo.temperature?.toInt() ?: 0))
                        }
                    }*/
            )
            if (showDialog) {
                WeatherDetailDialog(weatherInfo) { showDialog = false }
            }
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