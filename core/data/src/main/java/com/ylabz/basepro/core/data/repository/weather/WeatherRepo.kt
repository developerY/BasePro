package com.ylabz.basepro.core.data.repository.weather

import com.ylabz.basepro.core.model.weather.OpenWeatherResponse


interface WeatherRepo {
    suspend fun openCurrentWeatherByCity(location: String): OpenWeatherResponse?
    suspend fun openCurrentWeatherByCoords(lat: Double, lon: Double): OpenWeatherResponse?

}