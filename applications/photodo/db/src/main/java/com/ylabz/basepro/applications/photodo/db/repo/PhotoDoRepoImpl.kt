package com.ylabz.basepro.applications.photodo.db.repo

import com.ylabz.basepro.applications.photodo.db.PhotoDoDao
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.ProjectEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskWithPhotos
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Concrete implementation of the PhotoDo repository.
 *
 * @param photoDoDao The Data Access Object for the PhotoDo database.
 */
class PhotoDoRepoImpl @Inject constructor(
    private val photoDoDao: PhotoDoDao
) : PhotoDoRepo {

    // --- Project Operations ---

    override suspend fun insertProject(project: ProjectEntity) {
        photoDoDao.insertProject(project)
    }

    override suspend fun deleteProject(project: ProjectEntity) {
        photoDoDao.deleteProject(project)
    }

    override fun getAllProjects(): Flow<List<ProjectEntity>> {
        return photoDoDao.getAllProjects()
    }

    override fun getProjectById(projectId: Long): Flow<ProjectEntity?> {
        return photoDoDao.getProjectById(projectId)
    }

    // --- Task Operations ---

    override suspend fun insertTask(task: TaskEntity) {
        photoDoDao.insertTask(task)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        photoDoDao.deleteTask(task)
    }

    override fun getTaskById(taskId: Long): Flow<TaskEntity?> {
        return photoDoDao.getTaskById(taskId)
    }

    override fun getTasksForProject(projectId: Long): Flow<List<TaskEntity>> {
        return photoDoDao.getTasksForProject(projectId)
    }

    // --- Photo Operations ---

    override suspend fun insertPhoto(photo: PhotoEntity) {
        photoDoDao.insertPhoto(photo)
    }

    override suspend fun deletePhoto(photo: PhotoEntity) {
        photoDoDao.deletePhoto(photo)
    }

    override fun getPhotosForTask(taskId: Long): Flow<List<PhotoEntity>> {
        return photoDoDao.getPhotosForTask(taskId)
    }

    // --- Relational Operations ---

    override fun getTaskWithPhotos(taskId: Long): Flow<TaskWithPhotos?> {
        return photoDoDao.getTaskWithPhotos(taskId)
    }
}