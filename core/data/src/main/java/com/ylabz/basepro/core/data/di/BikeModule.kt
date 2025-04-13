package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeConnectivityRepository
import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeConnectivityRepositoryImpl
import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeRepository
import com.ylabz.basepro.core.data.repository.bikeConnectivity.BikeRepositoryImpl
import com.ylabz.basepro.core.data.repository.bikeConnectivity.DemoBikeConnectivityRepositoryImpl
import com.ylabz.basepro.core.data.repository.bikeConnectivity.DemoBikeRepositoryImpl
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import com.ylabz.basepro.core.data.repository.travel.compass.CompassRepository
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
    @Named("realConnectivity")
    fun provideRealBikeConnectivityRepository(
        //nfcReader: NfcReader,
        //bleAdapter: BleAdapter
    ): BikeConnectivityRepository = BikeConnectivityRepositoryImpl()//nfcReader, bleAdapter)

    @Singleton
    @Provides
    @Named("demoConnectivity")
    fun provideDemoBikeConnectivityRepository(): BikeConnectivityRepository =
        DemoBikeConnectivityRepositoryImpl()

    @Singleton
    @Provides
    @Named("realBike")
    fun provideRealBikeRepository(
        unifiedLocationRepository: UnifiedLocationRepository,
        compassRepository: CompassRepository
    ): BikeRepository = BikeRepositoryImpl(unifiedLocationRepository, compassRepository)

    @Singleton
    @Provides
    @Named("demoBike")
    fun provideDemoBikeRepository(
        @Named("demo") demoLocationRepository: UnifiedLocationRepository,
        @Named("demo") demoCompassRepository: CompassRepository
    ): BikeRepository = DemoBikeRepositoryImpl(demoLocationRepository, demoCompassRepository)
}
