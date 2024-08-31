package com.ylabz.twincam.cam.ui

// File: CamViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.ylabz.twincam.data.TwinCamEntity
import com.ylabz.twincam.data.TwinCamRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CamViewModel @Inject constructor(
    private val repository: TwinCamRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(CamUIState())
    val uiState: StateFlow<CamUIState> = _uiState

    init {
        onEvent(CamEvent.LoadData)
    }

    private fun onEvent(event: CamEvent) {
        when (event) {
            is CamEvent.LoadData -> {
                loadData()
            }
            is CamEvent.AddItem -> {
                addItem(event.name)
            }
            is CamEvent.DeleteItem -> {
                deleteItem(event.itemId)
            }
            is CamEvent.OnRetry -> {
                onEvent(CamEvent.LoadData)
            }
            is CamEvent.OnItemClicked -> {
                // Handle item click if needed
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.allGetTwinCams().collectLatest { data ->
                _uiState.value = CamUIState(data = data)
            }
        }
    }

    private fun addItem(name: String) {
        viewModelScope.launch {
            repository.insert(TwinCamEntity(title = name))
            onEvent(CamEvent.LoadData)  // Refresh the data after adding
        }
    }

    private fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            repository.getTwinCamById(itemId)
            onEvent(CamEvent.LoadData)  // Refresh the data after deleting
        }
    }
}
