package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

import com.ylabz.basepro.applications.photodo.db.entity.TaskEntity

sealed class PhotoDoListEvent {
    data class OnItemClick(val itemId: String) : PhotoDoListEvent()
    object OnAddTaskClicked : PhotoDoListEvent()
    // Add event for deleting a single task
    data class OnDeleteTaskClicked(val task: TaskEntity) : PhotoDoListEvent()
    // Add event for deleting all tasks
    object OnDeleteAllTasksClicked : PhotoDoListEvent()
}