package com.ylabz.basepro.applications.bike.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ylabz.basepro.applications.bike.database.converter.Converters
import kotlin.jvm.java

@Database(
    entities = [ RideEntity::class, RideLocationEntity::class ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)  // only if you have custom types
abstract class BikeRideDatabase : RoomDatabase() {
    abstract val rideDao: RideDao

    companion object {
        const val DATABASE_NAME = "bikeride_db"

        // volatile keeps INSTANCE visible across threads
        @Volatile private var INSTANCE: BikeRideDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): BikeRideDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                BikeRideDatabase::class.java,
                DATABASE_NAME
            )
                // DEV‑ONLY: drop & re-create when you change your schema
                .fallbackToDestructiveMigration()
                .build()
    }
}
