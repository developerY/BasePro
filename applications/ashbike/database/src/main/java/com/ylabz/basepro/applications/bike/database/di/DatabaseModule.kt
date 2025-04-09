package com.ylabz.basepro.applications.bike.database.di

import android.content.Context
import androidx.room.Room
import com.ylabz.basepro.applications.bike.database.BikeDatabase
import com.ylabz.basepro.applications.bike.database.BikeProDB
import com.ylabz.basepro.applications.bike.database.BikeProDao
import com.ylabz.basepro.applications.bike.database.OtherDatabase
import com.ylabz.basepro.applications.bike.database.repository.BikeProRepoImpl
import com.ylabz.basepro.applications.bike.database.BikeProRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): BikeProDB {
        return Room.databaseBuilder(
            appContext,
            BikeProDB::class.java,
            BikeProDB.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideBikeProDao(BikeProDB: BikeProDB): BikeProDao {
        return BikeProDB.baseproDao
    }

    @Provides
    @Singleton
    fun provideBikeProRepository(BikeProDao: BikeProDao): BikeProRepo {
        return BikeProRepoImpl(BikeProDao)
    }


    @BikeDatabase
    @Provides
    @Singleton
    fun provideBikeDatabase(@ApplicationContext context: Context): BikeProDB {
        return Room.databaseBuilder(
            context,
            BikeProDB::class.java,
            "bike_database.db"
        ).build()
    }

    @OtherDatabase
    @Provides
    @Singleton
    fun provideOtherDatabase(@ApplicationContext context: Context): BikeProDB {
        return Room.databaseBuilder(
            context,
            BikeProDB::class.java,
            "list_database.db"
        ).build()
    }

}