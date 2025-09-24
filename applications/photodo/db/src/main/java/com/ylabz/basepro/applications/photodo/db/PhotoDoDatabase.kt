package com.ylabz.basepro.applications.photodo.db


import androidx.room.Database
import androidx.room.RoomDatabase
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.ProjectEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskEntity

@Database(
    entities = [
        ProjectEntity::class,
        TaskEntity::class,
        PhotoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PhotoDoDB : RoomDatabase() {

    abstract fun photoDoDao(): PhotoDoDao
}