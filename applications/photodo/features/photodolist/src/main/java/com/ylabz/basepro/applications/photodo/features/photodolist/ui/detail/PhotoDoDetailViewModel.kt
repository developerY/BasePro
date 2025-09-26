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

    private val _listId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PhotoDoDetailUiState> = _listId.flatMapLatest { listId ->
        if (listId == null) {
            MutableStateFlow(PhotoDoDetailUiState.Loading)
        } else {
            val longListId = listId.toLongOrNull() ?: return@flatMapLatest MutableStateFlow(PhotoDoDetailUiState.Loading)
            photoDoRepo.getTaskListWithPhotos(longListId)
                .map { taskListWithPhotos ->
                    if (taskListWithPhotos != null) {
                        PhotoDoDetailUiState.Success(taskListWithPhotos)
                    } else {
                        PhotoDoDetailUiState.Loading
                    }
                }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PhotoDoDetailUiState.Loading
    )

    fun loadList(id: String) {
        _listId.value = id
    }
}
