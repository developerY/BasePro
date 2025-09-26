package com.ylabz.basepro.applications.photodo.features.home.ui

import com.ylabz.basepro.applications.photodo.db.entity.ProjectEntity
import com.ylabz.basepro.applications.photodo.db.entity.TaskEntity

/**
 * Represents the possible UI states for the Home screen.
 */
sealed interface HomeUiState {
    /**
     * The initial loading state.
     */
    object Loading : HomeUiState

    /**
     * The state representing that the projects and tasks have been successfully loaded.
     *
     * @param projects The complete list of all projects (categories).
     * @param selectedProject The currently selected project. Can be null if no project is selected.
     * @param tasksForSelectedProject The list of tasks (lists) for the `selectedProject`.
     */
    data class Success(
        val projects: List<ProjectEntity>,
        val selectedProject: ProjectEntity? = null,
        val tasksForSelectedProject: List<TaskEntity> = emptyList()
    ) : HomeUiState

    /**
     * The state representing an error has occurred.
     *
     * @param message A user-friendly error message.
     */
    data class Error(val message: String) : HomeUiState
}
