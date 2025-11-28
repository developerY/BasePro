package com.ylabz.basepro.applications.photodo.ui.navigation.main

import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity

/**
 * Defines the shared events that can be sent to the MainScreenViewModel.
 *
 * This file is part of the "Stateful ViewModel" pattern and is used by
 * navigation entries (like HomeEntry) to request global UI changes,
 * such as showing a bottom sheet.
 */
sealed interface MainScreenEvent {

    // --- ADD THIS ---
    data class OnCategorySelected(val categoryId: Long) : MainScreenEvent
    /**
     * Signals that the "Add Category" action was triggered.
     */
    data object OnAddCategoryClicked : MainScreenEvent

    // Edit the Category
    data class OnEditCategoryClicked(val category: CategoryEntity) : MainScreenEvent // Open sheet
    data class OnUpdateCategory(val category: CategoryEntity) : MainScreenEvent // Save

    /**
     * Signals that the "Add List" action was triggered.
     */
    data object OnAddListClicked : MainScreenEvent

    /**
     * Signals that the "Add Item" action was triggered.
     */
    data object OnAddItemClicked : MainScreenEvent

    /**
     * Signals that the user has dismissed the modal bottom sheet.
     */
    data object OnBottomSheetDismissed : MainScreenEvent

    /**
     * Event triggered when the user saves a new Task List.
     * @param categoryId The ID of the currently selected category (required foreign key).
     */
    data class OnSaveList(
        val title: String,
        val description: String,
        // CORRECT: Non-nullable. We can't save to the DB without this.
        val categoryId: Long
    ) : MainScreenEvent



    /**
     * Event triggered when the user saves a new Item (Photo) to a list.

    data class OnSaveItem(
        val caption: String,
        val uri: String, // URI of the captured image
        val listId: Long // Target list ID (foreign key)
    ) : MainScreenEvent*/

    /**
     * Signals that a category should be saved.
     */
    data class OnSaveCategory(val name: String, val description: String) : MainScreenEvent
}