package com.ylabz.basepro.applications.bike.ui.components.home.dials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
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
    // 1) read your colors in the composable scope
    val colors = MaterialTheme.colorScheme

    // 2) now it's safe to refer to `colors` inside remember
    val (icon, tint) = remember(weatherInfo.conditionText) {
        when {
            weatherInfo.conditionText.contains("rain", ignoreCase = true) ->
                Icons.Default.Umbrella to colors.primary

            weatherInfo.conditionText.contains("sun", ignoreCase = true) ||
                    weatherInfo.conditionText.contains("clear", ignoreCase = true) ->
                Icons.Default.WbSunny to colors.secondary

            weatherInfo.conditionText.contains("cloud", ignoreCase = true) ->
                Icons.Default.Cloud to colors.onSurfaceVariant

            else ->
                Icons.Default.HelpOutline to colors.onSurfaceVariant
        }
    }

    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = colors.surfaceVariant),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = weatherInfo.conditionText,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = weatherInfo.temperature
                        ?.let { "${it.toInt()}°" }
                        ?: "--°",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = colors.onSurfaceVariant
                    )
                )
                Text(
                    text = weatherInfo.conditionText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colors.onSurfaceVariant
                    )
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WeatherBadgePreview() {
    MaterialTheme {
        WeatherBadge(
            weatherInfo = BikeWeatherInfo(
                windDegree = 0,
                windSpeed = 0.7,
                conditionText = "Sunshine",
                temperature = 22.5
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
