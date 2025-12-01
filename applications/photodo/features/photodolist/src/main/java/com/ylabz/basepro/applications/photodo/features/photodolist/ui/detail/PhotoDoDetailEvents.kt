package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import android.net.Uri
import com.ylabz.basepro.applications.photodo.db.entity.TaskItemEntity

/**
 * Defines the events that can be triggered from the PhotoDoDetail screen.
 */
sealed interface PhotoDoDetailEvent {

    /**
     * Event triggered when the user saves a new photo from the camera.
     * @param uri The URI of the saved photo.
     */
    data class OnPhotoSaved(val uri: Uri) : PhotoDoDetailEvent

    /**
     * Event triggered when a photo is deleted.
     * @param photoId The ID of the photo to delete.
     */
    data class OnDeletePhoto(val photoId: Long) : PhotoDoDetailEvent

    /**
     * Event triggered when the "Add Photo" button is clicked.
     */
    data object OnCameraClick : PhotoDoDetailEvent

    /**
     * Event triggered when the user presses "Back" from the camera screen.
     */
    data object OnBackFromCamera : PhotoDoDetailEvent

    /**
     * Event triggered when the user clicks the delete button in the top bar.
     * This deletes the entire task list currently being viewed.
     */
    data object OnDeleteTaskListClicked : PhotoDoDetailEvent // <-- THE NEW EVENT

    // --- NEW EVENTS FOR CHECKLIST ---
    data object OnAddItemClicked : PhotoDoDetailEvent
    data class OnItemCheckedChange(val item: TaskItemEntity, val isChecked: Boolean) : PhotoDoDetailEvent
    data class OnDeleteItem(val item: TaskItemEntity) : PhotoDoDetailEvent

    /**
     * Event to handle changes to the task list title.
     * @param title The new title.
     */
    // data class OnTitleChange(val title: String) : PhotoDoDetailEvent

    /**
     * Event to handle changes to the task list description.
     * @param description The new description.
     */
    // data class OnDescriptionChange(val description: String) : PhotoDoDetailEvent
}