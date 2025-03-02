package com.ylabz.basepro.feature.weather.ui

sealed class WeatherEvent {
    object LoadBike : WeatherEvent()
    data class UpdateSetting(val settingKey: String, val settingValue: String) : WeatherEvent()
    object DeleteAllEntries : WeatherEvent()  // Existing event
    object FetchWeather : WeatherEvent()       // New event for fetching weather
}

