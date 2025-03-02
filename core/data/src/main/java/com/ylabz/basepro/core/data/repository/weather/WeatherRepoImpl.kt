package com.ylabz.basepro.core.data.repository.weather

import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import javax.inject.Inject

class WeatherRepoImpl @Inject constructor(

) : WeatherRepo {


    // Old Remove
    override suspend fun openCurrentWeather(location: String):  OpenWeatherResponse? {
        return openFetchWeatherData(location)
    }



}