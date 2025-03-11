package com.ylabz.basepro.core.data.di

import android.content.Context
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepositoryImpl
import com.ylabz.basepro.core.data.repository.travel.UnifiedLocationRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UnifiedLocationModule {


    @Singleton
    @Provides
    fun unifiedLocationRepo(@ApplicationContext context: Context): UnifiedLocationRepository {
        return UnifiedLocationRepositoryImpl(context)
    }

}