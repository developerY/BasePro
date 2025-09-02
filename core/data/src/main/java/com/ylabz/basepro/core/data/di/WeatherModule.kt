package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.weather.DemoWeatherRepoImpl
import com.ylabz.basepro.core.data.repository.weather.WeatherRepo
import com.ylabz.basepro.core.data.repository.weather.WeatherRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WeatherRepositoryModule {


    @Binds
    @Singleton
    abstract fun bindsRealWeatherRepo(
        impl: WeatherRepoImpl
    ): WeatherRepo


    @Binds
    @Singleton
    @Named("demo")
    abstract fun bindsDemoWeatherRepo(
        impl: DemoWeatherRepoImpl
    ): WeatherRepo

}