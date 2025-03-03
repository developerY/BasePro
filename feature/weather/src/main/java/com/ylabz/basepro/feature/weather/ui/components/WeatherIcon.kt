package com.ylabz.basepro.feature.weather.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun WeatherIcon(
    condition: String,
    modifier: Modifier = Modifier
) {
    // Map condition strings to icons. You can adjust these mappings as needed.
    val icon = when {
        condition.equals("Clear", ignoreCase = true) ||
                condition.equals("Sunny", ignoreCase = true) ->
            Icons.Filled.WbSunny
        condition.equals("Clouds", ignoreCase = true) ||
                condition.equals("Cloudy", ignoreCase = true) ->
            Icons.Filled.Cloud
        condition.equals("Rain", ignoreCase = true) ->
            Icons.Filled.BeachAccess  // Umbrella icon for rain
        condition.equals("Snow", ignoreCase = true) ->
            Icons.Filled.AcUnit
        else -> Icons.Filled.WbSunny  // Fallback icon
    }

    Icon(
        imageVector = icon,
        contentDescription = condition,
        modifier = modifier,
        tint = Color.White
    )
}
