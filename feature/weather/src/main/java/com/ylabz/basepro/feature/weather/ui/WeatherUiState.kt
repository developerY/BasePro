package com.ylabz.basepro.feature.weather.ui

import com.ylabz.basepro.core.model.weather.Weather

// Define UI states for weather.
sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val weather: Weather,
        val location: LatLng? = null
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}