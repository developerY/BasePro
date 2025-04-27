package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo


@Composable
fun WeatherBadgeContent(
    weatherInfo: BikeWeatherInfo,
    expanded: Boolean
) {
    val (icon, iconTint, cardBackground) = remember(weatherInfo.conditionText) {
        when {
            weatherInfo.conditionText.contains("rain", true) ->
                Triple(Icons.Default.Umbrella,
                    Color(0xFF1565C0),              // blue icon
                    Color(0xFFE3F2FD))               // very light blue background

            weatherInfo.conditionText.contains("clear", true)
                    || weatherInfo.conditionText.contains("sun", true) ->
                Triple(Icons.Default.WbSunny,
                    Color(0xFFFFA000),              // orange/yellow icon
                    Color(0xFFFFF8E1))               // very light yellow background

            weatherInfo.conditionText.contains("cloud", true) ->
                Triple(Icons.Default.Cloud,
                    Color(0xFF455A64),              // dark gray-blue icon
                    Color(0xFFECEFF1))               // very light gray background

            else ->
                Triple(Icons.AutoMirrored.Filled.HelpOutline,
                    Color(0xFF455A64), //MaterialTheme.colorScheme.surfaceVariant.colorSpace,
                    Color(0xFFECEFF1)) // MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        }
    }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = cardBackground),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        if (expanded) {
            Row(
                Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = weatherInfo.conditionText,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = weatherInfo.temperature
                            ?.let { "${it.toInt()}°" }
                            ?: "--°",
                        style = MaterialTheme.typography.titleMedium.copy(color = iconTint)
                    )
                    Text(
                        text = weatherInfo.conditionText,
                        style = MaterialTheme.typography.bodySmall.copy(color = iconTint)
                    )
                }
            }
        } else {
            // Only icon centered
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
