# Saving Picture Flow

Here is the exact flow of how it works:

1.  **Data Loading (`loadTaskDetails`)**: When the detail screen is shown, `loadTaskDetails(listId)` is called. This starts a coroutine that `.collect`s the flow from `photoDoRepo.getTaskListWithPhotos(listId)`.
    * This flow is "cold" and active as long as the ViewModel is alive. Room will automatically emit a new `TaskListWithPhotos` object *any time* the `task_lists` or `photos` table changes for that specific `listId`.

2.  **Saving a Photo (`onEvent(OnPhotoSaved)`)**:
    * The user takes a photo, and the `OnPhotoSaved` event is sent.
    * We call `photoDoRepo.insertPhoto(newPhoto)`.
    * This inserts a new row into the `photos` table.

3.  **Automatic UI Update**:
    * As soon as that `insertPhoto` call finishes, Room detects that the `photos` table has changed.
    * This change triggers the flow from step 1 to automatically emit a *new* `TaskListWithPhotos` object, which now contains the new photo.
    * The `.collect` block inside `loadTaskDetails` runs again, receives this new list, and updates the `_uiState` with the new `DetailLoadState.Success`.
    * The UI (`DetailCard.kt`) sees the new state and the `LazyRow` recomposes to show the new photo.

The only *manual* state update we do in `onEvent(OnPhotoSaved)` is this line:
`_uiState.update { it.copy(showCamera = false) }`

This is *only* to update the UI-specific state (to hide the camera). We are **not** manually adding the new photo to the state's photo list. We are letting the database (our single source of truth) do it for us.