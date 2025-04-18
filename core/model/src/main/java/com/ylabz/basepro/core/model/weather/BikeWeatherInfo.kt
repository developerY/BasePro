package com.ylabz.basepro.core.model.weather

data class BikeWeatherInfo(
    val windDegree: Int,
    val windSpeed: Double,
    val conditionText: String,
    val conditionDescription: String?,
    val conditionIcon: String?,
    val temperature: Double?,
    val feelsLike: Double?,
    val humidity: Int?
)
