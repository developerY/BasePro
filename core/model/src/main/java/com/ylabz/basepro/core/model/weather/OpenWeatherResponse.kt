package com.ylabz.basepro.core.model.weather

data class OpenWeatherResponse(
    val coord: Coord,
    val weatherOne: List<WeatherOne>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val rain: Rain?,
    val snow: Snow?,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class Snow(val `1h`: Double?,val `3h`: Double?)
data class Rain(val `1h`: Double?,val `3h`: Double?)
data class Coord(val lon: Double, val lat: Double)
data class WeatherOne(val id: Int, val main: String, val description: String, val icon: String)
data class Main(val temp: Double, val feels_like: Double, val temp_min: Double, val temp_max: Double, val pressure: Int, val humidity: Int)
data class Wind(val speed: Double, val deg: Int, val gust: Double)
data class Clouds(val all: Int)
data class Sys(val type: Int, val id: Int, val country: String, val sunrise: Long, val sunset: Long)