package com.ylabz.basepro.applications.bike.database.di

import android.content.Context
import androidx.room.Room
import com.ylabz.basepro.applications.bike.database.BikeRideDatabase
import com.ylabz.basepro.applications.bike.database.BikeRideDao
import com.ylabz.basepro.applications.bike.database.BikeRideRepo
import com.ylabz.basepro.applications.bike.database.repository.BikeRideRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.jvm.java


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Module
    @InstallIn(SingletonComponent::class)
    object DatabaseModule {

        @RideDatabase
        @Provides @Singleton
        fun provideBikeRideDB(@ApplicationContext ctx: Context): BikeRideDatabase =
            Room
                .databaseBuilder(ctx, BikeRideDatabase::class.java, BikeRideDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()

        // ✅ Match the RideDatabase qualifier
        @Provides @Singleton
        fun provideBikeRideDao(@RideDatabase db: BikeRideDatabase): BikeRideDao =
            db.bikeRideDao

        @Provides @Singleton
        fun provideBikeRideRepository(rideDao: BikeRideDao): BikeRideRepo =
            BikeRideRepoImpl(rideDao)
    }

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RideDatabase
