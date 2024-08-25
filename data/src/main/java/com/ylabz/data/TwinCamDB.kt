package com.ylabz.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TwinCamEntity::class], version = 1, exportSchema = false)
abstract class TwinCamDB : RoomDatabase() {

    abstract val twinCamDao: TwinCamDao

    companion object {
        const val DATABASE_NAME = "twincam_db"

        @JvmStatic
        fun getDatabase(context: Context): TwinCamDB {
            return Room.databaseBuilder(
                context,
                TwinCamDB::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
