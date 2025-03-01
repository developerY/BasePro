package com.ylabz.basepro.feature.weather.ui

import com.ylabz.basepro.core.model.weather.Weather
import com.google.android.gms.maps.model.LatLng



// Define UI states for weather.
sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val weather: Weather,
        val settings: Map<String, List<String>>,
        val location: LatLng? = null
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}