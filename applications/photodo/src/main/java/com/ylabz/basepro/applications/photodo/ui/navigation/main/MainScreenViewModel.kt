package com.ylabz.basepro.applications.photodo.ui.navigation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.entity.ProjectEntity
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the UI state for the main home screen.
 */
sealed interface MainScreenUiState {
    object Loading : MainScreenUiState
    data class Success(val projects: List<ProjectEntity>) : MainScreenUiState
}

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo
) : ViewModel() {

    /**
     * Exposes the UI state for the main screen.
     */
    val uiState: StateFlow<MainScreenUiState> =
        photoDoRepo.getAllProjects()
            .map { projects -> MainScreenUiState.Success(projects) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MainScreenUiState.Loading,
            )

    init {
        // For demonstration, insert default projects if the database is empty.
        viewModelScope.launch {
            if (photoDoRepo.getAllProjects().first().isEmpty()) {
                photoDoRepo.insertProject(ProjectEntity(name = "Home"))
                photoDoRepo.insertProject(ProjectEntity(name = "Car"))
                photoDoRepo.insertProject(ProjectEntity(name = "School"))
                photoDoRepo.insertProject(ProjectEntity(name = "Shopping"))
            }
        }
    }
}
