package com.ylabz.basepro.applications.photodo.features.home.ui

import com.ylabz.basepro.applications.photodo.db.entity.ProjectEntity

/**
 * Defines the events that can be triggered from the Home screen.
 */
sealed interface HomeEvent {
    /**
     * Event triggered when the "Add Category" button is clicked.
     */
    data object OnAddCategoryClicked : HomeEvent

    /**
     * Event triggered when the "Add List" button is clicked from the FAB.
     */
    data object OnAddListClicked : HomeEvent

    /**
     * Event triggered when a user selects a project (category) from the list or carousel.
     *
     * @param project The project that was selected.
     */
    data class OnProjectSelected(val project: ProjectEntity) : HomeEvent
}
