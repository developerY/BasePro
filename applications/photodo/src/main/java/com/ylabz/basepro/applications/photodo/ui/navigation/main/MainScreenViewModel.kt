package com.ylabz.basepro.applications.photodo.ui.navigation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.entity.CategoryEntity
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Represents the UI state for the main home screen.
 * Note: This ViewModel is likely unused as its responsibilities have been moved
 * to more specific ViewModels like HomeViewModel.
 */
sealed interface MainScreenUiState {
    object Loading : MainScreenUiState
    data class Success(val categories: List<CategoryEntity>) : MainScreenUiState
}

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo
) : ViewModel() {

    /**
     * Exposes the UI state for the main screen.
     */
    val uiState: StateFlow<MainScreenUiState> =
        photoDoRepo.getAllCategories()
            .map { categories -> MainScreenUiState.Success(categories) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MainScreenUiState.Loading,
            )

    // The init block to insert default data has been removed.
    // This logic now correctly resides in the RoomDatabase.Callback in the DatabaseModule,
    // ensuring it only runs once when the database is first created.
}
