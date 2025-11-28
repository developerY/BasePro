package com.ylabz.basepro.applications.photodo.db.repo

import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the PhotoDo repository. This is the single source of truth for all app data.
 */
interface PhotoDoRepo {

    // --- Category Operations ---

    suspend fun insertCategory(category: CategoryEntity)

    suspend fun deleteCategory(category: CategoryEntity)

    fun getAllCategories(): Flow<List<CategoryEntity>>

    fun getCategoryById(categoryId: Long): Flow<CategoryEntity?>

    suspend fun updateCategory(category: CategoryEntity) // <--- Add this


    // --- TaskList Operations ---

    suspend fun insertTaskList(taskList: TaskListEntity)

    suspend fun deleteTaskList(taskList: TaskListEntity)

    /**
     * Deletes a TaskListEntity and associated PhotoEntities by its ID.
     */
    suspend fun deleteTaskListById(listId: Long)

    fun getTaskListById(listId: Long): Flow<TaskListEntity?>

    fun getTaskListsForCategory(categoryId: Long): Flow<List<TaskListEntity>>

    // --- Photo Operations ---

    suspend fun insertPhoto(photo: PhotoEntity)

    suspend fun deletePhoto(photo: PhotoEntity)

    fun getPhotosForTaskList(listId: Long): Flow<List<PhotoEntity>>

    // --- Relational Operations ---

    fun getTaskListWithPhotos(listId: Long): Flow<TaskListWithPhotos?>

}
