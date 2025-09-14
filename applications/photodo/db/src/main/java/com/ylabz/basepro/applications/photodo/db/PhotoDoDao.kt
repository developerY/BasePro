package com.ylabz.basepro.applications.photodo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    // Add this method to delete a single task
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // Add this method to delete all tasks
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM tasks ORDER BY startTime DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>
}