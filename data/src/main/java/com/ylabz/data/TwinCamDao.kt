package com.ylabz.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TwinCamDao {
    // The flow always holds/caches latest version of data. Notifies its observers when the

    /* data has changed.
    @Query("SELECT * FROM twincam_table ORDER BY date ASC")
    fun getAllTwinCams(): Flow<List<TwinCamEntity>> // convert to Flow in the implementation.
    */

    @Query("SELECT * FROM twincam_table")//" ORDER BY date ASC")
    fun getAllTwinCams(): Flow<List<TwinCamEntity>> // convert to Flow in the implementation.

    // Inserting a new TwinCam
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TwinCamEntity)



    // Deleting a TwinCam
    @Delete
    suspend fun delete(todo: TwinCamEntity)

    // Deleting all TwinCams
    @Query("DELETE FROM twincam_table")
    suspend fun deleteAll()

    // Deleting a TwinCam by its id
    @Query("DELETE FROM twincam_table WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Finding a TwinCam by its id
    @Query("SELECT * FROM twincam_table WHERE id = :id")
    suspend fun findById(id: Int): TwinCamEntity?

    // Finding a TwinCam by its title
    @Query("SELECT * FROM twincam_table WHERE title LIKE :title")
    suspend fun findByTitle(title: String): TwinCamEntity?

    // Finding a TwinCam by its description
    @Query("SELECT * FROM twincam_table WHERE description LIKE :description")
    suspend fun findByDescription(description: String): TwinCamEntity?

    /* Finding a TwinCam by its date
    @Query("SELECT * FROM twincam_table WHERE date LIKE :date")
    suspend fun findByDate(date: String): TwinCamEntity?
    */


    /**
     * Updating only alarm switch
     * By order id
     */
    @Update(entity = TwinCamEntity::class)
    suspend fun update(obj: TwinCamUpdate)

    // Finding a TwinCam by its photoTodoId
    @Query("SELECT * FROM twincam_table WHERE id = :id")
    suspend fun findByPhotoTodoId(id: Int): TwinCamEntity?


}
