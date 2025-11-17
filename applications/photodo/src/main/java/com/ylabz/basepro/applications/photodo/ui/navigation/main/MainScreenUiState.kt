package com.ylabz.basepro.applications.photodo.ui.navigation.main

import com.ylabz.basepro.applications.photodo.ui.navigation.fab.FabState

/**
 * UI state for the MainScreen.
 *
 * @param fabState The current state of the Floating Action Button.
 * @param currentSheet The bottom sheet (if any) that should be displayed.
 */
data class MainScreenUiState(
    val fabState: FabState? = null,
    val currentSheet: BottomSheetType = BottomSheetType.NONE,
    // --- ADD THIS ---
    val lastSelectedCategoryId: Long = 1L // Default to 1L as you did
)