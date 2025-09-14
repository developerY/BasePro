package com.ylabz.basepro.applications.photodo.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class PhotoDoDatabase : RoomDatabase() {
    abstract fun photoDoDao(): PhotoDoDao
}