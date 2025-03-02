package com.ylabz.basepro.feature.weather.ui.components.combine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class WeatherCondition {
    SUNNY, RAINY, SNOWY
}


@Composable
fun UnifiedWeatherCard(
    weatherCondition: WeatherCondition,
    temperature: Double,
    conditionText: String,        // e.g. "Sunny", "Rainy", "Snowy"
    location: String,             // e.g. "Los Angeles, CA"
    windDegree: Int,
    modifier: Modifier = Modifier
) {
    val cardHeight = 120.dp
    val cardWidth = 240.dp

    // State for the raindrops or snowflakes if needed
    val showRainOrSnow = (weatherCondition == WeatherCondition.RAINY || weatherCondition == WeatherCondition.SNOWY)
    val effectParticles = remember(showRainOrSnow) {
        if (showRainOrSnow) {
            // More or fewer particles if you like
            val count = if (weatherCondition == WeatherCondition.RAINY) 250 else 200
            List(count) {
                WeatherParticle(
                    screenWidth = 600f,
                    screenHeight = cardHeight.value,
                    // Smaller size range for snow vs. rain, for example
                    sizeRange = if (weatherCondition == WeatherCondition.RAINY) 2f..6f else 1f..3f,
                    speedRange = if (weatherCondition == WeatherCondition.RAINY) 3f..7f else 1f..4f
                )
            }
        } else {
            emptyList()
        }
    }

    // Animate the particles if we have any (rain or snow)
    LaunchedEffect(showRainOrSnow) {
        while (showRainOrSnow) {
            effectParticles.forEach { particle ->
                particle.move()
                if (particle.y > cardHeight.value * 3) {
                    particle.resetPosition(cardHeight.value)
                    particle.startSplash()
                }
            }
            delay(16L) // ~60 FPS
        }
    }



    Card(
        modifier = modifier
            //.fillMaxWidth()
            .width(cardWidth)
            .height(cardHeight)
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 1) Rain or Snow background effect
            if (showRainOrSnow) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    effectParticles.forEach { p ->
                        if (p.isSplashing) {
                            drawCircle(
                                color = if (weatherCondition == WeatherCondition.RAINY)
                                    Color(0x9E0288D1) else Color.White.copy(alpha = 0.8f),
                                radius = p.splashRadius / 2,
                                center = Offset(p.x, size.height - p.splashRadius)
                            )
                        } else {
                            // For rain, draw lines. For snow, draw circles.
                            if (weatherCondition == WeatherCondition.RAINY) {
                                drawLine(
                                    color = Color(0x900288D1),
                                    start = Offset(p.x, p.y),
                                    end = Offset(p.x, p.y + p.size * 4),
                                    strokeWidth = 2f
                                )
                            } else {
                                drawCircle(
                                    color = Color.White,
                                    radius = p.size,
                                    center = Offset(p.x, p.y)
                                )
                            }
                        }
                    }
                }
            }

            // 2) If sunny, place a small sun icon in the top-left
            if (weatherCondition == WeatherCondition.SUNNY) {
                Image(
                    painter = painterResource(id = android.R.drawable.btn_star_big_on), // Replace with your sun icon
                    contentDescription = "Sun Icon",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .size(32.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // 3) The main text (temp, condition, location) in the center
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(start = 56.dp)  // to avoid overlapping the sun icon
            ) {
                Text(
                    text = "${"%.1f".format(temperature)}°C",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Text(
                    text = conditionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // 4) The wind dial in the bottom-left corner (abbreviated "WD")
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                WindDirectionDialUni(degree = windDegree.toFloat())
                // Optional: Abbreviation "WD" or remove entirely
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Text(
                        text = "WD",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UnifiedWeatherCardPreview() {
    UnifiedWeatherCard(
        weatherCondition = WeatherCondition.RAINY,
        temperature = 25.0,
        conditionText = "Rain",
        location = "Los Angeles, CA",
        windDegree = 120
    )
}




