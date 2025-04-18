package com.ylabz.basepro.core.data.repository.weather

import com.ylabz.basepro.core.model.weather.Clouds
import com.ylabz.basepro.core.model.weather.Coord
import com.ylabz.basepro.core.model.weather.Main
import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import com.ylabz.basepro.core.model.weather.Rain
import com.ylabz.basepro.core.model.weather.Snow
import com.ylabz.basepro.core.model.weather.Sys
import com.ylabz.basepro.core.model.weather.WeatherOne
import com.ylabz.basepro.core.model.weather.Wind
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DemoWeatherRepoImpl @Inject constructor() : WeatherRepo {

    override suspend fun openCurrentWeatherByCity(location: String): OpenWeatherResponse? {
        // Ignore location, return a demo response
        return demoResponse(lat = 0.0, lon = 0.0, name = location)
    }

    override suspend fun openCurrentWeatherByCoords(lat: Double, lon: Double): OpenWeatherResponse? {
        // Return a demo response for the given coords
        return demoResponse(lat = lat, lon = lon, name = "Demoville")
    }

    private fun demoResponse(lat: Double, lon: Double, name: String) = OpenWeatherResponse(
        coord    = Coord(lon = lon, lat = lat),
        weather = listOf(
            WeatherOne(
                id = 501,
                main = "Rain",
                description = "moderate rain",
                icon = "10d"
            )
        ),
        base      = "stations",
        main      = Main(
            temp = 18.5,
            feels_like = 17.0,
            temp_min = 17.0,
            temp_max = 20.0,
            pressure = 1012,
            humidity = 82
        ),
        visibility = 10000,
        wind      = Wind(
            speed = 5.5,   // m/s
            deg = 120,
            gust = 7.0
        ),
        clouds    = Clouds(all = 75),
        rain      = Rain(`1h` = 0.5, `3h` = 1.2),
        snow      = Snow(`1h` = null, `3h` = null),
        dt        = System.currentTimeMillis() / 1000,
        sys       = Sys(
            type = 1,
            id = 1234,
            country = "US",
            sunrise = 1_688_000_000,
            sunset = 1_688_036_000
        ),
        timezone  = 0,
        id        = 999_999,
        name      = name,
        cod       = 200
    )

}
