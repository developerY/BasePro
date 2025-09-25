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
        // Use Provider for lazy injection to break circular dependency
        callback: Provider<PhotoDoDatabaseCallback>
    ): PhotoDoDB {
        return Room.databaseBuilder(
            context,
            PhotoDoDB::class.java,
            "photodo_database"
        )
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

    // Callback to pre-populate the database
    @Provides
    @Singleton
    fun providePhotoDoDatabaseCallback(): PhotoDoDatabaseCallback {
        return PhotoDoDatabaseCallback()
    }
}

// The callback class itself
class PhotoDoDatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Use CoroutineScope to run this in a background thread
        CoroutineScope(Dispatchers.IO).launch {
            // Insert initial project
            db.execSQL("INSERT INTO projects (projectId, name, description) VALUES (1, 'Default Project', 'A default project to get you started.')")

            // Insert initial tasks for the default project (projectId = 1)
            val currentTime = System.currentTimeMillis()
            db.execSQL("INSERT INTO tasks (projectId, name, notes, status, priority, creationDate) VALUES (1, 'Welcome to PhotoDo!', 'You can add notes and photos to your tasks.', 'To-Do', 1, $currentTime)")
            db.execSQL("INSERT INTO tasks (projectId, name, notes, status, priority, creationDate) VALUES (1, 'Tap on a task to see details', null, 'To-Do', 0, $currentTime)")
            db.execSQL("INSERT INTO tasks (projectId, name, notes, status, priority, creationDate) VALUES (1, 'Use the + button to add a new task', 'You\'ll need to implement this feature.', 'To-Do', 0, $currentTime)")
        }
    }
}
