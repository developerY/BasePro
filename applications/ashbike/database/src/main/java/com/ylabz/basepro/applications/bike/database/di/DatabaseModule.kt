package com.ylabz.basepro.applications.bike.database.di

import android.content.Context
import androidx.room.Room
import com.ylabz.basepro.applications.bike.database.BikeProDB
import com.ylabz.basepro.applications.bike.database.BikeProDao
import com.ylabz.basepro.applications.bike.database.repository.BikeProRepoImpl
import com.ylabz.basepro.applications.bike.database.BikeProRepo
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

    @ProDatabase
    @Provides
    @Singleton
    fun provideBikeProDB(@ApplicationContext ctx: Context): BikeProDB =
        Room.databaseBuilder(ctx, BikeProDB::class.java, BikeProDB.DATABASE_NAME)
            .fallbackToDestructiveMigration() // dev‑only
            .build()

    @Provides
    @Singleton
    fun provideBikeProDao(@ProDatabase db: BikeProDB): BikeProDao =
        db.bikeProDao

    @RideDatabase
    @Provides
    @Singleton
    fun provideBikeRideDB(@ApplicationContext ctx: Context): BikeRideDatabase =
        Room.databaseBuilder(ctx, BikeRideDatabase::class.java, BikeRideDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration() // dev‑only
            .build()

    @Provides
    @Singleton
    fun provideBikeProRepository(BikeProDao: BikeProDao): BikeProRepo {
        return BikeProRepoImpl(BikeProDao)
    }


    @Provides
    @Singleton
    fun provideBikeRideRepository(bikeRideDao: BikeRideDao): BikeRideRepo {
        return BikeRideRepoImpl(bikeRideDao)
    }
}


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ProDatabase

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RideDatabase
