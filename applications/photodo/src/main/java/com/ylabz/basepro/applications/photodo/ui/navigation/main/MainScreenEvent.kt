package com.ylabz.basepro.applications.photodo.ui.navigation.main

/**
 * Defines the shared events that can be communicated across different screens
 * via the MainScreenViewModel.
 */
sealed interface MainScreenEvent {
    /**
     * An event to signal that the "Add Item" action was triggered from a global context.
     */
    data object AddItem : MainScreenEvent

    /**
     * An event to signal that the "Add List" action was triggered from a global context.
     */
    data object AddList : MainScreenEvent

    /**
     * An event to signal that the "Add Category" action was triggered from a global context.
     */
    data object AddCategory : MainScreenEvent
}