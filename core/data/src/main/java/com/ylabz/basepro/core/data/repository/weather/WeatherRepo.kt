package com.ylabz.basepro.core.data.repository.weather

import com.ylabz.basepro.core.model.weather.OpenWeatherResponse


interface WeatherRepo {
    suspend fun openCurrentWeather(location: String): OpenWeatherResponse?
}