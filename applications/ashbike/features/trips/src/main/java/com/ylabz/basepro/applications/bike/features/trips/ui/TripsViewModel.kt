package com.ylabz.basepro.applications.bike.features.trips.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ylabz.basepro.applications.bike.database.BikeProEntity
import com.ylabz.basepro.applications.bike.database.BikeProRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.ylabz.basepro.applications.bike.database.mapper.BikePro


@HiltViewModel
class TripsViewModel @Inject constructor(
    private val repository: BikeProRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripsUIState>(TripsUIState.Loading)
    val uiState: StateFlow<TripsUIState> = _uiState

    private val _selectedItem = MutableStateFlow<BikeProEntity?>(null)
    val selectedItem: StateFlow<BikeProEntity?> = _selectedItem

    init {
        onEvent(TripsEvent.LoadData)
    }

    fun onEvent(event: TripsEvent) {
        when (event) {
            is TripsEvent.LoadData -> loadData()
            is TripsEvent.AddItem -> addItem(event.name)
            is TripsEvent.DeleteItem -> deleteItem(event.itemId)
            is TripsEvent.DeleteAll -> deleteAll()
            is TripsEvent.OnRetry -> onEvent(TripsEvent.LoadData)
            is TripsEvent.OnItemClicked -> selectItem(event.itemId)
        }
    }

    fun selectItem(itemId: Int) {
        viewModelScope.launch {
            // Fetch the item details only if they are not already loaded
            if (_selectedItem.value?.todoId != itemId) {
                _selectedItem.value = repository.getBikeProById(itemId)
            }
        }
    }

    private fun deleteAll() {
        viewModelScope.launch {
            try {
                repository.deleteAll()
                // Optionally refresh data after deletion
                onEvent(TripsEvent.LoadData)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.value = TripsUIState.Loading
                repository.allGetBikePros().collect { data ->
                    _uiState.value = TripsUIState.Success(data = data)
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun addItem(name: String) {
        viewModelScope.launch {
            try {
                repository.insert(
                    BikePro(title = name)
                )
                onEvent(TripsEvent.LoadData)  // Refresh data after adding
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun deleteItem(itemId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteById(itemId)
                onEvent(TripsEvent.LoadData)  // Refresh data after deletion
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    // Centralized error handling
    private fun handleError(e: Exception) {
        _uiState.value = TripsUIState.Error(message = e.localizedMessage ?: "Unknown error")
    }
}
