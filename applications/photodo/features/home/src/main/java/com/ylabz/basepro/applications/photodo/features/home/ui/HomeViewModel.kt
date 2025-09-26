package com.ylabz.basepro.applications.photodo.features.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
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
        photoDoRepo.getAllCategories() // Updated from getAllProjects
            .map { categories -> HomeUiState.Success(categories = categories) } // Updated to use categories
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading,
            )

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnAddCategoryClicked -> {
                viewModelScope.launch {
                    val newCategoryName = "New Category ${System.currentTimeMillis() % 1000}"
                    // Updated to use insertCategory and CategoryEntity
                    photoDoRepo.insertCategory(CategoryEntity(name = newCategoryName))
                }
            }
            // This will be updated in a subsequent step when HomeEvent is refactored
            is HomeEvent.OnCategorySelected -> {
                // TODO: Handle category selection to show its task lists
            }
        }
    }
}
