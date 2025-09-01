package com.ylabz.basepro.applications.bike.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BikeRideDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: BikeRideEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocations(locations: List<RideLocationEntity>)

    @Transaction
    @Query("SELECT * FROM bike_rides_table WHERE rideId = :id")
    fun getRideWithLocations(id: String): Flow<RideWithLocations?>

    @Transaction
    @Query("SELECT * FROM bike_rides_table ORDER BY startTime DESC")
    fun getAllRidesWithLocations(): Flow<List<RideWithLocations>>

    /** just change the notes column */
    @Query("UPDATE bike_rides_table SET notes = :notes WHERE rideId = :rideId")
    suspend fun updateNotes(rideId: String, notes: String)

    @Query("DELETE FROM bike_rides_table WHERE rideId = :id")
    suspend fun deleteRide(id: String)

    /** Wipe entire rides table. */
    @Query("DELETE FROM bike_rides_table")
    suspend fun deleteAllBikeRides()

    /** Marks a ride as synced to Health Connect and stores the Health Connect ID. */
    @Query("UPDATE bike_rides_table SET isHealthDataSynced = 1, healthConnectRecordId = :healthConnectId WHERE rideId = :rideId")
    suspend fun markRideAsSyncedToHealthConnect(rideId: String, healthConnectId: String?)

    /** Gets the count of rides that are not yet synced to Health Connect. */
    @Query("SELECT COUNT(*) FROM bike_rides_table WHERE isHealthDataSynced = 0")
    fun getUnsyncedRidesCount(): Flow<Int>
}