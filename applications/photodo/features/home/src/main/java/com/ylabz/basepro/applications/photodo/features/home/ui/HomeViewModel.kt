package com.ylabz.basepro.applications.photodo.features.home.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "HomeViewModel" // Tag for logging


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /*val uiState: StateFlow<HomeUiState> =
        photoDoRepo.getAllCategories() // Updated from getAllProjects
            .map { categories -> HomeUiState.Success(categories = categories) } // Updated to use categories
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading,
            )
            */

    init {
        loadInitialData()
        /*viewModelScope.launch {
            photoDoRepo.getAllCategories().collect { categories ->
                _uiState.update { currentState ->
                    if (currentState is HomeUiState.Success) {
                        currentState.copy(categories = categories)
                    } else {
                        HomeUiState.Success(categories = categories)
                    }
                }
            }
        }*/
    }



    private fun loadInitialData() {
        viewModelScope.launch {
            Log.d(TAG, "Loading initial data...")
            photoDoRepo.getAllCategories().catch { e ->
                Log.e(TAG, "Error loading categories", e)
                _uiState.value = HomeUiState.Error("Failed to load categories.")
            }.collect { categories ->
                Log.d(TAG, "Successfully loaded ${categories.size} categories.")
                _uiState.value = HomeUiState.Success(
                    categories = categories,
                    taskListsForSelectedCategory = emptyList(),
                    selectedCategory = null
                )
            }
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            photoDoRepo.insertCategory(CategoryEntity(name = name))
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            // This will be updated in a subsequent step when HomeEvent is refactored
            is HomeEvent.OnCategorySelected -> {
                // --- THIS IS THE CRITICAL LOGIC ---
                val category = event.category
                Log.d(TAG, "OnCategorySelected event received for category: '${category.name}' (ID: ${category.categoryId})")

                viewModelScope.launch {
                    try {
                        // 1. Log before fetching from the database
                        Log.d(TAG, "Fetching task lists for category ID: ${category.categoryId}...")
                        // --- THE FIX IS HERE ---
                        // Use .first() to get the List from the Flow<List>
                        val taskLists = photoDoRepo.getTaskListsForCategory(category.categoryId).first()


                        // 2. Log the result of the database fetch
                        Log.d(TAG, "Found ${taskLists.count()} task lists for category '${category.name}'.")

                        // 3. Update the UI State with the new data
                        // This uses the current state to ensure we don't lose the category list
                        _uiState.update { currentState ->
                            if (currentState is HomeUiState.Success) {
                                currentState.copy(
                                    selectedCategory = category,
                                    taskListsForSelectedCategory = taskLists
                                )
                            } else {
                                // This case should ideally not happen if data loaded correctly
                                currentState
                            }
                        }
                        Log.d(TAG, "UI State updated successfully.")

                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching task lists for category ID ${category.categoryId}", e)
                        // Optionally, you can update the UI to show an error within the detail pane
                    }
                }
            }
            // --- END OF CRITICAL LOGIC ---

            // --- END OF CRITICAL LOGIC ---

            /* is HomeEvent.OnDeleteCategory -> {
                viewModelScope.launch {
                    repository.deleteCategory(event.category)
                }
            }*/
            HomeEvent.OnAddCategoryClicked -> {} //TODO()
        }
    }
}
