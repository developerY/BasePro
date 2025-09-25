package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

// import com.ylabz.basepro.applications.photodo.db.entity.TaskEntity // Not needed if only passing taskId

// This is the version consistent with PhotoDoListViewModel's onEvent handler
sealed interface PhotoDoListEvent {
    data object OnDeleteAllTasksClicked : PhotoDoListEvent
    data object OnAddTaskClicked : PhotoDoListEvent
    data class OnDeleteTaskClicked(val taskId: Long) : PhotoDoListEvent // Assuming taskId is Long
    data class OnItemClick(val taskId: Long) : PhotoDoListEvent      // Assuming taskId is Long
}
