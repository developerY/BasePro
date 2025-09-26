package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

/**
 * Defines the events that can be triggered from the Task List UI.
 */
sealed interface PhotoDoListEvent {
    data object OnDeleteAllTaskListsClicked : PhotoDoListEvent
    data object OnAddTaskListClicked : PhotoDoListEvent
    data class OnDeleteTaskListClicked(val listId: Long) : PhotoDoListEvent
    data class OnTaskListClick(val listId: Long) : PhotoDoListEvent
}
