package com.ylabz.basepro.applications.photodo.core.ui

/**
 * Defines the shared events that can be communicated across different screens
 * via the MainScreenViewModel.
 */
sealed interface MainScreenEvent {
    /** Signals that the user wants to add a new Category. */
    data class AddCategory(val categoryName: String) : MainScreenEvent

    // Event to request showing the "Add Category" UI
    data object RequestAddCategory : MainScreenEvent

    /** Signals that the user wants to add a new List. */
    data object AddList : MainScreenEvent

    /** Signals that the user wants to add a new Item. */
    data object AddItem : MainScreenEvent

    data object ShowAddCategorySheet : MainScreenEvent

    data class DeleteCategory(val categoryId: Long) : MainScreenEvent



}