package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.lerp
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
