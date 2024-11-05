package com.ylabz.basepro.core.database.di

import android.content.Context
import androidx.room.Room
import com.ylabz.basepro.core.database.BaseProDB
import com.ylabz.basepro.core.database.BaseProDao
import com.ylabz.basepro.core.database.BaseProRepo
import com.ylabz.basepro.core.database.repository.BaseProRepoImpl
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
        return BaseProDB.baseproDao
    }

    @Provides
    @Singleton
    fun provideBaseProRepository(BaseProDao: BaseProDao): BaseProRepo {
        return BaseProRepoImpl(BaseProDao)
    }




}