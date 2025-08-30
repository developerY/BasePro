package com.ylabz.basepro.feature.weather.ui.components.combine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
////import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.delay
import androidx.compose.ui.geometry.Size


enum class WeatherConditionUnif {
    SUNNY, RAINY, SNOWY, CLOUDY, CLEAR
}


@Composable
fun UnifiedWeatherCard(
    modifier: Modifier = Modifier,
    weatherCondition: WeatherConditionUnif,
    temperature: Double,
    conditionText: String,
    location: String,
    windDegree: Int,
) {
    var cardSize by remember { mutableStateOf(Size.Zero) }

    val showRainOrSnow = (weatherCondition == WeatherConditionUnif.RAINY || weatherCondition == WeatherConditionUnif.SNOWY)

    // We'll create or remember the particles only after we know if we need them (rain/snow).
    // The actual dimension logic will happen in LaunchedEffect once cardSize is known.
    val effectParticles = remember(showRainOrSnow) {
        if (showRainOrSnow) {
            // We’ll fill them in after we measure cardSize.
            mutableStateListOf<WeatherParticle>()
        } else {
            mutableStateListOf()
        }
    }

    // Animate or update the raindrops/snowflakes once cardSize is known.
    LaunchedEffect(cardSize, showRainOrSnow) {
        if (showRainOrSnow && cardSize.width > 0f && cardSize.height > 0f) {
            // Initialize the particles only once the card is measured.
            if (effectParticles.isEmpty()) {
                val count = if (weatherCondition == WeatherConditionUnif.RAINY) 250 else 200
                repeat(count) {
                    effectParticles.add(
                        WeatherParticle(
                            screenWidth = cardSize.width,
                            screenHeight = cardSize.height,
                            sizeRange = if (weatherCondition == WeatherConditionUnif.RAINY) 2f..6f else 1f..3f,
                            speedRange = if (weatherCondition == WeatherConditionUnif.RAINY) 3f..7f else 1f..4f
                        )
                    )
                }
            }
            while (true) {
                effectParticles.forEach { particle ->
                    particle.move()
                    if (particle.y > cardSize.height * 3) {
                        // Use cardSize.height for resetting position
                        particle.resetPosition(cardSize.height)
                        particle.startSplash()
                    }
                }
                delay(16L) // ~60 FPS
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)  // Force a real height so the card isn't 0px tall
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .onGloballyPositioned { layoutCoordinates ->
                // Convert the IntSize from layoutCoordinates.size to a Float-based Size
                cardSize = layoutCoordinates.size.toSize()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 1) Rain or Snow background effect
            if (showRainOrSnow && effectParticles.isNotEmpty()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    effectParticles.forEach { p ->
                        if (p.isSplashing) {
                            drawCircle(
                                color = if (weatherCondition == WeatherConditionUnif.RAINY)
                                    Color(0x9E0288D1)
                                else Color.White.copy(alpha = 0.8f),
                                radius = p.splashRadius / 2,
                                center = Offset(p.x, size.height - p.splashRadius)
                            )
                        } else {
                            // For rain, draw lines; for snow, circles
                            if (weatherCondition == WeatherConditionUnif.RAINY) {
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
            if (weatherCondition == WeatherConditionUnif.SUNNY) {
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

            // 3) Main text in the center
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(start = 56.dp)
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

            // 4) Wind dial in the bottom-left corner
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                val degree = 180
                val speed = 10.7
                WindDirectionDialWithSpeed(degree = degree, speed = speed)
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun UnifiedWeatherCardPreview() {
    UnifiedWeatherCard(
        weatherCondition = WeatherConditionUnif.RAINY,
        temperature = 25.0,
        conditionText = "Rain",
        location = "Los Angeles, CA",
        windDegree = 120
    )
}
*/




