package com.ylabz.basepro.applications.photodo.features.home.ui

import com.ylabz.basepro.applications.photodo.db.entity.ProjectEntity

/**
 * Represents the possible UI states for the Home screen.
 */
sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val projects: List<ProjectEntity>) : HomeUiState
}
