package com.ylabz.basepro.feature.weather.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun WeatherBackground(
    condition: String,
    modifier: Modifier = Modifier
) {
    // Choose a gradient brush based on the weather condition.
    val brush = when {
        condition.equals("Clear", ignoreCase = true) ||
                condition.equals("Sunny", ignoreCase = true) -> {
            Brush.verticalGradient(
                colors = listOf(Color(0xFFFFF176), Color(0xFFFFD54F)) // Bright yellow/orange
            )
        }
        condition.equals("Clouds", ignoreCase = true) ||
                condition.equals("Cloudy", ignoreCase = true) -> {
            Brush.verticalGradient(
                colors = listOf(Color(0xFFCFD8DC), Color(0xFFB0BEC5)) // Blue-grey tones
            )
        }
        condition.equals("Rain", ignoreCase = true) -> {
            Brush.verticalGradient(
                colors = listOf(Color(0xFF90CAF9), Color(0xFF64B5F6)) // Cool blues for rain
            )
        }
        condition.equals("Snow", ignoreCase = true) -> {
            Brush.verticalGradient(
                colors = listOf(Color(0xFFE1F5FE), Color(0xFFB3E5FC)) // Light blue for snow
            )
        }
        else -> {
            // Fallback gradient
            Brush.verticalGradient(
                colors = listOf(Color.LightGray, Color.DarkGray)
            )
        }
    }
    Box(modifier = modifier.background(brush))
}
