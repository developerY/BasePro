package com.ylabz.basepro.applications.photodo.core.ui

/**
 * Defines the shared events that can be communicated across different screens
 * via the MainScreenViewModel.
 */
sealed interface MainScreenEvent {
    /** Signals that the user wants to add a new Category. */
    data object AddCategory : MainScreenEvent

    /** Signals that the user wants to add a new List. */
    data object AddList : MainScreenEvent

    /** Signals that the user wants to add a new Item. */
    data object AddItem : MainScreenEvent
}