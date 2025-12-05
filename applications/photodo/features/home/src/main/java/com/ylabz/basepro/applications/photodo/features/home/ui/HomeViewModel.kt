package com.ylabz.basepro.applications.photodo.features.home.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "HomeViewModel" // Tag for logging


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo
) : ViewModel() {

    // 1. Input: User's Selection Intent (Null means "No selection made yet")
    private val _userSelectedCategoryId = MutableStateFlow<Long?>(null)

    // 2. Input: UI State for Bottom Sheet
    private val _isAddingCategory = MutableStateFlow(false)
    private val _categoryToEdit = MutableStateFlow<CategoryEntity?>(null)

    // 3. Side Effect: Navigation Event
    private val _categorySelectedEvent = MutableSharedFlow<Long>()
    val categorySelectedEvent: SharedFlow<Long> = _categorySelectedEvent.asSharedFlow()

    private val _isExpandedScreen = MutableStateFlow(false) // New

    // 2. One-Time Events (Side Effects)
    // Used to tell the UI to Navigate (e.g. slide to detail pane)
    private val _navigationEffect = Channel<HomeNavigationEffect>(Channel.BUFFERED)
    val navigationEffect = _navigationEffect.receiveAsFlow()

    // --- REACTIVE PIPELINE (Replaces loadInitialData) ---

    // A. The raw list of categories from the DB
    private val categoriesFlow = photoDoRepo.getAllCategories()
        .catch {
            Log.e(TAG, "Error loading categories", it)
            emit(emptyList())
        }

    // B. The "Active" Category (Calculated)
    // Combines the DB list + User Intent to decide what is actually selected.
    // This creates the "Auto-Select First" behavior automatically.
    private val activeCategoryFlow = combine(categoriesFlow, _userSelectedCategoryId) { categories, userSelectedId ->
        when {
            // Case 1: List is empty -> No selection
            categories.isEmpty() -> null

            // Case 2: User has selected a valid ID that exists in the list -> Use it
            userSelectedId != null && categories.any { it.categoryId == userSelectedId } -> {
                categories.find { it.categoryId == userSelectedId }
            }

            // Case 3: No selection (or selected ID was deleted) -> Auto-select the first one
            else -> {
                Log.d(TAG, "Auto-selecting first category: ${categories.first().name}")
                categories.first()
            }
        }
    }

    // C. The Task Lists (Reactive)
    // "flatMapLatest" is the magic. It says: "Whenever activeCategoryFlow changes,
    // unsubscribe from the old tasks and subscribe to the NEW category's tasks."
    @OptIn(ExperimentalCoroutinesApi::class)
    private val taskListsFlow = activeCategoryFlow.flatMapLatest { category ->
        if (category == null) {
            flowOf(emptyList())
        } else {
            Log.d(TAG, "Subscribing to tasks for category: ${category.name}")
            photoDoRepo.getTaskListsForCategory(category.categoryId)
        }
    }

    // D. The Final UI State
    // Combines all streams into one StateFlow for the UI.
    val uiState: StateFlow<HomeUiState> = combine(
        categoriesFlow,
        activeCategoryFlow,
        taskListsFlow,
        _isAddingCategory
    ) { categories, selectedCategory, taskLists, isAdding ->

        // Map to Success state (or Loading if we wanted to add that logic)
        HomeUiState.Success(
            categories = categories,
            selectedCategory = selectedCategory,
            taskListsForSelectedCategory = taskLists,
            isAddingCategory = isAdding
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState.Loading
    )


    /*val uiState: StateFlow<HomeUiState> =
        photoDoRepo.getAllCategories() // Updated from getAllProjects
            .map { categories -> HomeUiState.Success(categories = categories) } // Updated to use categories
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState.Loading,
            )
    */

    // --- EVENTS ---

    fun onEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeEvent.OnCategorySelected -> {
                    // 1. Update the selection flow (triggers the pipeline above)
                    _userSelectedCategoryId.value = event.categoryId
                    // 2. Emit the navigation side-effect
                    _categorySelectedEvent.emit(event.categoryId)
                }

                HomeEvent.OnAddCategoryClicked -> {
                    // Clear any previous edit state when opening for "Add"
                    _categoryToEdit.value = null
                    _isAddingCategory.value = true
                    // Update UI state accordingly
                }

                is HomeEvent.OnEditCategoryClicked -> {
                    // Set the category to edit and open sheet
                    _categoryToEdit.value = event.category
                    _isAddingCategory.value = true
                    // Update UI state via your pipeline or direct update
                    updateStateForEdit(event.category)
                }

                is HomeEvent.OnUpdateCategory -> {
                    photoDoRepo.updateCategory(event.category)
                    _isAddingCategory.value = false
                    _categoryToEdit.value = null
                    clearEditState()
                }

                HomeEvent.OnDismissAddCategory -> {
                    _isAddingCategory.value = false
                    _categoryToEdit.value = null
                    clearEditState()
                }

                is HomeEvent.OnSaveCategory -> {
                    // Save to DB
                    addCategory(event.categoryName, event.categoryDescription)
                    // Close sheet
                    _isAddingCategory.value = false
                }

                is HomeEvent.OnDeleteCategoryClicked -> {
                    photoDoRepo.deleteCategory(event.category)
                    // No extra logic needed!
                    // 1. DB updates categoriesFlow
                    // 2. activeCategoryFlow sees ID is missing, auto-selects new first
                    // 3. taskListsFlow switches to new first
                    // 4. UI updates automatically.
                }

                HomeEvent.OnAddListClicked -> { /* Handled by MainScreen */ }

                is HomeEvent.OnTaskListSelected -> { /* Navigation handled by UI Route */ }
                is HomeEvent.OnScreenLayoutChanged -> {
                    _isExpandedScreen.value = event.isExpanded
                }
            }
        }
    }

    private fun addCategory(name: String, description: String) {
        viewModelScope.launch {
            photoDoRepo.insertCategory(CategoryEntity(name = name, description = description))
        }
    }

    // Helper to update the specific state fields if you aren't using a single combined flow
    private fun updateStateForEdit(category: CategoryEntity) {
        // If using _uiState MutableStateFlow:
        /*
        _uiState.update { currentState ->
           (currentState as? HomeUiState.Success)?.copy(
               isAddingCategory = true,
               categoryToEdit = category
           ) ?: currentState
        }
        */
    }

    private fun clearEditState() {
        // If using _uiState MutableStateFlow:
        /*
        _uiState.update { currentState ->
           (currentState as? HomeUiState.Success)?.copy(
               isAddingCategory = false,
               categoryToEdit = null
           ) ?: currentState
        }
        */
    }
}


// Helper Sealed Class for One-Off Navigation Events
sealed interface HomeNavigationEffect {
    data class NavigateToDetailPane(val listId: Long) : HomeNavigationEffect
}
