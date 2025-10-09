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
     * An event to signal that a category should be added with the given name.
     */
    data class AddCategory(val name: String) : MainScreenEvent

    data object ShowAddCategorySheet : MainScreenEvent
}
