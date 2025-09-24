package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PhotoDoDetailViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long = savedStateHandle.get<Long>("taskId")!!

    val uiState: StateFlow<PhotoDoDetailUiState> =
        photoDoRepo.getTaskWithPhotos(taskId)
            .map { taskWithPhotos ->
                if (taskWithPhotos != null) {
                    PhotoDoDetailUiState.Success(taskWithPhotos)
                } else {
                    PhotoDoDetailUiState.Error("Task not found")
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PhotoDoDetailUiState.Loading
            )
}