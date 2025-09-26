package com.ylabz.basepro.applications.photodo.db.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ylabz.basepro.applications.photodo.db.PhotoDoDB
import com.ylabz.basepro.applications.photodo.db.PhotoDoDao
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePhotoDoDatabase(
        @ApplicationContext context: Context,
        callback: Provider<PhotoDoDatabaseCallback>
    ): PhotoDoDB {
        return Room.databaseBuilder(
            context,
            PhotoDoDB::class.java,
            "photodo_database"
        )
        .fallbackToDestructiveMigration() // Added to handle schema changes gracefully during dev
        .addCallback(callback.get())
        .build()
    }

    @Provides
    @Singleton
    fun providePhotoDoDao(database: PhotoDoDB): PhotoDoDao {
        return database.photoDoDao()
    }

    @Provides
    @Singleton
    fun providePhotoDoRepository(photoDoDao: PhotoDoDao): PhotoDoRepo {
        return PhotoDoRepoImpl(photoDoDao)
    }

    @Provides
    @Singleton
    fun providePhotoDoDatabaseCallback(): PhotoDoDatabaseCallback {
        return PhotoDoDatabaseCallback()
    }
}

class PhotoDoDatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val currentTime = System.currentTimeMillis()

            // Insert initial Categories (Projects)
            db.execSQL("INSERT INTO projects (projectId, name, description) VALUES (1, 'Home', 'Tasks related to home.')")
            db.execSQL("INSERT INTO projects (projectId, name, description) VALUES (2, 'Family', 'Family related tasks.')")
            db.execSQL("INSERT INTO projects (projectId, name, description) VALUES (3, 'Work', 'Work related tasks.')")

            // Insert initial Lists (as Tasks) for the 'Home' category
            db.execSQL("INSERT INTO tasks (projectId, name, notes, status, priority, creationDate) VALUES (1, 'Shopping List', 'Groceries and other items.', 'To-Do', 1, $currentTime)")
            db.execSQL("INSERT INTO tasks (projectId, name, notes, status, priority, creationDate) VALUES (1, 'Cleaning Tasks', 'Weekly cleaning schedule.', 'To-Do', 0, $currentTime)")
        }
    }
}
