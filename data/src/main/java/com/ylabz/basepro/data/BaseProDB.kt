package com.ylabz.basepro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BaseProEntity::class], version = 1, exportSchema = false)
abstract class BaseProDB : RoomDatabase() {

    abstract val probaseDao: BaseProDao

    companion object {
        const val DATABASE_NAME = "probase_db"

        @JvmStatic
        fun getDatabase(context: Context): BaseProDB {
            return Room.databaseBuilder(
                context,
                BaseProDB::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
