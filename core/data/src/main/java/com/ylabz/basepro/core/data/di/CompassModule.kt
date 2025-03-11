package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.travel.CompassRepository
import com.ylabz.basepro.core.data.repository.travel.CompassRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CompassModule {

    @Binds
    @Singleton
    abstract fun bindCompassRepository(
        impl: CompassRepositoryImpl
    ): CompassRepository
}
