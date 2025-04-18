package com.ylabz.basepro.applications.bike.ui.components.home.dials
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    // 1) pick icon, tint & pastel‑bg per conditionText
    val (icon, rawTint, rawBg) = remember(weatherInfo.conditionText) {
        when {
            weatherInfo.conditionText.contains("rain", true) ->
                Triple(
                    Icons.Default.Umbrella,
                    Color(0xFF01579B),                         // deep rainy blue
                    Color(0xFF81D4FA).copy(alpha = 0.3f)       // light sky blue bg
                )
            weatherInfo.conditionText.contains("clear", true)
                    || weatherInfo.conditionText.contains("sun", true) ->
                Triple(
                    Icons.Default.WbSunny,
                    Color(0xFFF57F17),                         // sunny gold
                    Color(0xFFFFF59D).copy(alpha = 0.3f)       // pale yellow bg
                )
            weatherInfo.conditionText.contains("cloud", true) ->
                Triple(
                    Icons.Default.Cloud,
                    Color(0xFF455A64),                         // slate gray
                    Color(0xFFCFD8DC).copy(alpha = 0.3f)       // pale gray bg
                )
            else ->
                Triple(
                    Icons.AutoMirrored.Filled.HelpOutline,
                    Color.Black,
                    Color.Black.copy(alpha = 0.1f)
                )
        }
    }

    // 2) animate tint & bg so changes cross‑fade smoothly
    val animatedTint by animateColorAsState(targetValue = rawTint, tween(600))
    val animatedBg   by animateColorAsState(targetValue = rawBg,  tween(600))

    // 3) card on top of your blue dash, always light container so it pops
    ElevatedCard(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(animatedBg),
        colors    = CardDefaults.elevatedCardColors(containerColor = animatedBg),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 4) crossfade the icon, tint it with animatedTint
            Crossfade(targetState = icon) { currentIcon ->
                Icon(
                    imageVector   = currentIcon,
                    contentDescription = weatherInfo.conditionText,
                    tint          = animatedTint,
                    modifier      = Modifier.size(24.dp)
                )
            }

            // 5) text: temperature + condition
            Column {
                Text(
                    text = weatherInfo.temperature
                        ?.let { "${it.toInt()}°C" }
                        ?: "--°C",
                    style = MaterialTheme.typography.titleMedium.copy(color = animatedTint)
                )
                Text(
                    text = weatherInfo.conditionText,
                    style = MaterialTheme.typography.bodySmall.copy(color = animatedTint)
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