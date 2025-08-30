package com.ylabz.basepro.feature.weather.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.weather.Weather

@Composable
fun WeatherCard(weather: Weather) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // For demonstration, we show a placeholder for the weather icon.
            // In your app, use an image loading library (e.g., Coil) to load weather.iconUrl.
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Icon", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${weather.temperature}°C",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = weather.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = weather.location,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun PreviewWeatherCard() {
    MaterialTheme {
        WeatherCard(
            weather = Weather(
                temperature = 25.0,
                description = "Partly Cloudy",
                iconUrl = "",
                location = "New York, NY"
            )
        )
    }
}
*/
