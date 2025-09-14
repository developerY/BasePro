package com.ylabz.basepro.applications.photodo.db.di

import android.content.Context
import androidx.room.Room
import com.ylabz.basepro.applications.photodo.db.PhotoDoDao
import com.ylabz.basepro.applications.photodo.db.PhotoDoDatabase
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepository
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepositoryImpl
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
    fun providePhotoDoDatabase(@ApplicationContext context: Context): PhotoDoDatabase {
        return Room.databaseBuilder(
            context,
            PhotoDoDatabase::class.java,
            "photodo_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePhotoDoDao(database: PhotoDoDatabase): PhotoDoDao {
        return database.photoDoDao()
    }

    @Provides
    @Singleton
    fun providePhotoDoRepository(photoDoDao: PhotoDoDao): PhotoDoRepository {
        return PhotoDoRepositoryImpl(photoDoDao)
    }
}