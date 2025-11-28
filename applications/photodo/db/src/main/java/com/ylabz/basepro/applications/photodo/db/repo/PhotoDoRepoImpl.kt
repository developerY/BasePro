package com.ylabz.basepro.applications.photodo.db.repo

import com.ylabz.basepro.applications.photodo.db.PhotoDoDao
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.PhotoEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotoDoRepoImpl @Inject constructor(
    private val photoDoDao: PhotoDoDao
) : PhotoDoRepo {

    // --- Category Operations ---

    override suspend fun insertCategory(category: CategoryEntity) {
        photoDoDao.insertCategory(category)
    }

    override suspend fun deleteCategory(category: CategoryEntity) {
        photoDoDao.deleteCategory(category)
    }

    override fun getAllCategories(): Flow<List<CategoryEntity>> {
        return photoDoDao.getAllCategories()
    }

    override fun getCategoryById(categoryId: Long): Flow<CategoryEntity?> {
        return photoDoDao.getCategoryById(categoryId)
    }

    override suspend fun updateCategory(category: CategoryEntity) {
        TODO("Not yet implemented")
    }

    // --- TaskList Operations ---

    override suspend fun insertTaskList(taskList: TaskListEntity) {
        photoDoDao.insertTaskList(taskList)
    }

    override suspend fun deleteTaskList(taskList: TaskListEntity) {
        photoDoDao.deleteTaskList(taskList)
    }

    override suspend fun deleteTaskListById(listId: Long) {
        photoDoDao.deleteTaskListById(listId)
    }


    override fun getTaskListById(listId: Long): Flow<TaskListEntity?> {
        return photoDoDao.getTaskListById(listId)
    }

    override fun getTaskListsForCategory(categoryId: Long): Flow<List<TaskListEntity>> {
        return photoDoDao.getTaskListsForCategory(categoryId)
    }

    // --- Photo Operations ---

    override suspend fun insertPhoto(photo: PhotoEntity) {
        photoDoDao.insertPhoto(photo)
    }

    override suspend fun deletePhoto(photo: PhotoEntity) {
        photoDoDao.deletePhoto(photo)
    }

    override fun getPhotosForTaskList(listId: Long): Flow<List<PhotoEntity>> {
        return photoDoDao.getPhotosForTaskList(listId)
    }

    // --- Relational Operations ---

    override fun getTaskListWithPhotos(listId: Long): Flow<TaskListWithPhotos?> {
        return photoDoDao.getTaskListWithPhotos(listId)
    }
}
