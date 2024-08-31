package com.ylabz.twincam.cam.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.twincam.data.TwinCamRepo
import com.ylabz.twincam.data.mapper.TwinCam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CamViewModel @Inject constructor(
    private val repository: TwinCamRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<CamUIState>(CamUIState.Loading)
    val uiState: StateFlow<CamUIState> = _uiState

    init {
        onEvent(CamEvent.LoadData)
    }

    fun onEvent(event: CamEvent) {
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
            is CamEvent.DeleteAll -> {
                deleteAll()
            }
            is CamEvent.OnRetry -> {
                onEvent(CamEvent.LoadData)
            }
            is CamEvent.OnItemClicked -> {
                // Handle item click if needed
            }
        }
    }

    private fun deleteAll() {
        viewModelScope.launch {
            try {
                repository.deleteAll()
                //onEvent(CamEvent.LoadData)  // Refresh the data after deleting
            } catch (e: Exception) {
                _uiState.value = CamUIState.Error(message = e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = CamUIState.Loading
                repository.allGetTwinCams().collect { data ->
                    _uiState.value = CamUIState.Success(data = data)
                }
            } catch (e: Exception) {
                _uiState.value = CamUIState.Error(message = e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun addItem(name: String) {
        viewModelScope.launch {
            try {
                repository.insert(TwinCam(title = name))
                onEvent(CamEvent.LoadData)  // Refresh the data after adding
            } catch (e: Exception) {
                _uiState.value = CamUIState.Error(message = e.localizedMessage ?: "Unknown error")
            }
        }
    }

    private fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            try {
                repository.getTwinCamById(itemId)
                onEvent(CamEvent.LoadData)  // Refresh the data after deleting
            } catch (e: Exception) {
                _uiState.value = CamUIState.Error(message = e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
