package com.ylabz.basepro.data.di

import android.content.Context
import androidx.room.Room
import com.ylabz.basepro.data.BaseProDB
import com.ylabz.basepro.data.BaseProDao
import com.ylabz.basepro.data.BaseProRepo
import com.ylabz.basepro.data.repository.BaseProRepoImpl
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
    fun provideAppDatabase(@ApplicationContext appContext: Context): BaseProDB {
        return Room.databaseBuilder(
            appContext,
            BaseProDB::class.java,
            BaseProDB.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideBaseProDao(BaseProDB: BaseProDB): BaseProDao {
        return BaseProDB.probaseDao
    }

    @Provides
    @Singleton
    fun provideBaseProRepository(BaseProDao: BaseProDao): BaseProRepo {
        return BaseProRepoImpl(BaseProDao)
    }




}