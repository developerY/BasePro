package com.ylabz.basepro.applications.photodo.features.photodolist.ui.list

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
class PhotoDoListViewModel @Inject constructor(
    private val photoDoRepo: PhotoDoRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: Long = savedStateHandle.get<Long>("projectId")!!

    val uiState: StateFlow<PhotoDoListUiState> =
        photoDoRepo.getTasksForProject(projectId)
            .map { tasks -> PhotoDoListUiState.Success(tasks) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PhotoDoListUiState.Loading
            )
}