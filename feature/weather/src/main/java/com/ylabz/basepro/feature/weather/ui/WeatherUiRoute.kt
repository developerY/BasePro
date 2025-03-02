package com.ylabz.basepro.feature.weather.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ylabz.basepro.feature.weather.ui.components.combine.UnifiedWeatherCard
import com.ylabz.basepro.feature.weather.ui.components.combine.WeatherCondition


@Composable
fun WeatherUiRoute(
    modifier: Modifier = Modifier,
    navTo: (String) -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value

    when (uiState) {
        is WeatherUiState.Loading -> {
            LoadingScreen()
        }
        is WeatherUiState.Error -> {
            ErrorScreen(errorMessage = uiState.message) {
                viewModel.onEvent(WeatherEvent.LoadBike)
            }
        }
        is WeatherUiState.Success -> {

            /*UnifiedWeatherCard(
                weatherCondition = uiState.weatherOpen.weatherCondition,
                temperature = uiState.weatherOpen.temperature,
                conditionText = uiState.weatherOpen.conditionText,
                location = uiState.weatherOpen.location,
                windDegree = uiState.weatherOpen.windDegree
            )*/


            UnifiedWeatherCard(
                weatherCondition = WeatherCondition.RAINY,
                temperature = 25.0,
                conditionText = "Rain",
                location = "Los Angeles, CA",
                windDegree = 120
            )

            /*BikeHomeScreen(
               modifier = modifier,
               settings = uiState.settings,
               location = uiState.location,   // <-- Pass the location here
               onEvent = { event -> viewModel.onEvent(event) },
               navTo = navTo
           )*/
        }
    }
}

// These will be move to a common directory.
@Composable
fun LoadingScreen() {
    Text(text = "Loading...", modifier = Modifier.fillMaxSize())
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Error: $errorMessage",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Retry",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .clickable { onRetry() }
                .padding(vertical = 8.dp)
        )
    }
}

