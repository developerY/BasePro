package com.ylabz.basepro.applications.photodo.core.ui

/**
 * Defines the shared events that can be communicated across different screens
 * via the MainScreenViewModel.
 */
sealed interface MainScreenEvent {

    /**
     * An event to request that the UI for adding a new category be shown.
     */
    // Event to request showing the "Add Category" UI
    data object RequestAddCategory : MainScreenEvent

    /**
     * An event that carries the new category's name to be saved.
     */
    /** Signals that the user wants to add a new Category. */

    data class AddCategory(val categoryName: String) : MainScreenEvent

    /**
     * An event to signal that the "Add List" action was triggered from a global context.
     */
    /** Signals that the user wants to add a new List. */

    data object AddList : MainScreenEvent

    /**
     * An event to signal that the "Add Item" action was triggered from a global context.
     */
    /** Signals that the user wants to add a new Item. */
    data object AddItem : MainScreenEvent


    data object ShowAddCategorySheet : MainScreenEvent

    data class DeleteCategory(val categoryId: Long) : MainScreenEvent
    data class DeleteList(val listId: Long) : MainScreenEvent
    data class DeleteItem(val itemId: Long) : MainScreenEvent




}