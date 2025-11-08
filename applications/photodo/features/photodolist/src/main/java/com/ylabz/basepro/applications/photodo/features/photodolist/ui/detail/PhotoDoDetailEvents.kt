package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

/**
 * Sealed interface for events that can be triggered from the PhotoDo Detail UI
 */
sealed interface PhotoDoDetailEvent {
    /**
     * Event to add a new photo to the current task list.
     * @param photoUri The string URI of the saved photo.
     */
    data class AddPhoto(val photoUri: String) : PhotoDoDetailEvent

    // Add other events here, e.g.:
    // data class UpdateTaskStatus(val newStatus: String) : PhotoDoDetailEvent
    // object DeleteTask : PhotoDoDetailEvent
}