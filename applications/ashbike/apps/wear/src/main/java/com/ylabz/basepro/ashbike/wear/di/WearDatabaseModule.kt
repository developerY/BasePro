package com.ylabz.basepro.ashbike.wear.di

import android.content.Context
import com.ylabz.basepro.applications.bike.database.BikeRideDao
import com.ylabz.basepro.applications.bike.database.BikeRideDatabase
import com.ylabz.basepro.applications.bike.database.di.RideDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WearDatabaseModule {

    // 1. You must annotate the provider so Hilt binds it to "@RideDatabase"
    @Provides
    @Singleton
    @RideDatabase
    fun provideBikeDatabase(@ApplicationContext context: Context): BikeRideDatabase {
        return BikeRideDatabase.getDatabase(context)
    }

    // 2. You must annotate the parameter so Hilt knows to look for the "@RideDatabase" version
    @Provides
    @Singleton
    fun provideBikeDao(@RideDatabase database: BikeRideDatabase): BikeRideDao =
        database.bikeRideDao
}