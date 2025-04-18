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
    val colors = MaterialTheme.colorScheme

    // pick icon, tint, and a container color based on condition
    val (icon, iconTint, background) = remember(weatherInfo.conditionText) {
        when {
            weatherInfo.conditionText.contains("rain", true) ->
                Triple(
                    Icons.Default.Umbrella,
                    colors.onPrimary,
                    colors.primary.copy(alpha = 0.15f)
                )
            weatherInfo.conditionText.contains("clear", true)
                    || weatherInfo.conditionText.contains("sun", true) ->
                Triple(
                    Icons.Default.WbSunny,
                    colors.onSecondary,
                    colors.secondary.copy(alpha = 0.15f)
                )
            weatherInfo.conditionText.contains("cloud", true) ->
                Triple(
                    Icons.Default.Cloud,
                    colors.onSurfaceVariant,
                    colors.surfaceVariant.copy(alpha = 0.15f)
                )
            else ->
                Triple(
                    Icons.Default.HelpOutline,
                    colors.onSurfaceVariant,
                    colors.surfaceVariant.copy(alpha = 0.10f)
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
            Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = weatherInfo.conditionText,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = weatherInfo.temperature
                        ?.let { "${it.toInt()}°" }
                        ?: "--°",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = iconTint
                    )
                )
                Text(
                    text = weatherInfo.conditionText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = iconTint
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
