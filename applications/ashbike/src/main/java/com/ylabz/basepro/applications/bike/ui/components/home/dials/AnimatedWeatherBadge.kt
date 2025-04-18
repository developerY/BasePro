package com.ylabz.basepro.applications.bike.ui.components.home.dials
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import com.ylabz.basepro.applications.bike.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedWeatherBadge(
    weatherInfo: BikeWeatherInfo,
    modifier: Modifier = Modifier
) {
    // replaces: val isDark = MaterialTheme.colorScheme.isLight.not()
    val isDark = isSystemInDarkTheme()

    // pick icon, tint & background pastel per condition
    val (targetIcon, targetTint, targetBg) = remember(weatherInfo.conditionText) {
        when {
            weatherInfo.conditionText.contains("rain", true) ->
                Triple(
                    Icons.Default.Umbrella,
                    Color(0xFF01579B),
                    Color(0xFF81D4FA).copy(alpha = 0.3f)
                )
            weatherInfo.conditionText.contains("clear", true)
                    || weatherInfo.conditionText.contains("sun", true) ->
                Triple(
                    Icons.Default.WbSunny,
                    Color(0xFFF57F17),
                    Color(0xFFFFF59D).copy(alpha = 0.3f)
                )
            weatherInfo.conditionText.contains("cloud", true) ->
                Triple(
                    Icons.Default.Cloud,
                    Color(0xFF455A64),
                    Color(0xFFCFD8DC).copy(alpha = 0.3f)
                )
            else ->
                Triple(
                    Icons.AutoMirrored.Filled.HelpOutline,
                        Color.Black,
                    Color.Black
                )
        }
        /*
        Triple(
                    Icons.AutoMirrored.Filled.HelpOutline,
                    colors.onSurfaceVariant,
                    colors.surfaceVariant.copy(alpha = 0.1f)
                )
         */
    }

    val background by animateColorAsState(
        targetBg.let { if (isDark) it.copy(0.2f) else it }
    )
    val tint by animateColorAsState(
        targetTint.let { if (isDark) it.copy(alpha = 0.8f) else it }
    )

    ElevatedCard(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.elevatedCardColors(containerColor = background),
        elevation= CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Crossfade(targetState = targetIcon) { icon ->
                Icon(icon, contentDescription = weatherInfo.conditionText, tint = tint, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(
                    text = weatherInfo.temperature
                        ?.let { stringResource(R.string.temperature_format, it.toInt()) }
                        ?: "--°C",
                    style = MaterialTheme.typography.titleMedium.copy(color = tint)
                )
                Text(
                    text = weatherInfo.conditionText,
                    style = MaterialTheme.typography.bodySmall.copy(color = tint)
                )
            }
        }
    }
}

@Preview
@Composable
fun AnimatedWeatherBadgePreview() {
    val weatherInfo = BikeWeatherInfo(
        windDegree = 180,
        windSpeed = 15.0,
        conditionText = "Clear",
        temperature = 25.0,
        feelsLike = 26.0, humidity = 60)
    AnimatedWeatherBadge(weatherInfo)
}