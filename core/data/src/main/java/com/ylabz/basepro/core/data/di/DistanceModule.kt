package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.travel.DistanceRepository
import com.ylabz.basepro.core.data.repository.travel.DistanceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DistanceModule {

    @Binds
    @Singleton
    abstract fun bindDistanceRepository(
        impl: DistanceRepositoryImpl
    ): DistanceRepository
}
