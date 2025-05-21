package com.ylabz.basepro.core.data.repository.weather

import android.util.Log
import com.ylabz.basepro.core.data.repository.weather.openFetchWeatherByCoords
import com.ylabz.basepro.core.model.weather.Clouds
import com.ylabz.basepro.core.model.weather.Coord
import com.ylabz.basepro.core.model.weather.Main
import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import com.ylabz.basepro.core.model.weather.Sys
import com.ylabz.basepro.core.model.weather.Wind
import javax.inject.Inject

class WeatherRepoImpl @Inject constructor(

) : WeatherRepo {

    override suspend fun openCurrentWeatherByCoords(
        lat: Double,
        lon: Double
    ): OpenWeatherResponse? {
        Log.d("WeatherRepoImpl", "openCurrentWeatherByCoords: $lat, $lon")
        return openFetchWeatherByCoords(lat, lon)
        /*return OpenWeatherResponse(
            coord = Coord(lon, lat),
            weather = listOf(),
            base = "",
            main = Main(0.0, 0.0, 0.0, 0.0, 0, 0),
            visibility = 0,
            wind = Wind(0.0, deg = 0, gust = 0.0),
            clouds = Clouds(0),
            rain = null,
            snow = null,
            dt = 0,
            sys = Sys(0, 0, "", 0, 0),
            timezone = 0,
            id = 0,
            name = "",
            cod = 0
        )*/

    }


    // Old Remove
    override suspend fun openCurrentWeatherByCity(location: String): OpenWeatherResponse? {
        Log.d("WeatherRepoImpl", "openCurrentWeatherByCoords: $location")
        return openFetchWeatherByCityData(location)
        /*return OpenWeatherResponse(
            coord = Coord(lon = 0.0, lat = 0.0),
            weather = listOf(),
            base = "",
            main = Main(0.0, 0.0, 0.0, 0.0, 0, 0),
            visibility = 0,
            wind = Wind(0.0, deg = 0, gust = 0.0),
            clouds = Clouds(0),
            rain = null,
            snow = null,
            dt = 0,
            sys = Sys(0, 0, "", 0, 0),
            timezone = 0,
            id = 0,
            name = "",
            cod = 0
        )*/
    }
}