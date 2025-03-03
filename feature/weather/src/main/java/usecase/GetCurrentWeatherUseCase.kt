package usecase

import com.ylabz.basepro.core.data.repository.weather.WeatherRepo
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepo: WeatherRepo
) {
    suspend operator fun invoke(city: String): WeatherInfo {
        val response = weatherRepo.openCurrentWeather(city)
        // Map the API response to our domain model.
        return WeatherInfo(
            temperature = response?.main?.temp,
            condition = response?.weatherOne?.firstOrNull()?.main ?: "Clear",
            location = "${response?.name}, ${response?.sys?.country}",
            windDegree = response?.wind?.deg,
            windSpeed = response?.wind?.speed?.toFloat()
        )
    }
}

data class WeatherInfo(
    val temperature: Double?,
    val condition: String?,
    val location: String?,
    val windDegree: Int?,
    val windSpeed: Float?,
    // Add other properties as needed
)

