package com.ylabz.basepro.applications.photodo.features.home.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import com.ylabz.basepro.applications.photodo.features.home.ui.HomeEvent.OnDeleteCategoryClicked
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _categorySelectedEvent = MutableSharedFlow<Long>()
    val categorySelectedEvent: SharedFlow<Long> = _categorySelectedEvent.asSharedFlow()

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
                _uiState.update {
                    if (it is HomeUiState.Success) {
                        it.copy(categories = categories)
                    } else {
                        HomeUiState.Success(categories = categories)
                    }
                }
            }
        }*/
    }



    private fun loadInitialData() {
        Log.d("ViewModelInstance", "HomeViewModel created with hashCode: ${this.hashCode()}")

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
                val category = event
                Log.d(TAG, "OnCategorySelected event received for category:")// '${category.name}' (ID: ${category.categoryId})")
                handleCategorySelection(event.categoryId)
                /*viewModelScope.launch {
                    try {
                        // 1. Log before fetching from the database
                        Log.d(TAG, "Fetching task lists for category ID") // ${category.categoryId}...")
                        // --- THE FIX IS HERE ---
                        // Use .first() to get the List from the Flow<List>
                        val taskLists = photoDoRepo.getTaskListsForCategory(category).first()


                        // 2. Log the result of the database fetch
                        Log.d(TAG, "Found ${taskLists.count()} task lists for category")// '${category.name}'.")

                        // 3. Update the UI State with the new data
                        // This uses the current state to ensure we don't lose the category list
                        _uiState.update { currentState ->
                            if (currentState is HomeUiState.Success) {
                                currentState.copy(
                                    selectedCategory = category,
                                    taskListsForSelectedCategory = taskLists
                                )
                                currentState
                            } else {
                                // This case should ideally not happen if data loaded correctly
                                currentState
                            }
                        }
                        Log.d(TAG, "UI State updated successfully.")

                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching task lists for category ID ${category}", e)
                        // Optionally, you can update the UI to show an error within the detail pane
                    }
                }*/
            }
            // --- END OF CRITICAL LOGIC ---

            // --- END OF CRITICAL LOGIC ---

            /* is HomeEvent.OnDeleteCategory -> {
                viewModelScope.launch {
                    repository.deleteCategory(event.category)
                }
            }
            HomeEvent.OnAddCategoryClicked -> {
                Log.d("HomeViewModel", "OnAddCategoryClicked event received. Updating state.")

                // --- THIS IS THE FIX ---
                _uiState.update { currentState ->
                    (currentState as? HomeUiState.Success)?.copy(isAddingCategory = true) ?: currentState
                }
                /*
                _uiState.update {
                    (it as? HomeUiState.Success)?.copy(isAddingCategory = true)
                }
                */
                Log.d(TAG,  "Add Cat Called ")
            }*/
            HomeEvent.OnAddCategoryClicked -> {
                Log.d(TAG, "OnAddCategoryClicked event received. Updating state.")
                _uiState.update { currentState ->
                    (currentState as? HomeUiState.Success)?.copy(isAddingCategory = true) ?: currentState
                }
            }

            // --- ADD THIS LOGIC ---
            HomeEvent.OnDismissAddCategory -> {
                Log.d(TAG, "OnDismissAddCategory event received. Hiding sheet.")
                _uiState.update { currentState ->
                    (currentState as? HomeUiState.Success)?.copy(isAddingCategory = false) ?: currentState
                }
            }

            // --- ADD THIS LOGIC ---
            is HomeEvent.OnSaveCategory -> {
                // Log.d(TAG, "OnSaveCategory event received for name: '${event.name}'. Saving and hiding sheet.")
                // Call the function to save the category to the database
                // addCategory(event.name)
                // Update the state to hide the sheet
                _uiState.update { currentState ->
                    (currentState as? HomeUiState.Success)?.copy(isAddingCategory = false) ?: currentState
                }
            }

            is OnDeleteCategoryClicked -> {
                viewModelScope.launch {
                    photoDoRepo.deleteCategory(event.category)
                }
            }

            HomeEvent.OnAddListClicked -> {} //TODO()

        }
    }


    private fun handleCategorySelection(categoryId: Long) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState !is HomeUiState.Success) return@launch

                val category = currentState.categories.find { it.categoryId == categoryId } ?: return@launch
                Log.d(TAG, "Fetching task lists for category ID: ${category.categoryId}...")

                val taskLists = photoDoRepo.getTaskListsForCategory(category.categoryId).first()
                Log.d(TAG, "Found ${taskLists.size} task lists for category '${category.name}'.")

                _uiState.update {
                    (it as HomeUiState.Success).copy(
                        selectedCategory = category,
                        taskListsForSelectedCategory = taskLists
                    )
                }
                _categorySelectedEvent.emit(categoryId)
                Log.d(TAG, "UI State updated successfully.")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching task lists for category ID $categoryId", e)
            }
        }
    }
}
