package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepository
import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepositoryRotVecImpl
import com.ylabz.basepro.core.data.repository.travel.compass.DemoCompassRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class CompassRepositoryModule {

    @Binds
    @Singleton
    @Named("real")
    abstract fun bindRealCompassRepository(
        impl: CompassRepositoryRotVecImpl
    ): CompassRepository

    @Binds
    @Singleton
    @Named("demo")
    abstract fun bindDemoCompassRepository(
        impl: DemoCompassRepositoryImpl
    ): CompassRepository
}
