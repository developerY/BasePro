package com.ylabz.basepro.applications.bike.database.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ylabz.basepro.applications.bike.database.BikeDatabase
import com.ylabz.basepro.applications.bike.database.BikeProDB
import com.ylabz.basepro.applications.bike.database.BikeProDao
import com.ylabz.basepro.applications.bike.database.BikeProEntity
import com.ylabz.basepro.applications.bike.database.OtherDatabase
import com.ylabz.basepro.applications.bike.database.repository.BikeProRepoImpl
import com.ylabz.basepro.applications.bike.database.BikeProRepo
import com.ylabz.basepro.applications.bike.database.BikeRideDatabase
import com.ylabz.basepro.applications.bike.database.RideDao
import com.ylabz.basepro.applications.bike.database.RideEntity
import com.ylabz.basepro.applications.bike.database.RideLocationEntity
import com.ylabz.basepro.applications.bike.database.converter.Converters
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
    fun provideBikeRideRepository(bikeRideDao: RideDao): BikeProRepo {
        return BikeRideRepoImpl(bikeRideDao)
    }
}


    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ProDatabase

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RideDatabase
