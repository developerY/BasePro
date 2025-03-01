package com.ylabz.basepro.core.data.di

import android.content.Context
import com.ylabz.basepro.core.data.repository.location.LocationRepository
import com.ylabz.basepro.core.data.repository.location.LocationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository
}
