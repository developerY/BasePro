package com.ylabz.basepro.core.data.di

// Note the specific imports
import com.ylabz.basepro.core.data.repository.sensor.glucose.BleGlucoseRepository
import com.ylabz.basepro.core.data.repository.sensor.glucose.GlucoseRepository
import com.ylabz.basepro.core.data.repository.sensor.glucose.LibreNfcRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GlucoseModule {

    @Provides
    @Singleton
    fun provideGlucoseRepository(
        // You can inject both implementations if you need a switching logic
        bleRepo: BleGlucoseRepository,
        libreRepo: LibreNfcRepository
    ): GlucoseRepository {
        // For now, returning Libre as the primary source
        return libreRepo
    }
}