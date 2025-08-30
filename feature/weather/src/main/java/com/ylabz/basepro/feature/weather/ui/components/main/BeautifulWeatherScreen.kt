package com.ylabz.basepro.feature.weather.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
//import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.core.model.weather.Weather

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeautifulWeatherScreenOne(weather: Weather) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Current Weather") }
            )
        },
        content = { innerPadding ->
            // A vertical gradient background.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF4FC3F7), // Light blue
                                Color(0xFF81D4FA)  // Even lighter blue
                            )
                        )
                    )
                    .padding(innerPadding)
            ) {
                BeautifulWeatherCardOne(
                    weather = weather,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
    )
}

@Composable
fun BeautifulWeatherCardOne(weather: Weather, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Weather Icon in a circular container.
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.WbSunny,
                    contentDescription = "Sunny",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Temperature display.
            Text(
                text = "${weather.temperature}°C",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Weather description.
            Text(
                text = weather.description,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Location information.
            Text(
                text = weather.location,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun PreviewBeautifulWeatherScreenOne() {
    MaterialTheme {
        BeautifulWeatherScreenOne(
            weather = Weather(
                temperature = 28.0,
                description = "Sunny",
                location = "Los Angeles, CA",
                iconUrl = null,
            )
        )
    }
}
*/