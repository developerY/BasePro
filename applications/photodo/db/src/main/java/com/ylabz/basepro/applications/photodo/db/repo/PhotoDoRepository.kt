package com.ylabz.basepro.applications.photodo.db.repo

import com.ylabz.basepro.applications.photodo.db.TaskEntity
import kotlinx.coroutines.flow.Flow

interface PhotoDoRepository {
    fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun insertTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun deleteAllTasks()

    // Added method to get a single task by its ID
    fun getTaskById(taskId: Long): Flow<TaskEntity?>
}