package com.ylabz.basepro.core.model.weather

data class BikeWeatherInfo(
    val windDegree: Int,
    val windSpeed: Double,
    val conditionText: String,
    val temperature : Double //  = it.main.temp                 // ← pull the temperature (°C)
)
