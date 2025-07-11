package com.ylabz.basepro.core.data.repository.weather

import android.util.Log
import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import com.ylabz.basepro.core.data.api.interfaces.OpenWeatherService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.ylabz.basepro.core.network.BuildConfig.OPEN_WEATHER_API_KEY
import retrofit2.HttpException


suspend fun openFetchWeatherByCityData(cityName: String) : OpenWeatherResponse? {
    var call : OpenWeatherResponse? = null

    try {
        call = OpenRetrofitClient.openWeatherService.getCurrentOpenWeatherByCity(cityName, OPEN_WEATHER_API_KEY)
    } catch (e: HttpException) {
        if (e.code() == 404) {
            Log.d("WeatherRepoImpl", "City not found")
        } else {
            Log.d("WeatherRepoImpl", "Error: ${e.message()}")
        }
        call = null
    } catch (e: Exception) {
        Log.d("WeatherRepoImpl", "An unexpected error occurred")
        call = null
    }
    return call
}


suspend fun openFetchWeatherByCoords(lat: Double, lon: Double) : OpenWeatherResponse? {
    var call : OpenWeatherResponse? = null

    try {
        call = OpenRetrofitClient.openWeatherService.getCurrentOpenWeatherByCoords(lat = lat, lon = lon, OPEN_WEATHER_API_KEY)
    } catch (e: HttpException) {
        if (e.code() == 404) {
            Log.d("WeatherRepoImpl", "City not found")
        } else {
            Log.d("WeatherRepoImpl", "Error: ${e.message()}")
        }
        call = null
    } catch (e: Exception) {
        Log.d("WeatherRepoImpl", "An unexpected error occurred")
        call = null
    }
    return call
}
private object OpenRetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val openWeatherService: OpenWeatherService = retrofit.create(OpenWeatherService::class.java)
}