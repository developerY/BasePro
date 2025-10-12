package com.ylabz.basepro.applications.photodo.features.home.ui

import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskListEntity

/**
 * Represents the possible UI states for the Home screen.
 */
sealed interface HomeUiState {
    /**
     * The initial loading state.
     */
    object Loading : HomeUiState

    /**
     * The state representing that the data has been successfully loaded.
     *
     * @param categories The list of all categories to display.
     * @param selectedCategory The currently selected category, if any.
     * @param taskListsForSelectedCategory The list of tasks for the currently selected category.
     * @param isAddingCategory A flag to control the visibility of the 'Add Category' bottom sheet.
     */
    data class Success(
        val categories: List<CategoryEntity> = emptyList(),
        val selectedCategory: CategoryEntity? = null,
        val taskListsForSelectedCategory: List<TaskListEntity> = emptyList(),
        val isAddingCategory: Boolean = false
    ) : HomeUiState

    /**
     * The state representing an error has occurred.
     *
     * @param message A user-friendly error message.
     */
    data class Error(val message: String) : HomeUiState
}
