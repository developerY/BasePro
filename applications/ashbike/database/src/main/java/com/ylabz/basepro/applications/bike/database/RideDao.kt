package com.ylabz.basepro.applications.bike.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RideDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: RideEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocations(locations: List<RideLocationEntity>)

    @Transaction
    @Query("SELECT * FROM rides WHERE rideId = :id")
    fun getRideWithLocations(id: String): Flow<RideWithLocations?>

    @Transaction
    @Query("SELECT * FROM rides ORDER BY startTime DESC")
    fun getAllRidesWithLocations(): Flow<List<RideWithLocations>>

    @Query("DELETE FROM rides WHERE rideId = :id")
    suspend fun deleteRide(id: String)
}
