package com.ylabz.basepro.applications.photodo.features.photodolist.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.photodo.db.repo.PhotoDoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class PhotoDoDetailViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo
) : ViewModel() {

    private val _taskId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PhotoDoDetailUiState> = _taskId.flatMapLatest { taskId ->
        if (taskId == null) {
            MutableStateFlow(PhotoDoDetailUiState.Loading)
        } else {
            // Ensure taskId can be converted to Long for the repository call
            val longTaskId = taskId.toLongOrNull() ?: return@flatMapLatest MutableStateFlow(PhotoDoDetailUiState.Loading)
            photoDoRepo.getTaskWithPhotos(longTaskId)
                .map { taskWithPhotos ->
                    if (taskWithPhotos != null) {
                        PhotoDoDetailUiState.Success(taskWithPhotos)
                    } else {
                        // Handle case where task is not found
                        PhotoDoDetailUiState.Loading // Or a specific Error state
                    }
                }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PhotoDoDetailUiState.Loading
    )

    /**
     * Sets the task ID to load details for. The uiState will automatically update.
     */
    fun loadTask(id: String) {
        _taskId.value = id
    }
}
