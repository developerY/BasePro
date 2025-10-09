package com.ylabz.basepro.applications.photodo.features.home.ui

import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity

/**
 * Defines the events that can be triggered from the Home screen.
 */
sealed interface HomeEvent {
    /**
     * Event triggered when the "Add Category" button is clicked.
     */
    data class OnAddCategoryClicked(val categoryName: String) : HomeEvent

    /**
     * Event triggered when a user selects a category from the list.
     *
     * @param category The category that was selected.
     */
    data class OnCategorySelected(val category: CategoryEntity) : HomeEvent
}
