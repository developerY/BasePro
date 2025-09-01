package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.weather.unused

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo

@Composable
fun ExpandedWeatherBadge(weatherInfo: BikeWeatherInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = weatherInfo.conditionText, style = MaterialTheme.typography.titleLarge)
        Text(text = "Temperature: ${weatherInfo.temperature}°C")
        Text(text = "Feels like: ${weatherInfo.feelsLike}°C")
        Text(text = "Humidity: ${weatherInfo.humidity}%")
        Text(text = "Wind: ${weatherInfo.windSpeed} km/h from ${weatherInfo.windDegree}°")
    }
}
/*
@Preview
@Composable
fun ExpandedWeatherBadgePreview() {
    val weatherInfo = BikeWeatherInfo(
        windDegree = 180,
        windSpeed = 15.5,
        conditionText = "Sunny",
        conditionDescription = "Clear skies",
        conditionIcon = "sun",
        temperature = 25.0,
        feelsLike = 23.0,
        humidity = 60)
    ExpandedWeatherBadge(weatherInfo)
}
*/