package com.ylabz.basepro.core.data.repository.weather

import android.os.Build
import androidx.annotation.RequiresExtension
import com.ylabz.basepro.core.model.weather.OpenWeatherResponse
import com.ylabz.basepro.core.data.api.interfaces.OpenWeatherService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.ylabz.basepro.core.network.BuildConfig.OPEN_WEATHER_API_KEY
import retrofit2.HttpException


suspend fun openFetchWeatherData(cityName: String) : OpenWeatherResponse? {
    var call : OpenWeatherResponse? = null

    try {
        call = OpenRetrofitClient.openWeatherService.getCurrentOpenWeather(cityName, OPEN_WEATHER_API_KEY)
    } catch (e: HttpException) {
        if (e.code() == 404) {
            print("City not found")
        } else {
            print("Error: ${e.message()}")
        }
        call = null
    } catch (e: Exception) {
        print("An unexpected error occurred")
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