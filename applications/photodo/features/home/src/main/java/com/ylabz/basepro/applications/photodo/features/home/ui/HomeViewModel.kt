package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.entity.ProjectEntity
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        photoDoRepo.getAllProjects()
            .map { projects -> HomeUiState.Success(projects) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading,
            )

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnAddCategoryClicked -> {
                // For now, we will just add a new project with a default name.
                // A more complete solution would show a dialog to get the name.
                viewModelScope.launch {
                    val newProjectName = "New Category ${System.currentTimeMillis() % 1000}"
                    photoDoRepo.insertProject(ProjectEntity(name = newProjectName))
                }
            }

            HomeEvent.OnAddListClicked -> {}//TODO()
            is HomeEvent.OnProjectSelected -> {}//TODO()
        }
    }
}
