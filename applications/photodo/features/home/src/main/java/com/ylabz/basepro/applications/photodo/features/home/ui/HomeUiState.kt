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
     * The state representing that the categories and task lists have been successfully loaded.
     *
     * @param categories The complete list of all categories.
     * @param selectedCategory The currently selected category. Can be null if no category is selected.
     * @param taskListsForSelectedCategory The list of task lists for the `selectedCategory`.
     */
    data class Success(
        val categories: List<CategoryEntity>,
        val selectedCategory: CategoryEntity? = null,
        val taskListsForSelectedCategory: List<TaskListEntity> = emptyList()
    ) : HomeUiState

    /**
     * The state representing an error has occurred.
     *
     * @param message A user-friendly error message.
     */
    data class Error(val message: String) : HomeUiState
}
