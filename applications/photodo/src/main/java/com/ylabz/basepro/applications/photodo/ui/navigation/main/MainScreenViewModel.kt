package com.ylabz.basepro.applications.photodo.ui.navigation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainScreenViewModel"

/**
 * Enum for the bottom sheets that can be shown.
 */
enum class BottomSheetType {
    NONE, ADD_CATEGORY, ADD_LIST, ADD_ITEM
}

/**
 * A stateful ViewModel that holds the UI state for the MainScreen.
 * It acts as the single source of truth for the FAB and bottom sheets,
 * as described in FAB.md.
 */
@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: PhotoDoRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    // Keep track of the last known FAB state per screen, so we can restore it
    // when a bottom sheet is dismissed.
    // We store the last-known FAB state so we can restore it when a sheet is dismissed.
    private var lastKnownFabState: FabState? = null

    /**
     * Allows a navigation entry (like HomeEntry) to set the FAB.
     * This is called from a LaunchedEffect in the entry.
     */
    fun setFabState(fabState: FabState?) {
        Log.d(TAG, "setFabState called with: $fabState")
        _uiState.update {
            // Only update if the sheet is not visible
            if (it.currentSheet == BottomSheetType.NONE) {
                lastKnownFabState = fabState
                it.copy(fabState = fabState)
            } else {
                // A sheet is open, just remember this for later
                lastKnownFabState = fabState
                it
            }
        }
    }

    /**
     * Sets the state of the Floating Action Button.
     * This is called by the navigation entries (e.g., HomeEntry, DetailEntry)
     * to define the FAB's behavior for the current screen.
     */
    fun setFabStateOrig(fabState: FabState?) {
        // Store this as the "default" FAB for the current screen
        lastKnownFabState = fabState

        // Only update the UI if a sheet is not currently open
        if (_uiState.value.currentSheet == BottomSheetType.NONE) {
            _uiState.update { it.copy(fabState = fabState) }
        }
    }

    /**
     * Handles events from the UI (e.g., from HomeEntry or from the BottomSheet).
     */
    fun onEvent(event: MainScreenEvent) {
        viewModelScope.launch {
            when (event) {
                // --- ADD THIS HANDLER ---
                is MainScreenEvent.OnCategorySelected -> {
                    _uiState.update { it.copy(lastSelectedCategoryId = event.categoryId) }
                }
                MainScreenEvent.OnAddCategoryClicked -> {
                    Log.d(TAG, "OnAddCategoryClicked event received")
                    _uiState.update { it.copy(
                        currentSheet = BottomSheetType.ADD_CATEGORY,
                        fabState = FabState.Hidden // Hide FAB when sheet is open
                    ) }
                }
                MainScreenEvent.OnAddListClicked -> {
                    Log.d(TAG, "OnAddListClicked event received")
                    _uiState.update { it.copy(
                        currentSheet = BottomSheetType.ADD_LIST,
                        fabState = FabState.Hidden
                    ) }
                }
                MainScreenEvent.OnAddItemClicked -> {
                    Log.d(TAG, "OnAddItemClicked event received")
                    _uiState.update { it.copy(
                        currentSheet = BottomSheetType.ADD_ITEM,
                        fabState = FabState.Hidden
                    ) }
                }
                MainScreenEvent.OnBottomSheetDismissed -> {
                    Log.d(TAG, "OnBottomSheetDismissed event received")
                    _uiState.update {
                        it.copy(
                            currentSheet = BottomSheetType.NONE,
                            // Restore the last known FAB state
                            fabState = lastKnownFabState
                        )
                    }
                }

                // --- NEW HANDLER FOR SAVING THE LIST ---
                is MainScreenEvent.OnSaveList -> {
                    Log.d(TAG, "OnSaveList event received. Title: ${event.title}, CategoryId: ${event.categoryId}")

                    // 1. Create the entity (Assuming TaskListEntity requires these fields)
                    // 1. Create the TaskListEntity using the user's structure
                    val newTaskList = TaskListEntity(
                        // listId is auto-generated
                        categoryId = event.categoryId,
                        name = event.title,
                        notes = event.description,
                        status = "To-Do", // Default status
                        priority = 0, // Default priority
                        creationDate = System.currentTimeMillis(),
                        dueDate = null
                    )

                    // 2. Call the repository
                    repository.insertTaskList(newTaskList)

                    // The sheet dismissal is handled by MainScreen.kt in the onSaveList lambda
                }

                /* --- NEW HANDLER FOR SAVING THE ITEM (PHOTO) ---
                is MainScreenEvent.OnSaveItem -> {
                    Log.d(TAG, "OnSaveItem event received. Caption: ${event.caption}, ListId: ${event.listId}")

                    // 1. Create the PhotoEntity
                    val newPhoto = PhotoEntity(
                        listId = event.listId,
                        uri = event.uri,
                        caption = event.caption,
                        timestamp = System.currentTimeMillis()
                    )

                    // 2. Call the repository (assuming insertPhoto exists in PhotoDoRepo)
                    repository.insertPhoto(newPhoto)
                }*/


                is MainScreenEvent.OnSaveCategory -> {
                    Log.d(TAG, "OnSaveCategory event received")
                    repository.insertCategory(
                        CategoryEntity(
                            name = event.name,
                            description = event.description
                        )
                    )
                }
            }
        }
    }
}