package com.ylabz.basepro.feature.weather.ui.components.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.ylabz.basepro.feature.weather.ui.components.rain.RainVolumeCard
import com.ylabz.basepro.feature.weather.ui.components.snow.BetterSnowVolumeCardAI
import com.ylabz.basepro.feature.weather.ui.components.sun.TemperatureCardAI
import com.ylabz.basepro.feature.weather.ui.components.wind.WindCard

// Import your custom cards from their packages
// For example:
// import com.ylabz.windwatersnow.wind.ui.components.cards.temp.TemperatureCardAI
// import com.ylabz.windwatersnow.wind.ui.components.cards.rain.RainVolumeCardAI
// import com.ylabz.windwatersnow.wind.ui.components.cards.snow.BetterSnowVolumeCardAI
// import com.ylabz.windwatersnow.wind.ui.components.cards.Wind.ImprovedWindCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreenCards(
    temperature: Double = 22.0,
    rainVolume: Double = 15.0,
    snowVolume: Double = 10.0,
    windDegree: Int = 45
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Current Weather") }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                TemperatureCardAI(temp = temperature)
                Spacer(modifier = Modifier.height(16.dp))
                RainVolumeCard(volume = rainVolume)
                Spacer(modifier = Modifier.height(16.dp))
                BetterSnowVolumeCardAI(volume = snowVolume)
                Spacer(modifier = Modifier.height(16.dp))
                WindCard(windDegree = windDegree)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenCardsPreview() {
    WeatherScreenCards()
}
