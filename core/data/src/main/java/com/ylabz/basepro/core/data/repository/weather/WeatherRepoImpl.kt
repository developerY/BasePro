package com.ylabz.basepro.core.data.repository.weather

import com.ylabz.basepro.core.data.repository.weather.openFetchWeatherByCoords
import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import javax.inject.Inject

class WeatherRepoImpl @Inject constructor(

) : WeatherRepo {

    override suspend fun openCurrentWeatherByCoords(lat: Double, lon: Double):  OpenWeatherResponse? {
        return openFetchWeatherByCoords(lat, lon)
    }


    // Old Remove
    override suspend fun openCurrentWeatherByCity(location: String):  OpenWeatherResponse? {
        return openFetchWeatherByCityData(location)
    }



}