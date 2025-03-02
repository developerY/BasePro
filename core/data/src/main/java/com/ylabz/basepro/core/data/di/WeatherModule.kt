package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.weather.WeatherRepo
import com.ylabz.basepro.core.data.repository.weather.WeatherRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {


    @Singleton
    @Provides
    fun bindsWeatherRepo(): WeatherRepo {
        return WeatherRepoImpl()
    }

}