package com.ylabz.basepro.applications.photodo.features.home.ui


/**
 * Defines all the user actions and events that can be triggered from the Home screen.
 */
sealed interface HomeEvent {
    /**
     * Event triggered when a user selects a category from the list.
     * @param categoryId The ID of the category that was selected.
     */
    data class OnCategorySelected(val categoryId: Long) : HomeEvent

    /**
     * Event triggered to signal the UI to show the 'Add Category' bottom sheet.
     * This event does not carry data.
     */
    data object OnAddCategoryClicked : HomeEvent

    /**
     * Event triggered when the user dismisses the 'Add Category' bottom sheet.
     */
    data object OnDismissAddCategory : HomeEvent

    /**
     * Event triggered when the user clicks the 'Save' button in the bottom sheet.
     * This event carries the new category's name.
     * @param categoryName The name of the new category to be created.
     */
    data class OnSaveCategory(val categoryName: String) : HomeEvent

    /**
     * Event triggered when the user clicks the 'Add List' button in the FAB.
     * This tells the ViewModel to add a new list to the currently selected category.
     */
    data object OnAddListClicked : HomeEvent

    /**
     * Event triggered when the user clicks the button to navigate to the new UI.
     */
    data object OnNavigateToNewUi : HomeEvent

    /**
     * Event triggered after navigation to the new UI has occurred.
     */
    data object OnNewUiNavigated : HomeEvent
}
