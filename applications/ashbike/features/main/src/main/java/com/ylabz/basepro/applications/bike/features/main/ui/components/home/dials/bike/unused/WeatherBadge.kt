package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.bike.unused

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo


/**
 * A small badge that displays the weather icon + text.
 * Replace the icon logic or images as needed for your app.
 */
@Composable
fun WeatherBadge(
    weatherInfo: BikeWeatherInfo,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    // Pick icon + explicit background pastel by condition:
    val (icon, tint, background) = remember(weatherInfo.conditionText) {
        when {
            weatherInfo.conditionText.contains("rain", true) ->
                Triple(
                    Icons.Default.Umbrella,
                    Color(0xFF01579B),               // deep blue tint
                    Color(0xFF81D4FA).copy(alpha = 0.3f) // light sky‑blue background
                )

            weatherInfo.conditionText.contains("clear", true)
                    || weatherInfo.conditionText.contains("sun", true) ->
                Triple(
                    Icons.Default.WbSunny,
                    Color(0xFFF57F17),               // dark gold tint
                    Color(0xFFFFF59D).copy(alpha = 0.3f) // pale yellow background
                )

            weatherInfo.conditionText.contains("cloud", true) ->
                Triple(
                    Icons.Default.Cloud,
                    Color(0xFF455A64),               // slate‑blue tint
                    Color(0xFFCFD8DC).copy(alpha = 0.3f) // light grey‑blue background
                )

            else ->
                Triple(
                    Icons.AutoMirrored.Filled.HelpOutline,
                    colors.onSurfaceVariant,
                    colors.surfaceVariant.copy(alpha = 0.1f)
                )
        }
    }

    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = background
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector   = icon,
                contentDescription = weatherInfo.conditionText,
                tint          = tint,
                modifier      = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text  = weatherInfo.temperature
                        ?.let { "${it.toInt()}°" }
                        ?: "--°",
                    style = MaterialTheme.typography.titleMedium.copy(color = tint)
                )
                Text(
                    text  = weatherInfo.conditionText,
                    style = MaterialTheme.typography.bodySmall.copy(color = tint)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherBadges() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
        WeatherBadge(
            weatherInfo = BikeWeatherInfo(0,0.7,"Sunny", conditionDescription = "Sunny Sky", conditionIcon = "01d",
                22.0, 20.0, 45),
            modifier = Modifier.fillMaxWidth()
        )
        WeatherBadge(
            weatherInfo = BikeWeatherInfo(0,0.7,"Rain",  conditionDescription = "Sunny Sky", conditionIcon = "01d",18.0, 17.0, 82),
            modifier = Modifier.fillMaxWidth()
        )
        WeatherBadge(
            weatherInfo = BikeWeatherInfo(0,0.7,"Clouds", conditionDescription = "Sunny Sky", conditionIcon = "01d",10.0, 10.0, 10),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
