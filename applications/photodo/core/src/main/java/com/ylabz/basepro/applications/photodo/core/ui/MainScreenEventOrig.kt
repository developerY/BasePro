package com.ylabz.basepro.applications.photodo.core.ui

/**
 * Defines the shared events that can be communicated across different screens
 * via the MainScreenViewModel.
 */
sealed interface MainScreenEventOrig {

    /**
     * An event to request that the UI for adding a new category be shown.
     */
    // Event to request showing the "Add Category" UI
    data object RequestAddCategory : MainScreenEventOrig

    /**
     * An event that carries the new category's name to be saved.
     */
    /** Signals that the user wants to add a new Category. */

    data class AddCategory(val categoryName: String) : MainScreenEventOrig

    /**
     * An event to signal that the "Add List" action was triggered from a global context.
     */
    /** Signals that the user wants to add a new List. */

    data object AddList : MainScreenEventOrig

    /**
     * An event to signal that the "Add Item" action was triggered from a global context.
     */
    /** Signals that the user wants to add a new Item. */
    data object AddItem : MainScreenEventOrig


    data object ShowAddCategorySheet : MainScreenEventOrig

    data class DeleteCategory(val categoryId: Long) : MainScreenEventOrig
    data class DeleteList(val listId: Long) : MainScreenEventOrig
    data class DeleteItem(val itemId: Long) : MainScreenEventOrig




}