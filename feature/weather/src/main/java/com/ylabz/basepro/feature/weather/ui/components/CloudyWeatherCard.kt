package com.ylabz.basepro.feature.weather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToLong
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CloudyWeatherCard(
    temperature: Double,
    location: String,
    modifier: Modifier = Modifier
) {
    val cardHeight = 200.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFB0BEC5) // A blue-grey tone for overcast skies
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFCFD8DC), Color(0xFFB0BEC5))
                    )
                )
        ) {
            // Cloud icon in the top-left
            Icon(
                imageVector = Icons.Filled.Cloud,
                contentDescription = "Cloudy",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )
            // Draw some static cloud shapes in the background
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw a few "fluffy" clouds
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = 30.dp.toPx(),
                    center = Offset(x = size.width * 0.7f, y = size.height * 0.3f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = 20.dp.toPx(),
                    center = Offset(x = size.width * 0.6f, y = size.height * 0.4f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.45f),
                    radius = 25.dp.toPx(),
                    center = Offset(x = size.width * 0.8f, y = size.height * 0.35f)
                )
            }
            // Center text content for temperature, condition, and location
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${temperature.roundToLong()}°C",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 36.sp,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cloudy",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CloudyWeatherCardPreview() {
    MaterialTheme {
        CloudyWeatherCard(
            temperature = 18.0,
            location = "Seattle, WA"
        )
    }
}
