package com.ylabz.basepro.camera.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.data.BaseProEntity
import com.ylabz.basepro.data.BaseProRepo
import com.ylabz.basepro.data.mapper.BasePro
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CamViewModel @Inject constructor(
    private val repository: BaseProRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<CamUIState>(CamUIState.Loading)
    val uiState: StateFlow<CamUIState> = _uiState

    private val _selectedItem = MutableStateFlow<BaseProEntity?>(null)
    val selectedItem: StateFlow<BaseProEntity?> = _selectedItem

    init {
        onEvent(CamEvent.LoadData)
    }

    fun onEvent(event: CamEvent) {
        when (event) {
            is CamEvent.LoadData -> loadData()
            is CamEvent.AddItem -> addItem(event.name, event.description, event.imgPath)
            is CamEvent.DeleteItem -> deleteItem(event.itemId)
            is CamEvent.DeleteAll -> deleteAll()
            is CamEvent.OnRetry -> onEvent(CamEvent.LoadData)
            is CamEvent.OnItemClicked -> selectItem(event.itemId)
        }
    }


    private fun addItem(name: String, description: String, imgPath: String) { // no image
        viewModelScope.launch {
            try {
                val newItem = BasePro(
                    title = name,
                    description = description,
                    imgPath = imgPath
                )
                repository.insert(newItem)
                onEvent(CamEvent.LoadData)  // Refresh the data after adding
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun selectItem(itemId: Int) {
        viewModelScope.launch {
            try {
                val selectedItem = repository.getBaseProById(itemId)
                _selectedItem.value = selectedItem
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }


    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = CamUIState.Loading
                repository.allGetBasePros().collect { data ->
                    _uiState.value = CamUIState.Success(data = data)
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteById(itemId)
                onEvent(CamEvent.LoadData)  // Refresh data after deletion
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteAll() {
        viewModelScope.launch {
            try {
                repository.deleteAll()
                onEvent(CamEvent.LoadData)  // Refresh data after deletion
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleError(e: Exception) {
        _uiState.value = CamUIState.Error(message = e.localizedMessage ?: "Unknown error")
    }
}
