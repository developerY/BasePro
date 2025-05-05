package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeConnectivityRepository
import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeConnectivityRepositoryImpl
import com.ylabz.basepro.core.data.repository.bikeConnectivity.DemoBikeConnectivityRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRealBikeConnectivityRepository(
        //nfcReader: NfcReader,
        //bleAdapter: BleAdapter
    ): BikeConnectivityRepository = BikeConnectivityRepositoryImpl()//nfcReader, bleAdapter)

    @Singleton
    @Provides
    @Named("demo")
    fun provideDemoBikeConnectivityRepository(): BikeConnectivityRepository =
        DemoBikeConnectivityRepositoryImpl()

}
