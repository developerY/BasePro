package com.ylabz.basepro.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BaseProDao {
    // The flow always holds/caches latest version of data. Notifies its observers when the

    /* data has changed.
    @Query("SELECT * FROM basepro_table ORDER BY date ASC")
    fun getAllBasePros(): Flow<List<BaseProEntity>> // convert to Flow in the implementation.
    */

    @Query("SELECT * FROM basepro_table")//" ORDER BY date ASC")
    fun getAllBasePros(): Flow<List<BaseProEntity>> // convert to Flow in the implementation.

    // Inserting a new BasePro
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: BaseProEntity)



    // Deleting a BasePro
    @Delete
    suspend fun delete(todo: BaseProEntity)

    // Deleting all BasePros
    @Query("DELETE FROM basepro_table")
    suspend fun deleteAll()

    // Deleting a BasePro by its id
    @Query("DELETE FROM basepro_table WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Finding a BasePro by its id
    @Query("SELECT * FROM basepro_table WHERE id = :id")
    suspend fun findById(id: Int): BaseProEntity?

    // Finding a BasePro by its title
    @Query("SELECT * FROM basepro_table WHERE title LIKE :title")
    suspend fun findByTitle(title: String): BaseProEntity?

    // Finding a BasePro by its description
    @Query("SELECT * FROM basepro_table WHERE description LIKE :description")
    suspend fun findByDescription(description: String): BaseProEntity?

    /* Finding a BasePro by its date
    @Query("SELECT * FROM basepro_table WHERE date LIKE :date")
    suspend fun findByDate(date: String): BaseProEntity?
    */


    /**
     * Updating only alarm switch
     * By order id
     */
    @Update(entity = BaseProEntity::class)
    suspend fun update(obj: BaseProUpdate)

    // Finding a BasePro by its photoTodoId
    @Query("SELECT * FROM basepro_table WHERE id = :id")
    suspend fun findByPhotoTodoId(id: Int): BaseProEntity?


}
