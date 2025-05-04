package com.ylabz.basepro.core.data.di

import android.content.Context
import com.ylabz.basepro.core.data.repository.travel.DemoUnifiedLocationRepositoryImpl
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationHighPowerRepositoryImpl
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationLowPowerRepositoryImpl
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import dagger.Binds

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UnifiedLocationRepositoryModule {

    @Binds
    @Singleton @HighPower
    abstract fun bindHighPowerRepo(
        impl: UnifiedLocationHighPowerRepositoryImpl
    ): UnifiedLocationRepository

    @Binds
    @Singleton @LowPower
    abstract fun bindLowPowerRepo(
        impl: UnifiedLocationLowPowerRepositoryImpl
    ): UnifiedLocationRepository

    @Binds
    @Singleton @Demo
    abstract fun bindDemoRepo(
        impl: DemoUnifiedLocationRepositoryImpl
    ): UnifiedLocationRepository
}
