package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

// This import is now needed, just as you said.
import com.ylabz.basepro.applications.photodo.db.entity.TaskListWithPhotos

/**
 * Represents the different states for the data-loading portion of the detail screen.
 * This is the pattern you suggested.
 */
sealed interface DetailLoadState {
    /**
     * The screen is currently loading the task details.
     */
    object Loading : DetailLoadState

    /**
     * The task details have been successfully loaded.
     * @param taskListWithPhotos The database object containing the task and its photos.
     */
    data class Success(
        val taskListWithPhotos: TaskListWithPhotos
    ) : DetailLoadState

    /**
     * An error occurred while loading the task details.
     * @param message A user-facing error message.
     */
    data class Error(val message: String) : DetailLoadState
}

/**
 * The complete UI state for the PhotoDoDetail screen.
 *
 * @param loadState The current state of data loading (Loading, Success, or Error).
 * @param showCamera True if the camera UI should be shown instead of the detail list.
 */
data class PhotoDoDetailUiState(
    val loadState: DetailLoadState = DetailLoadState.Loading,
    val showCamera: Boolean = false
)