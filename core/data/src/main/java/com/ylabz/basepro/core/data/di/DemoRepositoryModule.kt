package com.ylabz.basepro.core.data.di

import com.ylabz.basepro.core.data.repository.travel.CompassRepository
import com.ylabz.basepro.core.data.repository.travel.DemoCompassRepositoryImpl
import com.ylabz.basepro.core.data.repository.travel.DemoUnifiedLocationRepositoryImpl
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Alternative place to put it
 * import com.ylabz.basepro.core.data.repository.location.DemoUnifiedLocationRepositoryImpl
 * import com.ylabz.basepro.core.data.repository.location.UnifiedLocationRepository
 * import com.ylabz.basepro.core.data.repository.compass.DemoCompassRepositoryImpl
 * import com.ylabz.basepro.core.data.repository.compass.CompassRepository
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUnifiedLocationRepository(
        demoImpl: DemoUnifiedLocationRepositoryImpl
    ): UnifiedLocationRepository

    @Binds
    @Singleton
    abstract fun bindCompassRepository(
        demoImpl: DemoCompassRepositoryImpl
    ): CompassRepository
}
