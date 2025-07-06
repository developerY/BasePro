package com.ylabz.basepro.applications.bike.features.main.ui

// import com.ylabz.basepro.core.database.BaseProRepo  // Import your repository
import android.location.Location
import com.ylabz.basepro.core.data.repository.weather.WeatherRepo
import com.ylabz.basepro.core.model.weather.BikeWeatherInfo
import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import javax.inject.Inject
import javax.inject.Singleton


// 1) A new WeatherUseCase to fetch & hold current weather once
@Singleton
class WeatherUseCase @Inject constructor(
    private val weatherRepo: WeatherRepo
) {
    // keep one cached result per app run
    private var lastWeather: BikeWeatherInfo? = null

    suspend operator fun invoke(lat: Double, lng: Double): BikeWeatherInfo? {
        return getWeather(lat, lng)
    }

    /** One‐shot fetch of BikeWeatherInfo or null on error */
    private suspend fun getWeather(lat: Double, lng: Double): BikeWeatherInfo? {
        // if we’ve already fetched once, just return it
        lastWeather?.let { return it }

        // otherwise fetch and cache
        val fetched = runCatching {
            weatherRepo
                .openCurrentWeatherByCoords(lat, lng)
                ?.toBikeWeatherInfo()
        }.getOrNull()

        lastWeather = fetched
        return fetched
    }
}

// Reuse your existing mapper:
private fun OpenWeatherResponse.toBikeWeatherInfo(): BikeWeatherInfo =
    BikeWeatherInfo(
        windDegree           = wind.deg,
        windSpeed            = (wind.speed * 3.6f),
        conditionText        = weather.firstOrNull()?.main.orEmpty(),
        conditionDescription = weather.firstOrNull()?.description.orEmpty(),
        conditionIcon        = weather.firstOrNull()?.icon.orEmpty(),
        temperature          = main.temp,
        feelsLike            = main.feels_like,
        humidity             = main.humidity
    )
