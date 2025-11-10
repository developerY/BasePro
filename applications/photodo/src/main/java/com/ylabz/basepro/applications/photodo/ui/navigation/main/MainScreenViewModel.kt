package com.ylabz.basepro.applications.photodo.ui.navigation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * UI state for the MainScreen.
 *
 * @param fabState The current state of the Floating Action Button.
 * @param currentSheet The bottom sheet (if any) that should be displayed.
 */
data class MainScreenUiState(
    val fabState: FabState? = null,
    val currentSheet: BottomSheetType = BottomSheetType.NONE
)

/**
 * A stateful ViewModel that holds the UI state for the MainScreen.
 * It acts as the single source of truth for the FAB and bottom sheets,
 * as described in FAB.md.
 */
@HiltViewModel
class MainScreenViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    // Keep track of the last known FAB state per screen, so we can restore it
    // when a bottom sheet is dismissed.
    private var lastKnownFabState: FabState? = null

    /**
     * Handles events from the UI (e.g., from HomeEntry or from the BottomSheet).
     */
    fun onEvent(event: MainScreenEvent) {
        viewModelScope.launch {
            when (event) {
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
            }
        }
    }

    /**
     * Sets the state of the Floating Action Button.
     * This is called by the navigation entries (e.g., HomeEntry, DetailEntry)
     * to define the FAB's behavior for the current screen.
     */
    fun setFabState(fabState: FabState?) {
        // Store this as the "default" FAB for the current screen
        lastKnownFabState = fabState

        // Only update the UI if a sheet is not currently open
        if (_uiState.value.currentSheet == BottomSheetType.NONE) {
            _uiState.update { it.copy(fabState = fabState) }
        }
    }
}