package com.ylabz.basepro.feature.weather.ui

sealed class WeatherEvent {
    object LoadBike : WeatherEvent()
    data class UpdateSetting(val settingKey: String, val settingValue: String) : WeatherEvent()
    object DeleteAllEntries : WeatherEvent()  // New event to delete all entries
}

