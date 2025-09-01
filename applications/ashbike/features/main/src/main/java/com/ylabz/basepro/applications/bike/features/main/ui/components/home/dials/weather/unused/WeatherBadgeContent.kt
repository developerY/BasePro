package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.weather.unused

//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo

/*
@Preview
@Composable
fun WeatherBadgeContentPreviewExpanded() {
    val bikeWeatherInfo = BikeWeatherInfo(
        windDegree = 180,
        windSpeed = 12.5,
        conditionText = "Clear sky",
        conditionDescription = "Clear skies throughout the day",
        conditionIcon = "01d",
        temperature = 25.0,
        feelsLike = 26.5,
        humidity = 60
    )
    WeatherBadgeContent(
        weatherInfo = bikeWeatherInfo,
        expanded = true
    )
}
*/
@Composable
fun WeatherBadgeContent(
    weatherInfo: BikeWeatherInfo,
    expanded: Boolean
) {
    val (icon, iconTint, cardBackground) = remember(weatherInfo.conditionText) {
        when {
            weatherInfo.conditionText.contains("rain", true) ->
                Triple(Icons.Default.Umbrella, Color(0xFF1565C0), Color(0xFFE3F2FD))
            weatherInfo.conditionText.contains("clear", true)
                    || weatherInfo.conditionText.contains("sun", true) ->
                Triple(Icons.Default.WbSunny, Color(0xFFFFA000), Color(0xFFFFF8E1))
            weatherInfo.conditionText.contains("cloud", true) ->
                Triple(Icons.Default.Cloud, Color(0xFF455A64), Color(0xFFECEFF1))
            else ->
                Triple(Icons.AutoMirrored.Filled.HelpOutline,
                    Color(0xFF455A64),
                    Color(0xFFECEFF1))
                    //MaterialTheme.colorScheme.onSurfaceVariant,
                    //MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        }
    }
    val transition = updateTransition(targetState = expanded, label = "")
    val cardWidth by transition.animateDp(label = "") { isExpanded ->
        if (isExpanded) 160.dp else 56.dp
    }

    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentWidth()           // only as wide as its children
            .widthIn(max = 200.dp),       // optional max cap
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = cardBackground),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (expanded) 8.dp else 4.dp),
    ) {
        if (expanded) {
            // **No more fillMaxWidth() here** – let the Row size to its content
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // left: icon + “Clear” underneath
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = weatherInfo.conditionText,
                        tint = iconTint,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = weatherInfo.conditionText,
                        style = MaterialTheme.typography.bodySmall.copy(color = iconTint)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // right: temperature
                Text(
                    text = weatherInfo.temperature
                        ?.let { "${it.toInt()}°" }
                        ?: "--°",
                    style = MaterialTheme.typography.headlineSmall.copy(color = iconTint),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = weatherInfo.conditionText,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
