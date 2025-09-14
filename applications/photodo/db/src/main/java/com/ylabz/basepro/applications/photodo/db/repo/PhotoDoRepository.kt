package com.ylabz.basepro.applications.photodo.db.repo

import com.ylabz.basepro.applications.photodo.db.TaskEntity
import kotlinx.coroutines.flow.Flow

interface PhotoDoRepository {
    fun getAllTasks(): Flow<List<TaskEntity>>
    suspend fun insertTask(task: TaskEntity)
    // Add these two new functions
    suspend fun deleteTask(task: TaskEntity)
    suspend fun deleteAllTasks()
}