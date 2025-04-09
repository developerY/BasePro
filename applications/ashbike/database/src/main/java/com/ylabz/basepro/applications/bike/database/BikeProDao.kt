package com.ylabz.basepro.applications.bike.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BikeProDao {
    // The flow always holds/caches latest version of data. Notifies its observers when the

    /* data has changed.
    @Query("SELECT * FROM bikepro_table ORDER BY date ASC")
    fun getAllBasePros(): Flow<List<BikeProEntity>> // convert to Flow in the implementation.
    */

    @Query("SELECT * FROM bikepro_table")//" ORDER BY date ASC")
    fun getAllBikePros(): Flow<List<BikeProEntity>> // convert to Flow in the implementation.

    // Inserting a new BasePro
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: BikeProEntity)



    // Deleting a BasePro
    @Delete
    suspend fun delete(todo: BikeProEntity)

    // Deleting all BasePros
    @Query("DELETE FROM bikepro_table")
    suspend fun deleteAll()

    // Deleting a BasePro by its id
    @Query("DELETE FROM bikepro_table WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Finding a BasePro by its id
    @Query("SELECT * FROM bikepro_table WHERE id = :id")
    suspend fun findById(id: Int): BikeProEntity?

    // Finding a BasePro by its title
    @Query("SELECT * FROM bikepro_table WHERE title LIKE :title")
    suspend fun findByTitle(title: String): BikeProEntity?

    // Finding a BasePro by its description
    @Query("SELECT * FROM bikepro_table WHERE description LIKE :description")
    suspend fun findByDescription(description: String): BikeProEntity?

    /* Finding a BasePro by its date
    @Query("SELECT * FROM bikepro_table WHERE date LIKE :date")
    suspend fun findByDate(date: String): BikeProEntity?
    */


    /**
     * Updating only alarm switch
     * By order id
     */
    @Update(entity = BikeProEntity::class)
    suspend fun update(obj: BikeProUpdate)

    // Finding a BasePro by its photoTodoId
    @Query("SELECT * FROM bikepro_table WHERE id = :id")
    suspend fun findByPhotoTodoId(id: Int): BikeProEntity?


}
