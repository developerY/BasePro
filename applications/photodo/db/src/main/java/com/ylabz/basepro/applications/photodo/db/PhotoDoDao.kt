package com.ylabz.basepro.applications.photodo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.ProjectEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskWithPhotos
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDoDao {

    // --- Project Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("SELECT * FROM projects ORDER BY name ASC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE projectId = :projectId")
    fun getProjectById(projectId: Long): Flow<ProjectEntity?>

    // --- Task Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    fun getTaskById(taskId: Long): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY creationDate DESC")
    fun getTasksForProject(projectId: Long): Flow<List<TaskEntity>>

    // --- Photo Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("SELECT * FROM photos WHERE taskId = :taskId ORDER BY timestamp DESC")
    fun getPhotosForTask(taskId: Long): Flow<List<PhotoEntity>>

    // --- Relational Query ---

    @Transaction
    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    fun getTaskWithPhotos(taskId: Long): Flow<TaskWithPhotos?>
}