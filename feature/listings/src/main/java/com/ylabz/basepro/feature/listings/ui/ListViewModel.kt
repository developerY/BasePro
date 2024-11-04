package com.ylabz.basepro.listings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.core.data.BaseProEntity
import com.ylabz.basepro.core.data.BaseProRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: BaseProRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUIState>(ListUIState.Loading)
    val uiState: StateFlow<ListUIState> = _uiState

    private val _selectedItem = MutableStateFlow<BaseProEntity?>(null)
    val selectedItem: StateFlow<BaseProEntity?> = _selectedItem

    init {
        onEvent(ListEvent.LoadData)
    }

    fun onEvent(event: ListEvent) {
        when (event) {
            is ListEvent.LoadData -> loadData()
            is ListEvent.AddItem -> addItem(event.name)
            is ListEvent.DeleteItem -> deleteItem(event.itemId)
            is ListEvent.DeleteAll -> deleteAll()
            is ListEvent.OnRetry -> onEvent(ListEvent.LoadData)
            is ListEvent.OnItemClicked -> selectItem(event.itemId)
        }
    }

    fun selectItem(itemId: Int) {
        viewModelScope.launch {
            // Fetch the item details only if they are not already loaded
            if (_selectedItem.value?.todoId != itemId) {
                _selectedItem.value = repository.getBaseProById(itemId)
            }
        }
    }

    private fun deleteAll() {
        viewModelScope.launch {
            try {
                repository.deleteAll()
                // Optionally refresh data after deletion
                onEvent(ListEvent.LoadData)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = ListUIState.Loading
                repository.allGetBasePros().collect { data ->
                    _uiState.value = ListUIState.Success(data = data)
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun addItem(name: String) {
        viewModelScope.launch {
            try {
                repository.insert(com.ylabz.basepro.core.data.mapper.BasePro(title = name))
                onEvent(ListEvent.LoadData)  // Refresh data after adding
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteById(itemId)
                onEvent(ListEvent.LoadData)  // Refresh data after deletion
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    // Centralized error handling
    private fun handleError(e: Exception) {
        _uiState.value = ListUIState.Error(message = e.localizedMessage ?: "Unknown error")
    }
}
