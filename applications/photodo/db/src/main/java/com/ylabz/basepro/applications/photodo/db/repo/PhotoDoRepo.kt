package com.ylabz.basepro.applications.photodo.db.repo

import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.ProjectEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskWithPhotos
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the PhotoDo repository. This is the single source of truth for all app data.
 */
interface PhotoDoRepo {

    // --- Project Operations ---

    /**
     * Inserts a new project into the database.
     */
    suspend fun insertProject(project: ProjectEntity)

    /**
     * Deletes a project from the database.
     */
    suspend fun deleteProject(project: ProjectEntity)

    /**
     * Retrieves all projects from the database, ordered by name.
     */
    fun getAllProjects(): Flow<List<ProjectEntity>>

    /**
     * Retrieves a single project by its ID.
     */
    fun getProjectById(projectId: Long): Flow<ProjectEntity?>

    // --- Task Operations ---

    /**
     * Inserts a new task into the database.
     */
    suspend fun insertTask(task: TaskEntity)

    /**
     * Deletes a task from the database.
     */
    suspend fun deleteTask(task: TaskEntity)

    /**
     * Retrieves a single task by its ID.
     */
    fun getTaskById(taskId: Long): Flow<TaskEntity?>

    /**
     * Retrieves all tasks for a given project ID.
     */
    fun getTasksForProject(projectId: Long): Flow<List<TaskEntity>>

    // --- Photo Operations ---

    /**
     * Inserts a new photo into the database.
     */
    suspend fun insertPhoto(photo: PhotoEntity)

    /**
     * Deletes a photo from the database.
     */
    suspend fun deletePhoto(photo: PhotoEntity)

    /**
     * Retrieves all photos for a given task ID.
     */
    fun getPhotosForTask(taskId: Long): Flow<List<PhotoEntity>>

    // --- Relational Operations ---

    /**
     * Retrieves a task along with all of its associated photos.
     */
    fun getTaskWithPhotos(taskId: Long): Flow<TaskWithPhotos?>
}