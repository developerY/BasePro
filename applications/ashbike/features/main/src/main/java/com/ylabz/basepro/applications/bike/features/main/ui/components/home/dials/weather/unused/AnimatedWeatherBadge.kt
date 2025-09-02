package com.ylabz.basepro.applications.bike.features.main.ui.components.home.dials.weather.unused
//import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo


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
    val animatedBg by animateColorAsState(targetValue = rawBg, tween(600))

    val containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    val contentColor = animatedTint

    // 3) card on top of your blue dash, always light container so it pops
    ElevatedCard(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(animatedBg),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(
            Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 4) crossfade the icon, tint it with animatedTint
            Column( // make the Column fill its parent
                verticalArrangement = Arrangement.Center,    // center children top↕bottom
                horizontalAlignment = Alignment.CenterHorizontally // center children left↔right
            ) {
                Crossfade(targetState = icon) { currentIcon ->
                    Icon(
                        imageVector = currentIcon,
                        contentDescription = weatherInfo.conditionText,
                        tint = animatedTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = weatherInfo.conditionText,
                    style = MaterialTheme.typography.titleMedium.copy(color = animatedTint)
                )

            }
            Text(
                text = weatherInfo.temperature
                    ?.let { "${it.toInt()}°C" }
                    ?: "--°C",
                style = MaterialTheme.typography.headlineMedium.copy(color = animatedTint)
            )
        }
    }
}


/*
@Preview
@Composable
fun AnimatedWeatherBadgePreview() {
    val weatherInfo = BikeWeatherInfo(
        windDegree = 180,
        windSpeed = 15.0,
        conditionText = "Clear",
        conditionDescription = "Clear sky",
        conditionIcon = "01d",
        temperature = 25.0,
        feelsLike = 26.0,
        humidity = 60
    )
    AnimatedWeatherBadge(weatherInfo)
}

 */