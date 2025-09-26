package com.ylabz.basepro.applications.photodo.features.home.ui

/**
 * Represents the events that can be triggered from the Home UI.
 */
sealed interface HomeEvent {
    /**
     * User clicked the 'Add Category' button.
     */
    object OnAddCategoryClicked : HomeEvent
}
