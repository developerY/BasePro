package com.ylabz.basepro.applications.photodo.ui.navigation.main

/**
 * Defines the shared events that can be sent to the MainScreenViewModel.
 *
 * This file is part of the "Stateful ViewModel" pattern and is used by
 * navigation entries (like HomeEntry) to request global UI changes,
 * such as showing a bottom sheet.
 */
sealed interface MainScreenEvent {
    /**
     * Signals that the "Add Category" action was triggered.
     */
    data object OnAddCategoryClicked : MainScreenEvent

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
     * Signals that a category should be saved.
     */
    data class OnSaveCategory(val name: String, val description: String) : MainScreenEvent
}