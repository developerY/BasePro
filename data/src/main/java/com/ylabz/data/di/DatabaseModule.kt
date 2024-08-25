package com.ylabz.data.di

import android.content.Context
import androidx.room.Room
import com.ylabz.data.TwinCamDB
import com.ylabz.data.TwinCamDao
import com.ylabz.data.TwinCamRepo
import com.ylabz.data.repository.TwinCamRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): TwinCamDB {
        return Room.databaseBuilder(
            appContext,
            TwinCamDB::class.java,
            TwinCamDB.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideTwinCamDao(TwinCamDB: TwinCamDB): TwinCamDao {
        return TwinCamDB.twinCamDao
    }

    @Provides
    @Singleton
    fun provideTwinCamRepository(TwinCamDao: TwinCamDao): TwinCamRepo {
        return TwinCamRepoImpl(TwinCamDao)
    }




}