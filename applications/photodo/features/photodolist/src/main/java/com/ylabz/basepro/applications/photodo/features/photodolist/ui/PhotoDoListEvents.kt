package com.ylabz.basepro.applications.photodo.features.photodolist.ui

sealed class PhotoDoListEvent {
    data class OnItemClick(val itemId: String) : PhotoDoListEvent()
    // Add this new event for the FAB click
    object OnAddTaskClicked : PhotoDoListEvent()
}