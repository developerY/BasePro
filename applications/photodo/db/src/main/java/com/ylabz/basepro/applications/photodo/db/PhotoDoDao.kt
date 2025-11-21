package com.ylabz.basepro.applications.photodo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDoDao {

    // --- Category Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId")
    fun getCategoryById(categoryId: Long): Flow<CategoryEntity?>

    // --- TaskList Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskList(taskList: TaskListEntity)

    @Delete
    suspend fun deleteTaskList(taskList: TaskListEntity)

    @Query("DELETE FROM task_lists WHERE listId = :listId")
    suspend fun deleteTaskListById(listId: Long)

    @Query("SELECT * FROM task_lists WHERE listId = :listId")
    fun getTaskListById(listId: Long): Flow<TaskListEntity?>

    @Query("SELECT * FROM task_lists WHERE categoryId = :categoryId ORDER BY creationDate DESC")
    fun getTaskListsForCategory(categoryId: Long): Flow<List<TaskListEntity>>

    // --- Photo Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("SELECT * FROM photos WHERE listId = :listId ORDER BY timestamp DESC")
    fun getPhotosForTaskList(listId: Long): Flow<List<PhotoEntity>>

    // --- Relational Query ---

    @Transaction
    @Query("SELECT * FROM task_lists WHERE listId = :listId")
    fun getTaskListWithPhotos(listId: Long): Flow<TaskListWithPhotos?>
}
