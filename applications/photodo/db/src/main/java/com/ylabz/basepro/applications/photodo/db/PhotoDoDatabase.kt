package com.ylabz.basepro.applications.photodo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity

@Database(
    entities = [
        CategoryEntity::class,
        TaskListEntity::class,
        PhotoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PhotoDoDB : RoomDatabase() {

    abstract fun photoDoDao(): PhotoDoDao
}
