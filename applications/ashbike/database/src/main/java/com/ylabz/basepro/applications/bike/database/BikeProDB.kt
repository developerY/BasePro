package com.ylabz.basepro.applications.bike.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BikeProEntity::class], version = 1, exportSchema = false)
abstract class BikeProDB : RoomDatabase() {

    abstract val baseproDao: BikeProDao

    companion object {
        const val DATABASE_NAME = "bikepro_db"

        @JvmStatic
        fun getDatabase(context: Context): BikeProDB {
            return Room.databaseBuilder(
                context,
                BikeProDB::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
