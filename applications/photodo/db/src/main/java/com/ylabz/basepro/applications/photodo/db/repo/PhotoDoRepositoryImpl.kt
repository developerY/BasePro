package com.ylabz.basepro.applications.photodo.db.repo

import com.ylabz.basepro.applications.photodo.db.PhotoDoDao
import com.ylabz.basepro.applications.photodo.db.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotoDoRepositoryImpl @Inject constructor(
    private val photoDoDao: PhotoDoDao
) : PhotoDoRepository {

    override fun getAllTasks(): Flow<List<TaskEntity>> = photoDoDao.getAllTasks()

    override suspend fun insertTask(task: TaskEntity) {
        photoDoDao.insertTask(task)
    }
}