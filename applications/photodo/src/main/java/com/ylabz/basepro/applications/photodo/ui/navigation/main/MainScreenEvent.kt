package com.ylabz.basepro.applications.photodo.ui.navigation.main

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

    data object ShowAddCategorySheet : MainScreenEvent
}