package com.ylabz.basepro.feature.weather.ui

import com.google.android.gms.maps.model.LatLng
import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import com.ylabz.basepro.core.model.weather.Weather


// Define UI states for weather.
sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val weatherOpen: OpenWeatherResponse?,
        var locationString: String = "Santa Barbara, US",
        val weather: Weather,
        val settings: Map<String, List<String>>,
        val location: LatLng? = null
    ) : WeatherUiState()

    data class Error(val message: String) : WeatherUiState()
}